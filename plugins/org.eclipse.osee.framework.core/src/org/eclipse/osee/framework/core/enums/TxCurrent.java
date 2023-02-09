/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface TxCurrent extends Id {

   public static final TxCurrent SENTINEL = internalCreate(Id.SENTINEL);
   public static final TxCurrent NOT_CURRENT = internalCreate(0L);
   public static final TxCurrent CURRENT = internalCreate(1L);
   public static final TxCurrent DELETED = internalCreate(2L);
   public static final TxCurrent ARTIFACT_DELETED = internalCreate(3L);

   public static TxCurrent valueOf(int id) {
      switch (id) {
         case 0:
            return NOT_CURRENT;
         case 1:
            return CURRENT;
         case 2:
            return DELETED;
         case 3:
            return ARTIFACT_DELETED;
         default:
            return SENTINEL;
      }
   }

   /**
    * This method is only public because all methods in an interface are and it should never be called outside of this
    * interface
    */
   @SuppressWarnings("ComparableType")
   public static TxCurrent internalCreate(Long id) {
      final class TxChangeImpl extends BaseId implements TxCurrent, Comparable<TxCurrent> {
         public TxChangeImpl(Long id) {
            super(id);
         }

         @Override
         public int compareTo(TxCurrent o) {
            return getId().compareTo(o.getId());
         }
      }
      return new TxChangeImpl(id);
   }

   public static TxCurrent getCurrent(ModificationType type) {
      TxCurrent txChange;

      if (type == ModificationType.DELETED) {
         txChange = TxCurrent.DELETED;
      } else if (type == ModificationType.ARTIFACT_DELETED) {
         txChange = TxCurrent.ARTIFACT_DELETED;
      } else {
         txChange = TxCurrent.CURRENT;
      }
      return txChange;
   }

   default boolean isDeleted() {
      return this == DELETED || this == ARTIFACT_DELETED;
   }

   default boolean isCurrent() {
      return this != TxCurrent.NOT_CURRENT;
   }
}