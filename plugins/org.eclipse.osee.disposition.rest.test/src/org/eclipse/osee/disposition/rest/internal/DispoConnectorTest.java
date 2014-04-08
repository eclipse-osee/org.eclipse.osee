/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.disposition.rest.util.DispoUtil.annotationToJsonObj;
import static org.eclipse.osee.disposition.rest.util.DispoUtil.discrepancyToJsonObj;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class DispoConnectorTest {

   private DispoItemData dispoItem;
   private DispoConnector dispoConnector;

   String[] idsForDiscrepancies1_5 = {"adf", "ads", "acc", "abc", "ace"};

   String[] idsForDiscrepancies12_18 = {"cbb", "bcd", "cca", "ccd", "ccbb", "cabb", "cqqq"};

   String id20 = "gdd";

   String annotIdOne = "annotId0";
   String annotIdTwo = "annotId1";
   String annotIdThree = "annotId2";
   String annotIdFour = "annotId3";
   String annotIdFive = "annotId4";

   @Before
   public void setUp() {
      dispoItem = new DispoItemData();
      dispoConnector = new DispoConnector();

      Map<String, JSONObject> discrepancies = new HashMap<String, JSONObject>();

      for (int i = 1; i <= 5; i++) {
         Discrepancy discrepancy = new Discrepancy();
         discrepancy.setLocation(i);
         discrepancy.setId(idsForDiscrepancies1_5[i - 1]);
         discrepancies.put(discrepancy.getId(), discrepancyToJsonObj(discrepancy));
      }

      for (int i = 12; i <= 18; i++) {
         Discrepancy discrepancy = new Discrepancy();
         discrepancy.setLocation(i);
         discrepancy.setId(idsForDiscrepancies12_18[i - 12]);
         discrepancies.put(discrepancy.getId(), discrepancyToJsonObj(discrepancy));
      }

      Discrepancy discrepancy20 = new Discrepancy();
      discrepancy20.setLocation(20);
      discrepancy20.setId(id20);
      discrepancies.put(discrepancy20.getId(), discrepancyToJsonObj(discrepancy20));

      JSONObject discrepanciesList = new JSONObject(discrepancies);
      dispoItem.setDiscrepanciesList(discrepanciesList);
   }

   @Test
   public void testConnectAnnotationsSingleCompelete() throws JSONException {
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5, 12-18, 20");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      JSONArray annotationsList = new JSONArray();
      annotationsList.put(annotationToJsonObj(annotationOne));
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      JSONArray idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      for (int i = 0; i < 13; i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.getString(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.getString(i), idsForDiscrepancies12_18[i - 5]);
         } else {
            assertEquals(idsOfCoveredDiscrepancies.getString(i), id20);
         }
      }

      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testConnectAnnotationsSingleIncomplete() throws JSONException {
      // This will test a single annotation that covers most but not all discrepancies
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5, 12-18");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      JSONArray annotationsList = new JSONArray();
      annotationsList.put(annotationToJsonObj(annotationOne));
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      JSONArray idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      for (int i = 0; i < 13; i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.getString(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.getString(i), idsForDiscrepancies12_18[i - 5]);
         }
      }
      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);
   }

   @Test
   public void testConnectAnnotationsMultipleComplete() throws JSONException {
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-14");
      annotationTwo.setId(annotIdTwo);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 20, 18");
      annotationThree.setId(annotIdThree);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("15, 17");
      annotationFour.setId(annotIdFive);
      JSONArray idsOfCoveredDiscFive = new JSONArray();
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);

      JSONObject discrepanciesArray = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationThree, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFour, discrepanciesArray);

      JSONArray idsOfCoveredDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.length(), 5);
      for (int i = 0; i < 5; i++) {
         assertEquals(idsOfCoveredDiscrepancies.getString(i), idsForDiscrepancies1_5[i]);
      }

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveredDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.length(), 3);
      assertEquals(idsOfCoveredDiscrepancies.getString(0), idsForDiscrepancies12_18[0]);
      assertEquals(idsOfCoveredDiscrepancies.getString(1), idsForDiscrepancies12_18[1]);
      assertEquals(idsOfCoveredDiscrepancies.getString(2), idsForDiscrepancies12_18[2]);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveredDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.length(), 3);
      assertEquals(idsOfCoveredDiscrepancies.getString(0), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveredDiscrepancies.getString(1), idsForDiscrepancies12_18[6]);
      assertEquals(idsOfCoveredDiscrepancies.getString(2), id20);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveredDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.length(), 2);
      assertEquals(idsOfCoveredDiscrepancies.getString(0), idsForDiscrepancies12_18[3]);
      assertEquals(idsOfCoveredDiscrepancies.getString(1), idsForDiscrepancies12_18[5]);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedOneComplete() throws JSONException {
      JSONArray annotationsAsList = new JSONArray();
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5, 12-18, 20");
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);
      annotationOne.setIsResolutionValid(true);
      annotationOne.setId(annotIdOne);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      annotationsAsList.put(annotationToJsonObj(annotationOne));
      dispoItem.setAnnotationsList(annotationsAsList);
      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedManyComplete() throws JSONException {
      List<JSONObject> annotationsAsList = new ArrayList<JSONObject>();
      // Create 4 annotations, one for every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5");
      annotationOne.setId(annotIdOne);
      annotationOne.setIsResolutionValid(true);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);
      annotationsAsList.add(annotationToJsonObj(annotationOne));

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-18");
      annotationTwo.setId(annotIdTwo);
      annotationTwo.setIsResolutionValid(true);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);
      annotationsAsList.add(annotationToJsonObj(annotationTwo));

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("20");
      annotationThree.setId(annotIdThree);
      annotationThree.setIsResolutionValid(true);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);
      annotationsAsList.add(annotationToJsonObj(annotationThree));

      JSONArray annotationsList = new JSONArray(annotationsAsList);
      dispoItem.setAnnotationsList(annotationsList);

      JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();

      dispoConnector.connectAnnotation(annotationOne, discrepanciesList);
      annotationsList.put(0, annotationToJsonObj(annotationOne));
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.put(0, annotationToJsonObj(annotationOne));
      dispoItem.setAnnotationsList(annotationsList);
      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationTwo, discrepanciesList);
      annotationsList.put(1, annotationToJsonObj(annotationTwo));
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.put(1, annotationToJsonObj(annotationTwo));
      dispoItem.setAnnotationsList(annotationsList);
      actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationThree, discrepanciesList);
      annotationsList.put(2, annotationToJsonObj(annotationThree));
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.put(2, annotationToJsonObj(annotationThree));
      dispoItem.setAnnotationsList(annotationsList);
      actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testZComplexCase() throws JSONException {
      JSONArray annotationsList = new JSONArray();
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("5, 1-3");
      annotationOne.setId(annotIdOne);
      annotationOne.setIsResolutionValid(true);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-15");
      annotationTwo.setId(annotIdTwo);
      annotationTwo.setIsResolutionValid(true);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 20");
      annotationThree.setId(annotIdThree);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIsResolutionValid(true);
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("4, 20");
      annotationFour.setId(annotIdFour);
      JSONArray idsOfCoveredDiscFour = new JSONArray();
      annotationFour.setIsResolutionValid(true);
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFour);

      DispoAnnotationData annotationFive = new DispoAnnotationData();
      annotationFive.setLocationRefs("18, 16, 17, 4");
      annotationFive.setId(annotIdFive);
      JSONArray idsOfCoveredDiscFive = new JSONArray();
      annotationFive.setIsResolutionValid(true);
      annotationFive.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);

      JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesList);
      annotationsList.put(annotationToJsonObj(annotationOne));
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesList);
      annotationsList.put(annotationToJsonObj(annotationTwo));
      dispoConnector.connectAnnotation(annotationThree, discrepanciesList);
      annotationsList.put(annotationToJsonObj(annotationThree));
      dispoConnector.connectAnnotation(annotationFour, discrepanciesList);
      annotationsList.put(annotationToJsonObj(annotationFour));
      dispoConnector.connectAnnotation(annotationFive, discrepanciesList);
      annotationsList.put(annotationToJsonObj(annotationFive));

      dispoItem.setAnnotationsList(annotationsList);

      JSONArray idsOfCoveringDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveringDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 4);
      assertEquals(idsOfCoveringDiscrepancies.getString(0), idsForDiscrepancies1_5[0]);
      assertEquals(idsOfCoveringDiscrepancies.getString(1), idsForDiscrepancies1_5[1]);
      assertEquals(idsOfCoveringDiscrepancies.getString(2), idsForDiscrepancies1_5[2]);
      assertEquals(idsOfCoveringDiscrepancies.getString(3), idsForDiscrepancies1_5[4]);

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveringDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 4);
      assertEquals(idsOfCoveringDiscrepancies.getString(0), idsForDiscrepancies12_18[0]);
      assertEquals(idsOfCoveringDiscrepancies.getString(1), idsForDiscrepancies12_18[1]);
      assertEquals(idsOfCoveringDiscrepancies.getString(2), idsForDiscrepancies12_18[2]);
      assertEquals(idsOfCoveringDiscrepancies.getString(3), idsForDiscrepancies12_18[3]);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveringDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getString(0), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveringDiscrepancies.getString(1), id20);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getString(0), idsForDiscrepancies1_5[3]);
      assertEquals(idsOfCoveringDiscrepancies.getString(1), id20);

      assertTrue(annotationFive.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFive.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 4);
      assertEquals(idsOfCoveringDiscrepancies.getString(0), idsForDiscrepancies1_5[3]);
      assertEquals(idsOfCoveringDiscrepancies.getString(1), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveringDiscrepancies.getString(2), idsForDiscrepancies12_18[5]);
      assertEquals(idsOfCoveringDiscrepancies.getString(3), idsForDiscrepancies12_18[6]);

      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }
}
