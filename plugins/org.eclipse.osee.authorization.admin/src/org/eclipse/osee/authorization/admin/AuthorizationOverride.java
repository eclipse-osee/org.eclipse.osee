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
