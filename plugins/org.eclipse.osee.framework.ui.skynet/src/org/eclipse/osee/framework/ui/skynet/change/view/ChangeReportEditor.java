/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.view;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event2.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorPreferences;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportPreferences;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class ChangeReportEditor extends FormEditor implements IChangeReportView {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditor";

   private ChangeReportPage changeReportPage;
   private ChangeReportActionBarContributor actionBarContributor;
   private final EventRelay eventRelay;
   private final ChangeReportEditorPreferences preferences;

   public ChangeReportEditor() {
      super();
      this.eventRelay = new EventRelay();
      this.preferences = new ChangeReportEditorPreferences(EDITOR_ID);
   }

   public IChangeReportPreferences getPreferences() {
      return preferences;
   }

   public ChangeReportActionBarContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ChangeReportActionBarContributor(this);
      }
      return actionBarContributor;
   }

   @Override
   public ChangeReportEditorInput getEditorInput() {
      return (ChangeReportEditorInput) super.getEditorInput();
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (changeReportPage != null) {
         changeReportPage.showBusy(busy);
      }
   }

   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      try {
         changeReportPage = new ChangeReportPage(this);
         addPage(changeReportPage);
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      OseeContributionItem.addTo(this, true);
      OseeEventManager.addListener(eventRelay);
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == IActionable.class) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               StringBuilder sb = new StringBuilder();
               ChangeUiData changeData = getEditorInput().getChangeData();
               TransactionDelta txDelta = changeData.getTxDelta();
               Branch branch1 = null;
               Branch branch2 = null;
               try {
                  branch1 = txDelta.getStartTx().getBranch();
                  branch2 = txDelta.getEndTx().getBranch();
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
               }
               if (txDelta.areOnTheSameBranch()) {
                  sb.append(String.format("\nBranch - %s", branch1));
                  sb.append(String.format("\nTransactions - %s", txDelta.toString()));
               } else {
                  sb.append(String.format("\nBranch %s - %s Tx:[%s]", 1, branch1, txDelta.getStartTx().getId()));
                  sb.append(String.format("\nBranch %s - %s Tx:[%s]", 2, branch2, txDelta.getEndTx().getId()));
               }
               return sb.toString();
            }
         };
      }
      return super.getAdapter(adapter);
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(eventRelay);
      getPreferences().saveState();
      super.dispose();
   }

   @Override
   public void recomputeChangeReport() {
      if (changeReportPage != null && changeReportPage.isActive()) {
         changeReportPage.recomputeChangeReport(true);
      }
   }

   public void refresh() {
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());
      if (changeReportPage != null) {
         changeReportPage.refresh();
      }
   }

   private final class EventRelay implements IBranchEventListener, ITransactionEventListener, ITransactionsDeletedEventListener {

      @Override
      public void handleBranchEventREM1(Sender sender, BranchEventType branchModType, final int branchId) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         Branch[] branches = new Branch[2];
         try {
            branches[0] = changeUiData.getTxDelta().getStartTx().getBranch();
            branches[1] = changeUiData.getTxDelta().getEndTx().getBranch();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Error obtaining change report branches for branch event",
               ex);
         }
         for (Branch branch : branches) {
            if (branch != null && branch.getId() == branchId) {
               if (branchModType == BranchEventType.Deleted && branchModType == BranchEventType.Purged) {
                  close(false);
               } else if (branchModType == BranchEventType.Committed) {
                  recomputeChangeReport();
               }
            }
         }
      }

      @Override
      public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         int[] txDeltas = new int[2];
         txDeltas[0] = changeUiData.getTxDelta().getStartTx().getId();
         txDeltas[1] = changeUiData.getTxDelta().getEndTx().getId();
         for (int transactionIdToMatch : txDeltas) {
            for (int txId : transactionIds) {
               if (transactionIdToMatch == txId) {
                  close(false);
                  break;
               }
            }
         }
      }

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         int[] txDeltas = new int[2];
         txDeltas[0] = changeUiData.getTxDelta().getStartTx().getId();
         txDeltas[1] = changeUiData.getTxDelta().getEndTx().getId();
         for (int transactionIdToMatch : txDeltas) {
            for (TransactionChange transChange : transEvent.getTransactions()) {
               if (transactionIdToMatch == transChange.getTransactionId()) {
                  close(false);
                  break;
               }
            }
         }
      }

      @Override
      public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         Branch[] branches = new Branch[2];
         try {
            branches[0] = changeUiData.getTxDelta().getStartTx().getBranch();
            branches[1] = changeUiData.getTxDelta().getEndTx().getBranch();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Error obtaining change report branches for branch event",
               ex);
         }
         for (Branch branch : branches) {
            if (branch != null && branch.getGuid().equals(branchEvent.getBranchGuid())) {
               if (branchEvent.getEventType() == BranchEventType.Deleted && branchEvent.getEventType() == BranchEventType.Purged) {
                  close(false);
               } else if (branchEvent.getEventType() == BranchEventType.Committed) {
                  recomputeChangeReport();
               }
            }
         }

      }

      @Override
      public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
         // do nothing
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }
   }
}
