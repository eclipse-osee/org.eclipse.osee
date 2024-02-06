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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.Zip;

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
    * A shared implementation for Client and Server {@link MapEntryAttribute} data proxy local data storage.
    */

   public static class LocalData {

      /**
       * Saves the size of the {@link #storageString}. -1 is used as a sentinel value to indicate the {@link LocalData}
       * is invalid.
       */

      private int size;

      /**
       * Saves the data as a {@link Map.Entry}.
       */

      private Map.Entry<String, String> mapEntry;

      /**
       * Saves the data as a uncompressed byte array of the UTF-8 encoded JSON serialization of the {@link Map.Entry}.
       */

      private byte[] storageBytes;

      /**
       * Saves the JSON serialization of the {@link Map.Entry}.
       */

      private String storageString;

      /**
       * Creates a new local data store.
       */

      public LocalData() {
         this.clear();
      }

      /**
       * Sets the local data to sentinel values.
       */

      public void clear() {
         this.size = -1;
         this.mapEntry = EMPTY_MAP_ENTRY;
         this.storageBytes = EMPTY_BYTE_ARRAY;
         this.storageString = Strings.EMPTY_STRING;
      }

      /**
       * Get the locally stored map entry.
       *
       * @return an immutable foldable {@link Map.Entry} with the locally stored data or an empty {@link Map.Entry}.
       */

      public Map.Entry<String, String> getMapEntry() {
         return this.mapEntry;
      }

      /**
       * Get the locally stored map entry's JSON representation encoded as an uncompressed UTF-8 byte array.
       *
       * @return a copy of the locally stored map entry byte representation.
       */

      public byte[] getStorageBytes() {
         return Arrays.copyOf(this.storageBytes, this.storageBytes.length);
      }

      /**
       * Get the locally stored JSON representation of the {@link Map.Entry}.
       *
       * @return JSON representation of the {@link Map.Entry}.
       */

      public String getStorageString() {
         return this.storageString;
      }

      /**
       * Predicate to determine if the local data has been set.
       *
       * @return <code>true</code> when the local data is valid; otherwise, <code>false</code>.
       */

      public boolean isDataValid() {
         return this.size >= 0;
      }

      /**
       * Predicate to determine if the locally stored {@link Map.Entry} is equal to the <code>otherMapEntry</code>.
       *
       * @param otherMapEntry the {@link Map.Entry} to be tested.
       * @return <code>true</code> when the local data is valid, <code>otherMapEntry</code> is valid, and the local
       * {@link Map.Entry} equals <code>otherMapEntry</code>; otherwise, <code>false</code>.
       */

      public boolean isEqual(Map.Entry<String, String> otherMapEntry) {
         //@formatter:off
         return
            ( this.isDataValid() && MapEntryAttributeUtil.isValidMapEntry( otherMapEntry ) )
               ? this.mapEntry.equals( otherMapEntry )
               : false;
         //@formatter:on
      }

      /**
       * Sets the local storage from the <code>mapEntry</code>. When <code>mapEntry</code> is valid:
       * <ul>
       * <li>The <code>mapEntry</code> is JSON encoded and saved in {@link #storageString}.</li>
       * <li>The <code>mapEntry</code> JSON string is encoded as a UTF-8 byte array and saved in to
       * {@link #storageBytes}.</li>
       * <li>An immutable and foldable {@link Map.Entry} is created from the <code>mapEntry</code> key and value; and
       * saved in to {@link #mapEntry}.
       * </ul>
       * When <code>mapEntry</code> is invalid according to {@link MapEntryAttributeUtil#isValidMapEntry} or an error
       * occurs, the local storage is cleared.
       *
       * @param mapEntry the {@link Map.Entry} to set the local storage from.
       */

      public void set(Map.Entry<String, String> mapEntry) {

         if (!MapEntryAttributeUtil.isValidMapEntry(mapEntry)) {

            this.clear();
            return;

         }

         try {

            var storageString = MapEntryAttributeUtil.jsonEncode(mapEntry);
            var storageBytes = storageString.getBytes(MapEntryAttributeUtil.MAP_ENTRY_CHARSET);
            var immutableFoldableMapEntry = Map.entry(mapEntry.getKey(), mapEntry.getValue());

            this.mapEntry = immutableFoldableMapEntry;
            this.storageString = storageString;
            this.storageBytes = storageBytes;
            this.size = this.storageString.length();

         } catch (Exception e) {

            this.clear();

         }
      }

      /**
       * Sets the local storage from the <code>storageString</code>. When <code>storageString</code> is valid:
       * <ul>
       * <li>The <code>storageString</code> is JSON decoded and saved in {@link #mapEntry} as an immutable foldable
       * {@link Map.Entry}.</li>
       * <li>The {@link Map.Entry} created from the <code>storageString</code> is JSON encoded to create a normalized
       * storage string which is saved in to {@link #storageString}.</li>
       * <li>The normalized storage string is encoded as a UTF-8 byte array and saved in to {@link #storageBytes}.</li>
       * </ul>
       * When <code>storageString</code> is invalid according to
       * {@link MapEntryAttributeUtil#isValidMapEntryStorageString} or an error occurs, the local storage is cleared.
       *
       * @param storageString the JSON encoded {@link Map.Entry} string to set the local storage from.
       */

      public void set(String storageString) {

         if (!MapEntryAttributeUtil.isValidMapEntryStorageString(storageString)) {

            this.clear();
            return;

         }

         try {

            var immutableFoldableMapEntry = MapEntryAttributeUtil.jsonDecode(storageString);
            var normalizedStorageString = MapEntryAttributeUtil.jsonEncode(mapEntry);
            var storageBytes = normalizedStorageString.getBytes(MapEntryAttributeUtil.MAP_ENTRY_CHARSET);

            this.mapEntry = immutableFoldableMapEntry;
            this.storageString = normalizedStorageString;
            this.storageBytes = storageBytes;
            this.size = this.storageString.length();

         } catch (Exception e) {

            this.clear();

         }
      }

      /**
       * Sets the local storage from the <code>compressedContent</code>. When <code>compressedContent</code> is valid:
       * <ul>
       * <li>The <code>compressedContent</code> is decompressed and saved in to the member {@link #storageBytes}.</li>
       * <li>The decompressed bytes encoded as a {@link String} and saved in to the member {@link #storageString}.</li>
       * <li>The storage string is JSON decoded into an immutable foldable {@link Map.Entry} and saved in the member
       * {@link #mapEntry}.</li>
       * </ul>
       * When <code>compressedContent</code> is <code>null</code> or empty; or the decompressed content as a
       * {@link String} is invalid according to {@link MapEntryAttributeUtil#isValidMapEntryStorageString}; or an error
       * occurs the local storage is cleared.
       *
       * @param storageString the JSON encoded {@link Map.Entry} string to set the local storage from.
       */

      public boolean setFromCompressed(byte[] compressedContent) {

         if (Objects.isNull(compressedContent) || compressedContent.length <= 0) {

            this.clear();
            return false;

         }

         try (var dataStoreRawContentByteArrayInputStream = new ByteArrayInputStream(compressedContent)) {

            var storageBytes = Zip.decompressBytes(dataStoreRawContentByteArrayInputStream);

            if (Objects.isNull(storageBytes) || (storageBytes.length <= 0)) {

               this.clear();
               return false;

            }

            var storageString = new String(storageBytes, MapEntryAttributeUtil.MAP_ENTRY_CHARSET);

            if (!MapEntryAttributeUtil.isValidMapEntryStorageString(storageString)) {

               this.clear();
               return false;

            }

            var immutableFoldableMapEntry = MapEntryAttributeUtil.jsonDecode(storageString);

            this.mapEntry = immutableFoldableMapEntry;
            this.storageString = storageString;
            this.storageBytes = storageBytes;
            this.size = this.storageString.length();

            return true;

         } catch (IOException e) {

            this.clear();
            return false;

         }

      }

      /**
       * Gets the size of the locally stored storage string.
       *
       * @return when the local data is valid the size of {@link #storageString}; otherwise, 0.
       */

      public int size() {

         return (this.size >= 0) ? this.size : 0;

      }

   }

   /**
    * A deserialization class for creating a {@link Map.Entry} implementation from a JSON string.
    */

   private static class MapEntryDeserializer extends StdDeserializer<Map.Entry<String, String>> {

      /**
       * A default (Java) serialization identifier.
       */

      private static final long serialVersionUID = 1L;

      /**
       * Creates a new JSON deserializer for {@link MapEntryAttribute} {@link Map.Entry}s.
       */

      public MapEntryDeserializer() {
         super((Class<Map.Entry<String, String>>) null);
      }

      /**
       * Creates a new JSON deserializer for {@link MapEntryAttribute} {@link Map.Entry}s.
       */

      public MapEntryDeserializer(Class<Map.Entry<String, String>> mapEntryClass) {
         super(mapEntryClass);
      }

      /**
       * Deserialization method for {@link Map.Entry} JSON representations.
       *
       * @param jsonParser used for reading JSON content.
       * @param deserializationContext unused.
       * @return an immutable foldable {@link Map.Entry} implementation with the key and value from the JSON
       * representation.
       * @throws IOException when reading JSON from the parser fails.
       */

      @Override
      public Map.Entry<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
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

      /**
       * Creates a new JSON serializer for {@link MapEntryAttribute} {@link Map.Entry}s.
       */

      public MapEntrySerializer() {
         super((Class<Map.Entry<String, String>>) null);
      }

      /**
       * Creates a new JSON serializer for {@link MapEntryAttribute} {@link Map.Entry}s.
       */

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
      public void serialize(Map.Entry<String, String> mapEntry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
         jsonGenerator.writeStartObject();
         jsonGenerator.writeStringField("key", mapEntry.getKey());
         jsonGenerator.writeStringField("value", mapEntry.getValue());
         jsonGenerator.writeEndObject();
      }
   }

   /**
    * An empty byte array used for cleared local data storage.
    */

   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

   /**
    * An empty map entry used for cleared local data storage.
    */

   public static final Map.Entry<String, String> EMPTY_MAP_ENTRY =
      Map.entry(Strings.EMPTY_STRING, Strings.EMPTY_STRING);

   /**
    * The {@link CharacterSet} used to encode and decode map entry attribute values between {@link String} and byte
    * arrays.
    */

   public static final Charset MAP_ENTRY_CHARSET = StandardCharsets.UTF_8;

   /**
    * An instance of the {@link Map.Entry} class object casted to
    * {@link Class}&lt;{@link Map.Entry}&lt;String,String&gt;&gt; used to reduce the need for type casting.
    */

   @SuppressWarnings("unchecked")
   private static Class<Map.Entry<String, String>> mapEntryClass =
      (Class<Map.Entry<String, String>>) (Object) Map.Entry.class;

   /**
    * The {@link ObjectMapper} used for serialization and deserialization.
    */

   private final static ObjectMapper objectMapper = MapEntryAttributeUtil.create();

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
    * Predicate to determine if the {@link Object} implements the {@link Map.Entry} interface with {@link String} key
    * and value. The key {@link String} cannot be empty. The value {@link String} is allowed to be empty.
    *
    * @param object the {@link Object} to test.
    * @return <code>true</code>, when the <code>object</code> is a proper {@link Map.Entry}; otherwise,
    * <code>false</code>.
    */

   public static boolean isValidMapEntry(Object object) {

      if (!(object instanceof Map.Entry)) {
         return false;
      }

      var mapEntry = (Map.Entry<?, ?>) object;
      var key = mapEntry.getKey();
      var value = mapEntry.getValue();

      //@formatter:off
      return
         (    Strings.isValidAndNonBlank( key )
           && (value instanceof String) );
      //@formatter:on
   }

   /**
    * Predicate to determine if the {@link Object} is a {@link String} that is non-blank.
    *
    * @param object the {@link Object} to test.
    * @return <code>true</code>, when the <code>object</code> is a non-blank {@link String}; otherwise,
    * <code>false</code>.
    */

   public static boolean isValidMapEntryStorageString(Object object) {

      return Strings.isValidAndNonBlank(object);

   }

   /**
    * Deserializes the JSON string, <code>input</code>, to an immutable and constant-foldable {@link Map.Entry}
    * implementation.
    *
    * @param input the JSON string to be deserialized.
    * @return the created immutable foldable {@link Map.Entry} implementation.
    * @throws OseeCoreException when deserialization of the <code>input</code> JSON fails.
    */

   public static Map.Entry<String, String> jsonDecode(String input) {

      if (Objects.isNull(input)) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      "MapEntryAttributeUtil::jsonDecode, the parameter \"input\" cannot be null."
                   );
         //@formatter:on
      }

      if (input.isBlank()) {
         return MapEntryAttributeUtil.EMPTY_MAP_ENTRY;
      }

      try {
         var mutableMapEntry = MapEntryAttributeUtil.objectMapper.readValue(input, MapEntryAttributeUtil.mapEntryClass);
         var immutableFoldableMapEntry = Map.entry(mutableMapEntry.getKey(), mutableMapEntry.getValue());
         return immutableFoldableMapEntry;
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
    * The constructor is private to prevent direct instantiation of the class.
    */

   private MapEntryAttributeUtil() {
   }

}

/* EOF */
