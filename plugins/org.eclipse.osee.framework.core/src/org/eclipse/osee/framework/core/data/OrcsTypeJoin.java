/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.util.Arrays;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypeJoin<J, T extends Id> extends NamedIdBase {
   private final T[] types;
   private final Tuple2Type<J, T> tupleType;

   public OrcsTypeJoin(Tuple2Type<J, T> tupleType, String name, T[] types) {
      super(Arrays.hashCode(types), name);
      this.tupleType = tupleType;
      this.types = types;
   }

   public Tuple2Type<J, T> getTupleType() {
      return tupleType;
   }

   public T[] getTypes() {
      return types;
   }
}