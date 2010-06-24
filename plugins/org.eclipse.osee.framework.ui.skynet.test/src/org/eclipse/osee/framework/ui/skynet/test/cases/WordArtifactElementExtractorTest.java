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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordArtifactElementExtractor;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Test case for {@link WordArtifactElementExtractor}.
 *
 * @author Jeff C. Phillips
 */
public class WordArtifactElementExtractorTest {
   private final static String WORDML_START =
         "<w:wordDocument xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\"  xmlns:ns0=\"http:/ \" xmlns:ns1=\"http://www.w3.org/2001/XMLSchema\" w:macrosPresent=\"no\" w:embeddedObjPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\">";
   private final static String WORDML_END = "</w:wordDocument>";

   private final static String NO_CHANGE = "<w:body></w:body>";
   private final static String CHANGE_IN_BETWEEN_2007 =
         "<w:body><w:p wsp:rsidR=\"00682312\" wsp:rsidRDefault=\"00B17B94\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00A05BEC\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"002D7BD4\" wsp:rsidRDefault=\"002D7BD4\"><w:r><w:t>Middle change</w:t></w:r></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00A05BEC\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"/><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00A05BEC\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00B17B94\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";
   private final static String START_CHANGE_2007 =
         "<w:body><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00BC116F\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink><w:r wsp:rsidR=\"00982F7F\"><w:t>I am change</w:t></w:r></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00BC116F\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"/><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00BC116F\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00B17B94\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";
   private final static String END_CHANGE_2007 =
         "<w:body><w:p wsp:rsidR=\"00682312\" wsp:rsidRDefault=\"00B17B94\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00455A9D\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00682312\"><w:r><w:t>End Change</w:t></w:r><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00455A9D\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"/><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00455A9D\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00B17B94\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";
   private final static String CHANGE_IN_BETWEEN_2007_MULTI =
         "<w:body><wx:sub-section><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"00D61766\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"1.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>blah</w:t></w:r></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=4059\"><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00AB7603\" wsp:rsidRDefault=\"00AB7603\"><w:r><w:t>One</w:t></w:r></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=4059\"><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"/><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"00D61766\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"2.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>blah2</w:t></w:r></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:hlink w:dest=\"http://none/edit?guid=A+8lBVkeBRM4X6ZFJ+gA&amp;branchId=4059\"><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00BB1E70\" wsp:rsidRPr=\"007F5EE9\" wsp:rsidRDefault=\"00AB7603\"><w:pPr><w:rPr><w:rFonts w:ascii=\"C39HrP48DlTt\" w:h-ansi=\"C39HrP48DlTt\"/><wx:font wx:val=\"C39HrP48DlTt\"/></w:rPr></w:pPr><w:r><w:t>Two</w:t></w:r></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:hlink w:dest=\"http://none/edit?guid=A+8lBVkeBRM4X6ZFJ+gA&amp;branchId=4059\"><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"/><w:p wsp:rsidR=\"007E22BA\" wsp:rsidRDefault=\"007E22BA\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00D61766\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><w:sectPr wsp:rsidR=\"007E22BA\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";

