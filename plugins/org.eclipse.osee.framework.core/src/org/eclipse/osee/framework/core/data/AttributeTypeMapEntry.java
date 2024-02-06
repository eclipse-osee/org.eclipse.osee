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
import java.util.Objects;
import java.util.function.Supplier;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.enums.FileExtension;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * An {@link AttributeTypeToken} implementation for OSEE Attributes that save a JSON string representation of a
 * {@link Map.Entry}.
 *
 * @author Loren K. Ashley
 */

public class AttributeTypeMapEntry extends AttributeTypeGeneric<Map.Entry<String, String>> {

   /**
    * Saves a supplier to obtain the tool tip text for the "key" label in the artifact editor.
    */

   private final Supplier<String> keyDescriptionSupplier;

   /**
    * Saves a supplier to obtain the tool tip text for the "value" label in the artifact editor.
    */

   private final Supplier<String> valueDescriptionSupplier;

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
            Supplier<String> keyDescriptionSupplier,
            Supplier<String> valueDescriptionSupplier,
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

      this.keyDescriptionSupplier = keyDescriptionSupplier;
      this.valueDescriptionSupplier = valueDescriptionSupplier;

   }
   //@formatter:on

   /**
    * Gets the tool tip text for the "key" label when a {@link #keyDescriptionSupplier} was specified; otherwise, an
    * empty string.
    *
    * @return the "key" label tool tip text.
    */

   public String getKeyDescription() {
      //@formatter:off
      return
         Objects.nonNull( this.keyDescriptionSupplier )
            ? this.keyDescriptionSupplier.get()
            : Strings.EMPTY_STRING;
      //@formatter:on
   }

   /**
    * Gets the tool tip text for the "value" label when a {@link #valueDescriptionSupplier} was specified; otherwise, an
    * empty string.
    *
    * @return the "value" label tool tip text.
    */

   public String getValueDescription() {
      //@formatter:off
      return
         Objects.nonNull( this.valueDescriptionSupplier )
            ? this.valueDescriptionSupplier.get()
            : Strings.EMPTY_STRING;
      //@formatter:on
   }

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
    * For the {@link AttributeTypeMapEntry} class this override always returns <code>false</code>.
    *
    * @return <code>true</code>.
    */

   @Override
   public boolean isUri() {
      return false;
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

   //@formatter:on

}

/* EOF */
