/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class implements utility methods for encoding and decoding {@link Map.Entry}&lt;String,String&gt;
 * implementations to and from JSON for the {@link MapEntryAttributeType}. The JSON encoding used is as follows:
 *
 * <pre>
 * {
 *    "key"   : "&lt;key-string&gt;",
 *    "value" : "&ltvalue-string&gt"
 * }
 * </pre>
 *
 * Where:
 * <dl>
 * <dt>key-string</dt>
 * <dd>is a properly escaped JSON string representing the {@link Map.Entry} key string.</dd>
 * <dt>value-string</dt>
 * <dd>is a properly escaped JSON string representing the {@link Map.Entry} value string.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public class MapEntryAttributeUtil {

   /**
    * A deserialization class for creating a {@link Map.Entry} implementation from a JSON string.
    */

   private static class MapEntryDeserializer extends StdDeserializer<Map.Entry<String, String>> {

      /**
       * A default (Java) serialization identifier.
       */

      private static final long serialVersionUID = 1L;

      public MapEntryDeserializer() {
         super((Class<Map.Entry<String, String>>) null);
      }

      public MapEntryDeserializer(Class<Map.Entry<String, String>> mapEntryClass) {
         super(mapEntryClass);
      }

      /**
       * Deserialization method for {@link Map.Entry} JSON representations.
       *
       * @param jsonParser used for reading JSON content.
       * @param deserializationContext unused.
       * @throws IOException when reading JSON from the parser fails.
       */

      @Override
      public Map.Entry<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
         throws IOException {
         JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
         var key = jsonNode.get("key").asText();
         var value = jsonNode.get("value").asText();
         return Map.entry(key, value);
      }
   }

   /**
    * A serialization class for creating a JSON string representation of a {@link Map.Entry} implementation.
    */

   private static class MapEntrySerializer extends StdSerializer<Map.Entry<String, String>> {

      /**
       * A default (Java) serialization identifier.
       */

      private static final long serialVersionUID = 1L;

      public MapEntrySerializer() {
         super((Class<Map.Entry<String, String>>) null);
      }

      public MapEntrySerializer(Class<Map.Entry<String, String>> mapEntryClass) {
         super(mapEntryClass);
      }

      /**
       * Serialization method for creating the JSON representation of a {@link Map.Entry} implementation.
       *
       * @param mapEntry the {@link Map.Entry} implementation to be serialized.
       * @param jsonGenerator object used to build the JSON string.
       * @param serializerProvider unused.
       */

      @Override
      public void serialize(Map.Entry<String, String> mapEntry, JsonGenerator jsonGenerator,
         SerializerProvider serializerProvider) throws IOException {
         jsonGenerator.writeStartObject();
         jsonGenerator.writeStringField("key", mapEntry.getKey());
         jsonGenerator.writeStringField("value", mapEntry.getValue());
         jsonGenerator.writeEndObject();
      }
   }

   /**
    * An instance of the {@link Map.Entry} class object casted to
    * {@link Class}&lt;{@link Map.Entry}&lt;String,String&gt;&gt; used to reduce the need for type casting.
    */

   @SuppressWarnings("unchecked")
   private static Class<Map.Entry<String, String>> mapEntryClass =
      (Class<Map.Entry<String, String>>) (Object) Map.Entry.class;

   /**
    * Static method used to create the {@link ObjectMapper} instance used for serialization and deserialization by the
    * {@link MapEntryAttributeUtil} methods.
    *
    * @return an {@link ObjectMapper} customized for serialization and deserialization of {@link Map.Entry}
    * implementations.
    */

   private static ObjectMapper create() {

      var objectMapper = new ObjectMapper();

      var mapEntryDeserializerSimpleModule =
         new SimpleModule("MapEntryDeserializer", new Version(1, 0, 0, null, null, null));
      mapEntryDeserializerSimpleModule.addDeserializer(MapEntryAttributeUtil.mapEntryClass,
         new MapEntryAttributeUtil.MapEntryDeserializer());

      var mapEntrySerializerSimpleModule =
         new SimpleModule("MapEntrySerializer", new Version(1, 0, 0, null, null, null));
      mapEntrySerializerSimpleModule.addSerializer(MapEntryAttributeUtil.mapEntryClass,
         new MapEntryAttributeUtil.MapEntrySerializer());

      objectMapper.registerModule(mapEntryDeserializerSimpleModule);
      objectMapper.registerModule(mapEntrySerializerSimpleModule);

      return objectMapper;
   }

   /**
    * Deserializes the JSON string, <code>input</code>, to a {@link Map.Entry} implementations.
    *
    * @param input the JSON string to be deserialized.
    * @return the created {@link Map.Entry} implementation.
    * @throws OseeCoreException when deserialization of the <code>input</code> JSON fails.
    */

   public static Map.Entry<String, String> jsonDecode(String input) {

      if (Strings.isInvalidOrBlank(input)) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      "MapEntryAttributeUtil::jsonDecode, the parameter \"input\" cannot be null or blank."
                   );
         //@formatter:on
      }

      try {
         var mapEntry = MapEntryAttributeUtil.objectMapper.readValue(input, MapEntryAttributeUtil.mapEntryClass);
         return mapEntry;
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "MapEntryAttributeUtil::jsonDecode, failed to create object from the string value." )
                             .indentInc()
                             .follows( "JSON String", input )
                             .reasonFollows( e )
                             .toString(),
                       e
                   );
         //@formatter:on
      }
   }

   /**
    * Serializes the {@link Map.Entry} implementation into a JSON string.
    *
    * @param mapEntry the {@link Map.Entry} implementation to be serialized.
    * @return the JSON string.
    * @throws OseeCoreException when serialization of the <code>mapEntry</code> fails.
    */

   public static String jsonEncode(Map.Entry<String, String> mapEntry) {
      if (Objects.isNull(mapEntry)) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      "MapEntryAttributeUtil::jsonEncode, the parameter \"mapEntry\" cannot be null."
                   );
         //@formatter:on
      }

      try {
         var storageString = MapEntryAttributeUtil.objectMapper.writeValueAsString(mapEntry);
         return storageString;
      } catch (Exception e) {
         return null;
      }

   }

   /**
    * The {@link ObjectMapper} used for serialization and deserialization.
    */

   private final static ObjectMapper objectMapper = MapEntryAttributeUtil.create();

   /**
    * The constructor is private to prevent direct instantiation of the class.
    */

   private MapEntryAttributeUtil() {
   }

}

/* EOF */
