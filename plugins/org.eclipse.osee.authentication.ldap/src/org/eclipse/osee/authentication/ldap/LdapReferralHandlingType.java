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
public enum LdapReferralHandlingType {
   IGNORE,
   FOLLOW;

   public String getContextReferralName() {
      return this.name().toLowerCase();
   }

   public static LdapReferralHandlingType parse(String value) {
      LdapReferralHandlingType toReturn = LdapReferralHandlingType.IGNORE;
      if (Strings.isValid(value)) {
         String toFind = value.toUpperCase().trim();
         for (LdapReferralHandlingType type : LdapReferralHandlingType.values()) {
            if (type.name().equals(toFind)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }
}
