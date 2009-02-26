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
package org.eclipse.osee.framework.skynet.core.revision;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class HistoryTransactionItem implements IAdaptable{
   /**
    * @return the transactionData
    */
   public TransactionData getTransactionData() {
      return transactionData;
   }

   private TransactionData transactionData;
   private RevisionChange revisionChange;

   public HistoryTransactionItem(TransactionData transactionData, RevisionChange revisionChange) {
      super();
      this.transactionData = transactionData;
      this.revisionChange = revisionChange;
   }

   public int getTransactionNumber() {
      return transactionData.getTransactionNumber();
   }

   public long getGamma() {
      return revisionChange.getGammaId();
   }

   public String getChangeType() {
      String returnValue = "";
      if (revisionChange instanceof IAttributeChange) {
         returnValue = ((IAttributeChange) revisionChange).getName();
      } else if (revisionChange instanceof RelationLinkChange) {
         returnValue = ((RelationLinkChange) revisionChange).getRelTypeName();
      }else if (revisionChange instanceof ArtifactChange) {
         returnValue = ((ArtifactChange) revisionChange).getChange();
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

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      return transactionData.getAdapter(adapter);
   }
}