   private final static String CHANGE_IN_BETWEEN_2003 =
         "<w:body><wx:sect><w:p wsp:rsidR=\"00854E9E\" wsp:rsidRDefault=\"00854E9E\"><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00763F1D\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00D83EA2\" wsp:rsidRDefault=\"00763F1D\"><w:r><w:t>This is a test</w:t></w:r><w:r wsp:rsidR=\"00C4548D\"><w:t>-x</w:t></w:r></w:p><w:p wsp:rsidR=\"00854E9E\" wsp:rsidRDefault=\"00854E9E\"><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00763F1D\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00854E9E\" wsp:rsidRDefault=\"00854E9E\"/><w:p wsp:rsidR=\"00854E9E\" wsp:rsidRDefault=\"00854E9E\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00763F1D\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00854E9E\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></wx:sect></w:body>";
   private final static String START_END_CHANGE_2003 =
         "<w:body><wx:sect><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00474817\"><w:r><w:t>Not_this</w:t></w:r><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00E00A43\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink><w:r><w:t>this</w:t></w:r></w:p><w:p wsp:rsidR=\"00D83EA2\" wsp:rsidRDefault=\"00E00A43\"><w:r><w:t>This is a test</w:t></w:r></w:p><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00474817\"><w:r><w:t>that</w:t></w:r><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00E00A43\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink><w:r><w:t>not_that</w:t></w:r></w:p><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00235CEA\"/><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00235CEA\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00E00A43\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00235CEA\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></wx:sect></w:body>";
   private final static String CHANGE_IN_BETWEEN_2003_MULTI =
         "<w:body><wx:sect><wx:sub-section><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"00122167\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"1.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Tag Test</w:t></w:r></w:p><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00122167\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00D83EA2\" wsp:rsidRDefault=\"00122167\"><w:r><w:t>This is a test</w:t></w:r><w:r wsp:rsidR=\"00A4341C\"><w:t>-x</w:t></w:r></w:p><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:hlink w:dest=\"http://none/edit?guid=AZkaMmpE_AJAtatzUqwA&amp;branchId=2315\"><w:r wsp:rsidR=\"00122167\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"/><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00122167\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"00122167\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"2.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Tag Test 2</w:t></w:r></w:p><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:hlink w:dest=\"http://none/edit?guid=AZkj9HgJnT38eBT3jiAA&amp;branchId=2315\"><w:r wsp:rsidR=\"00122167\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00500093\" wsp:rsidRDefault=\"00122167\"><w:r><w:t>This has a newline</w:t></w:r><w:r wsp:rsidR=\"00A4341C\"><w:t>-y</w:t></w:r></w:p><w:p wsp:rsidR=\"00001CCC\" wsp:rsidRDefault=\"00122167\"/><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:hlink w:dest=\"http://none/edit?guid=AZkj9HgJnT38eBT3jiAA&amp;branchId=2315\"><w:r wsp:rsidR=\"00122167\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"/><w:p wsp:rsidR=\"005F4166\" wsp:rsidRDefault=\"005F4166\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00122167\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"005F4166\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></wx:sub-section></wx:sect></w:body>";
   private final static String MULTI_SUBSECTION_MULTI_EDIT = "<w:body><wx:sub-section><wx:sub-section><wx:sub-section><wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"0055790E\"><w:pPr><w:pStyle w:val=\"Heading5\"/><w:listPr><wx:t wx:val=\"3.2.2.1.3  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>{MAP_SYMBOLOGY_UIG}</w:t></w:r></w:p><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:hlink w:dest=\"http://none/edit?guid=AAABD2KaitEBi7yYFOzKvA&amp;branchId=4444\"><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00883BA1\" wsp:rsidRPr=\"00B0497D\" wsp:rsidRDefault=\"0055790E\" wsp:rsidP=\"00883BA1\"><w:pPr><w:pStyle w:val=\"dlbody\"/></w:pPr><w:r><w:t>Test this multi edit</w:t></w:r></w:p><w:p wsp:rsidR=\"007F3E1C\" wsp:rsidRDefault=\"007F3E1C\"/><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:hlink w:dest=\"http://none/edit?guid=AAABD2KaitEBi7yYFOzKvA&amp;branchId=4444\"><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"/><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"0055790E\"><w:pPr><w:pStyle w:val=\"Heading5\"/><w:listPr><wx:t wx:val=\"3.2.2.1.4  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>{ROUTE_LINE}</w:t></w:r></w:p><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:hlink w:dest=\"http://none/edit?guid=AAABD2KamSoBi7yYcgW0Gg&amp;branchId=4444\"><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"007F3E1C\" wsp:rsidRDefault=\"0055790E\"><w:r><w:t>Test subsection</w:t></w:r></w:p><w:p wsp:rsidR=\"005B0393\" wsp:rsidRDefault=\"0055790E\"><w:r><w:t>End test</w:t></w:r></w:p><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:hlink w:dest=\"http://none/edit?guid=AAABD2KamSoBi7yYcgW0Gg&amp;branchId=4444\"><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"/><w:p wsp:rsidR=\"00B85E0D\" wsp:rsidRDefault=\"00B85E0D\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"0055790E\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section></wx:sub-section></wx:sub-section></wx:sub-section></wx:sub-section><w:sectPr wsp:rsidR=\"00B85E0D\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";
   private final String MULIT_EDIT_SAME_IMG = "<w:body><wx:sub-section><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"00176772\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"1.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>blah</w:t></w:r></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00176772\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"00021E90\" wsp:rsidRDefault=\"004C7836\" wsp:rsidP=\"004C7836\"><w:r><w:t>Image</w:t></w:r></w:p><w:p wsp:rsidR=\"004C7836\" wsp:rsidRDefault=\"004C7836\" wsp:rsidP=\"004C7836\"><w:r><w:pict><v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\" path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\"><v:stroke joinstyle=\"miter\"/><v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/><v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/><v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/><v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/><v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas><v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/><o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype><w:binData w:name=\"wordml://03000001.png\" xml:space=\"preserve\">iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAABZJREFUCNdj1H4fwMDAwMTAwMDAwAAAEP0BbjibP6IAAAAASUVORK5CYIJ=</w:binData><v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:2.25pt;height:2.25pt\"><v:imagedata src=\"wordml://03000001.png\" o:title=\"square\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:hlink w:dest=\"http://none/edit?guid=A+7R6BYOEiR5BY230lgA&amp;branchId=2380\"><w:r wsp:rsidR=\"00176772\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"/><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00176772\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"00176772\"><w:pPr><w:pStyle w:val=\"Heading1\"/><w:listPr><wx:t wx:val=\"2.  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>blah2</w:t></w:r></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:hlink w:dest=\"http://none/edit?guid=A+8lBVkeBRM4X6ZFJ+gA&amp;branchId=2380\"><w:r wsp:rsidR=\"00176772\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_START</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"004C7836\" wsp:rsidRDefault=\"004C7836\"><w:r><w:t>Same image</w:t></w:r></w:p><w:p wsp:rsidR=\"004C7836\" wsp:rsidRDefault=\"004C7836\"><w:r><w:pict><v:shape id=\"_x0000_i1026\" type=\"#_x0000_t75\" style=\"width:2.25pt;height:2.25pt\"><v:imagedata src=\"wordml://03000001.png\" o:title=\"square\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:hlink w:dest=\"http://none/edit?guid=A+8lBVkeBRM4X6ZFJ+gA&amp;branchId=2380\"><w:r wsp:rsidR=\"00176772\"><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>OSEE_EDIT_END</w:t></w:r></w:hlink></w:p><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"/><w:p wsp:rsidR=\"000B2362\" wsp:rsidRDefault=\"000B2362\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00176772\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><w:sectPr wsp:rsidR=\"000B2362\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:sectPr></w:body>";
   
