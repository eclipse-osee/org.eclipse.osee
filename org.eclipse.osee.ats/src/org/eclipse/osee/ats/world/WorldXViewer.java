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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.IFavoriteableArtifact;
import org.eclipse.osee.ats.artifact.ISubscribableArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.ArtifactEmailWizard;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.Favorites;
import org.eclipse.osee.ats.util.Subscribe;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewer extends XViewer {

   private static String NAMESPACE = "org.eclipse.osee.ats.WorldXViewer";
   private String title;
   private String extendedStatusString = "";
   public static final String MENU_GROUP_ATS_WORLD_EDIT = "ATS WORLD EDIT";
   public static final String MENU_GROUP_ATS_WORLD_OPEN = "ATS WORLD OPEN";
   public static final String MENU_GROUP_ATS_WORLD_OTHER = "ATS WORLD OTHER";
   public static final String ADD_AS_FAVORITE = "Add as Favorite";
   public static final String REMOVE_FAVORITE = "Remove Favorite";
   public static final String SUBSCRIBE = "Subscribe for Notifications";
   public static final String UN_SUBSCRIBE = "Un-Subscribe for Notifications";

   /**
    * @param parent
    * @param style
    */
   public WorldXViewer(Composite parent, int style) {
      this(parent, style, NAMESPACE, new WorldXViewerFactory());
   }

   public WorldXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory) {
      super(parent, style, nameSpace, xViewerFactory);
      this.addDoubleClickListener(new IDoubleClickListener() {
         public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
            handleDoubleClick();
         };
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
   }

   Action editChangeTypeAction;
   Action editPriorityAction;
   Action editTargetVersionAction;
   Action editAssigneeAction;
   Action editActionableItemsAction;
   Action convertActionableItemsAction;
   Action openInAtsEditorAction, openInMassEditorAction;
   Action favoritesAction;
   Action subscribedAction;
   Action openInArtifactEditorAction;
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

      editChangeTypeAction = new Action("Edit Change Type", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangeType(getSelectedTeamWorkflowArtifacts(), true)) {
               update(getSelectedArtifactItemsArray(), null);
            }
         }
      };

      editPriorityAction = new Action("Edit Priority", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangePriority(getSelectedTeamWorkflowArtifacts(), true)) {
               update(getSelectedArtifactItemsArray(), null);
            }
         }
      };

      editTargetVersionAction = new Action("Edit Targeted Version", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAManager.promptChangeVersion(getSelectedTeamWorkflowArtifacts(),
                     (AtsPlugin.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased), true)) {
                  update(getSelectedArtifactItemsArray(), null);
               }
            } catch (SQLException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };

      editAssigneeAction = new Action("Edit Assignee", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            Set<StateMachineArtifact> artifacts = getSelectedSMAArtifacts();
            if (SMAManager.promptChangeAssignees(artifacts)) {
               try {
                  Artifacts.persist(artifacts, false);
               } catch (Exception ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
                  return;
               }
               update(getSelectedArtifactItemsArray(), null);
            }
         }
      };

      editActionableItemsAction = new Action("Edit Actionable Item(s)", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (getSelectedActionArtifacts().size() == 1) {
                  ActionArtifact actionArt = getSelectedActionArtifacts().iterator().next();
                  AtsLib.editActionActionableItems(actionArt);
                  refresh(getSelectedArtifactItemsArray()[0]);
               } else {
                  TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
                  AtsLib.editTeamActionableItems(teamArt);
                  refresh(getSelectedArtifactItemsArray()[0]);
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };

      convertActionableItemsAction = new Action("Convert to Actionable Item/Team", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               TeamWorkFlowArtifact teamArt = getSelectedTeamWorkflowArtifacts().iterator().next();
               Result result = teamArt.convertActionableItems();
               if (result.isFalse() && !result.getText().equals("")) result.popup(result.isTrue());
               refresh(getSelectedArtifactItemsArray()[0]);
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };

      openInAtsEditorAction = new Action("Open in ATS Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AtsLib.openAtsAction(getSelectedArtifactItems().iterator().next().getArtifact(),
                  AtsOpenOption.OpenOneOrPopupSelect);
         }
      };

      openInMassEditorAction = new Action("Mass Edit", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("Error", "No items selected");
               return;
            }
            MassArtifactEditor.editArtifacts("", getSelectedArtifacts());
         }
      };

      favoritesAction = new Action("Add as Favorite", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedSMA() != null) (new Favorites(getSelectedSMA())).toggleFavorite();
         }
      };

      subscribedAction = new Action("Subscribe for Notifications", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedSMA() != null) (new Subscribe(getSelectedSMA())).toggleSubscribe();
         }
      };

      openInArtifactEditorAction = new Action("Open in Artifact Editor", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (getSelectedArtifacts().size() > 0)
               ArtifactEditor.editArtifact(getSelectedArtifactItems().iterator().next().getArtifact());
            else
               OSEELog.logException(AtsPlugin.class, new Exception("Can't retrieve SMA"), true);
         }
      };

      deletePurgeAtsObjectAction = new Action("Delete/Purge ATS Object", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            handleDeleteAtsObject();
         }
      };

      emailAction = new Action("Email ATS Object", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            handleEmailSelectedAtsObject();
         }
      };

      resetActionArtifactAction = new Action("Reset Action off Children", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            for (ActionArtifact actionArt : getSelectedActionArtifacts()) {
               try {
                  actionArt.resetAttributesOffChildren();
               } catch (SQLException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         }
      };
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      handleColumnMultiEdit(treeColumn, treeItems, true);
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems, final boolean persist) {
      final AtsXColumn aCol = AtsXColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      XResultData rData = new XResultData(AtsPlugin.getLogger());
      final String attrName = getAttributeNameFromColumn(aCol);
      if (attrName == null) {
         AWorkbench.popup("ERROR", "Un-handled column " + treeColumn.getText());
         return;
      }
      final Set<Artifact> useArts = new HashSet<Artifact>();
      for (TreeItem item : treeItems) {
         Artifact art = ((WorldArtifactItem) item.getData()).getArtifact();
         if (art.isAttributeTypeValid(attrName))
            useArts.add(art);
         else
            rData.logError(attrName + " not valid for artifact " + art.getHumanReadableId() + " - " + art.getDescriptiveName());
      }
      if (!rData.isEmpty()) {
         rData.report("Column Multi Edit Errors");
         return;
      }
      try {
         if (useArts.size() > 0) {
            if (persist) {
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {

                        @Override
                        protected void handleTxWork() throws Exception {
                           ArtifactPromptChange.promptChangeAttribute(attrName, aCol.getName(), useArts, persist);
                        }
                     };
               txWrapper.execute();
            } else {
               ArtifactPromptChange.promptChangeAttribute(attrName, aCol.getName(), useArts, persist);
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   private String getAttributeNameFromColumn(AtsXColumn aCol) {
      if (aCol == AtsXColumn.Notes_Col)
         return ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Category_Col)
         return ATSAttributes.CATEGORY_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Category2_Col)
         return ATSAttributes.CATEGORY2_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Category3_Col)
         return ATSAttributes.CATEGORY3_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Related_To_State_Col)
         return ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Work_Package_Col)
         return ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Deadline_Col)
         return ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Estimated_Hours_Col)
         return ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName();
      else if (aCol == AtsXColumn.Description_Col) return ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName();
      return null;
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      AtsXColumn aCol = AtsXColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      if (aCol == null) return false;
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (xCol == null || !xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      String attrName = getAttributeNameFromColumn(aCol);
      if (attrName == null) return false;
      for (TreeItem item : treeItems) {
         Artifact art = ((WorldArtifactItem) item.getData()).getArtifact();
         if (art instanceof ActionArtifact) return false;
         if (!art.isAttributeTypeValid(attrName)) return false;
      }
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void handleEmailSelectedAtsObject() {
      try {
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
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public StateMachineArtifact getSelectedSMA() {
      Object obj = null;
      if (getSelectedArtifactItems().size() == 0) return null;
      obj = ((TreeItem) getTree().getSelection()[0]).getData();
      return (obj != null && (obj instanceof WorldArtifactItem)) ? ((WorldArtifactItem) obj).getSMA() : null;
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
      mm.insertBefore(MENU_GROUP_PRE, openInAtsEditorAction);
      openInAtsEditorAction.setEnabled(getSelectedArtifacts() != null);
      mm.insertBefore(MENU_GROUP_PRE, openInMassEditorAction);
      openInMassEditorAction.setEnabled(getSelectedArtifacts() != null);
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
      favoritesAction.setEnabled(getSelectedSMAArtifacts().size() == 1 && (getSelectedSMA() instanceof IFavoriteableArtifact));
      if (getSelectedSMA() == null)
         favoritesAction.setText(ADD_AS_FAVORITE);
      else
         favoritesAction.setText(((IFavoriteableArtifact) getSelectedSMA()).amIFavorite() ? REMOVE_FAVORITE : ADD_AS_FAVORITE);

      mm.insertBefore(MENU_GROUP_PRE, subscribedAction);
      subscribedAction.setEnabled(getSelectedSMAArtifacts().size() == 1 && (getSelectedSMA() instanceof ISubscribableArtifact));
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

   public void handleDoubleClick() {
      if (getSelectedArtifactItems().size() == 0) return;
      Artifact art = getSelectedArtifactItems().iterator().next().getArtifact();
      AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      for (WorldArtifactItem wai : ((WorldContentProvider) getContentProvider()).rootSet) {
         arts.add(wai.getArtifact());
      }
      return arts;
   }

   public void clear() {
      ((WorldContentProvider) getContentProvider()).clear();
   }

   /**
    * Release resources
    */
   public void dispose() {
      super.dispose();
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its ressources
      getLabelProvider().dispose();
   }

   public ArrayList<Artifact> getSelectedArtifacts() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Artifact) ((WorldArtifactItem) item.getData()).getArtifact());
      return arts;
   }

   /**
    * @return true if all selected are Workflow OR are Actions with single workflow
    */
   public boolean isSelectedTeamWorkflowArtifacts() {
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items) {
         Artifact art = (((WorldArtifactItem) item.getData()).getArtifact());
         if (art instanceof ActionArtifact) {
            try {
               if (((ActionArtifact) art).getTeamWorkFlowArtifacts().size() != 1) return false;
            } catch (SQLException ex) {
               // Do Nothing
            }
         } else if (!(art instanceof TeamWorkFlowArtifact)) return false;
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
         Artifact art = ((WorldArtifactItem) item.getData()).getArtifact();
         if (art instanceof TeamWorkFlowArtifact) teamArts.add((TeamWorkFlowArtifact) art);
         if (art instanceof ActionArtifact) {
            try {
               if (((ActionArtifact) art).getTeamWorkFlowArtifacts().size() == 1) teamArts.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
            } catch (SQLException ex) {
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
            if (obj instanceof WorldArtifactItem) {
               Artifact art = ((WorldArtifactItem) obj).getArtifact();
               if (art instanceof StateMachineArtifact)
                  smaArts.add((StateMachineArtifact) art);
               else if (art instanceof ActionArtifact) smaArts.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return smaArts;
   }

   public Set<ActionArtifact> getSelectedActionArtifacts() {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items) {
         Artifact art = ((WorldArtifactItem) item.getData()).getArtifact();
         if (art instanceof ActionArtifact) actionArts.add((ActionArtifact) art);
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
      Set<WorldArtifactItem> items = new HashSet<WorldArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new WorldArtifactItem(this, art, null));
      ((WorldContentProvider) getContentProvider()).set(items);
   }

   public void add(Collection<Artifact> artifacts) {
      Set<WorldArtifactItem> items = new HashSet<WorldArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new WorldArtifactItem(this, art, null));
      ((WorldContentProvider) getContentProvider()).add(items);
   }

   public void add(final Artifact artifact) {
      add(Arrays.asList(new Artifact[] {artifact}));
   }

   public void remove(final Artifact artifact) {
      ((WorldContentProvider) getContentProvider()).remove(artifact);
   }

   public void remove(final Collection<Artifact> artifacts) {
      ((WorldContentProvider) getContentProvider()).remove(artifacts);
   }

   public void removeItems(final Collection<? extends WorldArtifactItem> items) {
      ((WorldContentProvider) getContentProvider()).removeItems(items);
   }

   public ArrayList<WorldArtifactItem> getSelectedArtifactItems() {
      ArrayList<WorldArtifactItem> arts = new ArrayList<WorldArtifactItem>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((WorldArtifactItem) item.getData());
      return arts;
   }

   public Object[] getSelectedArtifactItemsArray() {
      return getSelectedArtifactItems().toArray(new WorldArtifactItem[getSelectedArtifactItems().size()]);
   }

   private void handleDeleteAtsObject() {
      try {
         ArrayList<Artifact> delArts = new ArrayList<Artifact>();
         StringBuilder artBuilder = new StringBuilder();
         ArrayList<WorldArtifactItem> items = getSelectedArtifactItems();
         ArrayList<Artifact> selectedArts = getSelectedArtifacts();

         for (Artifact art : selectedArts) {
            if (art instanceof ATSArtifact) {
               delArts.add(art);
               if (selectedArts.size() < 30) artBuilder.append(String.format("Name: %s  Type: %s\n",
                     art.getHumanReadableId(), art.getArtifactTypeName()));
            }
         }
         if (selectedArts.size() >= 5) {
            artBuilder.append(" < " + selectedArts.size() + " artifacts>");
         }
         MessageDialogWithToggle md =
               MessageDialogWithToggle.openOkCancelConfirm(
                     Display.getCurrent().getActiveShell(),
                     "Delete/Purge ATS Object",
                     "Prepare to Delete/Purge ATS Object\n\n" + artBuilder.toString().replaceFirst("\n$", "") + "\n\nAnd ALL it's ATS children.\n(Artifacts will be retrieved for confirmation)\nAre You Sure?",
                     "Purge", false, null, null);
         if (md.getReturnCode() == 0) {
            final boolean purge = md.getToggleState();
            StringBuilder delBuilder = new StringBuilder();
            final Set<Artifact> deleteArts = new HashSet<Artifact>(30);
            Map<Artifact, Object> ignoredArts = new HashMap<Artifact, Object>();
            for (Artifact art : delArts) {
               delBuilder.append("\nArtifact: " + art.getDescriptiveName());
               ((ATSArtifact) art).atsDelete(deleteArts, ignoredArts);
               delBuilder.append("\n\nDelete/Purge:\n");
               for (Artifact loopArt : deleteArts)
                  delBuilder.append(String.format("Guid %s Type: \"%s\" Name: \"%s\"", loopArt.getGuid(),
                        loopArt.getArtifactTypeName(), loopArt.getDescriptiveName()) + "\n");
               delBuilder.append("\n\nIngoring:\n");
               for (Artifact loopArt : ignoredArts.keySet())
                  if (!deleteArts.contains(loopArt)) delBuilder.append(String.format(
                        "Type: \"%s\" Name: \"%s\" <-rel to-> Class: %s", loopArt.getArtifactTypeName(),
                        loopArt.getDescriptiveName(), ignoredArts.get(loopArt).getClass().getCanonicalName()) + "\n");
            }
            String results = (purge ? "Purge" : "Delete") + " ATS Objects, Are You Sure?\n" + delBuilder.toString();
            results = results.replaceAll("\n", "<br>");
            HtmlDialog wd =
                  new HtmlDialog((purge ? "Purge" : "Delete") + " ATS Objects", "", AHTML.simplePage(results));
            wd.open();
            if (wd.getReturnCode() == 0) {
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {

                        @Override
                        protected void handleTxWork() throws Exception {
                           for (Artifact loopArt : deleteArts) {
                              if (purge)
                                 loopArt.purge();
                              else {
                                 loopArt.delete();
                              }
                           }
                        }
                     };
               txWrapper.execute();
               removeItems(items);
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
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
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         WorldArtifactItem wai = ((WorldArtifactItem) treeItem.getData());
         Artifact useArt = wai.getArtifact();
         if (useArt instanceof StateMachineArtifact) {
            SMAManager smaMgr = new SMAManager((StateMachineArtifact) useArt);
            boolean modified = false;
            if (useArt instanceof ActionArtifact) {
               if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1)
                  useArt = (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next());
               else
                  return false;
            } else if (aCol == AtsXColumn.Priority_Col) modified = smaMgr.promptChangePriority(true);
            if (aCol == AtsXColumn.Change_Type_Col) modified = smaMgr.promptChangeType(true);
            if (modified) {
               update(wai, null);
               return true;
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem, boolean persist) {
      try {
         super.handleAltLeftClick(treeColumn, treeItem);
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         WorldArtifactItem wai = ((WorldArtifactItem) treeItem.getData());
         Artifact useArt = wai.getArtifact();
         if (useArt instanceof ActionArtifact) {
            if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1)
               useArt = (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next());
            else
               return false;
         }
         SMAManager smaMgr = new SMAManager((StateMachineArtifact) useArt);
         boolean modified = false;
         if (aCol == AtsXColumn.Version_Target_Col)
            modified =
                  smaMgr.promptChangeVersion(
                        AtsPlugin.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, true);
         else if (aCol == AtsXColumn.Notes_Col)
            modified = smaMgr.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Percent_Rework_Col)
            modified = smaMgr.promptChangePercentAttribute(ATSAttributes.PERCENT_REWORK_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Estimated_Hours_Col) {
            if (smaMgr.getSma().isMetricsFromTasks()) {
               AWorkbench.popup(
                     "ERROR",
                     "Estimated Hours for this " + smaMgr.getSma().getArtifactTypeName() + " are" + " determined from Implement Tasks.\n\nSet Estimated Hours through Tasks.");
               return false;
            }
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, persist);
         } else if (aCol == AtsXColumn.Weekly_Benefit_Hrs_Col)
            modified = smaMgr.promptChangeFloatAttribute(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Estimated_Release_Date_Col)
            modified = smaMgr.promptChangeEstimatedReleaseDate();
         else if (aCol == AtsXColumn.Deadline_Col)
            modified = smaMgr.promptChangeDate(ATSAttributes.DEADLINE_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Remaining_Hours_Col) {
            AWorkbench.popup("Calculated Field",
                  "Hours Remaining field is calculated.\nHour Estimate - (Hour Estimate * Percent Complete)");
            return false;
         } else if (aCol == AtsXColumn.Man_Days_Needed_Col) {
            AWorkbench.popup(
                  "Calculated Field",
                  "Man Days Needed field is calculated.\nRemaining Hours / Hours per Week (" + smaMgr.getSma().getManDayHrsPreference() + ")");
            return false;
         } else if (aCol == AtsXColumn.Release_Date_Col)
            modified = smaMgr.promptChangeReleaseDate();
         else if (aCol == AtsXColumn.Work_Package_Col)
            modified = smaMgr.promptChangeAttribute(ATSAttributes.WORK_PACKAGE_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Category_Col)
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Category2_Col)
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY2_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Category3_Col)
            modified = smaMgr.promptChangeAttribute(ATSAttributes.CATEGORY3_ATTRIBUTE, persist);
         else if (aCol == AtsXColumn.Change_Type_Col)
            modified = smaMgr.promptChangeType(persist);
         else if (aCol == AtsXColumn.Priority_Col)
            modified = smaMgr.promptChangePriority(persist);
         else if (aCol == AtsXColumn.Metrics_from_Tasks_Col) {
            modified =
                  smaMgr.promptChangeBoolean(ATSAttributes.METRICS_FROM_TASKS_ATTRIBUTE,
                        AtsXColumn.Metrics_from_Tasks_Col.getDesc(), persist);
            if (modified) handleMetricsFromTasksToggle(smaMgr.getSma());
         }
         if (modified) {
            update(wai, null);
            return true;
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   /**
    * When metrics from tasks is chosen, confirm that implement's hours spent and percent complete change to 0. Prompt
    * the user as such.
    */
   public static void handleMetricsFromTasksToggle(StateMachineArtifact sma) {
      try {
         if (sma.isMetricsFromTasks()) {
            SMAManager smaMgr = new SMAManager(sma);
            SMAState implementState = smaMgr.getSMAState(DefaultTeamState.Implement.name(), false);
            if (implementState != null && (implementState.getPercentComplete() > 0 || implementState.getHoursSpent() > 0)) {
               StringBuffer sb = new StringBuffer();
               if (implementState.getPercentComplete() > 0) {
                  sb.append("Implement State Percent Complete was " + implementState.getPercentComplete() + "\n");
                  implementState.setPercentComplete(0);
               }
               if (implementState.getHoursSpent() > 0) {
                  sb.append("Implement State Hours Spent was " + implementState.getHoursSpentStr() + "\n");
                  implementState.setHoursSpent(0);
               }
               sb.append("\nSince metrics from tasks, values above have been cleared.");
               AWorkbench.popup("INFO", sb.toString());

               smaMgr.setState(implementState);
               smaMgr.getSma().persistAttributes();
            }
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
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
   }

}
