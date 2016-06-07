import java.io.{BufferedReader, FileReader, InputStreamReader}

import org.openfly._

/**
 * Created by mtao60 on 6/19/15.
 */
object PrintLineNumber {


  def main(arg: Array[String]): Unit = {

    val pParse=new ParameterParser();

    pParse.add(new Parameter("split-string").addAliasKey("-F").setDefaultValue("\t"));
    pParse.add(new Parameter("total-line-number").addAliasKey("-n").setDefaultValue("0"));
    pParse.add(new Parameter("number-base").addAliasKey("-b").setDefaultValue("1").setArgValidation(1,p1=>{
      val v=p1.getValueAt(0).getOrElse(null);
      if(v==null){
        "null value for "+p1.getName
      }
      else{
        if(v == "1" || v=="0"){
          null;
        }
        else{
          "value for "+p1.getName+" is only '1' or '0'"
        }
      }
    }));
    pParse.add(new Parameter("disable-line-number").setRequired(false));
    pParse.add(new Parameter("input-file-path").setRequired(false).setFollowingValueSize(1));

    pParse.parse(arg);

    if(pParse.isError()){
      println("There are some error in the command line:");

      pParse.getErrorMessage().foreach(println(_));

      println
      println (pParse.usage)


      return;
    }




    var br:BufferedReader=null;
    if(!pParse.isSet("input-file-path")){
      br=new BufferedReader(new InputStreamReader(System.in))
    }
    else{
      br=new BufferedReader(new FileReader(pParse.getFirstValue("input-file-path").get))
    }

    val F=pParse.getFirstValue("split-string").get;

    val n=Integer.parseInt(pParse.getFirstValue("total-line-number").getOrElse("0"));

    var c=0;

    var printLineNumber=true;
    if(pParse.isSet("disable-line-number")){
      printLineNumber=false;
    }

    val base=Integer.parseInt(pParse.getFirstValue("number-base").getOrElse("0"))
    var line=br.readLine();
    while((n==0 || c<=n) && line!=null )
    {

        if(printLineNumber){
          println("###################### "+(c+1)+" ######################");
        }
        val elements=line.split(F)

        for( ii <- 0 until elements.length){

          println((base+ii)+" -> ["+elements(ii)+"]")
        }


       line=br.readLine();
      c+=1;
    }
  }





}
