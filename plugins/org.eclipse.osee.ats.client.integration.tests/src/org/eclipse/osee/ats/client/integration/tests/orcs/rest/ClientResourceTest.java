/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.orcs.rest;

import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.demo.api.DemoUsers;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ClientResource}
 *
 * @author Donald G. Dunne
 */
public class ClientResourceTest {

   @Test
   public void testGetAll() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").build();

      String results = callAndGetResults(uri);
      getExpected("sessions", results);
   }

   @Test
   public void testGetAllDetails() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").path("details").build();

      String results = callAndGetResults(uri);
      getExpected("sessions", results);
   }

   @Test
   public void testGetClientsForUser() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").path(DemoUsers.Joe_Smith.getUserId()).build();

      String results = callAndGetResults(uri);
      getExpected("\"userId\" : \"" + DemoUsers.Joe_Smith.getUserId() + "\"", results);

      uri = UriBuilder.fromUri(appServer + "/orcs/client/Joe_Smith").build();

      results = callAndGetResults(uri);
      getExpected("\"userId\" : \"" + DemoUsers.Joe_Smith.getUserId() + "\"", results);

   }

   private void getExpected(String expected, String results) {
      Assert.assertTrue(String.format("Results should include \"%s\" but does not. Results [%s]", expected, results),
         results.contains(expected));
   }

   private String callAndGetResults(URI uri) {
      Response response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
      Assert.assertEquals("Unexpected error code: " + response.readEntity(String.class),
         javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      String results = response.readEntity(String.class);
      return results;
   }

}
