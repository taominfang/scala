name := "table_file_tools"

version := "1.0"

scalaVersion := "2.11.6"

assemblyOption in assembly ~= { _.copy(cacheOutput = false) }