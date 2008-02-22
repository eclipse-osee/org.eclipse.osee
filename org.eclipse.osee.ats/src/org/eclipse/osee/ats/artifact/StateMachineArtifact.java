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
package org.eclipse.osee.ats.artifact;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.NotifyUsersJob;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.ats.workflow.AtsWorkFlow;
import org.eclipse.osee.ats.workflow.AtsWorkFlowFactory;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class StateMachineArtifact extends ATSArtifact implements IEventReceiver, ISubscribableArtifact, IFavoriteableArtifact {

   private XCurrentStateDam currentStateDam;
   private XStateDam stateDam;
   protected SMAManager smaMgr;
   private ATSLog atsLog;
   private ATSNote atsNote;
   private Set<IRelationEnumeration> smaRelations = new HashSet<IRelationEnumeration>();
   private SMAState preSaveState;
   private User preSaveOriginator;
   public static double MAN_DAY_HOURS = 8;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public StateMachineArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      currentStateDam = new XCurrentStateDam(this);
      stateDam = new XStateDam(this);
      smaMgr = new SMAManager(this);
      atsLog = new ATSLog(this);
      atsNote = new ATSNote(this);
      preSaveState = smaMgr.getSMAState();
      if (getLog().getLastEvent(LogType.Originated) == null)
         preSaveOriginator = SkynetAuthentication.getInstance().getAuthenticatedUser();
      else
         preSaveOriginator = smaMgr.getOriginator();
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
   }

   public String getHelpContext() {
      return "atsWorkflowEditorWorkflowTab";
   }

   public abstract boolean isMetricsFromTasks() throws SQLException;

   public String getArtifactSuperTypeName() {
      return getArtifactTypeName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.Artifact#persist(boolean, boolean)
    */
   @Override
   public void persist(boolean recurse, boolean persistAttributes) throws SQLException {
      super.persist(recurse, persistAttributes);
      // Since multiple different ways exist to change the assignees,
      // notitification is performed on
      // the persist
      notifyNewAssigneesAndReset();
      notifyOriginatorAndReset();
   }

   /**
    * Override to apply different algorithm to current section expansion.
    * 
    * @param page
    * @return true if section should be expanded
    */
   public boolean isCurrentSectionExpanded(AtsWorkPage page) {
      return smaMgr.isCurrentState(page);
   }

   public void notifyNewAssigneesAndReset() {
      Set<User> newAssignees = new HashSet<User>();
      for (User user : smaMgr.getAssignees()) {
         if (!preSaveState.getAssignees().contains(user)) {
            newAssignees.add(user);
         }
      }
      preSaveState = smaMgr.getSMAState();
      if (newAssignees.size() == 0) return;
      try {
         NotifyUsersJob job = new NotifyUsersJob(this, newAssignees, NotifyUsersJob.NotifyType.Assignee);
         job.setPriority(Job.SHORT);
         job.schedule();
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void notifyOriginatorAndReset() {
      if (preSaveOriginator != null && smaMgr.getOriginator() != null && !smaMgr.getOriginator().equals(
            preSaveOriginator)) {
         try {
            Set<User> originators = new HashSet<User>();
            originators.add(smaMgr.getOriginator());
            NotifyUsersJob job = new NotifyUsersJob(this, originators, NotifyUsersJob.NotifyType.Originator);
            job.setPriority(Job.SHORT);
            job.schedule();
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      preSaveOriginator = smaMgr.getOriginator();
   }

   public boolean isValidationRequired() throws SQLException {
      return false;
   }

   public abstract Set<User> getPrivilegedUsers() throws SQLException;

   public String getDescription() {
      return "";
   }

   public ArrayList<EmailGroup> getEmailableGroups() {
      ArrayList<EmailGroup> groupNames = new ArrayList<EmailGroup>();
      ArrayList<String> emails = new ArrayList<String>();
      emails.add(smaMgr.getOriginator().getEmail());
      groupNames.add(new EmailGroup("Originator", emails));
      if (smaMgr.getAssignees().size() > 0) {
         emails = new ArrayList<String>();
         for (User u : smaMgr.getAssignees())
            emails.add(u.getEmail());
         groupNames.add(new EmailGroup("Assignees", emails));
      }
      return groupNames;
   }

   public abstract StateMachineArtifact getParentSMA() throws SQLException;

   public abstract ActionArtifact getParentActionArtifact() throws SQLException;

   public abstract TeamWorkFlowArtifact getParentTeamWorkflow() throws SQLException;

   public String getPreviewHtml() {
      return getPreviewHtml(PreviewStyle.NONE);
   }

   public String getPreviewHtml(PreviewStyle... styles) {
      Overview o = new Overview();
      o.addHeader(this, styles);
      o.addFooter(this, styles);
      return o.getPage();
   }

   @Override
   public Image getImage() {
      boolean subscribed = false;
      boolean favorite = false;
      boolean metricsFromTasks = false;
      try {
         subscribed = isSubscribed(SkynetAuthentication.getInstance().getAuthenticatedUser());
         favorite = isFavorite(SkynetAuthentication.getInstance().getAuthenticatedUser());
         metricsFromTasks = isMetricsFromTasks();
      } catch (SQLException ex) {
         // Do nothing
      }
      return super.getDescriptor().getImage(subscribed, favorite, getMainAnnotationType(), metricsFromTasks);
   }

   public boolean isDefaultShowAllTasks() {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return true;
      return false;
   }

   public boolean isUnCancellable() {
      try {
         LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateCancelled);
         if (item == null) throw new IllegalArgumentException("No Cancelled Event");
         for (WorkPage toPage : smaMgr.getWorkPage().getToPages())
            if (toPage.getName().equals(item.getState())) return true;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return false;
   }

   public boolean isTaskable() {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return false;
      return true;
   }

   public boolean showTaskTab() {
      return isTaskable();
   }

   public ATSLog getLog() {
      return atsLog;
   }

   public ATSNote getNotes() {
      return atsNote;
   }

   /**
    * If state has tasks, statePercentComplete is determined from #tasks/%complete Else return stored state percent
    * complete
    * 
    * @return percent complete of state/tasks
    */
   public int getStatePercentComplete() {
      return getStatePercentComplete(getCurrentStateName());
   }

   /**
    * If state has tasks, statePercentComplete is determined from #tasks/%complete Else return stored state percent
    * complete
    * 
    * @param stateName
    * @return percent complete of state/tasks
    */
   public int getStatePercentComplete(String stateName) {
      try {
         SMAState state = getCurrentStateDam().getState();
         if (state == null) state = getStateDam().getState(stateName, false);
         if (state == null) return 0;
         if (stateName.equals(DefaultTeamState.Implement.name()) && isMetricsFromTasks()) {
            Collection<TaskArtifact> tasks = smaMgr.getTaskMgr().getTaskArtifacts(state.getName());
            if (tasks.size() > 0) {
               int percent = 0;
               for (TaskArtifact taskArt : tasks)
                  percent += taskArt.getWorldViewTotalPercentComplete();
               if (percent == 0) return 0;
               return percent / tasks.size();
            }
            return 0;
         } else {
            return state.getPercentComplete();
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return 0;
      }
   }

   /**
    * @return return hours spent on state and state-tasks
    */
   public double getStateHoursSpent() {
      return getStateHoursSpent(getCurrentStateName());
   }

   /**
    * @param stateName
    * @return return hours spent on state and state-tasks
    */
   public double getStateHoursSpent(String stateName) {
      SMAState state = null;
      if (getCurrentStateName().equals(stateName)) state = getCurrentStateDam().getState();
      if (state == null) state = getStateDam().getState(stateName, false);
      if (state == null) return 0;

      double hours = 0;
      try {
         if ((this instanceof TeamWorkFlowArtifact) && stateName.equals(DefaultTeamState.Implement.name()) && isMetricsFromTasks())
            for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts(stateName))
               hours += taskArt.getTotalHoursSpent();
         else
            hours = state.getHoursSpent();
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return hours;
   }

   /**
    * @return total hours spent on current and past states and tasks
    */
   public double getTotalHoursSpent() {
      double hours = 0;
      try {
         for (String stateName : smaMgr.getWorkFlow().getPageNames())
            // Adds state hours and task hours
            hours += getStateHoursSpent(stateName);
      } catch (Exception ex) {
         // do nothing
      }
      return hours;
   }

   public String getEditorTitle() {
      return getDescriptiveName();
   }

   public String getWorldViewActionableItems() {
      return "";
   }

   public void registerSMARelation(RelationSide side) {
      smaRelations.add(side);
   }

   public Image getAssigneeImage() {
      if (isDeleted()) return null;
      if (smaMgr.getAssignees().size() > 0) {
         if (smaMgr.isAssigneeMe())
            return AtsPlugin.getInstance().getImage("red_user_sm.gif");
         else
            return AtsPlugin.getInstance().getImage("user_sm.gif");
      }
      return null;
   }

   public AtsWorkFlow getWorkFlow() {
      try {
         return AtsWorkFlowFactory.getInstance().getWorkflow(this);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return null;
   }

   public String getCurrentStateName() {
      try {
         return currentStateDam.getState().getName();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   /**
    * @return Returns the CurrentStateDam.
    */
   public XCurrentStateDam getCurrentStateDam() {
      return currentStateDam;
   }

   /**
    * @return Returns the current SMAState.
    */
   public SMAState getCurrentState() {
      return getCurrentStateDam().getState();
   }

   /**
    * @return Returns the StateDam.
    */
   public XStateDam getStateDam() {
      return stateDam;
   }

   public void addSubscribed(User user) throws SQLException {
      relate(RelationSide.SubscribedUser_User, user, true);
   }

   public void removeSubscribed(User user) throws SQLException {
      unrelate(RelationSide.SubscribedUser_User, user, true);
   }

   public boolean isSubscribed(User user) throws SQLException {
      return (getArtifacts(RelationSide.SubscribedUser_User).contains(user));
   }

   public ArrayList<User> getSubscribed() throws SQLException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getArtifacts(RelationSide.SubscribedUser_User))
         arts.add((User) art);
      return arts;
   }

   public void addFavorite(User user) throws SQLException {
      relate(RelationSide.FavoriteUser_User, user, true);
   }

   public void removeFavorite(User user) throws SQLException {
      unrelate(RelationSide.FavoriteUser_User, user, true);
   }

   public boolean isFavorite(User user) throws SQLException {
      return (getArtifacts(RelationSide.FavoriteUser_User).contains(user));
   }

   public ArrayList<User> getFavorites() throws SQLException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getArtifacts(RelationSide.FavoriteUser_User))
         arts.add((User) art);
      return arts;
   }

   public boolean amISubscribed() {
      try {
         return isSubscribed(SkynetAuthentication.getInstance().getAuthenticatedUser());
      } catch (SQLException ex) {
         return false;
      }
   }

   public boolean amIFavorite() {
      try {
         return isFavorite(SkynetAuthentication.getInstance().getAuthenticatedUser());
      } catch (SQLException ex) {
         return false;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.artifact.ATSArtifact#atsDelete(java.util.Set, java.util.Map)
    */
   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws SQLException {
      SMAEditor.close(this, true);
      super.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewType() {
      return getArtifactTypeName();
   }

   public String getWorldViewTitle() {
      return getDescriptiveName();
   }

   public String getWorldViewState() {
      return getCurrentStateName();
   }

   public String getWorldViewActivePoc() {
      return smaMgr.getAssigneesStr();
   }

   public String getWorldViewCreatedDateStr() {
      try {
         if (getWorldViewCreatedDate() == null) return XViewerCells.getCellExceptionString("No creation date");
         return new XDate(getWorldViewCreatedDate()).getMMDDYYHHMM();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewCompletedDateStr() {
      try {
         if (smaMgr.isCompleted()) {
            if (getWorldViewCompletedDate() == null) {
               OSEELog.logSevere(AtsPlugin.class, "Completed with no date => " + smaMgr.getSma().getHumanReadableId(),
                     true);
               return XViewerCells.getCellExceptionString("Completed with no date.");
            }
            return new XDate(getWorldViewCompletedDate()).getMMDDYYHHMM();
         }
         return "";
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public String getWorldViewCancelledDateStr() {
      try {
         if (smaMgr.isCancelled()) {
            if (getWorldViewCancelledDate() == null) {
               OSEELog.logSevere(AtsPlugin.class, "Cancelled with no date => " + smaMgr.getSma().getHumanReadableId(),
                     false);
               return XViewerCells.getCellExceptionString("Cancelled with no date.");
            }
            return new XDate(getWorldViewCancelledDate()).getMMDDYYHHMM();
         }
         return "";
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public Date getWorldViewCreatedDate() throws Exception {
      return getLog().getEvent(LogType.Originated).getDate();
   }

   public String getWorldViewOriginator() {
      return smaMgr.getOriginator().getName();
   }

   public String getWorldViewID() {
      return getHumanReadableId();
   }

   public String getWorldViewLegacyPCR() {
      if (isAttributeTypeValid(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName())) return getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName());
      return "";
   }

   public Date getWorldViewCompletedDate() throws Exception {
      LogItem item = getLog().getStateEvent(LogType.StateEntered, DefaultTeamState.Completed.name());
      if (item != null) return item.getDate();
      return null;
   }

   public Date getWorldViewCancelledDate() throws Exception {
      LogItem item = getLog().getStateEvent(LogType.StateEntered, DefaultTeamState.Cancelled.name());
      if (item != null) return item.getDate();
      return null;
   }

   public abstract VersionArtifact getTargetedForVersion() throws SQLException;

   public ChangeType getWorldViewChangeType() {
      return ChangeType.None;
   }

   public String getWorldViewChangeTypeStr() {
      if (getWorldViewChangeType() == null || getWorldViewChangeType() == ChangeType.None)
         return "";
      else
         return getWorldViewChangeType().name();
   }

   public double getWorldViewEstimatedHours() {
      try {
         if (isMetricsFromTasks()) {
            double hours = 0;
            for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts(DefaultTeamState.Implement.name())) {
               hours += taskArt.getWorldViewEstimatedHours();
            }
            return hours;
         } else {
            String value = getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
            if (value == null || value.equals("")) return 0;
            return new Float(value).doubleValue();
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "HRID " + getHumanReadableId(), ex, true);
      }
      return 0;
   }

   public String getWorldViewUserCommunity() {
      return "";
   }

   public String getWorldViewPriority() {
      return "";
   }

   public String getWorldViewResolution() {
      try {
         return getAttributesToString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
      } catch (SQLException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public double getWorldViewRemainHours() {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return 0;
      double est = getWorldViewEstimatedHours();
      if (getWorldViewStatePercentComplete() == 0) return getWorldViewEstimatedHours();
      SMAState implementState = smaMgr.getSMAState(DefaultTeamState.Implement.name(), false);
      if (implementState == null || implementState.getPercentComplete() == 0) return getWorldViewEstimatedHours();
      double remain = getWorldViewEstimatedHours() - (est * (implementState.getPercentComplete() / 100.0));
      return remain;
   }

   public Result isWorldViewRemainHoursValid() {
      String value = getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
      if (value == null || value.equals("")) return new Result("Estimated Hours not set.");
      try {
         new Float(value).doubleValue();
      } catch (NumberFormatException ex) {
         OSEELog.logException(AtsPlugin.class, "HRID " + getHumanReadableId(), ex, true);
         return new Result("Estimated Hours value is invalid double \"" + value + "\"");
      }
      return Result.TrueResult;
   }

   public Result isWorldViewManDaysNeededValid() {
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) return result;
      if (getManDayHrsPreference() == 0) return new Result("Man Day Hours Preference is not set.");

      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewManDaysNeeded()
    */
   public double getWorldViewManDaysNeeded() {
      double hrsRemain = getWorldViewRemainHours();
      double manDaysNeeded = 0;
      if (hrsRemain != 0) manDaysNeeded = hrsRemain / getManDayHrsPreference();
      return manDaysNeeded;
   }

   public double getManDayHrsPreference() {
      return MAN_DAY_HOURS;
   }

   public double getWorldViewAnnualCostAvoidance() {
      return 0;
   }

   public Result isWorldViewAnnualCostAvoidanceValid() {
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) return result;
      String value = getSoleAttributeValue(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName());
      if (value == null || value.equals("")) return new Result("Weekly Benefit Hours not set.");
      try {
         double val = new Float(value).doubleValue();
         if (val == 0) return new Result("Weekly Benefit Hours not set.");
      } catch (NumberFormatException ex) {
         OSEELog.logException(AtsPlugin.class, "HRID " + getHumanReadableId(), ex, true);
         return new Result("Weekly Benefit value is invalid double \"" + value + "\"");
      }
      return Result.TrueResult;
   }

   public String getWorldViewRemainHoursStr() {
      if (smaMgr.isCancelled() || smaMgr.isCompleted()) return AtsLib.doubleToStrString(0);
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) return result.getText();
      return AtsLib.doubleToStrString(getWorldViewRemainHours());
   }

   public String getWorldViewEstimatedHoursStr() {
      return AtsLib.doubleToStrString(getWorldViewEstimatedHours());
   }

   public String getWorldViewNotes() {
      return getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewWorkPackage() {
      return getSoleAttributeValue(ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewCategory() {
      return getSoleAttributeValue(ATSAttributes.CATEGORY_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewCategory2() {
      return getSoleAttributeValue(ATSAttributes.CATEGORY2_ATTRIBUTE.getStoreName());
   }

   public String getWorldViewCategory3() {
      return getSoleAttributeValue(ATSAttributes.CATEGORY3_ATTRIBUTE.getStoreName());
   }

   public int getWorldViewStatePercentComplete() {
      return getStatePercentComplete(getCurrentStateName());
   }

   public String getWorldViewNumberOfTasks() {
      try {
         int num = getSmaMgr().getTaskMgr().getTaskArtifacts().size();
         if (num == 0) return "";
         return String.valueOf(num);
      } catch (SQLException ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   public abstract int getWorldViewTotalPercentComplete();

   public double getWorldViewStateHoursSpent() {
      return getStateHoursSpent(getCurrentStateName());
   }

   public String getWorldViewRelatedToState() {
      return "";
   }

   public double getWorldViewTotalHoursSpent() {
      return getTotalHoursSpent();
   }

   public abstract String getWorldViewVersion();

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    * 
    * @return true if any object in SMA tree is dirty
    */
   public boolean isSMADirty() {
      return isSMADirty(new StringBuilder());
   }

   public boolean isSMADirty(StringBuilder info) {
      if (isRelationsAndArtifactsDirty(smaRelations, info)) return true;
      return false;
   }

   public void saveSMA() {
      try {
         saveArtifactsFromRelations(smaRelations);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, "Can't save artifact " + getHumanReadableId(), ex, true);
      }
   }

   public void revertSMA() {
      try {
         revertArtifactsFromRelations(smaRelations);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, "Can't revert artifact " + getHumanReadableId(), ex, true);
      }
   }

   public abstract Date getWorldViewEstimatedReleaseDate() throws Exception;

   public String getWorldViewEstimatedReleaseDateStr() {
      try {
         if (getWorldViewEstimatedReleaseDate() == null) return "";
         return new XDate(getWorldViewEstimatedReleaseDate()).getMMDDYYHHMM();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public abstract Date getWorldViewReleaseDate() throws Exception;

   public String getWorldViewReleaseDateStr() {
      try {
         if (getWorldViewReleaseDate() == null) return "";
         return new XDate(getWorldViewReleaseDate()).getMMDDYYHHMM();
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public boolean isReleased() {
      try {
         return getWorldViewReleaseDate() != null;
      } catch (Exception ex) {
         return false;
      }
   }

   /**
    * Will be called when status for this SMA has been changed. SMA's can override this to perform tasks.
    */
   public void statusChanged() {
   }

   /**
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    * 
    * @throws SQLException
    */
   public void transitioned(AtsWorkPage fromPage, AtsWorkPage toPage, Collection<User> toAssignees, boolean persist) throws SQLException {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperName()
    */
   public String getHyperName() {
      return getDescriptiveName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperType()
    */
   public String getHyperType() {
      return "Team";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperState()
    */
   public String getHyperState() {
      return getCurrentStateName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssignee()
    */
   public String getHyperAssignee() {
      return smaMgr.getAssigneesStr();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperImage()
    */
   public Image getHyperImage() {
      return getImage();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssigneeImage()
    */
   public Image getHyperAssigneeImage() {
      return smaMgr.getAssigneeImage();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperArtifact()
    */
   public Artifact getHyperArtifact() {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDecision()
    */
   public String getWorldViewDecision() {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact#getParentSMArt()
    */
   public Artifact getParentSMArt() throws SQLException {
      return getParentSMA();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewValidationRequiredStr()
    */
   public String getWorldViewValidationRequiredStr() {
      try {
         if (isAttributeTypeValid(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName())) return String.valueOf(getSoleBooleanAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName()));
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewDeadlineAlerting()
    */
   public Result isWorldViewDeadlineAlerting() {
      return Result.FalseResult;
   }

   public int getWorldViewPercentRework() {
      return 0;
   }

   public String getWorldViewPercentReworkStr() {
      int reWork = getWorldViewPercentRework();
      if (reWork == 0) return "";
      return String.valueOf(reWork);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      try {
         // Check if LocalEvent and NOT RemoteEvent. Since EventService will handle moving access
         // control changes across the OSEE instances, only handle local events
         if (event instanceof LocalTransactionEvent) {
            // Only update access control if THIS artifact is modified
            if (((LocalTransactionEvent) event).getEventData(this).isModified()) {
               // Update branch access control
               smaMgr.getBranchMgr().updateBranchAccessControl();
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return false;
   }

   public static Set<String> getAllSMATypeNames() {
      java.util.Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
      artTypeNames.addAll(Arrays.asList(new String[] {TaskArtifact.ARTIFACT_NAME, DecisionReviewArtifact.ARTIFACT_NAME,
            PeerToPeerReviewArtifact.ARTIFACT_NAME}));
      return artTypeNames;
   }

   public static Collection<Artifact> getAllSMATypeArtifacts() throws SQLException {
      List<ISearchPrimitive> artifactTypeCriteria = new LinkedList<ISearchPrimitive>();
      for (String artType : getAllSMATypeNames())
         artifactTypeCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));

      return ArtifactPersistenceManager.getInstance().getArtifacts(artifactTypeCriteria, false,
            BranchPersistenceManager.getInstance().getAtsBranch());
   }

   public static Collection<Artifact> getAllTeamWorkflowArtifacts() throws SQLException {
      List<ISearchPrimitive> artifactTypeCriteria = new LinkedList<ISearchPrimitive>();
      for (String artType : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         artifactTypeCriteria.add(new ArtifactTypeSearch(artType, Operator.EQUAL));

      return ArtifactPersistenceManager.getInstance().getArtifacts(artifactTypeCriteria, false,
            BranchPersistenceManager.getInstance().getAtsBranch());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() {
      return "";
   }

   /**
    * @return the smaMgr
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewAuthor()
    */
   public String getWorldViewReviewAuthor() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewDecider()
    */
   public String getWorldViewReviewDecider() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewModerator()
    */
   public String getWorldViewReviewModerator() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() {
      return "";
   }

}
