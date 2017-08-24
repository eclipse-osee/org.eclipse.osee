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
public interface Tuple3Type<E1, E2, E3> extends TupleTypeId {

   public static <E1, E2, E3> Tuple3Type<E1, E2, E3> valueOf(TupleFamilyId family, Long tupleTypeId) {
      final class Tuple3TypeImpl extends TupleTypeImpl implements Tuple3Type<E1, E2, E3> {
         public Tuple3TypeImpl(TupleFamilyId family, Long tupleTypeId) {
            super(family, tupleTypeId);
         }

         public Tuple3TypeImpl(Long tupleTypeId) {
            super(tupleTypeId);
         }
      }
      return new Tuple3TypeImpl(family, tupleTypeId);
   }

   public static <E1, E2, E3> Tuple3Type<E1, E2, E3> valueOf(Long tupleTypeId) {
      return valueOf(DefaultFamily, tupleTypeId);
   }
}