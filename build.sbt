val Http4sVersion = "0.19.0-M2"
val CirceVersion = "0.10.0-M2"
val Specs2Version = "4.2.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.xing",
    name := "tictactoe",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
  )
  .aggregate(server, client)

lazy val server = (project in file("server"))
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    )
  )
  .dependsOn(model)

lazy val client = (project in file("client"))
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    )
  )
  .dependsOn(model)

lazy val model = (project in file("model"))
