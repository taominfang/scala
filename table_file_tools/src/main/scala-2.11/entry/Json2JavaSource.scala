package entry

import java.io.FileReader
import java.util.Scanner

import com.google.gson.{JsonObject, JsonElement, JsonParser}
import org.apache.commons.lang.StringEscapeUtils

/**
 * Created by mtao60 on 8/7/15.
 */
object Json2JavaSource {

  def main(arg: Array[String]): Unit = {

    var fp="";
    var vName="";

    if (arg.length >= 2) {
      fp = arg {
        0
      };
      vName = arg {
        1
      };
    }

    else {

      println("please give a json file path:");

      val scanner = new Scanner(System.in);





      fp = scanner.next;



      println("variable name:")


      vName = scanner.next();
    }

    val je = new JsonParser().parse(new FileReader(fp))

    val sb = new StringBuilder

    sb.append("StringBuilder ").append(vName).append(" = new StringBuilder();\n");


    toJavaGsonSource(null, je, sb, 0, vName, true)

    sb.append("return ").append(vName).append(".toString();\n");

    println(sb.toString())
  }

  def toJavaGsonSource(key: String, jObj: JsonElement,
                       sb: StringBuilder, level: Integer, vName: String, isLast: Boolean) {
    val blank = new StringBuilder
    for (i <- 0 until level) {
      blank.append("  ");
    }
    sb.append(vName).append(".append(\"")
    sb.append(blank);
    if (key != null) {
      sb.append(StringEscapeUtils.escapeJava(key)).append(":");
    }

    if (jObj.isJsonObject) {
      val jSet = jObj.getAsJsonObject().entrySet();

      if (jSet.size() > 0) {

        val i1 = jSet.iterator();


        sb.append("{").append("\");\n");


        while (i1.hasNext) {
          val one = i1.next();
          toJavaGsonSource(one.getKey, one.getValue, sb, level + 1, vName, !i1.hasNext);

        }
        sb.append(vName).append(".append(\"").append(blank).append("}");
        if (!isLast) {
          sb.append(",");
        }
        sb.append("\");\n")
      }
      else {
        sb.append("{}");

        if (!isLast) {
          sb.append(",");
        }
        sb.append("\");\n")

      }

    }
    else if (jObj.isJsonArray) {

      val ja = jObj.getAsJsonArray();
      if (ja.size() > 0) {
        sb.append("[").append("\");\n");

        for (i <- 0 until ja.size()) {
          toJavaGsonSource(null, ja.get(i), sb, level + 1, vName, i == ja.size() - 1);
        }
        sb.append(vName).append(".append(\"").append(blank).append("]");
        if (!isLast) {
          sb.append(",");
        }
        sb.append("\");\n")
      }
      else {
        sb.append("[]");

        if (!isLast) {
          sb.append(",");
        }
        sb.append("\");\n")
      }
    }
    else if (jObj.isJsonNull) {
      sb.append("null");
      if (!isLast) {
        sb.append(",");
      }
      sb.append("\");\n")
    }
    else if (jObj.isJsonPrimitive) {
      val jp = jObj.getAsJsonPrimitive
      if (jp.isString)
        sb.append(StringEscapeUtils.escapeJava("\"" + jp.getAsString + "\""))
      else {
        sb.append(jp.toString);
      }
      if (!isLast) {
        sb.append(",");
      }
      sb.append("\");\n")
    }


  }


}
