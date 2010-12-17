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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.log.ArtifactLog;
import org.eclipse.osee.ats.artifact.log.AtsLog;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.artifact.note.ArtifactNote;
import org.eclipse.osee.ats.artifact.note.AtsNote;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.notify.AtsNotification;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.SimpleTeamState;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.ats.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.TransitionManager;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.HasCmAccessControl;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.HelpContext;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.group.IGroupExplorerProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkflowArtifact extends AbstractAtsArtifact implements HasCmAccessControl, IGroupExplorerProvider, IWorldViewArtifact {

   private final Set<IRelationEnumeration> atsWorldRelations = new HashSet<IRelationEnumeration>();
   private Collection<User> transitionAssignees;
   protected WorkDefinition workDefinition;
   protected AbstractWorkflowArtifact parentSma;
   protected TeamWorkFlowArtifact parentTeamArt;
   protected ActionArtifact parentAction;
   private StateManager stateMgr;
   private AtsLog atsLog;
   private AtsNote atsNote;
   private boolean inTransition = false;
   private boolean targetedErrorLogged = false;

   public AbstractWorkflowArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      if (getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, null) == null) {
         if (getSoleAttributeValue(AtsAttributeTypes.CurrentState, null) == null) {
            setSoleAttributeValue(AtsAttributeTypes.CurrentState, "");
         }
         if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
            setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, WorkPageType.Working.name());
         }
      }
   }

   @Override
   public void onInitializationComplete() throws OseeCoreException {
      super.onInitializationComplete();
      initializeSMA();
   }

   @Override
   public void reloadAttributesAndRelations() throws OseeCoreException {
      super.reloadAttributesAndRelations();
      initializeSMA();
   }

   @SuppressWarnings("unused")
   protected void initializeSMA() throws OseeCoreException {
      initalizePreSaveCache();
   }

   public void initalizePreSaveCache() {
      try {
         stateMgr = new StateManager(this);
         atsLog = new AtsLog(new ArtifactLog(this));
         atsNote = new AtsNote(new ArtifactNote(this));
         AtsNotification.notifyNewAssigneesAndReset(this, true);
         AtsNotification.notifyOriginatorAndReset(this, true);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public void initializeNewStateMachine(Collection<User> assignees, Date createdDate, User createdBy) throws OseeCoreException {
      StateDefinition startState = getWorkDefinition().getStartState();
      initializeNewStateMachine(startState, assignees, createdDate, createdBy);
   }

   public void initializeNewStateMachine(IWorkPage state, Collection<User> assignees, Date createdDate, User createdBy) throws OseeCoreException {
      getStateMgr().initializeStateMachine(state, assignees);
      setCreatedBy(createdBy, true, createdDate);
      (new TransitionManager(this)).logStateStartedEvent(state, createdDate, createdBy);
   }

   public boolean isTargetedVersionable() throws OseeCoreException {
      if (!isTeamWorkflow()) {
         return false;
      }
      return ((TeamWorkFlowArtifact) this).getTeamDefinition().getTeamDefinitionHoldingVersions() != null && ((TeamWorkFlowArtifact) this).getTeamDefinition().getTeamDefinitionHoldingVersions().isTeamUsesVersions();
   }

   public HelpContext getHelpContext() {
      return AtsHelpContext.WORKFLOW_EDITOR__WORKFLOW_TAB;
   }

   public String getArtifactSuperTypeName() {
      return getArtifactTypeName();
   }

   @SuppressWarnings("unused")
   public Collection<User> getImplementers() throws OseeCoreException {
      return Collections.emptyList();
   }

   @SuppressWarnings("unused")
   public double getWorldViewWeeklyBenefit() throws OseeCoreException {
      return 0;
   }

   @Override
   public void onAttributePersist(SkynetTransaction transaction) {
      // Since multiple ways exist to change the assignees, notification is performed on the persist
      if (isDeleted()) {
         return;
      }
      try {
         AtsNotification.notifyNewAssigneesAndReset(this, false);
         AtsNotification.notifyOriginatorAndReset(this, false);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @SuppressWarnings("unused")
   public boolean isValidationRequired() throws OseeCoreException {
      return false;
   }

   @Override
   public String getDescription() {
      return "";
   }

   @SuppressWarnings("unused")
   public AbstractWorkflowArtifact getParentSMA() throws OseeCoreException {
      return parentSma;
   }

   @SuppressWarnings("unused")
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      return parentAction;
   }

   @SuppressWarnings("unused")
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return parentTeamArt;
   }

   @SuppressWarnings("unused")
   public String getEditorTitle() throws OseeCoreException {
      return getType() + ": " + getName();
   }

   /**
    * Registers relation as part of the parent/child hierarchy in ATS World
    */
   public void registerAtsWorldRelation(AtsRelationTypes side) {
      atsWorldRelations.add(side);
   }

   @Override
   public Image getAssigneeImage() throws OseeCoreException {
      if (isDeleted()) {
         return null;
      }
      return FrameworkArtifactImageProvider.getUserImage(getStateMgr().getAssignees());
   }

   public void clearCaches() {
      workDefinition = null;
      implementersStr = null;
      stateToWeight = null;
   }

   public WorkDefinition getWorkDefinition() {
      if (workDefinition == null) {
         try {
            workDefinition = WorkDefinitionFactory.getWorkDefinition(this);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return workDefinition;

   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      SMAEditor.close(Collections.singleton(this), true);
      super.atsDelete(deleteArts, allRelated);
   }

   public String getType() {
      return getArtifactTypeName();
   }

   public String getCurrentStateName() {
      return getStateMgr().getCurrentStateName();
   }

   public boolean isInState(IWorkPage state) {
      return getStateMgr().isInState(state);
   }

   public String implementersStr = null;

   @Override
   public String getAssigneeStr() throws OseeCoreException {
      if (isCompletedOrCancelled()) {
         if (implementersStr == null && !getImplementers().isEmpty()) {
            implementersStr = "(" + Artifacts.toString("; ", getImplementers()) + ")";
         }
         return implementersStr;
      }
      return Artifacts.toString("; ", getStateMgr().getAssignees());
   }

   public double getEstimatedHoursFromArtifact() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.EstimatedHours)) {
         return getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0);
      }
      return 0;
   }

   public double getEstimatedHoursFromTasks(IWorkPage relatedToState) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getEstimatedHoursFromTasks(relatedToState);
   }

   public double getEstimatedHoursFromTasks() throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getEstimatedHoursFromTasks();
   }

   public double getEstimatedHoursFromReviews() throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getEstimatedHours((TeamWorkFlowArtifact) this);
      }
      return 0;
   }

   public double getEstimatedHoursFromReviews(IWorkPage relatedToState) throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getEstimatedHours((TeamWorkFlowArtifact) this, relatedToState);
      }
      return 0;
   }

   public double getEstimatedHoursTotal(IWorkPage relatedToState) throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks(relatedToState) + getEstimatedHoursFromReviews(relatedToState);
   }

   public double getEstimatedHoursTotal() throws OseeCoreException {
      return getEstimatedHoursFromArtifact() + getEstimatedHoursFromTasks() + getEstimatedHoursFromReviews();
   }

   public double getRemainHoursFromArtifact() throws OseeCoreException {
      if (isCompleted() || isCancelled()) {
         return 0;
      }
      double est = getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0);
      if (est == 0) {
         return getEstimatedHoursFromArtifact();
      }
      return est - est * getPercentCompleteSMATotal() / 100.0;
   }

   public double getRemainHoursTotal() throws OseeCoreException {
      return getRemainHoursFromArtifact() + getRemainFromTasks() + getRemainFromReviews();
   }

   public double getRemainFromTasks() throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getRemainHoursFromTasks();
   }

   public double getRemainFromReviews() throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getRemainHours((TeamWorkFlowArtifact) this);
      }
      return 0;
   }

   @SuppressWarnings("unused")
   public double getManHrsPerDayPreference() throws OseeCoreException {
      return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
   }

   public int getWorldViewStatePercentComplete() throws OseeCoreException {
      return getPercentCompleteSMAStateTotal(getStateMgr().getCurrentState());
   }

   /**
    * Return true if this artifact, it's ATS relations and any of the other side artifacts are dirty
    * 
    * @return true if any object in SMA tree is dirty
    */
   public Result isSMAEditorDirty() {
      try {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         getSmaArtifactsOneLevel(this, artifacts);
         for (Artifact artifact : artifacts) {
            if (artifact.isDirty()) {
               String rString = null;
               for (Attribute<?> attribute : artifact.internalGetAttributes()) {
                  if (attribute.isDirty()) {
                     rString = "Attribute: " + attribute.getNameValueDescription();
                     break;
                  }
               }

               if (rString == null) {
                  rString = RelationManager.reportHasDirtyLinks(artifact);
               }
               return new Result(true, String.format("Artifact [%s][%s] is dirty\n\n%s", artifact.getHumanReadableId(),
                  artifact, rString));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't save artifact " + getHumanReadableId(), ex);
      }
      return Result.FalseResult;
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

   @SuppressWarnings("unused")
   public void getSmaArtifactsOneLevel(AbstractWorkflowArtifact smaArtifact, Set<Artifact> artifacts) throws OseeCoreException {
      artifacts.add(smaArtifact);
   }

   /**
    * Called at the end of a transition just before transaction manager persist. SMAs can override to perform tasks due
    * to transition.
    */
   @SuppressWarnings("unused")
   public void transitioned(StateDefinition fromState, StateDefinition toState, Collection<User> toAssignees, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      // provided for subclass implementation
   }

   @Override
   public Artifact getParentAtsArtifact() throws OseeCoreException {
      return getParentSMA();
   }

   public static Set<IArtifactType> getAllSMAType() throws OseeCoreException {
      Set<IArtifactType> artTypeNames = TeamWorkflowExtensions.getAllTeamWorkflowArtifactTypes();
      artTypeNames.add(AtsArtifactTypes.Task);
      artTypeNames.add(AtsArtifactTypes.DecisionReview);
      artTypeNames.add(AtsArtifactTypes.PeerToPeerReview);
      return artTypeNames;
   }

   public static List<Artifact> getAllSMATypeArtifacts() throws OseeCoreException {
      List<Artifact> result = new ArrayList<Artifact>();
      for (IArtifactType artType : getAllSMAType()) {
         result.addAll(ArtifactQuery.getArtifactListFromType(artType, AtsUtil.getAtsBranch()));
      }
      return result;
   }

   public static List<TeamWorkFlowArtifact> getAllTeamWorkflowArtifacts() throws OseeCoreException {
      List<TeamWorkFlowArtifact> result = new ArrayList<TeamWorkFlowArtifact>();
      for (IArtifactType artType : TeamWorkflowExtensions.getAllTeamWorkflowArtifactTypes()) {
         List<TeamWorkFlowArtifact> teamArts =
            org.eclipse.osee.framework.jdk.core.util.Collections.castAll(ArtifactQuery.getArtifactListFromType(artType,
               AtsUtil.getAtsBranch()));
         result.addAll(teamArts);
      }
      return result;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public double getHoursSpentSMAState(IWorkPage state) throws OseeCoreException {
      return getStateMgr().getHoursSpent(state);
   }

   /**
    * Return hours spent working ONLY on tasks related to stateName
    */
   public double getHoursSpentSMAStateTasks(IWorkPage state) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getHoursSpentFromTasks(state);
   }

   /**
    * Return hours spent working ONLY on reviews related to stateName
    */
   public double getHoursSpentSMAStateReviews(IWorkPage state) throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getHoursSpent((TeamWorkFlowArtifact) this, state);
      }
      return 0;
   }

   /**
    * Return hours spent working on all things (including children SMAs) related to stateName
    */
   public double getHoursSpentSMAStateTotal(IWorkPage state) throws OseeCoreException {
      return getHoursSpentSMAState(state) + getHoursSpentSMAStateTasks(state) + getHoursSpentSMAStateReviews(state);
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException {
      return getHoursSpentSMAStateTotal(getStateMgr().getCurrentState());
   }

   /**
    * Return hours spent working on all things (including children SMAs) for this SMA
    */
   public double getHoursSpentSMATotal() throws OseeCoreException {
      double hours = 0.0;
      for (String stateName : getStateMgr().getVisitedStateNames()) {
         hours += getHoursSpentSMAStateTotal(new SimpleTeamState(stateName, WorkPageType.Working));
      }
      return hours;
   }

   /**
    * Return Percent Complete working ONLY the SMA stateName (not children SMAs)
    */
   public int getPercentCompleteSMAState(IWorkPage state) throws OseeCoreException {
      return getStateMgr().getPercentComplete(state);
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public int getPercentCompleteSMAStateTasks(IWorkPage state) throws OseeCoreException {
      if (!(this instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) this).getPercentCompleteFromTasks(state);
   }

   /**
    * Return Percent Complete ONLY on reviews related to stateName. Total Percent / # Reviews
    */
   public int getPercentCompleteSMAStateReviews(IWorkPage state) throws OseeCoreException {
      if (isTeamWorkflow()) {
         return ReviewManager.getPercentComplete((TeamWorkFlowArtifact) this, state);
      }
      return 0;
   }

   /**
    * Return Percent Complete on all things (including children SMAs) related to stateName. Total Percent for state,
    * tasks and reviews / 1 + # Tasks + # Reviews
    */
   public int getPercentCompleteSMAStateTotal(IWorkPage state) throws OseeCoreException {
      return getStateMetricsData(state).getResultingPercent();
   }

   /**
    * Return Percent Complete on all things (including children SMAs) for this SMA<br>
    * <br>
    * percent = all state's percents / number of states (minus completed/cancelled)
    */
   public int getPercentCompleteSMATotal() throws OseeCoreException {
      if (isCompletedOrCancelled()) {
         return 100;
      }
      Map<String, Double> stateToWeightMap = getStatePercentCompleteWeight();
      if (!stateToWeightMap.isEmpty()) {
         // Calculate total percent using configured weighting
         int percent = 0;
         for (StateDefinition state : getWorkDefinition().getStates()) {
            if (!state.isCompletedPage() && !state.isCancelledPage()) {
               Double weight = stateToWeightMap.get(state);
               if (weight == null) {
                  weight = 0.0;
               }
               percent += weight * getPercentCompleteSMAStateTotal(state);
            }
         }
         return percent;
      } else {
         int percent = 0;
         int numStates = 0;
         for (StateDefinition state : getWorkDefinition().getStates()) {
            if (!state.isCompletedPage() && !state.isCancelledPage()) {
               percent += getPercentCompleteSMAStateTotal(state);
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
         Collection<RuleDefinition> workRuleDefs = getRulesStartsWith(AtsStatePercentCompleteWeightRule.ID);
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

   private StateMetricsData getStateMetricsData(IWorkPage teamState) throws OseeCoreException {
      // Add percent and bump objects 1 for state percent
      int percent = getPercentCompleteSMAState(teamState);
      int numObjects = 1; // the state itself is one object

      // Add percent for each task and bump objects for each task
      if (this instanceof AbstractTaskableArtifact) {
         Collection<TaskArtifact> tasks = ((AbstractTaskableArtifact) this).getTaskArtifacts(teamState);
         for (TaskArtifact taskArt : tasks) {
            percent += taskArt.getPercentCompleteSMATotal();
         }
         numObjects += tasks.size();
      }

      // Add percent for each review and bump objects for each review
      if (isTeamWorkflow()) {
         Collection<AbstractReviewArtifact> reviews = ReviewManager.getReviews((TeamWorkFlowArtifact) this, teamState);
         for (AbstractReviewArtifact reviewArt : reviews) {
            percent += reviewArt.getPercentCompleteSMATotal();
         }
         numObjects += reviews.size();
      }

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
      return getHoursSpentSMAState(getStateMgr().getCurrentState());
   }

   @Override
   public double getWorldViewHoursSpentStateReview() throws OseeCoreException {
      return getHoursSpentSMAStateReviews(getStateMgr().getCurrentState());
   }

   @Override
   public double getWorldViewHoursSpentStateTask() throws OseeCoreException {
      return getHoursSpentSMAStateTasks(getStateMgr().getCurrentState());
   }

   @Override
   public double getWorldViewHoursSpentTotal() throws OseeCoreException {
      return getHoursSpentSMATotal();
   }

   @Override
   public int getWorldViewPercentCompleteState() throws OseeCoreException {
      return getPercentCompleteSMAState(getStateMgr().getCurrentState());
   }

   @Override
   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException {
      return getPercentCompleteSMAStateReviews(getStateMgr().getCurrentState());
   }

   @Override
   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException {
      return getPercentCompleteSMAStateTasks(getStateMgr().getCurrentState());
   }

   @Override
   public int getWorldViewPercentCompleteTotal() throws OseeCoreException {
      return getPercentCompleteSMATotal();
   }

   public Set<IRelationEnumeration> getAtsWorldRelations() {
      return atsWorldRelations;
   }

   public String getWorldViewLastUpdated() throws OseeCoreException {
      return DateUtil.getMMDDYYHHMM(getLastModified());
   }

   @SuppressWarnings("unused")
   public String getWorldViewSWEnhancement() throws OseeCoreException {
      return "";
   }

   @Override
   public String getGroupExplorerName() {
      return String.format("[%s] %s", getStateMgr().getCurrentStateName(), getName());
   }

   public void closeEditors(boolean save) {
      SMAEditor.close(java.util.Collections.singleton(this), save);
   }

   public AtsLog getLog() {
      return atsLog;
   }

   public AtsNote getNotes() {
      return atsNote;
   }

   public Result getUserInputNeeded() {
      return Result.FalseResult;
   }

   public StateDefinition getStateDefinition() {
      if (getStateMgr().getCurrentStateName() == null) {
         return null;
      }
      return getWorkDefinition().getStateByName(getStateMgr().getCurrentStateName());
   }

   public StateDefinition getStateDefinitionByName(String name) {
      return getWorkDefinition().getStateByName(name);
   }

   public boolean isHistoricalVersion() {
      return isHistorical();
   }

   public List<StateDefinition> getToStates() {
      return getStateDefinition().getToStates();
   }

   public List<StateDefinition> getReturnPages() {
      return getStateDefinition().getReturnStates();
   }

   public boolean isReturnPage(StateDefinition stateDefinition) {
      return getReturnPages().contains(stateDefinition);
   }

   public boolean isAccessControlWrite() throws OseeCoreException {
      return AccessControlManager.hasPermission(this, PermissionEnum.WRITE);
   }

   /**
    * @return true if this is a TeamWorkflow and it uses versions
    */
   public boolean isTeamUsesVersions() {
      if (!isTeamWorkflow()) {
         return false;
      }
      try {
         return ((TeamWorkFlowArtifact) this).getTeamDefinition().isTeamUsesVersions();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return false;
      }
   }

   /**
    * Return true if sma is TeamWorkflowArtifact or review of a team workflow and it's TeamDefinitionArtifact has rule
    * set
    */
   public boolean teamDefHasWorkRule(String ruleId) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = null;
      if (isTeamWorkflow()) {
         teamArt = (TeamWorkFlowArtifact) this;
      }
      if (this instanceof AbstractReviewArtifact) {
         teamArt = ((AbstractReviewArtifact) this).getParentTeamWorkflow();
      }
      if (teamArt == null) {
         return false;
      }
      try {
         return teamArt.getTeamDefinition().hasWorkRule(ruleId);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   public boolean workPageHasWorkRule(String ruleId) {
      return getStateDefinition().hasRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());
   }

   public Collection<RuleDefinition> getRulesStartsWith(String ruleName) throws OseeCoreException {
      Set<RuleDefinition> workRules = new HashSet<RuleDefinition>();
      if (!Strings.isValid(ruleName)) {
         return workRules;
      }
      if (isTeamWorkflow()) {
         // Get rules from team definition
         workRules.addAll(((TeamWorkFlowArtifact) this).getTeamDefinition().getRulesStartsWith(ruleName));
      }
      // Get work rules from workflow
      if (workDefinition != null) {
         // Get rules from workflow definitions
         workRules.addAll(workDefinition.getRulesStartsWith(ruleName));
      }
      // Add work rules from page
      workRules.addAll(getStateDefinition().getRulesStartsWith(ruleName));
      return workRules;
   }

   /**
    * @return true if this is a TeamWorkflow and the version it's been targeted for has been released
    */
   public boolean isReleased() {
      try {
         VersionArtifact verArt = getTargetedVersion();
         if (verArt != null) {
            return verArt.isReleased();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   public boolean isVersionLocked() {
      try {
         VersionArtifact verArt = getTargetedVersion();
         if (verArt != null) {
            return verArt.isVersionLocked();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   public VersionArtifact getTargetedVersion() throws OseeCoreException {
      return TargetedVersionColumn.getTargetedVersion(this);
   }

   public String getTargetedVersionStr() throws OseeCoreException {
      return TargetedVersionColumn.getTargetedVersionStr(this);
   }

   public void setCreatedBy(User user, boolean logChange, Date date) throws OseeCoreException {
      if (logChange) {
         if (getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null) == null) {
            atsLog.addLog(LogType.Originated, "", "", date, user);
         } else {
            atsLog.addLog(LogType.Originated, "", "Changed by " + UserManager.getUser().getName(), date, user);
            atsLog.internalResetOriginator(user);
         }
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
      }
   }

   public void internalSetCreatedBy(User user) throws OseeCoreException {
      atsLog.internalResetOriginator(user);
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedBy)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedBy, user.getUserId());
      }
   }

   public void internalSetCreatedDate(Date date) throws OseeCoreException {
      atsLog.internalResetCreatedDate(date);
      if (isAttributeTypeValid(AtsAttributeTypes.CreatedDate)) {
         setSoleAttributeValue(AtsAttributeTypes.CreatedDate, date);
      }
   }

   public Date getCreatedDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(AtsAttributeTypes.CreatedDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         return getLog().internalGetCreationDate();
      }
      return date;
   }

   public User getCreatedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CreatedBy, null);
      if (!Strings.isValid(userId)) {
         return getLog().internalGetOriginator();
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public Date internalGetCancelledDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(AtsAttributeTypes.CancelledDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCancelledLogItem();
         if (item != null) {
            return item.getDate();
         }
         return null;
      }
      return date;
   }

   public User internalGetCancelledBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CancelledBy, null);
      if (!Strings.isValid(userId)) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCancelledLogItem();
         if (item != null) {
            return item.getUser();
         }
         return null;
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public String getCancelledReason() throws OseeCoreException {
      String reason = getSoleAttributeValue(AtsAttributeTypes.CancelledReason, null);
      if (!Strings.isValid(reason)) {
         reason = getLog().internalGetCancelledReason();
      }
      return reason;
   }

   public void setCancellationReason(String reason) throws OseeCoreException {
      // Keep this for backward compatibility
      getLog().internalSetCancellationReason(reason);
      if (isAttributeTypeValid(AtsAttributeTypes.CancelledReason)) {
         setSoleAttributeValue(AtsAttributeTypes.CancelledReason, reason);
      }
   }

   public String getCancelledFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CancelledFromState, null);
      if (!Strings.isValid(fromState)) {
         // Keep this for backward compatibility
         return getLog().internalGetCancelledFromState();
      }
      return fromState;
   }

   public Date getCompletedDate() throws OseeCoreException {
      Date date = getSoleAttributeValue(AtsAttributeTypes.CompletedDate, null);
      if (date == null) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCompletedLogItem();
         if (item != null) {
            return item.getDate();
         }
         return null;
      }
      return date;
   }

   public User getCompletedBy() throws OseeCoreException {
      String userId = getSoleAttributeValue(AtsAttributeTypes.CompletedBy, null);
      if (!Strings.isValid(userId)) {
         // Keep this for backward compatibility
         LogItem item = getLog().internalGetCompletedLogItem();
         if (item != null) {
            return item.getUser();
         }
         return null;
      } else {
         return UserManager.getUserByUserId(userId);
      }
   }

   public LogItem getStateCompletedData(IWorkPage state) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateComplete, state.getPageName());
   }

   public LogItem getStateStartedData(IWorkPage state) throws OseeCoreException {
      return getLog().getStateEvent(LogType.StateEntered, state.getPageName());
   }

   public String getCompletedFromState() throws OseeCoreException {
      String fromState = getSoleAttributeValue(AtsAttributeTypes.CompletedFromState, null);
      if (!Strings.isValid(fromState)) {
         return getLog().internalGetCompletedFromState();
      }
      return fromState;
   }

   public boolean isInWork() throws OseeCoreException {
      // Backward compatibility; remove this once 0.9.7 released
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Working.name());
      } else {
         return !isCompletedOrCancelled();
      }

   }

   public boolean isCompleted() throws OseeCoreException {
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Completed.name());
      } else {
         return getCurrentStateName().equals(TeamState.Completed.getPageName());
      }
   }

   public boolean isCancelled() throws OseeCoreException {
      // Backward compatibility; remove this once 0.9.7 released
      if (isAttributeTypeValid(AtsAttributeTypes.CurrentStateType) && getSoleAttributeValue(
         AtsAttributeTypes.CurrentStateType, null) != null) {
         return getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(WorkPageType.Cancelled.name());
      } else {
         return getCurrentStateName().equals(TeamState.Cancelled.getPageName());
      }
   }

   public boolean isCompletedOrCancelled() throws OseeCoreException {
      return isCompleted() || isCancelled();
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
         if (!transitionAssignees.isEmpty() && transitionAssignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
            transitionAssignees.remove(UserManager.getUser(SystemUser.UnAssigned));
         }
         if (!transitionAssignees.isEmpty()) {
            return transitionAssignees;
         }
      }
      return stateMgr.getAssignees();
   }

   public String getTransitionAssigneesStr() throws OseeCoreException {
      return Artifacts.toTextList(getTransitionAssignees(), ";");
   }

   public boolean isInTransition() {
      return inTransition;
   }

   public void setInTransition(boolean inTransition) {
      this.inTransition = inTransition;
   }

   public StateManager getStateMgr() {
      return stateMgr;
   }

   public boolean isTeamWorkflow() {
      return this instanceof TeamWorkFlowArtifact;
   }

   public boolean isTask() {
      return this instanceof TaskArtifact;
   }

   public boolean isReview() {
      return this instanceof AbstractReviewArtifact;
   }

   public StateXWidgetPage getCurrentAtsWorkPage() throws OseeCoreException {
      for (StateXWidgetPage statePage : getStatePages()) {
         if (getStateMgr().isInState(statePage)) {
            return statePage;
         }
      }
      return null;
   }

   public List<StateXWidgetPage> getStatePages() throws OseeCoreException {
      List<StateXWidgetPage> statePages = new ArrayList<StateXWidgetPage>();
      for (StateDefinition stateDefinition : getWorkDefinition().getStatesOrdered()) {
         try {
            StateXWidgetPage statePage =
               new StateXWidgetPage(getWorkDefinition(), stateDefinition, null, ATSXWidgetOptionResolver.getInstance());
            statePages.add(statePage);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return statePages;
   }

   /**
    * Assigned or computed Id that will show at the top of the editor
    */
   public String getPcrId() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = getParentTeamWorkflow();
      if (teamArt != null) {
         return teamArt.getTeamName() + " " + getHumanReadableId();
      }
      return "";
   }

   @Override
   public CmAccessControl getAccessControl() {
      return AtsPlugin.getInstance().getCmService();
   }

   public boolean isTargetedErrorLogged() {
      return targetedErrorLogged;
   }

   public void setTargetedErrorLogged(boolean targetedErrorLogged) {
      this.targetedErrorLogged = targetedErrorLogged;
   }

}
