/*******************************************************************************
 * Copyright (c) 2018 Boeing.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephen J. Molaro
 */
public class UserTokenDeserializer extends StdDeserializer<UserToken> {

   private static final long serialVersionUID = 1L;

   public UserTokenDeserializer() {
      this(UserToken.class);
   }

   public UserTokenDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public UserToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      List<ArtifactToken> roles = new ArrayList<ArtifactToken>();
      for (JsonNode artToken : readTree.get("roles")) {
         ArtifactToken roleToken = ArtifactToken.valueOf(artToken.get("id").asLong(), artToken.get("name").textValue());
         roles.add(roleToken);
      }
      ArtifactToken.valueOf(readTree.get("id").asLong(), readTree.get("name").textValue());
      return UserToken.create(readTree.get("id").asLong(), readTree.get("name").textValue(),
         readTree.get("email").textValue(), readTree.get("userId").textValue(), readTree.get("active").asBoolean(),
         readTree.get("creationRequired").asBoolean());
   }
}