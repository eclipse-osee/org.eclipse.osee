/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.jdbc.JdbcClient;

public final class DeleteFromAllTablesWithGammaId {

   public static Response deleteAllGammas(JdbcClient client, String gammaQuery, List<Object> parameters,
      List<Object[]> gammaIds) {
      client.runQuery(stmt -> gammaIds.add(new Object[] {stmt.getLong("gamma_id")}), gammaQuery, parameters.toArray());

      String deleteFromWhereGamma = "delete from %s where gamma_id = ?";
      int purgeSearchTags = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_search_tags"), gammaIds);
      int purgeTxs = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_txs"), gammaIds);
      int purgeTxsArchived = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_txs_archived"), gammaIds);
      int purgeRelationLink =
         client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_relation_link"), gammaIds);
      int purgeRelation = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_relation"), gammaIds);
      int purgeAttribute = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_attribute"), gammaIds);
      int purgeTuple2 = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_tuple2"), gammaIds);
      int purgeTuple3 = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_tuple3"), gammaIds);
      int purgeTuple4 = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_tuple4"), gammaIds);
      int purgeArtifact = client.runBatchUpdate(String.format(deleteFromWhereGamma, "osee_artifact"), gammaIds);

      return Response.ok(
         "Counts: search_tags: " + purgeSearchTags + " txs:" + purgeTxs + " txsArchived: " + purgeTxsArchived + //
            purgeTxsArchived + " relation_link: " + purgeRelationLink + "relation: " + purgeRelation + " attribute: " + purgeAttribute + " tuple2: " + purgeTuple2 //
            + " tuple3: " + purgeTuple3 + " tuple4: " + purgeTuple4 + " artifact: " + purgeArtifact).build();
   }

}
