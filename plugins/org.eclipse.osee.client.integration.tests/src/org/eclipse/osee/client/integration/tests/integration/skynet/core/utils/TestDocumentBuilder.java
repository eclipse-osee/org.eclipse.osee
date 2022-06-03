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

package org.eclipse.osee.client.integration.tests.integration.skynet.core.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.Assert;

/**
 * Class for creating or verifying a tree of artifacts on a branch in a test database.
 *
 * @author Loren K. Ashley
 */

public class TestDocumentBuilder {

   /**
    * Internal implementation of the {@link BuidlerRecord} interface that wraps an unknown implementation of the
    * {@link BuilderRecord} interface with some additional members used in the test document building process.
    */

   private class BuilderRecordWrapper implements BuilderRecord {

      /**
       * The wrapped {@link BuilderRecord}.
       */

      private final BuilderRecord builderRecord;

      /**
       * Saves the test {@link Artifact} read from or created for the database.
       */

      private Artifact artifact;

      /**
       * The {@link ArtifactToken} ({@link ArtifactId}) of the test artifact.
       */

      private ArtifactToken artifactToken;

      /**
       * The test {@link Attribute} loaded from the database. Multi-value enumerations return an {@link Attribute} for
       * each value of the enumeration that is "selected". Regular attributes will only have one element on the list.
       */

      private List<Attribute<?>> attributeList;

      /**
       * Wraps a {@link BuilderRecord} with additional members for the test document building process.
       *
       * @param builderRecord {@link BuilderRecord} to be wrapped.
       */

      BuilderRecordWrapper(BuilderRecord builderRecord) {
         this.builderRecord = builderRecord;
         this.artifact = null;
         this.artifactToken = null;
         this.attributeList = null;
      }

      /**
       * Gets the test {@link Artifact} created for or read from the database.
       *
       * @return the test {@link Artifact}.
       */

      Artifact getArtifact() {
         //@formatter:off
         Assert.assertNotNull
            (
              "TestDocumentBuilder.BuilderRecordWrapper::getArtifact, member artifact has not been set.",
              this.artifact
            );
         //@formatter:on

         return this.artifact;
      }

      /**
       * Gets the identifier for the test artifact.
       *
       * @return the test artifact identifier.
       */

      @SuppressWarnings("unused")
      ArtifactToken getArtifactToken() {
         //@formatter:off
         Assert.assertNotNull
            (
              "TestDocumentBuilder.BuilderRecordWrapper::getArtifactToken, member artifactToken has not been set.",
              this.artifactToken
            );
         //@formatter:on

         return this.artifactToken;
      }

      /**
       * Gets the list of test attribute values read from the database.
       *
       * @return {@link List} of {@link Attribute} implementations read from the database for the test attribute type.
       */

      @SuppressWarnings("unused")
      List<Attribute<?>> getAttributeList() {
         return this.attributeList;
      }

      /**
       * Saves the {@link Artifact} created for or read from the database.
       *
       * @param artifact the {@link Artifact} to save.
       * @return this {@link BuilderRecordWrapper}.
       */

      BuilderRecordWrapper setArtifact(Artifact artifact) {
         //@formatter:off
         Assert.assertNull
            (
               "TestDocumentBuilder.BuilderRecordWrapper::getArtifact, member artifact is already set.",
               this.artifact
            );
         //@formatter:on

         this.artifact = artifact;
         return this;
      }

      /**
       * Saves the identifier of the test artifact.
       *
       * @param artifactToken {@link ArtifactToken} to save.
       */

      void setArtifactToken(ArtifactToken artifactToken) {
         //@formatter:off
         Assert.assertNull
            (
               "TestDocumentBuilder.BuilderRecordWrapper::setArtifactToken, member artifactToken is already set.",
               this.artifactToken
            );
         //@formatter:on

         this.artifactToken = artifactToken;
      }

      /**
       * Saves the test attributes read back from the database.
       *
       * @param attributeList the {@link List} of {@Attribute}} values for the test attribute type.
       */

      void setAttributeList(List<Attribute<?>> attributeList) {
         this.attributeList = attributeList;
      }

      /*
       * BuilderRecord methods
       */

      /**
       * {@inheritDoc}
       */

