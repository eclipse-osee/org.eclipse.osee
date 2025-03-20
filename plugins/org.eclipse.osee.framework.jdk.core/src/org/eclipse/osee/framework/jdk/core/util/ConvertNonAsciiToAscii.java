/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ConvertNonAsciiToAscii {

   private static final Map<Character, String> NONASCII_TO_ASCII = new HashMap<>();

   static {
      NONASCII_TO_ASCII.put('’', "'"); // Right single quote
      NONASCII_TO_ASCII.put('‘', "'"); // Left single quote
      NONASCII_TO_ASCII.put('”', "\\\""); // Right double quote
      NONASCII_TO_ASCII.put('“', "\\\""); // Left double quote
      NONASCII_TO_ASCII.put('…', "..."); // Ellipsis
      NONASCII_TO_ASCII.put('–', "-"); // En dash
      NONASCII_TO_ASCII.put('—', "-"); // Em dash
      NONASCII_TO_ASCII.put('•', "*"); // Single bullet dot
      NONASCII_TO_ASCII.put('„', "\""); // Upside-down double quote
      NONASCII_TO_ASCII.put('¿', "?"); // Upside-down question mark
      NONASCII_TO_ASCII.put('≥', ">="); // Greater than or equal to
      NONASCII_TO_ASCII.put('‹', "<"); // Less than
      NONASCII_TO_ASCII.put('≤', "<="); // Less than or equal to
      NONASCII_TO_ASCII.put('°', "deg"); // Degree symbol
      NONASCII_TO_ASCII.put('¼', "1/4");
      NONASCII_TO_ASCII.put('☹', " "); // Sad face to space
      NONASCII_TO_ASCII.put('»', ">>");
      NONASCII_TO_ASCII.put(' ', " "); // NO-BREAK to space
      NONASCII_TO_ASCII.put('', " "); // \uF0A7 to space
      NONASCII_TO_ASCII.put('ø', "o"); // Scandinavian 'ø'
      NONASCII_TO_ASCII.put('ß', "ss"); // German sharp S
      NONASCII_TO_ASCII.put('æ', "ae"); // Ligature AE
      NONASCII_TO_ASCII.put('œ', "oe"); // Ligature OE
      NONASCII_TO_ASCII.put('≠', "<>"); // Not Equals
      NONASCII_TO_ASCII.put('é', "e");
      NONASCII_TO_ASCII.put('‐', "-"); // Dash
      NONASCII_TO_ASCII.put('º', "deg"); // Degrees
      // json error on search and replace, handle manually
      //      NONASCII_TO_ASCII.put('¬', "  "); // \u00AC to space
      //      NONASCII_TO_ASCII.put('√', "check"); // Check
   }

   public static String toAscii(String input) {
      if (input == null) {
         return null;
      }
      StringBuilder result = new StringBuilder();
      for (char c : input.toCharArray()) {
         result.append(NONASCII_TO_ASCII.getOrDefault(c, String.valueOf(c)));
      }
      return result.toString();
   }

   public static XResultData reportNonAsciiCharacters(String input) {
      XResultData rd = new XResultData();
      for (int i = 0; i < input.length(); i++) {
         char character = input.charAt(i);
         // Non-ASCII range is 0-127)
         if (character > 127) {
            rd.errorf("Non-ASCII : '" + character + "' -> \\u" + String.format("%04X",
               (int) character) + " at " + (i + 1) + "\n");
         }
      }
      return rd;
   }

   /**
    * Run as Java Application to see examples
    */
   public static void main(String[] args) {
      String nonAsciiText = "é ø ! ‘single-quotes’ “quotes” —dash æ ø œ ß";
      System.out.println("Original: " + nonAsciiText);

      XResultData report = reportNonAsciiCharacters(nonAsciiText);
      System.err.println(report.toString());

      String asciiText = toAscii(nonAsciiText);
      System.out.println("Converted: " + asciiText);

      report = reportNonAsciiCharacters(asciiText);
      if (report.isErrors()) {
         System.err.println(report.toString());
      } else {
         System.out.println("\nNO NON-ASCII CHARS!!!");
      }

      //      try {
      //         for (String filename : Arrays.asList("TRAX_ISSUES_202503101044", "CPCR_MAIN_202503101049",
      //            "TRAX_FLIGHT_TEST_ISSUES_202503101048")) {
      //
      //            File file = new File("C:\\Tools\\TRAX\\" + filename + ".json");
      //            System.err.println("\n\nProcessing file " + file.getAbsolutePath());
      //            String input = Lib.fileToString(file);
      //            int inputChar = input.length();
      //            int inputBype = input.getBytes(StandardCharsets.UTF_8).length;
      //
      //            System.err.println(Strings.truncate("INPUT: " + input, 80, true));
      //            System.err.println(String.format("\nINPUT char: %s; bytes: %s", inputChar, inputBype));
      //
      //            String output = toAscii(input);
      //
      //            int outputChar = output.length();
      //            int outputBype = output.getBytes(StandardCharsets.UTF_8).length;
      //
      //            System.err.println(Strings.truncate("OUTPUT: " + output, 80, true));
      //            System.err.println(String.format("\nOUTPUT char: %s; bytes: %s\n", outputChar, outputBype));
      //            System.err.println(outputChar == outputBype ? "EQUALS!!!" : "STILL NOT EQUALS");
      //
      //            XResultData report = reportNonAsciiCharacters(output);
      //            if (report.isErrors()) {
      //               System.err.println(report.toString());
      //            } else {
      //               System.out.println("NO NON-ASCII CHARS!!!");
      //            }
      //
      //            File outFile = new File("C:\\Tools\\TRAX\\" + filename + "_cln.json");
      //            Lib.writeStringToFile(output, outFile);
      //         }
      //
      //      } catch (Exception ex) {
      //         System.err.println(Lib.exceptionToString(ex));
      //      }
   }
}
