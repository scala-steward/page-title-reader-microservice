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
    "com.typesafe.akka" %% "akka-http"            % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-stream"          % Versions.akka
  ),
  // Scalafix
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)
