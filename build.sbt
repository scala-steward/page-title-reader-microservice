lazy val akkaHttpVersion = "10.6.3"
lazy val akkaVersion    = "2.9.3"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion    := "2.13.14"
    )),
    resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
    name := "page-title-reader-microservice",
    // scalacOptions ++= Seq("-unchecked", "-deprecation"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      // "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      // "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      // "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      // "org.scalatest"     %% "scalatest"            % "3.0.1" 
    )
  )
