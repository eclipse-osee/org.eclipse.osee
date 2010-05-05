package org.eclipse.osee.framework.ui.skynet.change;

import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 */
public class ChangeReportEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInputFactory"; //$NON-NLS-1$
   private final static String TRANSACTION_ID_KEY = "org.eclipse.osee.framework.ui.skynet.change.TransactionId"; //$NON-NLS-1$
   private final static String BRANCH_ID_KEY = "org.eclipse.osee.framework.ui.skynet.change.BranchId"; //$NON-NLS-1$

   public ChangeReportEditorInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   public IAdaptable createElement(IMemento memento) {
      try {
         Integer branchId = null;
         if (memento != null) {
            if (SkynetViews.isSourceValid(memento)) {
               branchId = memento.getInteger(BRANCH_ID_KEY);
               if (branchId != null && branchId > -1) {
                  Branch branch = BranchManager.getBranch(branchId);
                  if (branch != null) {
                     ChangeUiData changeUiData = new ChangeUiData();
                     changeUiData.setBranch(branch);
                     return new ChangeReportEditorInput(changeUiData);
                  }
               } else {
                  Integer transactionNumber = memento.getInteger(TRANSACTION_ID_KEY);
                  if (transactionNumber != null && transactionNumber > -1) {
                     TransactionRecord transaction = TransactionManager.getTransactionId(transactionNumber);
                     ChangeUiData changeUiData = new ChangeUiData();
                     changeUiData.setTransaction(transaction);
                     return new ChangeReportEditorInput(changeUiData);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Change report error on init", ex);
      }
      return null;
   }

   /**
    * save transaction id or -1 and branch guid or ""
    */
   public static void saveState(IMemento memento, ChangeReportEditorInput input) {
      memento.putInteger(TRANSACTION_ID_KEY,
            input.getChangeData().getTransaction() != null ? input.getChangeData().getTransaction().getId() : -1);
      memento.putInteger(BRANCH_ID_KEY,
            input.getChangeData().getBranch() != null ? input.getChangeData().getBranch().getId() : -1);
      SkynetViews.addDatabaseSourceId(memento);
   }
}
