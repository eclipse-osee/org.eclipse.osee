/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.demo.DemoActionableItems;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager.ConfigurationProvider;
import org.eclipse.osee.ats.core.client.notify.AtsNotifyUsers;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.util.SubscribeManager;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.workflow.state.StateManagerUtility;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryOptions;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.INotificationManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;
import org.junit.Assert;

/**
 * Test unit for {@link AtsNotifyUsers}
 * 
 * @author Donald G. Dunne
 */
public class AtsNotifyUsersTest {
   @org.junit.BeforeClass
   public static void setup() throws OseeCoreException {
      AtsUtilClient.setEmailEnabled(true);
      AtsTestUtil.cleanupAndReset(AtsNotificationManagerTest.class.getSimpleName());
      cleanUpAction();
   }

   @org.junit.AfterClass
   public static void cleanup() throws OseeCoreException {
      UserManager.getUser(DemoUsers.Jason_Michael).reloadAttributesAndRelations();
      UserManager.getUser(DemoUsers.Kay_Jones).reloadAttributesAndRelations();
      UserManager.getUser(DemoUsers.Alex_Kay).reloadAttributesAndRelations();
      UserManager.getUser(DemoUsers.Joe_Smith).reloadAttributesAndRelations();
      UserManager.getUser(DemoUsers.Inactive_Steve).reloadAttributesAndRelations();
      AtsTestUtil.cleanup();
      cleanUpAction();

      AtsUtilClient.setEmailEnabled(false);

      AtsTestUtil.validateArtifactCache();
      AtsUtilClient.setEmailEnabled(false);

   }

   private static void cleanUpAction() throws OseeCoreException {
      SkynetTransaction changes =
         TransactionManager.createTransaction(AtsUtilClient.getAtsBranch(), AtsNotifyUsersTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromAttribute(CoreAttributeTypes.Name,
         AtsNotifyUsersTest.class.getSimpleName(), AtsUtilClient.getAtsBranch(), QueryOptions.CONTAINS_MATCH_OPTIONS)) {
         art.deleteAndPersist(changes);
      }
      changes.execute();
   }

   @org.junit.Test
   public void testNotify() throws OseeCoreException {
      User jason_ValidEmail = UserManager.getUser(DemoUsers.Jason_Michael);
      jason_ValidEmail.setEmail("jason@boeing.com");
      User alex_NoValidEmail = UserManager.getUser(DemoUsers.Alex_Kay);
      alex_NoValidEmail.setEmail("");
      User kay_ValidEmail = UserManager.getUser(DemoUsers.Kay_Jones);
      kay_ValidEmail.setEmail("kay@boeing.com");
      User joeSmith_CurrentUser = UserManager.getUser(DemoUsers.Joe_Smith);
      joeSmith_CurrentUser.setEmail("joe@boeing.com");
      User inactiveSteve = UserManager.getUser(DemoUsers.Inactive_Steve);
      inactiveSteve.setEmail("inactiveSteves@boeing.com");

      MockNotificationManager notifyManager = new MockNotificationManager();
      MockConfigurationProvider configProvider = new MockConfigurationProvider(notifyManager, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      teamArt.setName(AtsNotifyUsersTest.class.getSimpleName() + "-testNotify");
      teamArt.internalSetCreatedBy(AtsClientService.get().getUserAdmin().getUserFromOseeUser(kay_ValidEmail), changes);
      List<User> assignees = new ArrayList<User>();
      assignees.addAll(Arrays.asList(inactiveSteve, alex_NoValidEmail, jason_ValidEmail, kay_ValidEmail,
         joeSmith_CurrentUser));
      teamArt.getStateMgr().setAssignees(AtsClientService.get().getUserAdmin().getAtsUsers(assignees));
      changes.add(teamArt);
      changes.execute();

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Originator);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      OseeNotificationEvent event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Originator.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the originator of [Team Workflow] state [Analyze] titled [AtsNotifyUsersTest-testNotify]",
         event.getDescription());

