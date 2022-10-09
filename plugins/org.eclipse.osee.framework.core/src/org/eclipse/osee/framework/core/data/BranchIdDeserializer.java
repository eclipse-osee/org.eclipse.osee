/*********************************************************************
 * Copyright (c) 2017 Boeing
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
 * @author Morgan E. Cook
 */
public class BranchIdDeserializer extends StdDeserializer<@NonNull BranchId> {

   public BranchIdDeserializer() {
      this(BranchId.class);
   }

   public BranchIdDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public BranchId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      if (readTree instanceof TextNode) {
         return BranchId.valueOf(readTree.asText());
      }

      if (readTree != null) {
         return BranchId.create(readTree.get("id").asLong(), ArtifactId.valueOf(readTree.get("viewId").asLong()));
      } else {
         return null;
      }

   }

}
