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

   private static final String DEFAULT_ATTRIBUTE_DATA_PROVIDER = "%DefaultAttributeDataProvider";

   private static String URI_BY_GUID_PREFIX =
         "SELECT attr1.uri FROM osee_artifact art1, osee_attribute attr1, osee_attribute_type attyp1, osee_attribute_provider_type oapt1, osee_txs txs1, osee_tx_details txd1";

   private static String URI_BY_GUID_JOINS =
         " WHERE art1.art_id = attr1.art_id AND attr1.attr_type_id = attyp1.attr_type_id AND oapt1.attr_provider_type_id = attyp1.attr_provider_type_id AND attr1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id";

   private static String URI_BY_GUID_AND_BRANCH_ID =
         URI_BY_GUID_PREFIX + URI_BY_GUID_JOINS + " AND txs1.tx_current = 1 AND art1.guid = ? AND NOT oapt1.attribute_provider_class LIKE ? AND txd1.branch_id = ?";

   private static String URI_BY_GUID_AND_BRANCH_NAME =
         URI_BY_GUID_PREFIX + ", osee_branch ob1" + URI_BY_GUID_JOINS + " AND txd1.branch_id = ob1.branch_id AND txs1.tx_current = 1 AND art1.guid = ? AND NOT oapt1.attribute_provider_class LIKE ? AND ob1.branch_name = ?";

   public static String getUri(String guid, int branchId) throws OseeDataStoreException {
      return getUri(URI_BY_GUID_AND_BRANCH_ID, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchId);
   }

   public static String getUri(String guid, String branchName) throws OseeDataStoreException {
      return getUri(URI_BY_GUID_AND_BRANCH_NAME, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchName);
   }

   private static String getUri(String query, Object... dataBindings) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchString((String) null, query, dataBindings);
   }
}
