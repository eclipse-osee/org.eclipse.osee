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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightRule;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.group.IGroupExplorerProvider;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class StateMachineArtifact extends ATSArtifact implements IGroupExplorerProvider, IWorldViewArtifact, ISubscribableArtifact, IFavoriteableArtifact {

   protected SMAManager smaMgr;
   private final Set<IRelationEnumeration> smaEditorRelations = new HashSet<IRelationEnumeration>();
   private final Set<IRelationEnumeration> atsWorldRelations = new HashSet<IRelationEnumeration>();
   private Collection<User> preSaveStateAssignees;
   private User preSaveOriginator;
   public static double DEFAULT_MAN_HOURS_PER_DAY = 8;
   protected WorkFlowDefinition workFlowDefinition;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws OseeDataStoreException
    */
   public StateMachineArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      setSoleAttributeValue(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public void onInitializationComplete() {
      super.onInitializationComplete();
      initializeSMA();
   }

   @Override
   public void reloadAttributesAndRelations() throws OseeCoreException {
      super.reloadAttributesAndRelations();
      initializeSMA();
   }

   protected void initializeSMA() {
      initalizePreSaveCache();
   }

   public void initalizePreSaveCache() {
      try {
         smaMgr = new SMAManager(this);
         preSaveStateAssignees = smaMgr.getStateMgr().getAssignees();
         if (smaMgr.getOriginator() == null) {
            preSaveOriginator = UserManager.getUser();
         } else {
            preSaveOriginator = smaMgr.getOriginator();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public boolean hasAtsWorldChildren() throws OseeCoreException {
      for (IRelationEnumeration iRelationEnumeration : atsWorldRelations) {
         if (getRelatedArtifactsCount(iRelationEnumeration) > 0) {
            return true;
         }
      }
      return false;
   }

   public String getHelpContext() {
      return "atsWorkflowEditorWorkflowTab";
   }

   public String getArtifactSuperTypeName() {
      return getArtifactTypeName();
   }

   @Override
   public Date getWorldViewDeadlineDate() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewDeadlineDateStr() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewImplementer() throws OseeCoreException {
      return Artifacts.toString("; ", getImplementers());
   }

   public Collection<User> getImplementersByState(String stateName) throws OseeCoreException {
      if (smaMgr.isCancelled()) {
         return Arrays.asList(smaMgr.getLog().getCancelledLogItem().getUser());
      }
      Collection<User> users = new HashSet<User>(smaMgr.getStateMgr().getAssignees(stateName));
      LogItem item = smaMgr.getLog().getCompletedLogItem();
      if (item != null) {
         users.add(item.getUser());
      }
      return users;
   }

   public Collection<User> getImplementers() throws OseeCoreException {
      return Collections.emptyList();
   }

   @Override
   public String getWorldViewTeam() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewGroups() throws OseeCoreException {
      return Artifacts.toString("; ", getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP));
   }

   @Override
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      return 0;
   }

   @Override
   public void onAttributePersist(SkynetTransaction transaction) throws OseeCoreException {
      // Since multiple ways exist to change the assignees, notification is performed on the persist
      if (isDeleted()) {
         return;
      }
      try {
         notifyNewAssigneesAndReset();
         notifyOriginatorAndReset();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /**
    * Override to apply different algorithm to current section expansion.
    * 
    * @param page
    * @return true if section should be expanded
    * @throws OseeCoreException
    */
   public boolean isCurrentSectionExpanded(String stateName) throws OseeCoreException {
      return smaMgr.getStateMgr().getCurrentStateName().equals(stateName);
   }

   public void notifyNewAssigneesAndReset() throws OseeCoreException {
      if (preSaveStateAssignees == null) {
         preSaveStateAssignees = smaMgr.getStateMgr().getAssignees();
         return;
      }
      Set<User> newAssignees = new HashSet<User>();
      for (User user : smaMgr.getStateMgr().getAssignees()) {
         if (!preSaveStateAssignees.contains(user)) {
            newAssignees.add(user);
         }
      }
      preSaveStateAssignees = smaMgr.getStateMgr().getAssignees();
      if (newAssignees.size() == 0) {
         return;
      }
      try {
         // These will be processed upon save
         AtsNotifyUsers.notify(this, newAssignees, AtsNotifyUsers.NotifyType.Assigned);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void notifyOriginatorAndReset() throws OseeCoreException {
      if (preSaveOriginator != null && smaMgr.getOriginator() != null && !smaMgr.getOriginator().equals(
            preSaveOriginator)) {
         AtsNotifyUsers.notify(this, AtsNotifyUsers.NotifyType.Originator);
      }
      preSaveOriginator = smaMgr.getOriginator();
   }

   public boolean isValidationRequired() throws OseeCoreException {
      return false;
   }

   public abstract Set<User> getPrivilegedUsers() throws OseeCoreException;

   public String getDescription() {
      return "";
   }

   public ArrayList<EmailGroup> getEmailableGroups() throws OseeCoreException {
      ArrayList<EmailGroup> groupNames = new ArrayList<EmailGroup>();
      ArrayList<String> emails = new ArrayList<String>();
      emails.add(smaMgr.getOriginator().getEmail());
      groupNames.add(new EmailGroup("Originator", emails));
      if (smaMgr.getStateMgr().getAssignees().size() > 0) {
         emails = new ArrayList<String>();
         for (User u : smaMgr.getStateMgr().getAssignees()) {
            emails.add(u.getEmail());
         }
         groupNames.add(new EmailGroup("Assignees", emails));
      }
      return groupNames;
   }

   public abstract StateMachineArtifact getParentSMA() throws OseeCoreException;

   public abstract ActionArtifact getParentActionArtifact() throws OseeCoreException;

   public abstract TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException;

   public String getPreviewHtml() throws OseeCoreException {
      return getPreviewHtml(PreviewStyle.NONE);
   }

   public String getPreviewHtml(PreviewStyle... styles) throws OseeCoreException {
      Overview o = new Overview();
      o.addHeader(this, styles);
      o.addFooter(this, styles);
      return o.getPage();
   }

   public boolean isUnCancellable() {
      try {
         LogItem item = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
         if (item == null) {
            throw new IllegalArgumentException("No Cancelled Event");
         }
         for (WorkPageDefinition toWorkPageDefinition : smaMgr.getWorkFlowDefinition().getToPages(
               smaMgr.getWorkPageDefinition())) {
            if (toWorkPageDefinition.getPageName().equals(item.getState())) {
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   public boolean isTaskable() throws OseeCoreException {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) {
         return false;
      }
      return true;
   }

   public boolean showTaskTab() throws OseeCoreException {
      return isTaskable();
   }

   public String getEditorTitle() throws OseeCoreException {
      return getWorldViewType() + ": " + getName();
   }

   public String getWorldViewActionableItems() throws OseeCoreException {
      return "";
   }

   /**
    * Registers relation as part of the SMAEditor isDirty/save tree
    * 
    * @param side
    */
   public void registerSMAEditorRelation(AtsRelation side) {
      smaEditorRelations.add(side);
   }

   /**
    * Registers relation as part of the parent/child hierarchy in ATS World
    * 
    * @param side
    */
   public void registerAtsWorldRelation(AtsRelation side) {
      atsWorldRelations.add(side);
   }

   public Image getAssigneeImage() throws OseeCoreException {
      if (isDeleted()) {
         return null;
      }
      return FrameworkArtifactImageProvider.getUserImage(smaMgr.getStateMgr().getAssignees());
   }

   /**
    * @return WorkFlowDefinition
    */
   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException {
      if (workFlowDefinition == null) {
         try {
            workFlowDefinition = WorkFlowDefinitionFactory.getWorkFlowDefinition(this);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return workFlowDefinition;
   }

   public void addSubscribed(User user, SkynetTransaction transaction) throws OseeCoreException {
      if (!getRelatedArtifacts(AtsRelation.SubscribedUser_User).contains(user)) {
         addRelation(AtsRelation.SubscribedUser_User, user);
         persist(transaction);
      }

   }

   public void removeSubscribed(User user, SkynetTransaction transaction) throws OseeCoreException {
      deleteRelation(AtsRelation.SubscribedUser_User, user);
      persist(transaction);
   }

   public boolean isSubscribed(User user) throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.SubscribedUser_User).contains(user);
   }

   public ArrayList<User> getSubscribed() throws OseeCoreException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getRelatedArtifacts(AtsRelation.SubscribedUser_User)) {
         arts.add((User) art);
      }
      return arts;
   }

   public void addFavorite(User user, SkynetTransaction transaction) throws OseeCoreException {
      if (!getRelatedArtifacts(AtsRelation.FavoriteUser_User).contains(user)) {
         addRelation(AtsRelation.FavoriteUser_User, user);
         persist(transaction);
      }
   }

   public void removeFavorite(User user, SkynetTransaction transaction) throws OseeCoreException {
      deleteRelation(AtsRelation.FavoriteUser_User, user);
      persist(transaction);
   }

   public boolean isFavorite(User user) throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.FavoriteUser_User).contains(user);
   }

   public ArrayList<User> getFavorites() throws OseeCoreException {
      ArrayList<User> arts = new ArrayList<User>();
      for (Artifact art : getRelatedArtifacts(AtsRelation.FavoriteUser_User)) {
         arts.add((User) art);
      }
      return arts;
   }

   public boolean amISubscribed() {
      try {
         return isSubscribed(UserManager.getUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   public boolean amIFavorite() {
      try {
         return isFavorite(UserManager.getUser());
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      SMAEditor.close(Collections.singleton(this), true);
      super.atsDelete(deleteArts, allRelated);
   }

   public String getWorldViewType() throws OseeCoreException {
      return getArtifactTypeName();
   }

   public String getWorldViewTitle() throws OseeCoreException {
      return getName();
   }

   public String getWorldViewState() throws OseeCoreException {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   public String getWorldViewActivePoc() throws OseeCoreException {
      if (smaMgr.isCancelledOrCompleted()) {
         if (smaMgr.getSma().getImplementers().size() > 0) {
            return "(" + Artifacts.toString("; ", smaMgr.getSma().getImplementers()) + ")";
         }
         return "";
      }
      return Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees());
   }

   public String getWorldViewCreatedDateStr() throws OseeCoreException {
      if (getWorldViewCreatedDate() == null) {
         return XViewerCells.getCellExceptionString("No creation date");
      }
      return new XDate(getWorldViewCreatedDate()).getMMDDYYHHMM();
   }

   public String getWorldViewCompletedDateStr() throws OseeCoreException {
      if (smaMgr.isCompleted()) {
         if (getWorldViewCompletedDate() == null) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                  "Completed with no date => " + smaMgr.getSma().getHumanReadableId());
            return XViewerCells.getCellExceptionString("Completed with no date.");
         }
         return new XDate(getWorldViewCompletedDate()).getMMDDYYHHMM();
      }
      return "";
   }

   public String getWorldViewCancelledDateStr() throws OseeCoreException {
      if (smaMgr.isCancelled()) {
         if (getWorldViewCancelledDate() == null) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE,
                  "Cancelled with no date => " + smaMgr.getSma().getHumanReadableId());
            return XViewerCells.getCellExceptionString("Cancelled with no date.");
         }
         return new XDate(getWorldViewCancelledDate()).getMMDDYYHHMM();
      }
      return "";
   }

   public Date getWorldViewCreatedDate() throws OseeCoreException {
      return smaMgr.getLog().getCreationDate();
   }

   public String getWorldViewOriginator() throws OseeCoreException {
      return smaMgr.getOriginator().getName();
   }

   public String getWorldViewID() throws OseeCoreException {
      return getHumanReadableId();
   }

   public String getWorldViewLegacyPCR() throws OseeCoreException {
      if (isAttributeTypeValid(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName())) {
         return getSoleAttributeValue(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), "");
      }
      return "";
   }

   public Date getWorldViewCompletedDate() throws OseeCoreException {
      LogItem item = smaMgr.getLog().getCompletedLogItem();
      if (item != null) {
         return item.getDate();
      }
      return null;
   }

   public Date getWorldViewCancelledDate() throws OseeCoreException {
      LogItem item = smaMgr.getLog().getCancelledLogItem();
      if (item != null) {
         return item.getDate();
      }
      return null;
   }

   public abstract VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException;

   public ChangeType getWorldViewChangeType() throws OseeCoreException {
      return ChangeType.None;
   }

   public String getWorldViewChangeTypeStr() throws OseeCoreException {
      if (getWorldViewChangeType() == null || getWorldViewChangeType() == ChangeType.None) {
         return "";
      } else {
         return getWorldViewChangeType().name();
      }
   }

   public double getEstimatedHoursFromArtifact() throws OseeCoreException {
      if (isAttributeTypeValid(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName())) {
         return getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), 0.0);
      }
      return 0;
   }

   public double getEstimatedHoursFromTasks(String relatedToState) throws OseeCoreException {
      return smaMgr.getTaskMgr().getEstimatedHours(relatedToState);
   }

   public double getEstimatedHoursFromTasks() throws OseeCoreException {
      return smaMgr.getTaskMgr().getEstimatedHours();
   }

   public double getEstimatedHoursFromReviews() throws OseeCoreException {
      return smaMgr.getReviewManager().getEstimatedHours();
   }

   public double getEstimatedHoursFromReviews(String relatedToState) throws OseeCoreException {
      return smaMgr.getReviewManager().getEstimatedHours(relatedToState);
   }

   public double getEstimatedHoursTotal(String relatedToState) throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks(relatedToState) + getEstimatedHoursFromReviews(relatedToState);
   }

   public double getEstimatedHoursTotal() throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks() + getEstimatedHoursFromReviews();
   }

   public double getWorldViewEstimatedHours() throws OseeCoreException {
      return getEstimatedHoursTotal();
   }

   public String getWorldViewUserCommunity() throws OseeCoreException {
      return "";
   }

   public String getWorldViewPriority() throws OseeCoreException {
      return "";
   }

   public String getWorldViewResolution() throws OseeCoreException {
      return getAttributesToString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

   public double getRemainHoursFromArtifact() throws OseeCoreException {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) {
         return 0;
      }
      double est = getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), 0.0);
      if (est == 0) {
         return getEstimatedHoursFromArtifact();
      }
      double remain = est - est * getPercentCompleteSMATotal() / 100.0;
      return remain;
   }

   public double getRemainHoursTotal() throws OseeCoreException {
      return getRemainHoursFromArtifact() + getRemainFromTasks() + getRemainFromReviews();
   }

   public double getRemainFromTasks() throws OseeCoreException {
      return smaMgr.getTaskMgr().getRemainHours();
   }

   public double getRemainFromReviews() throws OseeCoreException {
      return smaMgr.getReviewManager().getRemainHours();
   }

   @Override
   public double getWorldViewRemainHours() throws OseeCoreException {
      return getRemainHoursTotal();
   }

   public Result isWorldViewRemainHoursValid() throws OseeCoreException {
      if (!isAttributeTypeValid(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName())) {
         return Result.TrueResult;
      }
      try {
         Double value = getSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), null);
         if (getSmaMgr().isCancelled()) {
            return Result.TrueResult;
         }
         if (value == null) {
            return new Result("Estimated Hours not set.");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         return new Result(
               ex.getClass().getName() + ": " + ex.getLocalizedMessage() + "\n\n" + Lib.exceptionToString(ex));
      }
   }

   public Result isWorldViewManDaysNeededValid() throws OseeCoreException {
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) {
         return result;
      }
      if (getManHrsPerDayPreference() == 0) {
         return new Result("Man Day Hours Preference is not set.");
      }

      return Result.TrueResult;
   }

   public double getWorldViewManDaysNeeded() throws OseeCoreException {
      double hrsRemain = getWorldViewRemainHours();
      double manDaysNeeded = 0;
      if (hrsRemain != 0) {
         manDaysNeeded = hrsRemain / getManHrsPerDayPreference();
      }
      return manDaysNeeded;
   }

   public double getManHrsPerDayPreference() throws OseeCoreException {
      return DEFAULT_MAN_HOURS_PER_DAY;
   }

   public double getWorldViewAnnualCostAvoidance() throws OseeCoreException {
      return 0;
   }

   public Result isWorldViewAnnualCostAvoidanceValid() throws OseeCoreException {
      if (isAttributeTypeValid(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName())) {
         return Result.TrueResult;
      }
      Result result = isWorldViewRemainHoursValid();
      if (result.isFalse()) {
         return result;
      }
      String value = null;
      try {
         value = getSoleAttributeValue(ATSAttributes.WEEKLY_BENEFIT_ATTRIBUTE.getStoreName(), "");
         if (value == null || value.equals("")) {
            return new Result("Weekly Benefit Hours not set.");
         }
         double val = new Float(value).doubleValue();
         if (val == 0) {
            return new Result("Weekly Benefit Hours not set.");
         }
      } catch (NumberFormatException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "HRID " + getHumanReadableId(), ex);
         return new Result("Weekly Benefit value is invalid double \"" + value + "\"");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "HRID " + getHumanReadableId(), ex);
         return new Result("Exception calculating cost avoidance.  See log for details.");
      }
      return Result.TrueResult;
   }

   public String getWorldViewNotes() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public String getWorldViewWorkPackage() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.WORK_PACKAGE_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.CATEGORY_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory2() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.CATEGORY2_ATTRIBUTE.getStoreName(), "");
   }

   public String getWorldViewCategory3() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.CATEGORY3_ATTRIBUTE.getStoreName(), "");
   }

   public int getWorldViewStatePercentComplete() throws OseeCoreException {
      return getPercentCompleteSMAStateTotal(smaMgr.getStateMgr().getCurrentStateName());
   }

   public String getWorldViewNumberOfTasks() throws OseeCoreException {
      int num = getSmaMgr().getTaskMgr().getTaskArtifacts().size();
      if (num == 0) {
         return "";
      }
      return String.valueOf(num);
   }

   public String getWorldViewRelatedToState() throws OseeCoreException {
      return "";
   }

   @Override
   public String getWorldViewTargetedVersionStr() throws OseeCoreException {
      if (getWorldViewTargetedVersion() == null) {
         return "";
      }
      return getWorldViewTargetedVersion().toString();
   }

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    * 
    * @return true if any object in SMA tree is dirty
    */
   public Result isSMAEditorDirty() {
      String resultText = isRelationsAndArtifactsDirty(smaEditorRelations);
      if (resultText == null) {
         return Result.FalseResult;
      }
      return new Result(true, resultText);
   }

   public void saveSMA(SkynetTransaction transaction) {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            artifact.persist(transaction);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't save artifact " + getHumanReadableId(), ex);
      }
   }

   public void revertSMA() {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            artifact.reloadAttributesAndRelations();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't revert artifact " + getHumanReadableId(), ex);
      }
   }

   public static void getSmaArtifactsOneLevel(StateMachineArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      artifacts.add(smaArtifact);
      for (IRelationEnumeration side : smaArtifact.getSmaEditorRelations()) {
         for (Artifact artifact : smaArtifact.getRelatedArtifacts(side)) {
            artifacts.add(artifact);
         }
      }
   }

   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
      Date parentDate = null;
      if (getParentSMA() != null) {
         parentDate = getParentSMA().getWorldViewEstimatedReleaseDate();
      }
      if (date == null && parentDate != null) {
         return parentDate;
      }
      return date;
   }

   @Override
   public Date getWorldViewEstimatedCompletionDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(ATSAttributes.ESTIMATED_COMPLETION_DATE_ATTRIBUTE.getStoreName(), null);
      Date parentDate = null;
      if (getParentSMA() != null) {
         parentDate = getParentSMA().getWorldViewEstimatedReleaseDate();
      }
      if (date == null && parentDate != null) {
         return parentDate;
      }
      return date;
   }

   public String getWorldViewEstimatedReleaseDateStr() throws OseeCoreException {
      if (getWorldViewEstimatedReleaseDate() == null) {
         return "";
      }
      return new XDate(getWorldViewEstimatedReleaseDate()).getMMDDYYHHMM();
   }

   public String getWorldViewEstimatedCompletionDateStr() throws OseeCoreException {
      if (getWorldViewEstimatedCompletionDate() == null) {
         return "";
      }
      return new XDate(getWorldViewEstimatedCompletionDate()).getMMDDYYHHMM();
   }

   public abstract Date getWorldViewReleaseDate() throws OseeCoreException;

   public String getWorldViewReleaseDateStr() throws OseeCoreException {
      if (getWorldViewReleaseDate() == null) {
         return "";
      }
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
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    * 
    * @throws Exception
    */
   public void transitioned(WorkPageDefinition fromPage, WorkPageDefinition toPage, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
   }

   public String getHyperName() {
      return getName();
   }

   public String getHyperType() {
      try {
         return getArtifactTypeName();
      } catch (Exception ex) {
         return ex.getLocalizedMessage();
      }
   }

   public String getHyperState() {
      try {
         return smaMgr.getStateMgr().getCurrentStateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   public String getHyperAssignee() {
      try {
         return Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   public Image getHyperAssigneeImage() throws OseeCoreException {
      return smaMgr.getAssigneeImage();
   }

   public Artifact getHyperArtifact() {
      return this;
   }

   public String getWorldViewDecision() throws OseeCoreException {
      return "";
   }

   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentSMA();
   }

   public String getWorldViewValidationRequiredStr() throws OseeCoreException {
      if (isAttributeTypeValid(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName())) {
         return String.valueOf(getSoleAttributeValue(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), false));
      }
      return "";
   }

   public Result isWorldViewDeadlineAlerting() throws OseeCoreException {
      return Result.FalseResult;
   }

   public int getWorldViewPercentRework() throws OseeCoreException {
      return 0;
   }

   public String getWorldViewPercentReworkStr() throws OseeCoreException {
      int reWork = getWorldViewPercentRework();
      if (reWork == 0) {
         return "";
      }
      return String.valueOf(reWork);
   }

   public static Set<String> getAllSMATypeNames() throws OseeCoreException {
      Set<String> artTypeNames = TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames();
      artTypeNames.add(TaskArtifact.ARTIFACT_NAME);
      artTypeNames.add(DecisionReviewArtifact.ARTIFACT_NAME);
      artTypeNames.add(PeerToPeerReviewArtifact.ARTIFACT_NAME);
      return artTypeNames;
   }

   public static List<Artifact> getAllSMATypeArtifacts() throws OseeCoreException {
      List<Artifact> result = new ArrayList<Artifact>();
      for (String artType : getAllSMATypeNames()) {
         result.addAll(ArtifactQuery.getArtifactListFromType(artType, AtsUtil.getAtsBranch()));
      }
      return result;
   }

   public static List<TeamWorkFlowArtifact> getAllTeamWorkflowArtifacts() throws OseeCoreException {
      List<TeamWorkFlowArtifact> result = new ArrayList<TeamWorkFlowArtifact>();
      for (String artType : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         List<TeamWorkFlowArtifact> teamArts =
               org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactQuery.getArtifactListFromType(
                     artType, AtsUtil.getAtsBranch()));
         result.addAll(teamArts);
      }
      return result;
   }

   public String getWorldViewBranchStatus() throws OseeCoreException {
      return "";
   }

   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   public String getWorldViewReviewAuthor() throws OseeCoreException {
      return "";
   }

   public String getWorldViewReviewDecider() throws OseeCoreException {
      return "";
   }

   public String getWorldViewReviewModerator() throws OseeCoreException {
      return "";
   }

   public String getWorldViewReviewReviewer() throws OseeCoreException {
      return "";
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    * 
    * @param stateName
    * @return hours
    */
   public double getHoursSpentSMAState(String stateName) throws OseeCoreException {
      return smaMgr.getStateMgr().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working ONLY on tasks related to stateName
    * 
    * @param stateName
    * @return hours
    * @throws Exception
    */
   public double getHoursSpentSMAStateTasks(String stateName) throws OseeCoreException {
      return smaMgr.getTaskMgr().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working ONLY on reviews related to stateName
    * 
    * @param stateName
    * @return hours
    * @throws Exception
    */
   public double getHoursSpentSMAStateReviews(String stateName) throws OseeCoreException {
      return smaMgr.getReviewManager().getHoursSpent(stateName);
   }

   /**
    * Return hours spent working on all things (including children SMAs) related to stateName
    * 
    * @param stateName
    * @return hours
    * @throws Exception
    */
   public double getHoursSpentSMAStateTotal(String stateName) throws OseeCoreException {
      return getHoursSpentSMAState(stateName) + getHoursSpentSMAStateTasks(stateName) + getHoursSpentSMAStateReviews(stateName);
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException {
      return getHoursSpentSMAStateTotal(smaMgr.getStateMgr().getCurrentStateName());
   }

   /**
    * Return hours spent working on all things (including children SMAs) for this SMA
    * 
    * @return hours
    * @throws Exception
    */
   public double getHoursSpentSMATotal() throws OseeCoreException {
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
    * @return percent
    */
   public int getPercentCompleteSMAState(String stateName) throws OseeCoreException {
      return smaMgr.getStateMgr().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    * 
    * @param stateName
    * @return percent
    * @throws Exception
    */
   public int getPercentCompleteSMAStateTasks(String stateName) throws OseeCoreException {
      return smaMgr.getTaskMgr().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete ONLY on reviews related to stateName. Total Percent / # Reviews
    * 
    * @param stateName
    * @return percent
    * @throws Exception
    */
   public int getPercentCompleteSMAStateReviews(String stateName) throws OseeCoreException {
      return smaMgr.getReviewManager().getPercentComplete(stateName);
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    * 
    * @param stateName
    * @return percent
    * @throws Exception
    */
   public int getPercentCompleteSMAStateTotal(String stateName) throws OseeCoreException {
      return getStateMetricsData(stateName).getResultingPercent();
   }

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/cancelled)
    * 
    * @return percent
    * @throws Exception
    */
   public int getPercentCompleteSMATotal() throws OseeCoreException {
      if (smaMgr.isCancelledOrCompleted()) {
         return 100;
      }
      Map<String, Double> stateToWeightMap = getStatePercentCompleteWeight();
      if (stateToWeightMap.size() > 0) {
         // Calculate total percent using configured weighting
         int percent = 0;
         for (String stateName : smaMgr.getWorkFlowDefinition().getPageNames()) {
            if (!stateName.equals(DefaultTeamState.Completed.name()) && !stateName.equals(DefaultTeamState.Cancelled.name())) {
               Double weight = stateToWeightMap.get(stateName);
               if (weight == null) {
                  weight = 0.0;
               }
               percent += weight * getPercentCompleteSMAStateTotal(stateName);
            }
         }
         return percent;
      } else {
         int percent = 0;
         int numStates = 0;
         for (String stateName : smaMgr.getWorkFlowDefinition().getPageNames()) {
            if (!stateName.equals(DefaultTeamState.Completed.name()) && !stateName.equals(DefaultTeamState.Cancelled.name())) {
               percent += getPercentCompleteSMAStateTotal(stateName);
               numStates++;
            }
         }
         if (numStates == 0) {
            return 0;
         }
         return percent / numStates;
      }
   }

   // Cache stateToWeight mapping
   private Map<String, Double> stateToWeight = null;

   public Map<String, Double> getStatePercentCompleteWeight() throws OseeCoreException {
      if (stateToWeight == null) {
         stateToWeight = new HashMap<String, Double>();
         Collection<WorkRuleDefinition> workRuleDefs =
               smaMgr.getWorkRulesStartsWith(AtsStatePercentCompleteWeightRule.ID);
         // Log error if multiple of same rule found, but keep going
         if (workRuleDefs.size() > 1) {
            OseeLog.log(
                  AtsPlugin.class,
                  Level.SEVERE,
                  "Team Definition has multiple rules of type " + AtsStatePercentCompleteWeightRule.ID + ".  Only 1 allowed.  Defaulting to first found.");
         }
         if (workRuleDefs.size() == 1) {
            stateToWeight = AtsStatePercentCompleteWeightRule.getStateWeightMap(workRuleDefs.iterator().next());
         }
      }
      return stateToWeight;
   }

   private StateMetricsData getStateMetricsData(String stateName) throws OseeCoreException {
      // Add percent and bump objects 1 for state percent
      int percent = getPercentCompleteSMAState(stateName);
      int numObjects = 1; // the state itself is one object

      // Add percent for each task and bump objects for each task
      Collection<TaskArtifact> tasks = smaMgr.getTaskMgr().getTaskArtifacts(stateName);
      for (TaskArtifact taskArt : tasks) {
         percent += taskArt.getPercentCompleteSMATotal();
      }
      numObjects += tasks.size();

      // Add percent for each review and bump objects for each review
      Collection<ReviewSMArtifact> reviews = smaMgr.getReviewManager().getReviews(stateName);
      for (ReviewSMArtifact reviewArt : reviews) {
         percent += reviewArt.getPercentCompleteSMATotal();
      }
      numObjects += reviews.size();

      return new StateMetricsData(percent, numObjects);
   }

   private static class StateMetricsData {
      public int numObjects = 0;
      public int percent = 0;

      public StateMetricsData(int percent, int numObjects) {
         this.numObjects = numObjects;
         this.percent = percent;
      }

      public int getResultingPercent() {
         return percent / numObjects;
      }

      @Override
      public String toString() {
         return "Percent: " + getResultingPercent() + "  NumObjs: " + numObjects + "  Total Percent: " + percent;
      }
   }

   @Override
   public double getWorldViewHoursSpentState() throws OseeCoreException {
      return getHoursSpentSMAState(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public double getWorldViewHoursSpentStateReview() throws OseeCoreException {
      return getHoursSpentSMAStateReviews(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public double getWorldViewHoursSpentStateTask() throws OseeCoreException {
      return getHoursSpentSMAStateTasks(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public double getWorldViewHoursSpentTotal() throws OseeCoreException {
      return getHoursSpentSMATotal();
   }

   @Override
   public int getWorldViewPercentCompleteState() throws OseeCoreException {
      return getPercentCompleteSMAState(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException {
      return getPercentCompleteSMAStateReviews(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException {
      return getPercentCompleteSMAStateTasks(smaMgr.getStateMgr().getCurrentStateName());
   }

   @Override
   public int getWorldViewPercentCompleteTotal() throws OseeCoreException {
      return getPercentCompleteSMATotal();
   }

   /**
    * @return the smaRelations
    */
   public Set<IRelationEnumeration> getSmaEditorRelations() {
      return smaEditorRelations;
   }

   public Set<IRelationEnumeration> getAtsWorldRelations() {
      return atsWorldRelations;
   }

   public String getWorldViewLastUpdated() throws OseeCoreException {
      return XDate.getDateStr(getLastModified(), XDate.MMDDYYHHMM);
   }

   public String getWorldViewLastStatused() throws OseeCoreException {
      return XDate.getDateStr(smaMgr.getLog().getLastStatusedDate(), XDate.MMDDYYHHMM);
   }

   public String getWorldViewSWEnhancement() throws OseeCoreException {
      return "";
   }

   public String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException {
      return "";
   }

   public String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException {
      return "";
   }

   public String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException {
      return "";
   }

   public String getWorldViewActionsIntiatingWorkflow() throws OseeCoreException {
      return getParentActionArtifact().getWorldViewActionsIntiatingWorkflow();
   }

   @Override
   public String getWorldViewDaysInCurrentState() throws OseeCoreException {
      double timeInCurrState = smaMgr.getStateMgr().getTimeInState();
      if (timeInCurrState == 0) {
         return "0.0";
      }
      return AtsUtil.doubleToI18nString(timeInCurrState / XDate.MILLISECONDS_IN_A_DAY);
   }

   @Override
   public String getWorldViewParentState() throws OseeCoreException {
      if (getParentSMA() != null) {
         return getParentSMA().getSmaMgr().getStateMgr().getCurrentStateName();
      }
      return "";
   }

   public String getGroupExplorerName() throws OseeCoreException {
      return String.format("[%s] %s", getSmaMgr().getStateMgr().getCurrentStateName(), getName());
   }

   @Override
   public String getWorldViewOriginatingWorkflowStr() throws OseeCoreException {
      return getParentActionArtifact().getWorldViewOriginatingWorkflowStr();
   }

   @Override
   public Collection<TeamWorkFlowArtifact> getWorldViewOriginatingWorkflows() throws OseeCoreException {
      return getParentActionArtifact().getWorldViewOriginatingWorkflows();
   }

}
