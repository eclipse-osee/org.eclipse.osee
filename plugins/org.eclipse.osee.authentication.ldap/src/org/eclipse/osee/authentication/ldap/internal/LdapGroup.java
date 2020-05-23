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

package org.eclipse.osee.authentication.ldap.internal;

import java.util.Map;
import javax.naming.NamingException;
import org.eclipse.osee.authentication.ldap.internal.util.LdapEntry;
import org.eclipse.osee.authentication.ldap.internal.util.VariablePattern;

/**
 * @author Roberto E. Escobar
 */
public class LdapGroup {

   private final LdapEntry ldapEntry;
   private final VariablePattern groupNamePattern;

   public LdapGroup(LdapEntry ldapEntry, VariablePattern groupNamePattern) {
      super();
      this.ldapEntry = ldapEntry;
      this.groupNamePattern = groupNamePattern;
   }

   public String getDistinguishedName() throws NamingException {
      return ldapEntry.getDistinguishedName();
   }

   public String getGroupName() throws NamingException {
      return getEntry(ldapEntry, groupNamePattern);
   }

   @Override
   public String toString() {
      try {
         return "LdapGroup [groupnName=" + getGroupName() + "]";
      } catch (NamingException ex) {
         return "LdapGroup ";
      }
   }

   private static String getEntry(LdapEntry entry, VariablePattern pattern) throws NamingException {
      String toReturn = null;
      if (pattern != null) {
         Map<String, String> values = entry.asMap();
         String value = pattern.expandVariables(values);
         toReturn = value.isEmpty() ? null : value;
      }
      return toReturn;
   }

}
