/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.client.WebTarget;
import org.eclipse.osee.framework.core.data.UserService;

/**
 * @author Ryan D. Brooks
 */
public interface JaxRsApi {

   /**
    * Deserialize JSON content as tree expressed using set of {@link JsonNode} instances. Returns root of the resulting
    * tree (where root can consist of just a single node if the current event is a value event, not container).
    */
   JsonNode readTree(String json);

   /**
    * Extract from JSON content the string value for a top-level key
    */
   String readValue(String json, String key);

   /**
    * Serialize any Java value as a String. Functionally equivalent to calling
    * {@link ObjectMapper#writeValueAsString(Object)}
    */
   String toJson(Object object);

   /**
    * Deserialize JSON content from given JSON content String to an object of type valueType
    */
   <T> T readValue(String json, Class<T> valueType);

   /**
    * Deserialize a collection of the specified type containing elements of type elementClass from the JSON content
    * string
    */
   <T, C extends Collection<T>> C readCollectionValue(String json, Class<? extends Collection> collectionClass, Class<T> elementClass);

   /**
    * Deserialize a map of the specified key and value types from the JSON content string
    */
   <K, V> Map<K, V> readMapValue(String json, Class<K> keyClass, Class<V> valueClass);

   WebTarget newTarget(String path);

   WebTarget newTargetNoRedirect(String path);

   /**
    * @return a WebTarget using the url created by the server's base URL appended with the provided path segments
    */
   WebTarget newTarget(String... pathSegments);

   /**
    * @return a WebTarget using the url created by the server's base URL appended with the provided path followed by ?
    * and the query parameters in key value format
    */
   WebTarget newTargetQuery(String path, String... queryParams);

   /**
    * @return a WebTarget using the given absolute URL followed by ? and the query parameters in key value format
    */
   WebTarget newTargetUrlQuery(String url, String... queryParams);

   WebTarget newTargetUrlPasswd(String url, String serverUsername, String serverPassword);

   WebTarget newTargetPasswd(String path, String serverUsername, String serverPassword);

   <T> T newProxy(WebTarget target, Class<T> clazz);

   ObjectMapper getObjectMapper();

   <T> T newProxy(String path, Class<T> clazz);

   /**
    * @return a WebTarget using the given absolute URL
    */
   WebTarget newTargetUrl(String url);

   JsonFactory getFactory();

   /**
    * Must only be called once on the client during startup and never on the server.
    */
   void createClientFactory(UserService userService);

}