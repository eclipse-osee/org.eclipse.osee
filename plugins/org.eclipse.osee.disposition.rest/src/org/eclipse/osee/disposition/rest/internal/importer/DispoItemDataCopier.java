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
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Angel Avila
 */

public class DispoItemDataCopier {
   public static void copyOldItemData(DispoItem sourceItem, DispoItemData destItem, OperationReport report) {
      StringBuilder message = new StringBuilder();

      Map<String, Discrepancy> destItemDiscrepancies = destItem.getDiscrepanciesList();
      List<DispoAnnotationData> sourceAnnotations = sourceItem.getAnnotationsList();
      Boolean needsReview = false;
      HashMap<String, String> idsToUpdate = matchupOldDiscrepancies(sourceItem.getDiscrepanciesList(),
         destItemDiscrepancies, sourceAnnotations, message, needsReview);
      updateTestPointNumbersForAnntations(idsToUpdate, sourceAnnotations, destItemDiscrepancies, message);
      destItem.setAnnotationsList(sourceAnnotations);

      report.addEntry(destItem.getName(), message.toString(), DispoSummarySeverity.UPDATE);
   }

   private static void updateTestPointNumbersForAnntations(HashMap<String, String> idsToUpdate, List<DispoAnnotationData> annotations, Map<String, Discrepancy> discrepancies, StringBuilder message) {
      for (DispoAnnotationData annotation : annotations) {
         List<String> idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
         updateIdsCoveredDiscrepancies(idsToUpdate, idsOfCoveredDiscrepancies, annotations, discrepancies, annotation,
            message);
      }
   }

   private static void updateIdsCoveredDiscrepancies(HashMap<String, String> idsToUpdate, List<String> idsOfCoveredDiscrepancies, List<DispoAnnotationData> annotations, Map<String, Discrepancy> discrepancies, DispoAnnotationData annotation, StringBuilder message) {
      for (String coveredId : idsOfCoveredDiscrepancies) {
         if (idsToUpdate.containsKey(coveredId)) {
            String newLocRef = rebuildLocRef(idsOfCoveredDiscrepancies, discrepancies, idsToUpdate, message);
            if (!newLocRef.isEmpty()) {
               annotation.setLocationRefs(newLocRef);
            }
            annotations.set(annotation.getIndex(), annotation);
            break; // We can break here because we're going reconstruct the whole locRefs for the annotation, no need to keep checking
         }
      }
   }

   private static String rebuildLocRef(List<String> idsOfCoveredDiscrepancies, Map<String, Discrepancy> discrepancies, HashMap<String, String> idsToUpdate, StringBuilder message) {
      boolean isGaveup = false;
      List<Integer> testPointNumber = new ArrayList<>();
      for (String id : idsOfCoveredDiscrepancies) {
         if (discrepancies.containsKey(id)) {
            Discrepancy discrepancy = discrepancies.get(id);
            String location = discrepancy.getLocation();
            testPointNumber.add(Integer.parseInt(location));
         } else if (idsToUpdate.containsKey(id)) {
            String justTestPoint = idsToUpdate.get(id);
            testPointNumber.add(Integer.parseInt(justTestPoint)); // Made this locationRef negative to convey to User it was valid at one point but on reimport it was invalid
         } else {
            message.append("Something went wrong with trying to rebuild the Annotations");
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

   private static HashMap<String, String> matchupOldDiscrepancies(Map<String, Discrepancy> oldDiscrepancies, Map<String, Discrepancy> newDiscrepancies, List<DispoAnnotationData> annotations, StringBuilder message, Boolean needsReview) {
      HashMap<String, Pair<Discrepancy, Boolean>> textToNewDiscrepancies =
         createTextToDiscrepanciesMap(newDiscrepancies);
      HashMap<String, String> idsToUpdate = new HashMap<>();

      for (String key : oldDiscrepancies.keySet()) {
         Discrepancy oldDiscrepany = oldDiscrepancies.get(key);
         String normalizedText = oldDiscrepany.getText().replaceFirst(".*?\\.", "");

         Pair<Discrepancy, Boolean> matchedPair = textToNewDiscrepancies.get(normalizedText);
         String oldTestPointLocation = oldDiscrepany.getLocation();

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
            String newTestPointLocation = matchedNewDiscrepancy.getLocation();
            if (!oldTestPointLocation.equals(newTestPointLocation)) {
               message.append("Test Point:");
               message.append(oldTestPointLocation);
               message.append(" is now:");
               message.append(newTestPointLocation);
               message.append(" ");
               idsToUpdate.put(idOfOldDiscrep, newTestPointLocation);
            }

            newDiscrepancies.remove(idToReplace);
            newDiscrepancies.put(idOfOldDiscrep, matchedNewDiscrepancy);
         } else {
            // This discrepancy doesn't exist on the new Item
            message.append("Test Point:");
            message.append(oldTestPointLocation);
            message.append(" No longer fails ");
            String outdatedLocation = "-" + oldTestPointLocation;
            idsToUpdate.put(DispoStrings.DeletedDiscrepancy + outdatedLocation, outdatedLocation);
            removeDiscrepancyFromAnnotation(oldDiscrepany, annotations);

         }
      }
      return idsToUpdate;
   }

   private static void removeDiscrepancyFromAnnotation(Discrepancy toRemove, List<DispoAnnotationData> annotations) {
      for (DispoAnnotationData annotation : annotations) {
         List<String> idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
         if (idsOfCoveredDiscrepancies.toString().contains(toRemove.getId())) {
            replaceIdInList(toRemove, idsOfCoveredDiscrepancies);
            annotation.setIsConnected(false);
         }

         annotations.set(annotation.getIndex(), annotation);
      }
   }

   private static void replaceIdInList(Discrepancy discrepany, List<String> idsList) {
      String id = discrepany.getId();
      for (int i = 0; i < idsList.size(); i++) {
         if (id.equals(idsList.get(i))) {
            String testPoint = "-" + discrepany.getLocation();
            String newFakeId = DispoStrings.DeletedDiscrepancy + testPoint;
            idsList.add(i, newFakeId);
            break;
         }
      }
   }

   private static HashMap<String, Pair<Discrepancy, Boolean>> createTextToDiscrepanciesMap(Map<String, Discrepancy> discrepancies) {
      HashMap<String, Pair<Discrepancy, Boolean>> textToDiscrepancy = new HashMap<>();
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         String normalizedText = discrepancy.getText().replaceFirst(".*?\\.", ""); // Want to exclude Point number from text we match with

         Pair<Discrepancy, Boolean> newPair = new Pair<>(discrepancy, textToDiscrepancy.containsKey(normalizedText));
         textToDiscrepancy.put(normalizedText, newPair);
      }

      return textToDiscrepancy;
   }

}
