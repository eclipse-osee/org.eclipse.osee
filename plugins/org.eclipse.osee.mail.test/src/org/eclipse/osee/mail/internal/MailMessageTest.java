/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import javax.activation.DataSource;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailMessage} using a multi-parameter list.
 * 
 * @author Shawn F. Cook
 */
@RunWith(Parameterized.class)
public class MailMessageTest {

   private final String value;
   private final Collection<String> llist;

   public MailMessageTest(String value, Collection<String> llist) {
      this.value = value;
      this.llist = llist;
   }

   @org.junit.Test
   public void testMailMsgSetFrom() {
      MailMessage msg = new MailMessage();
      msg.setFrom(value);
      Assert.assertEquals(value, msg.getFrom());
   }

   @org.junit.Test
   public void testMailMsgSetId() {
      MailMessage msg = new MailMessage();
      msg.setId(value);
      Assert.assertEquals(value, msg.getId());
   }

   @org.junit.Test
   public void testMailMsgSetSubject() {
      MailMessage msg = new MailMessage();
      msg.setSubject(value);
      Assert.assertEquals(value, msg.getSubject());
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
      LinkedList<DataSource> dslist = new LinkedList<>();

      for (String item : llist) {
         DataSource ds = MailUtils.createFromString(item, item);
         msg.addAttachment(ds);
         dslist.add(ds);
      }

      Assert.assertEquals(dslist.size(), msg.getAttachments().size());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();

      data.add(new Object[] {
         "One new string value.",
         Arrays.asList("One new string value.", "Another new string value", "Yet another string value.",
            "And the final string value.")});

      data.add(new Object[] {
         "joe.schmoe@somedomain.com",
         Arrays.asList("joe.schmoe@somedomain.com", "so.and.so@thisandthat.com", "this_is-an.email@some-where.org",
            "just-one-more-email.something.com")});

      data.add(new Object[] {
         "Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
         Arrays.asList("1.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
            "2.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
            "3.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./",
            "4.Testing special characters - ~`!@#$%^&*()_-+={}|[]:\";'<>?,./")});

      //generate a very long string
      StringBuilder buffer = new StringBuilder("1.");
      String aLongString2 = "2.";
      String aLongString3 = "3.";
      String aLongString4 = "4.";
      //make a 1000 char string.
      for (int i = 0; i < 1000; i++) {
         buffer.append("x");
      }
      String aLongString1 = buffer.toString();
      data.add(new Object[] {aLongString1, Arrays.asList(aLongString1, aLongString2, aLongString3, aLongString4)});

      return data;
   }//getData
}
