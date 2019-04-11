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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(using = UserTokenSerializer.class)
public interface UserToken extends ArtifactToken, UserId {

   @JsonCreator
   public static UserToken create(@JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("userId") String userId) {
      return create(Lib.generateArtifactIdAsInt(), name, email, userId, true, true);
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, boolean creationRequired, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id, name, userId, active, email, creationRequired, roles);
   }

   public String getUserId();

   public boolean isActive();

   public boolean isOseeAdmin();

   public String getEmail();

   public boolean isCreationRequired();

   public ArtifactToken[] getRoles();

   static final class UserTokenImpl extends NamedIdBase implements UserToken {
      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final boolean creationRequired;
      private final ArtifactToken[] roles;

      public UserTokenImpl(long id, String name, String userId, boolean active, String email, boolean creationRequired, ArtifactToken... roles) {
         super(id, name);
         this.userId = userId;
         this.active = active;
         this.email = email;
         this.creationRequired = creationRequired;
         this.roles = roles;
         this.admin = Arrays.asList(roles).contains(CoreArtifactTokens.OseeAdmin);
      }

      @Override
      public ArtifactTypeToken getArtifactTypeId() {
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
      public boolean isOseeAdmin() {
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
      public ArtifactToken[] getRoles() {
         return roles;
      }

      @Override
      public String toString() {
         return String.format(
            "UserToken [name [%s], userId=[%s], active=[%s], email=[%s], creationRequired=[%s], roles=[%s]", getName(),
            userId, active, email, creationRequired, getRoles());
      }

      @Override
      public BranchId getBranch() {
         return CoreBranches.COMMON;
      }
   }
}