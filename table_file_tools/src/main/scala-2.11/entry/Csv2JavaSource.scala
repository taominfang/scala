package entry

import java.io.{FileReader, BufferedReader}
import java.util.Scanner

import org.apache.commons.lang.StringEscapeUtils

/**
 * Created by mtao60 on 8/7/15.
 */
object Csv2JavaSource {

  def main(args: Array[String]): Unit = {
    var csvFP = "";
    var header = "";
    var data = "";
    if (args.length >= 3) {
      csvFP = args {
        0
      };
      header = args {
        1
      };
      data = args {
        2
      }
    }
    else {
      val scanner = new Scanner(System.in);
      println("csv file path:");
      csvFP = scanner.next();

      println("Header Variable Name:")

      header = scanner.next()

      println("Data Variable Name:")

      data = scanner.next()
    }

    val fr = new BufferedReader(new FileReader(csvFP))

    val firstLn = fr.readLine();


    val firstLine = stringArray2JavaCode(csvLine2StringArray(firstLn));

    println("String [] " + header + " = " + firstLine + ";\n")
    var line: String = fr.readLine();

    var sb=new StringBuilder
    var index=0;
    while (line != null) {


      sb.append("String [] templateStringArray"+index+" = " + stringArray2JavaCode(csvLine2StringArray(line)) + ";").append("\n");
      sb.append( data+"["+index+ "]=templateStringArray"+index+";").append("\n");
      line = fr.readLine();
      index+=1;
    }

    println("String [] [] "+data+" = new String["+index+"][];")

    println(sb.toString())







    fr.close();

  }

  def csvLine2StringArray(line: String) = {
    var re = Array[String]();

    val t1 = new StringBuilder
    line.toCharArray.foreach(f => {
      if (f == '\t') {
        re :+= t1.toString()
        t1.setLength(0)
      }
      else {
        t1.append(f)
      }
    })
    re :+= t1.toString()

    re
  }

  def stringArray2JavaCode(arr: Array[String]) = {
    val sb = new StringBuilder("{")
    var c = 0;
    arr.foreach(one => {
      if (c != 0) {
        sb.append(",")
      }
      c += 1;
      sb.append('"').append(StringEscapeUtils.escapeJava((one.replace("\"", "")))).append('"');

    })

    sb.append("}");
    sb.toString()
  }
}
