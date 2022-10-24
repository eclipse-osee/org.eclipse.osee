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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecification;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRelationshipRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
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
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( TestUserRules.createInPublishingGroupTestRule() )
         .around( new NotProductionDataStoreRule() );
   //@formatter:on

   /**
    * List of {@link ArtifactInfoRecords} describing the test artifacts.
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

   private static class ArtifactInfoRecord implements BuilderRecord {

      /**
       * The {@link ArtifactTypeToken} of the test artifact.
       */

      private final ArtifactTypeToken artifactTypeToken;

      /**
       * A list of the attributes to be defined for the artifact.
       */

      private final List<AttributeSpecificationRecord> attributeSpecifications;

      /**
       * A list of the relationships for the test artifact. The list may be empty but should never be <code>null</code>.
       */

      private final List<BuilderRelationshipRecord> builderRelationshipRecords;

      /**
       * The {@link ArtifactInfoRecord#identifier} for the test artifact that is the hierarchical parent of the test
       * artifact defined by this record. Use 0 for top level artifacts.
       */

      private final Integer hierarchicalParentIdentifier;

      /**
       * A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforce bad test setup will result if
       * not unique. The identifier 0 is reserved for the Default Hierarchy Root artifact.
       */

      private final Integer identifier;

      /**
       * A name for the test artifact.
       */

      private final String name;

      /**
       * Constructs a new {@link ArtifactInfoRecord} with the specified parameters.
       *
       * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value
       * 0 is reserved.
       * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
       * hierarchical parent of the test artifact. Use 0 for top level artifacts.
       * @param name A name for the test artifact.
       * @param typeToken The {@link ArtifactTypeToken} of the test artifact.
       * @param testAttributeType The {@link AttributeTypeGeneric} of the test attribute.
       * @param testAttributeValue The test attribute value.
       * @param attributeSetter A {@link BiConsumer} used to assign the attribute value to the test attribute. The first
       * parameter is the attribute as an {@link Attribute} and the second parameter is the value as an {@link Object}.
       * @param builderRelationshipsRecords a {@link List} of the {@link BuilderRelationshipRecord}s defining
       * relationships between this test artifact and other test artifacts. This parameter should be an empty list when
       * the test artifact does not have relationships instead of <code>null</code>.
       */

      //@formatter:off
      ArtifactInfoRecord
         (
            Integer                            identifier,
            Integer                            hierarchicalParentIdentifier,
            String                             name,
            ArtifactTypeToken                  typeToken,
            List<AttributeSpecificationRecord> attributeSpecifications,
            List<BuilderRelationshipRecord>    builderRelationshipRecords
         ) {
         this.identifier = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name = Objects.requireNonNull(name);
         this.artifactTypeToken = Objects.requireNonNull(typeToken);
         this.attributeSpecifications = Objects.requireNonNull(attributeSpecifications);
         this.builderRelationshipRecords = Objects.requireNonNull(builderRelationshipRecords);
      }
      //@formatter:on

      /*
       * BuildRecord Methods
       */

      /**
       * {@inheritDoc}
       */

      @Override
      public ArtifactTypeToken getArtifactTypeToken() {
         return this.artifactTypeToken;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public List<AttributeSpecificationRecord> getAttributeSpecifications() {
         return this.attributeSpecifications;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public List<BuilderRelationshipRecord> getBuilderRelationshipRecords() {
         return this.builderRelationshipRecords;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Integer getHierarchicalParentIdentifier() {
         return this.hierarchicalParentIdentifier;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Integer getIdentifier() {
         return this.identifier;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public String getName() {
         return this.name;
      }

   }

   private static BiConsumer<Attribute<?>, Object> stringAttributeSetter =
      (attribute, value) -> ((StringAttribute) attribute).setValue((String) value);

   //@formatter:off
   private static List<ArtifactInfoRecord> artifactInfoRecords =
      List.of
         (
            new ArtifactInfoRecord
            (
               1,                                                                                /* Identifier                             (Integer)                               */
               0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "Shared Folder",                                                                  /* Artifact Name                          (String)                                */
               CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "Publishing test shared folder." ),                       /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               2,                                                                                /* Identifier                             (Integer)                               */
               1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "A",                                                                              /* Artifact Name                          (String)                                */
               CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "Sub-folder A" ),                                         /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               3,                                                                                /* Identifier                             (Integer)                               */
               1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "B",                                                                              /* Artifact Name                          (String)                                */
               CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "Sub-folder B" ),                                         /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               4,                                                                                /* Identifier                             (Integer)                               */
               2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "A-0",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               5,                                                                                /* Identifier                             (Integer)                               */
               2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "A-1",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               6,                                                                                /* Identifier                             (Integer)                               */
               2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "A-2",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementHtml,                                        /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               7,                                                                                /* Identifier                             (Integer)                               */
               3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "B-0",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               8,                                                                                /* Identifier                             (Integer)                               */
               3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "B-1",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1234" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            ),

            new ArtifactInfoRecord
            (
               9,                                                                                /* Identifier                             (Integer)                               */
               3,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
               "B-2",                                                                            /* Artifact Name                          (String)                                */
               CoreArtifactTypes.SoftwareRequirementHtml,                                        /* Artifact Type                          (ArtifactTypeToken)                     */
               List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                  (
                     new BasicAttributeSpecification
                            (
                              CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                              List.of( "DOC-1235" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                              PublishingSharedArtifactsFolderTest.stringAttributeSetter          /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                            )
                  ),
               List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
            )

         );
   //@formatter:on

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
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Publishing Shared Artifacts Folder Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for Publishing Shared Artifacts Folder Testing";

   @SuppressWarnings("unchecked")
   @BeforeClass
   public static void testSetup() {

      /*
       * When the test suit is run directly it will be in Database Initialization mode.
       */

      if (OseeProperties.isInDbInit()) {
         /*
          * Get out of database initialization mode and re-authenticate as the test user
          */

         OseeProperties.setInDbInit(false);
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
      }

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(PublishingSharedArtifactsFolderTest.setValues);

      testDocumentBuilder.buildDocument(
         (List<BuilderRecord>) (Object) PublishingSharedArtifactsFolderTest.artifactInfoRecords,
         PublishingSharedArtifactsFolderTest.testBranchName,
         PublishingSharedArtifactsFolderTest.testBranchCreationComment);

      /*
       * Save identifiers of test document root
       */

      PublishingSharedArtifactsFolderTest.rootBranchId = testDocumentBuilder.getRootBranchId();
      PublishingSharedArtifactsFolderTest.rootArtifactId = testDocumentBuilder.getRootArtifactId();

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

      var expectedMessagePattern = Pattern.compile("Error:\\s*Unable to locate the shared folder\\.");

      //@formatter:off
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

         var message =
            new StringBuilder( 1024 )
                   .append( "Expected exception did not occur." ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      catch( OseeCoreException e ) {
         var expectedMessageMatcher = expectedMessagePattern.matcher( e.getMessage() );
         if( expectedMessageMatcher.find() ) {
            Assert.assertTrue( true );
         }
         else {
            var message =
               new StringBuilder( 1024 )
                      .append( "Expected exception message not found." ).append( "\n" )
                      .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                      .append( "Actual Exception Message Follows:" ).append( "\n" )
                      .append( e.getMessage() ).append( "\n" )
                      .toString();

            Assert.assertTrue(message,false);
         }
      }
      catch( Exception ue ) {
         var message =
            new StringBuilder( 1024 )
                   .append( "Unexpected Exception:" ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .append( ue.getMessage() ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      //@formatter:on
   }

   @Test
   public void testSharedFoldersAUnknownArtifact() {

      var expectedMessagePattern = Pattern.compile("Error:\\s*Unable to locate the shared folder\\.");

      //@formatter:off
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

         var message =
            new StringBuilder( 1024 )
                   .append( "Expected exception did not occur." ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      catch( OseeCoreException e ) {
         var expectedMessageMatcher = expectedMessagePattern.matcher( e.getMessage() );
         if( expectedMessageMatcher.find() ) {
            Assert.assertTrue( true );
         }
         else {
            var message =
               new StringBuilder( 1024 )
                      .append( "Expected exception message not found." ).append( "\n" )
                      .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                      .append( "Actual Exception Message Follows:" ).append( "\n" )
                      .append( e.getMessage() ).append( "\n" )
                      .toString();

            Assert.assertTrue(message,false);
         }
      }
      catch( Exception ue ) {
         var message =
            new StringBuilder( 1024 )
                   .append( "Unexpected Exception:" ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .append( ue.getMessage() ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      //@formatter:on
   }

   @Test
   public void testSharedFoldersAUnknownAttribute() {

      var expectedMessagePattern = Pattern.compile("Error:\\s*Child Attribute Type Identifier is SENTINEL\\.");

      //@formatter:off
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

         var message =
            new StringBuilder( 1024 )
                   .append( "Expected exception did not occur." ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      catch( OseeCoreException e ) {
         var expectedMessageMatcher = expectedMessagePattern.matcher( e.getMessage() );
         if( expectedMessageMatcher.find() ) {
            Assert.assertTrue( true );
         }
         else {
            var message =
               new StringBuilder( 1024 )
                      .append( "Expected exception message not found." ).append( "\n" )
                      .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                      .append( "Actual Exception Message Follows:" ).append( "\n" )
                      .append( e.getMessage() ).append( "\n" )
                      .toString();

            Assert.assertTrue(message,false);
         }
      }
      catch( Exception ue ) {
         var message =
            new StringBuilder( 1024 )
                   .append( "Unexpected Exception:" ).append( "\n" )
                   .append( "Expected Exception:     " ).append( "OseeCoreException" ).append( "\n" )
                   .append( "Expected Message Regex: " ).append( expectedMessagePattern ).append( "\n" )
                   .append( ue.getMessage() ).append( "\n" )
                   .toString();

         Assert.assertTrue( message, false );
      }
      //@formatter:on
   }

}

/* EOF */