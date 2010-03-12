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
package org.eclipse.osee.ote.core.testPoint;

import javax.print.attribute.EnumSyntax;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;




public class Operation extends EnumBase {

   /**
    * 
    */
   private static final long serialVersionUID = -3132727420541603024L;
   public static final Operation OR = new Operation(0);
   public static final Operation AND = new Operation(1);

   private static final String[] stringTable = new String[] {"OR", "AND"};
   private static final Operation[] enumValueTable = new Operation[] {OR, AND};

   private Operation(int value) {
      super(value);
   }

   public static Operation toEnum(String str) {
      return (Operation) getEnum(str, stringTable, enumValueTable);
   }

   protected static Operation toEnum(int value) {
      return (Operation) getEnum(value, enumValueTable);
   }

   protected String[] getStringTable() {
      return stringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return enumValueTable;
   }
}