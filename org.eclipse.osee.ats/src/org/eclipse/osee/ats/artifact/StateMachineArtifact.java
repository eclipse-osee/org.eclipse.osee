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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.NotifyUsersJob;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class StateMachineArtifact extends ATSArtifact implements IWorldViewArtifact, IEventReceiver, ISubscribableArtifact, IFavoriteableArtifact {

   protected SMAManager smaMgr;
   private ATSLog atsLog;
   private ATSNote atsNote;
   private Set<IRelationEnumeration> smaRelations = new HashSet<IRelationEnumeration>();
   private Collection<User> preSaveStateAssignees;
   private User preSaveOriginator;
   public static double MAN_DAY_HOURS = 8;
   protected WorkFlowDefinition workFlowDefinition;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public StateMachineArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      smaMgr = new SMAManager(this);
      atsLog = new ATSLog(this);
      atsNote = new ATSNote(this);
      preSaveStateAssignees = smaMgr.getStateMgr().getAssignees();
      if (smaMgr.getOriginator() == null)
         preSaveOriginator = SkynetAuthentication.getUser();
      else
         preSaveOriginator = smaMgr.getOriginator();
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
   }

   /**
    * This method will create an assignee relation for each current assignee. Assignees are related to user artifacts to
    * speed up ATS searching. This does not persist the artifact.
    * 
    * @throws OseeCoreException
    * @throws SQLException
    */
   public void updateAssigneeRelations() throws OseeCoreException, SQLException {
      setRelations(CoreRelationEnumeration.Users_User, getSmaMgr().getStateMgr().getAssignees());
   }

   public boolean hasChildren() throws OseeCoreException {
      for (IRelationEnumeration iRelationEnumeration : smaRelations) {
         if (getRelatedArtifactsCount(iRelationEnumeration) > 0) return true;
      }
      return false;
   }

   public String getHelpContext() {
      return "atsWorkflowEditorWorkflowTab";
   }

   public String getArtifactSuperTypeName() {
      return getArtifactTypeNameSuppressException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDate()
    */
   @Override
   public Date getWorldViewDeadlineDate() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDeadlineDateStr()
    */
   @Override
   public String getWorldViewDeadlineDateStr() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewDescription()
    */
   @Override
   public String getWorldViewDescription() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewImplementer()
    */
   @Override
   public String getWorldViewImplementer() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewTeam()
    */
   @Override
   public String getWorldViewTeam() throws Exception {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewWeeklyBenefit()
    */
   @Override
   public double getWorldViewWeeklyBenefit() throws Exception {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.Artifact#persistAttributes()
    */
   @Override
   public void persistAttributes() throws SQLException {
      super.persistAttributes();
      // Since multiple ways exist to change the assignees, notification is performed on the persist
      notifyNewAssigneesAndReset();
      notifyOriginatorAndReset();
   }

   /**
    * Override to apply different algorithm to current section expansion.
    * 
    * @param page
    * @return true if section should be expanded
    */
   public boolean isCurrentSectionExpanded(String stateName) {
      return smaMgr.getStateMgr().getCurrentStateName().equals(stateName);
   }

   public void notifyNewAssigneesAndReset() {
      Set<User> newAssignees = new HashSet<User>();
      for (User user : smaMgr.getStateMgr().getAssignees()) {
         if (!preSaveStateAssignees.contains(user)) {
            newAssignees.add(user);
         }
      }
      preSaveStateAssignees = smaMgr.getStateMgr().getAssignees();
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

   public boolean isValidationRequired() throws SQLException, MultipleAttributesExist {
      return false;
   }

   public abstract Set<User> getPrivilegedUsers() throws SQLException;

   public String getDescription() {
      return "";
   }

   public ArrayList<EmailGroup> getEmailableGroups() throws SQLException, MultipleAttributesExist {
      ArrayList<EmailGroup> groupNames = new ArrayList<EmailGroup>();
      ArrayList<String> emails = new ArrayList<String>();
      emails.add(smaMgr.getOriginator().getEmail());
      groupNames.add(new EmailGroup("Originator", emails));
      if (smaMgr.getStateMgr().getAssignees().size() > 0) {
         emails = new ArrayList<String>();
         for (User u : smaMgr.getStateMgr().getAssignees())
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
      try {
         subscribed = isSubscribed(SkynetAuthentication.getUser());
         favorite = isFavorite(SkynetAuthentication.getUser());
      } catch (SQLException ex) {
         // Do nothing
      }
      return super.getArtifactType().getImage(subscribed, favorite, getMainAnnotationType());
   }

   public boolean isDefaultShowAllTasks() {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return true;
      return false;
   }

   public boolean isUnCancellable() {
      try {
         LogItem item = smaMgr.getSma().getLog().getStateEvent(LogType.StateCancelled);
         if (item == null) throw new IllegalArgumentException("No Cancelled Event");
         for (WorkPageDefinition toWorkPageDefinition : smaMgr.getWorkFlowDefinition().getToPages(
               smaMgr.getWorkPageDefinition()))
            if (toWorkPageDefinition.getPageName().equals(item.getState())) return true;
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

   public String getEditorTitle() {
      return getDescriptiveName();
   }

   public String getWorldViewActionableItems() throws Exception {
      return "";
   }

   public void registerSMARelation(AtsRelation side) {
      smaRelations.add(side);
   }

   public Image getAssigneeImage() throws Exception {
      if (isDeleted()) return null;
      if (smaMgr.getStateMgr().getAssignees().size() > 0) {
         if (smaMgr.isAssigneeMe())
            return AtsPlugin.getInstance().getImage("red_user_sm.gif");
         else
            return AtsPlugin.getInstance().getImage("user_sm.gif");
      }
      return null;
   }

   /**
    * @return
    */
   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException, SQLException {
      if (workFlowDefinition == null) {
         try {
            workFlowDefinition = WorkFlowDefinitionFactory.getWorkFlowDefinition(this);
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
      return workFlowDefinition;
   }

   public void addSubscribed(User user) throws SQLException {
      relate(AtsRelation.SubscribedUser_User, user, true);
   }

   public void removeSubscribed(User user) throws SQLException {
      deleteRelation(AtsRelation.SubscribedUser_User, user);
      persistRelations();
   }

   public boolean isSubscribed(User user) throws SQLException {
      return (getRelatedArtifacts(AtsRelation.SubscribedUser_User).contains(user));
   }

   public ArrayList<User> getSubscribed() throws SQLException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getRelatedArtifacts(AtsRelation.SubscribedUser_User))
         arts.add((User) art);
      return arts;
   }

   public void addFavorite(User user) throws SQLException {
      relate(AtsRelation.FavoriteUser_User, user, true);
   }

   public void removeFavorite(User user) throws SQLException {
      deleteRelation(AtsRelation.FavoriteUser_User, user);
      persistRelations();
   }

   public boolean isFavorite(User user) throws SQLException {
      return (getRelatedArtifacts(AtsRelation.FavoriteUser_User).contains(user));
   }

   public ArrayList<User> getFavorites() throws SQLException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getRelatedArtifacts(AtsRelation.FavoriteUser_User))
         arts.add((User) art);
      return arts;
   }

   public boolean amISubscribed() {
      try {
         return isSubscribed(SkynetAuthentication.getUser());
      } catch (SQLException ex) {
         return false;
      }
   }

   public boolean amIFavorite() {
      try {
         return isFavorite(SkynetAuthentication.getUser());
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

   public String getWorldViewType() throws Exception {
      return getArtifactTypeNameSuppressException();
   }

   public String getWorldViewTitle() throws Exception {
      return getDescriptiveName();
   }

   public String getWorldViewState() throws Exception {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   public String getWorldViewActivePoc() throws Exception {
      return Artifacts.commaArts(smaMgr.getStateMgr().getAssignees());
   }

   public String getWorldViewCreatedDateStr() throws Exception {
      if (getWorldViewCreatedDate() == null) return XViewerCells.getCellExceptionString("No creation date");
      return new XDate(getWorldViewCreatedDate()).getMMDDYYHHMM();
   }

   public String getWorldViewCompletedDateStr() throws Exception {
      if (smaMgr.isCompleted()) {
         if (getWorldViewCompletedDate() == null) {
            OSEELog.logSevere(AtsPlugin.class, "Completed with no date => " + smaMgr.getSma().getHumanReadableId(),
                  true);
            return XViewerCells.getCellExceptionString("Completed with no date.");
         }
         return new XDate(getWorldViewCompletedDate()).getMMDDYYHHMM();
      }
      return "";
   }

   public String getWorldViewCancelledDateStr() throws Exception {
      if (smaMgr.isCancelled()) {
         if (getWorldViewCancelledDate() == null) {
            OSEELog.logSevere(AtsPlugin.class, "Cancelled with no date => " + smaMgr.getSma().getHumanReadableId(),
                  false);
            return XViewerCells.getCellExceptionString("Cancelled with no date.");
         }
         return new XDate(getWorldViewCancelledDate()).getMMDDYYHHMM();
      }
      return "";
   }

   public Date getWorldViewCreatedDate() throws Exception {
      return getLog().getCreationDate();
   }

   public String getWorldViewOriginator() throws Exception {
      return smaMgr.getOriginator().getName();
   }

   public String getWorldViewID() throws Exception {
      return getHumanReadableId();
   }

   public String getWorldViewLegacyPCR() throws Exception {
      if (isAttributeTypeValid(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName())) {
         return getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), "");
      }
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

   public ChangeType getWorldViewChangeType() throws SQLException, MultipleAttributesExist {
      return ChangeType.None;
   }

   public String getWorldViewChangeTypeStr() throws Exception {
      if (getWorldViewChangeType() == null || getWorldViewChangeType() == ChangeType.None)
         return "";
      else
         return getWorldViewChangeType().name();
   }

   public double getEstimatedHoursFromArtifact() throws Exception {
      if (isAttributeTypeValid(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName())) return getSoleAttributeValue(
            ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), 0.0);
      return 0;
   }

   public double getEstimatedHoursFromTasks(String relatedToState) throws Exception {
      return smaMgr.getTaskMgr().getEstimatedHours(relatedToState);
   }

   public double getEstimatedHoursFromTasks() throws Exception {
      return smaMgr.getTaskMgr().getEstimatedHours();
   }

   public double getEstimatedHoursFromReviews() throws Exception {
      return smaMgr.getReviewManager().getEstimatedHours();
   }

   public double getEstimatedHoursFromReviews(String relatedToState) throws Exception {
      return smaMgr.getReviewManager().getEstimatedHours(relatedToState);
   }

   public double getEstimatedHoursTotal(String relatedToState) throws Exception {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks(relatedToState) + getEstimatedHoursFromReviews(relatedToState);
   }

   public double getEstimatedHoursTotal() throws Exception {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks() + getEstimatedHoursFromReviews();
   }

   public double getWorldViewEstimatedHours() throws Exception {
      return getEstimatedHoursTotal();
   }

   public String getWorldViewUserCommunity() throws Exception {
      return "";
   }

   public String getWorldViewPriority() throws Exception {
      return "";
   }

   public String getWorldViewResolution() throws Exception {
      return getAttributesToString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

   public double getRemainHoursFromArtifact() throws Exception {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return 0;
      double est = getWorldViewEstimatedHours();
      if (est == 0) return getWorldViewEstimatedHours();
      double remain = est - (est * (getPercentCompleteSMATotal() / 100.0));
      return remain;
   }

   public double getRemainHoursTotal() throws Exception {
      return getRemainHoursFromArtifact() + getRemainFromTasks() + getRemainFromReviews();
   }

   public double getRemainFromTasks() throws Exception {
      return smaMgr.getTaskMgr().getRemainHours();
   }

   public double getRemainFromReviews() throws Exception {
      return smaMgr.getReviewManager().getRemainHours();
   }

   public double getWorldViewRemainHours() throws Exception {
      return getRemainHoursTotal();
   }

   public Result isWorldViewRemainHoursValid() throws Exception {
      if (!isAttributeTypeValid(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName())) return Result.TrueResult;
      try {
         Double value = getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), null);
         if (value == null) return new Result("Estimated Hours not set.");
         return Result.TrueResult;
      } catch (Exception ex) {
         return new Result(
               ex.getClass().getName() + ": " + ex.getLocalizedMessage() + "\n\n" + Lib.exceptionToString(ex));
      }
   }

   public Result isWorldViewManDaysNeededValid() throws Exception {
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
   public double getWorldViewManDaysNeeded() throws Exception {
      double hrsRemain = getWorldViewRemainHours();
      double manDaysNeeded = 0;
      if (hrsRemain != 0) manDaysNeeded = hrsRemain / getManDayHrsPreference();
      return manDaysNeeded;
   }

   public double getManDayHrsPreference() {
      return MAN_DAY_HOURS;
   }

   public double getWorldViewAnnualCostAvoidance() throws Exception {
      return 0;
   }

   public Result isWorldViewAnnualCostAvoidanceValid() throws Exception {
      if (isAttributeTypeValid(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName())) return Result.TrueResult;
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) return result;
      String value = null;
      try {
         value = getSoleAttributeValue(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName(), "");
         if (value == null || value.equals("")) return new Result("Weekly Benefit Hours not set.");
         double val = new Float(value).doubleValue();
         if (val == 0) return new Result("Weekly Benefit Hours not set.");
      } catch (NumberFormatException ex) {
         OSEELog.logException(AtsPlugin.class, "HRID " + getHumanReadableId(), ex, true);
         return new Result("Weekly Benefit value is invalid double \"" + value + "\"");
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "HRID " + getHumanReadableId(), ex, true);
         return new Result("Exception calculating cost avoidance.  See log for details.");
      }
      return Result.TrueResult;
   }

   public String getWorldViewNotes() throws Exception {
      return getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewWorkPackage() throws Exception {
      return getSoleAttributeValue(ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory() throws Exception {
      return getSoleAttributeValue(ATSAttributes.CATEGORY_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory2() throws Exception {
      return getSoleAttributeValue(ATSAttributes.CATEGORY2_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory3() throws Exception {
      return getSoleAttributeValue(ATSAttributes.CATEGORY3_ATTRIBUTE.getStoreName(), "");
   }

   public int getWorldViewStatePercentComplete() throws Exception {
      return getPercentCompleteSMAStateTotal(smaMgr.getStateMgr().getCurrentStateName());
   }

   public String getWorldViewNumberOfTasks() throws Exception {
      int num = getSmaMgr().getTaskMgr().getTaskArtifacts().size();
      if (num == 0) return "";
      return String.valueOf(num);
   }

   public String getWorldViewRelatedToState() throws Exception {
      return "";
   }

   public abstract String getWorldViewVersion() throws Exception;

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    * 
    * @return true if any object in SMA tree is dirty
    */
   public Result isSMADirty() {
      return isRelationsAndArtifactsDirty(smaRelations);
   }

   public void saveSMA() {
      try {
         updateAssigneeRelations();
         saveArtifactsFromRelations(smaRelations);
      } catch (Exception ex) {
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

   public String getWorldViewEstimatedReleaseDateStr() throws Exception {
      if (getWorldViewEstimatedReleaseDate() == null) return "";
      return new XDate(getWorldViewEstimatedReleaseDate()).getMMDDYYHHMM();
   }

   public abstract Date getWorldViewReleaseDate() throws Exception;

   public String getWorldViewReleaseDateStr() throws Exception {
      if (getWorldViewReleaseDate() == null) return "";
      return new XDate(getWorldViewReleaseDate()).getMMDDYYHHMM();
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
    * 
    * @throws Exception TODO
    */
   public void statusChanged() throws Exception {
   }

   /**
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    * 
    * @throws Exception
    */
   public void transitioned(WorkPageDefinition fromPage, WorkPageDefinition toPage, Collection<User> toAssignees, boolean persist) throws Exception {
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
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperAssignee()
    */
   public String getHyperAssignee() {
      return Artifacts.commaArts(smaMgr.getStateMgr().getAssignees());
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
   public Image getHyperAssigneeImage() throws Exception {
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
   public String getWorldViewDecision() throws Exception {
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
   public String getWorldViewValidationRequiredStr() throws Exception {
      if (isAttributeTypeValid(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName())) return String.valueOf(getSoleAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false));
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#isWorldViewDeadlineAlerting()
    */
   public Result isWorldViewDeadlineAlerting() throws Exception {
      return Result.FalseResult;
   }

   public int getWorldViewPercentRework() throws Exception {
      return 0;
   }

   public String getWorldViewPercentReworkStr() throws Exception {
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
      Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
      artTypeNames.add(TaskArtifact.ARTIFACT_NAME);
      artTypeNames.add(DecisionReviewArtifact.ARTIFACT_NAME);
      artTypeNames.add(PeerToPeerReviewArtifact.ARTIFACT_NAME);
      return artTypeNames;
   }

   public static List<Artifact> getAllSMATypeArtifacts() throws SQLException {
      List<Artifact> result = new ArrayList<Artifact>();
      for (String artType : getAllSMATypeNames()) {
         result.addAll(ArtifactQuery.getArtifactsFromType(artType, BranchPersistenceManager.getAtsBranch()));
      }
      return result;
   }

   public static List<Artifact> getAllTeamWorkflowArtifacts() throws SQLException {
      List<Artifact> result = new ArrayList<Artifact>();
      for (String artType : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         result.addAll(ArtifactQuery.getArtifactsFromType(artType, BranchPersistenceManager.getAtsBranch()));
      }
      return result;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewBranchStatus()
    */
   public String getWorldViewBranchStatus() throws Exception {
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
   public String getWorldViewReviewAuthor() throws Exception {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewDecider()
    */
   public String getWorldViewReviewDecider() throws Exception {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewModerator()
    */
   public String getWorldViewReviewModerator() throws Exception {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewReviewReviewer()
    */
   public String getWorldViewReviewReviewer() throws Exception {
      return "";
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    * 
    * @param stateName
    * @return
    */
   public double getHoursSpentSMAState(String stateName) throws Exception {
      return smaMgr.getStateMgr().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working ONLY on tasks related to stateName
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public double getHoursSpentSMAStateTasks(String stateName) throws Exception {
      return smaMgr.getTaskMgr().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working ONLY on reviews related to stateName
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public double getHoursSpentSMAStateReviews(String stateName) throws Exception {
      return smaMgr.getReviewManager().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working on all things (including children SMAs) related to stateName
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public double getHoursSpentSMAStateTotal(String stateName) throws Exception {
      return getHoursSpentSMAState(stateName) + getHoursSpentSMAStateTasks(stateName) + getHoursSpentSMAStateReviews(stateName);
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws Exception {
      return getHoursSpentSMAStateTotal(smaMgr.getStateMgr().getCurrentStateName());
   }

   /**
    * Return hours spent working on all things (including children SMAs) for this SMA
    * 
    * @return
    * @throws Exception
    */
   public double getHoursSpentSMATotal() throws Exception {
      double hours = 0.0;
      for (String stateName : smaMgr.getStateMgr().getVisitedStateNames()) {
         hours += getHoursSpentSMAStateTotal(stateName);
      }
      return hours;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    * 
    * @param stateName
    * @return
    */
   public int getPercentCompleteSMAState(String stateName) throws Exception {
      return smaMgr.getStateMgr().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public int getPercentCompleteSMAStateTasks(String stateName) throws Exception {
      return smaMgr.getTaskMgr().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete ONLY on reviews related to stateName. Total Percent / # Reviews
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public int getPercentCompleteSMAStateReviews(String stateName) throws Exception {
      return smaMgr.getReviewManager().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    * 
    * @param stateName
    * @return
    * @throws Exception
    */
   public int getPercentCompleteSMAStateTotal(String stateName) throws Exception {
      return getStateMetricsData(stateName).getResultingPercent();
   }

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/cancelled)
    * 
    * @return
    * @throws Exception
    */
   public int getPercentCompleteSMATotal() throws Exception {
      int percent = 0;
      int numStates = 0;
      for (String stateName : smaMgr.getWorkFlowDefinition().getPageNames()) {
         if (!stateName.equals(DefaultTeamState.Completed.name()) && !stateName.equals(DefaultTeamState.Cancelled.name())) {
            percent += getPercentCompleteSMAStateTotal(stateName);
            numStates++;
         }
      }
      if (numStates == 0) return 0;
      return percent / numStates;
   }

   private StateMetricsData getStateMetricsData(String stateName) throws Exception {
      // Add percent and bump objects 1 for state percent
      int percent = getPercentCompleteSMAState(stateName);
      int numObjects = 1; // the state itself is one object

      // Add percent for each task and bump objects for each task
      Collection<TaskArtifact> tasks = smaMgr.getTaskMgr().getTaskArtifacts(stateName);
      for (TaskArtifact taskArt : tasks)
         percent += taskArt.getPercentCompleteSMATotal();
      numObjects += tasks.size();

      // Add percent for each review and bump objects for each review
      Collection<ReviewSMArtifact> reviews = smaMgr.getReviewManager().getReviews(stateName);
      for (ReviewSMArtifact reviewArt : reviews)
         percent += reviewArt.getPercentCompleteSMATotal();
      numObjects += reviews.size();

      return new StateMetricsData(percent, numObjects);
   }
   private class StateMetricsData {
      public int numObjects = 0;
      public int percent = 0;

      public StateMetricsData(int percent, int numObjects) {
         this.numObjects = numObjects;
         this.percent = percent;
      }

      public int getResultingPercent() {
         return percent / numObjects;
      }

      public String toString() {
         return "Percent: " + getResultingPercent() + "  NumObjs: " + numObjects + "  Total Percent: " + percent;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentState()
    */
   @Override
   public double getWorldViewHoursSpentState() throws Exception {
      return getHoursSpentSMAState(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentStateReview()
    */
   @Override
   public double getWorldViewHoursSpentStateReview() throws Exception {
      return getHoursSpentSMAStateReviews(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentStateTask()
    */
   @Override
   public double getWorldViewHoursSpentStateTask() throws Exception {
      return getHoursSpentSMAStateTasks(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewHoursSpentTotal()
    */
   @Override
   public double getWorldViewHoursSpentTotal() throws Exception {
      return getHoursSpentSMATotal();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteState()
    */
   @Override
   public int getWorldViewPercentCompleteState() throws Exception {
      return getPercentCompleteSMAState(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteStateReview()
    */
   @Override
   public int getWorldViewPercentCompleteStateReview() throws Exception {
      return getPercentCompleteSMAStateReviews(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteStateTask()
    */
   @Override
   public int getWorldViewPercentCompleteStateTask() throws Exception {
      return getPercentCompleteSMAStateTasks(smaMgr.getStateMgr().getCurrentStateName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewPercentCompleteTotal()
    */
   @Override
   public int getWorldViewPercentCompleteTotal() throws Exception {
      return getPercentCompleteSMATotal();
   }

}
