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

/**
 * @author Roberto E. Escobar
 */
public final class ExportImportJoinQuery extends AbstractJoinQuery {

   private final class ExportImportEntry implements IJoinRow {
      private final Long id1;
      private final Long id2;

      private ExportImportEntry(Long id1, Long id2) {
         this.id1 = id1;
         this.id2 = id2;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), id1, id2};
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
         ExportImportEntry other = (ExportImportEntry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (id1 == null) {
            if (other.id1 != null) {
               return false;
            }
         } else if (!id1.equals(other.id1)) {
            return false;
         }
         if (id2 == null) {
            if (other.id2 != null) {
               return false;
            }
         } else if (!id2.equals(other.id2)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (id1 == null ? 0 : id1.hashCode());
         result = prime * result + (id2 == null ? 0 : id2.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return String.format("id1=%s id2=%s", id1, id2);
      }

      private ExportImportJoinQuery getOuterType() {
         return ExportImportJoinQuery.this;
      }
   }

   protected ExportImportJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId) {
      super(joinAccessor, JoinItem.EXPORT_IMPORT, expiresIn, queryId);
   }

   public void add(Long id1, Long id2) {
      entries.add(new ExportImportEntry(id1, id2));
   }
}