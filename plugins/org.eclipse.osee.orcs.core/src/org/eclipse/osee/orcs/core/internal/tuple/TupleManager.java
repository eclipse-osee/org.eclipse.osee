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

import org.eclipse.osee.orcs.core.internal.transaction.TxData;

/**
 * @author Angel Avila
 */
public interface TupleManager {

   Long addTupple2(TxData txData, Long branchId, Long tupleTypeId, Long e1, Long e2);

   Long addTupple3(TxData txData, Long branchId, Long tupleTypeId, Long e1, Long e2, Long e3);

   Long addTupple4(TxData txData, Long branchId, Long tupleTypeId, Long e1, Long e2, Long e3, Long e4);

}
