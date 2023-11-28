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

import java.util.List;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Validation;

/**
 * A basic implementation of the {@link RelationshipSpecificationRecord} that will provide the necessary information for
 * the {@link TestDocumentBuilder} to verify or create relationships between test artifacts.
 */

public class BasicRelationshipSpecificationRecord implements RelationshipSpecificationRecord {

   /**
    * The type of relationship
    */

   private final @NonNull RelationTypeToken relationTypeToken;

   /**
    * A {@link List} of the {@link ArtifactSpecificationRecord} identifiers representing the related test artifacts.
    */

   private final @NonNull List<@NonNull Integer> targetArtifactSpecificationRecordIdentifiers;

   /**
    * Constructs a new {@link BasicRelationshipSpecificationRecord} with the specified parameters.
    *
    * @param relationTypeToken the type of relationship.
    * @param targetArtifactSpecificationRecordIdentifiers a {@link List} of the {@link ArtifactSpecificationRecord}
    * identifiers of the related test artifacts.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    * @throws NullPointerException then the parameter <code>targetBuilderRecords</code> contains a <code>null</code>
    * entry.
    */

   public BasicRelationshipSpecificationRecord(@NonNull RelationTypeToken relationTypeToken, @NonNull List<@NonNull Integer> targetArtifactSpecificationRecordIdentifiers) {

      //@formatter:off
      this.relationTypeToken =
         Validation.requireNonNull
            (
               relationTypeToken,
               "BasicRelationshipSpecificationRecord",
               "new",
               "relationTypeToken"
            );
      //@formatter:on

      //@formatter:off
      this.targetArtifactSpecificationRecordIdentifiers =
         Validation.require
            (
               targetArtifactSpecificationRecordIdentifiers,
               Validation.ValueType.PARAMETER,
               "BasicRelationshipSpecificationRecord",
               "new",
               "targetArtifactSpecificationRecordIdentifiers",
               "cannot be null",
               Objects::isNull,
               "does not contain null elements",
               Validation::collectionContainsNull,
               NullPointerException::new
            );
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull RelationTypeToken getRelationTypeToken() {
      return this.relationTypeToken;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull List<@NonNull Integer> getRelationshipTargetArtifactSpecificationRecordIdentifiers() {
      return this.targetArtifactSpecificationRecordIdentifiers;
   }

}

/* EOF */
