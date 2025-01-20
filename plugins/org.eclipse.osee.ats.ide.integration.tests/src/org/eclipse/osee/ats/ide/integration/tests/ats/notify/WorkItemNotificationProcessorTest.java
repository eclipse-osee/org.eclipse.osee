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

package org.eclipse.osee.ats.ide.integration.tests.ats.notify;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Before;

/**
 * Test unit for {@link WorkItemNotificationProcessor}
 *
 * @author Donald G. Dunne
 */
public class WorkItemNotificationProcessorTest {

   AtsUser joeSmith_CurrentUser, kay_ValidEmail, jason_ValidEmail, alex_NoValidEmail, inactiveSteve;
   AtsApi atsApi;

   private AtsUser setupUser(UserToken userToken) {
      return atsApi.getUserService().getUserByUserId(userToken.getUserId());
   }

   @Before
   public void setup() {
      atsApi = AtsApiService.get();

      joeSmith_CurrentUser = setupUser(DemoUsers.Joe_Smith);
      kay_ValidEmail = setupUser(DemoUsers.Kay_Jones);
      jason_ValidEmail = setupUser(DemoUsers.Jason_Michael);
      alex_NoValidEmail = setupUser(DemoUsers.Alex_Kay);
      inactiveSteve = setupUser(DemoUsers.Inactive_Steve);
   }

   @org.junit.Test
   public void testNotifyOriginator() {

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeUnCommittedWf();
      XResultData rd = new XResultData();
      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Originator);

      // Fail if wrong id sent in
      event.getWorkItemIds().add(9238423948L);
      processor.run(notifications, event);
      Assert.assertTrue(rd.isFailed());
      Assert.assertTrue(rd.toString().contains("WorkItem id [9238423948] invalid"));

