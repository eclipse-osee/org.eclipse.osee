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

package org.eclipse.osee.ats.ide.integration.tests.ats.config.copy;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.config.copy.ConfigData;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;

/**
 * Test case for {@link ConfigData}
 *
 * @author Donald G. Dunne
 */
public class ConfigDataTest {

   @org.junit.Test
   public void testValidateData() throws Exception {
      ConfigData data = new ConfigData();
      XResultData results = new XResultData(false);
      data.validateData(results);
      Assert.assertTrue(results.isErrors());
      Assert.assertEquals(4, results.getNumErrors());

      data.setReplaceStr("ReplStr");
      data.setSearchStr("SrchStr");
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_SW);
      data.setTeamDef(tda);
      IAtsActionableItem aia = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code);
      data.setActionableItem(aia);
      results.clear();
      data.validateData(results);
      Assert.assertFalse(results.isErrors());
   }

   @org.junit.Test
   public void testGetSetTeamDefinition() throws Exception {
      ConfigData data = new ConfigData();
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(tda, data.getTeamDef());
   }

   @org.junit.Test
   public void testGetAiArts() throws Exception {
      ConfigData data = new ConfigData();
      IAtsActionableItem aia = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code);
      data.setActionableItem(aia);
      Assert.assertEquals(aia, data.getActionableItem());
   }

   @org.junit.Test
   public void testParentTeamDefinition() throws Exception {
      ConfigData data = new ConfigData();
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_Code);
      data.setTeamDef(tda);
      Assert.assertEquals(DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_SW), data.getParentTeamDef());

      tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(AtsApiService.get().getTeamDefinitionService().getTopTeamDefinition(),
         data.getParentTeamDef());
   }

   @org.junit.Test
   public void testParentActionableItem() throws Exception {
      ConfigData data = new ConfigData();
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_Code);
      data.setTeamDef(tda);
      IAtsActionableItem actionableItem = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_CSCI);
      IAtsActionableItem parentActionableItem = data.getParentActionableItem();
      Assert.assertEquals(actionableItem, parentActionableItem);

      tda = DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(AtsApiService.get().getActionableItemService().getTopActionableItem(AtsApiService.get()),
         data.getParentActionableItem());

   }
}
