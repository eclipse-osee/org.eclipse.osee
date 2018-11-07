/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AccessDetail<T> { //implements Comparable<AccessDetail<?>> {

   private final T accessObject;
   private PermissionEnum permission;
   private Scope scope;
   private String reason;

   public AccessDetail(T accessObject, PermissionEnum permission, Scope scope) {
      this(accessObject, permission, scope, Strings.emptyString());
   }

   public AccessDetail(T accessObject, PermissionEnum permission, Scope scope, String reason) {
      this.accessObject = accessObject;
      this.permission = permission;
      this.reason = reason;
      this.scope = scope;
   }

   public String getReason() {
      return Strings.isValid(reason) ? reason : scope.getPath();
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

   public PermissionEnum getPermission() {
      return permission;
   }

   public Scope getScope() {
      return scope;
   }

   public void setScope(Scope scope) {
      this.scope = scope;
   }

   public T getAccessObject() {
      return accessObject;
   }

   public void setPermission(PermissionEnum permission) {
      this.permission = permission;
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      return hashCode * 37 + (accessObject != null ? accessObject.hashCode() : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AccessDetail<?>) {
         AccessDetail<?> other = (AccessDetail<?>) obj;
         Object object1 = getAccessObject();
         Object object2 = other.getAccessObject();
         boolean result = false;
         if (object1 == null && object2 == null) {
            result = true;
         } else if (object1 != null && object2 != null) {
            result = getAccessObject().equals(other.getAccessObject());
         }
         return result;
      }
      return false;
   }

   @Override
   public String toString() {
      return "AccessDetail [permission=" + getPermission() + ", scope=" + getScope() + ", accessObject=" + getAccessObject() + ", reason=" + getReason() + "]";
   }

   public static AccessDetail<?> resolveAccess(AccessDetail<?> original, AccessDetail<?> data) {
      AccessDetail<?> toReturn = data;
      if (original != null) {
         Scope origScope = original.getScope();
         Scope dataScope = data.getScope();

         if (origScope.isLegacy() || dataScope.isLegacy() || origScope.getPath().equals(dataScope.getPath())) {
            toReturn = getMostRestrictive(original, data);
         } else {
            if (dataScope.getScopeDepth() > origScope.getScopeDepth()) {
               toReturn = data;
            } else {
               toReturn = original;
            }
         }

      }
      return toReturn;
   }

   private static AccessDetail<?> getMostRestrictive(AccessDetail<?> original, AccessDetail<?> data) {
      PermissionEnum origPermission = original.getPermission();
      PermissionEnum newPermission = data.getPermission();

      AccessDetail<?> toReturn = original;
      if (!origPermission.equals(newPermission)) {
         PermissionEnum netPermission = PermissionEnum.getMostRestrictive(origPermission, newPermission);
         if (netPermission.equals(newPermission)) {
            toReturn = data;
         }
      } else {
         String netReason = merge(toReturn.getReason(), data.getReason());
         toReturn.setReason(netReason);
      }
      return toReturn;
   }

   public static String merge(String reason1, String reason2) {
      StringBuilder builder = new StringBuilder();
      builder.append(reason1);
      if (!reason1.equals(reason2)) {
         builder.append(", ");
         builder.append(reason2);
      }
      return builder.toString();
   }
}
