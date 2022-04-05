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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests for the Synchronization REST API End point defined in the package
 * {@link org.eclipse.osee.synchronization.rest}.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointTest {

   /**
    * Testing rule used to prevent modification of a production database.
    */

   @ClassRule
   public static NotProductionDataStoreRule rule = new NotProductionDataStoreRule();

   /**
    * Class used to define and build test artifacts with a test attribute in the local database. Only one attribute per
    * test artifact is setup and used for testing.
    */

   private static class ArtifactInfoRecord {

      /**
       * A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforce bad test setup will result if
       * not unique. The identifier 0 is reserved for the Default Hierarchy Root artifact.
       */

      private final Integer identifier;

      /**
       * The {@link ArtifactInfoRecord#identifier} for the test artifact that is the hierarchical parent of the test
       * artifact defined by this record. Use 0 for top level artifacts.
       */

      private final Integer hierarchicalParentIdentifier;

      /**
       * A name for the test artifact.
       */

      private final String name;

      /**
       * The {@link ArtifactTypeToken} of the test artifact.
       */

      private final ArtifactTypeToken typeToken;

      /**
       * The {@link AttributeTypeGeneric> of the test attribute.
       */

      private final AttributeTypeGeneric<?> testAttributeType;

      /**
       * The value to be assigned to the test attribute.
       */

      private final Object testAttributeValue;

      /**
       * A {@link BiConsumer} implementation used to assign the attribute value to the test attribute.
       */

      private final BiConsumer<Attribute<?>, Object> attributeSetter;

      /**
       * The {@link ArtifactToken} ({@link ArtifactId}) of the test artifact.
       */

      private ArtifactToken artifactToken;

      /**
       * The test {@link Artifact} loaded from the database.
       */

      private Artifact artifact;

      /**
       * The test {@link Attribute} loaded from the database.
       */

      private Attribute<?> attribute;

      /**
       * Constructs a new empty {@link ArtifactInfoRecord} with the specified parameters.
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
       */

      ArtifactInfoRecord(Integer identifier, Integer hierarchicalParentIdentifier, String name, ArtifactTypeToken typeToken, AttributeTypeGeneric<?> testAttributeType, Object testAttributeValue, BiConsumer<Attribute<?>, Object> attributeSetter) {
         this.identifier = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name = Objects.requireNonNull(name);
         this.typeToken = Objects.requireNonNull(typeToken);
         this.testAttributeType = Objects.requireNonNull(testAttributeType);
         this.testAttributeValue = Objects.requireNonNull(testAttributeValue);
         this.attributeSetter = Objects.requireNonNull(attributeSetter);

         this.artifactToken = null;
         this.artifact = null;
         this.attribute = null;
      }

      /**
       * Creates a new artifact under the specified parent on the specified branch.
       *
       * @param artifactEndpoint The REST API end point for artifact functions.
       * @param parentBranchId The branch to create the new artifact upon.
       * @param parentArtifactId The hierarchical parent of the artifact to be created.
       * @return The {@link ArtifactToken} (identifier) for the newly created artifact.
       */

      private ArtifactToken createChild(ArtifactEndpoint artifactEndpoint, BranchId parentBranchId, ArtifactId parentArtifactId) {
         var newArtifactToken =
            artifactEndpoint.createArtifact(parentBranchId, this.typeToken, parentArtifactId, this.name);
         return artifactEndpoint.getArtifactToken(newArtifactToken);
      }

      /**
       * Creates the test attribute for the test artifact.
       *
       * @return the created attribute.
       * @throws NoSuchElementException when unable to obtain the created attribute.
       */

      private Attribute<?> createTestAttribute() {
         var artifact = this.getArtifact();
         artifact.addAttribute(this.testAttributeType);

         var attributes = artifact.getAttributes(this.testAttributeType);

         if (attributes.size() == 1) {
            return attributes.get(0);
         }

         throw new NoSuchElementException();
      }

      /**
       * Gets the test artifact.
       *
       * @return the test artifact.
       */

      Artifact getArtifact() {
         assert Objects.nonNull(this.artifact);
         return this.artifact;
      }

      /**
       * Gets the identifier for the test artifact.
       *
       * @return the test artifact identifier.
       */

      ArtifactToken getArtifactToken() {
         assert Objects.nonNull(this.artifactToken);
         return this.artifactToken;
      }

      /**
       * Gets a the hierarchical child {@link Artifact} by artifact name that is represented by this
       * {@link ArtifactInfoRecord}.
       *
       * @param relationEndpoint REST API end point for relationships.
       * @param parentArtifactId the {@link ArtifactId} of the artifact to find a child of.
       * @return If found, an {@link Optional} containing the loaded {@link Artifact}; otherwise, an empty
       * {@link Optional}.
       */

      private Optional<ArtifactToken> getChildByName(RelationEndpoint relationEndpoint, ArtifactId parentArtifactId) {
         //@formatter:off
         return
            relationEndpoint
               .getRelatedHierarchy( parentArtifactId )
               .stream()
               .filter( artifact -> artifact.getName().equals( this.name ) )
               .findFirst()
               ;
         //@formatter:on
      }

      /**
       * Gets or creates the test attribute for the test artifact. The test artifact must be obtained before calling
       * this method.
       */

      void getOrCreateAttribute() {
         assert Objects.isNull(this.attribute);
         this.attribute = this.getTestAttribute().orElseGet(this::createTestAttribute);
      }

      /**
       * Gets or creates the artifact identifier for the test artifact.
       *
       * @param relationEndpoint The REST API end point for obtaining related artifacts.
       * @param artifactEndpoint The REST API end point for creating artifacts.
       * @param parentBranchId The identifier of the branch to get or create the test artifact on.
       * @param hierarchicalParentArtifactIdMap A map of the {@link ArtifactToken} (identifiers) of the hierarchical
       * parent artifacts by {@link ArtifactInfoRecord} identifiers. The {@link ArtifactToken} for the obtained or
       * created artifact will be added to the map with the identifier of this {@link ArtifactInfoRecord} as the key.
       */

      void getOrCreateArtifactToken(RelationEndpoint relationEndpoint, ArtifactEndpoint artifactEndpoint, BranchId parentBranchId, Map<Integer, ArtifactId> hierarchicalParentArtifactIdMap) {
         assert Objects.isNull(this.artifactToken);
         var hierarchicalParentArtifactId = hierarchicalParentArtifactIdMap.get(this.hierarchicalParentIdentifier);

         this.artifactToken = this.getChildByName(relationEndpoint, hierarchicalParentArtifactId).orElseGet(
            () -> this.createChild(artifactEndpoint, parentBranchId, hierarchicalParentArtifactId));

         hierarchicalParentArtifactIdMap.put(this.identifier, artifactToken);
      }

      /**
       * Gets the test attribute from the test artifact.
       *
       * @return An {@link Optional} with the test attribute if it exists; otherwise, an empty {@link Optional}.
       */

      private Optional<Attribute<?>> getTestAttribute() {
         assert Objects.nonNull(this.artifact);
         var attributes = this.artifact.getAttributes(this.testAttributeType); //this.getTestAttributeTypeId());

         return (attributes.size() == 1) ? Optional.of(attributes.get(0)) : Optional.empty();
      }

      /**
       * Save the artifact back to the database if it has been modified.
       */

      void persistIfDirty() {
         if (this.artifact.isDirty()) {
            this.artifact.persist("Three Blind Mice");
         }
      }

      /**
       * If the test attribute is not the expected value, set the test attribute value.
       */

      void setAttributeValue() {
         if (!this.attribute.getValue().equals(this.testAttributeValue)) {
            this.attributeSetter.accept(this.attribute, this.testAttributeValue);
         }
      }

      /**
       * Saves the test artifact that was retrieved from the database.
       *
       * @param artifact the {@link Artifact} read from the database.
       * @return this {@link ArtifactInfoRecord}.
       */

      ArtifactInfoRecord setArtifact(Artifact artifact) {
         assert Objects.isNull(this.artifact);
         this.artifact = Objects.requireNonNull(artifact);
         return this;
      }

   }

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
                    1,                                        /* Identifier                     (Integer)                 */
                    0,                                        /* Hierarchical Parent Identifier (Integer)                 */
                    "ReqIF Test Specifications",              /* Artifact Name                  (String)                  */
                    CoreArtifactTypes.Folder,                 /* Artifact Type                  (ArtifactTypeToken)       */
                    CoreAttributeTypes.Description,           /* Test Attribute Type            (AttributeTypeGeneric<?>) */
                    "Motley folder of test artifacts",        /* Test Attribute Value           (Object)                  */
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
                  ),

           new ArtifactInfoRecord
                  (
                    2,
                    1,
                    "ARTIFACT_IDENTIFIER_TESTER",
                    CoreArtifactTypes.GitCommit,
                    CoreAttributeTypes.UserArtifactId,
                    ArtifactId.valueOf( 1938 ),
                    ( attribute, value ) -> ((ArtifactReferenceAttribute) attribute).setValue( (ArtifactId) value )
                  ),

           new ArtifactInfoRecord
                  (
                    3,
                    1,
                    "BOOLEAN_TESTER",
                    CoreArtifactTypes.User,
                    CoreAttributeTypes.Active,
                    true,
                    ( attribute, value ) -> ((BooleanAttribute) attribute).setValue( (Boolean) value )
                  ),

           new ArtifactInfoRecord
                  (
                    4,
                    1,
                    "DATE_TESTER",
                    CoreArtifactTypes.CertificationBaselineEvent,
                    CoreAttributeTypes.BaselinedTimestamp,
                    SynchronizationEndpointTest.getTestDate(),
                    ( attribute, value ) -> ((DateAttribute) attribute).setValue( (Date) value )
                  ),

           new ArtifactInfoRecord
                  (
                    5,
                    1,
                    "DOUBLE_TESTER",
                    SynchronizationEndpointTest.getArtifactType( "Work Package" ),
                    SynchronizationEndpointTest.getAttributeType( "ats.Estimated Hours" ),
                    8.75,
                    ( attribute, value ) -> ((FloatingPointAttribute) attribute).setValue( (Double) value )
                  ),

           new ArtifactInfoRecord
                  (
                    6,
                    1,
                    "INTEGER_TESTER",
                    CoreArtifactTypes.CertificationBaselineEvent,
                    CoreAttributeTypes.ReviewId,
                    42,
                    ( attribute, value ) -> ((IntegerAttribute) attribute).setValue( (Integer) value )
                  ),

           new ArtifactInfoRecord
                  (
                    7,
                    1,
                    "LONG_TESTER",
                    SynchronizationEndpointTest.getArtifactType( "Team Definition" ),
                    SynchronizationEndpointTest.getAttributeType( "ats.Task Set Id" ),
                    420L,
                    ( attribute, value ) -> ((LongAttribute) attribute).setValue( (Long) value )
                  ),

           new ArtifactInfoRecord
                  (
                    8,
                    1,
                    "STRING_TESTER",
                    CoreArtifactTypes.Artifact,
                    CoreAttributeTypes.Description,
                    "Three cats are required for all great software developments.",
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
                  ),

           new ArtifactInfoRecord
                  (
                    9,
                    1,
                    "STRING_WORD_ML_TESTER",
                    CoreArtifactTypes.MsWordWholeDocument,
                    CoreAttributeTypes.WholeWordContent,
                    "Three cats are required for all great software developments.",
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
                  ),

           new ArtifactInfoRecord
                  (
                    10,
                    1,
                    "URI_TESTER",
                    CoreArtifactTypes.OseeTypeDefinition,
                    CoreAttributeTypes.UriGeneralStringData,
                    "http://org.eclipse.org",
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value )
                  )

         );
   //@formatter:on

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId rootBranchId;

   /**
    * Saves a reference to the {@link SynchronizationEndpoint} used to make test call to the API.
    */

   private static SynchronizationEndpoint synchronizationEndpoint;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "ReqIF Test Branch";

   /**
    * Creates a new branch in the database.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName The name to assign to the new branch.
    * @return The newly created {@link Branch}.
    */

   private static Branch createTestBranch(BranchEndpoint branchEndpoint, String branchName) {
      var newBranch = new NewBranch();
      newBranch.setAssociatedArtifact(ArtifactId.SENTINEL);
      newBranch.setBranchName(branchName);
      newBranch.setBranchType(BranchType.WORKING);
      newBranch.setCreationComment("For ReqIF Synchronization Artifact Testing");
      newBranch.setMergeAddressingQueryId(0L);
      newBranch.setMergeDestinationBranchId(null);
      newBranch.setParentBranchId(CoreBranches.SYSTEM_ROOT);
      newBranch.setSourceTransactionId(TransactionManager.getHeadTransaction(CoreBranches.SYSTEM_ROOT));
      newBranch.setTxCopyBranchType(false);

      var newBranchId = branchEndpoint.createBranch(newBranch);

      return branchEndpoint.getBranchById(newBranchId);
   }

   /**
    * Gets the {@link ArtifactTypeToken} for the named artifact type. Not all of the artifact type classes are available
    * to the test suite at compile time. This method provides a means to specify test artifacts where the class
    * instantiation of the artifact type definition is not available to compile.
    *
    * @param name the artifact type name.
    * @return the {@link ArtifactTypeToken} for the named artifact type.
    */

   private static ArtifactTypeToken getArtifactType(String name) {
      return ServiceUtil.getTokenService().getArtifactType(name);
   }

   /**
    * Gets the {@link AttributeTypeGeneric} for the named attribute type. Not all of the attribute type classes are
    * available to the test suite at compile time. This method provides a means to specify test attributes where the
    * class instantiation of the attribute definition is not available to compile.
    *
    * @param name the attribute type name.
    * @return the {@link AttributeTypeGeneric} for the named attribute type.
    */

   private static AttributeTypeGeneric<?> getAttributeType(String name) {
      return ServiceUtil.getTokenService().getAttributeType(name);
   }

   /**
    * Gets a {@link Branch} by name.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName the name of the branch to get.
    * @return If found, an {@link Optional} containing the loaded {@link Branch}; otherwise, an empty {@link Optional}.
    */

   private static Optional<Branch> getBranchByName(BranchEndpoint branchEndpoint, String branchName) {
      //@formatter:off
      return
         branchEndpoint
            .getBranches("", "", "", false, false, SynchronizationEndpointTest.testBranchName, "", null, null, null)
            .stream()
            .filter( branch -> branch.getName().equals( branchName ) )
            .findFirst()
            ;
      //@formatter:on
   }

   /**
    * Generates a known {@link Date} value for testing.
    *
    * @return the {@link Date} October 26, 1967.
    */

   private static Date getTestDate() {
      var calendar = Calendar.getInstance();
      calendar.set(1967, 10 - 1, 26);
      return calendar.getTime();
   }

   @BeforeClass
   public static void testSetup() {
      //@formatter:off

      /*
       * Get OSEE Client
       */

      var oseeClient = OsgiUtil.getService( DemoChoice.class, OseeClient.class );

      /*
       * Get Synchronization Endpoint
       */

      SynchronizationEndpointTest.synchronizationEndpoint = oseeClient.getSynchronizationEndpoint();

      Assert.assertNotNull( SynchronizationEndpointTest.synchronizationEndpoint );

      /*
       * Get Branch end point for test data setup
       */

      var branchEndpoint = oseeClient.getBranchEndpoint();

      /*
       * Get or Create ReqIF test branch
       */

      var testBranch = SynchronizationEndpointTest.getBranchByName( branchEndpoint, SynchronizationEndpointTest.testBranchName )
                          .orElseGet( () -> SynchronizationEndpointTest.createTestBranch( branchEndpoint, SynchronizationEndpointTest.testBranchName ) );

      var testBranchId = BranchId.valueOf( testBranch.getId() );

      /*
       * Get ArtifactEndpoint and RelationEndpoint for the branch
       */

      var artifactEndpoint = oseeClient.getArtifactEndpoint( testBranchId );

      Assert.assertNotNull( artifactEndpoint );

      var relationEndpoint = oseeClient.getRelationEndpoint( testBranchId );

      Assert.assertNotNull( relationEndpoint );

      /*
       * Get Branch Root
       */

      var rootArtifactToken = CoreArtifactTokens.DefaultHierarchyRoot;

      /*
       * Load Artifacts
       */

      /*
       * Map of ArtifactToken objects by ArtifactInfoRecord identifiers. The branch default hierarchical root is saved
       * with the key of 0. This map is used to lookup the hierarchical parent ArtifactToken objects.
       */

      var hierarchicalParentArtifactIdMap = new HashMap<Integer, ArtifactId>();

      hierarchicalParentArtifactIdMap.put( 0, rootArtifactToken );

      /*
       * Map of ArtifactInfoRecord objects by ArtifactIds. This map is used to associate the loaded Artifact objects
       * back to the ArtifactInfoRecord objects.
       */

      var artifactInfoRecordByArtifactIdMap = new HashMap<ArtifactId, ArtifactInfoRecord>();

      /*
       * Load the artifacts and set the test attribute values
       */

      ArtifactLoader.loadArtifacts
         (
           SynchronizationEndpointTest.artifactInfoRecords.stream()
              .peek( artifactInfoRecord -> artifactInfoRecord.getOrCreateArtifactToken( relationEndpoint, artifactEndpoint, testBranchId, hierarchicalParentArtifactIdMap ) )
              .peek( artifactInfoRecord -> artifactInfoRecordByArtifactIdMap.put( artifactInfoRecord.getArtifactToken(), artifactInfoRecord ) )
              .map( ArtifactInfoRecord::getArtifactToken )
              .collect( Collectors.toList() ),
           testBranchId,
           LoadLevel.ALL,
           LoadType.RELOAD_CACHE,
           DeletionFlag.EXCLUDE_DELETED
         ).stream()
             .map( ( artifact ) -> artifactInfoRecordByArtifactIdMap.get( ArtifactId.valueOf( artifact.getId() ) ).setArtifact( artifact ) )
             .peek( ArtifactInfoRecord::getOrCreateAttribute )
             .peek( ArtifactInfoRecord::setAttributeValue )
             .forEach( ArtifactInfoRecord::persistIfDirty );

      /*
       * Save identifiers of test document root
       */

      SynchronizationEndpointTest.rootBranchId = testBranchId;
      SynchronizationEndpointTest.rootArtifactId = ArtifactId.valueOf( SynchronizationEndpointTest.artifactInfoRecords.get( 0 ).getArtifact().getId() );

      //@formatter:on
   }

   @Test
   public void getByBranchIdArtifactIdOk() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId,
         artifactId, synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void getByBranchIdArtifactIdKoBadArtifactType() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "ZooCreatures";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId, artifactId,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("Request for a Synchronization Artifact with an unknown artifact type."));
      }

      Assert.assertTrue(exceptionCought);
   }

   @Test
   public void getByRootsOk() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String artifactId = CoreArtifactTokens.DefaultHierarchyRoot.getIdString();
      String roots = branchId + ":" + artifactId;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
         synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void getByRootsKoBadRootsNonDigitFirstBranch() {
      String roots = "10a:1,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitSecondBranch() {
      String roots = "10:1,2,3;11z:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitFirstArtifact() {
      String roots = "10:1a,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitLastArtifact() {
      String roots = "10:1,2,3;11:4,5,6a";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsBranchDelimiterOutOfPlace() {
      String roots = "10:1:2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsArtifactDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11,4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsSpecificationDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11;4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

}

/* EOF */
