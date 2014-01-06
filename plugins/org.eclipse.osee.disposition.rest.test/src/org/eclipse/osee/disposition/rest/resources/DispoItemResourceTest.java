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
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.HtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */
public class DispoItemResourceTest {

   @Mock
   private DispoApi dispositionApi;
   @Mock
   private HtmlWriter htmlWriter;
   @Mock
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   @Mock
   private ArtifactReadable setArt;

   private DispoItemResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoItemResource(dispositionApi, htmlWriter, "branchId", "setId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
      when(setArt.getGuid()).thenReturn("setId");
   }

   @Test
   public void testGetAllAsHtml() {
      // No Items
      ResultSet<DispoItemData> emptyResultSet = ResultSets.emptyResultSet();
      when(dispositionApi.getDispoItems("branchId", "setId")).thenReturn(emptyResultSet);
      Response noItemsResponse = resource.getAllDispoItems();
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Item_NoneFound, messageActual);

      DispoItemData item = new DispoItemData();
      item.setGuid(id1.getGuid());
      item.setName("Item");
      ResultSet<DispoItemData> resultSet = ResultSets.singleton(item);

      when(dispositionApi.getDispoItems("branchId", "setId")).thenReturn(resultSet);
      when(htmlWriter.createDispositionPage("Dispositionable Items", "item/", resultSet)).thenReturn("htmlFromWriter");
      Response oneSetResponse = resource.getAllDispoItems();
      String html = (String) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals("htmlFromWriter", html);
   }

   @Test
   public void testGetSingleSetAsJson() {
      // No items
      when(dispositionApi.getDispoItemById("branchId", id2.getGuid())).thenReturn(null);
      Response noItemsResponse = resource.getDispoItemsByIdJson(id2.getGuid());
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Item_NotFound, messageActual);

      DispoItemData expectedItem = new DispoItemData();
      expectedItem.setGuid(id1.getGuid());
      expectedItem.setName("Item");
      when(dispositionApi.getDispoItemById("branchId", expectedItem.getGuid())).thenReturn(expectedItem);
      Response oneSetResponse = resource.getDispoItemsByIdJson(expectedItem.getGuid());
      DispoItemData returnedItem = (DispoItemData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedItem, returnedItem);
   }

   @Test
   public void testGetSingleSetAsHtml() {
      // No Items
      when(dispositionApi.getDispoItemById("branchId", id2.getGuid())).thenReturn(null);
      Response noItemsResponse = resource.getDispoItemsByIdHtml(id2.getGuid());
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Item_NotFound, messageActual);

      DispoItemData item = new DispoItemData();
      item.setGuid(id1.getGuid());
      item.setName("item");
      ResultSet<DispoAnnotationData> emptyResultSet = ResultSets.emptyResultSet();
      ResultSet<DispoAnnotationData> resultAnnotations = emptyResultSet;
      when(dispositionApi.getDispoItemById("branchId", item.getGuid())).thenReturn(item);
      when(dispositionApi.getDispoAnnotations("branchId", id1.getGuid())).thenReturn(resultAnnotations);
      String prefixPath = item.getGuid() + "/annotation";
      String subTitle = "Annotations";
      when(htmlWriter.createDispoPage(item.getName(), prefixPath, subTitle, "[]")).thenReturn("htmlFromWriter");
      Response response = resource.getDispoItemsByIdHtml(item.getGuid());
      String returnedHtml = (String) response.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      assertEquals("htmlFromWriter", returnedHtml);
   }

   @Test
   public void testPut() {
      DispoItemData newItem = new DispoItemData();
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.editDispoItem("branchId", id1.getGuid(), newItem)).thenReturn(true);
      Response response = resource.putDispoItem(id1.getGuid(), newItem);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoItem("branchId", id1.getGuid(), newItem)).thenReturn(false);
      response = resource.putDispoItem(id1.getGuid(), newItem);
      String returnedMessage = (String) response.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      assertEquals(DispoMessages.Item_NotFound, returnedMessage);
   }

   @Test
   public void testDelete() {
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.deleteDispoItem("branchId", id1.getGuid())).thenReturn(true);
      Response response = resource.deleteDispoItem(id1.getGuid());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoItem("branchId", id1.getGuid())).thenReturn(false);
      response = resource.deleteDispoItem(id1.getGuid());
      String returnedMessage = (String) response.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      assertEquals(DispoMessages.Item_NotFound, returnedMessage);
   }
}
