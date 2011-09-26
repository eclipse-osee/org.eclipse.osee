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
package org.eclipse.osee.ats.core.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.SubscribeManager;
import org.eclipse.osee.ats.core.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.junit.Assert;

/**
 * Test unit for {@link AtsNotifyUsers}
 * 
 * @author Donald G. Dunne
 */
public class AtsNotifyUsersTest {
   @org.junit.BeforeClass
   public static void setup() throws OseeCoreException {
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
   }

   private static void cleanUpAction() throws OseeCoreException {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtilCore.getAtsBranch(), AtsNotifyUsersTest.class.getSimpleName());
      for (Artifact art : ArtifactQuery.getArtifactListFromAttribute(CoreAttributeTypes.Name,
         AtsNotifyUsersTest.class.getSimpleName() + "%", AtsUtilCore.getAtsBranch())) {
         art.deleteAndPersist(transaction);
      }
      transaction.execute();
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

      TestNotificationManager notifyManager = new TestNotificationManager();
      AtsNotificationManager.setNotificationManager(notifyManager);
      AtsNotificationManager.setInTest(false);
      AtsNotificationManager.setIsProduction(true);

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      teamArt.setName(AtsNotifyUsersTest.class.getSimpleName());
      teamArt.internalSetCreatedBy(kay_ValidEmail);
      List<IBasicUser> assignees = new ArrayList<IBasicUser>();
      assignees.addAll(Arrays.asList(inactiveSteve, alex_NoValidEmail, jason_ValidEmail, kay_ValidEmail,
         joeSmith_CurrentUser));
      teamArt.getStateMgr().setAssignees(assignees);
      teamArt.persist(transaction);
      transaction.execute();

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Originator);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      OseeNotificationEvent event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Originator.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the originator of [Team Workflow] state [Analyze] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      teamArt.internalSetCreatedBy(inactiveSteve);
      teamArt.persist(getClass().getSimpleName());
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Originator);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());
      teamArt.internalSetCreatedBy(kay_ValidEmail);
      teamArt.persist(getClass().getSimpleName());

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      // joe smith should be removed from list cause it's current user
      // alex should be removed cause not valid email
      List<IBasicUser> expected = new ArrayList<IBasicUser>();
      expected.add(jason_ValidEmail);
      expected.add(kay_ValidEmail);
      List<IBasicUser> users = new ArrayList<IBasicUser>();
      users.addAll(event.getUsers());
      Assert.assertTrue(org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(expected, users));
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, Collections.singleton((IBasicUser) jason_ValidEmail),
         AtsNotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      // only alex should be emailed cause sent in list
      Assert.assertEquals(Collections.singleton(jason_ValidEmail), event.getUsers());
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      SubscribeManager.toggleSubscribe(teamArt);
      transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), "AtsNotifyUsersTests.toggle.subscribed");
      SubscribeManager.addSubscribed(teamArt, inactiveSteve, transaction);
      transaction.execute();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Subscribed);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Subscribed.name(), event.getType());
      Assert.assertEquals(UserManager.getUser(), event.getUsers().iterator().next());
      Assert.assertEquals(
         "[Team Workflow] titled [AtsNotifyUsersTest] transitioned to [Analyze] and you subscribed for notification.",
         event.getDescription());
      SubscribeManager.toggleSubscribe(teamArt);

      notifyManager.clear();
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());

      notifyManager.clear();
      teamArt.getStateMgr().initializeStateMachine(TeamState.Completed, null);
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Completed.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals("[Team Workflow] titled [AtsNotifyUsersTest] is [Completed]", event.getDescription());

      notifyManager.clear();
      teamArt.internalSetCreatedBy(inactiveSteve);
      teamArt.persist(getClass().getSimpleName());
      teamArt.getStateMgr().initializeStateMachine(TeamState.Completed, null);
      AtsNotificationManager.notify(teamArt, AtsNotifyType.Completed);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());
      teamArt.internalSetCreatedBy(kay_ValidEmail);
      teamArt.persist(getClass().getSimpleName());

      notifyManager.clear();
      teamArt.getStateMgr().initializeStateMachine(TeamState.Analyze, null);
      TransitionHelper helper =
         new TransitionHelper(getClass().getSimpleName(), Arrays.asList(teamArt), TeamState.Cancelled.getPageName(),
            null, "this is the reason", TransitionOption.OverrideTransitionValidityCheck);
      transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      TransitionManager transitionMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transitionMgr.handleAll();
      transaction.execute();
      Assert.assertTrue("Transition should have no errors", results.isEmpty());

      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Cancelled.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertTrue(event.getDescription().startsWith(
         "[Team Workflow] titled [AtsNotifyUsersTest] was [Cancelled] from the [Analyze] state on"));
      Assert.assertTrue(event.getDescription().endsWith(".<br>Reason: [this is the reason]"));

   }

   @org.junit.Test
   public void testNotifyOnNewAction() throws OseeCoreException {

      User kay_ValidEmail = UserManager.getUser(DemoUsers.Kay_Jones);
      kay_ValidEmail.setEmail("kay@boeing.com");

      TestNotificationManager notifyManager = new TestNotificationManager();
      AtsNotificationManager.setNotificationManager(notifyManager);
      AtsNotificationManager.setInTest(false);
      AtsNotificationManager.setIsProduction(true);

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getClass().getSimpleName());
      ActionManager.createAction(null, getClass().getSimpleName() + "-OnNewAction", "Description",
         ChangeType.Improvement, "2", false, null,
         ActionableItemManagerCore.getActionableItems(Arrays.asList(DemoActionableItems.SAW_SW_Design.getName())),
         new Date(), UserManager.getUser(), null, transaction);
      transaction.execute();

      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      OseeNotificationEvent event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Endorse] titled [AtsNotifyUsersTest-OnNewAction]",
         event.getDescription());

   }
}
