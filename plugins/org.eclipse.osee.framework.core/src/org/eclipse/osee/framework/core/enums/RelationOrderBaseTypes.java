/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderBaseTypes extends NamedIdentity implements IRelationSorterId {
   public static final RelationOrderBaseTypes USER_DEFINED = new RelationOrderBaseTypes("AAT0xogoMjMBhARkBZQA",
      "User Defined");
   public static final RelationOrderBaseTypes LEXICOGRAPHICAL_ASC = new RelationOrderBaseTypes("AAT1QW4eVE+YuzsoHFAA",
      "Lexicographical Ascending");
   public static final RelationOrderBaseTypes LEXICOGRAPHICAL_DESC = new RelationOrderBaseTypes("AAmATn6R9m7VCXQQwuQA",
      "Lexicographical Descending");
   public static final RelationOrderBaseTypes UNORDERED = new RelationOrderBaseTypes("AAT1uKZpeDQExlygoIAA",
      "Unordered");
   public static final RelationOrderBaseTypes[] values = new RelationOrderBaseTypes[] {USER_DEFINED,
      LEXICOGRAPHICAL_ASC, LEXICOGRAPHICAL_DESC, UNORDERED};

   RelationOrderBaseTypes(String guid, String name) {
      super(guid, name);
   }

   @Override
   public String toString() {
      return String.format("[%s,%s]", getName(), getGuid());
   }

   public static RelationOrderBaseTypes getFromGuid(String guid) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(guid, "guid");
      for (RelationOrderBaseTypes type : values) {
         if (type.getGuid().equals(guid)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type guid does not map to an enum");
   }

   public static RelationOrderBaseTypes getFromOrderTypeName(String orderTypeName) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(orderTypeName, "orderTypeName");
      for (RelationOrderBaseTypes type : values) {
         if (type.getName().equals(orderTypeName)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type name does not map to an enum");
   }
}
