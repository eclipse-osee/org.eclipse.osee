/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class DuplicateRelationsOperation {

   private final OrcsApi orcsApi;
   private final Map<String, RelationLink> idsToLink = new HashMap<>();

   public DuplicateRelationsOperation(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public String getReport(BranchId branch, boolean fix) {
      BranchToken useBranch =
         orcsApi.getQueryFactory().branchQuery().andId(branch).andIsOfType(BranchType.WORKING).getOneOrSentinel();

      XResultData rd = new XResultData();
      rd.logf("Duplicate Relation Report - Run: %s", new Date());
      if (useBranch.isInvalid()) {
         rd.errorf("<br/><br/>Invalid Working Branch - %s", branch);
         return rd.toString();
      }
      rd.logf("<br/><br/>Branch- %s<br/>", useBranch);
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("rel_link_id", "rel_link_type_id", "rel_link_name",
         "a_art_id", "b_art_id", "gamma_id", "DELETE").toArray(new String[6])));

      final TransactionBuilder tx =
         fix ? orcsApi.getTransactionFactory().createTransaction(branch, "Delete Duplicate Relations") : null;

      orcsApi.getJdbcService().getClient().runQuery(stmt -> {
         RelationId relId = RelationId.valueOf(stmt.getString("rel_link_id"));
         String relLinkTypeId = stmt.getString("rel_link_type_id");
         RelationTypeToken relType = orcsApi.tokenService().getRelationType(Long.valueOf(relLinkTypeId));
         ArtifactId aArtId = ArtifactId.valueOf(stmt.getLong("a_art_id"));
         ArtifactId bArtId = ArtifactId.valueOf(stmt.getLong("b_art_id"));
         ArtifactToken bArt = orcsApi.getQueryFactory().fromBranch(useBranch).andId(bArtId).asArtifactTokenOrSentinel();
         if (bArt.isInvalid()) {
            rd.warningf("Skipping Deleted %s<br/>", bArtId);
            return;
         }
         GammaId gamma = GammaId.valueOf(stmt.getString("gamma_id"));

         RelationLink link = new RelationLink(relId, relType, aArtId, bArtId, gamma);
         String linkKey = getKey(relType, aArtId, bArtId);
         RelationLink match = idsToLink.get(linkKey);

         RelationLink toDelete;
         String deleteResult = "";
         if (match != null) {
            toDelete = (link.getGamma().getId() < match.getGamma().getId() ? link : match);
            if (toDelete.getGamma().equals(link.getGamma())) {
               deleteResult = "this => " + link.getGamma();
            } else {
               deleteResult = "other=> " + match.getGamma().getIdString();
            }
            if (fix && tx != null) {
               tx.unrelate(aArtId, relType, bArtId, toDelete.getGamma());
            }
            rd.addRaw(AHTML.addRowMultiColumnTable(relId.getIdString() + ", " + match.getRelId(), relLinkTypeId,
               relType.getName(), aArtId.getIdString(), bArtId.getIdString(),
               gamma.getIdString() + ", " + match.getGamma(), deleteResult));
         }
         idsToLink.put(linkKey, link);

      }, getDuplicateRelationsQuery(), branch.getId(), branch.getId());
      rd.addRaw(AHTML.endMultiColumnTable());
      if (fix && tx != null) {
         TransactionToken res = tx.commit();
         rd.addRaw("Fixed in Transaction: " + res);
      }
      return rd.toString();
   }

   private String getKey(RelationTypeToken relType, ArtifactId aArtId, ArtifactId bArtId) {
      return relType.getIdString() + "," + aArtId.getIdString() + "," + bArtId.getIdString();
   }

   private String getDuplicateRelationsQuery() {
      return "SELECT r1.* " + //
         "FROM osee_relation_link r1 " + //
         "JOIN osee_relation_link r2 " + //
         "  ON r1.A_ART_ID = r2.A_ART_ID " + //
         " AND r1.B_ART_ID = r2.B_ART_ID AND r1.REL_LINK_TYPE_ID = r2.REL_LINK_TYPE_ID AND r1.REL_LINK_ID <> r2.REL_LINK_ID " + //
         "JOIN osee_txs t1 " + //
         "  ON r1.GAMMA_ID = t1.GAMMA_ID AND t1.TX_CURRENT = 1 AND t1.BRANCH_ID = ? " + //
         "JOIN osee_txs t2 " + //
         "  ON r2.GAMMA_ID = t2.GAMMA_ID AND t2.TX_CURRENT = 1 AND t2.BRANCH_ID = ? " + //
         "order by r1.A_ART_ID ASC";
   }

}