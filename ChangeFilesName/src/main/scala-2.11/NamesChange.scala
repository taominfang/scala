import java.io.File

import org.openfly.{ParameterParser, Parameter}

import scala.util.matching.Regex

/**
 * Created by mtao60 on 2/6/16.
 */
object NamesChange {

  def ChangeName(folder: File, one: File, newName: String) = {
    if(newName==one.getName)
      one
    else{
      val newFile=new File(folder,newName)

      if(newFile.exists()){
        println(newFile.getPath+" is exists"+one.getPath+" can not change to it  ignore it")
        one
      }
      else{
        if(one.renameTo(newFile)){
          newFile
        }
        else{
          println(one.getPath+" can not change to "+newFile.getPath)
          one
        }

      }
    }


  }


  def reCurChangeName(folder: File): Unit = {
    val files = folder.listFiles();

    files.foreach(one => {

      val name = one.getName;
      var newName: String = null;

      if (srcRegx != null) {
        newName = srcRegx.replaceAllIn(name, changeToString)
      }
      else {
        newName = name.replace(matchString, changeToString)
      }




      if (one.isDirectory) {

        if(changeDir){
          val n=ChangeName(folder,one,newName);
          if(subDir)
            reCurChangeName(n)
        }
        else{
          reCurChangeName(one)
        }
      }
      else if (one.isFile) {
        ChangeName(folder,one,newName)
      }
    })
  }

  var srcRegx: Regex = null;
  var subDir: Boolean = false;
  var changeDir: Boolean = false;
  var changeToString: String = null;
  var matchString: String = null;

  def main(args: Array[String]) {
    val p = new ParameterParser
    p.add(new Parameter("folder").setRequired(true).setFollowingValueSize(1).addAliasKey("-f"))
    p.add(new Parameter("matched-string").setRequired(true).addAliasKey("-m").setFollowingValueSize(1))
    p.add(new Parameter("change-to").setRequired(true).setFollowingValueSize(1).addAliasKey("-c"))
    p.add(new Parameter("is-regex-pattern").addAliasKey("-p").setRequired(false).setFollowingValueSize(0))
    p.add(new Parameter("is-include-sub-dir").addAliasKey("-s").setRequired(false).setFollowingValueSize(0))
    p.add(new Parameter("is-change-dir").addAliasKey("-d").setRequired(false).setFollowingValueSize(0))
    p.add(new Parameter("help").addAliasKey("-h").setRequired(false).setFollowingValueSize(0))

    p.parse(args)

    if (p.isError()) {
      p.getErrorMessage().foreach(println(_))
      println("Usage:")
      println(p.usage)
      return
    }
    if(p.isSet("help")){
      println("Usage:")
      println(p.usage)
      return
    }


    matchString = p.getFirstValue("matched-string").get;
    if (p.isSet("is-regex-pattern")) {
      srcRegx = matchString.r;
      println("reg:"+srcRegx)
    }

    subDir = if (p.isSet("is-include-sub-dir")) true; else false;

    changeDir = if (p.isSet("is-change-dir")) true; else false;

    changeToString = p.getFirstValue("change-to").get
    val rootFolder = new File(p.getFirstValue("folder").get)

    if (rootFolder.isDirectory) {
      reCurChangeName(rootFolder)
    }
    else {
      println(rootFolder.getPath + " is not a folder")
      println("Usage:")
      println(p.usage)
    }


  }
}