      notifyManager.clear();
      teamArt.internalSetCreatedBy(AtsClientService.get().getUserAdmin().getUserFromOseeUser(inactiveSteve), changes);
      teamArt.persist(getClass().getSimpleName());
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Originator);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());
      teamArt.internalSetCreatedBy(AtsClientService.get().getUserAdmin().getUserFromOseeUser(kay_ValidEmail), changes);
      teamArt.persist(getClass().getSimpleName());

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      // joe smith should be removed from list cause it's current user
      // alex should be removed cause not valid email
      List<User> expected = new ArrayList<User>();
      expected.add(jason_ValidEmail);
      expected.add(kay_ValidEmail);
      List<User> users = new ArrayList<User>();
      users.addAll(event.getUsers());
      Assert.assertTrue(org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(expected, users));
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [AtsNotifyUsersTest-testNotify]",
         event.getDescription());

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt,
         Collections.singleton(AtsClientService.get().getUserAdmin().getUserFromOseeUser(jason_ValidEmail)),
         AtsNotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      // only alex should be emailed cause sent in list
      Assert.assertEquals(1, event.getUsers().size());
      Assert.assertEquals(jason_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [AtsNotifyUsersTest-testNotify]",
         event.getDescription());

      notifyManager.clear();
      SubscribeManager.toggleSubscribe(teamArt);
      changes.clear();
      SubscribeManager.addSubscribed(teamArt, AtsClientService.get().getUserAdmin().getUserFromOseeUser(inactiveSteve),
         changes);
      changes.execute();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Subscribed);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Subscribed.name(), event.getType());
      Assert.assertEquals(UserManager.getUser(), event.getUsers().iterator().next());
      Assert.assertEquals(
         "[Team Workflow] titled [AtsNotifyUsersTest-testNotify] transitioned to [Analyze] and you subscribed for notification.",
         event.getDescription());
      SubscribeManager.toggleSubscribe(teamArt);

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());

      notifyManager.clear();
      StateManagerUtility.initializeStateMachine(teamArt.getStateMgr(), TeamState.Completed, null,
         AtsClientService.get().getUserAdmin().getCurrentUser(), changes);
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Completed.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals("[Team Workflow] titled [AtsNotifyUsersTest-testNotify] is [Completed]",
         event.getDescription());

      notifyManager.clear();
      teamArt.internalSetCreatedBy(AtsClientService.get().getUserAdmin().getUserFromOseeUser(inactiveSteve), changes);
      teamArt.persist(getClass().getSimpleName());
      StateManagerUtility.initializeStateMachine(teamArt.getStateMgr(), TeamState.Completed, null,
         AtsClientService.get().getUserAdmin().getCurrentUser(), changes);
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());
      teamArt.internalSetCreatedBy(AtsClientService.get().getUserAdmin().getUserFromOseeUser(kay_ValidEmail), changes);
      teamArt.persist(getClass().getSimpleName());

      notifyManager.clear();
      StateManagerUtility.initializeStateMachine(teamArt.getStateMgr(), TeamState.Analyze, null,
         AtsClientService.get().getUserAdmin().getCurrentUser(), changes);
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), TeamState.Cancelled.getName(), null,
            "this is the reason", changes, TransitionOption.OverrideTransitionValidityCheck);
      changes.clear();
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      Assert.assertTrue("Transition should have no errors", results.isEmpty());

      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Cancelled.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertTrue(event.getDescription().startsWith(
         "[Team Workflow] titled [AtsNotifyUsersTest-testNotify] was [Cancelled] from the [Analyze] state on"));
      Assert.assertTrue(event.getDescription().endsWith(".<br>Reason: [this is the reason]"));

   }

   @org.junit.Test
   public void testNotifyOnNewAction() throws OseeCoreException {

      User kay_ValidEmail = UserManager.getUser(DemoUsers.Kay_Jones);
      kay_ValidEmail.setEmail("kay@boeing.com");

      MockNotificationManager notifyManager = new MockNotificationManager();
      MockConfigurationProvider configProvider = new MockConfigurationProvider(notifyManager, true);
      AtsNotificationManager.setConfigurationProvider(configProvider);
      AtsNotificationManager.setInTest(false);

      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      ActionManager.createAction(null, getClass().getSimpleName() + "-OnNewAction", "Description",
         ChangeType.Improvement, "2", false, null,
         ActionableItems.getActionableItems(Arrays.asList(DemoActionableItems.SAW_SW_Design.getName())), new Date(),
         AtsClientService.get().getUserAdmin().getCurrentUser(), null, changes);
      changes.execute();

      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      OseeNotificationEvent event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Endorse] titled [AtsNotifyUsersTest-OnNewAction]",
         event.getDescription());
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