      // Set valid id and re-run
      event.getWorkItemIds().clear();
      event.getWorkItemIds().add(teamWf.getId());
      rd.clear();
      processor.run(notifications, event);
      // No notifications cause originator and current user are same
      Assert.assertTrue(rd.isSuccess());
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      // Send one event to Originator
      event.setFromUserId(kay_ValidEmail.getUserId());
      rd.clear();
      processor.run(notifications, event);
      // One notification
      Assert.assertTrue(rd.isSuccess());
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Originator.name(), notifyEvent.getType());
      Assert.assertEquals(joeSmith_CurrentUser.getEmail(), notifyEvent.getEmailAddresses().iterator().next());
      Assert.assertEquals("You have been set as the originator of [Demo Code Team Workflow] state [Implement] " //
         + "titled [SAW (uncommitted) More Reqt Changes for Diagram View]", notifyEvent.getDescription());

   }

   @org.junit.Test
   public void testNotifyAssignee() {

      XResultData rd = new XResultData();

      /////////////////////////////////////////////////////////////////
      // No email cause Joe assigned himself (from = Joe)
      IAtsTeamWorkflow codeTeamWf = DemoUtil.getSawCodeUnCommittedWf();
      Assert.assertNotNull(codeTeamWf);

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Assigned);
      event.getWorkItemIds().add(codeTeamWf.getId());

      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      /////////////////////////////////////////////////////////////////
      // One notification to assignee (Kay)
      IAtsTeamWorkflow designTeamWf = DemoUtil.getSawSWDesignUnCommittedWf();
      Assert.assertNotNull(designTeamWf);

      processor = new WorkItemNotificationProcessor(rd);
      notifications = new AtsNotificationCollector();
      event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Assigned);
      event.getWorkItemIds().add(designTeamWf.getId());
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), notifyEvent.getType());
      List<String> expectedUserEmails = new ArrayList<>();
      expectedUserEmails.add(kay_ValidEmail.getEmail());
      Assert.assertTrue(org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(expectedUserEmails,
         notifyEvent.getEmailAddresses()));
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Implement] titled [SAW (uncommitted) " //
            + "More Reqt Changes for Diagram View]",
         notifyEvent.getDescription());

      /////////////////////////////////////////////////////////////////
      // Two notifications should be created, one for each assignee
      IAtsPeerToPeerReview rev = (IAtsPeerToPeerReview) atsApi.getQueryService().getWorkItemByAtsId("RVW15");
      Assert.assertNotNull(rev);

      processor = new WorkItemNotificationProcessor(rd);
      notifications = new AtsNotificationCollector();
      event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Assigned);
      event.getWorkItemIds().add(rev.getId());
      event.setFromUserId(jason_ValidEmail.getUserId());
      processor.run(notifications, event);
      // Single event with 2 email addresses
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), notifyEvent.getType());
      expectedUserEmails.clear();
      expectedUserEmails.add(joeSmith_CurrentUser.getEmail());
      expectedUserEmails.add(kay_ValidEmail.getEmail());
      Assert.assertTrue(org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(expectedUserEmails,
         notifyEvent.getEmailAddresses()));
      Assert.assertEquals("You have been set as the assignee of [Peer-To-Peer Review] in state [Review] titled " //
         + "[2 - Peer Review algorithm used in code]", notifyEvent.getDescription());
   }

   @org.junit.Test
   public void testNotifySubscribe() {

      XResultData rd = new XResultData();

      IAtsTeamWorkflow codeTeamWf = DemoUtil.getSawCodeUnCommittedWf();
      Assert.assertNotNull(codeTeamWf);

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Subscribed);
      event.getWorkItemIds().add(codeTeamWf.getId());

      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      IAtsChangeSet changes = atsApi.createChangeSet("Add Subscribed");
      atsApi.getWorkItemService().getSubscribeService().addSubscribed(codeTeamWf, joeSmith_CurrentUser, changes);
      changes.execute();

      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Subscribed.name(), notifyEvent.getType());
      Assert.assertEquals(1, notifyEvent.getEmailAddresses().size());
      Assert.assertEquals(joeSmith_CurrentUser.getEmail(), notifyEvent.getEmailAddresses().iterator().next());
      Assert.assertEquals("[Demo Code Team Workflow] titled [SAW (uncommitted) More Reqt Changes for Diagram View] " //
         + "transitioned to [Implement] and you subscribed for notification.", notifyEvent.getDescription());

   }

   @org.junit.Test
   public void testNotifyCompleted() {

      XResultData rd = new XResultData();

      AtsTestUtil.cleanupAndReset("NotificationProcessor.testNotifyCompleted");
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = atsApi.createChangeSet("Set Originator");
      TeamWorkFlowManager wfMgr =
         new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideTransitionValidityCheck);
      wfMgr.transitionTo(TeamState.Completed, joeSmith_CurrentUser, false, changes);
      changes.execute();

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Completed);
      event.getWorkItemIds().add(teamWf.getId());

      // No notification since Joe completed his own workflow
      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      changes = atsApi.createChangeSet("Set Originator");
      changes.setCreatedBy(teamWf, kay_ValidEmail, false, null);
      changes.execute();

      processor = new WorkItemNotificationProcessor(rd);
      notifications = new AtsNotificationCollector();
      processor.run(notifications, event);

      // One notification because Kay is originator
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Completed.name(), notifyEvent.getType());
      Assert.assertEquals(kay_ValidEmail.getEmail(), notifyEvent.getEmailAddresses().iterator().next());
      Assert.assertEquals(
         "[Team Workflow] titled [AtsTestUtilCore - Team WF [NotificationProcessor.testNotifyCompleted]] is [Completed]",
         notifyEvent.getDescription());

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testNotifyCancelled() {

      XResultData rd = new XResultData();

      AtsTestUtil.cleanupAndReset("NotificationProcessor.testNotifyCancelled");
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = atsApi.createChangeSet("Set Originator");
      changes.setCreatedBy(teamWf, kay_ValidEmail, false, null);
      changes.execute();

      changes = atsApi.createChangeSet("Tansition to cancelled");
      TeamWorkFlowManager wfMgr =
         new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideTransitionValidityCheck);
      Result result = wfMgr.transitionTo(TeamState.Cancelled, joeSmith_CurrentUser, false, changes);
      Assert.assertTrue(result.getText(), result.isTrue());
      changes.execute();
      Assert.assertEquals("State must be Cancelled", TeamState.Cancelled.getName(), teamWf.getCurrentStateName());

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Cancelled);
      event.getWorkItemIds().add(teamWf.getId());

      processor.run(notifications, event);

      // One notification because Kay is originator
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Cancelled.name(), notifyEvent.getType());
      Assert.assertEquals(kay_ValidEmail.getEmail(), notifyEvent.getEmailAddresses().iterator().next());
      Assert.assertTrue(notifyEvent.getDescription().startsWith("[Team Workflow] titled [AtsTestUtilCore - " //
         + "Team WF [NotificationProcessor.testNotifyCancelled]] " //
         + "was [Cancelled] from the [Implement] state on "));

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testNotifySubscribedTeamOrAi() {

      XResultData rd = new XResultData();

      AtsTestUtil.cleanupAndReset("NotificationProcessor.testNotifySubscribedTeamOrAi");
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(rd);
      AtsNotificationCollector notifications = new AtsNotificationCollector();

      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(jason_ValidEmail.getUserId());
      event.setNotifyType(AtsNotifyType.SubscribedTeam);
      event.getWorkItemIds().add(teamWf.getId());
      processor.run(notifications, event);

      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      // Setup Subscribed Team for Joe and AI for Kay
      IAtsChangeSet changes = atsApi.createChangeSet("Set AI and Team Def Config");
      changes.relate(teamWf.getActionableItems().iterator().next(), AtsRelationTypes.SubscribedUser_User,
         DemoUsers.Joe_Smith);
      changes.relate(teamWf.getTeamDefinition(), AtsRelationTypes.SubscribedUser_User, DemoUsers.Kay_Jones);
      changes.execute();

      processor = new WorkItemNotificationProcessor(rd);
      notifications = new AtsNotificationCollector();
      event.setNotifyType(AtsNotifyType.SubscribedAi);
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      for (AtsNotificationEvent notifyEvent : notifications.getNotificationEvents()) {
         if (notifyEvent.getEmailAddresses().isEmpty()) {
            Assert.assertTrue(notifyEvent.getEmailAddresses().contains(joeSmith_CurrentUser.getEmail()));
            Assert.assertTrue(notifyEvent.getDescription().contains(
               "You have subscribed for email notification for Team [AtsTestUtilCore - Team Def [NotificationProcessor.testNotifySubscribedTeamOrAi]]"));
         }
      }

      processor = new WorkItemNotificationProcessor(rd);
      notifications = new AtsNotificationCollector();
      event.setNotifyType(AtsNotifyType.SubscribedTeam);
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      for (AtsNotificationEvent notifyEvent : notifications.getNotificationEvents()) {
         if (notifyEvent.getEmailAddresses().isEmpty()) {
            Assert.assertTrue(notifyEvent.getEmailAddresses().contains(kay_ValidEmail.getEmail()));
            Assert.assertTrue(notifyEvent.getDescription().contains(
               "You have subscribed for email notification for Team [AtsTestUtilCore - Team Def [NotificationProcessor.testNotifySubscribedTeamOrAi]]"));
         }
      }
   }

}
