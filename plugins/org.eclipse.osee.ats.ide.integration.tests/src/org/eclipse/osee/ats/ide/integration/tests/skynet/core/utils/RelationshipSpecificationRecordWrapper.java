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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * A wrapper for a {@link RelationshipSpecificationRecord} implementation that implements the
 * {@link RelationshipSpecificationRecord} interface with additional members and methods needed by the
 * {@link TestDocumentBuilder}.
 *
 * @author Loren K. Ashley
 * @ImplNote This wrapper is used to reduce the burden of creating a custom {@link RelationshipSpecificationRecord}
 * implementations by eliminating the need to implement the members and methods needed by the
 * {@link TestDocumentBuilder} that are necessary to specify a test relationship.
 */

public class RelationshipSpecificationRecordWrapper implements RelationshipSpecificationRecord {

   /**
    * The wrapped {@link RelationshipSpecificationRecord}.
    */

   private final @NonNull RelationshipSpecificationRecord relationshipSpecificationRecord;

   /**
    * Saves the {@link Artifact} that will be used as the source of the relationship.
    */

   private final Artifact sourceArtifact;

   /**
    * Wraps a {@link RelationshipSpecificationRecord} with the relationship source {@link Artifact} for the test
    * document building process.
    *
    * @param sourceArtifact the relationship source {@link Artifact}.
    * @param relationshipSpecificationRecord {@link RelationshipSpecificationRecord} to be wrapped.
    * @throws NullPointerException when any parameters are <code>null</code>.
    */

   RelationshipSpecificationRecordWrapper(@NonNull Artifact sourceArtifact, @NonNull RelationshipSpecificationRecord relationshipSpecificationRecord) {

      //@formatter:off
      this.sourceArtifact =
         Conditions.requireNonNull
            (
               sourceArtifact,
               "RelationshipSpecificationRecordWrapper",
               "new",
               "sourceArtifact"
            );
      //@formatter:on

      //@formatter:off
      this.relationshipSpecificationRecord =
         Conditions.requireNonNull
            (
               relationshipSpecificationRecord,
               "RelationshipSpecificationRecordWrapper",
               "new",
               "relationshipSpecificationRecord"
            );
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the wrapped {@link RelationshipSpecificationRecord}'s {@link List} of
    * {@link ArtifactSpecificationRecord} identifiers for relationship target artifacts is <code>null</code> or the list
    * contains a <code>null</code> entry.
    */

   @Override
   public @NonNull List<@NonNull Integer> getRelationshipTargetArtifactSpecificationRecordIdentifiers() {

      List<Integer> list =
         this.relationshipSpecificationRecord.getRelationshipTargetArtifactSpecificationRecordIdentifiers();

      //@formatter:off
      return
         Conditions.require
         (
            list,
            ValueType.RESULT,
            "RelationshipSpecificationRecordWrapper",
            "getRelationshipTargetArtifactSpecificationRecordIdentifiers",
            "this.relationshipSpecificationRecord.getRelationshipTargetArtifactSpecificationRecordIdentifiers()",
            "result is not null",
            Objects::isNull,
            "result does not contail a null element",
            Conditions::collectionContainsNull,
            NullPointerException::new
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the wrapped {@link RelationshipSpecificationRecord}'s {@link RelationTypeToken}
    * is not set.
    */

   @Override
   public @NonNull RelationTypeToken getRelationTypeToken() {

      //@formatter:off
      return
         Conditions.requireNonNull
            (
               this.relationshipSpecificationRecord.getRelationTypeToken(),
               "RelationshipSpecificationRecordWrapper",
               "getRelationTypeToken",
               "this.relationshipSpecificationRecord.getRelationTypeToken()"
            );
      //@formatter:on
   }

   /**
    * Gets the {@link Artifact} that is the source for the relationship.
    *
    * @return the referencer {@link Artifact}.
    */

   public @NonNull Artifact getSourceArtifact() {
      return this.sourceArtifact;
   }

}

/* EOF */
