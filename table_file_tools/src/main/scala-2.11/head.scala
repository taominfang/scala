import java.io.{FileReader, InputStreamReader, BufferedReader}

import org.openfly._

/**
 * Created by mtao60 on 12/11/15.
 */
object head {

  def main(args: Array[String]) {
    val p = ParameterParser(args)
    p.add(new Parameter("number-of-line").addAliasKey("-n").setRequired(false).setFollowingValueSize(1).setDefaultValue("10"))
    p.add(new Parameter("input-file-path").addAliasKey("-i").setRequired(false).setFollowingValueSize(1))
    p.parse()

    val n = p.getFirstValue("number-of-line").getOrElse("10").toLong
    var br: BufferedReader = null;

    p.getFirstValue("input-file-path") match {
      case Some(fp) => {
        br=new BufferedReader(new FileReader(fp))
      }
      case None=>{
        br=new BufferedReader(new InputStreamReader(System.in))
      }
    }

    var c:Long=0
    var line=br.readLine()
    while(c<n && line!=null){

      println(line)
      c+=1
      line=br.readLine()

    }
    br.close()

  }
}
