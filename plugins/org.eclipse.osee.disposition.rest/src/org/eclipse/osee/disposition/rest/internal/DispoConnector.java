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
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.LocationRange;
import org.eclipse.osee.disposition.rest.util.DiscrepancyComperator;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.disposition.rest.util.LocationRangeComparator;
import org.eclipse.osee.disposition.rest.util.LocationRangeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   public String allDiscrepanciesAnnotated(DispoItem item) {
      String toReturn;
      JSONArray discrepancies = item.getDiscrepanciesList();
      JSONObject annotations = item.getAnnotationsList();

      int discrepanciesSize = discrepancies.length();
      if (discrepanciesSize == 0) {
         toReturn = DispoStrings.Item_Pass;
      } else {
         boolean allDiscrepanciesCovered = true; // Assume everything is covered, on first false we break and return false
         for (int i = 0; i < discrepanciesSize; i++) {
            try {
               JSONObject discrepancyObject = discrepancies.getJSONObject(i);
               Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyObject);
               List<LocationRange> locRefsAsSortedList = getAllLocRefsAsSortedList(annotations, discrepancy);
               if (!LocationRangeUtil.isCovered(discrepancy.getLocationRange(), locRefsAsSortedList)) {
                  allDiscrepanciesCovered = false;
                  // We found a discrepancy that hasn't been fully covered
                  break;
               }
            } catch (JSONException ex) {
               throw new OseeCoreException(ex);
            }
         }

         if (allDiscrepanciesCovered) {
            toReturn = DispoStrings.Item_Complete;
         } else {
            toReturn = DispoStrings.Item_InComplete;
         }
      }

      return toReturn;
   }

   public boolean connectAnnotation(DispoAnnotationData annotation, JSONArray discrepanciesArray) {
      boolean isAllLocRefValid = true;
      List<Discrepancy> sortedDiscrepanciesList = arrayToSortedList(discrepanciesArray);
      List<LocationRange> listOfLocationRefs = sortList(annotation.getLocationRefs());
      int startIndexForNextMatch = 0;
      int idOfPreviousMatched = -1;

      for (LocationRange singleLocationRef : listOfLocationRefs) {
         try {
            Discrepancy matchedDiscrepancy =
               matchToDiscrepancy(annotation, singleLocationRef, sortedDiscrepanciesList, startIndexForNextMatch);
            if (matchedDiscrepancy == null) {
               isAllLocRefValid = false;
               break;
            } else {
               int idOfMatched = matchedDiscrepancy.getId();
               // only update discrepancy and annotation if we found a different matching discrepancy
               if (idOfPreviousMatched != idOfMatched) {
                  idOfPreviousMatched = idOfMatched;
                  startIndexForNextMatch = idOfMatched;
                  matchedDiscrepancy.addCoveringAnnotation(annotation);
                  JSONObject discrepancyAsObject = DispoUtil.discrepancyToJsonObj(matchedDiscrepancy);
                  annotation.addCoveredDiscrepancyIndex(matchedDiscrepancy);
                  discrepanciesArray.put(idOfMatched, discrepancyAsObject);
                  //on next search start at the previously matched one since both discrepancies and annotations are in order
               }
            }
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }

      annotation.setLocationRefs(getLocRefsAsString(listOfLocationRefs));
      annotation.setIsConnected(isAllLocRefValid);
      return isAllLocRefValid;
   }

   public void disconnectAnnotation(DispoAnnotationData annotation, JSONArray discrepanciesList) throws JSONException {
      JSONArray discrepanciesConnected = annotation.getIdsOfCoveredDiscrepancies();
      int size = discrepanciesConnected.length();
      for (int i = 0; i < size; i++) {
         int indexOfDiscrepancy = discrepanciesConnected.getInt(i); // the discrepancy to remove the annotation from, index in discrepanciesList
         JSONObject discrepancyAsObject = discrepanciesList.getJSONObject(indexOfDiscrepancy);
         Discrepancy discrepancyAsData = DispoUtil.jsonObjToDiscrepancy(discrepancyAsObject);
         JSONArray indexesOfAnnotations = discrepancyAsData.getIdsOfCoveringAnnotations();
         removeElementFromArray(indexesOfAnnotations, annotation.getId());
      }
      // clear list of connected discrepancies for this annotation
      annotation.setIdsOfCoveredDiscrepancies(new JSONArray());
      annotation.setIsConnected(false);
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

   private List<LocationRange> getAllLocRefsAsSortedList(JSONObject annotations, Discrepancy discrepancy) {
      StringBuilder sb = new StringBuilder();
      JSONArray indexesOfCoveringAnnotations = discrepancy.getIdsOfCoveringAnnotations();
      int size = indexesOfCoveringAnnotations.length();
      for (int i = 0; i < size; i++) {
         try {
            if (sb.length() > 0) {
               sb.append(",");
            }
            String annotationId = indexesOfCoveringAnnotations.getString(i);
            JSONObject annotationAsJsonObj = annotations.getJSONObject(annotationId);
            DispoAnnotationData annotationAsData = DispoUtil.jsonObjToDispoAnnotationData(annotationAsJsonObj);
            sb.append(annotationAsData.getLocationRefs());
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }

      return sortList(sb.toString());

   }

   private void removeElementFromArray(JSONArray listToRemoveFrom, String element) throws JSONException {
      int size = listToRemoveFrom.length();
      List<Integer> indexOfElementsToRemove = new ArrayList<Integer>();
      for (int i = 0; i < size; i++) {
         if (listToRemoveFrom.getString(i).equals(element)) {
            indexOfElementsToRemove.add(i);
         }
      }

      for (Integer index : indexOfElementsToRemove) {
         listToRemoveFrom.remove(index);
      }
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

   private List<Discrepancy> arrayToSortedList(JSONArray array) {
      List<Discrepancy> discrepanciesAsList = new ArrayList<Discrepancy>();
      int size = array.length();
      for (int i = 0; i < size; i++) {
         try {
            discrepanciesAsList.add(DispoUtil.jsonObjToDiscrepancy(array.getJSONObject(i)));
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      Collections.sort(discrepanciesAsList, new DiscrepancyComperator());
      return discrepanciesAsList;
   }

   private Discrepancy matchToDiscrepancy(DispoAnnotationData annotation, LocationRange singleLocationRangeRef, List<Discrepancy> discrepanciesList, int startIndex) {
      Discrepancy matchedDiscrepancy = null;
      int sizeOfList = discrepanciesList.size();
      int firstUnCovered = singleLocationRangeRef.getStart();

      for (int i = startIndex; i < sizeOfList; i++) {
         // want to guarantee that the discrepancy we're starting at has starting index <= to the first uncovered part of the location ref
         Discrepancy discrepancy = discrepanciesList.get(i);
         if (discrepancy.getLocationRange().getStart() > firstUnCovered) {
            break;
         } else {
            if (LocationRangeUtil.isLocRefWithinRange(discrepancy.getLocationRange(), singleLocationRangeRef)) {
               matchedDiscrepancy = discrepancy;
               break;
            }
         }
      }

      return matchedDiscrepancy;
   }
}