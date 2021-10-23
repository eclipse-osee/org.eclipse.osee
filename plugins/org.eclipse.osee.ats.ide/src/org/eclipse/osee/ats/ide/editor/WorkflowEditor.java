/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor;

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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.actions.IDirtyReportable;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.agile.SprintMemberProvider;
import org.eclipse.osee.ats.ide.editor.event.WfeArtifactEventManager;
import org.eclipse.osee.ats.ide.editor.event.WfeBranchEventManager;
import org.eclipse.osee.ats.ide.editor.tab.attributes.WfeAttributesTab;
import org.eclipse.osee.ats.ide.editor.tab.defects.WfeDefectsTab;
import org.eclipse.osee.ats.ide.editor.tab.details.WfeDetailsTab;
import org.eclipse.osee.ats.ide.editor.tab.journal.WfeJournalTab;
import org.eclipse.osee.ats.ide.editor.tab.members.WfeMembersTab;
import org.eclipse.osee.ats.ide.editor.tab.metrics.WfeMetricsTab;
import org.eclipse.osee.ats.ide.editor.tab.relations.WfeRelationsTab;
import org.eclipse.osee.ats.ide.editor.tab.reload.WfeReloadTab;
import org.eclipse.osee.ats.ide.editor.tab.task.WfeTasksTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.WfeWorkFlowTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.util.WfeOutlinePage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.RecentlyVisitedNavigateItems;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalMemberProvider;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskComposite;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.EditorData;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
public class WorkflowEditor extends AbstractArtifactEditor implements EditorData, IDirtyReportable, ISelectedAtsArtifacts, IAtsMetricsProvider {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.ide.editor.WorkflowEditor";
   private AbstractWorkflowArtifact workItem;
   private WfeWorkFlowTab workFlowTab;
   private WfeMembersTab membersTab;
   private WfeDefectsTab defectsTab;
   private WfeTasksTab taskTab;
   private WfeAttributesTab attrTab;
   private WfeRelationsTab relationsTab;
   private WfeJournalTab journalTab;
   int attrPageIndex = 0;
   private final List<IWfeEditorListener> editorListeners = new ArrayList<>();
   private WfeOutlinePage outlinePage;
   private WfeReloadTab reloadTab;
   private WfeMetricsTab metricsTab;
   private WfeDetailsTab detailsTab;

