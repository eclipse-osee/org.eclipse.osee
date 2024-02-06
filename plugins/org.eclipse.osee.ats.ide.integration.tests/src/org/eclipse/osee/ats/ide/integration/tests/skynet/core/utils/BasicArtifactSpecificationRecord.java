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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Basic implementation of the {@link ArtifactSpecificationRecord} interface for defining test {@link Artifact}s.
 *
 * @author Loren K. Ashley
 */

public class BasicArtifactSpecificationRecord implements ArtifactSpecificationRecord {

   /**
    * The {@link ArtifactId} the test artifact is to be created with or {@link ArtifactId#SENTINEL}.
    */

   private final @NonNull ArtifactId artifactId;

   /**
    * The {@link ArtifactTypeToken} for the test artifact.
    */

   private final @NonNull ArtifactTypeToken artifactTypeToken;

   /**
    * A list of the attributes to be defined for the artifact.
    */

   private final @NonNull List<@NonNull AttributeSpecificationRecord> attributeSpecifications;

   /**
    * The {@link ArtifactInfoRecord#identifier} for the test artifact that is the hierarchical parent of the test
    * artifact defined by this record. Use 0 for top level artifacts.
    */

   private final @NonNull Integer hierarchicalParentIdentifier;

   /**
    * A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced, a bad test setup will result
    * if not unique. The identifier 0 is reserved for the Default Hierarchy Root artifact.
    */

   private final @NonNull Integer identifier;

   /**
    * The name for the test artifact.
    */

   private final @NonNull String name;

   /**
    * A list of the relationships for the test artifact.
    */

   private final @NonNull List<@NonNull RelationshipSpecificationRecord> relationshipSpecificationRecords;

   /**
    * Constructs a new {@link ArtifactInfoRecord} with the specified parameters.
    *
    * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value 0
    * is reserved.
    * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
    * hierarchical parent of the test artifact. Use 0 for top level artifacts.
    * @param artifactToken The {@link ArtifactToken} that defines the artifact. The artifact will be created with the
    * identifier, name, and type specified in the {@link ArtifactToken}.
    * @param attributeSpecification a {@link List} of the {@link AttributeSpecificationRecord}s defining the attributes
    * and attribute values for the artifact. This parameter should be an empty list when the test artifact does not have
    * any attributes instead of <code>null</code>.
    * @param relationshipsRecords a {@link List} of the {@link RelationshipSpecificationRecord}s defining relationships
    * between this test artifact and other test artifacts. This parameter should be an empty list when the test artifact
    * does not have relationships instead of <code>null</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NullPointerException when the name, identifier, or artifact type in the <code>artifactToken</code> is
    * <code>null</code>.
    * @throws IllegalArgumentException when <code>identifier</code> is less than or equal to zero.
    * @throws IllegalArgumentException when <code>hierarchicalParentIdentifier</code> is less than zero.
    * @throws IllegalArgumentException when the name in <code>artifactToken</code> is blank.
    */

   //@formatter:off
   public
      BasicArtifactSpecificationRecord
         (
            @NonNull Integer                                        identifier,
            @NonNull Integer                                        hierarchicalParentIdentifier,
            @NonNull ArtifactToken                                  artifactToken,
            @NonNull List<@NonNull AttributeSpecificationRecord>    attributeSpecifications,
            @NonNull List<@NonNull RelationshipSpecificationRecord> relationshipSpecificationRecords
         ) {

         this
            (
               identifier,
               hierarchicalParentIdentifier,
               null,
               null,
               artifactToken,
               attributeSpecifications,
               relationshipSpecificationRecords
            );

      }
      //@formatter:on

