/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public abstract class OrcsObjectImpl<T extends Number> implements HasLocalId<T> {

   private T localId = null;

   protected OrcsObjectImpl() {
      super();
      setLocalId((T) RelationalConstants.DEFAULT_ITEM_ID);
   }

   @Override
   public T getLocalId() {
      return localId;
   }

   public void setLocalId(T localId) {
      this.localId = localId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + localId.intValue();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      OrcsObjectImpl other = (OrcsObjectImpl) obj;
      if (localId.equals(other.localId)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "OrcsObject [localId=" + localId + "]";
   }
}
