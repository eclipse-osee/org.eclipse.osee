/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/eplv10.html
 *
 * Contributors:
 *     Boeing  initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsTaskEditorAction.IOpenNewAtsTaskEditorHandler;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsTaskEditorSelected.IOpenNewAtsTaskEditorSelectedHandler;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorAction.IOpenNewAtsWorldEditorHandler;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorSelectedAction.IOpenNewAtsWorldEditorSelectedHandler;
import org.eclipse.osee.ats.ide.actions.TaskAddAction.ITaskAddActionHandler;
import org.eclipse.osee.ats.ide.agile.SprintOrderColumn;
import org.eclipse.osee.ats.ide.column.GoalOrderColumn;
import org.eclipse.osee.ats.ide.config.AtsBulkLoad;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.ITaskEditorProvider;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskXViewer;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class WorldComposite extends Composite implements IOseeTreeReportProvider, ISelectedAtsArtifacts, IWorldViewerEventHandler, IOpenNewAtsWorldEditorHandler, IOpenNewAtsWorldEditorSelectedHandler, IOpenNewAtsTaskEditorHandler, IOpenNewAtsTaskEditorSelectedHandler, IRefreshActionHandler, ITaskAddActionHandler {

   private final WorldXViewer worldXViewer;
   protected IWorldEditor iWorldEditor;
   private boolean showRemoveMenuItems;

   public WorldComposite(IWorldEditor worldEditor, Composite parent, int style) {
      this(worldEditor, null, parent, style, true);
   }

   public WorldComposite(final IWorldEditor worldEditor, IXViewerFactory xViewerFactory, Composite parent, int style, boolean createDragAndDrop) {
      super(parent, style);
      this.iWorldEditor = worldEditor;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      if (DbConnectionExceptionComposite.dbConnectionIsOk(this)) {

         worldXViewer = createXViewer(xViewerFactory, this);
         worldXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

         worldXViewer.setContentProvider(new WorldContentProvider(worldXViewer));
         worldXViewer.setLabelProvider(new WorldLabelProvider(worldXViewer));

         setupDragAndDropSupport(createDragAndDrop);

         WorldXViewerEventManager.add(this);
      } else {
         worldXViewer = null;
      }
   }

   protected void setupDragAndDropSupport(boolean createDragAndDrop) {
      if (createDragAndDrop) {
         new WorldViewDragAndDrop(this, WorldEditor.EDITOR_ID);
      }
   }

   protected WorldXViewer createXViewer(IXViewerFactory xViewerFactory, Composite mainComp) {
      return new WorldXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION,
         xViewerFactory != null ? xViewerFactory : new WorldXViewerFactory(this), null);
   }

   public static double getManHoursPerDayPreference(Collection<Artifact> worldArts) {
      if (worldArts.size() > 0) {
         Artifact artifact = worldArts.iterator().next();
         if (artifact.isOfType(AtsArtifactTypes.Action)) {
            artifact = AtsClientService.get().getQueryServiceClient().getArtifact(
               AtsClientService.get().getWorkItemService().getFirstTeam(artifact));
         }
         return ((AbstractWorkflowArtifact) artifact).getManHrsPerDayPreference();
      }
      return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      worldXViewer.getCustomizeMgr().loadCustomization(customizeData);
   }

   public Control getControl() {
      return worldXViewer.getControl();
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOptions) {
      load(name, arts, null, tableLoadOptions);
   }

   public void load(final String name, final Collection<? extends Artifact> arts, final CustomizeData customizeData, final TableLoadOption... tableLoadOptions) {
      load(name, arts, customizeData, null, tableLoadOptions);
   }

   public void load(final String name, final Collection<? extends Artifact> arts, final CustomizeData customizeData, final Artifact expandToArtifact, final TableLoadOption... tableLoadOptions) {

      Set<TableLoadOption> loadOptions = Collections.asHashSet(tableLoadOptions);
      boolean forcePend = loadOptions.contains(TableLoadOption.ForcePend);
      final Artifact fExpandToArtifact = expandToArtifact;
      if (!forcePend && Displays.isDisplayThread()) {
         Jobs.startJob(new Job("World Composite - Load") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  load(name, arts, customizeData, fExpandToArtifact, tableLoadOptions);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }
         }, true);
         return;
      }

      List<Artifact> worldArts = new LinkedList<>();
      List<Artifact> otherArts = new LinkedList<>();
      for (Artifact art : arts) {
         if (AtsObjects.isAtsWorkItemOrAction(art)) {
            worldArts.add(art);
         } else {
            otherArts.add(art);
         }
      }
      try {
         AtsBulkLoad.bulkLoadArtifacts(worldArts);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(worldXViewer.getTree())) {
               if (customizeData != null && !worldXViewer.getCustomizeMgr().generateCustDataFromTable().equals(
                  customizeData)) {
                  setCustomizeData(customizeData);
               }
               if (arts.isEmpty()) {
                  setTableTitle("No Results Found - " + name, true);
               } else {
                  setTableTitle(name, false);
               }
               worldXViewer.setInputXViewer(worldArts);
               if (worldArts.size() == 0) {
                  worldXViewer.setLoading(false);
               }
               worldXViewer.updateStatusLabel();
               if (otherArts.size() > 0 && MessageDialog.openConfirm(Displays.getActiveShell(),
                  "Open in Artifact Editor?",
                  otherArts.size() + " Non-WorldView Artifacts were returned from request.\n\nOpen in Artifact Editor?")) {
                  RendererManager.openInJob(otherArts, PresentationType.GENERALIZED_EDIT);
               }
               worldXViewer.getTree().setFocus();
            }
         }
      });

      if (expandToArtifact != null) {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               StructuredSelection newSelection = new StructuredSelection(Arrays.asList(expandToArtifact));
               worldXViewer.expandToLevel(expandToArtifact, 1);
               worldXViewer.setSelection(newSelection);
            }
         });
      }

      // Need to reflow the managed page based on the results.  Don't put this in the above thread.
      iWorldEditor.reflow();
   }

   public static class FilterLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         try {
            return ((WorldSearchItem) arg0).getSelectedName(SearchType.Search);
         } catch (OseeCoreException ex) {
            return ex.getLocalizedMessage();
         }
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }
   }

   public static class FilterContentProvider implements IStructuredContentProvider {
      @Override
      public Object[] getElements(Object arg0) {
         return ((ArrayList<?>) arg0).toArray();
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
         // do nothing
      }
   }

   public void setTableTitle(final String title, final boolean warning) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String useTitle = Strings.truncate(title, WorldEditor.TITLE_MAX_LENGTH);
            iWorldEditor.setTableTitle(useTitle, warning);
         };
      });
   }

   public List<Artifact> getLoadedArtifacts() {
      return getXViewer().getLoadedArtifacts();
   }

   public void disposeComposite() {
      if (worldXViewer != null && !worldXViewer.getTree().isDisposed()) {
         worldXViewer.dispose();
      }
      WorldXViewerEventManager.remove(this);
   }

   public WorldXViewer getXViewer() {
      return worldXViewer;
   }

   @Override
   public void refreshActionHandler() {
      try {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               iWorldEditor.reSearch();
            }

         });
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public CustomizeData getCustomizeDataCopy() {
      CustomizeData generateCustDataFromTable = worldXViewer.getCustomizeMgr().generateCustDataFromTable();
      return generateCustDataFromTable;
   }

   @Override
   public IWorldEditorProvider getWorldEditorProviderCopy() {
      IWorldEditorProvider copyProvider = iWorldEditor.getWorldEditorProvider().copyProvider();
      return copyProvider;
   }

   @Override
   public List<Artifact> getSelectedArtifacts() {
      return worldXViewer.getSelectedArtifacts();
   }

   @Override
   public WorldXViewer getWorldXViewer() {
      return worldXViewer;
   }

   @Override
   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered) {
      if (!goalMemberReordered.isEmpty()) {
         XViewerColumn column = getXViewer().getCustomizeMgr().getCurrentTableColumn(GoalOrderColumn.COLUMN_ID);
         if (column != null && column.isShow() == true) {
            getXViewer().refreshColumnsWithPreCompute();
         }
      }
      if (!sprintMemberReordered.isEmpty()) {
         XViewerColumn column = getXViewer().getCustomizeMgr().getCurrentTableColumn(SprintOrderColumn.COLUMN_ID);
         if (column != null && column.isShow() == true) {
            getXViewer().refreshColumnsWithPreCompute();
         }
      }
   }

   @Override
   public String toString() {
      return String.format("WorldComposite [%s]", iWorldEditor.getCurrentTitleLabel());
   }

   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> artifacts = new HashSet<>();
      for (Artifact art : getSelectedArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<>();
      for (Artifact art : getSelectedArtifacts()) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      List<TaskArtifact> tasks = new ArrayList<>();
      for (Artifact art : getSelectedArtifacts()) {
         if (art instanceof TaskArtifact) {
            tasks.add((TaskArtifact) art);
         }
      }
      return tasks;
   }

   @Override
   public ITaskEditorProvider getTaskEditorProviderCopy() {
      return (ITaskEditorProvider) getWorldEditorProviderCopy();
   }

   @Override
   public void taskAddActionHandler() {
      ((TaskXViewer) worldXViewer).handleNewTask();
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Table Report - %s", iWorldEditor.getCurrentTitleLabel());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - World Editor";
   }

   @Override
   public String getReportTitle() {
      return iWorldEditor.getCurrentTitleLabel();
   }

   public boolean isShowRemoveMenuItems() {
      return showRemoveMenuItems;
   }

   public void setShowRemoveMenuItems(boolean showRemoveMenuItems) {
      this.showRemoveMenuItems = showRemoveMenuItems;
      if (worldXViewer != null) {
         worldXViewer.setShowRemoveMenuItems(showRemoveMenuItems);
      }
   }

}
