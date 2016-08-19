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

import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.jdbc.SQL3DataType;
import org.eclipse.osee.orcs.db.internal.sql.join.DatabaseJoinAccessor.JoinItem;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactJoinQuery extends AbstractJoinQuery {

   private final int maxJoinSize;

   private final class Entry implements IJoinRow {
      private final Integer artId;
      private final Long branchUuid;
      private final TransactionId transactionId;

      private Entry(Integer artId, Long branchUuid, TransactionId transactionId) {
         this.artId = artId;
         this.branchUuid = branchUuid;
         this.transactionId = transactionId;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {
            getQueryId(),
            artId,
            branchUuid,
            transactionId != null ? transactionId : SQL3DataType.INTEGER};
      }

      @Override
      public String toString() {
         return String.format("art_id=%s, branch_id=%s, transaction_id=%s", artId, branchUuid, transactionId);
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
         if (artId == null) {
            if (other.artId != null) {
               return false;
            }
         } else if (!artId.equals(other.artId)) {
            return false;
         }
         if (branchUuid == null) {
            if (other.branchUuid != null) {
               return false;
            }
         } else if (!branchUuid.equals(other.branchUuid)) {
            return false;
         }
         if (transactionId == null) {
            if (other.transactionId != null) {
               return false;
            }
         } else if (!transactionId.equals(other.transactionId)) {
            return false;
         }
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (artId == null ? 0 : artId.hashCode());
         result = prime * result + (branchUuid == null ? 0 : branchUuid.hashCode());
         result = prime * result + (transactionId == null ? 0 : transactionId.hashCode());
         return result;
      }

      private ArtifactJoinQuery getOuterType() {
         return ArtifactJoinQuery.this;
      }
   }

   public ArtifactJoinQuery(IJoinAccessor joinAccessor, Long expiresIn, int queryId, int maxJoinSize) {
      super(joinAccessor, JoinItem.ARTIFACT, expiresIn, queryId);
      this.maxJoinSize = maxJoinSize;
   }

   public void add(Integer art_id, Long branchUuid, TransactionId transactionId) {
      entries.add(new Entry(art_id, branchUuid, transactionId));
      if (entries.size() > maxJoinSize) {
         throw new OseeDataStoreException("Exceeded max artifact join size of [%d]", maxJoinSize);
      }
   }
}