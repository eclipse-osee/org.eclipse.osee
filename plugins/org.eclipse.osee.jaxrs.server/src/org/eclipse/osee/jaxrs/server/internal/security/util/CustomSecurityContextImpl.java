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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class CustomSecurityContextImpl implements SecurityContext, Serializable {

   private static final long serialVersionUID = -8643469202637719566L;
   private OseePrincipal principal;

   public CustomSecurityContextImpl(OseePrincipal principal) {
      super();
      this.principal = principal;
   }

   @Override
   public boolean isUserInRole(String role) {
      Collection<String> roles = principal.getRoles();
      if (roles == null) {
         roles = Collections.emptyList();
      }
      return roles.contains(role);
   }

   @Override
   public OseePrincipal getUserPrincipal() {
      return principal;
   }

   private void writeObject(java.io.ObjectOutputStream out) throws IOException {
      out.writeLong(principal.getGuid());
      writeString(out, principal.getDisplayName());
      writeString(out, principal.getEmailAddress());
      writeString(out, principal.getLogin());
      writeString(out, principal.getName());
      writeString(out, principal.getUserName());
      out.writeBoolean(principal.isActive());
      out.writeBoolean(principal.isAuthenticated());
      writeRoles(out, principal.getRoles());
      writeProps(out, principal.getProperties());
   }

   private void writeString(java.io.ObjectOutputStream out, String value) throws IOException {
      if (value != null) {
         out.writeObject(value);
      } else {
         out.writeObject("");
      }
   }

   private void readObject(java.io.ObjectInputStream in) throws IOException {
      Long uuid = in.readLong();
      String displayName = in.readUTF();
      String email = in.readUTF();
      String login = in.readUTF();
      String name = in.readUTF();
      String username = in.readUTF();
      boolean active = in.readBoolean();
      boolean authenticated = in.readBoolean();
      Set<String> roles = readRoles(in);
      Map<String, String> props = readProps(in);
      principal =
         new OseePrincipalImpl(uuid, displayName, email, login, name, username, active, authenticated, roles, props);
   }

   private void writeRoles(java.io.ObjectOutputStream out, Set<String> roles) throws IOException {
      writeString(out, org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", roles));
   }

   private Set<String> readRoles(java.io.ObjectInputStream in) throws IOException {
      Set<String> roles;
      String allRoles = in.readUTF();
      if (Strings.isValid(allRoles)) {
         roles = new LinkedHashSet<>();
         for (String role : allRoles.split(",")) {
            roles.add(role);
         }
      } else {
         roles = java.util.Collections.emptySet();
      }
      return roles;
   }

   private void writeProps(java.io.ObjectOutputStream out, Map<String, String> props) throws IOException {
      String value = props.toString();
      if (!value.equals("[]")) {
         value = value.substring(1, value.length() - 1);
         out.writeUTF(value);
      } else {
         out.writeUTF("");
      }
   }

   private Map<String, String> readProps(java.io.ObjectInputStream in) throws IOException {
      Map<String, String> props;
      String allProps = in.readUTF();
      if (Strings.isValid(allProps)) {
         props = new LinkedHashMap<>();
         String key = null;
         for (String value : allProps.split(",")) {
            if (key == null) {
               key = value;
            } else {
               props.put(key, value);
               key = null;
            }
         }
      } else {
         props = Collections.emptyMap();
      }
      return props;
   }
}