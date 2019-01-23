/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import static org.eclipse.osee.disposition.model.DispoSummarySeverity.ERROR;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.WARNING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public class CoverageAdapter {

   private final DispoConnector dispoConnector;
   private static String PROPERTY_STORE_ID = "coverage.item";

   public CoverageAdapter(DispoConnector dispoConnector) {
      this.dispoConnector = dispoConnector;
   }

   public List<DispoItem> copyData(Map<String, ArtifactReadable> nameToCoverageUnit, List<DispoItem> dispoItems, OperationReport report) {
      Map<String, DispoItem> nameToDispoItem = getNameToDispoItemMap(dispoItems);

      List<DispoItem> modifiedItems = new ArrayList<>();

      for (Entry<String, ArtifactReadable> entry : nameToCoverageUnit.entrySet()) {
         DispoItem dispoItem = nameToDispoItem.get(entry.getKey());

         if (dispoItem != null) {
            List<DispoAnnotationData> newAnnotations = copyCoverageData(entry.getValue(), dispoItem, report);
            if (!newAnnotations.isEmpty()) {
               DispoItemData newItem = new DispoItemData();
               newItem.setName(dispoItem.getName());
               newItem.setGuid(dispoItem.getGuid());
               newItem.setAnnotationsList(newAnnotations);

               String newStatus;
               newItem.setDiscrepanciesList(dispoItem.getDiscrepanciesList());
               newStatus = dispoConnector.getItemStatus(newItem);
               newItem.setStatus(newStatus);

               modifiedItems.add(newItem);
            }
         }

      }
      return modifiedItems;
   }

   private List<DispoAnnotationData> copyCoverageData(ArtifactReadable source, DispoItem dest, OperationReport report) {
      boolean madeChange = false;
      List<String> covearageItems = source.getAttributeValues(CoverageUtil.Item);
      Map<String, Discrepancy> textToDiscrepancyMap = getTextToDiscrepancyMap(dest);
      List<DispoAnnotationData> annotations = dest.getAnnotationsList();

      PropertyStore store = new PropertyStore();

      for (String covearageItem : covearageItems) {
         try {
            store.load(covearageItem);
         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
         if (!store.getId().equals(PROPERTY_STORE_ID)) {
            report.addEntry("Property Store", String.format("Invalid store id [%s] for CoverageItem", store.getId()),
               ERROR);
         }

         String textFromCoverage = store.get("name").trim();
         String lineNumberFromCoverage = store.get("order");
         String resolutionFromCoverage = store.get("methodType").trim();
         String rationale = store.get("rationale").trim();
         if (!Strings.isValid(rationale)) {
            rationale = "N/A";
         }

         Discrepancy matchedDiscrepancy = textToDiscrepancyMap.get(textFromCoverage);
         if (!resolutionFromCoverage.equalsIgnoreCase("Test_Unit") && !resolutionFromCoverage.equalsIgnoreCase(
            "Exception_Handling") && matchedDiscrepancy != null) {
            madeChange = true;
            boolean isReplace = false;
            DispoAnnotationData annotationToUpdate = findAnnotation(matchedDiscrepancy.getText(), annotations);
            if (annotationToUpdate == null) {
               annotationToUpdate = new DispoAnnotationData();
               annotationToUpdate.setId(GUID.create());
               annotationToUpdate.setIndex(annotations.size());
               annotationToUpdate.setLocationRefs(lineNumberFromCoverage);
               annotationToUpdate.setCustomerNotes(textFromCoverage);
            }

            if (!annotationToUpdate.getResolutionType().equals(
               DispoStrings.Test_Unit_Resolution) && !annotationToUpdate.getResolutionType().equals(
                  DispoStrings.Exception_Handling_Resolution)) {

               annotationToUpdate.setIsDefault(false);
               annotationToUpdate.setResolutionType(resolutionFromCoverage);
               annotationToUpdate.setIsResolutionValid(true);
               annotationToUpdate.setResolution(rationale);
               annotationToUpdate.setLastResolution("N/A");
               annotationToUpdate.setDeveloperNotes("");
               dispoConnector.connectAnnotation(annotationToUpdate, dest.getDiscrepanciesList());

               if (isReplace) {
                  annotations.set(annotationToUpdate.getIndex(), annotationToUpdate);
               } else {
                  annotations.add(annotationToUpdate.getIndex(), annotationToUpdate);
               }
            }
         } else if (matchedDiscrepancy == null) {
            report.addEntry(source.getName(),
               String.format("Could not find matching Discrepancy for [%s]", covearageItem), WARNING);
         }
      }

      if (madeChange) {
         return annotations;
      } else {
         return Collections.emptyList();
      }
   }

   private DispoAnnotationData findAnnotation(String text, List<DispoAnnotationData> annotations) {
      for (DispoAnnotationData annotation : annotations) {
         if (annotation.getCustomerNotes().equals(text)) {
            return annotation;
         }
      }
      return null;
   }

   private Map<String, Discrepancy> getTextToDiscrepancyMap(DispoItem dest) {
      Map<String, Discrepancy> toReturn = new HashMap<>();

      Map<String, Discrepancy> discrepanciesList = dest.getDiscrepanciesList();
      for (String key : discrepanciesList.keySet()) {
         Discrepancy discrepancy;
         discrepancy = discrepanciesList.get(key);
         toReturn.put(discrepancy.getText(), discrepancy);
      }

      return toReturn;
   }

   private Map<String, DispoItem> getNameToDispoItemMap(List<DispoItem> items) {
      Map<String, DispoItem> toReturn = new HashMap<>();
      for (DispoItem item : items) {
         toReturn.put(item.getName(), item);
      }
      return toReturn;
   }
}
