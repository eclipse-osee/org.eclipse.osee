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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData extends BaseTransactionData {
   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (gamma_id, art_id, art_type_id, guid) VALUES (?,?,?,?)";

   private final Artifact artifact;

   public ArtifactTransactionData(Artifact artifact) {
      super(artifact, artifact.getModType());
      this.artifact = artifact;
   }

   @Override
   public OseeSql getSelectTxNotCurrentSql() {
      return OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS;
   }

   @Override
   protected void addInsertToBatch(InsertDataCollector collector) throws OseeCoreException {
      super.addInsertToBatch(collector);
      if (!artifact.isUseBackingdata()) {
         internalAddInsertToBatch(collector, 1, INSERT_ARTIFACT, getGammaId(), artifact.getArtId(),
            artifact.getArtTypeId(), artifact.getGuid());
      }
   }

   @Override
   protected void internalUpdate(TransactionRecord transactionId) throws OseeCoreException {
      artifact.internalSetPersistenceData(getGammaId(), transactionId, getModificationType(), getApplicabilityId(),
         false, false);
   }

   @Override
   protected void internalClearDirtyState() {
      // provided for subclass implementation
   }

   @Override
   protected void internalOnRollBack() {
      // provided for subclass implementation
   }

   @Override
   protected int createGammaId() throws OseeCoreException {
      return artifact.isUseBackingdata() ? artifact.getGammaId() : getNextGammaIdFromSequence();
   }

   @Override
   protected void internalAddToEvents(ArtifactEvent artifactEvent) {
      switch (getModificationType()) {
         case MODIFIED:
            // transactionEvent populated in SkynetTransaction after all attribute changes have been made
            break;
         case DELETED:
            artifactEvent.addArtifact(new EventBasicGuidArtifact(EventModType.Deleted, artifact));
            break;
         default:
            artifactEvent.addArtifact(new EventBasicGuidArtifact(EventModType.Added, artifact));
            break;
      }
   }

   @Override
   protected ApplicabilityId getApplicabilityId() {
      return artifact.getApplicablityId();
   }

}