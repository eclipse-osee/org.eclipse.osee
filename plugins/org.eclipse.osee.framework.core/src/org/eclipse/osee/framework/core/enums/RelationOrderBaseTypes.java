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

import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderBaseTypes {

   //@formatter:off
   public static final RelationSorter USER_DEFINED = TokenFactory.createSorterId("AAT0xogoMjMBhARkBZQA", "User Defined");
   public static final RelationSorter LEXICOGRAPHICAL_ASC = TokenFactory.createSorterId("AAT1QW4eVE+YuzsoHFAA", "Lexicographical Ascending");
   public static final RelationSorter LEXICOGRAPHICAL_DESC = TokenFactory.createSorterId("AAmATn6R9m7VCXQQwuQA", "Lexicographical Descending");
   public static final RelationSorter UNORDERED = TokenFactory.createSorterId("AAT1uKZpeDQExlygoIAA", "Unordered");
   public static final RelationSorter PREEXISTING = TokenFactory.createSorterId("AE2ypryqoVzNl6EjpgAA", "Preexisting");
   //@formatter:on

   private static final RelationSorter[] values =
      new RelationSorter[] {USER_DEFINED, LEXICOGRAPHICAL_ASC, LEXICOGRAPHICAL_DESC, UNORDERED};

   private RelationOrderBaseTypes() {
      // Constants
   }

   public static RelationSorter[] values() {
      return values;
   }

   public static RelationSorter getFromGuid(String guid) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(guid, "guid");
      for (RelationSorter type : values()) {
         if (type.getGuid().equals(guid)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type guid does not map to an enum");
   }

   public static RelationSorter getFromOrderTypeName(String orderTypeName) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(orderTypeName, "orderTypeName");
      for (RelationSorter type : values()) {
         if (type.getName().equals(orderTypeName)) {
            return type;
         }
      }
      throw new OseeArgumentException("Order type name does not map to an enum");
   }
}
