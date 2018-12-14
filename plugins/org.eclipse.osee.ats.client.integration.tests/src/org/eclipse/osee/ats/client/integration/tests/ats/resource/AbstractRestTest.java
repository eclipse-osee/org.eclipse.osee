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
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.ats.workflow.WorkItemsJsonReader;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractRestTest {

   protected void getAndCountWorkItems(String url, int expected) {
      String json = getJson(url);
      try {
         List<Long> ids = WorkItemsJsonReader.getWorkItemIdsFromJson(json);
         Assert.assertEquals(expected, ids.size());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   protected Object getFirstAndCount(String url, int count) {
      String json = getJson(url);
      Object[] objs = JsonUtil.readValue(json, Object[].class);
      Assert.assertEquals(count, objs.length);
      return objs[0];
   }

   protected Object getFirstAndCountGreater(String url, int count) {
      String json = getJson(url);
      Object[] objs = JsonUtil.readValue(json, Object[].class);
      boolean countGE = objs.length >= count;
      Assert.assertTrue(String.format("Length %d expected to be greater than or equal to %d", objs.length, count),
         countGE);
      return objs[0];
   }

   protected JsonNode readTree(String url) {
      return JsonUtil.readTree(getJson(url));
   }

   protected String getHtml(String url) {
      return getAndCheckResponseCode(url, MediaType.TEXT_HTML_TYPE);
   }

   protected String getXml(String url) {
      return getAndCheckResponseCode(url, MediaType.APPLICATION_XML_TYPE);
   }

   protected String getJson(String url) {
      return getAndCheckResponseCode(url, MediaType.APPLICATION_JSON_TYPE);
   }

   protected String getJson(URI uri) {
      return getAndCheckResponseCode(uri, MediaType.APPLICATION_JSON_TYPE);
   }

   private String getAndCheckResponseCode(String url, MediaType mediaType) {
      return getAndCheckResponseCode(toURI(url), mediaType);
   }

   protected URI toURI(String urlPath) {
      return UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath).build();
   }

   private String getAndCheckResponseCode(URI uri, MediaType mediaType) {
      Response response = JaxRsClient.newClient().target(uri).request(mediaType).get();
      Assert.assertEquals("Unexpected error code: " + response.readEntity(String.class),
         javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      return response.readEntity(String.class);
   }

   protected JsonNode testUrl(String url, int size, String expectedName, String key, boolean keyExists) {
      JsonNode array = readTree(url);
      Assert.assertEquals(array.toString(), size, array.size());

      JsonNode obj = JsonUtil.getArrayElement(array, "name", expectedName);
      Assert.assertNotNull(String.format("Did not find value [%s] in JsonArray [%s]", expectedName, array), obj);
      Assert.assertEquals(keyExists, obj.has(key));
      return obj;
   }

   protected JsonNode testUrl(String url, String expectedName) {
      JsonNode node = readTree(url);

      JsonNode obj = JsonUtil.getArrayElement(node, "name", expectedName);

      JsonNode nameNode = node.get("name");
      Assert.assertTrue(String.format("Did not find value [%s] in JsonArray [%s]", expectedName, node),
         nameNode != null && expectedName.equals(nameNode.asText()));
      return obj;
   }
}