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
package org.eclipse.osee.define.rest.internal.importing;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.QuirksMode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

/**
 * This class will convert an HTML string to a normalized format. This allows the same output from HTML regardless of
 * the input editor. The least versatile editor will be used. Currently that is TinyMCE. The reason to do this is that
 * options can be removed, but not added to the HTML
 *
 * <pre>
 * Assumptions: The input is valid HTML Items that change HTML tags may be upper case, tinyMCE are lower <FONT> is
 * converted to <span> tinyMCE does NOT use font point only small, medium, ... 6-8 point == xx-small 9-11 point == small
 * 12-13 point == medium 14-16 point == large 18-20 point == x-large 22-28 point == xx-large >28 point == 300% replace
 * &ldquot; &rdquot; &lsquot; &rsquot; to &quot; and ' replace <b> with <strong> replace <i> with <em> replace <u> with
 * <span style="text-decoration: underline;"> remove bordercolor from table tag
 *
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
   private static final String figureDash = String.valueOf('\u2012');
   private static final String enDash = String.valueOf('\u2013');
   private static final String emDash = String.valueOf('\u2014');
   private static final String NON_BREAK_SPACE = String.valueOf('\u00A0');
   private static final String NON_BREAK_FIGURE_SPACE = String.valueOf('\u2007');
   private static final String NON_BREAK_NARROW_SPACE = String.valueOf('\u202F');
   private static final String NON_BREAK_WORD_JOINER = String.valueOf('\u2060');
   private static final String NON_BREAK_ZERO_WIDTH = String.valueOf('\uFEFF');
   private static ArrayList<String> allowedAttributes = null;
   private static final String HTMLHEAD =
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><body>";
   private static final String HTMLTAIL = "</body></html>";

   private NormalizeHtml() {
      // Utility Class
   }

   static {
      allowedAttributes = new ArrayList<>();
      allowedAttributes.add("border");
      allowedAttributes.add("frame");
      allowedAttributes.add("rules");
      allowedAttributes.add("valign");
      allowedAttributes.add("src");
   }

   private static TreeMap<Integer, String> initializeFontMap() {
      TreeMap<Integer, String> map = new TreeMap<>();
      map.put(new Integer(8), "xx-small;");
      map.put(new Integer(11), "small;");
      map.put(new Integer(13), "medium;");
      map.put(new Integer(16), "large;");
      map.put(new Integer(20), "x-large;");
      map.put(new Integer(28), "xx-large;");
      map.put(new Integer(Integer.MAX_VALUE), "300%;");
      return map;
   }

   /**
    * Takes a string containing only the HTML body (without the body tags) and wraps it with correct HTML tags and
    * normalizes it
    *
    * @parm inputHTML bosy of HTML without <body> </body> tags
    * @return wraped and normalized HTML
    */
   public static String wrapAndNormalizeHTML(String htmlBody, boolean removeInitialStyle, boolean removeEmptyTags, boolean removeHeaderFooter) {
      htmlBody = HTMLHEAD + htmlBody + HTMLTAIL;
      return convertToNormalizedHTML(htmlBody, removeInitialStyle, removeEmptyTags, removeHeaderFooter);
   }

   /**
    * Takes HTML code and changes it to a normalized form. The normalized form is independent of the source editor. That
    * is, the format of the data would be the same regardless of source
    *
    * @param inputHTML HTML source to be normalized
    * @return Normalized HTML
    */
   public static String convertToNormalizedHTML(String inputHTML) {
      return convertToNormalizedHTML(inputHTML, false, false, false);
   }

   /**
    * Takes HTML code and changes it to a normalized form. The normalized form is independent of the source editor. That
    * is, the format of the data would be the same regardless of source. In addition, allows the caller to remove the
    * opening and closing format HTML code that is generated by Editors such as Open Office
    *
    * @param inputHTML HTML source to be normalized
    * @param removeInitialStyle Remove initial style information.
    * @param removeEmptyTags Remove any empty (containing no text) style sections
    * @return Normalized HTML
    */
   public static String convertToNormalizedHTML(String inputHTML, boolean removeInitialStyle, boolean removeEmptyTags, boolean removeHeaderFooter) {
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
      removeDepreactedTags(doc);
      processTagsWithAttributes(doc);
      processHeaderFooter(doc, removeHeaderFooter);
      processFontTags(doc);
      processInitialStyleTags(doc, removeInitialStyle);
      processEmptyTags(doc, removeEmptyTags);
      processSelfFormattingTags(doc);
      return processText(doc);
   }

   static void processSelfFormattingTags(Document doc) {
      /**********************************************************
       * Documents that are converted from MS Word have an extra \n in the list items remove. Also trim the item of
       * leading/trailing blanks
       */
      String[] tagsToCheck = {"li", "tr", "td", "table"};
      for (String select : tagsToCheck) {
         Elements theNode = doc.select(select);
         ArrayList<Node> remove = new ArrayList<>();
         for (Element item : theNode) {
            List<Node> kids = item.childNodes();
            for (Node n : kids) {
               if (n instanceof TextNode) {
                  TextNode t = (TextNode) n;
                  String theText = t.text();
                  theText = theText.replaceAll(NON_BREAK_SPACE, " ");
                  theText = theText.replaceAll(NON_BREAK_FIGURE_SPACE, " ");
                  theText = theText.replaceAll(NON_BREAK_NARROW_SPACE, " ");
                  theText = theText.replaceAll(NON_BREAK_WORD_JOINER, " ");
                  theText = theText.replaceAll(NON_BREAK_ZERO_WIDTH, " ");
                  theText = theText.trim().replaceAll("\\s+", "");
                  if (theText.isEmpty()) {
                     remove.add(t);
                  } else {
                     theText = t.text().trim();
                     String nbsp = "&nbsp;";
                     theText = theText.replaceAll(NON_BREAK_SPACE, nbsp);
                     theText = theText.replaceAll(NON_BREAK_FIGURE_SPACE, nbsp);
                     theText = theText.replaceAll(NON_BREAK_NARROW_SPACE, nbsp);
                     theText = theText.replaceAll(NON_BREAK_WORD_JOINER, nbsp);
                     theText = theText.replaceAll(NON_BREAK_ZERO_WIDTH, nbsp);
                     while (theText.indexOf(nbsp) == 0) {
                        theText = theText.substring(nbsp.length()).trim();
                     }
                     while (theText.lastIndexOf(nbsp) != -1 && theText.lastIndexOf(
                        nbsp) == theText.length() - nbsp.length()) {
                        theText = theText.substring(0, theText.length() - nbsp.length()).trim();
                     }
                     if (theText.isEmpty()) {
                        remove.add(t);
                     } else {
                        t.replaceWith(TextNode.createFromEncoded(theText, t.baseUri()));
                     }
                  }
               } else if (n instanceof Element) {
                  if (((Element) n).tagName().equals("br")) {
                     remove.add(n);
                  }
               }
            }
         }
         for (Node n : remove) {
            n.remove();
         }
      }
   }

   static void removeDepreactedTags(Document doc) {
      Elements center = doc.select("center");
      for (Element e : center) {
         Elements children = e.children();
         for (Element c : children) {
            e.before(c);
         }
         e.remove();
      }
   }

   private static synchronized void processTagsWithAttributes(Document doc) {
      /****************************************************************************
       * HTML allows the same table to be represented many ways. Normalize the information into a standard format. Note
       * this will simplify the table as well (that is some formatting may be lost) Remember, the goal is to reduce the
       * HTML to the point that it is the same regardless of the source editor Also images have similar issues --
       * normalize to the basic keyword
       */

      Elements tables = doc.select("table");
      for (Element table : tables) {
         removeUnsupportedAttributes(table, true);
         // remove Colgroup
         Elements colgroup = table.select("colgroup");
         for (Element c : colgroup) {
            c.remove();
         }
         // no support for header / footer -- just rows

         removeElements(table, "thead");
         removeElements(table, "tfoot");
         removeElements(table, "tbody");
         // remove unsupported attributes on tr and td tags and move the attributes from td to tr
         Elements rows = table.select("td");
         for (Element row : rows) {
            String[] attributeValues = removeUnsupportedAttributes(row, false);
            Element tr = null;
            Element parent = row.parent();
            if (parent.tagName().equals("tr")) {
               tr = parent;
            } else {
               Elements siblings = row.siblingElements();
               for (Element e : siblings) {
                  if (e.tagName().equals("tr")) {
                     tr = e;
                     break;
                  }
               }
            }
            if (tr != null) {
               for (int i = 0; i < attributeValues.length; i++) {
                  if (attributeValues[i] != null) {
                     tr.attr(allowedAttributes.get(i), attributeValues[i].toLowerCase());
                  }
               }
            }
         }
         rows = table.select("tr");
         for (Element row : rows) {
            removeUnsupportedAttributes(row, true);
         }
      }

      Elements images = doc.select("img");
      for (Element image : images) {
         removeUnsupportedAttributes(image, true);
      }
   }

   static void processInitialStyleTags(Document doc, boolean removeInitialStyle) {
      if (removeInitialStyle) {
         Elements pTags = doc.select("p");
         for (Element p : pTags) {
            if (!p.attr("style").equals("")) {
               if (p.hasText()) {
                  if (!p.parent().tagName().equals("li")) {
                     Element cr = new Element(Tag.valueOf("br"), p.baseUri());
                     p.after(cr);
                  }
                  p.unwrap();
               }
            }
         }
         Elements span = doc.select("span");
         for (Element s : span) {
            if (!s.attr("style").equals("") && !s.hasText()) {
               s.remove();
            }
         }
      }
   }

   private static void processEmptyTags(Document doc, boolean removeEmptyTags) {
      if (removeEmptyTags) {
         Elements pTags = doc.select("p");
         for (Element p : pTags) {
            deleteEmptyElemens(p);
         }
         Elements span = doc.select("span");
         for (Element s : span) {
            deleteEmptyElemens(s);
         }
         Elements div = doc.select("div");
         for (Element e : div) {
            if (!e.hasText() && !e.html().contains("<img")) {
               e.remove();
            } else {
               e.unwrap();
            }
         }
         Elements aTags = doc.select("a");
         for (Element a : aTags) {
            Attributes attr = a.attributes();
            if (attr.size() == 1 && !attr.get("name").equals("")) {
               a.unwrap();
            }
         }
      }
   }

   static void processHeaderFooter(Document doc, boolean removeHeaderFooter) {
      if (removeHeaderFooter) {
         Elements div = doc.select("div");
         for (Element d : div) {
            Elements headerFooter = d.getElementsByAttributeValueMatching("type", "HEADER*|FOOTER*");
            for (Element hf : headerFooter) {
               hf.remove();
            }
         }
      }
   }

   /**
    * @param elementToCheck - a jsoup ELEMENT to be checked. If empty, delete the element
    */
   private static void deleteEmptyElemens(Element elementToCheck) {
      Elements style = elementToCheck.getElementsByAttributeValueMatching("style", "font*|margin*");
      for (Element e : style) {
         if (e.hasText()) {
            continue;
         } else {
            e.unwrap();
         }
      }

   }

   private static String processText(Document doc) {
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

      /************************************************************************
       * Convert &ndash; and Unicode dashes to -. Not all editors handle this correctly
       */
      theText = theText.replaceAll("&ndash;", "-");
      theText = theText.replaceAll(figureDash, "-");
      theText = theText.replaceAll(enDash, "-");
      theText = theText.replaceAll(emDash, "-");

      //@formatter:off
      /*****************************************************************************
       * Convert the non-blocking characters to the HTML value (&nbsp;)
       * non-break space U+00A0
       * figure space U+2007
       * narrow no-break space U+202F
       * word joiner U+2060
       * zero width no-break space U+FEFF
       *
       */
      //@formatter:on
      theText = theText.replaceAll(NON_BREAK_SPACE, "&nbsp;");
      theText = theText.replaceAll(NON_BREAK_FIGURE_SPACE, "&nbsp;");
      theText = theText.replaceAll(NON_BREAK_NARROW_SPACE, "&nbsp;");
      theText = theText.replaceAll(NON_BREAK_WORD_JOINER, "&nbsp;");
      theText = theText.replaceAll(NON_BREAK_ZERO_WIDTH, "&nbsp;");

      /***********************************************************************************
       * Remove spaces after end of tags at end of lines
       */
      theText = theText.replaceAll("> {1,}\n", ">\n");
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
      while (inputStyle.charAt(thePointStart) == ' ' && thePointStart < inputStyle.length()) {
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

   static void removeElements(Element table, String theElementParent) {
      Elements parents = table.select(theElementParent);
      for (Element p : parents) {
         Elements children = p.children();
         for (Element c : children) {
            p.before(c);
         }
         p.remove();
      }

   }

   static String[] removeUnsupportedAttributes(Element e, boolean addBack) {
      String[] attributeValues = {null, null, null, null, null};
      // remove "unsupported" attributes
      Attributes attr = e.attributes();
      for (Attribute a : attr) {
         if (allowedAttributes.contains(a.getKey())) {
            if (!(a.getKey().equals("border") && a.getValue().equals("0"))) {
               attributeValues[allowedAttributes.indexOf(a.getKey())] = a.getValue();
            }
         }
         e.removeAttr(a.getKey());
      }
      if (addBack) {
         // set specific order for attributes
         for (int i = 0; i < attributeValues.length; i++) {
            if (attributeValues[i] != null) {
               e.attr(allowedAttributes.get(i), attributeValues[i]);
            }
         }
      }
      return attributeValues;
   }

}
