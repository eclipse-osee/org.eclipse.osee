/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.db.internal.proxy;

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil.LocalData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.framework.jdk.core.util.Zip;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.orcs.core.ds.DataProxy;

/**
 * A {@link DataProxy} implementation for {@link MapEntryAttribute}s.
 *
 * @author Loren K. Ashley
 */

public class MapEntryDataProxy extends AbstractDataProxy<Map.Entry<String, String>> implements org.eclipse.osee.orcs.core.ds.MapEntryDataProxy, ToMessage {

   /**
    * The reported character set name used for externally saved attribute data.
    */

   private static final String ENCODED_CHARSET_NAME = Strings.EMPTY_STRING;

   /**
    * The file extension used for externally saved attribute data.
    */

   private static final String ENCODED_EXTENSION = "zip";

   /**
    * The media type used for externally saved attribute data.
    */

   private static final String ENCODED_MEDIA_TYPE = "application/zip";

   /**
    * Saves the attribute data when it's data has been loaded from the database or the external datas tore.
    */

   private final MapEntryAttributeUtil.LocalData localData;

   /**
    * Creates an new empty {@link MapEntryDataProxy}.
    */

   public MapEntryDataProxy() {
      this.localData = new LocalData();
   }

   /**
    * Sets the local data to sentinel values.
    */

   public void clear() {
      this.localData.clear();
   }

   @Override
   public String getDisplayableString() {
      this.updateFromDataStore();
      return this.localData.getStorageString();
   }

   @Override
   public Map.Entry<String, String> getRawValue() {
      this.updateFromDataStore();
      return this.localData.getMapEntry();
   }

   @Override
   public String getStorageString() {
      if (this.getStorage().isLoadingAllowed()) {
         return Strings.EMPTY_STRING;
      }
      this.updateFromDataStore();
      return this.localData.getStorageString();
   }

   @Override
   public String getUri() {
      return super.getUri();
   }

   @Override
   public void persist() {
      super.persist();
   }

   // Client equivalent method is loadData
   @Override
   public void setData(Map.Entry<String, String> value, String uri) {
      throw new UnsupportedOperationException();
   }

   // Client equivalent method is loadData
   // VarCharDataProxy equivalent method is setData
   public void setDataByObject(Object value, String uri) {

      //@formatter:off
      var state =
           ( MapEntryAttributeUtil.isValidMapEntry(value) ? 1 : 0 )
         + ( Strings.isValidAndNonBlank( uri )            ? 2 : 0 )
         + ( Strings.isValidAndNonBlank( value )          ? 4 : 0 )
         ;
      //@formatter:on

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
             * Value
             */
            @SuppressWarnings("unchecked")
            var valueMapEntry = (Map.Entry<String, String>) value;
            this.setValue(valueMapEntry);
            return;
         }
         case 2: {
            /*
             * URI
             */
            this.localData.clear();
            this.getStorage().clear();
            this.getStorage().setLocator(uri);
            return;
         }
         case 3:
         case 6: {
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

   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean setValue(Map.Entry<String, String> mapEntry) {
      /*
       * If the provided {@link Map.Entry} is not valid, clear the local data and the data store.
       */

      if (!MapEntryAttributeUtil.isValidMapEntry(mapEntry)) {
         this.localData.clear();
         this.getStorage().clear();
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
      this.getStorage().clear();

      /*
       * Check the uncompressed storage size fits the database limit.
       */

      if (this.localData.size() <= JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
         return true;
      }

      /*
       * Uncompressed storage size exceeds database limit, queue for external storage
       */

      try {

         var filenameForZip = this.getResolver().getInternalFileName();
         var uncompressedBytes = this.localData.getStorageBytes();
         var compressedBytes = Zip.compressBytes(uncompressedBytes, filenameForZip);
         this.getStorage().clear();
         this.getStorage().setContent(compressedBytes, ENCODED_EXTENSION, ENCODED_MEDIA_TYPE, ENCODED_CHARSET_NAME);
         return true;

      } catch (Exception e) {

         this.localData.clear();
         this.getStorage().clear();
         return false;

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

      if( this.getStorage().isLocatorValid() ) {

         outMessage
            .title( "DataStore locator is valid." )
            .indentInc()
            .segment( "locator", this.getStorage().getLocator() )
            .indentDec();
      }

      if( this.getStorage().isDataValid() ) {

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

   private void updateFromDataStore() {

      if (this.localData.isDataValid()) {
         return;
      }

      var storage = this.getStorage();

      var compressedContent = storage.getContent();

      var status = this.localData.setFromCompressed(compressedContent);

      if (!status) {
         storage.clear();
      }

   }

}

/* EOF */
