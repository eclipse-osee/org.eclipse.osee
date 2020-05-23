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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Angel Avila
 */
public interface TupleObjectFactory extends VersionObjectFactory {

   TupleData createTuple2Data(VersionData version, BranchId branch, Tuple2Type<?, ?> tupleType, Long e1, Long e2);

   TupleData createTuple3Data(VersionData version, BranchId branch, Tuple3Type<?, ?, ?> tupleType, Long e1, Long e2, Long e3);

   TupleData createTuple4Data(VersionData version, BranchId branch, Tuple4Type<?, ?, ?, ?> tupleType, Long e1, Long e2, Long e3, Long e4);

}