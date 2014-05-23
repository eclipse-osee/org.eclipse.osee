/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil.AtsTestUtilState;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager.ConfigurationProvider;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.client.review.role.Role;
import org.eclipse.osee.ats.core.client.review.role.UserRole;
import org.eclipse.osee.ats.core.client.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.INotificationManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for (@link AtsNotificationManager}
 * 
 * @author Donald G. Dunne
 */
public class AtsNotificationManagerTest {

   @BeforeClass
   public static void setup() {
      AtsUtilClient.setEmailEnabled(true);
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      User user = UserManager.getUser(DemoUsers.Alex_Kay);
      user.setSoleAttributeValue(CoreAttributeTypes.Email, "");
      user.deleteRelations(AtsRelationTypes.SubscribedUser_Artifact);
      user.persist(AtsNotificationManagerTest.class.getSimpleName());

      AtsNotificationManager.setInTest(true);
      AtsTestUtil.cleanup();
      AtsUtilClient.setEmailEnabled(false);
   }

   @org.junit.Test
   public void testOriginatorNotification() throws OseeCoreException {

      //---------------------------------------------------
      // Test that notifications sent if originator changes
      //---------------------------------------------------

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);
      // create new action which should reset originator cache in notification manager
      AtsTestUtil.cleanupAndReset(AtsNotificationManagerTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      // set valid email for Alex_Kay
      UserManager.getUser(DemoUsers.Alex_Kay).setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      UserManager.getUser(DemoUsers.Alex_Kay).persist(getClass().getSimpleName());

      // reset the originator
      AtsChangeSet changes = new AtsChangeSet("Change Originator");
      teamArt.setCreatedBy(AtsClientService.get().getUserService().getUserById(DemoUsers.Alex_Kay.getUserId()), false,
         new Date(), changes);
      // persist will kick event which will log the notification event and send
      changes.execute();

      // verify notification exists now
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "You have been set as the originator"));

      //---------------------------------------------------
      // Test that NO notifications sent if in test mode
      //---------------------------------------------------

      // reset the originator back to joe smith
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), false,
         new Date(), changes);
      // persist will kick event which will log the notification event and send
      teamArt.persist("Change originator");
      AtsNotificationManager.setInTest(true);
      mgr.clear();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      // set valid email for Alex_Kay
      UserManager.getUser(DemoUsers.Alex_Kay).setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      UserManager.getUser(DemoUsers.Alex_Kay).persist(getClass().getSimpleName());

      // reset the originator
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), false,
         new Date(), changes);
      // persist will kick event which will log the notification event and send
      teamArt.persist("Change originator");

      // verify NO notification exists now
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      //---------------------------------------------------
      // Test that NO notifications sent if user email is invalid
      //---------------------------------------------------

      // reset the originator back to joe smith
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), false,
         new Date(), changes);
      // persist will kick event which will log the notification event and send
      teamArt.persist("Change originator");
      AtsNotificationManager.setInTest(true);
      mgr.clear();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      // set invalid email for Alex_Kay
      UserManager.getUser(DemoUsers.Alex_Kay).deleteAttributes(CoreAttributeTypes.Email);
      UserManager.getUser(DemoUsers.Alex_Kay).persist(getClass().getSimpleName());

      // reset the originator
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), false,
         new Date(), changes);
      // persist will kick event which will log the notification event and send
      teamArt.persist("Change originator");

      // verify NO notification exists now
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testAddAssigneeNotification() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();

      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(AtsNotificationManagerTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      // set valid email for Alex_Kay
      List<User> users = new ArrayList<User>();
      User Alex_Kay = UserManager.getUser(DemoUsers.Alex_Kay);
      Alex_Kay.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      users.add(Alex_Kay);

      User Jason_Michael = UserManager.getUser(DemoUsers.Jason_Michael);
      users.add(Jason_Michael);

      User Inactive_Steve = UserManager.getUser(DemoUsers.Inactive_Steve);
      Inactive_Steve.setSoleAttributeValue(CoreAttributeTypes.Email, "inactive.steve@boeing.com");
      users.add(Inactive_Steve);

      // current assignee shouldn't be emailed
      UserManager.getUser().setEmail("joe.smith@boeing.com");
      users.add(UserManager.getUser());

      teamArt.getStateMgr().addAssignees(AtsClientService.get().getUserServiceClient().getAtsUsers(users));
      AtsChangeSet.execute(getClass().getSimpleName(), teamArt);

      // verify notification exists now only for active, valid email Alex, not for others
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "You have been set as the assignee"));
      // but all 4 are now assigned
      Assert.assertEquals(4, teamArt.getStateMgr().getAssignees().size());

      for (User user : users) {
         user.reloadAttributesAndRelations();
      }
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testSetAssigneeNotification() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(AtsNotificationManagerTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      Assert.assertEquals("Joe should be assigned; currently = " + teamArt.getStateMgr().getAssigneesStr(), 1,
         teamArt.getStateMgr().getAssignees().size());

      // set valid email for Alex_Kay and add as assignee
      User Alex_Kay = UserManager.getUser(DemoUsers.Alex_Kay);
      Alex_Kay.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      Alex_Kay.persist(getClass().getSimpleName());
      teamArt.getStateMgr().addAssignee(AtsClientService.get().getUserServiceClient().getUserFromOseeUser(Alex_Kay));
      teamArt.persist(getClass().getSimpleName());
      Assert.assertEquals("Alex and Joe should be assigned; currently = " + teamArt.getStateMgr().getAssigneesStr(), 2,
         teamArt.getStateMgr().getAssignees().size());
      mgr.clear();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      List<User> usersToSet = new ArrayList<User>();
      User Jason_Michael = UserManager.getUser(DemoUsers.Jason_Michael);
      Jason_Michael.setEmail("jason.michael@boeing.com");
      usersToSet.add(Jason_Michael);

      User Inactive_Steve = UserManager.getUser(DemoUsers.Inactive_Steve);
      Inactive_Steve.setSoleAttributeValue(CoreAttributeTypes.Email, "inactive.steve@boeing.com");
      usersToSet.add(Inactive_Steve);

      // current assignee and Alex_Kay shouldn't be emailed cause they were already assigned
      UserManager.getUser().setEmail("joe.smith@boeing.com");
      usersToSet.add(UserManager.getUser());
      usersToSet.add(Alex_Kay);

      teamArt.getStateMgr().setAssignees(AtsClientService.get().getUserServiceClient().getAtsUsers(usersToSet));
      AtsChangeSet.execute(getClass().getSimpleName(), teamArt);

      // verify notification exists now only for Jason_Michael, not for others
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "You have been set as the assignee"));
      // but all 4 are now assigned
      Assert.assertEquals(4, teamArt.getStateMgr().getAssignees().size());

      for (User user : usersToSet) {
         user.reloadAttributesAndRelations();
      }
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCompletedNotification() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // set originator as Alex Kay
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName() + " - set originator");
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), false,
         new Date(), changes);
      changes.execute();

      // set alex kay having valid email address
      User user = UserManager.getUser(DemoUsers.Alex_Kay);
      user.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      user.persist(getClass().getSimpleName() + "- set alex email address");
      mgr.clear();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());
      changes.reset(getClass().getSimpleName());
      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Completed, AtsClientService.get().getUserService().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Completed.getName());
      changes.execute();

      // verify notification to originator
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().endsWith("is [Completed]"));

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testCancelledNotification() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // set originator as Alex Kay
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName() + " - set originator");
      teamArt.setCreatedBy(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), false,
         new Date(), changes);
      changes.execute();

      // set alex kay having valid email address
      User user = UserManager.getUser(DemoUsers.Alex_Kay);
      user.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      user.persist(getClass().getSimpleName() + "-set key email address");
      mgr.clear();

      // verify no notification events yet
      Assert.assertEquals(0, mgr.getNotificationEvents().size());
      changes.reset(getClass().getSimpleName());
      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Cancelled, AtsClientService.get().getUserService().getCurrentUser(),
            changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      Assert.assertEquals(teamArt.getCurrentStateName(), TeamState.Cancelled.getName());
      changes.execute();

      // verify notification to originator
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "[Team Workflow] titled [AtsTestUtil - Team WF [AtsNotificationManagerTest]] was [Cancelled] from the [Analyze]"));

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testSubscribedTeam() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // setup alex email and subscribe for team definition
      User alex = UserManager.getUser(DemoUsers.Alex_Kay);
      alex.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      alex.persist(getClass().getSimpleName() + "- set alex email address");

      AtsTestUtil.getTestTeamDef().getSubscribed().add(
         AtsClientService.get().getUserServiceClient().getUserFromOseeUser(alex));

      mgr.clear();

      // create another action
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      ActionArtifact actionArt =
         ActionManager.createAction(null, getClass().getSimpleName() + " - testSubscribedTeam", "description",
            ChangeType.Improvement, "1", false, null, Arrays.asList(AtsTestUtil.getTestAi()), new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), null, changes);

      // verify notification to subscriber
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "You have subscribed for email notification for Team "));
      changes.execute();

      SkynetTransaction transaction2 =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());

      actionArt.getTeams().iterator().next().deleteAndPersist(transaction2);
      actionArt.deleteAndPersist(transaction2);

      User user = UserManager.getUser(DemoUsers.Alex_Kay);
      user.setSoleAttributeValue(CoreAttributeTypes.Email, "");
      user.deleteRelations(AtsRelationTypes.SubscribedUser_Artifact);
      user.persist(transaction2);

      transaction2.execute();

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testSubscribedActionableItem() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // setup alex email and subscribe for AI
      User alex = UserManager.getUser(DemoUsers.Alex_Kay);
      alex.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      alex.persist(getClass().getSimpleName() + "- set alex email address");

      AtsTestUtil.getTestAi().getSubscribed().add(
         AtsClientService.get().getUserServiceClient().getUserFromOseeUser(alex));

      mgr.clear();

      // create another action
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      ActionArtifact actionArt =
         ActionManager.createAction(null, getClass().getSimpleName() + " - testSubscribedAI", "description",
            ChangeType.Improvement, "1", false, null, Arrays.asList(AtsTestUtil.getTestAi()), new Date(),
            AtsClientService.get().getUserService().getCurrentUser(), null, changes);

      // verify notification to subscriber
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().startsWith(
         "You have subscribed for email notification for Actionable Item "));
      changes.execute();

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());

      actionArt.getTeams().iterator().next().deleteAndPersist(transaction);
      actionArt.deleteAndPersist(transaction);

      User user = UserManager.getUser(DemoUsers.Alex_Kay);
      user.setSoleAttributeValue(CoreAttributeTypes.Email, "");
      user.deleteRelations(AtsRelationTypes.SubscribedUser_Artifact);
      user.persist(transaction);

      transaction.execute();

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testSubscribedWorkflow() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();
      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // setup alex email and subscribe for AI
      User alex = UserManager.getUser(DemoUsers.Alex_Kay);
      alex.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      alex.persist(getClass().getSimpleName() + "- set alex email address");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      teamArt.setRelations(AtsRelationTypes.SubscribedUser_User, Arrays.asList(alex));
      teamArt.persist(getClass().getSimpleName() + " - add Workflow subscription");

      mgr.clear();

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      Result result =
         AtsTestUtil.transitionTo(AtsTestUtilState.Implement,
            AtsClientService.get().getUserService().getCurrentUser(), changes,
            TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);
      Assert.assertEquals(Result.TrueResult, result);
      changes.execute();

      // verify notification to workflow subscriber
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertEquals(
         "[Team Workflow] titled [AtsTestUtil - Team WF [AtsNotificationManagerTest]] transitioned to [Implement] and you subscribed for notification.",
         mgr.getNotificationEvents().iterator().next().getDescription());

      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testReviewersCompleted() throws OseeCoreException {

      // create a test notification manager
      MockNotificationManager mgr = new MockNotificationManager();

      // restart notification manager with this one and set to NotInTest (cause normally, testing has notification system OFF)
      MockConfigurationProvider configProvider = new MockConfigurationProvider(mgr, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      // create new action
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

      // setup alex email and subscribe for AI
      User alex = UserManager.getUser(DemoUsers.Alex_Kay);
      alex.setSoleAttributeValue(CoreAttributeTypes.Email, "alex.kay@boeing.com");
      alex.persist(getClass().getSimpleName() + "- set alex email address");

      User kay = UserManager.getUser(DemoUsers.Kay_Jones);
      kay.setSoleAttributeValue(CoreAttributeTypes.Email, "kay.jones@boeing.com");
      kay.persist(getClass().getSimpleName() + "- set kay email address");

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());

      PeerToPeerReviewArtifact peerArt =
         AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.None, AtsTestUtilState.Analyze, changes);
      List<UserRole> roles = new ArrayList<UserRole>();
      UserRole author =
         new UserRole(Role.Author, AtsClientService.get().getUserServiceClient().getUserFromOseeUser(alex));
      roles.add(author);
      UserRole moderator =
         new UserRole(Role.Moderator, AtsClientService.get().getUserServiceClient().getUserFromOseeUser(kay));
      roles.add(moderator);
      UserRole reviewer1 = new UserRole(Role.Reviewer, AtsClientService.get().getUserService().getCurrentUser());
      roles.add(reviewer1);
      UserRole reviewer2 =
         new UserRole(Role.Reviewer, AtsClientService.get().getUserServiceClient().getUserFromToken(
            DemoUsers.Jason_Michael));
      roles.add(reviewer2);

      Result result =
         PeerToPeerReviewManager.transitionTo(peerArt, PeerToPeerReviewState.Review, roles, null,
            AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      Assert.assertEquals(Result.TrueResult, result);
      changes.execute();
      mgr.clear();

      peerArt.getCurrentStateName();

      // complete reviewer1 role
      changes.reset(getClass().getSimpleName() + " - update reviewer 1");
      UserRoleManager roleMgr = new UserRoleManager(peerArt);
      reviewer1.setHoursSpent(1.0);
      reviewer1.setCompleted(true);
      roleMgr.addOrUpdateUserRole(reviewer1, peerArt);
      roleMgr.saveToArtifact(changes);
      changes.execute();

      // no notifications sent
      Assert.assertEquals(0, mgr.getNotificationEvents().size());

      // complete reviewer2 role
      changes.reset(getClass().getSimpleName() + " - update reviewer 2");
      reviewer2.setHoursSpent(1.0);
      reviewer2.setCompleted(true);
      roleMgr.addOrUpdateUserRole(reviewer2, peerArt);
      roleMgr.saveToArtifact(changes);
      changes.execute();

      // notification sent to author/moderator
      Assert.assertEquals(1, mgr.getNotificationEvents().size());
      Assert.assertTrue(mgr.getNotificationEvents().iterator().next().getDescription().equals(
         "You are Author/Moderator of [PeerToPeer Review] titled [AtsTestUtil Test Peer Review] which has been reviewed by all reviewers"));
      // email both moderator and author
      Assert.assertEquals(2, mgr.getNotificationEvents().iterator().next().getUsers().size());
      peerArt.deleteAndPersist();
      AtsTestUtil.cleanup();
   }

   private static final class MockConfigurationProvider implements ConfigurationProvider {

      private final INotificationManager notificationManager;
      private final boolean isProduction;

      public MockConfigurationProvider(INotificationManager notificationManager, boolean isProduction) {
         super();
         this.notificationManager = notificationManager;
         this.isProduction = isProduction;
      }

      @Override
      public INotificationManager getNotificationManager() {
         return notificationManager;
      }

      @Override
      public boolean isProduction() {
         return isProduction;
      }
   }
}
