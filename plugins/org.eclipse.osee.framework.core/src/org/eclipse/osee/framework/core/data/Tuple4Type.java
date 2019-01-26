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
package org.eclipse.osee.framework.core.data;

import java.util.function.Function;

/**
 * @author Ryan D. Brooks
 */
public interface Tuple4Type<E1, E2, E3, E4> extends TupleTypeId {

   Function<Long, E1> getValueOfE1();

   Function<Long, E2> getValueOfE2();

   Function<Long, E3> getValueOfE3();

   Function<Long, E4> getValueOfE4();

   public static <E1, E2, E3, E4> Tuple4Type<E1, E2, E3, E4> valueOf(TupleFamilyId family, Long tupleTypeId, Function<Long, E1> valueOfE1, Function<Long, E2> valueOfE2, Function<Long, E3> valueOfE3, Function<Long, E4> valueOfE4) {
      final class Tuple4TypeImpl extends TupleTypeImpl implements Tuple4Type<E1, E2, E3, E4> {
         private final Function<Long, E1> valueOfE1;
         private final Function<Long, E2> valueOfE2;
         private final Function<Long, E3> valueOfE3;
         private final Function<Long, E4> valueOfE4;

         public Tuple4TypeImpl(TupleFamilyId family, Long tupleTypeId, Function<Long, E1> valueOfE1, Function<Long, E2> valueOfE2, Function<Long, E3> valueOfE3, Function<Long, E4> valueOfE4) {
            super(family, tupleTypeId);
            this.valueOfE1 = valueOfE1;
            this.valueOfE2 = valueOfE2;
            this.valueOfE3 = valueOfE3;
            this.valueOfE4 = valueOfE4;
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
         public Function<Long, E3> getValueOfE3() {
            return valueOfE3;
         }

         @Override
         public Function<Long, E4> getValueOfE4() {
            return valueOfE4;
         }
      }
      return new Tuple4TypeImpl(family, tupleTypeId, valueOfE1, valueOfE2, valueOfE3, valueOfE4);
   }
}