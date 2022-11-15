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

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Basic implementation of the {@link BuilderRecord} interface for defining test {@link Artifact}s.
 *
 * @author Loren K. Ashley
 */

public class BasicArtifactInfoRecord implements BuilderRecord {

   /**
    * The {@link ArtifactToken} for the test artifact, maybe SENTINEL.
    */

   private final ArtifactToken artifactToken;

   /**
    * The {@link ArtifactTypeToken} of the test artifact. Will be {@link ArtifactTypeToken#SENTINEL} when
    * {@link #artifactToken} is not {@link ArtifactToken#SENTINEL}.
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
    * A name for the test artifact. Will be <code>null</code> when {@link #artifactToken} is not
    * {@link ArtifactToken#SENTINEL}.
    */

   private final String name;

   /**
    * Constructs a new {@link ArtifactInfoRecord} with the specified parameters.
    *
    * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value 0
    * is reserved.
    * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
    * hierarchical parent of the test artifact. Use 0 for top level artifacts.
    * @param name A name for the test artifact.
    * @param typeToken The {@link ArtifactTypeToken} of the test artifact.
    * @param testAttributeType The {@link AttributeTypeGeneric} of the test attribute.
    * @param testAttributeValue The test attribute value.
    * @param attributeSetter A {@link BiConsumer} used to assign the attribute value to the test attribute. The first
    * parameter is the attribute as an {@link Attribute} and the second parameter is the value as an {@link Object}.
    * @param builderRelationshipsRecords a {@link List} of the {@link BuilderRelationshipRecord}s defining relationships
    * between this test artifact and other test artifacts. This parameter should be an empty list when the test artifact
    * does not have relationships instead of <code>null</code>.
    */

   public //@formatter:off
      BasicArtifactInfoRecord
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

         this.artifactToken = ArtifactToken.SENTINEL;
      }
      //@formatter:on

   /**
    * Constructs a new {@link ArtifactInfoRecord} with the specified parameters.
    *
    * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value 0
    * is reserved.
    * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
    * hierarchical parent of the test artifact. Use 0 for top level artifacts.
    * @param artifactToken The {@link ArtifactToken} that defines the artifact. The artifact will be created with the
    * identifier specified in the {@link ArtifactToken}.
    * @param testAttributeType The {@link AttributeTypeGeneric} of the test attribute.
    * @param testAttributeValue The test attribute value.
    * @param attributeSetter A {@link BiConsumer} used to assign the attribute value to the test attribute. The first
    * parameter is the attribute as an {@link Attribute} and the second parameter is the value as an {@link Object}.
    * @param builderRelationshipsRecords a {@link List} of the {@link BuilderRelationshipRecord}s defining relationships
    * between this test artifact and other test artifacts. This parameter should be an empty list when the test artifact
    * does not have relationships instead of <code>null</code>.
    */

   public //@formatter:off
      BasicArtifactInfoRecord
         (
            Integer                            identifier,
            Integer                            hierarchicalParentIdentifier,
            ArtifactToken                      artifactToken,
            List<AttributeSpecificationRecord> attributeSpecifications,
            List<BuilderRelationshipRecord>    builderRelationshipRecords
         ) {
         this.artifactToken = Objects.requireNonNull( artifactToken );
         this.identifier = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.attributeSpecifications = Objects.requireNonNull(attributeSpecifications);
         this.builderRelationshipRecords = Objects.requireNonNull(builderRelationshipRecords);

         this.name = null;
         this.artifactTypeToken = ArtifactTypeToken.SENTINEL;
      }
      //@formatter:on

   /*
    * BuildRecord Methods
    */

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactId getArtifactId() {
      //@formatter:off
      return
         ArtifactToken.SENTINEL.equals( this.artifactToken )
            ? ArtifactId.SENTINEL
            : this.artifactToken;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactTypeToken getArtifactTypeToken() {
      //@formatter:off
      return
         ArtifactToken.SENTINEL.equals( this.artifactToken )
            ? this.artifactTypeToken
            : this.artifactToken.getArtifactType();
      //@formatter:on
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

/* EOF */