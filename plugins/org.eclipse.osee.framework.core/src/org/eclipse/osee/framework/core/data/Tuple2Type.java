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

package org.eclipse.osee.framework.core.data;

import java.util.function.Function;

/**
 * @author Ryan D. Brooks
 */
public interface Tuple2Type<E1, E2> extends TupleTypeId {

   Function<Long, E1> getValueOfE1();

   Function<Long, E2> getValueOfE2();

   public static <E1, E2> Tuple2Type<E1, E2> valueOf(TupleFamilyId family, Long tupleTypeId, Function<Long, E1> valueOfE1, Function<Long, E2> valueOfE2) {
      final class Tuple2TypeImpl extends TupleTypeImpl implements Tuple2Type<E1, E2> {
         private final Function<Long, E1> valueOfE1;
         private final Function<Long, E2> valueOfE2;

         public Tuple2TypeImpl(TupleFamilyId family, Long tupleTypeId, Function<Long, E1> valueOfE1, Function<Long, E2> valueOfE2) {
            super(family, tupleTypeId);
            this.valueOfE1 = valueOfE1;
            this.valueOfE2 = valueOfE2;
         }

         @Override
         public Function<Long, E1> getValueOfE1() {
            return valueOfE1;
         }

         @Override
         public Function<Long, E2> getValueOfE2() {
            return valueOfE2;
         }

         @Override
         public Long getId() {
            return tupleTypeId;
         }
      }
      return new Tuple2TypeImpl(family, tupleTypeId, valueOfE1, valueOfE2);
   }
}