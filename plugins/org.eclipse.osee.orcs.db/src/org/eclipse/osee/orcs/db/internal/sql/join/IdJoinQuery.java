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
package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public class IdJoinQuery extends AbstractJoinQuery {

   private final class TempIdEntry implements IJoinRow {
      private final Long id;

      private TempIdEntry(Long id) {
         this.id = id;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), id};
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
         TempIdEntry other = (TempIdEntry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (id == null) {
            if (other.id != null) {
               return false;
            }
         } else if (!id.equals(other.id)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (id == null ? 0 : id.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return "id = " + id;
      }

      private IdJoinQuery getOuterType() {
         return IdJoinQuery.this;
      }
   }

   public IdJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.ID, expiresIn, queryId);
   }

   public void add(Number id) {
      entries.add(new TempIdEntry(id.longValue()));
   }

   public void add(Id id) {
      entries.add(new TempIdEntry(id.getId()));
   }
}