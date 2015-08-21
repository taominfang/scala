package org.openfly.tools.scala.syschronzie

import java.io.{BufferedReader, FileReader, File}

import com.google.gson.JsonParser
import org.scalatest.FunSuite

/**
 * Created by mtao60 on 8/19/15.
 */
class SynchronizeChanged2Ftp$Test extends FunSuite {

  test("testFtpConnect") {
     SynchronizeChanged2Ftp.connectFtp("cubicalmonkey.org","minfang@cubicalmonkey.org","anvato123",true)
  }

  test("upload files") {

    val jFile=new File("/tmp/all_file.json");
    assert(jFile.isFile)

    val t1=new JsonParser().parse(new FileReader(jFile)).getAsJsonArray
    val fileList=new collection.mutable.MutableList[String]()

    for(i<-0 until t1.size()){
      fileList+=t1.get(i).getAsString
    }


    SynchronizeChanged2Ftp.connectFtp("cubicalmonkey.org","minfang@cubicalmonkey.org","anvato123",true)

    val ftpDir="/mobile-share-server-service"

    SynchronizeChanged2Ftp.confirmDir(ftpDir);

    SynchronizeChanged2Ftp.uploadFiles(fileList,ftpDir,"/tmp/light-php-framework")

    SynchronizeChanged2Ftp.disconnectFtp();




  }

  test("check update") {
    val skipNames=collection.mutable.Set[String]()
    try{
      val br=new BufferedReader(new FileReader("/tmp/skip.txt"))
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

    val checkResult= SynchronizeChanged2Ftp.checkUpdate("/tmp/light-php-framework","/tmp/status.json",skipNames,false)

    println("check result")

    checkResult.foreach(println(_))
  }

  test("testMakeDir") {

  }

  test("testConnectFtp") {

  }

}
