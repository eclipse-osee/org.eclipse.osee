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

package org.eclipse.osee.ats.ide.integration.tests.synchronization;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecification;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BuilderRelationshipRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecRelation;
import org.eclipse.rmf.reqif10.SpecType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * ReqIF relationship tests for the Synchronization REST API End point defined in the package
 * {@link org.eclipse.osee.synchronization.rest}.
 *
 * @author Loren K. Ashley
 */

public class ReqifRelationships {

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
    * Class used to define relationships between test artifacts.
    */

   private static class ArtifactRelationshipInfoRecord implements BuilderRelationshipRecord {

      /**
       * The type of relationship
       */

      private final RelationTypeToken relationTypeToken;

      /**
       * A {@link List} of the {@link BuilderRecord} identifiers representing the related test artifacts.
       */

      private final List<Integer> targetBuilderRecords;

      /**
       * Constructs a new {@link ArtifactRelationshipInfoRecord} with the specified parameters.
       *
       * @param relationTypeToken the type of relationship.
       * @param targetBuilderRecords a {@link List} of the {@link BuilderRecord} identifiers of the related test
       * artifacts.
       */

      ArtifactRelationshipInfoRecord(RelationTypeToken relationTypeToken, List<Integer> targetBuilderRecords) {
         this.relationTypeToken = Objects.requireNonNull(relationTypeToken);
         this.targetBuilderRecords = Objects.requireNonNull(targetBuilderRecords);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public RelationTypeToken getRelationTypeToken() {
         return this.relationTypeToken;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public List<Integer> getTargetBuilderRecords() {
         return this.targetBuilderRecords;
      }

   }

   /**
    * Class used to define, and build test artifacts with a test attribute in the local database. Only one attribute per
    * test artifact is setup and used for testing.
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

         )
      {
         this.identifier                   = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name                         = Objects.requireNonNull(name);
         this.artifactTypeToken            = Objects.requireNonNull(typeToken);
         this.attributeSpecifications      = Objects.requireNonNull(attributeSpecifications);
         this.builderRelationshipRecords   = Objects.requireNonNull(builderRelationshipRecords);
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

   /**
    * Record for the identifier and type identifier of a ReqIF Spec Relation.
    */

   private static class RequirementTraceVerificationRecord {

      /**
       * ReqIF identifier of a ReqIF Spec Relation.
       */

      String reqifSpecRelationIdentifier;

      /**
       * ReqIF identifier of the ReqIF Spec Relation Type associated with the ReqIF Spec Relation.
       */

      String reqifSpecRelationTypeIdentifier;

      /**
       * Creates a new {@link RequirementTraceVerificationRecord}.
       *
       * @param reqifSpecRelationIdentifier ReqIF identifier of a ReqIF Spec Relation.
       * @param reqifSpecRelationTypeIdentifier ReqIF identifier of the ReqIF Spec Relation Type associated with the
       * ReqIF Spec Relation.
       */

      public RequirementTraceVerificationRecord(String reqifSpecRelationIdentifier, String reqifSpecRelationTypeIdentifier) {
         this.reqifSpecRelationIdentifier = reqifSpecRelationIdentifier;
         this.reqifSpecRelationTypeIdentifier = reqifSpecRelationTypeIdentifier;
      }

   }

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   @SuppressWarnings("unused")
   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   @SuppressWarnings("unused")
   private static BranchId rootBranchId;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "ReqIF Relationships Test Branch";

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for ReqIF Synchronizaion Artifact Testing";

   private static BiConsumer<Attribute<?>, Object> stringAttributeSetter =
      (attribute, value) -> ((StringAttribute) attribute).setValue((String) value);

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

   //@formatter:off
   private static List<ArtifactInfoRecord> artifactInfoRecords =
      List.of
         (
            new ArtifactInfoRecord
                   (
                      1,                                                                                /* Identifier                             (Integer)                               */
                      0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "ReqIF Relationship Test Document",                                               /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "ReqIF Relationship Test Document" ),                     /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      2,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements A Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "Requirements A Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      3,                                                                                /* Identifier                             (Integer)                               */
                      2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement A-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "This is Requirement A-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                         (
                         )
                   ),

