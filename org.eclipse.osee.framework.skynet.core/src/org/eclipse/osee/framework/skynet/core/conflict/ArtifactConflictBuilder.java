/*
 * Created on Apr 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;

/**
 * @author Theron Virgin
 */
public class ArtifactConflictBuilder extends ConflictBuilder {
   private int sourceModType;
   private int destModType;
   private int artTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param sourceBranch
    * @param destBranch
    */
   public ArtifactConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, Branch sourceBranch, Branch destBranch, int sourceTxType, int destTxType, int artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, sourceBranch, destBranch);
      this.artTypeId = artTypeId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder#getConflict(org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   @Override
   public Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws SQLException, IOException, Exception {
      return new ArtifactConflict(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType,
            ChangeType.CONFLICTING, mergeBranch, sourceBranch, destBranch, sourceModType, destModType, artTypeId);
   }

}
