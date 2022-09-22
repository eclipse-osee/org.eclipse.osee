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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * Instances of this interface are used to define test artifacts and the hierarchical structure of a test document.
 */

public interface BuilderRecord {

   /**
    * Gets the {@link ArtifactId} the artifact should be created with or {@link ArtifactId#SENTINEL}.
    *
    * @return an {@link ArtifactId}.
    */

   default ArtifactId getArtifactId() {
      return ArtifactId.SENTINEL;
   }

   /**
    * Gets the {@link ArtifactTypeToken} that specifies the test artifact's type.
    *
    * @return the test artifact's {@link ArtifactTypeToken}.
    */

   ArtifactTypeToken getArtifactTypeToken();

   /**
    * Gets a list of {@link AttributeSpecificationRecord}s for the attributes to be set for the artifact.
    *
    * @return a {@link List}, possibly empty, of {@link AttributeSpecificationRecords}.
    */

   List<AttributeSpecificationRecord> getAttributeSpecifications();

   /**
    * Gets the {@link BuilderRecord} identifier of this {@link BuilderRecord}'s test artifact's hierarchical parent.
    *
    * @return the {@link Integer} identifier of the hierarchical parent.
    */

   Integer getHierarchicalParentIdentifier();

   /**
    * Gets the {@link BuilderRecord} identifier for the test artifact.
    *
    * @return the assigned {@link Integer} identifier.
    */

   Integer getIdentifier();

   /**
    * Gets the test artifact's name.
    *
    * @return the test artifact name.
    */

   String getName();

   /**
    * Get a list of the relationships for test artifact.
    *
    * @return a {@link List} of {@link BuilderRelationshipRecords} for the test artifact.
    */

   List<BuilderRelationshipRecord> getBuilderRelationshipRecords();

   /**
    * Predicate to determine if the {@link BuilderRecord} contains any {@link BuilderRelationshipRecord}s.
    *
    * @return <code>true</code>, when the {@link BuilderRecord} contains {@link BuilderRelationshipRecords}; otherwise,
    * <code>false</code>.
    */

   default boolean hasBuilderRelationshipRecords() {
      var builderRelationshipRecords = this.getBuilderRelationshipRecords();
      return Objects.nonNull(builderRelationshipRecords) ? builderRelationshipRecords.size() > 0 : false;
   }

}

/* EOF */
