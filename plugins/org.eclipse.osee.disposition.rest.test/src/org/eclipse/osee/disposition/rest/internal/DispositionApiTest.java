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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Angel Avila
 */
public class DispositionApiTest {

   @Mock
   private Storage storage;
   @Mock
   private StorageProvider storageProvider;
   @Mock
   private IOseeBranch mockBranch;
   @Mock
   private DispoSet dispoSet;
   @Mock
   private DispoItem dispoItem;
   @Mock
   private ArtifactReadable author;
   @Mock
   private DispoProgram program;
   @Mock
   private Identifiable<String> setId;
   @Mock
   private Identifiable<String> itemId;
   @Mock
   private ArtifactId mockArtId;
   @Mock
   private DispoSetArtifact dispoSetArtifact;
   @Mock
   private JSONArray jsonArray;
   @Mock
   private JSONObject jsonObject;
   @Mock
   private JSONObject mockAnnotations;
   @Mock
   private Iterator<String> mockKeys;
   @Mock
   private Date mockDate;
   @Mock
   private DispoDataFactory dataFactory;
   @Mock
   private DispoConnector dispoConnector;

   DispoApiImpl dispoApi = new DispoApiImpl();

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      when(program.getGuid()).thenReturn("abcdef");
      when(program.getUuid()).thenReturn(23L);
      when(setId.getGuid()).thenReturn("ghijkl");
      when(itemId.getGuid()).thenReturn("mnopqr");
      when(mockArtId.getGuid()).thenReturn("artIdabc");

      when(mockBranch.getName()).thenReturn("branchName");
      when(storage.findUser()).thenReturn(author);
      when(storageProvider.get()).thenReturn(storage);

