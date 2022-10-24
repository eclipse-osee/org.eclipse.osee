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
    * The initial number of indented strings to create during static initialization.
    */

   private static int initialIndentStringCount;

   /**
    * The number of spaces for each indent level.
    */

   private static int indentAmount;

   static {
      IndentedString.initialIndentStringCount = 32;
      IndentedString.indentAmount = 3;
      IndentedString.indentedString = new IndentedString();
      IndentedString.indentedString.createInitialIndentStrings();
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

   private int largestIndent;

   /**
    * Create and initialize the single {@link IndentedString} object.
    */

   private IndentedString() {
      this.indentStrings = new ArrayList<String>(IndentedString.initialIndentStringCount);
      this.largestIndent = -1;
      this.indentBuffer =
         new StringBuilder(IndentedString.indentAmount * IndentedString.initialIndentStringCount + 1 * 1024);
   }

   /**
    * Create the initial indent strings.
    */

   private void createInitialIndentStrings() {
      for (int i = 0; i < IndentedString.initialIndentStringCount; i++) {
         this.createIndentString(i);
      }
   }

   /**
    * Creates or gets the indent string for the specified indent level.
    *
    * @param indent the indent level to create a string of spaces for.
    * @return the new indent string.
    */

   private String createIndentString(int indent) {
      if (indent > this.largestIndent) {

         synchronized (IndentedString.indentedString) {
            int start = largestIndent + 1;
            int end = indent;
            int newIndent;

            int spaceCount = IndentedString.indentAmount * (end - start + (this.largestIndent >= 0 ? 1 : 0));

            for (int i = 0; i < spaceCount; i++) {
               this.indentBuffer.append(" ");
            }

            for (newIndent = start; newIndent <= end; newIndent++) {
               this.indentStrings.add(newIndent, this.indentBuffer.toString());
            }

            this.largestIndent = indent;
         }
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
