package org.CS441HW1
package CommonUtils
import java.text.SimpleDateFormat
import java.util.Date

class CommonFunctions{
}

object CommonFunctions {

  // Function to get the batch number / group number for given Timestamp and interval length
  // Timestamp is represented as 'HH:MM:SS:sss'
  // Interval length is an Int which represents time interval in seconds
  // Return the batch number
  def getTimeSlot(tokens: Array[String], intervalLength: Int): Int =
    val dateFormat = new SimpleDateFormat("HH:mm:ss.SSS")
    // Convert milliseconds to seconds
    val dateTime = dateFormat.parse(tokens(0)).getTime.toInt / 1000
    // Return the grp number
    (dateTime / intervalLength)

  // Function to calculate Timestamo in HH:mm:ss.SSS format from the epoch
  // Epoch is the the grp number returned from the above function
  // Interval length is an Int which represents time interval in seconds
  // Returns date with the specific format
  def convertToTimeStamp(epoch: Int, intervalLength: Int): String =
    val timeStamp = new Date(epoch * 1000 * intervalLength)
    val dateFormat = new SimpleDateFormat("HH:mm:ss.SSS")
    dateFormat.format(timeStamp)
}
