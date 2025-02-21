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

package org.eclipse.osee.testscript.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.OrcsPurgeResult;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderData;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.testscript.DashboardApi;
import org.eclipse.osee.testscript.DeleteResultsData;
import org.eclipse.osee.testscript.ResultToPurge;
import org.eclipse.osee.testscript.ScriptConfigApi;
import org.eclipse.osee.testscript.ScriptConfigToken;
import org.eclipse.osee.testscript.ScriptPurgeApi;

public class ScriptPurgeApiImpl implements ScriptPurgeApi {
   private final OrcsApi orcsApi;
   private final ScriptConfigApi configApi;
   private final DashboardApi dashboardApi;

   public ScriptPurgeApiImpl(OrcsApi orcsApi, ScriptConfigApi configApi, DashboardApi dashboardApi) {
      this.orcsApi = orcsApi;
      this.configApi = configApi;
      this.dashboardApi = dashboardApi;
   }

   @Override
   public TransactionResult purgeResults(BranchId branch, boolean deleteOnly) {
      orcsApi.userService().requireRole(CoreUserGroups.AccountAdmin);

      TransactionResult txResult = new TransactionResult();

      Branch branchObj =
         orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
      if (branchObj.isInvalid()) {
         txResult.getResults().error("Invalid branch id");
         return txResult;
      }
      if (!branchObj.getBranchType().equals(BranchType.WORKING)) {
         txResult.getResults().errorf("Branch is of type %s. Can only purge from type %s",
            branchObj.getBranchType().getName(), BranchType.WORKING.getName());
         return txResult;
      }

      ScriptConfigToken config = this.configApi.get(branch);
      if (config.getArtifactId().isInvalid()) {
         txResult.getResults().error("No Zenith configuration artifact was found");
         return txResult;
      }
      int resultsToKeep = config.getTestResultsToKeep().getValue();
      if (resultsToKeep < 1) {
         txResult.getResults().log("Number of results to keep is less than 1. No results were purged.");
         return txResult;
      }
      return purgeResults(branch, branchObj.getParentBranch(), resultsToKeep, deleteOnly);
   }

   private TransactionResult purgeResults(BranchId branch, BranchId parentBranch, int numberToKeep,
      boolean deleteOnly) {
      Collection<ResultToPurge> results = getResultsForBranch(branch);
      Map<ArtifactId, Map<String, List<ResultToPurge>>> resultsBySet = getResultsBySet(results);
      DeleteResultsData deleteData = getResultsToDelete(resultsBySet, numberToKeep);

      for (ArtifactId ciSet : deleteData.getCiSets()) {
         this.dashboardApi.updateTimelineStats(branch, ciSet);
      }

      TransactionResult txResult = deleteResults(branch, deleteData.getResults());
      if (deleteOnly || txResult.getResults().isErrors()) {
         return txResult;
      }

      Collection<ResultToPurge> deletedResults = getDeletedResults(branch);
      List<ArtifactId> deletedArts = deletedResults.stream().map(r -> r.getArtId()).collect(Collectors.toList());

      List<ArtifactReadable> parentArts =
         orcsApi.getQueryFactory().fromBranch(parentBranch).andIds(deletedArts).asArtifacts();

      // Results are purged if they meet one of these conditions:
      // 1. The result is deleted on the parent branch
      // 2. The result does not exist on the parent branch
      List<ResultToPurge> resultsToPurge = new LinkedList<>();
      for (ResultToPurge result : deletedResults) {
         ArtifactReadable parentArt =
            parentArts.stream().filter(a -> a.getArtifactId().equals(result.getArtId())).findFirst().orElse(
               ArtifactReadable.SENTINEL);
         if (parentArt.isInvalid() || parentArt.isDeleted()) {
            resultsToPurge.add(result);
         }
      }

      purgeResults(resultsToPurge);

      return txResult;
   }

   private Collection<ResultToPurge> getResultsForBranch(BranchId branch) {
      // @formatter:off
      String query = "SELECT art.art_id, attr.attr_type_id, attr.value FROM osee_txs txs, osee_artifact art, osee_attribute attr "
         + "   WHERE txs.branch_id = ? " // branch
         + "   AND art.art_type_id = 8756764538 "
         + "   AND txs.tx_current = 1 "
         + "   AND txs.gamma_id = attr.gamma_id "
         + "   AND attr.art_id = art.art_id "
         + "   AND attr.attr_type_id IN (1152921504606847088, 1152921504606847365, 1152921504606847350)";
      // @formatter:on

      Map<ArtifactId, ResultToPurge> results = new HashMap<>();

      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
         ResultToPurge result = results.getOrDefault(artId, new ResultToPurge());
         result.setArtId(artId);

         AttributeTypeId attrType = AttributeTypeId.valueOf(stmt.getLong("attr_type_id"));
         String value = stmt.getString("value");

         if (CoreAttributeTypes.Name.equals(attrType)) {
            result.setName(value);
         } else if (CoreAttributeTypes.ExecutionDate.equals(attrType)) {
            result.setExecutionDate(new Date(Long.parseLong(value)));
         } else if (CoreAttributeTypes.SetId.equals(attrType)) {
            result.setCiSet(ArtifactId.valueOf(value));
         }
         results.put(artId, result);
      };

