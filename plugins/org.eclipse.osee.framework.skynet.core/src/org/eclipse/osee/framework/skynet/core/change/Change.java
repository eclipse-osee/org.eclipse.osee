/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.HasBranchId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionDeltaSupplier;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public abstract class Change implements IAdaptable, Comparable<Change>, HasBranchId, TransactionDeltaSupplier {
   private final GammaId sourceGamma;
   private final ArtifactId artId;
   private final TransactionDelta txDelta;
   private final ArtifactDelta artifactDelta;
   private final ModificationType modType;
   private final BranchToken branch;
   private final boolean isHistorical;
   private final Artifact changeArtifact;
   private ChangeItem changeItem;
   public static Boolean showTokenForChangeName;

   public Change(BranchToken branch, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, Artifact changeArtifact, ArtifactDelta artifactDelta) {
      this.branch = branch;
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.txDelta = txDelta;
      this.modType = modType;
      this.isHistorical = isHistorical;
      this.artifactDelta = artifactDelta;
      this.changeArtifact = changeArtifact;
   }

   public ChangeItem getChangeItem() {
      return changeItem;
   }

   public void setChangeItem(ChangeItem changeItem) {
      this.changeItem = changeItem;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Change) {
         Change change = (Change) obj;
         boolean areDeltasEqual = false;
         if (change.getDelta() != null && getDelta() != null) {
            areDeltasEqual = change.getDelta().equals(getDelta());
         } else if (change.getDelta() == null && getDelta() == null) {
            areDeltasEqual = true;
         }
         return areDeltasEqual && change.getArtId().equals(getArtId()) &&
         //
            change.getGamma() == getGamma() &&
            //
            change.getChangeArtifact().equals(getChangeArtifact()) &&
            //
            change.getModificationType().equals(getModificationType()) &&
            //
            change.getTxDelta().equals(getTxDelta());
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 7;
      hashCode += 13 * getArtId().hashCode();
      hashCode += 13 * getGamma().hashCode();
      hashCode += 13 * getChangeArtifact().hashCode();
      hashCode += getDelta() != null ? 13 * getDelta().hashCode() : 0;
      hashCode += getModificationType() != null ? 13 * getModificationType().hashCode() : 0;
      hashCode += getTxDelta() != null ? 13 * getTxDelta().hashCode() : 0;
      return hashCode;
   }

   public GammaId getBaselineGamma() {
      return changeItem.getBaselineVersion().getGammaId();
   }

   public boolean isBaseline() {
      return changeItem.getBaselineVersion().isValid();
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   public ModificationType getModificationType() {
      return modType;
   }

   public ArtifactDelta getDelta() {
      return artifactDelta;
   }

   public Artifact getChangeArtifact() {
      return changeArtifact;
   }

   /**
    * @return Name or [Name]-[Id] if UserManager.isShowTokenForChangeName()
    */
   public String getArtifactNameOrToken() {
      if (UserManager.isShowTokenForChangeName()) {
         return getChangeArtifact().toStringWithId();
      }
      return getChangeArtifact().getName();
   }

   public String getArtifactName() {
      return getChangeArtifact().getName();
   }

   public GammaId getGamma() {
      return sourceGamma;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   @Override
   public TransactionDelta getTxDelta() {
      return txDelta;
   }

   public abstract Id getItemTypeId();

   public ArtifactTypeToken getArtifactType() {
      return getChangeArtifact().getArtifactType();
   }

   @Override
   public BranchToken getBranch() {
      return branch;
   }

   /**
    * @return value after change or value before if modtype == DELETED
    */
   public abstract String getIsValue();

   /**
    * @return value before change or empty if modtype == DELETED. Use getIsValue() for modtype == DELETED.
    */
   public abstract String getWasValue();

   public abstract String getItemTypeName();

   /**
    * @return Name or [Name]-[Id] if UserManager.isShowTokenForChangeName()
    */
   public abstract String getNameOrToken();

   public abstract String getName();

   public abstract String getItemKind();

   public abstract Id getItemId();

   public abstract ChangeType getChangeType();

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      T toReturn = null;
      if (type != null) {
         if (type.isAssignableFrom(Artifact.class)) {
            toReturn = (T) getChangeArtifact();
         } else if (isHistorical() && type.isInstance(getTxDelta().getEndTx())) {
            toReturn = (T) getTxDelta().getEndTx();
         } else if (type.isAssignableFrom(getClass())) {
            toReturn = (T) this;
         }
      }

      return toReturn;
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public int compareTo(Change o) {
      return getName().compareTo(o.getName());
   }
}