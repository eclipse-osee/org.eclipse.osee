/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface BranchQuery extends BranchQueryBuilder<BranchQuery>, Query {

   ResultSet<Branch> getResults();

   ResultSet<IOseeBranch> getResultsAsId();
   BranchQuery SENTINEL = createSentinel();

   @Override
   int getCount();

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
         public ResultSet<IOseeBranch> getResultsAsId() {
            return null;
         }

         @Override
         public int getCount() {
            return 0;
         }

      }
      return new BranchQuerySentinel();
   }

}