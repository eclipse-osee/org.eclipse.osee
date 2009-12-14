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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.ConvertActionableItemsAction;
import org.eclipse.osee.ats.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.actions.EmailActionAction;
import org.eclipse.osee.ats.actions.FavoriteAction;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.actions.OpenInArtifactEditorAction;
import org.eclipse.osee.ats.actions.OpenInAtsWorkflowEditor;
import org.eclipse.osee.ats.actions.OpenInMassEditorAction;
import org.eclipse.osee.ats.actions.SubscribedAction;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.task.TaskXViewer;
import org.eclipse.osee.ats.util.ArtifactEmailWizard;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactReloadEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewer extends XViewer implements ISelectedAtsArtifacts, IArtifactsPurgedEventListener, IArtifactReloadEventListener, IArtifactsChangeTypeEventListener, IFrameworkTransactionEventListener {
   private String title;
   private String extendedStatusString = "";
   public static final String MENU_GROUP_ATS_WORLD_EDIT = "ATS WORLD EDIT";
   public static final String MENU_GROUP_ATS_WORLD_OPEN = "ATS WORLD OPEN";
   public static final String MENU_GROUP_ATS_WORLD_OTHER = "ATS WORLD OTHER";
   public static final String ADD_AS_FAVORITE = "Add as Favorite";
   public static final String REMOVE_FAVORITE = "Remove Favorite";
   public static final String SUBSCRIBE = "Subscribe for Notifications";
   public static final String UN_SUBSCRIBE = "Un-Subscribe for Notifications";
   public final WorldXViewer thisXViewer = this;

   public WorldXViewer(Composite parent, int style) {
      this(parent, style, new WorldXViewerFactory());
   }

   public WorldXViewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
      if (thisXViewer.getTree().isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      try {
         if (loadedArtifacts.getLoadedArtifacts().isEmpty()) {
            return;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               IContentProvider contentProvider = getContentProvider();
               if (contentProvider instanceof WorldContentProvider) {
                  remove(loadedArtifacts.getLoadedArtifacts().toArray(
                        new Object[loadedArtifacts.getLoadedArtifacts().size()]));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });

   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
      if (thisXViewer.getTree().isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      try {
         if (loadedArtifacts.getLoadedArtifacts().size() == 0) {
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  remove(loadedArtifacts.getLoadedArtifacts().toArray(
                        new Object[loadedArtifacts.getLoadedArtifacts().size()]));
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (thisXViewer.getTree().isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (getContentProvider() == null) {
               return;
            }
            if (transData.cacheDeletedArtifacts.size() > 0) {
               remove(transData.cacheDeletedArtifacts.toArray(new Object[transData.cacheDeletedArtifacts.size()]));
            }
            if (transData.cacheChangedArtifacts.size() > 0) {
               update(transData.cacheChangedArtifacts.toArray(new Object[transData.cacheChangedArtifacts.size()]), null);
               for (Artifact art : transData.cacheChangedArtifacts) {
                  if (art instanceof IWorldViewArtifact) {
                     // If parent is loaded and child changed, refresh parent
                     try {
                        if (art instanceof StateMachineArtifact && ((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact) {
                           update(((StateMachineArtifact) art).getParentAtsArtifact(), null);
                        }
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                  }
               }
            }
            Set<Artifact> arts = new HashSet<Artifact>();
            arts.addAll(transData.cacheRelationAddedArtifacts);
            arts.addAll(transData.cacheRelationChangedArtifacts);
            arts.addAll(transData.cacheRelationDeletedArtifacts);
            for (Artifact art : arts) {
               // Don't refresh deleted artifacts
               if (art.isDeleted()) {
                  continue;
               }
               if (art instanceof IWorldViewArtifact) {
                  refresh(art);
                  // If parent is loaded and child changed, refresh parent
                  try {
                     if (art instanceof StateMachineArtifact && ((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact) {
                        refresh(((StateMachineArtifact) art).getParentAtsArtifact());
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      });
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            ((WorldContentProvider) getContentProvider()).clear(false);
         }
      });
      createMenuActions();
      OseeEventManager.addListener(this);
   }

   Action editStatusAction, editNotesAction, editEstimateAction, editChangeTypeAction, editPriorityAction,
         editTargetVersionAction, editAssigneeAction, editActionableItemsAction;
   ConvertActionableItemsAction convertActionableItemsAction;
   Action openInAtsWorldEditorAction, openInAtsTaskEditorAction;
   OpenInAtsWorkflowEditor openInAtsWorkflowEditorAction;
   OpenInArtifactEditorAction openInArtifactEditorAction;
   OpenInMassEditorAction openInMassEditorAction;
   FavoriteAction favoritesAction;
   SubscribedAction subscribedAction;
   DeletePurgeAtsArtifactsAction deletePurgeAtsObjectAction;
   EmailActionAction emailAction;
   Action resetActionArtifactAction;

   public void createMenuActions() {

      convertActionableItemsAction = new ConvertActionableItemsAction(this);
      openInMassEditorAction = new OpenInMassEditorAction(this);
      openInAtsWorkflowEditorAction = new OpenInAtsWorkflowEditor("Open", this);
      favoritesAction = new FavoriteAction(this);
      subscribedAction = new SubscribedAction(this);
      openInArtifactEditorAction = new OpenInArtifactEditorAction(this);
      deletePurgeAtsObjectAction = new DeletePurgeAtsArtifactsAction(this);
      emailAction = new EmailActionAction(this);

      editNotesAction = new Action("Edit Notes", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAManager.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, getSelectedSMAArtifacts(), true,
                     true)) {
                  update(getSelectedSMAArtifacts().toArray(), null);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editStatusAction = new Action("Edit Status", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAPromptChangeStatus.promptChangeStatus(getSelectedSMAArtifacts(), true)) {
                  update(getSelectedSMAArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editEstimateAction = new Action("Edit Estimated Hours", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (ArtifactPromptChange.promptChangeFloatAttribute(
                     ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(),
                     ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName(), getSelectedSMAArtifacts(), true)) {
                  update(getSelectedSMAArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editChangeTypeAction = new Action("Edit Change Type", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAManager.promptChangeType(getSelectedTeamWorkflowArtifacts(), true)) {
                  update(getSelectedArtifactItems().toArray(), null);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editPriorityAction = new Action("Edit Priority", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangePriority(getSelectedTeamWorkflowArtifacts(), true)) {
               update(getSelectedArtifactItems().toArray(), null);
            }
         }
      };

      editTargetVersionAction = new Action("Edit Targeted Version", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAManager.promptChangeVersion(getSelectedTeamWorkflowArtifacts(),
                     (AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased), true)) {
                  update(getSelectedArtifactItems().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editAssigneeAction = new Action("Edit Assignee", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               Set<StateMachineArtifact> artifacts = getSelectedSMAArtifacts();
               if (SMAManager.promptChangeAssignees(artifacts, false)) {
                  Artifacts.persistInTransaction(artifacts);
                  update(getSelectedArtifactItems().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editActionableItemsAction = new Action("Edit Actionable Item(s)", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (getSelectedActionArtifacts().size() == 1) {
                  ActionArtifact actionArt = getSelectedActionArtifacts().iterator().next();
                  AtsUtil.editActionableItems(actionArt);
                  refresh(getSelectedArtifactItems().iterator().next());
               } else {
                  TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
                  AtsUtil.editActionableItems(teamArt);
                  refresh(getSelectedArtifactItems().toArray()[0]);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      openInAtsWorldEditorAction = new Action("Open in ATS World Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("Error", "No items selected");
               return;
            }
            WorldEditorInput worldEditorInput = null;
            if (thisXViewer instanceof TaskXViewer) {
               worldEditorInput =
                     new WorldEditorInput(new WorldEditorSimpleProvider("ATS World", getSelectedArtifacts(), null,
                           TableLoadOption.None));
            } else {
               worldEditorInput =
                     new WorldEditorInput(new WorldEditorSimpleProvider("ATS World", getSelectedArtifacts(),
                           getCustomizeMgr().generateCustDataFromTable(), TableLoadOption.None));
            }
            if (worldEditorInput != null) {
               IWorkbenchPage page = AWorkbench.getActivePage();
               try {
                  page.openEditor(worldEditorInput, WorldEditor.EDITOR_ID);
               } catch (PartInitException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }

         @Override
         public ImageDescriptor getImageDescriptor() {
            return ImageManager.getImageDescriptor(AtsImage.GLOBE);
         }

      };

      openInAtsTaskEditorAction = new Action("Open in ATS Task Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedTaskArtifacts().size() == 0) {
               AWorkbench.popup("Error", "No Tasks selected");
               return;
            }
            try {
               TaskEditor.open(new TaskEditorSimpleProvider("ATS Tasks", getSelectedTaskArtifacts()));
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         @Override
         public ImageDescriptor getImageDescriptor() {
            return ImageManager.getImageDescriptor(AtsImage.TASK);
         }

      };

      resetActionArtifactAction = new Action("Reset Action off Children", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            SkynetTransaction transaction;
            try {
               transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Reset Action off Children");
               for (ActionArtifact actionArt : getSelectedActionArtifacts()) {
                  actionArt.resetAttributesOffChildren(transaction);
               }
               transaction.execute();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }

         }
      };
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      handleColumnMultiEdit(treeColumn, treeItems, true);
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems, final boolean persist) {
      if (!(treeColumn.getData() instanceof XViewerAttributeColumn) && !(treeColumn.getData() instanceof XViewerAtsAttributeColumn)) {
         AWorkbench.popup("ERROR", "Column is not attribute and thus not multi-editable " + treeColumn.getText());
         return;
      }

      XResultData rData = new XResultData();
      String attrName = null;
      if (treeColumn.getData() instanceof XViewerAttributeColumn) {
         final XViewerAttributeColumn xCol = (XViewerAttributeColumn) treeColumn.getData();
         attrName = xCol.getAttributeTypeName();
      }
      if (treeColumn.getData() instanceof XViewerAtsAttributeColumn) {
         final XViewerAtsAttributeColumn xCol = (XViewerAtsAttributeColumn) treeColumn.getData();
         attrName = xCol.getAttributeTypeName();
      }
      if (attrName == null) {
         AWorkbench.popup("ERROR", "Can't retrieve attribute name from attribute column " + treeColumn.getText());
         return;
      }
      final Set<Artifact> useArts = new HashSet<Artifact>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
         try {
            if (art.isAttributeTypeValid(attrName)) {
               useArts.add(art);
            } else {
               rData.logError(attrName + " not valid for artifact " + art.getGuid() + " - " + art.getName());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            rData.logError(ex.getLocalizedMessage());
         }
      }

      try {
         if (!rData.isEmpty()) {
            rData.report("Column Multi Edit Errors");
            return;
         }
         if (useArts.size() > 0) {
            ArtifactPromptChange.promptChangeAttribute(attrName, attrName, useArts, persist);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
      String attrName = null;
      // Currently don't know how to multi-edit anything but attribute
      if (treeColumn.getData() instanceof XViewerAttributeColumn) {
         XViewerAttributeColumn xCol = (XViewerAttributeColumn) treeColumn.getData();
         attrName = xCol.getAttributeTypeName();
      } else if (treeColumn.getData() instanceof XViewerAtsAttributeColumn) {
         XViewerAtsAttributeColumn xCol = (XViewerAtsAttributeColumn) treeColumn.getData();
         attrName = xCol.getAttributeTypeName();
      } else {
         return false;
      }

      if (attrName == null) {
         AWorkbench.popup("ERROR", "Can't retrieve attribute name from attribute column " + treeColumn.getText());
         return false;
      }
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof ActionArtifact) {
            return false;
         }
         try {
            if (!((Artifact) item.getData()).isAttributeTypeValid(attrName)) {
               return false;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void handleEmailSelectedAtsObject() throws OseeCoreException {
      Artifact art = getSelectedArtifacts().iterator().next();
      if (art instanceof ActionArtifact) {
         if (((ActionArtifact) art).getTeamWorkFlowArtifacts().size() > 1) {
            art = AtsUtil.promptSelectTeamWorkflow((ActionArtifact) art);
            if (art == null) {
               return;
            }
         } else {
            art = ((ActionArtifact) art).getTeamWorkFlowArtifacts().iterator().next();
         }
      }
      if (art != null) {
         ArtifactEmailWizard ew = new ArtifactEmailWizard((StateMachineArtifact) art);
         WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), ew);
         dialog.create();
         dialog.open();
      }
   }

   public StateMachineArtifact getSelectedSMA() {
      Object obj = null;
      if (getSelectedArtifactItems().size() == 0) {
         return null;
      }
      obj = getTree().getSelection()[0].getData();
      return obj != null && obj instanceof StateMachineArtifact ? (StateMachineArtifact) obj : null;
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, editChangeTypeAction);
      editChangeTypeAction.setEnabled(getSelectedTeamWorkflowArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editPriorityAction);
      editPriorityAction.setEnabled(getSelectedTeamWorkflowArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editTargetVersionAction);
      editTargetVersionAction.setEnabled(getSelectedTeamWorkflowArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editAssigneeAction);
      editAssigneeAction.setEnabled(getSelectedSMAArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editStatusAction);
      editStatusAction.setEnabled(getSelectedSMAArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editEstimateAction);
      editEstimateAction.setEnabled(getSelectedSMAArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editNotesAction);
      editNotesAction.setEnabled(getSelectedSMAArtifacts().size() > 0);

      mm.insertBefore(MENU_GROUP_PRE, editActionableItemsAction);
      editActionableItemsAction.setEnabled(getSelectedActionArtifacts().size() == 1 || getSelectedTeamWorkflowArtifacts().size() == 1);

      mm.insertBefore(MENU_GROUP_PRE, convertActionableItemsAction);
      convertActionableItemsAction.updateEnablement();

   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_EDIT));
      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());

      // OPEN MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
      mm.insertBefore(MENU_GROUP_PRE, openInAtsWorkflowEditorAction);
      openInAtsWorkflowEditorAction.updateEnablement();

      MenuManager openWithMenuManager = new MenuManager("Open With", "openwith");

      openWithMenuManager.add(openInMassEditorAction);
      openInMassEditorAction.updateEnablement();

      openWithMenuManager.add(openInAtsWorldEditorAction);
      openInAtsWorldEditorAction.setEnabled(getSelectedArtifacts() != null);
      openWithMenuManager.add(openInAtsTaskEditorAction);
      openInAtsTaskEditorAction.setEnabled(getSelectedTaskArtifacts() != null);
      if (AtsUtil.isAtsAdmin()) {
         openWithMenuManager.add(openInArtifactEditorAction);
         openInArtifactEditorAction.updateEnablement();
      }
      mm.insertBefore(MENU_GROUP_PRE, openWithMenuManager);

      if (AtsUtil.isAtsAdmin()) {
         mm.insertBefore(MENU_GROUP_PRE, new Separator());
         mm.insertBefore(MENU_GROUP_PRE, deletePurgeAtsObjectAction);
         deletePurgeAtsObjectAction.updateEnablement();
      }

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OPEN));
      mm.insertBefore(MENU_GROUP_PRE, new Separator());

      // OTHER MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, favoritesAction);
      favoritesAction.updateEnablement();

      mm.insertBefore(MENU_GROUP_PRE, subscribedAction);
      subscribedAction.updateEnablement();

      mm.insertBefore(MENU_GROUP_PRE, emailAction);
      emailAction.updateEnablement();

      mm.insertBefore(MENU_GROUP_PRE, resetActionArtifactAction);
      resetActionArtifactAction.setEnabled(getSelectedActionArtifacts().size() > 0);

      mm.insertAfter(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OTHER));
      mm.insertAfter(MENU_GROUP_PRE, new Separator());

   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedArtifactItems().size() == 0) {
         return;
      }
      Artifact art = getSelectedArtifactItems().iterator().next();
      AtsUtil.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
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

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its resources
      getLabelProvider().dispose();
      super.dispose();
   }

   public ArrayList<Artifact> getSelectedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Artifact) item.getData());
         }
      }
      return arts;
   }

   public ArrayList<TaskArtifact> getSelectedTaskArtifacts() {
      ArrayList<TaskArtifact> arts = new ArrayList<TaskArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof TaskArtifact) {
               arts.add((TaskArtifact) item.getData());
            }
         }
      }
      return arts;
   }

   /**
    * @return true if all selected are Workflow OR are Actions with single workflow
    */
   public boolean isSelectedTeamWorkflowArtifacts() {
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof ActionArtifact) {
               try {
                  if (((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts().size() != 1) {
                     return false;
                  }
               } catch (OseeCoreException ex) {
                  // Do Nothing
               }
            } else if (!(item.getData() instanceof TeamWorkFlowArtifact)) {
               return false;
            }
         }
      }
      return true;
   }

   /**
    * @return all selected Workflow and any workflow that have Actions with single workflow
    */
   public Set<TeamWorkFlowArtifact> getSelectedTeamWorkflowArtifacts() {
      Set<TeamWorkFlowArtifact> teamArts = new HashSet<TeamWorkFlowArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof TeamWorkFlowArtifact) {
               teamArts.add((TeamWorkFlowArtifact) item.getData());
            }
            if (item.getData() instanceof ActionArtifact) {
               try {
                  if (((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts().size() == 1) {
                     teamArts.addAll(((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts());
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
   public Set<StateMachineArtifact> getSelectedSMAArtifacts() {
      Set<StateMachineArtifact> smaArts = new HashSet<StateMachineArtifact>();
      try {
         Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof StateMachineArtifact) {
               smaArts.add((StateMachineArtifact) obj);
            } else if (obj instanceof ActionArtifact) {
               smaArts.addAll(((ActionArtifact) obj).getTeamWorkFlowArtifacts());
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return smaArts;
   }

   public Set<ActionArtifact> getSelectedActionArtifacts() {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            if (item.getData() instanceof ActionArtifact) {
               actionArts.add((ActionArtifact) item.getData());
            }
         }
      }
      return actionArts;
   }

   public void setCancelledNotification() {
      TreeItem item = getTree().getItem(0);
      if (item.getData() instanceof String) {
         item.setData(DefaultTeamState.Cancelled.name());
      }
      refresh(item.getData());
   }

   /**
    * @param title string to be used in reporting
    */
   public void setReportingTitle(String title) {
      this.title = title;
   }

   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }

   @Override
   public void load(Collection<Object> objects) {
      Set<Artifact> arts = new HashSet<Artifact>();
      for (Object obj : objects) {
         if (obj instanceof IWorldViewArtifact) {
            arts.add((Artifact) obj);
         }
      }
      setInput(arts);
   }

   public ArrayList<Artifact> getSelectedArtifactItems() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
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

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem, true);
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         Artifact useArt = (Artifact) treeItem.getData();
         boolean modified = false;
         if (useArt instanceof ActionArtifact) {
            if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1) {
               useArt = ((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next();
            } else {
               return false;
            }
         }
         if (modified) {
            update(useArt, null);
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem, boolean persist) {
      try {
         super.handleAltLeftClick(treeColumn, treeItem);
         if (!(treeColumn.getData() instanceof XViewerColumn)) {
            return false;
         }
         Artifact useArt = (Artifact) treeItem.getData();
         if (useArt instanceof ActionArtifact) {
            if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1) {
               useArt = ((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next();
            } else {
               return false;
            }
         }
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) useArt);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         boolean modified = false;
         if (xCol.equals(WorldXViewerFactory.Version_Target_Col)) {
            modified =
                  smaMgr.promptChangeVersion(
                        AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, true);
         } else if (xCol.equals(WorldXViewerFactory.Notes_Col)) {
            modified = smaMgr.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, persist, true);
         } else if (xCol.equals(WorldXViewerFactory.Percent_Rework_Col)) {
            modified = smaMgr.promptChangePercentAttribute(ATSAttributes.PERCENT_REWORK_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Estimated_Hours_Col)) {
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Weekly_Benefit_Hrs_Col)) {
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Estimated_Release_Date_Col)) {
            modified = smaMgr.promptChangeEstimatedReleaseDate();
         } else if (xCol.equals(WorldXViewerFactory.Estimated_Completion_Date_Col)) {
            modified = smaMgr.promptChangeDate(ATSAttributes.ESTIMATED_COMPLETION_DATE_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Deadline_Col)) {
            modified = smaMgr.promptChangeDate(ATSAttributes.NEED_BY_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Assignees_Col)) {
            modified = smaMgr.promptChangeAssignees(persist);
         } else if (xCol.equals(WorldXViewerFactory.Remaining_Hours_Col)) {
            AWorkbench.popup("Calculated Field",
                  "Hours Remaining field is calculated.\nHour Estimate - (Hour Estimate * Percent Complete)");
            return false;
         } else if (xCol.equals(WorldXViewerFactory.Work_Days_Needed_Col)) {
            AWorkbench.popup(
                  "Calculated Field",
                  "Work Days Needed field is calculated.\nRemaining Hours / Hours per Week (" + smaMgr.getSma().getManHrsPerDayPreference() + ")");
            return false;
         } else if (xCol.equals(WorldXViewerFactory.Release_Date_Col)) {
            modified = smaMgr.promptChangeReleaseDate();
         } else if (xCol.equals(WorldXViewerFactory.Work_Package_Col)) {
            modified = smaMgr.promptChangeAttribute(ATSAttributes.WORK_PACKAGE_ATTRIBUTE, persist, false);
         } else if (xCol.equals(WorldXViewerFactory.Numeric1_Col)) {
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.NUMERIC1_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Numeric2_Col)) {
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.NUMERIC2_ATTRIBUTE, persist);
         } else if (xCol.equals(WorldXViewerFactory.Category_Col)) {
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY_ATTRIBUTE, persist, true);
         } else if (xCol.equals(WorldXViewerFactory.Category2_Col)) {
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY2_ATTRIBUTE, persist, true);
         } else if (xCol.equals(WorldXViewerFactory.Category3_Col)) {
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY3_ATTRIBUTE, persist, true);
         } else if (xCol.equals(WorldXViewerFactory.Change_Type_Col)) {
            modified = smaMgr.promptChangeType(persist);
         } else if (xCol.equals(WorldXViewerFactory.Priority_Col)) {
            modified = smaMgr.promptChangePriority(persist);
         }
         if (modified) {
            update(useArt, null);
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public String getExtendedStatusString() {
      return extendedStatusString;
   }

   public void setExtendedStatusString(String extendedStatusString) {
      this.extendedStatusString = extendedStatusString;
      updateStatusLabel();
   }

   @Override
   public void handleReloadEvent(Sender sender, final Collection<? extends Artifact> artifacts) throws OseeCoreException {
      if (!artifacts.iterator().next().getBranch().equals(AtsUtil.getAtsBranch())) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (Artifact art : artifacts) {
               if (art instanceof IWorldViewArtifact) {
                  refresh(art);
                  // If parent is loaded and child changed, refresh parent
                  try {
                     if (art instanceof StateMachineArtifact && ((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact) {
                        refresh(((StateMachineArtifact) art).getParentAtsArtifact());
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      });

   }

}
