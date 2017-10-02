/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertFalse;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.windows.OutlookCalendarEvent;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.junit.Before;
import org.junit.Ignore;

/**
 * Test ignored cause it sends email to current user. Un-ignore to debug email issues.
 * 
 * @author Donald G. Dunne
 */
@Ignore
public class OseeEmailTest {

   public static String emailAddress = null;
   private static String infoStr =
      "\n\nOseeEmailTest: This test will send 3 emails. If you do not receive 3, the test failed.";

   @Before
   public void setUp() throws Exception {
      if (emailAddress == null) {
         RendererManager.open(UserManager.getUser(), PresentationType.DEFAULT_OPEN);
         emailAddress = UserManager.getUser().getEmail();
         assertFalse("Invalid email address " + emailAddress + " for user " + UserManager.getUser(),
            emailAddress.contains("\\@"));
      }
   }

   @org.junit.Test
   public void testTextEmail() throws Exception {
      final String TEST_NAME = "Email Test 1/3 - Text Body";
      OseeEmail emailMessage =
         new OseeEmail(emailAddress, TEST_NAME, "Hello World - this is text only" + infoStr, BodyType.Text);
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
   }

   @org.junit.Test
   public void testHtmlEmail() throws Exception {
      final String TEST_NAME = "Email Test 2/3 - Html Body";
      OseeEmail emailMessage = new OseeEmail(emailAddress, TEST_NAME,
         AHTML.simplePage(AHTML.bold("Hello World - this should be bold" + infoStr)), BodyType.Html);
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
   }

   @org.junit.Test
   public void testAttachementEmail() throws Exception {
      final String TEST_NAME = "Email Test 3/3 - with Outlook Attachment";
      OseeEmail emailMessage = new OseeEmail(emailAddress, TEST_NAME, TEST_NAME + "\n\nTesting the attachment\n" +
      //
         "1) Double-click open attachment opens calendar event dialog\n" +
         //
         "2) Verify Time 8am - 1pm\n" +
         //
         "3) Verify date (today)\n" +
         //
         "4) Verify location: Conference Room\n" +
         //
         "5) Verify subject is name of this test" + infoStr, BodyType.Text);
      Date start = new Date();
      Date end = new Date(start.getTime() + 4 * 60 * 60 * 1000);
      String context = new OutlookCalendarEvent("Conference Room", TEST_NAME, start, end).getEvent();
      emailMessage.addAttachment(context, "schedule class.vcs");
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
   }

}
