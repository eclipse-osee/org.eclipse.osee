/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class TestWorkItemEmailSend extends XNavigateItemAction {

   private static final String TITLE = "Test WorkItem Email Send";
   AtsApi atsApi;
   private XResultData rd;
   private User user;
   private Long workItemTestId;

   public TestWorkItemEmailSend() {
      super(TITLE, FrameworkImage.EMAIL, XNavigateItem.EMAIL_NOTIFICATIONS);
      atsApi = AtsApiService.get();
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         if (!MessageDialog.openConfirm(Displays.getActiveShell(), TITLE,
            "Example ATS Workflow emails will be sent to current user.\n\n" //
               + "Emails will also be sent to AbridgedEmail if set on User artifact.\n\n" //
               + "Existing Team Workflow artifact id must be set in AtsConfig artifact with keys\n" //
               + "WorkItemTestInWorkArtId, WorkItemTestPeerReviewArtId, WorkItemTestCompletedArtId and WorkItemTestCancelledArtId.\n\n" //
               + "Continue?")) {
            return;
         }
         rd = new XResultData();
         rd.logf("%s\n\n", getName());

         user = UserManager.getUser();
         if (user.isInvalid()) {
            rd.errorf(TITLE, "User [%s] is invalid\n", user);

         } else {
            rd.logf("User %s\n", user.toStringWithId());
            rd.logf("Email %s\n", user.getEmail());
            rd.logf("Abridged Email %s\n\n", user.getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, ""));

            testEmail();
            testWorkItemOriginatorEmail();
            testWorkItemCompletedEmail();
            testWorkItemCancelledEmail();
            testWorkItemSubscribedEmail();
            testWorkItemSubscribedAiEmail();
            testWorkItemSubscribedTeamEmail();
            testWorkItemPeerReviewCompletedEmail();
         }

         rd.log("\nCompleted");
         XResultDataUI.report(rd, getName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void testWorkItemOriginatorEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.Originator);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemCompletedEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.Completed);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemCancelledEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.Cancelled);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemSubscribedEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.Subscribed);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemSubscribedAiEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.SubscribedAi);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemSubscribedTeamEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.SubscribedTeam);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private void testWorkItemPeerReviewCompletedEmail() {
      AtsNotificationCollector notifications = setupNotificationCollector(AtsNotifyType.Peer_Reviewers_Completed);
      if (notifications != null) {
         rd = atsApi.getNotificationService().sendNotifications(notifications, rd);
      }
   }

   private AtsNotificationCollector setupNotificationCollector(AtsNotifyType notifyType) {
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent workItemEvent = new AtsWorkItemNotificationEvent();
      notifications.addWorkItemNotificationEvent(workItemEvent);
      workItemEvent.setFromUserId(user.getUserId());
      workItemEvent.setNotifyType(notifyType);
      workItemEvent.setInTest(true);
      if (!getTestWorkItemId(workItemEvent, notifyType)) {
         return null;
      }
      return notifications;
   }

   /*
    * @return false if not configured
    */
   private boolean getTestWorkItemId(AtsWorkItemNotificationEvent workItemEvent, AtsNotifyType notifyType) {

      String workItemTestIdStr = null;
      if (notifyType == AtsNotifyType.Completed) {
         workItemTestIdStr = atsApi.getConfigValue("WorkItemTestCompletedArtId");
         if (!Strings.isNumeric(workItemTestIdStr)) {
            rd.error("WorkItemTestCompletedArtId not set in AtsConfig; Skipping test...");
            return false;
         }
      } else if (notifyType == AtsNotifyType.Cancelled) {
         workItemTestIdStr = atsApi.getConfigValue("WorkItemTestCancelledArtId");
         if (!Strings.isNumeric(workItemTestIdStr)) {
            rd.error("WorkItemTestCancelledArtId not set in AtsConfig; Skipping test...");
            return false;
         }
      } else if (notifyType == AtsNotifyType.Peer_Reviewers_Completed) {
         workItemTestIdStr = atsApi.getConfigValue("WorkItemTestPeerReviewArtId");
         if (!Strings.isNumeric(workItemTestIdStr)) {
            rd.error("WorkItemTestPeerReviewArtId not set in AtsConfig; Skipping test...");
            return false;
         }
      } else {
         workItemTestIdStr = atsApi.getConfigValue("WorkItemTestInWorkArtId");
         if (!Strings.isNumeric(workItemTestIdStr)) {
            rd.error("WorkItemTestInWorkArtId not set in AtsConfig; Skipping test...");
            return false;
         }
      }
      if (Strings.isNumeric(workItemTestIdStr)) {
         workItemTestId = Long.valueOf(workItemTestIdStr);
         workItemEvent.getWorkItemIds().add(workItemTestId);
      }
      return true;
   }

   private void testEmail() {

      AtsNotificationCollector notifications = new AtsNotificationCollector();
      notifications.setSubject(getName());

      AtsNotificationEvent notify = new AtsNotificationEvent();
      notifications.addNotificationEvent(notify);
      notify.setSubjectType("Generic Notification");
      notify.setSubjectDescription("This is normal description.");
      notify.setSubjectDescriptionAbridged("This is abridged description.");
      notify.setId("TW2000");
      notify.setUrl("http://www.google.com");
      notify.setFromEmailAddress(AtsCoreUsers.SYSTEM_USER.getEmail());

      rd.logf("Using email [%s]\n", user.getEmail());
      notify.setEmailAddresses(Arrays.asList(user.getEmail()));

      String abridgedEmail =
         atsApi.getAttributeResolver().getSoleAttributeValue(user, CoreAttributeTypes.AbridgedEmail, null);
      if (Strings.isValid(abridgedEmail)) {
         rd.logf("Using abridged email [%s]\n", abridgedEmail);
         notify.setEmailAddressesAbridged(Arrays.asList(abridgedEmail));
      } else {
         rd.warningf("Abridged email not set, skipping abridged test for user %s", user.toStringWithId());
      }

      rd = atsApi.getNotificationService().sendNotifications(notifications, rd);

   }

}