lazy val akkaHttpVersion = "10.6.3"
lazy val akkaVersion     = "2.9.3"

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      scalaVersion := "2.13.14"
    )
  ),
  run / fork := true, // Makes exit codes work as expected
  resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
  name := "page-title-reader-microservice",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion
  ),
  // Scalafix
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)
