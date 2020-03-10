/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;

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
}