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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.goal.GoalXViewerFactory;
import org.eclipse.osee.ats.goal.RemoveFromGoalAction;
import org.eclipse.osee.ats.goal.RemoveFromGoalAction.RemovedFromGoalHandler;
import org.eclipse.osee.ats.goal.SetGoalOrderAction;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.IMenuActionProvider;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.ats.world.WorldViewDragAndDrop;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMAGoalMembersSection extends Composite implements ISelectedAtsArtifacts, IWorldEditor, IMenuActionProvider, IRefreshActionHandler {

   private final SMAEditor editor;
   private WorldComposite worldComposite;
   private static final Map<String, CustomizeData> editorToCustDataMap = new HashMap<String, CustomizeData>(20);
   private final String id;
   private final GoalArtifact goalArtifact;

   public SMAGoalMembersSection(String id, SMAEditor editor, Composite parent, int style, Integer defaultTableWidth, GoalArtifact goalArtifact) {
      super(parent, style);
      this.id = id;
      this.editor = editor;
      this.goalArtifact = goalArtifact;

      setLayout(new GridLayout(2, true));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      ToolBar toolBar = createToolBar();

      createWorldComposite(defaultTableWidth);
      createActions();
      setupListenersForCustomizeDataCaching();
      fillActionBar(toolBar);
      editor.getToolkit().adapt(this);
   }

   private ToolBar createToolBar() {
      Composite actionComp = new Composite(this, SWT.NONE);
      actionComp.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.horizontalSpan = 2;
      actionComp.setLayoutData(gd);

      ToolBar toolBar = new ToolBar(actionComp, SWT.FLAT | SWT.RIGHT);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      editor.getToolkit().adapt(actionComp);
      editor.getToolkit().adapt(toolBar);
      return toolBar;
   }

   private void fillActionBar(ToolBar toolBar) {

      new ActionContributionItem(worldComposite.getXViewer().getCustomizeAction()).fill(toolBar, -1);
      new ActionContributionItem(new RefreshAction(this)).fill(toolBar, -1);
   }

   private void createWorldComposite(Integer defaultTableWidth) {
      worldComposite =
         new WorldComposite(id, this, new GoalXViewerFactory((GoalArtifact) editor.getAwa()), this, SWT.BORDER, false);

      new GoalDragAndDrop(worldComposite, SMAEditor.EDITOR_ID);

      CustomizeData customizeData = editorToCustDataMap.get(getTableExpandKey());
      if (customizeData == null) {
         customizeData = worldComposite.getCustomizeDataCopy();
      }
      WorldLabelProvider labelProvider = (WorldLabelProvider) worldComposite.getXViewer().getLabelProvider();
      labelProvider.setParentGoal((GoalArtifact) editor.getAwa());

      worldComposite.getWorldXViewer().addMenuActionProvider(this);

      GridData gd = null;
      if (defaultTableWidth != null && defaultTableWidth > 0) {
         gd = new GridData(SWT.FILL, SWT.NONE, true, false);
         gd.heightHint = defaultTableWidth;
      } else {
         gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      }
      gd.widthHint = 200;
      gd.horizontalSpan = 2;
      worldComposite.setLayoutData(gd);
      worldComposite.layout(true);

      customizeData = null;
      reload();
   }

   public void reload() {
      if (isTableDisposed()) {
         return;
      }
      Job job = new Job("Load Goal Members") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            if (isTableDisposed()) {
               return Status.OK_STATUS;
            }
            try {
               final List<Artifact> artifacts =
                  editor.getAwa().getRelatedArtifactsUnSorted(AtsRelationTypes.Goal_Member);
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     if (isTableDisposed()) {
                        return;
                     }
                     worldComposite.load("Members", artifacts, (CustomizeData) null, TableLoadOption.None);
                  }

               });
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception loading Goal Members", ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, false);
   }

   private boolean isTableDisposed() {
      return worldComposite == null || worldComposite.getXViewer() == null || worldComposite.getXViewer().getTree() == null || worldComposite.getXViewer().getTree().isDisposed();
   }

   private String getTableExpandKey() {
      return editor.getAwa().getHumanReadableId() + id;
   }

   private void setupListenersForCustomizeDataCaching() {
      worldComposite.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            editorToCustDataMap.put(getTableExpandKey(), worldComposite.getCustomizeDataCopy());
         }
      });
      editor.addEditorListeners(new ISMAEditorListener() {

         @Override
         public void editorDisposing() {
            editorToCustDataMap.remove(getTableExpandKey());
         }
      });
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(worldComposite)) {
               worldComposite.getXViewer().refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(worldComposite)) {
         worldComposite.dispose();
      }
      super.dispose();
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
      // do nothing
   }

   @Override
   public String getCurrentTitleLabel() {
      return "";
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   @Override
   public void reSearch() {
      // do nothing
   }

   @Override
   public void reflow() {
      // do nothing
   }

   @Override
   public void setTableTitle(String title, boolean warning) {
      // do nothing
   }

   Action setGoalOrderAction, removeFromGoalAction;

   public void createActions() {
      setGoalOrderAction = new SetGoalOrderAction((GoalArtifact) editor.getAwa(), this);
      RemovedFromGoalHandler handler = new RemovedFromGoalHandler() {

         @Override
         public void removedFromGoal(Collection<? extends Artifact> removed) {
            worldComposite.removeItems(removed);
            worldComposite.update();
         }

      };
      removeFromGoalAction = new RemoveFromGoalAction((GoalArtifact) editor.getAwa(), this, handler);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = worldComposite.getXViewer().getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, setGoalOrderAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, removeFromGoalAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
   }

   @Override
   public Set<Artifact> getSelectedSMAArtifacts() {
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

   public WorldComposite getWorldComposite() {
      return worldComposite;
   }

   @Override
   public void refreshActionHandler() {
      refresh();
   }

   private class GoalDragAndDrop extends WorldViewDragAndDrop {

      public GoalDragAndDrop(WorldComposite worldComposite, String viewId) {
         super(worldComposite, viewId);
      }

      private Artifact getSelectedArtifact(DropTargetEvent event) {
         if (event.item != null && event.item.getData() instanceof Artifact) {
            return (Artifact) event.item.getData();
         }
         return null;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         if (isValidForArtifactDrop(event)) {
            event.detail = DND.DROP_MOVE;
            if (getSelectedArtifact(event) != null) {
               event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL;
            } else {
               event.feedback = DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_SCROLL;
            }
         } else {
            event.detail = DND.DROP_COPY;
         }
      }

      @Override
      public void performDrop(final DropTargetEvent event) {
         final ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         final List<Artifact> droppedArtifacts = Arrays.asList(artData.getArtifacts());
         final Artifact dropTarget = getSelectedArtifact(event);
         if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
            if (dropTarget == null) {
               super.performDrop(event);
            } else {
               try {
                  Collections.reverse(droppedArtifacts);
                  List<Artifact> members = goalArtifact.getMembers();
                  for (Artifact dropped : droppedArtifacts) {
                     if (!members.contains(dropped)) {
                        goalArtifact.addMember(dropped);
                        reload();
                     }
                     goalArtifact.setRelationOrder(AtsRelationTypes.Goal_Member, dropTarget, true, dropped);
                  }
                  goalArtifact.persist(SMAGoalMembersSection.class.getSimpleName());
                  worldComposite.getXViewer().refresh(goalArtifact);
                  worldComposite.getXViewer().update(dropTarget, null);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.WARNING, Lib.exceptionToString(ex));
               }
            }
         }
      }

   }

}
