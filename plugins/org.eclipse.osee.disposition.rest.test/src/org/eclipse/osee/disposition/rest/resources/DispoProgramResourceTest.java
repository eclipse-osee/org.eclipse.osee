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

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.rest.DispoApi;
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
   private DispoProgramResource resource;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      resource = new DispoProgramResource(dispoApi);
   }

   @Test
   public void testGetAll() throws JSONException {
      // No Sets
      when(dispoApi.getDispoPrograms()).thenReturn(Collections.emptyList());

      Response noProgramsResponse = resource.getAllPrograms();
      String messageActual = (String) noProgramsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noProgramsResponse.getStatus());
      assertEquals("[ ]", messageActual);

      when(dispoApi.getDispoPrograms()).thenReturn(Collections.singletonList(SYSTEM_ROOT));

      Response oneSetResponse = resource.getAllPrograms();
      JSONArray entity = new JSONArray((String) oneSetResponse.getEntity());
      JSONObject programFromEntity = entity.getJSONObject(0);
      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(SYSTEM_ROOT.getIdString(), programFromEntity.getString("value"));
   }
}
