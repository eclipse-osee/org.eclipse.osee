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

import static org.eclipse.osee.disposition.model.DispoStrings.Item_Complete;
import static org.eclipse.osee.disposition.model.DispoStrings.Item_Pass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

public class DispoApiImpl implements DispoApi {

   private Log logger;
   private StorageProvider storageProvider;
   private DispoDataFactory dataFactory;
   private DispoConnector dispoConnector;

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

   public void start() {
      logger.trace("Starting DispoApiImpl...");
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
   public Identifiable<String> createDispoSet(String programId, DispoSetDescriptorData descriptor) {
      DispoSetData newSet = dataFactory.creteSetDataFromDescriptor(descriptor);
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoSet(author, programId, newSet);
   }

   @Override
   public Identifiable<String> createDispoItem(String programId, String setId, DispoItemData dispoItem) {
      DispoSet parentSet = getQuery().findDispoSetsById(programId, setId);
      Identifiable<String> itemId = null;
      if (parentSet != null) {
         ArtifactReadable author = getQuery().findUser();
         ArtifactReadable unassignedUser = getQuery().findUnassignedUser();
         dataFactory.initDispoItem(dispoItem);
         itemId = getWriter().createDispoItem(author, programId, parentSet, dispoItem, unassignedUser);
      }
      return itemId;
   }

   @Override
   public String createDispoAnnotation(String programId, String itemId, DispoAnnotationData annotationToCreate) {
      String idOfNewAnnotation = "";
      DispoItem dispoItem = getQuery().findDispoItemById(programId, itemId);
      if (dispoItem != null) {
         dataFactory.initAnnotation(annotationToCreate);
         idOfNewAnnotation = dataFactory.getNewId();
         annotationToCreate.setId(idOfNewAnnotation);

         boolean isValidResolution = false;
         String resolution = annotationToCreate.getResolution();
         if (resolution != null) {
            isValidResolution = validateResolution(resolution);
         }
         annotationToCreate.setIsResolutionValid(isValidResolution);

         JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
         if (isValidResolution) {
            dispoConnector.connectAnnotation(annotationToCreate, discrepanciesList);
         }
         JSONObject annotationsList = dispoItem.getAnnotationsList();
         try {
            annotationsList.put(idOfNewAnnotation, DispoUtil.annotationToJsonObj(annotationToCreate));
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }

         String currentStatus = dispoItem.getStatus();
         boolean updateStatus = false;
         if (!currentStatus.equals(Item_Complete) && !currentStatus.equals(Item_Pass) && annotationToCreate.isValid()) {
            updateStatus = true;
         }
         DispoItem updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList, updateStatus);
         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoItem(author, programId, dispoItem.getGuid(), updatedItem);
      }
      return idOfNewAnnotation;
   }

