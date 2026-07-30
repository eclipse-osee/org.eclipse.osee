/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.model.change.ChangeItem;

/**
 * Response DTO for the paginated artifact history endpoint. Contains the change items along with transaction metadata
 * (date, comment) keyed by transaction ID string.
 */
public class ArtifactHistoryResult {

   private List<ChangeItem> changes;
   private Map<String, TransactionInfo> transactions;

   public ArtifactHistoryResult() {
      this.changes = Collections.emptyList();
      this.transactions = Collections.emptyMap();
   }

   public ArtifactHistoryResult(List<ChangeItem> changes, Map<String, TransactionInfo> transactions) {
      this.changes = changes;
      this.transactions = transactions;
   }

   public List<ChangeItem> getChanges() {
      return changes;
   }

   public void setChanges(List<ChangeItem> changes) {
      this.changes = changes;
   }

   public Map<String, TransactionInfo> getTransactions() {
      return transactions;
   }

   public void setTransactions(Map<String, TransactionInfo> transactions) {
      this.transactions = transactions;
   }

   /**
    * Lightweight transaction metadata included alongside change items.
    */
   public static class TransactionInfo {
      private String id;
      private String comment;
      private long timestamp;

      public TransactionInfo() {}

      public TransactionInfo(String id, String comment, long timestamp) {
         this.id = id;
         this.comment = comment;
         this.timestamp = timestamp;
      }

      public String getId() {
         return id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getComment() {
         return comment;
      }

      public void setComment(String comment) {
         this.comment = comment;
      }

      public long getTimestamp() {
         return timestamp;
      }

      public void setTimestamp(long timestamp) {
         this.timestamp = timestamp;
      }
   }
}
