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
package org.eclipse.osee.framework.plugin.core.config;

import javax.print.attribute.EnumSyntax;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;

public class OseeRunMode extends EnumBase {
   //   Development, Production;
   /**
    * 
    */
   private static final long serialVersionUID = -6075818565647033161L;
   public static final OseeRunMode Development = new OseeRunMode(0);
   public static final OseeRunMode Production = new OseeRunMode(1);
   private static final String[] stringTable = new String[] {"Development", "Production"};
   private static final OseeRunMode[] enumValueTable = new OseeRunMode[] {Development, Production};

   /**
    * @param value The bus value used for this enumeration.
    */
   private OseeRunMode(int value) {
      super(value);
   }

   public static int getSize() {
      return stringTable.length;
   }

   public static OseeRunMode getEnum(String str) {
      return (OseeRunMode) getEnum(str, stringTable, enumValueTable);
   }

   public static OseeRunMode toEnum(int value) {
      return (OseeRunMode) getEnum(value, enumValueTable);
   }

   public static OseeRunMode toEnum(EnumBase otherEnum) {
      return toEnum(otherEnum.getValue());
   }

   protected String[] getStringTable() {
      return stringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return enumValueTable;
   }
}
