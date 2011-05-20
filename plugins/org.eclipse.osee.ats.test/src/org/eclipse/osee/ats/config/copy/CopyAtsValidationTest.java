/*
 * Created on Mar 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.DemoTeam;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link CopyAtsValidation}
 * 
 * @author Donald G. Dunne
 */
public class CopyAtsValidationTest {

   @BeforeClass
   public static void setup() throws Exception {
      DemoTestUtil.setUpTest();
   }

   @Test
   public void testValidate() throws OseeCoreException {

      // set name to one that can't be converted
      ConfigData data = getConfigData();
      ActionableItemArtifact aiArt = data.getActionableItem();
      String origName = aiArt.getName();
      aiArt.setName("CSCI");
      aiArt.persist(getClass().getSimpleName());

      XResultData results = new XResultData(false);
      data.setActionableItem(aiArt);
      CopyAtsValidation validation = new CopyAtsValidation(data, results);
      validation.validate();
      Assert.assertTrue(results.isErrors());
      Assert.assertEquals(1, results.getNumErrors());

      // reset name to normal
      aiArt.setName(origName);
      aiArt.persist(getClass().getSimpleName());

      results.clear();
      validation.validate();
      Assert.assertFalse(results.isErrors());
      Assert.assertEquals(0, results.getNumErrors());
   }

   public static ConfigData getConfigData() throws OseeCoreException {
      ConfigData data = new ConfigData();
      data.setReplaceStr("CISv2");
      data.setSearchStr("CIS");
      TeamDefinitionArtifact tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      ActionableItemArtifact aiArt = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_CSCI);
      data.setActionableItem(aiArt);
      data.setRetainTeamLeads(true);
      data.setPersistChanges(true);
      return data;
   }
}
