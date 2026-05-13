/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo.servertest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadableImpl;
import org.eclipse.osee.orcs.search.ds.FollowAllCriteria;

/**
 * Test server QueryBuilder against demo populated data
 *
 * @author Donald G. Dunne
 */
public class AtsDbTestQueryOp {

   private static final String ATSID_TW7 = "TW7";
   private final AtsApi atsApi;
   private final XResultData rd;
   private final OrcsApi orcsApi;

   public AtsDbTestQueryOp(XResultData rd, AtsApi atsApi, OrcsApi orcsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {
      rd.setLogToSysErr(true);

      rd.log(getClass().getSimpleName() + " - Start");

      testAttrValueExactMatch();
      testAtsGetArtifactNew();
      testOrcsAsArtifactsDefaultLoading();
      testOrcsAsArtifactsFollowsLoading();
      testAsArtifactFollowAllOneLevel();
      testAsArtifactFollowAllTwoLevel();
      testAtsGetArtifactNewGetSiblings();
      testAtsGetArtifactNewFollowAllGetSiblings();

      rd.log(getClass().getSimpleName() + " - End");
      return rd;
   }

   private void testAtsGetArtifactNewGetSiblings() {
      rd.log("testAtsGetArtifactNewGetSiblings");

      ArtifactReadableImpl sawCodeArt =
         (ArtifactReadableImpl) atsApi.getQueryService().getArtifactNew(DemoArtifactToken.SAW_UnCommited_Code_TeamWf);
      rd.assertNotNull(sawCodeArt);
      rd.assertEquals(sawCodeArt.getAttributeCount(CoreAttributeTypes.Name), 1);

      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(sawCodeArt);

      Collection<IAtsTeamWorkflow> siblings = atsApi.getWorkItemService().getSiblings(teamWf);

      rd.assertEquals(3, siblings.size());
   }

   private void testAsArtifactFollowAllOneLevel() {
      rd.log("testAsArtifactFollowAllOne");

      // Using asArtifacts and ArtifactReadableImpl API
      ArtifactReadableImpl sawCodeArt =
         (ArtifactReadableImpl) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            DemoArtifactToken.SAW_UnCommited_Code_TeamWf).followAll(FollowAllCriteria.OneLevel).asArtifact();

      rd.assertNotNull(sawCodeArt);
      rd.assertEquals(sawCodeArt.getAttributeCount(CoreAttributeTypes.Name), 1);

      // followAll(OneLevel - "true") loads all related artifacts and their attrs, but not their relations
      List<ArtifactReadable> actionArts = sawCodeArt.getRelatedList(AtsRelationTypes.ActionToWorkflow_Action);
      rd.assertEquals(1, actionArts.size());
      ArtifactReadable action = actionArts.iterator().next();
      // Attributes are loaded
      rd.assertFalse(action.getAttributesNew().isEmpty());
      // Relations are NOT loaded if followAll(OneLevel), so loaded flag should be false
      List<ArtifactReadable> teamWfs = action.getRelatedList(AtsRelationTypes.ActionToWorkflow_TeamWorkflow);
      rd.assertEquals(0, teamWfs.size());
      // SO, loaded flag is false
      rd.assertFalse(action.isLoaded());
   }

   private void testAsArtifactFollowAllTwoLevel() {
      rd.log("testAsArtifactFollowAllTwoLevel");

      // Using asArtifacts and ArtifactReadableImpl API
      ArtifactReadableImpl sawCodeArt =
         (ArtifactReadableImpl) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            DemoArtifactToken.SAW_UnCommited_Code_TeamWf).followAll(FollowAllCriteria.TwoLevels).asArtifact();

      rd.assertNotNull(sawCodeArt);
      rd.assertEquals(sawCodeArt.getAttributeCount(CoreAttributeTypes.Name), 1);

