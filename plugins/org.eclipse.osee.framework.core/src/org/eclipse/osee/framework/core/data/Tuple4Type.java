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

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.DefaultFamily;

/**
 * @author Ryan D. Brooks
 */
public interface Tuple4Type<E1, E2, E3, E4> extends TupleTypeId {

   public static <E1, E2, E3, E4> Tuple4Type<E1, E2, E3, E4> valueOf(TupleFamilyId family, Long tupleTypeId) {
      final class Tuple4TypeImpl extends TupleTypeImpl implements Tuple4Type<E1, E2, E3, E4> {
         public Tuple4TypeImpl(TupleFamilyId family, Long tupleTypeId) {
            super(family, tupleTypeId);
         }

         public Tuple4TypeImpl(Long tupleTypeId) {
            super(tupleTypeId);
         }
      }
      return new Tuple4TypeImpl(family, tupleTypeId);
   }

   public static <E1, E2, E3, E4> Tuple4Type<E1, E2, E3, E4> valueOf(Long tupleTypeId) {
      return valueOf(DefaultFamily, tupleTypeId);
   }
}