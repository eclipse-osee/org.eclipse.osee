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
package org.eclipse.osee.ats.rest.internal.test;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class VersionRelationToggleServerTest {

   private final AtsApi atsApi;
   private final XResultData rd;

   public VersionRelationToggleServerTest(AtsApi atsApi, XResultData rd) {
      this.atsApi = atsApi;
      this.rd = rd;
   }

   public XResultData run() {
      testSettingSameVersionDoesNotCreateTransaction();
      testToggledTargetVersionLoadsCorrectly();
      return rd;
   }

   public void testSettingSameVersionDoesNotCreateTransaction() {
      rd.log("Started VersionRelationToggleServerTest.testSettingSameVersionDoesNotCreateTransaction");

      AtsUser joe = atsApi.getUserService().getUserByToken(DemoUsers.Joe_Smith);

      AtsTestUtilCore.cleanupAndReset(getClass().getSimpleName(), false, joe);

      IAtsTeamWorkflow teamWf = AtsTestUtilCore.getTeamWf();
      IAtsVersion version1 = AtsTestUtilCore.getVerArt1();

      // Set to version1
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.executeIfNeeded();

      teamWf = reloadAttributesAndRelations(teamWf, atsApi, rd);
      testVersionCalls(atsApi, teamWf, version1, rd);

      // Change to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      TransactionToken tx = changes.executeIfNeeded();
      assertEquals(Boolean.TRUE, tx.isInvalid(), rd);

      rd.log("Completed");
   }

   public void testToggledTargetVersionLoadsCorrectly() {
      rd.log("Started VersionRelationToggleServerTest.testToggledTargetVersionLoadsCorrectly");

      AtsUser joe = atsApi.getUserService().getUserByToken(DemoUsers.Joe_Smith);

      AtsTestUtilCore.cleanupAndReset(getClass().getSimpleName(), false, joe);

      IAtsTeamWorkflow teamWf = AtsTestUtilCore.getTeamWf();
      IAtsVersion version1 = AtsTestUtilCore.getVerArt1();
      IAtsVersion version2 = AtsTestUtilCore.getVerArt2();

      // Set to version1
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.execute();
      teamWf = reloadAttributesAndRelations(teamWf, atsApi, rd);
      testVersionCalls(atsApi, teamWf, version1, rd);

      // Change to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version2, changes);
      changes.execute();
      teamWf = reloadAttributesAndRelations(teamWf, atsApi, rd);
      testVersionCalls(atsApi, teamWf, version2, rd);

      // Switch back to version1
      changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version1, changes);
      changes.execute();
      teamWf = reloadAttributesAndRelations(teamWf, atsApi, rd);
      testVersionCalls(atsApi, teamWf, version1, rd);

      // Switch back to version2
      changes = atsApi.createChangeSet(getClass().getSimpleName(), joe);
      atsApi.getVersionService().setTargetedVersion(teamWf, version2, changes);
      changes.execute();
      teamWf = reloadAttributesAndRelations(teamWf, atsApi, rd);
      testVersionCalls(atsApi, teamWf, version2, rd);

      rd.log("Completed");
   }

   private IAtsTeamWorkflow reloadAttributesAndRelations(IAtsTeamWorkflow teamWf, AtsApi atsApi, XResultData rd) {
      return atsApi.getWorkItemService().getTeamWf(atsApi.getQueryService().getArtifact(teamWf.getId()));
   }

   private void testVersionCalls(AtsApi atsApi, IAtsTeamWorkflow teamWf, IAtsVersion version, XResultData rd) {

      // This works
      Collection<ArtifactToken> versions =
         atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      assertEquals(versions.size(), 1, rd);
      if (!versions.isEmpty()) {
         assertEquals(version, versions.iterator().next(), rd);
      }

      versions = atsApi.getRelationResolver().getRelated(teamWf.getStoreObject(),
         AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      assertEquals(versions.size(), 1, rd);
      if (!versions.isEmpty()) {
         assertEquals(version, versions.iterator().next(), rd);
      }

   }

   private void assertEquals(Object obj1, Object obj2, XResultData rd) {
      if (!obj1.equals(obj2)) {
         rd.errorf("Not Equal [%s] [%s]", obj1, obj2);
      }
   }

}
