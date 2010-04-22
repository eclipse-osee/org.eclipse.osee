/*
 * Created on Oct 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.ArtifactType;

/**
 * @author Megumi Telles
 */
public class ErrorChange extends Change {
   final static String ERROR_STRING = "!Error - ";
   private String exception = "";

   public ErrorChange(IOseeBranch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical, ArtifactDelta artifactDelta) {
      super(branch, artifactType, sourceGamma, artId, txDelta, modType, isHistorical, artifactDelta);
   }

   public ErrorChange(IOseeBranch branch, int artId, String exception) {
      this(branch, null, 0, artId, null, null, false, null);
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
   public String getItemTypeName() {
      return ERROR_STRING + exception;
   }

   @Override
   public String getName() {
      return ERROR_STRING + "ArtID: " + getArtId() + " BranchGuid: " + (getBranch() == null ? null : getBranch().getGuid()) + ": " + exception;
   }

   @Override
   public String getWasValue() {
      return ERROR_STRING + exception;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

}
