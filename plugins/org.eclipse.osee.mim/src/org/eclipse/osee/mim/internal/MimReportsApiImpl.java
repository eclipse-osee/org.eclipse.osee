/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.mim.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.mim.MimReportsApi;
import org.eclipse.osee.mim.types.NodeTraceReportItem;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Ryan Baldwin
 */
public class MimReportsApiImpl implements MimReportsApi {

   private final OrcsApi orcsApi;

   public MimReportsApiImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch) {
      return getAllRequirementsToInterface(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch, long pageNum, long pageSize) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationExists(
            CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      query = query.follow(CoreRelationTypes.RequirementsToInterface_InterfaceArtifact);
      List<ArtifactReadable> requirements = query.asArtifacts();
      results = requirements.stream().map(
         r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
            Collectors.toList());
      return results;
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return getAllRequirementsToInterfaceWithNoMatch(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      return query.asArtifacts().stream().map(
         r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
            Collectors.toList());
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch) {
      return getAllInterfaceToRequirements(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch, long pageNum, long pageSize) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationExists(CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      query = query.follow(CoreRelationTypes.RequirementsToInterface_Artifact);
      List<ArtifactReadable> arts = query.asArtifacts();
      results =
         arts.stream().map(a -> new NodeTraceReportItem(a, CoreRelationTypes.RequirementsToInterface_Artifact)).collect(
            Collectors.toList());
      return results;
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface).asArtifacts().stream().map(
               a -> new NodeTraceReportItem(a, CoreRelationTypes.RequirementsToInterface_Artifact)).collect(
                  Collectors.toList());
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      return query.asArtifacts().stream().map(
         a -> new NodeTraceReportItem(a, CoreRelationTypes.RequirementsToInterface_Artifact)).collect(
            Collectors.toList());
   }

   @Override
   public NodeTraceReportItem getInterfacesFromRequirement(BranchId branch, ArtifactId artId) {
      ArtifactReadable requirement = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
         CoreRelationTypes.RequirementsToInterface_InterfaceArtifact).asArtifact();
      return new NodeTraceReportItem(requirement, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact);
   }

   @Override
   public NodeTraceReportItem getRequirementsFromInterface(BranchId branch, ArtifactId artId) {
      ArtifactReadable interfaceReadable = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
         CoreRelationTypes.RequirementsToInterface_Artifact).asArtifact();
      return new NodeTraceReportItem(interfaceReadable, CoreRelationTypes.RequirementsToInterface_Artifact);
   }

   @Override
   public int getCountRequirementsToInterface(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationExists(
         CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.Requirement).andRelationNotExists(CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountInterfaceToRequirements(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationExists(CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountInterfaceToRequirementsWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public List<ArtifactToken> getImpactedConnections(BranchId branch) {
      String query =
         "with " + orcsApi.getJdbcService().getClient().getDbType().getRecursiveWithSql() + " impactedArts as " + //
            "        (select distinct attr.art_id from osee_txs txs, osee_attribute attr "//
            + "               where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = attr.gamma_id union all" + // workingBranch,workingBranch
            "         select distinct art.art_id  from osee_txs txs, osee_artifact art where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = art.gamma_id union all" + //workingBranch,workingBranch
            "         select distinct art.a_art_id from osee_txs txs, osee_relation art where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = art.gamma_id)," + // workingBranch,workingBranch
            "  allRels (a_art_id, b_art_id, gamma_id, rel_type) as " + //
            "        (select a_art_id, b_art_id, txs.gamma_id, rel_type " + //
            "          from osee_txs txs, osee_relation rel " + //
            "          where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id " + //workingBranch,tx_current=1
            "          and rel.rel_type in (6039606571486514300,6039606571486514298,6039606571486514301,6039606571486514302,126164394421696912,2455059983007225780,3899709087455064780,126164394421696914,2455059983007225781,3899709087455064781,2455059983007225794,2455059983007225795,5540416179400488807,8734224778892840579,1859749228181133209,2283114833979032380)" + //
            "         union all" + //
            "         select a_art_id, b_art_id, txs.gamma_id, rel_type rel_type " + //
            "          from osee_txs txs, osee_relation rel " + //
            "          where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id " + //parentBranch,tx_current=1
            "          and rel.rel_type in (6039606571486514300,6039606571486514298,6039606571486514301,6039606571486514302,126164394421696912,2455059983007225780,3899709087455064780,126164394421696914,2455059983007225781,3899709087455064781,2455059983007225794,2455059983007225795,5540416179400488807,8734224778892840579,1859749228181133209,2283114833979032380)" + //
            "          )," + //
            "  cte_query (b_art_id, a_art_id, rel_type, art_path) as " + //
            "        (select b_art_id, a_art_id, rel_type, b_art_id||','||a_art_id art_path" + //
            "          from allRels " + //
            "          where b_art_id in (select art_id from impactedArts)" + //
            "         union all" + //
            "          select e.b_art_id, e.a_art_id, e.rel_type, c.art_path||','||e.a_art_id " + //
            "          from allRels e " + //
            "          inner join cte_query c on c.a_art_id = e.b_art_id) " + //
            " select distinct t2.art_id, t2.value||' - '|| case when art_cnt = max_cnt then 'Assumed Target Connection' else 'Impacted Artifact: '||oAttr.value||'('||oAttr.art_id||')' end value " + //
            " from osee_txs oTxs, osee_attribute oAttr, (" + //
            " select art_id, value, count(distinct art_id) over () conn_cnt, min(art_cnt) over () min_cnt, max(art_cnt) over () max_cnt, art_cnt, art_path from " + //
            " (" + //
            "   select attr.art_id, attr.value, count (art.art_id) over (partition by art.art_id) art_cnt, cq.art_path " + //
            "   from cte_query cq, osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr " + //
            "   where cq.a_art_id = art.art_id and art.art_type_id = ? and txs.branch_id = ? and txs.tx_current = ? and " + //CoreArtifactTypes.InterfaceConnection.getId(), CoreArtifactTypes.workingBranch,tx_current = 1
            " txs.gamma_id = art.gamma_id and attrTxs.branch_id = ? and attrTxs.tx_current = ? and attrTxs.gamma_id = attr.gamma_id and attr.art_id = art.art_id " + //workingBranch,tx_current = 1
            " and attr.attr_type_id = ? " + //CoreAttributeTypes.Name
            " union " + //
            "   select attr.art_id, attr.value, count (art.art_id) over (partition by art.art_id) art_cnt, cq.art_path " + //
            "   from cte_query cq, osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr " + //
            "   where cq.a_art_id = art.art_id and art.art_type_id = ? and txs.branch_id = ? and txs.tx_current = ? and " + //CoreArtifactTypes.InterfaceConnection.getId(), CoreArtifactTypes.workingBranch,tx_current = 1
            " txs.gamma_id = art.gamma_id and attrTxs.branch_id = ? and attrTxs.tx_current = ? and attrTxs.gamma_id = attr.gamma_id and attr.art_id = art.art_id " + //parentBranch,tx_current = 1
            " and attr.attr_type_id = ? " + //CoreAttributeTypes.Name
            " ) t1 " + //
            ") t2 " + //
            " where oTxs.branch_id = ? and oTxs.tx_current = ? and oTxs.gamma_id = oAttr.gamma_id and substr(art_path,1,instr(art_path,',')-1) = oAttr.art_id and oAttr.attr_type_id = ?"; //parent branch, txcurrent, name

      List<ArtifactToken> conns = new ArrayList<>();
      Branch branchArt = orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getExactlyOne();
      if (branchArt.getBranchType().equals(BranchType.WORKING)) {

         Consumer<JdbcStatement> consumer = stmt -> {
            conns.add(ArtifactToken.valueOf(ArtifactId.valueOf(stmt.getLong("art_id")), stmt.getString("value")));
         };
         orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch, branch, branch, branch, branch, branch,
            branch, TxCurrent.CURRENT.getIdIntValue(), branchArt.getParentBranch(), TxCurrent.CURRENT.getIdIntValue(),
            CoreArtifactTypes.InterfaceConnection.getId(), branch, TxCurrent.CURRENT.getIdIntValue(), branch,
            TxCurrent.CURRENT.getIdIntValue(), CoreAttributeTypes.Name.getId(),
            CoreArtifactTypes.InterfaceConnection.getId(), branch, TxCurrent.CURRENT.getIdIntValue(), branch,
            TxCurrent.CURRENT.getIdIntValue(), CoreAttributeTypes.Name.getId(), branchArt.getParentBranch(),
            TxCurrent.CURRENT, CoreAttributeTypes.Name.getId());
      }
      return conns;

   }
}