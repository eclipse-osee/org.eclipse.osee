/*
 * Created on Oct 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Megumi Telles
 */
public class ErrorChange extends Change {
   final static String ERROR_STRING = "!Error - ";
   private String exception = "";

   public ErrorChange(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionRecord toTransactionId, TransactionRecord fromTransactionId, ModificationType modType, ChangeType changeType, boolean isHistorical, Artifact artifact) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      super(branch, artifactType, sourceGamma, artId, toTransactionId, fromTransactionId, modType, changeType,
            isHistorical, artifact);
   }

   public ErrorChange(Branch branch, int artId, String exception) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      this(branch, null, 0, artId, null, null, null, ChangeType.OUTGOING, false, null);
      this.exception = exception;
   }

   @Override
   public String getIsValue() {
      return ERROR_STRING + exception;
   }

   @Override
   public int getItemId() {
      return 0;
   }

   @Override
   public String getItemKind() {
      return ERROR_STRING + exception;
   }

   @Override
   public int getItemTypeId() {
      return 0;
   }

   @Override
   public String getItemTypeName() throws Exception {
      return ERROR_STRING + exception;
   }

   @Override
   public String getName() throws IllegalArgumentException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return ERROR_STRING + "ArtID: " + getArtId() + " BranchId: " + (getBranch() == null ? null : getBranch().getId()) + ": " + exception;
   }

   @Override
   public String getWasValue() {
      return ERROR_STRING + exception;
   }

   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

}
