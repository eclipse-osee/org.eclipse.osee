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
package org.eclipse.osee.framework.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum PermissionEnum {

   NONE(5, "None"),
   READ(10, "Read"),
   WRITE(20, "Write"),
   FULLACCESS(30, "Full Access"),
   LOCK(40, "Lock"),
   DENY(65535, "Deny");

   // keeping this in sync with the number of permissions will ensure optimal memory usage
   private static final int COUNT = 4;

   private static final Map<Integer, PermissionEnum> rankToPermissionHash =
         new HashMap<Integer, PermissionEnum>((int) (COUNT / .75) + 1, .75f);
   private static final Map<String, PermissionEnum> NameToPermissionHash =
         new HashMap<String, PermissionEnum>((int) (COUNT / .75) + 1, .75f);
   private static final String[] NAME_ARRAY;

   static {
      NAME_ARRAY = new String[values().length];

      int i = 0;
      for (PermissionEnum permission : values()) {
         rankToPermissionHash.put(permission.getPermId(), permission);
         NameToPermissionHash.put(permission.getName(), permission);
         NAME_ARRAY[i++] = permission.getName();
      }
   }

   private int permissionId;
   private String name;
   public boolean add;

   PermissionEnum(int permissionId, String name) {
      this.permissionId = permissionId;
      this.name = name;
   }

   public int getRank() {
      return permissionId;
   }

   public String getName() {
      return name;
   }

   public static PermissionEnum getPermission(int permissionId) {
      return rankToPermissionHash.get(permissionId);
   }

   public static PermissionEnum getPermission(String name) {
      return NameToPermissionHash.get(name);
   }

   public static String[] getPermissionNames() {
      return NAME_ARRAY;
   }

   public int getPermId() {
      return permissionId;
   }
}
