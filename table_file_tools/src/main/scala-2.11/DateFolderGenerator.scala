import java.util.Calendar

/**
 * Created by mtao60 on 5/14/16.
 */
object DateFolderGenerator {

  def main(args: Array[String]) {

    val format=if(args.length>0) args(0) else "yyyy/MM/dd/"
    val days=if(args.length>1) args(1).toInt else 0

    val dformator = new java.text.SimpleDateFormat(format);
    val d=Calendar.getInstance()

    d.add(Calendar.DATE,days)

    println(dformator.format(d.getTime))

  }
}
