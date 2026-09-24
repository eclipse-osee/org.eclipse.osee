/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.BRANCH_OPERATION;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.InterfaceMessagesFolder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.event.BranchChangeTopic;
import org.eclipse.osee.framework.core.event.OriginContext;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.branch.BranchDataFactory;
import org.eclipse.osee.orcs.core.internal.branch.CommitBranchCallable;
import org.eclipse.osee.orcs.core.internal.branch.PurgeBranchCallable;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class OrcsBranchImpl implements OrcsBranch {
   private final OrcsApi orcsApi;
   private final Log logger;
   private final OrcsSession session;
   private final BranchDataStore branchStore;
   private final BranchDataFactory branchDataFactory;
   private final OrcsTokenService tokenService;
   private final QueryFactory queryFactory;
   private final UserService userService;
   private final EventAdmin eventAdmin;

   public OrcsBranchImpl(OrcsApi orcsApi, Log logger, OrcsSession session, BranchDataStore branchStore, QueryFactory queryFactory, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.session = session;
      this.branchStore = branchStore;
      branchDataFactory = new BranchDataFactory(queryFactory);
      this.tokenService = orcsApi.tokenService();
      this.queryFactory = queryFactory;
      userService = orcsApi.userService();
      this.eventAdmin = eventAdmin;
   }

   /**
    * Fires a {@link BranchChangeTopic} event so {@code orcs.rest} listeners fan the change out to
    * SSE web clients, peer web servers, and desktop clients. This is the single server-side branch
    * broadcast chokepoint (mirrors the transaction commit topic for artifacts). {@code originId}
    * must be captured from {@link OriginContext} on the request thread and carried in the payload
    * (EventAdmin dispatch is async, so the subscriber must not read the ThreadLocal itself).
    *
    * @param changeType a web-facing branch change type value (see {@code WebBranchChangeType})
    */
   private void postBranchChangeEvent(BranchId branch, String changeType) {
      postBranchChangeEvent(branch, changeType, OriginContext.get(), null);
   }

   private void postBranchChangeEvent(BranchId branch, String changeType, String originId) {
      postBranchChangeEvent(branch, changeType, originId, null);
   }

   /**
    * @param associatedArtifactId the branch's associated artifact id, or null when not readily
    * available. Pass it only when the full {@code Branch} is already in hand (create, state change)
    * so this stays query-free; consumers that don't track the branch by id use it to match.
    */
   private void postBranchChangeEvent(BranchId branch, String changeType, String originId,
      ArtifactId associatedArtifactId) {
      EventAdmin admin = this.eventAdmin;
      if (admin == null || branch == null || branch.isInvalid()) {
         return;
      }
      try {
         Map<String, Object> properties = new HashMap<>();
         properties.put(BranchChangeTopic.BRANCH_ID, branch.getIdString());
         properties.put(BranchChangeTopic.CHANGE_TYPE, changeType);
         properties.put(BranchChangeTopic.USER_ID, userService.getUser().getIdString());
         if (originId != null) {
            properties.put(BranchChangeTopic.ORIGIN_ID, originId);
         }
         if (associatedArtifactId != null && associatedArtifactId.isValid()) {
            properties.put(BranchChangeTopic.ASSOCIATED_ARTIFACT_ID, associatedArtifactId.getIdString());
         }
         admin.postEvent(new Event(BranchChangeTopic.TOPIC, properties));
      } catch (Exception ex) {
         logger.warn(ex, "Failed to post branch change event for branch [%s] type [%s]", branch, changeType);
      }
   }

   @Override
   public XResultData createBranchValidation(CreateBranchData branchData) {
      return branchStore.createBranchValidation(branchData, userService, orcsApi.tokenService());
   }

   @Override
   public Branch createBranch(CreateBranchData branchData) {
      Conditions.checkNotNull(branchData, "branchData");

      Conditions.checkNotNull(branchData.getBranch(), "branchUuid");
      Conditions.checkNotNull(branchData.getName(), "branchName");
      Conditions.checkNotNull(branchData.getBranchType(), "branchType");

      if (branchData.isTxCopyBranchType()) {
         TransactionQuery txQuery = queryFactory.transactionQuery();
         TransactionToken givenTx = branchData.getFromTransaction();
         Conditions.checkNotNull(givenTx, "Transaction used for copy");
         branchData.setSavedTransaction(givenTx);
         TransactionToken priorTx = txQuery.andIsPriorTx(givenTx).getResults().getExactlyOne();
         branchData.setFromTransaction(priorTx);
         branchStore.createBranchCopyTx(branchData, userService, orcsApi.tokenService());
      } else {
         TransactionToken txData = null;
         if (branchData.getParentBranch().isValid()) {
            txData = orcsApi.getQueryFactory().transactionQuery().andIsHead(
               branchData.getParentBranch()).getTokens().getExactlyOne();
         } else {
            txData = branchData.getFromTransaction();
         }
         Conditions.checkNotNull(txData, "sourceTransaction");
         branchData.setFromTransaction(txData);
         branchStore.createBranch(branchData, userService, orcsApi.tokenService());
      }
      if (!branchData.getCategories().isEmpty()) {
         for (BranchCategoryToken bc : branchData.getCategories()) {
            orcsApi.getBranchOps().setBranchCategory(branchData.getBranch(), bc);
         }
         // Reset to CREATED since inheriting branch category does not qualify as MODIFIED
         orcsApi.getBranchOps().setBranchState(branchData.getBranch(), BranchState.CREATED);
      }
      Branch newBranch =
         queryFactory.branchQuery().andId(branchData.getNewBranch()).getResults().getExactlyOne();
      // Every create path (REST, ATS, MIM, program) converges here -- broadcast 'created' once.
      postBranchChangeEvent(newBranch, WebBranchChangeType.CREATED.getWebValue(), OriginContext.get(),
         newBranch.getAssociatedArtifact());
      return newBranch;
   }

   @Override
   public BranchToken createTopLevelBranch(BranchToken branch) {
      return createTopLevelBranch(new CreateBranchData(branch), ArtifactId.SENTINEL);
   }

   private BranchToken createTopLevelBranch(CreateBranchData createData, ArtifactId associatedArtifact) {
      createData.setBranchType(BranchType.BASELINE);

      BranchToken parentBranch = CoreBranches.SYSTEM_ROOT;
      TransactionToken parentTx =
         orcsApi.getQueryFactory().transactionQuery().andIsHead(parentBranch).getTokens().getExactlyOne();

      String creationComment = String.format("New Branch from %s (%s)", parentBranch, parentTx.getId());
      createData.setCreationComment(creationComment);

      createData.setAssociatedArtifact(associatedArtifact);

      createData.setFromTransaction(parentTx);
      createData.setParentBranch(parentBranch);
      createData.setCategories(orcsApi.getQueryFactory().branchQuery().getBranchCategories(parentBranch));
      createData.setTxCopyBranchType(false);

      return createBranch(createData);
   }

   @Override
   public XResultData archiveBranch(BranchId branch) {
      XResultData rd = branchStore.archiveBranch(session, branch);
      if (rd.isSuccess()) {
         postBranchChangeEvent(branch, WebBranchChangeType.ARCHIVED.getWebValue());
      }
      return rd;
   }

   @Override
   public XResultData unarchiveBranch(BranchId branch) {
      XResultData rd = branchStore.unArchiveBranch(session, branch);
      if (rd.isSuccess()) {
         postBranchChangeEvent(branch, WebBranchChangeType.UNARCHIVED.getWebValue());
      }
      return rd;
   }

   @Override
   public XResultData deleteBranch(BranchId branch) {
      // branchStore.deleteBranch is a composite (changeBranchState(DELETED) + archiveBranch) at the
      // DB layer that bypasses this class, so broadcast 'deleted' here rather than relying on the
      // verb methods above.
      XResultData rd = branchStore.deleteBranch(session, branch);
      if (rd.isSuccess()) {
         postBranchChangeEvent(branch, WebBranchChangeType.DELETED.getWebValue());
      }
      return rd;
   }

   @Override
   public Callable<List<BranchId>> purgeBranch(BranchId branch, boolean recurse) {
      Callable<List<BranchId>> delegate =
         new PurgeBranchCallable(logger, session, branchStore, branch, recurse, queryFactory);
      // Purge runs when the caller executes the callable (still on the request thread), so capture
      // originId now and broadcast 'purged' after it succeeds -- from this chokepoint, not per-endpoint.
      String originId = OriginContext.get();
      return () -> {
         List<BranchId> purged = delegate.call();
         postBranchChangeEvent(branch, WebBranchChangeType.PURGED.getWebValue(), originId);
         return purged;
      };
   }

   @Override
   public Callable<TransactionToken> commitBranch(ArtifactId committer, BranchId source, BranchId destination) {
      Callable<TransactionToken> delegate = new CommitBranchCallable(logger, session, branchStore, orcsApi, committer,
         source, destination, tokenService);
      // Commit runs when the caller executes the callable (still on the request thread), so capture
      // originId now and broadcast 'committed' for both branches after it succeeds -- from this
      // chokepoint, so ATS-initiated commits (which bypass BranchEndpointImpl) notify too.
      String originId = OriginContext.get();
      return () -> {
         TransactionToken tx = delegate.call();
         postBranchChangeEvent(source, WebBranchChangeType.COMMITTED.getWebValue(), originId);
         postBranchChangeEvent(destination, WebBranchChangeType.COMMITTED.getWebValue(), originId);
         return tx;
      };
   }

   @Override
   public List<ChangeItem> compareBranch(TransactionToken sourceTx, TransactionToken destinationTx) {
      return branchStore.compareBranch(session, tokenService, sourceTx, destinationTx, orcsApi);
   }

   @Override
   public List<ChangeItem> compareBranch(BranchId branch) {
      TransactionId baseTransaction =
         queryFactory.branchQuery().andId(branch).getResults().getExactlyOne().getBaselineTx();
      TransactionToken fromTx = queryFactory.transactionQuery().andTxId(baseTransaction).getResults().getExactlyOne();
      TransactionToken toTx = queryFactory.transactionQuery().andIsHead(branch).getResults().getExactlyOne();
      return branchStore.compareBranch(session, tokenService, fromTx, toTx, orcsApi);
   }

   @Override
   public PermissionEnum getBranchPermission(ArtifactId subject, BranchId branch) {
      return branchStore.getBranchPermission(subject, branch);
   }

   @Override
   public void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission) {
      branchStore.setBranchPermission(subject, branch, permission);
   }

   @Override
   public XResultData changeBranchState(BranchId branch, BranchState branchState) {
      return branchStore.changeBranchState(session, branch, branchState);
   }

   @Override
   public XResultData changeBranchType(BranchId branch, BranchType branchType) {
      XResultData rd = branchStore.changeBranchType(session, branch, branchType);
      if (rd.isSuccess()) {
         postBranchChangeEvent(branch, WebBranchChangeType.TYPE_CHANGED.getWebValue());
      }
      return rd;
   }

   @Override
   public XResultData changeBranchName(BranchId branch, String branchName) {
      XResultData rd = branchStore.changeBranchName(session, branch, branchName);
      if (rd.isSuccess()) {
         postBranchChangeEvent(branch, WebBranchChangeType.RENAMED.getWebValue());
      }
      return rd;
   }

   @Override
   public XResultData associateBranchToArtifact(BranchId branch, ArtifactId associatedArtifact) {
      Conditions.checkNotNull(associatedArtifact, "associatedArtifact");
      return branchStore.changeBranchAssociatedArt(session, branch, associatedArtifact);
   }

   @Override
   public XResultData unassociateBranch(BranchId branch) {
      return branchStore.changeBranchAssociatedArt(session, branch, ArtifactId.SENTINEL);
   }

   @Override
   public Callable<URI> exportBranch(List<? extends BranchId> branches, PropertyStore options, String exportName) {
      return branchStore.exportBranch(session, branches, options, exportName);
   }

   @Override
   public Callable<URI> importBranch(URI fileToImport, List<? extends BranchId> branches, PropertyStore options) {
      return branchStore.importBranch(session, fileToImport, branches, options);
   }

   private void setAsUser(CreateBranchData branchData) {
      UserId asUser = userService.getUser();
      if (asUser.isInvalid()) {
         asUser = SystemUser.OseeSystem;
      }
      branchData.setAsUser(asUser);
   }

   @Override
   public Branch createBaselineBranch(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact) {
      CreateBranchData branchData = branchDataFactory.createBaselineBranchData(branch, parent, associatedArtifact);
      setAsUser(branchData);
      Branch newBranch = createBranch(branchData);
      setBranchPermission(userService.getUser(), newBranch, PermissionEnum.FULLACCESS);
      return newBranch;
   }

   @Override
   public Branch createWorkingBranch(BranchToken branch, BranchToken parent, ArtifactId associatedArtifact) {
      CreateBranchData branchData = branchDataFactory.createWorkingBranchData(branch, parent, associatedArtifact);
      setAsUser(branchData);
      return createBranch(branchData);
   }

   @Override
   public Branch createCopyTxBranch(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      CreateBranchData branchData =
         branchDataFactory.createCopyTxBranchData(branch, fromTransaction, associatedArtifact);
      setAsUser(branchData);
      return createBranch(branchData);
   }

   @Override
   public Branch createPortBranch(BranchToken branch, TransactionId fromTransaction, ArtifactId associatedArtifact) {
      CreateBranchData branchData = branchDataFactory.createPortBranchData(branch, fromTransaction, associatedArtifact);
      setAsUser(branchData);
      return createBranch(branchData);
   }

   @Override
   public void addMissingApplicabilityFromParentBranch(BranchId branch) {
      branchStore.addMissingApplicabilityFromParentBranch(branch);
   }

   @Override
   public BranchToken createProgramBranch(BranchToken branch, BranchToken parent) {
      BranchToken newBranch = createBaselineBranch(branch, parent, ArtifactId.SENTINEL);
      setBranchPermission(userService.getUser(), newBranch, PermissionEnum.FULLACCESS);
      return newBranch;
   }

   @Override
   public BranchToken createProgramBranch(BranchToken branch) {
      BranchToken newBranch = createTopLevelBranch(branch);
      setBranchPermission(userService.getUser(), branch, PermissionEnum.FULLACCESS);

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "Create Program Hierarchy");
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SystemRequirementsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SubSystemRequirementsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SoftwareRequirementsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.HardwareRequirementsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.InterfaceMessagesFolder);
      tx.createOrIntroduceArtifact(InterfaceMessagesFolder, CoreArtifactTokens.InterfacePlatformTypesFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SystemRequirementsFolderMarkdown);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SubSystemRequirementsFolderMarkdown);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.SoftwareRequirementsFolderMarkdown);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.VerificationTestsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.ValidationTestsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.IntegrationTestsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.ApplicabilityTestsFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.GitRepoFolder);
      tx.createOrIntroduceArtifact(DefaultHierarchyRoot, CoreArtifactTokens.CustomerReqFolder);
      tx.commit();

      return newBranch;
   }

   @Override
   public XResultData setBranchCategory(BranchId branch, BranchCategoryToken category) {
      return setBranchCategory(branch, orcsApi.userService().getUserOrSystem(), category);
   }

   @Override
   public XResultData setBranchCategory(BranchId branch, UserId asUser, BranchCategoryToken category) {
      XResultData result = new XResultData();
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, asUser, "Set Branch Category");
      tx.createBranchCategory(branch, category);
      tx.commit();
      return result;
   }

   @Override
   public XResultData deleteBranchCategory(BranchId branch, BranchCategoryToken category) {
      return deleteBranchCategory(branch, orcsApi.userService().getUser(), category);
   }

   @Override
   public XResultData deleteBranchCategory(BranchId branch, UserId asUser, BranchCategoryToken category) {
      XResultData result = new XResultData();
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, asUser, "Delete Branch Category");
      tx.deleteBranchCategory(branch, category);
      tx.commit();
      return result;
   }

   @Override
   public boolean setBranchState(BranchId branchId, BranchState newState) {
      Branch branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getExactlyOne();
      XResultData rd = branchStore.changeBranchState(session, branchId, newState);
      if (rd.isSuccess()) {
         orcsApi.getActivityLog().createEntry(BRANCH_OPERATION, ActivityLog.INITIAL_STATUS,
            String.format("Branch Operation Branch State Changed {branchId: %s prevState: %s newState: %s}", branchId,
               branch.getBranchType(), newState));
         // Broadcast only terminal, web-actionable states. Transient in-progress states
         // (*_IN_PROGRESS) and the internal post-create CREATED reset are notification noise -- the
         // web is GET-on-notify and has nothing to fetch mid-operation. A DELETED move is the
         // distinct 'deleted' type; MODIFIED is 'state_changed'. (COMMITTED/REBASELINED/PURGED are
         // broadcast from their own operations, not here.)
         String changeType = null;
         if (newState == BranchState.DELETED) {
            // NOTE: the public deleteBranch() path broadcasts DELETED itself (via the composite
            // branchStore.deleteBranch). This branch covers a direct setBranchState(DELETED) only.
            // Do not route deleteBranch() through here as well, or clients get two DELETED events.
            changeType = WebBranchChangeType.DELETED.getWebValue();
         } else if (newState == BranchState.MODIFIED) {
            changeType = WebBranchChangeType.STATE_CHANGED.getWebValue();
         }
         if (changeType != null) {
            postBranchChangeEvent(branchId, changeType, OriginContext.get(), branch.getAssociatedArtifact());
         }
         return true;
      }
      return false;
   }

}
