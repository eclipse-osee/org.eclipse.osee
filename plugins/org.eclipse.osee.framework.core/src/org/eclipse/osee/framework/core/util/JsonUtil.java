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
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TransactionTokenDeserializer;
import org.eclipse.osee.framework.core.data.TransactionTokenSerializer;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokenDeserializer;
import org.eclipse.osee.framework.core.data.UserTokenSerializer;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdDeserializer;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDeserializer;
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
      mapper = createObjectMapper().setDateFormat(new SimpleDateFormat("MMM d, yyyy h:mm:ss aa"));
      mapper2 = createObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm a z"));
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

   private static ObjectMapper createObjectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();

      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
      objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, true);
      objectMapper.configure(MapperFeature.AUTO_DETECT_SETTERS, true);
      objectMapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
      objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
      objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
      objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      objectMapper.configure(SerializationFeature.WRAP_EXCEPTIONS, true);
      objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
      objectMapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

      DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
      prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\n"));
      objectMapper.setDefaultPrettyPrinter(prettyPrinter);

      SimpleModule module = new SimpleModule("OSEE", new Version(1, 0, 0, "", "", ""));

      module.addDeserializer(ApplicabilityToken.class, new NamedIdDeserializer<>(ApplicabilityToken::create));
      module.addDeserializer(ArtifactToken.class, new NamedIdDeserializer<ArtifactToken>(ArtifactToken::valueOf));
      module.addDeserializer(ArtifactId.class, new IdDeserializer<ArtifactId>(ArtifactId::valueOf));
      module.addDeserializer(TransactionToken.class, new TransactionTokenDeserializer());
      module.addSerializer(TransactionToken.class, new TransactionTokenSerializer());
      module.addDeserializer(UserToken.class, new UserTokenDeserializer());
      module.addSerializer(UserToken.class, new UserTokenSerializer());
      JsonSerializer<Id> idSerializer = new IdSerializer();
      module.addSerializer(TransactionId.class, idSerializer);
      module.addSerializer(BranchType.class, idSerializer);
      module.addSerializer(BranchState.class, idSerializer);
      module.addDeserializer(TransactionId.class, new IdDeserializer<TransactionId>(TransactionId::valueOf));

      objectMapper.registerModule(module);
      return objectMapper;
   }
}