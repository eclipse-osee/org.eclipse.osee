/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access;

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