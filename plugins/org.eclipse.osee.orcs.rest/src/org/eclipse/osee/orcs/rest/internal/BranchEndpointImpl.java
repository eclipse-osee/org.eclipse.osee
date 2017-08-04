/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.BRANCH_OPERATION;
import static org.eclipse.osee.framework.jdk.core.util.Compare.isDifferent;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asBranch;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asBranches;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransaction;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asTransactions;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.CompareResults;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.ExportOptions;
import org.eclipse.osee.orcs.ImportOptions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.Branch;
import org.eclipse.osee.orcs.rest.model.BranchCommitOptions;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchExportOptions;
import org.eclipse.osee.orcs.rest.model.BranchImportOptions;
import org.eclipse.osee.orcs.rest.model.BranchQueryData;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.NewTransaction;
import org.eclipse.osee.orcs.rest.model.Transaction;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.orcs.utility.RestUtil;

/**
 * @author Roberto E. Escobar
 */
public class BranchEndpointImpl implements BranchEndpoint {

   private final OrcsApi orcsApi;
   private final IResourceManager resourceManager;
   private final ActivityLog activityLog;

   @Context
   private UriInfo uriInfo;

   @Context
   private HttpHeaders httpHeaders;

   public BranchEndpointImpl(OrcsApi orcsApi, IResourceManager resourceManager, ActivityLog activityLog) {
      this.orcsApi = orcsApi;
      this.resourceManager = resourceManager;
      this.activityLog = activityLog;
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

   private OrcsBranch getBranchOps() {
      return orcsApi.getBranchOps();
   }

   private BranchReadable getBranchById(BranchId branch) {
      ResultSet<BranchReadable> results = newBranchQuery().andId(branch)//
         .includeArchived()//
         .includeDeleted()//
         .getResults();
      return results.getExactlyOne();
   }

   private TransactionReadable getTxByBranchAndId(BranchId branch, TransactionId txId) {
      return newTxQuery().andBranch(branch).andTxId(txId).getResults().getExactlyOne();
   }

   private TransactionFactory newTxFactory() {
      return orcsApi.getTransactionFactory();
   }

   @Override
   public List<Branch> getBranches() {
      ResultSet<BranchReadable> results = newBranchQuery()//
         .includeArchived()//
         .includeDeleted()//
         .getResults();
      return asBranches(results);
   }

   @Override
   public List<Branch> getBranches(BranchQueryData options) {
      ResultSet<BranchReadable> results = searchBranches(options);
      return asBranches(results);
   }

   @Override
   public List<Branch> getBranches(String branchUuids, String branchTypes, String branchStates, boolean deleted, boolean archived, String nameEquals, String namePattern, Long childOf, Long ancestorOf) {
      BranchQueryData options = new BranchQueryData();
      options.setBranchIds(Collections.fromString(branchUuids, ",", BranchId::valueOf));

      if (Strings.isValid(branchTypes)) {
         List<BranchType> branchTypeVals = new LinkedList<>();
         for (String branchType : branchTypes.split(",")) {
            branchTypeVals.add(BranchType.valueOf(branchType.toUpperCase()));
         }
         options.setBranchTypes(branchTypeVals);
      }

      if (Strings.isValid(branchStates)) {
         List<BranchState> branchStateVals = new LinkedList<>();
         for (String branchState : branchStates.split(",")) {
            branchStateVals.add(BranchState.valueOf(branchState.toUpperCase()));
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
      return getBranches(options);
   }

   @Override
   public List<Branch> getBaselineBranches() {
      ResultSet<BranchReadable> results = newBranchQuery()//
         .includeArchived(false) //
         .includeDeleted(false) //
         .andIsOfType(BranchType.BASELINE)//
         .getResults();
      return asBranches(results);
   }

   @Override
   public List<Branch> getWorkingBranches() {
      ResultSet<BranchReadable> results = newBranchQuery()//
         .includeArchived(false) //
         .includeDeleted(false) //
         .andIsOfType(BranchType.WORKING)//
         .getResults();
      return asBranches(results);
   }

   @Override
   public Branch getBranch(BranchId branch) {
      return asBranch(getBranchById(branch));
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
   public CompareResults compareBranches(BranchId branch, BranchId branch2) {
      TransactionToken sourceTx = newTxQuery().andIsHead(branch).getResults().getExactlyOne();
      TransactionToken destinationTx = newTxQuery().andIsHead(branch2).getResults().getExactlyOne();

      Callable<List<ChangeItem>> op = getBranchOps().compareBranch(sourceTx, destinationTx);
      List<ChangeItem> changes = executeCallable(op);

      CompareResults data = new CompareResults();
      data.setChanges(changes);
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
   public Response createBranch(NewBranch data) {
      return createBranch(new CreateBranchData(), data);
   }

   @Override
   public Response createBranchWithId(BranchId branch, NewBranch data) {
      if (branch.isInvalid()) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "branchUuid [%d] uuid must be > 0", branch);
      }

      CreateBranchData createData = new CreateBranchData(branch);
      return createBranch(createData, data);
   }

   private Response createBranch(CreateBranchData createData, NewBranch data) {
      createData.setName(data.getBranchName());
      createData.setBranchType(data.getBranchType());
      createData.setCreationComment(data.getCreationComment());

      createData.setAuthor(data.getAuthor());
      createData.setAssociatedArtifact(data.getAssociatedArtifact());

      createData.setFromTransaction(data.getSourceTransaction());
      createData.setParentBranch(data.getParentBranch());

      createData.setMergeDestinationBranchId(data.getMergeDestinationBranchId());
      createData.setMergeAddressingQueryId(data.getMergeAddressingQueryId());

      createData.setTxCopyBranchType(data.isTxCopyBranchType());

      Callable<BranchReadable> op = getBranchOps().createBranch(createData);
      BranchReadable result = executeCallable(op);

      UriInfo uriInfo = getUriInfo();
      URI uri = getBranchLocation(uriInfo, result);

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format(
               "Branch Operation Create Branch {branchUUID: %s, branchName: %s accountId: %s serverId: %s clientId: %s}",
               createData.getBranch(), data.getBranchName(), RestUtil.getAccountId(httpHeaders),
               RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return Response.created(uri).entity(asBranch(result)).build();
   }

   private URI getBranchLocation(UriInfo uriInfo, BranchReadable branch) {
      URI location = null;
      String path = uriInfo.getPath();
      if (Strings.isValid(path)) {
         String value = path.replace("branches", "");
         value = value.replaceAll("/", "");
         if (Strings.isNumeric(value)) {
            try {
               Long id = Long.parseLong(value);
               if (branch.equals(id)) {
                  location = uriInfo.getRequestUri();
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }

      if (location == null) {
         location = uriInfo.getRequestUriBuilder().path("{branch-uuid}").build(branch.getGuid());
      }
      return location;
   }

   @Override
   public Response commitBranch(BranchId branch, BranchId destinationBranch, BranchCommitOptions options) {
      BranchReadable srcBranch = getBranchById(branch);
      BranchReadable destBranch = getBranchById(destinationBranch);

      Callable<TransactionToken> op = getBranchOps().commitBranch(options.getCommitter(), srcBranch, destBranch);
      TransactionToken tx = executeCallable(op);

      if (options.isArchive()) {
         Callable<?> op2 = getBranchOps().archiveUnarchiveBranch(srcBranch, ArchiveOperation.ARCHIVE);
         executeCallable(op2);
      }

      UriInfo uriInfo = getUriInfo();
      URI location = getTxLocation(uriInfo, tx);
      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format(
               "Branch Operation Commit Branch {branchId: %s srcBranch: %s destBranch: %s accountId: %s serverId: %s clientId: %s}",
               branch, srcBranch, destBranch, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
               RestUtil.getClientId(httpHeaders)));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Response.created(location).entity(asTransaction(orcsApi.getTransactionFactory().getTx(tx))).build();
   }

   private URI getTxLocation(UriInfo uriInfo, TransactionToken tx) {
      UriBuilder builder = uriInfo.getRequestUriBuilder();
      URI location = builder.path("..").path("..").path("txs").path("{tx-id}").build(tx);
      return location;
   }

   @Override
   public Response archiveBranch(BranchId branchId) {
      BranchReadable branch = getBranchById(branchId);

      boolean modified = false;
      BranchArchivedState currentState = branch.getArchiveState();
      if (BranchArchivedState.UNARCHIVED == currentState) {
         Callable<?> op = getBranchOps().archiveUnarchiveBranch(branch, ArchiveOperation.ARCHIVE);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Archive Branch {branchId: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
                  RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response writeTx(BranchId branch, NewTransaction data) {
      String comment = data.getComment();
      ArtifactReadable userArtifact = null;

      TransactionFactory txFactory = newTxFactory();
      TransactionBuilder txBuilder = txFactory.createTransaction(branch, userArtifact, comment);

      //TODO: Integrate data with TxBuilder

      TransactionReadable tx = txBuilder.commit();
      if (tx.isValid()) {
         URI location = uriInfo.getRequestUriBuilder().path("{tx-id}").build(tx);
         return Response.created(location).entity(asTransaction(tx)).build();
      } else {
         throw new OseeArgumentException("No Data Modified");
      }
   }

   @Override
   public Response validateExchange(String path) {
      String exchangePath = asExchangeLocator(path);
      IResourceLocator locator = resourceManager.getResourceLocator(exchangePath);
      Callable<URI> op = getBranchOps().checkBranchExchangeIntegrity(locator.getLocation());
      URI verifyUri = executeCallable(op);

      UriInfo uriInfo = getUriInfo();
      URI location = getExchangeResourceURI(uriInfo, verifyUri);
      return Response.created(location).build();
   }

   @Override
   public Response deleteBranchExchange(String path) {
      boolean modified = false;
      String exchangePath = asExchangeLocator(path);
      IResourceLocator locator = resourceManager.getResourceLocator(exchangePath);
      if (locator != null) {
         int deleteResult = resourceManager.delete(locator);
         switch (deleteResult) {
            case IResourceManager.OK:
               try {
                  activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
                     String.format("Branch Operation Delete Branch Exachange Resource {resource: %s, locator %s}", path,
                        locator.getLocation()));
               } catch (OseeCoreException ex) {
                  OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
               }
               modified = true;
               break;
            case IResourceManager.FAIL:
               throw new OseeWebApplicationException(Status.INTERNAL_SERVER_ERROR,
                  "Error deleting exchange resource [%s] - locator [%s]", path, locator.getLocation());
            case IResourceManager.RESOURCE_NOT_FOUND:
            default:
               // do nothing - no modification
               break;
         }
      }
      return OrcsRestUtil.asResponse(modified);
   }

   @Override
   public Response exportBranches(BranchExportOptions options) {
      List<IOseeBranch> branches = getExportImportBranches(options.getBranchUuids());

      PropertyStore exportOptions = new PropertyStore();
      addOption(exportOptions, ExportOptions.MIN_TXS, options.getMinTx());
      addOption(exportOptions, ExportOptions.MAX_TXS, options.getMaxTx());
      addOption(exportOptions, ExportOptions.COMPRESS, options.isCompress());

      Callable<URI> op = getBranchOps().exportBranch(branches, exportOptions, options.getFileName());
      URI exportURI = executeCallable(op);

      UriInfo uriInfo = getUriInfo();
      URI location = getExchangeExportUri(uriInfo, exportURI, options.isCompress());
      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS, String.format(
            "Branch Operation Export Branches {branchUUID(s): %s accountId: %s serverId: %s, clientId: %s}", branches,
            RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Response.created(location).build();
   }

   @Override
   public Response importBranches(BranchImportOptions options) {
      List<IOseeBranch> branches;
      if (!options.getBranchUuids().isEmpty()) {
         branches = getExportImportBranches(options.getBranchUuids());
      } else {
         branches = java.util.Collections.emptyList();
      }
      String path = options.getExchangeFile();
      String exchangePath = asExchangeLocator(path);

      IResourceLocator locator = resourceManager.getResourceLocator(exchangePath);

      PropertyStore importOptions = new PropertyStore();
      addOption(importOptions, ImportOptions.MIN_TXS, options.getMinTx());
      addOption(importOptions, ImportOptions.MAX_TXS, options.getMaxTx());
      addOption(importOptions, ImportOptions.USE_IDS_FROM_IMPORT_FILE, options.isUseIdsFromImportFile());
      addOption(importOptions, ImportOptions.EXCLUDE_BASELINE_TXS, options.isExcludeBaselineTxs());
      addOption(importOptions, ImportOptions.ALL_AS_ROOT_BRANCHES, options.isAllAsRootBranches());
      addOption(importOptions, ImportOptions.CLEAN_BEFORE_IMPORT, options.isCleanBeforeImport());

      Callable<URI> op = getBranchOps().importBranch(locator.getLocation(), branches, importOptions);
      URI importURI = executeCallable(op);

      Response response;
      if (importURI != null) {
         UriInfo uriInfo = getUriInfo();
         URI location = getExchangeResourceURI(uriInfo, importURI);
         response = Response.created(location).build();
      } else {
         response = Response.ok().build();
      }

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS, String.format(
            "Branch Operation Import Branches {branchUUID(s): %s accountId: %s serverId: %s clientId: %s}", branches,
            RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return response;
   }

   private void addOption(PropertyStore data, Enum<?> enumKey, Object value) {
      if (value != null) {
         data.put(enumKey.name(), String.valueOf(value));
      }
   }

   private String asExchangeLocator(String path) {
      String toReturn = path;
      if (Strings.isValid(toReturn)) {
         if (!toReturn.startsWith("exchange://")) {
            toReturn = "exchange://" + toReturn;
         }
      }
      return toReturn;
   }

   private URI getExchangeResourceURI(UriInfo uriInfo, URI rawUri) {
      URI toReturn = rawUri;
      String path = rawUri.toASCIIString();
      int index = path.indexOf("exchange/");
      if (index > 0 && index < path.length()) {
         path = path.substring(index);
         toReturn = uriInfo.getBaseUriBuilder().path("resources").queryParam("path", path).build();
      }
      return toReturn;
   }

   private URI getExchangeExportUri(UriInfo uriInfo, URI rawUri, boolean isCompressed) {
      String path = rawUri.toASCIIString();
      path = path.replace("://", "/");
      if (isCompressed && !path.endsWith(".zip")) {
         path = path + ".zip";
      } else {
         path = path + "/export.manifest.xml";
      }
      URI toReturn = uriInfo.getBaseUriBuilder().path("resources").queryParam("path", path).build();
      return toReturn;
   }

   private List<IOseeBranch> getExportImportBranches(Collection<BranchId> branchUids) {
      ResultSet<IOseeBranch> resultsAsId = newBranchQuery().andIds(branchUids) //
         .includeArchived()//
         .includeDeleted()//
         .getResultsAsId();
      return Lists.newLinkedList(resultsAsId);
   }

   @Override
   public Response setBranchName(BranchId branchId, String newName) {
      BranchReadable branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getName(), newName)) {
         Callable<?> op = getBranchOps().changeBranchName(branch, newName);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format(
                  "Branch Operation Set Branch Name {branchId: %s prevName: %s newName: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, branch.getName(), newName, RestUtil.getAccountId(httpHeaders),
                  RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response setBranchType(BranchId branchId, BranchType newType) {
      BranchReadable branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getBranchType(), newType)) {
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format(
                  "Branch Operation Set Branch Type {branchUUID: %s prevType: %s newType: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, branch.getBranchType(), newType, RestUtil.getAccountId(httpHeaders),
                  RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Callable<?> op = getBranchOps().changeBranchType(branch, newType);
         executeCallable(op);
         modified = true;
      }
      return asResponse(modified);
   }

   @Override
   public Response setBranchState(BranchId branchId, BranchState newState) {
      BranchReadable branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getBranchState(), newState)) {
         Callable<?> op = getBranchOps().changeBranchState(branch, newState);
         executeCallable(op);
         modified = true;

         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format(
                  "Branch Operation Branch State Changed {branchId: %s prevState: %s newState: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, branch.getBranchType(), newState, RestUtil.getAccountId(httpHeaders),
                  RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response associateBranchToArtifact(BranchId branchId, ArtifactId artifact) {
      BranchReadable branch = getBranchById(branchId);
      boolean modified = false;
      if (isDifferent(branch.getAssociatedArtifact(), artifact)) {
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format(
                  "Branch Operation Associate Branch to Artifact {branchId: %s prevArt: %s newArt: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, branch.getAssociatedArtifact(), artifact, RestUtil.getAccountId(httpHeaders),
                  RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Callable<?> op = getBranchOps().associateBranchToArtifact(branch, artifact);
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
               String.format(
                  "Branch Operation Set Tx Comment {branchId: %s prevComment: %s newComment: %s accountId: %s serverId: %s clientId: %s}",
                  branch, tx.getComment(), comment, RestUtil.getAccountId(httpHeaders),
                  RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders)));
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
      BranchReadable branch = getBranchById(branchId);
      if (branch != null) {
         Callable<?> op = getBranchOps().purgeBranch(branch, recurse);
         executeCallable(op);
         modified = true;
      }

      try {
         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Purge Branch {branchId: %s, accountId: %s serverId: %s clientId: %s}",
               branchId, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
               RestUtil.getClientId(httpHeaders)));
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return asResponse(modified);
   }

   @Override
   public Response unarchiveBranch(BranchId branchId) {
      BranchReadable branch = getBranchById(branchId);
      BranchArchivedState state = branch.getArchiveState();

      boolean modified = false;
      if (BranchArchivedState.ARCHIVED == state) {
         Callable<?> op = getBranchOps().archiveUnarchiveBranch(branch, ArchiveOperation.UNARCHIVE);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format("Branch Operation Unarchive Branch {branchId: %s accountId: %s serverId: %s clientId: %s}",
                  branchId, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
                  RestUtil.getClientId(httpHeaders)));
         } catch (OseeCoreException ex) {
            OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response unCommitBranch(BranchId branch, BranchId destinationBranch) {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   @Override
   public Response unassociateBranch(BranchId branchId) {
      BranchReadable branch = getBranchById(branchId);
      boolean modified = false;
      if (branch.getAssociatedArtifact().isValid()) {
         Callable<?> op = getBranchOps().unassociateBranch(branch);
         executeCallable(op);
         modified = true;
         try {
            activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
               String.format(
                  "Branch Operation Unassociate Branch {branchId: %s, accountId: %s serverId: %s clientId: %s}",
                  branchId, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
                  RestUtil.getClientId(httpHeaders)));
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
                  String.format(
                     "Branch Operation Purge Txs {branchId: %s, txsToDelete: %s accountId: %s serverId: %s clientId: %s}",
                     branch, txIds, RestUtil.getAccountId(httpHeaders), RestUtil.getServerId(httpHeaders),
                     RestUtil.getClientId(httpHeaders)));
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

   private ResultSet<BranchReadable> searchBranches(BranchQueryData options) {
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();

      List<BranchId> branchIds = options.getBranchIds();
      if (Conditions.hasValues(branchIds)) {
         query.andIds(branchIds);
      }

      List<BranchState> branchStates = options.getBranchStates();
      if (Conditions.hasValues(branchStates)) {
         query.andStateIs(branchStates.toArray(new BranchState[branchStates.size()]));
      }

      List<BranchType> branchTypes = options.getBranchTypes();
      if (Conditions.hasValues(branchTypes)) {
         query.andIsOfType(branchTypes.toArray(new BranchType[branchTypes.size()]));
      }

      if (options.isIncludeArchived()) {
         query.includeArchived();
      }

      if (options.isIncludeDeleted()) {
         query.includeDeleted();
      }

      String nameEquals = options.getNameEquals();
      if (Strings.isValid(nameEquals)) {
         query.andNameEquals(nameEquals);
      }

      String namePattern = options.getNamePattern();
      if (Strings.isValid(namePattern)) {
         query.andNamePattern(namePattern);
      }

      Long ancestorOf = options.getIsAncestorOf();
      if (ancestorOf > 0) {
         BranchId ancestorOfToken = BranchId.valueOf(ancestorOf);
         query.andIsAncestorOf(ancestorOfToken);
      }

      Long childOf = options.getIsChildOf();
      if (childOf > 0) {
         BranchId childOfToken = BranchId.valueOf(ancestorOf);
         query.andIsAncestorOf(childOfToken);
      }
      return query.getResults();
   }

   @Override
   public Response logBranchActivity(String comment) {
      try {
         comment += String.format(" accountId: %s serverId: %s clientId: %s", RestUtil.getAccountId(httpHeaders),
            RestUtil.getServerId(httpHeaders), RestUtil.getClientId(httpHeaders));

         activityLog.createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS, comment);
      } catch (OseeCoreException ex) {
         OseeLog.log(ActivityLog.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return Response.ok().build();
   }
}
