name := "mybatis_cdi_demo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.mybatis" % "mybatis" % "3.2.8",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.jboss.weld.se" % "weld-se" % "2.2.7.Final"
)     

play.Project.playJavaSettings
