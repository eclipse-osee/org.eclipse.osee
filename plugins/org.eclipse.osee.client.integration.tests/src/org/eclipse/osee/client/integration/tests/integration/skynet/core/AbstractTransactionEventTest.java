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
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperationWithListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractTransactionEventTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   protected AbstractTransactionEventTest() {
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
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      MockTransactionEventListener listener = new MockTransactionEventListener();

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(listener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());
   }

   @org.junit.Test
   public void testPurgeTransaction() throws Exception {
      String START_NAME = getClass().getSimpleName();
      String CHANGE_NAME = START_NAME + " - changed";

      // Create and persist new artifact
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON, START_NAME);
      newArt.persist(getClass().getSimpleName());
      Assert.assertEquals(START_NAME, newArt.getName());
      Assert.assertFalse(newArt.isDirty());

      // Make a change that we can delete
      newArt.setName(CHANGE_NAME);
      SkynetTransaction transaction = TransactionManager.createTransaction(newArt.getBranch(), "changed");
      newArt.persist(transaction);
      TransactionId transIdToDelete = transaction.execute();

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
      IOperation operation = PurgeTransactionOperationWithListener.getPurgeTransactionOperation(transIdToDelete);
      Asserts.assertOperation(operation, IStatus.OK);

      // Verify that all stuff reverted
      Assert.assertTrue(listener.wasEventReceived());
      Assert.assertEquals(1, listener.getEventCount());

      TransactionEvent resultTransEvent = listener.getResultTransEvent();
      assertSender(listener.getResultSender());
      Assert.assertNotNull(resultTransEvent);

      Assert.assertEquals(TransactionEventType.Purged, resultTransEvent.getEventType());

      Collection<TransactionChange> transactionChanges = resultTransEvent.getTransactionChanges();
      Assert.assertEquals(1, transactionChanges.size());
      Assert.assertEquals(1, transactionChanges.iterator().next().getArtifacts().size());
      Assert.assertEquals(newArt.getGuid(),
         transactionChanges.iterator().next().getArtifacts().iterator().next().getGuid());
      Assert.assertTrue(transactionChanges.iterator().next().getArtifacts().iterator().next().isTypeEqual(
         newArt.getArtifactTypeId()));
   }

   private void assertSender(Sender sender) {
      Assert.assertNotNull(sender);
      boolean senderType = isRemoteTest() ? sender.isRemote() : sender.isLocal();
      Assert.assertTrue(senderType);
   }

   private static final class MockTransactionEventListener implements ITransactionEventListener {
      private TransactionEvent resultTransEvent;
      private Sender resultSender;
      private int eventCount;

      public MockTransactionEventListener() {
         clear();
      }

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         incrementEventCount();
         resultTransEvent = transEvent;
         resultSender = sender;
      }

      public TransactionEvent getResultTransEvent() {
         return resultTransEvent;
      }

      public Sender getResultSender() {
         return resultSender;
      }

      public int getEventCount() {
         return eventCount;
      }

      public boolean wasEventReceived() {
         return eventCount > 0;
      }

      private void incrementEventCount() {
         eventCount++;
      }

      public void clear() {
         eventCount = 0;
         resultSender = null;
         resultTransEvent = null;
      }
   }
}