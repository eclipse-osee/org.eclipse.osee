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

/**
 * @author Roberto E. Escobar
 */
public enum PermissionEnum {

   NONE(5, "None", "Open Access for all Users"),
   READ(10, "Read", "Read only access for specified user"),
   WRITE(20, "Write", "Write access for specified user"),
   LOCK(25, "Lock", "Locked for write by only the specified user"),
   FULLACCESS(30, "Full Access", "Full Access to Read, Write and Change Permissions"),
   DENY(65535, "Deny", "Deny all access.  Usually only if something is ");

   // keeping this in sync with the number of permissions will ensure optimal memory usage
   private static final int COUNT = 4;

   private static final Map<Integer, PermissionEnum> rankToPermissionHash =
      new HashMap<>((int) (COUNT / .75) + 1, .75f);
   private static final Map<String, PermissionEnum> NameToPermissionHash = new HashMap<>((int) (COUNT / .75) + 1, .75f);
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
   private final String description;

   public String getDescription() {
      return description;
   }

   PermissionEnum(int permissionId, String name, String description) {
      this.permissionId = permissionId;
      this.name = name;
      this.description = description;
   }

   public int getRank() {
      return permissionId;
   }

   public String getName() {
      return name;
   }

   public static PermissionEnum getPermission(int permissionId) {
      // Retain for backward compatibility.  OWNER = 40 was removed.
      if (permissionId == 40) {
         return PermissionEnum.LOCK;
      }
      return rankToPermissionHash.get(permissionId);
   }

   public static PermissionEnum getPermission(String name) {
      return NameToPermissionHash.get(name);
   }

   public boolean matches(PermissionEnum toMatch) {
      boolean hasPermission = false;
      if (toMatch == PermissionEnum.READ && this == PermissionEnum.LOCK) {
         hasPermission = true;
      } else if (toMatch == null || this == PermissionEnum.LOCK) {
         hasPermission = false;
      } else {
         hasPermission = this.getRank() >= toMatch.getRank() && !this.equals(PermissionEnum.DENY);
      }
      return hasPermission;
   }

   public static PermissionEnum getMostRestrictive(PermissionEnum perm1, PermissionEnum perm2) {
      PermissionEnum net = null;
      if (perm1 == PermissionEnum.DENY || perm2 == PermissionEnum.DENY) {
         net = PermissionEnum.DENY;
      } else if (perm1 == PermissionEnum.LOCK || perm2 == PermissionEnum.LOCK) {
         net = PermissionEnum.LOCK;
      } else {
         PermissionEnum object1 = perm1 == null ? PermissionEnum.NONE : perm1;
         PermissionEnum object2 = perm2 == null ? PermissionEnum.NONE : perm2;
         if (object1.matches(object2)) {
            net = object2;
         } else {
            net = object1;
         }
      }
      return net;
   }

   public static String[] getPermissionNames() {
      return NAME_ARRAY;
   }

   public int getPermId() {
      return permissionId;
   }
}
