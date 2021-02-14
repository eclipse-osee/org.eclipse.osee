/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.access;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Collection;
import org.eclipse.osee.ats.api.access.AtsAccessContextTokens;
import org.eclipse.osee.ats.api.access.IAtsAccessService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.access.AtsAccessService;
import org.eclipse.osee.ats.core.access.demo.DemoAtsAccessContextTokens;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchAccessManagerTest {

   @Test
   public void testIsApplicable() {
      AtsAccessService mgr = new AtsAccessService(AtsApiService.get());
      Assert.assertFalse(mgr.isApplicable(AtsApiService.get().getAtsBranch()));
      Assert.assertFalse(mgr.isApplicable(SAW_Bld_1));

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertNotNull(teamArt);

      BranchId branch = AtsApiService.get().getBranchService().getWorkingBranch(teamArt);
      Assert.assertNotNull(branch);

      Assert.assertTrue(mgr.isApplicable(branch));
   }

        @Test
   public void testGetContextIds() throws Exception {
      IAtsAccessService accessService = AtsApiService.get().getAtsAccessService();
      TeamWorkFlowArtifact teamArt = DemoUtil.getButtonWDoesntWorkOnSituationPageWf();
      IAtsTeamDefinition teamDef = teamArt.getTeamDefinition();
      IAtsActionableItem ai = teamArt.getActionableItems().iterator().next();

      // confirm that deny context id is returned cause no branch
      Collection<AccessContextToken> contextIds = accessService.getContextIds(teamArt.getWorkingBranch(), false);
      Assert.assertEquals(1, contextIds.size());
      Assert.assertTrue(contextIds.contains(AtsAccessContextTokens.ATS_BRANCH_READ));

      // create branch
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - set target ver");
      IAtsVersion ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.CIS_Bld_1);
      AtsApiService.get().getVersionService().setTargetedVersion(teamArt, ver, changes);
      changes.execute();
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(teamArt, true);

      // confirm 0 contexts
      contextIds = accessService.getContextIds(teamArt.getWorkingBranch(), false);
      Assert.assertEquals(0, contextIds.size());

      // set 2 on team def
      AtsApiService.get().getAtsAccessService().setContextIds(teamDef, DemoAtsAccessContextTokens.DEMO_CODE_CONTEXT,
         DemoAtsAccessContextTokens.DEMO_TEST_CONTEXT);

      // 2 returned
      contextIds = accessService.getContextIds(teamArt.getWorkingBranch(), false);
      Assert.assertEquals(2, contextIds.size());
      Assert.assertTrue(contextIds.contains(DemoAtsAccessContextTokens.DEMO_CODE_CONTEXT));
      Assert.assertTrue(contextIds.contains(DemoAtsAccessContextTokens.DEMO_TEST_CONTEXT));

      // set 1 on ai
      AtsApiService.get().getAtsAccessService().setContextIds(ai, DemoAtsAccessContextTokens.DEMO_SYSTEMS_CONTEXT);

      // only 1 cause AI overrides Team Def
      contextIds = accessService.getContextIds(teamArt.getWorkingBranch(), false);
      Assert.assertEquals(1, contextIds.size());
      Assert.assertTrue(contextIds.contains(DemoAtsAccessContextTokens.DEMO_SYSTEMS_CONTEXT));

      // set 1 on workflow
      AtsApiService.get().getAtsAccessService().setContextIds(teamArt,
         DemoAtsAccessContextTokens.DEMO_REQUIREMENT_CONTEXT);

      // only 1 cause workflow overrides AI and Team Def
      contextIds = accessService.getContextIds(teamArt.getWorkingBranch(), false);
      Assert.assertEquals(1, contextIds.size());
      Assert.assertTrue(contextIds.contains(DemoAtsAccessContextTokens.DEMO_REQUIREMENT_CONTEXT));

      // cleanup
      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - cleanup");
      changes.deleteAttributes((ArtifactId) teamArt, CoreAttributeTypes.AccessContextId);
      changes.deleteAttributes(teamDef, CoreAttributeTypes.AccessContextId);
      changes.deleteAttributes(ai, CoreAttributeTypes.AccessContextId);
      changes.execute();

   }

}
