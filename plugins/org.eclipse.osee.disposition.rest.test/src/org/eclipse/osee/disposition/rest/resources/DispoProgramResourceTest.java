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
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
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
public class DispoProgramResourceTest {

   @Mock
   private DispoApi dispoApi;
   @Mock
   private BranchId id1;
   @Mock
   private BranchId id2;

   private DispoProgramResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoProgramResource(dispoApi);
      when(id1.getUuid()).thenReturn(23L);
      when(id2.getUuid()).thenReturn(25L);
   }

   @Test
   public void testGetAll() throws JSONException {
      // No Sets
      List<IOseeBranch> emptyResultSet = Collections.emptyList();
      when(dispoApi.getDispoPrograms()).thenReturn(emptyResultSet);
      Response noProgramsResponse = resource.getAllPrograms();
      String messageActual = (String) noProgramsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noProgramsResponse.getStatus());
      assertEquals("[]", messageActual);

      IOseeBranch branch = IOseeBranch.create(id1.getUuid(), "dispotestGetAll");
      List<IOseeBranch> branchList = Collections.singletonList(branch);

      when(dispoApi.getDispoPrograms()).thenReturn(branchList);
      Response oneSetResponse = resource.getAllPrograms();
      JSONArray entity = new JSONArray((String) oneSetResponse.getEntity());
      JSONObject programFromEntity = entity.getJSONObject(0);
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(String.valueOf(id1.getUuid()), programFromEntity.getString("value"));
   }
}
