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

package org.eclipse.osee.authorization.admin;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum AuthorizationOverride {
   NONE,
   PERMIT_ALL,
   DENY_ALL;

   public static AuthorizationOverride parse(String value) {
      AuthorizationOverride toReturn = AuthorizationOverride.NONE;
      if (Strings.isValid(value)) {
         String toFind = value.toUpperCase().trim();
         for (AuthorizationOverride type : AuthorizationOverride.values()) {
            if (type.name().equals(toFind)) {
               toReturn = type;
               break;
            }
         }
      }
      return toReturn;
   }
}
