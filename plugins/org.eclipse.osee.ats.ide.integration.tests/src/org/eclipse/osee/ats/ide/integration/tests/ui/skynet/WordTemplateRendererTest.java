/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.eclipse.osee.framework.core.util.RendererOption.BRANCH;
import static org.eclipse.osee.framework.core.util.RendererOption.COMPARE_BRANCH;
import static org.eclipse.osee.framework.core.util.RendererOption.EXCLUDE_ARTIFACT_TYPES;
import static org.eclipse.osee.framework.core.util.RendererOption.EXCLUDE_FOLDERS;
import static org.eclipse.osee.framework.core.util.RendererOption.FIRST_TIME;
import static org.eclipse.osee.framework.core.util.RendererOption.LINK_TYPE;
import static org.eclipse.osee.framework.core.util.RendererOption.MAINTAIN_ORDER;
import static org.eclipse.osee.framework.core.util.RendererOption.NO_DISPLAY;
import static org.eclipse.osee.framework.core.util.RendererOption.PUBLISH_DIFF;
import static org.eclipse.osee.framework.core.util.RendererOption.RECURSE_ON_LOAD;
import static org.eclipse.osee.framework.core.util.RendererOption.RESULT_PATH_RETURN;
import static org.eclipse.osee.framework.core.util.RendererOption.SKIP_ERRORS;
import static org.eclipse.osee.framework.core.util.RendererOption.TRANSACTION_OPTION;
import static org.eclipse.osee.framework.core.util.RendererOption.UPDATE_PARAGRAPH_NUMBERS;
import static org.eclipse.osee.framework.core.util.RendererOption.USE_TEMPLATE_ONCE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotForEclipseOrgRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * @author Mark Joy
 * @link: MSWordTemplateClientRenderer
 */

