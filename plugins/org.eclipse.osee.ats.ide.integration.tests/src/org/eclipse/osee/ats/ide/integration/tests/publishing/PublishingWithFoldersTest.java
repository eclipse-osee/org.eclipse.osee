/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.PublishingTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NoPopUpsRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author David W. Miller
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class PublishingWithFoldersTest {

   /**
    * Set this flag to <code>true</code> to clean up of the test branches.
    */

   private static boolean cleanUp = true;

   /**
    * Set this flag to <code>true</code> to print the received Word ML documents to <code>stdout</code>.
    */

   private static boolean printDocuments = false;

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
    * {@Link UserToken} cache has been flushed.</dd></dt>
    * <dt>NoPopUpsRule</dt>
    * <dd>Prevents word documents from being launched for the user during tests.</dd>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NoPopUpsRule() )
         ;
   //@formatter:on

   /*
    * Test level rules are applied before each test.
    */

   @Rule
   public TestInfo testInfo = new TestInfo();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   /**
    * Method used to obtain the parameters for each test run.
    *
    * @return an array of the parameters for the test.
    */

   @Parameters(name = "{0}")
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.<Object[]>of
            (
               (Object[]) new RenderLocation[] { RenderLocation.CLIENT },
               (Object[]) new RenderLocation[] { RenderLocation.SERVER }
            );
      //@formatter:on
   }

   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static final int testBranchSpecificationRecordIdentifier = 1;

   /**
    * Creation comment used for the OSEE test branch
    */

   private static final String testBranchCreationComment = "Branch for Publishing With Folders Test";

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static final String testBranchName = "Publishing With Folders Test Branch";

   /**
    * List of {@link BranchSpecificationRecord} implementations describing the branches for the test.
    * <p>
    * Branches are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * </ul>
    */

   //@formatter:off
   private static final List<BranchSpecificationRecord> branchSpecifications =
      List.of
         (
            new BasicBranchSpecificationRecord
                   (
                      PublishingWithFoldersTest.testBranchSpecificationRecordIdentifier,
                      PublishingWithFoldersTest.testBranchName,
                      PublishingWithFoldersTest.testBranchCreationComment
                   )
         );
   //@formatter:on

   /**
    * {@link MapList} of {@ArtifactSpecificationRecord}s describing the test artifacts for each branch.
    * <p>
    * Artifacts are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * <li>Top level test artifact have a hierarchical parent identifier of 0.</li>
    * <li>Ensure children artifact's of a hierarchical parent artifact have unique names.</li>
    * </ul>
    */

   //@formatter:off
   private static MapList<Integer,ArtifactSpecificationRecord> artifactSpecifications =
      MapList.ofEntries
         (
            /*
             * Artifacts for the test branch.
             */

            Map.entry
               (
                  PublishingWithFoldersTest.testBranchSpecificationRecordIdentifier,           /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                            /* Identifier                             (Integer)                               */
                                  0,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Software Requirements",                                      /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                            /* Identifier                             (Integer)                               */
                                  1,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Crew Station Requirements",                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                            /* Identifier                             (Integer)                               */
                                  2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Communication Subsystem Crew Interface",                     /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,        /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                         beginWordString
                                                       + "This is the list of Communication crew station requirements."
                                                       + endWordString
                                                    ),
                                                 AttributeSetters.stringAttributeSetter         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                            /* Identifier                             (Integer)                               */
                                  2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Navigation Subsystem Crew Interface",                        /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,        /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                        /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                         beginWordString
                                                       + "This is the list of Navigation crew station requirements."
                                                       + endWordString
                                                    ),
                                                 AttributeSetters.stringAttributeSetter         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                            )
                                  ),
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                            /* Identifier                             (Integer)                               */
                                  2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Aircraft Systems Management Subsystem Crew Interface",       /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,        /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                        /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "This is the list of Aircraft Management crew station requirements."
                                                        + endWordString
                                                    ),
                                                  AttributeSetters.stringAttributeSetter         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                            /* Identifier                             (Integer)                               */
                                  5,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Aircraft Drawing",                                           /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  7,                                                            /* Identifier                             (Integer)                               */
                                  5,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Ventilation",                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                  CoreAttributeTypes.WordTemplateContent,        /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                  List.of                                        /* Test Attribute Values                  (List<Object>)                          */
                                                     (
                                                          beginWordString
                                                        + "This is the list of Aircraft Management crew station requirements."
                                                        + endWordString
                                                     ),
                                                  AttributeSetters.stringAttributeSetter         /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )

                     )
               )
         );
   //@formatter:on

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private static Artifact rootArtifact;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId workingBranchId;

   /**
    * Flag used to specify if the test publish is to be performed with the client or the server.
    */

   private final RenderLocation renderLocation;

   /**
    * Constructor saves the parameters for the test.
    *
    * @param renderLocation flag to indicate if the publish is to be performed on the client or server.
    */

   public PublishingWithFoldersTest(RenderLocation renderLocation) {
      this.renderLocation = renderLocation;
   }

   @BeforeClass
   public static void beforeClass() {

      /*
       * Clean up test branches that may be left over
       */

      if (PublishingWithFoldersTest.cleanUp) {
         PublishingTestUtil.cleanUpBranches(PublishingWithFoldersTest.testBranchName);
      }

      /*
       * Create the test branches and artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingWithFoldersTest.setValues);

      testDocumentBuilder.buildDocument(PublishingWithFoldersTest.branchSpecifications,
         PublishingWithFoldersTest.artifactSpecifications);

      /*
       * Save identifiers of test branches and artifacts
       */

      PublishingWithFoldersTest.workingBranchId = testDocumentBuilder.getBranchIdentifier(
         PublishingWithFoldersTest.testBranchSpecificationRecordIdentifier).get();

      PublishingWithFoldersTest.rootArtifact =
         testDocumentBuilder.getArtifact(PublishingWithFoldersTest.testBranchSpecificationRecordIdentifier, 1).get();
   }

   @AfterClass
   public static void afterClass() {

      if (PublishingWithFoldersTest.cleanUp) {
         PublishingTestUtil.cleanUpBranch(PublishingWithFoldersTest.workingBranchId);
      }
   }

   @Test
   public void testPublish() {

      //@formatter:off
      var publishingRendererOptions =
         new EnumRendererMap
            (
               RendererOption.RENDER_LOCATION,   this.renderLocation,
               RendererOption.TEMPLATE_OPTION,   RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey(),
               RendererOption.LINK_TYPE,         LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
               RendererOption.MAX_OUTLINE_DEPTH, 9,
               RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML,
               RendererOption.BRANCH,            PublishingWithFoldersTest.workingBranchId
            );

      final var artifacts = List.of( PublishingWithFoldersTest.rootArtifact );

      final var contentPath =
         RendererManager.open
            (
               artifacts,
               PresentationType.PREVIEW,
               publishingRendererOptions
            );

      final var document =
         PublishingTestUtil.loadContent
            (
               contentPath,
               testInfo.getTestName()
            );

      final var documentString =
         PublishingTestUtil.prettyPrint
            (
               document,
               testInfo.getTestName(),
               PublishingWithFoldersTest.printDocuments
            );

      Assert.assertTrue( documentString.contains( "Communication Subsystem Crew Interface" ) );
      //@formatter:on
   }

}

/* EOF */
