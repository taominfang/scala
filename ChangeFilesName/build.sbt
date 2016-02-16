import _root_.sbtassembly.AssemblyPlugin.autoImport._

name := "ChangeFilesName"

version := "1.0"

scalaVersion := "2.11.7"

assemblyOption in assembly ~= { _.copy(cacheOutput = false) }

//my lib, include commandline parameter parse
libraryDependencies += "default" %% "my_libs" % "1.0"


libraryDependencies += "com.google.code.gson" % "gson" % "2.3.1"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"


libraryDependencies += "commons-httpclient" % "commons-httpclient" % "3.1"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.1"

libraryDependencies += "org.scala-lang" % "scala-actors" % "2.11.6"