   @Override
   public boolean editDispoSet(String programId, String setId, DispoSetData newSet) throws OseeCoreException {
      boolean wasUpdated = false;
      DispoSet dispSetToEdit = getQuery().findDispoSetsById(programId, setId);
      if (dispSetToEdit != null) {
         if (newSet.getNotesList() != null) {
            JSONArray mergedNotesList =
               dataFactory.mergeJsonArrays(dispSetToEdit.getNotesList(), newSet.getNotesList());
            newSet.setNotesList(mergedNotesList);
         }

         if (newSet.getOperation() != null) {
            runOperation(dispSetToEdit, newSet);
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoSet(author, programId, dispSetToEdit.getGuid(), newSet);
         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoSet(String programId, String setId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoSet(author, programId, setId);
   }

   @Override
   public boolean editDispoItem(String programId, String itemId, DispoItemData newDispoItem) {
      boolean wasUpdated = false;
      DispoItem dispoItemToEdit = getQuery().findDispoItemById(programId, itemId);

      if (dispoItemToEdit != null && newDispoItem.getAnnotationsList() == null) { // We will not allow them to do mass edit of Annotations
         // Check to see if we are editing the discrepancies
         if (newDispoItem.getDiscrepanciesList() != null) {
            JSONArray mergedDiscrepanciesList =
               dataFactory.mergeJsonArrays(dispoItemToEdit.getDiscrepanciesList(), newDispoItem.getDiscrepanciesList());
            newDispoItem.setDiscrepanciesList(mergedDiscrepanciesList);
            newDispoItem.setStatus(dispoConnector.allDiscrepanciesAnnotated(newDispoItem));
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoItem(author, programId, dispoItemToEdit.getGuid(), newDispoItem);
         wasUpdated = true;
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoItem(String programId, String itemId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoItem(author, programId, itemId);
   }

   @Override
   public boolean editDispoAnnotation(String programId, String itemId, String annotationId, DispoAnnotationData newAnnotation) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(programId, itemId);
      if (dispoItem != null) {
         JSONObject annotationsList = dispoItem.getAnnotationsList();
         JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
         try {
            DispoAnnotationData oldAnnotation =
               DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(annotationId));

            DispoAnnotationData consolidatedAnnotation = oldAnnotation;

            // Check if newAnnotation has notes, if it does then merge with old notes
            JSONArray newNotes = newAnnotation.getNotesList();
            if (newNotes != null) {
               consolidatedAnnotation.setNotesList(dataFactory.mergeJsonArrays(oldAnnotation.getNotesList(), newNotes));
            }

            // now if the new Annotation modified the location Reference or resolution then disconnect the annotation and try to match it to discrepancies again
            String newLocationRefs = newAnnotation.getLocationRefs();
            String newResolution = newAnnotation.getResolution();
            if (newLocationRefs != null || newResolution != null) {
               if (newResolution != null) {
                  consolidatedAnnotation.setResolution(newResolution);
                  consolidatedAnnotation.setIsResolutionValid(validateResolution(newResolution));
               }
               if (newLocationRefs != null) {
                  consolidatedAnnotation.setLocationRefs(newLocationRefs);
               }

               dispoConnector.disconnectAnnotation(consolidatedAnnotation, discrepanciesList);
               if (consolidatedAnnotation.getIsResolutionValid()) {
                  dispoConnector.connectAnnotation(consolidatedAnnotation, discrepanciesList);
               }
            }

            JSONObject annotationAsJsonObject = DispoUtil.annotationToJsonObj(consolidatedAnnotation);
            annotationsList.put(annotationId, annotationAsJsonObject);

            DispoItem updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList, true);
            ArtifactReadable author = getQuery().findUser();
            getWriter().updateDispoItem(author, programId, dispoItem.getGuid(), updatedItem);
            wasUpdated = true;
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      return wasUpdated;
   }

   @Override
   public boolean deleteDispoAnnotation(String programId, String itemId, String annotationId) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(programId, itemId);
      if (dispoItem != null) {
         JSONObject annotationsList = dispoItem.getAnnotationsList();
         JSONArray discrepanciesList = dispoItem.getDiscrepanciesList();
         try {
            DispoAnnotationData annotationToRemove =
               DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(annotationId));
            // No need to update status for new item if the deleted annotation was invalid, status would remain the same
            boolean updateStatus = false;
            if (annotationToRemove.getIsConnected()) {
               updateStatus = true;
            }
            dispoConnector.disconnectAnnotation(annotationToRemove, discrepanciesList);
            annotationsList.remove(annotationId);

            DispoItem updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList, updateStatus);

            ArtifactReadable author = getQuery().findUser();
            getWriter().updateDispoItem(author, programId, dispoItem.getGuid(), updatedItem);
            wasUpdated = true;
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }
      return wasUpdated;
   }

   @Override
   public ResultSet<IOseeBranch> getDispoPrograms() {
      List<IOseeBranch> results = new ArrayList<IOseeBranch>();
      ResultSet<? extends IOseeBranch> baselineBranches = getQuery().findBaselineBranches();
      for (IOseeBranch baselinebBranch : baselineBranches) {
         results.add(baselinebBranch);
      }
      return ResultSets.newResultSet(results);
   }

   @Override
   public IOseeBranch getDispoProgramById(String programId) {
      return getQuery().findProgramId(programId);
   }

   @Override
   public ResultSet<DispoSetData> getDispoSets(String programId) throws OseeCoreException {
      return translateAllToDispoSetData(getQuery().findDispoSets(programId));
   }

   @Override
   public DispoSetData getDispoSetById(String programId, String setId) throws OseeCoreException {
      return DispoUtil.setArtToSetData(getQuery().findDispoSetsById(programId, setId));
   }

   @Override
   public ResultSet<DispoItemData> getDispoItems(String programId, String setArtId) {
      return translateAllToDispoItemData(getQuery().findDipoItems(programId, setArtId));
   }

   @Override
   public DispoItemData getDispoItemById(String programId, String itemId) {
      DispoItemData dispositionableItem;
      DispoItem result = getQuery().findDispoItemById(programId, itemId);
      if (result != null) {
         dispositionableItem = DispoUtil.itemArtToItemData(result);
      } else {
         dispositionableItem = null;
      }
      return dispositionableItem;
   }

   @Override
   public ResultSet<DispoAnnotationData> getDispoAnnotations(String programId, String itemId) {
      List<DispoAnnotationData> toReturn = new ArrayList<DispoAnnotationData>();
      DispoItem dispoItem = getQuery().findDispoItemById(programId, itemId);
      JSONObject annotationsList = dispoItem.getAnnotationsList();
      @SuppressWarnings("unchecked")
      Iterator<String> keys = annotationsList.keys();
      try {
         while (keys.hasNext()) {
            toReturn.add(DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(keys.next())));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return ResultSets.newResultSet(toReturn);
   }

   @Override
   public DispoAnnotationData getDispoAnnotationByIndex(String programId, String itemId, String annotationId) {
      DispoAnnotationData toReturn = new DispoAnnotationData();
      DispoItem dispoItem = getQuery().findDispoItemById(programId, itemId);
      JSONObject annotationsList = dispoItem.getAnnotationsList();
      if (annotationsList.has(annotationId)) {
         try {
            toReturn = DispoUtil.jsonObjToDispoAnnotationData(annotationsList.getJSONObject(annotationId));
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      } else {
         toReturn = null;
      }
      return toReturn;
   }

   @Override
   public boolean isUniqueSetName(String programId, String name) {
      return getQuery().isUniqueSetName(programId, name);
   }

   @Override
   public boolean isUniqueItemName(String programId, String setId, String name) {
      return getQuery().isUniqueItemName(programId, setId, name);
   }

   private ResultSet<DispoItemData> translateAllToDispoItemData(ResultSet<DispoItem> list) {
      List<DispoItemData> toReturn = new ArrayList<DispoItemData>();
      for (DispoItem item : list) {
         toReturn.add(DispoUtil.itemArtToItemData(item));
      }

      return ResultSets.newResultSet(toReturn);
   }

   private ResultSet<DispoSetData> translateAllToDispoSetData(ResultSet<DispoSet> list) {
      List<DispoSetData> toReturn = new ArrayList<DispoSetData>();
      for (DispoSet set : list) {
         toReturn.add(DispoUtil.setArtToSetData(set));
      }

      return ResultSets.newResultSet(toReturn);
   }

   private void runOperation(DispoSet setToEdit, DispoSetData newSet) {
      // Add operation Functionality here
   }

   private boolean validateResolution(String resolution) {
      return resolution.equals("VALID");
      // Add PCR validation Functionality here
   }
}
