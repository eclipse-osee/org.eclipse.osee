/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.tuple;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.internal.transaction.TxData;

/**
 * @author Angel Avila
 */
public class TupleMangerImpl implements TupleManager {
   private final TupleFactory tupleFactory;

   public TupleMangerImpl(TupleFactory tupleFactory) {
      super();
      this.tupleFactory = tupleFactory;
   }

   @Override
   public Long addTupple2(TxData txData, BranchId branch, Long tupleTypeId, Long e1, Long e2) {
      TupleData tuple = tupleFactory.createTuple2(tupleTypeId, branch, e1, e2);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

   @Override
   public Long addTupple3(TxData txData, BranchId branch, Long tupleTypeId, Long e1, Long e2, Long e3) {
      TupleData tuple = tupleFactory.createTuple3(tupleTypeId, branch, e1, e2, e3);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

   @Override
   public Long addTupple4(TxData txData, BranchId branch, Long tupleTypeId, Long e1, Long e2, Long e3, Long e4) {
      TupleData tuple = tupleFactory.createTuple4(tupleTypeId, branch, e1, e2, e3, e4);
      txData.add(tuple);
      return tuple.getVersion().getGammaId();
   }

}
