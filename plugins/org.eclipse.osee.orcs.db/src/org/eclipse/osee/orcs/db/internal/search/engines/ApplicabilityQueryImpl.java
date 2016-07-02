/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityQueryImpl implements ApplicabilityQuery {
   private final JdbcClient jdbcClient;
   private static final String SELECT_APPLIC_FOR_ART =
      "SELECT distinct e2, value FROM osee_artifact art, osee_txs txs1, osee_tuple2 app, osee_txs txs2, osee_key_value WHERE art_id = ? and art.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current = 1 and tuple_type = 2 AND e2 = txs1.app_id AND app.gamma_id = txs2.gamma_id AND txs2.branch_id = txs1.branch_id AND txs2.tx_current = 1 AND e2 = key";

   public ApplicabilityQueryImpl(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      ApplicabilityToken[] result = new ApplicabilityToken[] {ApplicabilityToken.BASE};
      jdbcClient.runQuery(stmt -> result[0] = new ApplicabilityToken(stmt.getLong("e2"), stmt.getString("value")),
         SELECT_APPLIC_FOR_ART, artId, branch);
      return result[0];
   }
}