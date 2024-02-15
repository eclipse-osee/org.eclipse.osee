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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * JSON Deserializer for JSON encoded {@link RenderLocation} objects.
 *
 * @author Loren K. Ashley
 */

public class RenderLocationDeserializer extends StdDeserializer<@NonNull RenderLocation> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON deserializer for the {@link RenderLocation} class.
    */

   public RenderLocationDeserializer() {
      super(RenderLocation.class);
   }

   /**
    * Deserializes a JSON representation of a {@link RenderLocation} as follows:
    * <dl>
    * <dt>When the {@link RenderLocation} is deserialized from a serialized POJO:
    * <dt>
    * <dd>The JSON will be in the form of an object with as single field as follows:
    *
    * <pre>
    * { "renderLocation" : "&lt;render-location&gt;" }
    * </pre>
    *
    * </dd>
    * <dt>When the {@link RenderLocation} is deserialized from a {@link RendererMap}:
    * <dt>
    * <dd>The JSON will be in the form of an object field with in a multi-field object. The field will be in the form:
    *
    * <pre>
    *  "renderLocation" : "&lt;render-location&gt;"
    * </pre>
    *
    * The {@link RendererMap} deserializer will have already parsed the field value and this deserializer will get just
    * a {@link TextNode} with the &lt;render-location&gt;.</dd>
    * </dl>
    *
    * @param jsonParser the JSON parser to read from.
    * @param deserializationContext this parameter is not used.
    * @throws NullPointerException when <code>jsonParser</code> is <code>null</code>.
    * @throws IOException when reading from the <code>jsonParser</code> fails.
    * @throws OseeCoreException when the JSON does not reference a valid {@link RenderLocation.
    */

   @Override
   public @NonNull RenderLocation deserialize(@NonNull JsonParser jsonParser,
      @Nullable DeserializationContext deserializationContext) throws IOException {

      final var safeJsonParser = Conditions.requireNonNull(jsonParser);

      JsonNode readTree = safeJsonParser.getCodec().readTree(safeJsonParser);

      final var safeReadTree = Conditions.requireNonNull(readTree);

      //@formatter:off
      var renderLocationName =
         ( safeReadTree instanceof TextNode )
            ? ((TextNode) readTree).asText()
            : readTree.get(RenderLocation.jsonObjectName).asText();
      //@formatter:on

      try {

         var renderLocation = RenderLocation.valueOf(renderLocationName);

         return Conditions.requireNonNull(renderLocation);

      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "RenderLocation::deserialize, unknown format name." )
                             .indentInc()
                             .segment( "Render Location", renderLocationName )
                             .toString()
                   );
         //@formatter:on
      }
   }

}

/* EOF */
