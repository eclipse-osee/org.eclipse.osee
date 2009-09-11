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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Jeff C. Phillips
 */
public abstract class ChangeBuilder {
   private final int sourceGamma;
   private final int artId;
   private final TransactionId toTransactionId;
   private TransactionId fromTransactionId;
   private ModificationType modType;
   private final ChangeType changeType;
   private Branch branch;
   private final ArtifactType artifactType;
   private final boolean isHistorical;

   public ChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical) {
      super();
      this.sourceGamma = sourceGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.modType = modType;
      this.changeType = changeType;
      this.branch = branch;
      this.artifactType = artifactType;
      this.isHistorical = isHistorical;
   }

   public int getSourceGamma() {
      return sourceGamma;
   }

   public int getArtId() {
      return artId;
   }

   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }

   public ModificationType getModType() {
      return modType;
   }
   
   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public ChangeType getChangeType() {
      return changeType;
   }

   public Branch getBranch() {
      return branch;
   }

   public ArtifactType getArtifactType() {
      return artifactType;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   protected Artifact loadArtifact(){
      Artifact artifact;
      
      if (isHistorical()) {
         artifact = ArtifactCache.getHistorical(getArtId(), getToTransactionId().getTransactionNumber());
       } else {
          artifact = ArtifactCache.getActive(getArtId(), getBranch());
       }
      return artifact;
   }

   public abstract Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist;

}
