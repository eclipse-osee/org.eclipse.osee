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

import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactUtil {
   private static final String URI_BY_GUID =
      "SELECT att.uri FROM osee_artifact art, osee_attribute att, %s txs where art.guid = ? and art.art_id = att.art_id and att.uri is not null and att.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = ?";

   @SuppressWarnings("unchecked")
   public static String getUri(String artifactGuid, Branch branch) throws OseeCoreException {
      String sql = String.format(URI_BY_GUID, getTransactionTable(branch));
      return ConnectionHandler.runPreparedQueryFetchString("", sql, artifactGuid, branch.getId(),
         TxChange.CURRENT.getValue());
   }

   private static String getTransactionTable(Branch branch) {
      return branch.getArchiveState().isArchived() ? "osee_txs_archived" : "osee_txs";
   }
}