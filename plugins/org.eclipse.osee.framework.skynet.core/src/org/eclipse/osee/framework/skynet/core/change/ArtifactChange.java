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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactChange extends Change {

   public ArtifactChange(IOseeBranch branch, ArtifactType artType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, Artifact toArtifact, Artifact fromArtifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super(branch, artType, sourceGamma, artId, txDelta, modType, isHistorical, toArtifact, fromArtifact);
   }

   @Override
   public String getName() {
      return getArtifactName();
   }

   @Override
   public String getItemTypeName() {
      return getArtifactType().getName();
   }

   @Override
   public String getIsValue() {
      return "";
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(getToArtifact())) {
         return getToArtifact();
      } else if (adapter.isInstance(getTxDelta().getEndTx()) && isHistorical()) {
         return getTxDelta().getEndTx();
      } else if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   @Override
   public String getItemKind() {
      return "Artifact";
   }

   @Override
   public String getWasValue() {
      return null;
   }

   @Override
   public int getItemTypeId() {
      return getArtifactType().getId();
   }

   @Override
   public int getItemId() {
      return getArtId();
   }
}
