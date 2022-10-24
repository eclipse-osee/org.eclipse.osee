/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.synchronization.forest.denizens;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * A factory for creating {@link NativeDataTypeKey} objects. A new factory should be created and used for each
 * Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public class NativeDataTypeKeyFactory {

   /**
    * A static map to cache {@link NativeDataTypeKey} objects for the non-enumerated data types.
    */

   private static EnumMap<NativeDataType, NativeDataTypeKey> nonEnumeratedKeyCache;

   static {
      //@formatter:off
      NativeDataTypeKeyFactory.nonEnumeratedKeyCache = new EnumMap<>(NativeDataType.class);
      Stream.of( NativeDataType.values() )
         .filter  ( ( member ) -> !member.equals( NativeDataType.ENUMERATED) )
         .forEach ( ( member ) -> NativeDataTypeKeyFactory.nonEnumeratedKeyCache.put( member, new NativeDataTypeKey( member ) ) );
      //@formatter:on
   }

   /**
    * A {@link Map} used to cache {@link NativeDataTypeKey} objects for the enumerated data types of a single
    * Synchronization Artifact.
    */

   private final Map<Object, NativeDataTypeKey> enumeratedKeyCache;

   /**
    * Creates a new {@link NativeDataTypeKey} factory with an empty cache for enumerated data types.
    */

   public NativeDataTypeKeyFactory() {
      this.enumeratedKeyCache = new HashMap<>();
   }

   /**
    * Gets from cache or creates and caches a {@link NativeDataTypeKey} for the enumeration represented by the
    * {@link AttributeTypeToken}.
    *
    * @param attributeTypeToken the native {@link AttributeTypeToken} representing the enumeration.
    * @return the cached or new {@link NativeDataTypeKey} for the enumerated data type.
    */

   private NativeDataTypeKey createOrGetEnumeratedKey(AttributeTypeToken attributeTypeToken) {
      var key = attributeTypeToken.getId();
      var value = this.enumeratedKeyCache.get(key);

      if (value == null) {
         value = new NativeDataTypeKey(attributeTypeToken);
         this.enumeratedKeyCache.put(key, value);
      }

      return value;
   }

   /**
    * Creates or gets from cache a {@link NativeDataTypeKey} for the data type represented by the specified native
    * {@link AttributeTypeToken}.
    *
    * @param attributeTypeToken the native {@link AttributeTypeToken} representing a Synchronization Artifact data type.
    * @return the {@link NativeDataTypeKey} for the Synchronization Artifact data type.
    */

   public NativeDataTypeKey createOrGetKey(AttributeTypeToken attributeTypeToken) {

      var nativeDataType = NativeDataType.classifyNativeDataType(attributeTypeToken);

      //@formatter:off
      return
         ( nativeDataType == NativeDataType.ENUMERATED )
            ? this.createOrGetEnumeratedKey( attributeTypeToken )
            : NativeDataTypeKeyFactory.nonEnumeratedKeyCache.get( nativeDataType );
      //@formatter:on
   }

}

/* EOF */