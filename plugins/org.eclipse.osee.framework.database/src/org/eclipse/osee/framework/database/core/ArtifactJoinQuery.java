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
public class ArtifactJoinQuery extends AbstractJoinQuery {

   private final class Entry implements IJoinRow {
      private final Integer artId;
      private final Long branchId;
      private final Integer transactionId;

      private Entry(Integer artId, Long branchId, Integer transactionId) {
         this.artId = artId;
         this.branchId = branchId;
         this.transactionId = transactionId;
      }

      @Override
      public Object[] toArray() {
         return new Object[] {getQueryId(), getInsertTime(), artId, branchId, transactionId};
      }

      @Override
      public String toString() {
         return String.format("art_id=%s, branch_id=%s, transaction_id=%s", artId, branchId, transactionId);
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
         if (branchId == null) {
            if (other.branchId != null) {
               return false;
            }
         } else if (!branchId.equals(other.branchId)) {
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
         result = prime * result + ((artId == null) ? 0 : artId.hashCode());
         result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
         result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
         return result;
      }

      private ArtifactJoinQuery getOuterType() {
         return ArtifactJoinQuery.this;
      }
   }

   protected ArtifactJoinQuery(IJoinAccessor joinAccessor, int queryId) {
      super(joinAccessor, JoinItem.ARTIFACT, queryId);
   }

   public void add(Integer art_id, Long branchId, Integer transactionId) {
      entries.add(new Entry(art_id, branchId, transactionId));
   }

}