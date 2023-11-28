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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.Response;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.ListMap;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.Assert;

/**
 * Class for creating or verifying a tree of artifacts on a branch in a test database.
 *
 * @author Loren K. Ashley
 */

public class TestDocumentBuilder {

   /**
    * Class to verify or create a relationships between artifacts.
    */

   private class RelationshipSourceTargetTypeRecord {

      /**
       * Saves the {@link RelationTypeToken} for the relationship type.
       */

      private final RelationTypeToken relationTypeToken;

      /**
       * Saves the source {@link Artifact} for the relationship.
       */

      private final Artifact sourceArtifact;

      /**
       * Saves the target {@link Artifact} for the relationship.
       */

      private final Artifact targetArtifact;

      /**
       * Creates a new {@link RelationshipSourceTargetTypeRecord}.
       *
       * @param sourceArtifact the source {@link Artifact}.
       * @param targetArtifact the target {@link Artifact}.
       * @param relationTypeToken the {@link RelationTypeToken} for the relationship type.
       */

      RelationshipSourceTargetTypeRecord(Artifact sourceArtifact, Artifact targetArtifact, RelationTypeToken relationTypeToken) {
         this.sourceArtifact = Objects.requireNonNull(sourceArtifact);
         this.targetArtifact = Objects.requireNonNull(targetArtifact);
         this.relationTypeToken = Objects.requireNonNull(relationTypeToken);
      }

      /**
       * Creates the represented relationship between the source and target artifacts.
       *
       * @param relationEndpoint the ORCS REST API end point for managing relationships.
       * @return the server HTTP {@link Response}.
       */

      Response create(RelationEndpoint relationEndpoint) {
         return relationEndpoint.createRelationByType(this.targetArtifact, this.sourceArtifact, this.relationTypeToken);
      }

      /**
       * Verifies a relationship of the correct type exists between the source and target artifacts.
       *
       * @return <code>true</code>, when the represented relationship is present between the artifacts; otherwise,
       * <code>false</code>.
       */

      boolean isNotRelated() {
         return !RelationManager.getRelatedArtifacts(this.sourceArtifact, this.relationTypeToken,
            RelationSide.SIDE_A).contains(this.targetArtifact);
      }
   }

   /**
    * Saves a handle to the {@link BranchEndpoint}
    */

   private final BranchEndpoint branchEndpoint;

   /**
    * Ordered map of {@link BranchSpecificationRecordWrapper}s by {@link BranchSpecificationRecord} identifier.
    */

   private ListMap<Integer, BranchSpecificationRecordWrapper> branchSpecificationRecordWrapperByIdMap;

   /**
    * Double map of {@link ArtifactSpecificationRecordWrapper}s by {@link BranchSpecificationRecord} identifier and
    * {@link ArtifactSpecificationRecord} identifier.
    */

   private final DoubleMap<Integer, Integer, ArtifactSpecificationRecordWrapper> builderRecordWrapperMap;

   /**
    * Flag indicates if test document has been built.
    */

   private boolean built;

   /**
    * Saves a handle to the {@link OseeClient}.
    */

   private final OseeClient oseeClient;

   /**
    * When <code>false</code> the test setup code will not alter attribute values in the database.
    */

   private final boolean setValues;

   /**
    * Creates a new {@link TestDocumentBuilder}.
    *
    * @param setValues <code>false</code> to prevent the test setup code from altering attribute values in the database.
    */

   public TestDocumentBuilder(boolean setValues) {

      this.built = false;
      this.setValues = setValues;
      this.builderRecordWrapperMap = new DoubleHashMap<>();

      /*
       * Get OSEE Client
       */

      this.oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      Assert.assertNotNull("TestDocumentBuilder::buildDocument, Failed to get OSEE Client.", oseeClient);

      /*
       * Get Branch end point for test data setup
       */

      this.branchEndpoint = oseeClient.getBranchEndpoint();

      Assert.assertNotNull("TestDocumentBuilder::buildDocument, Failed to get BranchEndpoint.", branchEndpoint);

   }

   /**
    * Creates or verifies the test document specified by the {@link List} of {@link BuildRecord}s.
    *
    * @param branchSpecificationRecords
    * @param builderRecords list of {@link ArtifactSpecificationRecord}s specifying the artifacts and test attributes
    * for the test document.
    * @param testBranchName name of the branch containing the test document.
    * @param testBranchCreationComment database creation comment to used when creating the test branch.
    */