   public void loadPages() {
      addPages();
   }

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
            createAttributesTab();
            createRelationsTab();
            createTaskTab();
            createJournalTab();
            createDefectsTab();
            createMetricsTab();
            createDetailsTab();
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
      reloadTab = new WfeReloadTab(this);
      addPage(reloadTab);
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
         taskTab = new WfeTasksTab(this, (IAtsTeamWorkflow) workItem, AtsApiService.get());
         addPage(taskTab);
         taskTab.refreshTabName();
      }
   }

   private void createJournalTab() throws PartInitException {
      journalTab = new WfeJournalTab(this, workItem, AtsApiService.get());
      addPage(journalTab);
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
               IAtsChangeSet changes = AtsApiService.get().createChangeSet("Workflow Editor - Save");
               // If change was made on Attribute tab, persist workItem separately.  This is cause attribute
               // tab changes conflict with XWidget changes
               // Save widget data to artifact
               workFlowTab.saveXWidgetToArtifact();
               workItem.save(changes);
               changes.executeIfNeeded();
               if (saveListener != null) {
                  saveListener.saved(workItem, changes);
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
      if (workItem != null && !workItem.isDeleted() && workItem.isWfeDirty(new XResultData()).isErrors()) {
         workItem.revert();
      }
      // Tabs are disposed by editor
      if (getToolkit() != null) {
         getToolkit().dispose();
      }
      super.dispose();
   }

   public void disposeTabs() {
      if (metricsTab != null) {
         removePage(metricsTab.getIndex());
      }
      if (attrTab != null) {
         removePage(attrTab.getIndex());
      }
      if (relationsTab != null) {
         removePage(relationsTab.getIndex());
      }
      if (taskTab != null) {
         removePage(taskTab.getIndex());
      }
      if (workFlowTab != null) {
         removePage(workFlowTab.getIndex());
      }
      if (defectsTab != null) {
         removePage(defectsTab.getIndex());
      }
      if (detailsTab != null) {
         removePage(detailsTab.getIndex());
      }
      if (membersTab != null) {
         removePage(membersTab.getIndex());
      }
      if (journalTab != null) {
         removePage(journalTab.getIndex());
      }
      if (reloadTab != null) {
         removePage(reloadTab.getIndex());
         reloadTab = null;
      }
   }

   @Override
   public boolean isDirty() {
      return isDirtyResult(new XResultData()).isErrors();
   }

   @Override
   public XResultData isDirtyResult(XResultData rd) {
      if (workFlowTab == null || getWfeInput().isReload() || (workItem != null && workItem.isDeleted())) {
         return rd;
      }
      try {
         rd.log("===> WorkFlowTab.isXWidgetDirty\n");
         workFlowTab.isXWidgetDirty(rd);

         rd.log("\n===> AWA.isWfeDirty\n");
         ((AbstractWorkflowArtifact) ((WfeInput) getEditorInput()).getArtifact()).isWfeDirty(rd);

         rd.log("\n===> Attribute.isDirty\n");
         for (Attribute<?> attribute : workItem.internalGetAttributes()) {
            if (attribute.isDirty()) {
               rd.errorf("Attribute [%s] is dirty\n", attribute.getNameValueDescription());
            }
         }

         rd.log("\n===> RelationMgr.isDirty\n");
         String rString = RelationManager.reportHasDirtyLinks(workItem);
         if (Strings.isValid(rString)) {
            rd.error("Relation tab is dirty\n");
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         rd.errorf("\n===> WorkflowException.Exception\n[%s]\n", Lib.exceptionToString(ex));
      }
      return rd;
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

   private void createDetailsTab() {
      try {
         detailsTab = new WfeDetailsTab(this, workItem);
         addPage(detailsTab);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private void createMetricsTab() {
      try {
         metricsTab = new WfeMetricsTab(this, this, workItem);
         addPage(metricsTab);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

   }

   private void createAttributesTab() {
      try {
         if (AtsApiService.get().getUserService().isAtsAdmin()) {
            attrTab = new WfeAttributesTab(this, workItem);
            addPage(attrTab);
         }
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createRelationsTab() {
      try {
         if (AtsApiService.get().getUserService().isAtsAdmin()) {
            relationsTab = new WfeRelationsTab(this, workItem);
            addPage(relationsTab);
         }
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   /**
    * Refresh all tabs, sections and widgets. Should be called on reload of artifact or artifact changed event.
    * Attribute widgets are reset so they don't register as dirty cause they don't have latest value in artifact.
    */
   public void refresh() {
      try {
         if (getContainer() == null || getContainer().isDisposed()) {
            return;
         }
         if (workFlowTab != null) {
            workFlowTab.refresh();
         }
         if (attrTab != null) {
            attrTab.refresh();
         }
         if (relationsTab != null) {
            relationsTab.refresh();
         }
         if (journalTab != null) {
            journalTab.refresh();
         }
         if (membersTab != null) {
            membersTab.refresh();
         }
         if (taskTab != null) {
            taskTab.refresh();
         }
         if (defectsTab != null) {
            defectsTab.refresh();
         }
         if (detailsTab != null) {
            detailsTab.refresh();
         }
         // Don't refresh attribute tab, it listens for reload events and ArtifactEvents
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
      return ServiceUtil.getOseeClient().getAccessControlService().hasArtifactPermission(workItem, PermissionEnum.WRITE,
         null).isSuccess();
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
      return AtsApiService.get().getVersionService().getTargetedVersion(workItem);
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
         image = ImageManager.getImage(AtsImage.WORKFLOW);
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

   public void setTabName(int index, String tabName) {
      setPageText(index, tabName);
   }

   public void setPage(int pageId) {
      setActivePage(pageId);
   }

   public Button getReloadButton() {
      if (reloadTab != null) {
         return reloadTab.getReloadButtion();
      }
      return null;
   }

   @Override
   public String getEditorName() {
      return "Workflow Editor";
   }

}
