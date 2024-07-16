/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.account.admin;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Identity;

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