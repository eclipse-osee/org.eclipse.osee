/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   @Mock
   private ArtifactReadable setArt;
   @Mock
   private BranchId branch;

   private DispoItemResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoItemResource(dispositionApi, branch, "setId");
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
      when(setArt.getGuid()).thenReturn("setId");
   }

   @Test
   public void testGetAll() throws Exception {
      // No Items
      List<DispoItem> emptyResultSet = new ArrayList<>();
      when(dispositionApi.getDispoItems(branch, "setId", false)).thenReturn(emptyResultSet);
      Iterable<DispoItem> noItemsResponse = resource.getAllDispoItems(false);
      assertEquals(emptyResultSet, noItemsResponse);

      DispoItemData item = new DispoItemData();
      item.setAnnotationsList(new ArrayList<>());
      item.setDiscrepanciesList(new HashMap<>());
      item.setGuid(id1.getGuid());
      List<DispoItem> resultSet = Collections.singletonList((DispoItem) item);

      when(dispositionApi.getDispoItems(branch, "setId", false)).thenReturn(resultSet);
      Iterable<DispoItem> response = resource.getAllDispoItems(false);
      DispoItem itemFromResponse = response.iterator().next();
      assertEquals(id1.getGuid(), itemFromResponse.getGuid());
   }

   @Test
   public void testGetSingleSet() {
      // No items
      when(dispositionApi.getDispoItemById(branch, id2.getGuid())).thenReturn(null);
      DispoItem noItemsResponse = resource.getDispoItemsById(id2.getGuid());
      assertEquals(null, noItemsResponse);

      DispoItemData expectedItem = new DispoItemData();
      expectedItem.setGuid(id1.getGuid());
      expectedItem.setName("Item");
      expectedItem.setNeedsRerun(false);
      expectedItem.setTotalPoints("4");
      when(dispositionApi.getDispoItemById(branch, expectedItem.getGuid())).thenReturn(expectedItem);
      DispoItem oneSetResponse = resource.getDispoItemsById(expectedItem.getGuid());
      assertEquals(expectedItem.getGuid(), oneSetResponse.getGuid());
   }

   @Test
   public void testPut() {
      DispoItemData newItem = new DispoItemData();
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.editDispoItem(branch, id1.getGuid(), newItem, "", false)).thenReturn(true);
      try (Response response = resource.putDispoItem(id1.getGuid(), newItem, "", false)) {
         assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      }

      when(dispositionApi.editDispoItem(branch, id1.getGuid(), newItem, "", false)).thenReturn(false);
      try (Response response2 = resource.putDispoItem(id1.getGuid(), newItem, "", false)) {
         String returnedMessage = (String) response2.getEntity();
         assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response2.getStatus());
         assertEquals(DispoMessages.Item_NotFound, returnedMessage);
      }
   }

   @Test
   public void testDelete() {
      DispoItemData itemToEdt = new DispoItemData();
      itemToEdt.setGuid(id1.getGuid());
      when(dispositionApi.deleteDispoItem(branch, id1.getGuid(), "")).thenReturn(true);
      try (Response response = resource.deleteDispoItem(id1.getGuid(), "")) {
         assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      }

      when(dispositionApi.deleteDispoItem(branch, id1.getGuid(), "")).thenReturn(false);

      try (Response response2 = resource.deleteDispoItem(id1.getGuid(), "")) {
         String returnedMessage = (String) response2.getEntity();
         assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response2.getStatus());
         assertEquals(DispoMessages.Item_NotFound, returnedMessage);
      }
   }
}