   /**
    * Private constructor used by public constructor to initialize member values.
    *
    * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value 0
    * is reserved.
    * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
    * hierarchical parent of the test artifact. Use 0 for top level artifacts.
    * @param name A name for the test artifact. Ignored when <code>null</code> when <code>artifactToken</code> is
    * non-<code>null</code>.
    * @param typeToken The {@link ArtifactTypeToken} of the test artifact. Ignored when <code>null</code> when
    * <code>artifactToken</code> is non-<code>null</code>.
    * @param artifactToken The {@link ArtifactToken} that defines the artifact. The artifact will be created with the
    * identifier, name, and type specified in the {@link ArtifactToken}. When non-<code>null</code> the parameters
    * <code>name</code> and <code>typeToken</code> are ignored.
    * @param attributeSpecifications a {@link List} of the {@link AttributeSpecificationRecord}s defining the attributes
    * and attribute values for the artifact. This parameter should be an empty list when the test artifact does not have
    * any attributes instead of <code>null</code>.
    * @param relationshipSpecifications a {@link List} of the {@link RelationshipSpecificationRecord}s defining
    * relationships between this test artifact and other test artifacts. This parameter should be an empty list when the
    * test artifact does not have relationships instead of <code>null</code>.
    * @throws NullPointerException when any of the parameters
    * <ul>
    * <li><code>identifier</code>,</li>
    * <li><code>hierarchicalParentIdentifier</code>,</li>
    * <li><code>name</code>,</li>
    * <li><code>attributeSpecifications</code>, or</li>
    * <li><code>relationshipSpecifications</code> are <code>null</code>.</li>
    * </ul>
    * @throws NullPointerException when <code>artifactToken</code> is <code>null</code> and either <code>name</code> or
    * <code>typeToken</code> are <code>null</code>.
    * @throws NullPointerException when <code>artifactToken</code> is non-<code>null</code> and the name, identifier, or
    * artifact type in the {@link ArtifactTypeToken} is <code>null</code>.
    * @throws IllegalArgumentException when <code>identifier</code> is less than or equal to zero.
    * @throws IllegalArgumentException when <code>hierarchicalParentIdentifier</code> is less than zero.
    * @throws IllegalArgumentException when <code>name</code> or <code>artifactToken.getName()</code> is blank.
    */

   //@formatter:off
   private
      BasicArtifactSpecificationRecord
         (
            @NonNull Integer                                        identifier,
            @NonNull Integer                                        hierarchicalParentIdentifier,
                     String                                         name,
                     ArtifactTypeToken                              typeToken,
                     ArtifactToken                                  artifactToken,
            @NonNull List<@NonNull AttributeSpecificationRecord>    attributeSpecifications,
            @NonNull List<@NonNull RelationshipSpecificationRecord> relationshipSpecifications
         ) {
      //@formatter:on

      //@formatter:off
      this.identifier =
         Conditions.require
            (
               identifier,
               ValueType.PARAMETER,
               "BasicArtifactSpecificationRecord",
               "new",
               "identifier",
               "cannot be null",
               Objects::isNull,
               NullPointerException::new,
               "identifier less than or equal to zero",
               ( p ) -> ( p <= 0 ),
               IllegalArgumentException::new
            );
      //@formatter:on

      //@formatter:off
      this.hierarchicalParentIdentifier =
         Conditions.require
            (
               hierarchicalParentIdentifier,
               ValueType.PARAMETER,
               "BasicArtifactSpecificationRecord",
               "new",
               "hierarchicalParentIdentifier",
               "cannot be null",
               Objects::isNull,
               NullPointerException::new,
               "less than zero",
               ( p ) -> ( p < 0 ),
               IllegalArgumentException::new
            );
      //@formatter:on

      if (Objects.nonNull(artifactToken)) {

         //@formatter:off
         this.artifactId =
            ArtifactId.valueOf
               (
                  Conditions.requireNonNull
                     (
                        artifactToken.getId(),
                        "BasicArtifactSpecificationRecord",
                        "new",
                        "artifactToken.getId()"
                     )
               );
         //@formatter:on

         //@formatter:off
         this.name =
            Conditions.require
               (
                  artifactToken.getName(),
                  ValueType.PARAMETER,
                  "BasicArtifactSpecificationRecord",
                  "new",
                  "artifactToken.getName()",
                  "is valid and not blank",
                  Strings::isInvalidOrBlank,
                  IllegalArgumentException::new
               );
         //@formatter:on

         //@formatter:off
         this.artifactTypeToken =
            Conditions.requireNonNull
            (
               artifactToken.getArtifactType(),
               "BasicArtifactSpecificationRecord",
               "new",
               "artifactToken.getArtifactType()"
            );
         //@formatter:on

      } else {

         this.artifactId = ArtifactId.SENTINEL;

         //@formatter:off
         this.name =
            Conditions.require
               (
                  name,
                  ValueType.PARAMETER,
                  "BasicArtifactSpecificationRecord",
                  "new",
                  "name",
                  "is valid and not blank",
                  Strings::isInvalidOrBlank,
                  IllegalArgumentException::new
               );
         //@formatter:on

         //@formatter:off
         this.artifactTypeToken =
            Conditions.requireNonNull
               (
                  typeToken,
                  "BasicArtifactSpecificationRecord",
                  "new",
                  "typeToken"
               );
         //@formatter:on
      }

      //@formatter:off
      this.attributeSpecifications =
         Conditions.require
            (
               attributeSpecifications,
               ValueType.PARAMETER,
               "BasicArtifactSpecificationRecord",
               "new",
               "attributeSpecifications",
               "cannot be null",
               Objects::isNull,
               NullPointerException::new,
               "does not contain null elements",
               Conditions::collectionContainsNull,
               NullPointerException::new
            );
      //@formatter:on

      //@formatter:off
      this.relationshipSpecificationRecords =
         Conditions.require
            (
               relationshipSpecifications,
               ValueType.PARAMETER,
               "BasicArtifactSpecificationRecord",
               "new",
               "relationshipSpecifications",
               "cannot be null",
               Objects::isNull,
               NullPointerException::new,
               "does not contain null elements",
               Conditions::collectionContainsNull,
               NullPointerException::new
            );
      //@formatter:on
   }

