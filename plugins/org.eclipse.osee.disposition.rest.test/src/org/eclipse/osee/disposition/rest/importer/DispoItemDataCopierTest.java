/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributostmt:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.importer;

import static org.junit.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.integration.util.DispositionIntegrationRule;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.importer.DiscrepancyParser;
import org.eclipse.osee.disposition.rest.internal.importer.DispoItemDataCopier;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

/**
 * @author Angel Avila
 */

public class DispoItemDataCopierTest {

   @Mock
   DispoItem oldItem;

   @OsgiService
   public DispoConnector dispoConnector;

   @Rule
   public TestRule rule = DispositionIntegrationRule.integrationRule(this, "osee.demo.hsql");

   @Test
   public void testCopyItemData() throws Exception {

      DispoItemData oldItemTemp = new DispoItemData();
      String name = "sampleTmo.tmo";
      URL resource = getClass().getResource("sampleTmo.tmo");
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(resource.openStream());
         DiscrepancyParser.buildItemFromFile(oldItemTemp, name, stream, true, new Date());
      } finally {
         Lib.close(stream);
      }

      JSONObject discrepanciesList = oldItemTemp.getDiscrepanciesList();

      // We know the item has discrepancies at points 9-14
      JSONArray annotationsList = new JSONArray();
      addAnnotation("9", annotationsList, discrepanciesList);
      addAnnotation("10", annotationsList, discrepanciesList);
      addAnnotation("11", annotationsList, discrepanciesList);
      addAnnotation("12", annotationsList, discrepanciesList);
      addAnnotation("13", annotationsList, discrepanciesList);
      addAnnotation("14", annotationsList, discrepanciesList);
      oldItemTemp.setAnnotationsList(annotationsList);

      String itemStatus = dispoConnector.allDiscrepanciesAnnotated(oldItemTemp);
      @SuppressWarnings("rawtypes")
      Iterator keys = discrepanciesList.keys();
      Discrepancy discrepancy9 = null;
      while (keys.hasNext() && discrepancy9 == null) {
         JSONObject discrepancyAsJson = discrepanciesList.getJSONObject((String) keys.next());
         int loc = discrepancyAsJson.getInt("location");
         if (loc == 9) {
            discrepancy9 = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         }
      }

      DispoAnnotationData annotationOneFirstItem =
         DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(0));

      assertEquals(6, discrepanciesList.length());
      assertEquals(DispoStrings.Item_Complete, itemStatus);

      // Import new version of the file.
      DispoItemData itemFromNewVersion = new DispoItemData();
      String name2 = "sampleTmo.tmo";
      URL resource2 = getClass().getResource("newVersion.tmo");
      InputStream stream2 = null;
      try {
         stream2 = new BufferedInputStream(resource2.openStream());
         DiscrepancyParser.buildItemFromFile(itemFromNewVersion, name2, stream2, false, oldItemTemp.getLastUpdate());
      } finally {
         Lib.close(stream2);
      }

      JSONObject discrepanciesList2 = oldItemTemp.getDiscrepanciesList();

      DispoItemDataCopier.copyOldItemData(oldItemTemp, itemFromNewVersion);
      JSONArray annotationsList2 = itemFromNewVersion.getAnnotationsList();
      String secondItemStatus = dispoConnector.allDiscrepanciesAnnotated(itemFromNewVersion);

      DispoAnnotationData annotationOneSecondItem =
         DispoUtil.jsonObjToDispoAnnotationData(annotationsList2.getJSONObject(0));

      assertEquals(6, discrepanciesList2.length());
      // Make sure discrepancy10 in the new Item if the same as discrepancy9 from first item
      assertEquals(DispoStrings.Item_Complete, secondItemStatus);

      // Make sure the annotations from the second item are the same as those from the first except for the location ranges which should all increase by 1. 
      assertEquals(annotationOneFirstItem.getGuid(), annotationOneSecondItem.getGuid());
      assertEquals("9", annotationOneFirstItem.getLocationRefs());
      assertEquals("10", annotationOneSecondItem.getLocationRefs());
   }

   private void addAnnotation(String locationRef, JSONArray annotationsList, JSONObject discrepanciesList) throws JSONException {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      newAnnotation.setId(GUID.create());
      newAnnotation.setLocationRefs(locationRef);
      newAnnotation.setResolution("C1234");
      newAnnotation.setIsResolutionValid(true);
      newAnnotation.setDeveloperNotes("Notes");
      dispoConnector.connectAnnotation(newAnnotation, discrepanciesList);
      int length = annotationsList.length();
      newAnnotation.setIndex(length);
      annotationsList.put(length, DispoUtil.annotationToJsonObj(newAnnotation));
   }
}
