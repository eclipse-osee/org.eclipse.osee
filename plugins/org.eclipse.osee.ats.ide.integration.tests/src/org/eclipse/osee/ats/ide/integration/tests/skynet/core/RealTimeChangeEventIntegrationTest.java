/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test for the change-event backbone the web SSE real-time feature is built on.
 * <p>
 * The SSE server hooks (TransactionCommitTopic -> SseTransactionCommitHandler ->
 * OseeSseEndpoint.broadcast, and the ActiveMqSseBridge desktop relay) run in the server JVM and are
 * documented in {@code docs/ai/web/sse-real-time.md}. They are deliberately NOT reachable from this
 * client-side test bundle, so this test does not (and cannot) assert on them directly without
 * mocking, which would prove nothing.
 * <p>
 * What it does assert, against a live server, is the real backbone that every SSE broadcast rides
 * on: a genuine transaction commit ({@link SkynetTransaction#execute()}, the same
 * {@code TransactionBuilder.commit()} chokepoint the server's {@code SseTransactionCommitHandler}
 * subscribes to) round-trips through the server and fires the client framework
 * {@link ArtifactEvent} for the changed artifact. If this backbone breaks, no web client would ever
 * be notified. The client-side event delivery is verified deterministically; the server-side SSE
 * fan-out over that same commit event is covered by the {@code org.eclipse.osee.orcs.rest.test}
 * unit tests (ServerToServerEvent / ActiveMqSseBridge / PresenceRegistry) and the web Playwright
 * end-to-end tests.
 *
 * @author Boeing
 */
public class RealTimeChangeEventIntegrationTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private boolean priorPendRunning;

   @Before
   public void setup() {
      // Force synchronous event dispatch so the commit's framework event is delivered in-thread,
      // making the assertion deterministic (no sleep/poll race).
      priorPendRunning = OseeEventManager.getPreferences().isPendRunning();
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(priorPendRunning);
      OseeEventManager.removeAllListeners();
   }

   @Test
   public void testAttributeCommitFiresArtifactChangeEvent() throws Exception {
      String name = getClass().getSimpleName() + " Art";
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON, name);
      artifact.persist(getClass().getSimpleName() + " create");

      ChangeEventListener listener = new ChangeEventListener(artifact.getArtifactType());
      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(listener);

      // A genuine attribute edit + commit -- the same chokepoint the SSE broadcast rides on.
      artifact.setName(name + " Renamed");
      SkynetTransaction transaction =
         TransactionManager.createTransaction(artifact.getBranch(), getClass().getSimpleName() + " edit");
      artifact.persist(transaction);
      TransactionToken tx = transaction.execute();

      Assert.assertTrue("Commit did not produce a valid transaction", tx.isValid());
      Assert.assertTrue("No ArtifactEvent was delivered for the committed change",
         listener.wasEventReceived());

      ArtifactEvent event = listener.getEvent();
      Assert.assertNotNull(event);
      boolean sawOurArtifact = event.getArtifacts().stream().anyMatch(
         relevant -> relevant.getGuid().equals(artifact.getGuid()));
      Assert.assertTrue("The committed artifact was not in the delivered ArtifactEvent", sawOurArtifact);
   }

   /**
    * Captures the first {@link ArtifactEvent} delivered for a given artifact type. With
    * {@code pendRunning} true the event is delivered synchronously during {@code execute()}, so a
    * plain volatile flag suffices (no wait/notify needed).
    */
   private static final class ChangeEventListener implements IArtifactEventListener {
      private final ArtifactTypeEventFilter eventFilter;
      private volatile ArtifactEvent event;

      private ChangeEventListener(ArtifactTypeToken artifactType) {
         this.eventFilter = new ArtifactTypeEventFilter(artifactType);
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         if (event == null) {
            event = artifactEvent;
         }
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return Collections.singletonList(eventFilter);
      }

      private boolean wasEventReceived() {
         return event != null;
      }

      private ArtifactEvent getEvent() {
         return event;
      }
   }
}
