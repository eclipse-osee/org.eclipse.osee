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
public final class ExportImportJoinQuery extends AbstractJoinQuery {

   private final class ExportImportEntry implements IJoinRow {
      private final long id1;
      private final long id2;

      private ExportImportEntry(Long id1, Long id2) {
         this.id1 = id1;
         this.id2 = id2;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), id1, id2};
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof ExportImportEntry)) {
            return false;
         }
         ExportImportEntry other = (ExportImportEntry) obj;
         return this.id1 == other.id1 && this.id2 == other.id2;
      }

      @Override
      public int hashCode() {
         return Long.valueOf(37 * id1 * id2).hashCode();
      }

      @Override
      public String toString() {
         return String.format("id1=%s id2=%s", id1, id2);
      }
   }

   protected ExportImportJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.EXPORT_IMPORT, queryId);
   }

   public void add(Long id1, Long id2) {
      entries.add(new ExportImportEntry(id1, id2));
   }
}