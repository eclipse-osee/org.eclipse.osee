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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.change.BranchTransactionActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class ChangeReportEditor extends FormEditor implements IChangeReportView {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditor";

   private ChangeReportPage changeReportPage;
   private ChangeReportActionBarContributor changeReportActionBarContributor;
   private BranchTransactionActionBarContributor branchTransactionActionBarContributor;
   private final EventRelay eventRelay;
   private BranchTransactionPage branchTransactionPage;
   private ArtifactId viewId = ArtifactId.SENTINEL;

   public ChangeReportEditor() {
      eventRelay = new EventRelay();
   }

   public ChangeReportActionBarContributor getChangeReportActionBarContributor() {
      if (changeReportActionBarContributor == null) {
         changeReportActionBarContributor = new ChangeReportActionBarContributor(this);
      }
      return changeReportActionBarContributor;
   }

   public BranchTransactionActionBarContributor getBranchTransactionActionBarContributor() {
      if (branchTransactionActionBarContributor == null) {
         branchTransactionActionBarContributor = new BranchTransactionActionBarContributor(this);
      }
      return branchTransactionActionBarContributor;
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
      OseeStatusContributionItemFactory.addTo(this, true);
      try {
         changeReportPage = new ChangeReportPage(this);
         addPage(changeReportPage);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      if (getEditorInput().getBranch() != null) {
         try {
            branchTransactionPage = new BranchTransactionPage(this, getEditorInput().getBranch());
            addPage(branchTransactionPage);
         } catch (PartInitException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      setActivePage(getEditorInput().isTransactionTabActive() ? 1 : 0);
      OseeEventManager.addListener(eventRelay);
   }

   public ChangeUiData getChanges() {
      return changeReportPage.getEditorInput().getChangeData();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // method overridden only to satisfy its defining interface
   }

   @Override
   public void doSaveAs() {
      // method overridden only to satisfy its defining interface
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(eventRelay);
      super.dispose();
   }

   @Override
   public void recomputeChangeReport() {
      if (changeReportPage != null && changeReportPage.isActive()) {
         changeReportPage.recomputeChangeReport(true);
      }
   }

   @Override
   public void recomputeBranchTransactions() {
      if (branchTransactionPage != null && branchTransactionPage.isActive()) {
         branchTransactionPage.recomputeBranchTransactions(true);
      }
   }

   public void refresh() {
      setPartName(getEditorInput().getTitle());
      setTitleImage(getEditorInput().getImage());
      if (changeReportPage != null) {
         changeReportPage.refresh();
      }
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
   }

   private final class EventRelay implements IBranchEventListener, ITransactionEventListener {

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         TransactionId[] txDeltas = new TransactionId[2];
         txDeltas[0] = changeUiData.getTxDelta().getStartTx();
         txDeltas[1] = changeUiData.getTxDelta().getEndTx();
         for (TransactionId transactionIdToMatch : txDeltas) {
            for (TransactionChange transChange : transEvent.getTransactionChanges()) {
               if (transactionIdToMatch.equals(transChange.getTransactionId())) {
                  close(false);
                  break;
               }
            }
         }
      }

      @Override
      public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         BranchId[] branches = new BranchId[2];
         try {
            branches[0] = changeUiData.getTxDelta().getStartTx().getBranch();
            branches[1] = changeUiData.getTxDelta().getEndTx().getBranch();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error obtaining change report branches for branch event", ex);
         }
         for (BranchId branch : branches) {
            if (branch != null && branch.equals(branchEvent.getSourceBranch())) {
               switch (branchEvent.getEventType()) {
                  case Deleting:
                  case Deleted:
                  case Purging:
                  case Purged:
                  case Committing:
                  case Committed:
                     close(false);
                     break;
                  default:
                     break;
               }
            }
         }

      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
   }

   @Override
   protected void pageChange(int newPageIndex) {
      super.pageChange(newPageIndex);
      getEditorInput().setTransactionTabActive(newPageIndex == 1);
      if (newPageIndex == 0) {
         changeReportPage.setSelectionProvider();
      } else {
         branchTransactionPage.setSelectionProvider();
      }
   }

}
