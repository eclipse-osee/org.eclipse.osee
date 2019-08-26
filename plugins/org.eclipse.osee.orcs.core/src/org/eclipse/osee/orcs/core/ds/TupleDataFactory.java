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