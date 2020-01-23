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

package org.eclipse.osee.ats.ide.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.AccessControlAction;
import org.eclipse.osee.ats.ide.actions.DirtyReportAction;
import org.eclipse.osee.ats.ide.actions.IDirtyReportable;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.ide.agile.SprintMemberProvider;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandler;
import org.eclipse.osee.ats.ide.editor.event.WfeArtifactEventManager;
import org.eclipse.osee.ats.ide.editor.event.WfeBranchEventManager;
import org.eclipse.osee.ats.ide.editor.tab.defects.WfeDefectsTab;
import org.eclipse.osee.ats.ide.editor.tab.members.WfeMembersTab;
import org.eclipse.osee.ats.ide.editor.tab.reload.WfeReloadTab;
import org.eclipse.osee.ats.ide.editor.tab.task.WfeTasksTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.WfeWorkFlowTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfeOutlinePage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.navigate.RecentlyVisitedNavigateItems;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalMemberProvider;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskComposite;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.AtsMetricsComposite;
import org.eclipse.osee.ats.ide.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Donald G. Dunne
 */
public class WorkflowEditor extends AbstractArtifactEditor implements IDirtyReportable, IWfeEventHandler, ISelectedAtsArtifacts, IAtsMetricsProvider {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.ide.editor.WorkflowEditor";
   private AbstractWorkflowArtifact workItem;
   private WfeWorkFlowTab workFlowTab;
   private WfeMembersTab membersTab;
   private WfeDefectsTab defectsTab;
   private WfeTasksTab taskTab;
   int attributesPageIndex;
   private AttributesComposite attributesComposite;
   private final List<IWfeEditorListener> editorListeners = new ArrayList<>();
   WfeOutlinePage outlinePage;
   private final HashCollection<AttributeTypeToken, IWfeEventHandle> attrHandlers = new HashCollection<>();
   private final HashCollection<RelationTypeId, IWfeEventHandle> relHandlers = new HashCollection<>();
   // This MUST be string guid until types are converted to id all at once
   private final HashCollection<String, IWfeEventHandle> artHandlers = new HashCollection<>();

