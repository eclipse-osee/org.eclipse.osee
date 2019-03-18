/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;

/**
 * @author Morgan E. Cook
 */
public class BranchIdDeserializer extends StdDeserializer<BranchId> {

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
      return BranchId.create(readTree.get("id").asLong(), ArtifactId.valueOf(readTree.get("viewId").asLong()));
   }

}
