package org.openfly.tools.scala.syschronzie

import java.io._


import com.google.gson.{JsonObject, JsonParser}
import org.openfly.{Parameter, ParameterParser}


import scala.collection.mutable

/**
 * Created by mtao60 on 8/18/15.
 */
object SynchronizeChanged2Ftp {
  def main(argv:Array[String]): Unit = {
    val p = ParameterParser(argv);
    p.add(Parameter("files-status").addAliasKey("-F").setRequired(true).setFollowingValueSize(1));
    p.add(Parameter("source-folder").addAliasKey("-s").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("ftp-server").addAliasKey("-h").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("ftp-user").addAliasKey("-u").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("ftp-password").addAliasKey("-p").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("ftp-folder").addAliasKey("-t").setRequired(true).setFollowingValueSize(1))
    p.add(Parameter("force-copy-all").addAliasKey("-f").setRequired(false).setFollowingValueSize(0));
    p.add(Parameter("ftp-passive").addAliasKey("-fp").setRequired(false).setFollowingValueSize(0));

    p.add(Parameter("ignore-file-name").addAliasKey("-i").setRequired(false).setFollowingValueSize(1));
    p.add(Parameter("ignore-file-list-file").addAliasKey("-ifl").setRequired(false).setFollowingValueSize(1));
    p.parse();
    val es=p.getErrorMessage()

    if(es.size>0){
      es.foreach(println(_))
      println("error, quite")
      return
    }

    val sourceFolder=new File(p.getFirstValue("source-folder").get)

    require(sourceFolder.isDirectory,sourceFolder.getPath+" is not folder");

    val skipNames=collection.mutable.Set[String]()
    try{
      val br=new BufferedReader(new FileReader(p.getFirstValue("ignore-file-list-file").get))
      var line=br.readLine()
      while(line!=null){
        skipNames.add(line)
        line=br.readLine()
      }
      br.close();
    }
    catch{
      case _ =>{}
    }

    val updateFileList=checkUpdate(sourceFolder.getPath,p.getFirstValue("files-status").get,skipNames,p.isSet("force-copy-all"))

    if(updateFileList.size>0){
      connectFtp(p.getFirstValue("ftp-server").get,p.getFirstValue("ftp-user").get,p.getFirstValue("ftp-password").get,p.isSet("ftp-passive"))
      uploadFiles(updateFileList,p.getFirstValue("ftp-folder").get,sourceFolder.getPath)
      disconnectFtp()
      println("upload file finished, total file uploaded:"+updateFileList.size)
    }
    else{
      println("do not find any file need upload!")
    }
    //connect ftp


  }
  val ftpConnect=new org.apache.commons.net.ftp.FTPClient
  def connectFtp(host:String,user:String,password:String,ftp_passive:Boolean) ={
    ftpConnect.connect(host)
    require(ftpConnect.login(user,password),"can not login to ftp")

    ftpConnect.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)

    if(ftp_passive){
      ftpConnect.enterLocalPassiveMode()
    }
    else{
      ftpConnect.enterLocalActiveMode()
    }

    println("ftp login!")



