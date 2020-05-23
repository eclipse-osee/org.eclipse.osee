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

package org.eclipse.osee.framework.jdk.core.util.io;

/**
 * @author Robert A. Fisher
 */
public class Field {
   private final boolean isPublic;
   private final boolean isFinal;
   private final boolean isStatic;
   private final String type;
   private final String name;
   private final String initialValue;

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
         "\t" + (isPublic ? "public " : "private ") + (isStatic ? "static " : "") + (isFinal ? "final " : "") + type + " " + name;

      if (initialValue != null) {
         declaration += " = " + initialValue;
      }
      return declaration + ";";
   }

   public boolean isPublic() {
      return isPublic;
   }

   public boolean isStatic() {
      return isStatic;
   }

   public boolean isFinal() {
      return isFinal;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }
}
