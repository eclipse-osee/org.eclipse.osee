/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public interface VersionData extends Cloneable {

   long getGammaId();

   void setGammaId(long gamma);

   int getTransactionId();

   void setTransactionId(int txId);

   int getStripeId();

   void setStripeId(int stripeId);

   long getBranchId();

   void setBranchId(long branchUuid);

   boolean isInStorage();

   boolean isHistorical();

   void setHistorical(boolean historical);

   VersionData clone();

}