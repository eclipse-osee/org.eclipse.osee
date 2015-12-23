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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONException;
import org.json.JSONObject;

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
               newItem.setAnnotationsList(DispoUtil.listAsJsonArray(newAnnotations));

               String newStatus;
               try {
                  newItem.setDiscrepanciesList(dispoItem.getDiscrepanciesList());
                  newStatus = dispoConnector.getItemStatus(newItem);
                  newItem.setStatus(newStatus);
               } catch (JSONException ex) {
                  report.addMessageForItem(newItem.getName(), "Could not determine Status");
               }

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
      List<DispoAnnotationData> annotations = DispoUtil.asAnnotationsList(dest.getAnnotationsList());

      PropertyStore store = new PropertyStore();

      for (String covearageItem : covearageItems) {
         try {
            store.load(covearageItem);
         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
         if (!store.getId().equals(PROPERTY_STORE_ID)) {
            report.addOtherMessage("Invalid store id [%s] for CoverageItem", store.getId());
         }

         String textFromCoverage = store.get("name").trim();
         String lineNumberFromCoverage = store.get("order");
         String resolutionFromCoverage = store.get("methodType").trim();

         Discrepancy matchedDiscrepancy = textToDiscrepancyMap.get(textFromCoverage);
         if (!resolutionFromCoverage.equalsIgnoreCase("Test_Unit") && !resolutionFromCoverage.equalsIgnoreCase(
            "Exception_Handling") && matchedDiscrepancy != null) {
            madeChange = true;
            // Add Annotation
            DispoAnnotationData newAnnotation = new DispoAnnotationData();
            newAnnotation.setId(GUID.create());
            newAnnotation.setIsDefault(false);
            newAnnotation.setResolutionType(resolutionFromCoverage);
            newAnnotation.setIsResolutionValid(true);
            newAnnotation.setIndex(annotations.size());
            newAnnotation.setLocationRefs(lineNumberFromCoverage);
            newAnnotation.setCustomerNotes(textFromCoverage);
            newAnnotation.setResolution("n/a");
            newAnnotation.setDeveloperNotes("");
            try {
               dispoConnector.connectAnnotation(newAnnotation, dest.getDiscrepanciesList());
            } catch (JSONException ex) {
               throw new OseeCoreException(ex);
            }

            annotations.add(newAnnotation.getIndex(), newAnnotation);
         } else if (matchedDiscrepancy == null) {
            report.addMessageForItem(source.getName(), "Could not find matching Discrepancy for [%s]", covearageItem);
         }
      }

      if (madeChange) {
         return annotations;
      } else {
         return Collections.emptyList();
      }
   }

   private Map<String, Discrepancy> getTextToDiscrepancyMap(DispoItem dest) {
      Map<String, Discrepancy> toReturn = new HashMap<>();

      JSONObject discrepanciesList = dest.getDiscrepanciesList();
      @SuppressWarnings("rawtypes")
      Iterator keys = discrepanciesList.keys();
      while (keys.hasNext()) {
         String key = (String) keys.next();
         Discrepancy discrepancy;
         try {
            discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepanciesList.getJSONObject(key));
            toReturn.put(discrepancy.getText(), discrepancy);
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
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
