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

package org.eclipse.osee.ats.ide.integration.tests.define;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * @author David W. Miller
 * @author Loren K. Ashley
 */

public class RendererEndpointTest {

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

   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static final int testBranchSpecificationRecordIdentifier = 1;

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
                      RendererEndpointTest.testBranchSpecificationRecordIdentifier, /* BranchSpecificationRecord Identifier */
                      DemoBranches.SAW_PL_Working_Branch.getName(),                 /* Branch Name                          */
                      "Branch for RendererEndpointTest"                             /* Branch Creation Comment              */
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
                  RendererEndpointTest.testBranchSpecificationRecordIdentifier,                 /* Test Branch Identifier                 (Integer)                               */
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
    * Saves a handle to the Publishing REST API endpoint.
    */

   private static PublishingEndpoint publishingEndpoint;

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId rootBranchId;

   @BeforeClass
   public static void testSetup() {

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(RendererEndpointTest.setValues);

      testDocumentBuilder.buildDocument(RendererEndpointTest.branchSpecifications,
         RendererEndpointTest.artifactSpecifications);

      /*
       * Save identifiers of test document root
       */

      RendererEndpointTest.rootBranchId =
         testDocumentBuilder.getBranchIdentifier(RendererEndpointTest.testBranchSpecificationRecordIdentifier).get();
      RendererEndpointTest.rootArtifactId = ArtifactId.valueOf(testDocumentBuilder.getArtifactIdentifier(
         RendererEndpointTest.testBranchSpecificationRecordIdentifier, 1).get());

      /*
       * Get services
       */

      var oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      RendererEndpointTest.publishingEndpoint = oseeclient.getPublishingEndpoint();

   }

   @Test
   public void testPublish() throws IOException {

      //@formatter:off
      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer",
                   null,
                   PresentationType.PREVIEW.name(),
                   RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey(),
                   FormatIndicator.WORD_ML
                );

      var publishingRendererOptions =
         new EnumRendererMap
            (
               RendererOption.EXCLUDE_FOLDERS,   false,
               RendererOption.LINK_TYPE,         LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
               RendererOption.MAX_OUTLINE_DEPTH, 9,
               RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML,
               RendererOption.BRANCH,            RendererEndpointTest.rootBranchId
            );

      var msWordPreviewRequestData =
         new PublishingRequestData
                (
                   publishingTemplateRequest,
                   publishingRendererOptions,
                   List.of( RendererEndpointTest.rootArtifactId )
                );

      try(
            var inputStream =
               publishingEndpoint
                  .msWordPreview( msWordPreviewRequestData )
                  .getDataHandler()
                  .getInputStream()
         )
      {

         var fileContents = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

         Assert.assertTrue(fileContents.contains("Communication Subsystem Crew Interface"));
      }
      //@formatter:on
   }

}

/* EOF */
