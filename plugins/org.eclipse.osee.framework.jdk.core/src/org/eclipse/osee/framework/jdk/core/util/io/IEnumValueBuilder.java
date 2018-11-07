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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Started with EnumBaseBuilder and made modifications for IEnumValue
 * 
 * @author Cindy Maher
 */
public class IEnumValueBuilder extends JavaFileBuilder {

   protected PriorityQueue<EnumRecord> enums;

   public IEnumValueBuilder(String packageName, String className, String classJavaDoc) {
      super(packageName, className, classJavaDoc, null);

      // Place the enums in a priority queue based on their number value
      enums = new PriorityQueue<EnumRecord>(20, new Comparator<EnumRecord>() {

         @Override
         public int compare(EnumRecord i, EnumRecord j) {
            return i.number - j.number;
         }

      });

      addMethod(getBasicConstructor());
      addMethod(getGetIntValue());
      addMethod(getToEnum());
      addMethod(getGetToEnum());
   }

   public void addEnum(String name, int enumNumber) {
      addEnumField(new EnumRecord(enumNumber, name));
   }

   protected String getBasicConstructor() {
      return "\tprivate int value;\n\n" + "\tprivate " + className + "(int value) {\n" + "\t\tthis.value = value;\n" + "\t}\n";
   }

   private String getGetIntValue() {
      return "\tpublic int getIntValue() {\n" + "\t\treturn value;\n" + "\t}\n";
   }

   private String getToEnum() {
      return "\tpublic static " + className + " toEnum(int value) {\n" + "\t\tfor (" + className + " myEnum : " + className + ".values()) {\n" + "\t\t\tif (myEnum.getIntValue() == value) {\n" + "\t\t\t\treturn myEnum;\n" + "\t\t\t}\n" + "\t\t}\n" + "\t\tthrow new IllegalArgumentException(\"Unable to find " + className + " with value \" + value + \".\");\n" + "\t}\n";
   }

   private String getGetToEnum() {
      return "\tpublic static " + className + " getEnum(String enumString) {\n" + "\t\tfor (" + className + " myEnum : " + className + ".values()) {\n" + "\t\t\tif (" + className + ".valueOf(enumString) == myEnum) {\n" + "\t\t\t\treturn myEnum;\n" + "\t\t\t}\n" + "\t\t}\n" + "\t\tthrow new IllegalArgumentException(\"Unable to find " + className + " to match \" + enumString + \".\");\n" + "\t}\n";
   }

   public static class EnumRecord {
      private final int number;
      private final String name;

      /**
       * @param number The ordinal value of the enumeration
       * @param name The declaration name of the enumeration
       */
      public EnumRecord(int number, String name) {
         this.number = number;
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public int getNumber() {
         return number;
      }

      @Override
      public String toString() {
         String enumStr = name + "(" + number + ")";
         return enumStr;
      }

   }
}
