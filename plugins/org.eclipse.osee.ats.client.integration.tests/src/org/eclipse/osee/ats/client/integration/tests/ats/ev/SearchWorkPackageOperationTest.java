/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.demo.DemoArtifactToken;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.ev.SearchWorkPackageOperation;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test case for {@link SearchWorkPackageOperation}
 * 
 * @author Donald G. Dunne
 */
@RunWith(Parameterized.class)
public class SearchWorkPackageOperationTest {

   private final String resultSize;
   private final Collection<String> teamDefGuids;
   private final boolean includeChildrenTeamDefs;
   private final Collection<String> aiGuids;
   private final Active activeOption;
   private final Collection<String> expectedWpGuids;
   private final boolean includeChildrenAIs;

   public SearchWorkPackageOperationTest(String resultSize, Collection<String> teamDefGuids, boolean includeChildrenTeamDefs, Collection<String> aiGuids, boolean includeChildrenAIs, Active activeOption, Collection<String> expectedWpGuids) {
      this.resultSize = resultSize;
      this.teamDefGuids = teamDefGuids;
      this.includeChildrenTeamDefs = includeChildrenTeamDefs;
      this.aiGuids = aiGuids;
      this.includeChildrenAIs = includeChildrenAIs;
      this.activeOption = activeOption;
      this.expectedWpGuids = expectedWpGuids;
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      List<String> EMPYT_RESULTS = new ArrayList<String>();
      String WP_01 = DemoArtifactToken.SAW_Code_Team_WorkPackage_01.getGuid();
      String WP_02 = DemoArtifactToken.SAW_Code_Team_WorkPackage_02.getGuid();
      String WP_03 = DemoArtifactToken.SAW_Code_Team_WorkPackage_03.getGuid();
      String WP_0A = DemoArtifactToken.SAW_Test_AI_WorkPackage_0A.getGuid();
      String WP_0B = DemoArtifactToken.SAW_Test_AI_WorkPackage_0B.getGuid();
      String WP_0C = DemoArtifactToken.SAW_Test_AI_WorkPackage_0C.getGuid();

      // Test Work Packages configured by Team Def
      addTest(data, "no work packages should be returned", Arrays.asList(DemoArtifactToken.SAW_SW.getGuid()), false,
         new ArrayList<String>(), false, Active.Both, EMPYT_RESULTS);
      addTest(data, "3 work packages should be returned", Arrays.asList(DemoArtifactToken.SAW_SW.getGuid()), true,
         new ArrayList<String>(), false, Active.Both, Arrays.asList(WP_01, WP_02, WP_03));
      addTest(data, "2 active work packages should be returned", Arrays.asList(DemoArtifactToken.SAW_SW.getGuid()),
         true, new ArrayList<String>(), false, Active.Active, Arrays.asList(WP_01, WP_02));
      addTest(data, "1 in-active work packages should be returned", Arrays.asList(DemoArtifactToken.SAW_SW.getGuid()),
         true, new ArrayList<String>(), false, Active.InActive, Arrays.asList(WP_03));

      // Test Work Packages configured by AI
      addTest(data, "no work packages should be returned", new ArrayList<String>(), false,
         Arrays.asList(DemoArtifactToken.SAW_SW_AI.getGuid()), false, Active.Both, EMPYT_RESULTS);
      addTest(data, "3 work packages should be returned", new ArrayList<String>(), false,
         Arrays.asList(DemoArtifactToken.SAW_SW_AI.getGuid()), true, Active.Both, Arrays.asList(WP_0A, WP_0B, WP_0C));
      addTest(data, "2 active work packages should be returned", new ArrayList<String>(), false,
         Arrays.asList(DemoArtifactToken.SAW_SW_AI.getGuid()), true, Active.Active, Arrays.asList(WP_0A, WP_0B));
      addTest(data, "1 in-active work packages should be returned", new ArrayList<String>(), false,
         Arrays.asList(DemoArtifactToken.SAW_SW_AI.getGuid()), true, Active.InActive, Arrays.asList(WP_0C));

      // Test configured by both
      addTest(data, "4 active work packages should be returned", Arrays.asList(DemoArtifactToken.SAW_SW.getGuid()),
         true, Arrays.asList(DemoArtifactToken.SAW_Test_AI.getGuid()), true, Active.Active,
         Arrays.asList(WP_01, WP_02, WP_0A, WP_0B));

      return data;
   }

   @Test
   public void testSearchResults() throws OseeCoreException {
      List<IAtsTeamDefinition> teamDefs = new ArrayList<IAtsTeamDefinition>();
      for (String teamDefGuid : teamDefGuids) {
         IAtsTeamDefinition teamDef =
            (IAtsTeamDefinition) AtsClientService.get().getConfig().getSoleByGuid(teamDefGuid);
         teamDefs.add(teamDef);
      }

      List<IAtsActionableItem> ais = new ArrayList<IAtsActionableItem>();
      for (String aiGuid : aiGuids) {
         IAtsActionableItem ai = (IAtsActionableItem) AtsClientService.get().getConfig().getSoleByGuid(aiGuid);
         ais.add(ai);
      }

      SearchWorkPackageOperation operation =
         new SearchWorkPackageOperation("test", teamDefs, includeChildrenTeamDefs, ais, includeChildrenAIs,
            activeOption);
      Operations.executeWorkAndCheckStatus(operation);
      Set<Artifact> resultArtifacts = operation.getResultArtifacts();
      Collection<String> resultArtifactGuids = Artifacts.toGuids(resultArtifacts);
      Assert.assertEquals(resultSize, expectedWpGuids.size(), resultArtifacts.size());
      for (String expectedGid : expectedWpGuids) {
         Assert.assertTrue("Expected guid " + expectedGid + " not found in results",
            resultArtifactGuids.contains(expectedGid));
      }
   }

   private static void addTest(List<Object[]> testData, String toSearch, Collection<String> teamDefGuids, boolean includeChildrenTeamDefs, Collection<String> aiGuids, boolean includeChildrenAIs, Active both, Collection<String> expectedWpGuids) {
      testData.add(new Object[] {
         toSearch,
         teamDefGuids,
         includeChildrenTeamDefs,
         aiGuids,
         includeChildrenAIs,
         both,
         expectedWpGuids});
   }

}
