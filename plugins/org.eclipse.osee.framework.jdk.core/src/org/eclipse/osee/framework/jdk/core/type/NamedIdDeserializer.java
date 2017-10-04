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
import java.util.function.BiFunction;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

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
      return creator.apply(readTree.get("id").asLong(), readTree.get("name").getTextValue());
   }
}