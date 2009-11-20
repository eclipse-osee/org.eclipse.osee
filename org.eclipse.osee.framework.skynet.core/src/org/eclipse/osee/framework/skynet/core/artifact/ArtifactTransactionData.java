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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData extends BaseTransactionData {
   private static final String INSERT_ARTIFACT =
         "INSERT INTO osee_artifact (art_id, art_type_id, guid, human_readable_id) VALUES (?, ?, ?, ?)";

   private static final String INSERT_ARTIFACT_VERSION =
         "INSERT INTO osee_artifact_version (art_id, gamma_id) VALUES (?,?)";

   private final Artifact artifact;

   public ArtifactTransactionData(Artifact artifact) throws OseeDataStoreException {
      super(artifact.getArtId(), artifact.getModType());
      this.artifact = artifact;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS;
   }

   @Override
   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      super.addInsertToBatch(transaction);
      if (!useExistingBackingData()) {
         if (!artifact.isInDb()) {
            internalAddInsertToBatch(transaction, 1, INSERT_ARTIFACT, artifact.getArtId(), artifact.getArtTypeId(),
                  artifact.getGuid(), artifact.getHumanReadableId());
         }
         internalAddInsertToBatch(transaction, 2, INSERT_ARTIFACT_VERSION, getItemId(), getGammaId());
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) throws OseeCoreException {
      artifact.internalSetPersistenceData(getGammaId(), transactionId, getModificationType(), false);
   }

   @Override
   protected void internalClearDirtyState() {
   }

   @Override
   protected void internalOnRollBack() throws OseeCoreException {
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      if (useExistingBackingData()) {
         return artifact.getGammaId();
      }
      return SequenceManager.getNextGammaId();
   }

   @Override
   protected void internalAddToEvents(Collection<ArtifactTransactionModifiedEvent> events) throws OseeCoreException {
      ArtifactModType artifactModType;
      switch (getModificationType()) {
         case MODIFIED:
            artifactModType = ArtifactModType.Changed;
            break;
         case DELETED:
            artifactModType = ArtifactModType.Deleted;
            break;
         default:
            artifactModType = ArtifactModType.Added;
            break;
      }
      events.add(new ArtifactModifiedEvent(new Sender(this.getClass().getName()), artifactModType, artifact,
            artifact.getTransactionNumber(), artifact.getDirtySkynetAttributeChanges()));
   }
}