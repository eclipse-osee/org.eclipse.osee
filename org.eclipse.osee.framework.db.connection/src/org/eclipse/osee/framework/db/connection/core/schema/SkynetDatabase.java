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

   public static final Table ARTIFACT_TABLE = new Table("OSEE_ARTIFACT");
   public static final Table ARTIFACT_VERSION_TABLE = new Table("OSEE_ARTIFACT_VERSION");
   public static final Table ARTIFACT_TABLE_ACL = new Table("OSEE_ARTIFACT_ACL");
   public static final Table TRANSACTIONS_TABLE = new Table("OSEE_TXS");
   public static final Table REMOVED_TRANSACTIONS_TABLE = new Table("OSEE_REMOVED_TXS");
   public static final Table TRANSACTION_DETAIL_TABLE = new Table("OSEE_TX_DETAILS");
   public static final Table ARTIFACT_TYPE_TABLE = new Table("OSEE_ARTIFACT_TYPE");
   public static final Table ARTIFACT_TYPE_TABLE_ACL = new Table("OSEE_ARTIFACT_TYPE_ACL");
   public static final Table ATTRIBUTE_PROVIDER_TYPE_TABLE = new Table("OSEE_ATTRIBUTE_PROVIDER_TYPE");
   public static final Table ATTRIBUTE_BASE_TYPE_TABLE = new Table("OSEE_ATTRIBUTE_BASE_TYPE");
   public static final Table ATTRIBUTE_VERSION_TABLE = new Table("OSEE_ATTRIBUTE");
   public static final Table ATTRIBUTE_TYPE_TABLE = new Table("OSEE_ATTRIBUTE_TYPE");
   public static final Table ATTRIBUTE_TYPE_TABLE_ACL = new Table("OSEE_ATTRIBUTE_TYPE_ACL");
   public static final Table RELATION_LINK_VERSION_TABLE = new Table("OSEE_RELATION_LINK");
   public static final Table RELATION_LINK_TYPE_TABLE = new Table("OSEE_RELATION_LINK_TYPE");
   public static final Table RELATION_LINK_TYPE_TABLE_ACL = new Table("OSEE_RELATION_LINK_TYPE_ACL");
   public static final Table BRANCH_TABLE = new Table("OSEE_BRANCH");
   public static final Table BRANCH_DEFINITIONS = new Table("OSEE_BRANCH_DEFINITIONS");
   public static final Table BRANCH_TABLE_ACL = new Table("OSEE_BRANCH_ACL");

   public static final Table BRANCH_DELETE_HELPER = new Table("OSEE_BRANCH_DELETE_HELPER");
   public static final Table VALID_ATTRIBUTES_TABLE = new Table("OSEE_VALID_ATTRIBUTES");
   public static final Table PERMISSION_TABLE = new Table("OSEE_PERMISSION");
   public static final Table SEQUENCE_TABLE = new Table("OSEE_SEQUENCE");
   public static final Table OSEE_INFO_TABLE = new Table("OSEE_INFO");

   public static final Table OSEE_MERGE_TABLE = new Table("OSEE_MERGE");
   public static final Table OSEE_CONFLICT_TABLE = new Table("OSEE_CONFLICT");

   public static final String TXD_COMMENT = "OSEE_COMMENT";

   private static final View[] skynetViews = new View[] {};

   public static View[] getSkynetViews() {
      return skynetViews;
   }
}
