package crawler

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import crawler.CrawlerActor._

trait CrawlerRoutes extends Directives with SprayJsonSupport with DefaultJsonProtocol {
  implicit def system: ActorSystem

  implicit lazy val timeout: Timeout = Timeout(60.seconds)
  lazy val log                       = Logging(system, classOf[CrawlerRoutes])

  def crawlerActor: ActorRef

  lazy val crawlerRoutes: Route = pathSingleSlash {
    get {
      complete("""Please provide a list of url's in a POST request.
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
          onSuccess(res)(xs => complete(xs))
        }
      }
  }

}
