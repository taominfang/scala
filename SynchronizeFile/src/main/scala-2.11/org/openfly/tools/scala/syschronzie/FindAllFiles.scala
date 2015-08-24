package org.openfly.tools.scala.syschronzie

import com.google.gson.JsonPrimitive
import org.openfly.{Parameter, ParameterParser}

import java.io.{FileWriter, File}

import scala.collection.mutable
;
/**
 * Created by mtao60 on 8/18/15.
 */
object FindAllFiles {

  def main(args:Array[String]): Unit ={
    val p=new ParameterParser();

    p.add(new Parameter("root-folder").addAliasKey("-R").setFollowingValueSize(1).setRequired(true));
    p.add(new Parameter("output-file").addAliasKey("-o").setFollowingValueSize(1).setRequired(true));
    p.add(new Parameter("ignore-file").addAliasKey("-i").setFollowingValueSize(1).setRequired(false));

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




    val filters= collection.mutable.Set[String]((p.getValues("ignore-file").getOrElse(Array[String]()).toSet).toArray:_*)
    //(.toArray:_*)

    val root=new File(p.getFirstValue("root-folder").get)

    require(root.isDirectory,root.getPath+" is not a folder")


    val allFiles=mutable.MutableList[File]();

    findFiles(root,allFiles,filters)
    val files=for{n<-allFiles}
      yield{
        n.getPath.substring(root.getPath.length+1)
      }


    val result=new com.google.gson.JsonArray
    files.foreach(one=>{
      result.add(new JsonPrimitive(one))
      println(one)
    })

    println(result.toString)
    val fw=new FileWriter(p.getFirstValue("output-file").get)

    fw.write(result.toString)

    fw.close()


    println("total:"+allFiles.size)


  }

  def findFiles(folder:File,list:mutable.MutableList[File],filters:collection.mutable.Set[String]): Unit ={
    val sub=folder.list();

    sub.foreach(one=>{
      val newOne=new File(folder,one)
      if(!filters.contains(one)) {


        if (newOne.isFile) {
          list += newOne
        }
        else if (newOne.isDirectory) {
          findFiles(newOne, list, filters);
        }
      }
    })
  }

}
