/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authorization.admin.internal;

import java.security.Principal;
import java.util.Date;
import org.eclipse.osee.authorization.admin.Authority;
import org.eclipse.osee.authorization.admin.Authorization;

/**
 * @author Roberto E. Escobar
 */
public class AuthorizationImpl implements Authorization {

   private final String scheme;
   private final Date date;
   private final boolean secure;
   private final Principal principal;
   private final Authority authority;

   public AuthorizationImpl(String scheme, Date date, boolean secure, Principal principal, Authority authority) {
      super();
      this.scheme = scheme;
      this.date = date;
      this.secure = secure;
      this.principal = principal;
      this.authority = authority;
   }

   @Override
   public boolean isInRole(String role) {
      return authority.isInRole(role);
   }

   @Override
   public String getScheme() {
      return scheme;
   }

   @Override
   public Date getCreationDate() {
      return date;
   }

   @Override
   public boolean isSecure() {
      return secure;
   }

   @Override
   public Principal getPrincipal() {
      return principal;
   }

   @Override
   public String toString() {
      return "AuthorizationImpl [scheme=" + scheme + ", date=" + date + ", secure=" + secure + ", principal=" + principal + "]";
   }

}
