/*
 * Created on Jun 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.mail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.activation.DataSource;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
public class MailUtilsCreateFromHtmlTest {
   private final String name_value;
   private final String html_value;

   public MailUtilsCreateFromHtmlTest(String name_value, String html_value) {
      this.name_value = name_value;
      this.html_value = html_value;
   }

   @org.junit.Test
   public void testCreateFromHtml() throws Exception {
      DataSource source = MailUtils.createFromHtml(name_value, html_value);
      Assert.assertEquals(name_value, source.getName());

      String actual = null;
      InputStream is = null;
      try {
         is = source.getInputStream();
         actual = Lib.inputStreamToString(is);
      } finally {
         Lib.close(is);
      }

      /*
       * DataHandler handler = new DataHandler(source); String contentType = handler.getContentType();
       * handler.getContent(); handler.getPreferredCommands(); System.out.println(contentType);
       * Assert.assertEquals("text/html", source.getContentType()); String expected = ""; Assert.assertEquals(expected,
       * actual);
       */

   }

   @org.junit.Test(expected = UnsupportedOperationException.class)
   public void testCreateFromHtmlException() throws Exception {
      DataSource source = MailUtils.createFromHtml(name_value, html_value);
      source.getOutputStream();
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"name_value",//name_value
         "html_value",//html_value
      });

      data.add(new Object[] {"$p3c!@|_(#@0\\/@(+3&$",//name_value
         "$p3c!@|_(#@0\\/@(+3&$",//html_value
      });

      return data;
   }//getData
}
