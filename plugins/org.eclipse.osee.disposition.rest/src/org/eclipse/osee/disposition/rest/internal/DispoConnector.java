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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.LocationRange;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.disposition.rest.util.LocationRangeComparator;
import org.eclipse.osee.disposition.rest.util.LocationRangeUtil;
import org.eclipse.osee.logger.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

public class DispoConnector {

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start() {
      logger.trace("Starting DispoConnector...");
   }

   public void stop() {
      logger.trace("Stopping DispoConnector...");
   }

   public List<Integer> getAllUncoveredDiscprepancies(DispoItem item) throws JSONException {
      JSONObject discrepancies = item.getDiscrepanciesList();
      JSONArray annotations = item.getAnnotationsList();
      HashSet<Integer> allCoveredDiscrepancies = getAllCoveredDiscrepanciesFromAnnotations(discrepancies, annotations);
      ArrayList<Integer> allDiscrepancies = createDiscrepanciesList(discrepancies);

      allDiscrepancies.removeAll(allCoveredDiscrepancies);
      return allDiscrepancies;
   }

   public String allDiscrepanciesAnnotated(DispoItem item) throws JSONException {
      String toReturn;
      JSONArray annotations = item.getAnnotationsList();
      List<Integer> allUncoveredDiscprepancies = getAllUncoveredDiscprepancies(item);

      if (item.getDiscrepanciesList().length() == 0) {
         toReturn = DispoStrings.Item_Pass;
      } else if (allAnnotationsValid(annotations) && allUncoveredDiscprepancies.isEmpty()) {
         toReturn = DispoStrings.Item_Complete;
      } else {
         toReturn = DispoStrings.Item_InComplete;
      }

      return toReturn;
   }

   private boolean allAnnotationsValid(JSONArray annotatinos) throws JSONException {
      int length = annotatinos.length();
      for (int i = 0; i < length; i++) {
         JSONObject annotationAsJson = annotatinos.getJSONObject(i);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationAsJson);
         if (!annotation.isValid()) {
            return false;
         }
      }

