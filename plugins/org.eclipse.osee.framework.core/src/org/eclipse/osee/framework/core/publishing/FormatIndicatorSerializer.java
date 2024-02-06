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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * JSON Serializer for {@link FormatIndicator} objects.
 *
 * @author Loren K. Ashley
 */

public class FormatIndicatorSerializer extends StdScalarSerializer<@NonNull FormatIndicator> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON serializer for the {@link FormatIndicator} class.
    */

   public FormatIndicatorSerializer() {
      super(FormatIndicator.class);
   }

   /**
    * Creates the JSON serialized form of the {@link FormatIndicator}.
    *
    * @param formatIndicator the enumeration member to be serialized.
    * @param jsonGenerator the {@link JsonGenerator} used to build the serialized form.
    * @param serializerProvider this parameter is not used.
    * @throws NullPointerException when <code>formatIndicator</code> or <code>jsonGenerator</code> are
    * <code>null</code>.
    * @throws IOException when an error occurs writing to the {@link JsonGenerator}.
    */

   @SuppressWarnings("null")
   @Override
   public void serialize(@NonNull FormatIndicator formatIndicator, @Nullable JsonGenerator jsonGenerator, @Nullable SerializerProvider serializerProvider) throws IOException {
      if (jsonGenerator == null) {
         throw new NullPointerException();
      }
      Objects.requireNonNull(formatIndicator).serialize(jsonGenerator);
   }

}

/* EOF */
