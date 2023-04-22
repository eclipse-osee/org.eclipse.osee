/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.regex.TokenMatcher;
import org.eclipse.osee.framework.jdk.core.util.regex.TokenPattern;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;

/**
 * Collection of Word ML utility methods that may be used in both OSEE Client and OSEE Server code.
 *
 * @implNote All Word ML constants, patterns, and regular expressions should be placed into this file.
 * @implNote All methods that produce a Word ML segment or modify a Word ML segment should be placed in this file.
 * @implNote XML utility methods that do not have any Word ML specifics should be implemented in the class
 * {@link org.eclipse.osee.framework.jdk.core.util.xml.Xml}. Word ML utility methods that require OSEE Client specific
 * classes should be implemented in the class {@link org.eclipse.osee.framework.skynet.core.word.WordCoreUtilClient}.
 * Word ML utility methods that require OSEE Sever specific classes should be implemented in the class
 * {@link org.eclipse.osee.define.rest.internal.wordupdate.WordCoreUtilServer}.
 * @author Megumi Telles
 * @author Loren K. Ashley
 */

public class WordCoreUtil {

   /**
    * {@link Pattern} used to remove aml:annotation, aml:content, and w:delText tags; as well as everything between the
    * w:delText open and close tags.
    */

   //@formatter:off
   private static final Pattern ANNOTATIONS_REMOVAL_PATTERN =
      Pattern.compile
         (
              "(?:"
            + "<.??aml:annotation.*?>"
            + "|"
            + "<.??aml:content.*?>"
            + "|"
            + "<w:delText>.*?</w:delText>"
            + ")"
         );
   //@formatter:on

   //@formatter:off
   private static final Pattern APPENDIX_START_LETTER_PATTERN =
      Pattern.compile
         (
            "<w:start w:val=\"1\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>"
         );
   //@formatter:on

   /**
    * APPENDIX START LETTER Template used by {@link #setAppendixStartLetter} to rebuild the WordML for an appendix with
    * the proper appendix letter.
    */

   //@formatter:off
   private static final String APPENDIX_START_LETTER_TEMPLATE_PART_A =
      "<w:start w:val=\"";
   //@formatter:on

   /**
    * Part B of the APPENDIX START LETTER template
    */

   //@formatter:off
   private static final String APPENDIX_START_LETTER_TEMPLATE_PART_B =
      "\"/><w:nfc w:val=\"3\"/><w:pStyle w:val=\"APPENDIX1\"/>";
   //@formatter:on

   /**
    * Size of the APPENDIX START LETTER template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int APPENDIX_START_LETTER_TEMPLATE_SIZE =
        WordCoreUtil.APPENDIX_START_LETTER_TEMPLATE_PART_A.length()
      + WordCoreUtil.APPENDIX_START_LETTER_TEMPLATE_PART_B.length();
   //@formatter:on

   // @formatter:off
   public static final String BIN_DATA =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuI/HV" +
      "3L4l1XTJvEnhmwtrKK1aC5uYSRd+bEHZkzcKNoPTBbhhz3Ol478fWvhjR9TFhcW0us2awt9nmRmQ" +
      "eY4AViMDcU3sF3bsKWxgGix0bxLpfi3Xtahs9JnTVltSYnv5IzC0UW1hkQncMk4PHAHHOBleLPh/" +
      "rWrz+Kv7Nu9PWHXls2b7TvDRtAcbPlBGCAG3c9Nu3ncPfhHBSxEPaWUEovdav3OZOyv/ADPXe2lj" +
      "J81nY0ta8fjw7N4nluxDdw6V9mWC2tYpRKryoSBM5GxVJxhlzgcHLEA9pBMtxBHMgcJIodRIhRgC" +
      "M8qwBB9iARXAa98P9Q1l/G4W7tok11bI2pO4lWgAyHGOASAMjOAc44wen/4S/wAPwfub/XtGtryP" +
      "5Z4P7QjPlyDhlycHg5HIB9hXHiKNGdOH1dXlpe3+GHS383N+vQpN31ObtPGOsap4k1fSrW40azvr" +
      "G+8iHS7+OVJbqEYPmLKG/iUOwxG2BtzkHJ2bfxxp0/iHW9KeG5hGkrF5k0kD4kdyRtUbckk7Ao6y" +
      "FjtBAycbxb4O1jxRJIj2+jRTx3Mb2OswySxXdrErBsbAp3sMvj94qkkHCkVY1Hwjrp1Xxbd6RqcN" +
      "pJrltbiG4ywkt5Yl2bcAH5WXPzghlJ4U4zXTKGCqRV2otpddneN3dJ3VuZ6rmVnvoT7yNh/G3h+L" +
      "TdQv5r14YdOZEu1mtpY5YS+Nm6NlD4bcMHGDz6GoLTxvY33i+Hw9BZ6gsslm90ZZ7OWELhgoG10B" +
      "wfm+Y4XIABJOBx0vww1N9D8WWNpBpOnJqy2KWlvDcSSRwiAjducxgktjOcEkk59a7WXw/dj4jW/i" +
      "SGSF7ZtMbT54nYq6fvPMV1wCGyeCDtx1yelZ1KGAgpcsm3Z21W/LFrZd3Jb20GnNnR0UUV45oFFF" +
      "FABRRRQAUUUUAFFFFABRRRQB/9k=";
   // @formatter:on

   //@formatter:off
   public static final String BIN_DATA_END =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1jUPE" +
      "2oR+NF8NabpdtcTf2f8Ab2mubxoVC+YY9uFjck5wf886s2t6fYIE1XUNPsrpIFnmie6UCNSQu7Lb" +
      "SV3naGIGTjoeK5HxB4emufiUmsXXhn+29KGji1CYt32zecWztldei55H9761V17wjfal4hvL210V" +
      "I7U+EpbCzjYxAwXLFgsYAbCkIxXI+XBIzivYWGwk1BOSj7t3qt+2r0+5fMzvJXO11/XrTQNMubma" +
      "SFrmO2muILV5gj3HlIXYLnk8DkgHGc1Pouo/2xoOnan5Xk/bLaO48vdu2b1DYzgZxnrivMdQ8E68" +
      "1ra7dJhvDJ4QXSHiedB5FyhVwWzweR8pXPzKMlR8w9H8MWc+neE9GsbqPy7m2sYIZUyDtdY1BGRw" +
      "eQelY4rD4elh04TUpX79PS44tt6mUnifVb3xVrWiaZpFlL/ZXkeZNc37Rb/NTeMKsT9MEdfSp/GH" +
      "i+08J6Je3mIbq8tokm+w/aAkjRtKse/oSFy3XGMjFc3J4cdPH/iXU9T8Hf21Z332X7HJttZNuyLa" +
      "/EsilcnHbnb9KyvG/gnXtSuvGH2HSYb4av8AYJrSUzovktCNjgbuQ+CfQbWb5s/KeylhcFOvTUpJ" +
      "RtBvXdvk5k3zabyb0VrNIlykkzstT8c2OhTa62rrDBaaZ5IieO6SWW5eRC2zyh8yNxxu6jLcAEjp" +
      "oJ4rmCOeCVJYZVDxyRsGV1IyCCOCCO9eZeJfBeuas/xCW2tkA1ZbBrFnlUCYwgF165U5GBuwMkc4" +
      "5r02CRpYI5HheF3UM0UhBZCR907SRkdOCR7muHF0sPClCVJ+87X1/uQe3q5J+a6bFRbvqSUUUV55" +
      "YUUUUAFZV54Y8P6jdPdX2h6Zc3MmN809pG7tgYGSRk8AD8KKKuFScHeDt6Ba5owQRW0EcEESRQxK" +
      "EjjjUKqKBgAAcAAdqkooqG76sAooooAKKKKAP//Z";
   // @formatter:on

   /**
    * Word ML bold style
    */

   public static final String BOLD = "<w:b/>";

   /**
    * Template used to generate the Word ML for a book mark annotation with a specified identifier.
    */

   //@formatter:off
   private static final String BOOKMARK_TEMPLATE_PART_A =
      "<aml:annotation aml:id=\"";
   //@formatter:on

   /**
    * Part B of the WORDML BOOKMARK template.
    */

   //@formatter:off
   private static final String BOOKMARK_TEMPLATE_PART_B =
      "\" w:type=\"Word.Bookmark.Start\" w:name=\"OSEE.";
   //@formatter:on

   /**
    * Part C of the WORDML BOOKMARK template.
    */

   //@formatter:off
   private static final String BOOKMARK_TEMPLATE_PART_C =
      "\"/><aml:annotation aml:id=\"";
   //@formatter:on

   /**
    * Part D of the WORDML BOOKMARK template.
    */

   //@formatter:off
   private static final String BOOKMARK_TEMPLATE_PART_D =
      "\" w:type=\"Word.Bookmark.End\"/>";
   //@formatter:on

   /**
    * Size of the WORDML BOOKMARK template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int BOOKMARK_TEMPLATE_SIZE =
        WordCoreUtil.BOOKMARK_TEMPLATE_PART_A.length()
      + WordCoreUtil.BOOKMARK_TEMPLATE_PART_B.length()
      + WordCoreUtil.BOOKMARK_TEMPLATE_PART_C.length()
      + WordCoreUtil.BOOKMARK_TEMPLATE_PART_D.length();
   //@formatter:on

   /**
    * Template used to generate figure and table captions.
    */

   //@formatter:off
   private static final String CAPTION_TEMPLATE_PART_A =
      "<w:fldSimple w:instr=\" STYLEREF 1 \\s \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple><w:r><w:noBreakHyphen/></w:r><w:fldSimple w:instr=\" SEQ ";
   //@formatter:on

   /**
    * Part B of the NEW CAPTION template.
    */

   //@formatter:off
   private static final String CAPTION_TEMPLATE_PART_B =
      " \\* ARABIC \\s 1 \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple>";
   //@formatter:on

   /**
    * Part C of the NEW CAPTION template.
    */

   //@formatter:off
   private static final String CAPTION_TEMPLATE_PART_C =
      "<w:r><w:t>";
   //@formatter:on

   /**
    * Part D of the NEW CAPTION template.
    */

   //@formatter:off
   private static final String CAPTION_TEMPLATE_PART_D =
      "</w:t></w:r>";
   //@formatter:on

