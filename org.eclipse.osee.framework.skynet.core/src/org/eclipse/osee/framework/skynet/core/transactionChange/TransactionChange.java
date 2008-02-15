/*
 * Created on Feb 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.transactionChange;

import java.util.logging.Logger;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public abstract class TransactionChange implements IAdaptable {
   protected static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TransactionChange.class);
   private TransactionType transactionType;
   private ChangeType changeType;
   private TransactionId toTransactionId;
   private TransactionId fromTransactionId;

   /**
    * @param transactionType
    * @param changeType
    * @param toTransactionId
    * @param fromTransactionId
    */
   public TransactionChange(TransactionType transactionType, ChangeType changeType, TransactionId toTransactionId, TransactionId fromTransactionId) {
      super();
      this.transactionType = transactionType;
      this.changeType = changeType;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IRevisionChange#getImage()
    */
   public abstract Image getImage();

   /**
    * @return Returns the changeType.
    */
   public ChangeType getChangeType() {
      return changeType;
   }

   /**
    * @return the transactionType
    */
   public TransactionType getTransactionType() {
      return transactionType;
   }

   /**
    * @return the toTransactionId
    */
   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   /**
    * @return the fromTransactionId
    */
   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }
}
