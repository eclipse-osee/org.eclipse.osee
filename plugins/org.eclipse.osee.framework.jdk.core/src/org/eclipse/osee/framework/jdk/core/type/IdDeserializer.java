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
package org.eclipse.osee.framework.jdk.core.type;

import java.io.IOException;
import java.util.function.Function;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.TextNode;

/**
 * @author Ryan D. Brooks
 */
public class IdDeserializer<T extends Id> extends StdDeserializer<T> {
   private final Function<Long, T> creator;

   public IdDeserializer(Function<Long, T> creator) {
      this(Id.class, creator);
   }

   public IdDeserializer(Class<?> object, Function<Long, T> creator) {
      super(object);
      this.creator = creator;
   }

   @Override
   public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      Long id;
      if (readTree instanceof TextNode) {
         String value = readTree.asText();
         id = value.equals("") ? -1L : Long.valueOf(value);
      } else {
         id = readTree.get("id").asLong();
      }
      return creator.apply(id);
   }
}