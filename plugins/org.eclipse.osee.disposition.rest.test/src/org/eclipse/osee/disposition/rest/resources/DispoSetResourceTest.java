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
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
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
public class DispoSetResourceTest {

   @Mock
   private DispoApi dispositionApi;
   @Mock
   private HtmlWriter htmlWriter;
   @Mock
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;

   private DispoSetResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoSetResource(dispositionApi, htmlWriter, "branchId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
   }

   @Test
   public void testDispositionSetPost() {
      // test valid post
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("Name");
      descriptor.setImportPath("c:");

      DispoSetData expected = new DispoSetData();
      expected.setGuid(id1.getGuid());
      expected.setName(descriptor.getName());
      expected.setImportPath(descriptor.getImportPath());
      when(dispositionApi.createDispoSet("branchId", descriptor)).thenReturn(id1);
      when(dispositionApi.getDispoSetById("branchId", id1.getGuid())).thenReturn(expected);
      when(dispositionApi.isUniqueSetName("branchId", descriptor.getName())).thenReturn(true);

      Response postResponse = resource.postDispoSet(descriptor);
      DispoSetData returnedEntity = (DispoSetData) postResponse.getEntity();
      assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
      assertEquals("abcdef", returnedEntity.getGuid());
      assertEquals("Name", returnedEntity.getName());
      assertEquals("c:", returnedEntity.getImportPath());
   }

   @Test
   public void testPostBadParameters() {
      // Try to do post with invalid name
      DispoSetDescriptorData badNameDescriptor = new DispoSetDescriptorData();
      badNameDescriptor.setName("");
      badNameDescriptor.setImportPath("c:");
      Response postResponseBadName = resource.postDispoSet(badNameDescriptor);
      String returnedEntityBadName = (String) postResponseBadName.getEntity();
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponseBadName.getStatus());
      assertEquals(DispoMessages.Set_EmptyNameOrPath, returnedEntityBadName);

      // Try to do post with invalid name
      DispoSetDescriptorData badPathDescriptor = new DispoSetDescriptorData();
      badPathDescriptor.setName("name");
      badPathDescriptor.setImportPath("");
      Response postResponseBadPath = resource.postDispoSet(badPathDescriptor);
      String returnedEntityBadPath = (String) postResponseBadPath.getEntity();
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponseBadPath.getStatus());
      assertEquals(DispoMessages.Set_EmptyNameOrPath, returnedEntityBadPath);
   }

   @Test
   public void testPostDuplicateName() { // test valid post
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("Name");
      descriptor.setImportPath("c:");

      when(dispositionApi.isUniqueSetName("branchId", descriptor.getName())).thenReturn(false);

      Response postResponse = resource.postDispoSet(descriptor);
      String returnedEntity = (String) postResponse.getEntity();
      assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());
      assertEquals(DispoMessages.Set_ConflictingNames, returnedEntity);
   }

   @Test
   public void testGetAllAsHtml() {
      // No Sets
      ResultSet<DispoSetData> emptyResultSet = ResultSets.emptyResultSet();
      when(dispositionApi.getDispoSets("branchId")).thenReturn(emptyResultSet);
      Response noSetsResponse = resource.getAllDispoSets();
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals(DispoMessages.Set_NoneFound, messageActual);

      DispoSetData set = new DispoSetData();
      set.setGuid(id1.getGuid());
      set.setName("Set");
      ResultSet<DispoSetData> resultSet = ResultSets.singleton(set);

      when(dispositionApi.getDispoSets("branchId")).thenReturn(resultSet);
      when(htmlWriter.createDispositionPage("Disposition Sets", resultSet)).thenReturn("htmlFromWriter");
      Response oneSetResponse = resource.getAllDispoSets();
      String html = (String) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals("htmlFromWriter", html);
   }

   @Test
   public void testGetSingleSetAsJson() {
      // No Sets
      when(dispositionApi.getDispoSetById("branchId", id2.getGuid())).thenReturn(null);
      Response noSetsResponse = resource.getDispoSetByIdJson(id2.getGuid());
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals(DispoMessages.Set_NotFound, messageActual);

      DispoSetData expectedSet = new DispoSetData();
      expectedSet.setGuid(id1.getGuid());
      expectedSet.setName("Set");
      when(dispositionApi.getDispoSetById("branchId", expectedSet.getGuid())).thenReturn(expectedSet);
      Response oneSetResponse = resource.getDispoSetByIdJson(expectedSet.getGuid());
      DispoSetData returnedSet = (DispoSetData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedSet, returnedSet);
   }

   @Test
   public void testGetSingleSetAsHtml() {
      // No Sets
      when(dispositionApi.getDispoSetById("branchId", id2.getGuid())).thenReturn(null);
      Response noSetsResponse = resource.getDispoSetByIdHtml(id2.getGuid());
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals(DispoMessages.Set_NotFound, messageActual);

      DispoSetData set = new DispoSetData();
      set.setGuid(id1.getGuid());
      set.setName("set");
      JSONArray notesArray = new JSONArray();
      set.setNotesList(notesArray);
      ResultSet<DispoItemData> emptyResultSet = ResultSets.emptyResultSet();
      ResultSet<DispoItemData> resultListItems = emptyResultSet;
      when(dispositionApi.getDispoSetById("branchId", id1.getGuid())).thenReturn(set);
      when(dispositionApi.getDispoItems("branchId", id1.getGuid())).thenReturn(resultListItems);
      String prefixPath = set.getGuid() + "/dispositionableItem/";
      String subTitle = "Dispositionable Items";
      when(htmlWriter.createDispoPage(set.getName(), prefixPath, subTitle, "[]")).thenReturn("htmlFromWriter");
      Response response = resource.getDispoSetByIdHtml(set.getGuid());
      String returnedHtml = (String) response.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      assertEquals("htmlFromWriter", returnedHtml);
   }

   @Test
   public void testPut() {
      DispoSetData newSet = new DispoSetData();
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1.getGuid());
      when(dispositionApi.editDispoSet("branchId", id1.getGuid(), newSet)).thenReturn(true);
      Response response = resource.putDispoSet(id1.getGuid(), newSet);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoSet("branchId", id1.getGuid(), newSet)).thenReturn(false);
      response = resource.putDispoSet(id1.getGuid(), newSet);
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1.getGuid());
      when(dispositionApi.deleteDispoSet("branchId", id1.getGuid())).thenReturn(true);
      Response response = resource.deleteDispoSet(id1.getGuid());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoSet("branchId", id1.getGuid())).thenReturn(false);
      response = resource.deleteDispoSet(id1.getGuid());
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }
}
