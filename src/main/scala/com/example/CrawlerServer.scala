package com.example

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem }

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import akka.stream.ActorMaterializer

object CrawlerServer extends App with CrawlerRoutes {
  implicit val system: ActorSystem = ActorSystem("crawlerServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val crawlerActor: ActorRef = system.actorOf(CrawlerActor.props, "crawlerActor")

  lazy val routes: Route = crawlerRoutes
  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Crawler is online at http://localhost:8080/")

  Await.result(system.whenTerminated, Duration.Inf)
}