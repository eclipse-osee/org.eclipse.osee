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
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
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
   private BranchId branch;

   private DispoSetResource resource;
   private final Long id1 = 21351L;
   private final Long id2 = 222325L;

   private final String id1AsString = String.valueOf(id1);
   private final String id2AsString = String.valueOf(id2);;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoSetResource(dispositionApi, branch);
   }

   @Test
   public void testDispositionSetPost() {
      // test valid post
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("Name");
      descriptor.setImportPath("c:");
      descriptor.setDispoType("testScripts");

      DispoSetData expected = new DispoSetData();
      expected.setGuid(id1AsString);
      expected.setName(descriptor.getName());
      expected.setImportPath(descriptor.getImportPath());
      when(dispositionApi.createDispoSet(branch, descriptor, "")).thenReturn(id1);
      when(dispositionApi.getDispoSetById(branch, id1AsString)).thenReturn(expected);
      when(dispositionApi.isUniqueSetName(branch, descriptor.getName())).thenReturn(true);

      Response postResponse = resource.postDispoSet(descriptor, "");
      DispoSetData returnedEntity = (DispoSetData) postResponse.getEntity();
      assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
      assertEquals(id1AsString, returnedEntity.getGuid());
      assertEquals("Name", returnedEntity.getName());
      assertEquals("c:", returnedEntity.getImportPath());
   }

   @Test
   public void testPostBadParameters() {
      // Try to do post with invalid name
      DispoSetDescriptorData badNameDescriptor = new DispoSetDescriptorData();
      badNameDescriptor.setName("");
      badNameDescriptor.setImportPath("c:");
      Response postResponseBadName = resource.postDispoSet(badNameDescriptor, "");
      String returnedEntityBadName = (String) postResponseBadName.getEntity();
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponseBadName.getStatus());
      assertEquals(DispoMessages.Set_EmptyNameOrPath, returnedEntityBadName);

      // Try to do post with invalid name
      DispoSetDescriptorData badPathDescriptor = new DispoSetDescriptorData();
      badPathDescriptor.setName("name");
      badPathDescriptor.setImportPath("");
      Response postResponseBadPath = resource.postDispoSet(badPathDescriptor, "");
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

      when(dispositionApi.isUniqueSetName(branch, descriptor.getName())).thenReturn(false);

      Response postResponse = resource.postDispoSet(descriptor, "");
      String returnedEntity = (String) postResponse.getEntity();
      assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());
      assertEquals(DispoMessages.Set_ConflictingNames, returnedEntity);
   }

   @Test
   public void testGetAll() {
      // No Sets
      List<DispoSet> emptyResultSet = new ArrayList<>();
      when(dispositionApi.getDispoSets(branch, "testScript")).thenReturn(emptyResultSet);
      Iterable<DispoSet> noSetsResponse = resource.getAllDispoSets("testScript");
      assertEquals(Collections.emptyList(), noSetsResponse);

      DispoSetData set = new DispoSetData();
      set.setGuid(id1AsString);
      set.setDispoType("testScript");
      List<DispoSet> resultSet = Collections.singletonList((DispoSet) set);

      when(dispositionApi.getDispoSets(branch, "testScript")).thenReturn(resultSet);
      Iterable<DispoSet> oneSetResponse = resource.getAllDispoSets("testScript");
      DispoSet setFromEntity = oneSetResponse.iterator().next();
      assertEquals(id1AsString, setFromEntity.getGuid());
   }

   @Test
   public void testGetSingleSet() {
      // No Sets
      when(dispositionApi.getDispoSetById(branch, id2AsString)).thenReturn(null);
      DispoSet noSetsResponse = resource.getDispoSetById(id2AsString);
      assertEquals(null, noSetsResponse);

      DispoSetData expectedSet = new DispoSetData();
      expectedSet.setGuid(id1AsString);
      expectedSet.setName("Set");
      when(dispositionApi.getDispoSetById(branch, expectedSet.getGuid())).thenReturn(expectedSet);
      DispoSet oneSetResponse = resource.getDispoSetById(expectedSet.getGuid());
      assertEquals(expectedSet.getGuid(), oneSetResponse.getGuid());
   }

   @Test
   public void testPut() {
      new OperationReport();

      DispoSetData newSet = new DispoSetData();
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1AsString);
      Response response = resource.putDispoSet(id1AsString, newSet, "");
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
   }

   @Test
   public void testDelete() {
      DispoSetData setToEdt = new DispoSetData();
      setToEdt.setGuid(id1AsString);
      when(dispositionApi.deleteDispoSet(branch, id1AsString, "")).thenReturn(true);
      Response response = resource.deleteDispoSet(id1AsString, "");
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

      when(dispositionApi.deleteDispoSet(branch, id1AsString, "")).thenReturn(false);
      response = resource.deleteDispoSet(id1AsString, "");
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
   }
}
