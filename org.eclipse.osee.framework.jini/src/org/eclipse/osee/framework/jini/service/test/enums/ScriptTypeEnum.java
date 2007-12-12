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
package org.eclipse.osee.framework.jini.service.test.enums;

import javax.print.attribute.EnumSyntax;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;

public class ScriptTypeEnum extends EnumBase {

   private static final long serialVersionUID = -4167041713660187881L;
   public static final ScriptTypeEnum MSG_FUNCTIONAL = new ScriptTypeEnum(0);
   //  public static final ScriptTypeEnum CPP_UNIT_TEST = new ScriptTypeEnum(1);
   public static final ScriptTypeEnum UNIT_TEST = new ScriptTypeEnum(1);
   public static final ScriptTypeEnum FUNCTIONAL_TEST = new ScriptTypeEnum(2);
   public static final ScriptTypeEnum UNKNOWN = new ScriptTypeEnum(2);
   public static final ScriptTypeEnum INVALID = new ScriptTypeEnum(3);

   private static final String[] stringTable =
         new String[] {"MSG_FUNCTIONAL", "UNIT_TEST", "FUNCTIONAL_TEST", "UNKNOWN", "INVALID"};

   private static final ScriptTypeEnum[] enumValueTable =
         new ScriptTypeEnum[] {MSG_FUNCTIONAL, UNIT_TEST, FUNCTIONAL_TEST, UNKNOWN, INVALID};

   private ScriptTypeEnum(int value) {
      super(value);
   }

   public static ScriptTypeEnum toEnum(String str) {
      return (ScriptTypeEnum) getEnum(str, stringTable, enumValueTable);
   }

   protected static ScriptTypeEnum toEnum(int value) {
      return (ScriptTypeEnum) getEnum(value, enumValueTable);
   }

   protected String[] getStringTable() {
      return stringTable;
   }

   protected EnumSyntax[] getEnumValueTable() {
      return enumValueTable;
   }
}