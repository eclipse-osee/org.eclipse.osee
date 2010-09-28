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
package org.eclipse.osee.ats.test.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsNotifyUsers.NotifyType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.SubscribeManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationEvent;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsNotifyUsersTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      Collection<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromName(AtsNotifyUsersTest.class.getSimpleName(), AtsUtil.getAtsBranch(),
            EXCLUDE_DELETED);
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "AtsNotifyUsersTest Clean-up");
      for (Artifact artifact : artifacts) {
         artifact.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   @Test
   public void testNotify() throws OseeCoreException {
      User jason_ValidEmail = UserManager.getUser(DemoUsers.Jason_Michael);
      jason_ValidEmail.setEmail("this@boeing.com");
      User alex_NoValidEmail = UserManager.getUser(DemoUsers.Alex_Kay);
      User kay_ValidEmail = UserManager.getUser(DemoUsers.Kay_Jones);
      kay_ValidEmail.setEmail("this@boeing.com");
      User joeSmith_CurrentUser = UserManager.getUser(DemoUsers.Joe_Smith);
      joeSmith_CurrentUser.setEmail("this@boeing.com");

      TestNotificationManager notifyManager = new TestNotificationManager();
      AtsNotifyUsers atsNotifyUsers = AtsNotifyUsers.getInstance();
      atsNotifyUsers.setNotificationManager(notifyManager);
      atsNotifyUsers.setInTest(true);

      TeamWorkFlowArtifact teamArt = DemoTestUtil.createSimpleAction(AtsNotifyUsersTest.class.getSimpleName(), null);
      teamArt.setOriginator(kay_ValidEmail);
      List<User> assignees = Arrays.asList(alex_NoValidEmail, jason_ValidEmail, kay_ValidEmail, joeSmith_CurrentUser);
      teamArt.getStateMgr().setAssignees(assignees);
      teamArt.persist();

      notifyManager.clear();
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Originator);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      OseeNotificationEvent event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Originator.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals(
         "You have been set as the originator of [Demo Code Team Workflow] state [Endorse] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Assigned.name(), event.getType());
      // joe smith should be removed from list cause it's current user
      // alex should be removed cause not valid email
      Assert.assertTrue(org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(
         Arrays.asList(jason_ValidEmail, kay_ValidEmail), event.getUsers()));
      Assert.assertEquals(
         "You have been set as the assignee of [Demo Code Team Workflow] in state [Endorse] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      AtsNotifyUsers.getInstance().notify(teamArt, Collections.singleton(jason_ValidEmail), NotifyType.Assigned);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Assigned.name(), event.getType());
      // only alex should be emailed cause sent in list
      Assert.assertEquals(Collections.singleton(jason_ValidEmail), event.getUsers());
      Assert.assertEquals(
         "You have been set as the assignee of [Demo Code Team Workflow] in state [Endorse] titled [AtsNotifyUsersTest]",
         event.getDescription());

      notifyManager.clear();
      new SubscribeManager(teamArt).toggleSubscribe(false);
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Subscribed);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Subscribed.name(), event.getType());
      Assert.assertEquals(UserManager.getUser(), event.getUsers().iterator().next());
      Assert.assertEquals(
         "[Demo Code Team Workflow] titled [AtsNotifyUsersTest] transitioned to [Endorse] and you subscribed for notification.",
         event.getDescription());
      new SubscribeManager(teamArt).toggleSubscribe(false);

      notifyManager.clear();
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Completed);
      Assert.assertEquals(0, notifyManager.getNotificationEvents().size());

      notifyManager.clear();
      teamArt.getStateMgr().initializeStateMachine(DefaultTeamState.Completed.name());
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Completed);
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Completed.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertEquals("[Demo Code Team Workflow] titled [AtsNotifyUsersTest] is Completed", event.getDescription());

      notifyManager.clear();
      teamArt.getLog().addLog(LogType.StateCancelled, "Endorse", "this is the reason");
      teamArt.getStateMgr().initializeStateMachine(DefaultTeamState.Cancelled.name());
      AtsNotifyUsers.getInstance().notify(teamArt, NotifyType.Cancelled);
      Assert.assertEquals(1, notifyManager.getNotificationEvents().size());
      event = notifyManager.getNotificationEvents().get(0);
      Assert.assertEquals(NotifyType.Cancelled.name(), event.getType());
      Assert.assertEquals(kay_ValidEmail, event.getUsers().iterator().next());
      Assert.assertTrue(event.getDescription().startsWith(
         "[Demo Code Team Workflow] titled [AtsNotifyUsersTest] was cancelled from the [Endorse] state on"));
      Assert.assertTrue(event.getDescription().endsWith(".<br>Reason: [this is the reason]"));

   }

}
