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
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.util.DispoHtmlWriter;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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
   private DispoHtmlWriter htmlWriter;
   @Mock
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   @Mock
   private ArtifactReadable setArt;
   @Mock
   private DispoProgram program;

   private DispoItemResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoItemResource(dispositionApi, htmlWriter, program, "setId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
      when(setArt.getGuid()).thenReturn("setId");
   }

   @Test
   public void testGetAllAsHtml() throws Exception {
      // No Items
      List<DispoItem> emptyResultSet = new ArrayList<DispoItem>();
      when(dispositionApi.getDispoItems(program, "setId")).thenReturn(emptyResultSet);
      Response noItemsResponse = resource.getAllDispoItems();
      assertEquals(Response.Status.OK.getStatusCode(), noItemsResponse.getStatus());

      DispoItem item = new DispoItemData();
      List<DispoItem> resultSet = Collections.singletonList(item);

      when(dispositionApi.getDispoItems(program, "setId")).thenReturn(resultSet);
      Response oneSetResponse = resource.getAllDispoItems();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
   }

   @Test
   public void testGetSingleSetAsJson() {
      // No items
      when(dispositionApi.getDispoItemById(program, id2.getGuid())).thenReturn(null);
      Response noItemsResponse = resource.getDispoItemsByIdJson(id2.getGuid());
      String messageActual = (String) noItemsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noItemsResponse.getStatus());
      assertEquals(DispoMessages.Item_NotFound, messageActual);

      DispoItemData expectedItem = new DispoItemData();
      expectedItem.setGuid(id1.getGuid());
      expectedItem.setName("Item");
      expectedItem.setNeedsRerun(false);
      expectedItem.setTotalPoints("4");
      when(dispositionApi.getDispoItemById(program, expectedItem.getGuid())).thenReturn(expectedItem);
      Response oneSetResponse = resource.getDispoItemsByIdJson(expectedItem.getGuid());
      DispoItemData returnedItem = (DispoItemData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedItem.getGuid(), returnedItem.getGuid());
   }

   @Test
   public void testPut() {
      DispoItemData newItem = new DispoItemData();
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.editDispoItem(program, id1.getGuid(), newItem)).thenReturn(true);
      Response response = resource.putDispoItem(id1.getGuid(), newItem);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.editDispoItem(program, id1.getGuid(), newItem)).thenReturn(false);
      response = resource.putDispoItem(id1.getGuid(), newItem);
      String returnedMessage = (String) response.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      assertEquals(DispoMessages.Item_NotFound, returnedMessage);
   }

   @Test
   public void testDelete() {
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.deleteDispoItem(program, id1.getGuid())).thenReturn(true);
      Response response = resource.deleteDispoItem(id1.getGuid());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoItem(program, id1.getGuid())).thenReturn(false);
      response = resource.deleteDispoItem(id1.getGuid());
      String returnedMessage = (String) response.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      assertEquals(DispoMessages.Item_NotFound, returnedMessage);
   }
}
