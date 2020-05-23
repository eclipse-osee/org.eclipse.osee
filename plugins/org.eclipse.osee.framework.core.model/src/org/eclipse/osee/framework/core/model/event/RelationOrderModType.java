/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.model.event;

/**
 * @author Donald G. Dunne
 */
public enum RelationOrderModType {
   Default("AFRkIhe6hTMJL8pL4IAA"),
   Absolute("AFRkIhftz3PrR0yVYqwA");

   private final String guid;

   private RelationOrderModType(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static RelationOrderModType getType(String guid) {
      for (RelationOrderModType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

};
