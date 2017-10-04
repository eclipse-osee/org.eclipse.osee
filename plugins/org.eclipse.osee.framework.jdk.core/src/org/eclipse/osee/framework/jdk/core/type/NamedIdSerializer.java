/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.std.SerializerBase;

/**
 * @author Angel Avila
 */
public class NamedIdSerializer extends SerializerBase<NamedId> {

   public NamedIdSerializer() {
      super(NamedId.class);
   }

   @Override
   public void serialize(NamedId id, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      jgen.writeStartObject();
      jgen.writeStringField("id", id.getIdString());
      jgen.writeStringField("name", id.getName());
      jgen.writeEndObject();
   }

   /**
    * Default implementation will write type prefix, call regular serialization method (since assumption is that value
    * itself does not need JSON Array or Object start/end markers), and then write type suffix. This should work for
    * most cases; some sub-classes may want to change this behavior.
    */
   @Override
   public void serializeWithType(NamedId id, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
      typeSer.writeTypePrefixForScalar(id, jgen);
      serialize(id, jgen, provider);
      typeSer.writeTypeSuffixForScalar(id, jgen);
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return createSchemaNode("string", true);
   }
}
