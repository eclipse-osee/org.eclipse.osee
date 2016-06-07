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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.TupleDataFactory;

/**
 * @author Angel Avila
 */
public class TupleFactory {

   private final TupleDataFactory tupleDataFactory;

   public TupleFactory(TupleDataFactory tupleDataFactory) {
      this.tupleDataFactory = tupleDataFactory;
   }

   public TupleData createTuple2(Long tupleTypeId, Long branchId, Long e1, Long e2) throws OseeCoreException {
      return tupleDataFactory.createTuple2Data(tupleTypeId, branchId, e1, e2);
   }

   public TupleData createTuple3(Long tupleTypeId, Long branchId, Long e1, Long e2, Long e3) throws OseeCoreException {
      return tupleDataFactory.createTuple3Data(tupleTypeId, branchId, e1, e2, e3);
   }

   public TupleData createTuple4(Long tupleTypeId, Long e1, Long branchId, Long e2, Long e3, Long e4) throws OseeCoreException {
      return tupleDataFactory.createTuple4Data(tupleTypeId, branchId, e1, e2, e3, e4);
   }

}
