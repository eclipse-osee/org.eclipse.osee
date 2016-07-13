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
package org.eclipse.osee.orcs.search;

import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Tuple2Type;

/**
 * @author Angel Avila
 */
public interface TupleQuery {

   <E1, E2> Iterable<E2> getTuple2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1);

   <E1, E2> Iterable<Long> getTuple2Raw(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1);

   <E1, E2> void getTuple2NamedId(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1, BiConsumer<Long, String> consumer);

   /**
    * Get each unique E2 (Id and associated String value) for a particular TupleType regardless of E1
    *
    * @return Iterable of E2 as Id and associated String value Object
    */
   <E1, E2> void getTuple2UniqueE2Pair(Tuple2Type<E1, E2> tupleType, BranchId branchId, BiConsumer<Long, String> consumer);
}