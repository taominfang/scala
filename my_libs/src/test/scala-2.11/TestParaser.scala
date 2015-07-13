import scala.collection.mutable

/**
 * Created by mtao60 on 7/6/15.
 */
object TestParaser {

  def main(arg: Array[String]): Unit = {

    var args = mutable.MutableList[String]();

    var testNames = mutable.MutableList[String]();

    // Single flag, required

    ParameterParser.add(new Parameter("single-required"));

    args += "--single-required";
    testNames += "single-required";


    // Single flag required, but not set, should report error!

    ParameterParser.add(new Parameter("single-required-no-set"));


    testNames += "single-required-no-set";



    //// single flag no-required
    ParameterParser.add(new Parameter("single-no-required").setRequired(false));


    testNames += "single-no-required";


    /// values follow


    ParameterParser.add(new Parameter("2values").setFollowingValueSize(2));
    args += "--2values";
    args += "value-1values";
    args += "value-2values";
    testNames += "2values";

    /// with default value

    ParameterParser.add(new Parameter("with-default-value").setDefaultValue("defaultValue"));

    testNames += "with-default-value";

    /// with default values



    ParameterParser.add(new Parameter("with-default-value").setDefaultValues(Array[String]("defaultValue1","defaultValue2")));

    testNames += "with-default-value";


    // with default values , but user give the new values

    ParameterParser.add(new Parameter("with-default-value-user-give").setDefaultValues(Array[String]("defaultValue1","defaultValue2")));

    args += "--with-default-value-user-give";
    args += "user-1values";
    args += "user-2values";
    testNames += "with-default-value-user-give";


    // parameter name is different and with several aliases
    ParameterParser.add(new Parameter("different-name").setFollowingValueSize(1).setLongKey("--dkey").addAliasKey("--ddkey").addAliasKey("-dk"));

    args += "--dkey";
    args += "dkey_value";

    args += "-dk";
    args += "dk_value";




    testNames += "different-name";


    /// dependent

    ParameterParser.add(new Parameter("be-dependented1").setRequired(false));
    ParameterParser.add(new Parameter("be-dependented2").setRequired(false));

    ParameterParser.add(new Parameter("depend-test").setFollowingValueSize(1).setDependents(Array("be-dependented1","be-dependented2")));

    args += "--depend-test";
    args += "dependent-test-value";
    testNames += "depend-test";


    // against
    ParameterParser.add(new Parameter("be-against1").setRequired(false));
    ParameterParser.add(new Parameter("be-against2"));

    ParameterParser.add(new Parameter("against-test").setAgainsts(Array("be-against1","be-against2")).setRequired(false));

    args += "--against-test";
    args += "--be-against2";
    testNames += "against-test";

    ////////// parse

    ParameterParser.parse(args.toArray);

    if (ParameterParser.isError()) {
      println("Error:");
      ParameterParser.getErrorMessage().foreach(one => println(one));

      println("  <<<<<<  end error ");
      println();
    }

    println("-------- result --------");

    testNames.foreach(
      one => {
        if (ParameterParser.isSet(one)) {
          println()
          println(one + "  is set!");

          val size = ParameterParser.getValueSize(one);
          if (size != None && size.get > 0) {
            var c = 1;
            val vs = ParameterParser.getValues(one).getOrElse({
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

  }

}
