package com.example

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future
import scala.util._

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.scaladsl.Source
import akka.util.ByteString

object CrawlerActor {
  def props: Props = Props[CrawlerActor]()

  final case class ProcessList(urlList: List[String])

  def getTitle(src: String): Option[String] = {
    val titleRegex = """<title[^>]*>([^<]+)</title>""".r
    for (titleRegex(title) <- titleRegex.findFirstMatchIn(src)) yield title
  }

  def prependScheme(raw: String): Uri = {
    val defaultScheme = "http"
    Uri(raw) match {
      case uri if uri.isAbsolute => uri
      case uri                   => Uri(s"${defaultScheme}://${uri}")
    }
  }

  def serializeToMap(url: String, resOrError: Either[String, String]) = resOrError match {
    case Left(x)  => Map("url" -> url, "error" -> x)
    case Right(x) => Map("url" -> url, "title" -> x)
  }

}

class CrawlerActor extends Actor with ActorLogging {

  import CrawlerActor._

  implicit val system: ActorSystem                        = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def prepareRequest(url: String): Future[Either[String, HttpRequest]] =
    Future
      .fromTry(Try(Right(HttpRequest(uri = prependScheme(url)))))
      .recoverWith({ case _ => Future.successful(Left("Bad url")) })

  def sendRequest(req: Either[String, HttpRequest]): Future[Either[String, HttpResponse]] =
    (req match {
      case Right(x) => Http().singleRequest(x).map(Right(_))
      case Left(x)  => Future.successful(Left(x))
    }).recoverWith({ case _ => Future.successful(Left("Inaccessible host")) })

  def processResponse(res: Either[String, HttpResponse]): Future[Either[String, String]] =
    res match {
      case Right(HttpResponse(StatusCodes.OK, _, entity, _)) =>
        entity.dataBytes
          .runFold(ByteString(""))(_ ++ _)
          .map(_.utf8String)
          .map(getTitle)
          .map({
            case None        => Left("Page has no title")
            case Some(title) => Right(title)
          })
      case Right(resp) if resp.status.isRedirection =>
        val _ = resp.discardEntityBytes()
        resp.header[Location] match {
          case Some(h) => Future.successful(Left(s"Redirect: ${h.uri.toString}"))
          case None    => Future.successful(Left("Redirect with no location header"))
        }
      case Right(resp) =>
        val _ = resp.discardEntityBytes()
        Future.successful(Left(s"Status code: ${resp.status.intValue}"))
      case Left(str) => Future.successful(Left(str))
    }

  def processRequest(url: String): Future[(String, Map[String, String])] =
    prepareRequest(url)
      .flatMap(sendRequest)
      .flatMap(processResponse)
      .map(x => (url, serializeToMap(url, x)))

  def receive: Receive = { case ProcessList(urlList) =>
    sender() ! Source
      .fromIterator(() => urlList.toSet.iterator)
      .mapAsync(100)((x: String) => processRequest(x))
      .runFold(Map.empty[String, Map[String, String]])(_ + _)
      .flatMap(uniqMap =>
        Future.successful(urlList.map(x => uniqMap.getOrElse(x, Map(x -> "unknown error never happened"))))
      )
  }

}
