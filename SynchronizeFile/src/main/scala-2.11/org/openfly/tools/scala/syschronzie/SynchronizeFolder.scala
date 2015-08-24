package org.openfly.tools.scala.syschronzie

import java.io.File
import java.nio.file.StandardCopyOption._
import java.nio.file.{Files, Path}

import org.openfly._

import scala.collection.mutable

/**
 * Created by mtao60 on 8/23/15.
 */
object SynchronizeFolder {

  def collectCopyList(targetRootPath:String,tFolder: File, sFolder: File, copyList: mutable.MutableList[(Path, Path)], force: Boolean, ignoreFileNames: Set[String], ignoreFilePaths:Set[String]): Unit = {
    val subs=List(tFolder.listFiles():_*)
    subs.foreach(one=>{
      val fileName=one.getName
      val filePath=one.getPath
      val relativePath=filePath.substring(targetRootPath.length+1)
      if(fileName=="." || fileName==".."){
        //ignore
      }
      else if(ignoreFileNames.contains(fileName)){
        //ignore
      }
      else if(ignoreFilePaths.contains(relativePath)){
        //ignore
      }
      else {
        val s=new File(sFolder,fileName)
        if(s.isFile && one.isFile && (force || s.lastModified()> one.lastModified())){
          copyList += new Tuple2(s.toPath,one.toPath);
        }
        else if(s.isDirectory && one.isDirectory){
          collectCopyList(targetRootPath,one,s,copyList,force,ignoreFileNames,ignoreFilePaths)
        }
      }
    })

  }

  def main(argv:Array[String]): Unit ={
    val p=new ParameterParser(argv)
    p.add(Parameter("source-folder").addAliasKey("-s").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("target-folder").addAliasKey("-t").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("force-copy-all").addAliasKey("-f").setRequired(false).setFollowingValueSize(0));
    p.add(Parameter("ignore-file-name").addAliasKey("-in").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("ignore-file-path").addAliasKey("-ip").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("ignore-file-list-file").addAliasKey("-if").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("help").setRequired(false).setFollowingValueSize(0))

    p.parse();

    if(p.isSet("help")){
      println ("Usage:"+p.usage)
      return
    }

    val es=p.getErrorMessage()

    if(es.size>0){
      es.foreach(println(_))
      println("error, quite")
      println ("Usage:"+p.usage)
      return
    }
    val sFolder=new File(p.getFirstValue("source-folder").get)

    require(sFolder.isDirectory,sFolder.getPath+" should be the dir")

    val tFolder=new File(p.getFirstValue("target-folder").get)
    require(tFolder.isDirectory,tFolder.getPath+" should be the dir")


    val copyList=new mutable.MutableList[(Path,Path)];

    val ignoreFileNames= Set[String]((p.getValues("ignore-file-name").getOrElse(Array[String]())):_*)

    val ignoreFilePaths =Set[String]((p.getValues("ignore-file-path").getOrElse(Array[String]())):_*)



    collectCopyList(tFolder.getPath,tFolder,sFolder,copyList,p.isSet("force-copy-all"),ignoreFileNames,ignoreFilePaths)


    copyList.foreach(one =>{
      println(one._1+" -> "+one._2)
      Files.copy(one._1,one._2,REPLACE_EXISTING)
    })
  }
}
