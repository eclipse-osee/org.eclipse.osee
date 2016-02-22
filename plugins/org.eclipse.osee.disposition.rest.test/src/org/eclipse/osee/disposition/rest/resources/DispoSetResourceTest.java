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
import org.eclipse.osee.disposition.model.DispoMessages;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
   private Identifiable<String> id1;
   @Mock
   private Identifiable<String> id2;
   @Mock
   private DispoProgram program;

   private DispoSetResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoSetResource(dispositionApi, program);
      when(id1.getGuid()).thenReturn("abcdef");
      when(id2.getGuid()).thenReturn("fedcba");
   }

   @Test
   public void testDispositionSetPost() {
      // test valid post
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("Name");
      descriptor.setImportPath("c:");
      descriptor.setDispoType("testScripts");

      DispoSetData expected = new DispoSetData();
      expected.setGuid(id1.getGuid());
      expected.setName(descriptor.getName());
      expected.setImportPath(descriptor.getImportPath());
      when(dispositionApi.createDispoSet(program, descriptor)).thenReturn(id1);
      when(dispositionApi.getDispoSetById(program, id1.getGuid())).thenReturn(expected);
      when(dispositionApi.isUniqueSetName(program, descriptor.getName())).thenReturn(true);

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
      descriptor.setDispoType("testScript");

      when(dispositionApi.isUniqueSetName(program, descriptor.getName())).thenReturn(false);

      Response postResponse = resource.postDispoSet(descriptor);
      String returnedEntity = (String) postResponse.getEntity();
      assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());
      assertEquals(DispoMessages.Set_ConflictingNames, returnedEntity);
   }

   @Test
   public void testGetAll() throws JSONException {
      // No Sets
      List<DispoSet> emptyResultSet = new ArrayList<>();
      when(dispositionApi.getDispoSets(program)).thenReturn(emptyResultSet);
      Response noSetsResponse = resource.getAllDispoSets("testScript");
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals("[]", messageActual);

      DispoSetData set = new DispoSetData();
      set.setGuid(id1.getGuid());
      set.setDispoType("testScript");
      List<DispoSet> resultSet = Collections.singletonList((DispoSet) set);

      when(dispositionApi.getDispoSets(program)).thenReturn(resultSet);
      Response oneSetResponse = resource.getAllDispoSets("testScript");
      JSONArray entity = new JSONArray((String) oneSetResponse.getEntity());
      JSONObject setFromEntity = entity.getJSONObject(0);
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(id1.getGuid(), setFromEntity.getString("guid"));
   }

   @Test
   public void testGetSingleSet() {
      // No Sets
      when(dispositionApi.getDispoSetById(program, id2.getGuid())).thenReturn(null);
      Response noSetsResponse = resource.getDispoSetByIdJson(id2.getGuid());
      String messageActual = (String) noSetsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noSetsResponse.getStatus());
      assertEquals(DispoMessages.Set_NotFound, messageActual);

      DispoSetData expectedSet = new DispoSetData();
      expectedSet.setGuid(id1.getGuid());
      expectedSet.setName("Set");
      when(dispositionApi.getDispoSetById(program, expectedSet.getGuid())).thenReturn(expectedSet);
      Response oneSetResponse = resource.getDispoSetByIdJson(expectedSet.getGuid());
      DispoSetData returnedSet = (DispoSetData) oneSetResponse.getEntity();
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(expectedSet.getGuid(), returnedSet.getGuid());
   }

   @Test
   public void testPut() {
      new OperationReport();

      DispoSetData newSet = new DispoSetData();
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1.getGuid());
      Response response = resource.putDispoSet(id1.getGuid(), newSet);
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1.getGuid());
      when(dispositionApi.deleteDispoSet(program, id1.getGuid())).thenReturn(true);
      Response response = resource.deleteDispoSet(id1.getGuid());
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoSet(program, id1.getGuid())).thenReturn(false);
      response = resource.deleteDispoSet(id1.getGuid());
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }
}
