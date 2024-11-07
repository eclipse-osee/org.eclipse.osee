/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.internal.importer;

import static org.eclipse.osee.disposition.model.DispoSummarySeverity.UPDATE;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.WARNING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.disposition.model.CopySetParamOption;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.FindReruns;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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

   public List<DispoItem> copyAllDispositionsSameSet(Map<String, Set<DispoItemData>> nameToDestItems,
      Collection<DispoItem> sourceItems, boolean isCoverageCopy, HashMap<String, String> reruns,
      OperationReport report) {
      return copyAllDispositions(nameToDestItems, sourceItems, isCoverageCopy, reruns, false, Collections.emptySet(),
         true, report);
   }

   public List<DispoItem> copyAllDispositions(Map<String, Set<DispoItemData>> nameToDestItems,
      Collection<DispoItem> sourceItems, boolean isCoverageCopy, HashMap<String, String> reruns,
      boolean allowOnlyValidResolutionTypes, Set<String> validResolutionsTypes, boolean isSameSet,
      OperationReport report) {
      List<DispoItem> modifiedItems = new ArrayList<>();

      // Iterate through every source item since we want to try to find a match for every item in the source
      for (DispoItem sourceItem : sourceItems) {
         DispoItemData destItem = getCorrespondingDestItem(nameToDestItems, sourceItem);

         if (destItem.isValid()) {
            if (isSameSet) {
               destItem.setGuid(sourceItem.getGuid());
            }
            DispoItemData newItem = createNewItemWithCopiedAnnotations(destItem, sourceItem, isCoverageCopy, reruns,
               report, allowOnlyValidResolutionTypes, validResolutionsTypes);
            if (newItem != null && newItem.isValid()) {
               modifiedItems.add(newItem);
            }

         } else {
            report.addEntry(sourceItem.getName(), "No matching item found in the Destination Set", WARNING);
         }
      }
      return modifiedItems;
   }

   private DispoItemData getCorrespondingDestItem(Map<String, Set<DispoItemData>> nameToDestItems,
      DispoItem sourceItem) {
      DispoItemData destItem = DispoItemData.SENTINEL;
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

   private DispoItemData createNewItemWithCopiedAnnotations(DispoItemData destItem, DispoItem sourceItem,
      boolean isCoverageCopy, HashMap<String, String> reruns, OperationReport report,
      boolean allowOnlyValidResolutionTypes, Set<String> validResolutionTypes) {
      DispoItemData toReturn = null;
      toReturn = buildNewItem(destItem, sourceItem, isCoverageCopy, reruns, report, allowOnlyValidResolutionTypes,
         validResolutionTypes);
      return toReturn;
   }

   private DispoItemData buildNewItem(DispoItemData destItem, DispoItem sourceItem, boolean isCoverageCopy,
      HashMap<String, String> reruns, OperationReport report, boolean allowOnlyValidResolutionTypes,
      Set<String> validResolutionTypes) {
      List<DispoAnnotationData> sourceAnnotations = sourceItem.getAnnotationsList();
      Map<String, DispoAnnotationData> destAnnotationMap = getLocToAnnotationMap(destItem.getAnnotationsList());

      for (DispoAnnotationData sourceAnnotation : sourceAnnotations) {
         String sourceLocation = sourceAnnotation.getLocationRefs();
         if (sourceAnnotation.getGuid() == null) {
            sourceAnnotation.setId(String.valueOf(Lib.generateUuid()));
         }
         if (!destAnnotationMap.containsKey(sourceLocation)) {
            continue;
         }
         DispoAnnotationData destAnnotation = destAnnotationMap.get(sourceLocation);

         if (sourceAnnotation.getCustomerNotes() == null || destAnnotation.getCustomerNotes() == null) {
            continue;
         }

         if (!sourceAnnotation.getCustomerNotes().equals(
            destAnnotation.getCustomerNotes()) || ((allowOnlyValidResolutionTypes && !validResolutionTypes.contains(
               sourceAnnotation.getResolutionType())))) {
            if (DispoUtil.isDefaultAnotation(
               sourceAnnotation) || !Strings.isValid(sourceAnnotation.getResolutionType())) {
               destItem.setNeedsRerun(true);
               needsRerun.add(sourceAnnotation);
            }
            continue;
         }

         //Initial Source Values
         String sourceResolutionType = sourceAnnotation.getResolutionType();
         String sourceResolution = sourceAnnotation.getResolution();
         String sourceLastResolutionType = sourceAnnotation.getLastResolutionType();
         String sourceLastResolution = sourceAnnotation.getLastResolution();
         String sourceLastManualResolutionType = sourceAnnotation.getLastManualResolutionType();
         String sourceLastManualResolution = sourceAnnotation.getLastManualResolution();

         DispoAnnotationData newDestAnnot = new DispoAnnotationData();

         //If the source is blank, copy over all 'last resolution' fields
         if (DispoUtil.isAnnotationValueBlank(sourceAnnotation)) {
            if (DispoUtil.isAnnotationValueBlank(
               destAnnotation) && (!sourceLastManualResolutionType.isEmpty() && !sourceLastManualResolutionType.isBlank() && sourceLastManualResolutionType != null)) {
               //If dest is empty and there is a 'Last Manual Resolution' in source, copy it over as the resolution
               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceLastManualResolutionType,
                  sourceLastManualResolution, sourceLastResolutionType, sourceLastResolution,
                  sourceLastManualResolutionType, sourceLastManualResolution, true);
            } else {
               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceLastResolutionType, sourceLastResolution,
                  sourceLastManualResolutionType, sourceLastManualResolution);

            }
         } else if (DispoUtil.isDefaultAnotation(sourceAnnotation)) {
            //else if the source is Test_Script or Exception_Handling

            if (DispoUtil.isAnnotationValueBlank(
               destAnnotation) && (!sourceLastManualResolutionType.isEmpty() && !sourceLastManualResolutionType.isBlank() && sourceLastManualResolutionType != null)) {
               //If dest is empty and there is a 'Last Manual Resolution' in source, copy it over as the resolution
               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceLastManualResolutionType,
                  sourceLastManualResolution, sourceResolutionType, sourceResolution, sourceLastManualResolutionType,
                  sourceLastManualResolution, true);
            } else {
               //If the dest is also Test_Script or Exception Handling
               //In all other cases, record the source's resolution in the 'Last Resolution' fields.

               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceResolutionType, sourceResolution,
                  sourceLastManualResolutionType, sourceLastManualResolution);
            }
         } else {
            //If the source is a manual resolution

            if (DispoUtil.isAnnotationValueBlank(destAnnotation)) {
               //Set resolution to source's manual disposition
               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceResolutionType, sourceResolution,
                  sourceLastResolutionType, sourceLastResolution, sourceLastManualResolutionType,
                  sourceLastManualResolution, sourceAnnotation.getIsResolutionValid());
            } else {
               //If they are both manual resolutions or the destination is default, set last resolutions to source resolution
               newDestAnnot = new DispoAnnotationData(destAnnotation, sourceResolutionType, sourceResolution,
                  sourceResolutionType, sourceResolution);
            }
         }
         DispoUtil.addAnnotation(destItem.getAnnotationsList(), newDestAnnot);
      }

      if (destItem.getNeedsRerun() != null && destItem.getNeedsRerun()) {
         report.addEntry(destItem.getName(), "Needs Rerun", UPDATE);
         if (reruns != null) {
            HashMap<String, String> tmpList = new FindReruns().createList(needsRerun);
            for (Entry<String, String> entry : tmpList.entrySet()) {
               reruns.put(entry.getKey(), entry.getValue());
            }
         }
      }

      return destItem;
   }

   private Map<String, DispoAnnotationData> getLocToAnnotationMap(List<DispoAnnotationData> annotations) {
      Map<String, DispoAnnotationData> locToAnnotationMap = new HashMap<>();
      if (annotations == null) {
         annotations = new ArrayList<>();
      }
      for (DispoAnnotationData annotation : annotations) {
         locToAnnotationMap.put(annotation.getLocationRefs(), annotation);
      }
      return locToAnnotationMap;
   }

   public void copyCategories(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems,
      Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

         if (destItem.isValid()) {
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

   public void copyAssignee(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems,
      Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

         if (destItem.isValid()) {
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

   public void copyNotes(Map<String, Set<DispoItemData>> destinationItems, Collection<DispoItem> sourceItems,
      Map<String, DispoItem> toEdit, CopySetParamOption option) {
      for (DispoItem sourceItem : sourceItems) {
         DispoItem destItem = getCorrespondingDestItem(destinationItems, sourceItem);

         if (destItem.isValid()) {
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
