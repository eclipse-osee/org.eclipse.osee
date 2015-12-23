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
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.CopySetParamOption;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class DispoSetCopier {

   private final DispoConnector connector;

   public DispoSetCopier(DispoConnector connector) {
      this.connector = connector;
   }

   public List<DispoItem> copyAllDispositions(Map<String, DispoItemData> nameToDestItems, Collection<DispoItem> sourceItems, boolean isCoverageCopy, OperationReport report) {
      List<DispoItem> modifiedItems = new ArrayList<>();

      // Iterate through every source item since we want to try to find a match for every item in the source
      for (DispoItem sourceItem : sourceItems) {
         DispoItemData destItem = nameToDestItems.get(sourceItem.getName());
         if (destItem != null) {
            // Only try to copy over annotations if matching dest item is NOT PASS
            if (!destItem.getStatus().equals(DispoStrings.Item_Pass)) {
               DispoItemData newItem = createNewItemWithCopiedAnnotations(destItem, sourceItem, isCoverageCopy, report);
               if (newItem != null) {
                  modifiedItems.add(newItem);

                  JSONArray destAnnotationsList = destItem.getAnnotationsList();
                  report.addMessageForItem(destItem.getName(), "$$$$Had %s Dispositions$$$$\n",
                     destAnnotationsList.length());
                  report.addMessageForItem(destItem.getName(), "$$$$Now has %s Dispositions$$$$",
                     newItem.getAnnotationsList().length());
               }
            } else if (!Strings.isValid(destItem.getGuid()) && !sourceItem.getStatus().equals(DispoStrings.Item_Pass)) {
               /**
                * In the case of Coverage, the destination Item is the item created by a new import so we assign it the
                * id of the source so that it will overwrite the source date with the new import data
                */
               destItem.setGuid(sourceItem.getGuid());
               modifiedItems.add(destItem);
            }

         } else {
            report.addMessageForItem(sourceItem.getName(), "No matching item found in the Destination Set");
         }
      }
      return modifiedItems;
   }

   private DispoItemData createNewItemWithCopiedAnnotations(DispoItemData destItem, DispoItem sourceItem, boolean isCoverageCopy, OperationReport report) {
      DispoItemData toReturn = null;

      try {
         boolean isSameDiscrepancies = matchAllDiscrepancies(destItem, sourceItem);
         if (isSameDiscrepancies || isCoverageCopy) {
            toReturn = buildNewItem(destItem, sourceItem, isCoverageCopy, report, isSameDiscrepancies);
         } else {
            report.addMessageForItem(destItem.getName(),
               "Tried to copy from item id: [%s] but discrepancies were not the same", sourceItem.getGuid());
         }
      } catch (JSONException ex) {
         report.addOtherMessage("Item[%s] has bad JSON. Exception Message:[%s]", sourceItem.getName(), ex.getMessage());
      }

      return toReturn;
   }

   private DispoItemData buildNewItem(DispoItemData destItem, DispoItem sourceItem, boolean isCoverageCopy, OperationReport report, boolean isSameDiscrepancies) throws JSONException {
      boolean isChangesMade = false;
      DispoItemData newItem = initNewItem(destItem, sourceItem);
      JSONArray newAnnotations = newItem.getAnnotationsList();
      JSONArray sourceAnnotations = sourceItem.getAnnotationsList();
      Set<String> destDefaultAnntationLocations = getDefaultAnnotations(newItem);

      for (int i = 0; i < sourceAnnotations.length(); i++) {
         JSONObject annotationJson = sourceAnnotations.getJSONObject(i);
         DispoAnnotationData sourceAnnotation = DispoUtil.jsonObjToDispoAnnotationData(annotationJson);

         String sourceLocation = sourceAnnotation.getLocationRefs();

         // Check for ignore cases
         if (DispoUtil.isDefaultAnntoation(sourceAnnotation)) {
            /**
             * This means this annotation is TEST_UNIT or Exception_Handling, so don't copy it over, only log if the
             * destination item doesn't have this annotation as a Default i.e means something changed user should be
             * aware.Currently only for Coverage
             */
            if (!destDefaultAnntationLocations.contains(sourceLocation)) {
               report.addMessageForItem(destItem.getName(),
                  "Did not copy annotations for location(s) [%s] because they are default annotations",
                  sourceAnnotation.getLocationRefs());
            }
         } else if (isCoverageCopy && destDefaultAnntationLocations.contains(sourceLocation)) {
            /**
             * isCoverageCopy is true when annotation copier is called by a coverage import, this means we need to also
             * check that the matching dest annotation isn't a DEFAULT resolution before copying over.
             */
            report.addMessageForItem(destItem.getName(),
               "Did not copy annotations for location(s) [%s] because the destination item already has a default annotations at these locations",
               sourceAnnotation.getLocationRefs());
         } else if (newAnnotations.toString().contains(sourceAnnotation.getGuid())) {
            report.addMessageForItem(destItem.getName(),
               "Did not copy annotations for location(s) [%s] because the destination item already has this Annotation [%s]",
               sourceAnnotation.getLocationRefs(), sourceAnnotation.getGuid());
         } else {
            // Try to copy but check if Discrepancy is the same
            if (isSameDiscrepancies || isCoverageCopy && isCoveredDiscrepanciesExistInDest(destItem, sourceItem,
               sourceAnnotation, report)) {
               DispoAnnotationData newAnnotation = sourceAnnotation;
               if (destDefaultAnntationLocations.contains(sourceLocation)) {
                  /**
                   * The discrepancy of this manual disposition is now covered by a Default Annotation so this Manual
                   * Annotation is invalid, mark as such by making the location Ref negative, don't bother connecting
                   * the annotation
                   */
                  // Make location ref negative to indicate this
                  String locationRefs = sourceAnnotation.getLocationRefs();
                  Integer locationRefAsInt = Integer.valueOf(locationRefs);
                  if (locationRefAsInt > 0) {
                     newAnnotation.setLocationRefs(String.valueOf(locationRefAsInt * -1));
                  }
                  report.addMessageForItem(destItem.getName(),
                     "The annotation was copied over but is no longer needed: [%s]", locationRefs);
               }
               connector.connectAnnotation(newAnnotation, newItem.getDiscrepanciesList());
               isChangesMade = true;
               // Both the source and destination are dispositionable so copy the annotation
               int nextIndex = newAnnotations.length();
               newAnnotation.setIndex(nextIndex);
               newAnnotations.put(nextIndex, DispoUtil.annotationToJsonObj(newAnnotation));
            }
         }
      }

      if (isChangesMade) {
         newItem.setAnnotationsList(newAnnotations);
         String newStatus = connector.getItemStatus(newItem);
         newItem.setStatus(newStatus);
      } else if (isCoverageCopy) {
         // We want to take the new import version of this item even though no changes were made, this will only occur if
         // 1. All the Annotations from the source Item were default, in which case we can ignore those and take the ones from the new import
         // 2. None of the non-default Annotations cover a Discrepancy in the new import, in which case don't copy them over: MIGHT CHANGE THIS
         newItem = destItem;
         newItem.setGuid(sourceItem.getGuid());
      } else {
         report.addMessageForItem(destItem.getName(), "Nothing to copy");
         newItem = null;
      }
      return newItem;
   }

   private boolean isCoveredDiscrepanciesExistInDest(DispoItem destItem, DispoItem sourceItem, DispoAnnotationData annotation, OperationReport report) {
      JSONArray idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
      List<String> destDescrepanciesTextOnly =
         discrepanciesTextOnly(destItem.getDiscrepanciesList(), report, destItem.getName());
      JSONObject sourceDiscrepancies = sourceItem.getDiscrepanciesList();
      try {
         for (int i = 0; i < idsOfCoveredDiscrepancies.length(); i++) {
            String key = idsOfCoveredDiscrepancies.getString(i);
            Discrepancy coveredDiscrepancy = DispoUtil.jsonObjToDiscrepancy(sourceDiscrepancies.getJSONObject(key));
            if (!destDescrepanciesTextOnly.contains(coveredDiscrepancy.getText())) {
               return false;
            }
         }
      } catch (JSONException ex) {
         report.addMessageForItem(sourceItem.getName(), "Bad JSON!");
         return false;
      }

      return true;
   }

   private List<String> discrepanciesTextOnly(JSONObject discrepancies, OperationReport report, String itemName) {
      List<String> toReturn = new ArrayList<>();
      @SuppressWarnings("rawtypes")
      Iterator keys = discrepancies.keys();
      while (keys.hasNext()) {
         String key = (String) keys.next();
         try {
            Discrepancy discrepancy = DispoUtil.jsonObjToDiscrepancy(discrepancies.getJSONObject(key));
            toReturn.add(discrepancy.getText());
         } catch (JSONException ex) {
            report.addMessageForItem(itemName, "Bad JSON!");
         }
      }

      return toReturn;
   }

   private DispoItemData initNewItem(DispoItemData destItem, DispoItem sourceItem) throws JSONException {
      DispoItemData newItem = new DispoItemData();
      newItem.setDiscrepanciesList(destItem.getDiscrepanciesList());
      JSONArray newList = new JSONArray(destItem.getAnnotationsList().toString());
      newItem.setAnnotationsList(newList);
      if (Strings.isValid(destItem.getGuid())) {
         newItem.setGuid(destItem.getGuid());
      } else {
         newItem.setGuid(sourceItem.getGuid());
      }
      newItem.setName(destItem.getName());
      return newItem;
   }

   private Set<String> getDefaultAnnotations(DispoItemData item) throws JSONException {
      Set<String> defaultAnnotationLocations = new HashSet<>();
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
      Map<Integer, String> locationToText = new HashMap<>();
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

   public void copyCategories(Map<String, DispoItemData> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = destinationItems.get(sourceItem.getName());

         if (destItem != null) {
            String currentCategory = destItem.getCategory();
            String sourceCategory = sourceItem.getCategory();
            String newCategory;

            if (Strings.isValid(sourceCategory)) {
               switch (option) {
                  case OVERRIDE:
                     newCategory = sourceCategory;
                     break;
                  case OVERRIDE_EMPTY:
                     if (!Strings.isValid(currentCategory)) {
                        newCategory = sourceCategory;
                     } else {
                        newCategory = currentCategory;
                     }
                     break;
                  case MERGE:
                     if (!Strings.isValid(currentCategory)) {
                        newCategory = sourceCategory;
                     } else {
                        newCategory = currentCategory + "::" + sourceCategory;
                     }
                     break;
                  case NONE:
                  default:
                     newCategory = currentCategory;
                     break;
               }

               // Check to see if this item is already set to be edited
               DispoItem matchingToEdit = toEdit.get(sourceItem.getName());
               if (matchingToEdit != null) {
                  ((DispoItemData) matchingToEdit).setCategory(newCategory);
               } else {
                  DispoItemData newToEdit = new DispoItemData();
                  newToEdit.setGuid(destItem.getGuid());
                  newToEdit.setName(destItem.getName());
                  newToEdit.setCategory(newCategory);
                  toEdit.put(newToEdit.getName(), newToEdit);
               }
            }
         }
      }
   }

   public void copyAssignee(Map<String, DispoItemData> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = destinationItems.get(sourceItem.getName());

         if (destItem != null) {
            String currentAssignee = destItem.getAssignee();
            String sourceAssignee = sourceItem.getAssignee();
            String newAssignee;

            if (!sourceAssignee.equalsIgnoreCase("UNASSIGNED")) {
               switch (option) {
                  case OVERRIDE:
                     newAssignee = sourceAssignee;
                     break;
                  case OVERRIDE_EMPTY:
                     if (currentAssignee.equalsIgnoreCase("UNASSIGNED")) {
                        newAssignee = sourceAssignee;
                     } else {
                        newAssignee = currentAssignee;
                     }
                     break;
                  case MERGE:
                     // Should not get here
                  case NONE:
                  default:
                     newAssignee = currentAssignee;
                     break;
               }

               // Check to see if this item is already set to be edited
               DispoItem matchingToEdit = toEdit.get(sourceItem.getName());
               if (matchingToEdit != null) {
                  ((DispoItemData) matchingToEdit).setAssignee(newAssignee);
               } else {
                  DispoItemData newToEdit = new DispoItemData();
                  newToEdit.setGuid(destItem.getGuid());
                  newToEdit.setName(destItem.getName());
                  newToEdit.setAssignee(newAssignee);
                  toEdit.put(newToEdit.getName(), newToEdit);
               }
            }
         }
      }
   }

   public void copyNotes(Map<String, DispoItemData> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = destinationItems.get(sourceItem.getName());

         if (destItem != null) {
            String currentItemNotes = destItem.getItemNotes();
            String sourceItemNotes = sourceItem.getItemNotes();
            String newItemNotes;

            if (Strings.isValid(sourceItemNotes)) {
               switch (option) {
                  case OVERRIDE:
                     newItemNotes = sourceItemNotes;
                     break;
                  case OVERRIDE_EMPTY:
                     if (!Strings.isValid(currentItemNotes)) {
                        newItemNotes = sourceItemNotes;
                     } else {
                        newItemNotes = currentItemNotes;
                     }
                     break;
                  case MERGE:
                     if (!Strings.isValid(currentItemNotes)) {
                        newItemNotes = sourceItemNotes;
                     } else {
                        newItemNotes = currentItemNotes + "::" + sourceItemNotes;
                     }
                     break;
                  case NONE:
                  default:
                     newItemNotes = currentItemNotes;
                     break;
               }

               // Check to see if this item is already set to be edited
               DispoItem matchingToEdit = toEdit.get(sourceItem.getName());
               if (matchingToEdit != null) {
                  ((DispoItemData) matchingToEdit).setItemNotes(newItemNotes);
               } else {
                  DispoItemData newToEdit = new DispoItemData();
                  newToEdit.setGuid(destItem.getGuid());
                  newToEdit.setName(destItem.getName());
                  newToEdit.setItemNotes(newItemNotes);
                  toEdit.put(newToEdit.getName(), newToEdit);
               }
            }
         }
      }
   }
}
