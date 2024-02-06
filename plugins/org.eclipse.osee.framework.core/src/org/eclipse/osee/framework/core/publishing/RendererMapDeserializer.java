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

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;

/**
 * A JSON deserializer for {@link RendererMap} implementations.
 *
 * @author Loren K. Ashley
 */

public class RendererMapDeserializer extends StdDeserializer<@NonNull RendererMap> {

   /**
    * Default serialization version identifier.
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON deserializer for {@link RendererMap} implementations.
    */

   protected RendererMapDeserializer() {
      super(RendererMap.class);
   }

   /**
    * JSON deserialization method for {@link RendererMap} implementations.
    *
    * @param jsonParser the {@link JsonParser} containing the serialized {@link RendererMap}.
    * @param arg1 not used.
    * @return a {@link RendererMap} built from the JSON data.
    * @throws IOException when a JSON deserialization error occurs.
    * @throws JacksonException when a JSON deserialization error occurs.
    */

   @Override
   public @NonNull RendererMap deserialize(JsonParser jsonParser, DeserializationContext arg1) throws IOException, JacksonException {
      return RendererMap.deserialize(Objects.requireNonNull(jsonParser));
   }

}

/* EOF */
