/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.QuirksMode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

/**
 * This class will convert an HTML string to a normalized format. This allows the same output from HTML regardless of
 * the input editor. The least versatile editor will be used. Currently that is TinyMCE. The reason to do this is that
 * options can be removed, but not added to the HTML
 * 
 * <pre>
 * Assumptions:
 *    The input is valid HTML
 * Items that change
 *    HTML tags may be upper case, tinyMCE are lower
 *    <FONT> is converted to <span>
 *    tinyMCE does NOT use font point only small, medium, ...
 *          6-8 point == xx-small
 *          9-11 point == small
 *          12-13 point == medium
 *          14-16 point == large
 *          18-20 point == x-large
 *          22-28 point == xx-large
 *          >28 point == 300%
 *    replace &ldquot; &rdquot; &lsquot; &rsquot; to &quot; and '
 *    replace <b> with <strong>
 *    replace <i> with
 * <em>
 *    replace <u> with <span style="text-decoration: underline;">
 *    remove bordercolor from table tag
 * <pre/>
 *
 * @author Marc A. Potter
 */
public final class NormalizeHtml {

   private static final TreeMap<Integer, String> FONT_MAP = initializeFontMap();
   private static final String[] FONT_VALUES = FONT_MAP.values().toArray(new String[0]);
   private static final String MEDIUM_FONT = "medium;";
   private static final String CHARSET = "UTF-8";
   private static final int INDENT_AMOUNT = 4;
   private static final String ldquo = String.valueOf('\u201C');
   private static final String rdquo = String.valueOf('\u201D');
   private static final String lsquo = String.valueOf('\u2018');
   private static final String rsquo = String.valueOf('\u2019');

   private NormalizeHtml() {
      // Utility Class
   }

   private static TreeMap<Integer, String> initializeFontMap() {
      TreeMap<Integer, String> map = new TreeMap<Integer, String>();
      map.put(new Integer(8), "xx-small;");
      map.put(new Integer(11), "small;");
      map.put(new Integer(13), "medium;");
      map.put(new Integer(16), "large;");
      map.put(new Integer(20), "x-large;");
      map.put(new Integer(28), "xx-large;");
      map.put(new Integer(Integer.MAX_VALUE), "300%;");
      return map;
   }

   public static String convertToNormalizedHTML(String inputHTML) {
      Document doc = Jsoup.parse(inputHTML);
      doc.quirksMode(QuirksMode.noQuirks);
      OutputSettings outputSettings = doc.outputSettings();
      outputSettings.charset(CHARSET);
      outputSettings.escapeMode(EscapeMode.xhtml);
      outputSettings.prettyPrint(true);
      outputSettings.outline(true);
      outputSettings.indentAmount(INDENT_AMOUNT);
      doc.outputSettings(outputSettings);
      Elements bold = doc.select("b");
      for (Element e : bold) {
         e.tagName("strong");
      }
      Elements italic = doc.select("i");
      for (Element e : italic) {
         e.tagName("em");
      }
      Elements underline = doc.select("u");
      for (Element e : underline) {
         e.tagName("span");
         e.attr("style", "text-decoration: underline;");
      }
      Elements strike = doc.select("strike");
      for (Element e : strike) {
         e.tagName("span");
         e.attr("style", "text-decoration: line-through;");
      }
      processFontTags(doc);
      return processText(doc);
   }

   private static String processText(Document doc) {
      /**
       * Nothing is ever as easy as it should be, since text nodes are not elements the select does not work. Therefore,
       * process the output HTML
       */
      String theText = doc.outerHtml();

      /**
       * convert &ldquo; and &rdquo; to &quot; convert &lsquo; and &rsquo; to ' The parser itself changes the symbols to
       * the appropriate HTML variable at read/write time. Therefore, just change the actual symbols. Use the unicode
       * definitions of the special characters since they are multibyte Jsoup sets ' to &apos on input, reset this to '
       */
      theText = theText.replaceAll(ldquo, "\"");
      theText = theText.replaceAll(rdquo, "\"");
      theText = theText.replaceAll(lsquo, "'");
      theText = theText.replaceAll(rsquo, "'");
      theText = theText.replaceAll("&apos;", "'");
      return theText;
   }

   private static void processFontTags(Document doc) {
      Elements font = doc.select("font");
      for (Element e : font) {
         Attributes attrs = e.attributes().clone();
         StringBuilder styleString = new StringBuilder();
         String theSizeString = "";
         for (Attribute attribute : attrs) {
            String attributeName = attribute.getKey();
            String attributeValue = attribute.getValue();
            if (attributeName.equalsIgnoreCase("face")) {
               styleString.append(" font-family: ");
               styleString.append(attributeValue);
            } else if (attributeName.equalsIgnoreCase("size")) {
               int theSize = Integer.valueOf(attributeValue.trim());
               if (theSize <= FONT_VALUES.length) {
                  theSizeString = FONT_VALUES[theSize - 1];
               }
            } else if (attributeName.equalsIgnoreCase("color")) {
               styleString.append(" color: ");
               styleString.append(attributeValue);
            } else if (attributeName.equalsIgnoreCase("style")) {
               // possible that font size specified here (font-size: xxpt)
               int size = attributeValue.indexOf("font-size:");
               if (size != -1) {
                  size += "font-size:".length();
                  theSizeString = getFontSize(attributeValue.substring(size));
               }
            }
            e.removeAttr(attributeName);
         }
         if (theSizeString.length() > 0) {
            styleString.append(" font-size: ");
            styleString.append(theSizeString);
         }
         e.tagName("span");
         e.attr("style", styleString.toString());
      }
   }

   /**
    * Expected format of the input is font-size: NNpt Note that there may be other information after the pt font-size:
    * NNpt font-family: .... If the string is not formatted correctly, return a medium font as default
    */
   private static String getFontSize(String inputStyle) {
      int theSize = 1;
      String theReturn;
      int thePointStart = inputStyle.indexOf(' '), thePointEnd = inputStyle.lastIndexOf("pt");
      if (thePointStart == -1) {
         thePointStart = 0;
      }
      while ((inputStyle.charAt(thePointStart) == ' ') && (thePointStart < inputStyle.length())) {
         thePointStart++;
      }
      if (thePointStart >= thePointEnd) {
         theReturn = MEDIUM_FONT; // average middle font
      } else {
         theSize = Integer.valueOf(inputStyle.substring(thePointStart, thePointEnd));
         theReturn = FONT_MAP.ceilingEntry(theSize).getValue();
      }
      return theReturn;
   }

}
