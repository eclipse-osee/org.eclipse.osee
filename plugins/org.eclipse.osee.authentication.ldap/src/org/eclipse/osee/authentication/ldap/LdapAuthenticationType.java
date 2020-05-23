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
public enum LdapAuthenticationType {
   NONE("none"),
   SIMPLE("simple"),
   DIGEST_MD5("strong"),
   EXTERNAL("strong"),
   GSSAPI("strong");

   private String authStyle;

   private LdapAuthenticationType(String authStyle) {
      this.authStyle = authStyle;
   }

   public String getContextAuthenticationName() {
      return authStyle;
   }

   public static LdapAuthenticationType parse(String value) {
      LdapAuthenticationType toReturn = LdapAuthenticationType.NONE;
      if (Strings.isValid(value)) {
         String toFind = value.toUpperCase().trim();
         for (LdapAuthenticationType type : LdapAuthenticationType.values()) {
            if (type.name().equals(toFind)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }
}
