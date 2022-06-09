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

package org.eclipse.osee.client.integration.tests.integration.synchronization.rest;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.BuilderRelationshipRecord;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ReqIF relationship tests for the Synchronization REST API End point defined in the package
 * {@link org.eclipse.osee.synchronization.rest}.
 *
 * @author Loren K. Ashley
 */

public class ReqifRelationships {

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
       * A {@link BiConsumer} implementation used to assign the attribute value to the test attribute.
       */

      private final BiConsumer<Attribute<?>, Object> attributeSetter;

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
       * The {@link AttributeTypeGeneric} of the test attribute.
       */

      private final AttributeTypeGeneric<?> testAttributeType;

      /**
       * The values to be assigned to the test attributes.
       */

      private final List<Object> testAttributeValues;

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
            Integer                                identifier,
            Integer                                hierarchicalParentIdentifier,
            String                                 name,
            ArtifactTypeToken                      typeToken,
            AttributeTypeGeneric<?>                testAttributeType,
            List<Object>                           testAttributeValues,
            BiConsumer<Attribute<?>,Object>        attributeSetter,
            List<BuilderRelationshipRecord>        builderRelationshipRecords

         )
      {
         this.identifier                   = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name                         = Objects.requireNonNull(name);
         this.artifactTypeToken            = Objects.requireNonNull(typeToken);

         this.testAttributeType            = Objects.requireNonNull(testAttributeType);
         this.testAttributeValues          = Objects.requireNonNull(testAttributeValues);

         this.attributeSetter              = Objects.requireNonNull(attributeSetter);
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
      public BiConsumer<Attribute<?>, Object> getAttributeSetter() {
         return this.attributeSetter;
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

      /**
       * {@inheritDoc}
       */

      @Override
      public AttributeTypeGeneric<?> getTestAttributeType() {
         return this.testAttributeType;
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public List<Object> getTestAttributeValues() {
         return this.testAttributeValues;
      }

   }

   /**
    * Set this flag to <code>false</code> to prevent the test setup code from altering attribute values in the database.
    * The default (normal for testing) value is <code>true</code>.
    */

   private static boolean setValues = true;

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
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "ReqIF Relationship Test Document" ),                                    /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                   ),

            new ArtifactInfoRecord
                   (
                      2,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements A Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "Requirements A Folder" ),                                               /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                   ),

            new ArtifactInfoRecord
                   (
                      3,                                                                                /* Identifier                             (Integer)                               */
                      2,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement A-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "This is Requirement A-A" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                         (
                         )
                   ),

            new ArtifactInfoRecord
                   (
                      4,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements B Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "Requirements B Folder" ),                                               /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                   ),

            new ArtifactInfoRecord
                   (
                      5,                                                                                /* Identifier                             (Integer)                               */
                      4,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement B-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "This is Requirement B-A" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                         (
                           new ArtifactRelationshipInfoRecord
                                  (
                                    CoreRelationTypes.RequirementTrace,                                 /* Relationship Type                      RelationTypeToken                       */
                                    List.of( 3 )                                                        /* Targets                                (List<Integer>)                         */
                                  )
                         )
                   ),

            new ArtifactInfoRecord
                   (
                      6,                                                                                /* Identifier                             (Integer)                               */
                      1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirements C Folder",                                                          /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "Requirements C Folder" ),                                               /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)     */
                   ),

            new ArtifactInfoRecord
                   (
                      7,                                                                                /* Identifier                             (Integer)                               */
                      6,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                      "Requirement C-A",                                                                /* Artifact Name                          (String)                                */
                      CoreArtifactTypes.SoftwareRequirementPlainText,                                   /* Artifact Type                          (ArtifactTypeToken)                     */
                      CoreAttributeTypes.Description,                                                   /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                      List.of( "This is Requirement C-A" ),                                             /* Test Attribute Values                  (List<Object>)                          */
                      ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ), /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                      List.of                                                                           /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                         (
                            new ArtifactRelationshipInfoRecord
                            (
                               CoreRelationTypes.RequirementTrace,                                      /* Relationship Type                      RelationTypeToken                       */
                               List.of( 5 )                                                             /* Targets                                (List<Integer>)                         */
                            )
                         )
                   )

         );
   //@formatter:on

   @SuppressWarnings("unchecked")
   @BeforeClass
   public static void testSetup() {
      //@formatter:off

      var testDocumentBuilder = new TestDocumentBuilder( ReqifRelationships.setValues );

      testDocumentBuilder.buildDocument
                             (
                                (List<BuilderRecord>) (Object) ReqifRelationships.artifactInfoRecords,
                                ReqifRelationships.testBranchName,
                                ReqifRelationships.testBranchCreationComment
                             );

      /*
       * Save identifiers of test document root
       */

      ReqifRelationships.rootBranchId = testDocumentBuilder.getRootBranchId();
      ReqifRelationships.rootArtifactId = testDocumentBuilder.getRootArtifactId();
   }

   @Test
   public void testTrue() {

      Assert.assertTrue( true );
   }

}
