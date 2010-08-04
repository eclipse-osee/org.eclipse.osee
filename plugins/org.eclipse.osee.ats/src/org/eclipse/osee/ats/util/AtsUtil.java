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

package org.eclipse.osee.ats.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowLabelProvider;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event2.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.IncrementingNum;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.OseeEditor;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public final class AtsUtil {

   private static boolean emailEnabled = true;
   public static Color ACTIVE_COLOR = new Color(null, 206, 212, 239);
   private static OseeGroup atsAdminGroup = null;
   private static final Date today = new Date();
   public static int MILLISECS_PER_DAY = 1000 * 60 * 60 * 24;
   public final static String normalColor = "#FFFFFF";
   public final static String activeColor = "#EEEEEE";
   private static ArtifactTypeEventFilter atsObjectArtifactTypesFilter, reviewArtifactTypesFilter,
      teamWorkflowArtifactTypesFilter, workItemArtifactTypesFilter;
   private static List<IEventFilter> atsObjectEventFilter;

   private AtsUtil() {
      super();
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static long daysTillToday(Date date) {
      return (date.getTime() - today.getTime()) / MILLISECS_PER_DAY;
   }

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isAtsAdmin() {
      try {
         return getAtsAdminGroup().isCurrentUserMember();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return false;
      }
   }

   public static OseeGroup getAtsAdminGroup() {
      if (atsAdminGroup == null) {
         atsAdminGroup = new OseeGroup("AtsAdmin");
      }
      return atsAdminGroup;
   }

   public static Branch getAtsBranch() throws OseeCoreException {
      return BranchManager.getCommonBranch();
   }

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }

   public static void setEmailEnabled(boolean enabled) {
      if (!DbUtil.isDbInit()) {
         System.out.println("Email " + (enabled ? "Enabled" : "Disabled"));
      }
      emailEnabled = enabled;
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

   /**
    * The development of ATS requires quite a few Actions to be created. To facilitate this, this method will retrieve a
    * persistent number from the file-system so each action has a different name. By entering "tt" in the title, new
    * action wizard will be pre-populated with selections and the action name will be created as "tt <number in
    * atsNumFilename>".
    * 
    * @return number
    * @throws IOException
    */
   public static int getAtsDeveloperIncrementingNum() {
      try {
         return IncrementingNum.get();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return 99;
   }

   public static ToolBar createCommonToolBar(Composite parent) {
      return createCommonToolBar(parent, null);
   }

   public static ToolBar createCommonToolBar(Composite parent, XFormToolkit toolkit) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);
      if (toolkit != null) {
         toolkit.adapt(toolBar.getParent());
      }
      if (toolkit != null) {
         toolkit.adapt(toolBar);
      }
      return toolBar;
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static void editActionableItems(ActionArtifact actionArt) throws OseeCoreException {
      Result result = actionArt.editActionableItems();
      if (result.isFalse() && result.getText().equals("")) {
         return;
      }
      if (result.isFalse()) {
         result.popup();
      }
   }

   public static void editActionableItems(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Result result = teamArt.editActionableItems();
      if (result.isFalse() && result.getText().equals("")) {
         return;
      }
      if (result.isFalse() && !result.getText().equals("")) {
         result.popup();
      }
   }

   public static void openArtifact(String guidOrHrid, Integer branchId, OseeEditor view) {
      try {
         Branch branch = BranchManager.getBranch(branchId);
         Artifact artifact = ArtifactQuery.getArtifactFromId(guidOrHrid, branch);
         openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Only to be used by browser. Use open (artifact) instead.
    * 
    * @param guid
    * @throws OseeCoreException
    */
   public static void openArtifact(String guid, OseeEditor view) {
      AtsBulkLoad.loadConfig(false);
      Artifact artifact = null;
      try {
         artifact = ArtifactQuery.getArtifactFromId(guid, getAtsBranch());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      try {
         if (view == OseeEditor.ActionEditor) {
            if (artifact instanceof StateMachineArtifact || artifact instanceof ActionArtifact) {
               openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
            }
         } else if (view == OseeEditor.ArtifactEditor) {
            RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
         } else if (view == OseeEditor.ArtifactHyperViewer) {
            AWorkbench.popup("ERROR", "Unimplemented");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void createATSAction(String initialDescription, String actionableItemName) {
      // Ensure actionable item is configured for ATS before continuing
      try {
         AtsCacheManager.getSoleArtifactByName(ArtifactTypeManager.getType(AtsArtifactTypes.ActionableItem),
            actionableItemName);
      } catch (ArtifactDoesNotExist ex) {
         AWorkbench.popup(
            "Configuration Error",
            "Actionable Item \"" + actionableItemName + "\" is not configured for ATS tracking.\n\nAction can not be created.");
         return;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      NewAction newAction = new NewAction(actionableItemName);
      newAction.setInitialDescription(initialDescription);
      newAction.run();
   }

   public static void openATSArtifact(Artifact art) {
      if (art instanceof IATSArtifact) {
         try {
            openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         }
      } else {
         AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
      }
   }

   public static void openATSAction(final Artifact art, final AtsOpenOption atsOpenOption) {
      try {
         if (art instanceof ActionArtifact) {
            final ActionArtifact actionArt = (ActionArtifact) art;
            Collection<TeamWorkFlowArtifact> teams = actionArt.getTeamWorkFlowArtifacts();
            if (atsOpenOption == AtsOpenOption.OpenAll) {
               for (TeamWorkFlowArtifact team : teams) {
                  SMAEditor.editArtifact(team);
               }
            } else if (atsOpenOption == AtsOpenOption.AtsWorld) {
               WorldEditor.open(new WorldEditorSimpleProvider("Action " + actionArt.getHumanReadableId(),
                  Arrays.asList(actionArt)));
            } else if (atsOpenOption == AtsOpenOption.OpenOneOrPopupSelect) {
               if (teams.size() == 1) {
                  SMAEditor.editArtifact(teams.iterator().next());
               } else {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           TeamWorkFlowArtifact teamArt = promptSelectTeamWorkflow(actionArt);
                           if (teamArt != null) {
                              SMAEditor.editArtifact((Artifact) teamArt);
                           } else {
                              return;
                           }
                        } catch (OseeCoreException ex) {
                           OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               }
            }
         } else {
            SMAEditor.editArtifact(art);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static TeamWorkFlowArtifact promptSelectTeamWorkflow(ActionArtifact actArt) throws OseeCoreException {
      ListDialog ld = new ListDialog(Displays.getActiveShell());
      ld.setContentProvider(new ArrayContentProvider());
      ld.setLabelProvider(new TeamWorkflowLabelProvider());
      ld.setTitle("Select Team Workflow");
      ld.setMessage("Select Team Workflow");
      ld.setInput(actArt.getTeamWorkFlowArtifacts());
      if (ld.open() == 0) {
         if (ld.getResult().length == 0) {
            AWorkbench.popup("Error", "No Workflow Selected");
         } else {
            return (TeamWorkFlowArtifact) ld.getResult()[0];
         }
      }
      return null;
   }

   public static void openInAtsWorldEditor(String name, Collection<Artifact> artifacts) {
      Set<Artifact> otherArts = new HashSet<Artifact>();
      for (Artifact art : artifacts) {
         if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
            WorldEditor.open(new WorldEditorUISearchItemProvider(new GroupWorldSearchItem(art), null,
               TableLoadOption.None));
         } else {
            otherArts.add(art);
         }
      }
      if (otherArts.size() > 0) {
         WorldEditor.open(new WorldEditorSimpleProvider(name, otherArts));
      }
   }

   public static void openInAtsTaskEditor(String name, Collection<Artifact> artifacts) throws OseeCoreException {
      TaskEditor.open(new TaskEditorSimpleProvider(name, artifacts));
   }

   public static ToolItem actionToToolItem(ToolBar toolBar, Action action, KeyedImage imageEnum) {
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

   public static MenuItem actionToMenuItem(Menu menu, final Action action, final int buttonType) {
      final Action fAction = action;
      MenuItem item = new MenuItem(menu, buttonType);
      item.setText(action.getText());
      if (action.getImageDescriptor() != null) {
         item.setImage(action.getImageDescriptor().createImage());
      }
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (buttonType == SWT.CHECK) {
               action.setChecked(!action.isChecked());
            }
            fAction.run();
         }
      });
      return item;
   }

   /**
    * TODO Remove duplicate Active flags, need to convert all ats.Active to Active in DB
    * 
    * @param <A>
    * @param artifacts to iterate through
    * @param active state to validate against; Both will return all artifacts matching type
    * @param clazz type of artifacts to consider; null for all
    * @return set of Artifacts of type clazz that match the given active state of the "Active" or "ats.Active" attribute
    * value. If no attribute exists, Active == true; If does exist then attribute value "yes" == true, "no" == false.
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> List<A> getActive(Collection<A> artifacts, Active active, Class<? extends Artifact> clazz) throws OseeCoreException {
      List<A> results = new ArrayList<A>();
      Collection<? extends Artifact> artsOfClass =
         clazz != null ? Collections.castMatching(clazz, artifacts) : artifacts;
      for (Artifact art : artsOfClass) {
         if (active == Active.Both) {
            results.add((A) art);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = ((A) art).getSoleAttributeValue(AtsAttributeTypes.ATS_ACTIVE, false);
            if (active == Active.Active && attributeActive) {
               results.add((A) art);
            } else if (active == Active.InActive && !attributeActive) {
               results.add((A) art);
            }
         }
      }
      return results;
   }

   public static List<IEventFilter> getAtsObjectEventFilters() {
      try {
         if (atsObjectEventFilter == null) {
            atsObjectEventFilter = new ArrayList<IEventFilter>(2);
            atsObjectEventFilter.add(OseeEventManager.getCommonBranchFilter());
            atsObjectEventFilter.add(getAtsObjectArtifactTypeEventFilter());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return atsObjectEventFilter;
   }

   public static ArtifactTypeEventFilter getAtsObjectArtifactTypeEventFilter() {
      if (atsObjectArtifactTypesFilter == null) {
         atsObjectArtifactTypesFilter =
            new ArtifactTypeEventFilter(AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.Action, AtsArtifactTypes.Task,
               AtsArtifactTypes.Goal, AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview);
      }
      return atsObjectArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getTeamWorkflowArtifactTypeEventFilter() {
      if (teamWorkflowArtifactTypesFilter == null) {
         teamWorkflowArtifactTypesFilter = new ArtifactTypeEventFilter(AtsArtifactTypes.TeamWorkflow);
      }
      return teamWorkflowArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getReviewArtifactTypeEventFilter() {
      if (reviewArtifactTypesFilter == null) {
         reviewArtifactTypesFilter =
            new ArtifactTypeEventFilter(AtsArtifactTypes.PeerToPeerReview, AtsArtifactTypes.DecisionReview);
      }
      return reviewArtifactTypesFilter;
   }

   public static ArtifactTypeEventFilter getWorkItemArtifactTypeEventFilter() {
      if (workItemArtifactTypesFilter == null) {
         workItemArtifactTypesFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.WorkItemDefinition);
      }
      return workItemArtifactTypesFilter;
   }

   public static Set<Artifact> getAssigned(User user) throws OseeCoreException {
      return StateManager.getAssigned(user);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    * 
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(User user, Class<?> clazz) throws OseeCoreException {
      return StateManager.getAssigned(user, clazz);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    * 
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(String userId, Class<?> clazz) throws OseeCoreException {
      return StateManager.getAssigned(userId, clazz);
   }
}