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

package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil.LocalData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.framework.jdk.core.util.Zip;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.MapEntryAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;
import org.eclipse.osee.jdbc.JdbcConstants;

/**
 * A {@link ICharacterAttributeDataProvider} implementation for Client side attribute implementations with
 * {@link Map.Entry}&lt;String,String&gt; values.
 *
 * @implNote
 * <h2>Character Encoding</h2> The {@link DataStore} cannot determine the character encoding when the attribute is read
 * back from file. Upon read back the {@link AttributeResourceProcessor} always sets the character encoding to
 * "ISO-8859-1" when the filename extension is "zip". The {@link AttributeResourceProcessor} always uses the file
 * extension "zip". This makes the method {@link DataStore#getEncoding} useless as the returned value may not reflect
 * the actual character encoding of the {@link DataStore#rawContent}.
 * <p>
 * The {@link MapEntryAttributeDataProvider} encodes the JSON representation string of the {@link Map.Entry} with the
 * {@link StandardCharsets.UTF_8} and then compresses the data in the "Zip" format. Hence, byte data returned from the
 * {@link DataStore} is "Zip" compressed "UTF-8" encoded data. The encoding indicated by the {@link DataStore} is
 * ignored.
 * <p>
 * <h2>Data Hygiene</h2> The following rules must be adhered to in order to maintain data consistency between the
 * {@link MapEntryAttributeDataProvider}'s local data storage and its {@link DataStore}.
 * <p>
 * <div style="margin-left:2em">
 * <ol>
 * <li>
 * <h3>Setting Value</h3>
 * <ol>
 * <li>When the attribute's value is set from the database and the storage size is less than or equal to
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ul>
 * <li>The local storage is set with the attribute value from the database.</li>
 * <li>The {@link DataStore} data is cleared.</li>
 * <li>The {@link DataStore} resource locator is cleared.</li>
 * </ul>
 * </li>
 * <li>When the attribute's value is set from the database and the storage size is greater than
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ul>
 * <li>The local storage is cleared.</li>
 * <li>The {@link DataStore} data is cleared.</li>
 * <li>The {@link DataStore} is set with the resource locator from the database.</li>
 * </ul>
 * </li>
 * <li>When the attribute's value is set from the application and the storage size is less than or equal to
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ul>
 * <li>The local storage is set with the attribute value from the application.</li>
 * <li>The {@link DataStore} data is cleared.</li>
 * <li>The {@link DataStore} resource locator is cleared.</li>
 * </ul>
 * </li>
 * <li>When the attribute's value is set from the application and the storage size is greater than
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ul>
 * <li>The local storage is set with the attribute value from the application.</li>
 * <li>The {@link DataStore} is set with the attribute value from the application.</li>
 * <li>The {@link DataStore} resource locator is cleared.</li>
 * </ul>
 * </li>
 * </ol>
 * </li>
 * <li>
 * <h3>Getting Value</h3>
 * <ol>
 * <li>When the attribute value is retrieved for storage in the database and the storage size is less than or equal to
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ol>
 * <li>The attribute's value is JSON encoded to a string, the JSON string is encoded with the {@link MAP_ENTRY_CHARSET}
 * and provided for storage in the database.
 * </ol>
 * </li>
 * <li>When the attribute value is retrieved for storage in the database and the storage size is greater than
 * {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH} bytes:
 * <ol>
 * <li>The attribute's resource locator from the {@link DataStore} is provided for storage in the database.</li>
 * <ul>
 * <li>The method {@link #persist} must be called before calling {@link #getData} to push any data in the
 * {@link DataStore} to the external storage and to generate a new resource locator from the {@link GammaId} for the
 * transaction.
 * </ol>
 * </li>
 * <li>When the attribute value is retrieved by the application:
 * <ol>
 * <li>If the local data is not valid, the local data is updated from the {@link DataStore}.</li>
 * <li>A new {@link Map.Entry} that is uncoupled from the attribute's local storage and the {@link DataStore} is
 * provided to the application.</li>
 * </ol>
 * </li>
 * </ol>
 * </div>
 * @author Loren K. Ashley
 */

public class MapEntryAttributeDataProvider extends AbstractAttributeDataProvider<Map.Entry<String, String>> implements ICharacterAttributeDataProvider<Map.Entry<String, String>>, ToMessage {

   /**
    * An empty byte array used as a return value when the {@link DataStore} does not have any content.
    */

   private static final Object[] EMPTY_VALUE_AND_URI_ARRAY = new Object[] {Strings.EMPTY_STRING, Strings.EMPTY_STRING};

