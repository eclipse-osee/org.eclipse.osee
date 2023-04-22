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
import static org.eclipse.osee.framework.core.publishing.RendererOption.BRANCH;
import static org.eclipse.osee.framework.core.publishing.RendererOption.COMPARE_BRANCH;
import static org.eclipse.osee.framework.core.publishing.RendererOption.LINK_TYPE;
import static org.eclipse.osee.framework.core.publishing.RendererOption.PUBLISH_DIFF;
import static org.eclipse.osee.framework.core.publishing.RendererOption.RESULT_PATH_RETURN;
import static org.eclipse.osee.framework.core.publishing.RendererOption.TRANSACTION_OPTION;
import static org.eclipse.osee.framework.core.publishing.RendererOption.UPDATE_PARAGRAPH_NUMBERS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.USE_TEMPLATE_ONCE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
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
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
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
    * {@Link UserToken} cache has been flushed.</dd>
    * <dt>NoPopUpsRule</dt>
    * <dd>Prevents word documents from being launched for the user during tests.</dd>
    * </dl>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NoPopUpsRule() )
         .around( new NotForEclipseOrgRule() ) //<--ToDo: Remove with TW22315
         ;
   //@formatter:on

   @Rule
   public TestInfo method = new TestInfo();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static class Check {

      CheckFilter checkFilter;
      CheckStringSupplier checkStringSupplier;
      ErrorTitleSupplier errorTitleSupplier;
      boolean testIsRegex;

      Check(ErrorTitleSupplier errorTitleSupplier, CheckStringSupplier checkStringSupplier, CheckFilter checkFilter) {
         this.errorTitleSupplier = errorTitleSupplier;
         this.checkStringSupplier = checkStringSupplier;
         this.checkFilter = checkFilter;
         this.testIsRegex = false;
      }

      Check(ErrorTitleSupplier errorTitleSupplier, CheckStringSupplier checkStringSupplier, CheckFilter checkFilter, boolean testIsRegex) {
         this(errorTitleSupplier, checkStringSupplier, checkFilter);
         this.testIsRegex = testIsRegex;
      }

      void perform(String testName, String filePath, String document, String period, String altString, String pubString, Boolean mergeFlag, Boolean fieldcodeFlag) {

         if (this.checkFilter.apply(mergeFlag, fieldcodeFlag)) {

            var checkString = this.checkStringSupplier.apply(period, altString, pubString);

            //@formatter:off
            var result =
               this.testIsRegex
                  ? Pattern.compile(checkString).matcher(document).find()
                  : document.contains(checkString);

            if (!result) {
               //@formatter:off
               var message =
                  new Message()
                     .title( this.errorTitleSupplier.apply( testName ) )
                     .indentInc()
                     .segment( "Test Name",    testName    )
                     .segment( "File",         filePath    )
                     .segment( "Check String", checkString )
                     .follows( "Document",     document    )
                     .toString();
               //@formatter:on

               Assert.assertTrue(message, false);
            }

         }
      }
   }

   interface CheckFilter {
      Boolean apply(Boolean mergeFlag, Boolean fieldcodeFlag);
   }

   interface CheckStringSupplier {
      String apply(String period, String altString, String pubString);
   }

   interface ErrorTitleSupplier {
      String apply(String testName);
   }

   private static final String beginLinkInsert = "</w:t></w:r>OSEE_LINK(";
   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endLinkInsert = ")<w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";
   private static final Pattern findBlankPage = Pattern.compile("This page is intentionally left blank");
   private static final Pattern findHlinks =
      Pattern.compile("<w:hlink w:dest=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidR = Pattern.compile("wsp:rsidR=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRDefault =
      Pattern.compile("wsp:rsidRDefault=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);

   private static String MASTER_ID_RENDERER_OPTIONS;
   private static String MASTER_RENDERER_OPTIONS;
   private static String MASTER_TEMPLATE_STRING;

   private static String MASTER_TEMPLATE_STRING_IDANDNAME;
   private static String MASTER_TEMPLATE_STRING_IDONLY;
   private static String RECURSE_TEMPLATE_STRING;
   private static String RECURSIVE_RENDERER_OPTIONS;

   private static String SINGLE_ATTRIBUTE_RENDERER_OPTIONS;
   private static String SINGLE_RENDERER_OPTIONS;
   private static String SINGLE_TEMPLATE_STRING;
   private static String SINGLE_TEMPLATE_WITH_ATTRIBUTES_STRING;
   private static String SLAVE_RENDERER_OPTIONS;
   private static String SLAVE_TEMPLATE_STRING;

   private static final String tabString = "wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"";
   private static String getResourceData(String relativePath) throws IOException {
      String value = Lib.fileToString(WordTemplateProcessorTest.class, "support/" + relativePath);
      Assert.assertTrue(Strings.isValid(value));
      return value;
   }

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
   private final List<Check> basicDocumentChecks = List.of(new Check(
      (testName) -> String.format("%s, Expected 1. Introduction", testName),
      (period, altString, pubString) -> "<wx:t wx:val=\"1" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Introduction</w:t></w:r>",
      (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 1.1 Background", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Background</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> !mergeFlag),

      new Check((testName) -> String.format("%s, Expected 1.2 Scope", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"1.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Scope</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 2. Notes", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"2" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Notes</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 2.1 More Notes", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>More Notes</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 3. Subsystem", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"3" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Subsystem</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 3.1 Hardware", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"3.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> !fieldcodeFlag),

      new Check((testName) -> String.format("%s, Expected 3.1.1 Hardware Functions", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"3.1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware Functions</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 3.2 Software", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"3.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true),

      new Check((testName) -> String.format("%s, Expected 3.2.1 Software Functions", testName),
         (period, altString, pubString) -> "<wx:t wx:val=\"3.2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software Functions</w:t></w:r>",
         (mergeFlag, fieldcodeFlag) -> true));
   private String content;
   private String contentPath;
   private Artifact docFolder;
   private Artifact masterTemplate;
   private Artifact masterTemplate_idAndName;
   private Artifact masterTemplate_idOnly;

   private Artifact recurseTemplate;

   private MSWordTemplateClientRenderer renderer;

   private BranchToken rootBranch;;

   private Artifact singleTemplate;;

   private Artifact singleTemplateAttrib;

   private Artifact slaveTemplate;

   private Artifact swReqFolder;
   private Artifact templateFolder;

   //@formatter:off
   private final List<Check> testBlankWordTemplateContentChecks =
      List.of
         (
            new Check
                   (
                      ( testName )                     -> "Expected 1. Volume 4",
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"1" + period + altString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Volume 4 [MERGED]</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   ),

            new Check
                   (
                      ( testName )                     -> "Expected 2.",
                      ( period, altString, pubString ) -> "<wx:t wx:val=\"2" + period + altString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Intro</w:t></w:r>",
                      ( mergeFlag, fieldcodeFlag )     -> true
                   )
         );
   //@formatter:on

   private BranchToken updateBranch;

   private void basicDocumentCheck(String filePath, String document, String pubString, boolean merge, boolean fieldcode) {

      //@formatter:off
      var testName  = method.getQualifiedTestName();
      var altString = pubString.isEmpty() ? "  \"" : "\" ";
      var period    = pubString.isEmpty() ? "."    : "";

      basicDocumentChecks.forEach
         (
            ( check ) -> check.perform(testName, filePath, document, period, altString, pubString, merge, fieldcode )
         );
      //@formatter:on
   }

   private void blankPageCounter(int expectedCount) {
      this.blankPageCounter((count) -> count == expectedCount, Integer.toString(expectedCount));
   }

   private void blankPageCounter(IntPredicate expectedCountPredicate, String expectedMessage) {

      var blankPageMatcher = WordTemplateRendererTest.findBlankPage.matcher(this.content);
      var count = 0;
      while (blankPageMatcher.find()) {
         count++;
      }
      if (!expectedCountPredicate.test(count)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "WordTemplateRendererTest::blankPageCounter, Result document did not contain expected number of blank pages." )
                   .indentInc()
                   .segment( "Test Name",      this.method.getTestName() )
                   .segment( "File",           this.contentPath          )
                   .segment( "Expected Count", expectedMessage           )
                   .segment( "Actual Count",   count                     )
                   .follows( "Document",       this.content              )
                   .toString();
         //@formatter:on
         Assert.assertTrue(message, false);
      }
   }

   private String getFileAsString(String filePath) throws IOException {
      String retStr;
      Assert.assertNotNull("File is Null", filePath);
      File doc = new File(filePath);
      retStr = Lib.fileToString(doc);
      return retStr;
   }

   private void loadContent() {

      this.contentPath = (String) this.renderer.getRendererOptionValue(RESULT_PATH_RETURN);

      //@formatter:off
      if( Objects.isNull( this.contentPath ) ) {
         var message =
            new Message()
                   .title( "WordTemplateRendererTest::loadContent, Renderer did not supply a results path." )
                   .indentInc()
                   .segment( "Test Name", this.method.getTestName() )
                   .toString();

         Assert.assertTrue( message, false );
      }

      try {
         this.content = this.getFileAsString( this.contentPath );
      } catch (Exception e) {
         var message =
            new Message()
                   .title( "WordTemplateRenderTest::loadContent, Failed to load renderer content file." )
                   .indentInc()
                   .segment( "Test Name",    this.method.getTestName() )
                   .segment( "Content Path", this.contentPath          )
                   .toString();

         Assert.assertTrue( message, false );
      }

      // Need to replace word created ids with something consistent for testing
      Matcher m = findSetRsidR.matcher(this.content);
      while (m.find()) {
         String rev = m.group();
         this.content = this.content.replace(rev, "wsp:rsidR=\"TESTING\"");
      }
      m = findSetRsidRDefault.matcher(this.content);
      while (m.find()) {
         String rev = m.group();
         this.content = this.content.replace(rev, "wsp:rsidRDefault=\"TESTING\"");
      }

   }

   private void modifyOption(RendererOption optName, Object optValue) {
      if(Objects.nonNull(optValue)) {
      this.renderer.setRendererOption(optName, optValue);
      } else {
         this.renderer.removeRendererOption(optName);
      }
   }

   @Before
   public void setUp() {
      // Establish default option settings
      //@formatter:off
      RendererMap rendererOptionsMap =
         new EnumRendererMap
            (
              RendererOption.PUBLISH_DIFF,             true,
              RendererOption.LINK_TYPE,                LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
              RendererOption.UPDATE_PARAGRAPH_NUMBERS, false,
              RendererOption.SKIP_ERRORS,              true,
              RendererOption.EXCLUDE_FOLDERS,          true,
              RendererOption.EXCLUDE_ARTIFACT_TYPES,   new ArrayList<ArtifactTypeToken>(),
              RendererOption.RECURSE_ON_LOAD,          true,
              RendererOption.MAINTAIN_ORDER,           true,
              RendererOption.USE_TEMPLATE_ONCE,        true,
              RendererOption.FIRST_TIME,               true,
              RendererOption.NO_DISPLAY,               true,
              RendererOption.PUBLISH_EMPTY_HEADERS,    true
            );
      //@formatter:on
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

   @After
   public void tearDown() throws Exception {
      if (BranchManager.branchExists(updateBranch)) {
         BranchManager.purgeBranch(updateBranch);
      }
      if (BranchManager.branchExists(rootBranch)) {
         BranchManager.purgeBranch(rootBranch);
      }
   }

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

      this.renderer.setRendererOption(BRANCH, childBranch);
      this.renderer.setRendererOption(PUBLISH_DIFF, true);
      this.renderer.setRendererOption(COMPARE_BRANCH, rootBranch);

      this.renderer.publish(singleTemplate, null, Collections.singletonList(volume4ArtifactChildBranch));

      /*
       * Check publish results
       */

      this.loadContent();

      //@formatter:off
      var testName = method.getQualifiedTestName();
      var altString = "  \"";
      var period = ".";

      testBlankWordTemplateContentChecks.forEach
         (
            ( check ) -> check.perform(testName,this.contentPath, this.content, period, altString, "", false, false )
         );
      //@formatter:on
   }

   @Test
   public void testPublishDiffWithFieldCodes() {
      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      setupFieldCodeChange();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, true);

      Assert.assertTrue("Field Code Diff not as expected",
         this.content.contains("<w:fldChar w:fldCharType=\"begin\"/>"));
   }

   @Test
   public void testPublishDiffWithOutFieldCodes() {
      try {
         this.modifyOption(BRANCH, updateBranch);
         this.modifyOption(PUBLISH_DIFF, true);
         List<Artifact> artifacts = new ArrayList<>();
         setupFieldCodeChange();
         UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "true");
         Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
         artifacts.add(updateDoc);

         this.renderer.publish(singleTemplate, null, artifacts);

         this.loadContent();

         this.basicDocumentCheck(this.contentPath, this.content, "", false, true);

         Assert.assertTrue("Appears to have Field Code Diff",
            this.content.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Hardware"));
      } finally {
         UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "false");
      }
   }

   @Test
   public void testPublishSoftwareRequirements() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);

      this.renderer.publish(masterTemplate, slaveTemplate, artifacts);

      this.loadContent();

      Matcher m = findHlinks.matcher(this.content);
      int counter = 0;
      int indx = this.contentPath.lastIndexOf(File.separator);
      String justPath = this.contentPath.substring(0, indx + 1);
      while (m.find()) {
         String hfile = m.group();
         hfile = hfile.substring(17, hfile.length() - 1);
         File testFile = new File(justPath + hfile);
         Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
         counter++;
      }
      Assert.assertTrue("Did not find links to 3 files.", counter == 3);
   }

   @Test
   public void testPublishUsingIdAndName() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);

      this.renderer.publish(masterTemplate_idAndName, slaveTemplate, artifacts);

      this.loadContent();

      Matcher m = findHlinks.matcher(this.content);
      int counter = 0;
      int indx = this.contentPath.lastIndexOf(File.separator);
      String justPath = this.contentPath.substring(0, indx + 1);
      while (m.find()) {
         String hfile = m.group();
         hfile = hfile.substring(17, hfile.length() - 1);
         File testFile = new File(justPath + hfile);
         Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
         counter++;
      }
      Assert.assertTrue("Did not find links to 3 files.", counter == 3);
   }

   @Test
   public void testPublishUsingIds() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(swReqFolder);

      this.renderer.publish(masterTemplate_idOnly, slaveTemplate, artifacts);

      this.loadContent();

      Matcher m = findHlinks.matcher(this.content);
      int counter = 0;
      int indx = this.contentPath.lastIndexOf(File.separator);
      String justPath = this.contentPath.substring(0, indx + 1);
      while (m.find()) {
         String hfile = m.group();
         hfile = hfile.substring(17, hfile.length() - 1);
         File testFile = new File(justPath + hfile);
         Assert.assertTrue(String.format("File does not exist %s", testFile), testFile.exists());
         counter++;
      }
      Assert.assertTrue("Did not find links to 3 files.", counter == 3);
   }

   @Test
   public void testPublishWithDiff() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);
   }

   @Test
   public void testPublishWithDiffDontUseTemplateOnce() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      this.modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      this.modifyOption(USE_TEMPLATE_ONCE, false);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplateAttrib, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);

      this.blankPageCounter((count) -> count >= 10, ">= 10");
   }

   @Test
   public void testPublishWithDiffLinks() {
      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      this.modifyOption(COMPARE_BRANCH, null);
      this.modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplateAttrib, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);

      //@formatter:off
      var checks =
         List.of
            (
               new Check
                      (
                        ( testName )                     -> "Paragraph Number & Name Link not found",
                        ( period, altString, pubString ) -> "<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1 Hardware</w:t></w:r>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

                new Check
                       (
                          ( testName ) ->                     "Original Paragram Numbering for Notes is incorrect",
                          ( period, altString, pubString ) -> "<w:r><w:t>Notes</w:t></w:r></w:p><w:p wsp:rsidR=\"TESTING\" wsp:rsidRDefault=\"TESTING\".*?><w:r><w:t> Paragraph Number: 3</w:t></w:r>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                       ),
                new Check
                       (
                          ( testName )                     -> "Original Paragram Numbering for More Notes is incorrect",
                          ( period, altString, pubString ) -> "<w:r><w:t>More Notes</w:t></w:r></w:p><w:p wsp:rsidR=\"TESTING\" wsp:rsidRDefault=\"TESTING\".*?><w:r><w:t> Paragraph Number: 3.1</w:t></w:r>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                       )


      );
      //@formatter:on

      //@formatter:off
      checks.forEach
         (
            ( check ) -> check.perform
                            (
                               method.getQualifiedTestName(), /* testName       */
                               this.contentPath,              /* document path  */
                               this.content,                  /* document       */
                               "",                            /* period         */
                               "",                            /* altString      */
                               "",                            /* pubString      */
                               false,                         /* mergeFlag      */
                               false                          /* fieldcoderFlag */
                            )
         );
      //@formatter:on

      this.blankPageCounter(1);
   }

   @Test
   public void testPublishWithDiffMerge() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      this.modifyOption(COMPARE_BRANCH, rootBranch);
      this.modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", true, false);

      String testString;

      testString = "<aml:content><w:r><w:t>paragraph describes</w:t></w:r></aml:content>";
      Assert.assertTrue("Merge content \"paragraph describes\" added not found.", this.content.contains(testString));

      testString = "<aml:content><w:r><w:delText>is</w:delText></w:r></aml:content>";
      Assert.assertTrue("Merge content \"is\" deleted not found.", this.content.contains(testString));

      testString = "</aml:annotation><w:r><w:t> the background of the </w:t></w:r><aml:annotation";
      Assert.assertTrue("Merge content \" the background of the \" not changed not found.",
         this.content.contains(testString));

      testString = "<aml:content><w:r><w:t>doc</w:t></w:r></aml:content>";
      Assert.assertTrue("Merge content \"doc\" added not found.", this.content.contains(testString));

      testString = "<aml:content><w:r><w:delText>document</w:delText></w:r></aml:content>";
      Assert.assertTrue("Merge content \"document\" deleted not found.", this.content.contains(testString));

      Assert.assertTrue("Paragraph Number only Link not found",
         this.content.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1</w:t></w:r>"));
   }

   @Test
   public void testPublishWithDiffRecurseTemplate() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(recurseTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);
   }

   @Test
   public void testPublishWithoutDiff() {

      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(docFolder);

      this.renderer.publish(singleTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);
   }

   //@formatter:off


   @Test
   public void testPublishWithoutDiffRecurseTemplate() {
      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(PUBLISH_DIFF, false);
      List<Artifact> artifacts = new ArrayList<>();
      artifacts.add(docFolder);

      this.renderer.publish(recurseTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);
   }

   //@formatter:on

   @Test
   public void testPublishWithoutDiffUpdateParagraphNumbers() {

      SkynetTransaction transaction =
         TransactionManager.createTransaction(updateBranch, String.format("%s", method.getQualifiedTestName()));
      this.modifyOption(BRANCH, updateBranch);
      this.modifyOption(TRANSACTION_OPTION, transaction);
      this.modifyOption(PUBLISH_DIFF, false);
      this.modifyOption(LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      this.modifyOption(UPDATE_PARAGRAPH_NUMBERS, true);
      List<Artifact> artifacts = new ArrayList<>();
      Artifact updateDoc = ArtifactQuery.getArtifactFromId(docFolder, updateBranch);
      artifacts.add(updateDoc);

      this.renderer.publish(singleTemplateAttrib, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);

      Assert.assertTrue("Paragraph Number & Name Link not found",
         this.content.contains("<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1 Hardware</w:t></w:r>"));
      Assert.assertTrue("Paragraph Number 2 is not updated",
         this.content.contains("<w:p><w:r><w:t> Paragraph Number: </w:t></w:r><w:r><w:t>2</w:t></w:r>"));
      Assert.assertTrue("Paragraph Number 2.1 is not updated",
         this.content.contains("<w:p><w:r><w:t> Paragraph Number: </w:t></w:r><w:r><w:t>2.1</w:t></w:r>"));

      this.blankPageCounter(1);
   }
}
