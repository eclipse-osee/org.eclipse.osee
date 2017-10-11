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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
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
      atsApi = AtsClientService.get();
      sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      sawCodeUnCommittedWf = DemoUtil.getSawCodeUnCommittedWf();
      topAi = (Artifact) atsApi.getArtifact(AtsArtifactToken.TopActionableItem);
      relationResolver = atsApi.getRelationResolver();
   }

   @Test
   public void testGetRelatedArtifactIdRelationTypeSide() {
      Assert.assertEquals(6, relationResolver.getRelated(topAi, CoreRelationTypes.Default_Hierarchical__Child).size());
   }

   @Test
   public void testGetRelatedIAtsObjectRelationTypeSideClassOfT() {
      Assert.assertEquals(8, relationResolver.getRelatedArtifacts((ArtifactId) sawCodeCommittedWf,
         AtsRelationTypes.TeamWfToTask_Task).size());
   }

   @Test
   public void testAreRelatedArtifactIdRelationTypeSideArtifactId() {
      Artifact sawCsciAi = (Artifact) atsApi.getArtifact(DemoArtifactToken.SAW_CSCI_AI);
      Assert.assertTrue(relationResolver.areRelated(topAi, CoreRelationTypes.Default_Hierarchical__Child, sawCsciAi));
      Assert.assertTrue(relationResolver.areRelated(sawCsciAi, CoreRelationTypes.Default_Hierarchical__Parent, topAi));

      Artifact sawTestAi = (Artifact) atsApi.getArtifact(DemoArtifactToken.SAW_Test_AI);
      Assert.assertFalse(relationResolver.areRelated(topAi, CoreRelationTypes.Default_Hierarchical__Child, sawTestAi));
      Assert.assertFalse(relationResolver.areRelated(sawTestAi, CoreRelationTypes.Default_Hierarchical__Parent, topAi));
   }

   @Test
   public void testAreRelatedIAtsObjectRelationTypeSideIAtsObject() {
      Collection<ArtifactToken> related =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactId firstTask = related.iterator().next();

      Assert.assertTrue(relationResolver.areRelated(sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task, firstTask));
      Assert.assertTrue(
         relationResolver.areRelated(firstTask, AtsRelationTypes.TeamWfToTask_TeamWf, sawCodeCommittedWf));

      // get task from un-related workflow
      Collection<ArtifactToken> unRelated =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeUnCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactId firstUnRelatedTask = unRelated.iterator().next();

      Assert.assertFalse(
         relationResolver.areRelated(sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task, firstUnRelatedTask));
      Assert.assertFalse(
         relationResolver.areRelated(firstUnRelatedTask, AtsRelationTypes.TeamWfToTask_TeamWf, sawCodeCommittedWf));
   }

   @Test
   public void testGetRelatedOrNullArtifactIdRelationTypeSide() {
      ArtifactId sawTestAi = atsApi.getArtifact(DemoArtifactToken.SAW_Test_AI);
      ArtifactId relatedOrNull =
         relationResolver.getRelatedOrNull(sawTestAi, CoreRelationTypes.Default_Hierarchical__Parent);
      Assert.assertNotNull(relatedOrNull);

      ArtifactId nullParentId = relationResolver.getRelatedOrNull((Artifact) sawCodeCommittedWf,
         CoreRelationTypes.Default_Hierarchical__Parent);
      Assert.assertNull(nullParentId);
   }

   @Test
   public void testGetRelatedOrNullIAtsObjectRelationTypeSideClassOfT() {
      Collection<ArtifactToken> related =
         relationResolver.getRelatedArtifacts((IAtsWorkItem) sawCodeCommittedWf, AtsRelationTypes.TeamWfToTask_Task);
      ArtifactToken firstTaskArt = related.iterator().next();
      IAtsTask firstTask = atsApi.getWorkItemFactory().getTask(firstTaskArt);

      IAtsTeamWorkflow teamWf =
         relationResolver.getRelatedOrNull(firstTask, AtsRelationTypes.TeamWfToTask_TeamWf, IAtsTeamWorkflow.class);
      Assert.assertNotNull(teamWf);

      IAtsTeamWorkflow nullChild = relationResolver.getRelatedOrNull(firstTask,
         CoreRelationTypes.Default_Hierarchical__Child, IAtsTeamWorkflow.class);
      Assert.assertNull(nullChild);
   }

}
