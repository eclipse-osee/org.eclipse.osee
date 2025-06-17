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
import java.util.Arrays;
import java.util.Collections;
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
               List.of(                                                                                     /* Publishing Template Content Map Entries */
                  new PublishingTemplateContentMapEntry(
                     FormatIndicator.MARKDOWN,                                                              /* Template Content Format    */
                     "INSERT_ARTIFACT_HERE.md"                                                                 /* Template Content File Path */
                  )
               ),
               List.of(),                                                                                   /* Match Criteria      */
               new RelationTableOptions(
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
      assertNotNull("HTML document should not be null", htmlDoc);
      Elements metaCharset = htmlDoc.select("meta[charset]");
      assertFalse("HTML should contain a meta charset tag", metaCharset.isEmpty());
      assertEquals("UTF-8 charset should be used", "UTF-8", metaCharset.attr("charset"));
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
   public void testArtifactAppendixTable() {
      boolean foundArtApendixTable = false;

      Elements tables = htmlDoc.select("table");
      assertFalse("There should be tables in the document", tables.isEmpty());

      for (Element table : tables) {
         // Check for a main header that contains expected text.
         Element headerRow = table.selectFirst("tr");
         if (headerRow == null || !headerRow.text().equalsIgnoreCase(ArtifactAppendixTableBuilder.HEADER)) {
            continue;
         }

         foundArtApendixTable = true;

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
      }

      assertTrue("Should find at least one artifact appendix table", foundArtApendixTable);
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

      Pattern illegalTagPattern = Pattern.compile("<osee-artifact>(\\d+)</osee-artifact>");

      String rawHtml = htmlDoc.outerHtml();

      // Check for illegal <osee-artifact> tag
      Matcher illegalTagMatcher = illegalTagPattern.matcher(rawHtml);
      assertFalse("Illegal <osee-artifact> tag found in HTML", illegalTagMatcher.find());

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
}
