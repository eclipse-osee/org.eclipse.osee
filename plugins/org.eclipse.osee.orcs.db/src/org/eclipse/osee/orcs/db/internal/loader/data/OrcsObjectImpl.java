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
public abstract class OrcsObjectImpl implements HasLocalId {

   private int localId = RelationalConstants.DEFAULT_ITEM_ID;

   protected OrcsObjectImpl() {
      super();
   }

   @Override
   public int getLocalId() {
      return localId;
   }

   public void setLocalId(int localId) {
      this.localId = localId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + localId;
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
      if (localId != other.localId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "OrcsObject [localId=" + localId + "]";
   }
}
