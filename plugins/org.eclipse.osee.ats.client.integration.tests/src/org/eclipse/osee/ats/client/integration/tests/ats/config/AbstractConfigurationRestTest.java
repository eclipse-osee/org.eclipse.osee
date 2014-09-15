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
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.Assert;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractConfigurationRestTest {

   protected JsonObject getObjectNamed(String name, JsonArray array) throws Exception {
      for (int x = 0; x < array.size(); x++) {
         JsonObject obj = (JsonObject) array.get(x);
         if (obj.get("Name").getAsString().equals(name)) {
            return obj;
         }
      }
      return null;
   }

   protected JsonArray getAndCheck(String url) throws Exception {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path(url).build();

      Response response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON).get();
      Assert.assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      String json = response.readEntity(String.class);

      JsonParser jp = new JsonParser();
      JsonElement je = jp.parse(json);
      return (JsonArray) je;
   }

}
