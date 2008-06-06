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

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.core.OseeSequenceManager;

/**
 * Provides the ability to build the tables and sequences necessary for the Define tools to be able to work. This class
 * is only intended for installation purposes, and is not to support general runtime needs.
 * 
 * @author Robert A. Fisher
 */
public class SkynetDatabase {
   public static final int ARTIFACT_TYPE_NAME_SIZE = 55;
   public static final int ARTIFACT_TYPE_FACTORY_KEY_SIZE = 100;
   public static final int ATTRIBUTE_CLASS_SIZE = 500;
   public static final int ATTRIBUTE_DATA_SIZE = 500;
   public static final int ATTRIBUTE_NAME_SIZE = 500;
   public static final int ATTRIBUTE_XML_SIZE = 4000;
   public static final int FACTORY_BUNDLE_SIZE = 500;
   public static final int FACTORY_CLASS_SIZE = 500;
   public static final int GUID_SIZE = 28;
   public static final int RATIONALE_SIZE = 4000;
   public static final int RELATION_NAME_SIZE = 50;
   public static final int RELATION_PHRASING_SIZE = 50;
   public static final int TAG_NAME_SIZE = 25;
   public static final int TIP_TEXT_SIZE = 4000;
   public static final int RELATION_TYPE_NAME_SIZE = 50;
   public static final int RELATION_SIDE_NAME_SIZE = 50;
   public static final int RELATION_SHORT_NAME_SIZE = 20;
   public static final int BRANCH_NAME_SIZE = 100;
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
   public static final Table VALID_RELATIONS_TABLE = new Table("OSEE_DEFINE_VALID_RELATIONS");
   public static final Table PERMISSION_TABLE = new Table("OSEE_DEFINE_PERMISSION");
   public static final Table SEQUENCE_TABLE = new Table("OSEE_DEFINE_SEQUENCE");
   public static final Table OSEE_INFO_TABLE = new Table("OSEE_INFO");

   public static final Table DELETED_ARTIFACTS_TABLE = new Table("OSEE_DEFINE_DELETED_ARTIFACTS");
   public static final Table DELETED_ATTRIBUTES_TABLE = new Table("OSEE_DEFINE_DELETED_ATTRIBUTES");
   public static final Table DELETED_RELATION_LINKS_TABLE = new Table("OSEE_DEFINE_DELETED_LINKS");

   public static final Table TAG_ART_MAP_TABLE = new Table("OSEE_TAG_ART_MAP");
   public static final Table TAG_CLOUD_MAP_TABLE = new Table("OSEE_TAG_CLOUD_MAP");
   public static final Table TAG_CLOUD_TABLE = new Table("OSEE_TAG_CLOUD");
   public static final Table TAG_CLOUD_TYPE_TABLE = new Table("OSEE_TAG_CLOUD_TYPE");
   public static final Table TAG_TABLE = new Table("OSEE_TAG");
   public static final Table TAG_TYPE_TABLE = new Table("OSEE_TAG_TYPE");
   public static final Table SNAPSHOT_TABLE = new Table("OSEE_SNAPSHOT");
   public static final String TXD_COMMENT = "OSEE_COMMENT";

   public static final View CURRENT_VERSION_VIEW =
         new View(
               "CURRENT_VERSION_ARTIFACT",
               " AS" + " SELECT branch_id, MAX(transaction_id) AS largest_transaction_id FROM " + TRANSACTION_DETAIL_TABLE + " GROUP BY branch_id");

   private static final View[] skynetViews = new View[] {};

   public static final String ART_ID_SEQ = "SKYNET_ART_ID_SEQ";
   public static final String ART_TYPE_ID_SEQ = "SKYNET_ART_TYPE_ID_SEQ";
   public static final String ATTR_BASE_TYPE_ID_SEQ = "SKYNET_ATTR_BASE_TYPE_ID_SEQ";
   public static final String ATTR_PROVIDER_TYPE_ID_SEQ = "SKYNET_ATTR_PROVIDER_TYPE_ID_SEQ";
   public static final String ATTR_ID_SEQ = "SKYNET_ATTR_ID_SEQ";
   public static final String ATTR_TYPE_ID_SEQ = "SKYNET_ATTR_TYPE_ID_SEQ";
   public static final String FACTORY_ID_SEQ = "SKYNET_FACTORY_ID_SEQ";
   public static final String BRANCH_ID_SEQ = "SKYNET_BRANCH_ID_SEQ";
   public static final String REL_LINK_TYPE_ID_SEQ = "SKYNET_REL_LINK_TYPE_ID_SEQ";
   public static final String REL_LINK_ID_SEQ = "SKYNET_REL_LINK_ID_SEQ";
   public static final String GAMMA_ID_SEQ = "SKYNET_GAMMA_ID_SEQ";
   public static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";

   public static final String TAG_ID_SEQ = "SKYNET_TAG_ID_SEQ";
   public static final String TAG_TYPE_ID_SEQ = "SKYNET_TAG_TYPE_ID_SEQ";
   public static final String CLOUD_ID_SEQ = "SKYNET_CLOUD_ID_SEQ";
   public static final String CLOUD_TYPE_ID_SEQ = "SKYNET_CLOUD_TYPE_ID_SEQ";

   public static final String TTE_SESSION_SEQ = "TTE_SESSION_SEQ";

   public static final String[] sequences =
         new String[] {ART_ID_SEQ, ART_TYPE_ID_SEQ, ATTR_BASE_TYPE_ID_SEQ, ATTR_PROVIDER_TYPE_ID_SEQ, ATTR_ID_SEQ,
               ATTR_TYPE_ID_SEQ, FACTORY_ID_SEQ, BRANCH_ID_SEQ, REL_LINK_TYPE_ID_SEQ, REL_LINK_ID_SEQ, GAMMA_ID_SEQ,
               TRANSACTION_ID_SEQ, TAG_ID_SEQ, TAG_TYPE_ID_SEQ, CLOUD_ID_SEQ, CLOUD_TYPE_ID_SEQ, TTE_SESSION_SEQ};

   public static View[] getSkynetViews() {
      return skynetViews;
   }
   // must be initialized after table names since they are used in the static initializers for OseeSequenceManager
   private static final OseeSequenceManager sequenceManager = OseeSequenceManager.getInstance();

   public static int getNextGammaId() throws SQLException {
      return (int) sequenceManager.getNextSequence(GAMMA_ID_SEQ);
   }

   public static int getNextArtifactId() throws SQLException {
      return (int) sequenceManager.getNextSequence(ART_ID_SEQ);
   }

   public static int getNextTransactionId() throws SQLException {
      return (int) sequenceManager.getNextSequence(TRANSACTION_ID_SEQ);
   }

   public static int getNextSessionId() throws SQLException {
      return (int) sequenceManager.getNextSequence(TTE_SESSION_SEQ);
   }
}
