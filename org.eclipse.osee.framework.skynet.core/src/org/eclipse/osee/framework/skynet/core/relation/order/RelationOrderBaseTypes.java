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
package org.eclipse.osee.framework.skynet.core.relation.order;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Andrew M. Finkbeiner
 */
public enum RelationOrderBaseTypes implements IRelationSorterId {

   USER_DEFINED("AAT0xogoMjMBhARkBZQA", "User Defined"),
   LEXICOGRAPHICAL_ASC("AAT1QW4eVE+YuzsoHFAA", "Lexicographical Ascending"),
   LEXICOGRAPHICAL_DESC("AAmATn6R9m7VCXQQwuQA", "Lexicographical Descending"),
   UNORDERED("AAT1uKZpeDQExlygoIAA", "Unordered");

   private String guid;
   private String prettyName;

   RelationOrderBaseTypes(String guid, String prettyName) {
      this.guid = guid;
      this.prettyName = prettyName;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String prettyName() {
      return prettyName;
   }

   @Override
   public String toString() {
      return String.format("[%s,%s]", prettyName(), getGuid());
   }

   public static RelationOrderBaseTypes getFromGuid(String guid) throws OseeArgumentException {
      for (RelationOrderBaseTypes type : values()) {
         if (type.getGuid().equals(guid)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type guid does not map to an enum");
   }

   public static RelationOrderBaseTypes getFromOrderTypeName(String orderTypeName) throws OseeArgumentException {
      for (RelationOrderBaseTypes type : values()) {
         if (type.prettyName.equals(orderTypeName)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type name does not map to an enum");
   }
}
