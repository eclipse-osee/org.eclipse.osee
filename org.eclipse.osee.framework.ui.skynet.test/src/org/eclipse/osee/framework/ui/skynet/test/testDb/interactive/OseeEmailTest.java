/*
 * Created on May 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.testDb.interactive;

import java.util.Date;
import junit.framework.TestCase;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.windows.OutlookCalendarEvent;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class OseeEmailTest extends TestCase {

   public static String emailAddress = null;
   public static final StringBuffer results = new StringBuffer();
   public static boolean cancelled = false;

   /**
    * @throws java.lang.Exception
    */
   @Override
   protected void setUp() throws Exception {
      if (cancelled) return;
      if (emailAddress == null) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               // Classloading of OSEE framework may cause emailing to fail.  Load Artifact Editor first.
               ArtifactEditor.editArtifact(SkynetAuthentication.getUser());

               EntryDialog entryDialog = new EntryDialog("Osee Email Test", "Enter email address to use.");
               if (entryDialog.open() == 0) {
                  emailAddress = entryDialog.getEntry();
                  if (emailAddress == null || emailAddress.equals("")) {
                     throw new IllegalArgumentException("Email address is invalid.");
                  }
               } else {
                  cancelled = true;
                  throw new IllegalStateException("User cancelled email entry.");
               }
            }
         }, true);
      }
   }

   public void testTextEmail() throws Exception {
      final String TEST_NAME = "Email Test 1/3 - Text Body";
      OseeEmail emailMessage = new OseeEmail(emailAddress, TEST_NAME, "Hello World - this is text only", BodyType.Text);
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
      validateEmailReceived(TEST_NAME);
   }

   public void testHtmlEmail() throws Exception {
      final String TEST_NAME = "Email Test 2/3 - Html Body";
      OseeEmail emailMessage =
            new OseeEmail(emailAddress, TEST_NAME, AHTML.simplePage(AHTML.bold("Hello World - this should be bold")),
                  BodyType.Html);
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
      validateEmailReceived(TEST_NAME);
   }

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
      "5) Verify subject is name of this test", BodyType.Text);
      String context = new OutlookCalendarEvent("Conference Room", TEST_NAME, new Date(), "0800", "1300").getEvent();
      emailMessage.addAttachment(context, "schedule class.vcs");
      emailMessage.send();
      System.out.println(TEST_NAME + " sent to \"" + emailAddress + "\"");
      validateEmailReceived(TEST_NAME);
   }

   private void validateEmailReceived(final String TEST_NAME) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               Thread.sleep(5000);
               if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), TEST_NAME,
                     TEST_NAME + " was sent to \"" + emailAddress + "\".\n\nWait 5 minutes.\n\nDid you receive it?")) {
                  assertTrue(true);
               } else {
                  results.append(TEST_NAME + " FAILED\n");
               }
            } catch (Exception ex) {
               results.append(TEST_NAME + " with Exception - \n" + ex.getLocalizedMessage());
            }
         }
      }, true);

   }

   /**
    * This test only exists to report the results of the email tests above
    * 
    * @throws Exception
    */
   public void testReportResults() throws Exception {
      if (!results.toString().equals("")) {
         System.err.println(results.toString());
         assertTrue(false);
      } else
         assertTrue(true);
   }
}
