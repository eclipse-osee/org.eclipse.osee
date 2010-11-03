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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public final class IdJoinQuery extends AbstractJoinQuery {

   private final class TempIdEntry implements IJoinRow {
      private final int id;

      private TempIdEntry(Integer id) {
         this.id = id;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), id};
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof TempIdEntry)) {
            return false;
         }
         TempIdEntry other = (TempIdEntry) obj;
         return other.id == this.id;
      }

      @Override
      public int hashCode() {
         return 37 * id;
      }

      @Override
      public String toString() {
         return "id = " + id;
      }
   }

   protected IdJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.ID, queryId);
   }

   public void add(Integer id) {
      entries.add(new TempIdEntry(id));
   }
}