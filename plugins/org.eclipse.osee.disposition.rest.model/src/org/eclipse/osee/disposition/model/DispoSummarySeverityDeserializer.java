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

package org.eclipse.osee.disposition.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

/**
 * @author Dominic A. Guss
 */
@SuppressWarnings("serial")
public class DispoSummarySeverityDeserializer extends StdDeserializer<DispoSummarySeverity> {

   public DispoSummarySeverityDeserializer() {
      this(DispoSummarySeverity.class);
   }

   public DispoSummarySeverityDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public DispoSummarySeverity deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      JsonNode readTree = jp.getCodec().readTree(jp);
      if (readTree != null) {
         return DispoSummarySeverity.forVal(readTree.asText());
      } else {
         return DispoSummarySeverity.forVal("");
      }
   }
}