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

package org.eclipse.osee.ats.ide.notify;

import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.TestEmail;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmailIde;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Tests emails are working from both client and server. Client emails test email then calls server to send similar
 * email. Also tests the "abridged" capability where a simple/non-classified subject/body can be provided and will be
 * emailed to users with Abridged Email attribute set.
 *
 * @author Donald G. Dunne
 */
public class TestOseeEmailSend extends XNavigateItemAction {

   private static final String TITLE = "Test OseeEmail Send";
   AtsApi atsApi;
   private XResultData rd;

   public TestOseeEmailSend() {
      super(TITLE, FrameworkImage.EMAIL, XNavigateItem.EMAIL_NOTIFICATIONS);
      atsApi = AtsApiService.get();
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         rd = new XResultData();
         rd.logf("%s\n\n", getName());
         User user = UserManager.getUser();
         if (user.isInvalid()) {
            rd.errorf(TITLE, "User [%s] is invalid", user);
         }

         testBasicEmail(user);

         XResultDataUI.report(rd, getName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void testBasicEmail(User user) {
      String email = user.getEmail();
      if (!EmailUtil.isEmailValid(email)) {
         XResultDataUI.errorf(TITLE, "User email [%s] is invalid", user);
         return;
      }
      rd.log("1. Two basic \"Hello World\" emails should be sent; one from client and another from server.\n\n");

      // Test Email Client
      String title = "Send Test Email - Client";
      rd.log(title);
      try {
         OseeEmail emailMessage = OseeEmailIde.create(Arrays.asList(email), email, email, title,
            AHTML.simplePage(AHTML.bold("Hello World - this should be bold")), BodyType.Html, Arrays.asList(email),
            "Abridged - " + title, "This is the abridged message body");
         emailMessage.send();
         rd.log("Completed");
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }

      rd.log("\n\n2. If Abridged Email attribute is set on your user artifact, another two " //
         + "emails should be sent from client and server with only basic information " //
         + "about the change with Abridged in title and abridged message body, etc).\n\n");

      // Test Email Server
      String title2 = "Send Test Email - Server";
      rd.log(title2);
      try {
         XResultData remoteRd =
            atsApi.getServerEndpoints().getNotifyEndpoint().sendTestEmail(TestEmail.create(email, title2));
         rd.merge(remoteRd);
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }
      rd.log("Completed");
   }

}