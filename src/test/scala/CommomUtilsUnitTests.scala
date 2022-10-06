import com.typesafe.config.ConfigFactory
import org.CS441HW1.CommonUtils.CommonFunctions
import org.scalatest.flatspec.AnyFlatSpec

class CommomUtilsUnitTests extends AnyFlatSpec{
  // Unit Test to confirm if an integer value is returned which corresponds to the timestamp of grouped interval
  private val timeStamp: String = "17:01:36.728"
  private val intervalLength: Int = 5
  private val testGrpNum: String = "16579"
  private val testTimeStamp:String = "17:01:35.000"
  private val config = ConfigFactory.load()
  private val testPattern = "([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}"

  it should "Unit Test to Confirm if the integer value corresponds to appropriate group number" in {
    val token:Array[String] = Array(timeStamp, "[main]", "INFO", "HelperUtils.Parameters$", "F8rbg2ag3M7hB9kcf1be2U5k")
    val grpNum = CommonFunctions.getTimeSlot(token, intervalLength)
    assert(grpNum.toString.equals(testGrpNum))
  }

  // Unit Test to confirm if the grouped value returns the correct time stampvalue (correct till HH:mm:ss )
  it should "Unit Test to Confirm if the integer value corresponds to appropriate Time Stamp" in {
    val convertedTimeStamp = CommonFunctions.convertToTimeStamp(testGrpNum.toInt, intervalLength)
    println(convertedTimeStamp)
    assert(convertedTimeStamp.equals(testTimeStamp))
  }

  // Unit Test to get the config for pattern and checking that it matched the expected pattern
  it should "Unit Test to get the config for pattern and checking that it matched the expected pattern" in {
    val patternString:String = config.getString("HW1_Mapred.Pattern")
    assert(patternString.equals(testPattern))
  }

  // Unit Test to determine if the log injected log pattern is matched
  it should "Unit Test to determine if the log injected log pattern is matched" in {
    val injectedSamplePatternString: String = "dr`s$66,Zwi)s*|!cQW6qae2G5gcg1K8o5PV}}h}Reoc$$eT6[P"
    val patternString = config.getString("HW1_Mapred.Pattern").r
    assert(patternString.findFirstMatchIn(injectedSamplePatternString) match
      case Some(_) => true
      case None => false)
  }

  // Unit Test to determine if the injected log pattern is not found when not injected
  it should "Unit Test to determine if the log injected log pattern is not matched" in {
    val injectedSamplePatternString: String = "l9]|92!uHUQ/IVcz~(;.Uz%K*5jTUd08"
    val patternString = config.getString("HW1_Mapred.Pattern").r
    assert(patternString.findFirstMatchIn(injectedSamplePatternString) match
      case Some(_) => false
      case None => true)
  }

  // Unit Test to determine if the numOfMappers is returned
  it should "Unit Test to determine if the numOfMappers is returned" in {
    val numOfMappersReturned = config.getString("HW1_Mapred.numOfMappers")
    assert(numOfMappersReturned.toInt match {
      case _:Int => true
      case _ => false
    })
  }

  // Unit Test to determine if the numOfReducers is returned
  it should "Unit Test to determine if the numOfReducers is returned" in {
    val numOfReducersReturned = config.getString("HW1_Mapred.numOfReducers")
    assert(numOfReducersReturned.toInt match {
      case _: Int => true
      case _ => false
    })
  }

  // Unit Test to determine if the intervalLength is returned
  it should "Unit Test to determine if the intervalLength is returned" in {
    val timeInterval = config.getString("HW1_Mapred.timeInterval")
    assert(timeInterval.toInt match {
      case _: Int => true
      case _ => false
    })
  }

}
