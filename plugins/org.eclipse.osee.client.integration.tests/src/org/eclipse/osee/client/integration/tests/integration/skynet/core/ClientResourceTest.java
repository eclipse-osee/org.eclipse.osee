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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
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
   public void testGet() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").build();

      String results = callAndGetResults(uri);
      Assert.assertTrue(results.contains("supportedVersions"));
   }

   @Test
   public void testStatusGet() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").path("status").build();

      String results = callAndGetResults(uri);
      Assert.assertTrue(results.contains("releaseByType"));
      Assert.assertTrue(results.contains("releases"));
      Assert.assertTrue(results.contains("numberClients"));
   }

   @Test
   public void testStatusGetId() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("client").path("status").path("Joe%20Smith").build();

      String results = callAndGetResults(uri);
      Assert.assertTrue(results.contains("releaseByType"));
      Assert.assertTrue(results.contains("releases"));
      Assert.assertTrue(results.contains("numberClients"));
   }

   private String callAndGetResults(URI uri) {
      Response response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
      Assert.assertEquals("Unexpected error code: " + response.readEntity(String.class),
         javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      String results = response.readEntity(String.class);
      return results;
   }

}