      dispoApi.setStorageProvider(storageProvider);
      dispoApi.setDataFactory(dataFactory);
      dispoApi.setDispoConnector(dispoConnector);

   }

   @Test
   public void testGetDispoProgramById() {
      when(storage.findProgramId(program)).thenAnswer(newAnswer(mockBranch));
      IOseeBranch actual = dispoApi.getDispoProgramById(program);
      assertEquals(mockBranch, actual);
   }

   private <T> Answer<T> newAnswer(final T object) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }

   @Test
   public void testGetDispoPrograms() {
      ResultSet<IOseeBranch> programsSet = ResultSets.singleton(mockBranch);
      when(storage.findBaselineBranches()).thenAnswer(newAnswer(programsSet));
      ResultSet<IOseeBranch> actual = dispoApi.getDispoPrograms();
      assertEquals(programsSet.iterator().next(), actual.iterator().next());
   }

   @Test
   public void testGetDispoSets() {
      ResultSet<DispoSet> dispoSetArts = ResultSets.singleton(dispoSet);
      when(storage.findDispoSets(program)).thenAnswer(newAnswer(dispoSetArts));

      when(dispoSet.getName()).thenReturn("name");
      when(dispoSet.getImportPath()).thenReturn("path");
      when(dispoSet.getNotesList()).thenReturn(jsonArray);
      when(dispoSet.getGuid()).thenReturn("setGuid");
      when(dispoSet.getStatusCount()).thenReturn("count");

      ResultSet<DispoSetData> actualResultSet = dispoApi.getDispoSets(program);
      DispoSetData actualData = actualResultSet.iterator().next();
      assertEquals("setGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals("path", actualData.getImportPath());
      assertEquals(jsonArray, actualData.getNotesList());
      assertEquals("count", actualData.getStatusCount());
   }

   @Test
   public void testGetDispoSetById() {
      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(dispoSet);
      when(dispoSet.getName()).thenReturn("name");
      when(dispoSet.getImportPath()).thenReturn("path");
      when(dispoSet.getNotesList()).thenReturn(jsonArray);
      when(dispoSet.getGuid()).thenReturn("setGuid");
      when(dispoSet.getStatusCount()).thenReturn("count");

      DispoSetData actual = dispoApi.getDispoSetById(program, setId.getGuid());
      assertEquals("setGuid", actual.getGuid());
      assertEquals("name", actual.getName());
      assertEquals("path", actual.getImportPath());
      assertEquals(jsonArray, actual.getNotesList());
      assertEquals("count", actual.getStatusCount());
   }

   @Test
   public void testGetDispoItems() {
      ResultSet<DispoItem> dispoItemArts = ResultSets.singleton(dispoItem);
      when(storage.findDipoItems(program, setId.getGuid())).thenReturn(dispoItemArts);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      ResultSet<DispoItemData> actualResultSet = dispoApi.getDispoItems(program, setId.getGuid());
      DispoItemData actualData = actualResultSet.iterator().next();
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(jsonArray, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void testGetDispoItemById() {
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      DispoItemData actualData = dispoApi.getDispoItemById(program, itemId.getGuid());
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(jsonArray, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void getDispoAnnotations() throws JSONException {
      String annotId = "id";
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(mockAnnotations.keys()).thenReturn(mockKeys);
      when(mockKeys.hasNext()).thenReturn(true, false);
      when(mockKeys.next()).thenReturn(annotId);
      when(mockAnnotations.getJSONObject(annotId)).thenReturn(jsonObject);
      when(jsonObject.has("id")).thenReturn(true);
      when(jsonObject.getString("id")).thenReturn(annotId);
      when(jsonObject.has("locationRefs")).thenReturn(true);
      when(jsonObject.getString("locationRefs")).thenReturn("1-10");

      ResultSet<DispoAnnotationData> actualResultSet = dispoApi.getDispoAnnotations(program, itemId.getGuid());
      DispoAnnotationData actualData = actualResultSet.iterator().next();

      assertEquals(annotId, actualData.getId());
      assertEquals("1-10", actualData.getLocationRefs());
   }

   @Test
   public void getDispoAnnotationByIndex() throws JSONException {
      String idOfAnnot = "idW";
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(mockAnnotations.has(idOfAnnot)).thenReturn(true);
      when(mockAnnotations.getJSONObject(idOfAnnot)).thenReturn(jsonObject);
      when(jsonObject.has("id")).thenReturn(true);
      when(jsonObject.getString("id")).thenReturn(idOfAnnot);
      when(jsonObject.has("locationRefs")).thenReturn(true);
      when(jsonObject.getString("locationRefs")).thenReturn("1-10");
      DispoAnnotationData actualData = dispoApi.getDispoAnnotationByIndex(program, itemId.getGuid(), idOfAnnot);

      assertEquals(idOfAnnot, actualData.getId());
      assertEquals("1-10", actualData.getLocationRefs());
   }

   // Writers
   @Test
   public void testCreateDispositionSet() {
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setImportPath("C:\\");
      descriptor.setName("Test Disposition");

      DispoSetData setFromDescriptor = new DispoSetData();
      when(dataFactory.creteSetDataFromDescriptor(descriptor)).thenReturn(setFromDescriptor);

      when(storage.createDispoSet(author, program, setFromDescriptor)).thenReturn(mockArtId);
      Identifiable<String> createDispoSetId = dispoApi.createDispoSet(program, descriptor);
      assertEquals(mockArtId, createDispoSetId);
   }

   @Test
   public void testCreateDispositionItem() {
      ArtifactReadable x = null;
      DispoProgram programUuid = program;
      String setUuid = setId.getGuid();

      DispoItemData dispoItemData = new DispoItemData();
      dispoItemData.setGuid("itemId");
      dispoItemData.setName("Item Name");

      when(storage.findDispoSetsById(programUuid, setUuid)).thenReturn(dispoSet);
      when(storage.createDispoItem(author, programUuid, dispoSet, dispoItemData, x)).thenReturn(mockArtId);

      Identifiable<String> actual = dispoApi.createDispoItem(programUuid, setUuid, dispoItemData);

      assertEquals(mockArtId, actual);
   }

   @Test
   public void testCreateDispositionAnnotation() {
      ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
      String expectedId = "dfs";
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dataFactory.getNewId()).thenReturn(expectedId);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);
      when(dataFactory.createUpdatedItem(eq(jsonObject), eq(jsonArray), Matchers.anyBoolean())).thenReturn(dispoItem);
      when(dispoConnector.connectAnnotation(annotationToCreate, jsonArray)).thenReturn(false);
      annotationToCreate.setIsConnected(true); //Assume this Annotation was connected 

      // Only need to createUpdatedItem with updateStatus = True when annotation is valid and current status is INCOMPLETE 
      annotationToCreate.setResolution("VALID");
      when(dispoItem.getStatus()).thenReturn("COMPLETE"); // captor(0)
      String acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate);
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("PASS"); // captor(1)
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate);
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("INCOMPLETE"); // captor(2)
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate);
      assertEquals(expectedId, acutal);

      annotationToCreate.setResolution("INVALID"); // captor(3)
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate);
      assertEquals(expectedId, acutal);

      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(null); // shouldn't call dataFactory method
      acutal = dispoApi.createDispoAnnotation(program, itemId.getGuid(), annotationToCreate);
      assertEquals("", acutal);

      verify(dispoConnector, times(3)).connectAnnotation(annotationToCreate, jsonArray);// Only tried to connect 3 times, excluded when annotations was invalid
      verify(dataFactory, times(4)).createUpdatedItem(eq(mockAnnotations), eq(jsonArray), captor.capture());
      List<Boolean> booleanList = captor.getAllValues();
      assertFalse(booleanList.get(0));
      assertFalse(booleanList.get(1));
      assertTrue(booleanList.get(2));
      assertFalse(booleanList.get(3));
   }

   @Test
   public void testEditDispoSet() {
      ArgumentCaptor<JSONArray> captor = ArgumentCaptor.forClass(JSONArray.class);
      DispoSetData newSet = new DispoSetData();

      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(dispoSet);
      when(dispoSet.getNotesList()).thenReturn(jsonArray);

      boolean actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertTrue(actual);

      JSONArray setToEditNotes = new JSONArray();
      newSet.setNotesList(setToEditNotes);
      actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertTrue(actual);
      // Only should have merged Json Arrays once since the first newSet didn't have a Json Array
      verify(dataFactory, times(1)).mergeJsonArrays(eq(jsonArray), captor.capture());

      when(storage.findDispoSetsById(program, setId.getGuid())).thenReturn(null);
      actual = dispoApi.editDispoSet(program, setId.getGuid(), newSet);
      assertFalse(actual);
   }

   @Test
   public void testEditDispoItem() {
      ArgumentCaptor<JSONArray> captor = ArgumentCaptor.forClass(JSONArray.class);
      DispoItemData newItem = new DispoItemData();

      when(storage.findDispoItemById(program, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);

      boolean actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertTrue(actual);

      JSONArray discrepanciesList = new JSONArray();
      newItem.setDiscrepanciesList(discrepanciesList);
      actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertTrue(actual);
      // Only should have merged Json Arrays once since the first newSet didn't have a Json Array
      verify(dataFactory, times(1)).mergeJsonArrays(eq(jsonArray), captor.capture());

      newItem.setAnnotationsList(jsonObject);
      actual = dispoApi.editDispoItem(program, itemId.getGuid(), newItem);
      assertFalse(actual);
   }

   @Test
   public void editDispoAnnotation() throws JSONException {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      DispoProgram programUuid = program;
      String itemUuid = itemId.getGuid();
      String expectedId = "1";

      when(storage.findDispoItemById(programUuid, itemUuid)).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(jsonObject);
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);
      when(jsonObject.getJSONObject(expectedId)).thenReturn(jsonObject);
      // mocks for data util translation
      when(jsonObject.has("id")).thenReturn(false);
      when(jsonObject.has("locationRefs")).thenReturn(false);
      when(jsonObject.has("idsOfCoveredDiscrepancies")).thenReturn(false);
      when(jsonObject.has("notesList")).thenReturn(false);
      when(jsonObject.has("isResolutionValid")).thenReturn(true);
      when(jsonObject.getBoolean("isResolutionValid")).thenReturn(true); // We'll have the old annotation have a valid resolution to start
      // end

      // add location Ref to newAnnotation so that disconnect and connect are invoked
      newAnnotation.setLocationRefs("1-10");
      boolean actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation);
      assertTrue(actual);

      // reset loc ref to null and set new (invalid) resolution 
      newAnnotation.setLocationRefs(null);
      newAnnotation.setResolution("PCR 13"); // Since PCR validation isn't hooked up yet, only valid resolution is "VALID" 
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation);
      assertTrue(actual);

      // set to invalid, make sure connect is not invoked
      newAnnotation.setResolution("VALID"); // Since PCR validation isn't hooked up yet, only valid resolution is "VALID" 
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation);
      assertTrue(actual);

      //  notes are the only thing being modified, no need to disconnect or connect
      newAnnotation.setLocationRefs(null);
      newAnnotation.setResolution(null);
      JSONArray newNotes = new JSONArray();
      newAnnotation.setNotesList(newNotes);
      actual = dispoApi.editDispoAnnotation(program, itemId.getGuid(), expectedId, newAnnotation);
      assertTrue(actual);

      verify(dispoConnector, times(3)).disconnectAnnotation(any(DispoAnnotationData.class), eq(jsonArray));
      verify(dispoConnector, times(2)).connectAnnotation(any(DispoAnnotationData.class), eq(jsonArray));
      verify(dataFactory, times(1)).mergeJsonArrays(any(JSONArray.class), any(JSONArray.class));
   }

   @Test
   public void deletDispoAnnotation() throws JSONException {
      DispoProgram programUuid = program;
      String itemUuid = itemId.getGuid();
      String expectedId = "1";

      when(storage.findDispoItemById(programUuid, itemUuid)).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(jsonObject);
      when(dispoItem.getDiscrepanciesList()).thenReturn(jsonArray);
      // mocks for data util translation
      when(jsonObject.has("id")).thenReturn(false);
      when(jsonObject.has("locationRefs")).thenReturn(false);
      when(jsonObject.has("idsOfCoveredDiscrepancies")).thenReturn(false);
      when(jsonObject.has("isValid")).thenReturn(false);
      when(jsonObject.has("notesList")).thenReturn(false);
      // end
      // If the annotation being removed is invalid then createUpdatedItem should be called with 'false'
      JSONObject annotationInvalid = new JSONObject();
      annotationInvalid.put("isValid", false);
      when(jsonObject.getJSONObject(expectedId)).thenReturn(annotationInvalid);

      boolean actual = dispoApi.deleteDispoAnnotation(program, itemId.getGuid(), expectedId);
      verify(dispoConnector).disconnectAnnotation(any(DispoAnnotationData.class), eq(jsonArray));
      verify(jsonObject).remove(expectedId);
      verify(dataFactory).createUpdatedItem(eq(jsonObject), eq(jsonArray), eq(false));
      assertTrue(actual);

      // 
      JSONObject annotationValid = new JSONObject();
      annotationValid.put("isValid", true);
      when(jsonObject.getJSONObject(expectedId)).thenReturn(annotationValid);
      actual = dispoApi.deleteDispoAnnotation(program, itemId.getGuid(), expectedId);
      verify(dataFactory).createUpdatedItem(eq(jsonObject), eq(jsonArray), eq(true));
      assertTrue(actual);
   }
}
