/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.rest.operations;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class ValidateBranchOperation {

   private final String FIND_CHILDREN_WITH_DUPLICATE_DEF_HIER_PARENTS =
      "select the_count, b_art_id from ( SELECT count(*) the_count , B_ART_ID FROM OSEE_RELATION_LINK lin, " //
         + "OSEE_TXS txs WHERE txs.BRANCH_ID = ? AND lin.REL_LINK_TYPE_ID = 2305843009213694292 " //
         + "AND txs.GAMMA_ID = lin.GAMMA_ID AND txs.TX_CURRENT = 1 GROUP BY B_ART_ID ) count_alias where the_count > 1";
   private final BranchId branch;
   private final JdbcClient jdbcClient;
   private final XResultData results;
   private final OrcsApi orcsApi;

   public ValidateBranchOperation(JdbcClient jdbcClient, BranchId branch, XResultData results, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.branch = branch;
      this.results = results;
      this.orcsApi = orcsApi;
   }

   public ValidateBranchOperation(JdbcClient jdbcClient, BranchId branch, OrcsApi orcsApi) {
      this(jdbcClient, branch, new XResultData(false), orcsApi);
   }

   public XResultData getChildrenWithMultipleParents(ArtifactTypeToken artType) {
      List<Long> artIds = new LinkedList<>();
      jdbcClient.runQuery(stmt -> artIds.add(stmt.getLong("b_art_id")), FIND_CHILDREN_WITH_DUPLICATE_DEF_HIER_PARENTS,
         branch);
      List<Long> returnArtIds = new LinkedList<>();
      if (artType.isValid()) {
         returnArtIds.addAll(filterByArtifactTypeInherited(artIds, artType));
      } else {
         returnArtIds.addAll(artIds);
      }
      if (!artIds.isEmpty()) {
         results.errorf("Children have duplicate default hierarchy parents [%s].",
            Collections.toString(",", returnArtIds));
         for (Long id : returnArtIds) {
            results.getIds().add(id.toString());
         }
      }
      return results;
   }

   public XResultData runAll(ArtifactTypeToken artType) {
      getChildrenWithMultipleParents(artType);
      getOrphans(artType);
      return results;
   }

   public XResultData getOrphans(ArtifactTypeToken artType) {
      List<Long> resultIds = new LinkedList<>();
      for (ArtifactId art : orcsApi.getQueryFactory().fromBranch(branch).andRelationNotExists(
         CoreRelationTypes.DefaultHierarchical_Child).andIsOfType(artType).asArtifactIds()) {
         resultIds.add(art.getId());
      }
      return results;
   }

   private List<Long> filterByArtifactTypeInherited(List<Long> artIds, ArtifactTypeToken artType) {
      List<Long> resultIds = new LinkedList<>();
      for (ArtifactId art : orcsApi.getQueryFactory().fromBranch(branch).andIdsL(artIds).andIsOfType(
         artType).asArtifactIds()) {
         resultIds.add(art.getId());
      }
      return resultIds;
   }
}