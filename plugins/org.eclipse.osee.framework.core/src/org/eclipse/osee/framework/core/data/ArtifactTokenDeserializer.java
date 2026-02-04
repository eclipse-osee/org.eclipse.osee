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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.eclipse.osee.framework.core.OrcsTokenService;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTokenDeserializer extends StdDeserializer<ArtifactToken> {

   private static final long serialVersionUID = 596564602216588283L;
   private static OrcsTokenService tokenService;

   public ArtifactTokenDeserializer() {
      super(ArtifactToken.class);
   }

   @Override
   public ArtifactToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      Long id = readTree.get("id").asLong();
      String name = readTree.get("name").textValue();
      ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
      if (readTree.has("typeId")) {
         Long typeId = readTree.get("typeId").asLong();
         artifactType = tokenService.getArtifactType(typeId);
      }
      return ArtifactToken.valueOf(id, name, artifactType);
   }

   public static void setTokenService(OrcsTokenService tokenService) {
      ArtifactTokenDeserializer.tokenService = tokenService;
   }
}