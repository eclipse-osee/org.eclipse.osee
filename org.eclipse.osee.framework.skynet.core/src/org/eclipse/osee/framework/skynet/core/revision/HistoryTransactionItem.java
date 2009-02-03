/*
 * Created on Feb 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision;

import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class HistoryTransactionItem {
   private TransactionData transactionData;
   private RevisionChange revisionChange;

   public HistoryTransactionItem(TransactionData transactionData, RevisionChange revisionChange) {
      super();
      this.transactionData = transactionData;
      this.revisionChange = revisionChange;
   }

   public int getTransactionId() {
      return transactionData.getTransactionNumber();
   }

   public long getGamma() {
      return revisionChange.getGammaId();
   }

   public String changeType() {
      String returnValue = "";
      if (revisionChange instanceof IAttributeChange) {
         returnValue = ((IAttributeChange) revisionChange).getName();
      } else if (revisionChange instanceof RelationLinkChange) {
         returnValue = ((RelationLinkChange) revisionChange).getRelTypeName();
      }

      return returnValue;
   }

   public String getIsValue() {
      String returnValue = "";
      if (revisionChange instanceof IAttributeChange) {
         returnValue = ((IAttributeChange) revisionChange).getChange();
      } else if (revisionChange instanceof RelationLinkChange) {
         returnValue = ((RelationLinkChange) revisionChange).getOtherArtifactName();
      }

      return returnValue;
   }

   public String getWasValue() {
      String returnValue = "";
      if (revisionChange instanceof IAttributeChange) {
         returnValue = ((IAttributeChange) revisionChange).getWasValue();
      }

      return returnValue;
   }

   public String getAuthorName() {
      return transactionData.getName();
   }

   public String getTimeStamp() {
      return String.valueOf(transactionData.getTimeStamp());
   }

   public String getComment() {
      return transactionData.getComment();
   }

   public Image getChangeImage() {
      return revisionChange.getImage();
   }

   public Image getIsImage() {
      Image image = null;

      if (revisionChange instanceof RelationLinkChange) {
         ArtifactType type = ((RelationLinkChange) revisionChange).getOtherArtifactDescriptor();

         if (type != null) {
            image = type.getImage();
         }
      }
      return image;
   }
}
