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