      return true;
   }

   @SuppressWarnings("unchecked")
   private ArrayList<Integer> createDiscrepanciesList(JSONObject discrepancies) throws JSONException {
      ArrayList<Integer> toReturn = new ArrayList<Integer>();
      Iterator<String> iterator = discrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = discrepancies.getJSONObject(key);
         Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         toReturn.add(discrepancy.getLocation());
      }

      return toReturn;
   }

   private HashSet<Integer> getAllCoveredDiscrepanciesFromAnnotations(JSONObject discrepancies, JSONArray annotations) throws JSONException {
      HashSet<Integer> toReturn = new HashSet<Integer>();
      int length = annotations.length();
      for (int j = 0; j < length; j++) {
         JSONObject annotationAsObject = annotations.getJSONObject(j);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationAsObject);
         JSONArray idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
         for (int i = 0; i < idsOfCoveredDiscrepancies.length(); i++) {
            String id = idsOfCoveredDiscrepancies.getString(i);
            if (discrepancies.has(id)) {
               JSONObject discrepancyAsJson = discrepancies.getJSONObject(id);
               Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
               toReturn.add(discrepancy.getLocation());
            }
         }
      }

      return toReturn;
   }

   public boolean connectAnnotation(DispoAnnotationData annotation, JSONObject discrepanciesList) throws JSONException {
      boolean isAllLocRefValid = true;
      HashMap<Integer, String> testPointNumberToId = getPointNumbersToIds(discrepanciesList);
      List<LocationRange> listOfLocationRefs = sortList(annotation.getLocationRefs());
      List<String> workingIdsOfCovered = new ArrayList<String>();

      for (LocationRange singleLocationRef : listOfLocationRefs) {
         if (singleLocationRef.getStart() != singleLocationRef.getEnd()) {
            for (int i = singleLocationRef.getStart(); i <= singleLocationRef.getEnd(); i++) {
               if (!tryToAddDiscrepancyForTestPoint(testPointNumberToId, i, workingIdsOfCovered)) {
                  isAllLocRefValid = false;
                  break;
               }
            }
         } else {
            if (!tryToAddDiscrepancyForTestPoint(testPointNumberToId, singleLocationRef.getStart(), workingIdsOfCovered)) {
               isAllLocRefValid = false;
               break;
            }
         }
      }

      // Do this every time, if nothing else will ensure Loc Refs are always ordered
      annotation.setLocationRefs(getLocRefsAsString(listOfLocationRefs));

      if (isAllLocRefValid) {
         annotation.setIsConnected(true);
         annotation.setIdsOfCoveredDiscrepancies(new JSONArray(workingIdsOfCovered));
      } else {
         annotation.setIsConnected(false);
      }
      return isAllLocRefValid;
   }

   private boolean tryToAddDiscrepancyForTestPoint(HashMap<Integer, String> testPointNumberToId, int testPoint, List<String> workingList) {
      String idOfMatched = testPointNumberToId.get(testPoint);
      if (idOfMatched == null) {
         return false;
      } else {
         workingList.add(idOfMatched);
      }

      return true;
   }

   @SuppressWarnings("unchecked")
   private HashMap<Integer, String> getPointNumbersToIds(JSONObject discrepancies) throws JSONException {
      HashMap<Integer, String> toReturn = new HashMap<Integer, String>();
      Iterator<String> iterator = discrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = discrepancies.getJSONObject(key);
         Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         int pointNumber = discrepancy.getLocation();
         toReturn.put(pointNumber, discrepancy.getId());
      }

      return toReturn;
   }

   // Not currently used.  May implement if users want to see what discrepancies are left to cover
   public List<LocationRange> getAllUncovered(LocationRange toBeCovered, List<LocationRange> allLocationRefs) {
      int startIndex = toBeCovered.getStart();
      int endIndex = toBeCovered.getEnd();
      int firstUncoveredIndex = startIndex;

      Collections.reverse(allLocationRefs);
      Stack<LocationRange> locationRefsStack = new Stack<LocationRange>();
      locationRefsStack.addAll(allLocationRefs);
      List<LocationRange> allUncovered = new ArrayList<LocationRange>();

      while (locationRefsStack.size() > 0) {
         LocationRange locRef = locationRefsStack.pop();
         if (locRef.getEnd() < startIndex) {
            // go to next 
         } else {
            if (locRef.getStart() == firstUncoveredIndex) {
               if (locRef.getEnd() == endIndex) {
                  //all done
                  firstUncoveredIndex = -1;
                  break;
               } else {
                  // we have the start right but didn't finish the set, will try to find in next ref
                  firstUncoveredIndex = locRef.getEnd() + 1;
               }
            } else {
               // the first uncovered is not the same as the next loc ref's start so we have an uncovered int
               // now find out how many are uncovered until the next location ref starts if there is one
               if (locationRefsStack.size() > 0) {
                  LocationRange nextLocRef = locationRefsStack.peek();
                  LocationRange uncoveredRange = new LocationRange(firstUncoveredIndex, nextLocRef.getStart() - 1);
                  allUncovered.add(uncoveredRange);
               }
            }
         }

      }

      if (firstUncoveredIndex != -1) {
         LocationRange uncoveredRange = new LocationRange(firstUncoveredIndex, endIndex);
         allUncovered.add(uncoveredRange);
      }

      return allUncovered;
   }

   private static String getLocRefsAsString(List<LocationRange> list) {
      StringBuilder sb = new StringBuilder();
      for (LocationRange range : list) {
         if (sb.length() > 0) {
            sb.append(",");
         }
         sb.append(range.toString());
      }

      return sb.toString();
   }

   private List<LocationRange> sortList(String allLocationRefsString) {
      allLocationRefsString = allLocationRefsString.replaceAll("\\s*", "");
      List<LocationRange> toReturn = new ArrayList<LocationRange>();
      StringTokenizer tokenizer = new StringTokenizer(allLocationRefsString, ",");
      while (tokenizer.hasMoreTokens()) {
         String singleLocationRefString = tokenizer.nextToken();
         LocationRange range = LocationRangeUtil.parseLocation(singleLocationRefString);
         toReturn.add(range);
      }

      Collections.sort(toReturn, new LocationRangeComparator());

      return toReturn;
   }

}