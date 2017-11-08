/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class DispoSetCopierTest {

   @OsgiService
   public DispoConnector dispoConnector;

   private final String discrepancyId1 = "135rrf";
   private final String discrepancyId2 = "135rzzrf";
   private final String discrepancyId3 = "aaabe";
   private final String discrepancyId4 = "abazzeee";
   private final String discrepancyId5 = "cccce";

   private final String annotationId1 = "42241";
   private final String annotationId2 = "adag34";
   private final String annotationId3 = "ccccc";
   private final String annotationId4 = "dewet";
   private final String annotationId5 = "dddddddd";

   private final DispoItemData sourceItem = new DispoItemData();
   private final Map<String, Discrepancy> discrepancies = new HashMap<>();

   @Before
   public void setup() {
      dispoConnector = new DispoConnector();

      Discrepancy discrepancy1 = new Discrepancy();
      discrepancy1.setId(discrepancyId1);
      discrepancy1.setLocation("1");
      discrepancy1.setText("text");
      discrepancies.put(discrepancyId1, discrepancy1);

      Discrepancy discrepanc2 = new Discrepancy();
      discrepanc2.setId(discrepancyId2);
      discrepanc2.setLocation("2");
      discrepanc2.setText("text2");
      discrepancies.put(discrepancyId2, discrepanc2);

      Discrepancy discrepanc3 = new Discrepancy();
      discrepanc3.setId(discrepancyId3);
      discrepanc3.setLocation("3");
      discrepanc3.setText("text3");
      discrepancies.put(discrepancyId3, discrepanc3);

      Discrepancy discrepanc4 = new Discrepancy();
      discrepanc4.setId(discrepancyId4);
      discrepanc4.setLocation("4");
      discrepanc4.setText("text4");
      discrepancies.put(discrepancyId4, discrepanc4);

      Discrepancy discrepanc5 = new Discrepancy();
      discrepanc5.setId(discrepancyId5);
      discrepanc5.setLocation("5");
      discrepanc5.setText("text5");
      discrepancies.put(discrepancyId5, discrepanc5);

      List<DispoAnnotationData> annotations = new ArrayList<>();

      DispoAnnotationData annotation1 = new DispoAnnotationData();
      annotation1.setId(annotationId1);
      annotation1.setIndex(1);
      annotation1.setIsDefault(false);
      annotation1.setIsConnected(true);
      annotation1.setLocationRefs("1");
      annotation1.setResolutionType("ResolutionType1");
      annotation1.setResolution("Resolution1");
      dispoConnector.connectAnnotation(annotation1, discrepancies);
      annotations.add(annotation1);

      DispoAnnotationData annotation2 = new DispoAnnotationData();
      annotation2.setId(annotationId2);
      annotation2.setIndex(2);
      annotation2.setIsDefault(false);
      annotation2.setIsConnected(true);
      annotation2.setLocationRefs("2");
      annotation2.setResolutionType("ResolutionType2");
      annotation2.setResolution("Resolution2");
      dispoConnector.connectAnnotation(annotation2, discrepancies);
      annotations.add(annotation2);

      DispoAnnotationData annotation3 = new DispoAnnotationData();
      annotation3.setId(annotationId3);
      annotation3.setIndex(3);
      annotation3.setIsDefault(false);
      annotation3.setIsConnected(true);
      annotation3.setLocationRefs("3");
      annotation3.setResolutionType("ResolutionType3");
      annotation3.setResolution("Resolution3");
      dispoConnector.connectAnnotation(annotation3, discrepancies);
      annotations.add(annotation3);

      DispoAnnotationData annotation4 = new DispoAnnotationData();
      annotation4.setId(annotationId4);
      annotation4.setIndex(4);
      annotation4.setIsDefault(false);
      annotation4.setIsConnected(true);
      annotation4.setLocationRefs("4");
      annotation4.setResolutionType("ResolutionType4");
      annotation4.setResolution("Resolution4");
      dispoConnector.connectAnnotation(annotation4, discrepancies);
      annotations.add(annotation4);

      DispoAnnotationData annotation5 = new DispoAnnotationData();
      annotation5.setId(annotationId5);
      annotation5.setIndex(5);
      annotation5.setIsDefault(false);
      annotation5.setIsConnected(true);
      annotation5.setLocationRefs("5");
      annotation5.setResolutionType("ResolutionType5");
      annotation5.setResolution("Resolution5");
      dispoConnector.connectAnnotation(annotation5, discrepancies);
      annotations.add(annotation5);

      sourceItem.setAnnotationsList(annotations);
      sourceItem.setDiscrepanciesList(discrepancies);
      sourceItem.setName("name");
      sourceItem.setGuid("guid");
   }

   @Test
   public void testSourceAndDestHaveSameDiscrepancies() throws Exception {
      OperationReport report = new OperationReport();
      DispoItemData destItem = new DispoItemData();
      destItem.setName("name");
      destItem.setDiscrepanciesList(discrepancies);
      destItem.setAnnotationsList(new ArrayList<DispoAnnotationData>());
      String itemStatus = dispoConnector.getItemStatus(destItem);
      destItem.setStatus(itemStatus);

      DispoSetCopier copier = new DispoSetCopier(dispoConnector);
      List<DispoItem> copyResults =
         copier.copyAllDispositions(getNameToDestItemsMap(Collections.singletonList(destItem)),
            Collections.singletonList(sourceItem), false, null, report);

      DispoItem resultItem = copyResults.get(0);

      List<DispoAnnotationData> resultAnnotations = resultItem.getAnnotationsList();
      Assert.assertTrue(resultAnnotations.size() == 5);
   }

   private Map<String, Set<DispoItemData>> getNameToDestItemsMap(List<DispoItemData> items) {
      Map<String, Set<DispoItemData>> toReturn = new HashMap<>();

      for (DispoItemData item : items) {
         Set<DispoItemData> exitingItemsWithName = toReturn.get(item.getName());
         if (exitingItemsWithName == null) {
            exitingItemsWithName = new HashSet<>();
            exitingItemsWithName.add(item);
            toReturn.put(item.getName(), exitingItemsWithName);
         } else {
            exitingItemsWithName.add(item);
         }
      }
      return toReturn;
   }
}
