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

public class AccessDetail<T> {
   private PermissionEnum permission;
   private final T accessObject;
   private final String reason;

   public AccessDetail(T accessObject, PermissionEnum permission) {
      this.accessObject = accessObject;
      this.permission = permission;
      this.reason = Strings.emptyString();
   }

   public AccessDetail(T accessObject, PermissionEnum permission, String reason) {
      this.accessObject = accessObject;
      this.permission = permission;
      this.reason = reason;
   }

   public String getReason() {
      return reason;
   }

   public PermissionEnum getPermission() {
      return permission;
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
      return String.format("accessDetail [ object=[%s] permission=[%s] reason=[%s]]", getAccessObject(),
         getPermission(), getReason());
   }
}