      @Override
      public ArtifactTypeToken getArtifactTypeToken() {
         return this.builderRecord.getArtifactTypeToken();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public BiConsumer<Attribute<?>, Object> getAttributeSetter() {
         return this.builderRecord.getAttributeSetter();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Integer getHierarchicalParentIdentifier() {
         return this.builderRecord.getHierarchicalParentIdentifier();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Integer getIdentifier() {
         return this.builderRecord.getIdentifier();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public String getName() {
         return this.builderRecord.getName();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public AttributeTypeGeneric<?> getTestAttributeType() {
         return this.builderRecord.getTestAttributeType();
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public List<Object> getTestAttributeValues() {
         return this.builderRecord.getTestAttributeValues();
      }

   }

   /**
    * Flag indicates if test document has been built.
    */

   private boolean built;

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private BranchId rootBranchId;

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
      this.rootArtifactId = null;
      this.rootBranchId = null;
      this.setValues = setValues;
   }

   /**
    * Gets or creates the artifact identifier for the test artifact.
    *
    * @param builderRecord The {@link BuilderRecord} containing the test artifact specification.
    * @param relationEndpoint The REST API end point for obtaining related artifacts.
    * @param artifactEndpoint The REST API end point for creating artifacts.
    * @param parentBranchId The identifier of the branch to get or create the test artifact on.
    * @param hierarchicalParentArtifactIdMap A map of the {@link ArtifactToken} (identifiers) of the hierarchical parent
    * artifacts by {@link ArtifactInfoRecord} identifiers. The {@link ArtifactToken} for the obtained or created
    * artifact will be added to the map with the identifier of this {@link ArtifactInfoRecord} as the key.
    */

   //@formatter:off
   private ArtifactToken
      getOrCreateArtifactToken
        (
           BuilderRecordWrapper                  builderRecordWrapper,
           RelationEndpoint                      relationEndpoint,
           ArtifactEndpoint                      artifactEndpoint,
           BranchId                              parentBranchId,
           Map<Integer, ArtifactId>              hierarchicalParentArtifactIdMap,
           Map<ArtifactId, BuilderRecordWrapper> builderRecordWrapperByArtifactIdMap
        )
   {
      var artifactToken =
         TestUtil.getOrCreateChildArtifactTokenByName
            (
               relationEndpoint,
               artifactEndpoint,
               parentBranchId,
               hierarchicalParentArtifactIdMap.get( builderRecordWrapper.getHierarchicalParentIdentifier() ),
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
    * @param builderRecord The {@link BuilderRecord} containing the test artifact specification.
    */

   private void getOrCreateAttribute(BuilderRecordWrapper builderRecordWrapper) {

      //@formatter:off
      builderRecordWrapper.setAttributeList
         (
           TestUtil.getOrCreateAttributes
              (
                builderRecordWrapper.getArtifact(),
                builderRecordWrapper.getTestAttributeType(),
                builderRecordWrapper.getTestAttributeValues().size()
              )
         );
      //@formatter:on
   }

   /**
    * If the test artifact's attributes do not have the expected values, set the test attribute values.
    *
    * @param builderRecord The {@link BuilderRecord} containing the test artifact specification.
    */

   private void setAttributeValues(BuilderRecordWrapper builderRecordWrapper) {

      //@formatter:off
      if( this.setValues )
      {
         TestUtil.setAttributeValues
            (
               builderRecordWrapper.getArtifact(),
               builderRecordWrapper.getTestAttributeType(),
               new LinkedList<Object>( builderRecordWrapper.getTestAttributeValues() ),
               builderRecordWrapper.getAttributeSetter()
            );

         builderRecordWrapper.setAttributeList
            (
              TestUtil.getAttributes
                         (
                           builderRecordWrapper.getArtifact(),
                           builderRecordWrapper.getTestAttributeType()
                         )
                      .orElseThrow()
            );
      }
      //@formatter:on
   }

   /**
    * Save the artifact back to the database if it has been modified.
    *
    * @param builderRecord The {@link BuilderRecord} containing the test artifact specification.
    */

   private void persistIfDirty(BuilderRecordWrapper builderRecordWrapper) {
      var artifact = builderRecordWrapper.getArtifact();
      if (artifact.isDirty()) {
         artifact.persist("Three Blind Mice");
      }
   }

   /**
    * Creates or verifies the test document specified by the {@link List} of {@link BuildRecord}s.
    *
    * @param builderRecords list of {@link BuilderRecord}s specifying the artifacts and test attributes for the test
    * document.
    * @param testBranchName name of the branch containing the test document.
    * @param testBranchCreationComment database creation comment to used when creating the test branch.
    */

   public void buildDocument(List<BuilderRecord> builderRecords, String testBranchName, String testBranchCreationComment) {
      //@formatter:off

      /*
       * BuilderRecord identifier checks
       */

      Assert.assertTrue
         (
            "TestDocumentBuilder::buildDocument, builerRecords contains a BuilderRecord with the reserved identifier of 0.",
            builderRecords.stream().filter( ( builderRecord ) -> ( builderRecord.getIdentifier() == 0 ) ).findFirst().isEmpty()
         );

      Assert.assertEquals
         (
            "TestDocumentBuilder::buildDocument, builderRecords list contains BuilderRecord entries with duplicate identifiers.",
            builderRecords.size(),
            builderRecords.stream().map( BuilderRecord::getIdentifier ).collect( Collectors.toSet() ).size()
         );


      var builderRecordWrappers = builderRecords.stream().map( BuilderRecordWrapper::new ).collect( Collectors.toList() );

      /*
       * Get OSEE Client
       */

      var oseeClient = OsgiUtil.getService( DemoChoice.class, OseeClient.class );

      Assert.assertNotNull( "TestDocumentBuilder::buildDocument, Failed to get OSEE Client.", oseeClient );

      /*
       * Get Branch end point for test data setup
       */

      var branchEndpoint = oseeClient.getBranchEndpoint();

      Assert.assertNotNull( "TestDocumentBuilder::buildDocument, Failed to get BranchEndpoint.", branchEndpoint );

      /*
       * Get or Create Test branch
       */

      var testBranch = TestUtil
                          .getBranchByName( branchEndpoint, testBranchName )
                          .orElseGet( () -> TestUtil.createTestBranch( branchEndpoint, testBranchName, testBranchCreationComment ) );

      Assert.assertNotNull( "TestDocumentBuilder::buildDocument, Failed to get or create test branch ( " + testBranchName + " )", testBranch );

      var testBranchId = BranchId.valueOf( testBranch.getId() );

      /*
       * Get ArtifactEndpoint and RelationEndpoint for the branch
       */

      var artifactEndpoint = oseeClient.getArtifactEndpoint( testBranchId );

      Assert.assertNotNull( "TestDocumentBuilder::buildDocument, Failed to get ArtifactEndpoint.", artifactEndpoint );

      var relationEndpoint = oseeClient.getRelationEndpoint( testBranchId );

      Assert.assertNotNull( "TestDocumentBuilder::buildDocument, Failed to get RelationEndpoint.", relationEndpoint );

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

      var builderRecordWrapperByArtifactIdMap = new HashMap<ArtifactId, BuilderRecordWrapper>();

      /*
       * Load the artifacts and set the test attribute values
       */

      ArtifactLoader.loadArtifacts
         (
           builderRecordWrappers.stream()
              .map
                 (
                    ( builderRecordWrapper ) -> this.getOrCreateArtifactToken
                                                        (
                                                          builderRecordWrapper,
                                                          relationEndpoint,
                                                          artifactEndpoint,
                                                          testBranchId,
                                                          hierarchicalParentArtifactIdMap,
                                                          builderRecordWrapperByArtifactIdMap
                                                        )
                 )
              .collect( Collectors.toList() ),
           testBranchId,
           LoadLevel.ALL,
           LoadType.RELOAD_CACHE,
           DeletionFlag.EXCLUDE_DELETED
         ).stream()
             .map( ( artifact ) -> builderRecordWrapperByArtifactIdMap.get( ArtifactId.valueOf( artifact.getId() ) ).setArtifact( artifact ) )
             .peek( this::getOrCreateAttribute )
             .peek( this::setAttributeValues )
             .forEach( this::persistIfDirty );

      /*
       * Save identifiers of test document root
       */

      this.rootBranchId = testBranchId;
      this.rootArtifactId = ArtifactId.valueOf( builderRecordWrappers.get( 0 ).getArtifact().getId() );
      this.built = true;

      //@formatter:on
   }

   /**
    * Gets the {@link ArtifactId} of the test document's root {@link Artifact}.
    *
    * @return the identifier of the root artifact.
    */

   public ArtifactId getRootArtifactId() {
      Assert.assertTrue("TestDocumentBuilder::getRootArtifactId, test document has not been built.", this.built);
      Assert.assertNotNull("TestDocumentBuilder::getRootArtifactId, test document has not been built.",
         this.rootArtifactId);

      return this.rootArtifactId;
   }

   /**
    * Gets the {@link BranchId} of the {@link Branch} containing the test document.
    *
    * @return the identifier of the branch containing the test document.
    */

   public BranchId getRootBranchId() {
      Assert.assertTrue("TestDocumentBuilder::getRootArtifactId, test document has not been built.", this.built);
      Assert.assertNotNull("TestDocumentBuilder::getRootArtifactId, test document has not been built.",
         this.rootBranchId);

      return this.rootBranchId;
   }

}

/* EOF */
