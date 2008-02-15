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
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author David Diepenbrock
 */
public class Xml {
   private static final String[] XML_CHARS = new String[] {"[&]", "[<]", "[>]", "[\"]"};
   private static final String[] XML_ESCAPES = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"};
   private static final String LINEFEED = "&#10;";
   private static final String CARRIAGE_RETURN = "&#13;";
   private static final Pattern squareBracket = Pattern.compile("\\]");

   /**
    * TODO Optimize algorithm
    * 
    * @param text
    * @return Returns a string with entity reference characters unescaped.
    */
   public static String unescape(String text) {
      StringBuffer stringBuffer = new StringBuffer();
      int startIndex, endIndex;
      char chr;

      for (int index = 0; index < text.length(); index++) {
         chr = text.charAt(index);

         if (chr == '&') {
            startIndex = index;
            endIndex = text.indexOf(';', startIndex) + 1;

            String entityReference = text.substring(startIndex, endIndex);

            if (entityReference.equals("&amp;"))
               stringBuffer.append('&');
            else if (entityReference.equals("&lt;"))
               stringBuffer.append('<');
            else if (entityReference.equals("&gt;"))
               stringBuffer.append('>');
            else if (entityReference.equals("&nbsp;"))
               stringBuffer.append(' ');
            else if (entityReference.equals("&quot;"))
               stringBuffer.append('"');
            else
               throw new IllegalArgumentException("unknown entity reference: " + text.substring(startIndex, endIndex));

            index = endIndex - 1;
         } else {
            stringBuffer.append(chr);
         }
      }
      return stringBuffer.toString();
   }

   /**
    * TODO Optimize algorithm
    * 
    * @param text
    * @return Returns a string with entity reference characters escaped.
    */
   public static CharSequence escape(CharSequence text) {
      String textString = text.toString();
      for (int x = 0; x < XML_CHARS.length; x++) {
         textString = textString.replaceAll(XML_CHARS[x], XML_ESCAPES[x]);
      }

      return textString;
   }

   public static void writeAsCdata(Appendable appendable, String string) throws IOException {
      if (string.indexOf('<') == -1 && string.indexOf('&') == -1 && string.indexOf("]]>") == -1) {
         writeData(appendable, string);
      } else {
         if (string.indexOf(']') == -1) {
            writeCdata(appendable, string);
         } else {
            //  work around bug in excel xml parsing that thinks a single ] closes CDATA
            String[] tokens = squareBracket.split(string);
            for (int i = 0; i < tokens.length; i++) {
               writeCdata(appendable, tokens[i]);
               if (i != tokens.length - 1) { // the last token would not have been followed by ]
                  appendable.append(']');
               }
            }
         }
      }
   }

   private static void writeCdata(Appendable appendable, String content) throws IOException {
      appendable.append("<![CDATA[");
      appendable.append(content);
      appendable.append("]]>");
   }

   private static void writeData(Appendable appendable, String string) throws IOException {
      for (int index = 0; index < string.length(); index++) {
         char value = string.charAt(index);
         if (value == '\r') {
            appendable.append(CARRIAGE_RETURN);
         } else if (value == '\n') {
            appendable.append(LINEFEED);
         } else {
            appendable.append(value);
         }
      }
   }
}
