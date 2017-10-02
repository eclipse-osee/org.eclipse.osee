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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteBranchOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class BranchEventTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private IOseeBranch mainBranch;
   private BranchId topLevel;

   private BranchEventListener branchEventListener;

   @Before
   public void setup() {
      String topLevelBranchName = String.format("%s_TOP_LEVEL", method.getQualifiedTestName());
      mainBranch = IOseeBranch.create(topLevelBranchName);

      branchEventListener = new BranchEventListener();
   }

   @After
   public void tearDown()  {
      if (topLevel != null) {
         boolean pending = OseeEventManager.getPreferences().isPendRunning();
         try {
            OseeEventManager.getPreferences().setPendRunning(true);
            Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(mainBranch, true));
         } finally {
            OseeEventManager.getPreferences().setPendRunning(pending);
         }
      }
   }

   @Test
   public void testRegistration() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(branchEventListener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());
   }

   /**
    * If all branch tests take longer than 20seconds, fail test, something went wrong.
    */
   @Test(timeout = 20000)
   public void testEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(branchEventListener);

      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());
      boolean pending = OseeEventManager.getPreferences().isPendRunning();
      try {
         OseeEventManager.getPreferences().setPendRunning(true);

         topLevel = testEvents__topLevelAdded();

         IOseeBranch workingBranch = testEvents__workingAdded();
         testEvents__workingRenamed(workingBranch);
         testEvents__typeChange(workingBranch);
         testEvents__stateChange(workingBranch);
         testEvents__deleted(workingBranch);
         testEvents__purged();
         IOseeBranch committedBranch = testEvents__committed();
         testEvents__changeArchiveState(committedBranch);

      } finally {
         OseeEventManager.getPreferences().setPendRunning(pending);
         OseeEventManager.removeListener(branchEventListener);
      }
   }

   private void testEvents__changeArchiveState(IOseeBranch committedBranch) throws Exception {
      branchEventListener.reset();

      Assert.assertNotNull(committedBranch);
      Assert.assertTrue(BranchManager.isArchived(committedBranch));
      BranchManager.setArchiveState(committedBranch, BranchArchivedState.UNARCHIVED);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.ArchiveStateUpdated,
         committedBranch);

      Assert.assertFalse(BranchManager.isArchived(committedBranch));
      Assert.assertFalse(BranchManager.isEditable(committedBranch));
   }

   private IOseeBranch testEvents__committed() throws Exception {
      IOseeBranch workingBranch =
         BranchManager.createWorkingBranch(mainBranch, method.getQualifiedTestName() + " - to commit");

      Assert.assertNotNull(workingBranch);

      Assert.assertNotNull(workingBranch);
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, workingBranch);
      newArt.persist(getClass().getSimpleName());
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(mainBranch, workingBranch);
      branchEventListener.reset();
      BranchManager.commitBranch(null, conflictManager, true, true);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.Committing, workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(1), BranchEventType.Committed, workingBranch);

      Assert.assertEquals(BranchState.COMMITTED, BranchManager.getState(workingBranch));
      Assert.assertFalse(BranchManager.isEditable(workingBranch));
      return workingBranch;
   }

   private void testEvents__purged() throws Exception {
      BranchId workingBranch =
         BranchManager.createWorkingBranch(mainBranch, method.getQualifiedTestName() + " - to purge");

      Assert.assertNotNull(workingBranch);

      Assert.assertNotNull(workingBranch);

      branchEventListener.reset();
      Branch fullBranch = BranchManager.getBranch(workingBranch); // get full branch before purge because it will be decached
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(workingBranch, false));

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.Purging, workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(1), BranchEventType.Purged, workingBranch);

      Assert.assertEquals(BranchState.PURGED, fullBranch.getBranchState());
      Assert.assertFalse(BranchManager.isEditable(fullBranch));
      Assert.assertFalse("Branch should not exist", BranchManager.branchExists(workingBranch));
   }

   private void testEvents__deleted(IOseeBranch workingBranch) throws Exception {
      Assert.assertNotNull(workingBranch);
      Assert.assertNotSame(BranchState.DELETED, BranchManager.getState(workingBranch));

      branchEventListener.reset();
      Operations.executeWorkAndCheckStatus(new DeleteBranchOperation(workingBranch));

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.StateUpdated, workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(1), BranchEventType.ArchiveStateUpdated,
         workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(2), BranchEventType.Deleting, workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(3), BranchEventType.StateUpdated, workingBranch);
      verifyReceivedBranchStatesEvent(branchEventListener.getResults(4), BranchEventType.Deleted, workingBranch);

      Assert.assertEquals(BranchState.DELETED, BranchManager.getState(workingBranch));
   }

   private void testEvents__stateChange(IOseeBranch workingBranch) throws Exception {
      branchEventListener.reset();

      Assert.assertNotNull(workingBranch);
      Assert.assertEquals(BranchState.CREATED, BranchManager.getState(workingBranch));
      BranchManager.setState(workingBranch, BranchState.MODIFIED);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.StateUpdated, workingBranch);

      Assert.assertEquals(BranchState.MODIFIED, BranchManager.getState(workingBranch));
   }

   private void testEvents__typeChange(IOseeBranch workingBranch) throws Exception {
      branchEventListener.reset();
      Assert.assertNotNull(workingBranch);
      Assert.assertTrue(BranchManager.getType(workingBranch).isWorkingBranch());
      BranchManager.setType(workingBranch, BranchType.BASELINE);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.TypeUpdated, workingBranch);

      Assert.assertTrue(BranchManager.getType(workingBranch).isBaselineBranch());
   }

   private void testEvents__workingRenamed(BranchId workingBranch) throws Exception {
      branchEventListener.reset();

      Assert.assertNotNull(workingBranch);
      String newName = method.getQualifiedTestName() + " - working renamed";
      BranchManager.setName(workingBranch, newName);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.Renamed, workingBranch);

      Assert.assertEquals(newName, BranchManager.getBranchName(workingBranch));
   }

   private IOseeBranch testEvents__workingAdded() throws Exception {
      branchEventListener.reset();

      IOseeBranch workingBranch =
         BranchManager.createWorkingBranch(mainBranch, method.getQualifiedTestName() + " - working");
      Assert.assertNotNull(workingBranch);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.Added, null);

      return workingBranch;
   }

   private BranchId testEvents__topLevelAdded() throws Exception {
      branchEventListener.reset();
      BranchId branch = BranchManager.createTopLevelBranch(mainBranch);
      Assert.assertNotNull(branch);

      verifyReceivedBranchStatesEvent(branchEventListener.getResults(0), BranchEventType.Added, mainBranch);

      return branch;
   }

   private void verifyReceivedBranchStatesEvent(Pair<Sender, BranchEvent> eventPair, BranchEventType expectedEnumState, BranchId expectedBranch) {
      Sender receivedSender = eventPair.getFirst();
      BranchEvent receivedBranchEvent = eventPair.getSecond();

      Assert.assertEquals(expectedEnumState, receivedBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(receivedSender.isRemote());
      } else {
         Assert.assertTrue(receivedSender.isLocal());
      }

      if (expectedBranch != null) {
         Assert.assertEquals(expectedBranch, receivedBranchEvent.getSourceBranch());
      }
   }

   protected boolean isRemoteTest() {
      return false;
   }

   private class BranchEventListener implements IBranchEventListener {
      private final BranchEvent[] events = new BranchEvent[5];
      private final Sender[] senders = new Sender[events.length];

      public synchronized void reset() {
         Arrays.fill(events, null);
         Arrays.fill(senders, null);
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }

      @Override
      public synchronized void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         for (int i = 0; i < events.length; i++) {
            if (events[i] == null) {
               events[i] = branchEvent;
               senders[i] = sender;
               break;
            }
         }
         notify();
      }

      public synchronized Pair<Sender, BranchEvent> getResults(int sequence) throws InterruptedException {
         while (events[sequence] == null) {
            wait();
         }
         return new Pair<Sender, BranchEvent>(senders[sequence], events[sequence]);
      }
   };
}