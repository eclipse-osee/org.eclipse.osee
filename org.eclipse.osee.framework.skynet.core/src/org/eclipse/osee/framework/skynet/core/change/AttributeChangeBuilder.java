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
public final class AttributeChangeBuilder extends ChangeBuilder{
   private final String isValue;
   private String wasValue;
   private final int attrId;
   private final int attrTypeId;
   private final ModificationType artModType;
   


   public AttributeChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical, String isValue, String wasValue, int attrId, int attrTypeId, ModificationType artModType) {
      super(branch, artifactType, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical);
      this.isValue = isValue;
      this.wasValue = wasValue;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.artModType = artModType;
   }

   /**
    * @return the artModType
    */
   public ModificationType getArtModType() {
      return artModType;
   }
   
   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }
   
   @Override
   public Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      return new AttributeChange(branch, getArtifactType(), getSourceGamma(), getArtId(), getToTransactionId(), getFromTransactionId(), getModType(), getChangeType(), isValue, wasValue, attrId, attrTypeId, artModType, isHistorical(), loadArtifact());
   }
}
