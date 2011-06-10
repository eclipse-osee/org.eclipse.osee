/*
 * Created on Jun 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.activation.DataSource;
import javax.mail.MessagingException;
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
public class MailUtilsCreateFromStringTest {

   private final String str_value;

   public MailUtilsCreateFromStringTest(String str_value) {
      this.str_value = str_value;
   }

   @org.junit.Test
   public void testMailUtilsCreateFromString() throws IOException {
      DataSource source = MailUtils.createFromString(str_value, "message_value");
      Assert.assertEquals("text/plain; charset=UTF-8", source.getContentType());
      Assert.assertEquals(str_value, source.getName());
      Assert.assertNotNull(source.getOutputStream());
   }

   @org.junit.Test
   public void testMailUtilsCreateFromHtml() throws MessagingException {
      DataSource source = MailUtils.createFromHtml(str_value, "html_value");
      //MailUtils::createFromHtml returns an instance of MultiPartDataSource which inherits from DataSource but is
      // private to the MailUtils class.  This is the only effective test I can think of right now.
      Assert.assertEquals(str_value, source.getName());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"name_value"});
      data.add(new Object[] {"$p3c!@|_(#@0\\/@(+3&$"});

      String a = "";
      //make a 1000 char string.
      for (int i = 0; i < 1000; i++) {
         a += "x";
      }
      data.add(new Object[] {a});

      return data;
   }//getData
}
