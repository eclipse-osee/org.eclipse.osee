/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for the {@link WordCoreUtil} utility class.
 *
 * @author Loren K. Ashley
 */

public class WordCoreUtilTest {

   @Test
   public void removeTokenPattern() {

      //@formatter:off
      String input =
         "   <w:body>\n" +
         "      <wx:sect>\n" +
         "         <w:p>\n" +
         "            <w:pPr>\n" +
         "               <w:pStyle\n" +
         "                  w:val = \"Caption\"/>\n" +
         "               <w:keepNext/>\n" +
         "            </w:pPr>\n" +
         "            <w:r>\n" +
         "               <w:t>INSERT_ARTIFACT_HERE</w:t>\n" +
         "            </w:r>\n" +
         "         </w:p>\n" +
         "      </wx:sect>\n" +
         "   </w:body>"
         ;

      String expected =
         "   <w:body>\n" +
         "      <wx:sect>\n" +
         "         REPLACEMENT_TOKEN\n" +
         "      </wx:sect>\n" +
         "   </w:body>"
         ;

      //@formatter:on

      var output = new StringBuilder(4 * 1024);

      //@formatter:off
      WordCoreUtil.processPublishingTemplate
         (
            input,
            (section) ->
            {
              output.append(section);
              output.append("REPLACEMENT_TOKEN");
            },
            (tail) -> output.append(tail)
         );
      //@formatter:off

      Assert.assertEquals( expected, output.toString() );
   }

   @Test
   public void changeHyperlinksToReferencesTest() {

      //@formatter:off
      String input =
         "   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:pStyle\n" +
         "            w:val = \"Caption\"/>\n" +
         "         <w:keepNext/>\n" +
         "      </w:pPr>\n" +
         "      <w:r>\n" +
         "         <w:t>Table </w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"begin\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:instrText> STYLEREF 1 \\s </w:instrText>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"separate\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:rPr>\n" +
         "            <w:b\n" +
         "               w:val = \"off\"/>\n" +
         "            <w:b-cs\n" +
         "               w:val = \"off\"/>\n" +
         "            <w:noProof/>\n" +
         "         </w:rPr>\n" +
         "         <w:t>Error! No text of specified style in document.</w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"end\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:noBreakHyphen/>\n" +
         "      </w:r>\n" +
         "      <w:fldSimple\n" +
         "         w:instr = \" ABC Table \\* ARABIC \\s 1 \">\n" +
         "         <w:r>\n" +
         "            <w:rPr>\n" +
         "               <w:noProof/>\n" +
         "            </w:rPr>\n" +
         "            <w:t>2</w:t>\n" +
         "         </w:r>\n" +
         "      </w:fldSimple>\n" +
         "      <w:r>\n" +
         "         <w:t>:  </w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:t>ABC DEF GHI JKL MNO PQR STU VWX YZ</w:t>\n" +
         "      </w:r>\n" +
         "   </w:p>\n" +
         "   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:keepNext/>\n" +
         "      </w:pPr>\n" +
         "   </w:p>" +
         "   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:rPr>\n" +
         "            <w:rFonts\n" +
         "               w:ascii = \"Times New Roman\"\n" +
         "               w:h-ansi = \"Times New Roman\"/>\n" +
         "            <wx:font\n" +
         "               wx:val = \"Times New Roman\"/>\n" +
         "            <w:sz\n" +
         "               w:val = \"16\"/>\n" +
         "            <w:sz-cs\n" +
         "               w:val = \"16\"/>\n" +
         "         </w:rPr>\n" +
         "      </w:pPr>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"begin\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:instrText> HYPERLINK \\l &quot;OSEE.8714082&quot; </w:instrText>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"separate\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:rPr>\n" +
         "            <w:rStyle\n" +
         "               w:val = \"Hyperlink\"/>\n" +
         "         </w:rPr>\n" +
         "         <w:t>abc def ghi jkl mno pqr stu vwx yz</w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"end\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:rPr>\n" +
         "            <w:rFonts\n" +
         "               w:ascii = \"Times New Roman\"\n" +
         "               w:h-ansi = \"Times New Roman\"/>\n" +
         "            <wx:font\n" +
         "               wx:val = \"Times New Roman\"/>\n" +
         "            <w:sz\n" +
         "               w:val = \"16\"/>\n" +
         "            <w:sz-cs\n" +
         "               w:val = \"16\"/>\n" +
         "         </w:rPr>\n" +
         "         <w:t/>\n" +
         "      </w:r>\n" +
         "   </w:p>"
         ;


      //@formatter:off
      String expected =
         "   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:pStyle\n" +
         "            w:val = \"Caption\"/>\n" +
         "         <w:keepNext/>\n" +
         "      </w:pPr>\n" +
         "      <w:r>\n" +
         "         <w:t>Table </w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"begin\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:instrText> STYLEREF 1 \\s </w:instrText>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"separate\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:rPr>\n" +
         "            <w:b\n" +
         "               w:val = \"off\"/>\n" +
         "            <w:b-cs\n" +
         "               w:val = \"off\"/>\n" +
         "            <w:noProof/>\n" +
         "         </w:rPr>\n" +
         "         <w:t>Error! No text of specified style in document.</w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:fldChar\n" +
         "            w:fldCharType = \"end\"/>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:noBreakHyphen/>\n" +
         "      </w:r>\n" +
         "      <w:fldSimple\n" +
         "         w:instr = \" ABC Table \\* ARABIC \\s 1 \">\n" +
         "         <w:r>\n" +
         "            <w:rPr>\n" +
         "               <w:noProof/>\n" +
         "            </w:rPr>\n" +
         "            <w:t>2</w:t>\n" +
         "         </w:r>\n" +
         "      </w:fldSimple>\n" +
         "      <w:r>\n" +
         "         <w:t>:  </w:t>\n" +
         "      </w:r>\n" +
         "      <w:r>\n" +
         "         <w:t>ABC DEF GHI JKL MNO PQR STU VWX YZ</w:t>\n" +
         "      </w:r>\n" +
         "   </w:p>\n" +
         "   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:keepNext/>\n" +
         "      </w:pPr>\n" +
         "   </w:p>   <w:p>\n" +
         "      <w:pPr>\n" +
         "         <w:rPr>\n" +
         "            <w:rFonts\n" +
         "               w:ascii = \"Times New Roman\"\n" +
         "               w:h-ansi = \"Times New Roman\"/>\n" +
         "            <wx:font\n" +
         "               wx:val = \"Times New Roman\"/>\n" +
         "            <w:sz\n" +
         "               w:val = \"16\"/>\n" +
         "            <w:sz-cs\n" +
         "               w:val = \"16\"/>\n" +
         "         </w:rPr>\n" +
         "      </w:pPr>\n" +
         "      <w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> REF OSEE.8714082 \\h </w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Sec#</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r><w:r><w:t> \n" +
         "         \n" +
         "         abc def ghi jkl mno pqr stu vwx yz\n" +
         "      \n" +
         "      \n" +
         "         </w:t></w:r>\n" +
         "      <w:r>\n" +
         "         <w:rPr>\n" +
         "            <w:rFonts\n" +
         "               w:ascii = \"Times New Roman\"\n" +
         "               w:h-ansi = \"Times New Roman\"/>\n" +
         "            <wx:font\n" +
         "               wx:val = \"Times New Roman\"/>\n" +
         "            <w:sz\n" +
         "               w:val = \"16\"/>\n" +
         "            <w:sz-cs\n" +
         "               w:val = \"16\"/>\n" +
         "         </w:rPr>\n" +
         "         <w:t/>\n" +
         "      </w:r>\n" +
         "   </w:p>"
         ;
      //@formatter:on

      var result = WordCoreUtil.changeHyperlinksToReferences(input, Set.of());

      Assert.assertEquals(expected, result.toString());
   }

