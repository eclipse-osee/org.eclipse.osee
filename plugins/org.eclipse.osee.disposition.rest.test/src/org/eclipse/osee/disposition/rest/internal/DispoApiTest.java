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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.rest.internal.importer.DispoImporterFactory;
import org.eclipse.osee.disposition.rest.internal.importer.TmoImporter;
import org.eclipse.osee.disposition.rest.internal.importer.coverage.LisFileParser;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Angel Avila
 */
public class DispoApiTest {

   @Mock
   private Storage storage;
   @Mock
   private DispoResolutionValidator validator;
   @Mock
   private StorageProvider storageProvider;
   @Mock
   private IOseeBranch mockBranch;
   @Mock
   private DispoSet dispoSet;
   @Mock
   private DispoSetArtifact dispoSetArt;
   @Mock
   private DispoItem dispoItem;
   @Mock
   private ArtifactReadable author;
   @Mock
   private BranchId branch;
   @Mock
   private Identifiable<String> setId;
   @Mock
   private Identifiable<String> itemId;
   @Mock
   private DispoSetArtifact dispoSetArtifact;
   @Mock
   private JSONArray jsonArray;
   @Mock
   private List<Note> mockNotes;
   @Mock
   private JSONObject jsonObject;
   @Mock
   private DispoAnnotationData mockAnnotation;
   @Mock
   private Map<String, Discrepancy> mockDiscrepancies;
   @Mock
   private Map<String, Object> mockConfigProperties;
   @Mock
   private Discrepancy mockDiscrepancy;
   @Mock
   private List<DispoAnnotationData> mockAnnotations;
   @Mock
   private Iterator<String> mockKeys;
   @Mock
   private Date mockDate;
   @Mock
   private DispoDataFactory dataFactory;
   @Mock
   private DispoConnector dispoConnector;
   @Mock
   private DispoImporterFactory importerFactory;
   @Mock
   private Log logger;
   @Mock
   private TmoImporter tmoImporter;
   @Mock
   private LisFileParser lisImporter;

   private final Long mockArtId = 2351315L;

   DispoApiImpl dispoApi = new DispoApiImpl();

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      branch = BranchId.valueOf(23L);
      when(setId.getGuid()).thenReturn("ghijkl");
      when(itemId.getGuid()).thenReturn("mnopqr");

      when(mockBranch.getName()).thenReturn("branchName");
      when(storage.findUserByName(null)).thenReturn(author);
      when(storageProvider.get()).thenReturn(storage);

      dispoApi.setStorageProvider(storageProvider);
      dispoApi.setDataFactory(dataFactory);
      dispoApi.setDispoConnector(dispoConnector);
      dispoApi.setResolutionValidator(validator);
      dispoApi.setLogger(logger);

