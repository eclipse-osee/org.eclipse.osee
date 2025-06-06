/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewRequiredMinimum;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinition extends AbstractWorkDefItem {

   private final List<StateDefinition> states = new ArrayList<>(5);
   @JsonIgnore
   private final CountingMap<String> labelCount = new CountingMap<String>();
   @JsonIgnore
   private final List<CreateTasksDefinition> createTasksDefs = new ArrayList<>();
   @JsonIgnore
   private final Map<ReviewRole, Integer> reviewRoleMap = new ConcurrentHashMap<>();
   @JsonIgnore
   private final Map<ReviewRoleType, Integer> reviewRoleTypeMap = new ConcurrentHashMap<>();
   @JsonIgnore
   private StateDefinition startState;
   @JsonIgnore
   private HeaderDefinition headerDef;
   private boolean showStateMetrics = false;
   @JsonIgnore
   private List<XViewerColumn> reviewDefectColumns = new ArrayList<>();
   @JsonIgnore
   private XResultData results = new XResultData();
   private final List<WorkDefOption> options = new ArrayList<>();
   private final List<ChangeTypes> changeTypes = new ArrayList<>();
   private AttributeTypeToken pointsAttrType = AtsAttributeTypes.PointsNumeric;
   private final List<Priorities> priorities = new ArrayList<>();
   private final List<ReviewRequiredMinimum> reviewRequiredMinimums = new ArrayList<>();
   private List<IAtsTransitionHook> transitionHooks = new ArrayList<>();
   private List<ConditionalRule> conditions = new ArrayList<>();

   public WorkDefinition(Long id, String name) {
      this(id, name, ArtifactTypeToken.SENTINEL);
   }

   public WorkDefinition(Long id, String name, ArtifactTypeToken artType) {
      super(id, name, artType);
      if (artType == null) {
         results.errorf("Artifact Type can not be null Work Def [%s]", name);
      }
      headerDef = new HeaderDefinition(this);
   }

   public StateDefinition getStateByName(String name) {
      for (StateDefinition state : states) {
         if (state.getName().equals(name)) {
            return state;
         }
      }
      return null;
   }

   public StateDefinition getStartState() {
      return startState;
   }

   public void setStartState(StateDefinition startState) {
      this.startState = startState;
   }

   public StateDefinition addState(StateDefinition state) {
      states.add(state);
      return state;
   }

   public List<StateDefinition> getStates() {
      return states;
   }

   public HeaderDefinition getHeaderDef() {
      return headerDef;
   }

   public boolean hasHeaderDefinitionItems() {
      return headerDef != null && !headerDef.getLayoutItems().isEmpty();
   }

   @JsonIgnore
   public HeaderDefinition getDefaultHeaderDef() {
      HeaderDefinition defaultHeaderDef = new HeaderDefinition(this);
      defaultHeaderDef.setShowMetricsHeader(true);
      defaultHeaderDef.getLayoutItems().add( //
         new WidgetDefinition("Work Package", "XHyperlinkWorkPackageDam") //
      );
      return defaultHeaderDef;
   }

   public void setHeaderDefinition(HeaderDefinition headerDef) {
      this.headerDef = headerDef;
   }

   public boolean isShowStateMetrics() {
      return showStateMetrics;
   }

   public void setShowStateMetrics(boolean showStateMetrics) {
      this.showStateMetrics = showStateMetrics;
   }

   public void addTaskSetDef(CreateTasksDefinition createTasksDef) {
      createTasksDefs.add(createTasksDef);
   }

   public List<CreateTasksDefinition> getCreateTasksDefs() {
      return createTasksDefs;
   }

   public CountingMap<String> getLabelCount() {
      return labelCount;
   }

   public List<XViewerColumn> getReviewDefectColumns() {
      return reviewDefectColumns;
   }

   public void setReviewDefectColumns(List<XViewerColumn> reviewDefectColumns) {
      this.reviewDefectColumns = reviewDefectColumns;
   }

   public void addReviewRole(ReviewRole role, int minimum) {
      reviewRoleMap.put(role, minimum);
   }

   public Set<ReviewRole> getReviewRoles() {
      return reviewRoleMap.keySet();
   }

   public void andReviewRoleTypeMinimum(ReviewRoleType reviewRoleType, int minimum) {
      reviewRoleTypeMap.put(reviewRoleType, minimum);
   }

   public Map<ReviewRoleType, Integer> getReviewRoleTypeMap() {
      return reviewRoleTypeMap;
   }

   public Map<ReviewRole, Integer> getReviewRoleMap() {
      return reviewRoleMap;
   }

   public ReviewRole fromName(String name) {
      for (ReviewRole role : reviewRoleMap.keySet()) {
         if (role.getName().equals(name)) {
            return role;
         }
      }
      // Handle retired roles for old reviews
      ReviewRole newRole = new ReviewRole(Long.valueOf(reviewRoleMap.size()), name, ReviewRoleType.Reviewer);
      reviewRoleMap.put(newRole, 0);
      return newRole;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public List<WorkDefOption> getOptions() {
      return options;
   }

   public boolean hasOption(WorkDefOption workDefOption) {
      return options.contains(workDefOption);
   }

   public void addCreateTasksDefinition(CreateTasksDefinitionBuilder createTasksDefBldr) {
      getCreateTasksDefs().add(createTasksDefBldr.getCreateTasksDef());
   }

   public List<ChangeTypes> getChangeTypes() {
      return changeTypes;
   }

   public AttributeTypeToken getPointsAttrType() {
      return pointsAttrType;
   }

   public void setPointsAttrType(AttributeTypeToken pointsAttrType) {
      this.pointsAttrType = pointsAttrType;
   }

   public List<Priorities> getPriorities() {
      return priorities;
   }

   public String getStartStateName() {
      return getStartState().getName();
   }

   public List<String> getStateNames() {
      List<String> stateNames = new ArrayList<>();
      for (StateDefinition state : getStates()) {
         stateNames.add(state.getName());
      }
      return stateNames;
   }

   public void andRequiredMinimum(ReviewRequiredMinimum reviewRequiredMinimum) {
      reviewRequiredMinimums.add(reviewRequiredMinimum);
   }

   public List<ReviewRequiredMinimum> getReviewRequiredMinimums() {
      return reviewRequiredMinimums;
   }

   public void addTransitionHook(IAtsTransitionHook transitionHook) {
      transitionHooks.add(transitionHook);
   }

   public List<IAtsTransitionHook> getTransitionHooks() {
      return transitionHooks;
   }

   public void setTransitionHooks(List<IAtsTransitionHook> transitionHooks) {
      this.transitionHooks = transitionHooks;
   }

   public List<ConditionalRule> getConditions() {
      return conditions;
   }

   public void setConditions(List<ConditionalRule> conditions) {
      this.conditions = conditions;
   }

   public void addCondition(ConditionalRule conditionalRule) {
      this.conditions.add(conditionalRule);
   }

   public boolean isChangeRequest() {
      return this.options.contains(WorkDefOption.IsChangeRequest);
   }

   public boolean isProblemReport() {
      return this.options.contains(WorkDefOption.IsProblemReport);
   }
}
