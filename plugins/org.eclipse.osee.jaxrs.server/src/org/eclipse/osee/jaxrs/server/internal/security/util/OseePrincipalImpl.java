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
package org.eclipse.osee.jaxrs.server.internal.security.util;

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseePrincipalImpl extends BaseIdentity<Long>implements OseePrincipal {

   private final String displayName;
   private final String email;
   private final String login;
   private final String name;
   private final String username;
   private final boolean active;
   private final boolean authenticated;
   private final Set<String> roles;
   private final Map<String, String> props;

   public OseePrincipalImpl(Long uuid, String displayName, String email, String login, String name, String username, boolean active, boolean authenticated, Set<String> roles, Map<String, String> props) {
      super(uuid);
      this.displayName = displayName;
      this.email = email;
      this.login = login;
      this.name = name;
      this.username = username;
      this.active = active;
      this.authenticated = authenticated;
      this.roles = roles;
      this.props = props;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getLogin() {
      return login;
   }

   @Override
   public Set<String> getRoles() {
      return roles;
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String getUserName() {
      return username;
   }

   @Override
   public String getEmailAddress() {
      return email;
   }

   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public boolean isAuthenticated() {
      return authenticated;
   }

   @Override
   public Map<String, String> getProperties() {
      return props;
   }

   @Override
   public String getOseeGuid() {
      String toReturn = "N/A";
      if (props != null) {
         String value = props.get("osee.guid");
         if (Strings.isValid(value)) {
            toReturn = value;
         }
      }
      return toReturn;
   }
}
