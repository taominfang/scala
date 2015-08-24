package entry

import org.scalatest.FunSuite
import org.openfly._
/**
 * Created by mtao60 on 8/23/15.
 */
class ParameterParserTest extends FunSuite {

  test("testGetErrorMessage") {

    val p=new ParameterParser(Array[String]("-f",  "ss"  ,"ee", "cc", "-f", "22", "dd", "ff"));

    p.add(new Parameter("ff").addAliasKey("-f").setFollowingValueSize(3))
    p.add(new Parameter("require").setRequired(true).setFollowingValueSize(1).addAliasKey("-r"))

    p.parse()
    p.getErrorMessage().foreach(println(_))

    List((p.getValues("ff").get):_*).foreach(println(_))

  }

}
