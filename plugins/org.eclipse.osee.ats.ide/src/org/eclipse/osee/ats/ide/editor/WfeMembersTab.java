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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.FilterData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortingData;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.NewActionToGoalAction;
import org.eclipse.osee.ats.ide.workflow.goal.RemoveFromCollectorAction;
import org.eclipse.osee.ats.ide.workflow.goal.RemoveFromCollectorAction.RemovedFromCollectorHandler;
import org.eclipse.osee.ats.ide.workflow.goal.SetCollectorOrderAction;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.world.IMenuActionProvider;
import org.eclipse.osee.ats.ide.world.IWorldEditor;
import org.eclipse.osee.ats.ide.world.IWorldEditorProvider;
import org.eclipse.osee.ats.ide.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.ide.world.WorkflowMetricsUI;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.ats.ide.world.WorldViewDragAndDrop;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.ats.ide.world.WorldXViewerEventManager;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class WfeMembersTab extends FormPage implements IWorldEditor, ISelectedAtsArtifacts, IWorldViewerEventHandler, IMenuActionProvider {
   private IManagedForm managedForm;
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   private WorldComposite worldComposite;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.members.tab";
   private final WorkflowEditor editor;
   private static Map<Long, Integer> idToScrollLocation = new HashMap<>();
   private final ReloadJobChangeAdapter reloadAdapter;
   private final IMemberProvider provider;
   private WorkflowMetricsUI workflowMetricsUi;

   public WfeMembersTab(WorkflowEditor editor, IMemberProvider provider) {
      super(editor, ID, provider.getMembersName());
      this.editor = editor;
      this.provider = provider;
      reloadAdapter = new ReloadJobChangeAdapter(editor);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      this.managedForm = managedForm;
      scrolledForm = managedForm.getForm();
      try {
         scrolledForm.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
               storeScrollLocation();
            }
         });
         updateTitleBar();

         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, false, false);
         bodyComp.setLayoutData(gd);

         setLoading(true);
         refreshData();
         WorldXViewerEventManager.add(this);
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void updateTitleBar() {
      if (Widgets.isAccessible(scrolledForm)) {
         String titleString = editor.getTitleStr();
         String displayableTitle = Strings.escapeAmpersands(titleString);
         if (!scrolledForm.getText().equals(displayableTitle)) {
            scrolledForm.setText(displayableTitle);
         }

         KeyedImage image = provider.getImageKey();
         if (image != null) {
            if (!ImageManager.getImage(image).equals(scrolledForm.getImage())) {
               scrolledForm.setImage(ImageManager.getImage(image));
            }
         }
      }
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (getManagedForm() != null && Widgets.isAccessible(getManagedForm().getForm())) {
         getManagedForm().getForm().getForm().setBusy(busy);
      }
   }

   public void refreshData() {
      if (Widgets.isAccessible(bodyComp)) {
         List<IOperation> ops = AtsBulkLoad.getConfigLoadingOperations();
         IOperation operation =
            Operations.createBuilder("Load " + provider.getMembersName() + " Tab").addAll(ops).build();
         Operations.executeAsJob(operation, false, Job.LONG, reloadAdapter);
      }
   }

   private final class ReloadJobChangeAdapter extends JobChangeAdapter {

      private final WorkflowEditor editor;
      boolean firstTime = true;

      private ReloadJobChangeAdapter(WorkflowEditor editor) {
         this.editor = editor;
         showBusy(true);
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         Job job = new UIJob("Draw Members Tab") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               if (firstTime) {
                  try {
                     if (Widgets.isAccessible(scrolledForm)) {
                        updateTitleBar();
                        setLoading(false);
                        boolean createdAndLoaded = createMembersBody();
                        if (!createdAndLoaded) {
                           reload();
                        }
                        jumptoScrollLocation();
                        FormsUtil.addHeadingGradient(editor.getToolkit(), scrolledForm, true);
                        refreshToolbar();
                        editor.onDirtied();
                     }
                     firstTime = false;
                  } catch (OseeCoreException ex) {
                     handleException(ex);
                  } finally {
                     showBusy(false);
                  }
               } else {
                  try {
                     updateTitleBar();
                  } catch (OseeCoreException ex) {
                     handleException(ex);
                  } finally {
                     showBusy(false);
                  }
                  if (managedForm != null && Widgets.isAccessible(managedForm.getForm())) {
                     refresh();
                  }
               }
               return Status.OK_STATUS;
            }
         };
         Operations.scheduleJob(job, false, Job.SHORT, null);
      }
   }

   private void handleException(Exception ex) {
      setLoading(false);
      if (Widgets.isAccessible(worldComposite)) {
         worldComposite.dispose();
      }
      OseeLog.log(Activator.class, Level.SEVERE, ex);
      new ExceptionComposite(bodyComp, ex);
      bodyComp.layout();
   }

   private void setLoading(boolean set) {
      if (set) {
         loadingComposite = new LoadingComposite(bodyComp);
         bodyComp.layout();
      } else {
         if (Widgets.isAccessible(loadingComposite)) {
            loadingComposite.dispose();
         }
      }
      showBusy(set);
   }

   /**
    * @return true if created; false if skipped
    */
   private boolean createMembersBody() {
      if (!Widgets.isAccessible(worldComposite)) {
         worldComposite =
            new WorldComposite(this, provider.getXViewerFactory(provider.getArtifact()), bodyComp, SWT.BORDER, false);

         new MembersDragAndDrop(worldComposite, WorkflowEditor.EDITOR_ID);
         worldComposite.setLayout(ALayout.getZeroMarginLayout());

         if (editor.getWorkItem().isOfType(AtsArtifactTypes.Goal)) {
            worldComposite.getXViewer().setParentGoal((GoalArtifact) editor.getWorkItem());
         } else {
            worldComposite.getXViewer().setParentSprint((SprintArtifact) editor.getWorkItem());
         }

         worldComposite.getWorldXViewer().addMenuActionProvider(this);
         getSite().setSelectionProvider(worldComposite.getWorldXViewer());
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 100;
         gd.heightHint = 100;
         worldComposite.setLayoutData(gd);
         workflowMetricsUi = new WorkflowMetricsUI(worldComposite, editor.getToolkit());

         editor.getToolkit().adapt(worldComposite);

         reload();
         createActions();

         AttributeFormPart.setLabelFonts(worldComposite, FontManager.getDefaultLabelFont());
         worldComposite.setShowRemoveMenuItems(false);

         return true;
      }
      return false;
   }

   public void reload() {
      if (isTableDisposed()) {
         return;
      }
      String getLoadingString = String.format("Loading %s %s", provider.getCollectorName(), provider.getMembersName());
      Job job = new Job(getLoadingString) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            if (isTableDisposed()) {
               return Status.OK_STATUS;
            }
            try {
               final List<Artifact> artifacts = provider.getMembers();
               try {
                  AtsBulkLoad.bulkLoadArtifacts(artifacts);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     if (isTableDisposed()) {
                        return;
                     }
                     worldComposite.load(provider.getCollectorName(), artifacts, (CustomizeData) null,
                        TableLoadOption.None);
                  }

               });
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  String.format("Exception loading %s", provider.getCollectorName()), ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, false);
   }

   private boolean isTableDisposed() {
      return worldComposite == null || worldComposite.getXViewer() == null || worldComposite.getXViewer().getTree() == null || worldComposite.getXViewer().getTree().isDisposed();
   }

   private void jumptoScrollLocation() {
      //       Jump to scroll location if set
      Integer selection = idToScrollLocation.get(provider.getId());
      if (selection != null) {
         JumpScrollbarJob job = new JumpScrollbarJob("");
         job.schedule(500);
      }
   }

   @Override
   public void dispose() {
      if (worldComposite != null) {
         worldComposite.dispose();
      }
      if (editor.getToolkit() != null) {
         editor.getToolkit().dispose();
      }
   }

   private final Control control = null;

   private void storeScrollLocation() {
      if (managedForm != null && managedForm.getForm() != null) {
         Integer selection = managedForm.getForm().getVerticalBar().getSelection();
         idToScrollLocation.put(provider.getId(), selection);
      }
   }

   private class JumpScrollbarJob extends Job {
      public JumpScrollbarJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               Integer selection = idToScrollLocation.get(provider.getId());

               // Find the ScrolledComposite operating on the control.
               ScrolledComposite sComp = null;
               if (control == null || control.isDisposed()) {
                  return;
               }
               Composite parent = control.getParent();
               while (parent != null) {
                  if (parent instanceof ScrolledComposite) {
                     sComp = (ScrolledComposite) parent;
                     break;
                  }
                  parent = parent.getParent();
               }

               if (sComp != null) {
                  sComp.setOrigin(0, selection);
               }
            }
         });
         return Status.OK_STATUS;

      }
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(worldComposite)) {
               worldComposite.getXViewer().setInput(provider.getMembers());
            }
         }
      });
   }

   private void refreshToolbar() {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();
      toolBarMgr.add(new OpenNewAtsWorldEditorSelectedAction(worldComposite));
      toolBarMgr.add(getWorldXViewer().getCustomizeAction());
      toolBarMgr.add(new Separator());
      toolBarMgr.add(new ExpandAllAction(worldComposite.getXViewer()));
      toolBarMgr.add(new CollapseAllAction(worldComposite.getXViewer()));
      toolBarMgr.add(new RefreshAction(worldComposite));

      toolBarMgr.add(new Separator());

      createDropDownMenuActions();
      toolBarMgr.add(new DropDownAction());

      scrolledForm.updateToolBar();
   }

   public WorldComposite getMembersSection() {
      return worldComposite;
   }

   @Override
   public WorldXViewer getWorldXViewer() {
      if (worldComposite == null) {
         return null;
      }
      return worldComposite.getWorldXViewer();
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered) {
      if (goalMemberReordered.contains(provider.getArtifact()) || sprintMemberReordered.contains(
         provider.getArtifact())) {
         reload();
      } else if (relModifiedArts.contains(provider.getArtifact())) {
         refresh();
      }
   }

   @Override
   public boolean isDisposed() {
      return editor.isDisposed();
   }

   private class MembersDragAndDrop extends WorldViewDragAndDrop {

      private boolean isFeedbackAfter = false;

      public MembersDragAndDrop(WorldComposite worldComposite, String viewId) {
         super(worldComposite, viewId);
      }

      private Artifact getSelectedArtifact(DropTargetEvent event) {
         if (event.item != null && event.item.getData() instanceof Artifact) {
            return AtsClientService.get().getQueryServiceClient().getArtifact(
               AtsClientService.get().getQueryServiceClient().getArtifact(event.item.getData()));
         }
         return null;
      }

      private CustomizeData getCustomizeData() {
         CustomizeData customizeData = worldComposite.getCustomizeDataCopy();
         Conditions.checkNotNull(customizeData, "Customized Data");
         return customizeData;
      }

      private FilterData getFilterData() {
         FilterData filterData = getCustomizeData().getFilterData();
         Conditions.checkNotNull(filterData, "Filter Data");
         return filterData;
      }

      private SortingData getSortingData() {
         SortingData sortingData = getCustomizeData().getSortingData();
         Conditions.checkNotNull(sortingData, "Sort Data");
         return sortingData;
      }

      private String getFilterText() {
         String filterText = getFilterData().getFilterText();
         Conditions.checkNotNull(filterText, "Filter Text");
         return filterText;
      }

      private List<String> getSortingIds() {
         return getSortingData().getSortingIds();
      }

      private boolean isSortedByCollectorsOrder() {
         List<String> sortingIds = getSortingIds();
         return sortingIds.size() == 1 && sortingIds.contains(provider.getColumnName());
      }

      private boolean isFiltered() {
         String filterText = getFilterText();
         return Strings.isValid(filterText);
      }

      private boolean isDropValid() {
         return !isFiltered() && isSortedByCollectorsOrder();
      }

      @Override
      public void operationChanged(DropTargetEvent event) {
         if (!(event.detail == 1)) {
            isFeedbackAfter = false;
         } else {
            isFeedbackAfter = true;
         }
      }

      @Override
      protected boolean isValidForArtifactDrop(DropTargetEvent event) {
         boolean validForDrop = false;
         if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
            ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

            if (artData != null) {
               Artifact[] artifacts = artData.getArtifacts();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact) || art.isOfType(
                     CoreArtifactTypes.UniversalGroup)) {
                     validForDrop = true;
                     break;
                  }
               }
            }
         }
         return validForDrop;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         if (isValidForArtifactDrop(event)) {
            event.detail = DND.DROP_COPY;
            Artifact selectedArtifact = getSelectedArtifact(event);
            if (selectedArtifact != null) {
               if (isFeedbackAfter) {
                  event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL;
               } else {
                  event.feedback = DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_SCROLL;
               }
            }
         } else {
            event.feedback = DND.ERROR_INVALID_DATA;
         }
      }

      @Override
      public void performDrop(final DropTargetEvent event) {
         final ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         final List<Artifact> droppedArtifacts = Arrays.asList(artData.getArtifacts());
         Collections.reverse(droppedArtifacts);
         final Artifact dropTarget = getSelectedArtifact(event);
         try {
            boolean dropValid = isDropValid();
            if (dropValid && ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {

               Collections.reverse(droppedArtifacts);
               List<Artifact> members = provider.getMembers();
               Result result = provider.isAddValid(droppedArtifacts);
               if (result.isFalse()) {
                  if (MessageDialog.openQuestion(Displays.getActiveShell(), "Drop Error", result.getText())) {
                     for (Artifact dropped : droppedArtifacts) {
                        dropped.deleteRelations(provider.getMemberRelationTypeSide().getOpposite());
                     }
                  } else {
                     return;
                  }
               }
               for (Artifact dropped : droppedArtifacts) {
                  if (!dropped.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
                     continue;
                  }
                  if (!members.contains(dropped)) {
                     provider.addMember(dropped);
                  }
                  if (dropTarget != null) {
                     provider.getArtifact().setRelationOrder(provider.getMemberRelationTypeSide(), dropTarget,
                        isFeedbackAfter, dropped);
                  }
               }
               provider.getArtifact().persist(WfeMembersTab.class.getSimpleName());
            } else if (!dropValid) {
               AWorkbench.popup(
                  "Drag/Drop is disabled when table is filtered or sorted.\n\nSwitch to default table customization and try again.");
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
         }
      }

   }

   Action setCollectorOrderAction, removeFromCollectorAction;
   private NewActionToGoalAction newActionToGoalAction;

   private void createActions() {
      setCollectorOrderAction = new SetCollectorOrderAction(provider, (CollectorArtifact) editor.getWorkItem(), this);
      RemovedFromCollectorHandler handler = new RemovedFromCollectorHandler() {

         @Override
         public void removedFromCollector(Collection<? extends Artifact> removed) {
            worldComposite.getXViewer().setInput(provider.getMembers());
         }

      };
      removeFromCollectorAction =
         new RemoveFromCollectorAction(provider, (CollectorArtifact) editor.getWorkItem(), this, handler);
      newActionToGoalAction = new NewActionToGoalAction(provider, (CollectorArtifact) editor.getWorkItem(), this);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = worldComposite.getXViewer().getMenuManager();
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, setCollectorOrderAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, removeFromCollectorAction);
      newActionToGoalAction.refreshText();
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, newActionToGoalAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> artifacts = new HashSet<>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      List<TaskArtifact> tasks = new ArrayList<>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art instanceof TaskArtifact) {
            tasks.add((TaskArtifact) art);
         }
      }
      return tasks;
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
      JobChangeAdapter listener = new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            super.done(event);
            reload();
         }

      };
      provider.deCacheAndReload(false, listener);
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

   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
         addKeyListener();
      }

      @Override
      public Menu getMenu(Control parent) {
         if (fMenu != null) {
            fMenu.dispose();
         }

         fMenu = new Menu(parent);
         addActionToMenu(fMenu, workflowMetricsUi.getOrCreateAction());
         return fMenu;
      }

      @Override
      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      @Override
      public void run() {
         // provided for subclass implementation
      }

      /**
       * Get's rid of the menu, because the menu hangs on to * the searches, etc.
       */
      void clear() {
         dispose();
      }

      private void addKeyListener() {
         Tree tree = worldComposite.getXViewer().getTree();
         GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
         gridData.heightHint = 100;
         gridData.widthHint = 100;
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);

         worldComposite.getXViewer().getTree().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
               // do nothing
            }

            @Override
            public void keyReleased(KeyEvent event) {
               // if CTRL key is already pressed
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     worldComposite.getXViewer().getTree().setSelection(
                        worldComposite.getXViewer().getTree().getItems());
                  } else if (event.keyCode == 'x') {
                     workflowMetricsUi.getOrCreateAction().setChecked(
                        !workflowMetricsUi.getOrCreateAction().isChecked());
                     workflowMetricsUi.getOrCreateAction().run();
                  }
               }
            }
         });
      }
   }

   protected void createDropDownMenuActions() {
      workflowMetricsUi.getOrCreateAction();
   }

}