   public void buildDocument(List<BranchSpecificationRecord> branchSpecificationRecords, MapList<Integer, ? extends ArtifactSpecificationRecord> builderRecords) {
      //@formatter:off

      /*
       * BranchSpecificationRecord checks
       */

      Assert.assertTrue
         (
            "TestDocumentBuilder::buildDocument, branchSpecificationRecords contains a BranchSpecificationRecord with the reserved identifier of 0.",
            branchSpecificationRecords
               .stream()
               .filter( ( branchSpecificationRecord ) -> branchSpecificationRecord.getIdentifier() == 0 )
               .findFirst()
               .isEmpty()
         );

      Assert.assertEquals
         (
            "TestDocumentBuilder::buildDocument, branchSpecificationRecords contains BranchSpecificationRecord entries with duplicate identifiers.",
            branchSpecificationRecords.size(),
            branchSpecificationRecords
               .stream()
               .map( BranchSpecificationRecord::getIdentifier )
               .collect( Collectors.toSet() )
               .size()
         );

      /*
       * BuilderRecord checks
       */

      Assert.assertTrue
         (
            "TestDocumentBuilder::buildDocument, builerRecords contains a BuilderRecord with the reserved identifier of 0.",
            builderRecords
               .values()
               .stream()
               .flatMap( List::stream )
               .filter( ( builderRecord ) -> ( builderRecord.getIdentifier() == 0 ) )
               .findFirst()
               .isEmpty()
         );

      Assert.assertTrue
         (
            "TestDocumentBuilder::buildDocument, builderRecords list contains BuilderRecord entries with duplicate identifiers.",
            builderRecords
               .streamEntries()
               .map
                  (
                     ( entry ) ->
                     {
                        var builderRecordList = entry.getValue();
                        return
                        builderRecordList.size() ==
                           builderRecordList
                              .stream()
                              .map( ArtifactSpecificationRecord::getIdentifier )
                              .collect( Collectors.toSet() )
                              .size();
                     }
                  )
               .allMatch( ( result ) -> result )
         );


      /*
       * Get or Create Test branches
       */

      this.branchSpecificationRecordWrapperByIdMap =
         branchSpecificationRecords
            .stream()
            .map( BranchSpecificationRecordWrapper::new )
            .collect
               (
                  org.eclipse.osee.framework.jdk.core.util.Collectors.toListMap
                     (
                        BranchSpecificationRecordWrapper::getIdentifier,
                        Function.identity()
                     )
               );

      this.branchSpecificationRecordWrapperByIdMap.listView().forEach( this::initializeBranches );

      /*
       * Load Artifacts For All Branches
       */

      var rootArtifactToken = CoreArtifactTokens.DefaultHierarchyRoot;

      for( var entry : builderRecords.entrySet() ) {

         var branchSpecificationRecordIdentifier = entry.getKey();
         var builderRecordList = entry.getValue();

         /*
          * Get Test Branch
          */

         //@formatter:off
         var branchSpecificationRecordWrapper =
            this.branchSpecificationRecordWrapperByIdMap
               .get( branchSpecificationRecordIdentifier )
               .orElseThrow
                  (
                     () -> new AssertionError
                                  (
                                     new Message()
                                            .title( "TestDocumentBuilder::buildDocument, Failed to get branch for BranchSpecificationRecordWrapper identifier." )
                                            .indentInc()
                                            .segment( "BranchSpecificationRecord Identifier", branchSpecificationRecordIdentifier )
                                            .toString()
                                  )
                  );
         //@formatter:on

         var testBranch = branchSpecificationRecordWrapper.getTestBranch();

         Assert.assertNotNull(
            "TestDocumentBuilder::buildDocument, Failed to get branch for BranchSpecificationRecordWrapper identifier.",
            branchSpecificationRecordWrapper);

         /*
          * Map of ArtifactToken objects by ArtifactInfoRecord identifiers. The branch default hierarchical root is
          * saved with the key of 0. This map is used to lookup the hierarchical parent ArtifactToken objects.
          */

         var hierarchicalParentArtifactIdMap = new HashMap<Integer, ArtifactId>();

         hierarchicalParentArtifactIdMap.put(0, rootArtifactToken);

         /*
          * Map of ArtifactInfoRecord objects by ArtifactIds. This map is used to associate the loaded Artifact objects
          * back to the ArtifactInfoRecord objects.
          */

         var builderRecordWrapperByArtifactIdMap = new HashMap<ArtifactId, ArtifactSpecificationRecordWrapper>();

         /*
          * Wrap the BuilderRecords and index them by identifier
          */

         var builderRecordWrappers =
            builderRecordList.stream().map(ArtifactSpecificationRecordWrapper::new).collect(Collectors.toList());

         this.builderRecordWrapperMap.put(branchSpecificationRecordWrapper.getIdentifier(),
            builderRecordWrappers.stream().collect(
               Collectors.toMap((builderRecordWrapper) -> builderRecordWrapper.getIdentifier(),
                  (builderRecordWrapper) -> builderRecordWrapper)));

         /*
          * Load the artifacts and set the test attribute values
          */

         ArtifactLoader.loadArtifacts(
            builderRecordWrappers.stream().map((builderRecordWrapper) -> this.getOrCreateArtifactToken(
               builderRecordWrapper, branchSpecificationRecordWrapper.getRelationEndpoint(), testBranch,
               hierarchicalParentArtifactIdMap, builderRecordWrapperByArtifactIdMap)).collect(Collectors.toList()),
            testBranch, LoadLevel.ALL, LoadType.RELOAD_CACHE, DeletionFlag.EXCLUDE_DELETED).stream().map(
               (artifact) -> builderRecordWrapperByArtifactIdMap.get(ArtifactId.valueOf(artifact.getId())).setArtifact(
                  artifact)).peek(this::getOrCreateAttribute).peek(this::setAttributeValues).forEach(
                     this::persistIfDirty);

         /*
          * Once all test artifacts are known to exist, verify and/or create the relationships
          */

         var relationshipsCreated =
            builderRecordWrappers.stream().filter(ArtifactSpecificationRecord::hasRelationshipSpecifications).flatMap(
               ArtifactSpecificationRecordWrapper::streamBuilderRelationshipRecordWrappers).flatMap(
                  (builderRelationshipRecordWrapper) -> this.stream(branchSpecificationRecordWrapper.getIdentifier(),
                     builderRelationshipRecordWrapper)).filter(RelationshipSourceTargetTypeRecord::isNotRelated).map(
                        (relationshipSourceTargetTypeRecord) -> relationshipSourceTargetTypeRecord.create(
                           branchSpecificationRecordWrapper.getRelationEndpoint())

            ).allMatch((response) -> response.getStatus() == 200);

         Assert.assertTrue("Failed to create relationships", relationshipsCreated);
      }

      /*
       * Test document building is complete
       */

      this.built = true;
      //@formatter:on

   }

