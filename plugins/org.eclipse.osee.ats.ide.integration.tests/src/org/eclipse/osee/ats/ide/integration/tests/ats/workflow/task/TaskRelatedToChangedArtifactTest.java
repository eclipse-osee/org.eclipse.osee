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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Unit for {@link ArtifactReferenceAttribute}
 *
 * @author Donald G. Dunne
 */
public class TaskRelatedToChangedArtifactTest {

   @Before
   @After
   public void cleanup() {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testCreateSawTestWf() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getName());

      TeamWorkFlowArtifact codeWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeCommittedWf();
      assertNotNull(codeWf);
      IAtsTask codeTask = null;
      for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(codeWf)) {
         if (task.getName().equals("Make changes")) {
            codeTask = task;
            break;
         }
      }
      assertNotNull(codeTask);
      TeamWorkFlowArtifact reqWf = null;
      for (IAtsTeamWorkflow wf : AtsApiService.get().getActionService().getSiblingTeamWorkflows(codeWf)) {
         if (wf.getTeamDefinition().getName().contains("Requirements")) {
            reqWf = (TeamWorkFlowArtifact) wf.getStoreObject();
            break;
         }
      }
      assertNotNull(reqWf);
      IAtsChangeSet changes =
         AtsApiService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName(), AtsCoreUsers.SYSTEM_USER);
      changes.relate(reqWf, AtsRelationTypes.Derive_To, codeWf);

      Artifact robotReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord,
         "Robot API", DemoBranches.SAW_Bld_2);

      changes.setSoleAttributeValue(codeTask, AtsAttributeTypes.TaskToChangedArtifactReference, robotReq);
      changes.execute();

      assertFalse(AtsApiService.get().getQueryServiceIde().getArtifact(codeTask).isDirty());

      ArtifactId refArt = ((Artifact) codeTask.getStoreObject()).getSoleAttributeValue(
         AtsAttributeTypes.TaskToChangedArtifactReference, ArtifactId.SENTINEL);
      assertEquals(robotReq, refArt);
   }
}
