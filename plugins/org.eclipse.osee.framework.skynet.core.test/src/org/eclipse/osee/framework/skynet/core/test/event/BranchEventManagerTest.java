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
package org.eclipse.osee.framework.skynet.core.test.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.IBranchListener;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class BranchEventManagerTest {

   private BranchEvent resultBranchEvent = null;
   private Sender resultSender = null;
   public static List<String> ignoreLogging = Arrays.asList("");
   private static String BRANCH_NAME_PREFIX = "BranchEventManagerTest";

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      FrameworkEventManager.internalRemoveAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.removeListener(branchEventListener);
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      TestUtil.severeLoggingEnd(monitorLog);
   }

   @org.junit.Test
   public void testEvents() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FrameworkEventManager.internalRemoveAllListeners();
      FrameworkEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      Branch topLevel = testEvents__topLevelAdded();
      Branch workingBranch = testEvents__workingAdded(topLevel);
      testEvents__workingRenamed(workingBranch);
      testEvents__typeChange(workingBranch);
      testEvents__stateChange(workingBranch);
      testEvents__deleted(workingBranch);
      testEvents__purged(topLevel);
      Branch committedBranch = testEvents__committed(topLevel);
      testEvents__changeArchiveState(committedBranch);

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   private Branch testEvents__changeArchiveState(Branch committedBranch) throws Exception {
      clearEventCollections();

      Assert.assertNotNull(committedBranch);
      final String guid = committedBranch.getGuid();
      Assert.assertEquals(BranchArchivedState.ARCHIVED, committedBranch.getArchiveState());
      BranchManager.updateBranchArchivedState(null, committedBranch.getId(), committedBranch.getGuid(),
            BranchArchivedState.UNARCHIVED);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.ArchiveStateUpdated, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(BranchArchivedState.UNARCHIVED, committedBranch.getArchiveState());
      Assert.assertFalse(committedBranch.isEditable());
      return committedBranch;
   }

   private Branch testEvents__committed(Branch topLevel) throws Exception {
      clearEventCollections();
      Branch workingBranch =
            BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - to commit", UserManager.getUser());

      Assert.assertNotNull(workingBranch);

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, workingBranch);
      newArt.persist();
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(topLevel, workingBranch);
      BranchManager.commitBranch(null, conflictManager, true, true);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Committed, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(BranchState.COMMITTED, workingBranch.getBranchState());
      Assert.assertFalse(workingBranch.isEditable());
      return workingBranch;
   }

   private Branch testEvents__purged(Branch topLevel) throws Exception {
      clearEventCollections();
      Branch workingBranch =
            BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - to purge", UserManager.getUser());

      Assert.assertNotNull(workingBranch);

      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      BranchManager.purgeBranch(workingBranch);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Purged, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
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
      clearEventCollections();
      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertNotSame(BranchState.DELETED, workingBranch.getBranchState());
      BranchManager.deleteBranch(workingBranch);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Deleted, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(BranchState.DELETED, workingBranch.getBranchState());
      return workingBranch;
   }

   private Branch testEvents__stateChange(Branch workingBranch) throws Exception {
      clearEventCollections();
      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertEquals(BranchState.CREATED, workingBranch.getBranchState());
      BranchManager.updateBranchState(null, workingBranch.getId(), workingBranch.getGuid(), BranchState.MODIFIED);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.StateUpdated, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(BranchState.MODIFIED, workingBranch.getBranchState());
      return workingBranch;
   }

   private Branch testEvents__typeChange(Branch workingBranch) throws Exception {
      clearEventCollections();
      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      Assert.assertEquals(BranchType.WORKING, workingBranch.getBranchType());
      BranchManager.updateBranchType(null, workingBranch.getId(), workingBranch.getGuid(), BranchType.BASELINE);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.TypeUpdated, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(BranchType.BASELINE, workingBranch.getBranchType());
      return workingBranch;
   }

   private Branch testEvents__workingRenamed(Branch workingBranch) throws Exception {
      clearEventCollections();
      final String guid = workingBranch.getGuid();
      Assert.assertNotNull(workingBranch);
      String newName = BRANCH_NAME_PREFIX + " - working renamed";
      workingBranch.setName(newName);
      BranchManager.persist(workingBranch);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Renamed, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      Assert.assertEquals(newName, workingBranch.getName());
      Assert.assertNotNull(BranchManager.getBranchesByName(newName));
      return workingBranch;
   }

   private Branch testEvents__workingAdded(Branch topLevel) throws Exception {
      clearEventCollections();
      Branch workingBranch =
            BranchManager.createWorkingBranch(topLevel, BRANCH_NAME_PREFIX + " - working", UserManager.getUser());

      Assert.assertNotNull(workingBranch);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Added, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      return workingBranch;
   }

   private Branch testEvents__topLevelAdded() throws Exception {
      clearEventCollections();
      final String guid = GUID.create();
      Branch branch = BranchManager.createTopLevelBranch(new IOseeBranch() {

         @Override
         public String getName() {
            return BRANCH_NAME_PREFIX + " - top level branch";
         }

         @Override
         public String getGuid() {
            return guid;
         }
      });

      Assert.assertNotNull(branch);

      Thread.sleep(4000);

      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Added, resultBranchEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(guid, resultBranchEvent.getBranchGuid());
      return branch;
   }

   protected boolean isRemoteTest() {
      return false;
   }

   public class BranchEventListener implements IBranchListener {

      @Override
      public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         resultBranchEvent = branchEvent;
         resultSender = sender;
      }

      @Override
      public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      }
   }

   @org.junit.Before
   public void setUpTest() {
      OseeEventManager.setNewEvents(true);
   }

   @org.junit.BeforeClass
   @org.junit.AfterClass
   public static void cleanUp() throws OseeCoreException {

      for (Branch branch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.MERGE, BranchType.WORKING)) {
         if (branch.getName().startsWith(BRANCH_NAME_PREFIX)) {
            try {
               BranchManager.purgeBranch(branch);
            } catch (OseeCoreException ex) {

            }
         }
      }
      for (Branch branch : BranchManager.getBranches(BranchArchivedState.ALL, BranchType.BASELINE, BranchType.MERGE,
            BranchType.WORKING)) {
         if (branch.getName().startsWith(BRANCH_NAME_PREFIX)) {
            try {
               BranchManager.purgeBranch(branch);
            } catch (OseeCoreException ex) {

            }
         }
      }
   }
   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private BranchEventListener branchEventListener = new BranchEventListener();

   public void clearEventCollections() {
      resultBranchEvent = null;
   }

}