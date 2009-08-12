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
package org.eclipse.osee.framework.skynet.core.artifact.update;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Jeff C. Phillips
 *
 */
public class RebaselineDbTransaction extends DbTransaction{
   private UpdateArtifactHandler updateArtifactHandler;
   private Branch branchToUpdate;
   private List<Artifact> artifactVersions;
   
   /**
    * @throws OseeCoreException
    */
   public RebaselineDbTransaction(Branch branchToUpdate, Branch updatingSourceBranch, List<Artifact> artifactVersions) throws OseeCoreException {
      super();
      
      this.branchToUpdate = branchToUpdate;
      this.artifactVersions = artifactVersions;
      int transactionNumber = TransactionIdManager.getStartEndPoint(branchToUpdate).getFirst().getTransactionNumber();
      this.updateArtifactHandler = new UpdateArtifactHandler(branchToUpdate, updatingSourceBranch, artifactVersions, transactionNumber, true);
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      updateArtifactHandler.update(connection);
   }
   
   private void updateSystemCaches(Branch branchToUpdate, List<Artifact> artifactVersions) throws OseeCoreException {
      for (Artifact artifact : artifactVersions) {
         Artifact droppedArtifact = ArtifactCache.getActive(artifact.getArtId(), branchToUpdate);
         Artifact parent = null;

         //In case the artifact was new to the branch load him as a new artifact
         if (droppedArtifact == null) {
            droppedArtifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), branchToUpdate);
         }

         if (droppedArtifact != null) {
            parent = droppedArtifact.getParent();
         }

         if (droppedArtifact != null) {
            droppedArtifact.reloadAttributesAndRelations();

            if (parent != null) {
               parent.reloadAttributesAndRelations();
            }
            OseeEventManager.kickArtifactModifiedEvent(RebaselineDbTransaction.class, ArtifactModType.Reverted,
                  droppedArtifact);
            
            if(parent != null){
               OseeEventManager.kickArtifactModifiedEvent(RebaselineDbTransaction.class, ArtifactModType.Changed, parent);
            }
         }
      }
   }
   
   @Override
   protected void handleTxFinally() throws OseeCoreException {
      updateSystemCaches(branchToUpdate, artifactVersions);
   
      super.handleTxFinally();
   }

}
