/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationResolverServiceTest {

   private static TeamWorkFlowArtifact sawCodeCommittedWf;
   private static Artifact topAi;
   private static AtsApi atsApi;
   private static IRelationResolver relationResolver;
   private static TeamWorkFlowArtifact sawCodeUnCommittedWf;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      atsApi = AtsApiService.get();
      sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      sawCodeUnCommittedWf = DemoUtil.getSawCodeUnCommittedWf();
      topAi = AtsApiService.get().getQueryServiceIde().getArtifact(AtsArtifactToken.TopActionableItem);
      relationResolver = atsApi.getRelationResolver();
   }

   @Test
   public void testGetRelatedArtifactIdRelationTypeSide() {
      Assert.assertEquals(7, relationResolver.getRelated(topAi, CoreRelationTypes.DefaultHierarchical_Child).size());
   }

   @Test
   public void testGetRelatedIAtsObjectRelationTypeSideClassOfT() {
      Assert.assertEquals(8, relationResolver.getRelatedArtifacts((ArtifactId) sawCodeCommittedWf,
         AtsRelationTypes.TeamWfToTask_Task).size());
   }

   @Test
   public void testAreRelatedArtifactIdRelationTypeSideArtifactId() {
      Artifact sawCsciAi = AtsApiService.get().getQueryServiceIde().getArtifact(DemoArtifactToken.SAW_CSCI_AI);
      Assert.assertTrue(relationResolver.areRelated(topAi, CoreRelationTypes.DefaultHierarchical_Child, sawCsciAi));
      Assert.assertTrue(relationResolver.areRelated(sawCsciAi, CoreRelationTypes.DefaultHierarchical_Parent, topAi));

      Artifact sawTestAi = AtsApiService.get().getQueryServiceIde().getArtifact(DemoArtifactToken.SAW_Test_AI);
      Assert.assertFalse(relationResolver.areRelated(topAi, CoreRelationTypes.DefaultHierarchical_Child, sawTestAi));
      Assert.assertFalse(relationResolver.areRelated(sawTestAi, CoreRelationTypes.DefaultHierarchical_Parent, topAi));
   }

   @Test
   public void testAreRelatedIAtsObjectRelationTypeSideIAtsObject() {
      Collection<ArtifactToken> related =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactId firstTask = related.iterator().next();

      Assert.assertTrue(relationResolver.areRelated(sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task, firstTask));
      Assert.assertTrue(
         relationResolver.areRelated(firstTask, AtsRelationTypes.TeamWfToTask_TeamWorkflow, sawCodeCommittedWf));

      // get task from un-related workflow
      Collection<ArtifactToken> unRelated =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeUnCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactId firstUnRelatedTask = unRelated.iterator().next();

      Assert.assertFalse(
         relationResolver.areRelated(sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task, firstUnRelatedTask));
      Assert.assertFalse(
         relationResolver.areRelated(firstUnRelatedTask, AtsRelationTypes.TeamWfToTask_TeamWorkflow, sawCodeCommittedWf));
   }

   @Test
   public void testGetRelatedOrNullArtifactIdRelationTypeSide() {
      ArtifactId sawTestAi = atsApi.getQueryService().getArtifact(DemoArtifactToken.SAW_Test_AI);
      ArtifactId relatedOrNull =
         relationResolver.getRelatedOrNull(sawTestAi, CoreRelationTypes.DefaultHierarchical_Parent);
      Assert.assertNotNull(relatedOrNull);

      ArtifactId nullParentId = relationResolver.getRelatedOrNull(
         AtsApiService.get().getQueryServiceIde().getArtifact(sawCodeCommittedWf),
         CoreRelationTypes.DefaultHierarchical_Parent);
      Assert.assertNull(nullParentId);
   }

   @Test
   public void testGetRelatedOrNullIAtsObjectRelationTypeSideClassOfT() {
      Collection<ArtifactToken> related =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactToken firstTaskArt = related.iterator().next();
      IAtsTask firstTask = atsApi.getWorkItemService().getTask(firstTaskArt);

      IAtsTeamWorkflow teamWf =
         relationResolver.getRelatedOrNull(firstTask, AtsRelationTypes.TeamWfToTask_TeamWorkflow, IAtsTeamWorkflow.class);
      Assert.assertNotNull(teamWf);

      IAtsTeamWorkflow nullChild = relationResolver.getRelatedOrNull(firstTask,
         CoreRelationTypes.DefaultHierarchical_Child, IAtsTeamWorkflow.class);
      Assert.assertNull(nullChild);
   }

}