      dispoApi.start(mockConfigProperties);
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
      List<BranchId> programsSet = Collections.singletonList(mockBranch);
      when(storage.getDispoBranches()).thenAnswer(newAnswer(programsSet));
      List<IOseeBranch> actual = dispoApi.getDispoPrograms();
      assertEquals(programsSet.iterator().next(), actual.iterator().next());
   }

   @Test
   public void testGetDispoSets() {
      DispoSetData set = new DispoSetData();
      set.setGuid("expected");
      List<DispoSet> sets = new ArrayList<>();
      sets.add(set);

      when(storage.findDispoSets(branch, "code")).thenReturn(sets);

      List<DispoSet> actualSets = dispoApi.getDispoSets(branch, "code");
      DispoSet actualSet = actualSets.get(0);
      assertEquals("expected", actualSet.getGuid());
   }

   @Test
   public void testGetDispoSetById() {
      when(storage.findDispoSetsById(branch, setId.getGuid())).thenReturn(dispoSet);
      when(dispoSet.getName()).thenReturn("name");
      when(dispoSet.getImportPath()).thenReturn("path");
      when(dispoSet.getNotesList()).thenReturn(mockNotes);
      when(dispoSet.getGuid()).thenReturn("setGuid");

      DispoSet actual = dispoApi.getDispoSetById(branch, setId.getGuid());
      assertEquals("setGuid", actual.getGuid());
      assertEquals("name", actual.getName());
      assertEquals("path", actual.getImportPath());
      assertEquals(mockNotes, actual.getNotesList());
   }

   @Test
   public void testGetDispoItems() {
      List<DispoItem> dispoItemArts = Collections.singletonList(dispoItem);
      when(storage.findDipoItems(branch, setId.getGuid(), true)).thenReturn(dispoItemArts);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      List<DispoItem> actualResultSet = dispoApi.getDispoItems(branch, setId.getGuid(), true);
      DispoItem actualData = actualResultSet.iterator().next();
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(mockDiscrepancies, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void testGetDispoItemById() {
      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getName()).thenReturn("name");
      when(dispoItem.getGuid()).thenReturn("itemGuid");
      when(dispoItem.getCreationDate()).thenReturn(mockDate);
      when(dispoItem.getLastUpdate()).thenReturn(mockDate);
      when(dispoItem.getStatus()).thenReturn("status");
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);

      DispoItem actualData = dispoApi.getDispoItemById(branch, itemId.getGuid());
      assertEquals("itemGuid", actualData.getGuid());
      assertEquals("name", actualData.getName());
      assertEquals(mockDate, actualData.getCreationDate());
      assertEquals(mockDate, actualData.getLastUpdate());
      assertEquals("status", actualData.getStatus());
      assertEquals(mockDiscrepancies, actualData.getDiscrepanciesList());
      assertEquals(mockAnnotations, actualData.getAnnotationsList());
   }

   @Test
   public void getDispoAnnotations() {
      String expectedId = "dsf";
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setLocationRefs("1-10");
      annotation.setId(expectedId);
      List<DispoAnnotationData> annotations = Collections.singletonList(annotation);

      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(annotations);

      List<DispoAnnotationData> actualResultSet = dispoApi.getDispoAnnotations(branch, itemId.getGuid());
      DispoAnnotationData actualData = actualResultSet.iterator().next();

      assertEquals(expectedId, actualData.getId());
      assertEquals("1-10", actualData.getLocationRefs());
   }

   @Test
   public void getDispoAnnotationByIndex() {
      String idOfAnnot = "432";
      int indexOfAnnot = 0;
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setId(idOfAnnot);
      annotation.setIndex(indexOfAnnot);
      annotation.setLocationRefs("1-10");
      List<DispoAnnotationData> annotations = new ArrayList<>();
      annotations.add(annotation);

      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAnnotationsList()).thenReturn(annotations);
      DispoAnnotationData actualData = dispoApi.getDispoAnnotationById(branch, itemId.getGuid(), idOfAnnot);
      dispoApi.getDispoAnnotationById(branch, itemId.getGuid(), idOfAnnot);

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

      when(storage.createDispoSet(author, branch, setFromDescriptor)).thenReturn(mockArtId);
      Long createDispoSetId = dispoApi.createDispoSet(branch, descriptor, author.getIdString());
      assertEquals(mockArtId, createDispoSetId);
   }

   @Test
   public void testCreateDispositionAnnotation() {
      String expectedId = "dfs";
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dataFactory.getNewId()).thenReturn(expectedId);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(mockAnnotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(dataFactory.createUpdatedItem(eq(mockAnnotations), eq(mockDiscrepancies))).thenReturn(dispoItem);
      when(dispoConnector.connectAnnotation(annotationToCreate, mockDiscrepancies)).thenReturn(false);
      annotationToCreate.setIsConnected(true); //Assume this Annotation was connected

      // Only need to createUpdatedItem with updateStatus = True when annotation is valid and current status is INCOMPLETE
      annotationToCreate.setResolution("VALID");
      when(dispoItem.getStatus()).thenReturn("COMPLETE");
      String acutal = dispoApi.createDispoAnnotation(branch, itemId.getGuid(), annotationToCreate, "name", false);
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("PASS");
      acutal = dispoApi.createDispoAnnotation(branch, itemId.getGuid(), annotationToCreate, "name", false);
      assertEquals(expectedId, acutal);

      when(dispoItem.getStatus()).thenReturn("INCOMPLETE");
      acutal = dispoApi.createDispoAnnotation(branch, itemId.getGuid(), annotationToCreate, "name", false);
      assertEquals(expectedId, acutal);

      annotationToCreate.setResolution("INVALID");
      acutal = dispoApi.createDispoAnnotation(branch, itemId.getGuid(), annotationToCreate, "name", false);
      assertEquals(expectedId, acutal);

      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(null); // shouldn't call dataFactory method
      acutal = dispoApi.createDispoAnnotation(branch, itemId.getGuid(), annotationToCreate, "name", false);
      assertEquals("", acutal);

      verify(dispoConnector, times(4)).connectAnnotation(annotationToCreate, mockDiscrepancies);// Only tried to connect 3 times, excluded when annotations was invalid
   }

   @Test
   public void testEditDispoItem() {
      DispoItemData newItem = new DispoItemData();

      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);

      boolean actual = dispoApi.editDispoItem(branch, itemId.getGuid(), newItem, author.getIdString());
      assertTrue(actual);

      Map<String, Discrepancy> discrepanciesList = new HashMap<String, Discrepancy>();
      newItem.setDiscrepanciesList(discrepanciesList);
      actual = dispoApi.editDispoItem(branch, itemId.getGuid(), newItem, author.getIdString());
      assertFalse(actual);

      newItem.setAnnotationsList(mockAnnotations);
      actual = dispoApi.editDispoItem(branch, itemId.getGuid(), newItem, author.getIdString());
      assertFalse(actual);
   }

   @Test
   public void editDispoAnnotation() {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      String itemUuid = itemId.getGuid();
      String expectedId = "faf";

      List<DispoAnnotationData> annotations = new ArrayList<>();
      DispoAnnotationData origAnnotation = new DispoAnnotationData();
      origAnnotation.setId(expectedId);
      origAnnotation.setLocationRefs("5-10");
      origAnnotation.setResolution("resOrig");
      origAnnotation.setIsResolutionValid(true);
      origAnnotation.setResolutionType("CODE");
      annotations.add(origAnnotation);

      when(storage.findDispoItemById(branch, itemUuid)).thenReturn(dispoItem);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(annotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      when(mockDiscrepancies.get(expectedId)).thenReturn(mockDiscrepancy);
      // end

      // First with location refs, resolution type and resolution the same
      newAnnotation.setId(expectedId);
      newAnnotation.setLocationRefs("5-10");
      newAnnotation.setResolution("resOrig");
      newAnnotation.setResolutionType("CODE");
      boolean actual = dispoApi.editDispoAnnotation(branch, itemId.getGuid(), expectedId, newAnnotation, "name", false);
      assertTrue(actual);
      annotations.set(0, origAnnotation);
      // Now change Location Refs, disconnector should be called
      newAnnotation.setLocationRefs("1-10");
      actual = dispoApi.editDispoAnnotation(branch, itemId.getGuid(), expectedId, newAnnotation, "name", false);
      assertTrue(actual);
      annotations.set(0, origAnnotation);
      // reset the resolution and change just the resolution type, disconnector and should be called
      newAnnotation.setLocationRefs("5-10");
      newAnnotation.setResolutionType("TEST");
      actual = dispoApi.editDispoAnnotation(branch, itemId.getGuid(), expectedId, newAnnotation, "name", false);
      assertTrue(actual);
      annotations.set(0, origAnnotation);
      // Reset resolution type, only change to resolution, disconnector is called
      newAnnotation.setResolutionType("CODE");
      newAnnotation.setResolution("NEW");
      actual = dispoApi.editDispoAnnotation(branch, itemId.getGuid(), expectedId, newAnnotation, "name", false);
      assertTrue(actual);

      verify(validator, times(2)).validate(newAnnotation);
      verify(dispoConnector, times(3)).connectAnnotation(any(DispoAnnotationData.class), eq(mockDiscrepancies));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void deletDispoAnnotation() {
      String expectedId = "1";
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setId(expectedId);
      List<DispoAnnotationData> annotations = new ArrayList<>();
      annotations.add(annotation);

      when(storage.findDispoItemById(branch, itemId.getGuid())).thenReturn(dispoItem);
      when(dispoItem.getAssignee()).thenReturn("name");
      when(dispoItem.getAnnotationsList()).thenReturn(annotations);
      when(dispoItem.getDiscrepanciesList()).thenReturn(mockDiscrepancies);
      // end
      // If the annotation being removed is invalid then createUpdatedItem should be called with 'false'
      DispoAnnotationData annotationInvalid = new DispoAnnotationData();
      annotationInvalid.setIsResolutionValid(false);

      boolean actual = dispoApi.deleteDispoAnnotation(branch, itemId.getGuid(), expectedId, "name", false);
      assertTrue(actual);

      annotations.add(annotation);
      DispoAnnotationData annotationValid = new DispoAnnotationData();
      annotationValid.setIsResolutionValid(true);
      annotationValid.setIsConnected(true);
      annotationValid.setResolutionType("OTHER");

      when(mockAnnotations.get(0)).thenReturn(annotationValid);
      actual = dispoApi.deleteDispoAnnotation(branch, itemId.getGuid(), expectedId, "name", false);
      verify(dataFactory, times(2)).createUpdatedItem(any(List.class), eq(mockDiscrepancies));
      assertTrue(actual);
   }
}
