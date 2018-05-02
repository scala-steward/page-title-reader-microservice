package com.example

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import akka.pattern.ask
import akka.util.Timeout
import akka.event.Logging

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route

import com.example.CrawlerActor._

trait CrawlerRoutes extends Directives with SprayJsonSupport with DefaultJsonProtocol {
  implicit def system: ActorSystem

  implicit lazy val timeout = Timeout(60.seconds)
  lazy val log = Logging(system, classOf[CrawlerRoutes])

  def crawlerActor: ActorRef

  lazy val crawlerRoutes: Route = pathSingleSlash {
    get {
      complete(
        """Please provide a list of url's in a POST request.
          |
          |It must be a JSON array of strings.
          |Example: ["https://google.com", "google.ru"]""".stripMargin)
    } ~
      post {
        entity(as[List[String]]) { urlList =>
          val res: Future[List[Map[String, String]]] =
            (crawlerActor ? ProcessList(urlList))
              .mapTo[Future[List[Map[String, String]]]]
              .flatten
          rejectEmptyResponse {
            complete(res)
          }
        }
      }
  }
}
