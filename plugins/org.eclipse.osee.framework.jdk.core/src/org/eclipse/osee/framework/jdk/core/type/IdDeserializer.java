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

package org.eclipse.osee.framework.jdk.core.type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Ryan D. Brooks
 */
public class IdDeserializer<T extends Id> extends StdDeserializer<T> {
   private static final long serialVersionUID = 1L;
   private final Function<Long, T> creator;

   public IdDeserializer(Class<T> object, Function<Long, T> creator) {
      super(object);
      this.creator = creator;
   }

   @Override
   public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      Long id;
      if (readTree instanceof TextNode || readTree instanceof IntNode || readTree instanceof LongNode) {
         String value = readTree.asText();
         id = value.equals("") ? -1L : Long.valueOf(value);
      } else {
         Objects.requireNonNull(readTree);
         id = readTree.get("id").asLong();
      }
      return creator.apply(id);
   }
}