      List<ArtifactReadable> actionArts = sawCodeArt.getRelatedList(AtsRelationTypes.ActionToWorkflow_Action);
      rd.assertEquals(1, actionArts.size());
      ArtifactReadable action = actionArts.iterator().next();
      rd.assertFalse(action.getAttributesNew().isEmpty());
      // Relations ART NOW loaded if followAll(TwoLevels), so loaded flag should be false
      List<ArtifactReadable> teamWfs = action.getRelatedList(AtsRelationTypes.ActionToWorkflow_TeamWorkflow);
      int size = teamWfs.size();
      rd.assertEquals(4, size);
      // SO, loaded flag is NOW true since both rels and attrs loaded
      rd.assertTrue(action.isLoaded());
   }

   private void testAtsGetArtifactNewFollowAllGetSiblings() {
      rd.log("testAtsGetArtifactNewFollowAllGetSiblings");

      // Using asArtifacts and ArtifactReadableImpl API
      ArtifactReadableImpl sawCodeArt =
         (ArtifactReadableImpl) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            DemoArtifactToken.SAW_UnCommited_Code_TeamWf).followAll(FollowAllCriteria.OneLevel).asArtifact();

      // Using ATS API
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(sawCodeArt);
      Collection<IAtsTeamWorkflow> siblings = atsApi.getWorkItemService().getSiblings(teamWf);
      rd.assertEquals(3, siblings.size());

   }

   private void testAtsGetArtifactNew() {
      rd.log("testAtsGetArtifactNew");

      // Artifact, attrs and rels with artTokens on other side should be loaded
      ArtifactReadableImpl userArt =
         (ArtifactReadableImpl) atsApi.getQueryService().getArtifactNew(DemoUsers.Joe_Smith);
      rd.assertNotNull(userArt);
      rd.assertEquals(userArt.getAttributeCount(CoreAttributeTypes.Name), 1);

      // ArtifactTokens on other side of relation
      List<ArtifactReadable> userGroups = userArt.getRelatedList(CoreRelationTypes.Users_Artifact);
      rd.assertFalse(userGroups.isEmpty());

      // Because attrs and rels are loaded, loaded should be true
      rd.assertTrue(userArt.isLoaded());

      // ArtifactTokens have no attributes or relations loaded
      ArtifactReadable userGroup = userGroups.iterator().next();
      rd.assertNotNull(userGroup);
      rd.assertTrue(userGroup.getAttributesNew().isEmpty());
      rd.assertTrue(((ArtifactReadableImpl) userGroup).getRelatedCount(CoreRelationTypes.Users_User) == 0);
      // Because attrs and rels are NOT loaded, loaded should be false
      rd.assertFalse(userGroup.isLoaded());
      rd.assertTrue(userGroup.isNotLoaded());

   }

   private void testOrcsAsArtifactsDefaultLoading() {
      rd.log("testOrcsAsArtifactsDefaultLoading");

      // Artifact, attrs and rels with artTokens on other side should be loaded
      ArtifactReadableImpl userArt =
         (ArtifactReadableImpl) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            DemoUsers.Joe_Smith).asArtifact();
      rd.assertNotNull(userArt);
      rd.assertEquals(userArt.getAttributeCount(CoreAttributeTypes.Name), 1);

      // ArtifactTokens on other side of relation
      List<ArtifactReadable> userGroups = userArt.getRelatedList(CoreRelationTypes.Users_Artifact);
      rd.assertFalse(userGroups.isEmpty());

      // Because attrs and rels are loaded, loaded should be true
      rd.assertTrue(userArt.isLoaded());

      // ArtifactTokens have no attributes or relations loaded
      ArtifactReadable userGroup = userGroups.iterator().next();
      rd.assertNotNull(userGroup);
      rd.assertTrue(userGroup.getAttributesNew().isEmpty());
      rd.assertTrue(((ArtifactReadableImpl) userGroup).getRelatedCount(CoreRelationTypes.Users_User) == 0);
      // Because attrs and rels are NOT loaded, loaded should be false
      rd.assertFalse(userGroup.isLoaded());
      rd.assertTrue(userGroup.isNotLoaded());

   }

   private void testOrcsAsArtifactsFollowsLoading() {
      rd.log("testOrcsAsArtifactsFollowsLoading");

      // Artifact, attrs and rels with artTokens on other side should be loaded
      ArtifactReadableImpl userArt =
         (ArtifactReadableImpl) orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            DemoUsers.Joe_Smith).follow(AtsRelationTypes.TeamLead_Team).asArtifact();
      rd.assertNotNull(userArt);
      rd.assertEquals(userArt.getAttributeCount(CoreAttributeTypes.Name), 1);
      rd.assertEquals("testOrcsAsArtifactsFollowsLoading", userArt.getName(), DemoUsers.Joe_Smith.getName());
      // And is loaded == true
      rd.assertTrue(userArt.isLoaded());

      // Teams on follows relation DO have attributes AND rels loaded
      List<ArtifactReadable> teamDefArts = userArt.getRelatedList(AtsRelationTypes.TeamLead_Team);
      rd.assertFalse(teamDefArts.isEmpty());
      ArtifactReadable teamDefArt = teamDefArts.iterator().next(); // Note: All should be fully loaded, so doesn't matter which we get
      rd.assertNotNull(teamDefArt);
      rd.assertFalse(teamDefArt.getAttributesNew().isEmpty());
      // but not relations (Note: all should have rels loaded and all should have parent, so doesn't matter which we got)
      rd.assertTrue(
         ((ArtifactReadableImpl) teamDefArt).getRelatedCount(CoreRelationTypes.DefaultHierarchical_Parent) > 0);
      // And is loaded == true
      rd.assertTrue(teamDefArt.isLoaded());

      // Other relations DO NOT have attrs OR rels loaded
      List<ArtifactReadable> userGroups = userArt.getRelatedList(CoreRelationTypes.Users_Artifact);
      rd.assertFalse(userGroups.isEmpty());
      ArtifactReadable userGroup = userGroups.iterator().next();
      rd.assertNotNull(userGroup);
      rd.assertTrue(userGroup.getAttributesNew().isEmpty());
      rd.assertTrue(((ArtifactReadableImpl) userGroup).getRelatedCount(CoreRelationTypes.Users_User) == 0);
      // And is loaded == false
      rd.assertFalse(userGroup.isLoaded());

   }

   private void testAttrValueExactMatch() {
      rd.log("testAttrValueExactMatch");

      List<ArtifactReadable> asArtifacts =
         orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).and(AtsAttributeTypes.AtsId,
            Arrays.asList(ATSID_TW7), QueryOption.EXACT_MATCH_OPTIONS).asArtifacts();
      rd.assertEquals("testAttrValueExactMatch", 1, asArtifacts.size());
   }

}
