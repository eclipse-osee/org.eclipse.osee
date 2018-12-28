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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.integration.util.DispositionIntegrationRule;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.importer.DiscrepancyParser;
import org.eclipse.osee.disposition.rest.internal.importer.DiscrepancyParser.MutableString;
import org.eclipse.osee.disposition.rest.internal.importer.DispoItemDataCopier;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.db.mock.OsgiService;
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
   public TestRule rule = DispositionIntegrationRule.integrationRule(this);

   @Test
   public void testCopyItemData() throws Exception {
      MutableBoolean stoppedParsing = new MutableBoolean(false);
      MutableBoolean isException = new MutableBoolean(false);
      MutableString exMessage = new MutableString();

      DispoItemData oldItemTemp = new DispoItemData();
      String name = "sampleTmo.tmo";
      URL resource = getClass().getResource("sampleTmo.tmo");
      InputStream stream = null;
      try {
         stream = new BufferedInputStream(resource.openStream());
         DiscrepancyParser.buildItemFromFile(oldItemTemp, name, stream, true, new Date(), stoppedParsing, isException,
            exMessage);
      } finally {
         Lib.close(stream);
      }

      Map<String, Discrepancy> discrepanciesList = oldItemTemp.getDiscrepanciesList();

      // We know the item has discrepancies at points 9-14
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      addAnnotation("9", annotationsList, discrepanciesList);
      addAnnotation("10", annotationsList, discrepanciesList);
      addAnnotation("11", annotationsList, discrepanciesList);
      addAnnotation("12", annotationsList, discrepanciesList);
      addAnnotation("13", annotationsList, discrepanciesList);
      addAnnotation("14", annotationsList, discrepanciesList);
      oldItemTemp.setAnnotationsList(annotationsList);

      String itemStatus = dispoConnector.getItemStatus(oldItemTemp);
      DispoAnnotationData annotationOneFirstItem = annotationsList.get(0);

      assertEquals(6, discrepanciesList.size());
      assertEquals(DispoStrings.Item_Complete, itemStatus);

      // Import new version of the file.
      DispoItemData itemFromNewVersion = new DispoItemData();
      String name2 = "sampleTmo.tmo";
      URL resource2 = getClass().getResource("newVersion.tmo");
      InputStream stream2 = null;
      try {
         stream2 = new BufferedInputStream(resource2.openStream());
         DiscrepancyParser.buildItemFromFile(itemFromNewVersion, name2, stream2, false, oldItemTemp.getLastUpdate(),
            stoppedParsing, isException, exMessage);
      } finally {
         Lib.close(stream2);
      }

      Map<String, Discrepancy> discrepanciesList2 = oldItemTemp.getDiscrepanciesList();

      OperationReport report = new OperationReport();
      DispoItemDataCopier.copyOldItemData(oldItemTemp, itemFromNewVersion, report);
      List<DispoAnnotationData> annotationsList2 = itemFromNewVersion.getAnnotationsList();
      String secondItemStatus = dispoConnector.getItemStatus(itemFromNewVersion);

      DispoAnnotationData annotationOneSecondItem = annotationsList2.get(0);

      assertEquals(6, discrepanciesList2.size());
      // Make sure discrepancy10 in the new Item if the same as discrepancy9 from first item
      assertEquals(DispoStrings.Item_Complete, secondItemStatus);

      // Make sure the annotations from the second item are the same as those from the first except for the location ranges which should all increase by 1.
      assertEquals(annotationOneFirstItem.getGuid(), annotationOneSecondItem.getGuid());
      assertEquals("10", annotationOneSecondItem.getLocationRefs());
   }

   private void addAnnotation(String locationRef, List<DispoAnnotationData> annotationsList, Map<String, Discrepancy> discrepanciesList) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      newAnnotation.setId(GUID.create());
      newAnnotation.setLocationRefs(locationRef);
      newAnnotation.setResolution("C1234");
      newAnnotation.setResolutionType("C1234");
      newAnnotation.setIsResolutionValid(true);
      newAnnotation.setDeveloperNotes("Notes");
      dispoConnector.connectAnnotation(newAnnotation, discrepanciesList);
      int length = annotationsList.size();
      newAnnotation.setIndex(length);
      annotationsList.add(length, newAnnotation);
   }
}