      this.orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch);

      return results.values();
   }

   @Override
   public Collection<ResultToPurge> getDeletedResults(BranchId branch) {
      // @formatter:off
      String query = "SELECT art.art_id, attr.attr_type_id, attr.value FROM osee_txs txs, osee_artifact art, osee_attribute attr "
         + "   WHERE txs.branch_id = ? " // branch
         + "   AND txs.mod_type = 3 "
         + "   AND art.art_type_id = 8756764538 "
         + "   AND art.gamma_id = txs.gamma_id "
         + "   AND attr.art_id = art.art_id "
         + "   AND attr.attr_type_id IN  (1152921504606847088, 1152921504606847100)";
      // @formatter:on

      Map<ArtifactId, ResultToPurge> results = new HashMap<>();

      Consumer<JdbcStatement> consumer = stmt -> {
         ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
         ResultToPurge result = results.getOrDefault(artId, new ResultToPurge());
         result.setArtId(artId);

         AttributeTypeId attrType = AttributeTypeId.valueOf(stmt.getLong("attr_type_id"));
         String value = stmt.getString("value");

         if (CoreAttributeTypes.Name.equals(attrType)) {
            result.setName(value);
         } else if (CoreAttributeTypes.ContentUrl.equals(attrType)) {
            result.setFilePath(value);
         } else if (CoreAttributeTypes.ExecutionDate.equals(attrType)) {
            result.setExecutionDate(new Date(Long.parseLong(value)));
         } else if (CoreAttributeTypes.SetId.equals(attrType)) {
            result.setCiSet(ArtifactId.valueOf(value));
         }
         results.put(artId, result);
      };

      this.orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch);

      return results.values();
   }

   private Map<ArtifactId, Map<String, List<ResultToPurge>>> getResultsBySet(Collection<ResultToPurge> results) {
      Map<ArtifactId, Map<String, List<ResultToPurge>>> resultsBySet = new HashMap<>();
      for (ResultToPurge result : results) {
         Map<String, List<ResultToPurge>> scripts = resultsBySet.getOrDefault(result.getCiSet(), new HashMap<>());
         List<ResultToPurge> scriptResults = scripts.getOrDefault(result.getName(), new LinkedList<>());
         scriptResults.add(result);
         scripts.put(result.getName(), scriptResults);
         resultsBySet.put(result.getCiSet(), scripts);
      }
      return resultsBySet;
   }

   private DeleteResultsData getResultsToDelete(Map<ArtifactId, Map<String, List<ResultToPurge>>> resultsBySet,
      int numberToKeep) {
      List<ResultToPurge> resultsToDelete = new LinkedList<>();
      Set<ArtifactId> ciSets = new HashSet<>();
      for (Map<String, List<ResultToPurge>> resultsByScript : resultsBySet.values()) {
         for (List<ResultToPurge> results : resultsByScript.values()) {
            if (results.size() > numberToKeep) {
               results.sort(new Comparator<ResultToPurge>() {
                  @Override
                  public int compare(ResultToPurge o1, ResultToPurge o2) {
                     return o1.getExecutionDate().compareTo(o2.getExecutionDate());
                  }
               });
               for (int i = 0; i < results.size() - numberToKeep; i++) {
                  ResultToPurge result = results.get(i);
                  resultsToDelete.add(result);
                  ciSets.add(result.getCiSet());
               }
            }
         }
      }
      return new DeleteResultsData(resultsToDelete, ciSets);
   }

   private TransactionResult deleteResults(BranchId branch, List<ResultToPurge> results) {
      TransactionResult txResult = new TransactionResult();

      if (results.isEmpty()) {
         txResult.getResults().log("No results were deleted");
         return txResult;
      }

      ObjectMapper mapper = new ObjectMapper();
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);
      TransactionBuilderData txData = new TransactionBuilderData();
      txData.setBranch(branch.getIdString());
      txData.setTxComment("Delete results - " + branch.getIdString());
      txData.setDeleteArtifacts(results.stream().map(res -> res.getArtId().getId()).collect(Collectors.toList()));

      try {
         TransactionBuilder tx = txBdf.loadFromJson(mapper.writeValueAsString(txData));
         TransactionToken token = tx.commit();
         txResult.setTx(token);
         txResult.getResults().setIds(
            tx.getTxDataReadables().stream().map(readable -> readable.getIdString()).collect(Collectors.toList()));
      } catch (JsonProcessingException ex) {
         txResult.getResults().error("Error processing tx json");
      }

      return txResult;
   }

   private TransactionResult purgeResults(List<ResultToPurge> results) {
      TransactionResult txResult = new TransactionResult();
      for (ResultToPurge result : results) {
         if (result.getFilePath() != null && !result.getFilePath().isEmpty()) {
            File tmo = new File(result.getFilePath());
            if (tmo.exists()) {
               File parent = tmo.getParentFile();
               tmo.delete();
               if (parent.isDirectory()) {
                  boolean parentIsEmpty = false;
                  try (DirectoryStream<Path> directory = Files.newDirectoryStream(parent.toPath())) {
                     parentIsEmpty = !directory.iterator().hasNext();
                  } catch (IOException ex) {
                     txResult.getResults().errorf("Error accessing directory: %s", parent.toPath().toString());
                  }
                  if (parentIsEmpty) {
                     parent.delete();
                  }
               }
            }
         }
         OrcsPurgeResult purgeResult = orcsApi.getPurgeOps().purgeArtifact(result.getArtId());
         if (purgeResult.isError()) {
            txResult.getResults().error(purgeResult.getMessage());
         } else {
            txResult.getResults().log(purgeResult.getMessage());
         }
      }
      return txResult;
   }

}
