name := "table_file_tools"

version := "1.0"

scalaVersion := "2.11.7"

assemblyOption in assembly ~= { _.copy(cacheOutput = false) }

libraryDependencies += "default" %% "my_libs" % "1.0"

libraryDependencies += "com.google.code.gson" % "gson" % "2.3.1"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"