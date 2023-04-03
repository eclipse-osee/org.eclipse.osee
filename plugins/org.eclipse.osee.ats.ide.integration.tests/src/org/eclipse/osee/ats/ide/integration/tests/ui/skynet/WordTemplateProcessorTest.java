/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.RendererTemplateWholeWord;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.database.init.DefaultDbInitTasks;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
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
 * @author Karol M. Wilk
 * @link: WordTemplateProcessor
 */
public class WordTemplateProcessorTest {

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
    * </dl>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         ;
   //@formatter:on

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   /**
    * A rule to get the method name of the currently running test.
    */

   @Rule
   public TestInfo method = new TestInfo();

   private static final String F_STRING_TO_WRAP_IN_WORDML = "F's body content";
   private static final String ERROR_MESSAGE =
      "Found improper [%s]'s WordML in result file. Testing with PublishInLine set to [%s].";

   private static String A_WITH_PUBLISH_INLINE;
   private static String A_WITHOUT_PUBLISH_INLINE;
   private static String C_WITH_PUBLISH_INLINE;
   private static String C_WITHOUT_PUBLISH_INLINE;
   private static String F_WITH_PUBLISH_INLINE;
   private static String F_WITHOUT_PUBLISH_INLINE;

   ArtifactId recurseTemplate;

   @BeforeClass
   public static void loadTestFiles() throws Exception {
      C_WITH_PUBLISH_INLINE = getResourceData("wordtemplate_c_with_publish_inline.xml");
      C_WITHOUT_PUBLISH_INLINE = getResourceData("wordtemplate_c_without_publish_inline.xml");
      A_WITH_PUBLISH_INLINE = getResourceData("wordtemplate_a_with_publish_inline.xml");
      A_WITHOUT_PUBLISH_INLINE = getResourceData("wordtemplate_a_without_publish_inline.xml");
      F_WITH_PUBLISH_INLINE = getResourceData("wordtemplate_f_with_publish_inline.xml");
      F_WITHOUT_PUBLISH_INLINE = getResourceData("wordtemplate_f_without_publish_inline.xml");
   }

   private BranchToken branch;

   private Artifact myRootArtifact;

   @Before
   public void setUp() throws Exception {
      branch = BranchToken.create(method.getQualifiedTestName());

      BranchManager.createWorkingBranch(SAW_Bld_1, branch);

      recurseTemplate = ArtifactQuery.getArtifactFromTypeAndName(RendererTemplateWholeWord,
         DefaultDbInitTasks.PREVIEW_ALL_RECURSE, COMMON);
      myRootArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, branch, "WordTemplateProcessorTest_Root");

      Artifact artifactA = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch, "A");
      Artifact artifactB = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, branch, "B");
      Artifact artifactC = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch, "C");
      Artifact artifactD = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, branch, "D");
      Artifact artifactE = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Requirement, branch, "E");
      Artifact artifactF = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, branch, "F");

      //@formatter:off
      /*
       setup artifact tree of this form:
          A
          |---- C
          |  |
          |  -- D
          B
          |
          |
          E
          |
          |
          F
       */
      //@formatter:on

      myRootArtifact.addChild(artifactA);

      artifactA.addChild(artifactC);
      artifactA.addChild(artifactD);

      myRootArtifact.addChild(artifactB);
      myRootArtifact.addChild(artifactE);
      myRootArtifact.addChild(artifactF);

      myRootArtifact.persist(method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (BranchManager.branchExists(branch)) {
         BranchManager.purgeBranch(branch);
      }
   }

   @Test
   public void publishInLineOnChild() throws Exception {
      Artifact artifact_C = myRootArtifact.getDescendant("A").getDescendant("C");
      artifact_C.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_C.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, C_WITH_PUBLISH_INLINE);
      artifact_C.persist(method.getQualifiedTestName());

      checkPreviewContents(artifact_C, C_WITH_PUBLISH_INLINE, C_WITHOUT_PUBLISH_INLINE);
   }

   @Test
   public void publishInLineOnParent() throws Exception {
      Artifact artifact_A = myRootArtifact.getDescendant("A");
      artifact_A.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_A.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, A_WITH_PUBLISH_INLINE);
      artifact_A.persist(method.getQualifiedTestName());

      checkPreviewContents(artifact_A, A_WITH_PUBLISH_INLINE, A_WITHOUT_PUBLISH_INLINE);
   }

   /**
    * The GeneralStringData argument in this case is just plain string as <br/>
    * WordTemplateProcessor will wrap it in a <w:p>..whatever..</w:p> xml.
    */
   @Test
   public void publishInLineNonWordArtifact() throws Exception {
      Artifact artifact_F = myRootArtifact.getDescendant("F");
      artifact_F.setSoleAttributeValue(CoreAttributeTypes.PublishInline, true);
      artifact_F.setSoleAttributeValue(CoreAttributeTypes.GeneralStringData, F_STRING_TO_WRAP_IN_WORDML);
      artifact_F.persist(method.getQualifiedTestName());

      checkPreviewContents(artifact_F, F_WITH_PUBLISH_INLINE, F_WITHOUT_PUBLISH_INLINE);
   }

   private void checkPreviewContents(Artifact artifact, String expected, String notExpected) throws IOException {
      Map<RendererOption, Object> rendererOptions = new HashMap<>();

      rendererOptions.put(RendererOption.TEMPLATE_ARTIFACT, recurseTemplate);
      String filePath = RendererManager.open(myRootArtifact, PresentationType.PREVIEW, rendererOptions);

      String fileContents = Lib.fileToString(new File(filePath));
      //@formatter:off
      Asserts.assertTrue
         (
            () -> new Message()
                         .title( "Expected contents not found in publish file." )
                         .indentInc()
                         .segment( "Artifact",         artifact     )
                         .segment( "File Path",        filePath     )
                         .follows( "File Contents",    fileContents )
                         .follows( "Expected Content", expected     )
                         .toString(),
            fileContents.contains( expected )
         );
      //@formatter:on
      Assert.assertTrue(String.format(ERROR_MESSAGE, artifact, false), !fileContents.contains(notExpected));
   }

   private static String getResourceData(String relativePath) throws IOException {
      String value = Lib.fileToString(WordTemplateProcessorTest.class, "support/" + relativePath);
      Assert.assertTrue(Strings.isValid(value));
      return value;
   }
}