   /**
    * Size of the NEW CAPTION template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int CAPTION_TEMPLATE_SIZE =
        WordCoreUtil.CAPTION_TEMPLATE_PART_A.length()
      + WordCoreUtil.CAPTION_TEMPLATE_PART_B.length()
      + WordCoreUtil.CAPTION_TEMPLATE_PART_C.length()
      + WordCoreUtil.CAPTION_TEMPLATE_PART_D.length()
      ;
   //@formatter:on

   public static final String CONFIGAPP = "configuration";

   public static final String CONFIGGRPAPP = "configurationgroup";

   /**
    * Pattern used to remove no data rights artifact found statements.
    */

   //@formatter:off
   private static final Pattern DATA_RIGHTS_NO_ARTIFACT_FOUND_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:p>[\\s||\\S]+?<w:r><w:t>NO DATA RIGHTS ARTIFACT FOUND</w:t></w:r>[\\s\\S]+?</w:p>"
         );
   //@formatter:on

   /**
    * The default font name
    */

   public static final String DEFAULT_FONT = "Times New Roman";

   /**
    * Word ML word document
    */

   public static final String DOCUMENT = "<w:wordDocument>";

   /**
    * Word ML word document end
    */

   public static final String DOCUMENT_END = "</w:wordDocument>";

   public static final String FEATUREAPP = "feature";

   public static final String FILE_NAME = "fileName";

   /**
    * The page size tags are removed with {@link #PAGE_SIZE_REMOVAL_PATTERN} and replaced with this string.
    */

   private static final StringBuilder FOOTER_PAGE_SIZE =
      new StringBuilder("<w:type w:val=\"continuous\"/><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/>");

   /**
    * {@link Pattern} used to remove w:ftr tags and everything between the w:ftr open and closing tag.
    */

   //@formatter:off
   private static final Pattern FOOTER_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:ftr[\\s\\S]+?</w:ftr>"
         );
   //@formatter:on

   /**
    * {@link Pattern} used to remove a paragraph wrapped footer.
    */

   //@formatter:off
   public static final Pattern FOOTER_ENTIRE_EXTRA_PARA_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:p[^>]*><w:pPr>(<w:spacing w:after=\"[\\d]*\"[^>]*>)*(</w:spacing>)*<w:sectPr[^>]*>(<w:r><w:t>)?<w:ftr[^>]*>[\\s\\S]+</w:ftr>[\\s\\S]+</w:sectPr></w:pPr></w:p>"
         );
   //@formatter:on

   /**
    * {@link Pattern} used to remove a section presentation wrapped footer.
    */

   //@formatter:off
   public static final Pattern FOOTER_ENTIRE_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:sectPr[^>]*><w:ftr[\\s\\S]+?</w:ftr>[\\s\\S]+?</w:sectPr>"
         );
   //@formatter:on

   /**
    * Word ML hard line break
    */

   private static final StringBuilder HARD_LINE_BREAK = new StringBuilder("<w:br/>");

   private static final String HYPERLINK_DOCUMENT_TEMPLATE_PART_A = "<w:p><w:hlink w:dest=\"";

   private static final String HYPERLINK_DOCUMENT_TEMPLATE_PART_B =
      "\"><w:r wsp:rsidRPr=\"00CE6681\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>";

   private static final String HYPERLINK_DOCUMENT_TEMPLATE_PART_C = "</w:t></w:r></w:hlink></w:p>";

   //@formatter:off
   private static final int HYPERLINK_DOCUMENT_TEMPLATE_SIZE =
        HYPERLINK_DOCUMENT_TEMPLATE_PART_A.length()
      + HYPERLINK_DOCUMENT_TEMPLATE_PART_B.length()
      + HYPERLINK_DOCUMENT_TEMPLATE_PART_C.length()
      ;
   //@formatter:on

   /**
    * INITIAL HEADING NUMBER REGEX template used to replace the initial list sequence numbers in the list definitions
    * for each heading level.
    */

   private static final String INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_A =
      "<w:start w:val=\"(\\d*?)\"/><w:pStyle w:val=\"Heading";

   /**
    * Part B of the INITIAL HEADING NUMBER REGEX template.
    */

   private static final String INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_B = "\"/>";

   /**
    * Size of the INITIAL HEADING NUMBER REGEX template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_SIZE =
        INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_A.length()
      + INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_B.length();
   //@formatter:on

   /**
    * Shorter regular expression used to determine if a publishing template's replacement token is for artifacts or for
    * links.
    */

   //@formatter:off
   private static final Pattern INSERT_HERE_TEST_PATTERN =
      Pattern.compile
         (
            "INSERT_(ARTIFACT|LINK)_HERE",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE
         );
   //@formatter:off

   /**
    * {@link Pattern} used to replace line breaks &quot;\n&quot; with WordML &lt;w:br/&gt;.
    */

   //@formatter:off
   private static final Pattern LINE_BREAKS_REMOVAL_PATTERN =
      Pattern.compile
         (
            "\\v"
         );
   //@formatter:on

   //@formatter:off
   private static final String LINK_INTERNAL_DOC_TEMPLATE_PART_A =
      "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> HYPERLINK \\l \"OSEE.";
   //@formatter:on

   //@formatter:off
   private static final String LINK_INTERNAL_DOC_TEMPLATE_PART_B =
      "\" </w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>";
   //@formatter:on

   //@formatter:off
   private static final String LINK_INTERNAL_DOC_TEMPLATE_PART_C =
      "</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r>";
   //@formatter:on

   //@formatter:off
   private static final int LINK_INTERNAL_DOC_TEMPLATE_SIZE =
        WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_A.length()
      + WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_B.length()
      + WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_C.length();
   //@formatter:on

   /**
    * LINK template used by {@link #getLink} to produce a hyperlink to an OSEE artifact.
    */

   //@formatter:off
   private static final String LINK_TEMPLATE_PART_A =
      "<w:hlink w:dest=\"";
   //@formatter:on

   /**
    * Part B of the LINK template.
    */

   //@formatter:off
   private static final String LINK_TEMPLATE_PART_B =
      "\"><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>";
   //@formatter:on

   /**
    * Part C of the LINK template.
    */

   //@formatter:off
   private static final String LINK_TEMPLATE_PART_C =
      "</w:t></w:r></w:hlink>";
   //@formatter:on

   /**
    * Size of the LINK template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int LINK_TEMPLATE_SIZE =
        WordCoreUtil.LINK_TEMPLATE_PART_A.length()
      + WordCoreUtil.LINK_TEMPLATE_PART_B.length()
      + WordCoreUtil.LINK_TEMPLATE_PART_C.length();
   //@formatter:on

   /**
    * {@link Pattern} to remove and empty list from Word ML
    */

   public static final Pattern LIST_EMPTY_REMOVAL_PATTERN = Pattern.compile(
      "<w:p wsp:rsidP=\"[^\"]*?\" wsp:rsidR=\"[^\"]*?\" wsp:rsidRDefault=\"[^\"]*?\"><w:pPr><w:pStyle w:val=\"[^\"]*?\"></w:pStyle><w:listPr><wx:t wx:val=\"([^>]*?)\"></wx:t><wx:font wx:val=\"[^\"]*?\"></wx:font></w:listPr></w:pPr><w:r><w:t></w:t></w:r></w:p>");

   /**
    * Word ML list presentation
    */

   public static final String LIST_PRESENTATION = "<w:listPr>";

   /**
    * Word ML list presentation end
    */

   public static final String LIST_PRESENTATION_END = "</w:listPr>";

   public static final String LIST_NUMBER_FIELD_PARAGRAPH_PRESENTATION = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";

   public static final String LIST_NUMBER_FIELD_RUN =
      "<w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:instrText> LISTNUM  \\l 1 \\s 0 </w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\"1.\"/></w:r>";

   public static final String LIST_NUMBER_FIELD =
      WordCoreUtil.LIST_NUMBER_FIELD_PARAGRAPH_PRESENTATION + WordCoreUtil.LIST_NUMBER_FIELD_RUN;

   //This regular expression pulls out all of the stuff after the inserted listnum reordering stuff.  This needs to be
   //here so that we remove unwanted template information from single editing

   public static final String LIST_NUMBER_FIELD_TAIL_REG_EXP =
      "<w:r(>| .*?>)<w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r(>| .*?>)<w:rPr><w:vanish/></w:rPr><w:instrText> LISTNUM \"listreset\"";

   public static final String MAX_TAG_OCCURENCE = "30";

   /**
    * Word ML no proofing
    */

   public static final String NO_PROOF = "<w:noProof/>";

   public static final String OSEE_BOOKMARK_REGEX =
      "^<aml:annotation[^<>]+w:name=\"OSEE\\.([^\"]*)\"[^<>]+w:type=\"Word\\.Bookmark\\.Start\\\"/><aml:annotation[^<>]+Word.Bookmark.End\\\"/>";

   public static final String OSEE_HYPERLINK_REGEX =
      "<w:instrText>\\s+HYPERLINK[^<>]+\"OSEE\\.([^\"]*)\"\\s+</w:instrText>";

   /**
    * OSEE LINK MARKER template used by {@link getOseeLinkMarker} to generate an OSEE link marker.
    */

   private static final String OSEE_LINK_MARKER_TEMPLATE_PART_A = "OSEE_LINK(";

   /**
    * OSEE LINK MARKER template part B.
    */

   private static final String OSEE_LINK_MARKER_TEMPLATE_PART_B = ")";

   /**
    * Size of the OSEE LINK MARKER template parts for {@Link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int OSEE_LINK_MARKER_TEMPLATE_SIZE =
        WordCoreUtil.OSEE_LINK_MARKER_TEMPLATE_PART_A.length()
      + WordCoreUtil.OSEE_LINK_MARKER_TEMPLATE_PART_B.length();
   //@formatter:on

   public static final int OUTLINE_LEVEL_MAXIMUM = 9;

   /**
    * Word ML run with page break
    */

   private static final StringBuilder PAGE_BREAK = new StringBuilder("<w:r><w:br w:type=\"page\"/></w:r>");

   /**
    * Word ML page margins
    */

   public static final String PAGE_MARGINS =
      "<w:pgMar w:top=\"1440\" w:right=\"1296\" w:bottom=\"1440\" w:left=\"1296\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/>";

   /**
    * {@link Pattern} used to remove w:pageNumType tags with a w:start attribute of 1.
    */

   //@formatter:off
   private static final Pattern PAGE_NUMBER_TYPE_START_1_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:pgNumType [^>]*w:start=\"1\"/>"
         );
   //@formatter:on

   /**
    * The page size tags are removed and replaced with {@link #FOOTER_PAGE_SIZE}.
    */

   //@formatter:off
   private static final Pattern PAGE_SIZE_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:pgSz [^>]*/>"
         );
   //@formatter:on

   /**
    * An enumeration of page types that page size Word Ml can be obtained from.
    */

   public enum pageType {

      /**
       * Word ML landscape page size
       */

      //@formatter:off
      LANDSCAPE
         (
            "<w:pgSz w:w=\"15840\" w:h=\"12240\" w:orient=\"landscape\" w:code=\"1\" />"
         ),
      //@formatter:on

      /**
       * Word ML portrait page size
       */

      //@formatter:on
      PORTRAIT("<w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\" />");
      //@formatter:off


      /**
       * Word ML for the default page margins and columns settings.
       */

      private static final String marginsAndColumns =
         "<w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:cols w:space=\"720\"/>";


      /**
       * Prefix used to wrap the page size, page margin, and page columns for the Word Ml to start a new page.
       */

      private static final String newPagePrefix = "<w:p><w:pPr><w:spacing w:after=\"0\"/>";

      /**
       * Suffix used to wrap the page size, page margin, and page columns for the Word Ml to start a new page.
       */

      private static final String newPageSuffix = "</w:pPr></w:p>";

      /**
       * The size of the all strings for a new page template used for {@link StringBuilder} allocation.
       */

      //@formatter:off
      private static final int newPageTemplateSize =
           WordCoreUtil.SECTION_PRESENTATION.length()
         + marginsAndColumns.length()
         + newPagePrefix.length()
         + newPageSuffix.length()
         + WordCoreUtil.SECTION_PRESENTATION_END.length();
      //@formatter:on

      /**
       * Saves the page size Word Ml for the page type.
       */

      private final String pageSizeWordMl;

      /**
       * Saves the page size Word Ml with the margins and columns Word Ml appended.
       */

      private final String pageSizeWithMarginsAndColumnsWordMl;

      /**
       * Creates a new page type enumeration member.
       *
       * @param pageSizeWordMl the pages size Word Ml for the enumeration member.
       */

      private pageType(String pageSizeWordMl) {
         this.pageSizeWordMl = pageSizeWordMl;
         this.pageSizeWithMarginsAndColumnsWordMl = pageSizeWordMl.concat(marginsAndColumns);
      }

      /**
       * Gets the page size Word Ml for the page type.
       *
       * @return the page size Word Ml for the page type.
       */

      public String getPageSize() {
         return this.pageSizeWordMl;
      }

      /**
       * Gets the page size with page margins, and page columns Word Ml for the page type.
       *
       * @return the page size, page margins, and page columns Word Ml for the page type.
       */

      public String getPageSizeWithMarginsAndColumns() {
         return this.pageSizeWithMarginsAndColumnsWordMl;
      }

      /**
       * Writes the page size, page margins, and page columns Word ML wrapped in a section presentation to the provided
       * {@link StringBuilder}. The Word ML in <code>sectionPresentationWordMl</code> is also included within the
       * section presentation.
       *
       * @param sectionPresentationWordMl additional section presentation Word Ml.
       */

      private void getSection(CharSequence sectionPresentationWordMl, StringBuilder output) {
         //@formatter:off
         output
            .append( WordCoreUtil.SECTION_PRESENTATION        )
            .append( sectionPresentationWordMl                )
            .append( this.pageSizeWithMarginsAndColumnsWordMl )
            .append( WordCoreUtil.SECTION_PRESENTATION_END    )
            ;
         //@formatter:on
      }

      /**
       * Gets the page size, page margins, and page columns Word ML wrapped in a section presentation. The Word ML in
       * <code>sectionPresentationWordMl</code> is also included within the section presentation.
       *
       * @param sectionPresentationWordMl additional section presentation Word Ml.
       * @return a {@link StingBuilder} containing the generated Word ML.
       */

      public StringBuilder getSection(CharSequence sectionPresentationWordMl) {
         var size = this.pageSizeWithMarginsAndColumnsWordMl.length() + sectionPresentationWordMl.length();
         var output = new StringBuilder(size * 2);
         this.getSection(sectionPresentationWordMl, output);
         return output;
      }

      /**
       * Gets the page size, page margins, page columns, and <code>sectionPresentationWordMl</code> wrapped in a section
       * presentation and then in paragraph presentation and paragraph for a new page.
       *
       * @param sectionPresentationWordMl additional section presentation Word ML.
       * @return a {@link StringBuilder} containing the generated Word ML.
       */

      public StringBuilder getNewPage(CharSequence sectionPresentationWordMl) {
         var size = WordCoreUtil.pageType.newPageTemplateSize + sectionPresentationWordMl.length();
         var output = new StringBuilder(size * 2);
         output.append(WordCoreUtil.pageType.newPagePrefix);
         this.getSection(sectionPresentationWordMl, output);
         output.append(WordCoreUtil.pageType.newPageSuffix);
         return output;
      }

      /**
       * Predicate to determine if the {@link WordCoreUtil.pageType} is for a portrait page.
       *
       * @return <code>true</code>, when the {@link WordCoreUtil.pageType} has a portrait orientation; otherwise
       * <code>false</code>.
       */

      public boolean isPortrait() {
         return this == WordCoreUtil.pageType.PORTRAIT;
      }

      /**
       * Predicate to determine if the {@link WordCoreUtil.pageType} is for a landscape page.
       *
       * @return <code>true</code>, when the {@link WordCoreUtil.pageType} has a landscape orientation; otherwise
       * <code>false</code>.
       */

      public boolean isLandscape() {
         return this == WordCoreUtil.pageType.LANDSCAPE;
      }

      /**
       * Gets the {@link WordCoreUtil.pageType} from the string. If the string is &quot;landscape&quot; in any case, the
       * page type is {@link WordCoreUtil.pageType#LANDSCAPE}; otherwise, it is {@link WordCoreUtil.pageType#PORTRAIT}.
       *
       * @param value the string value to convert to a {@link WordCoreUtil.pageType}.
       * @return the determined {@link WordCoreUtil.pageType}.
       */

      public static pageType fromString(String value) {
         //@formatter:off
         return
            WordCoreUtil.pageType.LANDSCAPE.name().equalsIgnoreCase(value)
               ? WordCoreUtil.pageType.LANDSCAPE
               : WordCoreUtil.pageType.PORTRAIT;
         //@formatter:on
      }

      /**
       * Gets the default page type.
       *
       * @return {@link WordCoreUtil.pageType#PORTRAIT}.
       */

      public static pageType getDefault() {
         return WordCoreUtil.pageType.PORTRAIT;
      }

   }

   /**
    * Word ML paragraph start
    */

   public static final String PARAGRAPH = "<w:p>";

   /**
    * Word ML paragraph empty
    */

   public static final String PARAGRAPH_EMPTY = "<w:p/>";

   /**
    * Word ML paragraph end
    */

   public static final String PARAGRAPH_END = "</w:p>";

   /**
    * Word ML paragraph presentation
    */

   public static final String PARAGRAPH_PRESENTATION = "<w:pPr>";

   /**
    * Word ML paragraph presentation end
    */

   public static final String PARAGRAPH_PRESENTATION_END = "</w:pPr>";

   /**
    * Word ML paragraph style template
    */

   public static final String PARAGRAPH_STYLE_TEMPLATE_PART_A = "<w:pStyle w:val=\"";

   /**
    * Word ML paragraph style template part B
    */

   public static final String PARAGRAPH_STYLE_TEMPLATE_PART_B = "\"/>";

   /**
    * Word ML paragraph, run, and text start
    */

   public static final String PARAGRAPH_RUN_TEXT = "<w:p><w:r><w:t>";

   /**
    * Word ML paragraph, run, and text with space preserver attribute start
    */

   public static final String PARAGRAPH_RUN_TEXT_SPACE_PRESERVE = "<w:p><w:r><w:t xml:space=\"preserve\">";

   /**
    * Word ML paragraph, run, and text end
    */

   public static final String PARAGRAPH_RUN_TEXT_END = "</w:t></w:r></w:p>";

   //@formatter:off
   private static final String PIC_TAG_DATA =
      "<w:r><w:pict>" +
      "<v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\"" +
      " path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">" + "<v:stroke joinstyle=\"miter\"/>" +
      "<v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/>" +
      "<v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/>" +
      "<v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas>" +
      "<v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>" +
      "<o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype>" +
      "<w:binData w:name=\"wordml://%s\">%s</w:binData>" +
      "<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\">" +
      "<v:imagedata src=\"wordml://%s\" o:title=\"%s\"/></v:shape></w:pict></w:r>";
   // @formatter:on

   /**
    * REFERENCE INTERNAL DOC template used by {@link #changeHyperlinksToReferences} to change a hyperlink into an
    * internal document reference.
    */

   //@formatter:off
   private static final String REFERENCE_INTERNAL_DOC_TEMPLATE_PART_A =
      "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> REF OSEE.";
   //@formatter:on

   /**
    * Part B of the REFERENCE INTERNAL DOC template.
    */

   //@formatter:off
   private static final String REFERENCE_INTERNAL_DOC_TEMPLATE_PART_B =
      " \\h ";
   //@formatter:on

   /**
    * Part C of the REFERENCE INTERNAL DOC template.
    */

   //@formatter:off
   private static final String REFERENCE_INTERNAL_DOC_TEMPLATE_PART_C =
      "</w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Sec#</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r><w:r><w:t> ";
   //@formatter:on

   /**
    * Part D of the REFERENCE INTERNAL DOC template.
    */

   //@formatter:off
   private static final String REFERENCE_INTERNAL_DOC_TEMPLATE_PART_D =
      "</w:t></w:r>";
   //@formatter:on

   /**
    * Size of the REFERENCE INTERNAL DOC template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   private static final int REFERENCE_INTERNAL_DOC_TEMPLATE_SIZE =
        WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_A.length()
      + WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_B.length()
      + WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_C.length()
      + WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_D.length();
   //@formatter:on

   /**
    * {@link Pattern} to test Word ML for the presence of review comments.
    */

   //@formatter:off
   private static final Pattern REVIEW_COMMENT_TEST_PATTERN =
      Pattern.compile
         (
            "<aml:annotation[^>]*w:type=\"Word.Comment.Start\"/?>(</aml:annotation>)?[\\s\\S]+?<aml:annotation[^>]*w:type=\"Word.Comment.End\"/?>(</aml:annotation>)?[\\s\\S]+?</aml:annotation></w:r>"
         );
   //@formatter:on

   /**
    * {@link Pattern} used to remove Word.Comment.Start and Word.Comment.End annotations.
    */

   public static final Pattern REVIEW_COMMENT_REMOVAL_PATTERN =
      Pattern.compile("<aml:annotation[^>]*w:type=\"Word.Comment.(?:Start|End)\"/?>(</aml:annotation>)?");

   /**
    * {@link Pattern} used to remove CommentReferences.
    */

   public static final Pattern REVIEW_COMMENT_REFERENCE_REMOVAL_PATTERN = Pattern.compile(
      "<w:r[^>]*><w:rPr><w:rStyle w:val=\"CommentReference\"/?>(</w:rStyle>)?</w:rPr><aml:annotation[^>]*w:type=\"Word.Comment\"[^>]*><aml:content>[\\s\\S]+?</aml:content></aml:annotation></w:r>");

   /**
    * Pattern to valid an RGB color hex string
    */

   public static final Pattern RGB_HEX_COLOR_PATTERN = Pattern.compile("[0-9A-Fa-f]{6}");

   /**
    * Word ML run presentation with color template
    */

   public static final String RGB_COLOR_RUN_TEMPLATE_PART_A = "<w:rPr><w:color w:val=\"";

   /**
    * Word ML run presentation with color template part B
    */

   public static final String RGB_COLOR_RUN_TEMPLATE_PART_B = "\"/></w:rPr>";

   /**
    * Size of the RGB COLOR RUN template parts for {@link StringBuilder} allocation.
    */

   //@formatter:off
   public static final int RGB_COLOR_RUN_TEMPLATE_SIZE =
        RGB_COLOR_RUN_TEMPLATE_PART_A.length()
      + RGB_COLOR_RUN_TEMPLATE_PART_B.length();
   //@formatter:on

   /**
    * Word ML run
    */

   public static final String RUN = "<w:r>";

   /**
    * Word ML run end
    */

   public static final String RUN_END = "</w:r>";

   /**
    * Word ML run presentation
    */

   public static final String RUN_PRESENTATION = "<w:rPr>";

   /**
    * Word ML complete run presentation for bolded text
    */

   public static final String RUN_PRESENTATION_BOLD = "<w:rPr><w:b/></w:rPr>";

   /**
    * Word ML run presentation end
    */

   public static final String RUN_PRESENTATION_END = "</w:rPr>";

   /**
    * @deprecated Used to search for the start of a table row in {@link WordMLApplicabilityHandler} and
    * {@link BolcApplicabilityOps}.
    */

   @Deprecated
   public static final String START_TABLE_ROW = "<w:tr wsp:rsidR=";

   /**
    * Word ML section
    */

   public static final String SECTION = "<wx:sect>";

   //@formatter:off
   private static final Pattern SECTION_EMPTY_BREAK_REMOVAL_PATTERN =
      Pattern.compile
         (
            "<w:sectPr[^>]*>(<w:type[^>]*>(</w:type>)*)*<w:pgSz[^>]*>(</w:pgSz>)*<w:pgMar[^>]*>(</w:pgMar>)*<w:cols[^>]*>(</w:cols>)*</w:sectPr>"
         );
   //@formatter:on

   /**
    * Word ML section end
    */

   public static final String SECTION_END = "</wx:sect>";

   /**
    * Word ML section presentation
    */

   public static final String SECTION_PRESENTATION = "<w:sectPr>";

   /**
    * Word ML section presentation end
    */

   public static final String SECTION_PRESENTATION_END = "</w:sectPr>";

   @SuppressWarnings("unused")
   private static final String SUB_DOC =
      "<wx:sect><w:p><w:pPr><w:sectPr><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:pPr></w:p><w:subDoc w:link=\"" + WordCoreUtil.FILE_NAME + "\"/></wx:sect><wx:sect><wx:sub-section><w:p><w:pPr><w:pStyle w:val=\"Heading1\"/></w:pPr></w:p><w:sectPr><w:type w:val=\"continuous\"/><w:pgSz w:w=\"12240\" w:h=\"15840\"/><w:pgMar w:top=\"1440\" w:right=\"1800\" w:bottom=\"1440\" w:left=\"1800\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></wx:sub-section></wx:sect>";

   /**
    * Word ML sub-section start
    */

   public static final String SUBSECTION = "<wx:sub-section>";

   /**
    * Word ML sub-section end
    */

   public static final String SUBSECTION_END = "</wx:sub-section>";

   /**
    * Word ML table
    */

   public static final String TABLE = "<w:tbl>";

   /**
    * Word ML table end
    */

   public static final String TABLE_END = "</w:tbl>";

   /**
    * Word ML table column
    */

   public static final String TABLE_COLUMN = "<w:tc>";

   /**
    * Word ML table column end
    */

   public static final String TABLE_COLUMN_END = "</w:tc>";

   /**
    * Enumeration of table presentations.
    */

   public enum tablePresentation {

      /**
       * Word ML for a table presentation with the following:
       * <ul>
       * <li>Table width auto sizing.</li>
       * <li>Single line table borders with auto color.</li>
       * <li>Right justification.</li>
       * </ul>
       */

      //@formatter:off
      WIDTH_AUTO_BORDER_SINGLE_JUSTIFIED_LEFT
         (
            "<w:tblPr>"
               +    "<w:tblW w:w=\"0\" w:type=\"auto\"/>"
               +    "<w:tblBorders>"
               +       "<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +       "<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +       "<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +       "<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +       "<w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +       "<w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>"
               +    "</w:tblBorders>"
               + "</w:tblPr>"
         ),

      /**
       * Word ML for a table presentation with the following:
       * <ul>
       * <li>Table width of 5.694 inches.</li>
       * <li>No borders with auto color.</li>
       * <li>Center justification.</li>
       * </ul>
       */

      WIDTH_5_694_BORDER_NONE_JUSTIFIED_CENTER
         (
            "<w:tblPr>"
               +    "<w:tblW w:w=\"8200\" w:type=\"dxa\"/>"
               +    "<w:jc w:val=\"center\"/>"
               + "</w:tblPr>"
         );

      /**
       * Save the table presentation Word Ml for the enumeration member.
       */

      private final String tablePresentationWordMl;

      /**
       * Creates a new enumeration member.
       *
       * @param tablePresentationWordMl the Word Ml for the enumeration member's table presentation.
       */

      private tablePresentation(String tablePresentationWordMl) {
         this.tablePresentationWordMl = tablePresentationWordMl;
      }

      /**
       * Gets the enumeration member's table presentation Word ML.
       *
       * @return the enumeration member's table presentation Word ML.
       */

      public String get() {
         return this.tablePresentationWordMl;
      }
   }

   /**
    * Word ML table row
    */

   public static final String TABLE_ROW = "<w:tr>";

   /**
    * Word ML table row end
    */

   public static final String TABLE_ROW_END = "</w:tr>";

   /**
    * Used by the predicate {@link #containsList} to search a WORDML string for a list.
    *
    * @implNote TODO: Why so specific? Would the presence of a list presentation tag &ltw:listPr&gt; be sufficient?
    */

   //@formatter:off
   private static final Pattern TEST_FOR_LIST_PATTERN =
      Pattern.compile
         (
           "<w:listPr>(<w:ilvl([^>]*?)/?>(</w:ilvl>)?)?(<w:ilfo([^>]*?)/?>(</w:ilfo>)?)?<wx:t wx:val=\"([^>]*?)\"/?>(</wx:t>)?<wx:font wx:val=\"[^\"]*?\"/?>(</wx:font>)?</w:listPr>"
         );
   //@formatter:on

   /**
    * WordML text
    */

   public static final String TEXT = "<w:t>";

   /**
    * WordML text start with space preserver attribute
    */

   public static final String TEXT_SPACE_PRESERVE = "<w:t xml:space=\"preserve\">";

   /**
    * WordML text end
    */

   public static final String TEXT_END = "</w:t>";

   /**
    * {@link Pattern} used to search for w:delText open tags, and any w:type attribute with a value of:
    * <ul>
    * <li>Word.Insertion,</li>
    * <li>Word.Formatting, or</li>
    * <li>Word.Deletion.</li>
    * </ul>
    */

   /**
    * {@link TokenMatcherPattern} used to produce {@link TokenMatcher} objects for finding figure and table captions in
    * Word ML and setting the chapter numbers.
    */

   //@formatter:off
   private static final TokenPattern TOKEN_PATTERN_ADD_CHAPTER_NUM_TO_CAPTION_AND_BOOKMARK =
      TokenPattern.compile
         (
            /* prefix/suffix required */
            true,

            /* prefix (reversed) */
            " \"=rtsni:w elpmiSdlf:w<(.*?)(>[^<]*(?<=[ >]p:w)<)",

            /* core token */
            "SEQ (Figure|Table)",

            /* suffix */
            " \\\\\\* ARABIC \">.*?</w:fldSimple>(.*?)(</w:p[^>]*?>)"
         );
   //@formatter:on

   /**
    * {@link TokenMatcherPattern} used to produce {@link TokenMatcher} objects for finding hyperlinks in Word ML and
    * replacing them with references.
    */

   //@formatter:off
   private static final TokenPattern TOKEN_PATTERN_CHANGE_HYPERLINKS_TO_REFERENCES =
      TokenPattern.compile
         (
            /* prefix/suffix required */
           true,

           /* prefix (reversed) */
           "\\s*>[^<]*(?<=r:w)<\\s*>[^<]*(?<=r:w/)<\\s*>/\"nigeb\"\\s*=\\s*epyTrahCdlf:w\\s*rahCdlf:w<\\s*>[^<]*(?<=r:w)<",

           /* core token */
           "<w:instrText>\\s+HYPERLINK[^\"&]*(?:\"|&quot;)OSEE\\.([^\"&]*)(?:\"|&quot;)\\s+</w:instrText>",

           /* suffix */
           ".*?<w:fldChar\\s*w:fldCharType\\s*=\\s*\"separate\"\\s*/>.*?<w:rStyle\\s*w:val\\s*=\\s*\"Hyperlink\"\\s*/>(.*?)<w:fldChar\\s*w:fldCharType\\s*=\\s*\"end\"\\s*/>\\s*</w:r>"
         );
   //@formatter:off

   /**
    * {@link TokenMatcherPattern} used to produce {@link TokenMatcher} objects for finding the "insert thing here"
    * tokens in a publishing template.
    */

   //@formatter:off
   private static final TokenPattern TOKEN_PATTERN_PROCESS_PUBLISHING_TEMPLATE =
      TokenPattern.compile
         (
           /* prefix/suffix not required */
           false,

           /* prefix  (reversed) */
           "\\s*>[^<]*(?<=t:w)<\\s*>[^<]*(?<=r:w)<(?:\\s*>[^<]*(?<=rPp:w/)<.*?>[^<]*(?<=rPp:w)<)?\\s*>[^<]*(?<=p:w)<",

           /* core token */
           "INSERT_(ARTIFACT|LINK)_HERE",

           /* suffix */
           "\\s*</w:t>\\s*</w:r>\\s*</w:p>"
         );
   //@formatter:on

   //@formatter:off
   private static final Pattern WORD_ANNOTATIONS_REMOVAL_PATTERN =
      Pattern.compile
         (
            "(?:<w:delText>|w:type\\s*=\\s*\"Word\\.(?:Insertion|Formatting|Deletion)\")"
         );
   //@formatter:on

   public static final String WORD_ML_TAGS = "(\\<[^>]*?>){0," + MAX_TAG_OCCURENCE + "}";

   public static final String END = "E" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "d ?" + WORD_ML_TAGS + " ?";

   public static final String ELSE = "E" + WORD_ML_TAGS + "l" + WORD_ML_TAGS + "s" + WORD_ML_TAGS + "e ?";

   public static final String FEATURE =
      "F" + WORD_ML_TAGS + "e" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "e";

   public static final String CONFIG =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n";

   public static final String CONFIGGRP =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "G" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "p";

   public static final String NOT = "N" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "t";

   public static final String ENDBRACKETS = WORD_ML_TAGS + " ?(\\[(.*?)\\]) ?";
   public static final String OPTIONAL_ENDBRACKETS = " ?(" + WORD_ML_TAGS + "(\\[.*?\\]))?";
   public static final String BEGINFEATURE = FEATURE + WORD_ML_TAGS + " ?" + ENDBRACKETS;
   public static final String ENDFEATURE = END + WORD_ML_TAGS + FEATURE + OPTIONAL_ENDBRACKETS;
   public static final String BEGINCONFIG =
      CONFIG + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static final String ENDCONFIG = END + WORD_ML_TAGS + CONFIG + OPTIONAL_ENDBRACKETS;

   public static final String BEGINCONFIGGRP =
      CONFIGGRP + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static final String ENDCONFIGGRP = END + WORD_ML_TAGS + CONFIGGRP + OPTIONAL_ENDBRACKETS;
   public static final String ELSE_EXP =
      "(" + FEATURE + "|" + CONFIGGRP + "|" + CONFIG + ")" + WORD_ML_TAGS + " " + WORD_ML_TAGS + ELSE;

   public static final Pattern ELSE_PATTERN = Pattern.compile(ELSE_EXP, Pattern.DOTALL | Pattern.MULTILINE);

   public static final String BIN_DATA_STRING = "<w:binData.*?w:name=\"(.*?)\".*?</w:binData>";
   public static final Pattern BIN_DATA_PATTERN =
      Pattern.compile(BIN_DATA_STRING, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static final Pattern IMG_SRC_PATTERN =
      Pattern.compile("<v:imagedata.*?src=\"([^\"]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

   //@formatter:off
   public static final Pattern FULL_PATTERN =
      Pattern.compile
         (
               "(" + BEGINFEATURE   + ")"
            + "|(" + ENDFEATURE     + ")"
            + "|(" + BEGINCONFIGGRP + ")"
            + "|(" + ENDCONFIGGRP   + ")"
            + "|(" + BEGINCONFIG    + ")"
            + "|(" + ENDCONFIG      + ")",

            Pattern.DOTALL | Pattern.MULTILINE
         );
   //@formatter:on

   /**
    * The following static numbers are derived from the FULL_PATTERN compiled above
    */

   public final static int beginFeatureMatcherGroup = 1;
   public final static int beginConfigGroupMatcherGroup = 26;
   public final static int beginConfigMatcherGroup = 78;
   public final static int endFeatureMatcherGroup = 12;
   public final static int endConfigGroupMatcherGroup = 53;
   public final static int endConfigMatcherGroup = 100;
   public final static int endFeatureBracketMatcherGroup = 23;
   public final static int endConfigGroupBracketMatcherGroup = 75;
   public final static int endConfigBracketMatcherGroup = 117;

   /**
    * Searches the provided Word ML for table or figure captions, removes them, and replaces with a new caption
    * containing the provided book marks.
    *
    * @param input the Word ML to be processed.
    * @param amlBookmarkStart Word ML for the bookmark starting tag.
    * @param amlBookmarkEnd Word ML for the bookmark ending tag.
    * @return the provided Word ML with updated table and figure captions.
    */

   public static CharSequence addChapterNumToCaptionAndBookmark(CharSequence input, CharSequence amlBookmarkStart, CharSequence amlBookmarkEnd) {

      //@formatter:off
      var replacerText =
         WordCoreUtil.replacer
            (
               input,
               WordCoreUtil.TOKEN_PATTERN_ADD_CHAPTER_NUM_TO_CAPTION_AND_BOOKMARK,
               ( tokenMatcher ) ->
               {
                  //Prefix Groups
                  var paraStart = tokenMatcher.prefixGroupCharSequence(2);
                  var preStyleRefTags = tokenMatcher.prefixGroupCharSequence(1); // Normally contains figure or table text

                  //Core Token Groups
                  var seqType = tokenMatcher.coreTokenGroupCharSequence(1); // Figure or Table

                  //Suffix Groups
                  var captionText = tokenMatcher.suffixGroupCharSequence(1);
                  var paraEnd = tokenMatcher.suffixGroupCharSequence(2);

                  //Decode and remove XML tags from captionText
                  captionText = XmlEncoderDecoder.xmlToText(captionText, XmlEncoderDecoder.REMOVE_TAGS);

                  //@formatter:off
                  var newCaption =
                     WordCoreUtil.getCaption
                        (
                           paraStart,
                           amlBookmarkStart,
                           preStyleRefTags,
                           seqType,
                           amlBookmarkEnd,
                           captionText,
                           paraEnd
                        );

                  return newCaption;
               }
            );
      //@formatter:on

      return replacerText;
   }

   public static boolean areApplicabilityTagsInvalid(String wordml, BranchId branch, HashCollection<String, String> validFeatureValues, Set<String> allValidConfigurations, Set<String> allValidConfigurationGroups) {

      Matcher matcher = FULL_PATTERN.matcher(wordml);
      Stack<ApplicabilityBlock> applicabilityBlocks = new Stack<>();
      int applicBlockCount = 0;

      while (matcher.find()) {
         String beginFeature = matcher.group(beginFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginFeatureMatcherGroup)) : null;
         String beginConfiguration = matcher.group(beginConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigMatcherGroup)) : null;
         String beginConfigurationGroup = matcher.group(beginConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigGroupMatcherGroup)) : null;

         String endFeature = matcher.group(endFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endFeatureMatcherGroup)) : null;
         String endConfiguration = matcher.group(endConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigMatcherGroup)) : null;
         String endConfigurationGroup = matcher.group(endConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigGroupMatcherGroup)) : null;

         if (beginFeature != null && beginFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount += 1;
            applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Feature, beginFeature));
         } else if (beginConfiguration != null && beginConfiguration.toLowerCase().contains(CONFIGAPP)) {
            if (isValidConfigurationBracket(beginConfiguration, allValidConfigurations)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Configuration, beginConfiguration));
            }
         } else if (beginConfigurationGroup != null && beginConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            if (isValidConfigurationGroupBracket(beginConfigurationGroup, allValidConfigurationGroups)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(
                  createApplicabilityBlock(ApplicabilityType.ConfigurationGroup, beginConfigurationGroup));
            }
         } else if (endFeature != null && endFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount -= 1;

            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidFeatureBlock(applicabilityBlocks.pop(), matcher, branch, validFeatureValues)) {
               return true;
            }

         } else if (endConfiguration != null && endConfiguration.toLowerCase().contains(CONFIGAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         } else if (endConfigurationGroup != null && endConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationGroupBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         }
      }

      if (applicBlockCount != 0) {
         return true;
      }

      return false;
   }

   /**
    * Searches for hyperlinks in a text block of Word ML and replaces them with Word ML references generated with
    * {@link #getWordMlReference}.
    *
    * @param data the Word ML to be processed
    * @param headerGuids {@link Set} of the GUIDs of artifacts that are headings
    * @return the updated Word ML
    */

   public static CharSequence changeHyperlinksToReferences(CharSequence input, Set<String> headerGuids) {

      //@formatter:off
      var replacerText =
         WordCoreUtil.replacer
            (
               input,
               WordCoreUtil.TOKEN_PATTERN_CHANGE_HYPERLINKS_TO_REFERENCES,
               ( tokenMatcher ) ->
               {
                  var identifier = tokenMatcher.coreTokenGroupCharSequence(1);
                  var text = tokenMatcher.suffixGroupCharSequence(1);

                  var regularText = XmlEncoderDecoder.xmlToText(text, XmlEncoderDecoder.REMOVE_TAGS);
                  var xmlText = XmlEncoderDecoder.textToXml(regularText);
                  var isHeader = headerGuids.contains(identifier);

                  var newText = WordCoreUtil.getWordMlReference( identifier, isHeader, xmlText );

                  return newText;
               }
            );
      //@formatter:on

      return replacerText;
   }

   /**
    * Search the <code>input</code> Word ML for matches of the <code>pattern</code> and removes them from the returned
    * Word ML.
    *
    * @param input the Word ML to be processed.
    * @param pattern the {@link Pattern} to be searched for.
    * @return the provided Word ML with matches of the {@Link Pattern} removed.
    */

   private static CharSequence cleaner(CharSequence input, Pattern pattern) {

      var matcher = pattern.matcher(input);
      var changeSet = new ChangeSet(input);

      while (matcher.find()) {

         var start = matcher.start();
         var end = matcher.end();

         changeSet.delete(start, end);
      }

      if (!changeSet.hasChanges()) {
         return input;
      }

      try (var stringWriter = new StringWriter(input.length())) {
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         return input;
      }

   }

   /**
    * Searches the <code>input</code> Word ML for matches of each {@link Pattern} in <code>patterns</code> and removes
    * the matches from the returned Word ML. Each iteration applies a {@link Pattern} and removes the matches from the
    * input {@link CharSequence} before proceeding with the next iteration.
    *
    * @param input the Word ML to be processed.
    * @param patterns the {@link Pattern}s to be searched for in the Word ML.
    * @return the proved Word ML with matches of all the {@link Pattern}s removed.
    */

   private static CharSequence cleaner(CharSequence input, Pattern... patterns) {
      var output = input;
      for (var pattern : patterns) {
         output = WordCoreUtil.cleaner(output, pattern);
      }
      return output;
   }

   /**
    * Removes footers from the Word ML and replaces page size tags.
    *
    * @param input the Word ML to be processed.
    * @return the Word ML with footers removed and page size tags replaced.
    */

   public static CharSequence cleanupFooter(CharSequence input) {

      var noFooterText = WordCoreUtil.cleaner(input, WordCoreUtil.FOOTER_REMOVAL_PATTERN);

      //@formatter:off
      var newFooterText =
         WordCoreUtil.replacer
            (
               noFooterText,
               WordCoreUtil.PAGE_SIZE_REMOVAL_PATTERN,
               ( matcher ) ->
               {
                  return WordCoreUtil.FOOTER_PAGE_SIZE;
               }
            );
      //@formatter:on

      return newFooterText;
   }

   /**
    * Removes all w:pgNumType tags with an attribute of w:start = 1.
    *
    * @param input the Word ML to be processed.
    * @return the Word ML with page number tags with an starting attribute of 1 removed.
    */

   public static CharSequence cleanupPageNumberTypeStart1(CharSequence input) {

      var noPageText = WordCoreUtil.cleaner(input, WordCoreUtil.PAGE_NUMBER_TYPE_START_1_REMOVAL_PATTERN);

      return noPageText;
   }

   private static boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Predicate to determine if the provided WORD ML contains a list structure.
    *
    * @param input the WORD ML to be searched.
    * @return <code>true</code> when the provide WORD ML contains a list; otherwise, <code>false</code>.
    */

   public static boolean containsLists(CharSequence input) {

      return WordCoreUtil.TEST_FOR_LIST_PATTERN.matcher(input).find();
   }

   /**
    * Searches for w:delText tags and any w:type attributes with a value of:
    * <ul>
    * <li>Word.Insertion,</li>
    * <li>Word.Formatting, or</li>
    * <li>Word.Deletion.</li>
    * </ul>
    *
    * @param input the Word ML to be searched.
    * @return <code>true</code>, when the {@link Pattern} {@link #WORD_ANNOTATIONS_REMOVAL_PATTERN} matches the
    * <code>input</code>; otherwise, <code>false</code>.
    */

   public static boolean containsWordAnnotations(CharSequence input) {

      return WordCoreUtil.WORD_ANNOTATIONS_REMOVAL_PATTERN.matcher(input).find();
   }

   private static ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }

   /**
    * Generates the WordML for an appendix with the specified letter.
    *
    * @param appendixLetter the letter for the appendix. Maybe capital or lowercase a-z.
    * @return a {@link StringBuilder} with the WordML for an appendix with the specified letter.
    */

   private static StringBuilder getAppendixStart(char appendixLetter) {

      var appendixNumber = Character.toLowerCase(appendixLetter) - 'a' + 1;
      var size = WordCoreUtil.APPENDIX_START_LETTER_TEMPLATE_SIZE + 1;
      //@formatter:off
      var output = new StringBuilder( size )
                          .append( WordCoreUtil.APPENDIX_START_LETTER_TEMPLATE_PART_A )
                          .append( appendixNumber )
                          .append( WordCoreUtil.APPENDIX_START_LETTER_TEMPLATE_PART_B )
                          ;
      //@formatter:on
      return output;
   }

   /**
    * Generates the WordML for a figure or table caption.
    *
    * @param paragraphStart the Word ML paragraph tag to start the caption.
    * @param amlBookmarkStart the Word ML for the starting aml bookmark tags.
    * @param preStyleRefTags the Word ML for the paragraph style tags.
    * @param sequenceType the caption type as "Figure" or "Table"
    * @param amlBookmarkEnd the Word ML for the ending aml bookmark tags.
    * @param captionText the caption text.
    * @param paragraphEnd the Word ML paragraph end tag for the caption.
    * @return a {@link StringBuilder} with the WordML for the bookmark.
    */

   //@formatter:off
   private static StringBuilder
      getCaption
         (
            CharSequence paragraphStart,
            CharSequence amlBookmarkStart,
            CharSequence preStyleRefTags,
            CharSequence sequenceType,
            CharSequence amlBookmarkEnd,
            CharSequence captionText,
            CharSequence paragraphEnd
         ) {

      var xmlEscapedCaptionText = XmlEncoderDecoder.textToXml(captionText);

      var size =
           WordCoreUtil.CAPTION_TEMPLATE_SIZE
         + paragraphStart.length()
         + amlBookmarkStart.length()
         + preStyleRefTags.length()
         + sequenceType.length()
         + amlBookmarkEnd.length()
         + xmlEscapedCaptionText.length()
         + paragraphEnd.length();

      var output = new StringBuilder( 2 * size )
                          .append( paragraphStart )
                          .append( amlBookmarkStart )
                          .append( preStyleRefTags )
                          .append( WordCoreUtil.CAPTION_TEMPLATE_PART_A )
                          .append( sequenceType )
                          .append( WordCoreUtil.CAPTION_TEMPLATE_PART_B )
                          .append( amlBookmarkEnd )
                          .append( WordCoreUtil.CAPTION_TEMPLATE_PART_C )
                          .append( xmlEscapedCaptionText )
                          .append( WordCoreUtil.CAPTION_TEMPLATE_PART_D )
                          .append( paragraphEnd )
                          ;

      return output;
   }
   //@formatter:on

   private static String getEditImage(boolean isStart, String guid) {
      String imageId = String.format("%s_%s", guid, isStart ? "START.jpg" : "END.jpg");
      String imageData = isStart ? BIN_DATA : BIN_DATA_END;
      return String.format(PIC_TAG_DATA, imageId, imageData, imageId, guid);
   }

   public static String getEndEditImage(String guid) {
      return WordCoreUtil.getEditImage(false, guid);
   }

   /**
    * Gets the Word ML for a paragraph with a hyperlink to a file.
    *
    * @param filename the name of the file for the hyperlink.
    * @return a {@link StringBuilder} with the WordML for the hyperlink.
    * @throws IllegalArgumentException when the filename is <code>null</code> or empty.
    */

   public static StringBuilder getHyperlinkDocument(CharSequence filename) {

      if (Objects.isNull(filename) || filename.length() == 0) {
         throw new IllegalArgumentException("The file name can not be null or empty.");
      }

      var size = WordCoreUtil.HYPERLINK_DOCUMENT_TEMPLATE_SIZE + filename.length();

      //@formatter:off
      var output = new StringBuilder( size * 2 )
                          .append( WordCoreUtil.HYPERLINK_DOCUMENT_TEMPLATE_PART_A )
                          .append( filename )
                          .append( WordCoreUtil.HYPERLINK_DOCUMENT_TEMPLATE_PART_B )
                          .append( filename )
                          .append( WordCoreUtil.HYPERLINK_DOCUMENT_TEMPLATE_PART_C )
                          ;
      //@formatter:on

      return output;
   }

   /**
    * Gets the {@link PublishingTemplateInsertTokenType} of the first "insert here token" in the publishing template
    * content.
    *
    * @param templateContent the publishing template to be searched.
    * @return
    * <ul>
    * <li>{@link PublishingTemplateInsertTokenType#ARTIFACT} when the first "insert here token" is for an artifact.</li>
    * <li>{@link PublishingTemplateInsertTokenType#LINK} when the first "insert here token" is for a link.</li>
    * <li>{@link PublishingTemplateInsertTokenType#NONE} when a valid "insert here token" is not found.</li>
    * </ul>
    */

   public static PublishingTemplateInsertTokenType getInsertHereTokenType(CharSequence templateContent) {

      var matcher = WordCoreUtil.INSERT_HERE_TEST_PATTERN.matcher(templateContent);

      if (matcher.find()) {

         return PublishingTemplateInsertTokenType.parse(matcher.group(1));
      }

      return PublishingTemplateInsertTokenType.NONE;
   }

   /**
    * Generates the WordML for a link. When the <code>destLinkType</code> is {@link LinkType#OSEE_SERVER_LINK} the
    * template WORDML LINK template is used; otherwise, the WORDML INTERNAL DOC LINK template is used.
    *
    * @param destLinkType the type of link to create.
    * @param linkId the link destination identifier.
    * @param linkText the text to display for the link.
    * @return a {@link StringBuilder} with the WordML for the link.
    */

   public static StringBuilder getLink(LinkType destLinkType, CharSequence linkId, CharSequence linkText) {

      var parametersSize = linkId.length() + linkText.length();

      //@formatter:off
      var output = LinkType.OSEE_SERVER_LINK.equals(destLinkType)
                      ? new StringBuilder( ( WordCoreUtil.LINK_TEMPLATE_SIZE + parametersSize ) * 2 )
                               .append( WordCoreUtil.LINK_TEMPLATE_PART_A )
                               .append( linkId )
                               .append( WordCoreUtil.LINK_TEMPLATE_PART_B )
                               .append( linkText )
                               .append( WordCoreUtil.LINK_TEMPLATE_PART_C )
                      : new StringBuilder( ( WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_SIZE + parametersSize ) * 2 )
                               .append( WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_A )
                               .append( linkId )
                               .append( WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_B )
                               .append( linkText )
                               .append( WordCoreUtil.LINK_INTERNAL_DOC_TEMPLATE_PART_C );
      //@formatter:on
      return output;
   }

   /**
    * Generates an OSEE link marker with the specified identifier.
    *
    * @param guid the link marker identifier.
    * @return a {@link StringBuilder} with the OSEE link marker.
    */

   public static StringBuilder getOseeLinkMarker(CharSequence guid) {

      var size = WordCoreUtil.OSEE_LINK_MARKER_TEMPLATE_SIZE + guid.length();

      //@formatter:off
      var output = new StringBuilder( size * 2 )
                          .append( WordCoreUtil.OSEE_LINK_MARKER_TEMPLATE_PART_A )
                          .append( guid )
                          .append( WordCoreUtil.OSEE_LINK_MARKER_TEMPLATE_PART_B )
                          ;
      //@formatter:on
      return output;
   }

   /**
    * Gets a Word ML run presentation with an RGB hex color setting.
    *
    * @param rgbHexColor 6 digit RGB hex string.
    * @return a {@link StringBuilder} containing the Word ML run presentation.
    * @throws IllegalArgumentException when the parameter <code>rgbHexColor</code> is not a 6 character string composed
    * of the characters A-F, a-f, or 0-9.
    */

   public static StringBuilder getRunPresentationWithRgbHexColor(CharSequence rgbHexColor) {

      if (Objects.isNull(rgbHexColor)) {
         throw new IllegalArgumentException("rgbHexColor can not be null.");
      }

      if ((rgbHexColor.length() != 6) || (!WordCoreUtil.RGB_HEX_COLOR_PATTERN.matcher(rgbHexColor).lookingAt())) {
         throw new IllegalArgumentException("rgbHexColor should be a hex string 6 characters long.");
      }

      var size = WordCoreUtil.RGB_COLOR_RUN_TEMPLATE_SIZE + 6;

      //@formatter:off
      var output = new StringBuilder( size * 2 )
                          .append( WordCoreUtil.RGB_COLOR_RUN_TEMPLATE_PART_A )
                          .append( rgbHexColor )
                          .append( WordCoreUtil.RGB_COLOR_RUN_TEMPLATE_PART_B )
                          ;
      //@formatter:on
      return output;
   }

   public static StringBuilder getUnknownArtifactLink(CharSequence guid, BranchId branch) {
      //@formatter:off
      var message      = String.format( "Invalid Link: artifact with guid:[%s] on branchUuid:[%s] does not exist", guid, branch );
      var internalLink = String.format( "http://none/%s?guid=%s&amp;branchUuid=%s", "unknown", guid, branch);
      var artifactLink = WordCoreUtil.getLink( LinkType.OSEE_SERVER_LINK, internalLink, message );

      return artifactLink;
      //@formatter:on
   }

   public static String getStartEditImage(String guid) {
      return WordCoreUtil.getEditImage(true, guid);
   }

   /**
    * Generates the WordML for a bookmark.
    *
    * @param uuid the bookmark identifier.
    * @return a {@link StringBuilder} with the WordML for the bookmark.
    */

   public static StringBuilder getWordMlBookmark(Long uuid) {
      var size = WordCoreUtil.BOOKMARK_TEMPLATE_SIZE + 1 + 19 + 1;

      //@formatter:off
      var output = new StringBuilder( size * 2 )
                          .append( WordCoreUtil.BOOKMARK_TEMPLATE_PART_A )
                          .append( "0" )
                          .append( WordCoreUtil.BOOKMARK_TEMPLATE_PART_B )
                          .append( uuid )
                          .append( WordCoreUtil.BOOKMARK_TEMPLATE_PART_C )
                          .append( "0" )
                          .append( WordCoreUtil.BOOKMARK_TEMPLATE_PART_D )
                          ;
      //@formatter:on

      return output;
   }

   /**
    * Generates the replacement WordML for a document reference.
    *
    * @param linkId the link identifier
    * @param addParagraphNumber
    * @param linkText the text to be displayed for the reference.
    * @return a {@link StringBuilder} with the Word ML reference.
    */

   //@formatter:off
   private static StringBuilder getWordMlReference(CharSequence linkId, boolean addParagraphNumber, CharSequence linkText) {

      var paragraphNumSwitch = addParagraphNumber ? "\\n " : "";
      var xmlEscapedLinkText = XmlEncoderDecoder.textToXml(linkText);

      var size =
         WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_SIZE
         + linkId.length()
         + paragraphNumSwitch.length()
         + xmlEscapedLinkText.length();

      var output = new StringBuilder( size * 2 )
                          .append( WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_A )
                          .append( linkId )
                          .append( WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_B )
                          .append( paragraphNumSwitch )
                          .append( WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_C )
                          .append( xmlEscapedLinkText )
                          .append( WordCoreUtil.REFERENCE_INTERNAL_DOC_TEMPLATE_PART_D )
                          ;

      return output;
   }
   //@formatter:on

   /**
    * Sets the initial list sequence numbers in the list definitions for each heading level according to the
    * <code>outlineType</code> as follows:
    * <dl>
    * <dt>"APPENDIX"</dt>
    * <dd>The initial list sequence number for the list definition for style "APPENDIX1" is set to the value specified
    * by the string <code>outlineNumber</code>.</dd>
    * <dt>(other)</dt>
    * <dd>The string <code>outlineNumber</code> is split into an array of sub-strings using '.' as the delimiter. Each
    * sub-string should be the string representation of a non-negative integer. The value at an index of the sub-string
    * array is the initial list sequence number for the word heading level that is index plus one. The sub-string array
    * length determines the number of list definitions that will be updated in the publishing template. If the number of
    * outline levels exceeds {@link WordCoreUtil#OUTLINE_LEVEL_MAXIMUM}, the number of list definitions updated will be
    * limited to the maximum.</dd>
    * <dt>(null)</dt>
    * <dd>No action and the input <code>template</code> is returned</dd>
    * </dl>
    *
    * @param outlineNumber the paragraph number sequence or appendix number to initialize the publishing template with.
    * @param template the publishing template.
    * @param outlineType when "APPENDIX", the "APPENDIX1" style is updated; otherwise, "Heading" styles are updated.
    * @return a {@link CharSequence} of the updated publishing template.
    */

   public static CharSequence initializePublishingTemplateOutliningNumbers(String outlineNumber, CharSequence template, String outlineType) {

      if (Objects.isNull(outlineNumber)) {
         return template;
      }

      boolean appendixOutlineType = Objects.nonNull(outlineType) && outlineType.equalsIgnoreCase("APPENDIX");

      if (appendixOutlineType) {

         // Example of appendix number: A.0
         char[] chars = outlineNumber.toCharArray();
         template = WordCoreUtil.setAppendixStartLetter(chars[0], template);

         return template;
      }

      String[] outlineNumbers = outlineNumber.split("\\.");

      if (outlineNumbers.length > WordCoreUtil.OUTLINE_LEVEL_MAXIMUM) {
         outlineNumbers = Arrays.copyOf(outlineNumbers, WordCoreUtil.OUTLINE_LEVEL_MAXIMUM);
      }

      template = WordCoreUtil.setHeadingNumbers(outlineNumbers, template);

      return template;
   }

   public static boolean isExpressionInvalid(String expression, BranchId branch, HashCollection<String, String> validFeatureValues) {
      ApplicabilityGrammarLexer lex = new ApplicabilityGrammarLexer(new ANTLRStringStream(expression.toUpperCase()));
      ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

      try {
         parser.start();
      } catch (RecognitionException ex) {
         return true;
      }

      HashMap<String, List<String>> featureIdValuesMap = parser.getIdValuesMap();

      if (featureIdValuesMap.isEmpty()) {
         return true;
      }

      for (String featureId : featureIdValuesMap.keySet()) {
         featureId = featureId.trim();
         if (validFeatureValues.containsKey(featureId.toUpperCase())) {
            List<String> values = featureIdValuesMap.get(featureId);
            if (values.contains("Default")) {
               continue;
            }
            Collection<String> validValues = validFeatureValues.getValues(featureId.toUpperCase());
            for (String val : values) {
               val = val.trim();
               if (val.equals("(") || val.equals(")") || val.equals("|") || val.equals("&")) {
                  continue;
               }
               if (!containsIgnoreCase(validValues, val)) {
                  return true;
               }
            }
         } else {
            return true;
         }
      }

      return false;
   }

   private static boolean isInvalidConfigurationBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.Configuration) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidConfigurationGroupBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.ConfigurationGroup) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidFeatureBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher, BranchId branch, HashCollection<String, String> validFeatureValues) {

      if (applicabilityBlock.getType() != ApplicabilityType.Feature) {
         return true;
      }
      String applicabilityExpression = applicabilityBlock.getApplicabilityExpression();

      if (isExpressionInvalid(applicabilityExpression, branch, validFeatureValues)) {
         return true;
      }

      return false;
   }

   private static boolean isValidConfigurationBracket(String beginConfig, Set<String> allValidConfigurations) {
      beginConfig = WordCoreUtil.textOnly(beginConfig);
      int start = beginConfig.indexOf("[") + 1;
      int end = beginConfig.indexOf("]");
      String applicExpText = beginConfig.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurations.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isValidConfigurationGroupBracket(String beginConfigGroup, Set<String> allValidConfigurationGroups) {
      beginConfigGroup = WordCoreUtil.textOnly(beginConfigGroup);
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurationGroups.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   /**
    * Applies the <code>segmentProcessor</code> to each section of the <code>templateContent</code> from the start of
    * <code>templateContent</code> or the end of the last section. The <code>tailProcessor</code> is applied to the last
    * section of the <code>templateContent</code>. The <code>templateContent</code> is split into sections using a
    * {@link WordCoreUtil#TokenMatcher} produced by the {@link WordCoreUtil#tokenMatcherPattern}.
    *
    * @param templateContent the publishing template to be processed.
    * @param segmentProcessor a {@link Consumer} used to process sections of the <code>templateContent</code> leading up
    * to an "insert here token".
    * @param tailProcessor a {@link Consumer} used to process the final section of the <code>templateContent</code>.
    */

   public static void processPublishingTemplate(CharSequence templateContent, Consumer<CharSequence> segmentProcessor, Consumer<CharSequence> tailProcessor) {

      var tokenMatcher = WordCoreUtil.TOKEN_PATTERN_PROCESS_PUBLISHING_TEMPLATE.tokenMatcher(templateContent);
      int lastEndIndex = 0;

      while (tokenMatcher.find()) {

         var tokenStart = tokenMatcher.start();
         var tokenEnd = tokenMatcher.end();

         var segment = templateContent.subSequence(lastEndIndex, tokenStart);

         lastEndIndex = tokenEnd;
         segmentProcessor.accept(segment);
      }

      var tail = templateContent.subSequence(lastEndIndex, templateContent.length());
      tailProcessor.accept(tail);
   }

   /**
    * Removes aml:annotation, aml:content, w:delText tags; as well as everything between the w:delText open and close
    * tags.
    *
    * @param input the Word ML to be processed.
    * @return the Word ML with annotations and deleted text removed.
    */

   public static CharSequence removeAnnotations(CharSequence input) {

      var newText = WordCoreUtil.cleaner(input, WordCoreUtil.ANNOTATIONS_REMOVAL_PATTERN);

      return newText;
   }

   /**
    * Removes empty lists.
    *
    * @param input the Word ML to be processed.
    * @return the Word ML with empty lists removed.
    */

   public static CharSequence removeEmptyLists(CharSequence input) {

      var newText = WordCoreUtil.cleaner(input, WordCoreUtil.LIST_EMPTY_REMOVAL_PATTERN);

      return newText;
   }

   /**
    * Removes Footers wrapped in a paragraph, Footers wrapped in a section presentation, and data rights no artifact
    * found statements from the provided Word ML.
    *
    * @param input the Word ML to process.
    * @return the Word ML with footers and no data rights statements removed.
    */

   public static CharSequence removeFootersAndNoDataRightsStatements(CharSequence input) {

      //@formatter:off
      var newText =
         WordCoreUtil.cleaner
            (
               input,
               WordCoreUtil.FOOTER_ENTIRE_EXTRA_PARA_REMOVAL_PATTERN,
               WordCoreUtil.FOOTER_ENTIRE_REMOVAL_PATTERN,
               WordCoreUtil.DATA_RIGHTS_NO_ARTIFACT_FOUND_REMOVAL_PATTERN
            );
      //@formatter:on
      return newText;
   }

   /**
    * Removes review comments from the Word ML.
    *
    * @param input the Word ML to be processed.
    * @return the Word ML with review comments removed.
    */

   public static CharSequence removeReviewComments(CharSequence input) {

      var reviewCommentMatcher = WordCoreUtil.REVIEW_COMMENT_TEST_PATTERN.matcher(input);

      if (!reviewCommentMatcher.find()) {
         return input;
      }

      var noCommentStartOrEndText = WordCoreUtil.cleaner(input, WordCoreUtil.REVIEW_COMMENT_REMOVAL_PATTERN);
      var noReviewCommentText =
         WordCoreUtil.cleaner(noCommentStartOrEndText, WordCoreUtil.REVIEW_COMMENT_REFERENCE_REMOVAL_PATTERN);

      return noReviewCommentText;
   }

   /**
    * All matches of the <code>pattern</code> are replaced with the values produced by the <code>newTextFunction</code>.
    *
    * @param input the WordML to be processed.
    * @param pattern the {@link Pattern} to search with.
    * @param newTextFunction a function to generate the replacement
    * @return a {@link CharSequence} with the replacements made. When no replacements are made the provided
    * {@link CharSequence}, <code>input</code>, is returned.
    */

   private static CharSequence replacer(CharSequence input, Pattern pattern, Function<Matcher, StringBuilder> newTextFunction) {

      var matcher = pattern.matcher(input);
      var changeSet = new ChangeSet(input);

      while (matcher.find()) {

         var tokenStart = matcher.start();
         var tokenEnd = matcher.end();

         var newText = newTextFunction.apply(matcher);

         changeSet.replace(tokenStart, tokenEnd, newText);
      }

      if (!changeSet.hasChanges()) {
         return input;
      }

      try (var stringWriter = new StringWriter(input.length() * 2)) {
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         return input;
      }

   }

   /**
    * Matching {@link Matcher} objects derived from the {@link Pattern}s supplied by the <code>patternFunction</code>
    * are provided to the <code>newTextFunction</code> along with a {@link ChangeSet} to specify changes to be made to
    * the <code>input</code> by the {@link ChangeSet}. The <code>patternFunction</code> is used to obtain a
    * {@link Pattern} on each iteration of the search loop. If the search {@link Pattern} fails to match the search loop
    * is terminated. When the search {@link Pattern} is found, the <code>newTextFunction</code> is provided the
    * {@link Matcher} and the {@link ChangeSet}. When the <code>newTextFunction</code> indicates it is done by returning
    * <code>true</code> the search loop is terminated. Each iteration of the search loop will start where the end of the
    * last {@link Pattern} match was.
    *
    * @param input the Word ML to be processed.
    * @param patternFunction a {@link Supplier} that produces a {@link Pattern} to be used on the next iteration of the
    * search loop.
    * @param newTextFunction a {@link BiFunction} that takes the {@link Matcher} and {@link ChangeSet} from each
    * iteration of the search loop where the {@link Pattern} supplied by the <code>patternFunction</code> matched.
    * @return a {@link CharSequence} with the replacements made. When no replacement are made the provided
    * {@link CharSequence}, <code>input</code>, is returned.
    */

   private static CharSequence replacer(CharSequence input, Supplier<Pattern> patternFunction, BiFunction<Matcher, ChangeSet, Boolean> newTextFunction) {

      var changeSet = new ChangeSet(input);
      var done = false;
      var start = 0;

      //@formatter:off
      while( !done ) {

         var matcher = patternFunction.get().matcher(input);

         if( !matcher.find(start) ) {
            break;
         }

         start = matcher.end();

         done = newTextFunction.apply(matcher, changeSet);
      }
      //@formatter:on

      if (!changeSet.hasChanges()) {
         return input;
      }

      try (var stringWriter = new StringWriter(input.length() * 2)) {
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         return input;
      }

   }

   /**
    * All matches of the <code>tokenPattern</code> are replaced with the values produced by the
    * <code>newTextFunction</code>.
    *
    * @param input the WordML to be processed.
    * @param tokenPattern the {@link TokenPattern} to search with.
    * @param newTextFunction a function to generate the replacement
    * @return a {@link CharSequence} with the replacements made. When no replacements are made the provided
    * {@link CharSequence}, <code>input</code>, is returned.
    */

   private static CharSequence replacer(CharSequence input, TokenPattern tokenPattern, Function<TokenMatcher, StringBuilder> newTextFunction) {

      var tokenMatcher = tokenPattern.tokenMatcher(input);
      var changeSet = new ChangeSet(input);

      while (tokenMatcher.find()) {

         var tokenStart = tokenMatcher.start();
         var tokenEnd = tokenMatcher.end();

         var newText = newTextFunction.apply(tokenMatcher);

         changeSet.replace(tokenStart, tokenEnd, newText);
      }

      if (!changeSet.hasChanges()) {
         return input;
      }

      try (var stringWriter = new StringWriter(input.length() * 2)) {
         changeSet.applyChanges(stringWriter);
         var result = stringWriter.getBuffer();
         return result;
      } catch (Exception e) {
         return input;
      }

   }

   /**
    * Searches the Word ML for empty section breaks and replaces them with page breaks.
    *
    * @param input the Word ML to process.
    * @return a {@link CharSequence} of the Word ML with empty section breaks replaced with page breaks.
    */

   public static CharSequence replaceEmptySectionBreaksWithPageBreaks(CharSequence input) {

      //@formatter:off
      var output =
         WordCoreUtil.replacer
            (
               input,
               WordCoreUtil.SECTION_EMPTY_BREAK_REMOVAL_PATTERN,
               ( matcher ) ->
               {
                  return WordCoreUtil.PAGE_BREAK;
               }
            );
      //@formatter:on
      return output;
   }

   /**
    * Searches for vertical white space characters and replaces them with the Word ML for a hard line break.
    *
    * @param input the Word ML to process.
    * @return a {@link CharSequence} of the WOrd ML with vertical white space change to Word ML hard line breaks.
    */
   public static CharSequence replaceVerticalWhitespaceCharactersWithWordMlHardLineBreaks(CharSequence input) {

      //@formatter:off
      var output =
         WordCoreUtil.replacer
            (
               input,
               WordCoreUtil.LINE_BREAKS_REMOVAL_PATTERN,
               ( matcher ) ->
               {
                  return WordCoreUtil.HARD_LINE_BREAK;
               }
            );
      //@formatter:on
      return output;
   }

   /**
    * Searches the WordML for appendix declarations. When found they are replaced with an appendix declaration with the
    * specified letter.
    *
    * @param input the WordML to process.
    * @param appendixLetter the letter for the appendix. Maybe capital or lowercase a-z.
    * @return a {@link StringBuilder} with the WordML for an appendix with the specified letter.
    */

   public static CharSequence setAppendixStartLetter(char appendixLetter, CharSequence input) {

      //@formatter:off
      var output =
         WordCoreUtil.replacer
            (
               input,
               WordCoreUtil.APPENDIX_START_LETTER_PATTERN,
               ( matcher ) ->
               {
                  var newText = WordCoreUtil.getAppendixStart( appendixLetter );
                  return newText;
               }
            );
      //@formatter:on

      return output;

   }

   /**
    * Sets the initial list sequence numbers in the list definitions for the heading levels specified in the
    * <code>outlineNumbers</code> array. Word heading numbers range from 1 to 9. The <code>outlineNumbers.length</code>
    * number of Word headings are updated. Index zero of the <code>outlineNumbers</code> array corresponds to heading1.
    * The <code>outlineNumbers</code> array contains the string representation of the outline number of the heading
    * level that corresponds with the array index.
    *
    * @param outlineNumbers the outline heading numbers to initialize the publishing template with.
    * @param template the publishing template.
    * @return a {@link CharSequence} of the publishing template with the initial list sequence numbers for headings
    * updated.
    */

   public static CharSequence setHeadingNumbers(String[] outlineNumbers, CharSequence template) {

      //@formatter:off
      var indexArray = new Integer[1];
      indexArray[0] = 0;

      var size = WordCoreUtil.INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_SIZE + 1;

      var searchStringBuilder =
         new StringBuilder( size * 2 )
                .append( WordCoreUtil.INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_A );

      return
         WordCoreUtil.replacer
            (
               template,
               ( ) ->
               {
                  var index = indexArray[0];
                  var headingNumber = index + 1;

                  searchStringBuilder.setLength( INITIAL_HEADING_NUMBER_REGEX_TEMPLATE_PART_A.length() );

                  searchStringBuilder
                     .append( headingNumber )
                     .append( "\"/>" )
                     ;

                  return Pattern.compile( searchStringBuilder.toString() );
               },
               ( matcher, changeSet ) ->
               {
                  var index = indexArray[0];

                  var start = matcher.start( 1 );
                  var end   = matcher.end( 1 );

                  changeSet.replace( start, end, outlineNumbers[index].toCharArray() );

                  index++;

                  indexArray[0] = index;

                  return index >= outlineNumbers.length;
               }
            );
      //@formatter:on
   }

   /**
    * @deprecated Use {@link XmlEncoderDecoder#xmlToText} with the flag {@link XmlEncoderDecoder.REMOVE_TAGS} instead.
    */

   @Deprecated
   public static String textOnly(String str) {
      return XmlEncoderDecoder.xmlToText(str, XmlEncoderDecoder.REMOVE_TAGS).toString();
   }
}

/* EOF */