   @Override
   protected void addPages() {
      WfeInput input = getWfeInput();
      try {
         if (input.getArtifact() != null) {
            if (input.getArtifact() instanceof AbstractWorkflowArtifact) {
               workItem = (AbstractWorkflowArtifact) input.getArtifact();
            } else {
               throw new OseeArgumentException("WfeInput artifact must be StateMachineArtifact");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      if (!input.isReload() && workItem == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
            "Can't Find Action in DB");
         return;
      }
      try {
         if (input.isReload()) {
            createReloadTab();
         } else {
            WfeArtifactEventManager.add(this);
            WfeBranchEventManager.add(this);

            createMembersTab();
            createWorkflowTab();
            createTaskTab();
            createDefectsTab();
            createAttributesTab();
            createMetricsTab();
         }
         updatePartName();

         setActivePage(0);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public WfeInput getWfeInput() {
      WfeInput aei = null;
      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof WfeInput) {
         aei = (WfeInput) editorInput;
      } else {
         throw new OseeArgumentException("Editor Input not WfeInput");
      }
      return aei;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type != null && type.isAssignableFrom(IContentOutlinePage.class)) {
         WfeOutlinePage page = getOutlinePage();
         page.setInput(this);
         return (T) page;
      }
      return super.getAdapter(type);
   }

   public WfeOutlinePage getOutlinePage() {
      if (outlinePage == null) {
         outlinePage = new WfeOutlinePage();
      }
      return outlinePage;
   }

   /**
    * Do not throw exception here, want to create other tabs if this one fails
    */
   private void createWorkflowTab() {
      try {
         workFlowTab = new WfeWorkFlowTab(this, workItem);
         addPage(workFlowTab);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createReloadTab() throws PartInitException {
      addPage(new WfeReloadTab(this));
   }

   private void createMembersTab() throws PartInitException {
      if (workItem instanceof GoalArtifact) {
         membersTab = new WfeMembersTab(this, new GoalMemberProvider((GoalArtifact) workItem));
         addPage(membersTab);
      } else if (workItem instanceof SprintArtifact) {
         membersTab = new WfeMembersTab(this, new SprintMemberProvider((SprintArtifact) workItem));
         addPage(membersTab);
      }
   }

   private void createTaskTab() throws PartInitException {
      if (isTaskable()) {
         taskTab = new WfeTasksTab(this, (IAtsTeamWorkflow) workItem, AtsClientService.get());
         addPage(taskTab);
         taskTab.refreshTabName();
      }
   }

   private void createDefectsTab() throws PartInitException {
      if (workItem.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
         defectsTab = new WfeDefectsTab(this, (IAtsPeerToPeerReview) workItem);
         addPage(defectsTab);
      }
   }

   private void updatePartName() {
      setPartName(getTitleStr());
   }

   public String getTitleStr() {
      return getWfeInput().getName();
   }

   public static interface WfeSaveListener {
      void saved(IAtsWorkItem workItem, IAtsChangeSet changes);
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      doSave(monitor, null);
   }

   public void doSave(IProgressMonitor monitor, WfeSaveListener saveListener) {
      try {
         if (workItem.isHistorical()) {
            AWorkbench.popup("Historical Error",
               "You can not change a historical version of " + workItem.getArtifactTypeName() + ":\n\n" + workItem);
         } else if (!workItem.isAccessControlWrite()) {
            AWorkbench.popup("Authentication Error",
               "You do not have permissions to save " + workItem.getArtifactTypeName() + ":" + workItem);
         } else {
            try {
               if (attributesComposite != null && getActivePage() == attributesPageIndex) {
                  workItem.persist("Workflow Editor - Attributes Tab - Save");
               } else {
                  IAtsChangeSet changes = AtsClientService.get().createChangeSet("Workflow Editor - Save");
                  // If change was made on Attribute tab, persist awa separately.  This is cause attribute
                  // tab changes conflict with XWidget changes
                  // Save widget data to artifact
                  workFlowTab.saveXWidgetToArtifact();
                  workItem.save(changes);
                  changes.execute();
                  if (saveListener != null) {
                     saveListener.saved(workItem, changes);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            getWorkFlowTab().computeSizeAndReflow();
            onDirtied();
         }
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
      for (IWfeEditorListener listener : editorListeners) {
         listener.editorDisposing();
      }
      WfeArtifactEventManager.remove(this);
      WfeBranchEventManager.remove(this);
      if (workItem != null && !workItem.isDeleted() && workItem.isWfeDirty().isTrue()) {
         workItem.revert();
      }
      if (workFlowTab != null) {
         workFlowTab.dispose();
      }
      if (membersTab != null) {
         membersTab.dispose();
      }
      if (taskTab != null) {
         taskTab.dispose();
      }
      super.dispose();
   }

   @Override
   public boolean isDirty() {
      return isDirtyResult().isTrue();
   }

   @Override
   public Result isDirtyResult() {
      if (getWfeInput().isReload() || workItem.isDeleted()) {
         return Result.FalseResult;
      }
      try {
         Result result = workFlowTab.isXWidgetDirty();
         if (result.isTrue()) {
            return result;
         }
         result = ((AbstractWorkflowArtifact) ((WfeInput) getEditorInput()).getArtifact()).isWfeDirty();
         if (result.isTrue()) {
            return result;
         }

         String rString = null;
         for (Attribute<?> attribute : workItem.internalGetAttributes()) {
            if (attribute.isDirty()) {
               rString = "Attribute: " + attribute.getNameValueDescription();
               break;
            }
         }

         if (rString == null) {
            rString = RelationManager.reportHasDirtyLinks(workItem);
         }

         return new Result(rString != null, rString);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result(true, ex.getLocalizedMessage());
      }
   }

   @Override
   public String toString() {
      return "WorkflowEditor - " + workItem.getAtsId() + " - " + workItem.getArtifactTypeName() + " named \"" + workItem.getName() + "\"";
   }

   @Override
   protected void createPages() {
      super.createPages();
      OseeStatusContributionItemFactory.addTo(this, true);
   }

   private void createMetricsTab() {
      try {
         Composite composite = createCommonPageComposite(getContainer());
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
         if (!AtsClientService.get().getUserService().isAtsAdmin() && !isDemoDb()) {
            return;
         }

         // Create Attributes tab
         Composite composite = createCommonPageComposite(getContainer());
         ToolBar toolBar = createToolBar(composite);

         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(FrameworkImage.SAVE));
         item.setToolTipText("Save attributes changes only");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  workItem.persist(getClass().getSimpleName());
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         ToolItem refresh = new ToolItem(toolBar, SWT.PUSH);
         refresh.setImage(ImageManager.getImage(FrameworkImage.REFRESH));
         refresh.setToolTipText("Reload Table");
         refresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  workItem.reloadAttributesAndRelations();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, workItem);
         attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private static boolean isDemoDb() {
      String dbType = OseeInfo.getValue(OseeInfo.DB_TYPE_KEY);
      return "demo".equals(dbType);
   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = createCommonToolBar(parent);

      actionToToolItem(toolBar, new ResourceHistoryAction(workItem), FrameworkImage.EDIT_BLUE);
      actionToToolItem(toolBar, new AccessControlAction(workItem), FrameworkImage.AUTHENTICATED);
      actionToToolItem(toolBar, new DirtyReportAction(this), FrameworkImage.DIRTY);
      new ToolItem(toolBar, SWT.SEPARATOR);
      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText("Type: \"" + workItem.getArtifactTypeName() + "\"   ATS: " + workItem.getAtsId());
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
         if (taskTab != null) {
            taskTab.refresh();
         }
         if (attributesComposite != null) {
            attributesComposite.refreshArtifact(workItem);
         }
         onDirtied();
         updatePartName();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void editArtifact(Artifact artifact) {
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

   public static void editArtifact(final AbstractWorkflowArtifact workflow) {
      if (workflow == null) {
         return;
      }
      if (workflow.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new WfeInput(workflow), EDITOR_ID);
               RecentlyVisitedNavigateItems.addVisited(workflow);
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

   public static void close(Set<IAtsTeamWorkflow> singleton, boolean save) {
      close(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(AbstractWorkflowArtifact.class,
         AtsObjects.getArtifacts(singleton)), save);
   }

   public static void close(final Collection<? extends AbstractWorkflowArtifact> artifacts, boolean save) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IEditorReference editors[] = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
               IEditorReference editor = editors[j];
               if (editor.getPart(false) instanceof WorkflowEditor && artifacts.contains(
                  ((WorkflowEditor) editor.getPart(false)).getWorkItem())) {
                  ((WorkflowEditor) editor.getPart(false)).closeEditor();
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
               if (editor.getPart(false) instanceof WorkflowEditor) {
                  ((WorkflowEditor) editor.getPart(false)).closeEditor();
               }
            }
         }
      });
   }

   public static WorkflowEditor getWorkflowEditor(IAtsWorkItem workItem) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         try {
            IEditorReference editor = editors[j];
            if (editor.getPart(false) instanceof WorkflowEditor) {
               // Try to get from editor's work item
               IAtsWorkItem editorWorkItem = ((WorkflowEditor) editor.getPart(false)).getWorkItem();
               if (workItem.equals(editorWorkItem)) {
                  return (WorkflowEditor) editor.getPart(false);
               }
               // Else, try to load from saved work item id
               ArtifactId savedArtId = ((WfeInput) editor.getEditorInput()).getSavedArtUuid();
               if (savedArtId.isValid() && workItem.equals(savedArtId)) {
                  return (WorkflowEditor) editor.getPart(false);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, Lib.exceptionToString(ex));
         }

      }
      return null;
   }

   public static List<WorkflowEditor> getWorkflowEditors() {
      List<WorkflowEditor> results = new ArrayList<>();
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof WorkflowEditor) {
            results.add((WorkflowEditor) editor.getPart(false));
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

   public boolean isTaskable() {
      return workItem instanceof TeamWorkFlowArtifact;
   }

   public boolean isTasksEditable() {
      boolean editable = true;
      if (!(workItem instanceof TeamWorkFlowArtifact) || workItem.isCompletedOrCancelled()) {
         editable = false;
      }
      return editable;
   }

   public boolean isAccessControlWrite() {
      return AccessControlManager.hasPermission(workItem, PermissionEnum.WRITE);
   }

   @Override
   public Collection<? extends Artifact> getMetricsWorkItems() {
      if (workItem.isOfType(AtsArtifactTypes.Goal)) {
         return ((GoalArtifact) workItem).getMembers();
      }
      return Arrays.asList(workItem);
   }

   @Override
   public IAtsVersion getMetricsVersion() {
      return AtsClientService.get().getVersionService().getTargetedVersion(workItem);
   }

   @Override
   public double getManHoursPerDayPreference() {
      return workItem.getManHrsPerDayPreference();
   }

   public WfeWorkFlowTab getWorkFlowTab() {
      return workFlowTab;
   }

   public TaskComposite getTaskComposite() {
      return taskTab.getTaskComposite();
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      return Collections.singleton(workItem);
   }

   @Override
   public IEditorPart getActiveEditor() {
      return this;
   }

   @Override
   public WorkflowEditor getWorkflowEditor() {
      return this;
   }

   @Override
   public boolean isDisposed() {
      return getContainer() == null || getContainer().isDisposed();
   }

   public void addEditorListeners(IWfeEditorListener listener) {
      editorListeners.add(listener);
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      return Collections.<Artifact> singletonList(workItem);
   }

   @Override
   public Image getTitleImage() {
      Image image = null;
      if (getWfeInput().isReload()) {
         image = ImageManager.getImage(AtsImage.TEAM_WORKFLOW);
      } else if (getWfeInput().isBacklog()) {
         image = ImageManager.getImage(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG));
      } else {
         image = ArtifactImageManager.getImage(workItem);
      }
      return image;
   }

   public List<Object> getPages() {
      return pages;
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
               if (membersTab != null && membersTab.getMembersSection() != null) {
                  provider = membersTab.getWorldXViewer();
               }
            } else if (page.equals(taskTab)) {
               if (taskTab.getTaskComposite() != null) {
                  provider = taskTab.getTaskComposite().getWorldXViewer();
               }
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
      if (workItem instanceof TaskArtifact) {
         return Arrays.asList((TaskArtifact) workItem);
      }
      return java.util.Collections.emptyList();
   }

   public AbstractWorkflowArtifact getWorkItem() {
      return workItem;
   }

   public static void edit(IAtsWorkItem workItem) {
      editArtifact((Artifact) workItem.getStoreObject());
   }

   public static Composite createCommonPageComposite(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);

      return composite;
   }

   private ToolItem actionToToolItem(ToolBar toolBar, Action action, KeyedImage imageEnum) {
      final Action fAction = action;
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(imageEnum));
      item.setToolTipText(action.getToolTipText());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            fAction.run();
         }
      });
      return item;
   }

   private ToolBar createCommonToolBar(Composite parent) {
      return createCommonToolBar(parent, null);
   }

   private ToolBar createCommonToolBar(Composite parent, XFormToolkit toolkit) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);
      if (toolkit != null) {
         toolkit.adapt(toolBar.getParent());
      }
      if (toolkit != null) {
         toolkit.adapt(toolBar);
      }
      return toolBar;
   }

   public void setTabName(int index, String tabName) {
      setPageText(index, tabName);
   }

   public void registerEvent(IWfeEventHandle handler, AttributeTypeToken... attrTypes) {
      for (AttributeTypeToken attrType : attrTypes) {
         attrHandlers.put(attrType, handler);
      }
   }

   public void registerEvent(IWfeEventHandle handler, RelationTypeId... relTypes) {
      for (RelationTypeId relType : relTypes) {
         relHandlers.put(relType, handler);
      }
   }

   public void registerEvent(IWfeEventHandle handler, Artifact... artifacts) {
      for (Artifact art : artifacts) {
         artHandlers.put(art.getGuid(), handler);
      }
   }

   public void handleEvent(ArtifactEvent artifactEvent) {
      // Only want to call artHandlers once if an artifact changed for any reason
      Set<String> handledArts = new HashSet<>();
      for (EventBasicGuidArtifact eArt : artifactEvent.getArtifacts()) {
         if (eArt instanceof EventModifiedBasicGuidArtifact) {
            EventModifiedBasicGuidArtifact eMArt = (EventModifiedBasicGuidArtifact) eArt;
            handleArtifactEvent(handledArts, eMArt.getGuid());
            if (eMArt.getGuid().equals(getWorkItem().getGuid())) {
               for (AttributeChange attr : eMArt.getAttributeChanges()) {
                  handleEvent(AttributeTypeManager.getTypeById(attr.getAttrTypeGuid()));
               }
            }
         }
      }
      for (EventBasicGuidRelation eRel : artifactEvent.getRelations()) {
         if (eRel.getArtA().getGuid().equals(getWorkItem().getGuid()) || eRel.getArtB().getGuid().equals(
            getWorkItem().getGuid())) {
            List<IWfeEventHandle> handlers = relHandlers.getValues(RelationTypeId.valueOf(eRel.getRelTypeGuid()));
            if (handlers != null) {
               for (IWfeEventHandle handler : handlers) {
                  Displays.ensureInDisplayThread(new Runnable() {

                     @Override
                     public void run() {
                        handler.refresh();
                     }
                  });
               }
            }
            handledArts.clear();
            if (!handledArts.contains(getWorkItem().getGuid())) {
               List<IWfeEventHandle> handlers2 = artHandlers.getValues(getWorkItem().getStoreObject().getGuid());
               if (handlers2 != null) {
                  for (IWfeEventHandle handler : handlers2) {
                     Displays.ensureInDisplayThread(new Runnable() {

                        @Override
                        public void run() {
                           handler.refresh();
                        }
                     });
                  }
               }
            }
            handledArts.add(getWorkItem().getGuid());
         }
      }
      onDirtied();
      updatePartName();
   }

   private void handleArtifactEvent(Set<String> handledArts, String guid) {
      if (!handledArts.contains(guid)) {
         Artifact loadedWf = ArtifactCache.getActive(guid, AtsClientService.get().getAtsBranch());
         if (loadedWf != null) {
            if (artHandlers.containsKey(loadedWf.getGuid())) {
               Set<IWfeEventHandle> handlers = new HashSet<IWfeEventHandle>();
               handlers.addAll(artHandlers.getValues(loadedWf.getGuid()));
               for (IWfeEventHandle handler : handlers) {
                  handler.refresh();
               }
            }
         }
         handledArts.add(guid);
      }
   }

   public void handleEvent(AttributeTypeToken attrType) {
      List<IWfeEventHandle> handlers = attrHandlers.getValues(attrType);
      if (handlers != null && !handlers.isEmpty()) {
         for (IWfeEventHandle handler : handlers) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  handler.refresh();
               }
            });
         }
      }
   }

   public void setPage(int pageId) {
      setActivePage(pageId);
   }

}