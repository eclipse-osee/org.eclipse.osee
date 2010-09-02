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
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionOperation;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class TransactionEventTest {

   private TransactionEvent resultTransEvent = null;
   private Sender resultSender = null;

   @Before
   public void setup() {
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(transEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(transEventListener);
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
      OseeEventManager.addListener(transEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Delete it
      IOperation operation = new PurgeTransactionOperation(Activator.getInstance(), false, transIdToDelete);
      Operations.executeAndPend(operation, false);

      // Verify that all stuff reverted
      Assert.assertNotNull(resultTransEvent);
      Assert.assertEquals(TransactionEventType.Purged, resultTransEvent.getEventType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(1, resultTransEvent.getTransactions().size());
      TransactionChange transChange = resultTransEvent.getTransactions().iterator().next();
      Assert.assertEquals(transIdToDelete, transChange.getTransactionId());
      Assert.assertEquals(1, transChange.getArtifacts().size());
      Assert.assertEquals(BranchManager.getCommonBranch().getGuid(), transChange.getBranchGuid());
      DefaultBasicGuidArtifact guidArt = transChange.getArtifacts().iterator().next();
      Assert.assertEquals(BranchManager.getCommonBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(CoreArtifactTypes.GeneralData.getGuid(), guidArt.getArtTypeGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? Arrays.asList("") : new ArrayList<String>()));
   }

   protected boolean isRemoteTest() {
      return false;
   }

   public class TransactionEventListener implements ITransactionEventListener {

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         resultTransEvent = transEvent;
         resultSender = sender;
      }
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private final TransactionEventListener transEventListener = new TransactionEventListener();

   public void clearEventCollections() {
      resultTransEvent = null;
   }

}