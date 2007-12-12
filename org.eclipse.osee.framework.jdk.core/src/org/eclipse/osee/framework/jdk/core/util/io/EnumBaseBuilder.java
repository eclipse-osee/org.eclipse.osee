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
 * @author Robert A. Fisher
 * @deprecated
 */
@Deprecated
public class EnumBaseBuilder extends JavaFileBuilder {

   protected PriorityQueue<EnumRecord> enums;

   private static final String stringTableMethod =
         "\tprotected String[] getStringTable() {\n" + "\t\treturn stringTable;\n" + "\t}";
   private static final String enumTableMethod =
         "\tprotected EnumSyntax[] getEnumValueTable() {\n" + "\t\treturn enumValueTable;\n" + "\t}";

   public EnumBaseBuilder(String packageName, String className, String classJavaDoc) {
      super(packageName, className, classJavaDoc, "EnumBase");

      // Place the enums in a priority queue based on their number value
      enums = new PriorityQueue<EnumRecord>(20, new Comparator<EnumRecord>() {

         public int compare(EnumRecord i, EnumRecord j) {
            return i.number - j.number;
         }

      });

      addMethod(getGetEnum());
      addMethod(getToEnum1());
      addMethod(getToEnum2());
      addMethod(stringTableMethod);
      addMethod(enumTableMethod);
   }

   public void addEnum(String name, int enumNumber) {
      addEnum(name, enumNumber, null);
   }

   public void addEnum(String name, int enumNumber, String additionalValues) {
      addField(new Field(className, name, true, true, true, getInitialValue(enumNumber, additionalValues)));
      enums.offer(new EnumRecord(enumNumber, name));
   }

   protected String getBasicConstructor() {
      return "\tprotected " + className + "(int value) {\n" + "\t\tsuper(value);\n" + "\t}";
   }

   private String getInitialValue(int enumNumber, String additionalValues) {
      return "new " + className + "(" + enumNumber + ((additionalValues != null) ? "," + additionalValues : "") + ")";
   }

   private String getGetEnum() {
      return "\tpublic static " + className + " getEnum(String str) {\n" + "\t\treturn (" + className + ") getEnum(str, stringTable, enumValueTable);\n" + "\t}";
   }

   private String getToEnum1() {
      return "\tpublic static " + className + " toEnum(int value) {\n" + "\t\treturn (" + className + ") getEnum(value, enumValueTable);\n" + "\t}";
   }

   private String getToEnum2() {
      return "\tpublic static " + className + " toEnum(EnumBase otherEnum) {\n" + "\t\treturn toEnum(otherEnum.getValue());\n" + "\t}";
   }

   /*
    * Add the fields from the parent, along with fields that have to be generated dynamically
    * based on what enums have been added over the lifetime of this builder.
    * 
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.JavaFileBuilder#getFields(java.lang.StringBuilder)
    */
   @Override
   protected void getFields(StringBuilder string) {
      super.getFields(string);

      // Get a copy of the priority queue, in case this is called many times
      PriorityQueue<EnumRecord> records = new PriorityQueue<EnumRecord>(enums);

      StringBuilder stringTable = new StringBuilder();
      StringBuilder enumValueTable = new StringBuilder();

      // This shoud start at one less than the first value since EnumBase accounts for non-zero based enums
      int lastEnumVal = records.peek().number - 1;

      // Start the declarations for the two necessary fields
      stringTable.append("\tprivate static final String[] stringTable = new String[] {");
      enumValueTable.append("\tprivate static final " + className + "[] enumValueTable = new " + className + "[] {");

      EnumRecord record;
      // Pop each of the records from the queue
      while ((record = records.poll()) != null) {

         // Fill skipped values in the enum listing with null references
         for (int x = lastEnumVal; x < (record.number - 1); x++) {
            stringTable.append("null, ");
            enumValueTable.append("null, ");
         }

         stringTable.append("\"" + record.name + "\"");
         enumValueTable.append(record.name);

         // If there are more items, then add a comma
         if (!records.isEmpty()) {
            stringTable.append(", ");
            enumValueTable.append(", ");
         }

         lastEnumVal = record.number;
      }

      stringTable.append("};\n");
      enumValueTable.append("};\n");

      string.append(stringTable.toString());
      string.append(enumValueTable.toString());
   }

   private class EnumRecord {
      private int number;
      private String name;

      /**
       * @param number The ordinal value of the enumeration
       * @param name The declarartion name of the enumeration
       */
      public EnumRecord(int number, String name) {
         this.number = number;
         this.name = name;
      }

      /**
       * @return Returns the name.
       */
      public String getName() {
         return name;
      }

      /**
       * @return Returns the number.
       */
      public int getNumber() {
         return number;
      }
   }
}
