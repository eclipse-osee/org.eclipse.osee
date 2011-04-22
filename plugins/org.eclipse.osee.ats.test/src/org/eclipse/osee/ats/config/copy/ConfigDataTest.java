/*
 * Created on Mar 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemManager;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionManager;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.DemoTeam;

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
      TeamDefinitionArtifact tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      ActionableItemArtifact aia = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code);
      data.setActionableItem(aia);
      results.clear();
      data.validateData(results);
      Assert.assertFalse(results.isErrors());
   }

   @org.junit.Test
   public void testGetSetTeamDefinition() throws Exception {
      ConfigData data = new ConfigData();
      TeamDefinitionArtifact tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(tda, data.getTeamDef());
   }

   @org.junit.Test
   public void testGetAiArts() throws Exception {
      ConfigData data = new ConfigData();
      ActionableItemArtifact aia = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_Code);
      data.setActionableItem(aia);
      Assert.assertEquals(aia, data.getActionableItem());
   }

   @org.junit.Test
   public void testParentTeamDefinition() throws Exception {
      ConfigData data = new ConfigData();
      TeamDefinitionArtifact tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_Code);
      data.setTeamDef(tda);
      Assert.assertEquals(DemoTestUtil.getTeamDef(DemoTeam.CIS_SW), data.getParentTeamDef());

      tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(TeamDefinitionManager.getTopTeamDefinition(), data.getParentTeamDef());
   }

   @org.junit.Test
   public void testParentActionableItem() throws Exception {
      ConfigData data = new ConfigData();
      TeamDefinitionArtifact tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_Code);
      data.setTeamDef(tda);
      Assert.assertEquals(DemoTestUtil.getActionableItem(DemoActionableItems.CIS_CSCI), data.getParentActionableItem());

      tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      Assert.assertEquals(ActionableItemManager.getTopActionableItem(), data.getParentActionableItem());

   }
}
