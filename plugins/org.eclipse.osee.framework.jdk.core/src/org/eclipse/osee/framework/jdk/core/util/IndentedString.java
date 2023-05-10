/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates strings of spaces for use as indents in log or debug messages.
 *
 * @author Loren K. Ashley
 */

public class IndentedString {

   /**
    * The single instance of the {@link IndentedString} class.
    */

   private static IndentedString indentedString;

   /**
    * A string with the {@link IndentedString#indentAmount} number of spaces.
    */

   private static String indentedUnitString;

   /**
    * The initial number of indented strings to create during static initialization.
    *
    * @implNote This value must be at least 1.
    */

   private static int initialIndentStringCount;

   /**
    * The number of spaces for each indent level.
    */

   private static int indentAmount;

   static {
      IndentedString.initialIndentStringCount = 32;
      IndentedString.indentAmount = 3;
      IndentedString.indentedUnitString = "   ";
      IndentedString.indentedString = new IndentedString();
      IndentedString.indentedString.createIndentString(IndentedString.initialIndentStringCount - 1);
   }

   /**
    * Buffer used to build the indent strings.
    */

   private final StringBuilder indentBuffer;

   /**
    * List of indent strings indexed by indent level.
    */

   private final List<String> indentStrings;

   /**
    * The indent level of the largest indent string produced.
    */

   private final AtomicInteger largestIndent;

   /**
    * Create and initialize the single {@link IndentedString} object.
    */

   private IndentedString() {
      this.indentStrings = new ArrayList<String>(IndentedString.initialIndentStringCount);
      this.indentStrings.add("");
      this.largestIndent = new AtomicInteger(0);
      var size = IndentedString.indentAmount * (IndentedString.initialIndentStringCount - 1);
      size = size > 1024 ? size : 1024;
      this.indentBuffer = new StringBuilder(size);
   }

   /**
    * Creates or gets the indent string for the specified indent level.
    *
    * @param indent the indent level to create a string of spaces for.
    * @return the new indent string.
    */

   private String createIndentString(int indent) {

      indent = indent >= 0 ? indent : 0;

      if (indent <= this.largestIndent.get()) {
         return this.indentStrings.get(indent);
      }

      synchronized (IndentedString.indentedString) {
         int start = largestIndent.get() + 1;
         int end = indent;

         for (int newIndent = start; newIndent <= end; newIndent++) {
            this.indentStrings.add(newIndent, this.indentBuffer.append(IndentedString.indentedUnitString).toString());
         }

         this.largestIndent.set(indent);
      }

      return this.indentStrings.get(indent);
   }

   /**
    * Gets a string of spaces for the specified indent level. The string will contain <code>indent</code> *
    * {@link IndentedString.indentAmount} number of spaces.
    *
    * @param indent the indent level to obtain an indent string for.
    * @return the indent string.
    */

   public static String indentString(int indent) {
      return IndentedString.indentedString.createIndentString(indent);
   }

   /**
    * Gets the number of spaces for each level of indent.
    *
    * @return the number of spaces in an indent level.
    */

   public static int indentSize() {
      return IndentedString.indentAmount;
   }
}

/* EOF */
