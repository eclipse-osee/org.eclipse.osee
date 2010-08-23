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

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class BranchEventFiltersTest {

   private BranchEvent resultBranchEvent = null;
   private Sender resultSender = null;
   public static List<String> ignoreLogging = Arrays.asList("");
   private List<IEventFilter> eventFilters = null;

   @Before
   public void setup() {
      OseeEventManager.getPreferences().setNewEvents(true);
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   @org.junit.Test
   public void testBranchEventFilters() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(branchEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Create dummy branch event
      String branchGuid = GUID.create();
      BranchEvent testBranchEvent = new BranchEvent(BranchEventType.Renamed, branchGuid);

      // Register set filters to null to see if event comes through
      eventFilters = null;
      resultBranchEvent = null;
      resultSender = null;

      // Send dummy event
      Sender sender = new Sender(new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0"));
      processBranchEvent(sender, testBranchEvent);

      // Test that event did come through
      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Renamed, resultBranchEvent.getEventType());
      Assert.assertTrue(resultSender.isRemote());
      Assert.assertEquals(branchGuid, resultBranchEvent.getBranchGuid());

      // Reset event filters only allow events from this branch
      eventFilters = OseeEventManager.getEventFiltersForBranch("Test Branch", resultBranchEvent.getBranchGuid());
      resultBranchEvent = null;
      resultSender = null;

      // Re-send dummy event
      processBranchEvent(sender, testBranchEvent);

      // Test that event did come through
      Assert.assertNotNull(resultBranchEvent);
      Assert.assertEquals(BranchEventType.Renamed, resultBranchEvent.getEventType());
      Assert.assertTrue(resultSender.isRemote());
      Assert.assertEquals(branchGuid, resultBranchEvent.getBranchGuid());

      // Reset event filters only filter out this branch
      String otherBranchGuid = GUID.create();
      eventFilters = OseeEventManager.getEventFiltersForBranch("Other Test Branch", otherBranchGuid);
      resultBranchEvent = null;
      resultSender = null;

      // Re-send dummy event
      processBranchEvent(sender, testBranchEvent);

      // Test that event did NOT come through
      Assert.assertNull(resultBranchEvent);

      TestUtil.severeLoggingEnd(monitorLog);
   }

   private static void processBranchEvent(Sender sender, BranchEvent branchEvent) {
      OseeEventManager.internalTestProcessBranchEvent(sender, branchEvent);
   }

   private class BranchEventListener implements IBranchEventListener {

      @Override
      public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         resultBranchEvent = branchEvent;
         resultSender = sender;
      }

      @Override
      public void handleBranchEventREM1(Sender sender, BranchEventType branchModType, int branchId) {
         // do nothing, this is legacy branch handler call
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return eventFilters;
      }
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private final BranchEventListener branchEventListener = new BranchEventListener();

}