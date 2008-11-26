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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.data.OseeSql;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData extends BaseTransactionData {
   private static final String INSERT_ARTIFACT =
         "INSERT INTO osee_artifact (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_ARTIFACT_VERSION =
         "INSERT INTO osee_artifact_version (art_id, gamma_id, modification_id) VALUES (?,?,?)";

   private final Artifact artifact;

   public ArtifactTransactionData(Artifact artifact, ModificationType modificationType) throws OseeDataStoreException {
      super(artifact.getArtId(), modificationType);
      this.artifact = artifact;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return OseeSql.Transaction.SELECT_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#addInsertToBatch(org.eclipse.osee.framework.jdk.core.type.HashCollection)
    */
   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (getModificationType() == ModificationType.NEW) {
         internalAddInsertToBatch(transaction, 1, INSERT_ARTIFACT, artifact.getArtId(), artifact.getArtTypeId(),
               artifact.getGuid(), artifact.getHumanReadableId());
      }
      internalAddInsertToBatch(transaction, 2, INSERT_ARTIFACT_VERSION, getItemId(), getGammaId(),
            getModificationType().getValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalUpdate(org.eclipse.osee.framework.skynet.core.transaction.TransactionId)
    */
   @Override
   protected void internalUpdate(TransactionId transactionId) throws OseeCoreException {
      artifact.internalSetPersistenceData(getGammaId(), transactionId, getModificationType(), false);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalClearDirtyState()
    */
   @Override
   protected void internalClearDirtyState() {
      artifact.setNotDirty();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalOnRollBack()
    */
   @Override
   protected void internalOnRollBack() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#createGammaId()
    */
   @Override
   protected int createGammaId() throws OseeCoreException {
      return SequenceManager.getNextGammaId();
   }
}