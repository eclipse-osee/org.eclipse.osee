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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.AccessControlAction;
import org.eclipse.osee.ats.actions.DirtyReportAction;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.IXTaskViewer;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.ats.task.TaskTabXWidgetActionPage;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactReloadEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class SMAEditor extends AbstractArtifactEditor implements ISelectedAtsArtifacts, IDirtiableEditor, IActionable, IArtifactReloadEventListener, IAtsMetricsProvider, IArtifactsPurgedEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener, IBranchEventListener, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private SMAManager smaMgr;
   private int workFlowPageIndex, metricsPageIndex, attributesPageIndex;
   private SMAWorkFlowTab workFlowTab;
   private AttributesComposite attributesComposite;
   private AtsMetricsComposite metricsComposite;
   private boolean priviledgedEditModeEnabled = false;
   private Action printAction;
   private TaskTabXWidgetActionPage taskTabXWidgetActionPage;

   public SMAEditor() {
      super();
   }

   @Override
   protected void addPages() {

      IEditorInput editorInput = getEditorInput();
      StateMachineArtifact sma = null;
      if (editorInput instanceof SMAEditorInput) {
         SMAEditorInput aei = (SMAEditorInput) editorInput;
         if (aei.getArtifact() != null) {
            if (aei.getArtifact() instanceof StateMachineArtifact) {
               sma = (StateMachineArtifact) aei.getArtifact();
            } else {
               throw new IllegalArgumentException("SMAEditorInput artifact must be StateMachineArtifact");
            }
         }
      } else {
         throw new IllegalArgumentException("Editor Input not SMAEditorInput");
      }

      if (sma == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
               "Can't Find Action in DB");
         return;
      }
      try {
         smaMgr = new SMAManager(sma, this);
         smaMgr.setEditor(this);

         OseeEventManager.addListener(this);
         updatePartName();

         setContentDescription(priviledgedEditModeEnabled ? " PRIVILEGED EDIT MODE ENABLED" : "");

         // Create WorkFlow tab
         try {
            workFlowTab = new SMAWorkFlowTab(smaMgr);
            workFlowPageIndex = addPage(workFlowTab);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }

         // Create Tasks tab
         if (smaMgr.showTaskTab()) {
            createTaskTab();
         }

         createAttributesTab();
         createMetricsTab();

         setActivePage(workFlowPageIndex);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      enableGlobalPrint();
   }

   private void createTaskTab() throws OseeCoreException, PartInitException {
      taskTabXWidgetActionPage = new TaskTabXWidgetActionPage(this);
      addPage(taskTabXWidgetActionPage);
   }

   private void updatePartName() throws OseeCoreException {
      setPartName(getTitleStr());
      setTitleImage(ImageManager.getImage(smaMgr.getSma()));
   }

   public String getTitleStr() throws OseeCoreException {
      return smaMgr.getEditorTitle();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         if (smaMgr.isHistoricalVersion()) {
            AWorkbench.popup(
                  "Historical Error",
                  "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());
         } else if (!smaMgr.isAccessControlWrite()) {
            AWorkbench.popup(
                  "Authentication Error",
                  "You do not have permissions to save " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
         } else {
            try {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Workflow Editor - Save");
               // If change was made on Attribute tab, persist sma separately.  This is cause attribute
               // tab changes conflict with XWidget changes
               if (attributesComposite != null && getActivePage() == attributesPageIndex) {
                  smaMgr.getSma().persist(transaction);
               }
               // Save widget data to artifact
               workFlowTab.saveXWidgetToArtifact();
               smaMgr.getSma().saveSMA(transaction);
               transaction.execute();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            onDirtied();
         }

         OseeNotificationManager.sendNotifications();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public static void setLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         Label label = (Label) parent;
         label.setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFonts(child, font);
         }
         container.layout();
      }
   }

   void enableGlobalPrint() {
      printAction =
            new SMAPrint(smaMgr, workFlowTab,
                  taskTabXWidgetActionPage == null ? null : taskTabXWidgetActionPage.getTaskComposite());
      getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      try {
         if (smaMgr != null && !smaMgr.getSma().isDeleted() && smaMgr.getSma().isSMAEditorDirty().isTrue()) {
            smaMgr.getSma().revertSMA();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      workFlowTab.dispose();
      if (taskTabXWidgetActionPage != null) {
         taskTabXWidgetActionPage.dispose();
      }
      if (metricsComposite != null) {
         metricsComposite.disposeComposite();
      }

      super.dispose();
   }

   @Override
   public boolean isDirty() {
      try {
         return isDirtyResult().isTrue();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return false;
   }

   public Result isDirtyResult() throws OseeStateException {
      if (smaMgr.getSma().isDeleted()) {
         return Result.FalseResult;
      }
      try {
         Result result = workFlowTab.isXWidgetDirty();
         if (result.isTrue()) {
            return result;
         }
         result = ((StateMachineArtifact) ((SMAEditorInput) getEditorInput()).getArtifact()).isSMAEditorDirty();
         if (result.isTrue()) {
            return result;
         }

         String rString = null;
         for (Attribute<?> attribute : smaMgr.getSma().internalGetAttributes()) {
            if (attribute.isDirty()) {
               rString = "Attribute: " + attribute.getNameValueDescription();
               break;
            }
         }

         if (rString == null) {
            rString = RelationManager.reportHasDirtyLinks(smaMgr.getSma());
         }

         return new Result((rString != null), rString);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result(true, ex.getLocalizedMessage());
      }
   }

   @Override
   public String toString() {
      try {
         return "SMAEditor - " + smaMgr.getSma().getHumanReadableId() + " - " + smaMgr.getSma().getArtifactTypeName() + " named \"" + smaMgr.getSma().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return "SMAEditor";
   }

   @Override
   protected void createPages() {
      super.createPages();
      OseeContributionItem.addTo(this, true);
   }

   private void createMetricsTab() {
      try {
         Composite composite = AtsUtil.createCommonPageComposite(getContainer());
         createToolBar(composite);
         metricsComposite = new AtsMetricsComposite(this, composite, SWT.NONE);
         metricsPageIndex = addPage(composite);
         setPageText(metricsPageIndex, "Metrics");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   private void createAttributesTab() {
      try {
         if (!AtsUtil.isAtsAdmin()) {
            return;
         }

         // Create Attributes tab
         Composite composite = AtsUtil.createCommonPageComposite(getContainer());
         ToolBar toolBar = createToolBar(composite);

         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(FrameworkImage.SAVE));
         item.setToolTipText("Save attributes changes only");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  smaMgr.getSma().persist();
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, smaMgr.getSma());
         attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = AtsUtil.createCommonToolBar(parent);

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "ATS Editor");
      AtsUtil.actionToToolItem(toolBar, new ResourceHistoryAction(smaMgr), FrameworkImage.EDIT_BLUE);
      AtsUtil.actionToToolItem(toolBar, new AccessControlAction(smaMgr), FrameworkImage.AUTHENTICATED);
      AtsUtil.actionToToolItem(toolBar, new DirtyReportAction(smaMgr), FrameworkImage.DIRTY);
      new ToolItem(toolBar, SWT.SEPARATOR);
      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      try {
         artifactInfoLabel.setText("Type: \"" + smaMgr.getSma().getArtifactTypeName() + "\"   HRID: " + smaMgr.getSma().getHumanReadableId());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   public void refreshPages() throws OseeCoreException {
      if (getContainer() == null || getContainer().isDisposed()) {
         return;
      }
      if (workFlowTab != null) {
         workFlowTab.refresh();
      }
      if (attributesComposite != null) {
         attributesComposite.refreshArtifact(smaMgr.getSma());
      }
      smaMgr.getEditor().onDirtied();
      updatePartName();
   }

   public static void editArtifact(Artifact artifact) {
      if (artifact.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      if (artifact instanceof StateMachineArtifact) {
         editArtifact((StateMachineArtifact) artifact);
      } else {
         ArtifactEditor.editArtifact(artifact);
      }
   }

   public static void editArtifact(final StateMachineArtifact sma) {
      editArtifact(sma, false);
   }

   public static void editArtifact(final StateMachineArtifact sma, boolean pend) {
      if (sma.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new SMAEditorInput(sma), EDITOR_ID);
               VisitedItems.addVisited(sma);
            } catch (PartInitException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, pend);

   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public static void close(final Collection<? extends StateMachineArtifact> artifacts, boolean save) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               IEditorReference editors[] = page.getEditorReferences();
               for (int j = 0; j < editors.length; j++) {
                  IEditorReference editor = editors[j];
                  if (editor.getPart(false) instanceof SMAEditor) {
                     if (artifacts.contains(((SMAEditor) editor.getPart(false)).getSmaMgr().getSma())) {
                        ((SMAEditor) editor.getPart(false)).closeEditor();
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
            }
         }
      });
   }

   public static SMAEditor getSmaEditor(StateMachineArtifact artifact) {
      try {
         IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
         IEditorReference editors[] = page.getEditorReferences();
         for (int j = 0; j < editors.length; j++) {
            IEditorReference editor = editors[j];
            if (editor.getPart(false) instanceof SMAEditor) {
               if (((SMAEditor) editor.getPart(false)).getSmaMgr().getSma().equals(artifact)) {
                  return (SMAEditor) editor.getPart(false);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return null;
   }

   public void closeEditor() {
      final MultiPageEditorPart editor = this;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   public String getCurrentStateName() throws OseeCoreException {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   public IDirtiableEditor getEditor() throws OseeCoreException {
      return this;
   }

   public SMAManager getParentSmaMgr() throws OseeCoreException {
      return smaMgr;
   }

   public String getTabName() throws OseeCoreException {
      return "Tasks";
   }

   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      if (stateName == null || stateName.equals("")) {
         return smaMgr.getTaskMgr().getTaskArtifacts();
      } else {
         return smaMgr.getTaskMgr().getTaskArtifacts(stateName);
      }
   }

   public boolean isTaskable() throws OseeCoreException {
      return smaMgr.isTaskable();
   }

   public boolean isTasksEditable() throws OseeCoreException {
      return smaMgr.getSma().isTaskable();
   }

   public boolean isPriviledgedEditModeEnabled() {
      return priviledgedEditModeEnabled;
   }

   /**
    * @param priviledgedEditMode the priviledgedEditMode to set s * @throws OseeCoreException
    */
   public void setPriviledgedEditMode(boolean enabled) throws OseeCoreException {
      this.priviledgedEditModeEnabled = enabled;
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Workflow Editor - Save");
      smaMgr.getSma().saveSMA(transaction);
      transaction.execute();
      workFlowTab.refresh();
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(smaMgr.getSma(), PermissionEnum.WRITE);
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      try {
         if (smaMgr.isInTransition()) {
            return;
         }
         if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Purged || branchModType == BranchEventType.Committed) {
            if (smaMgr.getBranchMgr().getId() == null || smaMgr.getBranchMgr().getId() != branchId) {
               return;
            }
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (getContainer() == null || getContainer().isDisposed()) {
                     return;
                  }
                  try {
                     refreshPages();
                     onDirtied();
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (smaMgr.isInTransition()) {
         return;
      }
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) {
         return;
      }
      if (transData.isDeleted(smaMgr.getSma())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               closeEditor();
            }
         });
      } else if (transData.isHasEvent(smaMgr.getSma())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  refreshPages();
                  onDirtied();
               } catch (Exception ex) {
                  // do nothing
               }
            }
         });
      } else if (smaMgr.getReviewManager().hasReviews()) {
         try {
            // If related review has made a change, redraw
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviews()) {
               if (transData.isHasEvent(reviewArt)) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           refreshPages();
                           onDirtied();
                        } catch (Exception ex) {
                           // do nothing
                        }
                     }
                  });
                  // Only refresh editor for first review that has event
                  break;
               }
            }
         } catch (Exception ex) {
            // do nothings
         }
      }
      onDirtied();
   }

   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(smaMgr.getSma())) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  closeEditor();
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationEventType relationEventType, RelationLink link, Branch branch, String relationType) {
      try {
         if (branch.getId() != AtsUtil.getAtsBranch().getId()) {
            return;
         }
         if (link.getArtifactA().equals(smaMgr.getSma()) || link.getArtifactB().equals(smaMgr.getSma())) {
            onDirtied();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getActionDescription() {
      return null;
   }

   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException {
      return Arrays.asList(smaMgr.getSma());
   }

   @Override
   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException {
      return smaMgr.getSma().getWorldViewTargetedVersion();
   }

   @Override
   public void handleRefreshAction() throws OseeCoreException {
   }

   @Override
   public boolean isRefreshActionHandled() throws OseeCoreException {
      return false;
   }

   @Override
   public double getManHoursPerDayPreference() throws OseeCoreException {
      return smaMgr.getSma().getManHrsPerDayPreference();
   }

   public SMAWorkFlowTab getWorkFlowTab() {
      return workFlowTab;
   }

   public TaskComposite getTaskComposite() {
      return taskTabXWidgetActionPage.getTaskComposite();
   }

   public List<XWidget> getXWidgetsFromState(String stateName, Class<?> clazz) {
      return workFlowTab.getXWidgetsFromState(stateName, clazz);
   }

   @Override
   public void handleReloadEvent(Sender sender, Collection<? extends Artifact> artifacts) throws OseeCoreException {
      StateMachineArtifact sma = smaMgr.getSma();
      boolean reload = false;
      if (artifacts.contains(sma)) {
         reload = true;
      }
      if (!reload) {
         for (TaskArtifact taskArt : sma.getSmaMgr().getTaskMgr().getTaskArtifacts()) {
            if (artifacts.contains(taskArt)) {
               reload = true;
               break;
            }
         }
      }
      if (!reload) {
         for (ReviewSMArtifact reviewArt : sma.getSmaMgr().getReviewManager().getReviews()) {
            if (artifacts.contains(reviewArt)) {
               reload = true;
               break;
            }

         }
      }
      if (reload) {
         SMAEditor.close(Collections.singleton(sma), false);
         if (!sma.isDeleted()) {
            SMAEditor.editArtifact(sma);
         }
      }
   }

   @Override
   public IActionable getActionable() throws OseeCoreException {
      return this;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.actions.ISelectedAtsArtifacts#getSelectedSMAArtifacts()
    */
   @Override
   public Set<? extends Artifact> getSelectedSMAArtifacts() throws OseeStateException {
      return Collections.singleton(smaMgr.getSma());
   }

}