   /**
    * Gets the OSEE Artifact associated with the specified builder record identifier.
    *
    * @param builderRecordId identifier of the builder record to look up.
    * @return if found, an {@link Optional} containing the OSEE Artifact associated with the specified builder record;
    * otherwise, an empty {@link Optional}.
    */

   public Optional<Artifact> getArtifact(Integer branchSpecificationRecordIdentifier, Integer builderRecordId) {

      Assert.assertTrue("TestDocumentBuilder::getArtifact, test document has not been built.", this.built);

      //@formatter:off
      var artifactOptional =
         this.builderRecordWrapperMap
            .get( branchSpecificationRecordIdentifier, builderRecordId )
            .map( ArtifactSpecificationRecordWrapper::getArtifact );
      //@formatter:on

      return artifactOptional;
   }

   /**
    * Gets the OSEE Artifact Identifier for the artifact associated with the specified builder record identifier.
    *
    * @param builderRecordId identifier of the builder record to look up.
    * @return if found, an {@link Optional} containing the identifier for the OSEE Artifact associated with the
    * specified builder record; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactId> getArtifactIdentifier(Integer branchSpecificationRecordIdentifier, Integer builderRecordId) {

      //@formatter:off
      var artifactIdentifier =
         this
            .getArtifact( branchSpecificationRecordIdentifier, builderRecordId )
            .map( Artifact::getId )
            .map( ArtifactId::valueOf );
      //@formatter:on

      return artifactIdentifier;
   }

   /**
    * Gets the {@link BranchId} for the {@link Branch} specified by the
    * <code>branchSpecificationRecordIdentifier</code>.
    *
    * @return the identifier of the specified branch.
    */

