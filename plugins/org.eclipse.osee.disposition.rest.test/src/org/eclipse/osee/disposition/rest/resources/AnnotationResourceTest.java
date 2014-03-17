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
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
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
   @Mock
   private DispoProgram program;
   String mockId = "annotationID";

   private AnnotationResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new AnnotationResource(dispositionApi, htmlWriter, program, "setId", "itemId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
   }

   @Test
   public void testValidPost() {
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      annotationToCreate.setLocationRefs("1-10");
      annotationToCreate.setId(mockId);

      when(dispositionApi.createDispoAnnotation(program, "itemId", annotationToCreate)).thenReturn(mockId);
      when(dispositionApi.getDispoAnnotationByIndex(program, "itemId", mockId)).thenReturn(annotationToCreate);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setLocationRefs(annotationToCreate.getLocationRefs());
      expectedAnnotation.setId(mockId);
      when(dispositionApi.getDispoAnnotationByIndex(program, id1.getGuid(), mockId)).thenReturn(expectedAnnotation);

      Response postResponse = resource.postDispoAnnotation(annotationToCreate);
      DispoAnnotationData returnedEntity = (DispoAnnotationData) postResponse.getEntity();
      assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
      assertEquals(mockId, returnedEntity.getId());
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
      // No Annotations3
      ResultSet<DispoAnnotationData> emptyResultSet = ResultSets.emptyResultSet();
      when(dispositionApi.getDispoAnnotations(program, "itemId")).thenReturn(emptyResultSet);
      Response noAnnotationsReponse = resource.getAllDispoAnnotations();
      String messageActual = (String) noAnnotationsReponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noAnnotationsReponse.getStatus());
      assertEquals(DispoMessages.Annotation_NoneFound, messageActual);

      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setId(mockId);
      annotation.setLocationRefs("1-10");
      ResultSet<DispoAnnotationData> resultSet = ResultSets.singleton(annotation);

      when(dispositionApi.getDispoAnnotations(program, "itemId")).thenReturn(resultSet);
      when(htmlWriter.createDispositionPage("Annotations", "annotation/", resultSet)).thenReturn("htmlFromWriter");
      Response oneSetResponse = resource.getAllDispoAnnotations();
      String html = (String) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals("htmlFromWriter", html);
   }

   @Test
   public void testGetSingleAsJson() {
      // No items
      when(dispositionApi.getDispoAnnotationByIndex(program, "itemId", mockId)).thenReturn(null);
      Response noAnnotationsResponse = resource.getAnnotationByIdJson(mockId);
      String messageActual = (String) noAnnotationsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noAnnotationsResponse.getStatus());
      assertEquals(DispoMessages.Annotation_NotFound, messageActual);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setId(mockId);
      expectedAnnotation.setLocationRefs("1-10");
      when(dispositionApi.getDispoAnnotationByIndex(program, "itemId", expectedAnnotation.getId())).thenReturn(
         expectedAnnotation);
      Response oneSetResponse = resource.getAnnotationByIdJson(expectedAnnotation.getId());
      DispoAnnotationData returnedItem = (DispoAnnotationData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedAnnotation, returnedItem);
   }

   @Test
   public void testGetSingleAsHtml() {
      // No Items
      when(dispositionApi.getDispoAnnotationByIndex(program, "itemId", mockId)).thenReturn(null);
      Response noItemsResponse = resource.getAnnotationByIdHtml(mockId);
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Annotation_NotFound, messageActual);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setId(mockId);
      expectedAnnotation.setLocationRefs("1-10");
      JSONArray notes = new JSONArray();
      expectedAnnotation.setNotesList(notes);
      when(dispositionApi.getDispoAnnotationByIndex(program, "itemId", expectedAnnotation.getId())).thenReturn(
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
      annotationToEdit.setId(mockId);
      when(dispositionApi.editDispoAnnotation(program, "itemId", annotationToEdit.getId(), newAnnotation)).thenReturn(
         true);
      Response response = resource.putDispoAnnotation(annotationToEdit.getId(), newAnnotation);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoAnnotation(program, "itemId", annotationToEdit.getId(), newAnnotation)).thenReturn(
         false);
      response = resource.putDispoAnnotation(annotationToEdit.getId(), newAnnotation);
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoAnnotationData annotationToEdit = new DispoAnnotationData();
      annotationToEdit.setId(mockId);
      when(dispositionApi.deleteDispoAnnotation(program, "itemId", annotationToEdit.getId())).thenReturn(true);
      Response response = resource.deleteDispoAnnotation(annotationToEdit.getId());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoAnnotation(program, "itemId", annotationToEdit.getId())).thenReturn(false);
      response = resource.deleteDispoAnnotation(annotationToEdit.getId());
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }
}
