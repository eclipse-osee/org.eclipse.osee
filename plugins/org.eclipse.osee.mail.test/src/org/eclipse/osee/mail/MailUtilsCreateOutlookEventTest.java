/*
 * Created on Jun 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.mail;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.activation.DataSource;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailMessageFactory}.
 * 
 * @author Shawn F. cook
 */
@RunWith(Parameterized.class)
public class MailUtilsCreateOutlookEventTest {

   private final String location;
   private final String event;
   private final Date startDate;
   private final Date endDate;

   //NOTE: The current implementation of the MailUtils::createOutlookEvent() method requires that 
   // startDate and endDate to be on the same day.  Testing dates on different calendar days will
   // falsely fail.
   public MailUtilsCreateOutlookEventTest(String location, String event, Date startDate, Date endDate) {
      this.location = location;
      this.event = event;
      this.startDate = startDate;
      this.endDate = endDate;
   }

   @org.junit.Test
   public void testMailUtilsCreateOutlookEvent() {
      //MailUtils::createOutlookEvent returns an instance of StringDataSource which inherits from DataSource but is
      // private to the MailUtils class.  This is the only effective test I can think of right now.
      Calendar startCal = Calendar.getInstance();
      Calendar endCal = Calendar.getInstance();

      startCal.setTime(startDate);
      String startTime = startCal.get(Calendar.HOUR_OF_DAY) + "" + startCal.get(Calendar.MINUTE);

      endCal.setTime(endDate);
      String endTime = endCal.get(Calendar.HOUR_OF_DAY) + "" + endCal.get(Calendar.MINUTE);

      DataSource source = MailUtils.createOutlookEvent(location, event, startDate, startTime, endTime);
      //      String expectedResult =
      //         "\nBEGIN:VCALENDAR\nPRODID:-//Microsoft Corporation//Outlook 10.0 MIMEDIR//EN\nVERSION:1.0\nBEGIN:VEVENT\nDTSTART:" + startCal.get(Calendar.YEAR) + "" + startCal.get(Calendar.MONTH) + "" + startCal.get(Calendar.DAY_OF_MONTH) + "T" + startTime + "0Z\nDTEND:" + endCal.get(Calendar.YEAR) + "" + endCal.get(Calendar.MONTH) + "" + endCal.get(Calendar.DAY_OF_MONTH) + "T" + endTime + "0Z\nLOCATION;ENCODING=QUOTED-PRINTABLE:" + location + "\nTRANSP:1\nDESCRIPTION;ENCODING=QUOTED-PRINTABLE:=0D=0A\nSUMMARY;ENCODING=QUOTED-PRINTABLE:Event:" + event + "\nPRIORITY:3\nEND:VEVENT\nEND:VCALENDAR\n";

      Assert.assertTrue(source.getName().startsWith(event));
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());
   }//test_MailUtils_createOutlookEvent

   @Parameters
   public static Collection<Object[]> getData() throws ParseException {
      //PLEASE SEE NOTE above the constructor.
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"Meeting room 220.",//location
         "Daily SCRUM",//event
         DateFormat.getInstance().parse("01/31/2011 09:45 AM, PDT"),//startDate
         DateFormat.getInstance().parse("01/31/2011 10:00 AM, PDT"),//endDate
      });

      data.add(new Object[] {"!@#$%^ & ( ) _ + 1234567890-=`~}{[],.;'",//location
         "Testing!@#$%^ & ( ) _ + 1234567890-=`~}{[],.;'",//event
         DateFormat.getInstance().parse("01/31/2011 09:45 AM, PDT"),//startDate
         DateFormat.getInstance().parse("01/31/2011 10:00 AM, PDT"),//endDate
      });

      data.add(new Object[] {"Joe Schmoe's Desk",//location
         "Discuss what to do about the ant problem in the break room.",//event
         DateFormat.getInstance().parse("12/31/2300 00:00 AM, PDT"),//startDate
         DateFormat.getInstance().parse("12/31/2300 11:59 PM, PDT"),//endDate
      });

      data.add(new Object[] {"Joe Schmoe's Desk",//location
         "Discuss what to do about the ant problem in the break room.",//event
         DateFormat.getInstance().parse("12/31/2300 11:58 PM, PDT"),//startDate
         DateFormat.getInstance().parse("12/31/2300 11:59 PM, PDT"),//endDate
      });

      return data;
   }//getData
}
