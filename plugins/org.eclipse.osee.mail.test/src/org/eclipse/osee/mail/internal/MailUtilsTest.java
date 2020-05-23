/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.mail.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataSource;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.mail.api.MailUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link MailUtils}
 * 
 * @author Roberto E. Escobar
 */
public class MailUtilsTest {

   private static final String EXPECTED_NAME = "$p3c!@|_(#@0\\/@(+3&$";
   private static final String EXPECTED_MESSAGE = "aldsjfaljfajf;afja;alija;ewta efad4`93240741-07";
   private static final String EXTRA_MESSAGE = "---->>>>APPEND";
   private static final String EXTENDED_MESSAGE = EXPECTED_MESSAGE + EXTRA_MESSAGE;

   private static final String HTML_DATA = "<html><body><h1>Page</h1> <b>body</b> is here</body></html>";
   private static final String HTML_PARSED_DATA = "Page body is here";

   private static final Pattern MULTI_PART_PATTERN =
      Pattern.compile("_Part_\\d+_\\d+\\.\\d+\\s+(.*?)------=", Pattern.DOTALL);

   @Test
   public void testCreateFromString() throws IOException {
      DataSource source = MailUtils.createFromString(EXPECTED_NAME, EXPECTED_MESSAGE);
      Assert.assertEquals(EXPECTED_NAME, source.getName());
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());

      String actualMessage = Lib.inputStreamToString(source.getInputStream());
      Assert.assertEquals(EXPECTED_MESSAGE, actualMessage);

      OutputStream os = source.getOutputStream();
      Assert.assertNotNull(os);

      os.write(EXTRA_MESSAGE.getBytes("UTF-8"));

