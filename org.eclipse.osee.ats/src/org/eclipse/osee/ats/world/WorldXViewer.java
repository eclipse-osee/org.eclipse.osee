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
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.IFavoriteableArtifact;
import org.eclipse.osee.ats.artifact.ISubscribableArtifact;
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
import org.eclipse.osee.ats.util.AtsDeleteManager;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.Favorites;
import org.eclipse.osee.ats.util.Subscribe;
import org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
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
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
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
public class WorldXViewer extends XViewer implements IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener, IFrameworkTransactionEventListener {
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

   /**
    * @param parent
    * @param style
    */
   public WorldXViewer(Composite parent, int style) {
      this(parent, style, new WorldXViewerFactory());
   }

   public WorldXViewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
      super(parent, style, xViewerFactory);
   }

   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().size() == 0) return;
         // ContentProvider ensures in display thread
         ((WorldContentProvider) getContentProvider()).removeAll(loadedArtifacts.getLoadedArtifacts());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, final LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().size() == 0) return;
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  ((WorldContentProvider) getContentProvider()).removeAll(loadedArtifacts.getLoadedArtifacts());
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
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (getContentProvider() == null) return;
            if (transData.cacheDeletedArtifacts.size() > 0) {
               ((WorldContentProvider) getContentProvider()).removeAll(transData.cacheDeletedArtifacts);
            }
            if (transData.cacheChangedArtifacts.size() > 0) {
               ((WorldContentProvider) getContentProvider()).updateAll(transData.cacheChangedArtifacts);
               for (Artifact art : transData.cacheChangedArtifacts) {
                  if (art instanceof IWorldViewArtifact) {
                     // If parent is loaded and child changed, refresh parent
                     try {
                        if ((art instanceof StateMachineArtifact) && (((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact)) {
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
               if (art instanceof IWorldViewArtifact) {
                  refresh(art);
                  // If parent is loaded and child changed, refresh parent
                  try {
                     if ((art instanceof StateMachineArtifact) && (((StateMachineArtifact) art).getParentAtsArtifact() instanceof IWorldViewArtifact)) {
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
            ((WorldContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
      OseeEventManager.addListener(this);
   }

   Action editStatusAction, editNotesAction, editEstimateAction, editChangeTypeAction, editPriorityAction,
         editTargetVersionAction, editAssigneeAction, editActionableItemsAction;
   Action convertActionableItemsAction;
   Action openInArtifactEditorAction, openInAtsWorkflowEditorAction, openInMassEditorAction,
         openInAtsWorldEditorAction, openInAtsTaskEditorAction;
   Action favoritesAction;
   Action subscribedAction;
   Action deletePurgeAtsObjectAction;
   Action emailAction;
   Action resetActionArtifactAction;

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });

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
            if (SMAManager.promptChangeType(getSelectedTeamWorkflowArtifacts(), true)) {
               update(getSelectedArtifactItems().toArray(), null);
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
                     (AtsPlugin.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased), true)) {
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
                  AtsLib.editActionableItems(actionArt);
                  refresh(getSelectedArtifactItems().iterator().next());
               } else {
                  TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
                  AtsLib.editActionableItems(teamArt);
                  refresh(getSelectedArtifactItems().toArray()[0]);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      convertActionableItemsAction = new Action("Convert to Actionable Item/Team", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
               Result result = teamArt.convertActionableItems();
               if (result.isFalse() && !result.getText().equals("")) result.popup();
               refresh(getSelectedArtifactItems().iterator().next());
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      openInMassEditorAction = new Action("Open in Mass Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("Error", "No items selected");
               return;
            }
            MassArtifactEditor.editArtifacts("", getSelectedArtifacts());
         }
      };

      openInAtsWorkflowEditorAction = new Action("Open in ATS Workflow Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AtsLib.openAtsAction(getSelectedArtifactItems().iterator().next(), AtsOpenOption.OpenOneOrPopupSelect);
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
      };

      favoritesAction = new Action("Add as Favorite", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedSMA() != null) (new Favorites(getSelectedSMAArtifacts())).toggleFavorite();
         }
      };

      subscribedAction = new Action("Subscribe for Notifications", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedSMA() != null) (new Subscribe(getSelectedSMAArtifacts())).toggleSubscribe();
         }
      };

      openInArtifactEditorAction = new Action("Open in Artifact Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedArtifacts().size() > 0)
               ArtifactEditor.editArtifact(getSelectedArtifactItems().iterator().next());
            else {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, new Exception("Can't retrieve SMA"));
            }
         }
      };

      deletePurgeAtsObjectAction = new Action("Delete/Purge ATS Object", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               AtsDeleteManager.handleDeletePurgeAtsObject(getSelectedArtifacts(), DeleteOption.Prompt);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      emailAction = new Action("Email ATS Object", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               handleEmailSelectedAtsObject();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      resetActionArtifactAction = new Action("Reset Action off Children", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            SkynetTransaction transaction;
            try {
               transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
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
      if (!(treeColumn.getData() instanceof XViewerAttributeColumn)) {
         AWorkbench.popup("ERROR", "Column is not attribute and thus not multi-editable " + treeColumn.getText());
         return;
      }
      final XViewerAttributeColumn xCol = (XViewerAttributeColumn) treeColumn.getData();
      XResultData rData = new XResultData();
      final String attrName = xCol.getAttributeTypeName();
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
               rData.logError(attrName + " not valid for artifact " + art.getHumanReadableId() + " - " + art.getDescriptiveName());
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
            ArtifactPromptChange.promptChangeAttribute(attrName, xCol.getName(), useArts, persist);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!(treeColumn.getData() instanceof XViewerColumn)) return false;
      if (!((XViewerColumn) treeColumn.getData()).isMultiColumnEditable()) {
         return false;
      }
      // Currently don't know how to multi-edit anything but attribute
      if (!(treeColumn.getData() instanceof XViewerAttributeColumn)) return false;
      XViewerAttributeColumn xCol = (XViewerAttributeColumn) treeColumn.getData();
      final String attrName = xCol.getAttributeTypeName();
      if (attrName == null) {
         AWorkbench.popup("ERROR", "Can't retrieve attribute name from attribute column " + treeColumn.getText());
         return false;
      }
      if (attrName == null) return false;
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof ActionArtifact) return false;
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
            art = AtsLib.promptSelectTeamWorkflow((ActionArtifact) art);
            if (art == null) return;
         } else
            art = ((ActionArtifact) art).getTeamWorkFlowArtifacts().iterator().next();
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
      if (getSelectedArtifactItems().size() == 0) return null;
      obj = (getTree().getSelection()[0]).getData();
      return (obj != null && (obj instanceof StateMachineArtifact)) ? (StateMachineArtifact) obj : null;
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
      convertActionableItemsAction.setEnabled(getSelectedTeamWorkflowArtifacts().size() == 1);

   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_EDIT));
      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());

      // OPEN MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, new Separator());
      mm.insertBefore(MENU_GROUP_PRE, openInAtsWorkflowEditorAction);
      openInAtsWorkflowEditorAction.setEnabled(getSelectedArtifacts() != null);
      mm.insertBefore(MENU_GROUP_PRE, openInMassEditorAction);
      openInMassEditorAction.setEnabled(getSelectedArtifacts() != null);
      mm.insertBefore(MENU_GROUP_PRE, openInAtsWorldEditorAction);
      openInAtsWorldEditorAction.setEnabled(getSelectedArtifacts() != null);
      mm.insertBefore(MENU_GROUP_PRE, openInAtsTaskEditorAction);
      openInAtsTaskEditorAction.setEnabled(getSelectedTaskArtifacts() != null);
      if (AtsPlugin.isAtsAdmin()) {
         mm.insertBefore(MENU_GROUP_PRE, openInArtifactEditorAction);
         openInArtifactEditorAction.setEnabled(getSelectedArtifacts() != null);
         mm.insertBefore(MENU_GROUP_PRE, deletePurgeAtsObjectAction);
         deletePurgeAtsObjectAction.setEnabled(getSelectedArtifactItems().size() > 0);
      }
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OPEN));
      mm.insertBefore(MENU_GROUP_PRE, new Separator());

      // OTHER MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, favoritesAction);
      favoritesAction.setEnabled(enableFavoritesAction());
      if (getSelectedSMA() == null)
         favoritesAction.setText(ADD_AS_FAVORITE);
      else
         favoritesAction.setText(((IFavoriteableArtifact) getSelectedSMA()).amIFavorite() ? REMOVE_FAVORITE : ADD_AS_FAVORITE);

      mm.insertBefore(MENU_GROUP_PRE, subscribedAction);
      subscribedAction.setEnabled(enableSubscribedAction());
      if (getSelectedSMA() == null)
         subscribedAction.setText(SUBSCRIBE);
      else
         subscribedAction.setText(((ISubscribableArtifact) getSelectedSMA()).amISubscribed() ? UN_SUBSCRIBE : SUBSCRIBE);

      mm.insertBefore(MENU_GROUP_PRE, emailAction);
      emailAction.setEnabled(getSelectedArtifacts().size() == 1);
      emailAction.setText("Email " + ((getSelectedArtifacts().size() == 1) ? getSelectedArtifacts().iterator().next().getArtifactTypeName() : ""));

      mm.insertBefore(MENU_GROUP_PRE, resetActionArtifactAction);
      resetActionArtifactAction.setEnabled(getSelectedActionArtifacts().size() > 0);

      mm.insertAfter(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_WORLD_OTHER));
      mm.insertAfter(MENU_GROUP_PRE, new Separator());

   }

   private boolean enableFavoritesAction() {
      if (getSelectedSMAArtifacts().size() == 0) return false;
      for (StateMachineArtifact sma : getSelectedSMAArtifacts()) {
         if (!(sma instanceof IFavoriteableArtifact)) return false;
      }
      return true;
   }

   private boolean enableSubscribedAction() {
      if (getSelectedSMAArtifacts().size() == 0) return false;
      for (StateMachineArtifact sma : getSelectedSMAArtifacts()) {
         if (!(sma instanceof ISubscribableArtifact)) return false;
      }
      return true;
   }

   @Override
   public void handleDoubleClick() {
      if (getSelectedArtifactItems().size() == 0) return;
      Artifact art = getSelectedArtifactItems().iterator().next();
      AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
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

   public void clear() {
      ((WorldContentProvider) getContentProvider()).clear();
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
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) item.getData());
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
      if (items.length > 0) for (TreeItem item : items) {
         if (item.getData() instanceof ActionArtifact) {
            try {
               if (((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts().size() != 1) return false;
            } catch (OseeCoreException ex) {
               // Do Nothing
            }
         } else if (!(item.getData() instanceof TeamWorkFlowArtifact)) return false;
      }
      return true;
   }

   /**
    * @return all selected Workflow and any workflow that have Actions with single workflow
    */
   public Set<TeamWorkFlowArtifact> getSelectedTeamWorkflowArtifacts() {
      Set<TeamWorkFlowArtifact> teamArts = new HashSet<TeamWorkFlowArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items) {
         if (item.getData() instanceof TeamWorkFlowArtifact) teamArts.add((TeamWorkFlowArtifact) item.getData());
         if (item.getData() instanceof ActionArtifact) {
            try {
               if (((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts().size() == 1) teamArts.addAll(((ActionArtifact) item.getData()).getTeamWorkFlowArtifacts());
            } catch (OseeCoreException ex) {
               // Do Nothing
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
      if (items.length > 0) for (TreeItem item : items) {
         if (item.getData() instanceof ActionArtifact) actionArts.add((ActionArtifact) item.getData());
      }
      return actionArts;
   }

   public void setCancelledNotification() {
      TreeItem item = getTree().getItem(0);
      if (item.getData() instanceof String) item.setData(DefaultTeamState.Cancelled.name());
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

   public void set(Collection<? extends Artifact> artifacts) {
      ((WorldContentProvider) getContentProvider()).set(artifacts);
   }

   public void add(Collection<Artifact> artifacts) {
      ((WorldContentProvider) getContentProvider()).add(artifacts);
   }

   public void add(final Artifact artifact) {
      add(Arrays.asList(artifact));
   }

   public void remove(final Artifact artifact) {
      ((WorldContentProvider) getContentProvider()).remove(artifact);
   }

   @Override
   public void remove(final Collection<Object> artifacts) {
      ((WorldContentProvider) getContentProvider()).removeAll(artifacts);
   }

   @Override
   public void load(Collection<Object> objects) {
      Set<Artifact> arts = new HashSet<Artifact>();
      for (Object obj : objects) {
         if (obj instanceof IWorldViewArtifact) {
            arts.add((Artifact) obj);
         }
      }
      set(arts);
   }

   public ArrayList<Artifact> getSelectedArtifactItems() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) item.getData());
      return arts;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#getStatusString()
    */
   @Override
   public String getStatusString() {
      return extendedStatusString;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return handleAltLeftClick(treeColumn, treeItem, true);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClickInIconArea(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         Artifact useArt = (Artifact) treeItem.getData();
         if (useArt instanceof StateMachineArtifact) {
            boolean modified = false;
            if (useArt instanceof ActionArtifact) {
               if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1)
                  useArt = (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next());
               else
                  return false;
            }
            if (modified) {
               update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem, boolean persist) {
      try {
         super.handleAltLeftClick(treeColumn, treeItem);
         if (!(treeColumn.getData() instanceof XViewerColumn)) return false;
         Artifact useArt = (Artifact) treeItem.getData();
         if (useArt instanceof ActionArtifact) {
            if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1)
               useArt = (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next());
            else
               return false;
         }
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) useArt);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         boolean modified = false;
         if (xCol.equals(WorldXViewerFactory.Version_Target_Col))
            modified =
                  smaMgr.promptChangeVersion(
                        AtsPlugin.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, true);
         else if (xCol.equals(WorldXViewerFactory.Notes_Col))
            modified = smaMgr.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, persist, true);
         else if (xCol.equals(WorldXViewerFactory.Percent_Rework_Col))
            modified = smaMgr.promptChangePercentAttribute(ATSAttributes.PERCENT_REWORK_ATTRIBUTE, persist);
         else if (xCol.equals(WorldXViewerFactory.Estimated_Hours_Col))
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, persist);
         else if (xCol.equals(WorldXViewerFactory.Weekly_Benefit_Hrs_Col))
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE, persist);
         else if (xCol.equals(WorldXViewerFactory.Estimated_Release_Date_Col))
            modified = smaMgr.promptChangeEstimatedReleaseDate();
         else if (xCol.equals(WorldXViewerFactory.Estimated_Completion_Date_Col))
            modified = smaMgr.promptChangeDate(ATSAttributes.ESTIMATED_COMPLETION_DATE_ATTRIBUTE, persist);
         else if (xCol.equals(WorldXViewerFactory.Deadline_Col))
            modified = smaMgr.promptChangeDate(ATSAttributes.DEADLINE_ATTRIBUTE, persist);
         else if (xCol.equals(WorldXViewerFactory.Assignees_Col))
            modified = smaMgr.promptChangeAssignees(persist);
         else if (xCol.equals(WorldXViewerFactory.Remaining_Hours_Col)) {
            AWorkbench.popup("Calculated Field",
                  "Hours Remaining field is calculated.\nHour Estimate - (Hour Estimate * Percent Complete)");
            return false;
         } else if (xCol.equals(WorldXViewerFactory.Man_Days_Needed_Col)) {
            AWorkbench.popup(
                  "Calculated Field",
                  "Man Days Needed field is calculated.\nRemaining Hours / Hours per Week (" + smaMgr.getSma().getManHrsPerDayPreference() + ")");
            return false;
         } else if (xCol.equals(WorldXViewerFactory.Release_Date_Col))
            modified = smaMgr.promptChangeReleaseDate();
         else if (xCol.equals(WorldXViewerFactory.Work_Package_Col))
            modified = smaMgr.promptChangeAttribute(ATSAttributes.WORK_PACKAGE_ATTRIBUTE, persist, false);
         else if (xCol.equals(WorldXViewerFactory.Category_Col))
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY_ATTRIBUTE, persist, true);
         else if (xCol.equals(WorldXViewerFactory.Category2_Col))
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY2_ATTRIBUTE, persist, true);
         else if (xCol.equals(WorldXViewerFactory.Category3_Col))
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY3_ATTRIBUTE, persist, true);
         else if (xCol.equals(WorldXViewerFactory.Change_Type_Col))
            modified = smaMgr.promptChangeType(persist);
         else if (xCol.equals(WorldXViewerFactory.Priority_Col)) modified = smaMgr.promptChangePriority(persist);
         if (modified) {
            update(useArt, null);
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   /**
    * @return the extendedStatusString
    */
   public String getExtendedStatusString() {
      return extendedStatusString;
   }

   /**
    * @param extendedStatusString the extendedStatusString to set
    */
   public void setExtendedStatusString(String extendedStatusString) {
      this.extendedStatusString = extendedStatusString;
      updateStatusLabel();
   }

}
