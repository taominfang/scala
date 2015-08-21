import org.openfly.Parameter

import scala.collection.mutable

/**
 * Created by mtao60 on 7/6/15.
 */
object TestParaser {

  def main(arg:Array[String]): Unit = {
    try
    {
      testMain(arg);
      println("done")
    }
    catch {
      case e:Exception=>{
        e.printStackTrace()
      }
      case err:Error=>{
        err.printStackTrace()
      }
    }
  }

  def testMain(arg: Array[String]): Unit = {



    println ("start")

    var args = mutable.MutableList[String]();

    var testNames = mutable.MutableList[String]();

    // Single flag, required
    var parameterParser=new org.openfly.ParameterParser();

    parameterParser.add(new Parameter("single-required"));

    args += "--single-required";
    testNames += "single-required";


    // Single flag required, but not set, should report error!

    parameterParser.add(new Parameter("single-required-no-set"));


    testNames += "single-required-no-set";



    //// single flag no-required
    parameterParser.add(new Parameter("single-no-required").setRequired(false));


    testNames += "single-no-required";


    /// values follow


    parameterParser.add(new Parameter("2values").setFollowingValueSize(2));
    args += "--2values";
    args += "value-1values";
    args += "value-2values";
    testNames += "2values";

    /// with default value

    parameterParser.add(new Parameter("with-default-value").setDefaultValue("defaultValue"));

    testNames += "with-default-value";

    /// with default values



    parameterParser.add(new Parameter("with-default-value").setDefaultValues(Array[String]("defaultValue1","defaultValue2")));

    testNames += "with-default-value";


    // with default values , but user give the new values

    parameterParser.add(new Parameter("with-default-value-user-give").setDefaultValues(Array[String]("defaultValue1","defaultValue2")));

    args += "--with-default-value-user-give";
    args += "user-1values";
    args += "user-2values";
    testNames += "with-default-value-user-give";


    // parameter name is different and with several aliases
    parameterParser.add(new Parameter("different-name").setFollowingValueSize(1).setLongKey("--dkey").addAliasKey("--ddkey").addAliasKey("-dk"));

    args += "--dkey";
    args += "dkey_value";

    args += "-dk";
    args += "dk_value";




    testNames += "different-name";


    /// dependent

    parameterParser.add(new Parameter("be-dependented1").setRequired(false));
    parameterParser.add(new Parameter("be-dependented2").setRequired(false));

    parameterParser.add(new Parameter("depend-test").setFollowingValueSize(1).setDependents(Array("be-dependented1","be-dependented2")));

    args += "--depend-test";
    args += "dependent-test-value";
    testNames += "depend-test";


    // against
    parameterParser.add(new Parameter("be-against1").setRequired(false).setDesc("If against-test is set, be-against1 can not be set!!"));
    parameterParser.add(new Parameter("be-against2"));

    parameterParser.add(new Parameter("against-test").setAgainsts(Array("be-against1","be-against2")).setRequired(false));

    args += "--against-test";
    args += "--be-against2";
    testNames += "against-test";


    /// arg validation

    parameterParser.add(new Parameter("need-number-integer").setFollowingValueSize(2).setArgValidation(1,(p)=>{p.argValidateIsNumber(1)}).setArgValidation(2,(p)=>{p.argValidateIsInteger(2)}))

    testNames += "need-number-integer";

    args += "--need-number-integer";
    args += "abcd";
    args += "23.4";


    parameterParser.add(new Parameter("need-integer-in-range").setDefaultValue("25").setArgValidation(1,(p)=>{
      if(p.argValidateIsInteger(1)==null){
        val v=Integer.parseInt( p.getValueAt(0).get);
        if(v >=20 && v<=30){
           null;
        }
        else{
           "Arg1 of " +p.getName+" should between 20 and 30 (both included)";
        }
      }
      else{
         "Arg1 of " +p.getName+" is not a integer"
      }
    }));

    testNames += "need-integer-in-range";
    args += "--need-integer-in-range";
    args += "50";

    ////////// parse /////////

    parameterParser.parse(args.toArray);

    if (parameterParser.isError()) {
      println("Error:");
      parameterParser.getErrorMessage().foreach(one => println(one));

      println("  <<<<<<  end error ");
      println();
    }

    println("-------- result --------");

    testNames.foreach(
      one => {
        if (parameterParser.isSet(one)) {
          println()
          println(one + "  is set!");

          val size = parameterParser.getValueSize(one);
          if (size != None && size.get > 0) {
            var c = 1;
            val vs = parameterParser.getValues(one).getOrElse({
              println("error"); Array[String]()
            })
            vs.foreach(
              v => {
                println(one + " " + c + ":" + v)
                c += 1;
              }

            );
          }

        }
        else {
          println(one + "  is NOT set!");
        }
      }
    );



    println( )
    println("Uage:")

    println (parameterParser.usage);
  }

}