      actualMessage = Lib.inputStreamToString(source.getInputStream());
      Assert.assertEquals(EXTENDED_MESSAGE, actualMessage);
   }

   @Test
   public void testCreateFromStringWithFormat() throws IOException {
      DataSource source = MailUtils.createFromString(EXPECTED_NAME, "hello [%s]", 1);
      Assert.assertEquals(EXPECTED_NAME, source.getName());
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());

      String actualMessage = Lib.inputStreamToString(source.getInputStream());
      Assert.assertEquals("hello [1]", actualMessage);
   }

   @Test
   public void testCreateFromHtml() throws Exception {
      DataSource source = MailUtils.createFromHtml(EXPECTED_NAME, HTML_DATA);
      Assert.assertEquals(EXPECTED_NAME, source.getName());
      Assert.assertTrue(source.getContentType().contains("multipart/alternative;"));

      String actualMessage = Lib.inputStreamToString(source.getInputStream());
      List<String> parts = parseMultiPart(actualMessage);
      Assert.assertEquals(2, parts.size());

      Assert.assertEquals(HTML_DATA, parts.get(0));
      Assert.assertEquals(HTML_PARSED_DATA, parts.get(1));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testCreateFromHtmlGetOutputStream() throws Exception {
      DataSource source = MailUtils.createFromHtml(EXPECTED_NAME, HTML_DATA);
      Assert.assertEquals(EXPECTED_NAME, source.getName());
      Assert.assertTrue(source.getContentType().contains("multipart/alternative;"));

      // Throws Exception
      source.getOutputStream();
   }

   @Test
   public void testCreateAlternative() throws Exception {
      DataSource source = MailUtils.createAlternativeDataSource(EXPECTED_NAME, HTML_DATA, EXTENDED_MESSAGE);
      Assert.assertEquals(EXPECTED_NAME, source.getName());
      Assert.assertTrue(source.getContentType().contains("multipart/alternative;"));

      String actualMessage = Lib.inputStreamToString(source.getInputStream());
      List<String> parts = parseMultiPart(actualMessage);
      Assert.assertEquals(2, parts.size());

      Assert.assertEquals(HTML_DATA, parts.get(0));
      Assert.assertEquals(EXTENDED_MESSAGE, parts.get(1));
   }

   @org.junit.Test
   public void testCreateOutlookEvent() throws Exception {
      checkOutlookEvent("Daily+SCRUM.vcs", "Daily SCRUM", "Meeting room 220.", "01/31/2011 09:45 AM, PDT",
         "01/31/2011 10:00 AM, PDT");

      String fileName1 = "Discuss+what+to+do+about+the+ant+problem+in+the+break+room..vcs";

      checkOutlookEvent(fileName1, "Discuss what to do about the ant problem in the break room.", "Joe Schmoe's Desk",
         "12/31/2300 00:00 AM, PDT", "12/31/2300 11:59 PM, PDT");

      checkOutlookEvent(fileName1, "Discuss what to do about the ant problem in the break room.", "Joe Schmoe's Desk",
         "12/31/2300 11:58 PM, PDT", "12/31/2300 11:59 PM, PDT");
   }

   @org.junit.Test
   public void testCreateOutlookEventSpecialChars() throws Exception {
      String fileName1 = "Testing%21%40%23%24%25%5E+%26+%28+%29+_+%2B+1234567890-%3D%60%7E%7D%7B%5B%5D%2C.%3B%27.vcs";

      checkOutlookEvent(fileName1, "Testing!@#$%^ & ( ) _ + 1234567890-=`~}{[],.;'",
         "!@#$%^ & ( ) _ + 1234567890-=`~}{[],.;'", "01/31/2011 09:45 AM, PDT", "01/31/2011 10:00 AM, PDT");
   }

   @org.junit.Test(expected = UnsupportedOperationException.class)
   public void testCreateOutlookEventGetOutputStream() throws Exception {
      Date start = new Date();
      Date end = new Date(start.getTime() + 4 * 60 * 60 * 1000);
      DataSource source = MailUtils.createOutlookEvent("Test 1", "Here", start, end);
      Assert.assertEquals("Test+1.vcs", source.getName());
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());

      // Throws Exception
      source.getOutputStream();
   }

   private static void checkOutlookEvent(String filename, String eventName, String location, String startDateStr, String endDateStr) throws Exception {
      Date startDate = DateFormat.getInstance().parse(startDateStr);
      Date endDate = DateFormat.getInstance().parse(endDateStr);

      DataSource source = MailUtils.createOutlookEvent(eventName, location, startDate, endDate);

      Assert.assertEquals(filename, source.getName());
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());

      String actualMessage = Lib.inputStreamToString(source.getInputStream());
      String expectedMessage = toOutlookExpected(eventName, location, startDate, endDate);
      Assert.assertEquals(expectedMessage, actualMessage);

   }

   //@formatter:off
   private static String toOutlookExpected(String event, String location, Date startDate, Date endDate) {
     DateFormat myDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
     StringBuilder builder = new StringBuilder();
     builder.append("\nBEGIN:VCALENDAR\nPRODID:-//Microsoft Corporation//Outlook 10.0 MIMEDIR//EN\nVERSION:1.0\nBEGIN:VEVENT\nDTSTART:");
     builder.append(myDateFormat.format(startDate));
     builder.append("\nDTEND:");
     builder.append(myDateFormat.format(endDate));
     builder.append("\nLOCATION;ENCODING=QUOTED-PRINTABLE:");
     builder.append(location);
     builder.append("\nTRANSP:1\nDESCRIPTION;ENCODING=QUOTED-PRINTABLE:=0D=0A\nSUMMARY;ENCODING=QUOTED-PRINTABLE:Event:");
     builder.append(event);
     builder.append("\nPRIORITY:3\nEND:VEVENT\nEND:VCALENDAR\n");
     return builder.toString();
   }
   //@formatter:on

   private List<String> parseMultiPart(String actualMessage) {
      List<String> toReturn = new ArrayList<>();
      Matcher matcher = MULTI_PART_PATTERN.matcher(actualMessage);
      while (matcher.find()) {
         toReturn.add(matcher.group(1).trim());
      }
      return toReturn;
   }

}
