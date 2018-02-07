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
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.actions.DuplicateWorkflowAction;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.After;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowActionTest extends AbstractAtsActionRunTest {

   private IAtsTeamWorkflow newTeamWf;
   private IAtsTeamWorkflow dupTeamWf;

   @Override
   public DuplicateWorkflowAction createAction() {
      return new DuplicateWorkflowAction(Collections.singleton(AtsTestUtil.getTeamWf()));
   }

   @Test
   public void testAssigneesAndNotifications() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();

      List<IAtsUser> assignees = setupAssignees(teamWf);

      IAtsUser originator = AtsClientService.get().getUserServiceClient().getUserFromOseeUser(
         UserManager.getUser(DemoUsers.Jason_Michael));

      // new workflow
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Duplicate Workflow");
      newTeamWf = AtsClientService.get().getActionFactory().createTeamWorkflow(teamWf.getParentAction(),
         teamWf.getTeamDefinition(),
         AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(teamWf), assignees,
         changes, new Date(), originator, null, CreateTeamOption.Duplicate_If_Exists);

      assertEquals("invalid number of assignees", 2, newTeamWf.getAssignees().size());
      assertEquals("invalid number of notifications", 2,
         changes.getNotifications().getWorkItemNotificationEvents().size());

      // duplicate workflow
      dupTeamWf = AtsClientService.get().getWorkItemFactory().getTeamWf(
         ((TeamWorkFlowArtifact) teamWf.getStoreObject()).duplicate(AtsClientService.get().getAtsBranch(),
            Arrays.asList(AtsAttributeTypes.AtsId)));

      AtsClientService.get().getActionFactory().initializeNewStateMachine(dupTeamWf, assignees, new Date(),
         AtsCoreUsers.SYSTEM_USER, dupTeamWf.getWorkDefinition(), changes);

      changes.add(dupTeamWf);
      changes.addWorkItemNotificationEvent(
         AtsNotificationEventFactory.getWorkItemNotificationEvent(teamWf.getAssignees().iterator().next(), dupTeamWf,
            AtsNotifyType.Originator, AtsNotifyType.Assigned, AtsNotifyType.SubscribedTeamOrAi));

      assertTrue(changes.getNotifications().getWorkItemNotificationEvents().size() == 4);
      assertTrue(dupTeamWf.getAssignees().size() == 2);

      changes.execute();
   }

   private List<IAtsUser> setupAssignees(IAtsTeamWorkflow teamWf) {
      List<IAtsUser> assignees = new LinkedList<>();
      assignees.addAll(teamWf.getAssignees());
      IAtsUser lead =
         AtsClientService.get().getUserServiceClient().getUserFromOseeUser(UserManager.getUser(DemoUsers.Kay_Jones));
      assignees.add(lead);
      return assignees;
   }

   @After
   public void tearDown() throws Exception {
      if (newTeamWf != null) {
         ((TeamWorkFlowArtifact) newTeamWf.getStoreObject()).deleteAndPersist();
      }

      if (dupTeamWf != null) {
         ((TeamWorkFlowArtifact) dupTeamWf.getStoreObject()).deleteAndPersist();
      }
   }

}
