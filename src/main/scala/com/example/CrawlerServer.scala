package com.example

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem }

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import akka.stream.ActorMaterializer

object CrawlerServer extends App with CrawlerRoutes {
  implicit val system: ActorSystem = ActorSystem("crawlerServer")

  val crawlerActor: ActorRef = system.actorOf(CrawlerActor.props, "crawlerActor")

  lazy val routes: Route = crawlerRoutes
  Http().newServerAt("localhost", 8080).bindFlow(routes)
  println(s"Crawler is online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}
