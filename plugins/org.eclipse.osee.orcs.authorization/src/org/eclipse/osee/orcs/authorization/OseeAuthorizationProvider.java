/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.authorization;

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.authorization.admin.Authority;
import org.eclipse.osee.authorization.admin.AuthorizationConstants;
import org.eclipse.osee.authorization.admin.AuthorizationData;
import org.eclipse.osee.authorization.admin.AuthorizationProvider;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.authorization.admin.AuthorizationUser;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Angel Avila
 */
public class OseeAuthorizationProvider implements AuthorizationProvider, AuthorizationData, Authority {
   private OrcsApi orcsApi;

   Principal principal;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public String getScheme() {
      return AuthorizationConstants.OSEE_AUTHORIZATION_PROVIDER;
   }

   @Override
   public Principal getPrincipal() {
      return principal;
   }

   @Override
   public Authority getAuthority() {
      return this;
   }

   @Override
   public AuthorizationData authorize(AuthorizationRequest request) {
      Set<String> rolesFromStore = getRolesFromStore(request.getIdentifier());
      principal = newAuthorization(rolesFromStore);
      return this;
   }

   private BranchId getAdminBranch() {
      return CoreBranches.COMMON;
   }

   private QueryFactory getQuery() {
      return orcsApi.getQueryFactory();
   }

   private Set<String> getRolesFromStore(long identifier) {
      Set<String> roles = new HashSet<>();
      ArtifactReadable oseeUser =
         getQuery().fromBranch(getAdminBranch()).andUuid(identifier).getResults().getExactlyOne();
      ResultSet<ArtifactReadable> groups = oseeUser.getRelated(CoreRelationTypes.UniversalGrouping_Group);
      for (ArtifactReadable group : groups) {
         roles.add(group.getName());
      }

      return roles;
   }

   @Override
   public boolean isInRole(String role) {
      return true;
   }

   private AuthorizationUser newAuthorization(final Set<String> roles) {
      return new AuthorizationUser() {

         @Override
         public Iterable<String> getRoles() {
            return roles;
         }

         @Override
         public boolean isAuthenticated() {
            return true;
         }

         @Override
         public String getName() {
            return null;
         }

         @Override
         public Date getCreationDate() {
            return null;
         }

         @Override
         public boolean isSecure() {
            return false;
         }

         @Override
         public Principal getPrincipal() {
            return null;
         }

         @Override
         public String getScheme() {
            return null;
         }

         @Override
         public boolean isInRole(String role) {
            return false;
         }

      };
   }

}
