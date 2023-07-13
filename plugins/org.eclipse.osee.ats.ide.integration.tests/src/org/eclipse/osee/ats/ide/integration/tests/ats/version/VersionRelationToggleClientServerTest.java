/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.version;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class VersionRelationToggleClientServerTest {

   @Test
   public void testServerVersionRelationToggleTest() {
      AtsApiIde atsApi = AtsApiService.get();
      XResultData rd = atsApi.getServerEndpoints().getTestEp().testVersions();
      Assert.assertTrue(rd.toString(), rd.isSuccess());
   }

   @Test
   public void testSettingSameVersionDoesNotCreateTransaction() {

      AtsApiIde atsApi = AtsApiService.get();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsVersion version1 = AtsTestUtil.getVerArt1();

      // Set to version1
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.execute();
      teamWf.reloadAttributesAndRelations();
      testVersionCalls(atsApi, teamWf, version1);

      // Change to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      TransactionToken tx = changes.executeIfNeeded();
      Assert.assertTrue(tx.isInvalid());

   }

   @Test
   public void testToggledTargetVersionLoadsCorrectly() {

      AtsApiIde atsApi = AtsApiService.get();

      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsVersion version1 = AtsTestUtil.getVerArt1();
      IAtsVersion version2 = AtsTestUtil.getVerArt2();

      // Set to version1
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.execute();
      teamWf.reloadAttributesAndRelations();
      testVersionCalls(atsApi, teamWf, version1);

      // Change to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version2, changes);
      changes.execute();
      teamWf.reloadAttributesAndRelations();
      testVersionCalls(atsApi, teamWf, version2);

      // Switch back to version1
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.execute();
      teamWf.reloadAttributesAndRelations();
      testVersionCalls(atsApi, teamWf, version1);

      // Switch back to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      atsApi.getVersionService().setTargetedVersion(teamWf, version2, changes);
      changes.execute();
      teamWf.reloadAttributesAndRelations();
      testVersionCalls(atsApi, teamWf, version2);

   }

   private void testVersionCalls(AtsApiIde atsApi, TeamWorkFlowArtifact teamWf, IAtsVersion version) {

      // This works
      Collection<ArtifactToken> versions = atsApi.getRelationResolver().getRelated((IAtsObject) teamWf,
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      Assert.assertEquals(versions.size(), 1);
      Assert.assertEquals(version, versions.iterator().next());

      versions = atsApi.getRelationResolver().getRelated(teamWf.getStoreObject(),
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      Assert.assertEquals(versions.size(), 1);
      Assert.assertEquals(version, versions.iterator().next());

   }

}
