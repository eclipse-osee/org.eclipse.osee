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
 * A JSON serializer for {@link RendererMap} implementations.
 *
 * @author Loren K. Ashley
 */

public class RendererMapSerializer extends StdScalarSerializer<@NonNull RendererMap> {

   /**
    * Default serialization version identifier.
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON serializer for {@link RendererMap} implementations.
    */

   public RendererMapSerializer() {
      super(RendererMap.class);
   }

   /**
    * JSON serialization method for {@link RendererMap} implementation.
    *
    * @param rendererMap the {@link RendererMap} implementation to be JSON serialized.
    * @param jsonGenerator the {@link JsonGenerator} used to serialize the map entires.
    * @param serializerProvider not used.
    * @throws IOException when a JSON serialization error occurs.
    */

   @Override
   public void serialize(@NonNull RendererMap rendererMap, @NonNull JsonGenerator jsonGenerator, @Nullable SerializerProvider serializerProvider) throws IOException {
      Objects.requireNonNull(rendererMap).serialize(Objects.requireNonNull(jsonGenerator));
   }

}

/* EOF */
