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
package org.eclipse.osee.framework.db.connection.core.schema;

/**
 * Provides the ability to build the tables and sequences necessary for the Define tools to be able to work. This class
 * is only intended for installation purposes, and is not to support general runtime needs.
 * 
 * @author Robert A. Fisher
 */
public class SkynetDatabase {
   public static final int RELATION_SIDE_NAME_SIZE = 50;
   public static final int BRANCH_SHORT_NAME_SIZE = 25;

   public static final Table ARTIFACT_TABLE = new Table("OSEE_DEFINE_ARTIFACT");
   public static final Table ARTIFACT_VERSION_TABLE = new Table("OSEE_DEFINE_ARTIFACT_VERSION");
   public static final Table ARTIFACT_TABLE_ACL = new Table("OSEE_DEFINE_ARTIFACT_ACL");
   public static final Table TRANSACTIONS_TABLE = new Table("OSEE_DEFINE_TXS");
   public static final Table TRANSACTION_DETAIL_TABLE = new Table("OSEE_DEFINE_TX_DETAILS");
   public static final Table ARTIFACT_TYPE_TABLE = new Table("OSEE_DEFINE_ARTIFACT_TYPE");
   public static final Table ARTIFACT_TYPE_TABLE_ACL = new Table("OSEE_DEFINE_ARTIFACT_TYPE_ACL");
   public static final Table ATTRIBUTE_PROVIDER_TYPE_TABLE = new Table("OSEE_DEFINE_ATTR_PROVIDER_TYPE");
   public static final Table ATTRIBUTE_BASE_TYPE_TABLE = new Table("OSEE_DEFINE_ATTR_BASE_TYPE");
   public static final Table ATTRIBUTE_TABLE = new Table("OSEE_ATTRIBUTE"); // NEW TABLE WITH GUID AND ATTR_ID ONLY
   public static final Table ATTRIBUTE_VERSION_TABLE = new Table("OSEE_DEFINE_ATTRIBUTE");
   public static final Table ATTRIBUTE_TYPE_TABLE = new Table("OSEE_DEFINE_ATTRIBUTE_TYPE");
   public static final Table ATTRIBUTE_TYPE_TABLE_ACL = new Table("OSEE_DEFINE_ATTRIBUTE_TYPE_ACL");
   public static final Table RELATION_LINK_TABLE = new Table("OSEE_REL_LINK"); // NEW TABLE WITH GUID AND REL_LINK_ID ONLY
   public static final Table RELATION_LINK_VERSION_TABLE = new Table("OSEE_DEFINE_REL_LINK");
   public static final Table RELATION_LINK_TYPE_TABLE = new Table("OSEE_DEFINE_REL_LINK_TYPE");
   public static final Table RELATION_LINK_TYPE_TABLE_ACL = new Table("OSEE_DEFINE_REL_LINK_TYPE_ACL");
   public static final Table BRANCH_TABLE = new Table("OSEE_DEFINE_BRANCH");
   public static final Table BRANCH_DEFINITIONS = new Table("OSEE_BRANCH_DEFINITIONS");
   public static final Table BRANCH_TABLE_ACL = new Table("OSEE_DEFINE_BRANCH_ACL");
   public static final Table BRANCH_DELETE_HELPER = new Table("OSEE_BRANCH_DELETE_HELPER");
   public static final Table VALID_ATTRIBUTES_TABLE = new Table("OSEE_DEFINE_VALID_ATTRIBUTES");
   public static final Table PERMISSION_TABLE = new Table("OSEE_DEFINE_PERMISSION");
   public static final Table SEQUENCE_TABLE = new Table("OSEE_DEFINE_SEQUENCE");
   public static final Table OSEE_INFO_TABLE = new Table("OSEE_INFO");

   public static final Table DELETED_ARTIFACTS_TABLE = new Table("OSEE_DEFINE_DELETED_ARTIFACTS");
   public static final Table DELETED_ATTRIBUTES_TABLE = new Table("OSEE_DEFINE_DELETED_ATTRIBUTES");
   public static final Table DELETED_RELATION_LINKS_TABLE = new Table("OSEE_DEFINE_DELETED_LINKS");

   public static final Table SNAPSHOT_TABLE = new Table("OSEE_SNAPSHOT");
   public static final String TXD_COMMENT = "OSEE_COMMENT";

   public static final View CURRENT_VERSION_VIEW =
         new View(
               "CURRENT_VERSION_ARTIFACT",
               " AS" + " SELECT branch_id, MAX(transaction_id) AS largest_transaction_id FROM " + TRANSACTION_DETAIL_TABLE + " GROUP BY branch_id");

   private static final View[] skynetViews = new View[] {};

   public static View[] getSkynetViews() {
      return skynetViews;
   }
}
