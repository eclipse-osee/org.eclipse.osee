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
package org.eclipse.osee.disposition.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.eclipse.osee.framework.core.util.JsonUtil;

/**
 * @author Dominic A. Guss
 */
public class DispoSummarySeverityDeserializer extends StdDeserializer<DispoSummarySeverity> {

   public DispoSummarySeverityDeserializer() {
      this(DispoSummarySeverity.class);
   }

   public DispoSummarySeverityDeserializer(Class<?> object) {
      super(object);
   }

   @Override
   public DispoSummarySeverity deserialize(JsonParser jp, DeserializationContext ctxt) {
      JsonNode readTree = JsonUtil.getJsonParserTree(jp);
      return DispoSummarySeverity.forVal(readTree.get("name").asText());
   }
}
