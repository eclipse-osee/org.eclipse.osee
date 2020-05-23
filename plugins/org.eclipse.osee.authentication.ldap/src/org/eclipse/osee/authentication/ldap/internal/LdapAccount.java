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

package org.eclipse.osee.authentication.ldap.internal;

import java.util.Map;
import javax.naming.NamingException;
import org.eclipse.osee.authentication.ldap.internal.util.LdapEntry;
import org.eclipse.osee.authentication.ldap.internal.util.VariablePattern;

/**
 * @author Roberto E. Escobar
 */
public class LdapAccount {

   private final LdapEntry ldapEntry;
   private final VariablePattern accountFullName;
   private final VariablePattern accountEmailAddress;
   private final VariablePattern accountUserName;

   public LdapAccount(LdapEntry ldapEntry, VariablePattern accountFullName, VariablePattern accountEmailAddress, VariablePattern accountUserName) {
      super();
      this.ldapEntry = ldapEntry;
      this.accountFullName = accountFullName;
      this.accountEmailAddress = accountEmailAddress;
      this.accountUserName = accountUserName;
   }

   public String getDistinguishedName() throws NamingException {
      return ldapEntry.getDistinguishedName();
   }

   public String getField(String key) throws NamingException {
      return ldapEntry.get(key);
   }

   public String getDisplayName() throws NamingException {
      return getEntry(ldapEntry, accountFullName);
   }

   public String getUserName() throws NamingException {
      return getEntry(ldapEntry, accountUserName);
   }

   public String getEmailAddress() throws NamingException {
      return getEntry(ldapEntry, accountEmailAddress);
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
