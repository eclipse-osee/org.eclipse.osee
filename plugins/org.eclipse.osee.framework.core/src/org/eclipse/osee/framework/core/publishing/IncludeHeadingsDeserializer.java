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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * JSON Deserializer for JSON encoded {@link IncludeHeading} enumeration members.
 *
 * @author Loren K. Ashley
 */

public class IncludeHeadingsDeserializer extends StdDeserializer<@NonNull IncludeHeadings> {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new JSON deserializer for the {@link IncludeHeadings} class.
    */

   public IncludeHeadingsDeserializer() {
      super(IncludeHeadings.class);
   }

   /**
    * Deserializes a JSON representation of a {@link IncludeHeadings} enumeration member from it's associated
    * {@link OutliningOptions} name.
    *
    * @param jsonParser the JSON parser to read from.
    * @param deserializationContext this parameter is not used.
    * @throws NullPointerException when <code>jsonParser</code> is <code>null</code>.
    * @throws IOException when reading from the <code>jsonParser</code> fails.
    * @throws OseeCoreException when the JSON does not reference a valid {@link FormatIndicator.
    */

   @Override
   public @NonNull IncludeHeadings deserialize(@NonNull JsonParser jsonParser,
      @Nullable DeserializationContext deserializationContext) throws IOException {

      final var safeJsonParser = Conditions.requireNonNull(jsonParser);

      JsonNode readTree = safeJsonParser.getCodec().readTree(safeJsonParser);

      final var safeReadTree = Conditions.requireNonNull(readTree);

      //@formatter:off
      var includeHeadingsValue =
         ( safeReadTree instanceof TextNode )
            ? ((TextNode) readTree).asText()
            : readTree.get(IncludeHeadings.jsonObjectName).asText();

      var includeHeadings =
         IncludeHeadings
            .ofOutliningOptionName( includeHeadingsValue )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "IncludeHeadings::deserialize, unknown value name." )
                                         .indentInc()
                                         .segment( "Value Name", includeHeadingsValue )
                                         .toString()
                               )
               );
      //@formatter:on
      return Conditions.requireNonNull(includeHeadings);

   }
}
