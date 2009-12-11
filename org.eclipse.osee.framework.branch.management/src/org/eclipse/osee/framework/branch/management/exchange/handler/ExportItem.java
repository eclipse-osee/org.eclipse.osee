/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.handler;

/**
 * @author Ryan D. Brooks
 */
public enum ExportItem implements IExportItem {
   EXPORT_MANIFEST(""),
   EXPORT_DB_SCHEMA("db.metadata"),
   OSEE_BRANCH_DATA("osee_branch"),
   OSEE_TX_DETAILS_DATA("osee_tx_details"),
   OSEE_TXS_DATA("osee_txs"),
   OSEE_ARTIFACT_DATA("osee_artifact"),
   OSEE_ARTIFACT_VERSION_DATA("osee_artifact_version"),
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

   public String getFileName() {
      return this + ".xml";
   }

   public String getSource() {
      return source;
   }

   public int getPriority() {
      return ordinal();
   }

}