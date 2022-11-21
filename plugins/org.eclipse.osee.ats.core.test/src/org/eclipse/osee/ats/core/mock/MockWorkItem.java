/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.core.mock;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.ats.core.util.AtsUserGroup;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public class MockWorkItem implements IAtsWorkItem {
   private final String name;
   private final String atsId;
   private IAtsStateManager stateMgr;
   private final AtsUserGroup implementers = new AtsUserGroup();
   private final WorkDefinition workDefinition;
   private AtsUser completedBy;
   private AtsUser cancelledBy;
   private String completeFromState;
   private String cancelledFromState;

   public MockWorkItem(String name, String currentStateName, WorkDefinition workDefinition, StateType StateType) {
      this.name = name;
      atsId = name;
      this.stateMgr = new StateManager(this, new AtsLogFactory(), null);
      this.workDefinition = workDefinition;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getAtsId() {
      return atsId;
   }

   @Override
   public List<AtsUser> getImplementers() {
      return implementers.getUsers();
   }

   public void setImplementers(List<? extends AtsUser> implementers) {
      this.implementers.setUsers(implementers);
   }

   @Override
   public List<AtsUser> getAssignees() {
      return stateMgr.getAssignees();
   }

   public void addImplementer(AtsUser joe) {
      implementers.addUser(joe);
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      return null;
   }

   @Override
   public IAtsStateManager getStateMgr() {
      return null;
   }

   @Override
   public IAtsLog getLog() {
      return null;
   }

   @Override
   public StateDefinition getStateDefinition() {
      return null;
   }

   @Override
   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   @Override
   public boolean isTask() {
      return false;
   }

   @Override
   public boolean isTeamWorkflow() {
      return false;
   }

   @Override
   public AtsUser getCreatedBy() {
      return null;
   }

   @Override
   public Date getCreatedDate() {
      return null;
   }

   @Override
   public AtsUser getCompletedBy() {
      return completedBy;
   }

   @Override
   public AtsUser getCancelledBy() {
      return cancelledBy;
   }

   @Override
   public String getCompletedFromState() {
      return completeFromState;
   }

   @Override
   public String getCancelledFromState() {
      return cancelledFromState;
   }

   @Override
   public String getArtifactTypeName() {
      return null;
   }

   @Override
   public Date getCompletedDate() {
      return null;
   }

   public void setCompletedBy(AtsUser user) {
      this.completedBy = user;
   }

   public void setCancelledBy(AtsUser user) {
      this.cancelledBy = user;
   }

   public void setCompletedFromState(String stateName) {
      this.completeFromState = stateName;
   }

   public void setCancelledFromState(String stateName) {
      this.cancelledFromState = stateName;
   }

   @Override
   public Date getCancelledDate() {
      return null;
   }

   @Override
   public String getCancelledReason() {
      return null;
   }

   @Override
   public Long getId() {
      return Long.valueOf(456);
   }

   @Override
   public boolean isReview() {
      return false;
   }

   @Override
   public IAtsAction getParentAction() {
      return null;
   }

   @Override
   public boolean isGoal() {
      return false;
   }

   @Override
   public boolean isInWork() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public boolean isCompleted() {
      return getStateMgr().getStateType().isCompleted();
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isCancelled() {
      return getStateMgr().getStateType().isCancelled();
   }

   @Override
   public int compareTo(Named o) {
      return 0;
   }

   @Override
   public boolean isDecisionReview() {
      return false;
   }

   @Override
   public boolean isPeerReview() {
      return false;
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s]-[%s]", getName(), getIdString());
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return null;
   }

   @Override
   public void setStateMgr(IAtsStateManager stateMgr) {
      this.stateMgr = stateMgr;
   }

   @Override
   public void clearCaches() {
      // do nothing
   }

   @Override
   public AtsApi getAtsApi() {
      return null;
   }

   @Override
   public boolean isInState(IStateToken state) {
      return false;
   }

   @Override
   public Collection<WorkType> getWorkTypes() {
      return null;
   }

   @Override
   public boolean isWorkType(WorkType workType) {
      return false;
   }

   @Override
   public Collection<String> getTags() {
      return null;
   }

   @Override
   public boolean hasTag(String tag) {
      return false;
   }

}