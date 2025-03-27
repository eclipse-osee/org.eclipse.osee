/*********************************************************************
* Copyright (c) 2021 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://no-click.mil/?https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.RelTableInvalidException;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.orcs.rest.model.transaction.CycleDetectionResult;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Hugo Trejo, Torin Grenda, David Miller
 */
public class RelationEndpointImpl implements RelationEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final JdbcService jdbcService;

   public RelationEndpointImpl(OrcsApi orcsApi, JdbcService jdbcService, BranchId branch) {
      this.orcsApi = orcsApi;
      this.jdbcService = jdbcService;
      this.branch = branch;
   }

   @Override
   public Response createRelationByType(ArtifactId sideA, ArtifactId sideB, RelationTypeToken relationType) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch,
         String.format("RelationEndpoint REST api creating relation %s between %s and %s", relationType.getName(),
            sideA.getIdString(), sideB.getIdString()));
      tx.relate(sideA, relationType, sideB);
      tx.commit();

      return Response.ok().build();
   }

   @Override
   public List<ArtifactToken> getRelatedHierarchy(ArtifactId artifact, ArtifactId view) {

      List<ArtifactToken> ids = getRelated(artifact, CoreRelationTypes.DefaultHierarchical, RelationSide.SIDE_A, view);
      return ids;
   }

   private List<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeToken relationType, RelationSide side,
      ArtifactId view) {
      RelationTypeSide rts = new RelationTypeSide(relationType, side);
      return orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedTo(rts, artifact).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> getRelatedRecursive(ArtifactId artifact, RelationTypeToken relationType, ArtifactId view,
      boolean upstream) {
      RelationTypeSide side = upstream ? new RelationTypeSide(relationType,
         RelationSide.SIDE_A) : new RelationTypeSide(relationType, RelationSide.SIDE_B);
      return orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedRecursive(side, artifact,
         upstream).asArtifactTokens();
   }

   @Override
   public List<RelationTypeToken> convertRelations(ArtifactId artToken, RelationTypeToken oldRelationType,
      RelationTypeToken newRelationType) {
      RelationTypeSide sideB = new RelationTypeSide(oldRelationType, RelationSide.SIDE_B);
      ArtifactReadable art = orcsApi.getQueryFactory().fromBranch(branch).andId(artToken).asArtifact();

      if (art.getRelatedCount(sideB) > 0) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch,
            String.format("Converting relations for artifact %s.  Old Relation = %s New Relation = %s",
               artToken.getIdString(), newRelationType.getName(), oldRelationType.getName()));
         for (ArtifactReadable artB : art.getRelated(sideB).getList()) {
            tx.relate(art, newRelationType, artB);
            //tx.unrelate(art, oldRelationType, artB);  --this line can wait until we've done more testing
         }
         tx.commit();
      }

      return null;
   }

   @Override
   public TransactionToken convertAllRelations(RelationTypeToken oldRelationType, RelationTypeToken newRelationType) {
      RelationTypeSide sideB = new RelationTypeSide(oldRelationType, RelationSide.SIDE_B);
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch,
         String.format("Converting relations for Old Relation = %s New Relation = %s", oldRelationType.getName(),
            newRelationType.getName()));

      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(branch).andRelationExists(
         oldRelationType).getResults().getList()) {
         if (art.getRelatedCount(sideB) > 0) {
            for (ArtifactReadable artB : art.getRelated(sideB).getList()) {
               tx.relate(art, newRelationType, artB);
            }
         }
      }
      return tx.commit();
   }

   @Override
   public String validateRel2Table() {
      int count = orcsApi.getJdbcService().getClient().fetch(0,
         "select count(*) from (select distinct rel_type, a_art_id, rel_order, cnt\n" + "from\n" + "(select rel_type, a_art_id, b_art_id, rel_order, count('x') over (partition by rel_type, a_art_id, rel_order) cnt\n" + "from osee_relation rel where rel_order > 0) t1\n" + "where cnt > 1) t2");
      if (count > 0) {
         throw new RelTableInvalidException("Invalid relations detected. Count: %s", count);
      }
      return "<html><div>Relations Valid</div></html>";
   }

   @Override
   public CycleDetectionResult findRelationCycles(@PathParam("relationType") RelationTypeToken relationType) {
      List<int[]> relations = fetchRelations(relationType);

      Map<Integer, Set<Integer>> graph = buildGraph(relations);

      return getCyclicalComponents(graph);
   }

   private List<int[]> fetchRelations(RelationTypeToken relationType) {
      // @formatter:off
      String relQuery = "SELECT rel.A_ART_ID, rel.B_ART_ID \n" +
         "FROM osee_relation_link rel \n" +
         "JOIN osee_txs txs ON rel.gamma_id = txs.gamma_id \n" +
         "WHERE txs.tx_current = 1 AND rel.REL_LINK_TYPE_ID = ? AND txs.branch_id = ?";
      // @formatter:on

      List<int[]> relations = new ArrayList<>();

      jdbcService.getClient().runQuery(stmt -> {
         int parent = stmt.getInt("A_ART_ID");
         int child = stmt.getInt("B_ART_ID");
         relations.add(new int[] {parent, child});
      }, relQuery, relationType, branch);

      return relations;
   }

   private Map<Integer, Set<Integer>> buildGraph(List<int[]> relations) {
      Map<Integer, Set<Integer>> graph = new HashMap<>();

      for (int[] relation : relations) {
         int parent = relation[0];
         int child = relation[1];

         graph.computeIfAbsent(parent, k -> new HashSet<>()).add(child);
         graph.computeIfAbsent(child, k -> new HashSet<>());
      }

      return graph;
   }

   private CycleDetectionResult getCyclicalComponents(Map<Integer, Set<Integer>> graph) {
      Set<Integer> visited = new HashSet<>();
      List<Set<Integer>> components = new ArrayList<>();
      Set<Integer> cycleNodes = new HashSet<>();

      for (Integer node : graph.keySet()) {
         if (!visited.contains(node)) {
            Set<Integer> component = new HashSet<>();
            boolean hasCycle = collectComponent(graph, node, visited, component, cycleNodes);
            if (hasCycle) {
               components.add(component); // Add component only if it contains a cycle.
            }
         }
      }

      return new CycleDetectionResult(components, cycleNodes);
   }

   private boolean collectComponent(Map<Integer, Set<Integer>> graph, Integer currentNode, Set<Integer> visited,
      Set<Integer> component, Set<Integer> cycleNodes) {
      Set<Integer> recursionStack = new HashSet<>();
      return dfsWithCycleDetection(graph, currentNode, visited, recursionStack, component, cycleNodes);
   }

   private boolean dfsWithCycleDetection(Map<Integer, Set<Integer>> graph, Integer currentNode, Set<Integer> visited,
      Set<Integer> recursionStack, Set<Integer> component, Set<Integer> cycleNodes) {
      visited.add(currentNode);
      recursionStack.add(currentNode);
      component.add(currentNode);

      boolean cycleDetected = false;

      for (Integer neighbor : graph.getOrDefault(currentNode, Collections.emptySet())) {
         if (!visited.contains(neighbor)) {
            // Continue DFS to explore all neighbors while propagating flag up recursion.
            cycleDetected |= dfsWithCycleDetection(graph, neighbor, visited, recursionStack, component, cycleNodes);
         } else if (recursionStack.contains(neighbor)) {
            // Add the node to cycleNodes and flag cycle.
            cycleNodes.add(neighbor);
            cycleDetected = true;
         }
      }

      recursionStack.remove(currentNode);
      return cycleDetected;
   }
}
