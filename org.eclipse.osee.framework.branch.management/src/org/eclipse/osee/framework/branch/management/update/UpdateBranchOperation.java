/*
 * Created on Jan 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.update;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Megumi Telles
 */
public class UpdateBranchOperation extends AbstractDbTxOperation {

   private final IOseeCachingServiceProvider cachingService;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private BranchType branchType = null;
   private BranchState branchState = null;
   private BranchArchivedState archivedState = null;
   private final int branchId;
   private final static String UPDATE_BRANCH_TYPE = "update osee_branch set branch_type=? where branch_id=?";
   private final static String UPDATE_BRANCH_STATE = "update osee_branch set branch_state=? where branch_id=?";
   private final static String UPDATE_BRANCH_ARCHIVE_STATE = "update osee_branch set archived=? where branch_id=?";
   private String BRANCH_UPDATE = "";

   UpdateBranchOperation(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingService, int branchId) {
      super(provider, String.format("Update Branch"), Activator.PLUGIN_ID);
      this.cachingService = cachingService;
      this.oseeDatabaseProvider = provider;
      this.branchId = branchId;
   }

   public UpdateBranchOperation(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingService, int branchId, BranchType type) {
      this(provider, cachingService, branchId);
      this.branchType = type;
      this.BRANCH_UPDATE = Function.UPDATE_BRANCH_TYPE.name();
   }

   public UpdateBranchOperation(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingService, int branchId, BranchState state) {
      this(provider, cachingService, branchId);
      this.branchState = state;
      this.BRANCH_UPDATE = Function.UPDATE_BRANCH_STATE.name();
   }

   public UpdateBranchOperation(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingService, int branchId, BranchArchivedState state) {
      this(provider, cachingService, branchId);
      this.archivedState = state;
      this.BRANCH_UPDATE = Function.UPDATE_ARCHIVE_STATE.name();
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      String sqlStatement =
            BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_TYPE.name()) ? UPDATE_BRANCH_TYPE : BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_STATE.name()) ? UPDATE_BRANCH_STATE : UPDATE_BRANCH_ARCHIVE_STATE;
      int branchUpdateValue =
            BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_TYPE.name()) ? branchType.ordinal() : BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_STATE.name()) ? branchState.ordinal() : archivedState.ordinal();
      oseeDatabaseProvider.getOseeDatabaseService().runPreparedUpdate(connection, sqlStatement, branchUpdateValue,
            branchId);
   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);

      if (getStatus().isOK()) {
         BranchCache branchCache;
         try {
            branchCache = cachingService.getOseeCachingService().getBranchCache();
            Branch branch = branchCache.getById(branchId);
            if (BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_TYPE.name())) {
               branch.setBranchType(branchType);
            } else if (BRANCH_UPDATE.equals(Function.UPDATE_BRANCH_STATE.name())) {
               branch.setBranchState(branchState);
            } else {
               branch.setArchived(archivedState.isArchived());
            }
            branchCache.storeItems(branch);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}
