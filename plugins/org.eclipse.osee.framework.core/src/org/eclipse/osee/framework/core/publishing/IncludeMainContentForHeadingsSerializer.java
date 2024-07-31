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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * JSON Serializer for {@link IncludeMainContentForHeadings} objects.
 *
 * @author Jaden W. Puckett
 */

public class IncludeMainContentForHeadingsSerializer extends StdScalarSerializer<@NonNull IncludeMainContentForHeadings> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON serializer for the {@link IncludeMainContentForHeadings> class.
    */

   public IncludeMainContentForHeadingsSerializer() {
      super(IncludeMainContentForHeadings.class);
   }

   /**
    * Serializes an {@link IncludeMetadataAttributes} member as a JSON object with a single field as follows:
    *
    * <pre>
    * { "IncludeMainContentForHeadings" : "&lt;option-name&gt;" }
    * </pre>
    *
    * Where option-name is the option name used in the publishing template JSON.
    *
    * @param includeMainContentForHeadings the enumeration member to be serialized.
    * @param jsonGenerator the {@link JsonGenerator} used to build the serialized form.
    * @param serializerProvider this parameter is not used.
    * @throws NullPointerException when <code>includeMetadataAttributes</code> or <code>jsonGenerator</code> are
    * <code>null</code>.
    * @throws IOException when an error occurs writing to the {@link JsonGenerator}.
    */

   @Override
   public void serialize(@NonNull IncludeMainContentForHeadings includeMainContentForHeadings,
      @Nullable JsonGenerator jsonGenerator, @Nullable SerializerProvider serializerProvider) throws IOException {
      final var safeJsonGenerator = Conditions.requireNonNull(jsonGenerator);
      final var safeIncludeMainContentForHeadings = Conditions.requireNonNull(includeMainContentForHeadings);

      safeJsonGenerator.writeStartObject();
      safeJsonGenerator.writeStringField(IncludeMainContentForHeadings.jsonObjectName,
         safeIncludeMainContentForHeadings.getOptionName());
      safeJsonGenerator.writeEndObject();
   }

}

/* EOF */
