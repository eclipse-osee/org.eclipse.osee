/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.change;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public final class RelationChange extends Change {
   private final int bArtId;
   private final Artifact endTxBArtifact;
   private final int relLinkId;
   private final String rationale;
   private final RelationType relationType;

   public RelationChange(IOseeBranch branch, ArtifactType aArtType, long sourceGamma, int aArtId, TransactionDelta txDelta, ModificationType modType, int bArtId, int relLinkId, String rationale, RelationType relationType, boolean isHistorical, ArtifactDelta artifactDelta, Artifact endTxBArtifact) {
      super(branch, aArtType, sourceGamma, aArtId, txDelta, modType, isHistorical, artifactDelta);
      this.bArtId = bArtId;
      this.relLinkId = relLinkId;
      this.rationale = rationale;
      this.relationType = relationType;
      this.endTxBArtifact = endTxBArtifact;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      try {
         if (adapter.isInstance(getSourceArtifact())) {
            return getSourceArtifact();
         } else if (adapter.isInstance(getTxDelta().getEndTx()) && isHistorical()) {
            return getTxDelta().getEndTx();
         } else if (adapter.isInstance(this)) {
            return this;
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /**
    * @return the bArtId
    */
   public int getBArtId() {
      return bArtId;
   }

   public Artifact getEndTxBArtifact() {
      return endTxBArtifact;
   }

   /**
    * @return the relLinkId
    */
   public int getRelLinkId() {
      return relLinkId;
   }

   /**
    * @return the rationale
    */
   public String getRationale() {
      return rationale;
   }

   /**
    * @return the relationType
    */
   public RelationType getRelationType() {
      return relationType;
   }

   @Override
   public String getName() {
      return getArtifactName() + " <-> " + getEndTxBArtifact().getName();
   }

   @Override
   public String getItemTypeName() {
      return relationType.getName();
   }

   @Override
   public String getIsValue() {
      return getRationale();
   }

   @Override
   public String getItemKind() {
      return "Relation";
   }

   @Override
   public String getWasValue() {
      return null;
   }

   @Override
   public int getItemTypeId() {
      return relationType.getId();
   }

   @Override
   public int getItemId() {
      return relLinkId;
   }
}