   /**
    * Constructs a new {@link ArtifactInfoRecord} with the specified parameters.
    *
    * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value 0
    * is reserved.
    * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
    * hierarchical parent of the test artifact. Use 0 for top level artifacts.
    * @param name A name for the test artifact.
    * @param typeToken The {@link ArtifactTypeToken} of the test artifact.
    * @param attributeSpecification a {@link List} of the {@link AttributeSpecificationRecord}s defining the attributes
    * and attribute values for the artifact. This parameter should be an empty list when the test artifact does not have
    * any attributes instead of <code>null</code>.
    * @param relationshipsRecords a {@link List} of the {@link RelationshipSpecificationRecord}s defining relationships
    * between this test artifact and other test artifacts. This parameter should be an empty list when the test artifact
    * does not have relationships instead of <code>null</code>.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws IllegalArgumentException when <code>identifier</code> is less than or equal to zero.
    * @throws IllegalArgumentException when <code>hierarchicalParentIdentifier</code> is less than zero.
    * @throws IllegalArgumentException when <code>name</code> is blank.
    */

   public //@formatter:off
      BasicArtifactSpecificationRecord
         (
            @NonNull Integer                                        identifier,
            @NonNull Integer                                        hierarchicalParentIdentifier,
            @NonNull String                                         name,
            @NonNull ArtifactTypeToken                              typeToken,
            @NonNull List<@NonNull AttributeSpecificationRecord>    attributeSpecifications,
            @NonNull List<@NonNull RelationshipSpecificationRecord> relationshipSpecificationRecords
         ) {

            this
            (
               identifier,
               hierarchicalParentIdentifier,
               name,
               typeToken,
               null,
               attributeSpecifications,
               relationshipSpecificationRecords
            );

      }
      //@formatter:on

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull ArtifactId getArtifactId() {
      return this.artifactId;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull ArtifactTypeToken getArtifactTypeToken() {
      return this.artifactTypeToken;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull List<@NonNull AttributeSpecificationRecord> getAttributeSpecifications() {
      return this.attributeSpecifications;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Integer getHierarchicalParentIdentifier() {
      return this.hierarchicalParentIdentifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Integer getIdentifier() {
      return this.identifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull List<@NonNull RelationshipSpecificationRecord> getRelationshipSpecifications() {
      return this.relationshipSpecificationRecords;
   }

}

/* EOF */