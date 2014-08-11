/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.mock;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.StateManager;
import org.eclipse.osee.ats.core.util.AtsUserGroup;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MockWorkItem implements IAtsWorkItem {

   private final String name;
   private String atsId;
   private final IAtsStateManager stateMgr;
   private final AtsUserGroup implementers = new AtsUserGroup();
   private IAtsUser completedBy;
   private IAtsUser cancelledBy;
   private String completeFromState;
   private String cancelledFromState;

   public MockWorkItem(String name, String currentStateName, StateType StateType) {
      this(name);
   }

   public MockWorkItem(String name, String currentStateName, List<? extends IAtsUser> assignees) {
      this(name);
   }

   public MockWorkItem(String name) {
      this.name = name;
      atsId = name;
      this.stateMgr = new StateManager(this, new AtsLogFactory(), null);
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return atsId;
   }

   @Override
   public String getDescription() {
      return name;
   }

   @Override
   public String getAtsId() {
      return atsId;
   }

   @Override
   public List<IAtsUser> getImplementers() {
      return implementers.getUsers();
   }

   public void setImplementers(List<? extends IAtsUser> implementers) {
      this.implementers.setUsers(implementers);
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return stateMgr.getAssignees();
   }

   public void addImplementer(IAtsUser joe) {
      implementers.addUser(joe);
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow() {
      return null;
   }

   @Override
   public String toStringWithId() {
      return toString();
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
   public IAtsStateDefinition getStateDefinition() {
      return null;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition() {
      return null;
   }

   @Override
   public void setAtsId(String atsId, IAtsChangeSet changes) {
      this.atsId = atsId;
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
   public Object getStoreObject() {
      return null;
   }

   @Override
   public void setStoreObject(Object object) {
      // do nothing
   }

   @Override
   public IAtsUser getCreatedBy() {
      return null;
   }

   @Override
   public Date getCreatedDate() {
      return null;
   }

   @Override
   public IAtsUser getCompletedBy() {
      return completedBy;
   }

   @Override
   public IAtsUser getCancelledBy() {
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

   public void setCompletedBy(IAtsUser user) {
      this.completedBy = user;
   }

   public void setCancelledBy(IAtsUser user) {
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

}