   @org.junit.Test(expected = OseeCoreException.class)
   public void testEmptyChange() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor = new WordArtifactElementExtractor(getDocument(NO_CHANGE));
      artifactElementExtractor.extractElements();
   }

   @org.junit.Test
   public void testInBetweenChange2007() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(CHANGE_IN_BETWEEN_2007));

      Collection<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.extractElements().size() == 1);
      Assert.assertTrue("Middle change".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
            WordTemplateRenderer.getFormattedContent(artElements.iterator().next()))))));
   }

   @org.junit.Test
   public void testStartChange2007() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(START_CHANGE_2007));

      Collection<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.extractElements().size() == 1);
      Assert.assertTrue("I am change".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
            WordTemplateRenderer.getFormattedContent(artElements.iterator().next()))))));
   }

   @org.junit.Test
   public void testEndChange2007() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(END_CHANGE_2007));

      Collection<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.extractElements().size() == 1);
      Assert.assertTrue("End Change".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
            WordTemplateRenderer.getFormattedContent(artElements.iterator().next()))))));
   }

   @org.junit.Test
   public void testInBetweenChange2007Multi() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(CHANGE_IN_BETWEEN_2007_MULTI));

      List<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue("expected 2 got " + artifactElementExtractor.extractElements().size(),
            artifactElementExtractor.extractElements().size() == 2);
      List<String> testText = Arrays.asList("One", "Two");
      multiArtifactTest(artElements, testText);
   }

   @org.junit.Test
   public void testInBetweenChange2003() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(CHANGE_IN_BETWEEN_2003));

      Collection<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.extractElements().size() == 1);
      Assert.assertTrue("This is a test-x".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
            WordTemplateRenderer.getFormattedContent(artElements.iterator().next()))))));
   }

   @org.junit.Test
   public void testStartEndChange2003() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(START_END_CHANGE_2003));
      Collection<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.extractElements().size() == 1);
      String artContent =
            WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
                  WordTemplateRenderer.getFormattedContent(artElements.iterator().next()))));
      Assert.assertTrue("Got*" + artContent, "this This is a test that".equals(artContent));
   }

   @org.junit.Test
   public void testInBetweenChange2003Multi() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(CHANGE_IN_BETWEEN_2003_MULTI));

      List<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue("expected 2 got " + artifactElementExtractor.extractElements().size(),
            artifactElementExtractor.extractElements().size() == 2);
      List<String> testText = Arrays.asList("This is a test-x", "This has a newline-y");
      multiArtifactTest(artElements, testText);
   }
   
   @org.junit.Test
   public void test2003MultiSubsectionMultiEdit() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(MULTI_SUBSECTION_MULTI_EDIT));

      List<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue("expected 2 got " + artifactElementExtractor.extractElements().size(),
            artifactElementExtractor.extractElements().size() == 2);
      List<String> testText = Arrays.asList("Test this multi edit", "Test subsection End test");
      multiArtifactTest(artElements, testText);
   }

   private void multiArtifactTest(List<Element> actuals, List<String> expected) throws IOException {
      for (int i = 0; i < actuals.size(); i++) {
         String artContent =
               WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
                     WordTemplateRenderer.getFormattedContent(actuals.get(i)))));
         Assert.assertTrue("expected:*" + expected.get(i) + "* got:*" + artContent + "*", expected.get(i).equals(
               artContent));
      }
   }

   @org.junit.Test
   public void testSameImageMultiArtifacts() throws Exception {
      WordArtifactElementExtractor artifactElementExtractor =
            new WordArtifactElementExtractor(getDocument(MULIT_EDIT_SAME_IMG));

      List<Element> artElements = artifactElementExtractor.extractElements();
      Assert.assertTrue("expected 2 got " + artElements.size(),  artElements.size() == 2);
     
      Assert.assertTrue(checkforBinData(artElements.get(0)));
      Assert.assertTrue(checkforBinData(artElements.get(1)));
   }
   
   private boolean checkforBinData(Element element){
      boolean changedImage = false;
      NodeList descendants = element.getElementsByTagName("*");
      for (int i = 0; i < descendants.getLength(); i++) {
         Node descendant = descendants.item(i);
         if (descendant.getNodeName().contains("w:pic")) {
            NodeList imageDataElement = ((Element) descendant).getElementsByTagName("v:imagedata");
            if (imageDataElement.getLength() > 0) {
               if(((Element) descendant).getElementsByTagName("w:binData").getLength() > 0){
                  changedImage = true;
               }
            }
         }
      }
      return changedImage;
   }
   
   private Document getDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
      return Jaxp.readXmlDocument(WORDML_START + xmlString + WORDML_END);
   }
}
