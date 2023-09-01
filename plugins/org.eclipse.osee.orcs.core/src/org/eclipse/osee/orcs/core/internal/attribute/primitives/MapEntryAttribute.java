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

package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.util.Map;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * Server side implementation of an attribute that holds a string key and string value pair that is serialized from and
 * deserialized to a {@link Map.Entry} implementation.
 *
 * @author Loren K. Ashley
 */

@OseeAttribute("MapEntryAttribute")
public class MapEntryAttribute extends CharacterBackedAttribute<Map.Entry<String, String>> {

   /**
    * Creates a new empty {@link MapEntryAttribute} with the specified identifier.
    *
    * @param identifier the attribute's identifier.
    */

   public MapEntryAttribute(Long identifier) {
      super(identifier);
   }

   /**
    * Serializes a {@link Map.Entry} implementation as a JSON string.
    *
    * @return A JSON string representing the {@link Map.Entry}.
    * @throws OseeCoreException when the JSON encoding fails.
    */

   @Override
   public String convertToStorageString(Map.Entry<String, String> mapEntry) {
      return MapEntryAttributeUtil.jsonEncode(mapEntry);
   }

   /**
    * Deserializes a JSON string into a {@link Map.Entry}&lt;String,String&gt; implementation.
    *
    * @return a {@link Map.Entry} containing the string key and string value from the JSON string.
    * @throws OseeCoreException when the JSON decoding fails.
    */

   @Override
   Map.Entry<String, String> subclassConvertStringToValue(String value) {
      var mapEntry = MapEntryAttributeUtil.jsonDecode(value);
      return mapEntry;

   }

}

/* EOF */
