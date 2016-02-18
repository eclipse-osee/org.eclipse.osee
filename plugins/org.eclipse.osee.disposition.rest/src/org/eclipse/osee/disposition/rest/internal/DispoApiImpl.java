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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.disposition.model.CopySetParams;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory.ImportFormat;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.CoverageAdapter;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
   public Identifiable<String> createDispoSet(DispoProgram program, DispoSetDescriptorData descriptor) {
      DispoSetData newSet = dataFactory.creteSetDataFromDescriptor(descriptor);
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoSet(author, program, newSet);
   }

   private void createDispoItems(DispoProgram program, String setId, List<DispoItem> dispoItems) {
      DispoSet parentSet = getQuery().findDispoSetsById(program, setId);
      if (parentSet != null) {
         ArtifactReadable author = getQuery().findUser();
         getWriter().createDispoItems(author, program, parentSet, dispoItems, "UnAssigned");
      }
   }

   @Override
   public String createDispoAnnotation(DispoProgram program, String itemId, DispoAnnotationData annotationToCreate, String userName) {
      String idOfNewAnnotation = "";
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      if (dispoItem != null && dispoItem.getAssignee().equalsIgnoreCase(userName)) {
         try {
            JSONArray annotationsList = dispoItem.getAnnotationsList();
            dataFactory.initAnnotation(annotationToCreate);
            idOfNewAnnotation = dataFactory.getNewId();
            annotationToCreate.setId(idOfNewAnnotation);
            int indexOfAnnotation = annotationsList.length();
            annotationToCreate.setIndex(indexOfAnnotation);

            JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();

            dispoConnector.connectAnnotation(annotationToCreate, discrepanciesList);

            annotationsList.put(indexOfAnnotation, DispoUtil.annotationToJsonObj(annotationToCreate));

            DispoItem updatedItem;
            updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList);
            ArtifactReadable author = getQuery().findUser();
            getWriter().updateDispoItem(author, program, dispoItem.getGuid(), updatedItem);
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      return idOfNewAnnotation;
   }

   @Override
   public String editDispoSet(DispoProgram program, String setId, DispoSetData newSet) throws OseeCoreException {
      DispoSet dispSetToEdit = getQuery().findDispoSetsById(program, setId);
      String reportUrl = "";

      if (dispSetToEdit != null) {
         if (newSet.getNotesList() != null) {
            JSONArray mergedNotesList =
               dataFactory.mergeJsonArrays(dispSetToEdit.getNotesList(), newSet.getNotesList());
            newSet.setNotesList(mergedNotesList);
         }

         if (newSet.getOperation() != null) {
            reportUrl = runOperation(program, dispSetToEdit, newSet);
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoSet(author, program, dispSetToEdit.getGuid(), newSet);
      }
      return reportUrl;
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

   private boolean editDispoItems(DispoProgram program, Collection<DispoItem> dispoItems, boolean resetRerunFlag) {
      boolean wasUpdated = false;

      ArtifactReadable author = getQuery().findUser();
      getWriter().updateDispoItems(author, program, dispoItems, resetRerunFlag);
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
         JSONArray annotationsList = dispoItem.getAnnotationsList();
         JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();
         try {
            DispoAnnotationData origAnnotation =
               DispoUtil.jsonObjToDispoAnnotationData(DispoUtil.getById(annotationsList, annotationId));
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

            JSONObject annotationAsJsonObject = DispoUtil.annotationToJsonObj(newAnnotation);
            annotationsList.put(indexOfAnnotation, annotationAsJsonObject);

            ArtifactReadable author = getQuery().findUser();
            DispoItemData modifiedDispoItem = DispoUtil.itemArtToItemData(getDispoItemById(program, itemId), true);

            modifiedDispoItem.setAnnotationsList(annotationsList);
            modifiedDispoItem.setStatus(dispoConnector.getItemStatus(modifiedDispoItem));
            getWriter().updateDispoItem(author, program, dispoItem.getGuid(), modifiedDispoItem);

            wasUpdated = true;
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoAnnotation(DispoProgram program, String itemId, String annotationId, String userName) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      if (dispoItem != null && dispoItem.getAssignee().equalsIgnoreCase(userName)) {
         JSONArray annotationsList = dispoItem.getAnnotationsList();
         JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();
         try {
            DispoAnnotationData annotationToRemove =
               DispoUtil.jsonObjToDispoAnnotationData(DispoUtil.getById(annotationsList, annotationId));
            annotationToRemove.disconnect();

            // collapse list so there are no gaps
            JSONArray newAnnotationsList = collapseList(annotationsList, annotationToRemove.getIndex());

            DispoItem updatedItem = dataFactory.createUpdatedItem(newAnnotationsList, discrepanciesList);

            ArtifactReadable author = getQuery().findUser();
            getWriter().updateDispoItem(author, program, dispoItem.getGuid(), updatedItem);
            wasUpdated = true;
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      return wasUpdated;
   }

   @Override
   public List<IOseeBranch> getDispoPrograms() {
      return getQuery().getDispoBranches();
   }

   @Override
   public List<DispoSet> getDispoSets(DispoProgram program) throws OseeCoreException {
      return getQuery().findDispoSets(program);
   }

   @Override
   public DispoSet getDispoSetById(DispoProgram program, String setId) throws OseeCoreException {
      return getQuery().findDispoSetsById(program, setId);
   }

   @Override
   public List<DispoItem> getDispoItems(DispoProgram program, String setArtId) {
      return getQuery().findDipoItems(program, setArtId);
   }

   @Override
   public DispoItem getDispoItemById(DispoProgram program, String itemId) {
      return getQuery().findDispoItemById(program, itemId);
   }

   @Override
   public Collection<DispoItem> getDispoItemByAnnotationText(DispoProgram program, String setId, String keyword) {
      return getQuery().findDispoItemByAnnoationText(program, setId, keyword);
   }

   @Override
   public List<DispoAnnotationData> getDispoAnnotations(DispoProgram program, String itemId) {
      List<DispoAnnotationData> toReturn = new ArrayList<>();
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      JSONArray annotationsList = dispoItem.getAnnotationsList();
      try {
         for (int i = 0; i < annotationsList.length(); i++) {
            toReturn.add(DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(i)));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return toReturn;
   }

   @Override
   public DispoAnnotationData getDispoAnnotationById(DispoProgram program, String itemId, String annotationId) {
      DispoAnnotationData toReturn = new DispoAnnotationData();
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      JSONArray annotationsList = dispoItem.getAnnotationsList();
      JSONObject annotationInList = DispoUtil.getById(annotationsList, annotationId);
      if (annotationInList != null) {
         toReturn = DispoUtil.jsonObjToDispoAnnotationData(annotationInList);

      } else {
         toReturn = null;
      }
      return toReturn;
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

   private String runOperation(DispoProgram program, DispoSet setToEdit, DispoSetData newSet) {
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
                  report.addNewItem(item.getName());
               } else {
                  itemsToEdit.add(item);
               }
            }

            if (itemsToCreate.size() > 0) {
               createDispoItems(program, setToEdit.getGuid(), itemsToCreate);
            }
            if (itemsToEdit.size() > 0) {
               editDispoItems(program, itemsToEdit, true);
            }

         } catch (Exception ex) {
            throw new OseeCoreException(ex);
         }
      } else if (operation.equals("cleanPCRTypes")) {
         List<DispoItem> dispoItems = getDispoItems(program, setToEdit.getGuid());
         List<DispoItem> toModify = DispoSetDataCleaner.cleanUpPcrTypes(dispoItems);

         getWriter().updateDispoItems(author, program, toModify, false);
      }

      // Create the Note to document the Operation
      JSONArray oldNotes = setToEdit.getNotesList();
      JSONArray newNotes = dataFactory.mergeJsonArrays(oldNotes, generateOperationNotes(operation));
      newSet.setNotesList(newNotes);

      // Generate report
      String reportUrl = generateReportArt(program, author, report, operation);
      return reportUrl;
   }

   private String generateReportArt(DispoProgram program, ArtifactReadable author, OperationReport report, String title) {
      StringBuilder sb = new StringBuilder();
      sb.append(new Date().toString());
      sb.append("\n");
      Map<String, String> itemToSummaryMap = report.getItemToSummaryMap();
      sb.append("*******NEW ITEMS*********\n");
      for (String newItem : report.getNewItems()) {
         sb.append(newItem);
         sb.append("\n");
      }

      sb.append("\n\n*******MODIFIED ITEMS*********\n");
      for (Entry<String, String> modifiedItem : itemToSummaryMap.entrySet()) {
         sb.append(">>>>>>");
         sb.append(modifiedItem.getKey());
         sb.append(" - ");
         sb.append(modifiedItem.getValue());
         sb.append("\n");
         sb.append("\n");
      }
      return getWriter().createDispoReport(program, author, sb.toString(), title);
   }

   private HashMap<String, DispoItem> getItemsMap(DispoProgram program, DispoSet set) {
      HashMap<String, DispoItem> toReturn = new HashMap<>();
      List<DispoItem> dispoItems = getDispoItems(program, set.getGuid());
      for (DispoItem item : dispoItems) {
         toReturn.put(item.getName(), item);
      }
      return toReturn;
   }

   private JSONArray generateOperationNotes(String operation) {
      Note operationNote = new Note();
      Date date = new Date();
      operationNote.setDateString(date.toString());
      operationNote.setType("SYSTEM");
      operationNote.setContent(operation);
      JSONObject operationNoteAsJson = new JSONObject(operationNote);
      return new JSONArray(Collections.singleton(operationNoteAsJson));
   }

   private JSONArray collapseList(JSONArray oldList, int indexRemoved) throws JSONException {
      // JSONArray's remove(index) leaves a gap so this method was created to get around that
      // If the implementation is changed and remove(index) collapses the list, then this method can be removed
      JSONArray newList = new JSONArray();
      for (int i = 0; i < indexRemoved; i++) {
         newList.put(i, oldList.getJSONObject(i));
      }
      for (int i = indexRemoved + 1; i < oldList.length(); i++) {
         JSONObject annotationObject = oldList.getJSONObject(i);
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotationObject);
         annotation.setIndex(annotation.getIndex() - 1);
         newList.put(annotation.getIndex(), DispoUtil.annotationToJsonObj(annotation));
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
   public String copyDispoSetCoverage(long sourceBranch, String sourceCoverageGuid, DispoProgram destDispProgram, DispoSet destination, CopySetParams params) {
      Map<String, ArtifactReadable> coverageUnits = getQuery().getCoverageUnits(sourceBranch, sourceCoverageGuid);
      List<DispoItem> destItems = getDispoItems(destDispProgram, destination.getGuid());

      OperationReport report = new OperationReport();

      CoverageAdapter coverageAdapter = new CoverageAdapter(dispoConnector);
      List<DispoItem> copyData = coverageAdapter.copyData(coverageUnits, destItems, report);

      if (!copyData.isEmpty()) {
         editDispoItems(destDispProgram, copyData, false);
      }

      return generateReportArt(destDispProgram, getQuery().findUser(), report, "Import Coverage Package");
   }

   @Override
   public String copyDispoSet(DispoProgram program, DispoSet destination, DispoProgram sourceProgram, DispoSet sourceSet, CopySetParams params) {
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

      if (!namesToToEditItems.isEmpty()) {
         editDispoItems(program, namesToToEditItems.values(), false);
      }

      return generateReportArt(program, getQuery().findUser(), report, "Copy Dispositions");
   }

   @Override
   public DispoConfig getDispoConfig(DispoProgram program) {
      return getQuery().findDispoConfig(program);
   }
}
