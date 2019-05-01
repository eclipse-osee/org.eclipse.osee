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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Ryan D. Brooks
 */
public class ExtractText {
   protected String line;
   protected int lineNum;
   protected BufferedReader in;
   protected FileWriter out;

   public ExtractText(String sourceFile) {
      try {
         in = new BufferedReader(new FileReader(sourceFile));
         out = new FileWriter("sourceText.txt");
         line = null;
         lineNum = 0;

         while ((line = in.readLine()) != null) {
            int pos = line.indexOf("/*");
            if (pos != -1) { // beginning of block comment unless in a string literal
               if (!insideStringLiteral(line, pos)) {
                  //find whole block and write to file
                  pos = line.indexOf("*/");
                  while (line != null && pos == -1) {
                     writeText();
                     line = in.readLine();
                     if (line != null) {
                        pos = line.indexOf("*/");
                     }
                  }
                  writeText();
               } else { // line contains a string
                  writeText();
               }
            } else if (line.indexOf("//") != -1) { // line contains a single line comment or string literal
               writeText();
            } else if ((pos = line.indexOf("\"")) != -1 && line.charAt(pos + 1) != '\'') { // line contains a string literal
               writeText();
            }
            // line does not contain text
            lineNum++;
         }
         in.close();
         out.close();
      } catch (IOException ex) {
         System.err.println(ex);
         return;
      }
   }

   protected void writeText() throws IOException {
      String str = String.valueOf(++lineNum);
      out.write(str, 0, str.length());
      out.write(line, 0, line.length());
      out.write('\n');
   }

   public static boolean insideStringLiteral(String str, int pos) {
      int index = 0;
      boolean inside = false;

      char[] chars = new char[str.length()];
      str.getChars(0, chars.length, chars, 0);

      while (index < pos) {
         if (chars[index] == '\\' && chars[index + 1] == '\"') {
            index++; //	skip over literal quotation marks
         } else if (chars[index] == '\"') {
            inside = !inside;
         }
         index++;
      }
      return inside;
   }

   public static void main(String[] args) {
      if (args.length < 1) {
         System.out.println("Usage:	ExtractText [source	file]");
         return;
      }
      new ExtractText(args[0]);
   }
}
