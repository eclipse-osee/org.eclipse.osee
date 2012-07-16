/*
 * Created on Mar 26, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.ats.core.mock.MockWorkStateFactory;
import org.eclipse.osee.ats.core.notify.IAtsNotificationListener;
import org.eclipse.osee.ats.core.users.Guest;
import org.eclipse.osee.ats.core.users.UnAssigned;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WorkStateProviderImplTest {
   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   @Test
   public void testWorkStateProviderImplWorkStateFactory() {
      new WorkStateProviderImpl(new MockWorkStateFactory());
   }

   @Test
   public void testSetNotificationListener() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setNotificationListener(new IAtsNotificationListener() {

         @Override
         public void notifyAssigned(List<IAtsUser> notifyAssignees) {
            Assert.assertEquals(1, notifyAssignees.size());
            Assert.assertEquals(steve, notifyAssignees.iterator().next());
         }
      });
      provider.addAssignee(steve);

      provider.removeAssignee(steve);

      provider.setAssignee(steve);
   }

   @Test
   public void testAddAssignee() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");

      Assert.assertTrue(provider.getAssignees().isEmpty());
      provider.addAssignees("endorse", null);
      Assert.assertTrue(provider.getAssignees().isEmpty());

      List<IAtsUser> users = new ArrayList<IAtsUser>();
      provider.addAssignees("endorse", users);
      Assert.assertTrue(provider.getAssignees().isEmpty());

      Assert.assertTrue(provider.getAssignees().isEmpty());
      provider.addAssignee(joe);
      Assert.assertEquals(1, provider.getAssignees().size());

      provider.addAssignee(joe);
      Assert.assertEquals(1, provider.getAssignees().size());

   }

   @Test(expected = OseeArgumentException.class)
   public void testAddAssignee_exception() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");

      provider.addAssignee(Guest.instance);
   }

   @Test
   public void testSetAssignee() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");

      Assert.assertTrue(provider.getAssignees().isEmpty());
      provider.setAssignee("endorse", null);
      Assert.assertTrue(provider.getAssignees().isEmpty());
      provider.setAssignee(null);
      Assert.assertTrue(provider.getAssignees().isEmpty());

      List<IAtsUser> users = new ArrayList<IAtsUser>();
      provider.setAssignees("endorse", users);
      Assert.assertTrue(provider.getAssignees().isEmpty());

      provider.setAssignees("endorse", null);
      Assert.assertTrue(provider.getAssignees().isEmpty());

      Assert.assertTrue(provider.getAssignees().isEmpty());
      provider.setAssignee(joe);
      Assert.assertEquals(1, provider.getAssignees().size());

      provider.setAssignee(joe);
      Assert.assertEquals(1, provider.getAssignees().size());
   }

   @Test
   public void testSetAssignee_removeUnassigned() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");

      provider.setAssignee(UnAssigned.instance);
      Assert.assertEquals(1, provider.getAssignees().size());
      Assert.assertEquals(UnAssigned.instance, provider.getAssignees().iterator().next());

      provider.setAssignees(Arrays.asList(joe, UnAssigned.instance));
      Assert.assertEquals(1, provider.getAssignees().size());
      Assert.assertEquals(joe, provider.getAssignees().iterator().next());
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddState_exception() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));

      provider.addState(new WorkStateImpl("endorse"));
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddState_exception2() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));

      provider.addState("endorse", new LinkedList<IAtsUser>(), 34, 23);
   }

   @Test(expected = OseeArgumentException.class)
   public void testSetAssignee_exception() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");

      provider.setAssignee(Guest.instance);
   }

   @Test
   public void testIsUnAssigned() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      Assert.assertFalse(provider.isUnAssigned());
      provider.addAssignee(UnAssigned.instance);
      Assert.assertTrue(provider.isUnAssigned());
   }

   @Test
   public void testIsUnAssignedSolely() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      Assert.assertFalse(provider.isUnAssignedSolely());
      provider.addAssignee(UnAssigned.instance);
      Assert.assertTrue(provider.isUnAssignedSolely());
      provider.addAssignee(joe);
      Assert.assertFalse(provider.isUnAssignedSolely());
      provider.removeAssignee(UnAssigned.instance);
      Assert.assertFalse(provider.isUnAssignedSolely());
   }

   @Test
   public void testGetAssigneesStrString() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.addAssignee(steve);
      provider.addAssignee(joe);
      Assert.assertEquals("steve; joe", provider.getAssigneesStr());
      Assert.assertEquals("steve; joe", provider.getAssigneesStr("endorse"));
      Assert.assertEquals("", provider.getAssigneesStr("analyze"));
      Assert.assertEquals("stev...", provider.getAssigneesStr("endorse", 5));
      Assert.assertEquals("steve; joe", provider.getAssigneesStr("endorse", 50));
   }

   @Test
   public void testAddAssignees() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", provider.getAssigneesStr());
   }

   @Test
   public void testAddAssignees_state() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.addAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", provider.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesIAtsUser() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setAssignee(joe);
      Assert.assertEquals("joe", provider.getAssigneesStr());
      provider.setAssignee(alice);
      Assert.assertEquals("alice", provider.getAssigneesStr());
   }

   @Test
   public void testSetAssigneeStringIAtsUser() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setAssignee("endorse", joe);
      Assert.assertEquals("joe", provider.getAssigneesStr());
      provider.setAssignee("endorse", alice);
      Assert.assertEquals("alice", provider.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesListOfQextendsIAtsUser() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.addAssignees(Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", provider.getAssigneesStr());
      provider.setAssignees(Arrays.asList(steve));
      Assert.assertEquals("steve", provider.getAssigneesStr());
   }

   @Test
   public void testSetAssigneesStringListOfQextendsIAtsUser() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setAssignee("endorse", joe);
      Assert.assertEquals("joe", provider.getAssigneesStr());
      provider.setAssignee("endorse", alice);
      Assert.assertEquals("alice", provider.getAssigneesStr());
   }

   @Test
   public void testRemoveAssigneeIAtsUser() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", provider.getAssigneesStr());
      provider.removeAssignee(alice);
      Assert.assertEquals("joe", provider.getAssigneesStr());
   }

   @Test
   public void testClearAssignees() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setAssignees("endorse", Arrays.asList(joe, alice));
      Assert.assertEquals("joe; alice", provider.getAssigneesStr());
      provider.clearAssignees();
      Assert.assertTrue(provider.getAssignees().isEmpty());
   }

   @Test
   public void testIsStateVisited() throws OseeArgumentException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      Assert.assertFalse(provider.isStateVisited("endorse"));
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      Assert.assertTrue(provider.isStateVisited("endorse"));
      provider.addState(new WorkStateImpl("analyze"));
      Assert.assertTrue(provider.isStateVisited("analyze"));

   }

   @Test
   public void testCreateState() throws OseeArgumentException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.createState("endorse");
      Assert.assertTrue(provider.isStateVisited("endorse"));
      provider.createState("endorse");
      Assert.assertEquals(1, provider.getVisitedStateNames().size());
   }

   @Test
   public void testAddStateStringListOfQextendsIAtsUserDoubleInt() throws OseeArgumentException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState("endorse", Arrays.asList(joe), 4.2, 4);
      Assert.assertTrue(provider.isStateVisited("endorse"));
      Assert.assertEquals(1, provider.getVisitedStateNames().size());
      Assert.assertEquals(1, provider.getAssignees("endorse").size());
      Assert.assertEquals(4.2, provider.getHoursSpent("endorse"));
      Assert.assertEquals(4, provider.getPercentComplete("endorse"));
   }

   @Test(expected = OseeStateException.class)
   public void testGetAssigneesForState() throws OseeStateException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.getAssignees();
   }

   @Test
   public void testAddStateWorkState() throws OseeArgumentException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState("endorse", Arrays.asList(joe));
      Assert.assertTrue(provider.isStateVisited("endorse"));
      Assert.assertEquals(1, provider.getVisitedStateNames().size());
      Assert.assertEquals(1, provider.getAssignees("endorse").size());
      Assert.assertEquals(0.0, provider.getHoursSpent("endorse"));
      Assert.assertEquals(0, provider.getPercentComplete("endorse"));
   }

   @Test
   public void getPercentComplete() {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      Assert.assertEquals(0, provider.getPercentComplete("endorse"));
   }

   @Test(expected = OseeStateException.class)
   public void setPercentComplete_exception() throws OseeStateException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.setPercentComplete("endorse", 34);
   }

   @Test
   public void setPercentComplete() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setPercentComplete("endorse", 34);

      Assert.assertEquals(34, provider.getPercentComplete("endorse"));
   }

   @Test
   public void getHoursSpent() {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      Assert.assertEquals(0.0, provider.getHoursSpent("endorse"));
   }

   @Test(expected = OseeStateException.class)
   public void setHoursSpent_exception() throws OseeStateException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.setHoursSpent("endorse", 8.0);
   }

   @Test
   public void setHoursSpent() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.addState(new WorkStateImpl("endorse"));
      provider.setCurrentStateName("endorse");
      provider.setHoursSpent("endorse", 8.0);

      Assert.assertEquals(8.0, provider.getHoursSpent("endorse"));
   }

   @Test(expected = OseeStateException.class)
   public void removeAssignee_exception() throws OseeCoreException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      provider.removeAssignee(joe);
   }

   @Test
   public void testIsSame() throws OseeArgumentException {
      WorkStateProviderImpl provider = new WorkStateProviderImpl(new MockWorkStateFactory());
      WorkStateImpl endorse = new WorkStateImpl("endorse");
      provider.addState(endorse);

      WorkStateImpl endorse2 = new WorkStateImpl("endorse");

      Assert.assertTrue(provider.isSame(endorse2));
      endorse.setHoursSpent(4);
      endorse.setPercentComplete(23);
      endorse2.setHoursSpent(4);
      endorse2.setPercentComplete(23);
      Assert.assertTrue(provider.isSame(endorse2));

      endorse2.setHoursSpent(5);
      Assert.assertFalse(provider.isSame(endorse2));
      endorse2.setHoursSpent(4);
      Assert.assertTrue(provider.isSame(endorse2));

      endorse2.setPercentComplete(5);
      Assert.assertFalse(provider.isSame(endorse2));
      endorse2.setPercentComplete(23);
      Assert.assertTrue(provider.isSame(endorse2));

      endorse2.addAssignee(joe);
      Assert.assertFalse(provider.isSame(endorse2));
      endorse.addAssignee(joe);
      Assert.assertTrue(provider.isSame(endorse2));

      WorkStateImpl analyze = new WorkStateImpl("blah");
      Assert.assertFalse(provider.isSame(analyze));

   }

}
