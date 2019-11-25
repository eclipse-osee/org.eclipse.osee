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

import static org.eclipse.osee.disposition.model.DispoSummarySeverity.IGNORE;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.UPDATE;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.WARNING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.disposition.model.CopySetParamOption;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.FindReruns;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class DispoSetCopier {

   private final DispoConnector connector;

   private final List<DispoAnnotationData> needsRerun = new ArrayList<>();
   String batchRunList = "";

   public DispoSetCopier(DispoConnector connector) {
      this.connector = connector;
   }

   public List<DispoItem> copyAllDispositions(Map<String, Set<DispoItemData>> nameToDestItems, Collection<DispoItem> sourceItems, boolean isCoverageCopy, HashMap<String, String> reruns, OperationReport report) {
      return copyAllDispositions(nameToDestItems, sourceItems, isCoverageCopy, reruns, false, Collections.emptySet(),
         report);
   }

   public List<DispoItem> copyAllDispositions(Map<String, Set<DispoItemData>> nameToDestItems, Collection<DispoItem> sourceItems, boolean isCoverageCopy, HashMap<String, String> reruns, boolean allowOnlyValidResolutionTypes, Set<String> validResolutionsTypes, OperationReport report) {
      List<DispoItem> modifiedItems = new ArrayList<>();

      // Iterate through every source item since we want to try to find a match for every item in the source
      for (DispoItem sourceItem : sourceItems) {
         DispoItemData destItem = getCorrespondingDestItem(nameToDestItems, sourceItem);

         if (destItem != null) {
            // Only try to copy over annotations if matching dest item is NOT PASS
            if (!destItem.getStatus().equals(DispoStrings.Item_Pass)) {
               DispoItemData newItem = createNewItemWithCopiedAnnotations(destItem, sourceItem, isCoverageCopy, reruns,
                  report, allowOnlyValidResolutionTypes, validResolutionsTypes);
               if (newItem != null) {
                  modifiedItems.add(newItem);

                  if (!newItem.getGuid().equals(sourceItem.getGuid())) {
                     List<DispoAnnotationData> destAnnotationsList = destItem.getAnnotationsList();
                     String message = String.format("Had %s Dispositions now has %s", destAnnotationsList.size(),
                        newItem.getAnnotationsList().size());
                     report.addEntry(destItem.getName(), message, UPDATE);
                  }
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
            report.addEntry(sourceItem.getName(), "No matching item found in the Destination Set", WARNING);
         }
      }
      return modifiedItems;
   }

   private DispoItemData getCorrespondingDestItem(Map<String, Set<DispoItemData>> nameToDestItems, DispoItem sourceItem) {
      DispoItemData destItem = null;
      String name = sourceItem.getName();
      Set<DispoItemData> itemsWithSameName = nameToDestItems.get(name);

      if (itemsWithSameName != null) {
         if (itemsWithSameName.size() == 1) {
            destItem = itemsWithSameName.iterator().next();
         } else {
            for (DispoItemData itemWithSameName : itemsWithSameName) {
               if (itemWithSameName.getMethodNumber().equals(
                  sourceItem.getMethodNumber()) && itemWithSameName.getFileNumber().equals(
                     sourceItem.getFileNumber())) {
                  destItem = itemWithSameName;
               }
            }
         }
      }
      return destItem;
   }

   private DispoItemData createNewItemWithCopiedAnnotations(DispoItemData destItem, DispoItem sourceItem, boolean isCoverageCopy, HashMap<String, String> reruns, OperationReport report, boolean allowOnlyValidResolutionTypes, Set<String> validResolutionTypes) {
      DispoItemData toReturn = null;
      boolean isSameDiscrepancies = matchAllDiscrepancies(destItem, sourceItem);
      if (!isSameDiscrepancies) {
         report.addEntry(destItem.getName(),
            String.format("Tried to copy from item id: [%s] but discrepancies were not the same", sourceItem.getGuid()),
            WARNING);

      }
      toReturn = buildNewItem(destItem, sourceItem, isCoverageCopy, reruns, report, isSameDiscrepancies,
         allowOnlyValidResolutionTypes, validResolutionTypes);
      return toReturn;
   }

   private DispoItemData buildNewItem(DispoItemData destItem, DispoItem sourceItem, boolean isCoverageCopy, HashMap<String, String> reruns, OperationReport report, boolean isSameDiscrepancies, boolean allowOnlyValidResolutionTypes, Set<String> validResolutionTypes) {
      boolean isChangesMade = false;
      DispoItemData newItem = initNewItem(destItem, sourceItem);
      List<DispoAnnotationData> newAnnotations = newItem.getAnnotationsList();
      List<DispoAnnotationData> sourceAnnotations = sourceItem.getAnnotationsList();
      Set<String> destDefaultAnntationLocations = getDefaultAnnotations(newItem);
      Map<String, Integer> nonDefaultAnnotationLocations = getNonDefaultAnnotations(newItem);
      List<String> destDiscrepanciesTextOnly = discrepanciesTextOnly(destItem.getDiscrepanciesList());

      for (DispoAnnotationData sourceAnnotation : sourceAnnotations) {
         String sourceLocation = sourceAnnotation.getLocationRefs();

         // Check for ignore cases
         if (DispoUtil.isDefaultAnotation(sourceAnnotation) || !Strings.isValid(sourceAnnotation.getResolutionType())) {
            /**
             * This means this annotation is TEST_UNIT, Exception_Handling, or just place holder, so don't copy it over,
             * only log if the destination item doesn't have this annotation as a Default i.e means something changed
             * user should be aware.Currently only for Coverage
             */
            if (!destDefaultAnntationLocations.contains(sourceLocation)) {
               if (!nonDefaultAnnotationLocations.containsKey(sourceLocation)) {
                  newItem.setNeedsRerun(true);
                  needsRerun.add(sourceAnnotation);
               }
               report.addEntry(destItem.getName(),
                  String.format("Did not copy annotations for location(s) [%s] because they are default annotations",
                     sourceAnnotation.getLocationRefs()),
                  IGNORE);
            }
         } else if (destDefaultAnntationLocations.contains(sourceLocation)) {
            /**
             * isCoverageCopy is true when annotation copier is called by a coverage import, this means we need to also
             * check that the matching dest annotation isn't a DEFAULT resolution before copying over.
             */
            report.addEntry(destItem.getName(),
               String.format(
                  "Did not copy annotations for location(s) [%s] because the destination item already has a default annotations at these locations",
                  sourceAnnotation.getLocationRefs()),
               IGNORE);

         } else if (newAnnotations.toString().contains(sourceAnnotation.getGuid())) {
            report.addEntry(destItem.getName(),
               String.format(
                  "Did not copy annotations for location(s) [%s] because the destination item already has this Annotation [%s]",
                  sourceAnnotation.getLocationRefs(), sourceAnnotation.getGuid()),
               IGNORE);

         } else if (allowOnlyValidResolutionTypes && !validResolutionTypes.contains(sourceAnnotation.getResolutionType())) {
            report.addEntry(destItem.getName(),
               String.format(
                  "Did not copy annotations for location(s) [%s] because the resolution [%s] does not exist in the destination program",
                  sourceAnnotation.getLocationRefs(), sourceAnnotation.getResolutionType()),
               IGNORE);
         } else {
            // Try to copy but check if Discrepancy is the same and present in the destination set
            if (isSameDiscrepancies && isCoveredDiscrepanciesExistInDest(destDiscrepanciesTextOnly, sourceItem,
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
                  report.addEntry(destItem.getName(),
                     String.format("The annotation was copied over but is no longer needed: [%s]", locationRefs),
                     WARNING);
               }
               connector.connectAnnotation(newAnnotation, newItem.getDiscrepanciesList());
               isChangesMade = true;
               // Both the source and destination are dispositionable so copy the annotation
               int nextIndex;
               if (nonDefaultAnnotationLocations.containsKey(sourceLocation)) {
                  nextIndex = nonDefaultAnnotationLocations.get(sourceLocation);
                  newAnnotation.setIndex(nextIndex);
                  newAnnotations.set(nextIndex, newAnnotation);
               } else {
                  nextIndex = newAnnotations.size();
                  newAnnotation.setIndex(nextIndex);
                  newAnnotations.add(nextIndex, newAnnotation);
               }
            }
         }
      }

      if (isChangesMade) {
         newItem.setAnnotationsList(newAnnotations);
         String newStatus = connector.getItemStatus(newItem);
         newItem.setStatus(newStatus);
      } else if (!isCoverageCopy) {
         // We want to take the new import version of this item even though no changes were made, this will only occur if
         // 1. All the Annotations from the source Item were default, in which case we can ignore those and take the ones from the new import
         // 2. None of the non-default Annotations cover a Discrepancy in the new import, in which case don't copy them over: MIGHT CHANGE THIS
         newItem = destItem;
         newItem.setGuid(sourceItem.getGuid());
      } else if (newItem.getNeedsRerun() != null && newItem.getNeedsRerun()) {
         report.addEntry(destItem.getName(), "Needs Rerun", UPDATE);
         if (reruns != null) {
            HashMap<String, String> tmpList = new FindReruns().createList(needsRerun);
            for (Entry<String, String> entry : tmpList.entrySet()) {
               reruns.put(entry.getKey(), entry.getValue());
            }
         }
      } else {
         report.addEntry(destItem.getName(), "Nothing to copy", IGNORE);
         newItem = null;
      }
      return newItem;
   }

   private boolean isCoveredDiscrepanciesExistInDest(List<String> destDescrepanciesTextOnly, DispoItem sourceItem, DispoAnnotationData annotation, OperationReport report) {
      List<String> idsOfCoveredDiscrepancies = annotation.getIdsOfCoveredDiscrepancies();
      Map<String, Discrepancy> sourceDiscrepancies = sourceItem.getDiscrepanciesList();
      for (String id : idsOfCoveredDiscrepancies) {
         Discrepancy coveredDiscrepancy = sourceDiscrepancies.get(id);
         if (coveredDiscrepancy == null || !destDescrepanciesTextOnly.contains(coveredDiscrepancy.getText())) {
            return false;
         }
      }

      return true;
   }

   private List<String> discrepanciesTextOnly(Map<String, Discrepancy> discrepancies) {
      List<String> toReturn = new ArrayList<>();
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         toReturn.add(discrepancy.getText());
      }
      return toReturn;
   }

   private DispoItemData initNewItem(DispoItemData destItem, DispoItem sourceItem) {
      DispoItemData newItem = new DispoItemData();
      newItem.setDiscrepanciesList(destItem.getDiscrepanciesList());
      List<DispoAnnotationData> newList = destItem.getAnnotationsList();
      newItem.setAnnotationsList(newList);
      if (Strings.isValid(destItem.getGuid())) {
         newItem.setGuid(destItem.getGuid());
      } else {
         newItem.setGuid(sourceItem.getGuid());
      }
      newItem.setName(destItem.getName());
      return newItem;
   }

   private Map<String, Integer> getNonDefaultAnnotations(DispoItemData item) {
      Map<String, Integer> nonDefaultAnnotationLocations = new HashMap<>();
      List<DispoAnnotationData> annotations = item.getAnnotationsList();
      if (annotations == null) {
         annotations = new ArrayList<>();
      }
      for (DispoAnnotationData annotation : annotations) {
         if (!annotation.getIsDefault()) {
            nonDefaultAnnotationLocations.put(annotation.getLocationRefs(), annotation.getIndex());
         }
      }

      return nonDefaultAnnotationLocations;
   }

   private Set<String> getDefaultAnnotations(DispoItemData item) {
      Set<String> defaultAnnotationLocations = new HashSet<>();
      List<DispoAnnotationData> annotations = item.getAnnotationsList();
      if (annotations == null) {
         annotations = new ArrayList<>();
      }
      for (DispoAnnotationData annotation : annotations) {
         if (DispoUtil.isDefaultAnotation(annotation)) {
            defaultAnnotationLocations.add(annotation.getLocationRefs());
         }
      }

      return defaultAnnotationLocations;
   }

   private boolean matchAllDiscrepancies(DispoItemData destItem, DispoItem sourceItem) {
      Map<String, String> destLocationToText = generateLocationToTextMap(destItem);
      boolean toReturn = true;

      Map<String, Discrepancy> sourceDiscrepancies = sourceItem.getDiscrepanciesList();
      for (String key : sourceDiscrepancies.keySet()) {
         Discrepancy sourceDiscrepancy = sourceDiscrepancies.get(key);

         String sourceLocation = sourceDiscrepancy.getLocation();
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

   private Map<String, String> generateLocationToTextMap(DispoItem item) {
      Map<String, String> locationToText = new HashMap<>();
      Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         locationToText.put(discrepancy.getLocation(), discrepancy.getText());
      }
      return locationToText;
   }

   public void copyCategories(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

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

   public void copyAssignee(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

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

   public void copyNotes(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems, Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

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
