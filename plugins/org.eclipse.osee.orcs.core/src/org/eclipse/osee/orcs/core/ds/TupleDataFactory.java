/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;

/**
 * @author Angel Avila
 */
public interface TupleDataFactory {

   TupleData createTuple2Data(Tuple2Type<?, ?> tupleType, BranchId branch, Long e1, Long e2);

   TupleData createTuple3Data(Tuple3Type<?, ?, ?> tupleType, BranchId branch, Long e1, Long e2, Long e3);

   TupleData createTuple4Data(Tuple4Type<?, ?, ?, ?> tupleType, BranchId branch, Long e1, Long e2, Long e3, Long e4);

   TupleData introduceTupleData(TupleTypeId tupleType, GammaId tupleGamma);

}