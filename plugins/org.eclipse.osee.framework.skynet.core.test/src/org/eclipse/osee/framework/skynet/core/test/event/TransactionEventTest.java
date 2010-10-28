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
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.test.mocks.Asserts;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionOperation;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.test.mocks.MockTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public abstract class TransactionEventTest {

   protected TransactionEventTest() {
      // Extra protection
   }

   @Before
   public void setup() {
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   protected abstract boolean isRemoteTest();

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      MockTransactionEventListener listener = new MockTransactionEventListener();

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(listener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      TestUtil.severeLoggingEnd(monitorLog);
   }

   @org.junit.Test
   public void testPurgeTransaction() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      String START_NAME = getClass().getSimpleName();
      String CHANGE_NAME = START_NAME + " - changed";

      // Create and persist new artifact
      Artifact newArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch(), START_NAME);
      newArt.persist();
      Assert.assertEquals(START_NAME, newArt.getName());
      Assert.assertFalse(newArt.isDirty());

      // Make a change that we can delete
      newArt.setName(CHANGE_NAME);
      SkynetTransaction transaction = new SkynetTransaction(newArt.getBranch(), "changed");
      int transIdToDelete = transaction.getTransactionNumber();
      newArt.persist(transaction);
      transaction.execute();
      if (!isRemoteTest()) {
         Assert.assertEquals(CHANGE_NAME, newArt.getName());
         Assert.assertFalse(newArt.isDirty());
      }

      // Add listener for delete transaction event
      OseeEventManager.removeAllListeners();

      MockTransactionEventListener listener = new MockTransactionEventListener();

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Delete it
      IOperation operation =
         new PurgeTransactionOperation(Activator.getInstance().getOseeDatabaseService(), false, transIdToDelete);
      Operations.executeWork(operation);
      Asserts.testOperation(operation, IStatus.OK);

      // Verify that all stuff reverted
      Assert.assertTrue(listener.wasEventReceived());
      Assert.assertEquals(1, listener.getEventCount());

      TransactionEvent resultTransEvent = listener.getResultTransEvent();
      assertSender(listener.getResultSender());
      Assert.assertNotNull(resultTransEvent);

      Assert.assertEquals(TransactionEventType.Purged, resultTransEvent.getEventType());

      Collection<TransactionChange> transactions = resultTransEvent.getTransactions();
      Assert.assertEquals(1, transactions.size());

      TransactionChange transChange = transactions.iterator().next();
      Assert.assertEquals(transIdToDelete, transChange.getTransactionId());
      Assert.assertEquals(CoreBranches.COMMON.getGuid(), transChange.getBranchGuid());

      Collection<DefaultBasicGuidArtifact> artifacts = transChange.getArtifacts();
      Assert.assertEquals(1, artifacts.size());

      DefaultBasicGuidArtifact guidArt = artifacts.iterator().next();

      Assert.assertEquals(CoreBranches.COMMON.getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());

      Assert.assertEquals(CoreArtifactTypes.GeneralData.getGuid(), guidArt.getArtTypeGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? Arrays.asList("") : new ArrayList<String>()));
   }

   private void assertSender(Sender sender) {
      Assert.assertNotNull(sender);
      boolean senderType = isRemoteTest() ? sender.isRemote() : sender.isLocal();
      Assert.assertTrue(senderType);
   }

}