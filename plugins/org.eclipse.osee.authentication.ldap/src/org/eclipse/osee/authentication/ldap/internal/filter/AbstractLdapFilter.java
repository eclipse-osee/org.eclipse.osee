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

package org.eclipse.osee.authentication.ldap.internal.filter;

import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.eclipse.osee.authentication.ldap.internal.LdapFilter;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractLdapFilter implements LdapFilter {

   @Override
   public String getUserNameVariableName() {
      return "username";
   }

   @Override
   public LdapSearchScope getAccountSearchScope() {
      return LdapSearchScope.SUBTREE_SCOPE;
   }

   @Override
   public LdapSearchScope getGroupSearchScope() {
      return LdapSearchScope.SUBTREE_SCOPE;
   }

   @Override
   public String getAccountBase() {
      return "ou=people";
   }

   @Override
   public String getGroupBase() {
      return "ou=groups";
   }

}
