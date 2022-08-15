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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinition extends AbstractWorkDefItem {

   private final List<IAtsStateDefinition> states = new ArrayList<>(5);
   private final CountingMap<String> labelCount = new CountingMap<String>();
   private final List<CreateTasksDefinition> createTasksDefs = new ArrayList<>();
   private final Map<ReviewRole, Integer> reviewRoleMap = new ConcurrentHashMap<>();
   private final Map<ReviewRoleType, Integer> reviewRoleTypeMap = new ConcurrentHashMap<>();
   private IAtsStateDefinition startState;
   private HeaderDefinition headerDef;
   private boolean showStateMetrics = false;
   private List<XViewerColumn> reviewDefectColumns = new ArrayList<>();
   private XResultData results = new XResultData();
   private final List<WorkDefOption> options = new ArrayList<>();

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

   public IAtsStateDefinition getStateByName(String name) {
      for (IAtsStateDefinition state : states) {
         if (state.getName().equals(name)) {
            return state;
         }
      }
      return null;
   }

   public IAtsStateDefinition getStartState() {
      return startState;
   }

   public void setStartState(IAtsStateDefinition startState) {
      this.startState = startState;
   }

   public IAtsStateDefinition addState(IAtsStateDefinition state) {
      states.add(state);
      return state;
   }

   public List<IAtsStateDefinition> getStates() {
      return states;
   }

   public HeaderDefinition getHeaderDef() {
      return headerDef;
   }

   public boolean hasHeaderDefinitionItems() {
      return headerDef != null && !headerDef.getLayoutItems().isEmpty();
   }

   public HeaderDefinition getDefaultHeaderDef() {

      HeaderDefinition defaultHeaderDef = new HeaderDefinition(this);
      defaultHeaderDef.setShowMetricsHeader(true);
      defaultHeaderDef.setShowWorkPackageHeader(true);

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

}