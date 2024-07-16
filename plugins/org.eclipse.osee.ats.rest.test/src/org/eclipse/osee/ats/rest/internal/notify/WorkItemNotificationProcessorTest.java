/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.notify;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.notify.AtsWorkItemNotificationEvent;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test unit for {@link WorkItemNotificationProcessor}
 *
 * @author Donald G. Dunne
 */
public class WorkItemNotificationProcessorTest {

   AtsUser joeSmith_CurrentUser, kay_ValidEmail, jason_ValidEmail, alex_NoValidEmail, inactiveSteve;
   // @formatter:off
   @Mock ArtifactToken kayArtifact;
   @Mock IAtsTeamWorkflow teamWf;
   @Mock ArtifactToken teamWfArt;
   @Mock IAtsPeerToPeerReview peerReview;
   @Mock IAtsStateManager stateMgr;
   @Mock Log logger;
   @Mock AtsApiServer atsApiServer;
   @Mock OrcsApi orcsApi;
   @Mock IAtsWorkItemService workItemService;
   @Mock IAtsUserService userService;
   @Mock IAttributeResolver attrResolver;
   @Mock IRelationResolver relResolver;
   @Mock StateDefinition stateDef;
   @Mock IAtsTeamDefinition teamDef;
   @Mock IAtsTeamDefinitionService teamDefinitionService;
   @Mock IAtsActionableItemService actionableItemService;
   @Mock IAtsActionableItem ai;

   // @formatter:on

   private AtsUser setupUser(UserToken userToken) {
      AtsUser user = new AtsUser(userToken);
      when(userService.getUserByUserId(userToken.getUserId())).thenReturn(user);
      return user;
   }

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      joeSmith_CurrentUser = setupUser(DemoUsers.Joe_Smith);
      kay_ValidEmail = setupUser(DemoUsers.Kay_Jones);
      jason_ValidEmail = setupUser(DemoUsers.Jason_Michael);
      alex_NoValidEmail = setupUser(DemoUsers.Alex_Kay);
      inactiveSteve = setupUser(DemoUsers.Inactive_Steve);

      when(teamWf.getName()).thenReturn(WorkItemNotificationProcessorTest.class.getSimpleName() + "-testNotify");
      List<AtsUser> assignees = new ArrayList<>();
      assignees.addAll(
         Arrays.asList(inactiveSteve, alex_NoValidEmail, jason_ValidEmail, kay_ValidEmail, joeSmith_CurrentUser));
      String atsId = "ATS003";
      when(teamWf.getAtsId()).thenReturn(atsId);
      when(atsApiServer.getWorkItemService()).thenReturn(workItemService);
      when(workItemService.getWorkItemByAtsId(atsId)).thenReturn(teamWf);
      when(attrResolver.getSoleAttributeValue(teamWf, AtsAttributeTypes.LegacyPcrId, "")).thenReturn(atsId);

      when(atsApiServer.getUserService()).thenReturn(userService);
      when(atsApiServer.getAttributeResolver()).thenReturn(attrResolver);
      when(atsApiServer.getLogger()).thenReturn(logger);

      when(atsApiServer.getWorkItemService()).thenReturn(workItemService);
      when(atsApiServer.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(atsApiServer.getActionableItemService()).thenReturn(actionableItemService);
      when(workItemService.getCancelUrl(any(IAtsWorkItem.class), any(AtsApi.class))).thenReturn(
         "http://ats/action/ID/cancel");
      when(workItemService.getHtmlUrl(any(IAtsWorkItem.class), any(AtsApi.class))).thenReturn("http://ats/action/ID");
      when(teamWf.getId()).thenReturn(98L);
      when(teamWf.getArtifactTypeName()).thenReturn("Team Workflow");
      when(teamWf.getStateMgr()).thenReturn(stateMgr);
      when(stateMgr.getCurrentStateName()).thenReturn("Analyze");
      when(stateMgr.getAssignees()).thenReturn(assignees);

      when(teamWf.getStateDefinition()).thenReturn(stateDef);
      when(teamWf.getTeamDefinition()).thenReturn(teamDef);
      Set<IAtsActionableItem> ais = new HashSet<>();
      ais.add(ai);
      when(teamWf.getActionableItems()).thenReturn(ais);

   }

