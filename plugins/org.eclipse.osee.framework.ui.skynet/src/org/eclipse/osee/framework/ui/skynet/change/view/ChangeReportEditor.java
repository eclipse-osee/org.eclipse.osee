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
import org.eclipse.osee.framework.core.model.Branch;
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
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditorInput;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportView;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.editor.FormEditor;

public class ChangeReportEditor extends FormEditor implements IChangeReportView {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.change.ChangeReportEditor";

   private ChangeReportPage changeReportPage;
   private ChangeReportActionBarContributor actionBarContributor;
   private final EventRelay eventRelay;

   public ChangeReportEditor() {
      eventRelay = new EventRelay();
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
      OseeStatusContributionItemFactory.addTo(this, true);
      try {
         changeReportPage = new ChangeReportPage(this);
         addPage(changeReportPage);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
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

   public void refresh() {
      setPartName(getEditorInput().getTitle());
      setTitleImage(getEditorInput().getImage());
      if (changeReportPage != null) {
         changeReportPage.refresh();
      }
   }

   private final class EventRelay implements IBranchEventListener, ITransactionEventListener {

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         ChangeUiData changeUiData = getEditorInput().getChangeData();
         int[] txDeltas = new int[2];
         txDeltas[0] = changeUiData.getTxDelta().getStartTx().getId();
         txDeltas[1] = changeUiData.getTxDelta().getEndTx().getId();
         for (int transactionIdToMatch : txDeltas) {
            for (TransactionChange transChange : transEvent.getTransactionChanges()) {
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
            OseeLog.log(Activator.class, Level.SEVERE, "Error obtaining change report branches for branch event", ex);
         }
         for (Branch branch : branches) {
            if (branch != null && branch.getGuid().equals(branchEvent.getBranchGuid())) {
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
      // set the context (org.eclipse.ui.contexts) to osee to make the osee hotkeys available
      IContextService contextService = (IContextService) getSite().getService(IContextService.class);
      contextService.activateContext("org.eclipse.osee.contexts.window");
   }
}