   /**
    * File extension provided to the {@link AttributeResourceProcessor} when saving data to the external store.
    */

   private static final String ENCODED_EXTENSION = "zip";

   /**
    * Media type provided to the {@link AttributeResourceProcessor} when saving data to the external store.
    */

   private static final String ENCODED_MEDIA_TYPE = "application/zip";

   /**
    * Charset name provided to the {@link AttributeResourceProcessor} when saving data to the external store.
    */

   private static final String ENCODED_CHARSET_NAME = Strings.EMPTY_STRING;

   /**
    * The {@link DataStore} object used to save and retrieve the attribute data from the external store when the JSON
    * encoded storage string is greater than {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH}.
    */

   private final DataStore dataStore;

   /**
    * Saves the local data for the attribute.
    */

   private final MapEntryAttributeUtil.LocalData localData;

   /**
    * Creates a new {@link MapEntryAttributeDataProvider} for a {@link MapEntryAttribute}.
    *
    * @param attribute the {@link Attribute} to create the data provider for.
    */

   public MapEntryAttributeDataProvider(MapEntryAttribute attribute) {
      super(attribute);
      this.localData = new LocalData();
      this.dataStore = new DataStore(new AttributeResourceProcessor(attribute));
   }

   /**
    * Gets an array containing the data to be saved in the database for the attribute. The {@link DataStore} is not
    * expected to have a valid URI (locator) when the local data is valid and vice-versa. The returned array's contents
    * are as follows:
    * <dl>
    * <dt>Neither local data is valid or the data store's locator:</dt>
    * <dd>A two element array with empty strings in both elements.</dd>
    * <dt>Only the local data is valid:</dt>
    * <dd>A two element array with the attribute's storage string in the first element and an empty string in the second
    * element. The storage string is the JSON representation of the {@link Map.Entry}.</dd>
    * <dt>Only the data store locator is valid:</dt>
    * <dd>A two element array with an empty string in the first element and the data store's locator in the second
    * element. The locator is a string representation of the resource locator that indicates the external storage
    * containing the attribute's value.</dd>
    * <dt>Both local data is valid and the data store's locator are valid:</dt>
    * <dd>A two element array with empty strings in both elements. This is an unexpected condition. When assertions are
    * enabled, an assertion error will be thrown; otherwise, the attribute data is discarded and the local data and data
    * store are cleared.
    *
    * @return a two element array with the attribute data to be saved in the database.
    * @implNote The method {@link DataStore#persist} must be called prior to calling {@link #getData} to push any data
    * saved in the {@link DataStore} and to create a new resource locator with the {@link GammaId} for the transaction.
    */

   @Override
   public Object[] getData() {
      //@formatter:off
      var state =
           ( this.localData.isDataValid()    ? 1 : 0 )
         + ( this.dataStore.isLocatorValid() ? 2 : 0 )
         ;
      //@formatter:on
      switch (state) {
         case 0: {
            /*
             * (none)
             */
            return EMPTY_VALUE_AND_URI_ARRAY;
         }
         case 1: {
            /*
             * Value
             */
            return new Object[] {this.localData.getStorageString(), Strings.EMPTY_STRING};
         }
         case 2: {
            /*
             * URI
             */
            return new Object[] {Strings.EMPTY_STRING, this.dataStore.getLocator()};
         }
         case 3: {
            /*
             * Value & URI
             */
            //@formatter:off
            assert
                 false
               : "MapEntryAttributeDataProvider::getData, both valid local data valid and data store uri are unexpected.";
            //@formatter:on
            /*
             * Eat the value and the URI when both are valid, this is an unexpected condition.
             */
            this.localData.clear();
            this.dataStore.clear();
            return EMPTY_VALUE_AND_URI_ARRAY;
         }
         default: {
            throw Conditions.invalidCase(state, this.getClass().getName(), "loadData", "state", OseeCoreException::new);
         }
      }
   }

   /**
    * Gets a displayable string representation of the {@link Map.Entry}.
    *
    * @return a display string representing the {@link Map.Entry}.
    * @implNote When a local value is present it is returned; otherwise, a value is requested from the
    * {@link DataStore}, saved as the local value, and returned.
    */

   @Override
   public String getDisplayableString() {

      this.updateFromDataStore();
      return this.localData.getStorageString();
   }

