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

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTemplateSetterImpl;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotForEclipseOrgRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.IncludeHeadings;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;
import org.eclipse.osee.orcs.core.util.PublishingTemplateMatchCriterion;
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
 * @author Loren K. Ashley
 * @link: MSWordTemplateClientRenderer
 */

public class PublishingRendererTest {

   /**
    * Set this flag to <code>false</code> to prevent the test setup code from altering attribute values in the database.
    * The default (normal for testing) value is <code>true</code>.
    */

   private static boolean setValues = true;

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

      Check(ErrorTitleSupplier errorTitleSupplier, CheckStringSupplier checkStringSupplier, CheckFilter checkFilter,
         boolean testIsRegex) {
         this(errorTitleSupplier, checkStringSupplier, checkFilter);
         this.testIsRegex = testIsRegex;
      }

      void perform(String testName, String filePath, String document, String period, String altString, String pubString,
         Boolean mergeFlag, Boolean fieldcodeFlag) {

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

   private static final Pattern findSetRsidR =
      Pattern.compile(" *wsp:rsidR=\".*?\" *", Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern findSetRsidRDefault =
      Pattern.compile("wsp:rsidRDefault=\".*?\"", Pattern.DOTALL | Pattern.MULTILINE);

   private static final String PRIMARY_TEMPLATE = "SRS Primary Template";;

   private static final String PRIMARY_TEMPLATE_ID = "SRS Primary Template ID Only";;

   private static final String PRIMARY_TEMPLATE_ID_NAME = "SRS Primary Template ID and Name";

   /**
    * Defines the publishing templates for the tests. The templates will be created on the Common Branch under the "OSEE
    * Configuration/Document Templates" folder.
    */

   //@formatter:off
   private static Supplier<List<PublishingTemplate>> publishingTemplatesSupplier = new Supplier<> () {

      @Override
      public List<PublishingTemplate> get() {
         return

            List.of
            (
               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.RECURSE_TEMPLATE,                                               /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "RecursiveRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "wordrenderer_recurse.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                         (
                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_ID_WORD,                                                         /* Renderer Identifier */
                                      PresentationType.PREVIEW.name(),                                          /* Presentation Type   */
                                      RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()           /* Option */
                                   ),

                            new PublishingTemplateMatchCriterion
                                   (
                                      RENDERER_ID_MS_WORD_TEMPLATE_CLIENT_RENDERER,                             /* Renderer Identifier */
                                      PresentationType.PREVIEW.name(),                                          /* Presentation Type   */
                                      RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()           /* Option */
                                   )
                         )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.SINGLE_TEMPLATE,                                                /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "SingleRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                            /* Template Content Supplier  */
                                (
                                   "wordrenderer_single.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                           (
                           )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.TEST_PUBLISH_WITH_DIFF_LINKS_TEMPLATE,                          /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "TestPublishWithDiffLinks.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "TestPublishWithDiffLinks.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                           (
                           )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.SINGLE_TEMPLATE_ATTRIBUTES,                                     /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "SingleAttributeRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                            /* Template Content Supplier  */
                                (
                                   "wordrenderer_single_attrib.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                            (
                            )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.PRIMARY_TEMPLATE,                                               /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "PrimaryRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "wordrenderer_primary.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                            (
                            )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.PRIMARY_TEMPLATE_ID,                                            /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "PrimaryIdRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "wordrenderer_primary-idonly.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                            (
                            )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.PRIMARY_TEMPLATE_ID_NAME,                                       /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "PrimaryIdRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "wordrenderer_primary-idandname.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                            (
                            )
                      ),

               new PublishingTemplate
                      (
                         CoreArtifactTokens.DocumentTemplates,                                                  /* Parent Artifact Identifier */
                         PublishingRendererTest.SECONDARY_TEMPLATE,                                             /* Name                       */
                         new PublishingTemplate.FileSupplierSupport                                             /* Publish Options Supplier   */
                                (
                                   "SecondaryRendererOptions.json",
                                   PublishingRendererTest.class
                                ),
                         new PublishingTemplate.FileSupplierSupport                                             /* Template Content Supplier  */
                                (
                                   "wordrenderer_secondary.xml",
                                   PublishingRendererTest.class
                                ),
                         List.of
                            (
                            ),
                         List.of                                                                                /* Match Criteria             */
                            (
                            )
                      )

            );
         }
      };


   //@formatter:on
   private static final String RECURSE_TEMPLATE = "Recurse Template";

   private static String RENDERER_ID_MS_WORD_TEMPLATE_CLIENT_RENDERER =
      "org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer";

   private static String RENDERER_ID_WORD = "org.eclipse.osee.framework.ui.skynet.word";