   public Optional<BranchId> getBranchIdentifier(Integer branchSpecificationRecordIdentifier) {

      Assert.assertTrue("TestDocumentBuilder::getBranchIdentifier, test document has not been built.", this.built);

      //@formatter:off
      var testBranchIdentifierOptional =
         this.branchSpecificationRecordWrapperByIdMap
            .get(branchSpecificationRecordIdentifier)
            .map( BranchSpecificationRecordWrapper::getTestBranchIdentifier );
      //@formatter:on

      return testBranchIdentifierOptional;
   }

   /**
    * Gets or creates the artifact identifier for the test artifact.
    *
    * @param builderRecord The {@link ArtifactSpecificationRecord} containing the test artifact specification.
    * @param relationEndpoint The REST API end point for obtaining related artifacts.
    * @param parentBranchId The identifier of the branch to get or create the test artifact on.
    * @param hierarchicalParentArtifactIdMap A map of the {@link ArtifactToken} (identifiers) of the hierarchical parent
    * artifacts by {@link ArtifactInfoRecord} identifiers. The {@link ArtifactToken} for the obtained or created
    * artifact will be added to the map with the identifier of this {@link ArtifactInfoRecord} as the key.
    */

   //@formatter:off
   private ArtifactToken
      getOrCreateArtifactToken
        (
           ArtifactSpecificationRecordWrapper                  builderRecordWrapper,
           RelationEndpoint                      relationEndpoint,
           BranchId                              parentBranchId,
           Map<Integer, ArtifactId>              hierarchicalParentArtifactIdMap,
           Map<ArtifactId, ArtifactSpecificationRecordWrapper> builderRecordWrapperByArtifactIdMap
        )
   {
      var artifactToken =
         TestUtil.getOrCreateChildArtifactTokenByName
            (
               relationEndpoint,
               parentBranchId,
               hierarchicalParentArtifactIdMap.get( builderRecordWrapper.getHierarchicalParentIdentifier() ),
               builderRecordWrapper.getArtifactId(),
               builderRecordWrapper.getArtifactTypeToken(),
               builderRecordWrapper.getName()
            );

      hierarchicalParentArtifactIdMap.put( builderRecordWrapper.getIdentifier(), artifactToken );
      builderRecordWrapperByArtifactIdMap.put( artifactToken, builderRecordWrapper );

      builderRecordWrapper.setArtifactToken(artifactToken);

      return artifactToken;
   }
   //@formatter:on

   /**
    * Gets or creates the test attribute for the test artifact. The test artifact must be obtained before calling this
    * method.
    *
    * @param builderRecord The {@link ArtifactSpecificationRecord} containing the test artifact specification.
    */

   private void getOrCreateAttribute(ArtifactSpecificationRecordWrapper builderRecordWrapper) {

      //@formatter:off
      builderRecordWrapper.getAttributeSpecifications()
         .forEach
            (
               ( attributeSpecification ) ->

                  builderRecordWrapper.setAttributeValueList
                     (
                       attributeSpecification.getAttributeType(),
                       TestUtil.getOrCreateAttributes
                          (
                            builderRecordWrapper.getArtifact(),
                            attributeSpecification.getAttributeType(),
                            attributeSpecification.getAttributeValues().size()
                          )
                     )
            );
      //@formatter:on
   }

   /**
    * Gets the parent branch from the referenced parent branch {@link BranchSpecificationRecordWrapper} and gets or
    * creates the test branch. The parent branch, test branch, and {@link RelationEndpoint} for the test branch are
    * saved in the {@link BranchSpecificationRecordWrapper}.
    *
    * @param branchSpecificationRecordWrapper the {@link BranchSpecificationRecordWrapper} containing the branch
    * specification.
    */

