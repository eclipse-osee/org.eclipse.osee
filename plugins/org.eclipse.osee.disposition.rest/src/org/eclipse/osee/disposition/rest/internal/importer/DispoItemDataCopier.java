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
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

public class DispoItemDataCopier {
   public static void copyOldItemData(DispoItem sourceItem, DispoItemData destItem, OperationReport report) throws JSONException {
      StringBuilder message = new StringBuilder();

      JSONObject destItemDiscrepancies = destItem.getDiscrepanciesList();
      JSONArray sourceAnnotations = sourceItem.getAnnotationsList();
      Boolean needsReview = false;
      HashMap<String, Integer> idsToUpdate =
         matchupOldDiscrepancies(sourceItem.getDiscrepanciesList(), destItemDiscrepancies, sourceAnnotations, message,
            needsReview);
      updateTestPointNumbersForAnntations(idsToUpdate, sourceAnnotations, destItemDiscrepancies, message);
      destItem.setAnnotationsList(sourceAnnotations);

      report.addMessageForItem(destItem.getName(), message.toString());
   }

   private static void updateTestPointNumbersForAnntations(HashMap<String, Integer> idsToUpdate, JSONArray annotations, JSONObject discrepancies, StringBuilder message) throws JSONException {
      for (int j = 0; j < annotations.length(); j++) {
         JSONObject annotationAsJson = annotations.getJSONObject(j);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationAsJson);
         JSONArray idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();

         updateIdsCoveredDiscrepancies(idsToUpdate, idsOfCoveredDiscrepancies, annotations, discrepancies, annotation,
            message);
      }
   }

   private static void updateIdsCoveredDiscrepancies(HashMap<String, Integer> idsToUpdate, JSONArray idsOfCoveredDiscrepancies, JSONArray annotations, JSONObject discrepancies, DispoAnnotationData annotation, StringBuilder message) throws JSONException {
      int length = idsOfCoveredDiscrepancies.length();
      for (int i = 0; i < length; i++) {
         String coveredId = idsOfCoveredDiscrepancies.getString(i);
         if (idsToUpdate.containsKey(coveredId)) {
            String newLocRef = rebuildLocRef(idsOfCoveredDiscrepancies, discrepancies, idsToUpdate, message);
            if (!newLocRef.isEmpty()) {
               annotation.setLocationRefs(newLocRef);
            }
            JSONObject updatedAnnotationAsJson = DispoUtil.annotationToJsonObj(annotation);
            annotations.put(annotation.getIndex(), updatedAnnotationAsJson);
            break; // We can break here because we're going reconstruct the whole locRefs for the annotation, no need to keep checking
         }
      }
   }

   private static String rebuildLocRef(JSONArray idsOfCoveredDiscrepancies, JSONObject discrepancies, HashMap<String, Integer> idsToUpdate, StringBuilder message) throws JSONException {
      boolean isGaveup = false;
      int length = idsOfCoveredDiscrepancies.length();
      List<Integer> testPointNumber = new ArrayList<>();
      for (int i = 0; i < length; i++) {
         String id = idsOfCoveredDiscrepancies.getString(i);
         if (discrepancies.has(id)) {
            JSONObject discrepancyAsObject = discrepancies.getJSONObject(id);
            Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsObject);
            testPointNumber.add(discrepancy.getLocation());
         } else if (idsToUpdate.containsKey(id)) {
            int justTestPoint = idsToUpdate.get(id);
            testPointNumber.add(justTestPoint); // Made this locationRef negative to convey to User it was valid at one point but on reimport it was invalid
         } else {
            message.append("Something when wrong with trying to rebuild the Annotations");
            isGaveup = true;
            break;
         }

      }

      Collections.sort(testPointNumber);
      String toReturn;
      if (isGaveup) {
         toReturn = "";
      } else {
         toReturn = LocationRangesCompressor.compress(testPointNumber);
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   private static HashMap<String, Integer> matchupOldDiscrepancies(JSONObject oldDiscrepancies, JSONObject newDiscrepancies, JSONArray annotations, StringBuilder message, Boolean needsReview) throws JSONException {
      HashMap<String, Pair<Discrepancy, Boolean>> textToNewDiscrepancies =
         createTextToDiscrepanciesMap(newDiscrepancies);
      HashMap<String, Integer> idsToUpdate = new HashMap<>();

      Iterator<String> iterator = oldDiscrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject oldDiscrepancyAsJson = oldDiscrepancies.getJSONObject(key);
         Discrepancy oldDiscrepany = DispoUtil.jsonObjToDiscrepancy(oldDiscrepancyAsJson);
         String normalizedText = oldDiscrepany.getText().replaceFirst(".*?\\.", "");

         Pair<Discrepancy, Boolean> matchedPair = textToNewDiscrepancies.get(normalizedText);
         int oldTestPointNumber = oldDiscrepany.getLocation();

         /**
          * If there's a matching Discrepany try to transfer the id from the old discrepancy to the new one since
          * annotations have the old ID reference in 'idsOfCoveredDiscrepancies' field
          */
         if (matchedPair != null) {
            Discrepancy matchedNewDiscrepancy = matchedPair.getFirst();

            if (matchedPair.getSecond()) {
               // This discrepancy has text that's replicated so log as needs 
               needsReview = true;
            }
            String idToReplace = matchedNewDiscrepancy.getId();
            String idOfOldDiscrep = oldDiscrepany.getId();
            matchedNewDiscrepancy.setId(idOfOldDiscrep);

            // Now compare test points and see if they changed
            int newTestPointNumber = matchedNewDiscrepancy.getLocation();
            if (oldTestPointNumber != newTestPointNumber) {
               message.append("Test Point:");
               message.append(oldTestPointNumber);
               message.append("is now:");
               message.append(newTestPointNumber);
               idsToUpdate.put(idOfOldDiscrep, newTestPointNumber);
            }

            JSONObject matchedNewDiscrepAsJson = DispoUtil.discrepancyToJsonObj(matchedNewDiscrepancy);
            newDiscrepancies.remove(idToReplace);
            newDiscrepancies.put(idOfOldDiscrep, matchedNewDiscrepAsJson);
         } else {
            // This discrepancy doesn't exist on the new Item
            message.append("Test Point:");
            message.append(oldTestPointNumber);
            message.append("No longer fails");
            int outdateNumber = oldTestPointNumber * -1;
            idsToUpdate.put(DispoStrings.DeletedDiscrepancy + outdateNumber, outdateNumber);
            removeDiscrepancyFromAnnotation(oldDiscrepany, annotations);

         }
      }
      return idsToUpdate;
   }

   private static void removeDiscrepancyFromAnnotation(Discrepancy toRemove, JSONArray annotations) throws JSONException {
      for (int i = 0; i < annotations.length(); i++) {
         JSONObject annotationAsJson = annotations.getJSONObject(i);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationAsJson);
         JSONArray idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
         if (idsOfCoveredDiscrepancies.toString().contains(toRemove.getId())) {
            replaceIdInList(toRemove, idsOfCoveredDiscrepancies);
            annotation.setIsConnected(false);
         }

         JSONObject updatedAnnotationAsJson = DispoUtil.annotationToJsonObj(annotation);
         annotations.put(annotation.getIndex(), updatedAnnotationAsJson);
      }
   }

   private static void replaceIdInList(Discrepancy discrepany, JSONArray idsList) throws JSONException {
      int length = idsList.length();
      String id = discrepany.getId();
      for (int i = 0; i < length; i++) {
         if (id.equals(idsList.getString(i))) {
            int testPoint = discrepany.getLocation() * -1;
            String newMockId = DispoStrings.DeletedDiscrepancy + testPoint;
            idsList.put(i, newMockId);
            break;
         }
      }
   }

   @SuppressWarnings("unchecked")
   private static HashMap<String, Pair<Discrepancy, Boolean>> createTextToDiscrepanciesMap(JSONObject discrepancies) throws JSONException {
      HashMap<String, Pair<Discrepancy, Boolean>> textToDiscrepancy = new HashMap<>();
      Iterator<String> iterator = discrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsObject = discrepancies.getJSONObject(key);
         Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsObject);
         String normalizedText = discrepancy.getText().replaceFirst(".*?\\.", ""); // Want to exclude Point number from text we match with

         Pair newPair = new Pair<>(discrepancy, textToDiscrepancy.containsKey(normalizedText));
         textToDiscrepancy.put(normalizedText, newPair);
      }

      return textToDiscrepancy;
   }

}
