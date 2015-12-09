package org.openfly.tools.scala.syschronzie

import java.io.{FileReader, BufferedReader, File}
import java.nio.file.StandardCopyOption._

import java.nio.file.{Files, Path}

import org.openfly.{Parameter, ParameterParser}

import scala.collection.mutable

/**
 * Created by mtao60 on 8/24/15.
 */
object DeployFramework {


  def collectionCopyFiles(frRootPath: String, fFolder: File, ignoreFileNames: mutable.Set[String], ignoreFilePaths: mutable.Set[String], copyList: mutable.MutableList[(File, String, String)]): Unit = {
    val subs = List(fFolder.listFiles(): _*)
    subs.foreach(one => {
      val fileName = one.getName
      val filePath = one.getPath
      val relativePath = filePath.substring(frRootPath.length + 1)

      val relativeFolder = relativePath.substring(0, relativePath.length - fileName.length);

      if (fileName == "." || fileName == "..") {
        //ignore
      }
      else if (ignoreFileNames.contains(fileName)) {
        //ignore
      }
      else if (ignoreFilePaths.contains(relativePath)) {
        //ignore
      }
      else {

        if (one.isFile) {
          copyList += new Tuple3(one, relativeFolder, fileName);
        }
        else if (one.isDirectory) {
          collectionCopyFiles(frRootPath, one, ignoreFileNames, ignoreFilePaths, copyList)
        }
      }
    })
  }

  def main(argv: Array[String]): Unit = {
    val p = new ParameterParser(argv)
    p.add(Parameter("framework-folder").addAliasKey("-s").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("deploy-folder").addAliasKey("-d").setRequired(true).setDesc("support multiple folder, format: -d folder1 -d folder2").setFollowingValueSize(1))
    p.add(Parameter("force-copy-all").addAliasKey("-f").setRequired(false).setFollowingValueSize(0));
    p.add(Parameter("ignore-file-name").addAliasKey("-in").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("ignore-file-path").addAliasKey("-ip").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("ignore-file-list-file").addAliasKey("-if").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("dry-run").setRequired(false).setDesc("Only list the copy which need copy, do not do the copy action!"))
    p.add(Parameter("help").setRequired(false).setFollowingValueSize(0))

    p.parse();

    if (p.isSet("help")) {
      println("Usage:" + p.usage)
      return
    }

    val es = p.getErrorMessage()

    if (es.size > 0) {
      es.foreach(println(_))
      println("error, quite")
      println("Usage:" + p.usage)
      return
    }
    val fFolder = new File(p.getFirstValue("framework-folder").get)

    require(fFolder.isDirectory, fFolder.getPath + " should be the dir")




    val ignoreFileNames = mutable.Set[String]((p.getValues("ignore-file-name").getOrElse(Array[String]())): _*)

    val ignoreFilePaths = mutable.Set[String]((p.getValues("ignore-file-path").getOrElse(Array[String]())): _*)


    try {
      val br = new BufferedReader(new FileReader(p.getFirstValue("ignore-file-list-file").get))
      var line = br.readLine()
      while (line != null) {
        ignoreFilePaths.add(line)
        line = br.readLine()
      }
      br.close();
    }
    catch {
      case _ => {}
    }

    val copyList = new mutable.MutableList[(File, String, String)]();
    collectionCopyFiles(fFolder.getPath, fFolder,
      ignoreFileNames,
      ignoreFilePaths,
      copyList);


    val errorMessage = mutable.MutableList[String]();
    val dry_run = p.isSet("dry-run")

    val force = p.isSet("force-copy-all")
    p.getValues("deploy-folder").get.toList.foreach(one => {

      println("Start to deploy folder:" + one)

      val confirmDir = {
        (dir: File) => {
          if (!dir.isDirectory && !dir.mkdirs)
            false
          else
            true

        }
      }

      val dRoot = new File(one)
      if (confirmDir(dRoot) != true) {
        errorMessage += "Could not create dir:" + one + ", give up all deploy for this folder!";

      }
      else {
        copyList.foreach {
          case (source, relativePath, fileName) => {
            val folder = new File(dRoot, relativePath)
            if (confirmDir(folder) == true) {
              val tFi = new File(folder, fileName)
              if (force || !tFi.isFile || source.lastModified() > tFi.lastModified()) {
                if(dry_run){
                  println("dry-run copy :" + source.getPath + " => " + tFi.getPath)
                }
                else{
                  Files.copy(source.toPath, tFi.toPath, REPLACE_EXISTING)
                  println("copy :" + source.getPath + " => " + tFi.getPath)
                }

              }

            }
            else {
              errorMessage += "Could not create dir:" + folder.getPath
            }
          }
        }


      }
    })

    if (errorMessage.size > 0) {
      println("Error :")
      errorMessage.foreach(println(_))
    }

  }
}