   private static final String SECONDARY_TEMPLATE = "SRS Secondary Template";

   private static final String SINGLE_TEMPLATE = "Single Template";

   private static final String TEST_PUBLISH_WITH_DIFF_LINKS_TEMPLATE = "Single X";

   private static final String SINGLE_TEMPLATE_ATTRIBUTES = "Single With Attributes";

   private static final String tabString = "wx:wTabBefore=\"540\" wx:wTabAfter=\"90\"";

   /**
    * Map of the {@link PublishingTemplate} objects for the tests that were read back from the Publishing Template
    * Manager.
    */

   private static Map<String, org.eclipse.osee.framework.core.publishing.PublishingTemplate> templateMap;

   @BeforeClass
   public static void loadTemplateInfo() throws Exception {
      PublishingRendererTest.setupTemplates();
   }

   /**
    * Creates the Publishing Template Artifacts on the Common Branch, deletes the Publishing Template Manager's cache,
    * and then loads the Publishing Templates from the Publishing Template Manager.
    */

   private static void setupTemplates() {
      //@formatter:off
      var relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint( CoreBranches.COMMON );

      var publishingTemplateSetter = new PublishingTemplateSetterImpl( relationEndpoint );

      var publishingTemplateList =
         PublishingTemplate
            .load
               (
                  PublishingRendererTest.publishingTemplatesSupplier,
                  publishingTemplateSetter::set,
                  PublishingRendererTest.class,
                  false
               );

      PublishingRequestHandler.deletePublishingTemplateCache();

      PublishingRendererTest.templateMap =
         publishingTemplateList
            .stream()
            .map( PublishingTemplate::getIdentifier )
            .filter( Objects::nonNull )
            .map
               (
                  ( publishingTemplateIdentifier ) -> new PublishingTemplateRequest
                                                             (
                                                                Conditions.requireNonNull( publishingTemplateIdentifier ),
                                                                FormatIndicator.WORD_ML
                                                             )
               )
            .filter( Objects::nonNull )
            .map( PublishingRequestHandler::getPublishingTemplate )
            .filter( org.eclipse.osee.framework.core.publishing.PublishingTemplate::isNotSentinel )
            .collect
               (
                  Collectors.toMap
                     (
                        org.eclipse.osee.framework.core.publishing.PublishingTemplate::getName,
                        Function.identity()
                     )
               );
      //@formatter:on
   }

   //@formatter:off
   private final List<Check> basicDocumentChecks =
      List.of
         (
            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 1. Introduction", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"1" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Introduction</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 1.1 Background", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Background</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> !mergeFlag
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 1.2 Scope", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"1.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Scope</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 2. Subsystem", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"2" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Subsystem</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 2.1 Hardware", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> !fieldcodeFlag
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 2.1.1 Hardware Functions", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"2.1.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Hardware Functions</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 2.2 Software", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"2.2" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 2.2.1 Software Functions", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"2.2.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Software Functions</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 3. Notes", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"3" + period + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Notes</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   ),

