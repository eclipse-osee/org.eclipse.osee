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
package org.eclipse.osee.authentication.ldap;

import javax.naming.directory.SearchControls;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum LdapSearchScope {
   OBJECT_SCOPE(SearchControls.OBJECT_SCOPE),
   ONE_LEVEL_SCOPE(SearchControls.ONELEVEL_SCOPE),
   SUBTREE_SCOPE(SearchControls.SUBTREE_SCOPE);

   private int searchDepth;

   private LdapSearchScope(int searchDepth) {
      this.searchDepth = searchDepth;
   }

   public int asSearchDepth() {
      return searchDepth;
   }

   public static LdapSearchScope parse(String value) {
      LdapSearchScope toReturn = LdapSearchScope.SUBTREE_SCOPE;
      if (Strings.isValid(value)) {
         String toFind = value.toUpperCase().trim();
         for (LdapSearchScope type : LdapSearchScope.values()) {
            if (type.name().equals(toFind)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }
}
