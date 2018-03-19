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

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

/**
 * @author Donald G. Dunne
 */
public class TransactionTokenDeserializer extends StdDeserializer<TransactionToken> {

   public TransactionTokenDeserializer() {
      this(TransactionToken.class);
   }

   public TransactionTokenDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public TransactionToken deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      return TransactionToken.valueOf(readTree.get("id").asLong(), BranchId.valueOf(readTree.get("branchId").asLong()));
   }
}