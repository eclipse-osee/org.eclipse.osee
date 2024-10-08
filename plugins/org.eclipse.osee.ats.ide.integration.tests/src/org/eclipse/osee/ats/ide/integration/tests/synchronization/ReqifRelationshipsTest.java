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
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicRelationshipSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.MapList;
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

public class ReqifRelationshipsTest {

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

   private static BiConsumer<Attribute<?>, Object> stringAttributeSetter =
      (attribute, value) -> ((StringAttribute) attribute).setValue((String) value);

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
                      ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier, /* BranchSpecificationRecord Identifier */
                      "ReqIF Relationships Test Branch",                          /* Branch Name                          */
                      "Branch for ReqIF Synchronizaion Artifact Testing"          /* Branch Creation Comment              */
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
                  ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier,                                   /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "ReqIF Relationship Test Document",                                               /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "ReqIF Relationship Test Document" ),                     /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirements A Folder",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Requirements A Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement A-A",                                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement A-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                                     (
                                     )
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  4,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirements B Folder",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Requirements B Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  5,                                                                                /* Identifier                             (Integer)                               */
                                  4,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement B-A",                                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement B-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                                     (
                                       new BasicRelationshipSpecificationRecord
                                              (
                                                CoreRelationTypes.RequirementTrace,                                 /* Relationship Type                      RelationTypeToken                       */
                                                List.of( 3 )                                                        /* Targets                                (List<Integer>)                         */
                                              ),
                                       new BasicRelationshipSpecificationRecord
                                              (
                                                CoreRelationTypes.RequirementTrace,                                 /* Relationship Type                      RelationTypeToken                       */
                                                List.of( 10 )                                                       /* Targets                                (List<Integer>)                         */
                                              )
                                     )
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  6,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirements C Folder",                                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Requirements C Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  7,                                                                                /* Identifier                             (Integer)                               */
                                  6,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement C-A",                                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement C-A" ),                              /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                                     (
                                        new BasicRelationshipSpecificationRecord
                                        (
                                           CoreRelationTypes.RequirementTrace,                                      /* Relationship Type                      RelationTypeToken                       */
                                           List.of( 5 )                                                             /* Targets                                (List<Integer>)                         */
                                        )
                                     )
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  8,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "ReqIF Relationship Test Document 2",                                             /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "ReqIF Relationship Test Document 2" ),                   /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  9,                                                                                /* Identifier                             (Integer)                               */
                                  8,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Test Document 2 Requirements A Folder",                                          /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "Requirements A Folder" ),                                /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  10,                                                                                /* Identifier                             (Integer)                               */
                                  9,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Test Document 2 Requirement A-A",                                                /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of( "This is Requirement A-A for Test Document 2" ),          /* Test Attribute Values                  (List<Object>)                          */
                                                 ReqifRelationshipsTest.stringAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                                     (
                                        new BasicRelationshipSpecificationRecord
                                        (
                                           CoreRelationTypes.RequirementTrace,                                      /* Relationship Type                      RelationTypeToken                       */
                                           List.of( 3 )                                                             /* Targets                                (List<Integer>)                         */
                                        )
                                     )
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

   private static RequirementTraceVerificationRecord verifyRequirementTrace(String reqifSpecRelationTypeLongName,
      String sourceSpecObjectLongName, String targetSpecObjectLongName) {

      var reqifSpecRelationTypeOptional =
         ReqifRelationshipsTest.reqifSpecRelationTypesByLongNameMap.get(reqifSpecRelationTypeLongName);

      Assert.assertTrue(reqifSpecRelationTypeOptional.isPresent());

      var reqifSourceSpecObjectOptional =
         ReqifRelationshipsTest.reqifSpecObjectByLongNameMap.get(sourceSpecObjectLongName);

      Assert.assertTrue(reqifSourceSpecObjectOptional.isPresent());

      var reqifTargetSpecObjectOptional =
         ReqifRelationshipsTest.reqifSpecObjectByLongNameMap.get(targetSpecObjectLongName);

      Assert.assertTrue(reqifTargetSpecObjectOptional.isPresent());

      var reqifSpecRelationTypeIdentifier = reqifSpecRelationTypeOptional.get().getIdentifier();

      Assert.assertNotNull(reqifSpecRelationTypeIdentifier);

      var reqifSourceSpecObjectIdentifier = reqifSourceSpecObjectOptional.get().getIdentifier();

      Assert.assertNotNull(reqifSourceSpecObjectIdentifier);

      var reqifTargetSpecObjectIdentifier = reqifTargetSpecObjectOptional.get().getIdentifier();

      Assert.assertNotNull(reqifTargetSpecObjectIdentifier);

      var reqifSpecRelationOptional = ReqifRelationshipsTest.reqifSpecRelationByTypeSourceTargetIdentifierMap.get(
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

   private static void verifyRequirementTraceAttributeValueEnumeration(
      RequirementTraceVerificationRecord requirementTraceVerificationRecord, String reqifAttributeDefinitionLongName,
      String expectedValue) {

      var reqifRelationTypeMultiplicityAttributeDefinitionOptional =
         ReqifRelationshipsTest.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap.get(
            requirementTraceVerificationRecord.reqifSpecRelationTypeIdentifier, reqifAttributeDefinitionLongName);

      Assert.assertTrue(reqifRelationTypeMultiplicityAttributeDefinitionOptional.isPresent());

      var reqifRelationTypeMultiplicityAttributeDefinitionIdentifier =
         reqifRelationTypeMultiplicityAttributeDefinitionOptional.get().getIdentifier();

      var reqifRelationTypeMultiplicityAttributeValueOptional =
         ReqifRelationshipsTest.reqifAttributeValueByIdentifiersMap.get(
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

   private static void verifyRequirementTraceAttributeValueString(
      RequirementTraceVerificationRecord requirementTraceVerificationRecord, String reqifAttributeDefinitionLongName,
      String expectedValue) {

      var reqifSideAAttributeDefinitionOptional =
         ReqifRelationshipsTest.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap.get(
            requirementTraceVerificationRecord.reqifSpecRelationTypeIdentifier, reqifAttributeDefinitionLongName);

      Assert.assertTrue(reqifSideAAttributeDefinitionOptional.isPresent());

      var reqifSideAAttributeDefinitionIdentifier = reqifSideAAttributeDefinitionOptional.get().getIdentifier();

      var reqifSideAAttributeValueOptional = ReqifRelationshipsTest.reqifAttributeValueByIdentifiersMap.get(
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
      ReqifRelationshipsTest.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap = new RankHashMap<>( "reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap", 2, 256, 0.75f, KeyPredicates.keysAreStringsRank2 );
      ReqifRelationshipsTest.reqifAttributeValueByIdentifiersMap                                           = new RankHashMap<>( "reqifAttributeValueByIdentifiersMap",                                           2, 256, 0.75f, KeyPredicates.keysAreStringsRank2 );
      ReqifRelationshipsTest.reqifSpecObjectByIdentifierMap                                                = new RankHashMap<>( "reqifSpecObjectByIdentifierMap",                                                1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationshipsTest.reqifSpecObjectByLongNameMap                                                  = new RankHashMap<>( "reqifSpecObjectByLongNameMap",                                                  1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationshipsTest.reqifSpecRelationByTypeSourceTargetIdentifierMap                              = new RankHashMap<>( "reqifSpecRelationBySourceTargetTypeIdentifierMap",                              3, 256, 0.75f, KeyPredicates.keysAreStringsRank3 );
      ReqifRelationshipsTest.reqifSpecRelationTypesByIdentifierMap                                         = new RankHashMap<>( "reqifSpecRelationTypeByIdentifierMap",                                          1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      ReqifRelationshipsTest.reqifSpecRelationTypesByLongNameMap                                           = new RankHashMap<>( "reqifSpecRelationTypeByLongNameMap",                                            1, 256, 0.75f, KeyPredicates.keysAreStringsRank1 );
      //@formatter:on

      /*
       * Build test document
       */

      ReqifRelationshipsTest.testDocumentBuilder = new TestDocumentBuilder(ReqifRelationshipsTest.setValues);

      //@formatter:off
      ReqifRelationshipsTest.testDocumentBuilder.buildDocument
         (
            ReqifRelationshipsTest.branchSpecifications,
            ReqifRelationshipsTest.artifactSpecifications
         );
      //@formatter:on

      /*
       * Get services
       */

      var oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      var synchronizationEndpoint = oseeClient.getSynchronizationEndpoint();

      /*
       * Get and parse the Synchronization Artifact
       */

      var synchronizationArtifactParser = new SynchronizationArtifactParser(synchronizationEndpoint);

      //@formatter:off
      synchronizationArtifactParser.parseTestDocument
         (
            ReqifRelationshipsTest.testDocumentBuilder
               .getBranchIdentifier( ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier )
               .get(),

            ReqifRelationshipsTest.testDocumentBuilder
               .getArtifactIdentifier( ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier, 1 )
               .get(),

            "reqif"
         );

      /*
       * Index the members of the ReqIF by identifier and long name
       */

      //@formatter:off
      synchronizationArtifactParser.parseSpecObjects
         (
            ReqifRelationshipsTest.reqifSpecObjectByIdentifierMap,
            ReqifRelationshipsTest.reqifSpecObjectByLongNameMap
         );

      synchronizationArtifactParser.parseSpecRelationTypes
         (
            ReqifRelationshipsTest.reqifSpecRelationTypesByIdentifierMap,
            ReqifRelationshipsTest.reqifSpecRelationTypesByLongNameMap
         );

      synchronizationArtifactParser.parseSpecRelations
         (
            ReqifRelationshipsTest.reqifSpecRelationByTypeSourceTargetIdentifierMap
         );

      synchronizationArtifactParser.parseAttributeValues
         (
            new Function[]
            {
               (reqifCoreContent) -> ((ReqIFContent) reqifCoreContent).getSpecRelations()
            },
            Identifiable::getIdentifier,
            "getIdentifier",
            ReqifRelationshipsTest.reqifAttributeValueByIdentifiersMap
         );

      synchronizationArtifactParser.parseAttributeDefinitions
         (
            new Function[]
            {
               ( reqifCoreContent ) -> ((ReqIFContent) reqifCoreContent).getSpecTypes()
            },
            Identifiable::getIdentifier,
            "getLongName",
            ReqifRelationshipsTest.reqifAttributeDefinitionBySpecTypeIdentifierAndAttributeDefinitionLongNameMap
         );

      //@formatter:on
   }

   @Test
   public void testRequirementTraceCA2BA() {

      var requirementTraceVerificationRecord =
         ReqifRelationshipsTest.verifyRequirementTrace("Requirement Trace", "Requirement C-A", "Requirement B-A");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceBA2AA() {

      var requirementTraceVerificationRecord =
         ReqifRelationshipsTest.verifyRequirementTrace("Requirement Trace", "Requirement B-A", "Requirement A-A");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceTD2AA2AA() {

      //@formatter:off
      var testDocument2RequirementAASpecterName =
         ReqifRelationshipsTest.testDocumentBuilder
            .getArtifactIdentifier
               (
                  ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier,
                  10
               )
            .orElseThrow()
            .toString();
      //@formatter:on

      var requirementTraceVerificationRecord = ReqifRelationshipsTest.verifyRequirementTrace("Requirement Trace",
         testDocument2RequirementAASpecterName, "Requirement A-A");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

   @Test
   public void testRequirementTraceBA2TD2AA() {

      //@formatter:off
      var testDocument2RequirementAASpecterName =
         ReqifRelationshipsTest.testDocumentBuilder
            .getArtifactIdentifier
               (
                  ReqifRelationshipsTest.testBranchSpecificationRecordIdentifier,
                  10
               )
            .orElseThrow()
            .toString();
      //@formatter:on

      var requirementTraceVerificationRecord = ReqifRelationshipsTest.verifyRequirementTrace("Requirement Trace",
         "Requirement B-A", testDocument2RequirementAASpecterName);

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side A",
         "higher-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueString(requirementTraceVerificationRecord, "Side B",
         "lower-level requirement");

      ReqifRelationshipsTest.verifyRequirementTraceAttributeValueEnumeration(requirementTraceVerificationRecord,
         "RelationTypeMultiplicity", "MANY_TO_MANY");
   }

}

/* EOF */