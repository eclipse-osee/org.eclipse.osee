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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.OseeOAuthContext;
import org.eclipse.osee.framework.jdk.core.type.OseePermission;
import org.eclipse.osee.framework.jdk.core.type.OseePrincipal;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OseeOAuthContextProvider implements ContextProvider<OseeOAuthContext> {

   @Override
   public OseeOAuthContext createContext(Message message) {
      OseeOAuthContext toReturn = null;
      org.apache.cxf.rs.security.oauth2.common.OAuthContext cxt =
         message.getContent(org.apache.cxf.rs.security.oauth2.common.OAuthContext.class);
      if (cxt != null) {
         toReturn = new OAuthContextImpl(cxt);
      }
      return toReturn;
   }

   private static final class OAuthContextImpl implements OseeOAuthContext {

      private final org.apache.cxf.rs.security.oauth2.common.OAuthContext ctx;

      public OAuthContextImpl(org.apache.cxf.rs.security.oauth2.common.OAuthContext ctx) {
         super();
         this.ctx = ctx;
      }

      @Override
      public OseePrincipal getOwner() {
         UserSubject subject = ctx.getSubject();
         return subject != null ? OAuthUtil.newOseePrincipal(subject) : null;
      }

      @Override
      public OseePrincipal getClient() {
         UserSubject subject = ctx.getClientSubject();
         return subject != null ? OAuthUtil.newOseePrincipal(subject) : null;
      }

      @Override
      public List<OseePermission> getPermissions() {
         List<OseePermission> perms = Collections.emptyList();
         List<OAuthPermission> permissions = ctx.getPermissions();
         if (permissions != null && !permissions.isEmpty()) {
            perms = new ArrayList<OseePermission>();
            for (OAuthPermission permission : permissions) {
               perms.add(newPermission(permission));
            }
         }
         return perms;
      }

      @Override
      public String getTokenGrantType() {
         return ctx.getTokenGrantType();
      }

      @Override
      public String getClientId() {
         return ctx.getClientId();
      }

      @Override
      public String getTokenKey() {
         return ctx.getTokenKey();
      }

      @Override
      public String getTokenAudience() {
         return ctx.getTokenAudience();
      }
   }

   private static OseePermission newPermission(OAuthPermission permission) {
      Long id = new Long(permission.hashCode());
      return new OseePermissionImpl(id, permission);
   }

   private static final class OseePermissionImpl extends BaseIdentity<Long> implements OseePermission {

      private final OAuthPermission permission;

      public OseePermissionImpl(Long id, OAuthPermission permission) {
         super(id);
         this.permission = permission;
      }

      @Override
      public boolean isDefault() {
         return permission.isDefault();
      }

      @Override
      public String getName() {
         return permission.getPermission();
      }

      @Override
      public String getDescription() {
         return permission.getDescription();
      }

      @Override
      public List<String> getUris() {
         return permission.getUris();
      }

      @Override
      public List<String> getHttpVerbs() {
         return permission.getHttpVerbs();
      }

   };
}