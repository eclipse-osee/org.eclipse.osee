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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItems;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DeadlineManager;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.TaskManager;
import org.eclipse.osee.ats.util.widgets.dialog.AtsPriorityDialog;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeTypeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class SMAManager {

   private final WeakReference<StateMachineArtifact> smaRef;
   private Collection<User> transitionAssignees;
   private static String SEPERATOR = ";  ";
   private final TaskManager taskMgr;
   private final ReviewManager reviewMgr;
   private final AtsBranchManager branchMgr;
   private final StateManager stateMgr;
   private final DeadlineManager deadlineMgr;
   private SMAEditor editor;
   private final ATSLog atsLog;
   private final ATSNote atsNote;
   private static final AtsStateItems stateItems = new AtsStateItems();
   private boolean inTransition = false;
   public static enum TransitionOption {
      None, Persist,
      // Override check whether workflow allows transition to state
      OverrideTransitionValidityCheck,
      // Allows transition to occur with UnAssigned, OseeSystem or Guest
      OverrideAssigneeCheck
   };

   public SMAManager(StateMachineArtifact sma, SMAEditor editor) throws OseeStateException {
      super();
      this.smaRef = new WeakReference<StateMachineArtifact>(sma);
      this.editor = editor;
      stateMgr = new StateManager(this);
      reviewMgr = new ReviewManager(this);
      taskMgr = new TaskManager(this);
      branchMgr = new AtsBranchManager(this);
      deadlineMgr = new DeadlineManager(this);
      atsLog = new ATSLog(sma);
      atsNote = new ATSNote(sma);
   }

   public StateMachineArtifact getSma() throws OseeStateException {
      if (smaRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return smaRef.get();
   }

   public SMAManager(StateMachineArtifact sma) throws OseeStateException {
      this(sma, null);
   }

   public void closeEditors(boolean save) throws OseeStateException {
      SMAEditor.close(java.util.Collections.singleton(getSma()), save);
   }

   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      return getSma().getPrivilegedUsers();
   }

   public ATSLog getLog() {
      return atsLog;
   }

   public ATSNote getNotes() {
      return atsNote;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public WorkPageDefinition getWorkPageDefinition() throws OseeCoreException {
      if (getStateMgr().getCurrentStateName() == null) {
         return null;
      }
      return getSma().getWorkFlowDefinition().getWorkPageDefinitionByName(getStateMgr().getCurrentStateName());
   }

   public WorkPageDefinition getWorkPageDefinitionByName(String name) throws OseeCoreException {
      return getSma().getWorkFlowDefinition().getWorkPageDefinitionByName(name);
   }

   public WorkPageDefinition getWorkPageDefinitionById(String id) throws OseeCoreException {
      return getSma().getWorkFlowDefinition().getWorkPageDefinitionById(id);
   }

   public boolean isHistoricalVersion() throws OseeStateException {
      return getSma().isHistorical();
   }

   public List<WorkPageDefinition> getToWorkPages() throws OseeCoreException {
      return getWorkFlowDefinition().getToPages(getWorkPageDefinition());
   }

   public List<WorkPageDefinition> getReturnPages() throws OseeCoreException {
      return getWorkFlowDefinition().getReturnPages(getWorkPageDefinition());
   }

   public boolean isReturnPage(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return getWorkFlowDefinition().isReturnPage(getWorkPageDefinition(), workPageDefinition);
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(getSma(), PermissionEnum.WRITE);
   }

   public User getOriginator() throws OseeCoreException {
      return atsLog.getOriginator();
   }

   public void setOriginator(User user) throws OseeCoreException {
      atsLog.addLog(LogType.Originated, "", "Changed by " + UserManager.getUser().getName(), user);
   }

   /**
    * @return true if this is a TeamWorkflow and it uses versions
    * @throws OseeStateException
    */
   public boolean isTeamUsesVersions() throws OseeStateException {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) {
         return false;
      }
      try {
         return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().isTeamUsesVersions();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return false;
      }
   }

   /**
    * Return true if sma is TeamWorkflowArtifact and it's TeamDefinitionArtifact has rule set
    * 
    * @param ruleId
    * @return if has rule
    * @throws OseeCoreException
    * @throws
    */
   public boolean teamDefHasWorkRule(String ruleId) throws OseeCoreException {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) {
         return false;
      }
      try {
         return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().hasWorkRule(ruleId);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   public boolean workPageHasWorkRule(String ruleId) throws OseeCoreException {
      return getWorkPageDefinition().hasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());
   }

   public Collection<WorkRuleDefinition> getWorkRulesStartsWith(String ruleId) throws OseeCoreException {
      Set<WorkRuleDefinition> workRules = new HashSet<WorkRuleDefinition>();
      if (ruleId == null || ruleId.equals("")) {
         return workRules;
      }
      if (getSma() instanceof TeamWorkFlowArtifact) {
         // Get rules from team definition
         workRules.addAll(((TeamWorkFlowArtifact) getSma()).getTeamDefinition().getWorkRulesStartsWith(ruleId));
      }
      // Get work rules from workflow
      WorkFlowDefinition workFlowDefinition = getWorkFlowDefinition();
      if (workFlowDefinition != null) {
         // Get rules from workflow definitions
         workRules.addAll(getWorkFlowDefinition().getWorkRulesStartsWith(ruleId));
      }
      // Add work rules from page
      for (WorkItemDefinition wid : getWorkPageDefinition().getWorkItems(false)) {
         if (!wid.getId().equals("") && wid.getId().startsWith(ruleId)) {
            workRules.add((WorkRuleDefinition) wid);
         }
      }
      return workRules;
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   public boolean isReleased() {
      try {
         VersionArtifact verArt = getTargetedForVersion();
         if (verArt != null) {
            return verArt.isReleased();
         }
      } catch (Exception ex) {
         // Do Nothing
      }
      return false;
   }

   public VersionArtifact getTargetedForVersion() throws OseeCoreException {
      return getSma().getWorldViewTargetedVersion();
   }

   public boolean promptChangeAssignees(boolean persist) throws OseeCoreException {
      return promptChangeAssignees(Arrays.asList(getSma()), persist);
   }

   public static boolean promptChangeAssignees(final Collection<? extends StateMachineArtifact> smas, boolean persist) throws OseeCoreException {
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
      UserCheckTreeDialog uld = new UserCheckTreeDialog();
      uld.setMessage("Select to assign.\nDeSelect to un-assign.");
      if (smas.size() == 1) {
         SMAManager smaMgr = new SMAManager(smas.iterator().next());
         uld.setInitialSelections(smaMgr.getStateMgr().getAssignees());
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<User> users = uld.getUsersSelected();
      if (users.size() == 0) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return false;
      }
      // As a convenience, remove the UnAssigned user if another user is selected
      if (users.size() > 1) {
         users.remove(UserManager.getUser(SystemUser.UnAssigned));
      }
      for (StateMachineArtifact sma : smas) {
         sma.getSmaMgr().getStateMgr().setAssignees(users);
      }
      if (persist) {
         Artifacts.persistInTransaction(smas);
      }
      return true;
   }

   public boolean promptChangeOriginator() throws OseeCoreException {
      return promptChangeOriginator(Arrays.asList(getSma()));
   }

   public static boolean promptChangeOriginator(final Collection<? extends StateMachineArtifact> smas) throws OseeCoreException {
      UserListDialog ld = new UserListDialog(Display.getCurrent().getActiveShell(), "Select New Originator");
      int result = ld.open();
      if (result == 0) {
         User selectedUser = ld.getSelection();
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            smaMgr.setOriginator(selectedUser);
         }
         return true;
      }
      return false;
   }

   public boolean promptChangeVersion(VersionReleaseType versionReleaseType, boolean persist) throws OseeCoreException {
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) getSma()), versionReleaseType, persist);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> smas, VersionReleaseType versionReleaseType, final boolean persist) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : smas) {
         SMAManager smaMgr = new SMAManager(teamArt);
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }
         if (smaMgr.isReleased()) {
            String error = "Team Workflow\n \"" + teamArt.getName() + "\"\n is already released.";
            if (AtsUtil.isAtsAdmin() && !MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                  "Change Version", error + "\n\nOverride?")) {
               return false;
            } else if (!AtsUtil.isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
            }
         }
         if (teamDefHoldingVersions != null) {
            if (teamDefHoldingVersions != teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions()) {
               AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
               return false;
            }
         }
         if (teamDefHoldingVersions == null) {
            teamDefHoldingVersions = teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions();
         }
      }
      if (teamDefHoldingVersions == null) {
         AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
         return false;
      }
      final VersionListDialog vld =
            new VersionListDialog("Select Version", "Select Version",
                  teamDefHoldingVersions.getVersionsArtifacts(versionReleaseType));
      if (smas.size() == 1 && smas.iterator().next().getWorldViewTargetedVersion() != null) {
         Object[] objs = new Object[1];
         objs[0] = smas.iterator().next().getWorldViewTargetedVersion();
         vld.setInitialSelections(objs);
      }
      int result = vld.open();
      if (result != 0) {
         return false;
      }
      Object obj = vld.getResult()[0];
      VersionArtifact newVersion = (VersionArtifact) obj;
      for (TeamWorkFlowArtifact teamArt : smas) {
         teamArt.setRelations(AtsRelation.TeamWorkflowTargetedForVersion_Version,
               java.util.Collections.singleton(newVersion));
      }
      if (persist) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
         for (TeamWorkFlowArtifact teamArt : smas) {
            teamArt.persist(transaction);
         }
         transaction.execute();
      }
      return true;
   }

   public boolean promptChangeType(boolean persist) throws OseeStateException {
      if (getSma() instanceof TeamWorkFlowArtifact) {
         return promptChangeType(Arrays.asList((TeamWorkFlowArtifact) getSma()), persist);
      }
      return false;
   }

   public static boolean promptChangeType(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) throws OseeStateException {

      for (TeamWorkFlowArtifact team : teams) {
         SMAManager smaMgr = new SMAManager(team);
         if (smaMgr.isReleased()) {
            AWorkbench.popup("ERROR", "Team Workflow\n \"" + team.getName() + "\"\n is already released.");
            return false;
         }
      }
      final ChangeTypeDialog dialog = new ChangeTypeDialog(Display.getCurrent().getActiveShell());
      try {
         if (teams.size() == 1) {
            dialog.setSelected(teams.iterator().next().getChangeType());
         }
         if (dialog.open() == 0) {

            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());

            for (TeamWorkFlowArtifact team : teams) {
               if (team.getChangeType() != dialog.getSelection()) {
                  team.setChangeType(dialog.getSelection());
                  team.saveSMA(transaction);
               }
            }
            transaction.execute();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   public boolean promptChangePriority(boolean persist) throws OseeStateException {
      if (getSma() instanceof TeamWorkFlowArtifact) {
         return promptChangePriority(Arrays.asList((TeamWorkFlowArtifact) getSma()), persist);
      }
      return false;
   }

   public static boolean promptChangePriority(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      for (TeamWorkFlowArtifact team : teams) {
         if (team.getSmaMgr().isReleased()) {
            AWorkbench.popup("ERROR", "Team Workflow\n \"" + team.getName() + "\"\n is already released.");
            return false;
         }
      }
      final AtsPriorityDialog ald = new AtsPriorityDialog(Display.getCurrent().getActiveShell());
      try {
         if (teams.size() == 1) {
            ald.setSelected(teams.iterator().next().getPriority());
         }
         if (ald.open() == 0) {

            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch());
            for (TeamWorkFlowArtifact team : teams) {
               if (team.getPriority() != ald.getSelection()) {
                  team.setPriority(ald.getSelection());
                  team.saveSMA(transaction);
               }
            }
            transaction.execute();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   public boolean promptChangeFloatAttribute(ATSAttributes atsAttr, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeFloatAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(getSma()), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean promptChangeIntegerAttribute(ATSAttributes atsAttr, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeIntegerAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(getSma()), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean promptChangePercentAttribute(ATSAttributes atsAttr, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangePercentAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {getSma()}), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean promptChangeBoolean(ATSAttributes atsAttr, String toggleMessage, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeBoolean(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(getSma()), toggleMessage, persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Collection<? extends StateMachineArtifact> smas, boolean persist, boolean multiLine) throws OseeCoreException {
      return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(), smas,
            persist, multiLine);
   }

   public static boolean promptChangeAttribute(ATSAttributes atsAttr, final Artifact sma, boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(new Artifact[] {sma}), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean promptChangeAttribute(ATSAttributes atsAttr, final boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeStringAttribute(atsAttr.getStoreName(), atsAttr.getDisplayName(),
               Arrays.asList(getSma()), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean promptChangeDate(ATSAttributes atsAttr, boolean persist) throws OseeStateException {
      try {
         return ArtifactPromptChange.promptChangeDate(atsAttr.getStoreName(), atsAttr.getDisplayName(), getSma(),
               persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
               "Can't save " + atsAttr.getDisplayName() + " date to artifact " + getSma().getHumanReadableId(), ex);
      }
      return false;
   }

   public boolean promptChangeReleaseDate() throws OseeStateException {
      if (isReleased()) {
         AWorkbench.popup("ERROR", "Team Workflow\n \"" + getSma().getName() + "\"\n is already released.");
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
                        "Warning: " + getSma().getArtifactTypeName() + "'s release date is handled\n" + "by targeted for version \"" + verArt.getName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                        verArt.getReleaseDate());
            if (verArt.getReleaseDate() != null) {
               diag.setSelectedDate(verArt.getReleaseDate());
            }
            if (diag.open() == 0) {
               verArt.setSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), diag.getSelectedDate());
               verArt.persist();
               return true;
            }
         } else {
            // prompt that current release is (get from attribute) - want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog("Select Release Date", "Select Release Date",
                        getSma().getWorldViewReleaseDate());
            if (getSma().getWorldViewReleaseDate() != null) {
               diag.setSelectedDate(getSma().getWorldViewReleaseDate());
            }
            if (diag.open() == 0) {
               getSma().setSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               getSma().persist();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
               "Can't save release date " + getSma().getHumanReadableId(), ex);
      }
      return false;
   }

   public boolean promptChangeEstimatedReleaseDate() throws OseeStateException {
      try {
         VersionArtifact verArt = getTargetedForVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted for release xxx -
            // want to change?
            DateSelectionDialog diag =
                  new DateSelectionDialog(
                        "Select Estimated Release Date Date",
                        "Warning: " + getSma().getArtifactTypeName() + "'s estimated release date is handled\n" + "by targeted for version \"" + verArt.getName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                        verArt.getEstimatedReleaseDate());
            if (verArt.getEstimatedReleaseDate() != null) {
               diag.setSelectedDate(verArt.getEstimatedReleaseDate());
            }
            if (diag.open() == 0) {
               verArt.setSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               verArt.persist();
               return true;
            }
         } else {
            // prompt that current est release is (get from attribute); want to
            // change
            DateSelectionDialog diag =
                  new DateSelectionDialog("Select Estimate Release Date", "Select Estimated Release Date",
                        getSma().getWorldViewEstimatedReleaseDate());
            if (getSma().getWorldViewEstimatedReleaseDate() != null) {
               diag.setSelectedDate(getSma().getWorldViewEstimatedReleaseDate());
            }
            if (diag.open() == 0) {
               getSma().setSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(),
                     diag.getSelectedDate());
               getSma().persist();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
               "Can't save est release date " + getSma().getHumanReadableId(), ex);
      }
      return false;
   }

   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException {
      return getSma().getWorkFlowDefinition();
   }

   public boolean isCompleted() throws OseeCoreException {
      return stateMgr.getCurrentStateName().equals(DefaultTeamState.Completed.name());
   }

   public boolean isCancelled() throws OseeCoreException {
      return stateMgr.getCurrentStateName().equals(DefaultTeamState.Cancelled.name());
   }

   public boolean isCancelledOrCompleted() throws OseeCoreException {
      return isCompleted() || isCancelled();
   }

   public boolean isCurrentSectionExpanded(String stateName) throws OseeCoreException {
      return getSma().isCurrentSectionExpanded(stateName);
   }

   public boolean isCurrentState(String stateName) throws OseeCoreException {
      return stateName.equals(stateMgr.getCurrentStateName());
   }

   public void setTransitionAssignees(Collection<User> assignees) throws OseeCoreException {
      if (assignees.contains(UserManager.getUser(SystemUser.OseeSystem)) || assignees.contains(UserManager.getUser(SystemUser.Guest))) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignees.size() > 1 && assignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
         throw new OseeArgumentException("Can not assign to user and UnAssigned");
      }
      transitionAssignees = assignees;
   }

   public boolean isAssigneeMe() throws OseeCoreException {
      return stateMgr.getAssignees().contains(UserManager.getUser());
   }

   public Collection<User> getTransitionAssignees() throws OseeCoreException {
      if (transitionAssignees != null) {
         if (transitionAssignees.size() > 0 && transitionAssignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
            transitionAssignees.remove(UserManager.getUser(SystemUser.UnAssigned));
         }
         if (transitionAssignees.size() > 0) {
            return transitionAssignees;
         }
      }
      return stateMgr.getAssignees();
   }

   public String getTransitionAssigneesStr() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (User u : getTransitionAssignees()) {
         sb.append(u.getName() + SEPERATOR);
      }
      return sb.toString().replaceFirst(SEPERATOR + "$", "");
   }

   public Image getAssigneeImage() throws OseeCoreException {
      return getSma().getAssigneeImage();
   }

   /**
    * @return true if SMA is allowed to have tasks
    * @throws OseeCoreException
    */
   public boolean isTaskable() throws OseeCoreException {
      return getSma().isTaskable();
   }

   public boolean showTaskTab() throws OseeCoreException {
      return getSma().showTaskTab();
   }

   /**
    * @return Returns the taskMgr.
    */
   public TaskManager getTaskMgr() {
      return taskMgr;
   }

   public Result transitionToCancelled(String reason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      Result result =
            transition(DefaultTeamState.Cancelled.name(), Arrays.asList(new User[] {}), reason, transaction,
                  transitionOption);
      return result;
   }

   public Result transitionToCompleted(String reason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      Result result =
            transition(DefaultTeamState.Completed.name(), Arrays.asList(new User[] {}), reason, transaction,
                  transitionOption);
      return result;
   }

   public Result isTransitionValid(final String toStateName, final Collection<User> toAssignees, TransitionOption... transitionOption) throws OseeCoreException {
      boolean overrideTransitionCheck =
            Collections.getAggregate(transitionOption).contains(TransitionOption.OverrideTransitionValidityCheck);
      boolean overrideAssigneeCheck =
            Collections.getAggregate(transitionOption).contains(TransitionOption.OverrideAssigneeCheck);
      // Validate assignees
      if (!overrideAssigneeCheck && (getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.OseeSystem)) || getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.Guest)) || getStateMgr().getAssignees().contains(
            UserManager.getUser(SystemUser.UnAssigned)))) {
         return new Result("Can not transition with \"Guest\", \"UnAssigned\" or \"OseeSystem\" user as assignee.");
      }

      // Validate toState name
      final WorkPageDefinition fromWorkPageDefinition = getWorkPageDefinition();
      final WorkPageDefinition toWorkPageDefinition = getWorkPageDefinitionByName(toStateName);
      if (toWorkPageDefinition == null) {
         return new Result("Invalid toState \"" + toStateName + "\"");
      }

      // Validate transition from fromPage to toPage
      if (!overrideTransitionCheck && !getWorkFlowDefinition().getToPages(fromWorkPageDefinition).contains(
            toWorkPageDefinition)) {
         String errStr =
               "Not configured to transition to \"" + toStateName + "\" from \"" + fromWorkPageDefinition.getPageName() + "\"";
         OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr);
         return new Result(errStr);
      }
      // Don't transition with existing working branch
      if (toStateName.equals(DefaultTeamState.Cancelled.name()) && getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please delete working branch before cancelling.");
      }

      // Don't transition with uncommitted branch if this is a commit state
      if (AtsWorkDefinitions.isAllowCommitBranch(getWorkPageDefinition()) && getBranchMgr().isWorkingBranchInWork()) {
         return new Result("Working Branch exists.  Please commit or delete working branch before transition.");
      }

      // Check extension points for valid transition
      List<IAtsStateItem> atsStateItems = stateItems.getStateItems(fromWorkPageDefinition.getId());
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(this, fromWorkPageDefinition.getPageName(), toStateName, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      for (IAtsStateItem item : atsStateItems) {
         Result result = item.transitioning(this, fromWorkPageDefinition.getPageName(), toStateName, toAssignees);
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;
   }

   public Result transition(String toStateName, User toAssignee, SkynetTransaction transaction, TransitionOption... transitionOption) {
      List<User> users = new ArrayList<User>();
      if (toAssignee != null && !toStateName.equals(DefaultTeamState.Completed.name()) && !toStateName.equals(DefaultTeamState.Cancelled.name())) {
         users.add(toAssignee);
      }
      return transition(toStateName, users, transaction, transitionOption);
   }

   public boolean isTargetedVersionable() throws OseeCoreException {
      if (!(getSma() instanceof TeamWorkFlowArtifact)) {
         return false;
      }
      return ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().getTeamDefinitionHoldingVersions() != null && ((TeamWorkFlowArtifact) getSma()).getTeamDefinition().getTeamDefinitionHoldingVersions().isTeamUsesVersions();
   }

   public Result transition(String toStateName, Collection<User> toAssignees, SkynetTransaction transaction, TransitionOption... transitionOption) {
      return transition(toStateName, toAssignees, null, transaction, transitionOption);
   }

   private Result transition(final String toStateName, final Collection<User> toAssignees, final String completeOrCancelReason, SkynetTransaction transaction, TransitionOption... transitionOption) {
      try {
         final boolean persist = Collections.getAggregate(transitionOption).contains(TransitionOption.Persist);

         Result result = isTransitionValid(toStateName, toAssignees, transitionOption);
         if (result.isFalse()) {
            return result;
         }

         final WorkPageDefinition fromWorkPageDefinition = getWorkPageDefinition();
         final WorkPageDefinition toWorkPageDefinition = getWorkPageDefinitionByName(toStateName);

         transitionHelper(toAssignees, persist, fromWorkPageDefinition, toWorkPageDefinition, toStateName,
               completeOrCancelReason, transaction);
         if (persist) {
            OseeNotificationManager.sendNotifications();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Transaction failed " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   private void transitionHelper(Collection<User> toAssignees, boolean persist, WorkPageDefinition fromPage, WorkPageDefinition toPage, String toStateName, String completeOrCancelReason, SkynetTransaction transaction) throws OseeCoreException {
      // Log transition
      if (toPage.isCancelledPage()) {
         atsLog.addLog(LogType.StateCancelled, stateMgr.getCurrentStateName(), completeOrCancelReason);
      } else {
         atsLog.addLog(LogType.StateComplete, stateMgr.getCurrentStateName(),
               (completeOrCancelReason != null ? completeOrCancelReason : ""));
      }
      atsLog.addLog(LogType.StateEntered, toStateName, "");

      stateMgr.transitionHelper(toAssignees, persist, fromPage, toPage, toStateName, completeOrCancelReason);

      if (getSma().isValidationRequired()) {
         getReviewManager().createValidateReview(false, transaction);
      }

      AtsNotifyUsers.notify(getSma(), AtsNotifyUsers.NotifyType.Subscribed, AtsNotifyUsers.NotifyType.Completed,
            AtsNotifyUsers.NotifyType.Completed);

      // Persist
      if (persist) {
         getSma().persist(transaction);
      }

      getSma().transitioned(fromPage, toPage, toAssignees, true, transaction);

      // Notify extension points of transition
      for (IAtsStateItem item : stateItems.getStateItems(fromPage.getId())) {
         item.transitioned(this, fromPage.getPageName(), toStateName, toAssignees, transaction);
      }
      for (IAtsStateItem item : stateItems.getStateItems(toPage.getId())) {
         item.transitioned(this, fromPage.getPageName(), toStateName, toAssignees, transaction);
      }
   }

   /**
    * @return Returns the editor.
    */
   public SMAEditor getEditor() {
      return editor;
   }

   public String getEditorTitle() throws OseeCoreException {
      return getSma().getEditorTitle();
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
