/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link AtsTeamWfEndpoint}
 *
 * @author Stephen Molaro
 * @author Kenn Luecke
 */
public class AtsTeamWfEndpointTest {

   private AtsTeamWfEndpointApi teamWfEp;
   private AtsApiIde atsApi;
   private TeamWorkFlowArtifact codeTeamWorkFlow;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      teamWfEp = AtsApiService.get().getServerEndpoints().getTeamWfEp();
      codeTeamWorkFlow = DemoUtil.getSawCodeUnCommittedWf();
   }

   @Test
   public void testGetWfByRelease() {
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         getClass().getSimpleName() + " - Create Release Artifact", AtsCoreUsers.SYSTEM_USER);
      ArtifactToken release = changes.createArtifact(AtsArtifactTypes.ReleaseArtifact, "G123456.0");
      changes.relate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.execute();

      Collection<ArtifactToken> workflows = teamWfEp.getWfByRelease("G123456.0");
      Assert.assertTrue(workflows.contains(codeTeamWorkFlow));

      changes = atsApi.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - Cleanup Releases",
         AtsCoreUsers.SYSTEM_USER);
      changes.unrelate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.deleteArtifact(release);
      changes.execute();
   }

   @Test
   public void testRelateReleaseToWorkflow() {
      String changeId = "IO98293838";
      List<String> changeIds = new ArrayList<>();
      changeIds.add(changeId);

      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         getClass().getSimpleName() + " - Create Release artifact/Set GitChangeId", AtsCoreUsers.SYSTEM_USER);
      ArtifactToken release = changes.createArtifact(AtsArtifactTypes.ReleaseArtifact, "G123456.8");
      changes.addAttribute(codeTeamWorkFlow.getArtifactId(), CoreAttributeTypes.GitChangeId, changeId);
      changes.execute();

      XResultData rd = teamWfEp.relateReleaseToWorkflow(release.getName(), DemoUsers.Joe_Smith, changeIds);
      TransactionId txId = TransactionId.SENTINEL;
      if (Strings.isNumeric(rd.getTxId())) {
         txId = TransactionId.valueOf(rd.getTxId());
      }
      Collection<IAtsWorkItem> workItems = new ArrayList<>();
      workItems.add(codeTeamWorkFlow);

      atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, workItems, txId);
      atsApi.getStoreService().reload(workItems);

      boolean isRelease = atsApi.getRelationResolver().areRelated(codeTeamWorkFlow.getArtifactId(),
         AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      Assert.assertTrue(isRelease);

      // delete change that was just added
      changes = atsApi.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - Cleanup Releases",
         AtsCoreUsers.SYSTEM_USER);
      changes.unrelate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.deleteArtifact(release);
      changes.deleteAttributes(codeTeamWorkFlow.getArtifactId(), CoreAttributeTypes.GitChangeId);
      changes.execute();

   }

}