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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Stephen J. Molaro
 */
public class UserTokenSerializer extends StdSerializer<@NonNull UserToken> {

   private static final long serialVersionUID = 1L;

   public UserTokenSerializer() {
      super(UserToken.class);
   }

   @Override
   public void serialize(UserToken userToken, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      jgen.writeStartObject();
      jgen.writeStringField("id", userToken.getIdString());
      jgen.writeStringField("name", userToken.getName());
      jgen.writeStringField("userId", userToken.getUserId());
      jgen.writeBooleanField("active", userToken.isActive());
      jgen.writeStringField("email", userToken.getEmail());

      jgen.writeArrayFieldStart("loginIds");
      for (String loginId : userToken.getLoginIds()) {
         jgen.writeString(loginId);
      }
      jgen.writeEndArray();
      jgen.writeArrayFieldStart("roles");
      for (ArtifactToken role : userToken.getRoles()) {
         jgen.writeStartObject();
         jgen.writeStringField("id", role.getIdString());
         jgen.writeStringField("name", role.getName());
         jgen.writeEndObject();
      }
      jgen.writeEndArray();

      jgen.writeEndObject();
   }

   /**
    * Default implementation will write type prefix, call regular serialization method (since assumption is that value
    * itself does not need JSON Array or Object start/end markers), and then write type suffix. This should work for
    * most cases; some sub-classes may want to change this behavior.
    */
   @Override
   public void serializeWithType(UserToken transaction, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
      typeSer.writeTypePrefixForScalar(transaction, jgen);
      serialize(transaction, jgen, provider);
      typeSer.writeTypeSuffixForScalar(transaction, jgen);
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return createSchemaNode("string", true);
   }
}