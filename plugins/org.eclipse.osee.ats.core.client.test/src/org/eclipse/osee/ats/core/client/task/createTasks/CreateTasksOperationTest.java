/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task.createTasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.task.createtasks.CreateTasksOperation;
import org.eclipse.osee.ats.core.client.task.createtasks.GenerateTaskOpList;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskEnum;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskMetadata;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.IChangeWorker;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.junit.Assert;

/**
 * Test unit for {@link CreateTasksOperation}
 *
 * @author Shawn F. Cook
 */
public class CreateTasksOperationTest {

   @org.junit.Test
   public void testGenerateTaskOpList() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset("CreateTasksOperationTest - testGenerateTaskOpList");
      TeamWorkFlowArtifact destTeamWf = AtsTestUtil.getTeamWf();
      ChangeData changeData = getMockChangeData(destTeamWf);

      GenerateTaskOpList genTaskOpList = new GenerateTaskOpList();
      List<TaskMetadata> metadatas = genTaskOpList.generate(changeData, destTeamWf);

      Assert.assertEquals(changeData.getChanges().size(), metadatas.size());

      for (TaskMetadata metadata : metadatas) {
         Assert.assertEquals(metadata.getTaskEnum(), TaskEnum.CREATE);
      }

      //      TaskArtifact taskToMove = AtsTestUtil.getOrCreateTaskOffTeamWf1();
      //      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      //
      //      WorkDefinition taskWorkDef = WorkDefinitionFactory.getWorkDefinitionForTask(taskToMove).getWorkDefinition();
      //      WorkDefinition newTaskWorkDef =
      //         WorkDefinitionFactory.getWorkDefinitionForTaskNotYetCreated(teamWf2).getWorkDefinition();
      //      Assert.assertNotNull(taskWorkDef);
      //
      //      Assert.assertEquals(taskWorkDef, newTaskWorkDef);
      //      Result result = TaskManager.moveTasks(teamWf2, Arrays.asList(taskToMove));
      //
      //      Assert.assertTrue("This failed: " + result.getText(), result.isTrue());
   }

   private ChangeData getMockChangeData(TeamWorkFlowArtifact destTeamWf) throws OseeCoreException {
      //Create MockChange objects
      Artifact changeArt01 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt01");
      Artifact changeArt02 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt02");
      Artifact changeArt03 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt03");
      Artifact changeArt04 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt04");
      Artifact changeArt05 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt05");
      Artifact changeArt06 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt06");
      Artifact changeArt07 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt07");
      Artifact changeArt08 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt08");
      Artifact changeArt09 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt09");
      Artifact changeArt10 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, AtsUtilCore.getAtsBranch(), "changeArt10");
      Change mockChange01 = new MockChange(changeArt01);
      Change mockChange02 = new MockChange(changeArt02);
      Change mockChange03 = new MockChange(changeArt03);
      Change mockChange04 = new MockChange(changeArt04);
      Change mockChange05 = new MockChange(changeArt05);
      Change mockChange06 = new MockChange(changeArt06);
      Change mockChange07 = new MockChange(changeArt07);
      Change mockChange08 = new MockChange(changeArt08);
      Change mockChange09 = new MockChange(changeArt09);
      Change mockChange10 = new MockChange(changeArt10);

      destTeamWf.createNewTask("", null, null);

      Collection<Change> changes = new ArrayList<Change>(Collections.getAggregate(mockChange01));

      ChangeData changeData = new ChangeData(changes);

      return changeData;
   }
   private class MockChange extends Change {

      public MockChange(Artifact changeArtifact) {
         super(null, 0, 0, null, null, false, changeArtifact, null);
      }

      @Override
      public int getItemTypeId() {
         return 0;
      }

      @Override
      public String getIsValue() {
         return null;
      }

      @Override
      public String getWasValue() {
         return null;
      }

      @Override
      public String getItemTypeName() throws OseeCoreException {
         return null;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public String getItemKind() {
         return null;
      }

      @Override
      public int getItemId() {
         return 0;
      }

      @Override
      public LoadChangeType getChangeType() {
         return null;
      }

      @Override
      public Class<? extends IChangeWorker> getWorker() {
         return null;
      }
   }
}
