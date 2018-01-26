name := "AccountingNotebook"

version := "1.0"

lazy val `accountingnotebook` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += evolutions
libraryDependencies += guice
libraryDependencies += specs2 % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3"

unmanagedResourceDirectories in Test +=  baseDirectory(_ / "target/web/public/test").value

      