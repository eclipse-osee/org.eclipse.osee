/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("serial")
public class BranchTokenDeserializer extends StdDeserializer<@NonNull BranchToken> {

   public BranchTokenDeserializer() {
      this(BranchId.class);
   }

   public BranchTokenDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public BranchToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      if (readTree instanceof TextNode) {
         return BranchToken.create(Long.valueOf(readTree.asText()), "unknown");
      }

      if (readTree != null) {
         return BranchToken.create(readTree.get("id").asLong(), readTree.get("name").asText(),
            ArtifactId.valueOf(readTree.get("viewId").asLong()));
      } else {
         return null;
      }

   }

}
