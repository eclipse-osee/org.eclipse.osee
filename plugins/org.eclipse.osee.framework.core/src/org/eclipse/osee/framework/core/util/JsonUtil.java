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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
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

   static {
      mapper = new ObjectMapper();
      mapper.setDateFormat(new SimpleDateFormat("MMM d, yyyy h:mm:ss aa"));
      mapper2 = new ObjectMapper();
      mapper2.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm a z"));
   }

   public static synchronized ObjectMapper getMapper() {
      return mapper;
   }

   public static JsonNode readTree(String json) {
      try {
         return getMapper().readTree(json);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static String toJson(Object object) {
      try {
         return getMapper().writeValueAsString(object);
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

   private static synchronized ObjectMapper getMapperZ() {
      return mapper2;
   }

   public static JsonFactory getFactory() {
      return getMapperZ().getFactory();
   }

   /**
    * @param array must be a Json array of Json objects
    * @param expectedName the value of the "Name" field
    * @return the array element (JsonNode obj) named expectedName
    */
   public static JsonNode getArrayElement(JsonNode array, String key, String value) {
      for (JsonNode element : array) {
         JsonNode node = element.get(key);
         if (node != null) {
            if (value.equals(node.asText())) {
               return element;
            }
         }
      }
      return null;
   }

   public static JsonNode getJsonParserTree(JsonParser jp) {
      try {
         return jp.getCodec().readTree(jp);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

   }

   public static <T> T readValue(String json, TypeReference<Map<String, String>> typeReference) {
      try {
         return getMapper().readValue(json, typeReference);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
