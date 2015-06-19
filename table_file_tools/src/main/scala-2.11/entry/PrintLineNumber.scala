package entry

import java.io.{FileReader, InputStreamReader, BufferedReader}

import scala.collection.mutable.Map

/**
 * Created by mtao60 on 6/19/15.
 */
object PrintLineNumber {

  var parameters = Map("-i" -> null, "-F" -> "\t","-n"->"0","-v"->null,"-b"->"1");

  def main(arg: Array[String]): Unit = {


    parametersParse(arg)

    var br:BufferedReader=null;
    if(parameters("-i") == null){
      br=new BufferedReader(new InputStreamReader(System.in))
    }
    else{
      br=new BufferedReader(new FileReader(parameters("-i")))
    }

    val F=parameters("-F");

    val n=Integer.parseInt(parameters("-n"));

    var c=0;

    val base=Integer.parseInt(parameters("-b"))
    var line=br.readLine();
    while((n==0 || c<=n) && line!=null )
    {

        val elements=line.split(F)

        for( ii <- 0 until elements.length){

          println((base+ii)+" -> ["+elements(ii)+"]")
        }


       line=br.readLine();
      c+=1;
    }
  }

  def parametersParse(arg: Array[String]): Unit = {


    var i1 = 0;


    while (i1 < arg.length) {

      if (arg(i1) == "-i") {
        i1 += 1;
        if (i1 < arg.length) {
          parameters(arg(i1-1)) = arg(i1)
        }

      }


      else if (arg(i1) == "-F") {
        i1 += 1;
        if (i1 < arg.length) {
          parameters(arg(i1-1)) = arg(i1)
        }

      }

      else if (arg(i1) == "-n") {
        i1 += 1;
        if (i1 < arg.length) {
          parameters(arg(i1-1)) = arg(i1)
        }

      }

      else if (arg(i1) == "-b") {
        i1 += 1;
        if (i1 < arg.length) {
          parameters(arg(i1-1)) = arg(i1)
        }

      }

      else if (arg(i1) == "-v") {

          parameters(arg(i1)) = "true"


      }



      i1+=1;


    }

  }
}
