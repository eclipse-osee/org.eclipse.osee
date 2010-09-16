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
package org.eclipse.osee.framework.manager.servlet.data;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactUtil {

   private static final String DEFAULT_ATTRIBUTE_DATA_PROVIDER = "%DefaultAttributeDataProvider";

   private static String URI_BY_GUID_PREFIX =
      "SELECT attr1.uri FROM osee_artifact art1, osee_attribute attr1, osee_attribute_type attyp1, osee_attribute_provider_type oapt1, %s txs1";

   private static String URI_BY_GUID_AND_BRANCH_ID =
      URI_BY_GUID_PREFIX + " WHERE art1.art_id = attr1.art_id AND attr1.attr_type_id = attyp1.attr_type_id AND oapt1.attr_provider_type_id = attyp1.attr_provider_type_id AND attr1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND art1.guid = ? AND NOT oapt1.attribute_provider_class LIKE ? AND txs1.branch_id = ?";

   private static String URI_BY_GUID_AND_BRANCH_NAME =
      URI_BY_GUID_PREFIX + ", osee_branch ob1 WHERE art1.art_id = attr1.art_id AND attr1.attr_type_id = attyp1.attr_type_id AND oapt1.attr_provider_type_id = attyp1.attr_provider_type_id AND attr1.gamma_id = txs1.gamma_id AND txs1.branch_id = ob1.branch_id AND txs1.tx_current = 1 AND art1.guid = ? AND NOT oapt1.attribute_provider_class LIKE ? AND ob1.branch_name = ?";

   private static String URI_BY_GUID_AND_BRANCH_GUID =
      URI_BY_GUID_PREFIX + ", osee_branch ob1 WHERE art1.art_id = attr1.art_id AND attr1.attr_type_id = attyp1.attr_type_id AND oapt1.attr_provider_type_id = attyp1.attr_provider_type_id AND attr1.gamma_id = txs1.gamma_id AND txs1.branch_id = ob1.branch_id AND txs1.tx_current = 1 AND art1.guid = ? AND NOT oapt1.attribute_provider_class LIKE ? AND ob1.branch_guid = ?";

   public static String getUri(String guid, int branchId) throws OseeCoreException {
      BranchArchivedState state = isArchived(branchId);
      return getUri(state, URI_BY_GUID_AND_BRANCH_ID, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchId);
   }

   public static String getUri(String guid, String branchName) throws OseeCoreException {
      BranchArchivedState state = isArchived(branchName);
      return getUri(state, URI_BY_GUID_AND_BRANCH_NAME, guid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchName);
   }

   public static String getUriByGuids(String branchGuid, String artifactGuid) throws OseeCoreException {
      BranchArchivedState state = isArchivedBranchGuid(branchGuid);
      return getUri(state, URI_BY_GUID_AND_BRANCH_GUID, artifactGuid, DEFAULT_ATTRIBUTE_DATA_PROVIDER, branchGuid);
   }

   private static BranchArchivedState isArchivedBranchGuid(String branchGuid) throws OseeCoreException {
      return BranchArchivedState.valueOf(ConnectionHandler.runPreparedQueryFetchInt(0,
         "Select archived from osee_branch where branch_guid = ?", branchGuid));
   }

   private static BranchArchivedState isArchived(int branchId) throws OseeCoreException {
      return BranchArchivedState.valueOf(ConnectionHandler.runPreparedQueryFetchInt(0,
         "Select archived from osee_branch where branch_id = ?", branchId));
   }

   private static BranchArchivedState isArchived(String branchName) throws OseeCoreException {
      return BranchArchivedState.valueOf(ConnectionHandler.runPreparedQueryFetchInt(0,
         "Select archived from osee_branch where branch_name like ?", branchName));
   }

   private static String getUri(BranchArchivedState state, String query, Object... dataBindings) throws OseeCoreException {
      String sql = String.format(query, getTransactionTable(state));
      return ConnectionHandler.runPreparedQueryFetchString("", sql, dataBindings);
   }

   private static String getTransactionTable(BranchArchivedState state) {
      return state.isUnArchived() ? "osee_txs" : "osee_txs_archived";
   }
}
