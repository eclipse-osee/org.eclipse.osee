/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxIds extends Criteria implements TxCriteria {

   private final Collection<TransactionId> ids;

   public CriteriaTxIds(Collection<TransactionId> ids) {
      this.ids = ids;
   }

   public CriteriaTxIds(TransactionId txId) {
      List<TransactionId> ids = new ArrayList<>(1);
      ids.add(txId);
      this.ids = ids;
   }

   @Override
   public void checkValid(Options options)  {
      Conditions.checkNotNullOrEmpty(ids, "tx ids");
   }

   public Collection<TransactionId> getIds() {
      return ids;
   }

   @Override
   public String toString() {
      return "CriteriaTxIds [ids=" + ids + "]";
   }
}
