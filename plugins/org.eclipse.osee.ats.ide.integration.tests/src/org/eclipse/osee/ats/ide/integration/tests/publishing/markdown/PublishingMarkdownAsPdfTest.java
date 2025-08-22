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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestPublishingTemplateBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.table.RelationTableOptions;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.eclipse.osee.orcs.core.util.PublishingTemplateContentMapEntry;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * Tests for publishing Markdown as PDF. Single template and single published document.
 */
public class PublishingMarkdownAsPdfTest {

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

   private static String PUBLISHING_MARKDOWN_AS_PDF_TEST_TEMPLATE_A = "PUBLISHING_MARKDOWN_AS_PDF_TEST_TEMPLATE_A";

   //@formatter:off
   private static Supplier<List<PublishingTemplate>> publishingTemplatesSupplier = new Supplier<> () {

      @Override
      public List<PublishingTemplate> get() {
         return

         List.of(
            new PublishingTemplate(
               CoreArtifactTokens.DocumentTemplates,                                                        /* Parent Artifact Identifier */
               PUBLISHING_MARKDOWN_AS_PDF_TEST_TEMPLATE_A,                                                 /* Name                       */
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
                     "INSERT_ARTIFACT_HERE.md"                                                                 /* Template Content File Path */
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

   private static PDDocument pdfDoc;
   private static int numPages;

   @BeforeClass
   public static void testSetup() throws IOException {

      /*
       * Setup publishing templates
       */

      Map<String, org.eclipse.osee.framework.core.publishing.PublishingTemplate> templateMap =
         new TestPublishingTemplateBuilder(PublishingMarkdownAsPdfTest.class).buildPublishingTemplates(
            PublishingMarkdownAsPdfTest.publishingTemplatesSupplier);

      /**
       * Setup PublishingRequestData
       */

      var template_A = templateMap.get(PUBLISHING_MARKDOWN_AS_PDF_TEST_TEMPLATE_A);

      EnumRendererMap pubRenOpt = new EnumRendererMap(PublishingMarkdownAsPdfTest.rendererOptions);

      List<ArtifactId> art =
         Arrays.asList(ArtifactId.valueOf(CoreArtifactTokens.SystemRequirementsFolderMarkdown.getToken().getId()));

      PublishingTemplateRequest pubTemReq =
         new PublishingTemplateRequest(template_A.getIdentifier().toString(), FormatIndicator.MARKDOWN);

      PublishingRequestData publishingRequestData = new PublishingRequestData(pubTemReq, pubRenOpt, art);

      /*
       * Make request
       */

      PublishingMarkdownAsPdfTest.publishingEndpoint = ServiceUtil.getOseeClient().getPublishingEndpoint();

      var attachment = PublishingMarkdownAsPdfTest.publishingEndpoint.publishMarkdownAsPdf(publishingRequestData);

      assertNotNull("HTML attachment should be present",
         attachment.getContentType().getType().equals("application/pdf"));

      // Load the PDF
      try {
         pdfDoc = PDDocument.load(attachment.getDataHandler().getInputStream());
         numPages = pdfDoc.getNumberOfPages();
      } catch (IOException e) {
         throw new RuntimeException("Error loading PDF document", e);
      }
   }

   @Test
   public void testForContent() throws IOException {
      assertTrue("There should be a least one page in the pdf.", numPages > 0);

      // 1-based index
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setStartPage(1);
      stripper.setEndPage(1);

      // Get first page text
      String lastPageText = stripper.getText(pdfDoc).trim();

      assertFalse("The first page should not be blank.", lastPageText.isEmpty());
   }

   @Test
   public void testDataRightsClassifications() throws IOException {
      PDFTextStripper stripper = new PDFTextStripper();
      int numPages = pdfDoc.getNumberOfPages();

      boolean artifactSeen = false;
      boolean artifactSeenOnRestrictedPage = false;

      for (int pageNum = 1; pageNum <= numPages; pageNum++) {
         stripper.setStartPage(pageNum);
         stripper.setEndPage(pageNum);
         String pageText = stripper.getText(pdfDoc).trim();

         // Skip empty pages.
         if (pageText.isEmpty()) {
            continue;
         }

         List<String> lines = Arrays.asList(pageText.split("\\r?\\n"));
         String normalizedText = pageText.replaceAll("\\s+", " ").toUpperCase();

         boolean hasProprietary =
            normalizedText.contains("PROPRIETARY") && normalizedText.contains("EXPORT CONTROLLED INFORMATION");

         boolean hasRestricted = normalizedText.contains("RESTRICTED RIGHTS") && normalizedText.contains("CONTRACT NO");
         /*
          * Currently there is an exception for the appendix table. Remove when the table is properly split across pages
          * by classification.
          */
         if (!hasProprietary && !hasRestricted && !normalizedText.contains("LINKED ARTIFACTS APPENDIX")) {
            fail("Page " + pageNum + " does not contain any required classification footer." + normalizedText);
         }

         if (hasProprietary && hasRestricted) {
            fail("Page " + pageNum + " contains both PROPRIETARY and RESTRICTED RIGHTS classifications.");
         }

         // Ensure classification appears only once.
         int proprietaryCount = countOccurrences(normalizedText, "PROPRIETARY COPYRIGHT (C) 2025");
         int restrictedCount = countOccurrences(normalizedText, "RESTRICTED RIGHTS – EXPORT CONTROLLED");

         if (proprietaryCount > 1 || restrictedCount > 1) {
            fail("Page " + pageNum + " contains multiple classification footers.");
         }

         // Check artifact placement.
         boolean artifactOnPage = normalizedText.contains("ARTIFACT ID: 1970889096");
         if (artifactOnPage) {
            artifactSeen = true;
            if (hasRestricted) {
               artifactSeenOnRestrictedPage = true;
            } else {
               fail("Artifact 1970889096 appears on page " + pageNum + " without RESTRICTED RIGHTS classification.");
            }
         }
      }

      assertTrue("Artifact 1970889096 was not found in the document.", artifactSeen);
      assertTrue("Artifact 1970889096 must appear on a RESTRICTED RIGHTS classified page.",
         artifactSeenOnRestrictedPage);
   }

   private int countOccurrences(String text, String keyword) {
      int count = 0;
      int index = 0;
      while ((index = text.indexOf(keyword, index)) != -1) {
         count++;
         index += keyword.length();
      }
      return count;
   }

   @AfterClass
   public static void tearDown() {
      // Close the PDF document after all tests.
      if (pdfDoc != null) {
         try {
            pdfDoc.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
