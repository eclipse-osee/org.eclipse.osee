/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
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
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownZip;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * Tests for publishing Markdown as HTML. Single template and single published document.
 */
public class PublishingMarkdownTest {

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

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   /**
    * A rule to get the method name of the currently running test.
    */

   @Rule
   public TestName testName = new TestName();

   private static ApplicabilityEndpoint applEndpoint =
      ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch_Markdown);

   private static Long product_a_id = applEndpoint.getView("Product A").getId();
   private static Long product_b_id = applEndpoint.getView("Product B").getId();
   private static Long product_c_id = applEndpoint.getView("Product C").getId();
   private static Long product_d_id = applEndpoint.getView("Product D").getId();

   private static Long[] products = new Long[] {product_a_id, product_b_id, product_c_id, product_d_id};

   //@formatter:on

   private static PublishingEndpoint publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();

   /**
    * Defines the publishing templates for the tests. The templates will be created on the Common Branch under the "OSEE
    * Configuration/Document Templates" folder.
    */

   private static String PUBLISHING_MARKDOWN_TEST_TEMPLATE_A = "PUBLISHING_MARKDOWN_TEST_TEMPLATE_A";

   //@formatter:off
   private static Supplier<List<PublishingTemplate>> publishingTemplatesSupplier = new Supplier<> () {

   @Override
   public List<PublishingTemplate> get() {
      return

      List.of(
         new PublishingTemplate(
            CoreArtifactTokens.DocumentTemplates,                                                        /* Parent Artifact Identifier */
            PUBLISHING_MARKDOWN_TEST_TEMPLATE_A,                                                         /* Name                       */
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

   private static Map<Long, Node> productMarkdownDocs = new HashMap<>();
   private static Map<Long, HashSet<String>> productImageNames = new HashMap<>();

   @BeforeClass
   public static void testSetup() {

      /*
       * Setup publishing templates
       */

      Map<String, org.eclipse.osee.framework.core.publishing.PublishingTemplate> templateMap =
         new TestPublishingTemplateBuilder(PublishingMarkdownTest.class).buildPublishingTemplates(
            PublishingMarkdownTest.publishingTemplatesSupplier);

      /**
       * Setup PublishingRequestData
       */

      var template_A = templateMap.get(PUBLISHING_MARKDOWN_TEST_TEMPLATE_A);

      PublishingTemplateRequest pubTemReq =
         new PublishingTemplateRequest(template_A.getIdentifier().toString(), FormatIndicator.MARKDOWN);

      List<ArtifactId> SystemRequirementsFolderID =
         Arrays.asList(ArtifactId.valueOf(CoreArtifactTokens.SystemRequirementsFolderMarkdown.getToken().getId()));

      for (Long viewId : products) {
         //@formatter:off
         RendererMap rendererOptions =
         RendererMap.of
            (
               RendererOption.BRANCH, DemoBranches.SAW_PL_Working_Branch_Markdown,
               RendererOption.VIEW, ArtifactId.valueOf(viewId),
               RendererOption.PUBLISHING_FORMAT,  FormatIndicator.MARKDOWN
            );
         //@formatter:on

         EnumRendererMap pubRenOpt = new EnumRendererMap(rendererOptions);

         PublishingRequestData publishingRequestData =
            new PublishingRequestData(pubTemReq, pubRenOpt, SystemRequirementsFolderID);

         /*
          * Make request
          */

         var attachment = PublishingMarkdownTest.publishingEndpoint.publishMarkdown(publishingRequestData);

         try (ZipInputStream zipInputStream = new ZipInputStream(attachment.getDataHandler().getInputStream())) {

            MarkdownZip mdZip = MarkdownHtmlUtil.processMarkdownZip(zipInputStream);

            productMarkdownDocs.put(viewId, mdZip.getMarkdownDocument());
            productImageNames.put(viewId, mdZip.getImageNames());
         } catch (IOException e) {
            throw new AssertionError("Error reading the file: " + e.getMessage(), e);
         } catch (Exception e) {
            throw new AssertionError("An unexpected error occurred: " + e.getMessage(), e);
         }
      }

   }

   @Test
   public void testForContent() {
      for (Long productId : products) {
         Node markdownNode = productMarkdownDocs.get(productId);
         assertNotNull("Markdown document should not be null for product: " + productId, markdownNode);
      }
   }

   @Test
   public void testArtifactIdsExist() {
      Pattern artifactIdPattern = Pattern.compile("Artifact Id:\\s*(\\S+)");
      boolean artIdFound = false;

      for (Long productId : products) {
         Node doc = productMarkdownDocs.get(productId);

         for (Node node = doc.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Paragraph) {
               String text = getLiteralText(node);
               String[] lines = text.split("\\r?\\n");

               for (String line : lines) {
                  Matcher matcher = artifactIdPattern.matcher(line);
                  if (matcher.find()) {
                     artIdFound = true;
                     String id = matcher.group(1);
                     assertTrue("Artifact Id [" + id + "] should be numeric", id.matches("\\d+"));
                  }
               }
            }
         }

         assertTrue("There should be artifact Ids in the document", artIdFound);
      }
   }

   @Test
   public void testMarkdownHeadingStructureAndOrder() {
      Pattern headingPattern = Pattern.compile("^(#{1,6})\\s+(\\d+(?:\\.\\d+)*)(.*?)$");

      for (Long productId : products) {
         Node markdownDoc = productMarkdownDocs.get(productId);
         boolean headingFound = false;

         List<String> headingNumbers = new ArrayList<>();

         for (Node node : markdownDoc.getChildren()) {
            if (!(node instanceof Heading)) {
               continue;
            }

            headingFound = true;

            Heading heading = (Heading) node;
            String headingText = heading.getText().toString().trim();

            Matcher matcher = headingPattern.matcher("#".repeat(heading.getLevel()) + " " + headingText);
            assertTrue("Heading must be numbered and formatted correctly: " + headingText, matcher.matches());

            String numberPart = matcher.group(2);

            int expectedLevel = numberPart.split("\\.").length;
            assertEquals("Heading level must match depth of number: " + headingText, expectedLevel, heading.getLevel());

            // Enforce that heading numbers appear in increasing order hierarchically
            headingNumbers.add(numberPart);
         }

         // Check that the list of heading numbers are in proper hierarchical order
         String previous = null;
         for (String current : headingNumbers) {
            if (previous != null) {
               int result = compareHeadingNumbers(previous, current);
               assertTrue("Heading numbers must be strictly increasing: " + previous + " -> " + current, result < 0);
            }
            previous = current;
         }

         assertTrue("There must be at least one heading.", headingFound);
      }
   }

   @Test
   public void testHeaders() {
      for (Long productId : products) {
         Node doc = productMarkdownDocs.get(productId);
         boolean headingFound = false;

         for (Node node = doc.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Heading) {
               headingFound = true;

               String headingText = getLiteralText(node);
               assertFalse("Heading should not be empty", headingText.isEmpty());

               boolean paragraphFound = false;
               boolean anchorFound = false;

               Node nextNode = node.getNext();
               while (nextNode != null && !(nextNode instanceof Heading)) {
                  if (nextNode instanceof Paragraph) {
                     String paraText = getLiteralText(nextNode);
                     if (paraText.contains("Description") && paraText.contains("Artifact Id")) {
                        paragraphFound = true;
                     }

                     Pattern pattern = Pattern.compile("<a\\s+id\\s*=\\s*\"\\d+\"\\s*></a>");
                     Matcher matcher = pattern.matcher(paraText);

                     if (matcher.find()) {
                        anchorFound = true;
                     }
                  }

                  nextNode = nextNode.getNext();
               }

               assertTrue("Each heading should be followed by an paragraph with a description and artifact id.",
                  paragraphFound);
               assertTrue("Each heading should be followed by an anchor.", anchorFound);
            }
         }

         assertTrue("There must be at least one heading.", headingFound);
      }
   }

   @Test
   public void testRelationHtmlTableData() {
      for (Long productId : products) {
         Node mdDoc = productMarkdownDocs.get(productId);
         boolean foundRelationTable = false;

         String html = HtmlRenderer.builder().build().render(mdDoc);
         Document htmlDoc = Jsoup.parse(html);

         Elements tables = htmlDoc.select("table");
         assertFalse("There should be tables in the document", tables.isEmpty());

         for (Element table : tables) {
            // Check for a main header that contains the word "Relation"
            Element headerRow = table.selectFirst("tr");
            if (headerRow == null || !headerRow.text().toLowerCase().contains("relation")) {
               continue;
            }

            foundRelationTable = true;

            // Sub-headers expected on second row
            Elements headerRows = table.select("tr");
            assertFalse("Expected at least two header rows", headerRows.size() < 2);

            Elements subHeaders = headerRows.get(1).select("th");
            assertEquals("There should be 3 sub-headers", 3, subHeaders.size());
            assertEquals(RelationTableOptions.ARTIFACT_ID, subHeaders.get(0).text());
            assertEquals(RelationTableOptions.ARTIFACT_NAME, subHeaders.get(1).text());
            assertEquals(CoreAttributeTypes.MarkdownContent.getName(), subHeaders.get(2).text());

            // Data rows start after header rows
            for (int i = 2; i < headerRows.size(); i++) {
               Elements cols = headerRows.get(i).select("td");
               assertEquals("Each row should have 3 columns", 3, cols.size());

               String id = cols.get(0).text().trim();
               String name = cols.get(1).text().trim();
               String content = cols.get(2).text().trim();

               assertFalse(RelationTableOptions.ARTIFACT_ID + " should not be empty", id.isEmpty());
               assertTrue(RelationTableOptions.ARTIFACT_ID + " should be numeric", id.matches("\\d+"));
               assertFalse(RelationTableOptions.ARTIFACT_NAME + " should not be empty", name.isEmpty());
               assertFalse(CoreAttributeTypes.MarkdownContent.getName() + " should not be empty", content.isEmpty());
            }
         }

         assertTrue("Should find at least one relation table", foundRelationTable);
      }
   }

   @Test
   public void testArtifactAppendixTable() {
      for (Long productId : products) {
         Node mdDoc = productMarkdownDocs.get(productId);
         boolean foundArtApendixTable = false;

         String html = HtmlRenderer.builder().build().render(mdDoc);
         Document htmlDoc = Jsoup.parse(html);

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
   }

   @Test
   public void testImageReferences() {
      for (Long productId : products) {
         Node mdDocument = productMarkdownDocs.get(productId);

         HashSet<String> imageNames = productImageNames.get(productId);
         assertNotNull("Image names should not be null for product ID: " + productId, imageNames);
         assertFalse("Image names should not be empty for product ID: " + productId, imageNames.isEmpty());

         HashSet<String> foundImages = new HashSet<>();

         NodeVisitor visitor = new NodeVisitor(new VisitHandler<>(Image.class, image -> {
            String imageUrl = image.getUrl().toString();
            foundImages.add(imageUrl);
         }));

         visitor.visit(mdDocument);

         assertEquals("The found images do not match the expected image names for product ID: " + productId, imageNames,
            foundImages);
      }
   }

   @Test
   public void testArtifactLinks() {
      // Define expected artifact link text and their reference IDs (without leading #)
      ArtifactToken roboCamera = DemoArtifactToken.RobotCameraVisualization;
      ArtifactToken virtFix = DemoArtifactToken.VirtualFixtures;

      Map<String, String> expectedLinks =
         Map.of(roboCamera.getName(), roboCamera.getIdString(), virtFix.getName(), virtFix.getIdString());

      Pattern artifactTagPattern = Pattern.compile("<artifact-link>(\\d+)</artifact-link>");

      for (Long productId : products) {
         Node mdDocument = productMarkdownDocs.get(productId);

         // Convert document back to raw Markdown text
         String rawMarkdown = mdDocument.getChars().toString();

         // Check for illegal <artifact-link> tag
         Matcher artifactTagMatcher = artifactTagPattern.matcher(rawMarkdown);
         assertFalse("Illegal <artifact-link> tag found in product ID: " + productId, artifactTagMatcher.find());

         // Collect expected links from Markdown
         Map<String, String> foundLinks = new HashMap<>();
         NodeVisitor visitor = new NodeVisitor(new VisitHandler<>(Link.class, link -> {
            String linkText = link.getText().toString().trim();
            String linkUrl = link.getUrl().toString().trim();

            if (expectedLinks.containsKey(linkText)) {
               // Remove leading '#' from URL
               if (linkUrl.startsWith("#")) {
                  linkUrl = linkUrl.substring(1);
               }
               foundLinks.put(linkText, linkUrl);
            }
         }));
         visitor.visit(mdDocument);

         // Validate all expected links exist
         for (Map.Entry<String, String> expected : expectedLinks.entrySet()) {
            String expectedName = expected.getKey();
            String expectedId = expected.getValue();

            assertTrue("Missing link with text: " + expectedName + " in product ID: " + productId,
               foundLinks.containsKey(expectedName));
            assertEquals("Incorrect link reference for: " + expectedName + " in product ID: " + productId, expectedId,
               foundLinks.get(expectedName));

            // Ensure an anchor exists matching the link ID
            Pattern anchorPattern = Pattern.compile("<a\\s+id\\s*=\\s*\"" + Pattern.quote(expectedId) + "\"\\s*></a>");
            Matcher anchorMatcher = anchorPattern.matcher(rawMarkdown);
            assertTrue(
               "Missing <a id=\"" + expectedId + "\"></a> tag for link to '" + expectedName + "' in product ID: " + productId,
               anchorMatcher.find());
         }
      }
   }

   @Test
   public void testApplicabilityTagging() {
      String speakerAText = "(e.g., 20 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±.5 dB";
      String speakerBText = "(e.g., 45 Hz to 20,000 Hz), with sound pressure level (SPL) accuracy within ±1 dB";

      String speakerABText = "The speaker shall have a water-resistant rating of IPX4.";
      String speakerCDText = "The speaker shall have a water-resistant rating of IPX5.";

      //@formatter:off
      List<ApplicabilityTagTestCase> testCases = List.of(
            new ApplicabilityTagTestCase(products[0], false, speakerAText, speakerABText),
            new ApplicabilityTagTestCase(products[1], true, speakerAText, speakerABText),
            new ApplicabilityTagTestCase(products[2], false, speakerBText, speakerCDText),
            new ApplicabilityTagTestCase(products[3], false, speakerBText, speakerCDText)
        );
      //@formatter:on
      int productNumber = 0;
      for (ApplicabilityTagTestCase testCase : testCases) {
         Node doc = productMarkdownDocs.get(testCase.productId);
         String docText = getLiteralText(doc);

         String robotArmLightFeature = "The light shall support variable brightness levels from 10% to 100%";
         assertEquals(
            "Incorrect ROBOT_ARM_LIGHT feature inclusion/exclusion for product " + productNumber + ", ID: " + testCase.productId,
            testCase.expectsLight, docText.contains(robotArmLightFeature));

         assertTrue("Expected speaker text missing for product " + productNumber + ", ID: " + testCase.productId,
            docText.contains(testCase.expectedSpeakerText));
         assertTrue("Expected speaker group text missing for product " + productNumber + ", ID: " + testCase.productId,
            docText.contains(testCase.expectedSpeakerGroupText));

         String unexpectedSpeakerText = testCase.expectedSpeakerText.equals(speakerAText) ? speakerBText : speakerAText;
         String unexpectedSpeakerGroupText =
            testCase.expectedSpeakerGroupText.equals(speakerABText) ? speakerCDText : speakerABText;

         assertFalse("Unexpected speaker text found for product " + productNumber + ", ID: " + testCase.productId,
            docText.contains(unexpectedSpeakerText));
         assertFalse("Unexpected speaker group text found for product " + productNumber + ", ID: " + testCase.productId,
            docText.contains(unexpectedSpeakerGroupText));

         productNumber++;
      }
   }

   private String getLiteralText(Node node) {
      StringBuilder sb = new StringBuilder();
      NodeVisitor visitor =
         new NodeVisitor(new VisitHandler<>(Text.class, text -> sb.append(text.getChars()).append("\n")),
            new VisitHandler<>(Code.class, code -> sb.append(code.getText()).append("\n")),
            new VisitHandler<>(SoftLineBreak.class, br -> sb.append("\n")),
            new VisitHandler<>(HardLineBreak.class, br -> sb.append("\n")),
            new VisitHandler<>(Link.class, link -> sb.append(link.getText())),
            new VisitHandler<>(HtmlInline.class, html -> sb.append(html.getChars())),
            new VisitHandler<>(HtmlBlock.class, html -> sb.append(html.getChars())));
      visitor.visit(node);
      return sb.toString().trim();
   }

   private int compareHeadingNumbers(String a, String b) {
      String[] partsA = a.split("\\.");
      String[] partsB = b.split("\\.");

      int len = Math.max(partsA.length, partsB.length);
      for (int i = 0; i < len; i++) {
         int numA = i < partsA.length ? Integer.parseInt(partsA[i]) : 0;
         int numB = i < partsB.length ? Integer.parseInt(partsB[i]) : 0;

         if (numA != numB) {
            return Integer.compare(numA, numB);
         }
      }
      return 0; // Equal
   }

}
