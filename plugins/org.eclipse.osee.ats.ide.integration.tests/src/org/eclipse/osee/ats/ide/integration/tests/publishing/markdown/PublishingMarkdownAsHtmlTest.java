/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestPublishingTemplateBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.markdown.HtmlZip;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.eclipse.osee.framework.core.publishing.table.ArtifactAppendixTableBuilder;
import org.eclipse.osee.framework.core.publishing.table.RelationTableOptions;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.eclipse.osee.orcs.core.util.PublishingTemplateContentMapEntry;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * Tests for publishing Markdown as HTML. Single template and single published document.
 *
 * @author Jaden W. Puckett
 */
public class PublishingMarkdownAsHtmlTest {

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         ;

   private static ApplicabilityEndpoint applEndpoint =
      ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch_Markdown);

   private static Long product_a_id = applEndpoint.getView("Product A").getId();

   static RendererMap rendererOptions =
      RendererMap.of
         (
            RendererOption.BRANCH, DemoBranches.SAW_PL_Working_Branch_Markdown,
            RendererOption.VIEW, ArtifactId.valueOf(product_a_id),
            RendererOption.PUBLISHING_FORMAT,  FormatIndicator.MARKDOWN
         );
   //@formatter:on

   private static PublishingEndpoint publishingEndpoint;

   /**
    * Defines the publishing templates for the tests. The templates will be created on the Common Branch under the "OSEE
    * Configuration/Document Templates" folder.
    */

   private static String PUBLISHING_MARKDOWN_AS_HTML_TEST_TEMPLATE_A = "PUBLISHING_MARKDOWN_AS_HTML_TEST_TEMPLATE_A";

   //@formatter:off
   private static Supplier<List<PublishingTemplate>> publishingTemplatesSupplier = new Supplier<> () {

      @Override
      public List<PublishingTemplate> get() {
         return

         List.of(
            new PublishingTemplate(
               CoreArtifactTokens.DocumentTemplates,                                                        /* Parent Artifact Identifier */
               PUBLISHING_MARKDOWN_AS_HTML_TEST_TEMPLATE_A,                                                 /* Name                       */
               new PublishingTemplate.StringSupplier(                                                       /* Publish Options Supplier   */
                  new StringBuilder( 1024 )
                     .append( "{"                                                                                             ).append( "\n" )
                     .append( "   \"ElementType\" : \"Artifact\","                                                            ).append( "\n" )
                     .append( "   \"OutliningOptions\" :"                                                                     ).append( "\n" )
                     .append( "      ["                                                                                       ).append( "\n" )
                     .append( "        {"                                                                                     ).append( "\n" )
                     .append( "         \"RecurseChildren\"                   : true,"                                        ).append( "\n" )
                     .append( "         \"HeadingArtifactType\"               : \"<headers-only-heading-artifact-type>\","    ).append( "\n" )
                     .append( "         \"IncludeMainContentForHeadings\"     : \"Never\""                                    ).append( "\n" )
                     .append( "        }"                                                                                     ).append( "\n" )
                     .append( "      ],"                                                                                      ).append( "\n" )
                     .append( "   \"AttributeOptions\" :"                                                                     ).append( "\n" )
                     .append( "      ["                                                                                       ).append( "\n" )
                     .append( "        {"                                                                                     ).append( "\n" )
                     .append( "         \"AttrType\"   : \"Markdown Content\","                                               ).append( "\n" )
                     .append( "         \"FormatPost\" : \"\","                                                               ).append( "\n" )
                     .append( "         \"FormatPre\"  : \"\","                                                               ).append( "\n" )
                     .append( "         \"Label\"      : \"\""                                                                ).append( "\n" )
                     .append( "        },"                                                                                    ).append( "\n" )
                     .append( "        {"                                                                                     ).append( "\n" )
                     .append( "         \"AttrType\"   : \"Description\","                                                    ).append( "\n" )
                     .append( "         \"FormatPost\" : \"\","                                                               ).append( "\n" )
                     .append( "         \"FormatPre\"  : \"\","                                                               ).append( "\n" )
                     .append( "         \"Label\"      : \"Description: \""                                                   ).append( "\n" )
                     .append( "        }"                                                                                     ).append( "\n" )
                     .append( "      ],"                                                                                      ).append( "\n" )
                     .append( "   \"MetadataOptions\" :"                                                                      ).append( "\n" )
                     .append( "      ["                                                                                       ).append( "\n" )
                     .append( "        {"                                                                                     ).append( "\n" )
                     .append( "         \"Type\" : \"Artifact Id\""                                                           ).append( "\n" )
                     .append( "        }"                                                                                     ).append( "\n" )
                     .append( "      ]"                                                                                       ).append( "\n" )
                     .append( "}"                                                                                             ).append( "\n" )
                     .toString()
               ),
               null,                                                                                        /* Template Content File Name */
               null,
               List.of(                                                                                     /* Publishing Template Content Map Entries */
                  new PublishingTemplateContentMapEntry(
                     FormatIndicator.MARKDOWN,                                                              /* Template Content Format    */
                     "INSERT_ARTIFACT_HERE_AND_TOC.md"                                                                 /* Template Content File Path */
                  )
               ),                                                                                   /* Match Criteria      */
               List.of(), new RelationTableOptions(
                  Collections.emptyList(),
                  Arrays.asList(
                     RelationTableOptions.ARTIFACT_ID,
                     RelationTableOptions.ARTIFACT_NAME,
                     CoreAttributeTypes.MarkdownContent.getName()
                  ),
                  Arrays.asList(
                     CoreRelationTypes.RequirementTrace.getName() + "|" + CoreRelationTypes.RequirementTrace.getSideName(RelationSide.SIDE_A),
                     CoreRelationTypes.RequirementTrace.getName() + "|" + CoreRelationTypes.RequirementTrace.getSideName(RelationSide.SIDE_B)
                  )
               )
            )

         );
      }
   };
   //@formatter:on

   private static Document htmlDoc;
   private static HashSet<String> imageNames = new HashSet<>();

   private static String[] excludedHeadings = new String[] {ArtifactAppendixTableBuilder.SECTION_HEADING};

   @BeforeClass
   public static void testSetup() {

      /*
       * Setup publishing templates
       */

      Map<String, org.eclipse.osee.framework.core.publishing.PublishingTemplate> templateMap =
         new TestPublishingTemplateBuilder(PublishingMarkdownAsHtmlTest.class).buildPublishingTemplates(
            PublishingMarkdownAsHtmlTest.publishingTemplatesSupplier);

      /**
       * Setup PublishingRequestData
       */

      var template_A = templateMap.get(PUBLISHING_MARKDOWN_AS_HTML_TEST_TEMPLATE_A);

      EnumRendererMap pubRenOpt = new EnumRendererMap(PublishingMarkdownAsHtmlTest.rendererOptions);

      List<ArtifactId> art =
         Arrays.asList(ArtifactId.valueOf(CoreArtifactTokens.SystemRequirementsFolderMarkdown.getToken().getId()));

      PublishingTemplateRequest pubTemReq =
         new PublishingTemplateRequest(template_A.getIdentifier().toString(), FormatIndicator.MARKDOWN);

      PublishingRequestData publishMarkdownAsHtmlRequestData = new PublishingRequestData(pubTemReq, pubRenOpt, art);

      /*
       * Make request
       */

      PublishingMarkdownAsHtmlTest.publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();

      var attachment =
         PublishingMarkdownAsHtmlTest.publishingEndpoint.publishMarkdownAsHtml(publishMarkdownAsHtmlRequestData);

      assertNotNull("HTML attachment should be present",
         attachment.getContentType().getType().equals(MediaType.TEXT_HTML));

      // Read and parse the HTML
      try (ZipInputStream zipInputStream = new ZipInputStream(attachment.getDataHandler().getInputStream())) {

         HtmlZip htmlZip = MarkdownHtmlUtil.processHtmlZip(zipInputStream);

         htmlDoc = htmlZip.getHtmlDocument();
         imageNames = htmlZip.getImageNames();
      } catch (IOException e) {
         throw new AssertionError("Error reading the file: " + e.getMessage(), e);
      } catch (Exception e) {
         throw new AssertionError("An unexpected error occurred: " + e.getMessage(), e);
      }
   }

   @Test
   public void testHtmlValidity() {
      // Ensure the HTML document is not null
      assertNotNull("HTML document should not be null", htmlDoc);

      // Check for the presence of the <html> element
      Elements htmlElements = htmlDoc.select("html");
      assertFalse("HTML should contain a <html> section", htmlElements.isEmpty());

      // Check for the presence of the <head> element
      Elements headElements = htmlDoc.select("head");
      assertFalse("HTML should contain a <head> section", headElements.isEmpty());

      // Check for the presence of the <style> element within the <head>
      Elements styleElements = headElements.select("style");
      assertFalse("HTML should contain a <style> section within the <head>", styleElements.isEmpty());

      // Check for the presence of the <body> element
      Elements bodyElements = htmlDoc.select("body");
      assertFalse("HTML should contain a <body> section", bodyElements.isEmpty());
   }

   @Test
   public void testArtifactIdsExist() {
      Elements artifactIdElements = htmlDoc.select("p:contains(Artifact Id)");
      List<String> artifactIds =
         artifactIdElements.stream().map(element -> element.text().replaceAll(".*Artifact Id: ", "").trim()).collect(
            Collectors.toList());

      assertFalse("There should be artifact Ids in the document", artifactIds.isEmpty());

      for (String artifactId : artifactIds) {
         assertTrue("Artifact Id should be numeric", artifactId.matches("\\d+"));
      }
   }

   @Test
   public void testHeaders() {

      Elements headers = htmlDoc.select("h1, h2, h3");

      boolean headingFound = false;

      for (Element header : headers) {

         if (containsExcludedHeading(header.text())) {
            continue;
         }

         headingFound = true;

         String headingText = header.text().trim();
         assertFalse("Heading text should not be empty", headingText.isEmpty());

         boolean paragraphFound = false;
         boolean anchorFound = false;

         // Traverse siblings after the heading until next heading
         Element sibling = header.nextElementSibling();
         while (sibling != null && !sibling.tagName().matches("h1|h2|h3")) {
            if (sibling.tagName().equals("p")) {
               String paraText = sibling.text();
               if (paraText.contains("Description") && paraText.contains("Artifact Id")) {
                  paragraphFound = true;
               }

               // Check for anchor tag inside the paragraph
               Matcher anchorMatcher =
                  Pattern.compile("<a\\s+id\\s*=\\s*\"\\d+\"\\s*></a>").matcher(sibling.outerHtml());
               if (anchorMatcher.find()) {
                  anchorFound = true;
               }
            }

            sibling = sibling.nextElementSibling();
         }

         assertTrue("Each heading should be followed by a paragraph with 'Description' and 'Artifact Id'",
            paragraphFound);
         assertTrue("Each heading should be followed by an <a id=\"1234\"></a> anchor", anchorFound);
      }

      assertTrue("There must be at least one heading in the HTML document", headingFound);
   }

   @Test
   public void testRelationTableData() {
      Elements tables = htmlDoc.select("table");
      assertFalse("There should be tables in the document", tables.isEmpty());

      for (Element table : tables) {
         // Check for a main header that contains the word "Relation"
         Elements primaryHeader = table.select("thead tr th[colspan]"); // Look for headers spanning multiple columns
         if (primaryHeader.isEmpty() || !primaryHeader.text().toLowerCase().contains("relation")) {
            continue; // Skip non-relation tables
         }

         // Now check for the 3 sub-headers (assuming the next row contains them)
         Elements subHeaders = table.select("thead tr").get(1).select("th"); // Get the second row of headers
         assertEquals("There should be 3 sub-headers in the relation table", 3, subHeaders.size());

         // Assert correct sub-header names
         assertEquals("First sub-header should be '" + RelationTableOptions.ARTIFACT_ID + "'",
            RelationTableOptions.ARTIFACT_ID, subHeaders.get(0).text());
         assertEquals("Second sub-header should be '" + RelationTableOptions.ARTIFACT_NAME + "'",
            RelationTableOptions.ARTIFACT_NAME, subHeaders.get(1).text());
         assertEquals("Third sub-header should be '" + CoreAttributeTypes.MarkdownContent.getName() + "'",
            CoreAttributeTypes.MarkdownContent.getName(), subHeaders.get(2).text());

         // Check each row (excluding the header row)
         Elements rows = table.select("tbody tr"); // Select only rows in the table body

         for (Element row : rows) {
            Elements columns = row.select("td");
            assertEquals("Each row should have 3 columns", 3, columns.size());

            // Assert that the artifact ID column contains a numeric value and is non-empty
            String artifactId = columns.get(0).text();
            assertFalse(RelationTableOptions.ARTIFACT_ID + " should not be empty", artifactId.isEmpty());
            assertTrue(RelationTableOptions.ARTIFACT_ID + " should be numeric", artifactId.matches("\\d+"));

            // Assert that artifact name and Markdown content are non-empty
            String artifactName = columns.get(1).text();
            String markdownContent = columns.get(2).text();

            assertFalse(RelationTableOptions.ARTIFACT_NAME + " should not be empty", artifactName.isEmpty());
            assertFalse(CoreAttributeTypes.MarkdownContent.getName() + " should not be empty",
               markdownContent.isEmpty());
         }
      }
   }

   @Test
   public void testArtifactAppendixTables() {
      boolean foundArtAppendixTable = false;

      Elements tables = htmlDoc.select("table");
      assertFalse("There should be tables in the document", tables.isEmpty());

      for (Element table : tables) {
         // Check for a main header that contains expected text.
         Element headerRow = table.selectFirst("tr");
         if (headerRow == null || !headerRow.text().contains(ArtifactAppendixTableBuilder.HEADING)) {
            continue;
         }

         foundArtAppendixTable = true;

         // Sub-headers expected on second row
         Elements headerRows = table.select("tr");
         assertFalse("Expected at least two header rows", headerRows.size() < 2);

         Elements subHeaders = headerRows.get(1).select("th");
         assertEquals("There should be 3 sub-headers", 3, subHeaders.size());
         assertEquals(ArtifactAppendixTableBuilder.columns.get(0), subHeaders.get(0).text());
         assertEquals(ArtifactAppendixTableBuilder.columns.get(1), subHeaders.get(1).text());
         assertEquals(ArtifactAppendixTableBuilder.columns.get(2), subHeaders.get(2).text());

         // Data rows start after header rows
         for (int i = 2; i < headerRows.size(); i++) {
            Elements cols = headerRows.get(i).select("td");
            assertEquals("Each row should have 3 columns", 3, cols.size());

            String name = cols.get(0).text().trim();
            String id = cols.get(1).text().trim();
            String content = cols.get(2).text().trim();

            assertFalse(ArtifactAppendixTableBuilder.ARTIFACT_ID + " should not be empty", id.isEmpty());
            assertTrue(ArtifactAppendixTableBuilder.ARTIFACT_ID + " should be numeric", id.matches("\\d+"));
            assertFalse(ArtifactAppendixTableBuilder.ARTIFACT_NAME + " should not be empty", name.isEmpty());
            assertFalse(ArtifactAppendixTableBuilder.ARTIFACT_CONTENT + " should not be empty", content.isEmpty());
         }

         // Check if the table is followed by a paragraph
         Element nextElement = table.nextElementSibling();
         assertNotNull("There should be a element following the table.", nextElement);
         assertTrue("The element after the appendix table should be a Data Right paragraph.",
            nextElement.tagName().equals("p"));

         // Table is wrapped in <p> and followed by an anchor and caption, so three siblings ahead will be a data right marking.
         nextElement = nextElement.nextElementSibling().nextElementSibling().nextElementSibling();

         // Check if the paragraph contains the expected string
         String expectedDataRightsString = headerRow.text().split(" - ")[0].toUpperCase();
         assertTrue(
            "The paragraph should contain the string: \"" + expectedDataRightsString + "\". Text is: " + nextElement.text(),
            StringUtils.containsIgnoreCase(nextElement.text(), expectedDataRightsString));
      }

      assertTrue("Should find at least one artifact appendix table", foundArtAppendixTable);
   }

   @Test
   public void testHtmlForUnclosedTags() {
      // Ensure <head>, <style>, and <body> tags are present
      assertTrue("The document should have a <head> tag", !htmlDoc.select("head").isEmpty());
      assertTrue("The document should have a <style> tag within <head>", !htmlDoc.select("head style").isEmpty());
      assertTrue("The document should have a <body> tag", !htmlDoc.select("body").isEmpty());

      Elements allTags = htmlDoc.getAllElements();

      // Define a set of tags to ignore in the checks (these tags can be empty or self-closing)
      Set<String> ignoredTags = Set.of("html", "body", "head", "#root", "meta", "style", "br", "hr", "img");

      // Check for unclosed tags
      for (Element element : allTags) {
         String tagName = element.tagName();

         // Skip checking ignored tags
         if (!ignoredTags.contains(tagName)) {
            // Get the outer HTML of the element
            String outerHtml = element.outerHtml();

            // Check if the tag is properly closed
            if (!outerHtml.endsWith("</" + tagName + ">") && !element.isBlock()) {
               fail("Element <" + tagName + "> is missing a closing tag or is improperly closed");
            }
         }
      }
   }

   @Test
   public void testImageReferences() {
      assertNotNull("Image names should not be null.", imageNames);
      assertFalse("Image names should not be empty.", imageNames.isEmpty());

      HashSet<String> foundImages = new HashSet<>();

      // Select all <img> elements in the HTML document
      Elements imgElements = htmlDoc.select("img");
      for (Element img : imgElements) {
         String imageUrl = img.attr("src");
         foundImages.add(imageUrl);
      }

      assertEquals("The found images do not match the expected image names.", imageNames, foundImages);
   }

   @Test
   public void testArtifactLinks() {
      // Define expected artifact link text and their reference IDs
      ArtifactToken roboCamera = DemoArtifactToken.RobotCameraVisualization;
      ArtifactToken virtFix = DemoArtifactToken.VirtualFixtures;

      Map<String, String> expectedLinks =
         Map.of(roboCamera.getName(), roboCamera.getIdString(), virtFix.getName(), virtFix.getIdString());

      Pattern illegalTagPattern = Pattern.compile("<artifact-link>(\\d+)</artifact-link>");

      String rawHtml = htmlDoc.outerHtml();

      // Check for illegal <artifact-link> tag
      Matcher illegalTagMatcher = illegalTagPattern.matcher(rawHtml);
      assertFalse("Illegal <artifact-link> tag found in HTML", illegalTagMatcher.find());

      // Collect actual links
      Map<String, String> foundLinks = new HashMap<>();
      Elements links = htmlDoc.select("a[href]");

      for (Element link : links) {
         String linkText = link.text().trim();
         String href = link.attr("href").trim();

         if (expectedLinks.containsKey(linkText)) {
            // Remove leading '#' from URL
            if (href.startsWith("#")) {
               href = href.substring(1);
            }
            foundLinks.put(linkText, href);
         }
      }

      // Validate all expected links exist and point to correct ID
      for (Map.Entry<String, String> expected : expectedLinks.entrySet()) {
         String expectedName = expected.getKey();
         String expectedId = expected.getValue();

         assertTrue("Missing link with text: " + expectedName + " in HTML", foundLinks.containsKey(expectedName));
         assertEquals("Incorrect href target for link: " + expectedName, expectedId, foundLinks.get(expectedName));

         // Ensure a matching anchor exists somewhere in the doc
         Elements anchors = htmlDoc.select("a[id=\"" + expectedId + "\"]");
         boolean matchingAnchor = anchors.stream().anyMatch(a -> a.text().isEmpty() && a.children().isEmpty());
         assertTrue("Missing <a id=\"" + expectedId + "\"></a> tag for link to '" + expectedName, matchingAnchor);
      }
   }

   @Test
   public void testAplicabilityTagging() {
      String speakerAText = "(e.g., 20 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±.5 dB";
      String speakerBText = "(e.g., 45 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±1 dB";

      String speakerABText = "The speaker shall have a water-resistant rating of IPX4.";
      String speakerCDText = "The speaker shall have a water-resistant rating of IPX5.";

      ApplicabilityTagTestCase productATestCase =
         new ApplicabilityTagTestCase(product_a_id, false, speakerAText, speakerABText);

      String docText = htmlDoc.text();

      String robotArmLightFeature = "The light shall support variable brightness levels from 10% to 100%";
      assertEquals(
         "Incorrect ROBOT_ARM_LIGHT feature inclusion/exclusion for product A, ID: " + productATestCase.productId,
         productATestCase.expectsLight, docText.contains(robotArmLightFeature));

      assertTrue("Expected speaker text missing for product A, ID: " + productATestCase.productId,
         docText.contains(productATestCase.expectedSpeakerText));
      assertTrue("Expected speaker text missing for product A, ID: " + productATestCase.productId,
         docText.contains(productATestCase.expectedSpeakerGroupText));

      String unexpectedSpeaker =
         productATestCase.expectedSpeakerText.equals(speakerAText) ? speakerBText : speakerAText;
      String unexpectedSpeakerGroupText =
         productATestCase.expectedSpeakerGroupText.equals(speakerABText) ? speakerCDText : speakerABText;

      assertFalse("Unexpected speaker text found for product A, ID: " + productATestCase.productId,
         docText.contains(unexpectedSpeaker));
      assertFalse("Unexpected speaker group text found for product A, ID: " + productATestCase.productId,
         docText.contains(unexpectedSpeakerGroupText));

   }

   @Test
   public void testDataRightsClassifications() {
      Elements elements = htmlDoc.body().children();

      Deque<String> classificationStack = new ArrayDeque<>();
      boolean insideAnyClassification = false;
      boolean isInsideRestricted = false;
      boolean artifact1970889096FoundInsideRestricted = false;

      for (int i = 0; i < elements.size(); i++) {
         Element elem = elements.get(i);

         // Check for classification START: <hr> + <p>
         if (isClassificationHr(elem) && i + 1 < elements.size()) {
            Element next = elements.get(i + 1);

            if (next.tagName().equals("p")) {
               if (isClassificationText(next.text(), false)) {
                  if (insideAnyClassification) {
                     fail("Overlapping classification block detected (PROPRIETARY)");
                  }
                  classificationStack.push("PROPRIETARY");
                  insideAnyClassification = true;
                  i++; // skip <p>
                  continue;
               } else if (isClassificationText(next.text(), true)) {
                  if (insideAnyClassification) {
                     fail("Overlapping classification block detected (RESTRICTED)");
                  }
                  classificationStack.push("RESTRICTED");
                  insideAnyClassification = true;
                  isInsideRestricted = true;
                  i++; // skip <p>
                  continue;
               }
            }
         }

         // Check for classification END: <p> + <hr>
         if (elem.tagName().equals("p") && i + 1 < elements.size()) {
            Element next = elements.get(i + 1);
            String text = elem.text();

            if (isClassificationHr(next)) {
               if (isClassificationText(text,
                  false) && !classificationStack.isEmpty() && "PROPRIETARY".equals(classificationStack.peek())) {
                  classificationStack.pop();
                  insideAnyClassification = false;
                  i++; // skip <hr>
                  continue;
               } else if (isClassificationText(text,
                  true) && !classificationStack.isEmpty() && "RESTRICTED".equals(classificationStack.peek())) {
                  classificationStack.pop();
                  insideAnyClassification = false;
                  isInsideRestricted = false;
                  i++; // skip <hr>
                  continue;
               }
            }
         }

         // Check if artifact is found inside RESTRICTED block
         if (elem.tagName().equals("a") && "1970889096".equals(elem.id()) && isInsideRestricted) {
            artifact1970889096FoundInsideRestricted = true;
         }
      }

      assertTrue("Unclosed data rights classification block(s) found.", classificationStack.isEmpty());

      assertTrue("Artifact 1970889096 is not wrapped in a RESTRICTED RIGHTS block.",
         artifact1970889096FoundInsideRestricted);

   }

   @Test
   public void testPublishWithTemplateMdContentTocs() {

      String html = htmlDoc.html();

      // Check Generic TOCs
      Elements tocElements = htmlDoc.select(".toc");
      checkTocsAreRendered(html, tocElements, MarkdownHtmlUtil.TOC_PATTERN_STRING);

      // Check Figure TOCs
      Elements figureTocElements = htmlDoc.select(".figure-caption-toc");
      checkTocsAreRendered(html, figureTocElements, Pattern.quote(MarkdownHtmlUtil.FIGURE_TOC_STRING));
      checkTocsAreCorrect(html, figureTocElements, "Figure");

      // Check Table TOCs
      Elements tableTocElements = htmlDoc.select(".table-caption-toc");
      checkTocsAreRendered(html, tableTocElements, Pattern.quote(MarkdownHtmlUtil.TABLE_TOC_STRING));
      checkTocsAreCorrect(html, tableTocElements, "Table");
   }

   @Test
   public void testCaptions() {

      String html = htmlDoc.html();

      Elements figureCaptionElements = htmlDoc.select(".figure-caption");
      checkCaptionsAreRendered(html, figureCaptionElements, MarkdownHtmlUtil.FIGURE_CAPTION_PATTERN);

      Elements tableCaptionElements = htmlDoc.select(".table-caption");
      checkCaptionsAreRendered(html, tableCaptionElements, MarkdownHtmlUtil.TABLE_CAPTION_PATTERN);

   }

   private boolean isClassificationHr(Element element) {
      return element.tagName().equals("hr") && "border: 5px double #000;".equals(element.attr("style").trim());
   }

   private boolean isClassificationText(String text, boolean restricted) {
      if (restricted) {
         return text.contains("RESTRICTED RIGHTS") && text.contains("Contract No");
      } else {
         return text.contains("PROPRIETARY") && text.contains("Unpublished Work");
      }
   }

   public boolean containsExcludedHeading(String heading) {
      // Check if any excluded heading is a substring of the supplied heading
      for (String excluded : excludedHeadings) {
         if (heading.contains(excluded)) {
            return true;
         }
      }
      return false;
   }

   private void checkTocsAreRendered(String html, Elements tocElements, String tocPatternString) {

      Pattern tocPattern = Pattern.compile(tocPatternString);
      Matcher tocMatcher = tocPattern.matcher(html);

      assertFalse("There should not be any unrendered TOC tags matching \"" + tocPatternString + "\". HTML: " + html,
         tocMatcher.find());
      assertFalse("A rendered TOC element for \"" + tocPatternString + "\" should have been found. HTML: " + html,
         tocElements.isEmpty());
   }

   private void checkCaptionsAreRendered(String html, Elements captionElements, String captionPatternString) {

      Pattern captionPattern = Pattern.compile(captionPatternString);
      Matcher captionMatcher = captionPattern.matcher(html);

      assertFalse(
         "There should not be any unrendered caption tags matching \"" + captionPatternString + "\". HTML: " + html,
         captionMatcher.find());
      assertFalse(
         "A rendered caption element for \"" + captionPatternString + "\" should have been found. HTML: " + html,
         captionElements.isEmpty());
   }

   private void checkTocsAreCorrect(String html, Elements tocElements, String tocType) {
      Document htmlDoc = Jsoup.parse(html);

      // Ensure TOC list monotonically increases
      int previousNumber = 0;
      for (Element tocElement : tocElements) {
         Elements listItems = tocElement.select("li");
         for (Element listItem : listItems) {
            String content = listItem.select("a").text();

            assertTrue("List item should contain \"" + tocType + "\" but was: " + content, content.startsWith(tocType));

            int currentNumber = extractNumberFromContent(content);
            if (currentNumber <= previousNumber) {
               throw new AssertionError("TOC list is not monotonically increasing");
            }
            previousNumber = currentNumber;
         }
      }

      // Verify TOC list maps to anchors that exist
      for (Element tocElement : tocElements) {
         Elements listItems = tocElement.select("li");
         for (Element listItem : listItems) {
            String href = listItem.select("a").attr("href");
            String anchorId = href.substring(1); // Remove the leading '#'
            if (htmlDoc.select("a[id=" + anchorId + "]").isEmpty()) {
               throw new AssertionError("TOC list item does not map to an existing anchor: " + href);
            }
         }
      }

      // Check number of TOC list items matches number of captions
      for (Element tocElement : tocElements) {
         String tocClass = tocElement.className();
         String captionClass = tocClass.replace("-toc", "");
         Elements captions = htmlDoc.select("." + captionClass);
         Elements listItems = tocElement.select("li");
         if (captions.size() != listItems.size()) {
            throw new AssertionError("Number of TOC list items does not match number of captions");
         }
      }
   }

   private int extractNumberFromContent(String content) {
      int colonIndex = content.indexOf(':');
      if (colonIndex == -1) {
         throw new AssertionError("Invalid content format: " + content);
      }
      String numberString = content.substring(0, colonIndex).replaceAll("[^0-9]", "");
      try {
         return Integer.parseInt(numberString);
      } catch (NumberFormatException e) {
         throw new AssertionError("Invalid number format in content: " + content);
      }
   }
}
