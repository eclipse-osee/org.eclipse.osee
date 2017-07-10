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
package org.eclipse.osee.framework.core.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public class WordMLProducer {
   public static final String RGB_RED = "FF0000";
   public static final String RGB_GREEN = "00FF00";
   public static final String RGB_BLUE = "0000FF";

   private static final String FILE_NAME = "fileName";

   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";
   public static final String LISTNUM_FIELD_TAIL =
      "<w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\"1.\"/></w:r>";

   //This regular expression pulls out all of the stuff after the inserted listnum reordering stuff.  This needs to be
   //here so that we remove unwanted template information from single editing
   public static final String LISTNUM_FIELD_TAIL_REG_EXP =
      "<w:r(>| .*?>)<w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r(>| .*?>)<w:rPr><w:vanish/></w:rPr><w:instrText> LISTNUM \"listreset\"";
   public static final String LISTNUM_FIELD = LISTNUM_FIELD_HEAD + LISTNUM_FIELD_TAIL;
   private static final String SUB_DOC =
      "<wx:sect><w:p><w:pPr><w:sectPr><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:pPr></w:p><w:subDoc w:link=\"" + FILE_NAME + "\"/></wx:sect><wx:sect><wx:sub-section><w:p><w:pPr><w:pStyle w:val=\"Heading1\"/></w:pPr></w:p><w:sectPr><w:type w:val=\"continuous\"/><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></wx:sub-section></wx:sect>";
   private static final String HYPER_LINK_DOC =
      "<w:p><w:hlink w:dest=\"fileName\"><w:r wsp:rsidRPr=\"00CE6681\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>fileName</w:t></w:r></w:hlink></w:p>";
   private final Appendable strB;
   private final int[] outlineNumber;
   private int outlineLevel;
   private int flattenedLevelCount;
   private final Map<String, Integer> alphabetMap;

   private static final String DEFAULT_FONT = "Times New Roman";

   public WordMLProducer(Appendable str) {
      strB = str;
      outlineNumber = new int[10]; // word supports 9 levels of outlining; index this array from 1 to 9
      outlineLevel = 0;
      flattenedLevelCount = 0;

      alphabetMap = new HashMap<>();

      alphabetMap.put("A.0", 1);
      alphabetMap.put("B.0", 2);
      alphabetMap.put("C.0", 3);
   }

   public CharSequence startOutlineSubSection() throws OseeCoreException {
      CharSequence paragraphNumber = startOutlineSubSection(DEFAULT_FONT, null, null);
      return paragraphNumber;
   }

   public CharSequence startOutlineSubSection(CharSequence font, CharSequence headingText, String outlineType) throws OseeCoreException {
      if (okToStartSubsection()) {
         outlineNumber[++outlineLevel]++;
         CharSequence paragraphNumber = getOutlineNumber();
         startOutlineSubSection((outlineType != null ? outlineType : "Heading") + outlineLevel, paragraphNumber, font,
            headingText);
         return paragraphNumber;
      } else {
         flattenedLevelCount++;
         endOutlineSubSection(true);
         OseeLog.log(this.getClass(), Level.WARNING, "Outline level flattened, outline can only go 9 levels deep");
         return startOutlineSubSection(font, headingText, outlineType);
      }
   }

   private void append(CharSequence value) throws OseeCoreException {
      try {
         strB.append(value);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public void startOutlineSubSection(CharSequence style, CharSequence outlineNumber, CharSequence font, CharSequence headingText) throws OseeCoreException {
      append("<wx:sub-section>");
      if (Strings.isValid(headingText)) {
         startParagraph();
         append("<w:pPr>");
         writeParagraphStyle(style);
         append("<w:listPr><wx:t wx:val=\"");
         append(outlineNumber);
         append("\" wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"/><wx:font wx:val=\"");
         append(font);
         append("\"/></w:listPr></w:pPr>");
         writeHeadingText(headingText);
         endParagraph();
      }
   }

   public void setPageBreak() throws OseeCoreException {
      append("<w:p>");
      append("<w:pPr>");
      append("<w:sectPr>");
      append("<w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\" />");
      append("</w:sectPr>");
      append("</w:pPr>");
      append("</w:p>");
   }

   private void writeParagraphStyle(CharSequence style) throws OseeCoreException {
      append("<w:pStyle w:val=\"");
      append(style);
      append("\"/>");
   }

   private void writeHeadingText(CharSequence headingText) throws OseeCoreException {
      append("<w:r><w:t>");
      append(Xml.escape(headingText));
      append("</w:t></w:r>");
   }

   public String setHeadingNumbers(String outlineNumber, String template, String outlineType) {
      boolean appendixOutlineType = outlineType != null && outlineType.equalsIgnoreCase("APPENDIX");
      if (outlineNumber == null) {
         return template;
      }

      if (appendixOutlineType) {
         // Example of appendix number: A.0
         char[] chars = outlineNumber.toCharArray();
         template = setAppendixStartLetter(chars[0], template);
      } else {
         int index = 1;
         String[] numbers = outlineNumber.split("\\.");

         for (String number : numbers) {
            Matcher matcher = Pattern.compile(
               String.format("<w:start w:val=\"(\\d*?)\"/><w:pStyle w:val=\"Heading%d\"/>", index)).matcher("");
            matcher.reset(template);
            template = matcher.replaceAll(
               String.format("<w:start w:val=\"%s\"/><w:pStyle w:val=\"Heading%d\"/>", number, index));
            index++;
         }
      }
      if (!appendixOutlineType) {
         setNextParagraphNumberTo(outlineNumber);
      }
      return template;
   }

   public String setAppendixStartLetter(char chr, String template) {
      template = template.replace("<w:start w:val=\"1\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>",
         "<w:start w:val=\"" + (Character.toLowerCase(
            chr) - 'a' + 1) + "\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>");
      return template;
   }

   public void endOutlineSubSection() throws OseeCoreException {
      endOutlineSubSection(false);
   }

   private void endOutlineSubSection(boolean force) throws OseeCoreException {
      if (!force && flattenedLevelCount > 0) {
         flattenedLevelCount--;
      } else {
         append("</wx:sub-section>");
         if (outlineLevel + 1 < outlineNumber.length) {
            outlineNumber[outlineLevel + 1] = 0;
         }
         outlineLevel--;
      }
   }

   public void addWordMl(CharSequence wordMl) throws OseeCoreException {
      append(wordMl);
   }

   public void startParagraph() throws OseeCoreException {
      append("<w:p>");
   }

   public void startSubSection() throws OseeCoreException {
      append("<wx:sect>");
   }

   public void endSubSection() throws OseeCoreException {
      append("</wx:sect>");
   }

   public void createSubDoc(String fileName) throws OseeCoreException {
      if (Strings.isValid(fileName)) {
         throw new IllegalArgumentException("The file name can not be null or empty.");
      }

      append(SUB_DOC.replace(FILE_NAME, fileName));
   }

   public void createHyperLinkDoc(String fileName) throws OseeCoreException {
      if (!Strings.isValid(fileName)) {
         throw new IllegalArgumentException("The file name can not be null or empty.");
      }

      append(HYPER_LINK_DOC.replace(FILE_NAME, fileName));
   }

   public void resetListValue() throws OseeCoreException {
      // extra paragraph needed to support WORD's bug to add in a trailing zero when using field codes
      startParagraph();
      addWordMl(LISTNUM_FIELD_HEAD);
      endParagraph();

      startParagraph();
      //The listnum also acts a template delimiter to know when to remove unwanted content.
      addWordMl(LISTNUM_FIELD);
      endParagraph();
   }

   public void endParagraph() throws OseeCoreException {
      append("</w:p>");
   }

   public void startTable() throws OseeCoreException {
      append("<wx:sub-section><w:tbl>");
   }

   public void endTable() throws OseeCoreException {
      append("</w:tbl></wx:sub-section>");
   }

   public void startTableRow() throws OseeCoreException {
      append("<w:tr>");
   }

   public void endTableRow() throws OseeCoreException {
      append("</w:tr>");
   }

   public void startTableColumn() throws OseeCoreException {
      append("<w:tc>");
   }

   public void endTableColumn() throws OseeCoreException {
      append("</w:tc>");
   }

   public void addTableColumns(String... datas) throws OseeCoreException {
      for (String data : datas) {
         startTableColumn();
         addParagraph(data);
         endTableColumn();
      }
   }

   public void addTableRow(String... datas) throws OseeCoreException {
      startTableRow();
      addTableColumns(datas);
      endTableRow();
   }

   public void addParagraphNoEscape(CharSequence text) throws OseeCoreException {
      append("<w:p><w:r><w:t>");
      append(text);
      append("</w:t></w:r></w:p>");
   }

   public void addEditParagraphNoEscape(CharSequence text) throws OseeCoreException {
      startParagraph();
      append(text);
      endParagraph();
   }

   public void addParagraph(CharSequence text) throws OseeCoreException {
      startParagraph();
      addTextInsideParagraph(text);
      endParagraph();
   }

   public void addParagraphBold(CharSequence text) throws OseeCoreException {
      append("<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>");
      append(Xml.escape(text));
      append("</w:t><w:rPr><w:b/></w:rPr></w:r></w:p>");
   }

   /**
    * This method will escape the provided text.
    */
   public void addTextInsideParagraph(CharSequence text) throws OseeCoreException {
      append("<w:r><w:t>");
      append(Xml.escape(text));
      append("</w:t></w:r>");
   }

   public void addTextInsideParagraph(CharSequence text, String rgbHexColor) throws OseeCoreException {
      if (rgbHexColor == null) {
         throw new IllegalArgumentException("rgbHexColor can not be null");
      }
      if (rgbHexColor.length() != 6) {
         throw new IllegalArgumentException("rgbHexColor should be a hex string 6 characters long");
      }

      append("<w:r><w:rPr><w:color w:val=\"");
      append(rgbHexColor);
      append("\"/></w:rPr>");
      append("<w:t>");
      append(Xml.escape(text));
      append("</w:t></w:r>");
   }

   public void addOleData(CharSequence oleData) throws OseeCoreException {
      append("<w:docOleData>");
      append(oleData);
      append("</w:docOleData>");
   }

   private CharSequence getOutlineNumber() {
      StringBuilder strB = new StringBuilder();
      for (int i = 1; i < outlineLevel; i++) {
         strB.append(String.valueOf(outlineNumber[i]));
         strB.append(".");
      }
      strB.append(String.valueOf(outlineNumber[outlineLevel]));
      return strB;
   }

   public boolean okToStartSubsection() {
      return outlineLevel < 9;
   }

   public void setNextParagraphNumberTo(String nextOutlineNumber) {
      String[] nextOutlineNumbers = nextOutlineNumber.split("\\.");
      Arrays.fill(outlineNumber, 0);

      try {
         for (int i = 0; i < nextOutlineNumbers.length; i++) {

            outlineNumber[i + 1] = Integer.parseInt(nextOutlineNumbers[i]);
         }
         outlineNumber[nextOutlineNumbers.length]--;
         outlineLevel = nextOutlineNumbers.length - 1;
      } catch (NumberFormatException ex) {
         //Do nothing
      }
   }

   /**
    * Sets the page layout to either portrait/landscape depending on the artifacts pageType attribute value. Note: This
    * call should be done after processing each artifact so if a previous artifact was landscaped the following artifact
    * would be set back to portrait.
    */
   public void setPageLayout(String pageType) {
      boolean landscape = pageType != null && pageType.equals("Landscape");

      if (landscape) {
         append("<w:p>");
         append("<w:pPr>");
         append("<w:sectPr>");
         append("<w:pgSz w:w=\"15840\" w:h=\"12240\" w:orient=\"landscape\" w:code=\"1\" />");
         append("</w:sectPr>");
         append("</w:pPr>");
         append("</w:p>");
      }
   }
}
