package org.openfly.tools.scala.syschronzie

import java.io.{FileReader, File}

import com.google.gson.JsonParser
import org.scalatest.FunSuite

/**
 * Created by mtao60 on 8/19/15.
 */
class FileSetTest extends FunSuite {

  test("testSubFolders") {

  }

  test("testFileNames") {

  }

  test("testAdd") {

  }

  test("testRoot") {

    val jFile=new File("/tmp/file_list.json");
    assert(jFile.isFile)

    val fileList=new JsonParser().parse(new FileReader(jFile)).getAsJsonArray
    for(i<-0 until fileList.size()){
      FileSet.add(fileList.get(i).getAsString)
    }

    val root=FileSet.getRoot

    FileSet.navigate(root,"")

  }

  test("testGetRoot") {

  }

}