public class WordTemplateRendererTest {

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
         .around( new NotForEclipseOrgRule() ) //<--ToDo: Remove with TW22315
         ;
   //@formatter:on

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";
   private static final String beginLinkInsert = "</w:t></w:r>OSEE_LINK(";
   private static final String endLinkInsert = ")<w:r><w:t>";
   private static final String tabString = "wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"";
   private static final Pattern findSetRsidR = Pattern.compile("wsp:rsidR=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRDefault =
      Pattern.compile("wsp:rsidRDefault=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findBlankPage = Pattern.compile("This page is intentionally left blank");
   private static final Pattern findHlinks =
      Pattern.compile("<w:hlink w:dest=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);

   private static String RECURSE_TEMPLATE_STRING;
   private static String SINGLE_TEMPLATE_STRING;
   private static String SINGLE_TEMPLATE_WITH_ATTRIBUTES_STRING;

   private static String MASTER_TEMPLATE_STRING;
   private static String MASTER_TEMPLATE_STRING_IDONLY;
   private static String MASTER_TEMPLATE_STRING_IDANDNAME;
   private static String SLAVE_TEMPLATE_STRING;

   private static String MASTER_ID_RENDERER_OPTIONS;
   private static String MASTER_RENDERER_OPTIONS;
   private static String RECURSIVE_RENDERER_OPTIONS;
   private static String SINGLE_ATTRIBUTE_RENDERER_OPTIONS;
   private static String SINGLE_RENDERER_OPTIONS;
   private static String SLAVE_RENDERER_OPTIONS;

   private BranchToken rootBranch;
   private BranchToken updateBranch;
   private Artifact docFolder;
   private Artifact swReqFolder;
   private Artifact templateFolder;
   private Artifact recurseTemplate;
   private Artifact singleTemplate;
   private Artifact singleTemplateAttrib;
   private Artifact masterTemplate;
   private Artifact masterTemplate_idOnly;
   private Artifact masterTemplate_idAndName;
   private Artifact slaveTemplate;

   private MSWordTemplateClientRenderer renderer;

   interface ErrorTitleSupplier {
      String apply(String testName);
   }

   interface CheckStringSupplier {
      String apply(String period, String altString, String pubString);
   }

   interface CheckFilter {
      Boolean apply(Boolean mergeFlag, Boolean fieldcodeFlag);
   };

   private static class Check {
      ErrorTitleSupplier errorTitleSupplier;
      CheckStringSupplier checkStringSupplier;
      CheckFilter checkFilter;

      Check(ErrorTitleSupplier errorTitleSupplier, CheckStringSupplier checkStringSupplier, CheckFilter checkFilter) {
         this.errorTitleSupplier = errorTitleSupplier;
         this.checkStringSupplier = checkStringSupplier;
         this.checkFilter = checkFilter;
      }

      void perform(String testName, String document, String period, String altString, String pubString, Boolean mergeFlag, Boolean fieldcodeFlag) {

         if (this.checkFilter.apply(mergeFlag, fieldcodeFlag)) {

            var checkString = this.checkStringSupplier.apply(period, altString, pubString);

            var result = document.contains(checkString);

            if (!result) {
               var message =
                  new Message().title(this.errorTitleSupplier.apply(testName)).indentInc().segment("Check String",
                     checkString).follows("Document", document).toString();

               Assert.assertTrue(message, false);
            }

         }
      }
   };

   @BeforeClass
   public static void loadTemplateInfo() throws Exception {
      RECURSE_TEMPLATE_STRING = getResourceData("wordrenderer_recurse.xml");
      SINGLE_TEMPLATE_STRING = getResourceData("wordrenderer_single.xml");
      SINGLE_TEMPLATE_WITH_ATTRIBUTES_STRING = getResourceData("wordrenderer_single_attrib.xml");
      MASTER_TEMPLATE_STRING = getResourceData("wordrenderer_master.xml");
      MASTER_TEMPLATE_STRING_IDONLY = getResourceData("wordrenderer_master-idonly.xml");
      MASTER_TEMPLATE_STRING_IDANDNAME = getResourceData("wordrenderer_master-idandname.xml");
      SLAVE_TEMPLATE_STRING = getResourceData("wordrenderer_slave.xml");

      MASTER_ID_RENDERER_OPTIONS = getResourceData("MasterIdRendererOptions.json");
      MASTER_RENDERER_OPTIONS = getResourceData("MasterRendererOptions.json");
      RECURSIVE_RENDERER_OPTIONS = getResourceData("RecursiveRendererOptions.json");
      SINGLE_ATTRIBUTE_RENDERER_OPTIONS = getResourceData("SingleAttributeRendererOptions.json");
      SINGLE_RENDERER_OPTIONS = getResourceData("SingleRendererOptions.json");
      SLAVE_RENDERER_OPTIONS = getResourceData("SlaveRendererOptions.json");
   }

   @Before
   public void setUp() {
      // Establish default option settings
      HashMap<RendererOption, Object> rendererOptionsMap = new HashMap<>();
      rendererOptionsMap.put(BRANCH, null);
      rendererOptionsMap.put(COMPARE_BRANCH, null);
      rendererOptionsMap.put(PUBLISH_DIFF, true);
      rendererOptionsMap.put(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_NAME);
      rendererOptionsMap.put(UPDATE_PARAGRAPH_NUMBERS, false);
      rendererOptionsMap.put(TRANSACTION_OPTION, null);
      rendererOptionsMap.put(SKIP_ERRORS, true);
      rendererOptionsMap.put(EXCLUDE_FOLDERS, true);
      rendererOptionsMap.put(EXCLUDE_ARTIFACT_TYPES, new ArrayList<ArtifactTypeToken>());
      rendererOptionsMap.put(RECURSE_ON_LOAD, true);
      rendererOptionsMap.put(MAINTAIN_ORDER, true);
      rendererOptionsMap.put(USE_TEMPLATE_ONCE, true);
      rendererOptionsMap.put(FIRST_TIME, true);
      rendererOptionsMap.put(NO_DISPLAY, true);
      rendererOptionsMap.put(RendererOption.PUBLISH_EMPTY_HEADERS, true);

      renderer = new MSWordTemplateClientRenderer(rendererOptionsMap);

      String branchName = method.getQualifiedTestName();
      rootBranch = BranchManager.createTopLevelBranch(branchName);
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(DemoUsers.Joe_Smith),
         rootBranch, PermissionEnum.FULLACCESS);

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(rootBranch);

      templateFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, rootBranch, "Templates");
      swReqFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, rootBranch, "Software Requirements");
      docFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, rootBranch, "Document Folder");

