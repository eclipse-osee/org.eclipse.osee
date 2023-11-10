/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TxCurrent;

/**
 * @author Roberto E. Escobar
 */
public interface VersionData extends Cloneable {

   GammaId getGammaId();

   void setGammaId(GammaId gamma);

   TxCurrent getTxCurrent();

   void setTxCurrent(TxCurrent txCurrent);
   
   TransactionId getTransactionId();

   void setTransactionId(TransactionId txId);

   TransactionId getStripeId();

   void setStripeId(TransactionId stripeId);

   BranchId getBranch();

   void setBranch(BranchId branch);

   boolean isInStorage();

   boolean isHistorical();

   void setHistorical(boolean historical);

   VersionData clone();

}