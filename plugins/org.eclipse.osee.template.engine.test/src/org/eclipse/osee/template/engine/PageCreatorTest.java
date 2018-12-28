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
package org.eclipse.osee.template.engine;

import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.ArtifactSelect;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.ArtifactSelectExpected;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.RealizePage_ListItems;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.RealizePage_MainPageHtml;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.RealizePage_ValuesHtml;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.TestMainPage_WithIncludeFileHtml;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.TestValues_IncludeHtml;
import static org.eclipse.osee.template.engine.OseeTemplateTestTokens.TestValues_KeyValueHtml;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Unit Test for {@link PageCreator}
 *
 * @author Donald G. Dunne
 */
public class PageCreatorTest {

   private static final IResourceRegistry registry = new ResourceRegistry();

   @BeforeClass
   public static void setup() {
      OseeTemplateTestTokens.register(registry);
      OseeTemplateTokens.register(registry);
   }

   @Before
   public void setupTest() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHtmlPageCreator_KeyValues() {
      PageCreator page = PageFactory.newPageCreator(registry, "key", "value", "key2", "value2");
      Assert.assertEquals("value", page.getValue("key"));
      Assert.assertEquals("value2", page.getValue("key2"));
   }

   @Test
   public void addSubstitution() {
      StringRule rule = new StringRule("key", "value");
      PageCreator page = new PageCreator(registry);
      page.addSubstitution(rule);
      Assert.assertEquals("value", page.getValue("key"));
   }

   @Test
   public void readSubstitutions_keyValue() {
      PageCreator page = new PageCreator(registry);
      page.readKeyValuePairs(TestValues_KeyValueHtml);

      Assert.assertEquals("   <h1>value</h1>", page.getValue("key"));
      Assert.assertEquals("   <h2>value2</h2>", page.getValue("key2"));
   }

   @Test
   public void readSubstitutions_include() {
      PageCreator page = new PageCreator(registry);
      page.readKeyValuePairs(TestValues_IncludeHtml);
      Assert.assertEquals("<!-- testHeaderPage.html -->\n\n<b>header</b>\n", page.getValue("header"));
   }

   @Test
   public void realizePage() {
      PageCreator page = new PageCreator(registry);
      page.readKeyValuePairs(RealizePage_ValuesHtml);

      String expected = "<!-- header.html -->\n\nmy header\n\n<h1>heading1</h1>\n\n   <h2>heading2</h2>\n";
      String results = page.realizePage(RealizePage_MainPageHtml);
      Assert.assertEquals(expected, results);

      page = PageFactory.newPageCreator(registry, RealizePage_ValuesHtml);
      results = page.realizePage(RealizePage_MainPageHtml);
      Assert.assertEquals(expected, results);

      results = PageFactory.realizePage(registry, RealizePage_MainPageHtml, RealizePage_ValuesHtml);
      Assert.assertEquals(expected, results);
   }

   @Test
   public void realizePage_WithIncludes() {
      String results = PageFactory.realizePage(registry, TestMainPage_WithIncludeFileHtml);
      Assert.assertTrue(results.contains("my header"));
      Assert.assertTrue(results.contains("header_NoTokenOnFirstLine.html -->"));

      Assert.assertTrue(results.contains("background-color"));
      Assert.assertTrue(results.contains("myTest.css */"));
   }

   @Test
   public void realizePage_NoTokenFirstLine() {
      String results = PageFactory.realizePage(registry, TestMainPage_WithIncludeFileHtml);
      Assert.assertTrue(results.contains("my header"));
   }

   @Test(expected = OseeArgumentException.class)
   public void realizePage_noSubstitution() {
      PageFactory.realizePage(registry, RealizePage_MainPageHtml);
   }

   @Test
   public void realizePage__KeyValues() {
      String expected = "headerA\n\nvalue1\n\nvalue2\n";
      String results = PageFactory.realizePage(registry, RealizePage_MainPageHtml, "header", "headerA", "key1",
         "value1", "key2", "value2");
      Assert.assertEquals(expected, results);
   }

   @Test
   public void testToString() {
      PageCreator page = new PageCreator(registry);
      page.readKeyValuePairs(TestValues_KeyValueHtml);
      Assert.assertEquals("{key2=   <h2>value2</h2>, key=   <h1>value</h1>}", page.toString());
   }

   @Test
   public void testHyperLinkRule() {
      PageCreator page = new PageCreator(registry);
      page.addSubstitution(new HyperLinkRule("header", "http://www.stackoverflow.com", "Stack Overflow"));
      page.addSubstitution(new HyperLinkRule("key1", "http://www.google.com", "Google!"));
      page.addSubstitution(new HyperLinkRule("key2", "http://www.eclipse.org", "Eclipse"));
      String expected = "<a href=\"http://www.stackoverflow.com\">Stack Overflow</a>\n\n" //
         + "<a href=\"http://www.google.com\">Google!</a>\n\n" //
         + "<a href=\"http://www.eclipse.org\">Eclipse</a>\n";
      String actual = page.realizePage(RealizePage_MainPageHtml);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testListItemRule() {
      PageCreator page = new PageCreator(registry);
      page.addSubstitution(new ListItemRule<>("key1",
         new HyperLinkRule("ruleNameDoesntMatter", "http://www.eclipse.org", "Eclipse")));
      CompositeRule<Pair<CharSequence, CharSequence>> listItemsComposite =
         new CompositeRule<>("key2");

      ListItemRule<Pair<CharSequence, CharSequence>> li1 = new ListItemRule<>(
         "keyDoesntMatter", new HyperLinkRule("keyDoesntMatter", "http://www.stackoverflow.com", "Stack Overflow"));
      ListItemRule<Pair<CharSequence, CharSequence>> li2 = new ListItemRule<>(
         "keyDoesntMatter", new HyperLinkRule("keyDoesntMatter", "http://www.google.com", "Google!"));

      listItemsComposite.addRule(li1);
      listItemsComposite.addRule(li2);

      page.addSubstitution(listItemsComposite);
      String expected = "<ol>\n<li><a href=\"http://www.eclipse.org\">Eclipse</a></li>\n\n</ol>\n\n" //
         + "<ul>\n<li><a href=\"http://www.stackoverflow.com\">Stack Overflow</a></li>\n" //
         + "<li><a href=\"http://www.google.com\">Google!</a></li>\n\n</ul>";
      String actual = page.realizePage(RealizePage_ListItems);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testArtifactSelectRule() {
      PageCreator page = new PageCreator(registry);
      Set<String> types = new HashSet<>();
      types.add("Software Requirement");
      ArtifactTypeOptionsRule rule = new ArtifactTypeOptionsRule("select", types, types);
      page.addSubstitution(rule);
      String actual = page.realizePage(ArtifactSelect);
      String expected = page.realizePage(ArtifactSelectExpected);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void realizePage_WithRule() {
      Set<String> types = new HashSet<>();
      types.add("Software Requirement");
      ArtifactTypeOptionsRule rule = new ArtifactTypeOptionsRule("select", types, types);
      PageCreator page = PageFactory.newPageCreatorWithRules(registry, ArtifactSelect, rule);
      String actual = page.realizePage(ArtifactSelect);
      String expected = page.realizePage(ArtifactSelectExpected);
      Assert.assertEquals(expected, actual);
   }

}