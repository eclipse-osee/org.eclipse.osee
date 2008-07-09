/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.editor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Donald G. Dunne
 */
public class SMAEditor extends AbstractArtifactEditor implements IDirtiableEditor, IEventReceiver, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private SMAManager smaMgr;
   private int workFlowPageIndex, taskPageIndex, historyPageIndex;
   private SMAWorkFlowTab workFlowTab;
   private SMATaskComposite taskComposite;
   private SMAHistoryComposite historyComposite;
   public static enum PriviledgedEditMode {
      Off, CurrentState, Global
   };
   private PriviledgedEditMode priviledgedEditMode = PriviledgedEditMode.Off;
   private Action printAction;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      if (smaMgr.isHistoricalVersion()) {
         AWorkbench.popup(
               "Historical Error",
               "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());
      } else if (!smaMgr.isAccessControlWrite()) {
         AWorkbench.popup("Authentication Error",
               "You do not have permissions to save " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
      } else {
         try {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  // Save widget data to artifact
                  workFlowTab.saveXWidgetToArtifact();
                  smaMgr.getSma().saveSMA();
               }
            };
            txWrapper.execute();
            workFlowTab.refresh();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
         onDirtied();
      }
      try {
         OseeNotificationManager.sendNotifications();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   void enableGlobalPrint() {
      printAction = new SMAPrint(smaMgr, workFlowTab, taskComposite);
      getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
   }

   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      if (smaMgr != null && !smaMgr.getSma().isDeleted() && smaMgr.getSma().isSMADirty().isTrue()) smaMgr.getSma().revertSMA();
      SkynetEventManager.getInstance().unRegisterAll(this);
      workFlowTab.dispose();
      if (taskComposite != null) taskComposite.dispose();
      super.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return isDirtyResult().isTrue();
   }

   public Result isDirtyResult() {
      if (smaMgr.getSma().isDeleted()) return Result.FalseResult;
      try {
         Result result = workFlowTab.isXWidgetDirty();
         if (result.isTrue()) return result;

         result = ((StateMachineArtifact) ((SMAEditorInput) getEditorInput()).getArtifact()).isSMADirty();
         if (result.isTrue()) return result;

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return new Result(true, ex.getLocalizedMessage());
      }
      return Result.FalseResult;
   }

   public String toString() {
      return "SMAEditor " + smaMgr.getSma().getHumanReadableId() + " - " + smaMgr.getSma().getArtifactTypeName() + " - " + smaMgr.getSma().getDescriptiveName();
   }

   @Override
   protected void createPages() {
      super.createPages();
      SkynetContributionItem.addTo(this, true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      IEditorInput editorInput = getEditorInput();
      StateMachineArtifact sma = null;
      if (editorInput instanceof SMAEditorInput) {
         SMAEditorInput aei = (SMAEditorInput) editorInput;
         if (aei.getArtifact() != null) {
            if (aei.getArtifact() instanceof StateMachineArtifact)
               sma = (StateMachineArtifact) aei.getArtifact();
            else
               throw new IllegalArgumentException("SMAEditorInput artifact must be StateMachineArtifact");
         }
      } else
         throw new IllegalArgumentException("Editor Input not SMAEditorInput");

      if (sma == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
               "Can't Find Action in DB");
         return;
      }
      try {
         smaMgr = new SMAManager(sma, this);
         smaMgr.setEditor(this);

         SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
         SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
         SkynetEventManager.getInstance().register(LocalNewBranchEvent.class, this);
         SkynetEventManager.getInstance().register(RemoteNewBranchEvent.class, this);
         SkynetEventManager.getInstance().register(LocalCommitBranchEvent.class, this);
         SkynetEventManager.getInstance().register(RemoteCommitBranchEvent.class, this);

         setPartName(smaMgr.getSma().getEditorTitle());
         setContentDescription(priviledgedEditMode != PriviledgedEditMode.Off ? " PRIVILEGED EDIT MODE ENABLED - " + priviledgedEditMode.name() : "");
         setTitleImage(smaMgr.getSma().getImage());

         // Create WorkFlow tab
         workFlowTab = new SMAWorkFlowTab(smaMgr);
         workFlowPageIndex = addPage(workFlowTab);

         // Create Tasks tab
         if (smaMgr.showTaskTab()) {
            taskComposite = new SMATaskComposite(this, getContainer(), SWT.NONE);
            taskPageIndex = addPage(taskComposite);
            setPageText(taskPageIndex, "Tasks");
         }

         // Create History tab
         if (smaMgr.showTaskTab()) {
            historyComposite = new SMAHistoryComposite(smaMgr, getContainer(), SWT.NONE);
            historyPageIndex = addPage(historyComposite);
            setPageText(historyPageIndex, "History");
         }

         setActivePage(workFlowPageIndex);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      enableGlobalPrint();
   }

   public void redrawPages() {
      if (workFlowTab != null) workFlowTab.dispose();
      if (taskComposite != null) taskComposite.dispose();
      int page = getActivePage();
      for (int x = getPageCount() - 1; x >= 0; x--)
         removePage(x);
      addPages();
      if (page < getPageCount() && page != -1) setActivePage(page);
   }

   public static void editArtifact(Artifact artifact) {
      if (artifact.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      if (artifact instanceof StateMachineArtifact)
         editArtifact((StateMachineArtifact) artifact);
      else
         ArtifactEditor.editArtifact(artifact);
   }

   public static void editArtifact(StateMachineArtifact sma) {
      if (sma.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         page.openEditor(new SMAEditorInput(sma), EDITOR_ID);
         VisitedItems.addVisited(sma);
      } catch (PartInitException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public static void close(StateMachineArtifact artifact, boolean save) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor) {
            if (((SMAEditor) editor.getPart(false)).getSmaMgr().getSma().equals(artifact)) {
               System.out.println("Closing editor \"" + (artifact.isDeleted() ? "" : artifact.getDescriptiveName()) + "\"");
               page.closeEditor(editor.getEditor(false), save);
            }
         }
      }
   }

   /**
    * @return Returns the smaMgr.
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   public void onEvent(final Event event) {
      try {
         if (smaMgr.isInTransition()) return;
         if (getContainer() == null || getContainer().isDisposed()) return;

         if ((event instanceof LocalCommitBranchEvent) || (event instanceof RemoteCommitBranchEvent) ||
         //
         (event instanceof LocalNewBranchEvent) || (event instanceof RemoteNewBranchEvent)) {
            int branchId = ((BranchEvent) event).getBranchId();
            if (smaMgr.getBranchMgr().getBranchId() != null && smaMgr.getBranchMgr().getBranchId() == branchId) {
               redrawPages();
            }
         } else if (event instanceof TransactionEvent) {
            EventData ed = ((TransactionEvent) event).getEventData(smaMgr.getSma());
            if (ed.isRemoved()) {
               smaMgr.closeEditors(false);
               return;
            } else if (ed.isHasEvent()) {
               setTitleImage(smaMgr.getSma().getImage());
               redrawPages();
            } else if (smaMgr.getReviewManager().hasReviews()) {
               // If related review has made a change, redraw
               for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviews()) {
                  EventData revEd = ((TransactionEvent) event).getEventData(reviewArt);
                  if (revEd.isHasEvent()) {
                     redrawPages();
                     break;
                  }
               }
            }
         } else if (event instanceof CacheArtifactModifiedEvent) {
            if (((CacheArtifactModifiedEvent) event).getType() == ModType.Reverted && ((CacheArtifactModifiedEvent) event).getArtifact().equals(
                  smaMgr.getSma())) {
               redrawPages();
            }
         } else
            OSEELog.logException(AtsPlugin.class, "Unexpected event => " + event, null, false);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      onDirtied();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() throws OseeCoreException, SQLException {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() throws OseeCoreException, SQLException {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() throws OseeCoreException, SQLException {
      return smaMgr;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getResOptions()
    */
   public List<TaskResOptionDefinition> getResOptions() throws SQLException, OseeCoreException {
      return TaskResolutionOptionRule.getTaskResolutionOptions(smaMgr.getWorkPageDefinition());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() throws OseeCoreException, SQLException {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException, SQLException {
      if (stateName == null || stateName.equals(""))
         return smaMgr.getTaskMgr().getTaskArtifacts();
      else
         return smaMgr.getTaskMgr().getTaskArtifacts(stateName);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTaskable()
    */
   public boolean isTaskable() throws OseeCoreException, SQLException {
      return smaMgr.isTaskable();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isUsingTaskResolutionOptions()
    */
   public boolean isUsingTaskResolutionOptions() throws OseeCoreException, SQLException {
      if (smaMgr.getWorkPageDefinition() == null) return false;
      return (smaMgr.getWorkPageDefinition().getWorkItemDefinitionsByType(TaskResolutionOptionRule.WORK_TYPE).size() == 1);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isEditable()
    */
   public boolean isTasksEditable() throws OseeCoreException, SQLException {
      return smaMgr.getSma().isTaskable();
   }

   /**
    * @return the priviledgedEditMode
    */
   public PriviledgedEditMode getPriviledgedEditMode() {
      return priviledgedEditMode;
   }

   /**
    * @param priviledgedEditMode the priviledgedEditMode to set
    */
   public void setPriviledgedEditMode(PriviledgedEditMode priviledgedEditMode) {
      this.priviledgedEditMode = priviledgedEditMode;
      smaMgr.getSma().saveSMA();
      redrawPages();
   }

   /**
    * @return the isAccessControlWrite
    */
   public boolean isAccessControlWrite() {
      return AccessControlManager.getInstance().checkCurrentUserObjectPermission(smaMgr.getSma(), PermissionEnum.WRITE);
   }

}