   @org.junit.Test
   public void testNotifyOriginator() {

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Originator);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());
      when(teamWf.getCreatedBy()).thenReturn(kay_ValidEmail);

      processor.run(notifications, event);

      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Originator.name(), notifyEvent.getType());
      Assert.assertEquals(kay_ValidEmail.getUserId(), notifyEvent.getUserIds().iterator().next());
      Assert.assertEquals(
         "You have been set as the originator of [Team Workflow] state [Analyze] titled [WorkItemNotificationProcessorTest-testNotify]",
         notifyEvent.getDescription());

      notifications = new AtsNotificationCollector();
      when(teamWf.getCreatedBy()).thenReturn(inactiveSteve);
      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

   }

   @org.junit.Test
   public void testNotifyAssignee() {

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Assigned);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());
      event.getUserIds().add(kay_ValidEmail.getUserId());

      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Assigned.name(), notifyEvent.getType());

      // joe smith should be removed from list cause it's current user
      // alex should be removed cause not valid email
      List<String> expectedUserIds = new ArrayList<>();
      expectedUserIds.add(jason_ValidEmail.getUserId());
      expectedUserIds.add(kay_ValidEmail.getUserId());
      List<AtsUser> users = new ArrayList<>();
      for (String userId : event.getUserIds()) {
         users.add(userService.getUserByUserId(userId));
      }
      event.getUserIds().clear();

      notifications = new AtsNotificationCollector();
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      notifyEvent = notifications.getNotificationEvents().get(0);

      Assert.assertTrue(
         org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(expectedUserIds, notifyEvent.getUserIds()));
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [WorkItemNotificationProcessorTest-testNotify]",
         notifyEvent.getDescription());

      notifications = new AtsNotificationCollector();
      event.getUserIds().add(jason_ValidEmail.getUserId());
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      notifyEvent = notifications.getNotificationEvents().get(0);

      Assert.assertEquals(AtsNotifyType.Assigned.name(), notifyEvent.getType());
      // only alex should be emailed cause sent in list
      Assert.assertEquals(1, notifyEvent.getUserIds().size());
      Assert.assertEquals(jason_ValidEmail.getUserId(), notifyEvent.getUserIds().iterator().next());
      Assert.assertEquals(
         "You have been set as the assignee of [Team Workflow] in state [Analyze] titled [WorkItemNotificationProcessorTest-testNotify]",
         notifyEvent.getDescription());
   }

   @org.junit.Test
   public void testNotifySubscribe() {
      when(teamWf.getStoreObject()).thenReturn(teamWfArt);
      when(atsApiServer.getRelationResolver()).thenReturn(relResolver);
      when(atsApiServer.getAttributeResolver()).thenReturn(attrResolver);
      when(relResolver.getRelated(teamWf.getStoreObject(), AtsRelationTypes.SubscribedUser_User)).thenReturn(
         Arrays.asList(kayArtifact));
      when(attrResolver.getSoleAttributeValue(kayArtifact, CoreAttributeTypes.UserId, null)).thenReturn("4444");
      when(userService.getUserByUserId(eq("4444"))).thenReturn(kay_ValidEmail);
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Subscribed);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      processor.run(notifications, event);

      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);

      Assert.assertEquals(AtsNotifyType.Subscribed.name(), notifyEvent.getType());
      // only alex should be emailed cause sent in list
      Assert.assertEquals(1, notifyEvent.getUserIds().size());
      Assert.assertEquals(kay_ValidEmail.getUserId(), notifyEvent.getUserIds().iterator().next());
      Assert.assertEquals(
         "[Team Workflow] titled [WorkItemNotificationProcessorTest-testNotify] transitioned to [Analyze] and you subscribed for notification.",
         notifyEvent.getDescription());

   }

   @org.junit.Test
   public void testNotifyCompleted() {

      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Completed);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());
      when(teamWf.isTask()).thenReturn(false);
      when(stateDef.getStateType()).thenReturn(StateType.Completed);
      when(teamWf.getCreatedBy()).thenReturn(inactiveSteve);
      when(stateMgr.getCurrentStateName()).thenReturn("Completed");

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);
      AtsNotificationCollector notifications = new AtsNotificationCollector();

      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      notifications = new AtsNotificationCollector();
      when(teamWf.getCreatedBy()).thenReturn(kay_ValidEmail);
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Completed.name(), notifyEvent.getType());
      Assert.assertEquals(kay_ValidEmail.getUserId(), notifyEvent.getUserIds().iterator().next());
      Assert.assertEquals("[Team Workflow] titled [WorkItemNotificationProcessorTest-testNotify] is [Completed]",
         notifyEvent.getDescription());

      notifications = new AtsNotificationCollector();
      when(teamWf.getCreatedBy()).thenReturn(inactiveSteve);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

   }

   @org.junit.Test
   public void testNotifyCancelled() {

      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.Cancelled);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());
      when(teamWf.isTask()).thenReturn(false);
      when(stateDef.getStateType()).thenReturn(StateType.Cancelled);
      when(teamWf.getCreatedBy()).thenReturn(inactiveSteve);
      when(teamWf.getCancelledReason()).thenReturn("this is the reason");
      when(teamWf.getCancelledFromState()).thenReturn("Analyze");
      when(stateMgr.getCurrentStateName()).thenReturn("Cancelled");

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);
      AtsNotificationCollector notifications = new AtsNotificationCollector();

      processor.run(notifications, event);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      notifications = new AtsNotificationCollector();
      when(teamWf.getCreatedBy()).thenReturn(kay_ValidEmail);
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());
      AtsNotificationEvent notifyEvent = notifications.getNotificationEvents().get(0);
      Assert.assertEquals(AtsNotifyType.Cancelled.name(), notifyEvent.getType());
      Assert.assertEquals(kay_ValidEmail.getUserId(), notifyEvent.getUserIds().iterator().next());
      Assert.assertTrue(notifyEvent.getDescription().startsWith(
         "[Team Workflow] titled [WorkItemNotificationProcessorTest-testNotify] was [Cancelled] from the [Analyze] state on"));
      Assert.assertTrue(notifyEvent.getDescription().endsWith(".<br>Reason: [this is the reason]"));

      notifications = new AtsNotificationCollector();
      when(teamWf.getCreatedBy()).thenReturn(inactiveSteve);
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

   }

   @org.junit.Test
   public void testNotifySubscribedTeamOrAi() {

      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(joeSmith_CurrentUser.getUserId());
      event.setNotifyType(AtsNotifyType.SubscribedTeamOrAi);
      event.getAtsIds().add(teamWf.getAtsId());
      event.getIds().add(teamWf.getId());
      when(teamWf.isTeamWorkflow()).thenReturn(true);
      when(stateDef.getStateType()).thenReturn(StateType.Working);
      when(stateMgr.getCurrentStateName()).thenReturn(StateType.Working.name());

      WorkItemNotificationProcessor processor = new WorkItemNotificationProcessor(atsApiServer);

      AtsNotificationCollector notifications = new AtsNotificationCollector();
      processor.run(notifications, event);
      when(teamDefinitionService.getSubscribed(teamDef)).thenReturn(new ArrayList<AtsUser>());
      when(actionableItemService.getSubscribed(ai)).thenReturn(new ArrayList<AtsUser>());
      Assert.assertEquals(0, notifications.getNotificationEvents().size());

      notifications = new AtsNotificationCollector();
      when(teamDefinitionService.getSubscribed(teamDef)).thenReturn(Arrays.asList(kay_ValidEmail));
      when(actionableItemService.getSubscribed(ai)).thenReturn(new ArrayList<AtsUser>());
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());

      notifications = new AtsNotificationCollector();
      when(teamDefinitionService.getSubscribed(teamDef)).thenReturn(new ArrayList<AtsUser>());
      when(actionableItemService.getSubscribed(ai)).thenReturn(Arrays.asList(kay_ValidEmail));
      processor.run(notifications, event);
      Assert.assertEquals(1, notifications.getNotificationEvents().size());

      notifications = new AtsNotificationCollector();
      when(teamDefinitionService.getSubscribed(teamDef)).thenReturn(Arrays.asList(jason_ValidEmail));
      when(actionableItemService.getSubscribed(ai)).thenReturn(Arrays.asList(kay_ValidEmail));
      processor.run(notifications, event);
      Assert.assertEquals(2, notifications.getNotificationEvents().size());
   }
}
