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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.actions.AccessControlAction;
import org.eclipse.osee.ats.actions.DirtyReportAction;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.IXTaskViewer;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.ats.task.TaskTabXWidgetActionPage;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class SMAEditor extends AbstractArtifactEditor implements ISMAEditorEventHandler, ISelectedAtsArtifacts, IActionable, IAtsMetricsProvider, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private StateMachineArtifact sma;
   private int workFlowPageIndex, metricsPageIndex, attributesPageIndex;
   private SMAWorkFlowTab workFlowTab;
   private AttributesComposite attributesComposite;
   private boolean priviledgedEditModeEnabled = false;
   private Action printAction;
   private TaskTabXWidgetActionPage taskTabXWidgetActionPage;
   private final List<ISMAEditorListener> editorListeners = new ArrayList<ISMAEditorListener>();

   public SMAEditor() {
      super();
   }

   @Override
   protected void addPages() {
      try {
         IEditorInput editorInput = getEditorInput();
         if (editorInput instanceof SMAEditorInput) {
            SMAEditorInput aei = (SMAEditorInput) editorInput;
            if (aei.getArtifact() != null) {
               if (aei.getArtifact() instanceof StateMachineArtifact) {
                  sma = (StateMachineArtifact) aei.getArtifact();
               } else {
                  throw new OseeArgumentException("SMAEditorInput artifact must be StateMachineArtifact");
               }
            }
         } else {
            throw new OseeArgumentException("Editor Input not SMAEditorInput");
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      if (sma == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
            "Can't Find Action in DB");
         return;
      }
      try {
         sma.setEditor(this);
         SMAEditorArtifactEventManager.add(this);
         SMAEditorBranchEventManager.add(this);

         updatePartName();

         setContentDescription(priviledgedEditModeEnabled ? " PRIVILEGED EDIT MODE ENABLED" : "");

         // Create WorkFlow tab
         try {
            workFlowTab = new SMAWorkFlowTab(sma);
            workFlowPageIndex = addPage(workFlowTab);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }

         // Create Tasks tab
         if (sma.showTaskTab()) {
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

   private void createTaskTab() throws PartInitException {
      taskTabXWidgetActionPage = new TaskTabXWidgetActionPage(this);
      addPage(taskTabXWidgetActionPage);
   }

   private void updatePartName() throws OseeCoreException {
      setPartName(getTitleStr());
      setTitleImage(ArtifactImageManager.getImage(sma));
   }

   public String getTitleStr() throws OseeCoreException {
      return sma.getEditorTitle();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         if (sma.isHistoricalVersion()) {
            AWorkbench.popup("Historical Error",
               "You can not change a historical version of " + sma.getArtifactTypeName() + ":\n\n" + sma);
         } else if (!sma.isAccessControlWrite()) {
            AWorkbench.popup("Authentication Error",
               "You do not have permissions to save " + sma.getArtifactTypeName() + ":" + sma);
         } else {
            try {
               SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Workflow Editor - Save");
               // If change was made on Attribute tab, persist sma separately.  This is cause attribute
               // tab changes conflict with XWidget changes
               if (attributesComposite != null && getActivePage() == attributesPageIndex) {
                  sma.persist(transaction);
               }
               // Save widget data to artifact
               workFlowTab.saveXWidgetToArtifact();
               sma.saveSMA(transaction);
               transaction.execute();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            onDirtied();
         }

         OseeNotificationManager.getInstance().sendNotifications();
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
      printAction = new SMAPrint(sma);
      getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      for (ISMAEditorListener listener : editorListeners) {
         listener.editorDisposing();
      }
      SMAEditorArtifactEventManager.remove(this);
      SMAEditorBranchEventManager.remove(this);
      if (sma != null && !sma.isDeleted() && sma.isSMAEditorDirty().isTrue()) {
         sma.revertSMA();
      }
      workFlowTab.dispose();
      super.dispose();
   }

   @Override
   public boolean isDirty() {
      return isDirtyResult().isTrue();
   }

   public Result isDirtyResult() {
      if (sma.isDeleted()) {
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
         for (Attribute<?> attribute : sma.internalGetAttributes()) {
            if (attribute.isDirty()) {
               rString = "Attribute: " + attribute.getNameValueDescription();
               break;
            }
         }

         if (rString == null) {
            rString = RelationManager.reportHasDirtyLinks(sma);
         }

         return new Result((rString != null), rString);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result(true, ex.getLocalizedMessage());
      }
   }

   @Override
   public String toString() {
      return "SMAEditor - " + sma.getHumanReadableId() + " - " + sma.getArtifactTypeName() + " named \"" + sma.getName() + "\"";
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
         new AtsMetricsComposite(this, composite, SWT.NONE);
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
                  sma.persist();
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, sma);
         attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = AtsUtil.createCommonToolBar(parent);

      OseeUiActions.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "ATS Editor");
      AtsUtil.actionToToolItem(toolBar, new ResourceHistoryAction(sma), FrameworkImage.EDIT_BLUE);
      AtsUtil.actionToToolItem(toolBar, new AccessControlAction(sma), FrameworkImage.AUTHENTICATED);
      AtsUtil.actionToToolItem(toolBar, new DirtyReportAction(sma), FrameworkImage.DIRTY);
      new ToolItem(toolBar, SWT.SEPARATOR);
      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText("Type: \"" + sma.getArtifactTypeName() + "\"   HRID: " + sma.getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   public void refreshPages() {
      try {
         if (getContainer() == null || getContainer().isDisposed()) {
            return;
         }
         if (workFlowTab != null) {
            workFlowTab.refresh();
         }
         if (attributesComposite != null) {
            attributesComposite.refreshArtifact(sma);
         }
         sma.getEditor().onDirtied();
         updatePartName();
      } catch (Exception ex) {
         // do nothing
      }
   }

   public static void editArtifact(Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         return;
      }
      if (artifact.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      if (artifact instanceof StateMachineArtifact) {
         editArtifact((StateMachineArtifact) artifact);
      } else {
         RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
      }
   }

   public static void editArtifact(final StateMachineArtifact sma) {
      if (sma == null) {
         return;
      }
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
      });

   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public static void close(final Collection<? extends StateMachineArtifact> artifacts, boolean save) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorReference editors[] = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
               IEditorReference editor = editors[j];
               if (editor.getPart(false) instanceof SMAEditor) {
                  if (artifacts.contains(((SMAEditor) editor.getPart(false)).getSma())) {
                     ((SMAEditor) editor.getPart(false)).closeEditor();
                  }
               }
            }
         }
      });
   }

   public static SMAEditor getSmaEditor(StateMachineArtifact artifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor) {
            if (((SMAEditor) editor.getPart(false)).getSma().equals(artifact)) {
               return (SMAEditor) editor.getPart(false);
            }
         }
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

   @Override
   public StateMachineArtifact getSma() {
      return sma;
   }

   @Override
   public String getCurrentStateName() throws OseeCoreException {
      return sma.getStateMgr().getCurrentStateName();
   }

   @Override
   public IDirtiableEditor getEditor() {
      return this;
   }

   @Override
   public String getTabName() {
      return "Tasks";
   }

   @Override
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      if (sma instanceof TaskableStateMachineArtifact) {
         if (!Strings.isValid(stateName)) {
            return ((TaskableStateMachineArtifact) sma).getTaskArtifacts();
         } else {
            return ((TaskableStateMachineArtifact) sma).getTaskArtifacts(stateName);
         }
      }
      return Collections.emptyList();
   }

   @Override
   public boolean isTaskable() throws OseeCoreException {
      return sma.isTaskable();
   }

   @Override
   public boolean isTasksEditable() throws OseeCoreException {
      return sma.isTaskable();
   }

   public boolean isPriviledgedEditModeEnabled() {
      return priviledgedEditModeEnabled;
   }

   /**
    * @param priviledgedEditMode the priviledgedEditMode to set s
    */
   public void setPriviledgedEditMode(boolean enabled) {
      this.priviledgedEditModeEnabled = enabled;
      doSave(null);
      workFlowTab.refresh();
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(sma, PermissionEnum.WRITE);
   }

   @Override
   public String getActionDescription() {
      return null;
   }

   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() {
      return Arrays.asList(sma);
   }

   @Override
   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException {
      return sma.getWorldViewTargetedVersion();
   }

   @Override
   public void handleRefreshAction() {
      // do nothing
   }

   @Override
   public boolean isRefreshActionHandled() {
      return false;
   }

   @Override
   public double getManHoursPerDayPreference() throws OseeCoreException {
      return sma.getManHrsPerDayPreference();
   }

   public SMAWorkFlowTab getWorkFlowTab() {
      return workFlowTab;
   }

   public TaskComposite getTaskComposite() {
      return taskTabXWidgetActionPage.getTaskComposite();
   }

   @Override
   public IActionable getActionable() {
      return this;
   }

   @Override
   public Set<? extends Artifact> getSelectedSMAArtifacts() {
      return Collections.singleton(sma);
   }

   public Action getPrintAction() {
      return printAction;
   }

   @Override
   public IEditorPart getActiveEditor() {
      return this;
   }

   @Override
   public SMAEditor getSMAEditor() {
      return this;
   }

   @Override
   public boolean isDisposed() {
      return getContainer() == null || getContainer().isDisposed();
   }

   public void addEditorListeners(ISMAEditorListener listener) {
      editorListeners.add(listener);
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> arts = new ArrayList<Artifact>();
      arts.add(sma);
      return arts;
   }
}