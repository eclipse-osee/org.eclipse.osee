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

import static java.util.Collections.singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.CopySetParams;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStorageMetadata;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoApiConfiguration;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.external.DispoUpdateBroadcaster;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory.ImportFormat;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.CoverageAdapter;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public class DispoApiImpl implements DispoApi {

   private ExecutorAdmin executor;

   private Log logger;
   private StorageProvider storageProvider;
   private DispoDataFactory dataFactory;
   private DispoConnector dispoConnector;
   private DispoResolutionValidator resolutionValidator;
   private DispoImporterFactory importerFactory;
   private DispoUpdateBroadcaster updateBroadcaster;
   private volatile DispoApiConfiguration config;

   @Override
   public DispoApiConfiguration getConfig() {
      return config;
   }

   public void setConfig(DispoApiConfiguration config) {
      this.config = config;
   }

   public void setExecutor(ExecutorAdmin executor) {
      this.executor = executor;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDataFactory(DispoDataFactory dataFactory) {
      this.dataFactory = dataFactory;
   }

   public void setDispoConnector(DispoConnector dispoConnector) {
      this.dispoConnector = dispoConnector;
   }

   public void setStorageProvider(StorageProvider storageProvider) {
      this.storageProvider = storageProvider;
   }

   public void setResolutionValidator(DispoResolutionValidator resolutionValidator) {
      this.resolutionValidator = resolutionValidator;
   }

   public void setDispoUpdateBroadcaster(DispoUpdateBroadcaster updateBroadcater) {
      this.updateBroadcaster = updateBroadcater;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting DispoApiImpl...");
      importerFactory = new DispoImporterFactory(dataFactory, executor, logger);
      update(props);
   }

   public void update(Map<String, Object> props) {
      logger.trace("Configuring [%s]...", getClass().getSimpleName());
      setConfig(DispoApiConfiguration.newConfig(props));
   }

   public void stop() {
      logger.trace("Stopping DispoApiImpl...");
   }

   private DispoQuery getQuery() {
      return storageProvider.get();
   }

   private DispoWriter getWriter() {
      return storageProvider.get();
   }

   @Override
   public Long createDispoProgram(String name) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoProgram(author, name);
   }

   @Override
   public Long createDispoSet(BranchId branch, DispoSetDescriptorData descriptor) {
      DispoSetData newSet = dataFactory.creteSetDataFromDescriptor(descriptor);
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoSet(author, branch, newSet);
   }

   private void createDispoItems(BranchId branch, String setId, List<DispoItem> dispoItems) {
      DispoSet parentSet = getQuery().findDispoSetsById(branch, setId);
      if (parentSet != null) {
         ArtifactReadable author = getQuery().findUser();
         getWriter().createDispoItems(author, branch, parentSet, dispoItems);
      }
   }

   @Override
   public String createDispoAnnotation(BranchId branch, String itemId, DispoAnnotationData annotationToCreate, String userName) {
      String idOfNewAnnotation = "";
      DispoItem dispoItem = getQuery().findDispoItemById(branch, itemId);
      if (dispoItem != null && dispoItem.getAssignee().equalsIgnoreCase(userName)) {
         List<DispoAnnotationData> annotationsList = dispoItem.getAnnotationsList();
         dataFactory.initAnnotation(annotationToCreate);
         idOfNewAnnotation = dataFactory.getNewId();
         annotationToCreate.setId(idOfNewAnnotation);
         int indexOfAnnotation = annotationsList.size();
         annotationToCreate.setIndex(indexOfAnnotation);

         Map<String, Discrepancy> discrepanciesList = dispoItem.getDiscrepanciesList();
         dispoConnector.connectAnnotation(annotationToCreate, discrepanciesList);
         annotationsList.add(indexOfAnnotation, annotationToCreate);

         DispoItem updatedItem;
         updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList);
         ArtifactReadable author = getQuery().findUser();

         DispoStorageMetadata metadata = new DispoStorageMetadata();
         getWriter().updateDispoItem(author, branch, dispoItem.getGuid(), updatedItem, metadata);
         if (!metadata.getIdsOfUpdatedItems().isEmpty()) {
            updateBroadcaster.broadcastUpdateItems(metadata.getIdsOfUpdatedItems(), singleton(dispoItem),
               getDispoItemParentSet(branch, itemId));
         }

      }
      return idOfNewAnnotation;
   }

   @Override
   public void editDispoSet(BranchId branch, String setId, DispoSetData newSet) throws OseeCoreException {
      DispoSet dispSetToEdit = getQuery().findDispoSetsById(branch, setId);

      if (dispSetToEdit != null) {
         if (newSet.getOperation() != null) {
            runOperation(branch, dispSetToEdit, newSet);
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoSet(author, branch, dispSetToEdit.getGuid(), newSet);
      }
   }

   @Override
   public boolean deleteDispoSet(BranchId branch, String setId) {
      ArtifactReadable author = getQuery().findUser();
      updateBroadcaster.broadcastDeleteSet(getDispoSetById(branch, setId));
      return getWriter().deleteDispoSet(author, branch, setId);
   }

   @Override
   public boolean editDispoItem(BranchId branch, String itemId, DispoItemData newDispoItem) {
      boolean wasUpdated = false;
      DispoItem dispoItemToEdit = getQuery().findDispoItemById(branch, itemId);

      if (dispoItemToEdit != null && newDispoItem.getAnnotationsList() == null && newDispoItem.getDiscrepanciesList() == null) { // We will not allow the user to do mass edit of Annotations or discrepancies
         ArtifactReadable author = getQuery().findUser();
         DispoStorageMetadata metadata = new DispoStorageMetadata();
         getWriter().updateDispoItem(author, branch, dispoItemToEdit.getGuid(), newDispoItem, metadata);
         if (!metadata.getIdsOfUpdatedItems().isEmpty()) {
            updateBroadcaster.broadcastUpdateItems(metadata.getIdsOfUpdatedItems(), singleton(newDispoItem),
               getDispoItemParentSet(branch, itemId));
         }
         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public boolean massEditTeam(BranchId branch, String setId, List<String> itemNames, String team, String operation) {
      boolean wasUpdated = false;
      Set<DispoItem> dispoItems = new HashSet<>();
      List<DispoItem> itemsFromSet = getDispoItems(branch, setId);
      Map<String, String> nameToId = new HashMap<>();
      OperationReport report = new OperationReport();

      for (DispoItem item : itemsFromSet) {
         nameToId.put(item.getName(), item.getGuid());
      }

      Set<String> itemsUpdated = new HashSet<>();
      for (String name : itemNames) {
         name = name.trim();
         String matchingItemId = nameToId.get(name);
         if (matchingItemId == null) {
            report.addEntry(name, "No existing item with this name for the selected set", DispoSummarySeverity.WARNING);
         } else {
            itemsUpdated.add(name);
            DispoItemData newItem = new DispoItemData();
            newItem.setGuid(matchingItemId);
            newItem.setTeam(team);
            dispoItems.add(newItem);
         }

      }

      if (!itemsUpdated.isEmpty()) {
         report.addEntry(team, String.format("Team Applied to %s of %s items", itemsUpdated.size(), itemNames.size()),
            DispoSummarySeverity.UPDATE);
         if (itemsUpdated.size() != itemNames.size()) {
            Set<String> uniqueNames = new HashSet<>(itemNames);
            itemNames.removeAll(uniqueNames);
            if (!itemNames.isEmpty()) {
               String duplicatesAsString = Collections.toString(", ", itemNames);
               report.addEntry(team,
                  String.format("There were %s duplciates: %s", itemNames.size(), duplicatesAsString),
                  DispoSummarySeverity.WARNING);
            }
         }
         editDispoItems(branch, setId, dispoItems, false, operation);
      } else {
         report.addEntry("Womp womp womp",
            "No items were updated. Please check your 'Items' list and make sure it's a comma seperated list of item names",
            DispoSummarySeverity.ERROR);
      }

      // Generate report
      ArtifactReadable author = getQuery().findUser();
      getWriter().updateOperationSummary(author, branch, setId, report);
      return wasUpdated;
   }

   private boolean editDispoItems(BranchId branch, String setId, Collection<DispoItem> dispoItems, boolean resetRerunFlag, String operation) {
      boolean wasUpdated = false;

      ArtifactReadable author = getQuery().findUser();
      DispoStorageMetadata metadata = new DispoStorageMetadata();
      getWriter().updateDispoItems(author, branch, dispoItems, resetRerunFlag, operation, metadata);
      if (!metadata.getIdsOfUpdatedItems().isEmpty()) {
         updateBroadcaster.broadcastUpdateItems(metadata.getIdsOfUpdatedItems(), dispoItems,
            getDispoSetById(branch, setId));
      }
      wasUpdated = true;
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoItem(BranchId branch, String itemId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoItem(author, branch, itemId);
   }

   @Override
   public boolean editDispoAnnotation(BranchId branch, String itemId, String annotationId, DispoAnnotationData newAnnotation, String userName) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(branch, itemId);
      if (dispoItem != null && dispoItem.getAssignee().equalsIgnoreCase(userName)) {
         List<DispoAnnotationData> annotationsList = dispoItem.getAnnotationsList();
         Map<String, Discrepancy> discrepanciesList = dispoItem.getDiscrepanciesList();
         DispoAnnotationData origAnnotation = DispoUtil.getById(annotationsList, annotationId);
         int indexOfAnnotation = origAnnotation.getIndex();

         boolean needToReconnect = false;
         // now if the new Annotation modified the location Reference or resolution then disconnect the annotation and try to match it to discrepancies again
         String newLocationRefs = newAnnotation.getLocationRefs();
         String newResolution = newAnnotation.getResolution();
         String newResolutionType = newAnnotation.getResolutionType();

         if (!origAnnotation.getResolutionType().equals(newResolutionType) || !origAnnotation.getResolution().equals(
            newResolution)) {
            newAnnotation.setIsResolutionValid(validateResolution(newAnnotation));
            needToReconnect = true;
         }
         if (!origAnnotation.getLocationRefs().equals(newLocationRefs)) {
            needToReconnect = true;
         }

         if (needToReconnect == true) {
            newAnnotation.disconnect();
            dispoConnector.connectAnnotation(newAnnotation, discrepanciesList);
         }
         annotationsList.set(indexOfAnnotation, newAnnotation);
         dispoItem.getAnnotationsList().get(0);
         ArtifactReadable author = getQuery().findUser();
         DispoItemData modifiedDispoItem = DispoUtil.itemArtToItemData(getDispoItemById(branch, itemId), true);

         modifiedDispoItem.setAnnotationsList(annotationsList);
         modifiedDispoItem.setStatus(dispoConnector.getItemStatus(modifiedDispoItem));

         DispoStorageMetadata metadata = new DispoStorageMetadata();
         getWriter().updateDispoItem(author, branch, dispoItem.getGuid(), modifiedDispoItem, metadata);
         if (!metadata.getIdsOfUpdatedItems().isEmpty()) {
            updateBroadcaster.broadcastUpdateItems(metadata.getIdsOfUpdatedItems(), singleton(modifiedDispoItem),
               getDispoItemParentSet(branch, itemId));
         }

         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoAnnotation(BranchId branch, String itemId, String annotationId, String userName) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(branch, itemId);
      if (dispoItem != null && dispoItem.getAssignee().equalsIgnoreCase(userName)) {
         List<DispoAnnotationData> annotationsList = dispoItem.getAnnotationsList();
         Map<String, Discrepancy> discrepanciesList = dispoItem.getDiscrepanciesList();
         DispoAnnotationData annotationToRemove = DispoUtil.getById(annotationsList, annotationId);
         annotationToRemove.disconnect();

         // collapse list so there are no gaps
         List<DispoAnnotationData> newAnnotationsList =
            removeAnnotationFromList(annotationsList, annotationToRemove.getIndex());

         DispoItem updatedItem = dataFactory.createUpdatedItem(newAnnotationsList, discrepanciesList);

         ArtifactReadable author = getQuery().findUser();
         DispoStorageMetadata metadata = new DispoStorageMetadata();
         getWriter().updateDispoItem(author, branch, dispoItem.getGuid(), updatedItem, metadata);
         if (!metadata.getIdsOfUpdatedItems().isEmpty()) {
            updateBroadcaster.broadcastUpdateItems(metadata.getIdsOfUpdatedItems(), singleton(updatedItem),
               getDispoItemParentSet(branch, itemId));
         }
         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public List<IOseeBranch> getDispoPrograms() {
      return getQuery().getDispoBranches();
   }

   @Override
   public List<DispoSet> getDispoSets(BranchId branch, String type) throws OseeCoreException {
      return getQuery().findDispoSets(branch, type);
   }

   @Override
   public DispoSet getDispoSetById(BranchId branch, String setId) throws OseeCoreException {
      return getQuery().findDispoSetsById(branch, setId);
   }

   @Override
   public List<DispoItem> getDispoItems(BranchId branch, String setArtId, boolean isDetailed) {
      return getQuery().findDipoItems(branch, setArtId, isDetailed);
   }

   private List<DispoItem> getDispoItems(BranchId branch, String setArtId) {
      return getDispoItems(branch, setArtId, true);
   }

   @Override
   public DispoItem getDispoItemById(BranchId branch, String itemId) {
      return getQuery().findDispoItemById(branch, itemId);
   }

   @Override
   public Collection<DispoItem> getDispoItemByAnnotationText(BranchId branch, String setId, String keyword, boolean isDetailed) {
      return getQuery().findDispoItemByAnnoationText(branch, setId, keyword, isDetailed);
   }

   @Override
   public List<DispoAnnotationData> getDispoAnnotations(BranchId branch, String itemId) {
      DispoItem dispoItem = getQuery().findDispoItemById(branch, itemId);
      return dispoItem.getAnnotationsList();
   }

   @Override
   public DispoAnnotationData getDispoAnnotationById(BranchId branch, String itemId, String annotationId) {
      DispoItem dispoItem = getQuery().findDispoItemById(branch, itemId);
      List<DispoAnnotationData> annotationsList = dispoItem.getAnnotationsList();
      return DispoUtil.getById(annotationsList, annotationId);
   }

   @Override
   public boolean isUniqueProgramName(String name) {
      return getQuery().isUniqueProgramName(name);
   }

   @Override
   public boolean isUniqueItemName(BranchId branch, String setId, String name) {
      return getQuery().isUniqueItemName(branch, setId, name);
   }

   @Override
   public boolean isUniqueSetName(BranchId branch, String name) {
      return getQuery().isUniqueSetName(branch, name);
   }

   private void runOperation(BranchId branch, DispoSet setToEdit, DispoSetData newSet) {
      OperationReport report = new OperationReport();
      String operation = newSet.getOperation();
      ArtifactReadable author = getQuery().findUser();
      if (operation.equals(DispoStrings.Operation_Import)) {
         try {
            HashMap<String, DispoItem> nameToItemMap = getItemsMap(branch, setToEdit);

            DispoImporterApi importer;
            if (setToEdit.getDispoType().equalsIgnoreCase("codeCoverage")) {
               importer = importerFactory.createImporter(ImportFormat.LIS);
            } else {
               importer = importerFactory.createImporter(ImportFormat.TMO);
            }

            List<DispoItem> itemsFromParse =
               importer.importDirectory(nameToItemMap, new File(setToEdit.getImportPath()), report);

            List<DispoItem> itemsToCreate = new ArrayList<>();
            List<DispoItem> itemsToEdit = new ArrayList<>();

            for (DispoItem item : itemsFromParse) {
               // if the ID is non-empty then we are updating an item instead of creating a new one
               if (item.getGuid() == null) {
                  itemsToCreate.add(item);
                  report.addEntry(item.getName(), "", DispoSummarySeverity.NEW);
               } else {
                  itemsToEdit.add(item);
               }
            }

            if (!report.getStatus().isFailed()) {
               if (itemsToCreate.size() > 0) {
                  createDispoItems(branch, setToEdit.getGuid(), itemsToCreate);
               }
               if (itemsToEdit.size() > 0) {
                  editDispoItems(branch, setToEdit.getGuid(), itemsToEdit, true, "Import");
               }
            }

         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
      } else if (operation.equals(DispoStrings.Operation_MassSendStatus)) {
         MassSendDispoItemStatus(branch, setToEdit);
      }

      // Create the Note to document the Operation
      List<Note> notesList = setToEdit.getNotesList();
      notesList.add(generateOperationNotes(operation));
      newSet.setNotesList(notesList);

      // Generate report
      getWriter().updateOperationSummary(author, branch, setToEdit.getGuid(), report);
   }

   private void MassSendDispoItemStatus(BranchId branch, DispoSet set) {
      try {
         HashMap<String, DispoItem> nameToItemMap = getItemsMap(branch, set);
         Collection<String> ids = new ArrayList<>();
         for (DispoItem item : nameToItemMap.values()) {
            ids.add(item.getGuid());
         }
         //let ci tool know to delete all the ciset dispo data
         updateBroadcaster.broadcastDeleteSet(set);
         // now send all the current data
         updateBroadcaster.broadcastUpdateItems(ids, nameToItemMap.values(), set);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private HashMap<String, DispoItem> getItemsMap(BranchId branch, DispoSet set) {
      HashMap<String, DispoItem> toReturn = new HashMap<>();
      List<DispoItem> dispoItems = getDispoItems(branch, set.getGuid());
      for (DispoItem item : dispoItems) {
         toReturn.put(item.getName(), item);
      }
      return toReturn;
   }

   private Note generateOperationNotes(String operation) {
      Note operationNote = new Note();
      Date date = new Date();
      operationNote.setDateString(date.toString());
      operationNote.setType("SYSTEM");
      operationNote.setContent(operation);
      return operationNote;
   }

   private List<DispoAnnotationData> removeAnnotationFromList(List<DispoAnnotationData> oldList, int indexRemoved) {
      List<DispoAnnotationData> newList = new ArrayList<DispoAnnotationData>();
      oldList.remove(indexRemoved);

      // Re assign index to Annotations still left in list
      int newIndex = 0;
      for (DispoAnnotationData annotation : oldList) {
         annotation.setIndex(newIndex);
         newList.add(newIndex, annotation);
         newIndex++;
      }
      return newList;
   }

   private boolean validateResolution(DispoAnnotationData annotation) {
      return resolutionValidator.validate(annotation);
   }

   @Override
   public void copyDispoSetCoverage(BranchId sourceBranch, Long sourceCoverageUuid, BranchId destBranch, String destSetId, CopySetParams params) {
      Map<String, ArtifactReadable> coverageUnits = getQuery().getCoverageUnits(sourceBranch, sourceCoverageUuid);
      List<DispoItem> destItems = getDispoItems(destBranch, destSetId);

      OperationReport report = new OperationReport();

      CoverageAdapter coverageAdapter = new CoverageAdapter(dispoConnector);
      List<DispoItem> copyData = coverageAdapter.copyData(coverageUnits, destItems, report);

      String operation =
         String.format("Copy From Legacy Coverage - Branch [%s] and Source Set [%s]", sourceBranch, sourceCoverageUuid);
      if (!copyData.isEmpty()) {
         editDispoItems(destBranch, destSetId, copyData, false, operation);
         storageProvider.get().updateOperationSummary(getQuery().findUser(), destBranch, destSetId, report);
      }
   }

   @Override
   public void copyDispoSet(BranchId branch, String destSetId, BranchId sourceBranch, String sourceSetId, CopySetParams params) {
      List<DispoItem> sourceItems = getDispoItems(sourceBranch, sourceSetId);
      Map<String, Set<DispoItemData>> namesToDestItems = new HashMap<>();
      for (DispoItem itemArt : getDispoItems(branch, destSetId)) {
         DispoItemData itemData = DispoUtil.itemArtToItemData(itemArt, true, true);

         String name = itemData.getName();
         Set<DispoItemData> itemsWithSameName = namesToDestItems.get(name);
         if (itemsWithSameName == null) {
            Set<DispoItemData> set = new HashSet<>();
            set.add(itemData);
            namesToDestItems.put(name, set);
         } else {
            itemsWithSameName.add(itemData);
            namesToDestItems.put(name, itemsWithSameName);
         }
      }
      Map<String, DispoItem> namesToToEditItems = new HashMap<>();
      OperationReport report = new OperationReport();

      DispoSetCopier copier = new DispoSetCopier(dispoConnector);
      if (!params.getAnnotationParam().isNone()) {
         List<DispoItem> copyResults = copier.copyAllDispositions(namesToDestItems, sourceItems, true, report);
         for (DispoItem item : copyResults) {
            namesToToEditItems.put(item.getName(), item);
         }
      }

      copier.copyCategories(namesToDestItems, sourceItems, namesToToEditItems, params.getCategoryParam());
      copier.copyAssignee(namesToDestItems, sourceItems, namesToToEditItems, params.getAssigneeParam());
      copier.copyNotes(namesToDestItems, sourceItems, namesToToEditItems, params.getNoteParam());

      String operation = String.format("Copy Set from Program [%s] and Set [%s]", sourceBranch, sourceSetId);
      if (!namesToToEditItems.isEmpty() && !report.getStatus().isFailed()) {
         editDispoItems(branch, destSetId, namesToToEditItems.values(), false, operation);
         storageProvider.get().updateOperationSummary(getQuery().findUser(), branch, destSetId, report);
      }

   }

   @Override
   public DispoConfig getDispoConfig(BranchId branch) {
      return getQuery().findDispoConfig(branch);
   }

   @Override
   public DispoSet getDispoItemParentSet(BranchId branch, String itemId) {
      Long id = getQuery().getDispoItemParentSet(branch, itemId);
      return getDispoSetById(branch, String.valueOf(id));
   }

}
