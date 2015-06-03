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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpRequest.HttpMethod;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      return doRequest(url, HttpMethod.GET, null, javax.ws.rs.core.Response.Status.OK.getStatusCode());
   }

   protected JsonArray postAndCheck(String url, Entity<String> entity) throws Exception {
      return doRequest(url, HttpMethod.POST, entity, javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
   }

   protected JsonArray putAndCheck(String url, Entity<String> entity) throws Exception {
      return doRequest(url, HttpMethod.PUT, entity, javax.ws.rs.core.Response.Status.OK.getStatusCode());
   }

   protected JsonArray deleteAndCheck(String url) throws Exception {
      return doRequest(url, HttpMethod.DELETE, null, javax.ws.rs.core.Response.Status.OK.getStatusCode());
   }

   protected JsonArray doRequest(String url, HttpMethod method, Entity<String> entity, int expected) {
      String json = doRequestString(url, method, entity, expected);
      if (!json.isEmpty()) {
         JsonParser jp = new JsonParser();
         JsonElement je = jp.parse(json);
         return (JsonArray) je;
      }
      return null;
   }

   protected String doRequestString(String url, HttpMethod method, Entity<String> entity, int expected) {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path(url).build();

      Response response = null;
      switch (method) {
         case GET: {
            response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON).get();
            break;
         }
         case POST: {
            response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON).post(entity);
            break;
         }
         case PUT: {
            response = JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON).put(entity);
            break;
         }
         case DELETE: {
            response = JaxRsClient.newClient().target(uri).request().delete();
            break;
         }
         default: {
            throw new OseeCoreException("invalid http method type");
         }
      }
      Assert.assertEquals(expected, response.getStatus());
      return response.readEntity(String.class);
   }

}
