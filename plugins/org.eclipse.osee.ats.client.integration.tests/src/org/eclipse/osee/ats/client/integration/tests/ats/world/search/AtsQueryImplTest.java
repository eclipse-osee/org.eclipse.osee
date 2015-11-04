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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsQueryImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsQueryImplTest {

   private static IAtsClient client;
   private static IAtsUser joeSmith;
   private static IAtsQueryService queryService;
   private static Artifact wpArt;
   private static IAtsWorkPackage wp;

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();

      client = AtsClientService.get();
      joeSmith = client.getUserService().getUserByName("Joe Smith");
      queryService = client.getQueryService();

      wpArt = (Artifact) AtsClientService.get().getArtifactByName(AtsArtifactTypes.WorkPackage, "Work Pkg 01");
      Conditions.checkNotNull(wpArt, "Work Package");
      wp = client.getEarnedValueService().getWorkPackage(wpArt);

      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andAssignee(joeSmith);
      query.andAttr(AtsAttributeTypes.WorkPackageGuid, wpArt.getGuid());
      ResultSet<IAtsWorkItem> workItems = query.getResults();

      if (!workItems.isEmpty()) {
         client.getEarnedValueService().removeWorkPackage(wp, workItems.getList());
      }
   }

   @Test
   public void test() {

      // test by type
      IAtsQuery query = queryService.createQuery(WorkItemType.TeamWorkflow);
      assertEquals(25, query.getResults().size());
      query = queryService.createQuery(WorkItemType.Task);
      assertEquals(14, query.getResults().size());

      // assignee
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andAssignee(client.getUserService().getUserById("3333"));
      assertEquals(7, query.getResults().size());

      // team
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andTeam(Arrays.asList(30013695L));
      assertEquals(3, query.getResults().size());

      // ai
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      ArtifactId ai = AtsClientService.get().getArtifactByName(AtsArtifactTypes.ActionableItem, "SAW Requirements");
      query.andActionableItem(Arrays.asList(ai.getUuid()));
      assertEquals(4, query.getResults().size());

      // by uuids (hijack two workflows from previous search)
      List<Long> uuids = new LinkedList<>();
      for (IAtsWorkItem workItem : query.getResults()) {
         uuids.add(workItem.getUuid());
      }
      query = queryService.createQuery(WorkItemType.WorkItem);
      Iterator<Long> iterator = uuids.iterator();
      query.andUuids(iterator.next(), iterator.next());
      assertEquals(2, query.getResults().size());

      // by state name
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.isOfType(WorkItemType.PeerReview);
      query.andState("Prepare");
      assertEquals(4, query.getResults().size());

      // by state type
      query = queryService.createQuery(WorkItemType.WorkItem);
      query.andStateType(StateType.Working);
      assertEquals(48, query.getResults().size());

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
      ArtifactId version = AtsClientService.get().getArtifactByName(AtsArtifactTypes.Version, "SAW_Bld_2");
      query.andVersion(version.getUuid());
      for (IAtsWorkItem item : query.getResults()) {
         System.out.print(item.getUuid() + ",");
      }
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

      IAtsInsertionActivity activity = client.getConfigItemFactory().getInsertionActivity(
         wpArt.getRelatedArtifact(AtsRelationTypes.InsertionActivityToWorkPackage_InsertionActivity));
      IAtsInsertion insertion =
         client.getConfigItemFactory().getInsertion(((Artifact) activity.getStoreObject()).getRelatedArtifact(
            AtsRelationTypes.InsertionToInsertionActivity_Insertion));
      IAtsProgram program = client.getConfigItemFactory().getProgram(
         ((Artifact) insertion.getStoreObject()).getRelatedArtifact(AtsRelationTypes.ProgramToInsertion_Program));

      IAtsTeamWorkflow codeWf = null;
      IAtsTask codeTask = null;
      IAtsQuery query2 = queryService.createQuery(WorkItemType.WorkItem);
      query2.isOfType(WorkItemType.TeamWorkflow);
      query2.andAssignee(joeSmith);
      for (IAtsWorkItem workItem : query2.getResults()) {
         if (workItem.getArtifactTypeName().contains("Code")) {
            codeWf = (IAtsTeamWorkflow) workItem;
            for (IAtsTask task : client.getTaskService().getTasks(codeWf)) {
               codeTask = task;
               break;
            }
         }
         if (codeTask != null) {
            break;
         }
      }
      Conditions.checkNotNull(codeWf, "Code Team Workflow");
      Conditions.checkNotNull(codeTask, "Code Team Workflow");

      client.getEarnedValueService().setWorkPackage(wp, Arrays.asList(codeWf, codeTask));

      // by program
      query = queryService.createQuery(WorkItemType.TeamWorkflow);
      query.andProgram(program.getUuid());
      assertEquals(1, query.getResults().size());

      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andProgram(program.getUuid());
      assertEquals(2, query.getResults().size());

      // by insertion
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertion(insertion.getUuid());
      assertEquals(2, query.getResults().size());

      // by insertion activity
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andInsertionActivity(activity.getUuid());
      assertEquals(2, query.getResults().size());

      // by work package
      query = queryService.createQuery(WorkItemType.TeamWorkflow, WorkItemType.Task);
      query.andWorkPackage(wp.getUuid());
      assertEquals(2, query.getResults().size());
   }

   public static void assertEquals(int v1, int v2) {
      if (v1 != v2) {
         throw new OseeStateException("Expected %d, was %d", v1, v2);
      }
   }

}
