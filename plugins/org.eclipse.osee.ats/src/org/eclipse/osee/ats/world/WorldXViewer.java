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

package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.nebula.widgets.xviewer.action.ColumnMultiEditAction;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.ats.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.actions.DeleteTasksAction;
import org.eclipse.osee.ats.actions.DeleteTasksAction.TaskArtifactProvider;
import org.eclipse.osee.ats.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.actions.EditAssigneeAction;
import org.eclipse.osee.ats.actions.EditStatusAction;
import org.eclipse.osee.ats.actions.EmailActionAction;
import org.eclipse.osee.ats.actions.FavoriteAction;
import org.eclipse.osee.ats.actions.SubscribedAction;
import org.eclipse.osee.ats.agile.SprintOrderColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.column.GoalOrderColumn;
import org.eclipse.osee.ats.column.IPersistAltLeftClickProvider;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionArtifactRollup;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.actions.ISelectedTeamWorkflowArtifacts;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XWorldTextFilter;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeColumn;
import org.eclipse.osee.ats.workflow.TransitionToMenu;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewer extends XViewer implements ISelectedAtsArtifacts, IPersistAltLeftClickProvider, ISelectedTeamWorkflowArtifacts, ISelectedArtifacts, IDirtiableEditor {
   private String extendedStatusString = "";
   public static final String MENU_GROUP_ATS_WORLD_EDIT = "ATS WORLD EDIT";
   public static final String MENU_GROUP_ATS_WORLD_OPEN = "ATS WORLD OPEN";
   public static final String MENU_GROUP_ATS_WORLD_OTHER = "ATS WORLD OTHER";
   public static final String ADD_AS_FAVORITE = "Add as Favorite";
   public static final String REMOVE_FAVORITE = "Remove Favorite";
   public static final String SUBSCRIBE = "Subscribe for Notifications";
   public static final String UN_SUBSCRIBE = "Un-Subscribe for Notifications";
   public final WorldXViewer thisXViewer = this;
   public List<IMenuActionProvider> menuActionProviders = new ArrayList<>();
   protected final IDirtiableEditor editor;
   private GoalArtifact parentGoalArtifact;
   private SprintArtifact parentSprintArtifact;

   public WorldXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor) {
      super(parent, style, xViewerFactory);
      this.editor = editor;
      getTree().addKeyListener(new KeySelectedListener());
   }

   private class KeySelectedListener implements KeyListener {
      @Override
      public void keyPressed(KeyEvent e) {
         // do nothing
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (e.keyCode == SWT.F5) {
            try {
               List<Artifact> artifacts = getSelectedArtifacts();
               RendererManager.open(artifacts, PresentationType.F5_DIFF);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            ((WorldContentProvider) getContentProvider()).clear(false);
         }
      });
      createMenuActions();
   }

   Action editActionableItemsAction;
   protected EditStatusAction editStatusAction;
   EditAssigneeAction editAssigneeAction;

   FavoriteAction favoritesAction;
   SubscribedAction subscribedAction;
   DeletePurgeAtsArtifactsAction deletePurgeAtsObjectAction;
   EmailActionAction emailAction;
   Action resetActionArtifactAction;
   DeleteTasksAction deleteTasksAction;

   public void createMenuActions() {

      favoritesAction = new FavoriteAction(this);
      subscribedAction = new SubscribedAction(this);
      deletePurgeAtsObjectAction = new DeletePurgeAtsArtifactsAction(this, false);
      emailAction = new EmailActionAction(this);
      editStatusAction = new EditStatusAction(this, this, this);
      editAssigneeAction = new EditAssigneeAction(this, this);
      TaskArtifactProvider taskProvider = new TaskArtifactProvider() {

         @Override
         public List<TaskArtifact> getSelectedArtifacts() {
            return getSelectedTaskArtifacts();
         }

      };

      deleteTasksAction = new DeleteTasksAction(taskProvider);

      new Action("Edit", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               new ColumnMultiEditAction(thisXViewer).run();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editActionableItemsAction = new Action("Edit Actionable Item(s)", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (getSelectedTeamWorkflowArtifacts().size() == 1) {
                  TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
                  EditActionableItemsAction.editActionableItems(teamArt);
                  refresh(getSelectedArtifactItems().toArray()[0]);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      resetActionArtifactAction = new Action("Reset Action off Children", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            SkynetTransaction transaction;
            try {
               transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
                  "Reset Action off Children");
               for (ActionArtifact actionArt : getSelectedActionArtifacts()) {
                  ActionArtifactRollup rollup = new ActionArtifactRollup(actionArt);
                  rollup.resetAttributesOffChildren();
                  actionArt.persist(transaction);
               }
               transaction.execute();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }

         }
      };
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      super.handleColumnMultiEdit(treeColumn, treeItems);
      handleColumnMultiEdit(treeColumn, treeItems, true);
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems, final boolean persist) {
      if (treeColumn.getData() instanceof IMultiColumnEditProvider) {
         return;
      }
      if (!(treeColumn.getData() instanceof IAttributeColumn) && !(treeColumn.getData() instanceof XViewerAtsAttributeColumn)) {
         AWorkbench.popup("ERROR", "Column is not attribute and thus not multi-editable " + treeColumn.getText());
         return;
      }

      XResultData rData = new XResultData();
      AttributeTypeToken attributeType = null;
      if (treeColumn.getData() instanceof IAttributeColumn) {
         attributeType = ((IAttributeColumn) treeColumn.getData()).getAttributeType();
      }
      if (attributeType == null) {
         AWorkbench.popup("ERROR", "Can't retrieve attribute name from attribute column " + treeColumn.getText());
         return;
      }
      final Set<Artifact> useArts = new HashSet<>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
         try {
            if (art.isAttributeTypeValid(attributeType)) {
               useArts.add(art);
            } else {
               rData.error(attributeType + " not valid for artifact " + art.toStringWithId());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            rData.error(ex.getLocalizedMessage());
         }
      }

      try {
         if (!rData.isEmpty()) {
            XResultDataUI.report(rData, "Column Multi Edit Errors");
            return;
         }
         if (useArts.size() > 0) {
            ArtifactPromptChange.promptChangeAttribute(attributeType, useArts, persist);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!(treeColumn.getData() instanceof XViewerColumn)) {
         return false;
      }
      if (!((XViewerColumn) treeColumn.getData()).isMultiColumnEditable()) {
         return false;
      }
      if ((XViewerColumn) treeColumn.getData() instanceof IMultiColumnEditProvider) {
         return true;
      }
      AttributeTypeToken attributeType = null;
      // Currently don't know how to multi-edit anything but attribute
      if (treeColumn.getData() instanceof IAttributeColumn) {
         attributeType = ((IAttributeColumn) treeColumn.getData()).getAttributeType();
      } else {
         return false;
      }

      if (attributeType == null) {
         AWorkbench.popup("ERROR", "Can't retrieve attribute name from attribute column " + treeColumn.getText());
         return false;
      }
      for (TreeItem item : treeItems) {
         if (Artifacts.isOfType(item.getData(), AtsArtifactTypes.Action)) {
            return false;
         }
         try {
            Artifact artifact = (Artifact) item.getData();
            if (!artifact.isAttributeTypeValid(attributeType)) {
               return false;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   /**
    * Create Edit menu at top to make easier for users to see and eventually enable menu to get rid of all separate edit
    * items
    */
   public MenuManager updateEditMenu(MenuManager mm) {
      final Collection<TreeItem> selectedTreeItems = Arrays.asList(thisXViewer.getTree().getSelection());
      Set<TreeColumn> editableColumns = ColumnMultiEditAction.getEditableTreeColumns(thisXViewer, selectedTreeItems);

      return XViewerCustomMenu.createEditMenuManager(thisXViewer, "Edit", selectedTreeItems, editableColumns);
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
      MenuManager editMenuManager = updateEditMenu(mm);
      mm.insertBefore(MENU_GROUP_PRE, editMenuManager);

      final Collection<TreeItem> selectedTreeItems = Arrays.asList(thisXViewer.getTree().getSelection());
      mm.insertBefore(MENU_GROUP_PRE,
         TransitionToMenu.createTransitionToMenuManager(thisXViewer, "Transition-To", selectedTreeItems));

      mm.insertBefore(MENU_GROUP_PRE, editStatusAction);
      editStatusAction.setEnabled(getSelectedWorkflowArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editAssigneeAction);
      editAssigneeAction.setEnabled(getSelectedWorkflowArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editActionableItemsAction);
      editActionableItemsAction.setEnabled(getSelectedTeamWorkflowArtifacts().size() == 1);

   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();

      // OPEN MENU BLOCK
      OpenContributionItem contrib = new OpenContributionItem(getClass().getSimpleName() + ".open");
      contrib.fill(mm.getMenu(), -1);
      mm.insertBefore(XViewer.MENU_GROUP_PRE, contrib);
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_EDIT));

      updateEditMenuActions();

      if (AtsClientService.get().getUserService().isAtsAdmin()) {
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
         mm.insertBefore(XViewer.MENU_GROUP_PRE, deletePurgeAtsObjectAction);
         deletePurgeAtsObjectAction.setEnabled(getSelectedAtsArtifacts().size() > 0);
      }

      mm.insertBefore(XViewer.MENU_GROUP_PRE, deleteTasksAction);
      deleteTasksAction.updateEnablement(getSelectedArtifacts());

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OPEN));
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      // OTHER MENU BLOCK
      mm.insertBefore(XViewer.MENU_GROUP_PRE, favoritesAction);
      favoritesAction.updateEnablement();

      mm.insertBefore(XViewer.MENU_GROUP_PRE, subscribedAction);
      subscribedAction.updateEnablement();

      mm.insertBefore(XViewer.MENU_GROUP_PRE, emailAction);
      emailAction.setEnabled(getSelectedWorkflowArtifacts().size() == 1);

      mm.insertBefore(XViewer.MENU_GROUP_PRE, resetActionArtifactAction);
      resetActionArtifactAction.setEnabled(getSelectedActionArtifacts().size() > 0);

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OTHER));
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      for (IMenuActionProvider provider : menuActionProviders) {
         provider.updateMenuActionsForTable();
      }
   }

   @Override
   public void handleDoubleClick() {
      ArtifactDoubleClick.openArtifact(getSelection());
   }

   public List<Artifact> getLoadedArtifacts() {
      List<Artifact> arts = new ArrayList<>();
      if (getRoot() != null) {
         for (Object artifact : (Collection<?>) getRoot()) {
            if (artifact instanceof Artifact) {
               arts.add((Artifact) artifact);
            }
         }
      }
      return arts;
   }

   public void clear(boolean forcePend) {
      ((WorldContentProvider) getContentProvider()).clear(forcePend);
   }

   public void insert(Artifact toInsert, int position) {
      insert(getRoot(), toInsert, position);
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
      super.dispose();
   }

   @Override
   public List<Artifact> getSelectedArtifacts() {
      List<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Artifact) item.getData());
         }
      }
      return arts;
   }

   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() {
      List<TaskArtifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (Artifacts.isOfType(item.getData(), AtsArtifactTypes.Task)) {
               arts.add((TaskArtifact) item.getData());
            }
         }
      }
      return arts;
   }

   /**
    * @return all selected Workflow and any workflow that have Actions with single workflow
    */
   @Override
   public Set<TeamWorkFlowArtifact> getSelectedTeamWorkflowArtifacts() {
      Set<TeamWorkFlowArtifact> teamArts = new HashSet<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (Artifacts.isOfType(item.getData(), AtsArtifactTypes.TeamWorkflow)) {
               teamArts.add((TeamWorkFlowArtifact) item.getData());
            }
            if (Artifacts.isOfType(item.getData(), AtsArtifactTypes.Action)) {
               try {
                  if (AtsClientService.get().getWorkItemService().getTeams(item.getData()).size() == 1) {
                     teamArts.addAll(Collections.castAll(
                        AtsObjects.getArtifacts(AtsClientService.get().getWorkItemService().getTeams(item.getData()))));
                  }
               } catch (OseeCoreException ex) {
                  // Do Nothing
               }
            }
         }
      }
      return teamArts;
   }

   /**
    * @return all selected Workflow and any workflow that have Actions with single workflow
    */
   @Override
   public Set<Artifact> getSelectedWorkflowArtifacts() {
      Set<Artifact> smaArts = new HashSet<>();
      try {
         Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof AbstractWorkflowArtifact) {
               smaArts.add((AbstractWorkflowArtifact) obj);
            } else if (Artifacts.isOfType(obj, AtsArtifactTypes.Action)) {
               smaArts.addAll(Collections.castAll(
                  AtsObjects.getArtifacts(AtsClientService.get().getWorkItemService().getTeams(obj))));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return smaArts;
   }

   public Set<ActionArtifact> getSelectedActionArtifacts() {
      Set<ActionArtifact> actionArts = new HashSet<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (Artifacts.isOfType(item.getData(), AtsArtifactTypes.Action)) {
               actionArts.add((ActionArtifact) item.getData());
            }
         }
      }
      return actionArts;
   }

   public List<Artifact> getSelectedArtifactItems() {
      List<Artifact> arts = new ArrayList<>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Artifact) item.getData());
         }
      }
      return arts;
   }

   @Override
   public String getStatusString() {
      return extendedStatusString;
   }

   public void setExtendedStatusString(String extendedStatusString) {
      this.extendedStatusString = extendedStatusString;
      updateStatusLabel();
   }

   /**
    * store off parent goalItem in label provider so it can determine parent when providing order of goal member
    */
   @Override
   protected void doUpdateItem(Item item, Object element) {
      if (item instanceof TreeItem) {
         GoalArtifact parentGoalArtifact =
            GoalOrderColumn.getParentGoalArtifact((TreeItem) item, AtsArtifactTypes.Goal);
         if (parentGoalArtifact != null) {
            this.parentGoalArtifact = parentGoalArtifact;
         }
         SprintArtifact parentSprintArtifact1 = SprintOrderColumn.getParentSprintArtifact((TreeItem) item);
         if (parentSprintArtifact1 != null) {
            this.parentSprintArtifact = parentSprintArtifact1;
         }
      }
      super.doUpdateItem(item, element);
   }

   public void addMenuActionProvider(IMenuActionProvider provider) {
      menuActionProviders.add(provider);
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<>();
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof AbstractWorkflowArtifact) {
            artifacts.add((AbstractWorkflowArtifact) obj);
         }
      }
      return artifacts;
   }

   @Override
   public boolean isAltLeftClickPersist() {
      return true;
   }

   @Override
   public void onDirtied() {
      if (editor != null) {
         editor.onDirtied();
      }
   }

   /**
    * Value will be set, and changed, as label provider refreshes its elements. This is so the goal members can tell
    * which parent they belong to.
    */
   public void setParentGoal(GoalArtifact parentGoalArtifact) {
      this.parentGoalArtifact = parentGoalArtifact;
   }

   public GoalArtifact getParentGoalArtifact() {
      return parentGoalArtifact;
   }

   public void setParentSprint(SprintArtifact parentSprintArtifact) {
      this.parentSprintArtifact = parentSprintArtifact;
   }

   public SprintArtifact getParentSprintArtifact() {
      return parentSprintArtifact;
   }

   public boolean isDisposed() {
      return getTree() == null || getTree().isDisposed();
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      return new XWorldTextFilter(this);
   }

   @Override
   public void refresh() {
      if (isDisposed()) {
         return;
      }
      super.refreshColumnsWithPreCompute(getInput());
   }

}
