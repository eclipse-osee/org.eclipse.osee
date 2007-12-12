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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.io.IEnumValueBuilder.EnumRecord;

/**
 * Allows java files to be built dynamically and written out to disk.
 * 
 * @author Robert A. Fisher
 */
public class JavaFileBuilder {
   protected String packageName;
   protected String className;
   protected String classJavaDoc;
   protected String extendsClass;
   private ArrayList<String> interfaces;
   private List<Field> fields;
   private List<String> imports;
   private List<String> methods;
   private List<EnumRecord> enumFields;

   /**
    * Create a JavaFileBuilder for a class.
    * 
    * @param className The class of the file.
    */
   public JavaFileBuilder(String className) {
      this(null, className, null, null);
   }

   /**
    * Constructor
    */
   public JavaFileBuilder(String packageName, String className, String classJavaDoc, String extendsClass) {
      this.packageName = packageName;
      this.className = className;
      this.classJavaDoc = classJavaDoc;
      this.extendsClass = extendsClass;
      interfaces = new ArrayList<String>();
      fields = new LinkedList<Field>();
      imports = new LinkedList<String>();
      methods = new LinkedList<String>();
      enumFields = new LinkedList<EnumRecord>();
   }

   public void addInterface(String interfaceName) {
      interfaces.add(interfaceName);
   }

   public void addField(Field field) {
      fields.add(field);
   }

   public void addEnumField(EnumRecord enumField) {
      enumFields.add(enumField);
   }

   public void addImport(String importName) {
      imports.add(importName);
   }

   public void addMethod(String method) {
      methods.add(method);
   }

   /**
    * @param classJavaDoc The classJavaDoc to set.
    */
   public void setClassJavaDoc(String classJavaDoc) {
      this.classJavaDoc = classJavaDoc;
   }

   /**
    * @param className The className to set.
    */
   public void setClassName(String className) {
      this.className = className;
   }

   /**
    * @param extendsClass The extendsClass to set.
    */
   public void setExtendsClass(String extendsClass) {
      this.extendsClass = extendsClass;
   }

   /**
    * @param packageName The packageName to set.
    */
   public void setPackageName(String packageName) {
      this.packageName = packageName;
   }

   /**
    * Write the file out to disk. The file will be written to the specified directory. By virtue of Java, the filename
    * will be the name of the class with .java as the extension.
    * 
    * @throws FileNotFoundException
    */
   public void write(File directory) throws FileNotFoundException {
      if (!directory.isDirectory()) throw new IllegalArgumentException("Supplied file is not a directory");

      PrintWriter out = new PrintWriter(new File(directory, className + ".java"));

      out.write(this.toString());

      out.close();
   }

   public static class Field {
      private boolean isPublic;
      private boolean isFinal;
      private boolean isStatic;
      private String type;
      private String name;
      private String initialValue;

      public Field(String type, String name, boolean isFinal) {
         this(type, name, false, false, isFinal);
      }

      public Field(String type, String name, boolean isPublic, boolean isStatic, boolean isFinal) {
         this(type, name, isPublic, isStatic, isFinal, null);
      }

      /**
       * @param type The type of the field
       * @param name The name of the field
       * @param isPublic Whether the field should be declared as public
       * @param isStatic Whether the field should be declared as static
       * @param isFinal Whether the field should be declared as final
       * @param initialValue An initial value to set the field to.
       */
      public Field(String type, String name, boolean isPublic, boolean isStatic, boolean isFinal, String initialValue) {
         this.type = type;
         this.name = name;
         this.isPublic = isPublic;
         this.isStatic = isStatic;
         this.isFinal = isFinal;
         this.initialValue = initialValue;
      }

      @Override
      public String toString() {
         String declaration =
               "\t" + ((isPublic) ? "public " : "private ") + ((isStatic) ? "static " : "") + ((isFinal) ? "final " : "") + type + " " + name;

         if (initialValue != null) declaration += " = " + initialValue;
         return declaration + ";";
      }

      /**
       * @return Returns the isPublic.
       */
      public boolean isPublic() {
         return isPublic;
      }

      /**
       * @return Returns the isStatic.
       */
      public boolean isStatic() {
         return isStatic;
      }

      /**
       * @return Returns the isFinal.
       */
      public boolean isFinal() {
         return isFinal;
      }

      /**
       * @return Returns the name.
       */
      public String getName() {
         return name;
      }

      /**
       * @return Returns the type.
       */
      public String getType() {
         return type;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder string = new StringBuilder();

      SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

      // Add the head comment for the file
      string.append("/*\n * Created on " + dateFormat.format(new Date()) + "\n *\n * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE\n */\n\n");
      string.append("package " + packageName + ";");
      string.append("\n\n");

      // Add all of the imports
      for (String importName : imports) {
         string.append("import " + importName + ";\n");
      }

      string.append("\n");

      // Add the class comment if it is available
      if (classJavaDoc != null) {
         string.append(stringToJavadoc(classJavaDoc));
      }

      // add the start of the class
      // Check to see if this is an IEnumValue file
      if (interfaces.size() != 0 && interfaces.get(0).equals("IEnumValue") && extendsClass == null) {
         string.append("public enum " + className + " implements IEnumValue");
         string.append(" {\n");
         getEnumFields(string);
      } else {
         string.append("public class " + className + ((extendsClass == null) ? "" : " extends " + extendsClass.trim()));
         for (int i = 0; i < interfaces.size(); i++) {
            if (i == 0)
               string.append(" implements " + interfaces.get(i));
            else
               string.append(", " + interfaces.get(i));
         }
         string.append(" {\n");
         getFields(string);
      }

      getMethods(string);

      // add the end of the file
      string.append("}");

      return string.toString();
   }

   protected void getFields(StringBuilder string) {
      for (Field field : fields)
         string.append(field.toString() + "\n");
   }

   protected void getEnumFields(StringBuilder string) {
      Iterator<EnumRecord> enumFieldsIter = enumFields.iterator();
      while (enumFieldsIter.hasNext()) {
         EnumRecord eField = enumFieldsIter.next();
         string.append("\t" + eField);
         if (!enumFieldsIter.hasNext())
            string.append(";\n\n");
         else
            string.append(",\n");
      }

   }

   protected void getMethods(StringBuilder string) {
      for (String method : methods)
         string.append(method + "\n");
   }

   private String stringToJavadoc(String string) {
      return "/**\n" + string.replaceAll("^", " * ").replaceAll("\n", "\n * ") + "\n */\n";
   }

}
