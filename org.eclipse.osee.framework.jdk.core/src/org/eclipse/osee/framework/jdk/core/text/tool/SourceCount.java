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
package org.eclipse.osee.framework.jdk.core.text.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Ryan D. Brooks
 */
public class SourceCount {
   protected BufferedReader in;
   protected String line;

   public SourceCount() {
      in = null;
      line = null;
   }

   public int countLOC(String sourceFile) {
      try {
         in = new BufferedReader(new FileReader(sourceFile));
      } catch (FileNotFoundException ex) {
         System.err.println(ex);
         System.exit(1);
      }

      int count = 0;
      try {
         while ((line = in.readLine()) != null) {
            ignoreComments();
            if (line != null) { //a comment might be the last line of the file
               if (line.endsWith("{") || line.endsWith(";")) { // if logical LOC
                  count++;
               }
            }
         }
      } catch (IOException ex) {
         System.err.println(ex);
         System.exit(2);
      }
      return count;
   }

   protected void ignoreComments() {
      try {
         while (line != null) {
            line = line.trim();
            if (line.startsWith("/*")) {
               while (line != null && !line.endsWith("*/")) {
                  line = in.readLine().trim();
               }
            } else if (!line.startsWith("//")) {
               //a logical LOC might have a comment folloing it on the same line					
               line = stripOffComment(line);
               return;
            }
            line = in.readLine();
         }
      } catch (IOException ex) {
         System.err.println(ex);
         System.exit(2);
      }
   }

   public static String stripOffComment(String line) {
      int pos = line.lastIndexOf("//");
      if (pos != -1) { //if a comment is indeed there (maybe)
         //the single line comment symbol might be part of a string literal							
         //this is hard because the string delimiter might also be part of a string literal							
         if (!insideStringLiteral(line, pos)) {
            return line.substring(0, pos).trim();
         }
      }
      return line;
   }

   public static boolean insideStringLiteral(String str, int pos) {
      int index = 0;
      boolean inside = false;

      char[] chars = new char[str.length()];
      str.getChars(0, chars.length, chars, 0);

      while (index < pos) {
         if (chars[index] == '\\' && chars[index + 1] == '\"') {
            index++; // skip over literal quotation marks
         } else if (chars[index] == '\"') {
            inside = !inside;
         }
         index++;
      }
      return inside;
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.out.println("Usage: SourceCount [source file]");
         return;
      }
      SourceCount app = new SourceCount();
      int loc = app.countLOC(args[0]);
      System.out.println("Logical LOC: " + loc);
   }
}
