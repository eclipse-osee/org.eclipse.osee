/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.BRANCH_OPERATION;
import static org.eclipse.osee.framework.jdk.core.util.Compare.isDifferent;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransaction;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransactions;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonAttribute;
import org.eclipse.osee.framework.core.data.JsonRelation;
import org.eclipse.osee.framework.core.data.JsonRelations;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.internal.branch.UpdateBranchOperation;
import org.eclipse.osee.orcs.rest.model.BranchCommitOptions;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchQueryData;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.NewTransaction;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class BranchEndpointImpl implements BranchEndpoint {

   private final OrcsApi orcsApi;
   private final ActivityLog activityLog;
   private final OrcsBranch branchOps;
   private static final String OTHER_EDIT_SQL =
      "select txs.mod_type, br.branch_id from osee_attribute att, osee_txs txs, osee_branch br where att.art_id = ? and att.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and txs.transaction_id <> br.baseline_transaction_id and txs.tx_current <> 0 and  br.branch_id <> ? and br.branch_type = ? and br.branch_state = ? AND NOT EXISTS (SELECT 1 FROM osee_txs txs1 WHERE txs1.branch_id = br.branch_id AND txs1.transaction_id = br.baseline_transaction_id AND txs1.gamma_id = txs.gamma_id AND txs1.mod_type = txs.mod_type)";

   @Context
   private UriInfo uriInfo;

   @Context
   private HttpHeaders httpHeaders;

   public BranchEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.activityLog = activityLog;
      this.branchOps = orcsApi.getBranchOps();
   }

   public HttpHeaders getHeaders() {
      return httpHeaders;
   }

   public void setHeaders(HttpHeaders httpHeaders) {
      this.httpHeaders = httpHeaders;
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public UriInfo getUriInfo() {
      return uriInfo;
   }

   private QueryFactory newQuery() {
      return orcsApi.getQueryFactory();
   }

   private BranchQuery newBranchQuery() {
      return newQuery().branchQuery();
   }

   private TransactionQuery newTxQuery() {
      return newQuery().transactionQuery();
   }

   private TransactionReadable getTxByBranchAndId(BranchId branch, TransactionId txId) {
      return newTxQuery().andBranch(branch).andTxId(txId).getResults().getExactlyOne();
   }

   private TransactionFactory newTxFactory() {
      return orcsApi.getTransactionFactory();
   }

   @Override
   public List<Branch> getBranches(BranchQueryData options) {
      ResultSet<Branch> results = searchBranches(options);
      return results.getList();
   }

   @Override
   public List<Branch> getBranches(String branchUuids, String branchTypes, String branchStates, boolean deleted, boolean archived, String nameEquals, String namePattern, Long childOf, Long ancestorOf, BranchCategoryToken category) {
      BranchQueryData options = new BranchQueryData();
      options.setBranchIds(Collections.fromString(branchUuids, ",", BranchId::valueOf));

      if (Strings.isValid(branchTypes)) {
         List<BranchType> branchTypeVals = new LinkedList<>();
         for (String branchType : branchTypes.split(",")) {
            branchTypeVals.add(BranchType.fromName(branchType.toUpperCase()));
         }
         options.setBranchTypes(branchTypeVals);
      }

      if (Strings.isValid(branchStates)) {
         List<BranchState> branchStateVals = new LinkedList<>();
         for (String branchState : branchStates.split(",")) {
            branchStateVals.add(BranchState.fromName(branchState.toUpperCase()));
         }
         options.setBranchStates(branchStateVals);
      }

      options.setIncludeDeleted(deleted);
      options.setIncludeArchived(archived);

      if (Strings.isValid(nameEquals)) {
         options.setNameEquals(nameEquals);
      }

      if (Strings.isValid(namePattern)) {
         options.setNamePattern(namePattern);
      }

      if (childOf != null) {
         options.setIsChildOf(childOf);
      }

      if (ancestorOf != null) {
         options.setIsAncestorOf(ancestorOf);
      }

      if (category != null) {
         options.setCategory(category);
      }
      return getBranches(options);
   }

   @Override
   public List<Branch> getBaselineBranches() {
      return newBranchQuery().includeArchived(false).includeDeleted(false).andIsOfType(
         BranchType.BASELINE).getResults().getList();
   }

   @Override
   public List<Branch> getWorkingBranches() {
      return newBranchQuery().includeArchived(false).includeDeleted(false).andIsOfType(
         BranchType.WORKING).getResults().getList();
   }

   @Override
   public Branch getBranchById(BranchId branch) {
      return newBranchQuery().andId(branch).includeArchived().includeDeleted().getResults().getExactlyOne();
   }

   @Override
   public List<Branch> getBranchesByCategory(BranchCategoryToken id) {
      return newBranchQuery().andIsOfCategory(id).getResults().getList();
   }

   @Override
   public List<BranchCategoryToken> getBranchCategories(BranchId branch) {
      return newBranchQuery().getBranchCategories(branch);
   }

   @Override
   public XResultData setBranchCategory(BranchId branch, BranchCategoryToken category) {
      return branchOps.setBranchCategory(branch, category);
   }

   @Override
   public XResultData deleteBranchCategory(BranchId branch, BranchCategoryToken category) {
      return branchOps.deleteBranchCategory(branch, category);
   }

   @Override
   public List<Transaction> getAllBranchTxs(BranchId branch) {
      return asTransactions(newTxQuery().andBranch(branch).getResults());
   }

   @Override
   public Transaction getBranchTx(BranchId branchUuid, TransactionId txId) {
      return asTransaction(getTxByBranchAndId(branchUuid, txId));
   }

   @Override
   public List<JsonArtifact> getArtifactDetailsByType(BranchId branchId, String artifactTypes) {
      return getArtifactDetailsByType(branchId, ArtifactId.SENTINEL, artifactTypes);
   }

   @Override
   public List<JsonArtifact> getArtifactDetailsByType(BranchId branchId, ArtifactId viewId, String artifactTypes) {
      List<JsonArtifact> arts = new ArrayList<JsonArtifact>();
      List<Long> typesLong = Collections.fromString(artifactTypes, ",", Long::valueOf);
      List<ArtifactTypeToken> types = new ArrayList<ArtifactTypeToken>();

      for (Long typeId : typesLong) {
         types.add(orcsApi.tokenService().getArtifactType(typeId));
      }

      List<ArtifactReadable> artifacts = null;
      if (viewId.isValid()) {
         artifacts = orcsApi.getQueryFactory().fromBranch(branchId, viewId).andIsOfType(types).getResults().getList();
      } else {
         artifacts = orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(types).getResults().getList();
      }
      for (ArtifactReadable art : artifacts) {
         JsonArtifact jArt = new JsonArtifact();
         jArt.setType(art.getArtifactType());
         jArt.setTypeName(art.getArtifactType().getName());
         jArt.setId(ArtifactId.create(art));
         jArt.setName(art.getName());
         List<JsonAttribute> attrList = new ArrayList<JsonAttribute>();
         List<? extends AttributeReadable<Object>> list = art.getAttributes().getList();
         for (AttributeReadable<?> attr : list) {
            JsonAttribute attrRep = new JsonAttribute();
            attrRep.setTypeId(attr.getAttributeType());
            attrRep.setTypeName(attr.getAttributeType().getName());
            if (attr.getAttributeType().getMediaType().startsWith("application")) {
               attrRep.setValue("<large>");
            } else {
               attrRep.setValue(attr.getDisplayableString());
            }
            attrList.add(attrRep);
         }
         jArt.setAttrs(attrList);
         arts.add(jArt);
      }
      return arts;
   }

   @Override
   public List<ChangeItem> compareBranches(BranchId branch, BranchId branch2) {
      TransactionToken sourceTx = newTxQuery().andIsHead(branch).getResults().getExactlyOne();
      TransactionToken destinationTx = newTxQuery().andIsHead(branch2).getResults().getExactlyOne();
      List<ChangeItem> data = branchOps.compareBranch(sourceTx, destinationTx);

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Compare Branches {sourceTx: %s, destTx: %s}", sourceTx.toString(),
               destinationTx.toString()));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return data;
   }

   @Override
   public BranchId createBranch(NewBranch data) {
      return createBranch(new CreateBranchData(), data);
   }

   @Override
   public BranchId createBranchWithId(BranchId branch, NewBranch data) {
      if (branch.isInvalid()) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "branchUuid [%d] uuid must be > 0", branch);
      }

      CreateBranchData createData = new CreateBranchData(branch);
      return createBranch(createData, data);
   }

   @Override
   public XResultData createBranchValidation(NewBranch data) {
      CreateBranchData branchData = new CreateBranchData();
      createBranchData(branchData, data);
      return branchOps.createBranchValidation(branchData);
   }

   @Override
   public UpdateBranchData updateBranch(BranchId branch, UpdateBranchData branchData) {
      UpdateBranchOperation op = new UpdateBranchOperation(branchData, orcsApi);
      return op.run();
   }

   private BranchId createBranch(CreateBranchData createData, NewBranch data) {
      createBranchData(createData, data);

      Branch result = branchOps.createBranch(createData);

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Create Branch {branchId: %s, branchName: %s}", createData.getBranch(),
               data.getBranchName()));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return result;
   }

   private void createBranchData(CreateBranchData createData, NewBranch data) {
      createData.setName(data.getBranchName());
      createData.setBranchType(data.getBranchType());
      createData.setCreationComment(data.getCreationComment());

      createData.setAssociatedArtifact(data.getAssociatedArtifact());

      createData.setFromTransaction(data.getSourceTransaction());
      createData.setParentBranch(data.getParentBranch());

      createData.setMergeDestinationBranchId(data.getMergeDestinationBranchId());
      createData.setMergeAddressingQueryId(data.getMergeAddressingQueryId());

      createData.setTxCopyBranchType(data.isTxCopyBranchType());
   }

   @Override
   public TransactionResult commitBranch(BranchId branch, BranchId destinationBranch, BranchCommitOptions options) {
      Branch srcBranch = getBranchById(branch);
      Branch destBranch = getBranchById(destinationBranch);
      TransactionResult tr = new TransactionResult();
      tr.getResults().logf("Commiting Branch\n");
      tr.getResults().logf("Source Branch [%s]\n", branch);
      tr.getResults().logf("Destination Branch [%s]\n", destBranch);
      tr.getResults().logf("Options [%s]\n\n", options);

      Callable<TransactionToken> op = branchOps.commitBranch(options.getCommitter(), srcBranch, destBranch);
      try {
         TransactionToken tx = executeCallable(op);
         tr.setTx(tx);
      } catch (Exception ex) {
         tr.getResults().errorf("Exception commiting branch [%s]", Lib.exceptionToString(ex));
         return tr;
      }

      if (options.isArchive()) {
         Callable<?> op2 = branchOps.archiveUnarchiveBranch(srcBranch, ArchiveOperation.ARCHIVE);
         executeCallable(op2);
      }

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Commit Branch {branchId: %s srcBranch: %s destBranch: %s}", branch,
               srcBranch, destBranch));
      } catch (OseeCoreException ex) {
         tr.getResults().errorf("Exception logging activity [%s]", Lib.exceptionToString(ex));
         return tr;
      }
      return tr;
   }

   @Override
   public Response archiveBranch(BranchId branchId) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (!branch.isArchived()) {
         try {
            Callable<?> op = branchOps.archiveUnarchiveBranch(branch, ArchiveOperation.ARCHIVE);
            executeCallable(op);
         } catch (Exception ex) {
            orcsApi.getActivityLog().createThrowableEntry(CoreActivityTypes.BRANCH_OPERATION, ex,
               "Archive of branch " + branchId.getIdString());
            return asResponse(modified);
         }
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Archive Branch {branchId: %s}", branchId));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response unarchiveBranch(BranchId branchId) {
      Branch branch = getBranchById(branchId);

      boolean modified = false;
      if (branch.isArchived()) {
         try {
            Callable<?> op = branchOps.archiveUnarchiveBranch(branch, ArchiveOperation.UNARCHIVE);
            executeCallable(op);
         } catch (Exception ex) {
            orcsApi.getActivityLog().createThrowableEntry(CoreActivityTypes.BRANCH_OPERATION, ex,
               "Unarchive of branch " + branchId.getIdString());
            return asResponse(modified);
         }
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Unarchive Branch {branchId: %s}", branchId));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response writeTx(BranchId branch, NewTransaction data) {
      String comment = data.getComment();

      TransactionFactory txFactory = newTxFactory();
      TransactionBuilder txBuilder = txFactory.createTransaction(branch, null, comment);

      //TODO: Integrate data with TxBuilder

      TransactionToken txId = txBuilder.commit();
      TransactionReadable tx = orcsApi.getQueryFactory().transactionQuery().andTxId(txId).getResults().getExactlyOne();

      if (tx.isValid()) {
         URI location = uriInfo.getRequestUriBuilder().path("{tx-id}").build(tx);
         return Response.created(location).entity(asTransaction(tx)).build();
      } else {
         throw new OseeArgumentException("No Data Modified");
      }
   }

   @Override
   public Response setBranchName(BranchId branchId, String newName) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getName(), newName)) {
         Callable<?> op = branchOps.changeBranchName(branch, newName);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Set Branch Name {branchId: %s prevName: %s newName: %s}", branchId,
                  branch.getName(), newName));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response setBranchType(BranchId branchId, BranchType newType) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getBranchType(), newType)) {
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Set Branch Type {branchUUID: %s prevType: %s newType: %s}", branchId,
                  branch.getBranchType(), newType));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Callable<?> op = branchOps.changeBranchType(branch, newType);
         executeCallable(op);
         modified = true;
      }
      return asResponse(modified);
   }

   @Override
   public Response setBranchState(BranchId branchId, BranchState newState) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getBranchState(), newState)) {
         Callable<?> op = branchOps.changeBranchState(branch, newState);
         executeCallable(op);
         modified = true;

         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Branch State Changed {branchId: %s prevState: %s newState: %s}",
                  branchId, branch.getBranchType(), newState));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission) {
      branchOps.setBranchPermission(subject, branch, permission);
   }

   @Override
   public Response associateBranchToArtifact(BranchId branchId, ArtifactId artifact) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getAssociatedArtifact(), artifact)) {
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Associate Branch to Artifact {branchId: %s prevArt: %s newArt: %s}",
                  branchId, branch.getAssociatedArtifact(), artifact));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Callable<?> op = branchOps.associateBranchToArtifact(branch, artifact);
         executeCallable(op);
         modified = true;
      }
      return asResponse(modified);
   }

   @Override
   public Response setTxComment(BranchId branch, TransactionId txId, String comment) {
      TransactionReadable tx = getTxByBranchAndId(branch, txId);
      boolean modified = false;
      if (Compare.isDifferent(tx.getComment(), comment)) {
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Set Tx Comment {branchId: %s prevComment: %s newComment: %s}", branch,
                  tx.getComment(), comment));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
         TransactionFactory txFactory = newTxFactory();
         txFactory.setTransactionComment(tx, comment);
         modified = true;
      }
      return asResponse(modified);
   }

   @Override
   public Response purgeBranch(BranchId branchId, boolean recurse) {
      boolean modified = false;
      Branch branch = getBranchById(branchId);
      if (branch != null) {
         Callable<?> op = branchOps.purgeBranch(branch, recurse);
         executeCallable(op);
         modified = true;
      }

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Purge Branch {branchId: %s}", branchId));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return asResponse(modified);
   }

   @Override
   public Response unCommitBranch(BranchId branch, BranchId destinationBranch) {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   @Override
   public Response unassociateBranch(BranchId branchId) {
      Branch branch = getBranchById(branchId);
      boolean modified = false;
      if (branch.getAssociatedArtifact().isValid()) {
         Callable<?> op = branchOps.unassociateBranch(branch);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Unassociate Branch {branchId: %s}", branchId));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response purgeTxs(BranchId branch, String txIds) {
      boolean modified = false;
      List<TransactionId> txsToDelete = Collections.fromString(txIds, TransactionId::valueOf);
      if (!txsToDelete.isEmpty()) {
         ResultSet<? extends TransactionId> results = newTxQuery().andBranch(branch).andTxIds(txsToDelete).getResults();
         if (!results.isEmpty()) {
            checkAllTxFoundAreOnBranch("Purge Transaction", branch, txsToDelete, results);
            List<TransactionId> list = Lists.newArrayList(results);
            Callable<?> op = newTxFactory().purgeTransaction(list);
            executeCallable(op);
            modified = true;

            try {
               activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
                  String.format("Branch Operation Purge Txs {branchId: %s, txsToDelete: %s}", branch, txIds));
            } catch (OseeCoreException ex) {
               OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
      return asResponse(modified);
   }

   private void checkAllTxFoundAreOnBranch(String opName, BranchId branch, List<TransactionId> txIds, ResultSet<? extends TransactionId> result) {
      if (txIds.size() != result.size()) {
         List<TransactionId> difference = Collections.setComplement(txIds, result.getList());
         if (!difference.isEmpty()) {
            throw new OseeWebApplicationException(Status.BAD_REQUEST,
               "%s Error - The following transactions from %s were not found on branch [%s] - txs %s - Please remove them from the request and try again.",
               opName, txIds, branch, difference);
         }
      }
   }

   private ResultSet<Branch> searchBranches(BranchQueryData options) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();

      if (options != null) {
         Collection<BranchId> branchIds = options.getBranchIds();
         if (Conditions.hasValues(branchIds)) {
            query.andIds(branchIds);
         }

         Collection<BranchState> branchStates = options.getBranchStates();
         if (Conditions.hasValues(branchStates)) {
            query.andStateIs(branchStates.toArray(new BranchState[branchStates.size()]));
         }

         Collection<BranchType> branchTypes = options.getBranchTypes();
         if (Conditions.hasValues(branchTypes)) {
            query.andIsOfType(branchTypes.toArray(new BranchType[branchTypes.size()]));
         }

         if (options.isIncludeArchived()) {
            query.includeArchived();
         } else {
            query.excludeArchived();
         }

         if (options.isIncludeDeleted()) {
            query.includeDeleted();
         } else {
            query.excludeDeleted();
         }

         String nameEquals = options.getNameEquals();
         if (Strings.isValid(nameEquals)) {
            query.andNameEquals(nameEquals);
         }

         String namePattern = options.getNamePattern();
         if (Strings.isValid(namePattern)) {
            query.andNamePattern(namePattern);
         }

         String namePatternIgnoreCase = options.getNamePatternIgnoreCase();
         if (Strings.isValid(namePatternIgnoreCase)) {
            query.andNamePatternIgnoreCase(namePatternIgnoreCase);
         }

         Long ancestorOf = options.getIsAncestorOf();
         if (ancestorOf > 0) {
            BranchId ancestorOfToken = BranchId.valueOf(ancestorOf);
            query.andIsAncestorOf(ancestorOfToken);
         }

         Long childOf = options.getIsChildOf();
         if (childOf > 0) {
            BranchId childOfToken = BranchId.valueOf(childOf);
            query.andIsChildOf(childOfToken);
         }
         BranchCategoryToken category = options.getCategory();
         if (category.isValid()) {
            query.andIsOfCategory(category);
         }
      }

      return query.getResults();
   }

   @Override
   public Response logBranchActivity(String comment) {
      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS, comment);
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return Response.ok().build();
   }

   @Override
   public BranchToken createProgramBranch(BranchId branch, String branchName) {
      BranchToken branchToken =
         branch.isValid() ? BranchToken.create(branch, branchName) : BranchToken.create(branchName);
      return branchOps.createProgramBranch(branchToken);
   }

   @Override
   public JsonRelations getRelationsByType(BranchId branch, String relationTypes) {

      String query = "select * from osee_relation_link rel, osee_txs txs " //
         + "where txs.branch_id = ? " //
         + "and rel.gamma_id = txs.gamma_id " //
         + "and rel_link_type_id = ? " //
         + "and txs.mod_type in (1,2,6) " //
         + "and tx_current = 1";

      JsonRelations relations = new JsonRelations();
      Set<ArtifactId> artIds = new HashSet<>();
      for (String relTypeId : relationTypes.split(",")) {
         relTypeId = relTypeId.replaceAll(" ", "");
         if (Strings.isNumeric(relTypeId)) {
            orcsApi.getJdbcService().getClient().runQuery(chStmt -> relations.add(getJaxRelation(chStmt, artIds)),
               query, branch.getIdString(), relTypeId);
         }
      }

      Map<ArtifactId, ArtifactReadable> artifactMap = new HashMap<ArtifactId, ArtifactReadable>();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(branch).andIds(artIds).getResults().getList()) {
         artifactMap.put(art, art);
      }
      for (JsonRelation rel : relations.getRelations()) {
         RelationTypeToken relationType = orcsApi.tokenService().getRelationType(Long.valueOf(rel.getTypeId()));
         rel.setTypeName(relationType.getName());
         ArtifactReadable art = artifactMap.get(ArtifactId.valueOf(rel.getArtA()));
         rel.setArtAName(art.getName());
         ArtifactReadable art2 = artifactMap.get(ArtifactId.valueOf(rel.getArtB()));
         rel.setArtBName(art2.getName());
      }
      return relations;
   }

   private JsonRelation getJaxRelation(JdbcStatement chStmt, Set<ArtifactId> artIds) {
      JsonRelation rel = new JsonRelation();
      String artA = chStmt.getString("a_art_id");
      artIds.add(ArtifactId.valueOf(artA));
      rel.setArtA(artA);
      String artB = chStmt.getString("b_art_id");
      artIds.add(ArtifactId.valueOf(artB));
      rel.setArtB(artB);
      rel.setTypeId(chStmt.getString("rel_link_type_id"));
      return rel;
   }

   @Override
   public List<BranchId> getOtherBranchesWithModifiedArtifacts(BranchId branchId, ArtifactId artifactId) {

      List<BranchId> setOfBranchIds = new ArrayList<>();

      orcsApi.getJdbcService().getClient().runQuery(chStmt -> setOfBranchIds.add(getBranchId(chStmt)), OTHER_EDIT_SQL,
         artifactId, branchId, BranchType.WORKING, BranchState.MODIFIED);

      return setOfBranchIds;
   }

   private BranchId getBranchId(JdbcStatement chStmt) {

      long modifiedOnBranchId = chStmt.getLong("branch_id");

      return BranchId.valueOf(modifiedOnBranchId);

   }

}
