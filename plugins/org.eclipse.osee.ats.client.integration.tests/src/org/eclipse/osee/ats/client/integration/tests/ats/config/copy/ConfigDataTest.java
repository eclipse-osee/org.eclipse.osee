/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.config.copy;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.demo.api.DemoActionableItems;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.eclipse.osee.framework.core.util.result.XResultData;
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
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
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
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
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
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_Code);
      data.setTeamDef(tda);
      Assert.assertEquals(DemoTestUtil.getTeamDef(DemoTeam.CIS_SW), data.getParentTeamDef());

      tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         data.getParentTeamDef());
   }

   @org.junit.Test
   public void testParentActionableItem() throws Exception {
      ConfigData data = new ConfigData();
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_Code);
      data.setTeamDef(tda);
      Assert.assertEquals(DemoTestUtil.getActionableItem(DemoActionableItems.CIS_CSCI), data.getParentActionableItem());

      tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(ActionableItems.getTopActionableItem(AtsClientService.get()), data.getParentActionableItem());

   }
}