            new Check
                   (
                     (testName)                     -> String.format("%s, Expected 3.1 More Notes", testName),
                     (period, altString, pubString) -> "<wx:t wx:val=\"3.1" + altString + pubString + "/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>More Notes</w:t></w:r>",
                     (mergeFlag, fieldcodeFlag)     -> true
                   )
         );
    //@formatter:on

   private String content;

   private String contentPath;;

   @Rule
   public TestInfo method = new TestInfo();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private WordTemplateRenderer renderer;

   private BranchId baselineBranch;
   private BranchId workingBranch;
   private ArtifactId documentFolderArtifactId;
   private ArtifactId softwareRequirementFolderArtifactId;

   /**
    * Saves a {@link Map} of the artifacts associated with each {@link ArtifactSpecificationRecord}.
    */

   private static Map<Integer, Optional<Artifact>> builderRecordMap;

   private static int baselineBranchSpecificationRecordIdentifier = 1;
   private static int workingBranchSpecificationRecordIdentifier = 2;

   //@formatter:off
   private static final List<BranchSpecificationRecord> branchSpecifications =
      List.of
         (
            new BasicBranchSpecificationRecord
                   (
                      PublishingRendererTest.baselineBranchSpecificationRecordIdentifier,  /* BranchSpecificationRecord Identifier */
                      "Word Template Renderer Test Baseline Branch",                       /* Branch Name                          */
                      "Branch for Word Template Renderer Testing"                          /* Branch Creation Comment              */
                   ),
            new BasicBranchSpecificationRecord
                   (
                      PublishingRendererTest.workingBranchSpecificationRecordIdentifier,   /* BranchSpecificationRecord Identifier */
                      "Word Template Renderer Test Working Branch",                        /* Branch Name                          */
                      "Branch for Word Template Renderer Testing",                         /* Branch Creation Comment              */
                      PublishingRendererTest.baselineBranchSpecificationRecordIdentifier   /* Parent Branch Identifier             */
                   )
         );
   //@formatter:on

   //@formatter:off
   /*
    root
    |
    |----Document Folder                                                     (Folder)
    |    |
    |    |----Introduction                                                   (HeadingMsWord)                (C1 <- Baseline )
    |         |---- Background                                               (SubsystemDesignMsWord)        (C2 <- Baseline & Working )
    |         |---- Scope                                                    (SubsystemDesignMsWord)
    |         |
    |         Subsystem                                                      (SubsystemDesignMsWord)
    |         |---- Hardware                                                 (HeadingMsWord)
    |         |     |---- Hardware Functions                                 (HardwareRequirementMsWord)    (C3 <- Working )
    |         |---- Software                                                 (HeadingMsWord)
    |         |     |---- Software Functions                                 (SoftwareDesignMsWord)
    |         |
    |         Notes                                                          (HeadingMsWord)
    |         |---- More Notes                                               (HeadingMsWord)
    |
    |----Software Requirements                                               (Folder)
    |    |
    |    |----Crew Station Requirements                                      (HeadingMsWord)
    |         |---- Communication Subsystem Crew Interface                   (SoftwareRequirementMsWord)
    |         |---- Navigation Subsystem Crew Interface                      (SoftwareRequirementMsWord)
    |         |---- Aircraft Systems Management Subsystem Crew Interface     (HeadingMsWord)
    |               |---- Aircraft Drawing                                   (HeadingMsWord)
    |               |---- Ventilation                                        (SoftwareRequirementMsWord)
    |
    |----Templates                                                           (Folder)
    */
   //@formatter:on

   //@formatter:off
   private static MapList<Integer,ArtifactSpecificationRecord> artifactSpecifications =
      MapList.ofEntries
         (
            /*
             * Artifacts for the test branch.
             */

            Map.entry
               (
                  PublishingRendererTest.baselineBranchSpecificationRecordIdentifier,                         /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                           (
                              1,                                                                                /* Identifier                             (Integer)                               */
                              0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Document Folder",                                                                /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              2,                                                                                /* Identifier                             (Integer)                               */
                              1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Introduction",                                                                   /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "Introduction section of the document." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              3,                                                                                /* Identifier                             (Integer)                               */
                              2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Background",                                                                     /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SubsystemDesignMsWord,                                          /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "1.1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "This is the background of the doc" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                     new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.SeverityCategory,                               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "III"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              4,                                                                                /* Identifier                             (Integer)                               */
                              2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Scope",                                                                          /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SubsystemDesignMsWord,                                          /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "1.2"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "The scope is the entire test" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              5,                                                                                /* Identifier                             (Integer)                               */
                              1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Subsystem",                                                                      /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SubsystemDesignMsWord,                                          /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "2"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "The following are SubSystems of the test document" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              6,                                                                                /* Identifier                             (Integer)                               */
                              5,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              //"Hardware",                                                                       /* Artifact Name                          (String)                                */
                              //CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              ArtifactToken.valueOf
                                     (
                                        232323L,
                                        "Hardware",
                                        CoreArtifactTypes.HeadingMsWord
                                     ),
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "2.1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "Hardware is an important Sub System" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              7,                                                                                /* Identifier                             (Integer)                               */
                              6,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Hardware Functions",                                                             /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HardwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "2.1.1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "The first hardware function is power on switch" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              8,                                                                                /* Identifier                             (Integer)                               */
                              5,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Software",                                                                       /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "2.2"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "Software is crucial to be running correctly" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              9,                                                                                /* Identifier                             (Integer)                               */
                              8,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Software Functions",                                                             /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SoftwareDesignMsWord,                                           /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "2.2.1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "Hello World, is basic software." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              10,                                                                               /* Identifier                             (Integer)                               */
                              1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Notes",                                                                          /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "3"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "Notes are great for small topics, and the link" + beginLinkInsert + "232323" + endLinkInsert + " too." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              11,                                                                               /* Identifier                             (Integer)                               */
                              10,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "More Notes",                                                                     /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.ParagraphNumber,                                /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   "3.1"
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           ),
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "More notes to read!" + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              12,                                                                               /* Identifier                             (Integer)                               */
                              0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Software Requirements",                                                          /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              13,                                                                               /* Identifier                             (Integer)                               */
                              12,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Crew Station Requirements",                                                      /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              14,                                                                               /* Identifier                             (Integer)                               */
                              13,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Communication Subsystem Crew Interface",                                         /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "This is the list of Communication crew station requirements." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              15,                                                                               /* Identifier                             (Integer)                               */
                              13,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Navigation Subsystem Crew Interface",                                            /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "This is the list of Navigation crew station requirements." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              16,                                                                               /* Identifier                             (Integer)                               */
                              13,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Aircraft Systems Management Subsystem Crew Interface",                           /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "This is the list of Aircraft Management crew station requirements." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              17,                                                                               /* Identifier                             (Integer)                               */
                              16,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Aircraft Drawing",                                                               /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.HeadingMsWord,                                                  /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              18,                                                                               /* Identifier                             (Integer)                               */
                              16,                                                                               /* Hierarchical Parent Identifier         (Integer)                               */
                              "Ventilation",                                                                    /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   beginWordString + "This is the Ventilation crew station requirements." + endWordString
                                                ),
                                             AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),
                        new BasicArtifactSpecificationRecord
                           (
                              19,                                                                               /* Identifier                             (Integer)                               */
                              0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Templates",                                                                      /* Artifact Name                          (String)                                */
                              CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           )
                     )
               )
         );


   private void basicDocumentCheck(String filePath, String document, String pubString, boolean merge,
      boolean fieldcode) {

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

      var blankPageMatcher = PublishingRendererTest.findBlankPage.matcher(this.content);
      var count = 0;
      while (blankPageMatcher.find()) {
         count++;
      }
      if (!expectedCountPredicate.test(count)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "PublishingRendererTest::blankPageCounter, Result document did not contain expected number of blank pages." )
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

   private void checkLinks(int expectedLinkCount) {

      Matcher findHyperLinksMatcher = findHlinks.matcher(this.content);

      int counter = 0;

      var contentFolderPath = Paths.get(this.contentPath).getParent();

      while (findHyperLinksMatcher.find()) {

         String hfile = findHyperLinksMatcher.group();
         hfile = hfile.substring(17, hfile.length() - 1);

         var testFilePath = contentFolderPath.resolve(hfile);

         if (!testFilePath.toFile().exists()) {
            //@formatter:off
            throw
               new AssertionError
                      (
                         new Message()
                                .title( "Linked file does not exist." )
                                .indentInc()
                                .segment( "Test Name",    this.method.getTestName() )
                                .segment( "Content File", this.contentPath          )
                                .segment( "Linked File",  testFilePath              )
                                .segment( "Document",     this.content              )
                                .toString()
                      );
            //@formatter:on
         }

         counter++;

      }

      if (counter != expectedLinkCount) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( "Did not find the expected number of hyperlinks in the document." )
                             .indentInc()
                             .segment( "Test Name",      this.method.getTestName() )
                             .segment( "Content File",   this.contentPath          )
                             .segment( "Expected Count", expectedLinkCount         )
                             .segment( "Actual Count",   counter                   )
                             .segment( "Document",       this.content              )
                             .toString()
                   );
         //@formatter:on

      }

   }

   private void documentCheck(List<Check> checks, String filePath, String document, String pubString) {

      var altString = pubString.isEmpty() ? "  \"" : "\" ";
      var period = pubString.isEmpty() ? "." : "";

      //@formatter:off
      checks.forEach
         (
            ( check ) -> check.perform
                            (
                               method.getQualifiedTestName(), /* testName       */
                               this.contentPath,              /* document path  */
                               this.content,                  /* document       */
                               period,                        /* period         */
                               altString,                     /* altString      */
                               pubString,                     /* pubString      */
                               false,                         /* mergeFlag      */
                               false                          /* fieldcoderFlag */
                            )
         );
      //@formatter:on
   }

   private String getFileAsString(String filePath) throws IOException {
      String retStr;
      Assert.assertNotNull("File is Null", filePath);
      File doc = new File(filePath);
      retStr = Lib.fileToString(doc);
      return retStr;
   }

   private void loadContent() {

      this.contentPath = (String) this.renderer.getRendererOptionValue(RendererOption.RESULT_PATH_RETURN);

      //@formatter:off
      if( Objects.isNull( this.contentPath ) ) {
         var message =
            new Message()
                   .title( "PublishingRendererTest::loadContent, Renderer did not supply a results path." )
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
         this.content = this.content.replace(rev, "");
      }
      m = findSetRsidRDefault.matcher(this.content);
      while (m.find()) {
         String rev = m.group();
         this.content = this.content.replace(rev, "");
      }

   }


   private void modifyOption(RendererOption optName, Object optValue) {
      this.renderer.setRendererOption(optName, optValue);
   }



   @Before
   public void setUp() {

      /*
       * Try to cleanup any old branches
       */

      final var oseeClient = OsgiUtil.getService(PublishingRendererTest.class, OseeClient.class);

      Assert.assertNotNull("TestDocumentBuilder::buildDocument, Failed to get OSEE Client.", oseeClient);

      /*
       * Get Branch end point for test data setup
       */

      final var branchEndpoint = oseeClient.getBranchEndpoint();

      //@formatter:off
      for (var branch  = TestUtil.getBranchByName(branchEndpoint, "Word Template Renderer Test Working Branch").orElse(null);
               branch != null;
               branch  = TestUtil.getBranchByName(branchEndpoint, "Word Template Renderer Test Working Branch").orElse(null) ) {

         BranchManager.purgeBranch(branch);
      }

      for (var branch  = TestUtil.getBranchByName(branchEndpoint, "Word Template Renderer Test Baseline Branch").orElse(null);
               branch != null;
               branch  = TestUtil.getBranchByName(branchEndpoint, "Word Template Renderer Test Baseline Branch").orElse(null) ) {

         BranchManager.purgeBranch(branch);
      }

      //@formatter:on

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingRendererTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            PublishingRendererTest.branchSpecifications,
            PublishingRendererTest.artifactSpecifications
         );
      //@formatter:on

      /*
       * Save identifiers of test document root
       */

      //@formatter:off
      this.baselineBranch =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  PublishingRendererTest.baselineBranchSpecificationRecordIdentifier
               )
            .get();

      this.workingBranch =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  PublishingRendererTest.workingBranchSpecificationRecordIdentifier
               )
            .get();

      this.documentFolderArtifactId =
         testDocumentBuilder
            .getArtifactIdentifier
               (
                  PublishingRendererTest.baselineBranchSpecificationRecordIdentifier,
                  1
               )
            .get();

      this.softwareRequirementFolderArtifactId =
         testDocumentBuilder
            .getArtifactIdentifier
               (
                  PublishingRendererTest.baselineBranchSpecificationRecordIdentifier,
                  8
               )
            .get();

      PublishingRendererTest.builderRecordMap =
         PublishingRendererTest
            .artifactSpecifications
            .stream( PublishingRendererTest.baselineBranchSpecificationRecordIdentifier )
            .map( ArtifactSpecificationRecord::getIdentifier )
            .collect
               (
                  Collectors.toMap
                     (
                        Function.identity(),
                        ( builderRecordIdentifier ) -> testDocumentBuilder.getArtifact
                                                     (
                                                        PublishingRendererTest.baselineBranchSpecificationRecordIdentifier,
                                                        builderRecordIdentifier
                                                     )
                     )
               );

      // Establish default option settings
      //@formatter:off
      EnumRendererMap rendererOptionsMap =
         new EnumRendererMap
                (
                  RendererOption.PUBLISH_DIFF,                                     true,
                  RendererOption.LINK_TYPE,                                        LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                  RendererOption.UPDATE_PARAGRAPH_NUMBERS,                         false,
                  RendererOption.SKIP_ERRORS,                                      true,
                  RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES, List.of( CoreArtifactTypes.Folder ),
                  RendererOption.RECURSE_ON_LOAD,                                  true,
                  RendererOption.MAINTAIN_ORDER,                                   true,
                  RendererOption.USE_TEMPLATE_ONCE,                                true,
                  RendererOption.FIRST_TIME,                                       true,
                  RendererOption.NO_DISPLAY,                                       true,
                  RendererOption.OUTLINING_OPTION_OVERRIDE_INCLUDE_HEADINGS,       IncludeHeadings.ALWAYS
                );
      //@formatter:on
      this.renderer = new WordTemplateRenderer(rendererOptionsMap);

      final var documentFolderArtifact =
         ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.baselineBranch);

      this.setUpDocChanges(documentFolderArtifact);
   }

   // Add changes to the Document

   // 3. Change to just the working branch
   private void setUpDocChanges(Artifact documentFolderArtifact) {

      /*
       * (C1) Change just the baseline branch
       */

      SkynetTransaction onRootTx = TransactionManager.createTransaction(this.baselineBranch, "ORIG UPDATE");

      Artifact intro = documentFolderArtifact.getDescendant("Introduction");
      Assert.assertNotNull("Cant find Introduction on branch", intro);

      intro.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Introduction section of the test document." + endWordString);
      intro.persist(onRootTx);

      /*
       * (C2) Change both the baseline and working branches
       */

      Artifact bckgrd = documentFolderArtifact.getDescendant("Introduction").getDescendant("Background");
      Assert.assertNotNull("Cant find Background on branch", bckgrd);

      bckgrd.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the background of the document" + endWordString);
      bckgrd.persist(onRootTx);
      onRootTx.execute();

      SkynetTransaction onChildTx = TransactionManager.createTransaction(this.workingBranch, "WORKING UPDATE");

      Artifact background = ArtifactQuery.getArtifactFromId(bckgrd, this.workingBranch);
      Assert.assertNotNull("Cant find Background on update branch", background);

      background.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This paragraph describes the background of the doc" + endWordString);
      background.persist(onChildTx);

      /*
       * (C3) Change just the working branch
       */

      Artifact hw = documentFolderArtifact.getDescendant("Subsystem").getDescendant("Hardware").getDescendant(
         "Hardware Functions");
      Artifact hdwrFunc = ArtifactQuery.getArtifactFromId(hw, this.workingBranch);
      Assert.assertNotNull("Cant find Hardware Functions on update branch", hdwrFunc);

      hdwrFunc.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "The first hardware function of importance is the power on switch." + endWordString);
      hdwrFunc.persist(onChildTx);

      onChildTx.execute();
   }

   // Add a change to use a different hyperlink for field code diff testing
   private void setupFieldCodeChange() {
      final var documentFolderArtifact =
         ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch);
      SkynetTransaction onChildTx = TransactionManager.createTransaction(this.workingBranch, "WORKING UPDATE");
      String hdwrGuid = documentFolderArtifact.getDescendant("Subsystem").getDescendant("Hardware").getDescendant(
         "Hardware Functions").getGuid();
      Artifact notes =
         ArtifactQuery.getArtifactFromId(documentFolderArtifact.getDescendant("Notes"), this.workingBranch);
      notes.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "Notes are great for small topics, and the link " + beginLinkInsert + hdwrGuid + endLinkInsert + " too." + endWordString);
      notes.persist(onChildTx);
      onChildTx.execute();
   }

   @After
   public void tearDown() throws Exception {

      if ((this.workingBranch != null) && BranchManager.branchExists(this.workingBranch)) {
         BranchManager.purgeBranch(this.workingBranch);
      }

      if ((this.baselineBranch != null) && BranchManager.branchExists(this.baselineBranch)) {
         BranchManager.purgeBranch(this.baselineBranch);
      }
   }

   @Test
   public void testBlankWordTemplateContent() {

      //@formatter:off
      var checks =
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

      /*
       * Create "Root Branch"
       */

      var rootBranch = BranchManager.createTopLevelBranch("Root Branch");

      Artifact volume4ArtifactRootBranch = null;

      {
         //@formatter:off
         ServiceUtil
            .getOseeClient()
            .getAccessControlService()
            .setPermission
               (
                  UserManager.getUser( DemoUsers.Joe_Smith ),
                  rootBranch,
                  PermissionEnum.FULLACCESS
               );

         /*
          * Create the following artifact structure:
          *
          * +- "Volume 4" (HeadingMsWord)
          *    |
          *    +- "Intro" (HeadingMsWord)
          */

         var tx = TransactionManager.createTransaction( rootBranch, method.getQualifiedTestName() );

         volume4ArtifactRootBranch = ArtifactTypeManager.addArtifact( CoreArtifactTypes.HeadingMsWord, rootBranch, "Volume 4" );

         volume4ArtifactRootBranch.setSoleAttributeValue
            (
               CoreAttributeTypes.ParagraphNumber,
               "1"
            );

         volume4ArtifactRootBranch.setSoleAttributeFromString
            (
               CoreAttributeTypes.WordTemplateContent,
               "<w:p><w:r><w:t>Volume 4 on the Root Branch.</w:t></w:r></w:p>"
            );

         volume4ArtifactRootBranch.persist( tx );

         var introArt = ArtifactTypeManager.addArtifact( CoreArtifactTypes.HeadingMsWord, rootBranch, "Intro" );

         introArt.setSoleAttributeFromString
            (
               CoreAttributeTypes.WordTemplateContent,
               "<w:p><w:r><w:t>Intro on the Root Branch.</w:t></w:r></w:p>"
            );

         volume4ArtifactRootBranch.addChild( introArt );

         introArt.persist( tx );
         tx.execute();
         //@formatter:on
      }

      /*
       * Create "Middle Branch"
       */

      var middleBranch = BranchManager.createWorkingBranch(rootBranch, "Middle Branch");

      {
         //@formatter:off
         /*
          * Get the "Volume 4" artifact on the "Middle Branch" and change "Root" to "Middle".
          */

         var volume4ArtifactMiddleBranch = ArtifactQuery.getArtifactFromId( volume4ArtifactRootBranch, middleBranch );

         volume4ArtifactMiddleBranch.setSoleAttributeFromString
            (
               CoreAttributeTypes.WordTemplateContent,
               "<w:p><w:r><w:t>Volume 4 on the Middle Branch.</w:t></w:r></w:p>"
            );

         volume4ArtifactMiddleBranch.persist( "Added Volume 4 artifact on the Middle Branch." );
         //@formatter:on
      }

      /*
       * Create "Child Branch"
       */

      var childBranch = BranchManager.createWorkingBranch(middleBranch, "Child Branch");

      var volume4ArtifactChildBranch = ArtifactQuery.getArtifactFromId(volume4ArtifactRootBranch, childBranch);

      {
         //@formatter:off
         /*
          * Get the "Volume 4" artifact on the "Child Branch" and change "Middle" to "Child".
          */

         volume4ArtifactChildBranch.setSoleAttributeFromString
            (
               CoreAttributeTypes.WordTemplateContent,
               "<w:p><w:r><w:t>Volume 4 on the Child Branch.</w:t></w:r></w:p>"
            );

         volume4ArtifactChildBranch.persist( "Added Volume 4 artifact on the Child Branch." );
         //@formatter:on
      }

      /*
       * Setup comparison publish between the root and child branches
       */

      this.renderer.setRendererOption(RendererOption.BRANCH, childBranch);
      this.renderer.setRendererOption(RendererOption.COMPARE_BRANCH, rootBranch);
      this.renderer.setRendererOption(RendererOption.PUBLISH_DIFF, true);

      var artifacts = List.of(volume4ArtifactChildBranch);

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.documentCheck(checks, this.contentPath, this.content, "");
   }

   @Test
   public void testPublishDiffWithFieldCodes() {

      //@formatter:off
      var checks =
         List.of
            (
               new Check
                      (
                        ( testName )                     -> "Field Code Diff not as expected",
                        ( period, altString, pubString ) -> "<w:fldChar w:fldCharType=\"begin\"/>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      )

            );
      //@formatter:on

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);

      this.setupFieldCodeChange();

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, true);

      this.documentCheck(checks, this.contentPath, this.content, "");
   }

   @Test
   public void testPublishDiffWithOutFieldCodes() {

      //@formatter:off
      var checks =
         List.of
            (
               new Check
                      (
                        ( testName )                     -> "Appears to have Field Code Diff",
                        ( period, altString, pubString ) -> "<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>Hardware",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      )

            );
      //@formatter:on

      try {
         this.modifyOption(RendererOption.BRANCH, this.workingBranch);
         this.modifyOption(RendererOption.PUBLISH_DIFF, true);

         setupFieldCodeChange();
         UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "true");

         var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

         var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

         this.renderer.publish(template, null, artifacts);

         this.loadContent();

         this.basicDocumentCheck(this.contentPath, this.content, "", false, true);

         this.documentCheck(checks, this.contentPath, this.content, "");

      } finally {
         UserManager.setSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES, "false");
      }
   }

   @Test
   public void testPublishSoftwareRequirements() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);

      var artifacts =
         List.of(ArtifactQuery.getArtifactFromId(this.softwareRequirementFolderArtifactId, this.workingBranch));

      var primaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.PRIMARY_TEMPLATE);
      var secondaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.SECONDARY_TEMPLATE);

      this.renderer.publish(primaryTemplate, secondaryTemplate, artifacts);

      this.loadContent();

      this.checkLinks(3);
   }

   @Test
   public void testPublishUsingIdAndName() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);

      var artifacts =
         List.of(ArtifactQuery.getArtifactFromId(this.softwareRequirementFolderArtifactId, this.workingBranch));

      var primaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.PRIMARY_TEMPLATE_ID_NAME);
      var secondaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.SECONDARY_TEMPLATE);

      this.renderer.publish(primaryTemplate, secondaryTemplate, artifacts);

      this.loadContent();

      this.checkLinks(3);
   }

   @Test
   public void testPublishUsingIds() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);

      var artifacts =
         List.of(ArtifactQuery.getArtifactFromId(this.softwareRequirementFolderArtifactId, this.workingBranch));

      var primaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.PRIMARY_TEMPLATE_ID);
      var secondaryTemplate = PublishingRendererTest.templateMap.get(PublishingRendererTest.SECONDARY_TEMPLATE);

      this.renderer.publish(primaryTemplate, secondaryTemplate, artifacts);

      this.loadContent();

      this.checkLinks(3);
   }

   @Test
   public void testPublishWithDiff() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);
   }

   @Test
   public void testPublishWithDiffDontUseTemplateOnce() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);
      this.modifyOption(RendererOption.LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      this.modifyOption(RendererOption.USE_TEMPLATE_ONCE, false);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE_ATTRIBUTES);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);

      this.blankPageCounter((count) -> count >= 10, ">= 10");
   }

   @Test
   public void testPublishWithDiffLinks() {
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
                          ( period, altString, pubString ) -> "<w:r><w:t>Notes</w:t></w:r></w:p><w:p><w:r><w:t>Paragraph Number: 3</w:t></w:r>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                      ),
                new Check
                      (
                          ( testName )                     -> "Original Paragram Numbering for More Notes is incorrect",
                          ( period, altString, pubString ) -> "<w:r><w:t>More Notes</w:t></w:r></w:p><w:p><w:r><w:t>Paragraph Number: 3.1</w:t></w:r>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                      )
      );
      //@formatter:on

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);
      this.modifyOption(RendererOption.LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template =
         PublishingRendererTest.templateMap.get(PublishingRendererTest.TEST_PUBLISH_WITH_DIFF_LINKS_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);

      this.documentCheck(checks, this.contentPath, this.content, "");

      this.blankPageCounter(1);
   }

   @Test
   public void testPublishWithDiffMerge() {

      //@formatter:off
      var checks =
         List.of
            (
               new Check
                      (
                        ( testName )                     -> "Merge content \"paragraph describes\" added not found.",
                        ( period, altString, pubString ) -> "<aml:content><w:r><w:t>paragraph describes</w:t></w:r></aml:content>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

               new Check
                      (
                        ( testName )                     -> "Merge content \"is\" deleted not found.",
                        ( period, altString, pubString ) -> "<aml:content><w:r><w:delText>is</w:delText></w:r></aml:content>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

               new Check
                      (
                        ( testName )                     -> "Merge content \" the background of the \" not changed not found.",
                        ( period, altString, pubString ) -> "</aml:annotation><w:r><w:t> the background of the </w:t></w:r><aml:annotation",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

               new Check
                      (
                        ( testName )                     -> "Merge content \"doc\" added not found.",
                        ( period, altString, pubString ) -> "<aml:content><w:r><w:t>doc</w:t></w:r></aml:content>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

               new Check
                      (
                        ( testName )                     -> "Merge content \"document\" deleted not found.",
                        ( period, altString, pubString ) -> "<aml:content><w:r><w:delText>document</w:delText></w:r></aml:content>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      ),

               new Check
                      (
                        ( testName )                     -> "Paragraph Number only Link not found",
                        ( period, altString, pubString ) -> "<w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>2.1</w:t></w:r>",
                        ( mergeFlag, fieldcodeFlag )     -> true
                      )

            );
      //@formatter:on

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);
      this.modifyOption(RendererOption.COMPARE_BRANCH, baselineBranch);
      this.modifyOption(RendererOption.LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", true, false);

      this.documentCheck(checks, this.contentPath, this.content, "");
   }

   @Test
   public void testPublishWithDiffRecurseTemplate() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, true);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.RECURSE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, "", false, false);
   }

   @Test
   public void testPublishWithoutDiff() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);
   }

   @Test
   public void testPublishWithoutDiffRecurseTemplate() {

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var template = PublishingRendererTest.templateMap.get(PublishingRendererTest.RECURSE_TEMPLATE);

      this.renderer.publish(template, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);
   }

   @Test
   public void testPublishWithoutDiffUpdateParagraphNumbers() {
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
                          ( testName ) ->                     "Paragraph Number 2 is not updated",
                          ( period, altString, pubString ) -> "<w:p><w:r><w:t>Paragraph Number: 2</w:t></w:r></w:p>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                       ),
                new Check
                       (
                          ( testName )                     -> "Paragraph Number 2.1 is not updated",
                          ( period, altString, pubString ) -> "<w:p><w:r><w:t>Paragraph Number: 2.1</w:t></w:r></w:p>",
                          ( mergeFlag, fieldcodeFlag )     -> true,
                          true
                       )
      );
      //@formatter:on

      SkynetTransaction transaction =
         TransactionManager.createTransaction(this.workingBranch, method.getQualifiedTestName());

      this.modifyOption(RendererOption.BRANCH, this.workingBranch);
      this.modifyOption(RendererOption.TRANSACTION_OPTION, transaction);
      this.modifyOption(RendererOption.PUBLISH_DIFF, false);
      this.modifyOption(RendererOption.LINK_TYPE, LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME);
      this.modifyOption(RendererOption.UPDATE_PARAGRAPH_NUMBERS, true);

      var artifacts = List.of(ArtifactQuery.getArtifactFromId(this.documentFolderArtifactId, this.workingBranch));

      var publishingTemplate =
         PublishingRendererTest.templateMap.get(PublishingRendererTest.SINGLE_TEMPLATE_ATTRIBUTES);

      this.renderer.publish(publishingTemplate, null, artifacts);

      this.loadContent();

      this.basicDocumentCheck(this.contentPath, this.content, tabString, false, false);

      this.documentCheck(checks, this.contentPath, this.content, "");

      this.blankPageCounter(1);
   }
}
