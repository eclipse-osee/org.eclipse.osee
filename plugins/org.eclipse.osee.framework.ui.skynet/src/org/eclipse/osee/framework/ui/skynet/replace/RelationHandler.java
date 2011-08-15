/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink.ArtifactLinker;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Jeff C. Phillips
 */
public class RelationHandler {

   public HandleAttributeOrderData handleRelations(Artifact artifact, TransactionRecord baselineTransactionRecord, RelationChange change, SkynetTransaction skynetTransaction) throws OseeCoreException {
      boolean linkDeleted = false;
      RelationLink link =
         RelationManager.getLoadedRelationById(change.getItemId(), change.getArtId(), change.getBArtId(),
            artifact.getBranch());

      if (link == null) {
         linkDeleted = true;
         link =
            new RelationLink(new ReplaceRelationLinker(), change.getArtId(), change.getBArtId(), artifact.getBranch(),
               change.getRelationType(), change.getRelLinkId(), (int) change.getGamma(), change.getRationale(),
               change.getModificationType());
      }

      linkDeleted = linkDeleted || link.getModificationType().isDeleted();
      boolean isInBaselineTransaction = isBaselineTransaction(change, baselineTransactionRecord);

      if (isInBaselineTransaction) {
         if (link.getGammaId() != change.getGamma() || linkDeleted) {
            link.replaceWithVersion((int) change.getGamma());
            skynetTransaction.addRelation(link);
         }
      } else {
         link.delete(false);
      }
      return new HandleAttributeOrderData(link, artifact, baselineTransactionRecord);
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baseTx) {
      return change.getTxDelta().getStartTx().getId() == baseTx.getId();
   }

   private class ReplaceRelationLinker implements ArtifactLinker {

      @Override
      public void updateCachedArtifact(int artId, Branch branch) {
         //Do nothing
      }

      @Override
      public Artifact getArtifact(int ArtId, Branch branch) {
         return null;
      }

      @Override
      public String getLazyArtifactName(int aArtifactId, Branch branch) {
         return null;
      }

      @Override
      public void deleteFromRelationOrder(Artifact aArtifact, Artifact bArtifact, IRelationType relationType) {
         //Do nothing
      }
   }
}
