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

package org.eclipse.osee.framework.skynet.core.transactionChange;

import java.util.logging.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionType;
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
