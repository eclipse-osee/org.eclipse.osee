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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.LocationRange;
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

   int idZero = 0;
   int idOne = 1;
   int idTwo = 2;
   int idThree = 3;

   String annotIdOne = "id1";
   String annotIdTwo = "id2";
   String annotIdThree = "id3";
   String annotIdFour = "id4";
   String annotIdFive = "id5";

   @Before
   public void setUp() {
      //      MockitoAnnotations.initMocks(this);
      dispoItem = new DispoItemData();
      dispoConnector = new DispoConnector();

      List<JSONObject> discrepancies = new ArrayList<JSONObject>();

      Discrepancy discrepancyOne = new Discrepancy();
      LocationRange rangeOne = new LocationRange(1, 10);
      discrepancyOne.setLocationRange(rangeOne);
      discrepancyOne.setId(idZero);
      discrepancyOne.setIdsOfCoveringAnnotations(new JSONArray());
      discrepancies.add(discrepancyToJsonObj(discrepancyOne));

      Discrepancy discrepancyTwo = new Discrepancy();
      LocationRange rangeTwo = new LocationRange(12, 20);
      discrepancyTwo.setLocationRange(rangeTwo);
      discrepancyTwo.setId(idOne);
      discrepancyTwo.setIdsOfCoveringAnnotations(new JSONArray());
      discrepancies.add(discrepancyToJsonObj(discrepancyTwo));

      Discrepancy discrepancyThree = new Discrepancy();
      LocationRange rangeThree = new LocationRange(23);
      discrepancyThree.setLocationRange(rangeThree);
      discrepancyThree.setId(idTwo);
      discrepancyThree.setIdsOfCoveringAnnotations(new JSONArray());
      discrepancies.add(discrepancyToJsonObj(discrepancyThree));

      Discrepancy discrepancyFour = new Discrepancy();
      LocationRange rangeFour = new LocationRange(25);
      discrepancyFour.setLocationRange(rangeFour);
      discrepancyFour.setId(idThree);
      discrepancyFour.setIdsOfCoveringAnnotations(new JSONArray());
      discrepancies.add(discrepancyToJsonObj(discrepancyFour));

      JSONArray discrepanciesAsArray = new JSONArray(discrepancies);
      dispoItem.setDiscrepanciesList(discrepanciesAsArray);
   }

   @Test
   public void testConnectAnnotationsSingleCompelete() throws JSONException {
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10, 12-20, 23, 25");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());

      // annotation 1 should be connected to all Discrepancies
      JSONArray idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.getInt(0), idZero);
      assertEquals(idsOfCoveredDiscrepancies.getInt(1), idOne);
      assertEquals(idsOfCoveredDiscrepancies.getInt(2), idTwo);
      assertEquals(idsOfCoveredDiscrepancies.getInt(3), idThree);
      assertTrue(annotationOne.getIsConnected());

      // Each discrepancy should only be connected to the one annotation
      JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
      JSONObject jsonObject = discrepanciesList.getJSONObject(0);
      JSONArray jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);
   }

   @Test
   public void testConnectAnnotationsSingleIncomplete() throws JSONException {
      // This will test a single annotation that covers most but not all discrepancies
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10, 12-20, 23");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());

      // annotation 1 should be connected to all Discrepancies but idThree which is '25'
      JSONArray idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveredDiscrepancies.getInt(0), idZero);
      assertEquals(idsOfCoveredDiscrepancies.getInt(1), idOne);
      assertEquals(idsOfCoveredDiscrepancies.getInt(2), idTwo);
      assertTrue(annotationOne.getIsConnected());

      // Each discrepancy should only be connected to the one annotation except the last one 
      JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
      JSONObject jsonObject = discrepanciesList.getJSONObject(0);
      JSONArray jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 0);
   }

   @Test
   public void testConnectAnnotationsMultipleComplete() throws JSONException {
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-15");
      annotationTwo.setId(annotIdTwo);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 25, 23, 18");
      annotationThree.setId(annotIdThree);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("17-19, 25");
      annotationFour.setId(annotIdFour);
      JSONArray idsOfCoveredDiscFour = new JSONArray();
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFour);

      DispoAnnotationData annotationFive = new DispoAnnotationData();
      annotationFive.setLocationRefs("23, 20");
      annotationFive.setId(annotIdFive);
      JSONArray idsOfCoveredDiscFive = new JSONArray();
      annotationFive.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);

      JSONArray discrepanciesArray = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationThree, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFour, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFive, discrepanciesArray);

      JSONArray idsOfCoveringDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveringDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idZero);

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveringDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveringDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 3);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idTwo);
      assertEquals(idsOfCoveringDiscrepancies.getInt(2), idThree);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idThree);

      assertTrue(annotationFive.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFive.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idTwo);

      // Test Discrepancies
      JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
      JSONObject jsonObject = discrepanciesList.getJSONObject(0);
      JSONArray jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 1);
      assertEquals(jsonArray.getString(0), annotIdOne);

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 4);
      assertEquals(jsonArray.getString(0), annotIdTwo);
      assertEquals(jsonArray.getString(1), annotIdThree);
      assertEquals(jsonArray.getString(2), annotIdFour);
      assertEquals(jsonArray.getString(3), annotIdFive);

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdThree);
      assertEquals(jsonArray.getString(1), annotIdFive);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdThree);
      assertEquals(jsonArray.getString(1), annotIdFour);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedOneComplete() {
      Map<String, JSONObject> annotationsAsMap = new HashMap<String, JSONObject>();
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10, 12-20, 23, 25");
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);
      annotationOne.setId(annotIdOne);
      annotationsAsMap.put(annotIdOne, annotationToJsonObj(annotationOne));

      JSONObject annotations = new JSONObject(annotationsAsMap);
      dispoItem.setAnnotationsList(annotations);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testAllDiscrepanciesAnnotatedManyComplete() {
      Map<String, JSONObject> annotationsAsMap = new HashMap<String, JSONObject>();
      // Create 4 annotations, one for every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);
      //      annotationsAsList.add(annotationToJsonObj(annotationOne));
      annotationsAsMap.put(annotIdOne, annotationToJsonObj(annotationOne));

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-20");
      annotationTwo.setId(annotIdTwo);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);
      //      annotationsAsList.add(annotationToJsonObj(annotationTwo));
      annotationsAsMap.put(annotIdTwo, annotationToJsonObj(annotationTwo));

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("23, 25");
      annotationThree.setId(annotIdThree);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);
      //      annotationsAsList.add(annotationToJsonObj(annotationThree));
      annotationsAsMap.put(annotIdThree, annotationToJsonObj(annotationThree));

      JSONObject annotationsAsArray = new JSONObject(annotationsAsMap);
      dispoItem.setAnnotationsList(annotationsAsArray);

      JSONArray discrepanciesArray = dispoItem.getDiscrepanciesList();

      dispoConnector.connectAnnotation(annotationOne, discrepanciesArray);
      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationTwo, discrepanciesArray);
      actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_InComplete, actual);

      dispoConnector.connectAnnotation(annotationThree, discrepanciesArray);
      actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }

   @Test
   public void testDisconnect() throws JSONException {
      // Create one annotation with every discrepancy covered
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("12-20, 23, 25");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDisc = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDisc);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("1-10, 23");
      annotationTwo.setId(annotIdTwo);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);

      dispoConnector.connectAnnotation(annotationOne, dispoItem.getDiscrepanciesList());
      dispoConnector.connectAnnotation(annotationTwo, dispoItem.getDiscrepanciesList());

      // annotation 1 should be connected to 3 Discrepancies
      JSONArray idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(3, idsOfCoveredDiscrepancies.length());
      assertEquals(idsOfCoveredDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveredDiscrepancies.getInt(1), idTwo);
      assertEquals(idsOfCoveredDiscrepancies.getInt(2), idThree);
      assertTrue(annotationOne.getIsConnected());

      // annotation 2 should be connected to discrepancy idZero
      JSONArray idsOfCoveredDiscrepanciesTwo = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(2, idsOfCoveredDiscrepanciesTwo.length());
      assertEquals(idsOfCoveredDiscrepanciesTwo.getInt(0), idZero);
      assertEquals(idsOfCoveredDiscrepanciesTwo.getInt(1), idTwo);
      assertTrue(annotationTwo.getIsConnected());

      JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
      JSONObject jsonObject = discrepanciesList.getJSONObject(0);
      JSONArray jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdTwo);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.getString(1), annotIdTwo);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.getString(0), annotIdOne);
      assertEquals(jsonArray.length(), 1);

      // Disconect the annotaiton
      dispoConnector.disconnectAnnotation(annotationOne, discrepanciesList);

      idsOfCoveredDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(0, idsOfCoveredDiscrepancies.length());
      assertFalse(annotationOne.getIsConnected());

      //      discrepanciesList = dispoItem.getDiscrepanciesList();

      jsonObject = discrepanciesList.getJSONObject(0);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(1, jsonArray.length());

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(0, jsonArray.length());

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(1, jsonArray.length());

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(0, jsonArray.length());
   }

   @Test
   public void testZComplexCase() throws JSONException {
      Map<String, JSONObject> annotationsAsMap = new HashMap<String, JSONObject>();
      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("10, 1-9");
      annotationOne.setId(annotIdOne);
      JSONArray idsOfCoveredDiscOne = new JSONArray();
      annotationOne.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscOne);
      annotationsAsMap.put(annotIdOne, annotationToJsonObj(annotationOne));

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-15");
      annotationTwo.setId(annotIdTwo);
      JSONArray idsOfCoveredDiscTwo = new JSONArray();
      annotationTwo.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscTwo);
      annotationsAsMap.put(annotIdTwo, annotationToJsonObj(annotationTwo));

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("16, 25, 23, 18, 20");
      annotationThree.setId(annotIdThree);
      JSONArray idsOfCoveredDiscThree = new JSONArray();
      annotationThree.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscThree);
      annotationsAsMap.put(annotIdThree, annotationToJsonObj(annotationThree));

      DispoAnnotationData annotationFour = new DispoAnnotationData();
      annotationFour.setLocationRefs("17-19, 25");
      annotationFour.setId(annotIdFour);
      JSONArray idsOfCoveredDiscFour = new JSONArray();
      annotationFour.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFour);
      annotationsAsMap.put(annotIdFour, annotationToJsonObj(annotationFour));

      DispoAnnotationData annotationFive = new DispoAnnotationData();
      annotationFive.setLocationRefs("23, 20");
      annotationFive.setId(annotIdFive);
      JSONArray idsOfCoveredDiscFive = new JSONArray();
      annotationFive.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscFive);
      annotationsAsMap.put(annotIdFive, annotationToJsonObj(annotationFive));

      JSONArray discrepanciesArray = dispoItem.getDiscrepanciesList();
      dispoConnector.connectAnnotation(annotationOne, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationTwo, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationThree, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFour, discrepanciesArray);
      dispoConnector.connectAnnotation(annotationFive, discrepanciesArray);

      JSONObject annotationsAsArray = new JSONObject(annotationsAsMap);
      dispoItem.setAnnotationsList(annotationsAsArray);

      JSONArray idsOfCoveringDiscrepancies;

      assertTrue(annotationOne.getIsConnected());
      idsOfCoveringDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idZero);

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveringDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveringDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 3);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idTwo);
      assertEquals(idsOfCoveringDiscrepancies.getInt(2), idThree);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idThree);

      assertTrue(annotationFive.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFive.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idTwo);

      // Test Discrepancies
      JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
      JSONObject jsonObject = discrepanciesList.getJSONObject(0);
      JSONArray jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 1);
      assertEquals(jsonArray.getString(0), annotIdOne);

      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 4);
      assertEquals(jsonArray.getString(0), annotIdTwo);
      assertEquals(jsonArray.getString(1), annotIdThree);
      assertEquals(jsonArray.getString(2), annotIdFour);
      assertEquals(jsonArray.getString(3), annotIdFive);

      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdThree);
      assertEquals(jsonArray.getString(1), annotIdFive);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdThree);
      assertEquals(jsonArray.getString(1), annotIdFour);

      String actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);

      // Disconnect Annotation Five.  All discrep. should still be covered since annotationThree has the locRefs that annotationFive had
      dispoConnector.disconnectAnnotation(annotationFive, discrepanciesList);
      assertTrue(annotationOne.getIsConnected());
      idsOfCoveringDiscrepancies = annotationOne.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idZero);

      assertTrue(annotationTwo.getIsConnected());
      idsOfCoveringDiscrepancies = annotationTwo.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 1);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);

      assertTrue(annotationThree.getIsConnected());
      idsOfCoveringDiscrepancies = annotationThree.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 3);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idTwo);
      assertEquals(idsOfCoveringDiscrepancies.getInt(2), idThree);

      assertTrue(annotationFour.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFour.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 2);
      assertEquals(idsOfCoveringDiscrepancies.getInt(0), idOne);
      assertEquals(idsOfCoveringDiscrepancies.getInt(1), idThree);

      // disconnected
      assertFalse(annotationFive.getIsConnected());
      idsOfCoveringDiscrepancies = annotationFive.getIdsOfCoveredDiscrepancies();
      assertEquals(idsOfCoveringDiscrepancies.length(), 0);

      // Discrepancies
      discrepanciesList = dispoItem.getDiscrepanciesList();
      jsonObject = discrepanciesList.getJSONObject(0);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 1);
      assertEquals(jsonArray.getString(0), annotIdOne);

      // Size is now just 3
      jsonObject = discrepanciesList.getJSONObject(1);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 3);
      assertEquals(jsonArray.getString(0), annotIdTwo);
      assertEquals(jsonArray.getString(1), annotIdThree);
      assertEquals(jsonArray.getString(2), annotIdFour);

      // size is now just 1
      jsonObject = discrepanciesList.getJSONObject(2);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 1);
      assertEquals(jsonArray.getString(0), annotIdThree);

      jsonObject = discrepanciesList.getJSONObject(3);
      jsonArray = jsonObject.getJSONArray("idsOfCoveringAnnotations");
      assertEquals(jsonArray.length(), 2);
      assertEquals(jsonArray.getString(0), annotIdThree);
      assertEquals(jsonArray.getString(1), annotIdFour);

      // Should still be complete
      actual = dispoConnector.allDiscrepanciesAnnotated(dispoItem);
      assertEquals(DispoStrings.Item_Complete, actual);
   }
}
