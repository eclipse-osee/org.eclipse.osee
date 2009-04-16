/*
 * Created on Apr 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.update;

import java.util.List;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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
public class UpdateArtifactDbTransaction extends DbTransaction{
   private UpdateArtifactHandler updateArtifactHandler;
   private Branch branchToUpdate;
   private List<Artifact> artifactVersions;
   
   /**
    * @throws OseeCoreException
    */
   public UpdateArtifactDbTransaction(Branch branchToUpdate, Branch updatingSourceBranch, List<Artifact> artifactVersions) throws OseeCoreException {
      super();
      
      this.branchToUpdate = branchToUpdate;
      this.artifactVersions = artifactVersions;
      int transactionNumber = TransactionIdManager.getStartEndPoint(branchToUpdate).getKey().getTransactionNumber();
      this.updateArtifactHandler = new UpdateArtifactHandler(branchToUpdate, updatingSourceBranch, artifactVersions, transactionNumber, true);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxWork(org.eclipse.osee.framework.db.connection.OseeConnection)
    */
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
            OseeEventManager.kickArtifactModifiedEvent(UpdateArtifactDbTransaction.class, ArtifactModType.Reverted,
                  droppedArtifact);
            
            if(parent != null){
               OseeEventManager.kickArtifactModifiedEvent(UpdateArtifactDbTransaction.class, ArtifactModType.Changed, parent);
            }
         }
      }
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxFinally()
    */
   @Override
   protected void handleTxFinally() throws OseeCoreException {
      updateSystemCaches(branchToUpdate, artifactVersions);
   
      super.handleTxFinally();
   }

}
