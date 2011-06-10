/*
 * Created on Jun 6, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import javax.activation.DataSource;
import junit.framework.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailMessage} using a multi-parameter list.
 * 
 * @author Shawn F. cook
 */
@RunWith(Parameterized.class)
public class MailMessageMultiParamTest {

   private final Collection<String> llist;

   public MailMessageMultiParamTest(Collection<String> llist) {
      this.llist = llist;
   }

   @org.junit.Test
   public void testMailMsgSetRecipients() {
      MailMessage msg = new MailMessage();

      msg.setRecipients(llist);
      Assert.assertEquals(llist.size(), msg.getRecipients().size());
      Assert.assertTrue(msg.getRecipients().containsAll(llist));
   }

   @org.junit.Test
   public void testMailMsgSetReplyTo() {
      MailMessage msg = new MailMessage();

      msg.setReplyTo(llist);
      Assert.assertEquals(llist.size(), msg.getReplyTo().size());
      Assert.assertTrue(msg.getReplyTo().containsAll(llist));
   }

   @org.junit.Test
   public void testMailMsgAddAttachments() {
      MailMessage msg = new MailMessage();
      LinkedList<DataSource> dslist = new LinkedList<DataSource>();

      for (String item : llist) {
         DataSource ds = MailUtils.createFromString(item, item);
         msg.addAttachment(ds);
         dslist.add(ds);
      }

      Assert.assertTrue("Item not retained by MailMessage.attachments.", msg.getAttachments().containsAll(dslist));
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {Arrays.asList("One new string value.", "Another new string value",
         "Yet another string value.", "And the final string value.")});

      data.add(new Object[] {Arrays.asList("joe.schmoe@somedomain.com", "so.and.so@thisandthat.com",
         "this_is-an.email@some-where.org", "just-one-more-email.something.com")});

      data.add(new Object[] {Arrays.asList("1.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
         "2.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
         "3.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
         "4.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./")});

      //generate a very long string
      String aLongString1 = "1.";
      String aLongString2 = "2.";
      String aLongString3 = "3.";
      String aLongString4 = "4.";
      //make a 1000 char string.
      for (int i = 0; i < 1000; i++) {
         aLongString1 += "x";
      }
      data.add(new Object[] {Arrays.asList(aLongString1, aLongString2, aLongString3, aLongString4)});

      return data;
   }//getData
}
