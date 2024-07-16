/*********************************************************************
 * Copyright (c) 2011 Boeing
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(using = UserTokenSerializer.class)
public interface UserToken extends ArtifactToken, UserId {

   public static final UserToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL, "", "-1", true);

   @JsonCreator
   public static UserToken create(@JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("userId") String userId) {
      return create(Lib.generateArtifactIdAsInt(), name, email, userId, true);
   }

   public static UserToken create(long id, String name, String email, String userId, boolean active, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id, name, userId, active, email, java.util.Collections.singleton(userId), roles);
   }

   public static UserToken create(String name, String email, String userId, boolean active, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(ArtifactId.SENTINEL.getId(), name, userId, active, email,
         java.util.Collections.singleton(userId), roles);
   }

   public static @NonNull UserToken create(ArtifactId id, String name, String email, String userId, boolean active, Collection<String> loginIds, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id.getId(), name, userId, active, email, loginIds, roles);
   }

   public static @NonNull UserToken create(long id, String name, String email, String userId, boolean active, Collection<String> loginIds, IUserGroupArtifactToken... roles) {
      return new UserTokenImpl(id, name, userId, active, email, loginIds, roles);
   }

   public String getUserId();

   public boolean isActive();

   public boolean isOseeAdmin();

   public String getEmail();

   public Collection<ArtifactToken> getRoles();

   public Collection<String> getLoginIds();

   public ArtifactToken getArtifact();

   public void setArtifact(ArtifactToken artifact);

   static final class UserTokenImpl extends NamedIdBase implements UserToken {
      private final String userId;
      private final boolean active;
      private final boolean admin;
      private final String email;
      private final Set<ArtifactToken> roles = new HashSet<>();
      private final Collection<String> loginIds = new ArrayList<String>();
      private ArtifactToken artifact;

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
      public BranchToken getBranch() {
         return CoreBranches.COMMON;
      }

      @Override
      public Collection<String> getLoginIds() {
         return loginIds;
      }

      @Override
      public ArtifactToken getArtifact() {
         return artifact;
      }

      @Override
      public void setArtifact(ArtifactToken artifact) {
         this.artifact = artifact;
      }

      @Override
      public int hashCode() {
         if (Strings.isValid(userId)) {
            userId.hashCode();
         }
         return id.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof UserToken) {
            if (Strings.isValid(userId) && Strings.isValid(((UserToken) obj).getUserId())) {
               return userId.equals(((UserToken) obj).getUserId());
            }
         }
         return super.equals(obj);
      }

   }

}