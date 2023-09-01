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

import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.enums.FileExtension;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * An {@link AttributeTypeToken} implementation for OSEE Attributes that save a JSON string representation of a
 * {@link Map.Entry}.
 *
 * @author Loren K. Ashley
 */

public class AttributeTypeMapEntry extends AttributeTypeGeneric<Map.Entry<String, String>> {

   /**
    * Creates a new {@link AttributeTypeToken} implementation for an OSEE Attribute that saves a JSON string
    * representation of a {@link Map.Entry}.
    */

   //@formatter:off
   public
      AttributeTypeMapEntry
         (
            Long identifier,
            NamespaceToken namespace,
            String name,
            String description,
            String defaultKey,
            String defaultValue
         ) {

      super
      (
         identifier,
         namespace,
         name,
         MediaType.APPLICATION_JSON,
         description,
         TaggerTypeToken.SENTINEL,
         FileExtension.JSON.getFileExtension(),
         Map.entry(defaultKey,defaultValue),
         DisplayHint.MultiLine
      );

   }
   //@formatter:on

   /**
    * For the {@link AttributeTypeMapEntry} class this override always returns <code>true</code>.
    *
    * @return <code>true</code>.
    */

   @Override
   public boolean isMapEntry() {
      return true;
   }

   /**
    * Deserializes a JSON string into a {@link Map.Entry}&lt;String,String&gt; implementation.
    *
    * @return a {@link Map.Entry} containing the string key and string value from the JSON string.
    * @throws OseeCoreException when the JSON decoding fails.
    */

   @Override
   public Map.Entry<String, String> valueFromStorageString(String value) {
      return MapEntryAttributeUtil.jsonDecode(value);
   }

   /**
    * Serializes a {@link Map.Entry} implementation as a JSON string.
    *
    * @return A JSON string representing the {@link Map.Entry}.
    * @throws OseeCoreException when the JSON encoding fails.
    */

   @Override
   public String storageStringFromValue(Map.Entry<String, String> mapEntry) {
      return MapEntryAttributeUtil.jsonEncode(mapEntry);

   }

   //@formatter:on

}

/* EOF */
