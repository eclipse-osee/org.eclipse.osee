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

package org.eclipse.osee.ats.ide.integration.tests;

import java.util.Map;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoProgram;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.ats.health.AtsHealthTestTest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test for AtsConfigurations
 *
 * @author Donald G Dunne
 */
public class AtsConfigurationsTest extends AtsHealthTestTest {

   @BeforeClass
   public static void cleanup() {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @SuppressWarnings("unlikely-arg-type")
   @org.junit.Test
   public void testConfigs() {
      AtsApiService.get().clearCachesServerWithPend();
      AtsApiService.get().clearCaches();
      AtsConfigurations configs = AtsApiService.get().getConfigService().getConfigurationsWithPend();

      Assert.assertNotNull(configs.getValidStateNames());
      Assert.assertTrue(!configs.getValidStateNames().isEmpty());

      ArtifactId topActionableItem = configs.getTopActionableItem();
      Assert.assertNotNull(topActionableItem);
      Assert.assertTrue(!configs.getIdToAi().isEmpty());
      Assert.assertNotNull(configs.getIdToAi().get(topActionableItem.getId()));
      Assert.assertTrue(configs.getIdToAi().containsValue(topActionableItem));

      ArtifactId topTeamDefinition = configs.getTopTeamDefinition();
      Assert.assertNotNull(topTeamDefinition);
      Assert.assertTrue(!configs.getIdToTeamDef().isEmpty());
      Assert.assertNotNull(configs.getIdToTeamDef().get(topTeamDefinition.getId()));
      Assert.assertTrue(configs.getIdToTeamDef().containsValue(topTeamDefinition));

      Assert.assertTrue(!configs.getIdToVersion().isEmpty());

      Assert.assertTrue(!configs.getIdToUser().isEmpty());
      Assert.assertTrue(configs.getIdToUser().containsKey(DemoUsers.Joe_Smith.getId()));
      Assert.assertTrue(configs.getIdToUser().containsValue(DemoUsers.Joe_Smith));

      Assert.assertTrue(!configs.getIdToProgram().isEmpty());
      Assert.assertTrue(configs.getIdToProgram().containsKey(DemoProgram.sawProgram.getId()));
      Assert.assertTrue(configs.getIdToProgram().containsValue(DemoProgram.sawProgram));

      Map<Long, JaxAgileTeam> idToAgileTeam = configs.getIdToAgileTeam();
      Assert.assertTrue(!idToAgileTeam.isEmpty());
      Assert.assertTrue(idToAgileTeam.containsKey(DemoArtifactToken.SAW_Agile_Team.getId()));
      Assert.assertTrue(idToAgileTeam.containsValue(DemoArtifactToken.SAW_Agile_Team));

      Assert.assertEquals(configs.getIdToAgileFeature().size(), 4);

      Assert.assertTrue(!configs.getUserIdToUserArtId().isEmpty());
      Assert.assertTrue(configs.getUserIdToUserArtId().containsKey(DemoUsers.Joe_Smith.getUserId()));
      Assert.assertTrue(configs.getUserIdToUserArtId().containsValue(DemoUsers.Joe_Smith));

      Assert.assertTrue(!configs.getUserNameToUserArtId().isEmpty());
      Assert.assertTrue(configs.getUserNameToUserArtId().containsKey(DemoUsers.Joe_Smith.getName()));
      Assert.assertTrue(configs.getUserNameToUserArtId().containsValue(DemoUsers.Joe_Smith));

      Assert.assertEquals(configs.getTeamDefToAgileTeam().size(), 6);
      Assert.assertTrue(configs.getTeamDefToAgileTeam().containsKey(DemoArtifactToken.SAW_Requirements));
      Assert.assertTrue(configs.getTeamDefToAgileTeam().containsValue(DemoArtifactToken.SAW_Agile_Team));

      Assert.assertTrue(!configs.getTeamDefToProgram().isEmpty());
      Assert.assertTrue(configs.getTeamDefToProgram().containsKey(DemoArtifactToken.SAW_Requirements.getId()));
      Assert.assertTrue(configs.getTeamDefToProgram().containsValue(DemoProgram.sawProgram.getId()));

      Assert.assertTrue(!configs.getFeatureToAgileTeam().isEmpty());
      Assert.assertTrue(configs.getFeatureToAgileTeam().containsValue(DemoArtifactToken.SAW_Agile_Team));

   }

}
