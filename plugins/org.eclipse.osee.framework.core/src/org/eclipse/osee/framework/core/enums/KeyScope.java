/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.enums;

public enum KeyScope {
   READ(0L, "Read"),
   WRITE(1L, "Write"),
   DELETE(2L, "Delete");

   private final Long id;
   private final String name;

   KeyScope(Long id, String name) {
      this.id = id;
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public static KeyScope fromId(Long id) {
      for (KeyScope scope : KeyScope.values()) {
         if (scope.getId().equals(id)) {
            return scope;
         }
      }
      throw new IllegalArgumentException("Invalid scope ID: " + id);
   }
}
