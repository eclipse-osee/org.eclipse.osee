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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jaden W. Puckett
 */
public class WordTemplateContentToMarkdownContentConversionTest {
   private static ArtifactEndpoint workingBranchArtifactEndpoint =
      ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch_Markdown);

   private static String imageArtifactTempNameFromConversion = "";
   private final static BranchToken branch = DemoBranches.SAW_PL_Working_Branch_Markdown;
   private final static ArtifactToken folder = DemoArtifactToken.Folder_WtcToMarkdownConversion;

   String mdBoldsItalicsUnderline = "Dummy text with a **bold**, *italic*, and <u>underline</u>.";

   Pattern mdImageWithCaption = Pattern.compile(
      "Dummy text for image with caption.\n\n<image-link>\\d+</image-link>\n\n<image-caption>Dummy Caption</image-caption>");

   Pattern mdArtifactLinkWithArtifactId =
      Pattern.compile("Dummy text for artifact link with artifact ID <artifact-link>\\d+</artifact-link>.");;

   String mdTab =
      "Dummy text with tab:\n" + "       1) Dummy item 1\n" + "       2) Dummy item 2\n" + "       3) Dummy item 3\n" + "       4) Dummy item 4";

   String mdBulletedList = "* <u>Dummy bullet</u>: Dummy description for bullet point..";

   String mdBulletedList2 = "* **Dummy bullet 2**: Dummy description for bullet point 2.";

   String mdSubscriptSuperscript =
      "Dummy text for subscript<sub>Subscript text</sub>           Dummy text for superscript^Superscript text^";

   String mdTableSimpleCells =
      "<table-caption>Dummy Table Caption</table-caption>\n" + "\n" + "\n" + "| **PB** | **Dummy text for table cell** | **Dummy text for table cell** | **Dummy text for table cell** | **Dummy text for table cell** | **Dummy text for table cell** |\n" + "|---|---|---|---|---|---|";

   String mdNumberedList = "1. Dummy text for numbered list item.";

   String mdHeader = "# Dummy header text";

   @BeforeClass
   public static void setUpBeforeClass() {
      // Need to be a user with OseeAdmin role
      System.setProperty("user.name", DemoUsers.Jason_Michael.getLoginIds().get(0));

      // Covert word template content to Markdown content
      try (Response conversionResponse =
         workingBranchArtifactEndpoint.convertWordTemplateContentToMarkdownContent(branch, folder, false, false)) {

         assertEquals(Status.OK.getStatusCode(), conversionResponse.getStatus());

         /*
          * Ensure error log returns properly and extract name of created image artifact
          */
         String errorLog = conversionResponse.readEntity(String.class);
         assertTrue(errorLog.contains("Summary"));
         Pattern imageNamePattern = Pattern.compile("wordToMarkdownConversionImageTempName\\d+");
         Matcher matcher = imageNamePattern.matcher(errorLog);
         if (matcher.find()) {
            String imageName = matcher.group(0);
            imageArtifactTempNameFromConversion = imageName;
         }
      }
   }

   @AfterClass
   public static void tearDownAfterClass() {
      // Set the user back to Joe Smith
      System.setProperty("user.name", DemoUsers.Joe_Smith.getLoginIds().get(0));
   }

   @Test
   public void testImageArtifactCreation() {
      // Retrieve the artifact based on type and name
      Artifact newImageArtifact =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Image, imageArtifactTempNameFromConversion, branch);

      // Retrieve the relations of the artifact
      List<RelationLink> relations = newImageArtifact.getRelations(CoreRelationTypes.DefaultHierarchical_Parent);

      // Check if the artifact has the proper parent
      boolean hasProperParent = false;
      for (RelationLink rel : relations) {
         if (rel.getArtifactA().getArtifactId().equals(
            DemoArtifactToken.ImageLinkWithCaption_WtcToMarkdownConversion)) {
            hasProperParent = true;
         }
      }

      // Assert that the artifact has the proper parent
      assertTrue(hasProperParent);
   }

   // Helper to fetch the MarkdownContent from an artifact
   private String getMarkdownContent(ArtifactToken token) {
      Artifact artifact = ArtifactQuery.getArtifactFromId(token, branch);

      List<Attribute<Object>> markdownAttrs = new ArrayList<>();
      for (Attribute<Object> attr : artifact.getAttributes(CoreAttributeTypes.MarkdownContent)) {
         markdownAttrs.add(attr);
      }
      return markdownAttrs.stream().map(attr -> (String) attr.getValue()).findFirst().orElse(null);
   }

   @Test
   public void testBoldsItalicsUnderline() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.BoldItalicsUnderline_WtcToMarkdownConversion);
      assertEquals(mdBoldsItalicsUnderline, markdownContent);
   }

   @Test
   public void testArtifactLink() {
      // Using the provided regex pattern since content contains dynamic artifact IDs
      String markdownContent = getMarkdownContent(DemoArtifactToken.ArtifactLink_WtcToMarkdownConversion);
      assertNotNull("Markdown content should not be null", markdownContent);
      assertTrue("Markdown content did not match expected artifact link pattern.\nActual:\n" + markdownContent,
         mdArtifactLinkWithArtifactId.matcher(markdownContent).matches());
   }

   @Test
   public void testTabConversion() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.Tab_WtcToMarkdownConversion);
      assertEquals(mdTab, markdownContent);
   }

   @Test
   public void testBulletedList1() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.BulletedList1_WtcToMarkdownConversion);
      assertEquals(mdBulletedList, markdownContent);
   }

   @Test
   public void testBulletedList2() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.BulletedList2_WtcToMarkdownConversion);
      assertEquals(mdBulletedList2, markdownContent);
   }

   @Test
   public void testTableSimpleCells() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.TableSimpleCells_WtcToMarkdownConversion);
      assertEquals(mdTableSimpleCells, markdownContent);
   }

   @Test
   public void testNumberedList() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.NumberedList_WtcToMarkdownConversion);
      assertEquals(mdNumberedList, markdownContent);
   }

   @Test
   public void testHeader() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.Header_WtcToMarkdownConversion);
      assertEquals(mdHeader, markdownContent);
   }

   @Test
   public void testSubscriptSuperscript() {
      String markdownContent = getMarkdownContent(DemoArtifactToken.SubscriptSuperscript_WtcToMarkdownConversion);
      assertEquals(mdSubscriptSuperscript, markdownContent);
   }

   @Test
   public void testImageLinkWithCaption_PendingExpected() {
      // Using the provided regex pattern since content contains dynamic artifact IDs
      String markdownContent = getMarkdownContent(DemoArtifactToken.ImageLinkWithCaption_WtcToMarkdownConversion);
      assertNotNull("Markdown content should not be null", markdownContent);
      assertTrue("Markdown content did not match expected artifact link pattern.\nActual:\n" + markdownContent,
         mdImageWithCaption.matcher(markdownContent).matches());
   }
}
