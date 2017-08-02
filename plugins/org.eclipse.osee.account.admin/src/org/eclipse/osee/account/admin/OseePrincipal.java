/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.SystemRoles;

/**
 * @author Roberto E. Escobar
 */
public interface OseePrincipal extends Principal, Identity<Long> {

   String getLogin();

   Set<String> getRoles();

   String getDisplayName();

   String getUserName();

   String getEmailAddress();

   boolean isActive();

   boolean isAuthenticated();

   Map<String, String> getProperties();

   public static final OseePrincipal Anonymous = createAnonymous();

   public static OseePrincipal createAnonymous() {
      Set<String> roles = new LinkedHashSet<>();
      roles.add(SystemRoles.ROLES_ANONYMOUS);
      Account account = UserTokenAccount.Anonymous;
      return OseePrincipal.valueOf(SystemUser.Anonymous.getName(), account, true, roles, Collections.emptyMap());
   }

   public static OseePrincipal valueOf(String login, Account data, boolean authenticated, Set<String> roles, Map<String, String> properties) {
      final class OseePrincipalImpl extends BaseIdentity<Long> implements OseePrincipal {
         private final String login;
         private final Account data;
         private final boolean authenticated;
         private final Set<String> roles;
         private final Map<String, String> properties;

         public OseePrincipalImpl(String login, Account data, boolean authenticated, Set<String> roles, Map<String, String> properties) {
            super(data.getId());
            this.login = login;
            this.data = data;
            this.authenticated = authenticated;
            this.roles = roles;
            this.properties = properties;
         }

         @Override
         public String getDisplayName() {
            return data.getName();
         }

         @Override
         public String getUserName() {
            return data.getUserName();
         }

         @Override
         public String getEmailAddress() {
            return data.getEmail();
         }

         @Override
         public boolean isActive() {
            return data.isActive();
         }

         @Override
         public Set<String> getRoles() {
            return roles;
         }

         @Override
         public boolean isAuthenticated() {
            return authenticated;
         }

         @Override
         public String getName() {
            return getDisplayName();
         }

         @Override
         public String getLogin() {
            return login;
         }

         @Override
         public Map<String, String> getProperties() {
            return properties;
         }
      }
      return new OseePrincipalImpl(login, data, authenticated, roles, properties);
   }
}