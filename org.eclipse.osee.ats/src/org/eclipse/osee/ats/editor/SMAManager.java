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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ATSLog;
import org.eclipse.osee.ats.artifact.ATSNote;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.DeadlineManager;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.TaskManager;
import org.eclipse.osee.ats.util.widgets.dialog.AtsPriorityDialog;
import org.eclipse.osee.ats.util.widgets.dialog.SMAStatusDialog;
import org.eclipse.osee.ats.util.widgets.dialog.TaskOptionStatusDialog;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeTypeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
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
   private AtsBranchManager branchMgr;
   private StateManager stateMgr;
   private DeadlineManager deadlineMgr;
   private SMAEditor editor;
   private ATSLog atsLog;
   private ATSNote atsNote;
   private AtsStateItems stateItems;
   private boolean inTransition = false;

   public SMAManager(StateMachineArtifact sma, SMAEditor editor) {
      super();
      this.sma = sma;
      this.editor = editor;
      stateMgr = new StateManager(this);
      reviewMgr = new ReviewManager(this);
      taskMgr = new TaskManager(this);
      branchMgr = new AtsBranchManager(this);
      deadlineMgr = new DeadlineManager(this);
      stateItems = new AtsStateItems();
      atsLog = new ATSLog(sma);
      atsNote = new ATSNote(sma);
   }

   public SMAManager(StateMachineArtifact sma) {
      this(sma, null);
   }

   public void closeEditors(boolean save) {
      SMAEditor.close(sma, save);
   }

   public Set<User> getPrivilegedUsers() throws SQLException {
      return sma.getPrivilegedUsers();
   }

   public ATSLog getLog() {
      return atsLog;
   }

   public ATSNote getNotes() {
      return atsNote;
   }

   public String getEditorHeaderString() {
      if (sma instanceof TeamWorkFlowArtifact)
         return String.format("Current State: %s        Team: %s        Created: %s", stateMgr.getCurrentStateName(),
               ((TeamWorkFlowArtifact) sma).getTeamName(), XDate.getDateStr(atsLog.getCreationDate(), XDate.MMDDYYHHMM));
      else
         return String.format("Current State: %s        Created: %s", stateMgr.getCurrentStateName(), XDate.getDateStr(
               atsLog.getCreationDate(), XDate.MMDDYYHHMM));
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public WorkPageDefinition getWorkPageDefinition() throws OseeCoreException, SQLException {
      if (getStateMgr().getCurrentStateName() == null) return null;
      return sma.getWorkFlowDefinition().getWorkPageDefinitionByName(getStateMgr().getCurrentStateName());
   }

   public WorkPageDefinition getWorkPageDefinitionByName(String name) throws OseeCoreException, SQLException {
      return sma.getWorkFlowDefinition().getWorkPageDefinitionByName(name);
   }

   public WorkPageDefinition getWorkPageDefinitionById(String id) throws OseeCoreException, SQLException {
      return sma.getWorkFlowDefinition().getWorkPageDefinitionById(id);
   }

   public boolean isHistoricalVersion() {
      return sma.isHistorical();
   }

   public List<WorkPageDefinition> getToWorkPages() throws OseeCoreException, SQLException {
      return getWorkFlowDefinition().getToPages(getWorkPageDefinition());
   }

   public List<WorkPageDefinition> getReturnPages() throws OseeCoreException, SQLException {
      return getWorkFlowDefinition().getReturnPages(getWorkPageDefinition());
   }

   public boolean isReturnPage(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return getWorkFlowDefinition().isReturnPage(getWorkPageDefinition(), workPageDefinition);
   }

   public boolean isAccessControlWrite() {
      return AccessControlManager.getInstance().checkCurrentUserObjectPermission(sma, PermissionEnum.WRITE);
   }

   public User getOriginator() {
      return atsLog.getOriginator();
   }

   public void setOriginator(User user) throws IllegalStateException, SQLException, MultipleAttributesExist {
      atsLog.addLog(LogType.Originated, "", "Changed by " + SkynetAuthentication.getUser().getName(), user);
   }

   /**
    * @return true if this is a TeamWorkflow and it uses versions
    */
   public boolean isTeamUsesVersions() {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) return false;
      try {
         return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().isTeamUsesVersions();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
   }

   /**
    * @return true if this is a TeamWorkflow requires a targeted version at all times
    */
   public boolean isRequireTargetedVersion() {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) return false;
      try {
         return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().isRequireTargetedVersion();
      } catch (Exception ex) {
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
      } catch (Exception ex) {
         // Do Nothing
      }
      return false;
   }

   public VersionArtifact getTargetedForVersion() throws SQLException {
      return sma.getTargetedForVersion();
   }

   public boolean promptChangeAssignees() throws OseeCoreException, SQLException {
      return promptChangeAssignees(Arrays.asList(sma));
   }

   public static boolean promptChangeAssignees(final Collection<? extends StateMachineArtifact> smas) throws OseeCoreException, SQLException {
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
         uld.setInitialSelections(smaMgr.getStateMgr().getAssignees());
      }
      if (uld.open() != 0) return false;
      Collection<User> users = uld.getUsersSelected();
      if (users.size() == 0) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return false;
      }
      for (StateMachineArtifact sma : smas) {
         sma.getSmaMgr().getStateMgr().setAssignees(users);
      }
      return true;
   }

   public boolean promptChangeOriginator() throws OseeCoreException, SQLException {
      return promptChangeOriginator(Arrays.asList(sma));
   }

   public static boolean promptChangeOriginator(final Collection<? extends StateMachineArtifact> smas) throws OseeCoreException, SQLException {
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

   public boolean promptChangeVersion(VersionReleaseType versionReleaseType, boolean persist) throws SQLException, OseeCoreException {
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) sma), versionReleaseType, persist);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> smas, VersionReleaseType versionReleaseType, final boolean persist) throws SQLException, OseeCoreException {
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
      if (persist) {
         AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
            @Override
            protected void handleTxWork() throws OseeCoreException, SQLException {
               promptChangeVersionHelper(smas, vld, persist);
            }
         };
         txWrapper.execute();
      } else {
         promptChangeVersionHelper(smas, vld, persist);
      }
      return true;
   }

   private static void promptChangeVersionHelper(Collection<? extends TeamWorkFlowArtifact> smas, VersionListDialog vld, boolean persist) throws SQLException, ArtifactDoesNotExist {
      Object obj = vld.getResult()[0];
      VersionArtifact newVersion = (VersionArtifact) obj;

      for (TeamWorkFlowArtifact teamArt : smas) {
         teamArt.setSoleRelation(AtsRelation.TeamWorkflowTargetedForVersion_Version, newVersion);
         if (persist) teamArt.persistRelations();
      }
   }

   public boolean promptChangeType(boolean persist) {
      if (sma instanceof TeamWorkFlowArtifact) return promptChangeType(Arrays.asList((TeamWorkFlowArtifact) sma),
            persist);
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
      final ChangeTypeDialog dialog = new ChangeTypeDialog(Display.getCurrent().getActiveShell());
      try {
         if (teams.size() == 1) {
            dialog.setSelected(teams.iterator().next().getChangeType());
         }
         if (dialog.open() == 0) {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {

                  for (TeamWorkFlowArtifact team : teams) {
                     if (team.getChangeType() != dialog.getSelection()) {
                        team.setChangeType(dialog.getSelection());
                        team.saveSMA();
                     }
                  }
               }
            };
            txWrapper.execute();
         }
         return true;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't change priority", ex, true);
         return false;
      }
   }

   public boolean promptChangePriority(boolean persist) {
      if (sma instanceof TeamWorkFlowArtifact) return promptChangePriority(Arrays.asList((TeamWorkFlowArtifact) sma),
            persist);
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
      try {
         if (teams.size() == 1) {
            ald.setSelected(teams.iterator().next().getPriority());
         }
         if (ald.open() == 0) {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  for (TeamWorkFlowArtifact team : teams) {
                     if (team.getPriority() != ald.getSelection()) {
                        team.setPriority(ald.getSelection());
                        team.saveSMA();
                     }
                  }
               }
            };
            txWrapper.execute();
         }
         return true;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't change priority", ex, true);
         return false;
      }
   }

   public boolean promptChangeStatus(boolean persist) throws OseeCoreException, SQLException {
      return promptChangeStatus(null, persist);
   }

   public boolean promptChangeStatus(List<TaskResOptionDefinition> options, boolean persist) throws OseeCoreException, SQLException {
      return promptChangeStatus(options, Arrays.asList(sma), persist);
   }

   public static boolean promptChangeStatus(List<TaskResOptionDefinition> options, final Collection<? extends StateMachineArtifact> smas, boolean persist) throws OseeCoreException, SQLException {
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
                  sma.getSmaMgr().getStateMgr().updateMetrics(hours, tsd.getPercent().getInt(), true);
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
                  sma.getSmaMgr().getStateMgr().updateMetrics(hours, tsd.getPercent().getInt(), true);
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
      try {
         return ArtifactPromptChange.promptChangeFloatAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(sma), persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeIntegerAttribute(ATSAttributes atsAttr, boolean persist) throws SQLException {
      try {
         return ArtifactPromptChange.promptChangeIntegerAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(sma), persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangePercentAttribute(ATSAttributes atsAttr, boolean persist) throws SQLException {
      try {
         return ArtifactPromptChange.promptChangePercentAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {sma}), persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeBoolean(ATSAttributes atsAttr, String toggleMessage, boolean persist) throws SQLException {
      try {
         return ArtifactPromptChange.promptChangeBoolean(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(sma), toggleMessage, persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Collection<? extends StateMachineArtifact> smas, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               null, smas, persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Artifact sma, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {sma}), persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeAttribute(ATSAttributes atsAttr, final boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(sma), persist);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return false;
   }

   public boolean promptChangeDate(ATSAttributes atsAttr, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeDate(atsAttr.getStoreName(), atsAttr.getDisplayName(), sma, persist);
      } catch (Exception ex) {
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
               verArt.setSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), diag.getSelectedDate());
               verArt.persistAttributes();
               return true;
            }
         } else {
            // prompt that current release is (get from attribute) - want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog("Select Release Date", "Select Release Date", sma.getWorldViewReleaseDate());
            if (getSma().getWorldViewReleaseDate() != null) diag.setSelectedDate(sma.getWorldViewReleaseDate());
            if (diag.open() == 0) {
               sma.setSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), diag.getSelectedDate());
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
               verArt.setSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
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
               sma.setSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
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

   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException, SQLException {
      return sma.getWorkFlowDefinition();
   }

   public boolean isCompleted() {
      return (stateMgr.getCurrentStateName().equals(DefaultTeamState.Completed.name()));
   }

   public boolean isCancelled() {
      return (stateMgr.getCurrentStateName().equals(DefaultTeamState.Cancelled.name()));
   }

   public boolean isCancelledOrCompleted() {
      return isCompleted() || isCancelled();
   }

   public boolean isCurrentSectionExpanded(String stateName) {
      return sma.isCurrentSectionExpanded(stateName);
   }

   public boolean isCurrentState(String stateName) {
      return stateName.equals(stateMgr.getCurrentStateName());
   }

   public void setTransitionAssignees(Collection<User> assignees) {
      transitionAssignees.clear();
      transitionAssignees.addAll(assignees);
   }

   public boolean isAssigneeMe() {
      return stateMgr.getAssignees().contains(SkynetAuthentication.getUser());
   }

   public Set<User> getTransitionAssignees() {
      if (transitionAssignees.size() == 0) transitionAssignees.addAll(stateMgr.getAssignees());
      return transitionAssignees;
   }

   public String getTransitionAssigneesStr() {
      StringBuffer sb = new StringBuffer();
      for (User u : getTransitionAssignees()) {
         sb.append(u.getName() + SEPERATOR);
      }
      return sb.toString().replaceFirst(SEPERATOR + "$", "");
   }

   public String getAssigneesWasIsStr() {
      if (isCompleted() || isCancelled()) return "(" + Artifacts.commaArts(stateMgr.getAssignees(TaskStates.InWork.name())) + ")";
      return Artifacts.commaArts(stateMgr.getAssignees());
   }

   public Image getAssigneeImage() throws OseeCoreException, SQLException {
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
      return result;
   }

   public Result transition(String toStateName, Collection<User> toAssignees, boolean persist, boolean overrideTransitionCheck) {
      return transition(toStateName, toAssignees, persist, null, overrideTransitionCheck);
   }

   public Result transition(String toStateName, User toAssignee, boolean persist, boolean overrideTransitionCheck) {
      return transition(toStateName, Arrays.asList(toAssignee), persist, null, overrideTransitionCheck);
   }

   private Result transition(final String toStateName, final Collection<User> toAssignees, final boolean persist, final String cancelReason, boolean overrideTransitionCheck) {
      try {
         // Validate toState name
         final WorkPageDefinition fromWorkPageDefinition = getWorkPageDefinition();
         final WorkPageDefinition toWorkPageDefinition = getWorkPageDefinitionByName(toStateName);
         if (toWorkPageDefinition == null) return new Result("Invalid toState \"" + toStateName + "\"");

         // Validate transition from fromPage to toPage
         if (!overrideTransitionCheck && !getWorkFlowDefinition().getToPages(fromWorkPageDefinition).contains(
               toWorkPageDefinition)) {
            String errStr =
                  "Not configured to transition to \"" + toStateName + "\" from \"" + fromWorkPageDefinition.getPageName() + "\"";
            OSEELog.logSevere(AtsPlugin.class, errStr, false);
            return new Result(errStr);
         }

         // Don't transition with uncommitted branch if this is a commit state
         if (AtsWorkDefinitions.isAllowCommitBranch(getWorkPageDefinition()) && getBranchMgr().isWorkingBranch()) return new Result(
               "Working Branch exists.  Please commit or delete working branch before transition.");

         // Check extension points for valid transition
         for (IAtsStateItem item : stateItems.getStateItems(fromWorkPageDefinition.getId())) {
            Result result = item.transitioning(this, fromWorkPageDefinition.getPageName(), toStateName, toAssignees);
            if (result.isFalse()) return result;
         }
         for (IAtsStateItem item : stateItems.getStateItems(toWorkPageDefinition.getId())) {
            Result result = item.transitioning(this, fromWorkPageDefinition.getPageName(), toStateName, toAssignees);
            if (result.isFalse()) return result;
         }

         if (persist) {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  transitionHelper(toAssignees, persist, fromWorkPageDefinition, toWorkPageDefinition, toStateName,
                        cancelReason);
               }

            };
            txWrapper.execute();
            // This should only send subscribed notifications as saveSMA() done before 
            OseeNotificationManager.sendNotifications();
         } else {
            transitionHelper(toAssignees, persist, fromWorkPageDefinition, toWorkPageDefinition, toStateName,
                  cancelReason);
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return new Result("Transaction failed " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void transitionHelper(Collection<User> toAssignees, boolean persist, WorkPageDefinition fromPage, WorkPageDefinition toPage, String toStateName, String cancelReason) throws OseeCoreException, SQLException {
      // Log transition
      if (toPage.isCancelledPage()) {
         atsLog.addLog(LogType.StateCancelled, stateMgr.getCurrentStateName(), cancelReason);
      } else {
         atsLog.addLog(LogType.StateComplete, stateMgr.getCurrentStateName(), "");
      }
      atsLog.addLog(LogType.StateEntered, toStateName, "");

      stateMgr.transitionHelper(toAssignees, persist, fromPage, toPage, toStateName, cancelReason);

      if (getSma().isValidationRequired()) {
         getReviewManager().createValidateReview(false);
      }

      AtsNotifyUsers.notify(sma, AtsNotifyUsers.NotifyType.Subscribed, AtsNotifyUsers.NotifyType.Completed,
            AtsNotifyUsers.NotifyType.Completed);

      // Persist
      if (persist) {
         getSma().persistAttributesAndRelations();
      }

      getSma().transitioned(fromPage, toPage, toAssignees, true);

      // Notify extension points of transition
      for (IAtsStateItem item : stateItems.getStateItems(fromPage.getId())) {
         item.transitioned(this, fromPage.getPageName(), toStateName, toAssignees);
      }
      for (IAtsStateItem item : stateItems.getStateItems(toPage.getId())) {
         item.transitioned(this, fromPage.getPageName(), toStateName, toAssignees);
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
   public AtsBranchManager getBranchMgr() {
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

   /**
    * @return the stateMgr
    */
   public StateManager getStateMgr() {
      return stateMgr;
   }

}
