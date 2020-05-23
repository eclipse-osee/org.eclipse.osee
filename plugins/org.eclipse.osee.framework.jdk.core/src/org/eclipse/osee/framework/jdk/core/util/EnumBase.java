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

package org.eclipse.osee.framework.jdk.core.util;

import javax.print.attribute.EnumSyntax;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class EnumBase extends EnumSyntax {
   //TODO We need to possibly refactor getEnum and rework the structure of the enums???

   private static final long serialVersionUID = -5380925813499901084L;

   @Override
   protected abstract String[] getStringTable();

   @Override
   protected abstract EnumSyntax[] getEnumValueTable();

   protected EnumBase(int value) {

      super(value);
   }

   protected static EnumBase getEnum(String str, String[] stringTable, EnumBase[] enumValueTable) {

      for (int i = 0; i < stringTable.length; i++) {
         if (stringTable[i].toUpperCase().equals(str.toUpperCase())) {
            return enumValueTable[i];
         }
      }

      throw new IllegalArgumentException("Not a valid enumeration name: " + str);
      // TODO - We might want to come back and make this more efficient later!!!
      // This was a binary search, but was changed because it was forcing the tables to have to be
      // in the correct binary search order to work.

   }

   protected static EnumBase getEnum(int value, EnumBase[] enumValueTable) {

      return enumValueTable[value - enumValueTable[0].getValue()];
   }

   @Override
   protected int getOffset() {

      return getEnumValueTable()[0].getValue();
   }

   public String getName() {

      return getStringTable()[getValue() - getOffset()];
   }
}