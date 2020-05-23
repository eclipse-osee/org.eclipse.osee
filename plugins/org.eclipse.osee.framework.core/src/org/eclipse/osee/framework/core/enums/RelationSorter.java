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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Defines built-in relation sorting type including unordered
 *
 * @author Andrew M. Finkbeiner
 */
public enum RelationSorter {

   USER_DEFINED("AAT0xogoMjMBhARkBZQA", "User Defined"),
   LEXICOGRAPHICAL_ASC("AAT1QW4eVE+YuzsoHFAA", "Lexicographical_Ascending"),
   LEXICOGRAPHICAL_DESC("AAmATn6R9m7VCXQQwuQA", "Lexicographical_Descending"),
   UNORDERED("AAT1uKZpeDQExlygoIAA", "Unordered"),
   PREEXISTING("AE2ypryqoVzNl6EjpgAA", "Preexisting");

   private final String guid;
   private final String name;

   private RelationSorter(String guid, String name) {
      this.guid = guid;
      this.name = name;
   }

   public boolean equals(String guid) {
      return this.guid.equals(guid);
   }

   public static RelationSorter valueOfName(String name) {
      for (RelationSorter sorter : values()) {
         if (sorter.name.equals(name)) {
            return sorter;
         }
      }
      throw new OseeArgumentException("Order type guid does not map to an enum [%s]", name);
   }

   public static RelationSorter valueOfGuid(String guid) {
      for (RelationSorter sorter : values()) {
         if (sorter.guid.equals(guid)) {
            return sorter;
         }
      }
      throw new OseeArgumentException("Order type guid does not map to an enum [%s]", guid);
   }

   public String getGuid() {
      return guid;
   }
}