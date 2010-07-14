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
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event2.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class TransactionEventTest {

   private TransactionEvent resultTransEvent = null;
   private Sender resultSender = null;
   public static List<String> ignoreLogging = Arrays.asList("");

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      FrameworkEventManager.internalRemoveAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.addListener(transEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.removeListener(transEventListener);
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

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

      Thread.sleep(3000);

      // Add listener for delete transaction event
      FrameworkEventManager.internalRemoveAllListeners();
      FrameworkEventManager.addListener(transEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      // Delete it
      IOperation operation = new PurgeTransactionOperation(Activator.getInstance(), false, transIdToDelete);
      Operations.executeAndPend(operation, false);

      Thread.sleep(8000);

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

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
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

   @org.junit.Before
   public void setUpTest() {
      OseeEventManager.setNewEvents(true);
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private TransactionEventListener transEventListener = new TransactionEventListener();

   public void clearEventCollections() {
      resultTransEvent = null;
   }

}