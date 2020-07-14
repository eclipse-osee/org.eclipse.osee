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

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.eclipse.osee.disposition.rest.DispoApi;
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
   public void testGetAll() throws Exception {
      // No Sets
      when(dispoApi.getDispoPrograms()).thenReturn(Collections.emptyList());

      Response noProgramsResponse = resource.getAllPrograms();
      String messageActual = (String) noProgramsResponse.getEntity();
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), noProgramsResponse.getStatus());
      assertEquals("[ ]", messageActual);

      when(dispoApi.getDispoPrograms()).thenReturn(Collections.singletonList(SYSTEM_ROOT));

      Response oneSetResponse = resource.getAllPrograms();
      ObjectMapper OM = new ObjectMapper();
      String entityString = (String) oneSetResponse.getEntity();
      String noBrackets = entityString.substring(1, entityString.length() - 1);
      JsonNode programFromEntity = OM.readTree(noBrackets);

      assertEquals(Response.Status.OK.getStatusCode(), oneSetResponse.getStatus());
      assertEquals(SYSTEM_ROOT.getIdString(), programFromEntity.get("value").asText());
   }
}
