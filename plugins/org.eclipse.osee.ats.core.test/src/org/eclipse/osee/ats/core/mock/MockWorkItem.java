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

import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.model.impl.WorkStateProviderImpl;
import org.eclipse.osee.ats.core.util.AtsUserGroup;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MockWorkItem implements IAtsWorkItem {

   private final String name;
   private String atsId;
   private IAtsWorkData atsWorkData;
   private WorkStateProvider workStateProvider;
   private final AtsUserGroup implementers = new AtsUserGroup();

   public MockWorkItem(String name, String currentStateName, StateType StateType) {
      this(name, new MockWorkData(StateType), new WorkStateProviderImpl(new MockWorkStateFactory(), new WorkStateImpl(
         currentStateName)));
   }

   public MockWorkItem(String name, String currentStateName, List<? extends IAtsUser> assignees) {
      this(name, new MockWorkData(StateType.Working), new WorkStateProviderImpl(new MockWorkStateFactory(),
         new WorkStateImpl(currentStateName)));
   }

   public MockWorkItem(String name, IAtsWorkData atsWorkData, WorkStateProvider workStateProvider) {
      this.name = name;
      atsId = name;
      this.atsWorkData = atsWorkData;
      this.workStateProvider = workStateProvider;
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
   public IAtsWorkData getWorkData() {
      return atsWorkData;
   }

   @Override
   public List<IAtsUser> getImplementers() {
      return implementers.getUsers();
   }

   public void setImplementers(List<? extends IAtsUser> implementers) {
      this.implementers.setUsers(implementers);
   }

   public void setWorkData(IAtsWorkData atsWorkData) {
      this.atsWorkData = atsWorkData;
   }

   @Override
   public WorkStateProvider getStateData() {
      return workStateProvider;
   }

   public void setStateData(WorkStateProvider atsStateData) {
      workStateProvider = atsStateData;
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return workStateProvider.getAssignees();
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
   public void setAtsId(String atsId) throws OseeCoreException {
      this.atsId = atsId;
   }

}
