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

package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.Map;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * Client side implementation of an {@link Attribute} that holds a string key and string value pair that is serialized
 * from and deserialized to a {@link Map.Entry} implementation.
 *
 * @author Loren K. Ashley
 */

public class MapEntryAttribute extends CharacterBackedAttribute<Map.Entry<String, String>> {

   /**
    * Creates a new uninitialized {@link MapEntryAttribute} implementation.
    */

   public MapEntryAttribute() {
   }

   /**
    * Gets the attribute's string key and string value pair as a {@link Map.Entry}&lt;String,String&gt; implementation.
    *
    * @return a {@link Map.Entry} holding the attribute's string key and string value pair.
    */

   @Override
   public Map.Entry<String, String> getValue() {
      @SuppressWarnings("unchecked")
      var mapEntry = (Map.Entry<String, String>) getAttributeDataProvider().getValue();
      return mapEntry;
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
   public Map.Entry<String, String> convertStringToValue(String value) {
      var mapEntry = MapEntryAttributeUtil.jsonDecode(value);
      return mapEntry;
   }

   /**
    * Assigns the <code>mapEntry</code> to the attribute's data provider.
    *
    * @return <code>true</code> when the attribute's value is changed; otherwise, <code>false</code>.
    */

   @Override
   protected boolean subClassSetValue(Map.Entry<String, String> mapEntry) {

      @SuppressWarnings("unchecked")
      ICharacterAttributeDataProvider<Map.Entry<String, String>> dataProvider = this.getAttributeDataProvider();

      return dataProvider.setValue(mapEntry);
   }

}

/* EOF */
