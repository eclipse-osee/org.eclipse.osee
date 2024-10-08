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
import java.io.IOException;
import java.util.function.BiFunction;

/**
 * @author Ryan D. Brooks
 */
public class NamedIdDeserializer<T extends NamedId> extends StdDeserializer<T> {
   private final BiFunction<Long, String, T> creator;

   public NamedIdDeserializer(BiFunction<Long, String, T> creator) {
      this(NamedId.class, creator);
   }

   public NamedIdDeserializer(Class<?> object, BiFunction<Long, String, T> creator) {
      super(object);
      this.creator = creator;
   }

   @Override
   public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      return creator.apply(readTree.get("id").asLong(), readTree.get("name").textValue());
   }
}