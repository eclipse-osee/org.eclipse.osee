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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * Instances of this interface are used to define test artifacts and the hierarchical structure of a test document.
 *
 * @author Loren K. Ashley
 */

public interface ArtifactSpecificationRecord {

   /**
    * Gets the {@link ArtifactId} the artifact should be created with or {@link ArtifactId#SENTINEL}.
    *
    * @return an {@link ArtifactId}.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public default @NonNull ArtifactId getArtifactId() {
      return ArtifactId.SENTINEL;
   }

   /**
    * Gets the {@link ArtifactTypeToken} that specifies the test artifact's type.
    *
    * @return the test artifact's {@link ArtifactTypeToken}.
    * @implSpec Implementations shall not return <code>null</code> or {@link ArtifactTypeToken#SENTINEL}.
    */

   public @NonNull ArtifactTypeToken getArtifactTypeToken();

   /**
    * Gets a list of {@link AttributeSpecificationRecord}s for the attributes to be set for the artifact.
    *
    * @return a {@link List}, possibly empty, of {@link AttributeSpecificationRecords}.
    * @implSpec Implementations shall not return <code>null</code>.
    * @implSpec Implementations shall not return a {@link List} with <code>null</code> elements.
    */

   public @NonNull List<@NonNull AttributeSpecificationRecord> getAttributeSpecifications();

   /**
    * Gets the {@link ArtifactSpecificationRecord} identifier of this {@link ArtifactSpecificationRecord}'s test
    * artifact's hierarchical parent.
    *
    * @return the {@link Integer} identifier of the hierarchical parent.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull Integer getHierarchicalParentIdentifier();

   /**
    * Gets the {@link ArtifactSpecificationRecord} identifier for the test artifact.
    *
    * @return the assigned {@link Integer} identifier.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull Integer getIdentifier();

   /**
    * Gets the test artifact's name.
    *
    * @return the test artifact name.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull String getName();

   /**
    * Get a list of the relationships for test artifact.
    *
    * @return a {@link List} of {@link RelationshipSpecificationRecord}s for the test artifact.
    * @implSpec Implementations shall not return <code>null</code>.
    * @implSpec Implementations shall not return a {@link List} with <code>null</code> elements.
    */

   public @NonNull List<@NonNull RelationshipSpecificationRecord> getRelationshipSpecifications();

   /**
    * Predicate to determine if the {@link ArtifactSpecificationRecord} contains any
    * {@link RelationshipSpecificationRecord}s.
    *
    * @return <code>true</code>, when the {@link ArtifactSpecificationRecord} contains
    * {@link RelationshipSpecificationRecord}s; otherwise, <code>false</code>.
    */

   default boolean hasRelationshipSpecifications() {
      var builderRelationshipRecords = this.getRelationshipSpecifications();
      return Objects.nonNull(builderRelationshipRecords) ? builderRelationshipRecords.size() > 0 : false;
   }

}

/* EOF */
