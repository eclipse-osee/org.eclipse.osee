/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

public final class TokenFactory {

   public static final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   private TokenFactory() {
      // Utility Class
   }

   public static IArtifactType createArtifactType(long guid, String name) {
      return new ArtifactTypeToken(guid, name);
   }

   /**
    * @param token as [name]-[uuid]
    */
   public static IArtifactType createArtifactTypeFromToken(String token) {
      Matcher matcher = nameIdPattern.matcher(token);
      if (matcher.find()) {
         Long uuid = Long.valueOf(matcher.group(2));
         String name = matcher.group(1);
         return new ArtifactTypeToken(uuid, name);
      }
      return null;
   }

   public static RelationTypeToken createRelationType(long id, String name) {
      return RelationTypeToken.create(id, name);
   }

   public static IAccessContextId createAccessContextId(String guid, String name) {
      return new AccessContextIdToken(guid, name);
   }

   public static ArtifactToken createArtifactToken(long id, String guid, String name, IArtifactType artifactType) {
      return ArtifactToken.valueOf(id, guid, name, BranchId.SENTINEL, artifactType);
   }

   public static IUserToken createUserToken(long uuid, String guid, String name, String email, String userId, boolean active, boolean admin, boolean creationRequired) {
      Conditions.checkExpressionFailOnTrue(uuid <= 0, "User Token Uuid must be > 0 for userId [%s]", userId);
      return new UserToken(uuid, guid, name, userId, active, admin, email, creationRequired);
   }

   private final static class ArtifactTypeToken extends NamedIdBase implements IArtifactType {
      public ArtifactTypeToken(Long id, String name) {
         super(id, name);
      }

      @Override
      public Long getGuid() {
         return getId();
      }
   }

   private static class UserToken extends NamedIdBase implements IUserToken {
      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final String guid;
      private final boolean creationRequired;

      public UserToken(long id, String guid, String name, String userId, boolean active, boolean admin, String email, boolean creationRequired) {
         super(id, name);
         this.guid = guid;
         this.userId = userId;
         this.active = active;
         this.admin = admin;
         this.email = email;
         this.creationRequired = creationRequired;
      }

      @Override
      public IArtifactType getArtifactType() {
         return CoreArtifactTypes.User;
      }

      @Override
      public String getUserId() {
         return userId;
      }

      @Override
      public boolean isActive() {
         return active;
      }

      @Override
      public boolean isAdmin() {
         return admin;
      }

      @Override
      public String getEmail() {
         return email;
      }

      @Override
      public boolean isCreationRequired() {
         return creationRequired;
      }

      @Override
      public String toString() {
         return String.format("UserToken [userId=[%s], active=[%s], admin=[%s], email=[%s], creationRequired=[%s]",
            userId, active, admin, email, creationRequired);
      }

      @Override
      public String getGuid() {
         return guid;
      }

      @Override
      public BranchId getBranch() {
         return CoreBranches.COMMON;
      }
   }

   private final static class AccessContextIdToken extends NamedIdentity<String> implements IAccessContextId {
      public AccessContextIdToken(String guid, String name) {
         super(guid, name);
      }

      @Override
      public String toString() {
         return String.format("%s - %s", getName(), getGuid());
      }
   }

   public static String createArtifactTypeTokenString(IArtifactType artifactType) {
      return String.format("[%s]-[%d]", artifactType.getName(), artifactType.getGuid());
   }

}
