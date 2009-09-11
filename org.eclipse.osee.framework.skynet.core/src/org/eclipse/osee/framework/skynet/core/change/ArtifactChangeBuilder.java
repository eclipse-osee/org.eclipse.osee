/*
 * Created on Sep 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 *
 */
public class ArtifactChangeBuilder extends ChangeBuilder {

   /**
    * @param sourceGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param artifact
    * @param modType
    * @param changeType
    * @param branch
    * @param artifactType
    * @param isHistorical
    */
   public ArtifactChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical) {
      super(branch, artifactType, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType, isHistorical);
   }

   @Override
   public Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      return new ArtifactChange(branch, getArtifactType(), getSourceGamma(), getArtId(), getToTransactionId(), getFromTransactionId(), getModType(), getChangeType(), isHistorical(), loadArtifact());
   }
}
