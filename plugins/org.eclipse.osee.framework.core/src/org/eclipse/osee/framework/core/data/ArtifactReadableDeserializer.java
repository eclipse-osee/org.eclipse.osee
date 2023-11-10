/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.ArtifactReadable.ArtifactReadableImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Audrey Denk
 */
public class ArtifactReadableDeserializer extends StdDeserializer<@NonNull ArtifactReadable> {

   private static final long serialVersionUID = 1L;

   public ArtifactReadableDeserializer() {
      this(ArtifactReadable.class);
   }

   public ArtifactReadableDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public ArtifactReadable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      
      if (readTree != null) {
         
    	 return ArtifactReadableImpl.create(readTree.get("id").asLong(), readTree.get("name").asText());
    	 
         
      }

      return ArtifactReadable.SENTINEL;
   }
}