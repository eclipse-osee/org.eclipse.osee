/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

public class ChangeUiData {
   private final Collection<Change> changes = new ArrayList<>();
   private Artifact associatedArtifact;
   private boolean isLoaded;
   private boolean loadOnOpen;
   private boolean areBranchesValid;
   private CompareType compareType;

   private TransactionDelta txDelta;
   private IOseeBranch mergeBranch;

   public ChangeUiData(CompareType compareType, TransactionDelta txDelta) {
      this.compareType = compareType;
      this.txDelta = txDelta;
   }

   public void setCompareType(CompareType compareType) {
      this.compareType = compareType;
   }

   public CompareType getCompareType() {
      return compareType;
   }

   public void setTxDelta(TransactionDelta txDelta) {
      this.txDelta = txDelta;
   }

   public void reset() {
      changes.clear();
      setAssociatedArtifact(null);
      setIsLoaded(false);
   }

   public boolean isMergeBranchValid() {
      return mergeBranch != null;
   }

   public void setMergeBranch(IOseeBranch mergeBranch) {
      this.mergeBranch = mergeBranch;
   }

   public boolean isLoaded() {
      return isLoaded;
   }

   public void setIsLoaded(boolean isLoaded) {
      this.isLoaded = isLoaded;
   }

   public void setLoadOnOpen(boolean loadOnOpen) {
      this.loadOnOpen = loadOnOpen;
   }

   public boolean isLoadOnOpenEnabled() {
      return loadOnOpen;
   }

   public Collection<Change> getChanges() {
      return changes;
   }

   public void setAssociatedArtifact(Artifact associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public Artifact getAssociatedArtifact() {
      return associatedArtifact;
   }

   public TransactionDelta getTxDelta() {
      return txDelta;
   }

   public boolean areBranchesValid() {
      return areBranchesValid;
   }

   public void setAreBranchesValid(boolean areBranchesValid) {
      this.areBranchesValid = areBranchesValid;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (compareType == null ? 0 : compareType.hashCode());
      result = prime * result + (txDelta == null ? 0 : txDelta.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ChangeUiData other = (ChangeUiData) obj;
      if (compareType == null) {
         if (other.compareType != null) {
            return false;
         }
      } else if (!compareType.equals(other.compareType)) {
         return false;
      }
      if (txDelta == null) {
         if (other.txDelta != null) {
            return false;
         }
      } else if (!txDelta.equals(other.txDelta)) {
         return false;
      }
      return true;
   }
}