/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

/**
 * @author Megumi Telles
 */
public class ReportConstants {

   public ReportConstants() {
      //Utility class
   }

   //regex
   public static final String FTR = "<w:ftr[\\s\\S]+?</w:ftr>";
   public static final String PAGE_SZ = "<w:pgSz [^>]*/>";
   public static final String ENTIRE_FTR_EXTRA_PARA =
      "<w:p[^>]*><w:pPr><w:spacing w:after=\"[\\d]*\"[^>]*>(</w:spacing>)*<w:sectPr[^>]*>(<w:r><w:t>)?<w:ftr[^>]*>[\\s\\S]+</w:ftr>[\\s\\S]+</w:sectPr></w:pPr></w:p>";
   public static final String ENTIRE_FTR = "<w:sectPr[^>]*><w:ftr[\\s\\S]+?</w:ftr>[\\s\\S]+?</w:sectPr>";
   public static final String FULL_PARA_END = "</w:pPr></w:p>";
   public static final String NO_DATA_RIGHTS =
      "<w:p>[\\s||\\S]+?<w:r><w:t>NO DATA RIGHTS ARTIFACT FOUND</w:t></w:r>[\\s\\S]+?</w:p>";

   //wordml
   public static final String PG_SZ = "<w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/>";
   public static final String CONTINUOUS = "<w:type w:val=\"continuous\"/>";
   public static final String SECTION_TEMPLATE = "<w:sectPr>%s</w:sectPr>";
   public static final String LANDSCAPE_ORIENT = "<w:pgSz w:w=\"15840\" w:h=\"12240\" w:orient=\"landscape\"/>";
   public static final String PORTRAIT_ORIENT = "<w:pgSz w:w=\"12240\" w:h=\"15840\"/>";
   public static final String NEW_PAGE_TEMPLATE =
      "<w:p><w:pPr><w:spacing w:after=\"0\"/>" + SECTION_TEMPLATE + "</w:pPr></w:p>";
   public static final String PAGE_ADDS =
      "%s <w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:cols w:space=\"720\"/>";
   public static final String INS = "</w:ins>";
   public static final String[] WXML_CHARS = new String[] {"&", "<", ">", "\""};
   public static final String[] WXML_ESCAPES = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"};
   public static final String FONT = "<w:rFonts w:ascii=\"Helvetica\" w:hAnsi=\"Helvetica\" w:cs=\"Helvetica\"/>";
   public static final String RUN_END = "</w:t></w:r>";
   public static final String PARA_END = "</w:p>";
   public static final String PARA_START = "<w:p>";
   public static final String EMPTY_PARAGRAPH = "<w:p/>";
   public static final String PARAGRAPH_END = RUN_END + PARA_END;
   public static final String PARAGRAPH_START = PARA_START + "<w:r><w:t>";
   public static final String HEADING_BOLDED = "<w:rPr><w:b/></w:rPr><w:t xml:space=\"preserve\">%s</w:t>";
   public static final String SENTENCE = "<w:t xml:space=\"preserve\">%s</w:t>";
   public static final String BULLETSYM = "<w:t></w:t>";
   public static final String OLD_BULLET_STYLE = "bullettight1";
   public static final String NEW_BULLET_STYLE = "bulletlvl2";
   public static final String PARA_REGEX = "<w:p(.*?)>";
   public static final String PARA_PROP_START = "<w:pPr>";
   public static final String PARA_PROP_REGEX = PARA_PROP_START + "(.*?)</w:pPr>";
   public static final String FONT_REGEX = "<w:rFonts(.*?)/>";
   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";
   public static final String LISTNUM_FIELD_TAIL =
      "<w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\"1.\"/></w:r>";
   public static final String LISTNUM_FIELD = LISTNUM_FIELD_HEAD + LISTNUM_FIELD_TAIL;
}