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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(using = UserTokenSerializer.class)
public interface UserToken extends ArtifactToken, UserId {

   @JsonCreator
   public static UserToken create(@JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("userId") String userId) {
      return create(Lib.generateArtifactIdAsInt(), name, email, userId, true);
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id, name, userId, active, email, java.util.Collections.singleton(userId), roles);
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, Collection<String> loginIds, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id, name, userId, active, email, loginIds, roles);
   }

   public String getUserId();

   public boolean isActive();

   public boolean isOseeAdmin();

   public String getEmail();

   public Collection<ArtifactToken> getRoles();

   public Collection<String> getLoginIds();

   static final class UserTokenImpl extends NamedIdBase implements UserToken {
      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final Set<ArtifactToken> roles = new HashSet<>();
      private final Collection<String> loginIds = new ArrayList<String>();

      public UserTokenImpl(long id, String name, String userId, boolean active, String email, Collection<String> loginIds, ArtifactToken... roles) {
         super(id, name);
         this.userId = userId;
         this.active = active;
         this.email = email;
         this.loginIds.addAll(loginIds);
         this.roles.addAll(Collections.asHashSet(roles));
         this.admin = Arrays.asList(roles).contains(CoreUserGroups.OseeAdmin);
      }

      @Override
      public ArtifactTypeToken getArtifactType() {
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
      public Collection<ArtifactToken> getRoles() {
         return roles;
      }

      @Override
      public String toString() {
         return String.format("UserToken [name [%s], userId=[%s], active=[%s], email=[%s], loginIds=[%s], roles=[%s]",
            getName(), userId, active, email, getLoginIds(), getRoles());
      }

      @Override
      public BranchId getBranch() {
         return CoreBranches.COMMON;
      }

      @Override
      public Collection<String> getLoginIds() {
         return loginIds;
      }

   }

}