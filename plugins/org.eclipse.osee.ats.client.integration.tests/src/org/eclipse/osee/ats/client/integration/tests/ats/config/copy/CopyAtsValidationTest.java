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
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.config.copy.CopyAtsValidation;
import org.eclipse.osee.ats.demo.api.DemoActionableItems;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
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
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @Test
   public void testValidate()  {

      // set name to one that can't be converted
      ConfigData data = getConfigData();
      IAtsActionableItem ai = data.getActionableItem();
      String origName = ai.getName();
      ai.setName("CSCI");

      XResultData results = new XResultData(false);
      data.setActionableItem(ai);
      CopyAtsValidation validation = new CopyAtsValidation(data, results);
      validation.validate();
      Assert.assertTrue(results.isErrors());
      Assert.assertEquals(1, results.getNumErrors());

      // reset name to normal
      ai.setName(origName);

      results.clear();
      validation.validate();
      Assert.assertFalse(results.isErrors());
      Assert.assertEquals(0, results.getNumErrors());
   }

   public static ConfigData getConfigData()  {
      ConfigData data = new ConfigData();
      data.setReplaceStr("CISv2");
      data.setSearchStr("CIS");
      IAtsTeamDefinition tda = DemoTestUtil.getTeamDef(DemoTeam.CIS_SW);
      data.setTeamDef(tda);
      IAtsActionableItem aiArt = DemoTestUtil.getActionableItem(DemoActionableItems.CIS_CSCI);
      data.setActionableItem(aiArt);
      data.setRetainTeamLeads(true);
      data.setPersistChanges(true);
      return data;
   }
}
