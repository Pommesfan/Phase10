val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Phase10",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

      libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
      libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24",
      libraryDependencies += "com.google.inject" % "guice" % "4.2.3",
      libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2").cross(CrossVersion.for3Use2_13),
      libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
      libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1",

      libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
        lazy val osName = System.getProperty("os.name") match {
          case n if n.startsWith("Linux") => "linux"
          case n if n.startsWith("Mac") => "mac"
          case n if n.startsWith("Windows") => "win"
          case _ => throw new Exception("Unknown platform!")
        }
        Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
          .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
      },
      jacocoExcludes := Seq("*gui.*", "*fileIO.*", "*Main.scala"),
      jacocoCoverallsServiceName := "github-actions",
      jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
      jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
      jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  ).enablePlugins(JacocoCoverallsPlugin)
