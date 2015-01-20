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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.FilterData;
import org.eclipse.nebula.widgets.xviewer.customize.SortingData;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.artifact.CollectorArtifact;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.goal.RemoveFromCollectorAction;
import org.eclipse.osee.ats.goal.RemoveFromCollectorAction.RemovedFromCollectorHandler;
import org.eclipse.osee.ats.goal.SetCollectorOrderAction;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.IMenuActionProvider;
import org.eclipse.osee.ats.world.IWorldViewerEventHandler;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.ats.world.WorldViewDragAndDrop;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerEventManager;
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
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.util.LoadingComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ExceptionComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class SMAMembersTab extends FormPage implements ISelectedAtsArtifacts, IWorldViewerEventHandler, IMenuActionProvider {
   private IManagedForm managedForm;
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   private WorldComposite worldComposite;
   private LoadingComposite loadingComposite;
   public final static String ID = "ats.members.tab";
   private final SMAEditor editor;
   private static Map<String, Integer> guidToScrollLocation = new HashMap<String, Integer>();
   private final ReloadJobChangeAdapter reloadAdapter;
   private final IMemberProvider provider;

   public SMAMembersTab(SMAEditor editor, IMemberProvider provider) {
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

   private void updateTitleBar() throws OseeCoreException {
      if (Widgets.isAccessible(scrolledForm)) {
         String titleString = editor.getTitleStr();
         String displayableTitle = Strings.escapeAmpersands(titleString);
         if (!scrolledForm.getText().equals(displayableTitle)) {
            scrolledForm.setText(displayableTitle);
         }
         KeyedImage image = provider.getImageKey();
         if (!ImageManager.getImage(image).equals(scrolledForm.getImage())) {
            scrolledForm.setImage(ImageManager.getImage(image));
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

      private final SMAEditor editor;
      boolean firstTime = true;

      private ReloadJobChangeAdapter(SMAEditor editor) {
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
            new WorldComposite("workflow.edtor.members.tab", editor,
               provider.getXViewerFactory(provider.getArtifact()), bodyComp, SWT.BORDER, false);

         new MembersDragAndDrop(worldComposite, SMAEditor.EDITOR_ID);

         WorldLabelProvider labelProvider = (WorldLabelProvider) worldComposite.getXViewer().getLabelProvider();
         if (editor.getAwa().isOfType(AtsArtifactTypes.Goal)) {
            labelProvider.setParentGoal((GoalArtifact) editor.getAwa());
         } else {
            labelProvider.setParentSprint((SprintArtifact) editor.getAwa());
         }

         worldComposite.getWorldXViewer().addMenuActionProvider(this);
         getSite().setSelectionProvider(worldComposite.getWorldXViewer());
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 100;
         gd.heightHint = 100;
         worldComposite.setLayoutData(gd);

         reload();
         createActions();
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
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Exception loading %s",
                  provider.getCollectorName()), ex);
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
      Integer selection = guidToScrollLocation.get(provider.getGuid());
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
         guidToScrollLocation.put(provider.getGuid(), selection);
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
               Integer selection = guidToScrollLocation.get(provider.getGuid());

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
               updateShown();
               worldComposite.update();
               worldComposite.getXViewer().refresh();
            }
         }
      });
   }

   private void updateShown() {
      List<Artifact> members;
      try {
         members = provider.getMembers();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return;
      }
      List<Artifact> loadedArtifacts = worldComposite.getLoadedArtifacts();
      List<Artifact> toRemoveFromLoaded = new LinkedList<Artifact>(members);
      members.removeAll(loadedArtifacts);
      for (Artifact art : members) {
         worldComposite.insert(art, -1);
      }
      loadedArtifacts.removeAll(toRemoveFromLoaded);
      worldComposite.removeItems(loadedArtifacts);
      worldComposite.getXViewer().remove(loadedArtifacts);
      worldComposite.getXViewer().refresh(provider.getArtifact());
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
   public void removeItems(Collection<? extends Object> objects) {
      for (Object obj : objects) {
         if (obj instanceof EventBasicGuidArtifact) {
            EventBasicGuidArtifact guidArt = (EventBasicGuidArtifact) obj;
            if (guidArt.getModType() == EventModType.Purged) {
               refresh();
               return;
            }
         }
      }
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts) {
      if (relModifiedArts.contains(provider.getArtifact())) {
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
            return (Artifact) event.item.getData();
         }
         return null;
      }

      private CustomizeData getCustomizeData() throws OseeCoreException {
         CustomizeData customizeData = worldComposite.getCustomizeDataCopy();
         Conditions.checkNotNull(customizeData, "Customized Data");
         return customizeData;
      }

      private FilterData getFilterData() throws OseeCoreException {
         FilterData filterData = getCustomizeData().getFilterData();
         Conditions.checkNotNull(filterData, "Filter Data");
         return filterData;
      }

      private SortingData getSortingData() throws OseeCoreException {
         SortingData sortingData = getCustomizeData().getSortingData();
         Conditions.checkNotNull(sortingData, "Sort Data");
         return sortingData;
      }

      private String getFilterText() throws OseeCoreException {
         String filterText = getFilterData().getFilterText();
         Conditions.checkNotNull(filterText, "Filter Text");
         return filterText;
      }

      private List<String> getSortingIds() throws OseeCoreException {
         return getSortingData().getSortingIds();
      }

      private boolean isSortedByCollectorsOrder() throws OseeCoreException {
         List<String> sortingIds = getSortingIds();
         return (sortingIds.size() == 1 && sortingIds.contains(provider.getColumnName()));
      }

      private boolean isFiltered() throws OseeCoreException {
         String filterText = getFilterText();
         return Strings.isValid(filterText);
      }

      private boolean isDropValid() throws OseeCoreException {
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
                  if (!members.contains(dropped)) {
                     provider.addMember(dropped);
                  }
                  if (dropTarget != null) {
                     provider.getArtifact().setRelationOrder(provider.getMemberRelationTypeSide(), dropTarget,
                        isFeedbackAfter, dropped);
                  }
               }
               provider.getArtifact().persist(SMAMembersTab.class.getSimpleName());
            } else if (!dropValid) {
               AWorkbench.popup("Drag/Drop is disabled when table is filtered or sorted.\n\nSwitch to default table customization and try again.");
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
         }
      }

   }

   Action setCollectorOrderAction, removeFromCollectorAction;

   private void createActions() {
      setCollectorOrderAction = new SetCollectorOrderAction(provider, (CollectorArtifact) editor.getAwa(), this);
      RemovedFromCollectorHandler handler = new RemovedFromCollectorHandler() {

         @Override
         public void removedFromCollector(Collection<? extends Artifact> removed) {
            worldComposite.removeItems(removed);
            worldComposite.getXViewer().remove(removed);
            worldComposite.getXViewer().refresh(provider.getArtifact());
         }

      };
      removeFromCollectorAction =
         new RemoveFromCollectorAction(provider, (CollectorArtifact) editor.getAwa(), this, handler);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = worldComposite.getXViewer().getMenuManager();
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, setCollectorOrderAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, removeFromCollectorAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
   }

   @Override
   public Set<? extends Artifact> getSelectedSMAArtifacts() {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      List<TaskArtifact> tasks = new ArrayList<TaskArtifact>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art instanceof TaskArtifact) {
            tasks.add((TaskArtifact) art);
         }
      }
      return tasks;
   }

}
