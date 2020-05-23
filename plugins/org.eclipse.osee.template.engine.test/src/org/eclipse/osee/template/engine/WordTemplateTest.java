/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.template.engine;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class WordTemplateTest {

   private static final IResourceRegistry registry = new ResourceRegistry();

   @BeforeClass
   public static void setup() {
      OseeTemplateTestTokens.register(registry);
      OseeTemplateTokens.register(registry);
   }

   @Test
   public void testWordXml() throws IOException {
      String document_content =
         "<w:p wsp:rsidR=\"004B591F\" wsp:rsidRDefault=\"00BF6647\" wsp:rsidP=\"0022055B\"><w:pPr><w:jc w:val=\"center\"/><w:rPr><w:b/></w:rPr></w:pPr><w:r wsp:rsidRPr=\"0022055B\"><w:rPr><w:b/></w:rPr><w:t>Easy Company Test</w:t></w:r></w:p><w:p wsp:rsidR=\"0022055B\" wsp:rsidRDefault=\"00BF6647\" wsp:rsidP=\"0022055B\"/><w:p wsp:rsidR=\"0022055B\" wsp:rsidRDefault=\"00BF6647\" wsp:rsidP=\"00F16119\"><w:r><w:t>This is a test of the common word template</w:t></w:r><w:r wsp:rsidR=\"00F16119\"><w:t>.</w:t></w:r></w:p>";
      String document_properties =
         "<o:Title>SRS</o:Title><o:Author>Easy Company</o:Author><o:LastAuthor>Easy Company</o:LastAuthor><o:Revision>37</o:Revision><o:TotalTime>1332</o:TotalTime><o:LastPrinted>2008-09-25T21:40:00Z</o:LastPrinted><o:Created>2007-04-21T15:49:00Z</o:Created><o:LastSaved>2012-08-04T17:22:00Z</o:LastSaved><o:Pages>1</o:Pages><o:Words>4</o:Words><o:Characters>28</o:Characters><o:Company>The 502nd</o:Company><o:Lines>1</o:Lines><o:Paragraphs>1</o:Paragraphs><o:CharactersWithSpaces>31</o:CharactersWithSpaces><o:Version>12</o:Version>";

      String realizePage = PageFactory.realizePage(registry, OseeTemplateTokens.WordXml, "properties",
         document_properties, "content", document_content);
      Assert.assertNotNull(realizePage);

      String expected = Lib.fileToString(getClass(), "xml/testWord.xml");
      Assert.assertNotNull(expected);
      Assert.assertEquals(expected, realizePage);
   }

   @Test
   public void testWordPropertiesRule() {
      WordDocumentPropertiesRule rule =
         new WordDocumentPropertiesRule("properties", "SRS", "Easy Company", "The 502nd");

      PageCreator page = new PageCreator(registry);
      page.addSubstitution(rule);

      Assert.assertEquals("title = [ SRS ], author = [ Easy Company ], company = [ The 502nd ]",
         page.getValue("properties"));
   }
}