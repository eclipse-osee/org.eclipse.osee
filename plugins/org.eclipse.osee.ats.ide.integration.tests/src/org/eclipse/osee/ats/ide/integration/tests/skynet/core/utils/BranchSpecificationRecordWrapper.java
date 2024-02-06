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

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;

/**
 * A wrapper for a {@link BranchSpecificationRecord} implementation that implements the
 * {@link BranchSpecificationRecord} interface with additional members and methods needed by the
 * {@link TestDocumentBuilder}.
 *
 * @author Loren K. Ashley
 * @ImplNote This wrapper is used to reduce the burden of creating a custom {@link BranchSpecificationRecord}
 * implementations by eliminating the need to implement the members and methods needed by the
 * {@link TestDocumentBuilder} that are necessary to specify a test branch.
 */

public class BranchSpecificationRecordWrapper implements BranchSpecificationRecord {

   /**
    * Saves the wrapped {@link BranchSpecificationRecord} implementation.
    */

   private final @NonNull BranchSpecificationRecord branchSpecificationRecord;

   /**
    * Saves the parent branch of the test branch.
    */

   private Branch parentTestBranch;

   /**
    * Saves an instance of the {@link RelationEnpoint} that was instantiated for the test branch.
    */

   private RelationEndpoint relationEndpoint;

   /**
    * Saves the test branch.
    */

   private Branch testBranch;

   /**
    * Creates a new {@link BranchSpecificationRecordWrapper} with the provided <code>branchSpecificationRecord</code>.
    *
    * @param branchSpecificationRecord the {@link BranchSpecificationRecord} implementation to be wrapped.
    * @throws NullPointerException when <code>branchSpecificationRecord</code> is <code>null</code>.
    */

   BranchSpecificationRecordWrapper(@NonNull BranchSpecificationRecord branchSpecificationRecord) {
      //@formatter:off
      this.branchSpecificationRecord =
         Conditions.requireNonNull
            (
               branchSpecificationRecord,
               "BranchSpecificationRecordWrapper",
               "new",
               "branchSpecificationRecord"
            );
      //@formatter:on
      this.relationEndpoint = null;
      this.parentTestBranch = null;
      this.testBranch = null;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the hierarchical parent {@link BranchSpecificationRecord} identifier is not set.
    */

   @Override
   public @NonNull Integer getHierarchicalParentIdentifier() {
      return Objects.requireNonNull(this.branchSpecificationRecord.getHierarchicalParentIdentifier());
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the wrapped {@link BranchSpecificationRecord}'s identifier is not set.
    */

   @Override
   public @NonNull Integer getIdentifier() {
      return Objects.requireNonNull(this.branchSpecificationRecord.getIdentifier());
   }

   /**
    * Gets the parent of the test branch.
    *
    * @return the {@link Branch} for the parent of the test branch.
    * @throws IllegalStateException if the branches have not yet been set with {@link #setBranches}.
    */

   public @NonNull Branch getParentTestBranch() {
      if (Objects.isNull(this.parentTestBranch)) {
         throw new IllegalStateException();
      }
      return this.parentTestBranch;
   }

   /**
    * Gets the identifier of the parent test branch as a {@link BranchId} implementation that only contains the
    * {@link Long} branch identifier and a {@link ArtifactId#SENTINEL} view identifier.
    *
    * @return the parent test branch {@link BranchId}.
    * @throws IllegalStateException if the branches have not yet been set with {@link #setBranches}.
    */

   public @NonNull BranchId getParentTestBranchIdentifier() {
      if (Objects.isNull(this.parentTestBranch)) {
         throw new IllegalStateException();
      }
      return BranchId.valueOf(this.parentTestBranch.getId());
   }

   /**
    * Gets the {@link RelationEndpoint} instance for the test branch.
    *
    * @return the test branch {@link RelationEndpoint}.
    * @throws IllegalStateException if the branches have not yet been set with {@link #setBranches}.
    */

   public @NonNull RelationEndpoint getRelationEndpoint() {
      if (Objects.isNull(this.relationEndpoint)) {
         throw new IllegalStateException();
      }
      return this.relationEndpoint;
   }

   /**
    * Gets the test branch.
    *
    * @return the {@link Branch} for the test branch.
    * @throws IllegalStateException if the branches have not yet been set with {@link #setBranches}.
    */

   public @NonNull Branch getTestBranch() {
      if (Objects.isNull(this.testBranch)) {
         throw new IllegalStateException();
      }
      return this.testBranch;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the wrapped {@link BranchSpecificationRecord}'s branch creation comment is not
    * set.
    */

   @Override
   public @NonNull String getTestBranchCreationComment() {
      return Objects.requireNonNull(this.branchSpecificationRecord.getTestBranchCreationComment());
   }

   /**
    * Gets the identifier of the test branch as a {@link BranchId} implementation that only contains the {@link Long}
    * branch identifier and a {@link ArtifactId#SENTINEL} view identifier.
    *
    * @return the test branch {@link BranchId}.
    * @throws IllegalStateException if the branches have not yet been set with {@link #setBranches}.
    */

   public @NonNull BranchId getTestBranchIdentifier() {
      if (Objects.isNull(this.testBranch)) {
         throw new IllegalStateException();
      }
      return BranchId.valueOf(this.testBranch.getId());
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the wrapped {@link BranchSpecificationRecord}'s branch name is not set.
    */

   @Override
   public @NonNull String getTestBranchName() {
      return Objects.requireNonNull(this.branchSpecificationRecord.getTestBranchName());
   }

   /**
    * Saves the <code>parentTestBranch</code>, <code>testBranch</code>, and the <code>relationEndpoint</code>.
    *
    * @param parentTestBranch the {@link Branch} for the parent test branch.
    * @param testBranch the {@link Branch} for the test branch.
    * @param relationEndpoint the {@link RelationEndpoint} for the test branch.
    * @throws IllegalStateException when the branches have already been set with this method.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public void setBranches(@NonNull Branch parentTestBranch, @NonNull Branch testBranch, @NonNull RelationEndpoint relationEndpoint) {

      //@formatter:off
      Conditions.requireNull
         (
            this.testBranch,
            "BranchSpecificationRecordWrapper",
            "setBranches",
            "testBranch"
         );
      //@formatter:on

      //@formatter:off
      this.parentTestBranch =
         Conditions.requireNonNull
           (
              parentTestBranch,
              "BranchSpecificationRecordWrapper",
              "setBranches",
              "parentTestBranch"
           );
      //@formatter:on

      //@formatter:off
      this.testBranch =
         Conditions.requireNonNull
            (
               testBranch,
               "BranchSpecificationRecordWrapper",
               "setBranches",
               "testBranch"
            );
      //@formatter:on

      //@formatter:off
      this.relationEndpoint =
         Conditions.requireNonNull
            (
               relationEndpoint,
               "BranchSpecificationRecordWrapper",
               "setBranches",
               "relationEndpoint"
            );
      //@formatter:on
   }

}

/* EOF */
