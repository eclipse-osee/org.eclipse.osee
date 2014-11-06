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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class AnnotationCopier {

   private final DispoConnector connector;

   public AnnotationCopier(DispoConnector connector) {
      this.connector = connector;
   }

   public List<DispoItem> copyEntireSet(List<DispoItemData> destinationItems, Collection<DispoItem> sourceItems, boolean isCopySet) throws JSONException {
      List<DispoItem> modifiedItems = new ArrayList<DispoItem>();

      HashMap<String, DispoItemData> nameToDestItems = createNameToItemList(destinationItems);
      for (DispoItem sourceItem : sourceItems) {
         DispoItemData destItem = nameToDestItems.get(sourceItem.getName());
         if (destItem != null) {

            /**
             * If item is PASS don't bother copying over Annotations from Source Item, all annotations are Default
             * Annotations and already created in the Import
             */
            if (!destItem.getStatus().equals(DispoStrings.Item_Pass)) {
               DispoItemData newItem = createNewItemWithCopiedAnnotations(destItem, sourceItem, isCopySet);
               if (newItem != null) {
                  if (!Strings.isValid(destItem.getGuid())) {
                     newItem.setGuid(sourceItem.getGuid());
                  } else {
                     newItem.setGuid(destItem.getGuid());
                  }
                  modifiedItems.add(newItem);
               }
            } else if (!Strings.isValid(destItem.getGuid()) && !sourceItem.getStatus().equals(DispoStrings.Item_Pass)) {
               destItem.setGuid(sourceItem.getGuid());
               modifiedItems.add(destItem);
            }

         }
      }
      return modifiedItems;
   }

   private DispoItemData createNewItemWithCopiedAnnotations(DispoItemData destItem, DispoItem sourceItem, boolean isCopySet) throws JSONException {
      DispoItemData toReturn;
      boolean isSameDiscrepancies = matchAllDiscrepancies(destItem, sourceItem);
      if (isSameDiscrepancies) {
         toReturn = buildNewItem(destItem, sourceItem, isCopySet);
      } else {
         toReturn = null;
         //log in report
      }

      return toReturn;
   }

   private DispoItemData buildNewItem(DispoItemData destItem, DispoItem sourceItem, boolean isSkipDestDefaultAnnotations) throws JSONException {
      boolean isChangesMade = false;
      DispoItemData newItem = new DispoItemData();
      newItem.setDiscrepanciesList(destItem.getDiscrepanciesList());
      JSONArray newList = new JSONArray(destItem.getAnnotationsList().toString());
      newItem.setAnnotationsList(newList);

      JSONArray newAnnotations = newItem.getAnnotationsList();
      JSONArray sourceAnnotations = sourceItem.getAnnotationsList();

      Set<String> destDefaultAnntationLocations = getDefaultAnnotations(newItem);

      for (int i = 0; i < sourceAnnotations.length(); i++) {
         JSONObject annotationJson = sourceAnnotations.getJSONObject(i);
         DispoAnnotationData sourceAnnotation = DispoUtil.jsonObjToDispoAnnotationData(annotationJson);

         String sourceLocation = sourceAnnotation.getLocationRefs();

         if (DispoUtil.isDefaultAnntoation(sourceAnnotation)) {
            /**
             * This means the source has an annotation that's TEST_UNIT or Exception_Handling, so don't copy it over, we
             * might leave an uncovered discrepancy which is intended do nothing, maybe log
             */
         } else if (isSkipDestDefaultAnnotations && destDefaultAnntationLocations.contains(sourceLocation)) {
            /**
             * isSkipDestDefault is true when annotation copier is called by a copy set, this means we do not want to
             * copy over source annotations that have the same location as a Dest annotation that's already covered by a
             * Default Annotation This means the destination has an annotation that's TEST_UNIT or Exception_Handling,
             * so don't copy over a manual Disposition do nothing, maybe log
             */
         } else if (newAnnotations.toString().contains(sourceAnnotation.getGuid())) {
            //  The Destination already has this annotation
         } else {
            DispoAnnotationData newAnnotation = sourceAnnotation;

            /**
             * The discrepancy of this manual disposition is now covered by a Default Annotation so this Manual
             * Annotation is invalid mark as such by making the location Ref negative, don't bother connecting the
             * annotation
             */
            if (destDefaultAnntationLocations.contains(sourceLocation)) {
               // Make location ref negative to indicate this 
               String locationRefs = sourceAnnotation.getLocationRefs();
               Integer locationRefAsInt = Integer.valueOf(locationRefs);
               if (locationRefAsInt > 0) {
                  newAnnotation.setLocationRefs(String.valueOf(locationRefAsInt * -1));
               }
            }
            connector.connectAnnotation(newAnnotation, newItem.getDiscrepanciesList());
            isChangesMade = true;
            // Both the source and destination are dispositionable so copy the annotation
            int nextIndex = newAnnotations.length();
            newAnnotation.setIndex(nextIndex);
            newAnnotations.put(nextIndex, DispoUtil.annotationToJsonObj(newAnnotation));
         }
      }

      if (isChangesMade) {
         newItem.setAnnotationsList(newAnnotations);
         String newStatus = connector.getItemStatus(newItem);
         newItem.setStatus(newStatus);
      } else {
         newItem = null;
      }
      return newItem;
   }

   private Set<String> getDefaultAnnotations(DispoItemData item) throws JSONException {
      Set<String> defaultAnnotationLocations = new HashSet<String>();
      JSONArray annotations = item.getAnnotationsList();
      if (annotations == null) {
         annotations = new JSONArray();
      }
      for (int i = 0; i < annotations.length(); i++) {
         JSONObject annotationJson = annotations.getJSONObject(i);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationJson);
         if (DispoUtil.isDefaultAnntoation(annotation)) {
            defaultAnnotationLocations.add(annotation.getLocationRefs());
         }
      }

      return defaultAnnotationLocations;
   }

   private HashMap<String, DispoItemData> createNameToItemList(List<DispoItemData> destinationItems) {
      HashMap<String, DispoItemData> nameToItem = new HashMap<String, DispoItemData>();
      for (DispoItemData item : destinationItems) {
         nameToItem.put(item.getName(), item);
      }
      return nameToItem;
   }

   private boolean matchAllDiscrepancies(DispoItemData destItem, DispoItem sourceItem) throws JSONException {
      Map<Integer, String> destLocationToText = generateLocationToTextMap(destItem);
      boolean toReturn = true;

      JSONObject sourceDiscrepancies = sourceItem.getDiscrepanciesList();
      @SuppressWarnings("unchecked")
      Iterator<String> iterator = sourceDiscrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = sourceDiscrepancies.getJSONObject(key);
         Discrepancy sourceDiscrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);

         int sourceLocation = sourceDiscrepancy.getLocation();
         String destDicrepancyText = destLocationToText.get(sourceLocation);
         if (destDicrepancyText == null) {
            // No Discrepancy with that location in the destination item, return false
            toReturn = false;
            break;
         } else if (sourceDiscrepancy.getText().equals(destDicrepancyText)) {
            continue;
         } else {
            toReturn = false;
            break;
         }

      }
      return toReturn;
   }

   private Map<Integer, String> generateLocationToTextMap(DispoItem item) throws JSONException {
      Map<Integer, String> locationToText = new HashMap<Integer, String>();
      JSONObject discrepancies = item.getDiscrepanciesList();
      @SuppressWarnings("unchecked")
      Iterator<String> iterator = discrepancies.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = discrepancies.getJSONObject(key);
         Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);
         locationToText.put(discrepancy.getLocation(), discrepancy.getText());
      }

      return locationToText;
   }
}
