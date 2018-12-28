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
import org.junit.Assert;
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

      Map<String, Discrepancy> discrepancies = new HashMap<>();

      for (int i = 1; i <= 5; i++) {
         Discrepancy discrepancy = new Discrepancy();
         discrepancy.setLocation(String.valueOf(i));
         discrepancy.setId(idsForDiscrepancies1_5[i - 1]);
         discrepancies.put(discrepancy.getId(), discrepancy);
      }

      for (int i = 12; i <= 18; i++) {
         Discrepancy discrepancy = new Discrepancy();
         discrepancy.setLocation(String.valueOf(i));
         discrepancy.setId(idsForDiscrepancies12_18[i - 12]);
         discrepancies.put(discrepancy.getId(), discrepancy);
      }

      Discrepancy discrepancy20 = new Discrepancy();
      discrepancy20.setLocation("20");
      discrepancy20.setId(id20);
      discrepancies.put(discrepancy20.getId(), discrepancy20);

      dispoItem.setDiscrepanciesList(discrepancies);
   }

   @Test
   public void testConnectAnnotationsStringComplete() {
      // Convert Location Refs to have characters
      for (Discrepancy discrepancy : dispoItem.getDiscrepanciesList().values()) {
         discrepancy.setLocation(discrepancy.getLocation() + "zzz");
      }

      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1zzz, 2zzz, 3zzz,4zzz,5zzz,12zzz,13zzz,14zzz,15zzz,16zzz,17zzz,18zzz,20zzz");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("Code");
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      annotationsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      List<String> idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      Assert.assertTrue(idsOfCoveredDiscrepancies.size() == 13);
      for (int i = 0; i < idsOfCoveredDiscrepancies.size(); i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies12_18[i - 5]);
         } else {
            assertEquals(idsOfCoveredDiscrepancies.get(i), id20);
         }
      }

      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testConnectAnnotationsStringAnalyze() {
      // Convert Location Refs to have characters
      for (Discrepancy discrepancy : dispoItem.getDiscrepanciesList().values()) {
         discrepancy.setLocation(discrepancy.getLocation() + "zzz");
      }

      int idsSize = 13;
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1zzz, 2zzz, 3zzz,4zzz,5zzz,12zzz,13zzz,14zzz,15zzz,16zzz,17zzz,18zzz,20zzz");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("Modify_Code");
      annotationOne.setIsAnalyze(true);
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      annotationsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      List<String> idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      Assert.assertTrue(idsOfCoveredDiscrepancies.size() == idsSize);
      for (int i = 0; i < idsOfCoveredDiscrepancies.size(); i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies12_18[i - 5]);
         } else {
            assertEquals(idsOfCoveredDiscrepancies.get(i), id20);
         }
      }

      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Analyzed, actual);
   }

   @Test
   public void testConnectAnnotationsStringIncomplete() {
      // Convert Location Refs to have characters
      for (Discrepancy discrepancy : dispoItem.getDiscrepanciesList().values()) {
         discrepancy.setLocation(discrepancy.getLocation() + "zzz");
      }

      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1zzz, 2zzz,12zzz,13zzz,14zzz,15zzz,16zzz,17zzz,18zzz");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("Code");
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      annotationsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      List<String> idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      Assert.assertTrue(idsOfCoveredDiscrepancies.size() == 9);
      for (int i = 0; i < idsOfCoveredDiscrepancies.size(); i++) {
         if (i < 2) {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
         } else {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies12_18[i - 2]);
         }

      }

      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);
   }

   @Test
   public void testConnectAnnotationsSingleCompelete() {
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1- 5, 12 - 18, 20");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("Code");
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      annotationsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      List<String> idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      for (int i = 0; i < 13; i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies12_18[i - 5]);
         } else {
            assertEquals(idsOfCoveredDiscrepancies.get(i), id20);
         }
      }

      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testConnectAnnotationsSingleIncomplete() {
      // This will test a single annotation that covers most but not all discrepancies
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5, 12-18");
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("Code");
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      annotationsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsList);

      // annotation 1 should be connected to all Discrepancies
      List<String> idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      for (int i = 0; i < 13; i++) {
         if (i < 5) {//first 5 discrepancies are from ids array 1-5
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
         } else if (i < 12) {
            assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies12_18[i - 5]);
         }
      }
      assertTrue(annotationOne.getIsConnected());

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);
   }

   @Test
   public void testConnectAnnotationsMultipleComplete() {
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5");
      annotationOne.setId(annotIdOne);
      List<String> idsOfCoveredDiscOne = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-14");
      annotationTwo.setId(annotIdTwo);
      List<String> idsOfCoveredDiscTwo = new ArrayList<>();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 20, 18");
      annotationThree.setId(annotIdThree);
      List<String> idsOfCoveredDiscThree = new ArrayList<>();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("15, 17");
      annotationFour.setId(annotIdFive);
      List<String> idsOfCoveredDiscFive = new ArrayList<>();
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);

      Map<String, Discrepancy> discrepanciesArray = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationThree, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFour, discrepanciesArray);

      List<String> idsOfCoveredDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.size(), 5);
      for (int i = 0; i < 5; i++) {
         assertEquals(idsOfCoveredDiscrepancies.get(i), idsForDiscrepancies1_5[i]);
      }

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveredDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.size(), 3);
      assertEquals(idsOfCoveredDiscrepancies.get(0), idsForDiscrepancies12_18[0]);
      assertEquals(idsOfCoveredDiscrepancies.get(1), idsForDiscrepancies12_18[1]);
      assertEquals(idsOfCoveredDiscrepancies.get(2), idsForDiscrepancies12_18[2]);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveredDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.size(), 3);
      assertEquals(idsOfCoveredDiscrepancies.get(0), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveredDiscrepancies.get(1), idsForDiscrepancies12_18[6]);
      assertEquals(idsOfCoveredDiscrepancies.get(2), id20);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveredDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.size(), 2);
      assertEquals(idsOfCoveredDiscrepancies.get(0), idsForDiscrepancies12_18[3]);
      assertEquals(idsOfCoveredDiscrepancies.get(1), idsForDiscrepancies12_18[5]);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedOneComplete() {
      List<DispoAnnotationData> annotationsAsList = new ArrayList<>();
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5, 12-18, 20");
      List<String> idsOfCoveredDisc = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("OTHER");
      annotationOne.setId(annotIdOne);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      annotationsAsList.add(annotationOne);
      dispoItem.setAnnotationsList(annotationsAsList);
      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedManyComplete() {
      List<DispoAnnotationData> annotationsAsList = new ArrayList<>();
      // Create 4 annotations, one for every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-5");
      annotationOne.setId(annotIdOne);
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("test");
      List<String> idsOfCoveredDiscOne = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);
      annotationsAsList.add(annotationOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-18");
      annotationTwo.setId(annotIdTwo);
      annotationTwo.setIsResolutionValid(true);
      annotationTwo.setResolutionType("Req");
      List<String> idsOfCoveredDiscTwo = new ArrayList<>();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);
      annotationsAsList.add(annotationTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("20");
      annotationThree.setId(annotIdThree);
      annotationThree.setIsResolutionValid(true);
      annotationThree.setResolutionType("Undetermined");
      List<String> idsOfCoveredDiscThree = new ArrayList<>();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);
      annotationsAsList.add(annotationThree);

      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      dispoItem.setAnnotationsList(annotationsList);

      Map<String, Discrepancy> discrepanciesList = dispoItem.getDiscrepanciesList();

      dispoConnector.connectAnnotation(annotationOne, discrepanciesList);
      annotationsList.add(0, annotationOne);
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.add(0, annotationOne);
      dispoItem.setAnnotationsList(annotationsList);
      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationTwo, discrepanciesList);
      annotationsList.add(1, annotationTwo);
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.add(1, annotationTwo);
      dispoItem.setAnnotationsList(annotationsList);
      actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationThree, discrepanciesList);
      annotationsList.add(2, annotationThree);
      dispoItem.setAnnotationsList(annotationsList);
      annotationsList.add(2, annotationThree);
      dispoItem.setAnnotationsList(annotationsList);
      actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testZComplexCase() {
      List<DispoAnnotationData> annotationsList = new ArrayList<>();
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("5, 1-3");
      annotationOne.setId(annotIdOne);
      annotationOne.setIsResolutionValid(true);
      annotationOne.setResolutionType("CODE");
      List<String> idsOfCoveredDiscOne = new ArrayList<>();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-15");
      annotationTwo.setId(annotIdTwo);
      annotationTwo.setIsResolutionValid(true);
      annotationTwo.setResolutionType("CODE");
      List<String> idsOfCoveredDiscTwo = new ArrayList<>();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 20");
      annotationThree.setId(annotIdThree);
      List<String> idsOfCoveredDiscThree = new ArrayList<>();
      annotationThree.setIsResolutionValid(true);
      annotationThree.setResolutionType("CODE");
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("4, 20");
      annotationFour.setId(annotIdFour);
      List<String> idsOfCoveredDiscFour = new ArrayList<>();
      annotationFour.setIsResolutionValid(true);
      annotationFour.setResolutionType("CODE");
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFour);

      DispoAnnotationData annotationFive = new DispoAnnotationData();
      annotationFive.setLocationRefs("18, 16, 17, 4");
      annotationFive.setId(annotIdFive);
      List<String> idsOfCoveredDiscFive = new ArrayList<>();
      annotationFive.setIsResolutionValid(true);
      annotationFive.setResolutionType("CODE");
      annotationFive.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);

      Map<String, Discrepancy> discrepanciesList = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesList);
      annotationsList.add(annotationOne);
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesList);
      annotationsList.add(annotationTwo);
      dispoConnector.connectAnnotation(annotationThree, discrepanciesList);
      annotationsList.add(annotationThree);
      dispoConnector.connectAnnotation(annotationFour, discrepanciesList);
      annotationsList.add(annotationFour);
      dispoConnector.connectAnnotation(annotationFive, discrepanciesList);
      annotationsList.add(annotationFive);

      dispoItem.setAnnotationsList(annotationsList);

      List<String> idsOfCoveringDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveringDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.size(), 4);
      assertEquals(idsOfCoveringDiscrepancies.get(0), idsForDiscrepancies1_5[0]);
      assertEquals(idsOfCoveringDiscrepancies.get(1), idsForDiscrepancies1_5[1]);
      assertEquals(idsOfCoveringDiscrepancies.get(2), idsForDiscrepancies1_5[2]);
      assertEquals(idsOfCoveringDiscrepancies.get(3), idsForDiscrepancies1_5[4]);

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveringDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.size(), 4);
      assertEquals(idsOfCoveringDiscrepancies.get(0), idsForDiscrepancies12_18[0]);
      assertEquals(idsOfCoveringDiscrepancies.get(1), idsForDiscrepancies12_18[1]);
      assertEquals(idsOfCoveringDiscrepancies.get(2), idsForDiscrepancies12_18[2]);
      assertEquals(idsOfCoveringDiscrepancies.get(3), idsForDiscrepancies12_18[3]);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveringDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.size(), 2);
      assertEquals(idsOfCoveringDiscrepancies.get(0), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveringDiscrepancies.get(1), id20);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.size(), 2);
      assertEquals(idsOfCoveringDiscrepancies.get(0), idsForDiscrepancies1_5[3]);
      assertEquals(idsOfCoveringDiscrepancies.get(1), id20);

      assertTrue(annotationFive.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFive.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.size(), 4);
      assertEquals(idsOfCoveringDiscrepancies.get(0), idsForDiscrepancies1_5[3]);
      assertEquals(idsOfCoveringDiscrepancies.get(1), idsForDiscrepancies12_18[4]);
      assertEquals(idsOfCoveringDiscrepancies.get(2), idsForDiscrepancies12_18[5]);
      assertEquals(idsOfCoveringDiscrepancies.get(3), idsForDiscrepancies12_18[6]);

      String actual = dispoConnector.getItemStatus(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }
}
