/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface BranchQuery extends BranchQueryBuilder<BranchQuery>, Query {
   BranchQuery SENTINEL = createSentinel();

   ResultSet<Branch> getResults();

   ResultSet<BranchToken> getResultsAsId();

   boolean isArchived(BranchId branchId);

   BranchToken getOneOrSentinel();

   public static BranchQuery createSentinel() {
      final class BranchQuerySentinel extends NamedIdBase implements BranchQuery {

         @Override
         public BranchQuery includeDeleted() {
            return null;
         }

         @Override
         public BranchQuery excludeDeleted() {
            return null;
         }

         @Override
         public BranchQuery includeDeleted(boolean enabled) {
            return null;
         }

         @Override
         public boolean areDeletedIncluded() {
            return false;
         }

         @Override
         public BranchQuery includeArchived() {
            return null;
         }

         @Override
         public BranchQuery includeArchived(boolean enabled) {
            return null;
         }

         @Override
         public BranchQuery excludeArchived() {
            return null;
         }

         @Override
         public boolean areArchivedIncluded() {
            return false;
         }

         @Override
         public BranchQuery andIds(Collection<? extends BranchId> ids) {
            return null;
         }

         @Override
         public BranchQuery andId(BranchId branchId) {
            return null;
         }

         @Override
         public BranchQuery andIsOfType(BranchType... branchType) {
            return null;
         }

         @Override
         public BranchQuery andStateIs(BranchState... branchState) {
            return null;
         }

         @Override
         public BranchQuery andNameEquals(String value) {
            return null;
         }

         @Override
         public BranchQuery andNamePattern(String pattern) {
            return null;
         }

         @Override
         public BranchQuery andIsChildOf(BranchId branch) {
            return null;
         }

         @Override
         public BranchQuery andIsAncestorOf(BranchId branch) {
            return null;
         }

         @Override
         public BranchQuery andIsMergeFor(BranchId source, BranchId destination) {
            return null;
         }

         @Override
         public BranchQuery andAssociatedArtId(ArtifactId artId) {
            return null;
         }

         @Override
         public boolean exists() {
            return false;
         }

         @Override
         public ResultSet<Branch> getResults() {
            return null;
         }

         @Override
         public ResultSet<BranchToken> getResultsAsId() {
            return null;
         }

         @Override
         public int getCount() {
            return 0;
         }

         @Override
         public BranchQuery andNamePatternIgnoreCase(String pattern) {
            return null;
         }

         @Override
         public boolean isArchived(BranchId branchId) {
            return false;
         }

         @Override
         public BranchToken getOneOrSentinel() {
            return null;
         }
      }
      return new BranchQuerySentinel();
   }

}