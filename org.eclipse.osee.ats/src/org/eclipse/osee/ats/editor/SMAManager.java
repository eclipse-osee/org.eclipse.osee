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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.BranchManager;
import org.eclipse.osee.ats.util.DeadlineManager;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.NotifyUsersJob;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.TaskManager;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.ats.util.widgets.dialog.AtsPriorityDialog;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.util.widgets.dialog.TaskOptionStatusDialog;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.workflow.AtsWorkFlow;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeTypeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class SMAManager {

   private StateMachineArtifact sma;
   private Set<User> transitionAssignees = new HashSet<User>();
   private static String SEPERATOR = ";  ";
   private TaskManager taskMgr;
   private ReviewManager reviewMgr;
   private BranchManager branchMgr;
   private DeadlineManager deadlineMgr;
   private SMAEditor editor;
   private AtsStateItems stateItems;
   private boolean inTransition = false;

   public SMAManager(StateMachineArtifact sma, SMAEditor editor) {
      super();
      this.sma = sma;
      this.editor = editor;
      reviewMgr = new ReviewManager(this);
      taskMgr = new TaskManager(this);
      branchMgr = new BranchManager(this);
      deadlineMgr = new DeadlineManager(this);
      stateItems = new AtsStateItems();
   }

   public SMAManager(StateMachineArtifact sma) {
      this(sma, null);
   }

   public void closeEditors(boolean save) {
      SMAEditor.close(sma, save);
   }

   public boolean isFirstState(AtsWorkPage page) {
      if (page.isStartPage()) return true;
      if ((sma instanceof TeamWorkFlowArtifact) && page.isEndorsePage()) return true;
      if ((sma instanceof TaskArtifact) && page.getName().equals(TaskArtifact.INWORK_STATE)) return true;
      return false;
   }

   public Set<User> getPrivilegedUsers() throws SQLException {
      return sma.getPrivilegedUsers();
   }

   public String getEditorHeaderString() {
      if (sma instanceof TeamWorkFlowArtifact)
         return String.format("Current State: %s        Team: %s       Assignee(s): %s", getCurrentStateName(),
               ((TeamWorkFlowArtifact) sma).getTeamName(), getAssigneesStr());
      else
         return String.format("Current State: %s        Assignee(s): %s", getCurrentStateName(), getAssigneesStr());
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public void setState(SMAState state) throws IllegalStateException, SQLException {
      if (getCurrentStateName().equals(state.getName()))
         getCurrentStateDam().setState(state);
      else
         getStateDam().setState(state);
   }

   public AtsWorkPage getWorkPage() {
      try {
         return (AtsWorkPage) sma.getWorkFlow().getPage(sma.getCurrentStateName());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return null;
   }

   public boolean isHistoricalVersion() {
      return !sma.getPersistenceMemo().getTransactionId().isHead();
   }

   public boolean isAccessControlWrite() {
      return AccessControlManager.getInstance().checkCurrentUserObjectPermission(sma, PermissionEnum.WRITE);
   }

   public AtsWorkPage getWorkPage(String name) {
      try {
         return (AtsWorkPage) sma.getWorkFlow().getPage(name);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return null;
   }

   public User getOriginator() {
      LogItem item = sma.getLog().getLastEvent(LogType.Originated);
      if (item == null) return null;
      return item.getUser();
   }

   public void setOriginator(User user) {
      sma.getLog().addLog(LogType.Originated, "",
            "Changed by " + SkynetAuthentication.getInstance().getAuthenticatedUser().getName(), user);
   }

   /**
    * @return true if this is a TeamWorkflow and it uses versions
    */
   public boolean isTeamUsesVersions() {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) return false;
      try {
         return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().isTeamUsesVersions();
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   public boolean isReleased() {
      try {
         VersionArtifact verArt = getTargetedForVersion();
         if (verArt != null) return verArt.isReleased();
      } catch (SQLException ex) {
         // Do Nothing
      }
      return false;
   }

   public VersionArtifact getTargetedForVersion() throws SQLException {
      return sma.getTargetedForVersion();
   }

   public SMAState getSMAState() {
      return sma.getCurrentStateDam().getState();
   }

   /**
    * Return current or past state from name
    * 
    * @param name
    * @param create TODO
    * @return state matching name
    * @throws SQLException
    */
   public SMAState getSMAState(String name, boolean create) {
      if (sma.getCurrentStateDam().getState().getName().equals(name))
         return sma.getCurrentStateDam().getState();
      else
         return (sma.getStateDam().getState(name, create));
   }

   public boolean promptChangeAssignees() {
      return promptChangeAssignees(Arrays.asList(new StateMachineArtifact[] {sma}));
   }

   public static boolean promptChangeAssignees(final Collection<? extends StateMachineArtifact> smas) {
      try {
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            if (smaMgr.isCompleted()) {
               AWorkbench.popup("ERROR",
                     "Can't assign completed " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
               return false;
            } else if (smaMgr.isCancelled()) {
               AWorkbench.popup("ERROR",
                     "Can't assign cancelled " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
               return false;
            }
         }
         UserCheckTreeDialog uld = new UserCheckTreeDialog(Display.getCurrent().getActiveShell());
         uld.setMessage("Select to assign.\nDeSelect to un-assign.");
         if (smas.size() == 1) {
            SMAManager smaMgr = new SMAManager(smas.iterator().next());
            uld.setInitialSelections(smaMgr.getAssignees());
         }
         if (uld.open() != 0) return false;
         Collection<User> users = uld.getUsersSelected();
         if (users.size() == 0) {
            AWorkbench.popup("ERROR", "Must have at least one assignee");
            return false;
         }
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            smaMgr.setAssignees(users);
         }
      } catch (IllegalStateException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return true;
   }

   public boolean promptChangeOriginator() {
      return promptChangeOriginator(Arrays.asList(new StateMachineArtifact[] {sma}));
   }

   public static boolean promptChangeOriginator(final Collection<? extends StateMachineArtifact> smas) {
      UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New Originator");
      int result = ld.open();
      if (result == 0) {
         User selectedUser = (User) ld.getSelection();
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            smaMgr.setOriginator(selectedUser);
         }
         return true;
      }
      return false;
   }

   public boolean promptChangeVersion(VersionReleaseType versionReleaseType, boolean persist) throws SQLException {
      return promptChangeVersion(Arrays.asList(new TeamWorkFlowArtifact[] {(TeamWorkFlowArtifact) sma}),
            versionReleaseType, persist);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> smas, VersionReleaseType versionReleaseType, final boolean persist) throws SQLException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : smas) {
         SMAManager smaMgr = new SMAManager(teamArt);
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR",
                  "Team \"" + teamArt.getTeamDefinition().getDescriptiveName() + "\" doesn't use versions.");
            return false;
         }
         if (smaMgr.isReleased()) {
            String error = "Team Workflow\n \"" + teamArt.getDescriptiveName() + "\"\n is already released.";
            if (AtsPlugin.isAtsAdmin() && !MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                  "Change Version", error + "\n\nOverride?")) {
               return false;
            } else if (!AtsPlugin.isAtsAdmin()) AWorkbench.popup("ERROR", error);
         }
         if (teamDefHoldingVersions != null) {
            if (teamDefHoldingVersions != teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions()) {
               AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
               return false;
            }
         }
         if (teamDefHoldingVersions == null) teamDefHoldingVersions =
               teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions();
      }
      if (teamDefHoldingVersions == null) {
         AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
         return false;
      }
      final VersionListDialog vld =
            new VersionListDialog("Select Version", "Select Version",
                  teamDefHoldingVersions.getVersionsArtifacts(versionReleaseType));
      if (smas.size() == 1 && smas.iterator().next().getTargetedForVersion() != null) {
         Object[] objs = new Object[1];
         objs[0] = smas.iterator().next().getTargetedForVersion();
         vld.setInitialSelections(objs);
      }
      int result = vld.open();
      if (result != 0) {
         return false;
      }
      try {
         if (persist) {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {
                        promptChangeVersionHelper(smas, vld, persist);
                     }
                  };
            txWrapper.execute();
         } else {
            promptChangeVersionHelper(smas, vld, persist);
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Prompt Change Version Version Exception", ex, false);
      }
      return true;
   }

   private static void promptChangeVersionHelper(Collection<? extends TeamWorkFlowArtifact> smas, VersionListDialog vld, boolean persist) throws SQLException {
      Object obj = vld.getResult()[0];
      VersionArtifact newVersion = (VersionArtifact) obj;

      for (TeamWorkFlowArtifact teamArt : smas) {
         teamArt.setTargetedForVersion(newVersion, persist);
      }
   }

   public boolean promptChangeType(boolean persist) {
      if (sma instanceof TeamWorkFlowArtifact) return promptChangeType(
            Arrays.asList(new TeamWorkFlowArtifact[] {(TeamWorkFlowArtifact) sma}), persist);
      return false;
   }

   public static boolean promptChangeType(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      for (TeamWorkFlowArtifact team : teams) {
         SMAManager smaMgr = new SMAManager(team);
         if (smaMgr.isReleased()) {
            AWorkbench.popup("ERROR", "Team Workflow\n \"" + team.getDescriptiveName() + "\"\n is already released.");
            return false;
         }
      }
      final ChangeTypeDialog ald = new ChangeTypeDialog(Display.getCurrent().getActiveShell());
      if (teams.size() == 1) {
         ald.setSelected(teams.iterator().next().getChangeType());
      }
      if (ald.open() == 0) {

         try {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {

                        for (TeamWorkFlowArtifact team : teams) {
                           if (team.getChangeType() != ald.getSelection()) {
                              team.setChangeType(ald.getSelection());
                              team.saveSMA();
                           }
                        }
                     }
                  };
            txWrapper.execute();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, "Can't change priority", ex, true);
            return false;
         }
      }
      return true;
   }

   public boolean promptChangePriority(boolean persist) {
      if (sma instanceof TeamWorkFlowArtifact) return promptChangePriority(
            Arrays.asList(new TeamWorkFlowArtifact[] {(TeamWorkFlowArtifact) sma}), persist);
      return false;
   }

   public static boolean promptChangePriority(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      for (TeamWorkFlowArtifact team : teams) {
         SMAManager smaMgr = new SMAManager(team);
         if (smaMgr.isReleased()) {
            AWorkbench.popup("ERROR", "Team Workflow\n \"" + team.getDescriptiveName() + "\"\n is already released.");
            return false;
         }
      }
      final AtsPriorityDialog ald = new AtsPriorityDialog(Display.getCurrent().getActiveShell());
      if (teams.size() == 1) {
         ald.setSelected(teams.iterator().next().getPriority());
      }
      if (ald.open() == 0) {
         try {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {
                        for (TeamWorkFlowArtifact team : teams) {
                           if (team.getPriority() != ald.getSelection()) {
                              team.setPriority(ald.getSelection());
                              team.saveSMA();
                           }
                        }
                     }
                  };
            txWrapper.execute();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, "Can't change priority", ex, true);
            return false;
         }
      }
      return true;
   }

   public boolean promptChangeStatus(boolean persist) {
      return promptChangeStatus(null, persist);
   }

   public boolean promptChangeStatus(List<TaskResOptionDefinition> options, boolean persist) {
      return promptChangeStatus(options, Arrays.asList(new StateMachineArtifact[] {sma}), persist);
   }

   public static boolean promptChangeStatus(List<TaskResOptionDefinition> options, final Collection<? extends StateMachineArtifact> smas, boolean persist) {
      try {
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            if (smaMgr.isReleased()) {
               AWorkbench.popup("ERROR",
                     sma.getArtifactTypeName() + " \"" + sma.getDescriptiveName() + "\"\n is already released.");
               return false;
            }
         }
         if (options != null) {
            TaskOptionStatusDialog tsd =
                  new TaskOptionStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Enter State Status",
                        "Select resolution, enter percent complete and number of hours you spent since last status.",
                        true, options, smas);
            int result = tsd.open();
            if (result == 0) {
               double hours = tsd.getHours().getFloat();
               if (tsd.isSplitHours()) {
                  hours = hours / smas.size();
               }
               for (StateMachineArtifact sma : smas) {
                  sma.getCurrentStateDam().setHoursSpent(hours + sma.getCurrentStateDam().getState().getHoursSpent());
                  sma.getCurrentStateDam().setPercentComplete(tsd.getPercent().getInt());
                  sma.setSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(),
                        tsd.getSelectedOptionDef().getName());
                  sma.statusChanged();
               }
               return true;
            }
         } else {
            SMAStatusDialog tsd =
                  new SMAStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Enter State Status",
                        "Enter percent complete and number of hours you spent since last status.", true, smas);
            int result = tsd.open();
            if (result == 0) {
               double hours = tsd.getHours().getFloat();
               if (tsd.isSplitHours()) {
                  hours = hours / smas.size();
               }
               for (StateMachineArtifact sma : smas) {
                  sma.getCurrentStateDam().setHoursSpent(hours + sma.getCurrentStateDam().getState().getHoursSpent());
                  sma.getCurrentStateDam().setPercentComplete(tsd.getPercent().getInt());
                  sma.statusChanged();
               }
               return true;
            }
         }
      } catch (IllegalStateException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      return false;
   }

   public boolean promptChangeFloatAttribute(ATSAttributes atsAttr, boolean persist) throws SQLException {
      return ArtifactPromptChange.promptChangeFloatAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
            Arrays.asList(new Artifact[] {sma}), persist);
   }

   public boolean promptChangeIntegerAttribute(ATSAttributes atsAttr, boolean persist) throws SQLException {
      return ArtifactPromptChange.promptChangeIntegerAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
            Arrays.asList(new Artifact[] {sma}), persist);
   }

   public boolean promptChangePercentAttribute(ATSAttributes atsAttr, boolean persist) throws SQLException {
      return ArtifactPromptChange.promptChangePercentAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
            Arrays.asList(new Artifact[] {sma}), persist);
   }

   public boolean promptChangeBoolean(ATSAttributes atsAttr, String toggleMessage, boolean persist) throws SQLException {
      return ArtifactPromptChange.promptChangeBoolean(atsAttr.getStoreName(), atsAttr.getDisplayName(),
            Arrays.asList(new Artifact[] {sma}), toggleMessage, persist);
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Collection<? extends StateMachineArtifact> smas, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               null, smas, persist);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Artifact sma, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {sma}), persist);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeAttribute(ATSAttributes atsAttr, final boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {sma}), persist);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeDate(ATSAttributes atsAttr, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeDate(atsAttr.getStoreName(), atsAttr.getDisplayName(), sma, persist);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class,
               "Can't save " + atsAttr.getDisplayName() + " date to artifact " + sma.getHumanReadableId(), ex, true);
      }
      return false;
   }

   public boolean promptChangeReleaseDate() {
      if (isReleased()) {
         AWorkbench.popup("ERROR", "Team Workflow\n \"" + sma.getDescriptiveName() + "\"\n is already released.");
         return false;
      }
      try {
         VersionArtifact verArt = getTargetedForVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted
            // for release xxx - want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog(
                        "Select Release Date Date",
                        "Warning: " + sma.getArtifactTypeName() + "'s release date is handled\n" + "by targeted for version \"" + verArt.getDescriptiveName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                        verArt.getReleaseDate());
            if (verArt.getReleaseDate() != null) diag.setSelectedDate(verArt.getReleaseDate());
            if (diag.open() == 0) {
               verArt.setSoleDateAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               verArt.persistAttributes();
               return true;
            }
         } else {
            // prompt that current release is (get from attribute) - want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog("Select Release Date", "Select Release Date", sma.getWorldViewReleaseDate());
            if (getSma().getWorldViewReleaseDate() != null) diag.setSelectedDate(sma.getWorldViewReleaseDate());
            if (diag.open() == 0) {
               sma.setSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate().getTime() + "");
               sma.persistAttributes();
               return true;
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't save release date " + sma.getHumanReadableId(), ex, true);
      }
      return false;
   }

   public boolean promptChangeEstimatedReleaseDate() {
      try {
         VersionArtifact verArt = getTargetedForVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted for release xxx -
            // want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog(
                        "Select Estimated Release Date Date",
                        "Warning: " + sma.getArtifactTypeName() + "'s estimated release date is handled\n" + "by targeted for version \"" + verArt.getDescriptiveName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                        verArt.getEstimatedReleaseDate());
            if (verArt.getEstimatedReleaseDate() != null) diag.setSelectedDate(verArt.getEstimatedReleaseDate());
            if (diag.open() == 0) {
               verArt.setSoleDateAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               verArt.persistAttributes();
               return true;
            }
         } else {
            // prompt that current est release is (get from attribute); want to
            // change
            DateSelectionDialog diag =
                  new DateSelectionDialog("Select Estimate Release Date", "Select Estimated Release Date",
                        sma.getWorldViewEstimatedReleaseDate());
            if (getSma().getWorldViewEstimatedReleaseDate() != null) diag.setSelectedDate(sma.getWorldViewEstimatedReleaseDate());
            if (diag.open() == 0) {
               sma.setSoleDateAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               sma.persistAttributes();
               return true;
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't save est release date " + sma.getHumanReadableId(), ex, true);
      }
      return false;
   }

   public AtsWorkFlow getWorkFlow() {
      return sma.getWorkFlow();
   }

   public boolean isStateVisited(String name) {
      return sma.getStateDam().getState(name, false) != null;
   }

   public XCurrentStateDam getCurrentStateDam() {
      return sma.getCurrentStateDam();
   }

   public XStateDam getStateDam() {
      return sma.getStateDam();
   }

   public boolean isCompleted() {
      return (getCurrentStateName().equals(DefaultTeamState.Completed.name()));
   }

   public boolean isCancelled() {
      return (getCurrentStateName().equals(DefaultTeamState.Cancelled.name()));
   }

   public boolean isCurrentSectionExpanded(AtsWorkPage page) {
      return sma.isCurrentSectionExpanded(page);
   }

   public boolean isCurrentState(WorkPage page) {
      return sma.getCurrentStateDam().getState(page.getName(), false) != null;
   }

   public void setTransitionAssignees(Collection<User> assignees) {
      transitionAssignees.clear();
      transitionAssignees.addAll(assignees);
   }

   public boolean isAssigneeMe() {
      return getAssignees().contains(SkynetAuthentication.getInstance().getAuthenticatedUser());
   }

   /**
    * Sets the assignees AND writes to SMA. Does not persist.
    * 
    * @param assignees
    * @throws SQLException
    * @throws IllegalStateException
    */
   public void setAssignees(Collection<User> assignees) throws IllegalStateException, SQLException {
      SMAState currState = getSMAState();
      currState.setAssignees(assignees);
      sma.getCurrentStateDam().setState(currState);
   }

   /**
    * Sets the assignee AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws SQLException
    * @throws IllegalStateException
    */
   public void setAssignee(User assignee) throws IllegalStateException, SQLException {
      SMAState currState = getSMAState();
      currState.setAssignee(assignee);
      sma.getCurrentStateDam().setState(currState);
   }

   public void clearAssignees() throws IllegalStateException, SQLException {
      SMAState currState = getSMAState();
      currState.clearAssignees();
      sma.getCurrentStateDam().setState(currState);
   }

   public Set<User> getTransitionAssignees() {
      if (transitionAssignees.size() == 0) transitionAssignees.addAll(getAssignees());
      return transitionAssignees;
   }

   public String getCurrentStateName() {
      try {
         return sma.getCurrentStateDam().getState().getName();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
      }
   }

   public String getTransitionAssigneesStr() {
      StringBuffer sb = new StringBuffer();
      for (User u : getTransitionAssignees()) {
         sb.append(u.getName() + SEPERATOR);
      }
      return sb.toString().replaceFirst(SEPERATOR + "$", "");
   }

   /**
    * @return Returns the assignees.
    * @throws SQLException
    */
   public Collection<User> getAssignees() {
      return getSMAState().getAssignees();
   }

   public String getAssigneesStr() {
      StringBuffer sb = new StringBuffer();
      for (User u : getAssignees()) {
         sb.append(u.getName() + SEPERATOR);
      }
      return sb.toString().replaceFirst(SEPERATOR + "$", "");
   }

   public String getAssigneesWasIsStr() {
      if (isCompleted() || isCancelled()) return "(" + Artifacts.commaArts(getStateDam().getState(
            TaskArtifact.INWORK_STATE, false).getAssignees()) + ")";
      return getAssigneesStr();
   }

   public Image getAssigneeImage() {
      return getSma().getAssigneeImage();
   }

   /**
    * @return Returns the sma.
    */
   public StateMachineArtifact getSma() {
      return sma;
   }

   /**
    * @return true if SMA is allowed to have tasks
    */
   public boolean isTaskable() {
      return sma.isTaskable();
   }

   public boolean showTaskTab() {
      return sma.showTaskTab();
   }

   /**
    * @return Returns the taskMgr.
    */
   public TaskManager getTaskMgr() {
      return taskMgr;
   }

   public Result transition(String toStateName, User toAssignee, boolean persist) {
      List<User> users = new ArrayList<User>();
      if (toAssignee != null && !toStateName.equals(DefaultTeamState.Completed.name()) && !toStateName.equals(DefaultTeamState.Cancelled.name())) users.add(toAssignee);
      return transition(toStateName, users, persist, false);
   }

   public Result transitionToCancelled(String reason, boolean persist) throws SQLException {
      Result result =
            transition(DefaultTeamState.Cancelled.name(), Arrays.asList(new User[] {}), persist, reason, false);
      if (result.isTrue()) {
         for (VersionArtifact verArt : sma.getArtifacts(RelationSide.TeamWorkflowTargetedForVersion_Version,
               VersionArtifact.class)) {
            sma.unrelate(RelationSide.TeamWorkflowTargetedForVersion_Version, verArt, true);
         }
      }
      return result;
   }

   public Result transition(String toStateName, Collection<User> toAssignees, boolean persist, boolean overrideTransitionCheck) {
      return transition(toStateName, toAssignees, persist, null, overrideTransitionCheck);
   }

   public Result transition(String toStateName, User toAssignee, boolean persist, boolean overrideTransitionCheck) {
      return transition(toStateName, Arrays.asList(new User[] {toAssignee}), persist, null, overrideTransitionCheck);
   }

   private Result transition(final String toStateName, final Collection<User> toAssignees, final boolean persist, final String cancelReason, boolean overrideTransitionCheck) {
      try {
         // Validate toState name
         final AtsWorkPage fromPage = getWorkPage();
         final AtsWorkPage toPage = getWorkPage(toStateName);
         if (toPage == null) return new Result("Invalid toState \"" + toStateName + "\"");

         // Validate transition from fromPage to toPage
         if (!overrideTransitionCheck && !fromPage.getToPages().contains(toPage)) {
            String errStr =
                  "According to transition configuration, can't transition to \"" + toStateName + "\" from \"" + fromPage.getName() + "\"";
            OSEELog.logSevere(AtsPlugin.class, errStr, false);
            return new Result(errStr);
         }

         // Check extension points for valid transition
         for (IAtsStateItem item : stateItems.getStateItems(fromPage.getId())) {
            Result result = item.transitioning(this, fromPage.getName(), toStateName, toAssignees);
            if (result.isFalse()) return result;
         }
         for (IAtsStateItem item : stateItems.getStateItems(toPage.getId())) {
            Result result = item.transitioning(this, fromPage.getName(), toStateName, toAssignees);
            if (result.isFalse()) return result;
         }

         if (persist) {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {

                     @Override
                     protected void handleTxWork() throws Exception {
                        transitionHelper(toAssignees, persist, fromPage, toPage, toStateName, cancelReason);
                     }

                  };
            txWrapper.execute();
         } else {
            transitionHelper(toAssignees, persist, fromPage, toPage, toStateName, cancelReason);
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return new Result("Transaction failed " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void transitionHelper(Collection<User> toAssignees, boolean persist, AtsWorkPage fromPage, AtsWorkPage toPage, String toStateName, String cancelReason) throws SQLException {
      // Log transition
      if (toPage.isCancelledPage()) {
         getSma().getLog().addLog(LogType.StateCancelled, getCurrentStateName(), cancelReason);
      } else {
         getSma().getLog().addLog(LogType.StateComplete, getCurrentStateName(), "");
      }
      getSma().getLog().addLog(LogType.StateEntered, toStateName, "");

      // Set XCurrentState info to XState
      getSma().getStateDam().setState(getSma().getCurrentStateDam().getState());

      // Set XCurrentState; If been to this state, copy state info from
      // prev state; else create
      // new
      SMAState previousState = getSma().getStateDam().getState(toStateName, false);
      if (previousState != null) {
         if (toAssignees.size() > 0) previousState.setAssignees(toAssignees);
         getSma().getCurrentStateDam().setState(previousState);
      } else {
         getSma().getCurrentStateDam().setState(new SMAState(toStateName, toAssignees));
      }

      if (getSma().isValidationRequired()) {
         getReviewManager().createValidateReview(false);
      }

      // Notify Users; NOTE: Assignees are notified as part of
      // StateMachineArtifact.persist
      NotifyUsersJob job =
            new NotifyUsersJob(sma, NotifyUsersJob.NotifyType.Subscribers, NotifyUsersJob.NotifyType.Completed);
      job.setPriority(Job.SHORT);
      job.schedule();

      // Persist
      if (persist) {
         getSma().persistAttributes();
      }

      getSma().transitioned(fromPage, toPage, toAssignees, true);

      // Notify extension points of transition
      for (IAtsStateItem item : stateItems.getStateItems(fromPage.getId())) {
         item.transitioned(this, fromPage.getName(), toStateName, toAssignees);
      }
      for (IAtsStateItem item : stateItems.getStateItems(toPage.getId())) {
         item.transitioned(this, fromPage.getName(), toStateName, toAssignees);
      }
   }

   /**
    * @return Returns the editor.
    */
   public SMAEditor getEditor() {
      return editor;
   }

   /**
    * @param editor The editor to set.
    */
   public void setEditor(SMAEditor editor) {
      this.editor = editor;
   }

   /**
    * @return Returns the branchMgr.
    */
   public BranchManager getBranchMgr() {
      return branchMgr;
   }

   /**
    * @return the reviewManager
    */
   public ReviewManager getReviewManager() {
      return reviewMgr;
   }

   /**
    * @return the stateItems
    */
   public AtsStateItems getStateItems() {
      return stateItems;
   }

   /**
    * @return the inTransition
    */
   public boolean isInTransition() {
      return inTransition;
   }

   /**
    * @param inTransition the inTransition to set
    */
   public void setInTransition(boolean inTransition) {
      this.inTransition = inTransition;
   }

   /**
    * @return the deadlineMgr
    */
   public DeadlineManager getDeadlineMgr() {
      return deadlineMgr;
   }
}
