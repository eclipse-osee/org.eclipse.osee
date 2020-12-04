/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.core.workflow.util.WorkItemsJsonReader;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractRestTest {

   protected void getAndCountWorkItems(URI url, int expected) {
      String json = getJson(url);
      try {
         List<Long> ids = WorkItemsJsonReader.getWorkItemIdsFromJson(json);
         Assert.assertEquals(expected, ids.size());
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   protected Object getFirstAndCount(URI url, int count) {
      String json = getJson(url);

      Object[] objs = JsonUtil.readValue(json, Object[].class);
      Assert.assertEquals(count, objs.length);
      return objs[0];
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