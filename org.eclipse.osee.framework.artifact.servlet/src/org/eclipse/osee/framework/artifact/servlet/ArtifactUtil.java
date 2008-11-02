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
package org.eclipse.osee.framework.artifact.servlet;

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactUtil {

   private static final int DEFAULT_ATTRIBUTE_DATA_PROVIDER = 1;

   private static String URI_BY_GUID_AND_BRANCH_ID =
         "SELECT uri FROM osee_tx_details txd1, osee_txs txs1, osee_artifact art1, osee_attribute attr1, osee_attribute_type attyp1 WHERE art1.guid =? AND attyp1.attr_provider_type_id <> ? AND attyp1.attr_type_id = attr1.attr_type_id AND txd1.branch_id = ? AND txs1.gamma_id = attr1.gamma_id AND attr1.art_id = art1.art_id AND txs1.transaction_id = txd1.transaction_id AND txs1.tx_current = 1";

   private static String URI_BY_GUID_AND_BRANCH_NAME =
         "SELECT uri FROM osee_tx_details txd1, osee_txs txs1, osee_artifact art1, osee_attribute attr1, osee_attribute_type attyp1, osee_branch branch1 WHERE art1.guid = ? AND attyp1.attr_provider_type_id <> ? AND attyp1.attr_type_id = attr1.attr_type_id AND txs1.gamma_id = attr1.gamma_id AND attr1.art_id = art1.art_id AND txs1.transaction_id = txd1.transaction_id AND txs1.tx_current = 1 AND txd1.branch_id = branch1.branch_id AND branch1.branch_name = ?";

   public static String getUri(String guid, int branchId) throws OseeDataStoreException {
      return getUri(URI_BY_GUID_AND_BRANCH_ID, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchId);
   }

   public static String getUri(String guid, String branchName) throws OseeDataStoreException {
      return getUri(URI_BY_GUID_AND_BRANCH_NAME, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchName);
   }

   private static String getUri(String query, Object... dataBindings) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchString(null, query, dataBindings);
   }
}
