/*********************************************************************
 * Copyright (c) 2018 Boeing
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
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Donald G. Dunne
 */
public class TransactionTokenDeserializer extends StdDeserializer<@NonNull TransactionToken> {

   public TransactionTokenDeserializer() {
      this(TransactionToken.class);
   }

   public TransactionTokenDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public TransactionToken deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      Objects.requireNonNull(readTree);
      return TransactionToken.valueOf(readTree.get("id").asLong(), BranchId.valueOf(readTree.get("branchId").asLong()));
   }
}