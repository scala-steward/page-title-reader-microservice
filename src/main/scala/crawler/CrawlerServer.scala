package crawler

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

object CrawlerServer extends App with CrawlerRoutes {
  implicit val system: ActorSystem = ActorSystem("crawlerServer")

  val crawlerActor: ActorRef = system.actorOf(CrawlerActor.props, "crawlerActor")

  lazy val routes: Route = crawlerRoutes
  locally {
    val _ = Http().newServerAt("localhost", 8080).bindFlow(routes)
    println(s"Crawler is online at http://localhost:8080/")
    val _ = Await.result(system.whenTerminated, Duration.Inf)
  }

}
