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

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum LdapCredentialsSource {
   SYSTEM_CREDENTIALS,
   USER_CREDENTIALS;

   public boolean isSystemCredentials() {
      return this == SYSTEM_CREDENTIALS;
   }

   public static LdapCredentialsSource parse(String value) {
      LdapCredentialsSource toReturn = LdapCredentialsSource.SYSTEM_CREDENTIALS;
      if (Strings.isValid(value)) {
         String toFind = value.toUpperCase().trim();
         for (LdapCredentialsSource type : LdapCredentialsSource.values()) {
            if (type.name().equals(toFind)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }
}
