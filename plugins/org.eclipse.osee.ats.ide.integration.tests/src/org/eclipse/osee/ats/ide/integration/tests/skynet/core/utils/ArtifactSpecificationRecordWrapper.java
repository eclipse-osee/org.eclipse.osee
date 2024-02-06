/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.junit.Assert;

/**
 * Internal implementation of the {@link BuidlerRecord} interface that wraps an unknown implementation of the
 * {@link ArtifactSpecificationRecord} interface with some additional members used in the test document building
 * process.
 */
public class ArtifactSpecificationRecordWrapper implements ArtifactSpecificationRecord {

   /**
    * The wrapped {@link ArtifactSpecificationRecord}.
    */

   private final @NonNull ArtifactSpecificationRecord builderRecord;

   /**
    * Saves the test {@link Artifact} read from or created for the database.
    */

   private Artifact artifact;

   /**
    * The {@link ArtifactToken} ({@link ArtifactId}) of the test artifact.
    */

   private ArtifactToken artifactToken;

   /**
    * A {@link Map} of {@link Attribute} value {@link List}s by the associated attribute {@link AttributeTypeGeneric}
    * classes.
    */

   private final Map<AttributeTypeGeneric<?>, List<Attribute<?>>> attributeValueListByAttributeTypeMap;

   /**
    * Wraps a {@link ArtifactSpecificationRecord} with additional members for the test document building process.
    *
    * @param builderRecord {@link ArtifactSpecificationRecord} to be wrapped.
    */

   ArtifactSpecificationRecordWrapper(@NonNull ArtifactSpecificationRecord builderRecord) {

      //@formatter:off
      this.builderRecord =
         Conditions.requireNonNull
            (
               builderRecord,
               "ArtifactSpecificationRecordWrapper",
               "new",
               "builderRecord"
            );
      //@formatter:on

      this.artifact = null;
      this.artifactToken = null;
      this.attributeValueListByAttributeTypeMap = new HashMap<>();
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
   Optional<List<Attribute<?>>> getAttributeValueList(AttributeTypeGeneric<?> attributeType) {

      //@formatter:off
      return
         Optional.ofNullable
            (
              this.attributeValueListByAttributeTypeMap.get
                 (
                    Objects.requireNonNull( attributeType )
                 )
            );
      //@formatter:on
   }

   /**
    * Saves the {@link Artifact} created for or read from the database.
    *
    * @param artifact the {@link Artifact} to save.
    * @return this {@link ArtifactSpecificationRecordWrapper}.
    */

   ArtifactSpecificationRecordWrapper setArtifact(Artifact artifact) {
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

   void setAttributeValueList(AttributeTypeGeneric<?> attributeType, List<Attribute<?>> attributeList) {
      //@formatter:off
      this.attributeValueListByAttributeTypeMap.put
         (
            Objects.requireNonNull( attributeType ),
            Objects.requireNonNull( attributeList )
         );
      //@formatter:on
   }

   /**
    * Get a {@link Stream} of the relationships for test artifact.
    *
    * @return a {@link Stream} of {@link BuilderRelationshipRecords} for the test artifact.
    */

   Stream<RelationshipSpecificationRecordWrapper> streamBuilderRelationshipRecordWrappers() {
      if (this.getRelationshipSpecifications() != null) {
         return this.getRelationshipSpecifications().stream().map(
            (builderRelationshipRecord) -> new RelationshipSpecificationRecordWrapper(this.getArtifact(),
               builderRelationshipRecord));
      }
      return null;
   }

   /*
    * BuilderRecord methods
    */

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactId getArtifactId() {
      return this.builderRecord.getArtifactId();
   }

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
   public List<AttributeSpecificationRecord> getAttributeSpecifications() {
      return this.builderRecord.getAttributeSpecifications();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<RelationshipSpecificationRecord> getRelationshipSpecifications() {
      return this.builderRecord.getRelationshipSpecifications();
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

}

/* EOF */
