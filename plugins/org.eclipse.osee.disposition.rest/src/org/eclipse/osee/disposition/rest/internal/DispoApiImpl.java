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
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory.ImportFormat;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.CoverageAdapter;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   private DispoFactory dispoFactory;
   private DispoResolutionValidator resolutionValidator;
   private DispoImporterFactory importerFactory;

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

   public void start() {
      logger.trace("Starting DispoApiImpl...");
      dispoFactory = new DispoFactoryImpl();
      importerFactory = new DispoImporterFactory(dataFactory, executor, logger);
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
   public Long createDispoSet(DispoProgram program, DispoSetDescriptorData descriptor) {
      DispoSetData newSet = dataFactory.creteSetDataFromDescriptor(descriptor);
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoSet(author, program, newSet);
   }

   private void createDispoItems(DispoProgram program, String setId, List<DispoItem> dispoItems) {
      DispoSet parentSet = getQuery().findDispoSetsById(program, setId);
      if (parentSet != null) {
         ArtifactReadable author = getQuery().findUser();
         getWriter().createDispoItems(author, program, parentSet, dispoItems);
      }
   }

   @Override
   public String createDispoAnnotation(DispoProgram program, String itemId, DispoAnnotationData annotationToCreate, String userName) {
      String idOfNewAnnotation = "";
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
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
         getWriter().updateDispoItem(author, program, dispoItem.getGuid(), updatedItem);
      }
      return idOfNewAnnotation;
   }

   @Override
   public void editDispoSet(DispoProgram program, String setId, DispoSetData newSet) throws OseeCoreException {
      DispoSet dispSetToEdit = getQuery().findDispoSetsById(program, setId);

      if (dispSetToEdit != null) {
         if (newSet.getOperation() != null) {
            runOperation(program, dispSetToEdit, newSet);
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoSet(author, program, dispSetToEdit.getGuid(), newSet);
      }
   }

   @Override
   public boolean deleteDispoSet(DispoProgram program, String setId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoSet(author, program, setId);
   }

   @Override
   public boolean editDispoItem(DispoProgram program, String itemId, DispoItemData newDispoItem) {
      boolean wasUpdated = false;
      DispoItem dispoItemToEdit = getQuery().findDispoItemById(program, itemId);

      if (dispoItemToEdit != null && newDispoItem.getAnnotationsList() == null && newDispoItem.getDiscrepanciesList() == null) { // We will not allow the user to do mass edit of Annotations or discrepancies
         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoItem(author, program, dispoItemToEdit.getGuid(), newDispoItem);
         wasUpdated = true;
      }
      return wasUpdated;
   }

   private boolean editDispoItems(DispoProgram program, Collection<DispoItem> dispoItems, boolean resetRerunFlag, String operation) {
      boolean wasUpdated = false;

      ArtifactReadable author = getQuery().findUser();
      getWriter().updateDispoItems(author, program, dispoItems, resetRerunFlag, operation);
      wasUpdated = true;
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoItem(DispoProgram program, String itemId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoItem(author, program, itemId);
   }

   @Override
   public boolean editDispoAnnotation(DispoProgram program, String itemId, String annotationId, DispoAnnotationData newAnnotation, String userName) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
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
         DispoItemData modifiedDispoItem = DispoUtil.itemArtToItemData(getDispoItemById(program, itemId), true);

         modifiedDispoItem.setAnnotationsList(annotationsList);
         modifiedDispoItem.setStatus(dispoConnector.getItemStatus(modifiedDispoItem));
         getWriter().updateDispoItem(author, program, dispoItem.getGuid(), modifiedDispoItem);

         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoAnnotation(DispoProgram program, String itemId, String annotationId, String userName) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
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
         getWriter().updateDispoItem(author, program, dispoItem.getGuid(), updatedItem);
         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public List<IOseeBranch> getDispoPrograms() {
      return getQuery().getDispoBranches();
   }

   @Override
   public List<DispoSet> getDispoSets(DispoProgram program, String type) throws OseeCoreException {
      return getQuery().findDispoSets(program, type);
   }

   @Override
   public DispoSet getDispoSetById(DispoProgram program, String setId) throws OseeCoreException {
      return getQuery().findDispoSetsById(program, setId);
   }

   @Override
   public List<DispoItem> getDispoItems(DispoProgram program, String setArtId, boolean isDetailed) {
      return getQuery().findDipoItems(program, setArtId, isDetailed);
   }

   private List<DispoItem> getDispoItems(DispoProgram program, String setArtId) {
      return getDispoItems(program, setArtId, true);
   }

   @Override
   public DispoItem getDispoItemById(DispoProgram program, String itemId) {
      return getQuery().findDispoItemById(program, itemId);
   }

   @Override
   public Collection<DispoItem> getDispoItemByAnnotationText(DispoProgram program, String setId, String keyword, boolean isDetailed) {
      return getQuery().findDispoItemByAnnoationText(program, setId, keyword, isDetailed);
   }

   @Override
   public List<DispoAnnotationData> getDispoAnnotations(DispoProgram program, String itemId) {
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      return dispoItem.getAnnotationsList();
   }

   @Override
   public DispoAnnotationData getDispoAnnotationById(DispoProgram program, String itemId, String annotationId) {
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      List<DispoAnnotationData> annotationsList = dispoItem.getAnnotationsList();
      return DispoUtil.getById(annotationsList, annotationId);
   }

   @Override
   public boolean isUniqueProgramName(String name) {
      return getQuery().isUniqueProgramName(name);
   }

   @Override
   public boolean isUniqueItemName(DispoProgram program, String setId, String name) {
      return getQuery().isUniqueItemName(program, setId, name);
   }

   @Override
   public boolean isUniqueSetName(DispoProgram program, String name) {
      return getQuery().isUniqueSetName(program, name);
   }

   private void runOperation(DispoProgram program, DispoSet setToEdit, DispoSetData newSet) {
      OperationReport report = new OperationReport();
      String operation = newSet.getOperation();
      ArtifactReadable author = getQuery().findUser();
      if (operation.equals(DispoStrings.Operation_Import)) {
         try {
            HashMap<String, DispoItem> nameToItemMap = getItemsMap(program, setToEdit);

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
                  createDispoItems(program, setToEdit.getGuid(), itemsToCreate);
               }
               if (itemsToEdit.size() > 0) {
                  editDispoItems(program, itemsToEdit, true, "Import");
               }
            }

         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
      }

      // Create the Note to document the Operation
      List<Note> notesList = setToEdit.getNotesList();
      notesList.add(generateOperationNotes(operation));
      newSet.setNotesList(notesList);

      // Generate report
      getWriter().updateOperationSummary(author, program, setToEdit, report);
   }

   private HashMap<String, DispoItem> getItemsMap(DispoProgram program, DispoSet set) {
      HashMap<String, DispoItem> toReturn = new HashMap<>();
      List<DispoItem> dispoItems = getDispoItems(program, set.getGuid());
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
   public DispoFactory getDispoFactory() {
      return dispoFactory;
   }

   @Override
   public void copyDispoSetCoverage(long sourceBranch, Long sourceCoverageUuid, DispoProgram destDispProgram, DispoSet destination, CopySetParams params) {
      Map<String, ArtifactReadable> coverageUnits = getQuery().getCoverageUnits(sourceBranch, sourceCoverageUuid);
      List<DispoItem> destItems = getDispoItems(destDispProgram, destination.getGuid());

      OperationReport report = new OperationReport();

      CoverageAdapter coverageAdapter = new CoverageAdapter(dispoConnector);
      List<DispoItem> copyData = coverageAdapter.copyData(coverageUnits, destItems, report);

      String operation =
         String.format("Copy From Legacy Coverage - Branch [%s] and Source Set [%s]", sourceBranch, sourceCoverageUuid);
      if (!copyData.isEmpty()) {
         editDispoItems(destDispProgram, copyData, false, operation);
         storageProvider.get().updateOperationSummary(getQuery().findUser(), destDispProgram, destination, report);
      }
   }

   @Override
   public void copyDispoSet(DispoProgram program, DispoSet destination, DispoProgram sourceProgram, DispoSet sourceSet, CopySetParams params) {
      List<DispoItem> sourceItems = getDispoItems(sourceProgram, sourceSet.getGuid());
      Map<String, Set<DispoItemData>> namesToDestItems = new HashMap<>();
      for (DispoItem itemArt : getDispoItems(program, destination.getGuid())) {
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

      String operation =
         String.format("Copy Set from Program [%s] and Set [%s]", sourceProgram.getUuid(), sourceSet.getGuid());
      if (!namesToToEditItems.isEmpty() && !report.getStatus().isFailed()) {
         editDispoItems(program, namesToToEditItems.values(), false, operation);
         storageProvider.get().updateOperationSummary(getQuery().findUser(), program, destination, report);
      }

   }

   @Override
   public DispoConfig getDispoConfig(DispoProgram program) {
      return getQuery().findDispoConfig(program);
   }
}
