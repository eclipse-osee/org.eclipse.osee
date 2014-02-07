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
package org.eclipse.osee.define.report.internal.util;

public final class RequirementConstants {

   public RequirementConstants() {
      //Utility class
   }

   //document specific
   public static final String COMPANY = "Company";
   public static final String PUBLISH_REQUIREMENT = "Publish Requirement";
   public static final String PROPERTIES = "properties";
   public static final String DOCUMENT_TEMPLATE_GUID = "AK49Q_AUDSgm+rW5zFwA";
   public static final String CSID_NONE = "CSID None";
   public static final String SUBDD_NONE = "SubDD None";
   public static final String PIDS_NONE = "PIDS None";

   //wordml
   public static final String INS = "</w:ins>";
   public static final String[] WXML_CHARS = new String[] {"&", "<", ">", "\""};
   public static final String[] WXML_ESCAPES = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"};
   public static final String FONT = "<w:rFonts w:ascii=\"Helvetica\" w:hAnsi=\"Helvetica\" w:cs=\"Helvetica\"/>";
   public static final String RUNEND = "</w:t></w:r>";
   public static final String EMPTY_PARAGRAPH = "<w:p/>";
   public static final String PARAGRAPH_END = "</w:t></w:r></w:p>";
   public static final String PARAGRAPH_START = "<w:p><w:r><w:t>";
   public static final String HEADING_BOLDED = "<w:rPr><w:b/></w:rPr><w:t xml:space=\"preserve\">%s</w:t>";
   public static final String SENTENCE = "<w:t xml:space=\"preserve\">%s</w:t>";
   public static final String BULLETSYM = "<w:t></w:t>";
   public static final String OLD_BULLET_STYLE = "bullettight1";
   public static final String NEW_BULLET_STYLE = "bulletlvl2";
   public static final String PARA_REGEX = "<w:p(.*?)>";
   public static final String FONT_REGEX = "<w:rFonts(.*?)/>";
   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";
   public static final String LISTNUM_FIELD_TAIL =
      "<w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\"1.\"/></w:r>";
   public static final String LISTNUM_FIELD = LISTNUM_FIELD_HEAD + LISTNUM_FIELD_TAIL;

}