   @Test
   public void addChapterNumToCaptionAndBookmark() {

      //@formatter:off
      String input =
         "<w:p wsp:rsidR=\"00585BC8\" wsp:rsidRDefault=\"007F2555\"><w:pPr><w:rPr><w:noProof></w:noProof><w:color w:val=\"0000FF\"></w:color></w:rPr></w:pPr></w:p><w:p wsp:rsidP=\"006E7A34\" wsp:rsidR=\"006E7A34\" wsp:rsidRDefault=\"006E7A34\"><w:pPr><w:pStyle w:val=\"Caption\"></w:pStyle><w:keepNext></w:keepNext></w:pPr><w:r><w:t>Table </w:t></w:r><w:fldSimple w:instr=\" SEQ Table \\* ARABIC \"><w:r><w:rPr><w:noProof></w:noProof></w:rPr><w:t>3</w:t></w:r></w:fldSimple><w:r><w:t>  </w:t></w:r><w:r wsp:rsidRPr=\"00AD6E36\"><w:t>ABC Table</w:t></w:r></w:p><w:p wsp:rsidP=\"006E7A34\" wsp:rsidR=\"006E7A34\" wsp:rsidRDefault=\"006E7A34\" wsp:rsidRPr=\"006E7A34\"><w:pPr><w:keepNext></w:keepNext></w:pPr></w:p><w:tbl>";

      String expected =
         "<w:p wsp:rsidR=\"00585BC8\" wsp:rsidRDefault=\"007F2555\"><w:pPr><w:rPr><w:noProof></w:noProof><w:color w:val=\"0000FF\"></w:color></w:rPr></w:pPr></w:p><w:p wsp:rsidP=\"006E7A34\" wsp:rsidR=\"006E7A34\" wsp:rsidRDefault=\"006E7A34\"><w:pPr><w:pStyle w:val=\"Caption\"></w:pStyle><w:keepNext></w:keepNext></w:pPr><w:r><w:t>Table </w:t></w:r><w:fldSimple w:instr=\" STYLEREF 1 \\s \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple><w:r><w:noBreakHyphen/></w:r><w:fldSimple w:instr=\" SEQ Table \\* ARABIC \\s 1 \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple><w:r><w:t>  ABC Table</w:t></w:r></w:p><w:p wsp:rsidP=\"006E7A34\" wsp:rsidR=\"006E7A34\" wsp:rsidRDefault=\"006E7A34\" wsp:rsidRPr=\"006E7A34\"><w:pPr><w:keepNext></w:keepNext></w:pPr></w:p><w:tbl>";
      //@formatter:on

      var result = WordCoreUtil.addChapterNumToCaptionAndBookmark(input, "", "");

      System.out.println(result);
      Assert.assertEquals(expected, result.toString());

   }

}

/* EOF */
