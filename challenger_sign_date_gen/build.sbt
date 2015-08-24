import _root_.sbtassembly.AssemblyPlugin.autoImport._

name := "challenger_sign_date_gen"

version := "1.0"

scalaVersion := "2.11.7"



assemblyOption in assembly ~= { _.copy(cacheOutput = false) }

test in assembly := {}

assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter {jar =>  jar.data.getName == "servlet-api-2.5-20081211.jar" ||
    jar.data.getName == "scalatest_2.11-2.2.1.jar" }
}

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "default" %% "my_libs" % "1.0"

libraryDependencies += "com.google.code.gson" % "gson" % "2.3.1"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "commons-net" % "commons-net" % "3.3"