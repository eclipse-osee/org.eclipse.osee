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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

   public List<String> getAllUncoveredDiscprepancies(DispoItem item) {
      Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
      List<DispoAnnotationData> annotationsList = item.getAnnotationsList();
      HashSet<String> allCoveredDiscrepancies =
         getAllCoveredDiscrepanciesFromAnnotations(discrepancies, annotationsList);
      ArrayList<String> allDiscrepancies = createDiscrepanciesList(discrepancies);

      allDiscrepancies.removeAll(allCoveredDiscrepancies);
      return allDiscrepancies;
   }

   public String getItemStatus(DispoItem item) {
      String toReturn;
      List<DispoAnnotationData> annotations = item.getAnnotationsList();
      List<String> allUncoveredDiscprepancies = getAllUncoveredDiscprepancies(item);
      if (item.getDiscrepanciesList().size() == 0) {
         toReturn = DispoStrings.Item_Pass;
      } else {
         Collection<DispoAnnotationData> defaultAnnotations = new HashSet<>();
         Collection<DispoAnnotationData> invalidAnotations = new HashSet<>();
         Collection<DispoAnnotationData> analyzeAnnotations = new HashSet<>();
         parseThroughAnnotations(annotations, defaultAnnotations, invalidAnotations, analyzeAnnotations);

         if (invalidAnotations.isEmpty() && allUncoveredDiscprepancies.isEmpty()) {
            if (analyzeAnnotations.isEmpty()) {
               toReturn = DispoStrings.Item_Complete;
               if (anyAnnotationsModifyType(annotations)) {
                  toReturn = DispoStrings.Item_Complete_Analyzed;
               }
            } else {
               toReturn = DispoStrings.Item_Analyzed;
            }
         } else {
            toReturn = DispoStrings.Item_InComplete;
         }
      }
      return toReturn;
   }

   private void parseThroughAnnotations(Collection<DispoAnnotationData> annotations, Collection<DispoAnnotationData> defaultAnnotations, Collection<DispoAnnotationData> invalidAnnotations, Collection<DispoAnnotationData> analyzeAnnotations) {
      if (annotations != null) {
         for (DispoAnnotationData annotation : annotations) {
            if (annotation.getIsDefault()) {
               defaultAnnotations.add(annotation);
            }
            if (!annotation.isValid()) {
               invalidAnnotations.add(annotation);
            }
            if (annotation.getIsAnalyze()) {
               analyzeAnnotations.add(annotation);
            }
         }
      }
   }

   private boolean anyAnnotationsModifyType(List<DispoAnnotationData> annotations) {
      for (DispoAnnotationData annotation : annotations) {
         if (annotation.getResolutionMethodType() != null) {
            if (annotation.getResolutionMethodType().startsWith(DispoStrings.MODIFY)) {
               return true;
            }
         }
      }
      return false;
   }

   private ArrayList<String> createDiscrepanciesList(Map<String, Discrepancy> discrepancies) {
      ArrayList<String> toReturn = new ArrayList<>();
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         toReturn.add(discrepancy.getLocation());
      }

      return toReturn;
   }

   private HashSet<String> getAllCoveredDiscrepanciesFromAnnotations(Map<String, Discrepancy> discrepancies, List<DispoAnnotationData> annotations) {
      HashSet<String> toReturn = new HashSet<>();
      if (annotations != null) {
         for (DispoAnnotationData annotation : annotations) {
            List<String> idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
            for (String id : idsOfCoveredDiscrepancies) {
               if (discrepancies.containsKey(id)) {
                  Discrepancy discrepancy = discrepancies.get(id);
                  toReturn.add(discrepancy.getLocation());
               }
            }
         }
      }
      return toReturn;
   }

   public boolean connectAnnotation(DispoAnnotationData annotation, Map<String, Discrepancy> discrepanciesList) {
      if (DispoUtil.isNumericLocations(annotation.getLocationRefs())) {
         return connectNumberLocationRangeAnnotation(annotation, discrepanciesList);
      } else {
         return connectStringLocationRangeAnnotation(annotation, discrepanciesList);
      }
   }

   private boolean connectNumberLocationRangeAnnotation(DispoAnnotationData annotation, Map<String, Discrepancy> discrepanciesList) {
      boolean isAllLocRefValid = true;
      HashMap<String, String> testPointNumberToId = getPointNumbersToIds(discrepanciesList);
      List<LocationRange> listOfLocationRefs = sortList(annotation.getLocationRefs());
      List<String> workingIdsOfCovered = new ArrayList<>();

      for (LocationRange singleLocationRef : listOfLocationRefs) {
         if (singleLocationRef.getStart() != singleLocationRef.getEnd()) {
            for (int i = singleLocationRef.getStart(); i <= singleLocationRef.getEnd(); i++) {
               if (!tryToAddDiscrepancyForTestPointNumber(testPointNumberToId, i, workingIdsOfCovered)) {
                  isAllLocRefValid = false;
                  break;
               }
            }
         } else {
            if (!tryToAddDiscrepancyForTestPointNumber(testPointNumberToId, singleLocationRef.getStart(),
               workingIdsOfCovered)) {
               isAllLocRefValid = false;
               break;
            }
         }
      }

      // Do this every time, if nothing else will ensure Loc Refs are always ordered
      annotation.setLocationRefs(getLocRefsAsString(listOfLocationRefs));

      if (isAllLocRefValid) {
         annotation.setIsConnected(true);
         annotation.setIdsOfCoveredDiscrepancies(new ArrayList<String>(workingIdsOfCovered));
      } else {
         annotation.setIsConnected(false);
      }
      return isAllLocRefValid;
   }

   private boolean connectStringLocationRangeAnnotation(DispoAnnotationData annotation, Map<String, Discrepancy> discrepanciesList) {
      boolean isAllLocRefValid = true;
      HashMap<String, String> testPointIdentifierToId = getPointIdentifiersToIds(discrepanciesList);
      String locationRef = annotation.getLocationRefs();
      List<String> workingIdsOfCovered = new ArrayList<>();

      if (locationRef.contains(",")) {
         List<String> indvLocationRefs = Arrays.asList(locationRef.split(","));
         for (String indvLocationRef : indvLocationRefs) {
            if (!tryToAddDiscrepancyForStringLocationRef(testPointIdentifierToId, indvLocationRef.trim(),
               workingIdsOfCovered)) {
               isAllLocRefValid = false;
               break;
            }
         }
      } else {
         if (!tryToAddDiscrepancyForStringLocationRef(testPointIdentifierToId, locationRef.trim(),
            workingIdsOfCovered)) {
            isAllLocRefValid = false;
         }
      }

      if (isAllLocRefValid) {
         annotation.setIsConnected(true);
         annotation.setIdsOfCoveredDiscrepancies(new ArrayList<String>(workingIdsOfCovered));
      } else {
         annotation.setIsConnected(false);
      }
      return isAllLocRefValid;
   }

   private boolean tryToAddDiscrepancyForStringLocationRef(HashMap<String, String> locationToId, String locationRef, List<String> workingList) {
      String idOfMatched = locationToId.get(locationRef);
      if (idOfMatched == null) {
         return false;
      } else {
         workingList.add(idOfMatched);
      }
      return true;
   }

   private boolean tryToAddDiscrepancyForTestPointNumber(HashMap<String, String> testPointLocationToId, int testPoint, List<String> workingList) {
      String idOfMatched = testPointLocationToId.get(String.valueOf(testPoint));
      if (idOfMatched == null) {
         return false;
      } else {
         workingList.add(idOfMatched);
      }

      return true;
   }

   private HashMap<String, String> getPointIdentifiersToIds(Map<String, Discrepancy> discrepancies) {
      HashMap<String, String> toReturn = new HashMap<>();
      for (Discrepancy discrepancy : discrepancies.values()) {
         toReturn.put(discrepancy.getLocation().trim(), discrepancy.getId());
      }
      return toReturn;
   }

   private HashMap<String, String> getPointNumbersToIds(Map<String, Discrepancy> discrepancies) {
      HashMap<String, String> toReturn = new HashMap<>();
      Set<String> keys = discrepancies.keySet();
      for (String key : keys) {
         Discrepancy discrepancy = discrepancies.get(key);
         String pointNumber = discrepancy.getLocation();
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
      Stack<LocationRange> locationRefsStack = new Stack<>();
      locationRefsStack.addAll(allLocationRefs);
      List<LocationRange> allUncovered = new ArrayList<>();

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
      List<LocationRange> toReturn = new ArrayList<>();
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