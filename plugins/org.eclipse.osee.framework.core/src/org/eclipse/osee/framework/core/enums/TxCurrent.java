/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.core.enums;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

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