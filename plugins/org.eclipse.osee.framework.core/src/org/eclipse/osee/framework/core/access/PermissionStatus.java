/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.access;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public final class PermissionStatus {
   private final StringBuilder reason;
   private boolean matchedPermission;

   public PermissionStatus() {
      this(true, null);
   }

   public PermissionStatus(boolean matched, String reason) {
      this.reason = new StringBuilder();
      this.matchedPermission = matched;
      if (reason != null) {
         append(reason);
      }
   }

   public boolean matched() {
      return matchedPermission;
   }

   public String getReason() {
      return reason.toString();
   }

   @Override
   public String toString() {
      return "PermissionStatus [reason=" + reason + ", matchedPermission=" + matchedPermission + "]";
   }

   void append(String reason) {
      if (Strings.isValid(reason)) {
         this.reason.append(reason);
      }
   }

   void setReason(String reason) {
      append(reason);
   }

   void setMatches(boolean matchedPermission) {
      this.matchedPermission = matchedPermission;
   }
}