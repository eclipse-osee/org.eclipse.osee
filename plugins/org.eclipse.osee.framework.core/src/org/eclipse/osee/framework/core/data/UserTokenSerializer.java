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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Stephen J. Molaro
 */

@SuppressWarnings("serial")
public class UserTokenSerializer extends StdScalarSerializer<@NonNull UserToken> {

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
}