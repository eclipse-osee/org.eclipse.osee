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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Ryan D. Brooks
 */
public class WordMLProducer {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(WordMLProducer.class);
   public static final String RGB_RED = "FF0000";
   public static final String RGB_GREEN = "00FF00";
   public static final String RGB_BLUE = "0000FF";

   private static final String FILE_NAME = "fileName";
   private static final String LISTNUM_FIELD =
         "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:instrText> LISTNUM \"listreset\"  \\l 1 \\s 0 </w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r>";
   private static final String SUB_DOC =
         "<wx:sect><w:p><w:pPr><w:sectPr><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:pPr></w:p><w:subDoc w:link=\"" + FILE_NAME + "\"/></wx:sect><wx:sect><wx:sub-section><w:p><w:pPr><w:pStyle w:val=\"Heading1\"/></w:pPr></w:p><w:sectPr><w:type w:val=\"continuous\"/><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></wx:sub-section></wx:sect>";
   private static final String HYPER_LINK_DOC =
         "<w:p><w:hlink w:dest=\"fileName\"><w:r wsp:rsidRPr=\"00CE6681\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>fileName</w:t></w:r></w:hlink></w:p>";
   private Appendable strB;
   private final int[] outlineNumber;
   private int outlineLevel;
   private int flattenedLevelCount;
   private boolean previousPageLandsacpe;
   private Map<String, Integer> alphabetMap;

   public WordMLProducer(Appendable strB) {
      this.strB = strB;
      this.outlineNumber = new int[10]; // word supports 9 levels of outlining; index this array from 1 to 9
      this.outlineLevel = 0;
      this.flattenedLevelCount = 0;

      this.alphabetMap = new HashMap<String, Integer>();

      alphabetMap.put("A.0", 1);
      alphabetMap.put("B.0", 2);
      alphabetMap.put("C.0", 3);
   }

   public CharSequence startOutlineSubSection(CharSequence font, CharSequence headingText, String outlineType) throws IOException {
      if (okToStartSubsection()) {
         outlineNumber[++outlineLevel]++;
         CharSequence paragraphNumber = getOutlineNumber();
         startOutlineSubSection((outlineType != null ? outlineType : "Heading") + outlineLevel, paragraphNumber, font,
               headingText);

         return paragraphNumber;
      } else {
         flattenedLevelCount++;
         endOutlineSubSection(true);
         logger.log(Level.WARNING, "Outline level flattened, outline can only go 9 levels deep");
         //         startParagraph();
         //         addTextInsideParagraph("OUTLINE LEVEL FLATTENED: " + headingText, RGB_RED);
         //         endParagraph();

         return startOutlineSubSection(font, headingText, outlineType);
      }
   };

   public void startOutlineSubSection(CharSequence style, CharSequence outlineNumber, CharSequence font, CharSequence headingText) throws IOException {
      strB.append("<wx:sub-section>");
      strB.append("<w:p><w:pPr><w:pStyle w:val=\"");
      strB.append(style);
      strB.append("\"/><w:listPr><wx:t wx:val=\"");
      strB.append(outlineNumber);
      strB.append("\" wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"/><wx:font wx:val=\"");
      strB.append(font);
      strB.append("\"/></w:listPr></w:pPr><w:r><w:t>");
      strB.append(Xml.escape(headingText));
      strB.append("</w:t></w:r></w:p>");
   }

   public String setHeadingNumbers(String outLineNumber, String template) throws CharacterCodingException {
      if (outLineNumber == null) {
         return template;
      }

      int index = 1;
      String[] numbers = outLineNumber.split("\\.");

      for (String number : numbers) {
         template =
               template.replaceAll("<w:start w:val=\"1\"/><w:pStyle w:val=\"Heading" + index + "\"/>",
                     "<w:start w:val=\"" + number + "\"/><w:pStyle w:val=\"Heading" + index + "\"/>");

         index++;
      }
      return template;
   }

   public String setAppendixStartLetter(char chr, String template) {
      template =
            template.replace(
                  "<w:start w:val=\"1\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>",
                  "<w:start w:val=\"" + (Character.toLowerCase(chr) - 'a' + 1) + "\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>");
      return template;
   }

   public void endOutlineSubSection() throws IOException {
      endOutlineSubSection(false);
   }

