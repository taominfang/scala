package org.openfly.tools.scala.syschronzie

import java.io.{FileWriter, FileReader, File}

import com.google.gson.{JsonObject, JsonParser}
import org.openfly._

import scala.util.Try

/**
 * Created by mtao60 on 8/18/15.
 */
object FirstCollectInfo {
  def main(argv:Array[String]): Unit ={
    val p=ParameterParser(argv);
    p.add( Parameter("file-list").addAliasKey("-F").setRequired(true).setFollowingValueSize(1));
    p.add(Parameter("out-file").addAliasKey("-o").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("target-folder").addAliasKey("-t").setRequired(true).setFollowingValueSize(1))
    p.parse();

    val es=p.getErrorMessage()

    if(es.size>0){
      es.foreach(println(_))
      println("error, quite")
      return
    }

    val fi=new File(p.getFirstValue("file-list").get)
    require(fi.isFile,fi.getPath+" is not a valid file!")

    val folder=new File(p.getFirstValue("target-folder").get)

    require(folder.isDirectory,folder.getPath+" is not folder");

    val fr=new FileReader(fi);
    try{
      val fl=new JsonParser().parse(fr).getAsJsonArray
      val outJson=new JsonObject;
      for(i1<- 0 until fl.size()){
        val fn=fl.get(i1).getAsString

        val t1=new File(folder,fn);
        if(t1.isFile){
          outJson.addProperty(fn,t1.lastModified());
        }

      }

      val fw=new FileWriter(p.getFirstValue("out-file").get)

      fw.write(outJson.toString)

      fw.close()
    }
    catch{
      case ex:Exception =>ex.printStackTrace()
    }
    finally {
      fr.close();
    }



  }

}
