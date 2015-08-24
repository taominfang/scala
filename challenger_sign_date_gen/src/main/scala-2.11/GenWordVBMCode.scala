import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.openfly.{ParameterParser,Parameter}

import scala.collection.mutable

/**
 * Created by mtao60 on 8/22/15.
 */
object GenWordVBMCode {

  def main(argv:Array[String]): Unit ={
    val p=ParameterParser(argv);
    p.add(new Parameter("start-date").setRequired(true).addAliasKey("-s").setFollowingValueSize(1).setDesc("format:yyyy-MM-dd") )
    p.add(new Parameter("end-date").setRequired(true).addAliasKey("-e").setFollowingValueSize(1).setDesc("format:yyyy-MM-dd") )

    p.add(new Parameter("ignore-date").setRequired(false).addAliasKey("-i").setFollowingValueSize(1).setDesc("format:yyyy-MM-dd") )

    p.add(new Parameter("ignore-date-range").setRequired(false).addAliasKey("-r").setFollowingValueSize(2).setDesc("format: yyyy-MM-dd yyyy-MM-dd") )


    p.parse()

    if(p.errorMessages.size>0){
      println("Error:")
      p.getErrorMessage().foreach(println(_))
      return
    }

    val dFormat=new SimpleDateFormat("yyyy-MM-dd");

    var start=dFormat.parse(p.getFirstValue("start-date").get).getTime
    val end=dFormat.parse(p.getFirstValue("end-date").get).getTime

    val igoreDate=collection.mutable.Set[Long]();


    List[String]((p.getValues("ignore-date").getOrElse(Array[String]())):_*).foreach(
      one=>igoreDate.add(dFormat.parse(one).getTime)
    )

    val ir=p.getValues("ignore-date-range").getOrElse(Array[String]())

    for(i<-0 until ir.length/2){
      val index1=i*2
      val index2=i*2+1

      var s=dFormat.parse(ir(index1)).getTime
      var e=dFormat.parse(ir(index2)).getTime

      if(e>s){
        do{
          igoreDate.add(s);
          s+=(24*60*60)*1000
        }while(s<=e)
      }
      else{
        do{
          igoreDate.add(e);
          e+=(24*60*60*1000)
        }while(e<=s)
      }

    }


    val dOutput=new SimpleDateFormat("MM/dd/yyyy")


    println("' start:"+dOutput.format(new Date(start)))
    println("' end (included):"+dOutput.format(new Date(end)))

    igoreDate.toList.sorted.foreach(one=>{println("' ignore:"+dFormat.format(new Date(one)))})

    val allDate=mutable.MutableList[String]();
    while(start<=end){
      val d=new Date(start);
      val cal= Calendar.getInstance();
      cal.setTime(d);

      if(cal.get(Calendar.DAY_OF_WEEK)==1 || cal.get(Calendar.DAY_OF_WEEK)==7 || igoreDate.contains(start)){

      }
      else{
        allDate+=(dOutput.format(d))
      }

      start+=60*60*24*1000
    }

    println("Dim aStrings(1 To "+allDate.size+") As String")




    var index=1;
    allDate.foreach(one=>{
      println("aStrings("+index+") = \""+one+"\"")
      index+=1
    })
  }
}
