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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceValidationApi;
import org.eclipse.osee.mim.types.ConnectionValidationResult;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceValidationApiImpl implements InterfaceValidationApi {

   private final InterfaceConnectionViewApi connectionApi;
   private final JdbcClient jdbcClient;
   InterfaceValidationApiImpl(InterfaceConnectionViewApi connectionApi, JdbcClient jdbcClient) {
      this.connectionApi = connectionApi;
      this.jdbcClient = jdbcClient;
   }

   @Override
   public ConnectionValidationResult validateConnection(BranchId branch, ArtifactId viewId, ArtifactId connectionId) {
      InterfaceConnection connection = this.connectionApi.get(branch, viewId, connectionId,
         Arrays.asList(FollowRelation.fork(CoreRelationTypes.InterfaceConnectionNode_Node),
            FollowRelation.fork(CoreRelationTypes.InterfaceConnectionTransportType_TransportType),
            FollowRelation.follow(CoreRelationTypes.InterfaceConnectionMessage_Message),
            FollowRelation.fork(CoreRelationTypes.InterfaceMessagePubNode_Node),
            FollowRelation.follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage),
            FollowRelation.follow(CoreRelationTypes.InterfaceSubMessageContent_Structure),
            FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement),
            FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement,
               FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType)),
            FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType)));

      List<InterfaceMessageToken> messages = connection.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceConnectionMessage_Message).getList().stream().map(
            art -> new InterfaceMessageToken(art)).collect(Collectors.toList());

      List<InterfaceStructureToken> structures = new LinkedList<>();

      ConnectionValidationResult result = new ConnectionValidationResult(branch, viewId, connection.getName());

      // All messages must have a message type
      for (InterfaceMessageToken message : messages) {
         if (Strings.isInvalid(message.getInterfaceMessageType())) {
            result.getMessageTypeErrors().add(message.getName());
         }

         // Get all the structs for this message while we're here
         message.getSubMessages().stream().map(subMsg -> subMsg.getArtifactReadable().getRelated(
            CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream().map(
               art -> new InterfaceStructureToken(art)).collect(Collectors.toList())).forEach(
                  structs -> structures.addAll(structs));
      }

      // All structures must be byte aligned based on transport type settings
      // Structures should not have duplicate names or abbreviations
      Set<String> structureSheetNames = new HashSet<>();
      for (InterfaceStructureToken structure : structures) {
         if (structure.getIncorrectlySized()) {
            result.getStructureByteAlignmentErrors().add(structure.getName());
         }
         String structureSheetName =
            structure.getNameAbbrev().isEmpty() ? structure.getName() : structure.getNameAbbrev();
         if (structureSheetNames.contains(structureSheetName)) {
            result.getDuplicateStructureNameErrors().add(structureSheetName);
         }
         structureSheetNames.add(structureSheetName);
      }

      return result;
   }

   @Override
   public List<ArtifactToken> getImpactedConnections(BranchId branch) {
      String query = "with impactedArts as " + //
         "        (select distinct attr.art_id from osee_txs txs, osee_attribute attr "//
         + "               where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = attr.gamma_id union all" + // workingBranch,workingBranch
         "         select distinct art.art_id  from osee_txs txs, osee_artifact art where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = art.gamma_id union all" + //workingBranch,workingBranch
         "         select distinct art.a_art_id from osee_txs txs, osee_relation art where branch_id = ? and transaction_id > (select baseline_transaction_id from osee_branch b where b.branch_id = ?) and txs.gamma_id = art.gamma_id)," + // workingBranch,workingBranch
         "  allRels (a_art_id, b_art_id, gamma_id, rel_type) as " + //
         "        (select a_art_id, b_art_id, txs.gamma_id, rel_type " + //
         "          from osee_txs txs, osee_relation rel " + //
         "          where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id " + //workingBranch,tx_current=1
         "         union all" + //
         "         select a_art_id, b_art_id, txs.gamma_id, rel_link_type_id rel_type " + //
         "          from osee_txs txs, osee_relation_link rel " + //
         "          where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id), " + //workingBranch,tx_current=1
         "  cte_query (b_art_id, a_art_id, rel_type) as " + //
         "        (select b_art_id, a_art_id, rel_type " + //
         "          from allRels " + //
         "          where b_art_id in (select art_id from impactedArts)" + //
         "         union all" + //
         "          select e.b_art_id, e.a_art_id, e.rel_type " + //
         "          from allRels e " + //
         "          inner join cte_query c on c.a_art_id = e.b_art_id) " + //
         " select distinct attr.art_id, attr.value " + //
         " from cte_query cq, osee_txs txs, osee_artifact art, osee_txs attrTxs, osee_attribute attr " + //
         " where cq.a_art_id = art.art_id and art.art_type_id = ? and txs.branch_id = ? and txs.tx_current = ? and " + //CoreArtifactTypes.InterfaceConnection.getId(), CoreArtifactTypes.workingBranch,tx_current = 1
         " txs.gamma_id = art.gamma_id and attrTxs.branch_id = ? and attrTxs.tx_current = ? and attrTxs.gamma_id = attr.gamma_id and attr.art_id = art.art_id " + //workingBranch,tx_current = 1
         " and attr.attr_type_id = ?"; //CoreAttributeTypes.Name

      List<ArtifactToken> conns = new ArrayList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         conns.add(ArtifactToken.valueOf(ArtifactId.valueOf(stmt.getLong("art_id")), stmt.getString("value")));
      };

      jdbcClient.runQuery(consumer, query, branch, branch, branch, branch, branch, branch, branch,
         TxCurrent.CURRENT.getIdIntValue(), branch, TxCurrent.CURRENT.getIdIntValue(),
         CoreArtifactTypes.InterfaceConnection.getId(), branch, TxCurrent.CURRENT.getIdIntValue(), branch,
         TxCurrent.CURRENT.getIdIntValue(), CoreAttributeTypes.Name.getId());

      return conns;

   }

}
