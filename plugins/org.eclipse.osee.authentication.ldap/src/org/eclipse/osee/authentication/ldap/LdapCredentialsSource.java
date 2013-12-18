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
