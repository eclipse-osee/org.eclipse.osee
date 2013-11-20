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
package org.eclipse.osee.disposition.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.resources.AnnotationResource;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */
public class AnnotationResourceTest {

   @Mock
   private DispoApi dispositionApi;
   @Mock
   private HtmlWriter htmlWriter;
   @Mock
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   int mockIndex = 0;

   private AnnotationResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new AnnotationResource(dispositionApi, htmlWriter, "branchId", "setId", "itemId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
   }

   @Test
   public void testValidPost() {
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      annotationToCreate.setLocationRefs("1-10");

      when(dispositionApi.createDispoAnnotation("branchId", "setId", "itemId", annotationToCreate)).thenReturn(
         mockIndex);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setLocationRefs(annotationToCreate.getLocationRefs());
      expectedAnnotation.setId(mockIndex);
      when(dispositionApi.getDispoAnnotationByIndex("branchId", id1.getGuid(), mockIndex)).thenReturn(
         expectedAnnotation);

      Response postResponse = resource.postDispoAnnotation(annotationToCreate);
      DispoAnnotationData returnedEntity = (DispoAnnotationData) postResponse.getEntity();
      assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
      assertEquals(mockIndex, returnedEntity.getId());
      assertEquals("1-10", returnedEntity.getLocationRefs());
   }

   @Test
   public void testPostBadParameters() {
      // Try to do post with invalid name
      DispoAnnotationData badAnnotationToCreate = new DispoAnnotationData();
      badAnnotationToCreate.setLocationRefs("");
      Response postResponseBadName = resource.postDispoAnnotation(badAnnotationToCreate);
      String returnedEntityBadName = (String) postResponseBadName.getEntity();
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponseBadName.getStatus());
      assertEquals(DispoMessages.Annotation_EmptyLocRef, returnedEntityBadName);
   }

   @Test
   public void testGetAllAsHtml() {
      // No Annotations
      when(dispositionApi.getDispoAnnotations("branchId", "itemId")).thenReturn(
         new ResultSetList<DispoAnnotationData>());
      Response noAnnotationsReponse = resource.getAllDispoAnnotations();
      String messageActual = (String) noAnnotationsReponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noAnnotationsReponse.getStatus());
      assertEquals(DispoMessages.Annotation_NoneFound, messageActual);

      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setId(mockIndex);
      annotation.setLocationRefs("1-10");
      ResultSetList<DispoAnnotationData> resultSet =
         new ResultSetList<DispoAnnotationData>(Collections.singletonList(annotation));

      when(dispositionApi.getDispoAnnotations("branchId", "itemId")).thenReturn(resultSet);
      when(htmlWriter.createDispositionPage("Annotations", resultSet)).thenReturn("htmlFromWriter");
      Response oneSetResponse = resource.getAllDispoAnnotations();
      String html = (String) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals("htmlFromWriter", html);
   }

   @Test
   public void testGetSingleSetAsJson() {
      // No items
      when(dispositionApi.getDispoAnnotationByIndex("branchId", "itemId", mockIndex)).thenReturn(null);
      Response noAnnotationsResponse = resource.getAnnotationByIdJson(mockIndex);
      String messageActual = (String) noAnnotationsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noAnnotationsResponse.getStatus());
      assertEquals(DispoMessages.Annotation_NotFound, messageActual);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setId(mockIndex);
      expectedAnnotation.setLocationRefs("1-10");
      when(dispositionApi.getDispoAnnotationByIndex("branchId", "itemId", expectedAnnotation.getId())).thenReturn(
         expectedAnnotation);
      Response oneSetResponse = resource.getAnnotationByIdJson(expectedAnnotation.getId());
      DispoAnnotationData returnedItem = (DispoAnnotationData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedAnnotation, returnedItem);
   }

   @Test
   public void testGetSingleSetAsHtml() {
      // No Items
      when(dispositionApi.getDispoAnnotationByIndex("branchId", "itemId", mockIndex)).thenReturn(null);
      Response noItemsResponse = resource.getAnnotationByIdHtml(mockIndex);
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Annotation_NotFound, messageActual);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setId(mockIndex);
      expectedAnnotation.setLocationRefs("1-10");
      JSONArray notes = new JSONArray();
      expectedAnnotation.setNotesList(notes);
      when(dispositionApi.getDispoAnnotationByIndex("branchId", "itemId", expectedAnnotation.getId())).thenReturn(
         expectedAnnotation);
      when(htmlWriter.createDispoPage(expectedAnnotation.getName(), "", "", "[]")).thenReturn("htmlFromWriter");
      Response response = resource.getAnnotationByIdHtml(expectedAnnotation.getId());
      String returnedHtml = (String) response.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      assertEquals("htmlFromWriter", returnedHtml);
   }

   @Test
   public void testPut() {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      newAnnotation.setLocationRefs("2-11");
      DispoAnnotationData annotationToEdit = new DispoAnnotationData();
      annotationToEdit.setId(mockIndex);
      when(dispositionApi.editDispoAnnotation("branchId", "itemId", annotationToEdit.getId(), newAnnotation)).thenReturn(
         true);
      Response response = resource.putDispoAnnotation(annotationToEdit.getId(), newAnnotation);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoAnnotation("branchId", "itemId", annotationToEdit.getId(), newAnnotation)).thenReturn(
         false);
      response = resource.putDispoAnnotation(annotationToEdit.getId(), newAnnotation);
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoAnnotationData annotationToEdit = new DispoAnnotationData();
      annotationToEdit.setId(mockIndex);
      when(dispositionApi.deleteDispoAnnotation("branchId", "itemId", annotationToEdit.getId())).thenReturn(true);
      Response response = resource.deleteDispoAnnotation(annotationToEdit.getId());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoAnnotation("branchId", "itemId", annotationToEdit.getId())).thenReturn(false);
      response = resource.deleteDispoAnnotation(annotationToEdit.getId());
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }
}
