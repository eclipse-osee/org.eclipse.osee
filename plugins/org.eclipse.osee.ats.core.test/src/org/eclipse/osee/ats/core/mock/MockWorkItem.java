/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.mock;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsWorkData;
import org.eclipse.osee.ats.core.model.IAtsWorkItem;
import org.eclipse.osee.ats.core.model.WorkStateProvider;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.model.impl.WorkStateProviderImpl;
import org.eclipse.osee.ats.core.util.AtsUserGroup;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MockWorkItem implements IAtsWorkItem {

   private final String name;
   private final String id;
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
      this.id = name;
      this.atsWorkData = atsWorkData;
      this.workStateProvider = workStateProvider;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return id;
   }

   @Override
   public String getDescription() {
      return name;
   }

   @Override
   public String getHumanReadableId() {
      return id;
   }

   @Override
   public IAtsWorkData getWorkData() {
      return atsWorkData;
   }

   @Override
   public List<IAtsUser> getImplementers() {
      return this.implementers.getUsers();
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
      this.workStateProvider = atsStateData;
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return workStateProvider.getAssignees();
   }

   public void addImplementer(IAtsUser joe) {
      implementers.addUser(joe);
   }

}
