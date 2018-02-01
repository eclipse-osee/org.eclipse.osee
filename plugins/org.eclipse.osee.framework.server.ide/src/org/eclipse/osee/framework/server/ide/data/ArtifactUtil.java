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
package org.eclipse.osee.framework.server.ide.data;

import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactUtil {
   private static final String URI_BY_GUID =
      "SELECT att.uri FROM osee_artifact art, osee_attribute att, %s txs where art.guid = ? and art.art_id = att.art_id and att.uri is not null and att.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = ?";

   public static String getUri(JdbcClient jdbcClient, String artifactGuid, BranchReadable branch) {
      String sql = String.format(URI_BY_GUID, getTransactionTable(branch));
      return jdbcClient.fetch("", sql, artifactGuid, branch, TxChange.CURRENT);
   }

   private static String getTransactionTable(BranchReadable branch) {
      return branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
   }
}