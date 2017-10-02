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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test unit for {@link FrameworkEventToRemoteEventListener}
 *
 * @author Shawn F. Cook
 */
public class FrameworkEventToRemoteEventListenerTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final BranchId BRANCH = SAW_Bld_1;
   private static final String ARTIFACT_NAME_1 =
      FrameworkEventToRemoteEventListenerTest.class.getSimpleName() + ".Edit1";
   private static final TransactionId newArtTx = TransactionId.valueOf(12345);

   private RemoteNetworkSender1 networkSender;

   @Before
   public void setup() {
      networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(ArtifactEventTest.class.getName());
      networkSender.setSessionId("N23422.32");
      networkSender.setMachineName("A2340422");
      networkSender.setUserId("b345344");
      networkSender.setMachineIp("123.421.56.342");
      networkSender.setPort(485);
      networkSender.setClientVersion("123.2");
   }

   /**
    * We want to test FrameworkEventToRemoteEventListener.updateModifiedArtifact(). But it's nested rather deeply in
    * dependencies on other classes. So we need to fire up several other classes. So we make use of OseeEventManager
    * which already has a convenience method for passing in a sample RemoteEvent to the
    * FrameworkEventToRemoteEventListener. The test needed here is to verify that the Artifact.transactionId field is
    * updated by the recipient of a remote event.
    */
   @Test
   public void testUpdateModifiedArtifact() throws Exception {
      Artifact artifact = createArtifact(BRANCH, ARTIFACT_NAME_1);
      artifact.persist(getClass().getSimpleName());

      boolean eventBoolean = OseeEventManager.isDisableEvents();

      ArtifactTypeEventFilter eventFilter = new ArtifactTypeEventFilter(artifact.getArtifactType());
      UpdateArtifactListener listener = new UpdateArtifactListener(eventFilter);

      RemotePersistEvent1 remoteEvent = createRemoteEvent(artifact);
      remoteEvent.setTransaction(newArtTx);
      TransactionToken origArtTx = artifact.getTransaction();

      OseeEventManager.addListener(listener);
      OseeEventManager.setDisableEvents(false);
      try {
         synchronized (listener) {
            OseeEventManager.internalTestSendRemoteEvent(remoteEvent);
            listener.wait(30000);
         }
      } finally {
         OseeEventManager.setDisableEvents(eventBoolean);
         OseeEventManager.removeListener(listener);
      }

      Assert.assertTrue("Event completion was not received.", listener.wasUpdateReceived());
      Artifact artifactAfterUpdate = ArtifactQuery.getArtifactFromId(artifact.getGuid(), BRANCH);
      Assert.assertEquals(newArtTx, artifactAfterUpdate.getTransaction());

      //Reset artifact tx - just to be clean
      artifact.setTransactionId(origArtTx);
   }

   private RemotePersistEvent1 createRemoteEvent(Artifact modifiedArt) {
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setBranchGuid(BRANCH);
      RemoteBasicGuidArtifact1 remGuidArt = new RemoteBasicGuidArtifact1();
      remGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      remGuidArt.setBranch(BRANCH);
      remGuidArt.setArtifactType(modifiedArt.getArtifactTypeId());
      remGuidArt.setArtGuid(modifiedArt.getGuid());

      remoteEvent.getArtifacts().add(remGuidArt);
      return remoteEvent;
   }

   private static Artifact createArtifact(BranchId branch, String artifactName)  {
      Assert.assertNotNull(branch);
      Assert.assertNotNull(artifactName);
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, branch, artifactName);
      Assert.assertNotNull(artifact);
      return artifact;
   }

   private static final class UpdateArtifactListener implements IArtifactEventListener {
      private volatile boolean wasUpdateReceived;
      private ArtifactEvent artifactEvent;
      private final ArtifactTypeEventFilter eventFilter;

      public UpdateArtifactListener(ArtifactTypeEventFilter eventFilter) {
         this.eventFilter = eventFilter;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         this.artifactEvent = artifactEvent;

         synchronized (this) {
            wasUpdateReceived = true;
            notify();
         }
      }

      public synchronized boolean wasUpdateReceived() {
         return wasUpdateReceived;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return Collections.singletonList(eventFilter);

      }

      @Override
      public String toString() {
         return "UpdateArtifactListener [wasUpdateReceived=" + wasUpdateReceived + ", artifactEvent=" + artifactEvent + "]";
      }

   };
}
