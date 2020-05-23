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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface TupleFamilyId extends Id {

   public static TupleFamilyId valueOf(Long tupleFamilyTypeId) {
      final class TupleFailyTypeImpl extends BaseId implements TupleFamilyId {
         public TupleFailyTypeImpl(Long tupleFamilyTypeId) {
            super(tupleFamilyTypeId);
         }
      }
      return new TupleFailyTypeImpl(tupleFamilyTypeId);
   }
}