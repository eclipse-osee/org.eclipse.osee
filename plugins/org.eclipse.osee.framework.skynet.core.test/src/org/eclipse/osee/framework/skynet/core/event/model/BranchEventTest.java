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
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.DeleteBranchOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;

/**
 * @author Donald G. Dunne
 */
public class BranchEventTest {

   public static List<String> ignoreLogging = Arrays.asList("");
   private static String BRANCH_NAME_PREFIX = "BranchEventManagerTest";
   private static String TOP_LEVEL_BRANCH_NAME = String.format("%s - top level branch", BRANCH_NAME_PREFIX);
   private static Branch topLevel;

   private final BranchEventListener branchEventListener = new BranchEventListener();

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(branchEventListener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      TestUtil.severeLoggingEnd(monitorLog);
   }

   /**
    * If all branch tests take longer than 10seconds, fail test, something went wrong.
    */
   @org.junit.Test(timeout = 10000)
   public void testEvents() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      try {
         OseeEventManager.getPreferences().setPendRunning(true);

         testEvents__topLevelAdded();
         Branch workingBranch = testEvents__workingAdded();
         testEvents__workingRenamed(workingBranch);
         testEvents__typeChange(workingBranch);
         testEvents__stateChange(workingBranch);
         testEvents__deleted(workingBranch);
         testEvents__purged();
         Branch committedBranch = testEvents__committed();
         testEvents__changeArchiveState(committedBranch);

         TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
      } finally {
         OseeEventManager.getPreferences().setPendRunning(false);
         OseeEventManager.removeListener(branchEventListener);
      }
   }

   private Branch testEvents__changeArchiveState(Branch committedBranch) throws Exception {
      branchEventListener.reset();

      Assert.assertNotNull(committedBranch);
      final String guid = committedBranch.getGuid();
      Assert.assertEquals(BranchArchivedState.ARCHIVED, committedBranch.getArchiveState());
      BranchManager.updateBranchArchivedState(null, committedBranch.getId(), committedBranch.getGuid(),
         BranchArchivedState.UNARCHIVED);

      verifyReceivedBranchStates(BranchEventType.ArchiveStateUpdated, guid);

      Assert.assertEquals(BranchArchivedState.UNARCHIVED, committedBranch.getArchiveState());
      Assert.assertFalse(committedBranch.isEditable());
      return committedBranch;
   }

   private Branch testEvents__committed() throws Exception {
      branchEventListener.reset();

      Branch workingBranch =
         BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - to commit", UserManager.getUser());

      Assert.assertNotNull(workingBranch);

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, workingBranch);
      newArt.persist(getClass().getSimpleName());
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(topLevel, workingBranch);
      BranchManager.commitBranch(null, conflictManager, true, true);

      verifyReceivedBranchStates(BranchEventType.Committed, guid);

      Assert.assertEquals(BranchState.COMMITTED, workingBranch.getBranchState());
      Assert.assertFalse(workingBranch.isEditable());
      return workingBranch;
   }

   private Branch testEvents__purged() throws Exception {
      branchEventListener.reset();

      Branch workingBranch =
         BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - to purge", UserManager.getUser());

      Assert.assertNotNull(workingBranch);

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);

      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(workingBranch, false));

      verifyReceivedBranchStates(BranchEventType.Purged, guid);

      Assert.assertEquals(BranchState.CREATED, workingBranch.getBranchState());
      Assert.assertEquals(StorageState.PURGED, workingBranch.getStorageState());
      Assert.assertFalse(workingBranch.isEditable());
      try {
         BranchManager.getBranchByGuid(guid);
         Assert.fail("Branch should not exist");
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
      return workingBranch;
   }

   private Branch testEvents__deleted(Branch workingBranch) throws Exception {
      branchEventListener.reset();

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertNotSame(BranchState.DELETED, workingBranch.getBranchState());

      Operations.executeWork(new DeleteBranchOperation(workingBranch));

      verifyReceivedBranchStates(BranchEventType.Deleted, guid);

      Assert.assertEquals(BranchState.DELETED, workingBranch.getBranchState());
      return workingBranch;
   }

   private Branch testEvents__stateChange(Branch workingBranch) throws Exception {
      branchEventListener.reset();

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertEquals(BranchState.CREATED, workingBranch.getBranchState());
      BranchManager.updateBranchState(null, workingBranch.getId(), workingBranch.getGuid(), BranchState.MODIFIED);

      verifyReceivedBranchStates(BranchEventType.StateUpdated, guid);

      Assert.assertEquals(BranchState.MODIFIED, workingBranch.getBranchState());
      return workingBranch;
   }

   private Branch testEvents__typeChange(Branch workingBranch) throws Exception {
      branchEventListener.reset();
      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertEquals(BranchType.WORKING, workingBranch.getBranchType());
      BranchManager.updateBranchType(null, workingBranch.getId(), workingBranch.getGuid(), BranchType.BASELINE);

      verifyReceivedBranchStates(BranchEventType.TypeUpdated, guid);

      Assert.assertEquals(BranchType.BASELINE, workingBranch.getBranchType());
      return workingBranch;
   }

   private Branch testEvents__workingRenamed(Branch workingBranch) throws Exception {
      branchEventListener.reset();

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      String newName = BRANCH_NAME_PREFIX + " - working renamed";
      workingBranch.setName(newName);
      BranchManager.persist(workingBranch);

      verifyReceivedBranchStates(BranchEventType.Renamed, guid);

      Assert.assertEquals(newName, workingBranch.getName());
      Assert.assertNotNull(BranchManager.getBranchesByName(newName));
      return workingBranch;
   }

   private Branch testEvents__workingAdded() throws Exception {
      branchEventListener.reset();

      Branch workingBranch =
         BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - working", UserManager.getUser());
      Assert.assertNotNull(workingBranch);

      verifyReceivedBranchStates(BranchEventType.Added, null);

      return workingBranch;
   }

   private Branch testEvents__topLevelAdded() throws Exception {
      branchEventListener.reset();

      String guid = GUID.create();
      String branchName = TOP_LEVEL_BRANCH_NAME;
      IOseeBranch branchToken = TokenFactory.createBranch(guid, branchName);
      topLevel = BranchManager.createTopLevelBranch(branchToken);
      Assert.assertNotNull(topLevel);

      verifyReceivedBranchStates(BranchEventType.Added, guid);

      return topLevel;
   }

   private void verifyReceivedBranchStates(BranchEventType expectedEnumState, String expectedBranchGuid) throws InterruptedException {
      Pair<Sender, BranchEvent> update = branchEventListener.getResults();
      Sender receivedSender = update.getFirst();
      BranchEvent receivedBranchEvent = update.getSecond();

      Assert.assertEquals(expectedEnumState, receivedBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(receivedSender.isRemote());
      } else {
         Assert.assertTrue(receivedSender.isLocal());
      }

      if (Strings.isValid(expectedBranchGuid)) {
         Assert.assertEquals(expectedBranchGuid, receivedBranchEvent.getBranchGuid());
      }
   }

   protected boolean isRemoteTest() {
      return false;
   }

   @AfterClass
   public static void cleanUp() throws OseeCoreException {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(topLevel, true));
   }

   private class BranchEventListener implements IBranchEventListener {
      private BranchEvent branchEvent;
      private Sender sender;
      private boolean receivedUpdate = false;

      public synchronized void reset() {
         receivedUpdate = false;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }

      @Override
      public synchronized void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         this.branchEvent = branchEvent;
         this.sender = sender;
         receivedUpdate = true;
         notify();
      }

      public synchronized Pair<Sender, BranchEvent> getResults() throws InterruptedException {
         while (!receivedUpdate) {
            wait();
         }
         receivedUpdate = false;
         return new Pair<Sender, BranchEvent>(sender, branchEvent);
      }
   };
}