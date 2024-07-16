/*********************************************************************
 * Copyright (c) 2019 Boeing
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

/**
 * @author Donald G Dunne
 */
public class ApplicabilityTokenDeserializer extends StdDeserializer<ApplicabilityToken> {

   public ApplicabilityTokenDeserializer() {
      this(ApplicabilityId.class);
   }

   public ApplicabilityTokenDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public ApplicabilityToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      if (readTree != null) {
         return ApplicabilityToken.valueOf(readTree.get("id").asLong(), readTree.get("name").asText(""));
      } else {
         return null;
      }
   }

}
