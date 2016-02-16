import java.io.File

import org.openfly.{Parameter, ParameterParser}

import scala.util.matching.Regex

/**
 * Created by mtao60 on 2/7/16.
 */
object NumberizeFileNames {

  def ChangeName(folder: File, one: File, newName: String) = {
    if (newName == one.getName)
      one
    else {
      val newFile = new File(folder, newName)

      if (newFile.exists()) {
        println(newFile.getPath + " is exists" + one.getPath + " can not change to it  ignore it")
        one
      }
      else {
        if (one.renameTo(newFile)) {
          newFile
        }
        else {
          println(one.getPath + " can not change to " + newFile.getPath)
          one
        }

      }
    }


  }

  var counter=0;
  def reCurChangeName(folder: File): Unit = {
    val files = folder.listFiles();

    files.foreach(one => {





      if (one.isDirectory) {


        reCurChangeName(one)

      }
      else if (one.isFile) {
        val name = one.getName;


        val f=srcRegx.findAllIn(name);
        if(f.hasNext){
          val newName = changeToString.format(counter);
          counter+=1;
          ChangeName(folder, one, newName)
        }
        else{
          println(name+" is not match!")
        }


      }
    })
  }

  var srcRegx: Regex = null;
  var subDir: Boolean = false;

  var changeToString: String = null;
  var matchString: String = null;

  def main(args: Array[String]) {
    val p = new ParameterParser
    p.add(new Parameter("folder").setRequired(true).setFollowingValueSize(1).addAliasKey("-f"))
    p.add(new Parameter("matched-string").setRequired(true).addAliasKey("-m").setFollowingValueSize(1))
    p.add(new Parameter("change-to").setRequired(true).setFollowingValueSize(1).addAliasKey("-c"))
    p.add(new Parameter("is-include-sub-dir").addAliasKey("-s").setRequired(false).setFollowingValueSize(0))
    p.add(new Parameter("help").addAliasKey("-h").setRequired(false).setFollowingValueSize(0))

    p.parse(args)

    if (p.isError()) {
      p.getErrorMessage().foreach(println(_))
      println("Usage:")
      println(p.usage)
      return
    }
    if (p.isSet("help")) {
      println("Usage:")
      println(p.usage)
      return
    }


    matchString = p.getFirstValue("matched-string").get;

    srcRegx = matchString.r;
    println("reg:" + srcRegx)


    subDir = if (p.isSet("is-include-sub-dir")) true; else false;


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
