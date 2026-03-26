package crawler

import scala.concurrent.Future
import scala.concurrent.duration._

import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.event.Logging
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.pekko.http.scaladsl.server.Directives
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
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
