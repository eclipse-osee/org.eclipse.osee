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

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
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
    * A two level map structure where the primary key selects a secondary map and the secondary key is used as the key
    * with the secondary map.
    *
    * @param <Kp> the type of primary map keys
    * @param <Ks> the type of the secondary map keys
    * @param <V> the type of the map values
    */

   interface DoubleMap<Kp, Ks, V> {

      /**
       * Returns the value which is mapped to the primary and secondary keys.
       *
       * @param primaryKey the key used to select the secondary map.
       * @param secondaryKey the key used to select the value from the secondary map.
       * @return when the key pair maps to a value, an {@link Optional} with the selected value; otherwise, an empty
       * {@link Optional}.
       */

      Optional<V> get(Kp primaryKey, Ks secondaryKey);

      /**
       * Associates the provide <code>value</code> with the primary and secondary keys. If a secondary map is not
       * associated with the primary key, the secondary map will be created and associated with the primary key.
       *
       * @param primaryKey the key used to select the secondary map.
       * @param secondaryKey the key used to associate the value with in the secondary map.
       * @param value the value to be associated with the key pair.
       * @return when the key pair currently maps to a value, an {@link Optional} with the previous value; otherwise, an
       * empty {@link Optional}.
       */

      Optional<V> put(Kp primaryKey, Ks secondaryKey, V value);
   }

   /**
    * An implementation of the {@link DoubleMap} interface using {@link HashMap} maps for the primary and secondary
    * maps.
    *
    * @param <Kp> the type of primary map keys
    * @param <Ks> the type of the secondary map keys
    * @param <V> the type of the map values
    */

   static class DoubleHashMap<Kp, Ks, V> implements DoubleMap<Kp, Ks, V> {

      /**
       * The primary map.
       */

      private final HashMap<Kp, Map<Ks, V>> primaryMap;

      /**
       * Constructor creates the primary {@link HashMap} with default initialization values.
       */

      DoubleHashMap() {
         this.primaryMap = new HashMap<>();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Optional<V> get(Kp primaryKey, Ks secondaryKey) {
         var secondaryMap = primaryMap.get(primaryKey);

         if (secondaryMap == null) {
            return Optional.empty();
         }

         var value = secondaryMap.get(secondaryKey);

         return Optional.ofNullable(value);
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Optional<V> put(Kp primaryKey, Ks secondaryKey, V value) {
         var secondaryMap = primaryMap.get(primaryKey);

         if (secondaryMap == null) {
            secondaryMap = new HashMap<Ks, V>();

            primaryMap.put(primaryKey, secondaryMap);

            secondaryMap.put(secondaryKey, value);

            return Optional.empty();
         }

         var priorValue = secondaryMap.put(secondaryKey, value);

         return Optional.ofNullable(priorValue);
      }
   }

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
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * identifier and then by the ReqIF Attribute Definition identifier.
    */

   private static DoubleMap<String, String, AttributeDefinition> reqifAttributeDefinitionByIdentifiersMap;

   /**
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * long name and then by the ReqIF Attribute Definition long name.
    */

   private static DoubleMap<String, String, AttributeDefinition> reqifAttributeDefinitionByLongNamesMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object identifier and then by the ReqIF
    * Attribute Value's Attribute Definition reference Identifier.
    */

   private static DoubleMap<String, String, AttributeValue> reqifAttributeValueByIdentifiersMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object long name and then by the ReqIF
    * Attribute Value's Attribute Definition reference long name.
    */

   private static DoubleMap<String, String, AttributeValue> reqifAttributeValueByLongNamesMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their identifiers.
    */

   private static Map<String, DatatypeDefinition> reqifDatatypeDefinitionByIdentifierMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their long names.
    */

   private static Map<String, DatatypeDefinition> reqifDatatypeDefinitionByLongNameMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their identifiers. This map does not include the ReqIF
    * Specifications.
    */

   private static Map<String, SpecObject> reqifSpecObjectByIdentifierMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their long names. This map does not include the ReqIF
    * Specifications.
    */

   private static Map<String, SpecObject> reqifSpecObjectByLongNameMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their
    * identifiers.
    */

   private static Map<String, SpecType> reqifSpecTypeByIdentifierMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their long names.
    */

   private static Map<String, SpecType> reqifSpecTypeByLongNameMap;

   /**
    * Saves the {@link ReqIF} DOM of the test document read back from the server.
    */

   private static ReqIF reqifTestDocument;

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

   /**
    * For each provided {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>adds the {@link Identifiable} to the maps using the identifier or long name as the key.</li>
    * </ul>
    *
    * @param reqifIdentifiables the list of {@link Identifiable> objects to be added to the maps.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when:
    * <ul>
    * <li>an identifier is not extracted from the {@link Identifiable},</li>
    * <li>a long name is not extracted from the {@link Identifiable},</li>
    * <li>an entry for the {@link Identifiable} already exits in one of the maps.</li>
    * </ul>
    */

   private static void mapIdentifiables(EList<? extends Identifiable> reqifIdentifiables, Map<String, ? extends Identifiable> byIdentifierMap, Map<String, ? extends Identifiable> byLongNameMap) {

      for (var reqifIdentifiable : reqifIdentifiables) {

         var identifier = reqifIdentifiable.getIdentifier();

         Assert.assertNotNull(identifier);
         Assert.assertFalse(byIdentifierMap.containsKey(identifier));

         ((Map<String, Identifiable>) byIdentifierMap).put(identifier, reqifIdentifiable);

         var longName = reqifIdentifiable.getLongName();

         Assert.assertNotNull(longName);
         Assert.assertFalse(byLongNameMap.containsKey(longName));

         ((Map<String, Identifiable>) byLongNameMap).put(longName, reqifIdentifiable);
      }
   }

   /**
    * For each provided secondary {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>adds the secondary {@link Identifiable} to the maps using the primary and secondary identifiers or long names
    * as keys.</li>
    * </ul>
    *
    * @param primaryIdentifier the identifier to use as the primary map key for the <code>byIdentifierMap</code>. This
    * parameter may be <code>null</code>. When <code>null</code> the <code>byIdentifierMap</code> will not be populated.
    * @param primaryLongName the long name to use as the primary map key for the <code>byLongNameMap</code>. This
    * parameter may be <code>null</code>. When <code>null</code> the <code>byLongNameMap</code> will not be populated.
    * @param reqifSecondaryEObjects the list of secondary {@link Identifiable} objects that were extracted from the
    * primary {@link Identifiable}.
    * @param secondaryIdentifierFunction a {@link Function} used to extract the identifier from the secondary
    * {@link Identifiable}.
    * @param secondaryLongNameFunction a {@link Function } used to extract the long name from the secondary
    * {@link Identifiable}.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when:
    * <ul>
    * <li>an identifier is needed and not extracted from the secondary {@link Identifiable},</li>
    * <li>a long name is needed and not extracted from the secondary {@link Identifiable},
    * <li>
    * <li>an entry for the secondary {@link Identifiable} already exists in a map being populated.</li>
    * </ul>
    */
   private static void mapSecondaryEObjects(String primaryIdentifier, String primaryLongName, EList<? extends EObject> reqifSecondaryEObjects, Function<EObject, String> secondaryIdentifierFunction, Function<EObject, String> secondaryLongNameFunction, DoubleMap<String, String, ? extends EObject> byIdentifierMap, DoubleMap<String, String, ? extends EObject> byLongNameMap) {

      for (var reqifSecondaryEObject : reqifSecondaryEObjects) {

         if (Objects.nonNull(primaryIdentifier)) {
            var secondaryIdentifier = secondaryIdentifierFunction.apply(reqifSecondaryEObject);

            Assert.assertNotNull(secondaryIdentifier);

            var priorValueOptional = ((DoubleMap<String, String, EObject>) byIdentifierMap).put(primaryIdentifier,
               secondaryIdentifier, reqifSecondaryEObject);

            Assert.assertTrue(priorValueOptional.isEmpty());
         }

         if (Objects.nonNull(primaryLongName)) {
            var secondaryLongName = secondaryLongNameFunction.apply(reqifSecondaryEObject);

            Assert.assertNotNull(secondaryLongName);

            var priorValueOptional = ((DoubleMap<String, String, EObject>) byIdentifierMap).put(primaryLongName,
               secondaryLongName, reqifSecondaryEObject);

            Assert.assertTrue(priorValueOptional.isEmpty());
         }
      }
   }

   /**
    * For each provided primary {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>extracts a list of secondary {@link Identifiable} objects.</li>
    * </ul>
    * The secondary {@link Identifiable} objects are then added to the maps using the identifier and long name keys from
    * the associated primary {@link Identifiable} object.
    *
    * @param reqifPrimaryIdentifiables list of ReqIF {@link Identifiable} objects to be stored into the maps.
    * @param secondaryIdentifiablesFunction a {@link Function} used to extract the secondary {@link Identifiable} from
    * the primary {@link Identifiable}.
    * @param secondaryIdentifierFunction a {@link Function} used to extract the identifier from the secondary
    * {@link Identifiable}.
    * @param secondaryLongNameFunction a {@link Function} used to extract the long name from the secondary
    * {@link Identifiable}.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>an identifier cannot be extracted from a primary {@link Identifiable}, or</li>
    * <li>an long name cannot be extracted from a primary {@link Identifiable}.</li>
    * </ul>
    */

   private static void mapSecondaryEObjects(EList<? extends Identifiable> reqifPrimaryIdentifiables, Function<Identifiable, EList<? extends EObject>> secondaryIdentifiablesFunction, Function<EObject, String> secondaryIdentifierFunction, Function<EObject, String> secondaryLongNameFunction, DoubleMap<String, String, ? extends EObject> byIdentifierMap, DoubleMap<String, String, ? extends EObject> byLongNameMap) {

      for (var reqifPrimaryIdentifiable : reqifPrimaryIdentifiables) {

         var primaryIdentifier = reqifPrimaryIdentifiable.getIdentifier();

         Assert.assertNotNull(primaryIdentifier);

         var primaryLongName = reqifPrimaryIdentifiable.getLongName();

         Assert.assertNotNull(primaryLongName);

         var reqifSecondaryIdentifiables = secondaryIdentifiablesFunction.apply(reqifPrimaryIdentifiable);

         SynchronizationEndpointTest.mapSecondaryEObjects(primaryIdentifier, primaryLongName,
            reqifSecondaryIdentifiables, secondaryIdentifierFunction, secondaryLongNameFunction, byIdentifierMap,
            byLongNameMap);
      }
   }

   /**
    * Parses ReqIF AttributeDefinitions from the test document into {@link DoubleMap} keyed with the Specification Type
    * or Spec Object Type identifier or long name; and then keyed by the Attribute Definitions's identifier or long
    * name;
    *
    * @param reqif the test document to parse
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>the test document core content is missing,</li>
    * <li>the test document spec types are missing.</li>
    * </ul>
    */

   private static void parseAttributeDefinitions(ReqIF reqif, DoubleMap<String, String, AttributeDefinition> byIdentifierMap, DoubleMap<String, String, AttributeDefinition> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecTypes = reqifCoreContent.getSpecTypes();

      Assert.assertNotNull(reqifSpecTypes);

      SynchronizationEndpointTest.mapSecondaryEObjects(reqifSpecTypes,
         (specType) -> ((SpecType) specType).getSpecAttributes(), (eObject) -> ((Identifiable) eObject).getIdentifier(),
         (eObject) -> ((Identifiable) eObject).getLongName(), byIdentifierMap, byLongNameMap);
   }

   /**
    * ReqIF Attribute Value objects don't have an identifier or long name. The Attribute Value objects reference an
    * Attribute Definition which does have an identifier and long name. The referenced Attribute Definition's identifier
    * and long name are used for the Attribute Value. The ReqIF Attribute Value classes do not share a common base or
    * interface which allows for access to the Attribute Definition or the Attribute Definition's values. Since this is
    * test code, reflection is used to obtain the Attribute Definition object and then either it's identifier or long
    * name instead of implementing class specific code.
    *
    * @param eObject the ReqIF Attribute Value to obtain an identifier or long name for.
    * @param secondaryFunctionName use "getIdentifier" to obtain the identifier and "getLongName" to get the long name.
    * @return the identifier or long name to be used for the Attribute Value.
    * @throws AssertionError when any of the reflective methods fail.
    */

   private static String parseAttributeValueIdentifiers(EObject eObject, String secondaryFunctionName) {
      try {
         var theClass = eObject.getClass();

         var getDefinitionMethod = theClass.getDeclaredMethod("getDefinition");

         var attributeDefinition = getDefinitionMethod.invoke(eObject);

         var attributeDefinitionClass = attributeDefinition.getClass();

         var getIdentifierMethod = attributeDefinitionClass.getMethod(secondaryFunctionName);

         var value = (String) getIdentifierMethod.invoke(attributeDefinition);

         return value;
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
         //Should never get here.
         return null;
      }
   }

   /**
    * Parses ReqIF AttributeValues from the test document into {@link DoubleMap} keyed with the Spec Object identifier
    * or long name; and then keyed by the Attribute Value's Attribute Definition reference identifier or long name;
    *
    * @param reqif the test document to parse
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>the test document core content is missing,</li>
    * <li>the test document spec objects are missing.</li>
    * </ul>
    */

   private static void parseAttributeValues(ReqIF reqif, DoubleMap<String, String, AttributeValue> byIdentifierMap, DoubleMap<String, String, AttributeValue> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecObjects = reqifCoreContent.getSpecObjects();

      Assert.assertNotNull(reqifSpecObjects);

      SynchronizationEndpointTest.mapSecondaryEObjects(reqifSpecObjects,
         (specObject) -> ((SpecObject) specObject).getValues(),
         (eObject) -> SynchronizationEndpointTest.parseAttributeValueIdentifiers(eObject, "getIdentifier"),
         (eObject) -> SynchronizationEndpointTest.parseAttributeValueIdentifiers(eObject, "getLongName"),
         byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Data Type Definitions from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF data type definitions are not available,</li>
    * </ul>
    */

   private static void parseDatatypeDefinitions(ReqIF reqif, Map<String, DatatypeDefinition> byIdentifierMap, Map<String, DatatypeDefinition> byLongNameMap) {

      var reqifCoreContent = reqif.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifDatatypes = reqifCoreContent.getDatatypes();

      Assert.assertNotNull(reqifDatatypes);

      SynchronizationEndpointTest.mapIdentifiables(reqifDatatypes, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Objects from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF spec objects are not available,</li>
    * </ul>
    */

   private static void parseSpecObjects(ReqIF reqif, Map<String, SpecObject> byIdentifierMap, Map<String, SpecObject> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecObjects = reqifCoreContent.getSpecObjects();

      Assert.assertNotNull(reqifSpecObjects);

      SynchronizationEndpointTest.mapIdentifiables(reqifSpecObjects, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Types from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF spec types are not available,</li>
    * </ul>
    */

   private static void parseSpecTypes(ReqIF reqif, Map<String, SpecType> byIdentifierMap, Map<String, SpecType> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecTypes = reqifCoreContent.getSpecTypes();

      Assert.assertNotNull(reqifSpecTypes);

      SynchronizationEndpointTest.mapIdentifiables(reqifSpecTypes, byIdentifierMap, byLongNameMap);
   }

   /**
    * Get the test document from the server and parse it into a ReqIF DOM.
    *
    * @return the ReqIF DOM.
    * @throws AssertionError when
    * <ul>
    * <li>a response is not received from the server,</li>
    * <li>the server response is not OK,</li>
    * <li>the received resource does not have any contents,</li>
    * <li>the received resource does not contain a {@link ReqIF} object.</li>
    * </ul>
    * @throws RuntimeException when an error occurs loading the {@link InputStream} received from the server into a
    * resource.
    */

   private static ReqIF parseTestDocument() {
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(
         SynchronizationEndpointTest.rootBranchId, SynchronizationEndpointTest.rootArtifactId,
         synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);

      var reqIfInputStream = response.readEntity(InputStream.class);

      var resourceSet = new ResourceSetImpl();

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("reqif",
         new ReqIF10ResourceFactoryImpl());

      var uri = URI.createFileURI("i.reqif");

      var resource = resourceSet.createResource(uri);

      try {
         resource.load(reqIfInputStream, null);
      } catch (Exception e) {
         throw new RuntimeException("Resource Load Failed", e);
      }

      var eObjectList = resource.getContents();

      Assert.assertNotNull(eObjectList);

      var rootEObject = eObjectList.get(0);

      Assert.assertTrue(rootEObject instanceof ReqIF);

      return (ReqIF) rootEObject;
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

      /*
       * Create tracking maps
       */

      SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap = new DoubleHashMap<>();
      SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap = new DoubleHashMap<>();
      SynchronizationEndpointTest.reqifAttributeValueByIdentifiersMap = new DoubleHashMap<>();
      SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap = new DoubleHashMap<>();
      SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap = new HashMap<>();
      SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap = new HashMap<>();
      SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap = new HashMap<>();
      SynchronizationEndpointTest.reqifSpecObjectByLongNameMap = new HashMap<>();
      SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap = new HashMap<>();
      SynchronizationEndpointTest.reqifSpecTypeByLongNameMap = new HashMap<>();

      /*
       * Get and save the ReqIF test document from the server
       */

      SynchronizationEndpointTest.reqifTestDocument = SynchronizationEndpointTest.parseTestDocument();

      /*
       * Index the members of the ReqIF by identifier and long name
       */

      SynchronizationEndpointTest.parseSpecTypes
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecTypeByLongNameMap
         );

      SynchronizationEndpointTest.parseDatatypeDefinitions
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap,
            SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap
         );

      SynchronizationEndpointTest.parseAttributeDefinitions
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap,
            SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap
         );

      SynchronizationEndpointTest.parseSpecObjects
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecObjectByLongNameMap
         );

      SynchronizationEndpointTest.parseAttributeValues
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifAttributeValueByIdentifiersMap,
            SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap
         );

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