   /**
    * Gets the attribute's value as a {@link Map.Entry}<code>&lt;String,String&gt;</code>. The returned
    * {@link Map.Entry} is not backed by the attribute. Changes to the attribute will not be reflected in the returned
    * {@link Map.Entry}.
    *
    * @return when the attribute has a value a {@link Map.Entry}<code>&lt;String,String&gt;</code>; otherwise, an empty
    * {@link Map.Entry}.
    * @implNote When a local value is present it is returned; otherwise, a value is requested from the
    * {@link DataStore}, saved as the local value, and returned.
    */

   @Override
   public Object getValue() {

      this.updateFromDataStore();
      return this.localData.getMapEntry();
   }

   /**
    * Gets the JSON {@link String} representation of the attribute's value.
    *
    * @return a {@link String} containing the attribute's value.
    * @implNote When a local value is present it is returned; otherwise, a value is requested from the
    * {@link DataStore}, saved as the local value, and returned.
    */

   @Override
   public String getValueAsString() {

      this.updateFromDataStore();
      return this.localData.getStorageString();
   }

   /**
    * This method is called after the attributes data has been read from the database to initialize the attribute. The
    * first member of the <code>objects</code> array is expected to be the contents of the "value" column and the second
    * member of the <code>objects</code> array is expected to be the content of the "uri" column. The array elements are
    * expected to contain:
    * <dl>
    * <dt>objects[0] (value):</dt>
    * <dd>This array element contains the value of the "value" column from the database read. When the {@link Map.Entry}
    * is saved in the database, this element is expected to a non-<code>null</code> and non-blank {@link String}. When
    * the {@link Map.Entry} is saved in an external file, this element is expected to be a <code>null</code>, blank, or
    * empty {@link String}. When a valid {@link String} is present, it is expected to be a JSON representation of the
    * {@link Map.Entry}.</dd>
    * <dt>objects[1] (uri):</dt>
    * <dd>This array element contains the value of the "uri" column from the database read. When the {@link Map.Entry}
    * is saved in the database, this element is expected to be a <code>null</code>, blank, or empty {@link String}. When
    * the {@link Map.Entry} is saved in an external file, this element is expected to be a non-<code>null</code> and
    * non-blank {@link String}. When a valid {@link String} is present, it is expected to be a resource locator for the
    * external file containing the JSON representation of the {@link Map.Entry}.</dd>
    * </dl>
    * When the <code>objects</code> array contains a value, the {@link String} is JSON decoded and saved in the local
    * storage as a {@link Map.Entry}. When the <code>objects</code> array contains a URI, the {@link String} is saved in
    * the {@link DataStore} as the resource locator for the file containing the {@link Map.Entry}.
    *
    * @param Objects an array of one or two objects.
    * @ImplNote When a URI resource locator is set in the {@link DataStore} it does not cause the {@link DataStore} to
    * load the file.
    */

   @Override
   public void loadData(Object... objects) {

      if (Objects.isNull(objects) || (objects.length == 0)) {
         return;
      }

      var value = objects[0];
      var uri = (objects.length > 1) ? objects[1] : null;
      //@formatter:off
      var state =
           ( Strings.isValidAndNonBlank( value )            ? 1 : 0 )
         + ( MapEntryAttributeUtil.isValidMapEntry( value ) ? 2 : 0 )
         + ( Strings.isValidAndNonBlank( uri )              ? 4 : 0 )
         ;
      //@formatter:on
      switch (state) {
         case 0: {
            /*
             * (none)
             */
            return;
         }
         case 1: {
            /*
             * Value String
             */
            var valueString = (String) value;
            this.localData.set(valueString);
            this.dataStore.clear();
            return;
         }
         case 2: {
            /*
             * Value Map.Entry
             */
            @SuppressWarnings("unchecked")
            var valueMapEntry = (Map.Entry<String, String>) value;
            this.localData.set(valueMapEntry);
            this.dataStore.clear();
            return;
         }
         case 4: {
            /*
             * URI
             */
            this.localData.clear();
            this.dataStore.clear();
            this.dataStore.setLocator((String) uri);
            return;
         }
         case 3: {
            /*
             * Value & URI
             */
            //@formatter:off
            assert
                 false
               : new Message()
                        .title( "MapEntryAttributeDataProvider::loadData, both value and uri are unexpected." )
                        .indentInc()
                        .segment( "Value", value )
                        .segment( "URI",   uri   )
                        .toString();
            //@formatter:on
            return;
         }
         default: {
            throw Conditions.invalidCase(state, this.getClass().getName(), "loadData", "state", OseeCoreException::new);
         }
      }
   }

   /**
    * If the {@link DataStore} contains attribute data, it is pushed to the external storage. The
    * {@link DataStore#locator} will be set with a resource locator indicating where the attribute data was saved.
    *
    * @param storageId the {@link GammaId} for the transaction being committed to the database.
    * @implNote This method must be called before calling the method {@link #getData()}.
    */