            new ArtifactInfoRecord
                   (
                      4,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements B Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "Requirements B Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      5,                                                                                /* Identifier                             (Integer)                               */
                      4,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement B-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "This is Requirement B-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                         (
                           new ArtifactRelationshipInfoRecord
                                  (
                                    CoreRelationTypes.RequirementTrace,                                 /* Relationship Type                      RelationTypeToken                       */
                                    List.of( 3 )                                                        /* Targets                                (List<Integer>)                         */
                                  ),
                                  new ArtifactRelationshipInfoRecord
                                  (
                                    CoreRelationTypes.RequirementTrace,                                 /* Relationship Type                      RelationTypeToken                       */
                                    List.of( 10 )                                                       /* Targets                                (List<Integer>)                         */
                                  )
                         )
                   ),

            new ArtifactInfoRecord
                   (
                      6,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements C Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "Requirements C Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      7,                                                                                /* Identifier                             (Integer)                               */
                      6,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement C-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "This is Requirement C-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                         (
                            new ArtifactRelationshipInfoRecord
                            (
                               CoreRelationTypes.RequirementTrace,                                      /* Relationship Type                      RelationTypeToken                       */
                               List.of( 5 )                                                             /* Targets                                (List<Integer>)                         */
                            )
                         )
                   ),

            new ArtifactInfoRecord
                   (
                      8,                                                                                /* Identifier                             (Integer)                               */
                      0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "ReqIF Relationship Test Document 2",                                             /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "ReqIF Relationship Test Document 2" ),                   /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      9,                                                                                /* Identifier                             (Integer)                               */
                      8,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Test Document 2 Requirements A Folder",                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "Requirements A Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                   ),

            new ArtifactInfoRecord
                   (
                      10,                                                                                /* Identifier                             (Integer)                               */
                      9,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Test Document 2 Requirement A-A",                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                         (
                            new BasicAttributeSpecification
                                   (
                                     CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                     List.of( "This is Requirement A-A for Test Document 2" ),          /* Test Attribute Values                  (List<Object>)                          */
                                     ReqifRelationships.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                   )
                         ),
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                         (
                            new ArtifactRelationshipInfoRecord
                            (
                               CoreRelationTypes.RequirementTrace,                                      /* Relationship Type                      RelationTypeToken                       */
                               List.of( 3 )                                                             /* Targets                                (List<Integer>)                         */
                            )
                         )
                   )
         );
   //@formatter:on

   /**
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * identifier and then by the ReqIF Attribute Definition identifier.
    */

   private static RankMap<AttributeDefinition> reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap;

   /**
    * This is a map of the ReqIF Attribute Values in the test document. ReqIF Attribute Values are specific to a ReqIF
    * Specification, Spec Object, or Spec Relation.
    * <dl>
    * <dt>Rank:</dt>
    * <dd>2</dd>
    * <dt>Primary Key:</dt>
    * <dd>Identifier of the containing ReqIF Specification,Spec Object, or Spec Relation</dd>
    * <dt>Secondary Key:</dt>
    * <dd>ReqIF Attribute Value's Attribute Definition reference Identifier</dt>
    * </dl>
    */

   private static RankMap<AttributeValue> reqifAttributeValueByIdentifiersMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their identifiers. This map does not include the ReqIF
    * Specifications.
    */

   private static RankMap<SpecObject> reqifSpecObjectByIdentifierMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their long names. This map does not include the ReqIF
    * Specifications.
    */

   private static RankMap<SpecObject> reqifSpecObjectByLongNameMap;

   /**
    * Map of ReqIF Spec Relations from the test document keyed by their type, source, and target identifiers.
    */

   private static RankMap<SpecRelation> reqifSpecRelationByTypeSourceTargetIdentifierMap;

   /**
    * Map of ReqIF Spec Relation Types from the test document keyed by their long names.
    */

   private static RankMap<SpecType> reqifSpecRelationTypesByLongNameMap;

   /**
    * Map of ReqIF Spec Relation Types from the test document keyed by their identifiers.
    */

   private static RankMap<SpecType> reqifSpecRelationTypesByIdentifierMap;

   /**
    * Saves the {@link TestDocumentBuilder} for later lookup of OSEE Artifacts by the builder record identifier.
    */

   private static TestDocumentBuilder testDocumentBuilder;

   /**
    * Verifies a ReqIF Spec Relation exists for the specified ReqIF Spec Relation Type, ReqIF Source Spec Object, and
    * ReqIF Target Spec Object.
    *
    * @param reqifSpecRelationTypeLongName the long name for the ReqIF Spec Relation Type of the desired ReqIF Spec
    * Relation.
    * @param sourceSpecObjectLongName the long name of the ReqIF Spec Object that is the source of the relationship.
    * @param targetSpecObjectLongName the long name of the ReqIF Spec Object that is the target of the relationship.
    * @return a {@link RequirementTraceVerificationRecord} with the Req IF Spec Relation identifier and the ReqIF Spec
    * Relation Type identifier of the relationship's type.
    * @throws AssertionError when the specified ReqIF Spec Relation is not found.
    */

   private static RequirementTraceVerificationRecord verifyRequirementTrace(String reqifSpecRelationTypeLongName, String sourceSpecObjectLongName, String targetSpecObjectLongName) {

      var reqifSpecRelationTypeOptional =
         ReqifRelationships.reqifSpecRelationTypesByLongNameMap.get(reqifSpecRelationTypeLongName);

      Assert.assertTrue(reqifSpecRelationTypeOptional.isPresent());

      var reqifSourceSpecObjectOptional = ReqifRelationships.reqifSpecObjectByLongNameMap.get(sourceSpecObjectLongName);

      Assert.assertTrue(reqifSourceSpecObjectOptional.isPresent());

      var reqifTargetSpecObjectOptional = ReqifRelationships.reqifSpecObjectByLongNameMap.get(targetSpecObjectLongName);

      Assert.assertTrue(reqifTargetSpecObjectOptional.isPresent());

      var reqifSpecRelationTypeIdentifier = reqifSpecRelationTypeOptional.get().getIdentifier();

      Assert.assertNotNull(reqifSpecRelationTypeIdentifier);

      var reqifSourceSpecObjectIdentifier = reqifSourceSpecObjectOptional.get().getIdentifier();

      Assert.assertNotNull(reqifSourceSpecObjectIdentifier);

      var reqifTargetSpecObjectIdentifier = reqifTargetSpecObjectOptional.get().getIdentifier();

      Assert.assertNotNull(reqifTargetSpecObjectIdentifier);

      var reqifSpecRelationOptional = ReqifRelationships.reqifSpecRelationByTypeSourceTargetIdentifierMap.get(
         reqifSpecRelationTypeIdentifier, reqifSourceSpecObjectIdentifier, reqifTargetSpecObjectIdentifier);

      Assert.assertTrue(reqifSpecRelationOptional.isPresent());

      var reqifSpecRelationIdentifier = reqifSpecRelationOptional.get().getIdentifier();

      Assert.assertNotNull(reqifSpecRelationIdentifier);

      return new RequirementTraceVerificationRecord(reqifSpecRelationIdentifier, reqifSpecRelationTypeIdentifier);
   }

   /**
    * Verifies the value of a ReqIF Spec Relation enumerated attribute.
    *
    * @param requirementTraceVerificationRecord the identifiers of the ReqIF Spec Relation and the ReqIF Spec Relation
    * Type of the ReqIf Spec Relation.
    * @param reqifAttributeDefinitionLongName the long name of the ReqIF attribute to test.
    * @param expectedValue the expected value of the ReqIF attribute.
    * @throws AssertionError when the ReqIF Spec Relation string attribute does not contain the expected value.
    */

   private static void verifyRequirementTraceAttributeValueEnumeration(RequirementTraceVerificationRecord requirementTraceVerificationRecord, String reqifAttributeDefinitionLongName, String expectedValue) {

      var reqifRelationTypeMultiplicityAttributeDefinitionOptional =
         ReqifRelationships.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap.get(
            requirementTraceVerificationRecord.reqifSpecRelationTypeIdentifier, reqifAttributeDefinitionLongName);

      Assert.assertTrue(reqifRelationTypeMultiplicityAttributeDefinitionOptional.isPresent());

      var reqifRelationTypeMultiplicityAttributeDefinitionIdentifier =
         reqifRelationTypeMultiplicityAttributeDefinitionOptional.get().getIdentifier();

      var reqifRelationTypeMultiplicityAttributeValueOptional =
         ReqifRelationships.reqifAttributeValueByIdentifiersMap.get(
            requirementTraceVerificationRecord.reqifSpecRelationIdentifier,
            reqifRelationTypeMultiplicityAttributeDefinitionIdentifier);

      Assert.assertTrue(reqifRelationTypeMultiplicityAttributeValueOptional.isPresent());

      var reqifRelationTypeMultiplicityAttributeValueList =
         ((AttributeValueEnumeration) reqifRelationTypeMultiplicityAttributeValueOptional.get()).getValues();

      Assert.assertEquals(1, reqifRelationTypeMultiplicityAttributeValueList.size());

      Assert.assertEquals(expectedValue, reqifRelationTypeMultiplicityAttributeValueList.get(0).getLongName());
   }

   /**
    * Verifies the value of a ReqIF Spec Relation string attribute.
    *
    * @param requirementTraceVerificationRecord the identifiers of the ReqIF Spec Relation and the ReqIF Spec Relation
    * Type of the ReqIf Spec Relation.
    * @param reqifAttributeDefinitionLongName the long name of the ReqIF attribute to test.
    * @param expectedValue the expected value of the ReqIF attribute.
    * @throws AssertionError when the ReqIF Spec Relation string attribute does not contain the expected value.
    */

   private static void verifyRequirementTraceAttributeValueString(RequirementTraceVerificationRecord requirementTraceVerificationRecord, String reqifAttributeDefinitionLongName, String expectedValue) {

      var reqifSideAAttributeDefinitionOptional =
         ReqifRelationships.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap.get(
            requirementTraceVerificationRecord.reqifSpecRelationTypeIdentifier, reqifAttributeDefinitionLongName);

      Assert.assertTrue(reqifSideAAttributeDefinitionOptional.isPresent());

      var reqifSideAAttributeDefinitionIdentifier = reqifSideAAttributeDefinitionOptional.get().getIdentifier();

      var reqifSideAAttributeValueOptional = ReqifRelationships.reqifAttributeValueByIdentifiersMap.get(
         requirementTraceVerificationRecord.reqifSpecRelationIdentifier, reqifSideAAttributeDefinitionIdentifier);

      Assert.assertTrue(reqifSideAAttributeValueOptional.isPresent());

      Assert.assertEquals(expectedValue, ((AttributeValueString) reqifSideAAttributeValueOptional.get()).getTheValue());
   }

   @SuppressWarnings("unchecked")
   @BeforeClass
   public static void testSetup() {

      /*
       * Create tracking maps
       */

      //@formatter:off
      ReqifRelationships.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap = new RankHashMap<>( "reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap", 2, 256, 0.75f, KeyPredicates.keysAreStringsRank2 );
      ReqifRelationships.reqifAttributeValueByIdentifiersMap                                           = new RankHashMap<>( "reqifAttributeValueByIdentifiersMap",                                           2, 256, 0.75f, KeyPredicates.keysAreStringsRank2 );
      ReqifRelationships.reqifSpecObjectByIdentifierMap                                                = new RankHashMap<>( "reqifSpecObjectByIdentifierMap",                                                1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationships.reqifSpecObjectByLongNameMap                                                  = new RankHashMap<>( "reqifSpecObjectByLongNameMap",                                                  1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationships.reqifSpecRelationByTypeSourceTargetIdentifierMap                              = new RankHashMap<>( "reqifSpecRelationBySourceTargetTypeIdentifierMap",                              3, 256, 0.75f, KeyPredicates.keysAreStringsRank3 );
      ReqifRelationships.reqifSpecRelationTypesByIdentifierMap                                         = new RankHashMap<>( "reqifSpecRelationTypeByIdentifierMap",                                          1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationships.reqifSpecRelationTypesByLongNameMap                                           = new RankHashMap<>( "reqifSpecRelationTypeByLongNameMap",                                            1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      //@formatter:on

      /*
       * Build test document
       */

      ReqifRelationships.testDocumentBuilder = new TestDocumentBuilder(ReqifRelationships.setValues);

      ReqifRelationships.testDocumentBuilder.buildDocument(
         (List<BuilderRecord>) (Object) ReqifRelationships.artifactInfoRecords, ReqifRelationships.testBranchName,
         ReqifRelationships.testBranchCreationComment);

      /*
       * Get services
       */

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      var synchronizationEndpoint = oseeClient.getSynchronizationEndpoint();

      /*
       * Get and parse the Synchronization Artifact
       */

      var synchronizationArtifactParser = new SynchronizationArtifactParser(synchronizationEndpoint);

      synchronizationArtifactParser.parseTestDocument(ReqifRelationships.testDocumentBuilder.getRootBranchId(),
         ReqifRelationships.testDocumentBuilder.getRootArtifactId(), "reqif");

      /*
       * Index the members of the ReqIF by identifier and long name
       */

      //@formatter:off
      synchronizationArtifactParser.parseSpecObjects
         (
            ReqifRelationships.reqifSpecObjectByIdentifierMap,
            ReqifRelationships.reqifSpecObjectByLongNameMap
         );

      synchronizationArtifactParser.parseSpecRelationTypes
         (
            ReqifRelationships.reqifSpecRelationTypesByIdentifierMap,
            ReqifRelationships.reqifSpecRelationTypesByLongNameMap
         );

      synchronizationArtifactParser.parseSpecRelations
         (
            ReqifRelationships.reqifSpecRelationByTypeSourceTargetIdentifierMap
         );

      synchronizationArtifactParser.parseAttributeValues
         (
            new Function[]
            {
               (reqifCoreContent) -> ((ReqIFContent) reqifCoreContent).getSpecRelations()
            },
            Identifiable::getIdentifier,
            "getIdentifier",
            ReqifRelationships.reqifAttributeValueByIdentifiersMap
         );

      synchronizationArtifactParser.parseAttributeDefinitions
         (
            new Function[]
            {
               ( reqifCoreContent ) -> ((ReqIFContent) reqifCoreContent).getSpecTypes()
            },
            Identifiable::getIdentifier,
            "getLongName",
            ReqifRelationships.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap
         );

      //@formatter:on
   }

   @Test
   public void testRequirementTraceCA2BA() {

      var requirementTraceVerificationRecord =
         ReqifRelationships.verifyRequirementTrace("Requirement Trace", "Requirement C-A", "Requirement B-A");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceBA2AA() {

      var requirementTraceVerificationRecord =
         ReqifRelationships.verifyRequirementTrace("Requirement Trace", "Requirement B-A", "Requirement A-A");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceTD2AA2AA() {

      var testDocument2RequirementAASpecterName =
         ReqifRelationships.testDocumentBuilder.getArtifactIdByBuilderRecordId(10).orElseThrow().toString();

      var requirementTraceVerificationRecord = ReqifRelationships.verifyRequirementTrace("Requirement Trace",
         testDocument2RequirementAASpecterName, "Requirement A-A");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceBA2TD2AA() {

      var testDocument2RequirementAASpecterName =
         ReqifRelationships.testDocumentBuilder.getArtifactIdByBuilderRecordId(10).orElseThrow().toString();

      var requirementTraceVerificationRecord = ReqifRelationships.verifyRequirementTrace("Requirement Trace",
         "Requirement B-A", testDocument2RequirementAASpecterName);

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationships.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

}

/* EOF */