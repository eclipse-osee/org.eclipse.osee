/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Since both ObjectReader / ObjectWriter are immutable, methods will create new readers/writers and configuration is
 * fully thread-safe. Cost of these instances is minimal (unlike cost of creating ObjectMappers, which is substantial)
 * <a href="https://github.com/FasterXML/jackson-databind/wiki/JacksonFeature">Jackson Documentation</a> <br />
 * <br />
 * ObjectMappers is thread safe but cost of new instances is substantial so we will share an instance. Callers are
 * prohibited from changing its configuration <a href=
 * "https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field">Stackoverflow
 * Ref</a>
 *
 * @author Ryan D. Brooks
 */
public class JsonUtil {

   private static ObjectMapper mapper;
   private static ObjectMapper mapper2;

   public static ObjectMapper getMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
         mapper.setDateFormat(new SimpleDateFormat("MMM d, yyyy h:mm:ss aa"));
      }
      return mapper;
   }

   public static JsonNode readTree(String json) {
      try {
         return getMapper().readTree(json);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static <T> T readValue(String content, Class<T> valueType) {
      try {
         return getMapper().readValue(content, valueType);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private static ObjectMapper getMapperZ() {
      if (mapper2 == null) {
         mapper2 = new ObjectMapper();
         mapper2.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm a z"));
      }
      return mapper2;
   }

   public static JsonFactory getFactory() {
      return getMapperZ().getJsonFactory();
   }

   /**
    * @param array must be a Json array of Json objects
    * @param expectedName the value of the "Name" field
    * @return the array element (JsonNode obj) named expectedName
    */
   public static JsonNode getArrayElement(JsonNode array, String key, String value) {
      for (JsonNode element : array) {
         JsonNode nameNode = element.get(key);
         if (nameNode != null) {
            if (value.equals(nameNode.asText())) {
               return element;
            }
         }
      }
      return null;
   }
}