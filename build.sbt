lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      scalaVersion := "2.13.17"
    )
  ),
  run / fork := true, // Makes exit codes work as expected
  name       := "page-title-reader-microservice",
  libraryDependencies ++= Seq(
    "org.apache.pekko" %% "pekko-http"            % Versions.pekkoHttp,
    "org.apache.pekko" %% "pekko-http-spray-json" % Versions.pekkoHttp,
    "org.apache.pekko" %% "pekko-stream"          % Versions.pekko
  ),
  // Scalafix
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)