    true
  }

  def disconnectFtp(): Unit ={
    ftpConnect.logout();
  }

  def checkUpdate(root:String,status:String,skipFiles:collection.mutable.Set[String],force:Boolean)={
    val allList=new mutable.MutableList[File]()


    val r=new File(root);
    require(r.isDirectory,r.getPath+" is not a folder");
    FindAllFiles.findFiles(r,allList,skipFiles)
    val statusMap=collection.mutable.Map[String,Long]();
    try{
      val statusFile=new File(status)
      if(statusFile.isFile){
        val t3=new FileReader(statusFile)
        val t1=new JsonParser().parse(t3).getAsJsonObject.entrySet().iterator()

        while(t1.hasNext){
          val t2=t1.next();
          statusMap.update(t2.getKey,t2.getValue.getAsLong)
        }
        t3.close()

      }
    }
    catch {
      case _ =>{}
    }

    val rootLen=if(root.charAt(root.length-1)=='/')root.length else root.length+1
    val updateFiles=mutable.MutableList[String]();
    allList.foreach{
      file=>{
        val name=file.getPath.substring(rootLen);
        if(! statusMap.contains(name) || statusMap(name)!=file.lastModified() || force){
          updateFiles+=name
        }
        statusMap.update(name,file.lastModified())
      }
    }

    //wrtie back to status file
    val jStatus=new JsonObject
    statusMap.foreach(st=>{
      jStatus.addProperty(st._1,st._2)
    })
    val fw=new FileWriter(status)
    fw.write(jStatus.toString)
    fw.close()




    updateFiles

  }



  def confirmDir(dir:String): Unit ={
    require(dir.charAt(0)=='/',"we need abstract path! it should start /, but we got:"+dir)
    val paths=dir.split("/").toList

    var p="";

    paths.foreach{
      a=> {
        if (a != "") {
          p = p +"/"+ a;
          if (!ftpConnect.changeWorkingDirectory(p)) {
            ftpConnect.makeDirectory(p)
            require(ftpConnect.changeWorkingDirectory(p), "Could not create the dir:" + p)
            println("mkdir and switch to:" + p)
          }
          else {
            println("switch to path:" + p)
          }
        }
      }
    }

  }

  def uploadFiles(fileList:mutable.MutableList[String],_ftpRoot:String,_localRoot:String): Unit ={

     //currentDirs.foreach(println(_))

    val removeLastSlish={(f:String)=>if(f.charAt(f.length-1)=='/') f.substring(0,f.length-2) else f}

    val ftpRoot=removeLastSlish(_ftpRoot)
    val localRoot=removeLastSlish(_localRoot);

    fileList.foreach{
      name=>{
        val ftpFilePath=ftpRoot+"/"+name

        val (dir, fn)=parseFullPath2DirAndFileName(ftpFilePath)
        confirmDir(dir);
        val localFilePath=localRoot+"/"+name
        val input=new FileInputStream(localFilePath)
        ftpConnect.storeFile(fn,input)
        println("uploaded:"+localFilePath+" -> "+ftpFilePath);

      }
    }




  }

  def parseFullPath2DirAndFileName(path:String)={
    val lastSlish=path.lastIndexOf('/');


    if(lastSlish == -1){
      ("",path)
    }
    else {
      (path.substring(0,lastSlish),path.substring(lastSlish+1))
    }
  }

  def uploadFile(ftpFullPath:String,input:InputStream): Unit ={

    val lastSlish=ftpFullPath.lastIndexOf('/');


    if(lastSlish == -1){
      println("uploading "+ftpFullPath)
      ftpConnect.storeFile(ftpFullPath,input)
    }
    else {
      val path=ftpFullPath.substring(0,lastSlish);
      val fileName=ftpFullPath.substring(lastSlish+1);
      ftpConnect.changeWorkingDirectory(path);
      println("Change dir to:"+path)

      ftpConnect.storeFile(fileName,input)
      println("Done uploading for "+fileName)
    }


  }





}

object FileSet{
  val root=new FileSet()

  def add(path:String): Unit ={

    val paths=if (path.charAt(0)=='/') path.substring(1).split("/") else path.split("/")

    root.add("","",path,paths,0)

  }

  def getRoot={
    root
  }

  def navigate(one:FileSet,space:String): Unit ={
    println(space+"folder:"+one.my_name)
    println(space+"folder full path:"+one.my_full_path)
    val newSpace=space+"  ";
    one.fileNames.foreach{fn=>println(newSpace+"File:"+fn._2)}
    one.subFolders.foreach{
      p=>{

        navigate(p._2,newSpace)
      }
    }
    println(space)
  }
}

class FileSet(){


  import collection.mutable._
  val subFolders:Map[String,FileSet] = Map()
  val fileNames=mutable.Map[String,String]();
  var my_name:String=_
  var my_parent:String=_
  var my_full_path:String=_

  def add(myPatern:String,myName:String,oPath:String,path:Array[String],start:Int): Unit ={
    if(path==null || path.length-start<=0){
      return;
    }
    my_name=myName
    my_parent=myPatern
    my_full_path=if(my_parent=="") my_name else my_parent+"/"+my_name

    val len=path.length-start
    val fName=path(start);
    if(len>1){

      if(!subFolders.contains(fName)){
        subFolders.update(fName,new FileSet);
      }
      subFolders.get(fName).get.add(my_full_path,fName,oPath,path,start+1)
    }
    else{
      fileNames.update(fName,oPath);
    }

  }


}
