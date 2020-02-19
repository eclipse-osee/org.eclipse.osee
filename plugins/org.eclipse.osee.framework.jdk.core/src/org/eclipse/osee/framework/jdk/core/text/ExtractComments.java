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
package org.eclipse.osee.framework.jdk.core.text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class ExtractComments {
   protected BufferedReader in;
   protected String line;
   protected ArrayList<String> comments;
   protected int count;

   public ExtractComments() {
      in = null;
      line = null;
      comments = new ArrayList<>();
   }

   public Object[] extract(String sourceFile) {
      try {
         in = new BufferedReader(new FileReader(sourceFile));
      } catch (FileNotFoundException ex) {
         XConsoleLogger.err(Lib.exceptionToString(ex));
         throw new IllegalArgumentException(ex.toString());
      }

      try {
         while ((line = in.readLine()) != null) {
            handleComments();
         }
      } catch (IOException ex) {
         XConsoleLogger.err(Lib.exceptionToString(ex));
         System.exit(2);
      }
      return comments.toArray();
   }

   protected void handleComments() {
      try {
         while (line != null) {
            String tLine = line.trim();
            if (tLine.startsWith("/*")) {
               comments.add(++count + line);
               while (tLine != null && !tLine.endsWith("*/")) {
                  tLine = in.readLine();
                  if (tLine != null) {
                     tLine = tLine.trim();
                  }
                  comments.add(++count + line);
               }
            } else if (!tLine.startsWith("//")) {
               //a logical	LOC might have	a  comment following it on the  same line
               line = stripOffComment(tLine);
               return;
            }
            comments.add(++count + line);
            line = in.readLine();
         }
      } catch (IOException ex) {
         XConsoleLogger.err(Lib.exceptionToString(ex));
         return;
      }
   }

   public String stripOffComment(String line) {
      int pos = line.lastIndexOf("//");
      if (pos != -1) { //if  a comment is indeed there (maybe)
         //the	single line	comment symbol	might	be	part of a string literal
         //this is hard	because the	string delimiter might also be part	of	a string literal
         if (!insideStringLiteral(line, pos)) {
            comments.add(++count + this.line);
            return line.substring(0, pos).trim();
         }
      }
      count++;
      return line;
   }

   public boolean insideStringLiteral(String str, int pos) {
      int index = 0;
      boolean inside = false;

      char[] chars = new char[str.length()];
      str.getChars(0, chars.length, chars, 0);

      while (index < pos) {
         if (chars[index] == '\\' && chars[index + 1] == '\"') {
            index++; //	skip  over literal	quotation marks
         } else if (chars[index] == '\"') {
            inside = !inside;
         }
         index++;
      }
      return inside;
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         XConsoleLogger.out("Usage:	ExtractComments [source	file]");
         return;
      }
      ExtractComments app = new ExtractComments();
      Object[] text = app.extract(args[0]);

      try {
         FileWriter out = new FileWriter("comments.txt");
         for (int i = 0; i < text.length; i++) {
            String str = (String) text[i];
            out.write(str, 0, str.length());
            out.write('\n');
         }
         out.close();
      } catch (IOException ex) {
         XConsoleLogger.err(Lib.exceptionToString(ex));
         return;
      }
   }
}
