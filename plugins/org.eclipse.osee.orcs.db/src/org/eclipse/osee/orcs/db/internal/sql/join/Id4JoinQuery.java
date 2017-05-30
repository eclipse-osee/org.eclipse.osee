/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.sql.join;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Morgan E. Cook
 */
public class Id4JoinQuery extends AbstractJoinQuery {

   private final int maxJoinSize;

   private final class Entry implements IJoinRow {
      private final Id id_1;
      private final Id id_2;
      private final Id id_3;
      private final Id id_4;

      private Entry(Id id_1, Id id_2, Id id_3, Id id_4) {
         this.id_1 = id_1;
         this.id_2 = id_2;
         this.id_3 = id_3;
         this.id_4 = id_4;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), id_1, id_2, id_3, id_4};
      }

      @Override
      public String toString() {
         return String.format("id_1=%s, id_2=%s, id_3=%s, id_4=%s", id_1, id_2, id_3, id_4);
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
         Entry other = (Entry) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (id_1 == null) {
            if (other.id_1 != null) {
               return false;
            }
         } else if (!id_1.equals(other.id_1)) {
            return false;
         }
         if (id_2 == null) {
            if (other.id_2 != null) {
               return false;
            }
         } else if (!id_2.equals(other.id_2)) {
            return false;
         }
         if (id_3 == null) {
            if (other.id_3 != null) {
               return false;
            }
         } else if (!id_3.equals(other.id_3)) {
            return false;
         }
         if (!id_4.equals(other.id_4)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (id_1 == null ? 0 : id_1.hashCode());
         result = prime * result + (id_2 == null ? 0 : id_2.hashCode());
         result = prime * result + (id_3 == null ? 0 : id_3.hashCode());
         result = prime * result + (id_4 == null ? 0 : id_4.hashCode());
         return result;
      }

      private Id4JoinQuery getOuterType() {
         return Id4JoinQuery.this;
      }
   }

   public Id4JoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId, int maxJoinSize) {
      super(joinAccessor, JoinItem.ID4, expiresIn, queryId);
      this.maxJoinSize = maxJoinSize;
   }

   public void add(Id id_1, Id id_2, Id id_3, Id id_4) {
      entries.add(new Entry(id_1, id_2, id_3, id_4));
      if (entries.size() > maxJoinSize) {
         throw new OseeDataStoreException("Exceeded max artifact join size of [%d]", maxJoinSize);
      }
   }

   public void add(Id id_1, Id id_2, Id id_3) {
      add(id_1, id_2, id_3, ArtifactId.SENTINEL);

   }

   public void add(Id id_1, Id id_2) {
      add(id_1, id_2, TransactionId.SENTINEL, ArtifactId.SENTINEL);
   }
}