      programRoot.addChild(docFolder);
      programRoot.addChild(templateFolder);
      programRoot.addChild(swReqFolder);

      setupTemplates(templateFolder, rootBranch);
      templateFolder.persist("TEMPLATE FOLDER SETUP");

      setUpDocFolder(docFolder, rootBranch);
      docFolder.persist("DOCUMENT FOLDER SETUP");

      setUpSWReq(swReqFolder, rootBranch);
      swReqFolder.persist("SOFTWARE REQUIREMENTS SETUP");

      String workingBranchName = String.format("%s.child_branch", method.getQualifiedTestName());
      updateBranch = BranchManager.createWorkingBranch(rootBranch, workingBranchName);
      setUpDocChanges(docFolder);
   }

   @After
   public void tearDown() throws Exception {
      if (BranchManager.branchExists(updateBranch)) {
         BranchManager.purgeBranch(updateBranch);
      }
      if (BranchManager.branchExists(rootBranch)) {
         BranchManager.purgeBranch(rootBranch);
      }
   }

   //@formatter:off
   private final List<Check> testBlankWordTemplateContentChecks =
      List.of
         (
            new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 1. Volume 4", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"1" + period + altString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Volume 4 [MERGED]</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

            new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 2.", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"2" + period + altString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Intro</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   )
         );
   //@formatter:on

   @Test
   public void testBlankWordTemplateContent() {

      /*
       * Create "Root Branch"
       */

      BranchToken rootBranch = BranchManager.createTopLevelBranch("Root Branch");
      Artifact volume4ArtifactRootBranch = null;

      {
         //@formatter:off
         /*
          * Create the following artifact structure:
          *
          * +- "Volume 4" (HeadingMsWord)
          *    |
          *    +- "Intro" (HeadingMsWord)
          */
         //@formatter:on

         ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(DemoUsers.Joe_Smith),
            rootBranch, PermissionEnum.FULLACCESS);

         SkynetTransaction tx = TransactionManager.createTransaction(rootBranch, method.getQualifiedTestName());

         volume4ArtifactRootBranch =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, rootBranch, "Volume 4");
         volume4ArtifactRootBranch.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "1");
         volume4ArtifactRootBranch.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent,
            "<w:p><w:r><w:t>Volume 4 on the Root Branch.</w:t></w:r></w:p>");

         volume4ArtifactRootBranch.persist(tx);

         Artifact introArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, rootBranch, "Intro");
         introArt.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent,
            "<w:p><w:r><w:t>Intro on the Root Branch.</w:t></w:r></w:p>");

         volume4ArtifactRootBranch.addChild(introArt);
         introArt.persist(tx);
         tx.execute();
      }

      /*
       * Create "Middle Branch"
       */

      BranchId middleBranch = BranchManager.createWorkingBranch(rootBranch, "Middle Branch");

      {
         /*
          * Get the "Volume 4" artifact on the "Middle Branch" and set the WordTemplateContent to a space.
          */

         Artifact volume4ArtifactMiddleBranch =
            ArtifactQuery.getArtifactFromId(volume4ArtifactRootBranch, middleBranch);

         volume4ArtifactMiddleBranch.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent,
            "<w:p><w:r><w:t>Volume 4 on the Middle Branch.</w:t></w:r></w:p>");
         volume4ArtifactMiddleBranch.persist("Added Volume 4 artifact on the Middle Branch.");
      }

      /*
       * Create "Child Branch"
       */

      BranchId childBranch = BranchManager.createWorkingBranch(middleBranch, "Child Branch");

      Artifact volume4ArtifactChildBranch = ArtifactQuery.getArtifactFromId(volume4ArtifactRootBranch, childBranch);

      {
         volume4ArtifactChildBranch.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent,
            "<w:p><w:r><w:t>Volume 4 on the Child Branch.</w:t></w:r></w:p>");
         volume4ArtifactChildBranch.persist("Added Volume 4 artifact on the Child Branch.");
      }

      /*
       * Setup comparison publish between the root and child branches
       */

      this.renderer.updateOption(BRANCH, childBranch);
      this.renderer.updateOption(PUBLISH_DIFF, true);
      this.renderer.updateOption(COMPARE_BRANCH, rootBranch);

      this.renderer.publish(singleTemplate, null, Collections.singletonList(volume4ArtifactChildBranch));

      /*
       * Check publish results
       */

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);

      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);

      String documentContent = null;

      try {
         documentContent = getFileAsString(resultPath);
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
      }

      //@formatter:off
      var document = documentContent;
      var testName = method.getQualifiedTestName();
      var altString = "  \"";
      var period = ".";

      testBlankWordTemplateContentChecks.forEach
         (
            ( check ) -> check.perform(testName, document, period, altString, "", false, false )
         );
   }

   @Test
   public void testPublishWithoutDiff() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(docFolder);
      renderer.publish(singleTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, tabString, false, false);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithDiff() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, "", false, false);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithoutDiffRecurseTemplate() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(docFolder);
      renderer.publish(recurseTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, tabString, false, false);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithDiffRecurseTemplate() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(recurseTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, "", false, false);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithDiffMerge() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      modifyOption(COMPARE_BRANCH, rootBranch);
      modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);


         String testString;

         testString = "<aml:content><w:r><w:t>paragraph describes</w:t></w:r></aml:content>";
         Assert.assertTrue("Merge content \"paragraph describes\" added not found.", contents.contains(testString));

         testString = "<aml:content><w:r><w:delText>is</w:delText></w:r></aml:content>";
         Assert.assertTrue("Merge content \"is\" deleted not found.", contents.contains(testString));

         testString = "</aml:annotation><w:r><w:t> the background of the </w:t></w:r><aml:annotation";
         Assert.assertTrue("Merge content \" the background of the \" not changed not found.", contents.contains(testString));

         testString = "<aml:content><w:r><w:t>doc</w:t></w:r></aml:content>";
         Assert.assertTrue("Merge content \"doc\" added not found.", contents.contains(testString));

         testString = "<aml:content><w:r><w:delText>document</w:delText></w:r></aml:content>";
         Assert.assertTrue("Merge content \"document\" deleted not found.", contents.contains(testString));

         Assert.assertTrue("Paragraph Number only Link not found",
            contents.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1</w:t></w:r>"));
         basicDocumentCheck(contents, "", true, false);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithDiffLinks() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      modifyOption(COMPARE_BRANCH, null);
      modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplateAttrib, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         Assert.assertTrue("Paragraph Number & Name Link not found",
            contents.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1 Hardware</w:t></w:r>"));
         basicDocumentCheck(contents, "", false, false);
         // Need to replace word created ids with something consistent for testing
         Matcher m = findSetRsidR.matcher(contents);
         while (m.find()) {
            String rev = m.group();
            contents = contents.replace(rev, "wsp:rsidR=\"TESTING\"");
         }
         m = findSetRsidRDefault.matcher(contents);
         while (m.find()) {
            String rev = m.group();
            contents = contents.replace(rev, "wsp:rsidRDefault=\"TESTING\"");
         }

         Matcher m2 = Pattern.compile(
            "<w:r><w:t>Notes</w:t></w:r></w:p><w:p wsp:rsidR=\"TESTING\" wsp:rsidRDefault=\"TESTING\".*?><w:r><w:t> Paragraph Number: 3</w:t></w:r>").matcher(
               contents);
         Assert.assertTrue("Original Paragram Numbering for Notes is incorrect", m2.find());

         Matcher m3 = Pattern.compile(
            "<w:r><w:t>More Notes</w:t></w:r></w:p><w:p wsp:rsidR=\"TESTING\" wsp:rsidRDefault=\"TESTING\".*?><w:r><w:t> Paragraph Number: 3.1</w:t></w:r>").matcher(
               contents);
         Assert.assertTrue("Original Paragram Numbering for More Notes is incorrect", m3.find());
         m = findBlankPage.matcher(contents);
         int counter = 0;
         while (m.find()) {
            counter++;
         }
         Assert.assertEquals("Number of blank pages should only be 1", 1, counter);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithoutDiffUpdateParagraphNumbers() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(updateBranch, String.format("%s", method.getQualifiedTestName()));
      modifyOption(BRANCH, updateBranch);
      modifyOption(TRANSACTION_OPTION, transaction);
      modifyOption(PUBLISH_DIFF, false);
      modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      modifyOption(UPDATE_PARAGRAPH_NUMBERS, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplateAttrib, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         System.out.println(contents);
         Assert.assertTrue("Paragraph Number & Name Link not found",
            contents.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1 Hardware</w:t></w:r>"));
         basicDocumentCheck(contents, tabString, false, false);
         Assert.assertTrue("Paragraph Number 2 is not updated",
            contents.contains("<w:p><w:r><w:t> Paragraph Number: </w:t></w:r><w:r><w:t>2</w:t></w:r>"));
         Assert.assertTrue("Paragraph Number 2.1 is not updated",
            contents.contains("<w:p><w:r><w:t> Paragraph Number: </w:t></w:r><w:r><w:t>2.1</w:t></w:r>"));
         Matcher m = findBlankPage.matcher(contents);
         int counter = 0;
         while (m.find()) {
            counter++;
         }
         Assert.assertEquals("Number of blank pages should only be 1", 1, counter);
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishWithDiffDontUseTemplateOnce() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      modifyOption(USE_TEMPLATE_ONCE, false);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplateAttrib, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, "", false, false);
         Matcher m = findBlankPage.matcher(contents);
         int counter = 0;
         while (m.find()) {
            counter++;
         }
         Assert.assertTrue("Number of blank pages not found", counter >= 10);

      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishSoftwareRequirements() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);
      renderer.publish(masterTemplate, slaveTemplate, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         Matcher m = findHlinks.matcher(contents);
         int counter = 0;
         int indx = resultPath.lastIndexOf(File.separator);
         String justPath = resultPath.substring(0, indx + 1);
         while (m.find()) {
            String hfile = m.group();
            hfile = hfile.substring(17, hfile.length() - 1);
            File testFile = new File(justPath + hfile);
            Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
            counter++;
         }
         Assert.assertTrue("Did not find links to 3 files.", counter == 3);

      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishUsingIds() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);
      renderer.publish(masterTemplate_idOnly, slaveTemplate, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         Matcher m = findHlinks.matcher(contents);
         int counter = 0;
         int indx = resultPath.lastIndexOf(File.separator);
         String justPath = resultPath.substring(0, indx + 1);
         while (m.find()) {
            String hfile = m.group();
            hfile = hfile.substring(17, hfile.length() - 1);
            File testFile = new File(justPath + hfile);
            Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
            counter++;
         }
         Assert.assertTrue("Did not find links to 3 files.", counter == 3);

      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishUsingIdAndName() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);
      renderer.publish(masterTemplate_idAndName, slaveTemplate, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      String contents;
      try {
         contents = getFileAsString(resultPath);
         Matcher m = findHlinks.matcher(contents);
         int counter = 0;
         int indx = resultPath.lastIndexOf(File.separator);
         String justPath = resultPath.substring(0, indx + 1);
         while (m.find()) {
            String hfile = m.group();
            hfile = hfile.substring(17, hfile.length() - 1);
            File testFile = new File(justPath + hfile);
            Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
            counter++;
         }
         Assert.assertTrue("Did not find links to 3 files.", counter == 3);

      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishDiffWithFieldCodes() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      setupFieldCodeChange();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, "", false, true);
         Assert.assertTrue("Field Code Diff not as expected",
            contents.contains("<w:fldChar w:fldCharType=\"begin\"/>"));
      } catch (IOException ex) {
         // Do nothing - test failed
      }
   }

   @Test
   public void testPublishDiffWithOutFieldCodes() {
      modifyOption(BRANCH, updateBranch);
      modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      setupFieldCodeChange();
      UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "true");
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);
      renderer.publish(singleTemplate, null, artifacts);

      String resultPath = (String) renderer.getRendererOptionValue(RESULT_PATH_RETURN);
      Assert.assertNotEquals(String.format("%s Published Doc not found", method.getQualifiedTestName()), resultPath,
         null);
      try {
         String contents = getFileAsString(resultPath);
         basicDocumentCheck(contents, "", false, true);
         Assert.assertTrue("Appears to have Field Code Diff",
            contents.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Hardware"));
      } catch (IOException ex) {
         // Do nothing - test failed
      } finally {
         UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "false");
      }
   }

   // Create the folder to store the templates
   private void setupTemplates(Artifact folder, BranchToken branch) {
      recurseTemplate =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch, "Recurse Template");
      recurseTemplate.setSoleAttributeValue(CoreAttributeTypes.WholeWordContent, RECURSE_TEMPLATE_STRING);
      recurseTemplate.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.word PREVIEW PREVIEW_WITH_RECURSE_NO_ATTRIBUTES");
      recurseTemplate.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria,
         "org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer PREVIEW PREVIEW_WITH_RECURSE_NO_ATTRIBUTES");
      recurseTemplate.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions, RECURSIVE_RENDERER_OPTIONS);

      singleTemplate =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch, "Single Template");
      singleTemplate.setSoleAttributeValue(CoreAttributeTypes.WholeWordContent, SINGLE_TEMPLATE_STRING);
      singleTemplate.setSoleAttributeValue(CoreAttributeTypes.RendererOptions, SINGLE_RENDERER_OPTIONS);

      singleTemplateAttrib =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch, "Single With Attributes");
      singleTemplateAttrib.setSoleAttributeValue(CoreAttributeTypes.WholeWordContent,
         SINGLE_TEMPLATE_WITH_ATTRIBUTES_STRING);
      singleTemplateAttrib.setSoleAttributeValue(CoreAttributeTypes.RendererOptions, SINGLE_ATTRIBUTE_RENDERER_OPTIONS);

      masterTemplate =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch, "srsMaster Template");
      masterTemplate.setSoleAttributeFromString(CoreAttributeTypes.WholeWordContent, MASTER_TEMPLATE_STRING);
      masterTemplate.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions, MASTER_RENDERER_OPTIONS);

      masterTemplate_idOnly = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch,
         "srsMaster Template ID only");
      masterTemplate_idOnly.setSoleAttributeFromString(CoreAttributeTypes.WholeWordContent,
         MASTER_TEMPLATE_STRING_IDONLY);
      masterTemplate_idOnly.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions, MASTER_ID_RENDERER_OPTIONS);

      masterTemplate_idAndName = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch,
         "srsMaster Template ID and name");
      masterTemplate_idAndName.setSoleAttributeFromString(CoreAttributeTypes.WholeWordContent,
         MASTER_TEMPLATE_STRING_IDANDNAME);
      masterTemplate_idAndName.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions,
         MASTER_ID_RENDERER_OPTIONS);

      slaveTemplate =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, branch, "srsSlave Template");
      slaveTemplate.setSoleAttributeFromString(CoreAttributeTypes.WholeWordContent, SLAVE_TEMPLATE_STRING);
      slaveTemplate.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions, SLAVE_RENDERER_OPTIONS);

      folder.addChild(recurseTemplate);
      folder.addChild(singleTemplate);
      folder.addChild(singleTemplateAttrib);
      folder.addChild(masterTemplate);
      folder.addChild(masterTemplate_idOnly);
      folder.addChild(masterTemplate_idAndName);
      folder.addChild(slaveTemplate);
   }

   // Create the SW Requirement test artifact structure
   //@formatter:off
   /*
    Software Requirements
    |
    |----Crew Station Requirements
         |---- Communication Subsystem Crew Interface
         |---- Navigation Subsystem Crew Interface
         |---- Aircraft Systems Management Subsystem Crew Interface
         |     |---- Aircraft Drawing
         |     |---- Ventilation
    */
   //@formatter:on
   private void setUpSWReq(Artifact swReqFolder, BranchToken branch) {
      Artifact crewReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Crew Station Requirements");
      Artifact commReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch,
         "Communication Subsystem Crew Interface");
      Artifact navReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch,
         "Navigation Subsystem Crew Interface");
      Artifact airReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch,
         "Aircraft Systems Management Subsystem Crew Interface");
      Artifact airDrawReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Aircraft Drawing");
      Artifact ventReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch, "Ventilation");

      swReqFolder.addChild(crewReq);
      crewReq.addChild(USER_DEFINED, commReq);
      crewReq.addChild(USER_DEFINED, navReq);
      crewReq.addChild(USER_DEFINED, airReq);
      airReq.addChild(USER_DEFINED, airDrawReq);
      airReq.addChild(USER_DEFINED, ventReq);

      commReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Communication crew station requirements." + endWordString);
      navReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Navigation crew station requirements." + endWordString);
      airReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Aircraft Management crew station requirements." + endWordString);
      ventReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the Ventilation crew station requirements." + endWordString);
   }

   // Create the generic test document artifact structure
   //@formatter:off
   /*
    Document Folder
    |
    |----Introduction
         |---- Background
         |---- Scope
         |
         Subsystem
         |---- Hardware
         |     |---- Hardware Functions
         |---- Software
         |     |---- Software Functions
         |
         Notes
         |---- More Notes
    */
   //@formatter:on
   private void setUpDocFolder(Artifact docFolder, BranchToken branch) {
      Artifact intro = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Introduction");
      Artifact background =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SubsystemDesignMsWord, branch, "Background");
      Artifact scope = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SubsystemDesignMsWord, branch, "Scope");
      Artifact subSystem =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SubsystemDesignMsWord, branch, "Subsystem");
      Artifact hardware = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Hardware");
      Artifact hardwareFunc =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.HardwareRequirementMsWord, branch, "Hardware Functions");
      Artifact software = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Software");
      Artifact softwareFunc =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareDesignMsWord, branch, "Software Functions");
      Artifact notes = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Notes");
      Artifact morenotes = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "More Notes");

      docFolder.addChild(intro);
      intro.addChild(background);
      intro.addChild(scope);
      docFolder.addChild(subSystem);
      subSystem.addChild(hardware);
      hardware.addChild(hardwareFunc);
      subSystem.addChild(software);
      software.addChild(softwareFunc);
      docFolder.addChild(notes);
      notes.addChild(morenotes);

      intro.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Introduction section of the document." + endWordString);
      intro.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "1");
      background.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the background of the doc" + endWordString);
      background.setSoleAttributeValue(CoreAttributeTypes.SeverityCategory, "III");
      background.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "1.1");
      scope.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "The scope is the entire test" + endWordString);
      scope.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "1.2");
      subSystem.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "The following are SubSystems of the test document" + endWordString);
      subSystem.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2");
      hardware.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Hardware is an important Sub System" + endWordString);
      hardware.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.1");
      hardwareFunc.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "The first hardware function is power on switch" + endWordString);
      hardwareFunc.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.1.1");
      software.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Software is crucial to be running correctly" + endWordString);
      software.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.2");
      softwareFunc.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Hello World, is basic software." + endWordString);
      softwareFunc.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.2.1");
      notes.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Notes are great for small topics, and the link" + beginLinkInsert + hardware.getGuid() + endLinkInsert + " too." + endWordString);
      notes.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "3");
      morenotes.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "More notes to read!" + endWordString);
      morenotes.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "3.1");
   }

   // Add changes to the Document
   // 1. Change just the original branch
   // 2. Change to both the original branch and working branch
   // 3. Change to just the working branch
   private void setUpDocChanges(Artifact folder) {
      // 1.
      SkynetTransaction onRootTx = TransactionManager.createTransaction(rootBranch, "ORIG UPDATE");

      Artifact intro = folder.getDescendant("Introduction");
      Assert.assertNotNull("Cant find Introduction on branch", intro);
      intro.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Introduction section of the test document." + endWordString);
      intro.persist(onRootTx);

      // 2.
      Artifact bckgrd = folder.getDescendant("Introduction").getDescendant("Background");
      Assert.assertNotNull("Cant find Background on branch", bckgrd);
      bckgrd.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the background of the document" + endWordString);
      bckgrd.persist(onRootTx);
      onRootTx.execute();

      SkynetTransaction onChildTx = TransactionManager.createTransaction(updateBranch, "WORKING UPDATE");

      Artifact background = ArtifactQuery.getArtifactFromId(bckgrd, updateBranch);
      Assert.assertNotNull("Cant find Background on update branch", background);
      background.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This paragraph describes the background of the doc" + endWordString);
      background.persist(onChildTx);

      // 3.
      Artifact hw = folder.getDescendant("Subsystem").getDescendant("Hardware").getDescendant("Hardware Functions");
      Artifact hdwrFunc = ArtifactQuery.getArtifactFromId(hw, updateBranch);
      Assert.assertNotNull("Cant find Hardware Functions on update branch", hdwrFunc);
      hdwrFunc.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "The first hardware function of importance is the power on switch." + endWordString);
      hdwrFunc.persist(onChildTx);

      onChildTx.execute();
   }

   // Add a change to use a different hyperlink for field code diff testing
   private void setupFieldCodeChange() {
      SkynetTransaction onChildTx = TransactionManager.createTransaction(updateBranch, "WORKING UPDATE");
      String hdwrGuid =
         docFolder.getDescendant("Subsystem").getDescendant("Hardware").getDescendant("Hardware Functions").getGuid();
      Artifact notes = ArtifactQuery.getArtifactFromId(docFolder.getDescendant("Notes"), updateBranch);
      notes.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Notes are great for small topics, and the link " + beginLinkInsert + hdwrGuid + endLinkInsert + " too." + endWordString);

      notes.persist(onChildTx);
      onChildTx.execute();
   }

   private static String getResourceData(String relativePath) throws IOException {
      String value = Lib.fileToString(WordTemplateProcessorTest.class, "support/" + relativePath);
      Assert.assertTrue(Strings.isValid(value));
      return value;
   }

   private String getFileAsString(String filePath) throws IOException {
      String retStr;
      Assert.assertNotNull("File is Null", filePath);
      File doc = new File(filePath);
      retStr = Lib.fileToString(doc);
      return retStr;
   }

   private void modifyOption(RendererOption optName, Object optValue) {
      this.renderer.updateOption(optName, optValue);
   }

   //@formatter:off


   private final List<Check> basicDocumentChecks =
      List.of
         (
            new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 1. Introduction", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"1" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Introduction</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 1.1 Background", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Background</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> !mergeFlag
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 1.2 Scope", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"1.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Scope</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 2. Notes", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"2" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Notes</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 2.1 More Notes", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>More Notes</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 3. Subsystem", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"3" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Subsystem</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 3.1 Hardware", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"3.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> !fieldcodeFlag
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 3.1.1 Hardware Functions", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"3.1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware Functions</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 3.2 Software", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"3.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

             new Check
                   (
                      ( testName )                     -> String.format( "%s, Expected 3.2.1 Software Functions", testName ),
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"3.2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software Functions</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   )
         );

   //@formatter:on

   private void basicDocumentCheck(String document, String pubString, boolean merge, boolean fieldcode) {

      //@formatter:off
      var testName  = method.getQualifiedTestName();
      var altString = pubString.isEmpty() ? "  \"" : "\" ";
      var period    = pubString.isEmpty() ? "."    : "";

      basicDocumentChecks.forEach
         (
            ( check ) -> check.perform(testName, document, period, altString, pubString, merge, fieldcode )
         );
      //@formatter:on
   }
}
