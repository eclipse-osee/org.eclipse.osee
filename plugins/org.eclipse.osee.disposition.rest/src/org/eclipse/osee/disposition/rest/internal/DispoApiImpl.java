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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoFactory;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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
   public Identifiable<String> createDispoSet(DispoProgram program, DispoSetDescriptorData descriptor) {
      DispoSetData newSet = dataFactory.creteSetDataFromDescriptor(descriptor);
      ArtifactReadable author = getQuery().findUser();
      return getWriter().createDispoSet(author, program, newSet);
   }

   @Override
   public Identifiable<String> createDispoItem(DispoProgram program, String setId, DispoItemData dispoItem) {
      DispoSet parentSet = getQuery().findDispoSetsById(program, setId);
      Identifiable<String> itemId = null;
      if (parentSet != null) {
         ArtifactReadable author = getQuery().findUser();
         ArtifactReadable unassignedUser = getQuery().findUnassignedUser();
         dataFactory.initDispoItem(dispoItem);
         itemId = getWriter().createDispoItem(author, program, parentSet, dispoItem, unassignedUser);
      }
      return itemId;
   }

   @Override
   public String createDispoAnnotation(DispoProgram program, String itemId, DispoAnnotationData annotationToCreate) {
      String idOfNewAnnotation = "";
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      if (dispoItem != null) {
         try {
            JSONArray annotationsList = dispoItem.getAnnotationsList();
            dataFactory.initAnnotation(annotationToCreate);
            idOfNewAnnotation = dataFactory.getNewId();
            annotationToCreate.setId(idOfNewAnnotation);
            int indexOfAnnotation = annotationsList.length();
            annotationToCreate.setIndex(indexOfAnnotation);

            boolean isValidResolution = false;
            String resolution = annotationToCreate.getResolution();
            if (resolution != null) {
               isValidResolution = validateResolution(annotationToCreate);
            }
            annotationToCreate.setIsResolutionValid(isValidResolution);

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
   public boolean editDispoSet(DispoProgram program, String setId, DispoSetData newSet) throws OseeCoreException {
      boolean wasUpdated = false;
      DispoSet dispSetToEdit = getQuery().findDispoSetsById(program, setId);
      if (dispSetToEdit != null) {
         if (newSet.getNotesList() != null) {
            JSONArray mergedNotesList =
               dataFactory.mergeJsonArrays(dispSetToEdit.getNotesList(), newSet.getNotesList());
            newSet.setNotesList(mergedNotesList);
         }

         if (newSet.getOperation() != null) {
            runOperation(program, dispSetToEdit, newSet);
         }

         ArtifactReadable author = getQuery().findUser();
         getWriter().updateDispoSet(author, program, dispSetToEdit.getGuid(), newSet);
         wasUpdated = true;
      }
      return wasUpdated;
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

   @Override
   public boolean deleteDispoItem(DispoProgram program, String itemId) {
      ArtifactReadable author = getQuery().findUser();
      return getWriter().deleteDispoItem(author, program, itemId);
   }

   @Override
   public boolean editDispoAnnotation(DispoProgram program, String itemId, String annotationId, DispoAnnotationData newAnnotation) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      if (dispoItem != null) {
         JSONArray annotationsList = dispoItem.getAnnotationsList();
         JSONObject discrepanciesList = dispoItem.getDiscrepanciesList();
         try {
            DispoAnnotationData oldAnnotation =
               DispoUtil.jsonObjToDispoAnnotationData(DispoUtil.getById(annotationsList, annotationId));
            int indexOfAnnotation = oldAnnotation.getIndex();

            DispoAnnotationData consolidatedAnnotation = oldAnnotation;

            // Check if newAnnotation has notes, if it does then merge with old notes
            String newNotes = newAnnotation.getNotes();
            consolidatedAnnotation.setNotes(newNotes);

            // now if the new Annotation modified the location Reference or resolution then disconnect the annotation and try to match it to discrepancies again
            String newLocationRefs = newAnnotation.getLocationRefs();
            String newResolution = newAnnotation.getResolution();
            if (newLocationRefs != null || newResolution != null) {
               if (newResolution != null) {
                  consolidatedAnnotation.setResolution(newResolution);
                  consolidatedAnnotation.setIsResolutionValid(validateResolution(consolidatedAnnotation));
               }
               if (newLocationRefs != null) {
                  consolidatedAnnotation.setLocationRefs(newLocationRefs);
               }

               consolidatedAnnotation.disconnect();
               dispoConnector.connectAnnotation(consolidatedAnnotation, discrepanciesList);
            }

            JSONObject annotationAsJsonObject = DispoUtil.annotationToJsonObj(consolidatedAnnotation);
            annotationsList.put(indexOfAnnotation, annotationAsJsonObject);

            DispoItem updatedItem = dataFactory.createUpdatedItem(annotationsList, discrepanciesList);
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
   public boolean deleteDispoAnnotation(DispoProgram program, String itemId, String annotationId) {
      boolean wasUpdated = false;
      DispoItem dispoItem = getQuery().findDispoItemById(program, itemId);
      if (dispoItem != null) {
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
   public ResultSet<IOseeBranch> getDispoPrograms() {
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
   public List<DispoAnnotationData> getDispoAnnotations(DispoProgram program, String itemId) {
      List<DispoAnnotationData> toReturn = new ArrayList<DispoAnnotationData>();
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
   public boolean isUniqueItemName(DispoProgram program, String setId, String name) {
      return getQuery().isUniqueItemName(program, setId, name);
   }

   @Override
   public boolean isUniqueSetName(DispoProgram program, String name) {
      return getQuery().isUniqueSetName(program, name);
   }

   private void runOperation(DispoProgram program, DispoSet setToEdit, DispoSetData newSet) {
      //do nothing
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
}