   @Override
   public void persist(GammaId storageId) {
      this.dataStore.persist(storageId);
   }

   /**
    * When the {@link DataStore} contains data that was loaded from the external storage, the external data store will
    * be removed and the contents of the {@link DataStore} and the local values are cleared.
    */

   @Override
   public void purge() {
      this.dataStore.purge();
      if (!this.dataStore.isDataValid()) {
         this.localData.clear();
      }
   }

   /**
    * Setting of the attribute's display string from an external source is not permitted.
    */

   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   /**
    * Sets the attribute's value from the provided {@link Map.Entry}. If the storage byte representation of the
    * <code>mapEntry</code> is greater than {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH}
    *
    * @param mapEntry the value to be set.
    * @return <code>true</code> when the attribute's value has been updated with <code>mapEntry</code>; otherwise,
    * <code>false</code> when the provided value was the same as the current value or when the local data and
    * {@link DataStore} are cleared due to a processing error.
    * @implNote The nominal processing for the <code>mapEntry</code> is as follows:
    * <ul>
    * <li>The <code>mapEntry</code> is JSON encoded into the storage string.</li>
    * <li>The storage string is converted to a byte array using the
    * {@link MapEntryAttributeUtil#MAP_ENTRY_CHARSET}.</li>
    * <li>The <code>mapEntry</code>, storage string, and byte array are saved in local storage.</li>
    * <li>If the byte array length is greater than {@link JdbcConstants#JDBC__MAX_VARCHAR_LENGTH}:</li>
    * <ul>
    * <li>The byte array is compressed and saved in the {@link DataStore}.</li>
    * <li>The {@link DataStore} resource locator is cleared.</li>
    * </ul>
    * <li>Else:</li>
    * <ul>
    * <li>The {@link DataStore} data is cleared.</li>
    * <li>The {@link DataStore} resource locator is cleared.</li>
    * </ul>
    * </ul>
    */

   @Override
   public boolean setValue(Map.Entry<String, String> mapEntry) {

      /*
       * If the provided {@link Map.Entry} is not valid, clear the local data and the data store.
       */

      if (!MapEntryAttributeUtil.isValidMapEntry(mapEntry)) {
         this.localData.clear();
         this.dataStore.clear();
         return false;
      }

      /*
       * If a local value is not available request a value from the data store, so a change in value can be detected.
       */

      this.updateFromDataStore();

      /*
       * If there is a value, it is now set as the local value. Check if new and current attribute values are the same.
       */

      if (this.localData.isEqual(mapEntry)) {
         return false;
      }

      /*
       * The new map entry is valid and does not match the current attribute value. Save it.
       */

      this.localData.set(mapEntry);
      this.dataStore.clear();

      /*
       * Check the uncompressed storage size.
       */

      if (this.localData.size() > JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {

         /*
          * Store in datastore
          */

         var locator = BinaryContentUtils.generateFileName(this.getAttribute());

         byte[] compressedBytes;

         try {
            compressedBytes = Zip.compressBytes(this.localData.getStorageBytes(), locator);
         } catch (Exception e) {
            this.localData.clear();
            this.dataStore.clear();
            return false;
         }

         dataStore.clear();
         dataStore.setContent(compressedBytes, ENCODED_EXTENSION, ENCODED_MEDIA_TYPE, ENCODED_CHARSET_NAME);

      }

      return true;

   }

   /**
    * When the local data is not valid and data is available in the {@link DataStore}, the attribute data will be loaded
    * from the {@link DataStore} and set into the local data.
    *
    * @implNote If an error occurs setting the local data from the {@link DataStore}, both the local data and
    * {@link DataStore} are cleared.
    */

   private void updateFromDataStore() {

      if (this.localData.isDataValid()) {
         return;
      }

      var compressedContent = this.dataStore.getContent();

      var status = this.localData.setFromCompressed(compressedContent);

      if (!status) {
         this.dataStore.clear();
      }

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "MapEntryAttributeDataProvider" )
         .indentInc();

      if( this.localData.isDataValid() ) {

         outMessage
            .title( "Local data is valid." );
      }

      if( this.dataStore.isLocatorValid() ) {

         outMessage
            .title( "DataStore locator is valid." )
            .indentInc()
            .segment( "locator", this.dataStore.getLocator() )
            .indentDec();
      }

      if( this.dataStore.isDataValid() ) {

         outMessage
            .title( "DataStore data is valid." );
      }

      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
