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

package org.eclipse.osee.orcs.search;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.jdk.core.type.TriConsumer;

/**
 * @author Angel Avila
 */
public interface TupleQuery {

   <E1, E2> Iterable<E2> getTuple2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1);

   <E1, E2> void getTuple2UniqueE1(Tuple2Type<E1, E2> tupleType, BranchId branchId, Consumer<E1> consumer);

   <E1, E2> Iterable<Long> getTuple2Raw(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1);

   /**
    * @param e2Raw - instead of using the Applicability Value as a string (which later gets resolved to an
    * ApplicabilityId, this fx takes the raw applicabilityId value
    * @return - returns a list of branchView Ids for which the passed in Applicability is valid
    */
   <E1, E2> Iterable<Long> getTuple2E1ListRaw(Tuple2Type<E1, E2> tupleType, BranchId branchId, Long e2Raw);

   <E1, E2> void getTuple2NamedId(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1,
      BiConsumer<Long, String> consumer);

   <E1, E2> void getTuple2KeyValuePair(Tuple2Type<E1, E2> tupleType, E1 e1, BranchId branch,
      BiConsumer<Long, String> consumer);

   <E1, E2> boolean doesTuple2Exist(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);

   <E1, E2, E3> boolean doesTuple3E3Exist(Tuple3Type<E1, E2, E3> tupleType, E3 e3);

   <E1, E2, E3, E4> boolean doesTuple4E3Exist(Tuple4Type<E1, E2, E3, E4> tupleType, E3 e3);

   /**
    * Get each unique E2 (Id and associated String value) for a particular TupleType regardless of E1
    *
    * @return Iterable of E2 as Id and associated String value Object
    */
   <E1, E2> void getTuple2UniqueE2Pair(Tuple2Type<E1, E2> tupleType, BranchId branchId,
      BiConsumer<Long, String> consumer);

   <E1, E2> void getTuple2UniqueE2Pair(Tuple2Type<E1, E2> tupleType, BranchId branchId, boolean orderByName,
      String filter, Long pageNum, Long pageSize, BiConsumer<Long, String> consumer);

   <E1, E2> Long getTuple2UniqueE2PairCount(Tuple2Type<E1, E2> tupleType, BranchId branchId, String filter);

   <E1, E2, E3> void getTuple3E1ValueFromType(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId,
      BiConsumer<E1, String> consumer);

   <E1, E2, E3> void getTuple3E3ValueFromType(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId,
      BiConsumer<E3, String> consumer);

   <E1, E2, E3> void getTuple3E3ValueFromE1(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, Long e1,
      BiConsumer<E3, String> consumer);

   <E1, E2, E3> void getTuple3GammaFromE1(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, Long e1,
      Consumer<GammaId> consumer);

   <E1, E2, E3> void getTuple3E2FromE1(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, E1 e1,
      Consumer<E2> consumer);

   <E1, E2, E3> void getTuple3E2FromE3(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, E3 e3,
      Consumer<E2> consumer);

   <E1, E2> void getTuple2E1E2FromType(Tuple2Type<E1, E2> tupleType, BranchId branchId,
      BiConsumer<Long, Long> consumer);

   <E1, E2, E3, E4> void getTuple4E3E4FromE1E2(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1, E2 e2,
      BiConsumer<E3, E4> consumer);

   <E1, E2, E3, E4> void getTuple4E2E3E4FromE1(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1,
      TriConsumer<E2, E3, E4> consumer);

   <E1, E2, E3, E4> void getTuple4E1FromTupleType(Tuple4Type<E1, E2, E3, E4> tupleType, Long tupleTypeId, Long e4,
      Consumer<E1> consumer);

   <E1, E2, E3, E4> void getTuple4GammaFromE1E2(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1, E2 e2,
      Consumer<GammaId> consumer);

   <E1, E2> void getTuple2GammaFromE1E2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1, E2 e2,
      Consumer<GammaId> consumer);

   <E1, E2> GammaId getTuple2GammaFromE1E2Raw(Tuple2Type<E1, E2> tupleType, E1 e1, Long e2Raw);

   /**
    * Return GammaId from Tuple2 without using BranchId. TupleType,E1,E2 is a primary key (unique)
    */
   <E1, E2> GammaId getTuple2GammaFromE1E2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2);
}