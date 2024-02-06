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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * JSON Deserializer for JSON encoded {@link FormatIndicator} objects.
 *
 * @author fi390f
 */
public class FormatIndicatorDeserializer extends StdDeserializer<@NonNull FormatIndicator> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON deserializer for the {@link FormatIndicator} class.
    */

   public FormatIndicatorDeserializer() {
      super(FormatIndicator.class);
   }

   /**
    * Finds the {@link FormatIndicator} described by the JSON in the <code>jsonParser</code>.
    *
    * @param jsonParser the JSON parser to read from.
    * @param deserializationContext this parameter is not used.
    * @throws NullPointerException when <code>jsonParser</code> is <code>null</code>.
    * @throws IOException when reading from the <code>jsonParser</code> fails.
    * @throws OseeCoreException when the JSON does not reference a valid {@link FormatIndicator.
    */

   @Override
   public @NonNull FormatIndicator deserialize(@NonNull JsonParser jsonParser, @Nullable DeserializationContext deserializationContext) throws IOException {

      return FormatIndicator.deserialize(Objects.requireNonNull(jsonParser));
   }
}
