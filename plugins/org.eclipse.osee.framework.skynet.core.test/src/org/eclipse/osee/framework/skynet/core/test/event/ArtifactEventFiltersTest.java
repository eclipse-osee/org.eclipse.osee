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
import org.eclipse.osee.framework.core.data.OseeBranch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.InternalEventManager2;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventFiltersTest {

   private ArtifactEvent resultArtifactEvent = null;
   private Sender resultSender = null;
   public static List<String> ignoreLogging = Arrays.asList("");
   private List<IEventFilter> eventFilters = null;

   @org.junit.Test
   public void testArtifactEventFilters() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      InternalEventManager2.internalRemoveAllListeners();
      InternalEventManager2.addListener(artifactEventListener);
      Assert.assertEquals(1, InternalEventManager2.getNumberOfListeners());

      testArtifactEventFilters__branchFilter();
      testArtifactEventFilters__branchFilterArtifactType();

      TestUtil.severeLoggingEnd(monitorLog);
   }

   private void testArtifactEventFilters__branchFilter() throws Exception {
      // Create dummy artifact event
      String branchGuid = GUID.create();
      ArtifactEvent testArtifactEvent = new ArtifactEvent();
      testArtifactEvent.setBranchGuid(branchGuid);
      testArtifactEvent.setNetworkSender(getDummyRemoteNetworkSender());

      // Register set filters to null to see if event comes through
      eventFilters = null;
      resultArtifactEvent = null;
      resultSender = null;

      // Send dummy event
      Sender sender = new Sender(new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0"));
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());
      Assert.assertTrue(resultSender.isRemote());

      // Reset event filters only allow events from this branch
      eventFilters = OseeEventManager.getEventFiltersForBranch("Test Branch", resultArtifactEvent.getBranchGuid());
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());

      // Reset event filters only filter out this branch
      String otherBranchGuid = GUID.create();
      eventFilters = OseeEventManager.getEventFiltersForBranch("Other Test Branch", otherBranchGuid);
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

   }

   private void testArtifactEventFilters__branchFilterArtifactType() throws Exception {
      // Create dummy artifact event
      String branchGuid = GUID.create();
      String artifactTypeGuid = GUID.create();
      ArtifactEvent testArtifactEvent = new ArtifactEvent();
      testArtifactEvent.setBranchGuid(branchGuid);
      testArtifactEvent.setNetworkSender(getDummyRemoteNetworkSender());
      testArtifactEvent.getArtifacts().add(
            new EventBasicGuidArtifact(EventModType.Added, branchGuid, artifactTypeGuid, GUID.create()));

      // Reset event filters only allow events from this branch
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(new OseeBranch("Test Branch", branchGuid)));
      eventFilters.add(new ArtifactTypeEventFilter(new ArtifactType(artifactTypeGuid, "Test Art Type", false)));
      resultArtifactEvent = null;
      resultSender = null;

      // Send dummy event
      Sender sender = new Sender(new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0"));
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());

      // Reset event filters to only send other artifact type of this branch
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(new OseeBranch("Test Branch", branchGuid)));
      eventFilters.add(new ArtifactTypeEventFilter(new ArtifactType(GUID.create(), "Other Test Art Type", false)));
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

      // Reset event filters to only send OTHER branch events
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(new OseeBranch("Other Test Branch", GUID.create())));
      eventFilters.add(new ArtifactTypeEventFilter(new ArtifactType(artifactTypeGuid, "Test Art Type", false)));
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      InternalEventManager2.processEventArtifactsAndRelations(sender, testArtifactEvent);

      Thread.sleep(4000);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

   }

   private NetworkSender getDummyRemoteNetworkSender() {
      return new NetworkSender(this.getClass(), GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0");
   }
   private class ArtifactEventListener implements IArtifactEventListener {

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return eventFilters;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         resultArtifactEvent = artifactEvent;
         resultSender = sender;
      }

   }

   @org.junit.Before
   public void setUpTest() {
      OseeEventManager.setNewEvents(true);
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private ArtifactEventListener artifactEventListener = new ArtifactEventListener();

   public void clearEventCollections() {
      resultArtifactEvent = null;
   }

}