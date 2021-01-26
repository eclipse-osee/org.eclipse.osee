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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import javax.ws.rs.client.WebTarget;

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
    * Serialize any Java value as a String. Functionally equivalent to calling
    * {@link ObjectMapper#writeValueAsString(Object)}
    */
   String toJson(Object object);

   /**
    * Deserialize JSON content from given JSON content String to an object of type valueType
    */
   <T> T readValue(String json, Class<T> valueType);

   <T> T readValue(String json, Class<? extends Collection> collectionClass, Class<?> elementClass);

   WebTarget newTarget(String path);

   /**
    * @return a WebTarget using the url created by the server's base url appened with the provided path segments
    */
   WebTarget newTarget(String... pathSegments);

   WebTarget newTargetPasswd(String path, String serverUsername, String serverPassword);

   <T> T newProxy(WebTarget target, Class<T> clazz);

   ObjectMapper getObjectMapper();

   <T> T newProxy(String path, Class<T> clazz);
}