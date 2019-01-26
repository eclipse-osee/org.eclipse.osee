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
import org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Ryan D. Brooks
 */
public class TupleTypeImpl extends BaseId implements TupleTypeToken {
   public static final Function<Long, String> KeyedString = l -> "";
   private final TupleFamilyId family;

   public TupleTypeImpl(Long tupleTypeId) {
      this(CoreTupleFamilyTypes.DefaultFamily, tupleTypeId);
   }

   public TupleTypeImpl(TupleFamilyId family, Long tupleTypeId) {
      super(tupleTypeId);
      this.family = family;
   }

   @Override
   public TupleFamilyId getFamily() {
      return family;
   }
}