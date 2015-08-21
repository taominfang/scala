package org.openfly.tools.scala.syschronzie

import java.io.{FileWriter, FileReader, File}

import java.nio.file.Files
import  java.nio.file.StandardCopyOption._;

import com.google.gson.{JsonObject, JsonParser}
import org.openfly.{Parameter, ParameterParser}

/**
 * Created by mtao60 on 8/18/15.
 */
object SynchronizeChangedFile {
  def main(argv:Array[String]): Unit = {
    val p = ParameterParser(argv);
    p.add(Parameter("files-status").addAliasKey("-F").setRequired(true).setFollowingValueSize(1));
    p.add(Parameter("source-folder").addAliasKey("-s").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("target-folder").addAliasKey("-t").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("force-copy-all").addAliasKey("-f").setRequired(false).setFollowingValueSize(0));
    p.parse();
    val es=p.getErrorMessage()

    if(es.size>0){
      es.foreach(println(_))
      println("error, quite")
      return
    }
    val fi=new File(p.getFirstValue("files-status").get)
    require(fi.isFile,fi.getPath+" is not a valid file!")

    val targetFolder=new File(p.getFirstValue("target-folder").get)

    require(targetFolder.isDirectory,targetFolder.getPath+" is not folder");
    val sourceFolder=new File(p.getFirstValue("source-folder").get)

    require(sourceFolder.isDirectory,sourceFolder.getPath+" is not folder");

    val fr=new FileReader(fi);
    try{
      val info=new JsonParser().parse(fr).getAsJsonObject
      fr.close();

      val i1=info.entrySet().iterator();
      val force=p.isSet("force-copy-all");
      while(i1.hasNext){
        val one=i1.next();
        val fn=one.getKey
        val mt=one.getValue.getAsLong

        val fInfo=new File(sourceFolder,fn);
        println("check:"+fInfo.getPath)
        if(fInfo.isFile){
          if(fInfo.lastModified()!=mt || force){
            val targetFile=new File(targetFolder,fn)
            println("copy file from :"+fInfo.getPath+" to:"+targetFile.getPath)
            copyFile(fInfo,targetFile)
          }
          else{
            info.addProperty(fn,fInfo.lastModified())
          }
        }

      }


      val fw=new FileWriter(p.getFirstValue("files-status").get)

      fw.write(info.toString)

      fw.close()
    }
    catch{
      case ex:Exception =>ex.printStackTrace()
    }


  }

  def copyFile(s:File,t:File): Unit ={
    if(!t.isFile){


      makeDir(t.getParentFile);

    }
    Files.copy(s.toPath,t.toPath,REPLACE_EXISTING)
  }

  def makeDir(d:File): Unit ={
    require(d!=null,"parent is null!");
    if(!d.isDirectory){
      val p=d.getParentFile
      makeDir(p);
      d.mkdir()
    }
  }






}
