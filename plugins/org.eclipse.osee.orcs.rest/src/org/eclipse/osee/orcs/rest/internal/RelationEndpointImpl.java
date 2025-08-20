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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.exception.RelTableInvalidException;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OseeDb;
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
   private static final long SPACING = (long) Math.pow(2.0, 18.0);
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
   public TransactionToken convertAllRelations(boolean initial) {
      for (RelationTypeToken rel : orcsApi.tokenService().getRelationTypes().stream().filter(
         a -> a.isNewRelationTable()).collect(Collectors.toList())) {

         if (rel.getOldRelationTypeToken() != null && rel.getOldRelationTypeToken().isValid()) {
            System.out.println("Converting " + rel.getName() + " to " + rel.getOldRelationTypeToken().getName());
            convert_single_relation(branch, rel.getOldRelationTypeToken(), rel, initial);
         }
      }

      return null;
   }

   public static int calculateHeadInsertionOrderIndex(int currentHeadIndex) {
      long idealIndex = currentHeadIndex - SPACING;
      if (idealIndex > Integer.MIN_VALUE) {
         return (int) idealIndex;
      }
      return calculateInsertionOrderIndex(Integer.MIN_VALUE, currentHeadIndex);
   }

   public static int calculateEndInsertionOrderIndex(int currentEndIndex) {
      long idealIndex = currentEndIndex + SPACING;
      if (idealIndex < Integer.MAX_VALUE) {
         return (int) idealIndex;
      }
      return calculateInsertionOrderIndex(currentEndIndex, Integer.MAX_VALUE);
   }

   public static int calculateInsertionOrderIndex(int afterIndex, int beforeIndex) {
      return (int) ((long) (afterIndex) + beforeIndex) / 2;
   }

   private void convert_single_relation(BranchId branchId, RelationTypeToken oldRelationType,
      RelationTypeToken newRelationType, boolean initial) {
      JdbcConnection connection = orcsApi.getJdbcService().getClient().getConnection();

      RelationTypeSide sideB = new RelationTypeSide(oldRelationType, RelationSide.SIDE_B);
      String getCurrentRelations = "select distinct artA.art_id " //
         + "from osee_txs txs, osee_relation_link rel, osee_txs aTxs, osee_artifact artA " //
         + "where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id and rel.rel_link_type_id = ? " //
         + "and aTxs.branch_id = ? and aTxs.tx_current = ? and aTxs.gamma_id = artA.gamma_id and artA.art_id = rel.a_art_id ";
      if (initial) {
         getCurrentRelations =
            getCurrentRelations + " and not exists (select null from osee_relation rel2 where rel2.a_art_id = rel.a_art_id and rel2.rel_type = ? )";
      }
      String getConvertedRelations = //
         "select b_art_id, rel_order " + //
            "from osee_txs txs, osee_relation rel " + //
            "where txs.branch_id = ? and txs.tx_current = ? and txs.gamma_id = rel.gamma_id and rel.rel_type = ? and rel.a_art_id = ? " + //
            " order by rel_order ";

      List<ArtifactId> artIds = new ArrayList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         artIds.add(ArtifactId.valueOf(stmt.getLong("art_id")));
      };
      if (initial) {
         orcsApi.getJdbcService().getClient().runQuery(consumer, getCurrentRelations, branchId.getId(),
            TxCurrent.CURRENT.getId(), oldRelationType.getId(), branchId.getId(), TxCurrent.CURRENT.getId(),
            newRelationType.getId());
      } else {
         orcsApi.getJdbcService().getClient().runQuery(consumer, getCurrentRelations, branchId.getId(),
            TxCurrent.CURRENT.getId(), oldRelationType.getId(), branchId.getId(), TxCurrent.CURRENT.getId());
      }
      System.out.println(artIds.size() + " a_art_ids for relation " + oldRelationType.getName());

      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(branchId).andIds(
         artIds).getResults().getList()) {
         if (art.getRelatedCount(sideB) > 0) {
            //System.out.println("converting rels for " + art.getArtifactId().getIdString() + " name: " + art.getName());
            OseePreparedStatement addressing = orcsApi.getJdbcService().getClient().getBatchStatement(connection,
               OseeDb.RELATION_TABLE2.getInsertSql());

            int relOrder = 0;
            int nextRelOrder = -1;
            int currentOrder = 0;
            CombinedStructure convertedRels = new CombinedStructure();
            if (!initial) {
               Consumer<JdbcStatement> convertedConsumer = stmt -> {
                  convertedRels.add(
                     new ArtIdRelOrder(ArtifactId.valueOf(stmt.getInt("b_art_id")), stmt.getInt("rel_order")));
               };
               orcsApi.getJdbcService().getClient().runQuery(convertedConsumer, getConvertedRelations, branchId.getId(),
                  TxCurrent.CURRENT.getId(), newRelationType.getId(), art.getId());
            }
            List<IRelationLink> list = art.getRelations(sideB).getList();
            ArtIdRelOrder previousArt = null;
            art.getIdString();
            if (art.getName().startsWith("Product")) {
               int i = 1;
            }
            for (ArtifactReadable bArt : art.getRelatedList(sideB)) {

               GammaId gammaId = list.stream().filter(a -> a.isOfType(oldRelationType) && a.getArtifactIdB().equals(
                  bArt.getArtifactId().getId())).findFirst().get().getGammaId();

               ArtIdRelOrder existingArtB = convertedRels.getByArtId(bArt.getArtifactId());
               ArtIdRelOrder newArtB = null;
               if (existingArtB != null) {
                  previousArt = existingArtB;
                  relOrder = existingArtB.relOrder;
                  currentOrder = relOrder;

                  ArtIdRelOrder nextById2 = convertedRels.getNextByArtId(previousArt.artId);
                  if (nextById2 != null) {
                     nextRelOrder = nextById2.relOrder;
                  } else {
                     nextRelOrder = -1;
                  }
               } else {
                  if (newRelationType.getMultiplicity().equals(
                     RelationTypeMultiplicity.MANY_TO_MANY) || newRelationType.getMultiplicity().equals(
                        RelationTypeMultiplicity.ONE_TO_MANY)) {
                     if (nextRelOrder > 0) {
                        relOrder = calculateInsertionOrderIndex(currentOrder, nextRelOrder);
                     } else {
                        relOrder = calculateEndInsertionOrderIndex(currentOrder);
                     }
                     currentOrder = relOrder;
                     if (!initial) {
                        newArtB = new ArtIdRelOrder(bArt.getArtifactId(), relOrder);
                        convertedRels.add(newArtB);
                     }
                     addressing.addToBatch(newRelationType.getId(), art.getId(), bArt.getArtifactId().getId(), -1L,
                        relOrder, gammaId.getId());
                     if (previousArt == null) {
                        previousArt = newArtB;
                     }
                  }
               }

            }

            if (addressing.size() > 0) {
               addressing.execute();
            }

         }
      }
      connection.close();

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

   class ArtIdRelOrder {
      ArtifactId artId;
      int relOrder;

      public ArtIdRelOrder(ArtifactId id1, int id2) {
         this.artId = id1;
         this.relOrder = id2;
      }

      @Override
      public String toString() {
         return "(" + artId + ", " + relOrder + ")";
      }

      // equals and hashCode based on id1 (ArtifactId)
      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (!(o instanceof ArtIdRelOrder)) {
            return false;
         }
         ArtIdRelOrder other = (ArtIdRelOrder) o;
         return Objects.equals(this.artId, other.artId);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(artId);
      }
   }

   class CombinedStructure {
      private final Map<ArtifactId, ArtIdRelOrder> mapByArtId = new HashMap<>();
      private final TreeSet<ArtIdRelOrder> setByRelOrder;

      public CombinedStructure() {
         setByRelOrder =
            new TreeSet<>(Comparator.comparingInt((ArtIdRelOrder o) -> o.relOrder).thenComparing(o -> o.artId.getId())); // compare by id2, then by ArtifactId.id
      }

      public void add(ArtIdRelOrder obj) {
         ArtIdRelOrder existing = mapByArtId.get(obj.artId);
         if (existing != null) {
            setByRelOrder.remove(existing);
         }
         mapByArtId.put(obj.artId, obj);
         setByRelOrder.add(obj);
      }

      public ArtIdRelOrder getByArtId(ArtifactId id1) {
         return mapByArtId.get(id1);
      }

      public ArtIdRelOrder getNextByArtId(ArtifactId id1) {
         ArtIdRelOrder obj = mapByArtId.get(id1);
         if (obj == null) {
            return null;
         }
         return setByRelOrder.higher(obj);
      }

   }

}
