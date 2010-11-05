/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionLockedType;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserGroupsCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public final class PromptChangeUtil {

   private PromptChangeUtil() {
      // Utility class
   }

   public static boolean promptChangeGroups(AbstractWorkflowArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeGroups(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeGroups(final Collection<? extends AbstractWorkflowArtifact> smas, boolean persist) throws OseeCoreException {
      Set<Artifact> selected = new HashSet<Artifact>();
      for (AbstractWorkflowArtifact sma : smas) {
         selected.addAll(sma.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
      }
      Collection<Artifact> allGroups = UniversalGroup.getGroupsNotRoot(AtsUtil.getAtsBranch());
      UserGroupsCheckTreeDialog dialog = new UserGroupsCheckTreeDialog(allGroups);
      dialog.setTitle("Select Groups");
      dialog.setInitialSelections(selected.toArray());
      if (dialog.open() == 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Set Groups");
         for (AbstractWorkflowArtifact sma : smas) {
            sma.setRelations(CoreRelationTypes.Universal_Grouping__Group, dialog.getSelection());
            sma.persist(transaction);
         }
         transaction.execute();
         return true;
      }
      return false;
   }

   public static boolean promptChangeAssignees(AbstractWorkflowArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeAssignees(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeAssignees(final Collection<? extends AbstractWorkflowArtifact> smas, boolean persist) throws OseeCoreException {
      for (AbstractWorkflowArtifact sma : smas) {
         if (sma.isCompleted()) {
            AWorkbench.popup("ERROR",
               "Can't assign completed " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
            return false;
         } else if (sma.isCancelled()) {
            AWorkbench.popup("ERROR",
               "Can't assign cancelled " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
            return false;
         }
      }
      UserCheckTreeDialog uld = new UserCheckTreeDialog();
      uld.setMessage("Select to assign.\nDeSelect to un-assign.");
      if (smas.iterator().next().getParentTeamWorkflow() != null) {
         uld.setTeamMembers(smas.iterator().next().getParentTeamWorkflow().getTeamDefinition().getMembersAndLeads());
      }

      if (smas.size() == 1) {
         uld.setInitialSelections(smas.iterator().next().getStateMgr().getAssignees());
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<User> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return false;
      }
      // As a convenience, remove the UnAssigned user if another user is selected
      if (users.size() > 1) {
         users.remove(UserManager.getUser(SystemUser.UnAssigned));
      }
      for (AbstractWorkflowArtifact sma : smas) {
         sma.getStateMgr().setAssignees(users);
      }
      if (persist) {
         Artifacts.persistInTransaction(smas);
      }
      return true;
   }

   public static boolean promptChangeOriginator(AbstractWorkflowArtifact sma) throws OseeCoreException {
      return promptChangeOriginator(Arrays.asList(sma));
   }

   public static boolean promptChangeOriginator(final Collection<? extends AbstractWorkflowArtifact> smas) throws OseeCoreException {
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select New Originator", Active.Active);
      int result = ld.open();
      if (result == 0) {
         User selectedUser = ld.getSelection();
         for (AbstractWorkflowArtifact sma : smas) {
            sma.setOriginator(selectedUser);
         }
         return true;
      }
      return false;
   }

   public static boolean promptChangeVersion(AbstractWorkflowArtifact sma, VersionReleaseType versionReleaseType, VersionLockedType versionLockType, boolean persist) throws OseeCoreException {
      if (AtsUtil.isAtsAdmin() && !sma.isTeamWorkflow()) {
         AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + sma.getName());
         return false;
      }
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) sma), versionReleaseType, versionLockType,
         persist);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> smas, VersionReleaseType versionReleaseType, VersionLockedType versionLockType, final boolean persist) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : smas) {
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }
         if (teamArt.isReleased() || teamArt.isVersionLocked()) {
            String error =
               "Team Workflow\n \"" + teamArt.getName() + "\"\n targeted version is locked or already released.";
            if (AtsUtil.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
               error + "\n\nOverride?")) {
               return false;
            } else if (!AtsUtil.isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
            }
         }
         if (teamDefHoldingVersions != null && teamDefHoldingVersions != teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions()) {
            AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
            return false;
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
         new VersionListDialog("Select Version", "Select Version", teamDefHoldingVersions.getVersionsArtifacts(
            versionReleaseType, versionLockType));
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
      //now check selected version
      if (newVersion.isVersionLocked()) {
         String error = "Version \"" + newVersion.getFullDisplayName() + "\" is locked or already released.";
         if (AtsUtil.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
            error + "\n\nOverride?")) {
            return false;
         } else if (!AtsUtil.isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }

      for (TeamWorkFlowArtifact teamArt : smas) {
         teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
            java.util.Collections.singleton(newVersion));
      }
      if (persist) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Version");
         for (TeamWorkFlowArtifact teamArt : smas) {
            teamArt.persist(transaction);
         }
         transaction.execute();
      }
      return true;
   }

   public static boolean promptChangePoints(AbstractWorkflowArtifact sma, boolean persist) {
      if (sma.isTeamWorkflow()) {
         return promptChangePoints(Arrays.asList((TeamWorkFlowArtifact) sma), persist);
      }
      return false;
   }

   public static boolean promptChangePoints(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      final ChangePointDialog dialog = new ChangePointDialog(Displays.getActiveShell());
      try {
         if (teams.size() == 1) {
            dialog.setSelected(teams.iterator().next().getWorldViewPoint());
         }
         if (dialog.open() == 0) {

            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Points");

            for (TeamWorkFlowArtifact team : teams) {
               if (dialog.isClearSelected() || !team.getWorldViewPoint().equals(dialog.getSelection())) {
                  if (dialog.isClearSelected()) {
                     team.deleteAttributes(AtsAttributeTypes.Points);
                  } else {
                     team.setSoleAttributeFromString(AtsAttributeTypes.Points, dialog.getSelection());
                  }
                  team.saveSMA(transaction);
               }
            }
            transaction.execute();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change points", ex);
         return false;
      }
   }

   public static boolean promptChangePercentAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(final Collection<? extends AbstractWorkflowArtifact> smas, IAttributeType attributeType, boolean persist, boolean multiLine) {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, smas, persist, multiLine);
   }

   public static boolean promptChangeAttribute(final Artifact sma, IAttributeType attributeType, boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist,
            multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, final boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(sma), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeDate(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, java.util.Collections.singleton(sma), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
            "Can't save " + attributeType.getUnqualifiedName() + " date to artifact " + sma.getHumanReadableId(), ex);
      }
      return false;
   }

   public static boolean promptChangeReleaseDate(AbstractWorkflowArtifact sma) {
      if (sma.isReleased() || sma.isVersionLocked()) {
         AWorkbench.popup("ERROR", "Team Workflow\n \"" + sma.getName() + "\"\n version is locked or already released.");
         return false;
      }
      try {
         VersionArtifact verArt = sma.getTargetedForVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted
            // for release xxx - want to change?
            DateSelectionDialog diag =
               new DateSelectionDialog(
                  "Select Release Date Date",
                  "Warning: " + sma.getArtifactTypeName() + "'s release date is handled\n" + "by targeted for version \"" + verArt.getName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                  verArt.getReleaseDate());
            if (verArt.getReleaseDate() != null) {
               diag.setSelectedDate(verArt.getReleaseDate());
            }
            if (diag.open() == 0) {
               verArt.setSoleAttributeValue(AtsAttributeTypes.ReleaseDate, diag.getSelectedDate());
               verArt.persist();
               return true;
            }
         } else {
            // prompt that current release is (get from attribute) - want to change?
            DateSelectionDialog diag =
               new DateSelectionDialog("Select Release Date", "Select Release Date", sma.getWorldViewReleaseDate());
            if (sma.getWorldViewReleaseDate() != null) {
               diag.setSelectedDate(sma.getWorldViewReleaseDate());
            }
            if (diag.open() == 0) {
               sma.setSoleAttributeValue(AtsAttributeTypes.ReleaseDate, diag.getSelectedDate());
               sma.persist();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't save release date " + sma.getHumanReadableId(), ex);
      }
      return false;
   }

   public static boolean promptChangeEstimatedReleaseDate(AbstractWorkflowArtifact sma) {
      try {
         VersionArtifact verArt = sma.getTargetedForVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted for release xxx -
            // want to change?
            DateSelectionDialog diag =
               new DateSelectionDialog(
                  "Select Estimated Release Date Date",
                  "Warning: " + sma.getArtifactTypeName() + "'s estimated release date is handled\n" + "by targeted for version \"" + verArt.getName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                  verArt.getEstimatedReleaseDate());
            if (verArt.getEstimatedReleaseDate() != null) {
               diag.setSelectedDate(verArt.getEstimatedReleaseDate());
            }
            if (diag.open() == 0) {
               verArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, diag.getSelectedDate());
               verArt.persist();
               return true;
            }
         } else {
            // prompt that current est release is (get from attribute); want to
            // change
            DateSelectionDialog diag =
               new DateSelectionDialog("Select Estimate Release Date", "Select Estimated Release Date",
                  sma.getWorldViewEstimatedReleaseDate());
            if (sma.getWorldViewEstimatedReleaseDate() != null) {
               diag.setSelectedDate(sma.getWorldViewEstimatedReleaseDate());
            }
            if (diag.open() == 0) {
               sma.setSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, diag.getSelectedDate());
               sma.persist();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
            "Can't save est release date " + sma.getHumanReadableId(), ex);
      }
      return false;
   }
}