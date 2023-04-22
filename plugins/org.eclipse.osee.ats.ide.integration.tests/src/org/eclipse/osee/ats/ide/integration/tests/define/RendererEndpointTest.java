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
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactInfoRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecification;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
    * List of {@BuilderRecord}s describing the test artifacts.
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
   private static List<BuilderRecord> artifactInfoRecords =
      List.of
         (
            new BasicArtifactInfoRecord
                   (
                      1,                                                            /* Identifier                             (Integer)                               */
                      0,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Software Requirements",                                      /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                     /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                      List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new BasicArtifactInfoRecord
                   (
                      2,                                                            /* Identifier                             (Integer)                               */
                      1,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Crew Station Requirements",                                  /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                      List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new BasicArtifactInfoRecord
                   (
                      3,                                                            /* Identifier                             (Integer)                               */
                      2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Communication Subsystem Crew Interface",                     /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
                   (
                      4,                                                            /* Identifier                             (Integer)                               */
                      2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Navigation Subsystem Crew Interface",                        /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
                   (
                      5,                                                            /* Identifier                             (Integer)                               */
                      2,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Aircraft Systems Management Subsystem Crew Interface",       /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
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

            new BasicArtifactInfoRecord
                   (
                      6,                                                            /* Identifier                             (Integer)                               */
                      5,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Aircraft Drawing",                                           /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.HeadingMsWord,                              /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of(),                                                    /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                      List.of()                                                     /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new BasicArtifactInfoRecord
                   (
                      7,                                                            /* Identifier                             (Integer)                               */
                      5,                                                            /* Hierarchical Parent Identifier         (Integer)                               */
                      "Ventilation",                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementMsWord,                  /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                       /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
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

   /**
    * Branch token for the test branch.
    */

   private static final BranchToken testBranch = DemoBranches.SAW_PL_Working_Branch;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = RendererEndpointTest.testBranch.getName();

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Setup for importing software requirements";

   @BeforeClass
   public static void testSetup() {

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(RendererEndpointTest.setValues);

      testDocumentBuilder.buildDocument(RendererEndpointTest.artifactInfoRecords, RendererEndpointTest.testBranchName,
         RendererEndpointTest.testBranchCreationComment);

      /*
       * Save identifiers of test document root
       */

      RendererEndpointTest.rootBranchId = testDocumentBuilder.getRootBranchId();
      RendererEndpointTest.rootArtifactId = testDocumentBuilder.getRootArtifactId();

      /*
       * Get services
       */

      var oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      RendererEndpointTest.publishingEndpoint = oseeclient.getPublishingEndpoint();

   }

   @Test
   public void testImport() throws IOException {

      //@formatter:off
      var template =
         ArtifactQuery.getArtifactFromTypeAndName
            (
               CoreArtifactTypes.RendererTemplateWholeWord,
               RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey(),
               CoreBranches.COMMON
            );

      try(
            var inputStream = publishingEndpoint.msWordTemplatePublish
                                 (
                                    RendererEndpointTest.rootBranchId,
                                    template,
                                    RendererEndpointTest.rootArtifactId,
                                    ArtifactId.SENTINEL
                                 ).getDataHandler().getInputStream()
         )
      {

         var fileContents = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

         Assert.assertTrue(fileContents.contains("Communication Subsystem Crew Interface"));
      }
      //@formatter:on
   }

}

/* EOF */