   private void endOutlineSubSection(boolean force) throws IOException {
      if (!force && flattenedLevelCount > 0) {
         flattenedLevelCount--;
      } else {
         strB.append("</wx:sub-section>");
         if (outlineLevel + 1 < outlineNumber.length) outlineNumber[outlineLevel + 1] = 0;
         outlineLevel--;
      }
   }

   public void addWordMl(CharSequence wordMl) throws IOException {
      strB.append(wordMl);
   }

   public void startParagraph() throws IOException {
      strB.append("<w:p>");
   }

   public void createSubDoc(String fileName) throws IOException {
      if (fileName == null || fileName.length() == 0) {
         throw new IllegalArgumentException("The file name can not be null or empty.");
      }

      strB.append(SUB_DOC.replace(FILE_NAME, fileName));
   }

   public void createHyperLinkDoc(String fileName) throws IOException {
      if (fileName == null || fileName.length() == 0) {
         throw new IllegalArgumentException("The file name can not be null or empty.");
      }

      strB.append(HYPER_LINK_DOC.replace(FILE_NAME, fileName));
   }

   public void resetListValue() throws IOException {
      startParagraph();
      addWordMl(LISTNUM_FIELD);
      endParagraph();
   }

   public void endParagraph() throws IOException {
      strB.append("</w:p>");
   }

   public void addParagraph(CharSequence text) throws IOException {
      strB.append("<w:p><w:r><w:t>");
      strB.append(Xml.escape(text));
      strB.append("</w:t></w:r></w:p>");
   }

   public void addTextInsideParagraph(CharSequence text) throws IOException {
      strB.append("<w:r><w:t>");
      strB.append(Xml.escape(text));
      strB.append("</w:t></w:r>");
   }

   public void addTextInsideParagraph(CharSequence text, String rgbHexColor) throws IOException {
      if (rgbHexColor == null) throw new IllegalArgumentException("rgbHexColor can not be null");
      if (rgbHexColor.length() != 6) throw new IllegalArgumentException(
            "rgbHexColor should be a hex string 6 characters long");

      strB.append("<w:r><w:rPr><w:color w:val=\"");
      strB.append(rgbHexColor);
      strB.append("\"/></w:rPr>");
      strB.append("<w:t>");
      strB.append(Xml.escape(text));
      strB.append("</w:t></w:r>");
   }

   public void addOleData(CharSequence oleData) throws IOException {
      strB.append("<w:docOleData>");
      strB.append(oleData);
      strB.append("</w:docOleData>");
   }

   private CharSequence getOutlineNumber() {
      StringBuilder strB = new StringBuilder();
      for (int i = 1; i < outlineLevel; i++) {
         strB.append(String.valueOf(outlineNumber[i]));
         strB.append('.');
      }
      strB.append(String.valueOf(outlineNumber[outlineLevel]));
      return strB;
   }

   public boolean okToStartSubsection() {
      return outlineLevel < 9;
   }

   /**
    * @param nextOutlineNumber
    */
   public void setNextParagraphNumberTo(String nextOutlineNumber) {
      String[] nextOutlineNumbers = nextOutlineNumber.split("\\.");
      Arrays.fill(outlineNumber, 0);

      for (int i = 0; i < nextOutlineNumbers.length; i++) {

         outlineNumber[i + 1] = Integer.parseInt(nextOutlineNumbers[i]);
      }
      outlineNumber[nextOutlineNumbers.length]--;
      outlineLevel = nextOutlineNumbers.length - 1;
   }

   /**
    * Sets the page layout to either portrait/landscape depending on the artifacts pageType attribute value. Note: This
    * call should be done after processing each artifact so if a previous artifact was landscaped the following artifact
    * would be set back to portrait.
    */
   public void setPageLayout(Attribute pageTypeAttr) throws IOException {
      boolean landscape = (pageTypeAttr != null && pageTypeAttr.getStringData().equals("Landscape"));

      if (landscape || previousPageLandsacpe) {
         strB.append("<w:p>");
         strB.append("<w:pPr>");
         strB.append("<w:sectPr>");
         strB.append(landscape ? "<w:pgSz w:w=\"15840\" w:h=\"12240\" w:orient=\"landscape\" w:code=\"1\" />" : "<w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\" />");
         strB.append("</w:sectPr>");
         strB.append("</w:pPr>");
         strB.append("</w:p>");

         previousPageLandsacpe = landscape;
      }
   }
}
