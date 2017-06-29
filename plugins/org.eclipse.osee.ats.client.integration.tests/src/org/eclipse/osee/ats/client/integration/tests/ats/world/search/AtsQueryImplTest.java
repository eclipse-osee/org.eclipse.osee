/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoWorkType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryImplTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();

      IAtsClient client = AtsClientService.get();

      Artifact wpArt = (Artifact) AtsClientService.get().getArtifactByName(AtsArtifactTypes.WorkPackage, "Work Pkg 01");
      Conditions.checkNotNull(wpArt, "Work Package");
      IAtsWorkPackage wp = client.getEarnedValueService().getWorkPackage(wpArt);

      IAtsTeamWorkflow codeWf = AtsClientService.get().getWorkItemFactory().getTeamWf(
         DemoTestUtil.getCommittedActionWorkflow(DemoWorkType.Code));
      IAtsTask codeTask = (IAtsTask) AtsClientService.get().getQueryService().createQuery(WorkItemType.Task).andAttr(
         CoreAttributeTypes.Name, "Create test plan").getItems().iterator().next();

      client.getEarnedValueService().removeWorkPackage(wp, Arrays.asList(codeWf, codeTask));
   }

   @Test
   public void test() {
      IAtsServices services = AtsClientService.get().getServices();
      IAtsQueryService queryService = services.getQueryService();

      IAtsUser joeSmith = services.getUserService().getUserById("3333");

      // test by type
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);

      assertEquals(25, query.getResults().size());
      query = queryService.createQuery(WorkItemType.Task);
      assertEquals(14, query.getResults().size());

      // assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(services.getUserService().getUserById("3333"));
      assertEquals(7, query.getResults().size());

      // team
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andTeam(Arrays.asList(30013695L));
      assertEquals(3, query.getResults().size());

      // ai
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId ai = services.getArtifactByName(AtsArtifactTypes.ActionableItem, "SAW Requirements");
      query.andActionableItem(Arrays.asList(ai.getId()));
      assertEquals(4, query.getResults().size());

      // by uuids (hijack two workflows from previous search)
      List<Long> uuids = new LinkedList<>();
      for (IAtsWorkItem workItem : query.getResults()) {
         uuids.add(workItem.getId());
      }
      query = queryService.createQuery(WorkItemType.WorkItem);
      Iterator<Long> iterator = uuids.iterator();
      query.andUuids(iterator.next(), iterator.next());
      assertEquals(2, query.getResults().size());

      // by state name
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.isOfType(WorkItemType.PeerReview);
      query.andState("Prepare");
      assertEquals(1, query.getResults().size());

      // by state type
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.andStateType(StateType.Working);
      assertEquals(42, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      assertEquals(22, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed);
      assertEquals(3, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Completed, StateType.Working);
      assertEquals(25, query.getResults().size());

      // by version
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId version = services.getArtifactByName(AtsArtifactTypes.Version, "SAW_Bld_2");
      query.andVersion(version.getId());
      assertEquals(14, query.getResults().size());

      // by assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(joeSmith);
      assertEquals(7, query.getResults().size());

      // by originator
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andOriginator(joeSmith);
      assertEquals(25, query.getResults().size());

      // by favorite
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andFavorite(joeSmith);
      assertEquals(3, query.getResults().size());

      // by subscribed
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andStateType(StateType.Working);
      query.andSubscribed(joeSmith);
      assertEquals(1, query.getResults().size());

      // setup code workflow and task to have a work package

      IAtsProgramService programService = services.getProgramService();

      IAtsWorkPackage wp =
         services.getProgramService().getWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getId()); // Work Pkg 01
      IAtsInsertionActivity activity = programService.getInsertionActivity(wp); // COMM Page
      IAtsInsertion insertion = programService.getInsertion(activity); // COMM
      IAtsProgram program = programService.getProgram(insertion); // SAW Program

      IAtsTeamWorkflow codeWf = AtsClientService.get().getWorkItemFactory().getTeamWf(
         DemoTestUtil.getCommittedActionWorkflow(DemoWorkType.Code));
      IAtsTask codeTask = (IAtsTask) AtsClientService.get().getQueryService().createQuery(WorkItemType.Task).andAttr(
         CoreAttributeTypes.Name, "Create test plan").getItems().iterator().next();

      Conditions.checkNotNull(codeWf, "Code Team Workflow");
      Conditions.checkNotNull(codeTask, "Code Team Workflow");

      services.getProgramService().setWorkPackage(wp, Arrays.asList(codeWf, codeTask), AtsCoreUsers.SYSTEM_USER);

      // by program
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andProgram(program.getId());
      assertEquals(2, query.getResults().size());

      query = queryService.createQuery(WorkItemType.Task);
      query.andProgram(program.getId());
      assertEquals(1, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andProgram(program.getId());
      assertEquals(3, query.getResults().size());

      // by insertion
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertion(insertion.getId());
      assertEquals(3, query.getResults().size());

      // by insertion activity
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertionActivity(activity.getId());
      assertEquals(2, query.getResults().size());

      // by work package
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andWorkPackage(wp.getId());
      assertEquals(2, query.getResults().size());
   }

   @Test
   public void testWorkPackage() {
      IAtsClient client = AtsClientService.get();

      IAtsWorkPackage workPackage =
         (IAtsWorkPackage) client.getQueryService().createQuery(AtsArtifactTypes.WorkPackage).andName(
            "Work Pkg 0A").getConfigObjectResultSet().getAtMostOneOrNull();
      Assert.assertNotNull(workPackage);
   }
}
