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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Stephen J. Molaro
 */
public class UserTokenDeserializer extends StdDeserializer<@NonNull UserToken> {

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
      List<IUserGroupArtifactToken> userGroups = new ArrayList<IUserGroupArtifactToken>();

      if (readTree != null) {
         JsonNode rolesNode = readTree.get("roles");
         if (rolesNode != null) {
            for (JsonNode artToken : rolesNode) {
               IUserGroupArtifactToken roleToken =
                  UserGroupArtifactToken.valueOf(artToken.get("id").asLong(), artToken.get("name").textValue());
               userGroups.add(roleToken);
            }
         }
         List<String> loginIds = new ArrayList<String>();
         for (JsonNode loginId : readTree.get("loginIds")) {
            loginIds.add(loginId.asText());
         }

         boolean active = true;
         JsonNode activeNode = readTree.get("active");
         if (activeNode != null) {
            active = activeNode.asBoolean();
         }

         return UserToken.create(readTree.get("id").asLong(), readTree.get("name").textValue(),
            readTree.get("email").textValue(), readTree.get("userId").textValue(), active, loginIds, userGroups);
      }

      return null;
   }
}