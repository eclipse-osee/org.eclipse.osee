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

package org.eclipse.osee.framework.core.renderer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * JSON Serializer for {@link RenderLocation} objects.
 *
 * @author Loren K. Ashley
 */

public class RenderLocationSerializer extends StdScalarSerializer<@NonNull RenderLocation> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON serializer for the {@link FormatIndicator} class.
    */

   public RenderLocationSerializer() {
      super(RenderLocation.class);
   }

   /**
    * Serializes a {@link RenderLocation} member as a JSON object with a single field as follows:
    *
    * <pre>
    * { "renderLocation" : "&lt;render-location&gt;" }
    * </pre>
    *
    * Where render-location is the Java code name of the {@link RenderLocation} enumeration member given by
    * {@link RenderLocation#name}.
    *
    * @param renderLocation the enumeration member to be serialized.
    * @param jsonGenerator the {@link JsonGenerator} used to build the serialized form.
    * @param serializerProvider this parameter is not used.
    * @throws NullPointerException when <code>renderLocation</code> or <code>jsonGenerator</code> are <code>null</code>.
    * @throws IOException when an error occurs writing to the {@link JsonGenerator}.
    */

   @Override
   public void serialize(@NonNull RenderLocation renderLocation, @Nullable JsonGenerator jsonGenerator,
      @Nullable SerializerProvider serializerProvider) throws IOException {
      final var safeJsonGenerator = Conditions.requireNonNull(jsonGenerator);
      final var safeRenderLocation = Conditions.requireNonNull(renderLocation);

      safeJsonGenerator.writeStartObject();
      safeJsonGenerator.writeStringField(RenderLocation.jsonObjectName, safeRenderLocation.name());
      safeJsonGenerator.writeEndObject();
   }

}

/* EOF */
