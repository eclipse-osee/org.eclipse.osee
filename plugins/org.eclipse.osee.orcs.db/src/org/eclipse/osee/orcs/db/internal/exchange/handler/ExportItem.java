/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.handler;

/**
 * @author Ryan D. Brooks
 */
public enum ExportItem implements IExportItem {
   EXPORT_MANIFEST(""),
   EXPORT_TYPE_MODEL("osee_type_model"),
   EXPORT_DB_SCHEMA("db.metadata"),
   OSEE_BRANCH_DATA("osee_branch"),
   OSEE_TX_DETAILS_DATA("osee_tx_details"),
   OSEE_TXS_DATA("osee_txs"),
   OSEE_TXS_ARCHIVED_DATA("osee_txs_archived"),
   OSEE_ARTIFACT_DATA("osee_artifact"),
   OSEE_ATTRIBUTE_DATA("osee_attribute"),
   OSEE_RELATION_LINK_DATA("osee_relation_link"),
   OSEE_MERGE_DATA("osee_merge"),
   OSEE_CONFLICT_DATA("osee_conflict"),
   OSEE_BRANCH_ACL_DATA("osee_branch_acl"),
   OSEE_ARTIFACT_ACL_DATA("osee_artifact_acl");

   private final String source;

   private ExportItem(String source) {
      this.source = source;
   }

   @Override
   public String toString() {
      return this.name().toLowerCase().replace('_', '.');
   }

   @Override
   public String getFileName() {
      String extension = this != EXPORT_TYPE_MODEL ? ".xml" : ".osee";
      return this + extension;
   }

   @Override
   public String getSource() {
      return source;
   }

   @Override
   public int getPriority() {
      return ordinal();
   }

   public boolean matches(ExportItem... exportItems) {
      for (ExportItem exportItem : exportItems) {
         if (this == exportItem) {
            return true;
         }
      }
      return false;
   }
}