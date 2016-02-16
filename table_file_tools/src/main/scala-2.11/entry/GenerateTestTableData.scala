package entry

import java.io.{File, FileWriter}
import java.util.Random

import org.openfly.{Parameter, ParameterParser}

/**
 * Created by mtao60 on 2/14/16.
 */
object GenerateTestTableData {

  val random = new Random(System.currentTimeMillis())

  def randomeSize(min: Int, max: Int): Int = {
    val n = max - min;
    if (n == 0) {
      min
    }
    else
      min + random.nextInt(n)
  };

  def main(args: Array[String]) {
    val p = new ParameterParser(args)

    p.add(Parameter("help").addAliasKey("-h").setRequired(false).setFollowingValueSize(0))
    p.add(Parameter("output-folder").addAliasKey("-d").setRequired(true).setFollowingValueSize(1).setDefaultValue("/tmp"))
    p.add(Parameter("file-number").addAliasKey("-s").setRequired(true).setFollowingValueSize(1).setDefaultValue("1"))
    p.add(Parameter("file-name-start-index").addAliasKey("-fs").setRequired(true).setFollowingValueSize(1).setDefaultValue("1"))

    p.add(Parameter("file-name-pattern").addAliasKey("-fp").setRequired(true).setFollowingValueSize(1).setDefaultValue("%d.txt"))

    p.add(Parameter("line-size-min").addAliasKey("-minl").setRequired(true).setFollowingValueSize(1).setDefaultValue("100"))
    p.add(Parameter("line-size-max").addAliasKey("-maxl").setRequired(true).setFollowingValueSize(1).setDefaultValue("100"))
    p.add(Parameter("key-min").addAliasKey("-mink").setRequired(true).setFollowingValueSize(1).setDefaultValue("1"))
    p.add(Parameter("key-max").addAliasKey("-maxk").setRequired(true).setFollowingValueSize(1).setDefaultValue("10"))
    p.add(Parameter("key-pattern").addAliasKey("-kp").setRequired(true).setFollowingValueSize(1).setDefaultValue("%d"))
    p.add(Parameter("value-min").addAliasKey("-minv").setRequired(true).setFollowingValueSize(1).setDefaultValue("1"))
    p.add(Parameter("value-max").addAliasKey("-maxv").setRequired(true).setFollowingValueSize(1).setDefaultValue("100"))
    p.add(Parameter("value-pattern").addAliasKey("-vp").setRequired(true).setFollowingValueSize(1).setDefaultValue("%d"))
    p.add(Parameter("column-size").addAliasKey("-c").setRequired(true).setFollowingValueSize(1).setDefaultValue("2"))

    p.parse()

    if (p.isSet("help")) {
      println(s"Usage:\n${p.usage}")
      return
    }
    if (p.isError()) {
      p.errorMessages.foreach(println(_))
      println(s"Usage:\n${p.usage}")
      return
    }

    val outputDir = new File(p.getFirstValue("output-folder").get)

    if (!outputDir.isDirectory) {
      if (!outputDir.mkdirs()) {
        println(s"Could not make a dir :${outputDir.getPath}")
        return
      }
    }

    val fileNameStartIndex = p.getFirstValue("file-name-start-index").get.toInt
    val fileNamePatten = p.getFirstValue("file-name-pattern").get

    val lineMin = p.getFirstValue("line-size-min").get.toInt
    val lineMax = p.getFirstValue("line-size-max").get.toInt

    val keyMin = p.getFirstValue("key-min").get.toInt
    val keyMax = p.getFirstValue("key-max").get.toInt
    val keyPatten = p.getFirstValue("key-pattern").get

    val valMin = p.getFirstValue("value-min").get.toInt
    val valMax = p.getFirstValue("value-max").get.toInt
    val valPatten = p.getFirstValue("value-pattern").get

    val columnSize = p.getFirstValue("column-size").get.toInt

    for (i <- 0 until p.getFirstValue("file-number").get.toInt) {
      val index = i + fileNameStartIndex
      val fName = fileNamePatten.format(index)
      val fw = new FileWriter(new File(outputDir, fName))
      for (line <- 0 until randomeSize(lineMin, lineMax)) {
        val sb = new StringBuilder
        sb.append(keyPatten.format(randomeSize(keyMin, keyMax)))
        for (c <- 1 until columnSize) {
          sb.append("\t").append(valPatten.format(randomeSize(valMin, valMax)))
        }
        sb.append("\n")
        fw.write(sb.toString())
      }

      fw.close()
    }

  }
}
