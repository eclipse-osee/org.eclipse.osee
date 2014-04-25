/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.importer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.internal.importer.DiscrepancyParser;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class DispoImporterTest {

   @Test
   public void testConnectAnnotationsSingleCompelete() throws Exception {

      DispoItemData dispoItem = new DispoItemData();
      boolean stoppedParsing = false;

      String name = "sampleTmo.tmo";
      URL resource = getClass().getResource("sampleTmo.tmo");
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(resource.openStream());
         stoppedParsing = DiscrepancyParser.buildItemFromFile(dispoItem, name, stream, false, new Date(0));
      } finally {
         Lib.close(stream);
      }

      int actualLength = dispoItem.getDiscrepanciesList().length();
      Assert.assertEquals(6, actualLength);
      Assert.assertFalse(stoppedParsing);

      JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();
      @SuppressWarnings("rawtypes")
      Iterator keys = discrepanciesList.keys();
      Discrepancy discrepancy13 = null;
      while (keys.hasNext() && discrepancy13 == null) {
         JSONObject discrepancyAsJson = discrepanciesList.getJSONObject((String) keys.next());
         int loc = discrepancyAsJson.getInt("location");
         if (loc == 13) {
            discrepancy13 = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         }
      }
      String actualText = discrepancy13.getText();

      Assert.assertEquals("Failure at Test Point 13. Check Point: Boo_READY. Expected: WT[1]. Actual: WT [0]. ",
         actualText);

      // Test Group Discrepancy
      keys = discrepanciesList.keys();
      Discrepancy discrepancy12 = null;
      while (keys.hasNext() && discrepancy12 == null) {
         JSONObject discrepancyAsJson = discrepanciesList.getJSONObject((String) keys.next());
         int loc = discrepancyAsJson.getInt("location");
         if (loc == 14) {
            discrepancy12 = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         }
      }
      actualText = discrepancy12.getText();

      // @formatter:off
		Assert.assertEquals(
				"Failure at Test Point 14. Check Group with Checkpoint Failures: "
				+ "Check Point: pokemon. Expected: charmander. Actual: squirtle. "
				+ "Check Point: superman. Expected: unbeatable. Actual: kryptonite. "
				+ "Check Point: Nicest Guy Ever. Expected: Bob Ross. Actual: Bob Ross. "
				+ "Check Point: Things fail. Expected: sometimes. Actual: A lot. ", actualText);
		// @formatter:on
   }
}
