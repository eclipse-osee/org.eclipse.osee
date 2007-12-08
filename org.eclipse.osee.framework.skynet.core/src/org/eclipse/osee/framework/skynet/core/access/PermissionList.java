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
package org.eclipse.osee.framework.skynet.core.access;

import java.util.ArrayList;

/**
 * @author Jeff C. Phillips
 */
public class PermissionList {

   private ArrayList<PermissionEnum> permissions = new ArrayList<PermissionEnum>();

   public PermissionList() {
      super();

   }

   /**
    * @return permission list.
    */
   public ArrayList<PermissionEnum> getPermissions() {
      return permissions;
   }

   public void addPermission(PermissionEnum permission) {
      permissions.add(permission);
   }

   public void resetPermissionList() {
      for (int i = 0; i < permissions.size(); i++) {
         permissions.remove(i);
      }
   }
}