   private void initializeBranches(BranchSpecificationRecordWrapper branchSpecificationRecordWrapper) {

      //@formatter:off
      var parentTestBranchSpecificationRecordIdentifier = branchSpecificationRecordWrapper.getHierarchicalParentIdentifier();

      var parentTestBranch =
         ( parentTestBranchSpecificationRecordIdentifier == 0 )
            ? this.branchEndpoint.getBranchById( CoreBranches.SYSTEM_ROOT )
            : TestUtil
                 .getBranchByName
                    (
                       this.branchEndpoint,
                       this.branchSpecificationRecordWrapperByIdMap
                          .get( parentTestBranchSpecificationRecordIdentifier )
                          .map( BranchSpecificationRecordWrapper::getTestBranchName )
                          .orElseThrow
                             (
                                () -> new AssertionError
                                             (
                                                new Message()
                                                   .title( "TestDocumentBuilder::initializeBranches, failed to get BranchSpecificationRecordWrapper by identifier for parent branch." )
                                                   .indentInc()
                                                   .segment( "Test Branch BranchSpecificationRecord Identifier",         branchSpecificationRecordWrapper.getIdentifier() )
                                                   .segment( "Test Parent Branch BranchSpecification Record Identifier", parentTestBranchSpecificationRecordIdentifier    )
                                                   .toString()
                                             )
                             )
                    )
                 .orElse( Branch.SENTINEL );
      //@formatter:on

      //@formatter:off
      var testBranch =
         Objects.nonNull( branchSpecificationRecordWrapper.getTestBranchName() )
            ? TestUtil
                 .getOrCreateTestBranch
                    (
                       branchEndpoint,
                       parentTestBranch,
                       branchSpecificationRecordWrapper.getTestBranchName(),
                       branchSpecificationRecordWrapper.getTestBranchCreationComment()
                    )
            : Branch.SENTINEL;
      //@formatter:on

      var relationEndpoint = this.oseeClient.getRelationEndpoint(testBranch);

      branchSpecificationRecordWrapper.setBranches(parentTestBranch, testBranch, relationEndpoint);

   }

   /**
    * Save the artifact back to the database if it has been modified.
    *
    * @param builderRecord The {@link ArtifactSpecificationRecord} containing the test artifact specification.
    */

   private void persistIfDirty(ArtifactSpecificationRecordWrapper builderRecordWrapper) {
      var artifact = builderRecordWrapper.getArtifact();
      if (artifact.isDirty()) {
         artifact.persist("Three Blind Mice");
      }
   }

   /**
    * If the test artifact's attributes do not have the expected values, set the test attribute values.
    *
    * @param builderRecord The {@link ArtifactSpecificationRecord} containing the test artifact specification.
    */

   private void setAttributeValues(ArtifactSpecificationRecordWrapper builderRecordWrapper) {

      if (!this.setValues) {
         return;
      }

      //@formatter:off
      builderRecordWrapper.getAttributeSpecifications()
         .forEach
            (
               ( attributeSpecification ) ->
               {
                  TestUtil.setAttributeValues
                     (
                       builderRecordWrapper.getArtifact(),
                       attributeSpecification.getAttributeType(),
                       new LinkedList<Object>( attributeSpecification.getAttributeValues() ),
                       attributeSpecification.getAttributeSetter()
                     );

                  builderRecordWrapper.setAttributeValueList
                     (
                       attributeSpecification.getAttributeType(),
                       TestUtil.getAttributes
                          (
                            builderRecordWrapper.getArtifact(),
                            attributeSpecification.getAttributeType()
                          )
                          .orElseThrow()
                     );
               }
            );
      //@formatter:on
   }

   /**
    * Builds a {@link Stream} of {@link RelationshipSourceTargetTypeRecord}s. The source artifact is from the
    * constructor. The relationship type is from the wrapped {@link RelationshipSpecificationRecord}. The target
    * artifact is found from a map in the containing {@link TestDocumentBuilder} class using the
    * {@link ArtifactSpecificationRecord} identifier for the relationship target.
    *
    * @return a {@link Stream} of {@link RelationshipSourceTargetTypeRecord}s for
    */

   Stream<RelationshipSourceTargetTypeRecord> stream(Integer branchSpecificationRecordIdentifier, RelationshipSpecificationRecordWrapper relationshipSpecificationRecordWrapper) {
      //@formatter:off
      return
         relationshipSpecificationRecordWrapper
            .getRelationshipTargetArtifactSpecificationRecordIdentifiers()
            .stream()
            .map
               (
                  ( builderRecordIdentifier ) -> TestDocumentBuilder.this
                                                    .builderRecordWrapperMap
                                                    .get( branchSpecificationRecordIdentifier, builderRecordIdentifier )
               )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .map( ArtifactSpecificationRecordWrapper::getArtifact )
            .map( (targetArtifact) -> new RelationshipSourceTargetTypeRecord
                                             (
                                                relationshipSpecificationRecordWrapper.getSourceArtifact(),
                                                targetArtifact,
                                                relationshipSpecificationRecordWrapper.getRelationTypeToken()
                                              )
                );
      //@formatter:on
   }

}

/* EOF */
