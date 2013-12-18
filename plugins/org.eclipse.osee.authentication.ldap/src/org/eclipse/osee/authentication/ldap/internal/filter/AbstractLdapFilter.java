/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
