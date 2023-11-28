/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ExceptionLogBlocker;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class PublishingSharedArtifactsFolderTest {

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
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static int testBranchSpecificationRecordIdentifier = 1;

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
                      PublishingSharedArtifactsFolderTest.testBranchSpecificationRecordIdentifier, /* BranchSpecificationRecord Identifier */
                      "Publishing Shared Artifacts Folder Test Branch",                            /* Branch Name                          */
                      "Branch for Publishing Shared Artifacts Folder Testing"                      /* Branch Creation Comment              */
                   )
         );
   //@formatter:on

   /**
    * {@link MapList} of {@link ArtifactSpecificationRecord}s describing the test artifacts for each branch.
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
                  PublishingSharedArtifactsFolderTest.testBranchSpecificationRecordIdentifier,                      /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Shared Folder",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Publishing test shared folder." ),                       /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "A",                                                                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Sub-folder A" ),                                         /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "B",                                                                              /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Sub-folder B" ),                                         /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "A-0",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "A-1",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "A-2",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementHtml,                                        /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  7,                                                                                /* Identifier                             (Integer)                               */
                                  3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "B-0",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  8,                                                                                /* Identifier                             (Integer)                               */
                                  3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "B-1",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  9,                                                                                /* Identifier                             (Integer)                               */
                                  3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "B-2",                                                                            /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementHtml,                                        /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
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

   @SuppressWarnings("unchecked")
   @BeforeClass
   public static void testSetup() {

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingSharedArtifactsFolderTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            PublishingSharedArtifactsFolderTest.branchSpecifications,
            PublishingSharedArtifactsFolderTest.artifactSpecifications
         );
      //@formatter:on

      /*
       * Save identifiers of test document root
       */

      //@formatter:off
      PublishingSharedArtifactsFolderTest.rootBranchId =
         testDocumentBuilder
            .getBranchIdentifier
                (
                   PublishingSharedArtifactsFolderTest.testBranchSpecificationRecordIdentifier
                )
            .get();

      PublishingSharedArtifactsFolderTest.rootArtifactId =
         testDocumentBuilder
            .getArtifactIdentifier
               (
                  PublishingSharedArtifactsFolderTest.testBranchSpecificationRecordIdentifier,
                  1
               )
            .get();

      /*
       * Get services
       */

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      PublishingSharedArtifactsFolderTest.publishingEndpoint = oseeClient.getPublishingEndpoint();
   }

   @Test
   public void testSharedFoldersA() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, ArtifactTypeToken.SENTINEL, CoreAttributeTypes.Description,
         "DOC-1234");

      Assert.assertEquals(3, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("A-0"));
      Assert.assertTrue(sharedFoldersMap.containsKey("A-2"));
      Assert.assertTrue(sharedFoldersMap.containsKey("B-1"));
   }

   @Test
   public void testSharedFoldersACoreArtifactTypesArtifact() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, CoreArtifactTypes.SoftwareRequirementMsWord,
         CoreAttributeTypes.Description, "DOC-1234");

      Assert.assertEquals(2, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("A-0"));
      Assert.assertTrue(sharedFoldersMap.containsKey("B-1"));
   }

   @Test
   public void testSharedFoldersACoreArtifactTypesMsWordWholeDocument() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, CoreArtifactTypes.SoftwareRequirementHtml,
         CoreAttributeTypes.Description, "DOC-1234");

      Assert.assertEquals(1, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("A-2"));
   }

   @Test
   public void testSharedFoldersB() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, ArtifactTypeToken.SENTINEL, CoreAttributeTypes.Description,
         "DOC-1235");

      Assert.assertEquals(3, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("A-1"));
      Assert.assertTrue(sharedFoldersMap.containsKey("B-0"));
      Assert.assertTrue(sharedFoldersMap.containsKey("B-2"));
   }

   @Test
   public void testSharedFoldersBCoreArtifactTypesArtifact() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, CoreArtifactTypes.SoftwareRequirementMsWord,
         CoreAttributeTypes.Description, "DOC-1235");

      Assert.assertEquals(2, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("A-1"));
      Assert.assertTrue(sharedFoldersMap.containsKey("B-0"));
   }

   @Test
   public void testSharedFoldersBCoreArtifactTypesMsWordWholeDocument() {

      var sharedFolders = PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts(
         PublishingSharedArtifactsFolderTest.rootBranchId, ArtifactId.SENTINEL,
         PublishingSharedArtifactsFolderTest.rootArtifactId, CoreArtifactTypes.SoftwareRequirementHtml,
         CoreAttributeTypes.Description, "DOC-1235");

      Assert.assertEquals(1, sharedFolders.size());

      var sharedFoldersMap = sharedFolders.stream().collect(
         Collectors.toMap((artifactToken) -> artifactToken.getName(), Function.identity()));

      Assert.assertTrue(sharedFoldersMap.containsKey("B-2"));
   }

   @Test
   public void testSharedFoldersAUnknownBranch() {

      //@formatter:off
      try(
            var exceptionLogBlocker =
               new ExceptionLogBlocker
                      (
                         "javax.ws.rs.NotFoundException",
                         "org.eclipse.osee.framework.core.exception.OseeNotFoundException",
                         "org.eclipse.osee.framework.jdk.core.type.OseeCoreException",
                         "Error:\\s*Unable to locate the shared folder\\."
                      )
         )
      {
         try {
            @SuppressWarnings("unused")
            var sharedFolders =
               PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts
                  (
                    BranchId.valueOf( 0x1234L ),
                    ArtifactId.SENTINEL,
                    PublishingSharedArtifactsFolderTest.rootArtifactId,
                    ArtifactTypeToken.SENTINEL,
                    CoreAttributeTypes.Description,
                    "DOC-1234"
                  );

            exceptionLogBlocker.assertNoException();
         }
         catch( Exception e ) {
            exceptionLogBlocker.assertExpectedException(e);
         }
      }
      //@formatter:on
   }

   @Test
   public void testSharedFoldersAUnknownArtifact() {

      //@formatter:off
      try(
           var exceptionLogBlocker =
               new ExceptionLogBlocker
                      (
                        "javax.ws.rs.NotFoundException",
                        "org.eclipse.osee.framework.core.exception.OseeNotFoundException",
                        "org.eclipse.osee.framework.jdk.core.type.OseeCoreException",
                        "Error:\\s*Unable to locate the shared folder\\."
                      )
         )
      {
         try {
            @SuppressWarnings("unused")
            var sharedFolders =
               PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts
                  (
                    PublishingSharedArtifactsFolderTest.rootBranchId,
                    ArtifactId.SENTINEL,
                    ArtifactId.valueOf( 0x1234L ),
                    ArtifactTypeToken.SENTINEL,
                    CoreAttributeTypes.Description,
                    "DOC-1234"
                  );

            exceptionLogBlocker.assertNoException();
         }
         catch( Exception e ) {
            exceptionLogBlocker.assertExpectedException(e);
         }
      }
      //@formatter:on
   }

   @Test
   public void testSharedFoldersAUnknownAttribute() {

      //@formatter:off
      try(
           var exceptionLogBlocker =
              new ExceptionLogBlocker
                     (
                       "javax.ws.rs.NotFoundException",
                       "org.eclipse.osee.framework.core.exception.OseeNotFoundException",
                       "org.eclipse.osee.framework.jdk.core.type.OseeCoreException",
                       "Error:\\s*Child Attribute Type Identifier is SENTINEL\\."
                     )
         )
      {
         try {
            @SuppressWarnings("unused")
            var sharedFolders =
               PublishingSharedArtifactsFolderTest.publishingEndpoint.getSharedPublishingArtifacts
                  (
                    PublishingSharedArtifactsFolderTest.rootBranchId,
                    ArtifactId.SENTINEL,
                    PublishingSharedArtifactsFolderTest.rootArtifactId,
                    ArtifactTypeToken.SENTINEL,
                    AttributeTypeGeneric.SENTINEL,
                    "DOC-1234"
                  );

            exceptionLogBlocker.assertNoException();
         }
         catch( Exception e ) {
            exceptionLogBlocker.assertExpectedException(e);
         }
      }
      //@formatter:on
   }

}

/* EOF */