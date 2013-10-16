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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.osee.ats.actions.AccessControlAction;
import org.eclipse.osee.ats.actions.DirtyReportAction;
import org.eclipse.osee.ats.actions.IDirtyReportable;
import org.eclipse.osee.ats.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.IXTaskViewer;
import org.eclipse.osee.ats.task.TaskComposite;
import org.eclipse.osee.ats.task.TaskTabXWidgetActionPage;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Donald G. Dunne
 */
public class SMAEditor extends AbstractArtifactEditor implements IWorldEditor, IDirtyReportable, ISMAEditorEventHandler, ISelectedAtsArtifacts, IAtsMetricsProvider, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private AbstractWorkflowArtifact awa;
   private SMAWorkFlowTab workFlowTab;
   private SMAMembersTab membersTab;
   int attributesPageIndex;
   private AttributesComposite attributesComposite;
   private boolean privilegedEditModeEnabled = false;
   private TaskTabXWidgetActionPage taskTabXWidgetActionPage;
   private final List<ISMAEditorListener> editorListeners = new ArrayList<ISMAEditorListener>();
   SMAEditorOutlinePage outlinePage;

   @Override
   protected void addPages() {
      try {
         IEditorInput editorInput = getEditorInput();
         if (editorInput instanceof SMAEditorInput) {
            SMAEditorInput aei = (SMAEditorInput) editorInput;
            if (aei.getArtifact() != null) {
               if (aei.getArtifact() instanceof AbstractWorkflowArtifact) {
                  awa = (AbstractWorkflowArtifact) aei.getArtifact();
               } else {
                  throw new OseeArgumentException("SMAEditorInput artifact must be StateMachineArtifact");
               }
            }
         } else {
            throw new OseeArgumentException("Editor Input not SMAEditorInput");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      if (awa == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
            "Can't Find Action in DB");
         return;
      }
      try {
         SMAEditorArtifactEventManager.add(this);
         SMAEditorBranchEventManager.add(this);

         updatePartName();
         setContentDescription(privilegedEditModeEnabled ? " PRIVILEGED EDIT MODE ENABLED" : "");

         createMembersTab();
         createWorkflowTab();
         createTaskTab();
         createAttributesTab();
         createMetricsTab();

         setActivePage(0);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == IContentOutlinePage.class) {
         SMAEditorOutlinePage page = getOutlinePage();
         page.setInput(this);
         return page;
      }
      return super.getAdapter(adapter);
   }

   public SMAEditorOutlinePage getOutlinePage() {
      if (outlinePage == null) {
         outlinePage = new SMAEditorOutlinePage();
      }
      return outlinePage;
   }

   /**
    * Do not throw exception here, want to create other tabs if this one fails
    */
   private void createWorkflowTab() {
      try {
         workFlowTab = new SMAWorkFlowTab(this, awa);
         addPage(workFlowTab);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createMembersTab() throws PartInitException {
      if (awa instanceof GoalArtifact) {
         membersTab = new SMAMembersTab(this, (GoalArtifact) awa);
         addPage(membersTab);
      }
   }

   private void createTaskTab() throws PartInitException {
      if (isTaskable()) {
         taskTabXWidgetActionPage = new TaskTabXWidgetActionPage(this);
         addPage(taskTabXWidgetActionPage);
      }
   }

   private void updatePartName() throws OseeCoreException {
      setPartName(getTitleStr());
   }

   public String getTitleStr() throws OseeCoreException {
      return awa.getEditorTitle();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         if (awa.isHistoricalVersion()) {
            AWorkbench.popup("Historical Error",
               "You can not change a historical version of " + awa.getArtifactTypeName() + ":\n\n" + awa);
         } else if (!awa.isAccessControlWrite()) {
            AWorkbench.popup("Authentication Error",
               "You do not have permissions to save " + awa.getArtifactTypeName() + ":" + awa);
         } else {
            try {
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Workflow Editor - Save");
               // If change was made on Attribute tab, persist awa separately.  This is cause attribute
               // tab changes conflict with XWidget changes
               if (attributesComposite != null && getActivePage() == attributesPageIndex) {
                  awa.persist(transaction);
               }
               // Save widget data to artifact
               workFlowTab.saveXWidgetToArtifact();
               awa.saveSMA(transaction);
               transaction.execute();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            onDirtied();
         }

         OseeNotificationManager.getInstance().sendNotifications();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
      if (awa != null && !awa.isDeleted() && awa.isSMAEditorDirty().isTrue()) {
         awa.revertSMA();
      }
      if (workFlowTab != null) {
         workFlowTab.dispose();
      }
      if (membersTab != null) {
         membersTab.dispose();
      }
      super.dispose();
   }

   @Override
   public boolean isDirty() {
      Result result = isDirtyResult();
      //      if (result.isTrue()) {
      //         System.out.println(result.getText());
      //      }
      return result.isTrue();
   }

   @Override
   public Result isDirtyResult() {
      if (awa.isDeleted()) {
         return Result.FalseResult;
      }
      try {
         Result result = workFlowTab.isXWidgetDirty();
         if (result.isTrue()) {
            return result;
         }
         result = ((AbstractWorkflowArtifact) ((SMAEditorInput) getEditorInput()).getArtifact()).isSMAEditorDirty();
         if (result.isTrue()) {
            return result;
         }

         String rString = null;
         for (Attribute<?> attribute : awa.internalGetAttributes()) {
            if (attribute.isDirty()) {
               rString = "Attribute: " + attribute.getNameValueDescription();
               break;
            }
         }

         if (rString == null) {
            rString = RelationManager.reportHasDirtyLinks(awa);
         }

         return new Result((rString != null), rString);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result(true, ex.getLocalizedMessage());
      }
   }

   @Override
   public String toString() {
      return "SMAEditor - " + awa.getAtsId() + " - " + awa.getArtifactTypeName() + " named \"" + awa.getName() + "\"";
   }

   @Override
   protected void createPages() {
      super.createPages();
      OseeStatusContributionItemFactory.addTo(this, true);
   }

   private void createMetricsTab() {
      try {
         Composite composite = AtsUtil.createCommonPageComposite(getContainer());
         createToolBar(composite);
         new AtsMetricsComposite(this, composite, SWT.NONE);
         int metricsPageIndex = addPage(composite);
         setPageText(metricsPageIndex, "Metrics");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private void createAttributesTab() {
      try {
         if (!AtsUtilCore.isAtsAdmin()) {
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
                  awa.persist(getClass().getSimpleName());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, awa);
         attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = AtsUtil.createCommonToolBar(parent);

      AtsUtil.actionToToolItem(toolBar, new ResourceHistoryAction(awa), FrameworkImage.EDIT_BLUE);
      AtsUtil.actionToToolItem(toolBar, new AccessControlAction(awa), FrameworkImage.AUTHENTICATED);
      AtsUtil.actionToToolItem(toolBar, new DirtyReportAction(this), FrameworkImage.DIRTY);
      new ToolItem(toolBar, SWT.SEPARATOR);
      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText("Type: \"" + awa.getArtifactTypeName() + "\"   ATS: " + awa.getAtsId());
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
         if (membersTab != null) {
            membersTab.refresh();
         }
         if (attributesComposite != null) {
            attributesComposite.refreshArtifact(awa);
         }
         onDirtied();
         updatePartName();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
      if (artifact instanceof AbstractWorkflowArtifact) {
         editArtifact((AbstractWorkflowArtifact) artifact);
      } else {
         RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
      }
   }

   public static void editArtifact(final AbstractWorkflowArtifact sma) {
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
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
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

   public static void close(final Collection<? extends AbstractWorkflowArtifact> artifacts, boolean save) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorReference editors[] = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
               IEditorReference editor = editors[j];
               if (editor.getPart(false) instanceof SMAEditor && artifacts.contains(((SMAEditor) editor.getPart(false)).getAwa())) {
                  ((SMAEditor) editor.getPart(false)).closeEditor();
               }
            }
         }
      });
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorReference editors[] = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
               IEditorReference editor = editors[j];
               if (editor.getPart(false) instanceof SMAEditor) {
                  ((SMAEditor) editor.getPart(false)).closeEditor();
               }
            }
         }
      });
   }

   public static SMAEditor getSmaEditor(AbstractWorkflowArtifact artifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor && ((SMAEditor) editor.getPart(false)).getAwa().equals(artifact)) {
            return (SMAEditor) editor.getPart(false);
         }
      }
      return null;
   }

   public static List<SMAEditor> getSmaEditors() {
      List<SMAEditor> results = new ArrayList<SMAEditor>();
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor) {
            results.add((SMAEditor) editor.getPart(false));
         }
      }
      return results;
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
   public AbstractWorkflowArtifact getAwa() {
      return awa;
   }

   @Override
   public String getCurrentStateName() {
      return awa.getStateMgr().getCurrentStateName();
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
   public Collection<TaskArtifact> getTaskArtifacts(IStateToken state) throws OseeCoreException {
      if (awa instanceof AbstractTaskableArtifact) {
         return ((AbstractTaskableArtifact) awa).getTaskArtifacts(state);
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<TaskArtifact> getTaskArtifacts() throws OseeCoreException {
      if (awa instanceof AbstractTaskableArtifact) {
         return ((AbstractTaskableArtifact) awa).getTaskArtifacts();
      }
      return Collections.emptyList();
   }

   @Override
   public boolean isTaskable() {
      return awa instanceof AbstractTaskableArtifact;
   }

   @Override
   public boolean isTasksEditable() {
      try {
         if (!(awa instanceof AbstractTaskableArtifact) || awa.isCompletedOrCancelled()) {
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
      return true;
   }

   public boolean isPrivilegedEditModeEnabled() {
      return privilegedEditModeEnabled;
   }

   /**
    * @param privilegedEditMode the privilegedEditMode to set s
    */
   public void setPrivilegedEditMode(boolean enabled) {
      this.privilegedEditModeEnabled = enabled;
      doSave(null);
      if (workFlowTab != null) {
         workFlowTab.refresh();
      }
      if (membersTab != null) {
         membersTab.refresh();
      }
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(awa, PermissionEnum.WRITE);
   }

   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() {
      return Arrays.asList(awa);
   }

   @Override
   public IAtsVersion getMetricsVersionArtifact() throws OseeCoreException {
      return AtsVersionService.get().getTargetedVersion(awa);
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
      return awa.getManHrsPerDayPreference();
   }

   public SMAWorkFlowTab getWorkFlowTab() {
      return workFlowTab;
   }

   public TaskComposite getTaskComposite() {
      return taskTabXWidgetActionPage.getTaskComposite();
   }

   @Override
   public Set<? extends Artifact> getSelectedSMAArtifacts() {
      return Collections.singleton(awa);
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
      return Collections.<Artifact> singletonList(awa);
   }

   @Override
   public Image getTitleImage() {
      return ArtifactImageManager.getImage(awa);
   }

   @Override
   protected void pageChange(int newPageIndex) {
      super.pageChange(newPageIndex);
      if (newPageIndex != -1 && pages.size() > newPageIndex) {
         Object page = pages.get(newPageIndex);
         if (page != null) {
            ISelectionProvider provider = getDefaultSelectionProvider();
            if (page.equals(workFlowTab)) {
               provider = getDefaultSelectionProvider();
            } else if (page.equals(membersTab)) {
               if (membersTab != null && membersTab.getGoalMembersSection() != null) {
                  provider = membersTab.getWorldXViewer();
               }
            } else if (page.equals(taskTabXWidgetActionPage)) {
               provider = taskTabXWidgetActionPage.getTaskComposite().getTaskXViewer();
            } else {
               String title = getPageText(newPageIndex);
               if (title.equalsIgnoreCase("metrics")) {
                  provider = null;
               } else if (title.equalsIgnoreCase("attributes")) {
                  provider = attributesComposite.getTableViewer();
               }
            }
            getSite().setSelectionProvider(provider);
         }
      }
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      if (awa instanceof TaskArtifact) {
         return Arrays.asList((TaskArtifact) awa);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public void reflow() {
      // do nothing
   }

   @Override
   public void setTableTitle(String title, boolean warning) {
      // do nothing
   }

   @Override
   public void reSearch() {
      // do nothing
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
      // do nothing
   }

   @Override
   public String getCurrentTitleLabel() {
      return null;
   }

}