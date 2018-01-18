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
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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
   private DispoItemData dispoItem;
   @Mock
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   @Mock
   private BranchId branch;
   String mockId = "annotationID";

   private AnnotationResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new AnnotationResource(dispositionApi, branch, "setId", "itemId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
   }

   @Test
   public void testValidPost() {
      DispoAnnotationData annotationToCreate = new DispoAnnotationData();
      annotationToCreate.setLocationRefs("1-10");
      annotationToCreate.setId(mockId);

      when(dispositionApi.createDispoAnnotation(branch, "itemId", annotationToCreate, "name", false)).thenReturn(
         mockId);
      when(dispositionApi.getDispoAnnotationById(branch, "itemId", mockId)).thenReturn(annotationToCreate);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setLocationRefs(annotationToCreate.getLocationRefs());
      expectedAnnotation.setId(mockId);
      when(dispositionApi.getDispoAnnotationById(branch, id1.getGuid(), mockId)).thenReturn(expectedAnnotation);

      Response postResponse = resource.postDispoAnnotation(annotationToCreate, "name");
      DispoAnnotationData returnedEntity = (DispoAnnotationData) postResponse.getEntity();
      assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
      assertEquals(mockId, returnedEntity.getGuid());
      assertEquals("1-10", returnedEntity.getLocationRefs());
   }

   @Test
   public void testPostBadParameters() {
      // Try to do post with invalid name
      DispoAnnotationData badAnnotationToCreate = new DispoAnnotationData();
      badAnnotationToCreate.setLocationRefs("");
      Response postResponseBadName = resource.postDispoAnnotation(badAnnotationToCreate, "name");
      String returnedEntityBadName = (String) postResponseBadName.getEntity();
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponseBadName.getStatus());
      assertEquals(DispoMessages.Annotation_EmptyLocRef, returnedEntityBadName);
   }

   @Test
   public void testGetAll() {
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setId(mockId);
      annotation.setLocationRefs("1-10");
      List<DispoAnnotationData> resultSet = new ArrayList<>();
      resultSet.add(annotation);

      when(dispositionApi.getDispoAnnotations(branch, "itemId")).thenReturn(resultSet);
      Iterable<DispoAnnotationData> oneSetResponse = resource.getAllDispoAnnotations();
      DispoAnnotationData annotationFromResponse = oneSetResponse.iterator().next();
      assertEquals(mockId, annotationFromResponse.getGuid());
   }

   @Test
   public void testGetSingleAsJson() {
      // No items
      when(dispositionApi.getDispoAnnotationById(branch, "itemId", mockId)).thenReturn(null);
      DispoAnnotationData noAnnotationsResponse = resource.getAnnotationByIdJson(mockId);
      assertEquals(null, noAnnotationsResponse);

      DispoAnnotationData expectedAnnotation = new DispoAnnotationData();
      expectedAnnotation.setId(mockId);
      expectedAnnotation.setLocationRefs("1-10");
      when(dispositionApi.getDispoAnnotationById(branch, "itemId", expectedAnnotation.getGuid())).thenReturn(
         expectedAnnotation);
      DispoAnnotationData oneSetResponse = resource.getAnnotationByIdJson(expectedAnnotation.getGuid());
      assertEquals(expectedAnnotation, oneSetResponse);
   }

   @Test
   public void testPut() {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      newAnnotation.setLocationRefs("2-11");
      DispoAnnotationData annotationToEdit = new DispoAnnotationData();
      annotationToEdit.setId(mockId);
      when(dispositionApi.editDispoAnnotation(branch, "itemId", annotationToEdit.getGuid(), newAnnotation, "name",
         false)).thenReturn(true);
      when(dispositionApi.getDispoItemById(branch, "itemId")).thenReturn(dispoItem);
      when(dispoItem.getStatus()).thenReturn(DispoStrings.Item_Complete);
      Response response = resource.putDispoAnnotation(annotationToEdit.getGuid(), newAnnotation, "name");
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoAnnotation(branch, "itemId", annotationToEdit.getGuid(), newAnnotation, "name",
         false)).thenReturn(false);
      response = resource.putDispoAnnotation(annotationToEdit.getGuid(), newAnnotation, "name");
      assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoAnnotationData annotationToEdit = new DispoAnnotationData();
      annotationToEdit.setId(mockId);
      when(
         dispositionApi.deleteDispoAnnotation(branch, "itemId", annotationToEdit.getGuid(), "name", false)).thenReturn(
            true);
      when(dispositionApi.getDispoItemById(branch, "itemId")).thenReturn(dispoItem);
      when(dispoItem.getStatus()).thenReturn(DispoStrings.Item_InComplete);
      Response response = resource.deleteDispoAnnotation(annotationToEdit.getGuid(), "name");
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(
         dispositionApi.deleteDispoAnnotation(branch, "itemId", annotationToEdit.getGuid(), "name", false)).thenReturn(
            false);
      response = resource.deleteDispoAnnotation(annotationToEdit.getGuid(), "name");
      assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
   }
}
