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
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class BranchEventFiltersTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private IOseeBranch branch;
   private BranchEventListener listener;
   private BranchEvent event;
   private Sender sender;

   @Before
   public void setup() {
      listener = new BranchEventListener();
      OseeEventManager.getPreferences().setPendRunning(true);

      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(listener);

      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      branch = TokenFactory.createBranch(GUID.create(), method.getQualifiedTestName());

      event = new BranchEvent(BranchEventType.Renamed, branch.getGuid());

      NetworkSender networkSender =
         new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0");
      sender = Sender.createSender(networkSender);
   }

   @After
   public void cleanup() {
      OseeEventManager.removeAllListeners();
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   @Test
   public void testNullBranchEventFilters() throws Exception {
      // Register set filters to null to see if event comes through
      processBranchEvent(sender, event);

      // Test that event did come through
      BranchEvent actualEvent = listener.getBranchEvent();
      Sender actualSender = listener.getSender();

      Assert.assertNotNull(actualEvent);
      Assert.assertEquals(BranchEventType.Renamed, actualEvent.getEventType());
      Assert.assertEquals(branch.getGuid(), actualEvent.getBranchGuid());
      Assert.assertEquals(true, actualSender.isRemote());
   }

   @Test
   public void testBranchEventFilters() throws Exception {
      // Reset event filters only allow events from this branch
      List<IEventFilter> filters = OseeEventManager.getEventFiltersForBranch(branch);
      listener.setEventFilters(filters);

      // Re-send dummy event
      processBranchEvent(sender, event);

      // Test that event did come through
      BranchEvent actualEvent = listener.getBranchEvent();
      Sender actualSender = listener.getSender();

      Assert.assertNotNull(actualEvent);
      Assert.assertEquals(BranchEventType.Renamed, actualEvent.getEventType());
      Assert.assertEquals(branch.getGuid(), actualEvent.getBranchGuid());
      Assert.assertEquals(true, actualSender.isRemote());
   }

   @Test
   public void testBranchEventFiltersNotMatch() throws Exception {
      // Reset event filters only filter out this branch
      String otherBranchGuid = GUID.create();
      IOseeBranch otherBranchToken = TokenFactory.createBranch(otherBranchGuid, "Other Test Branch");
      List<IEventFilter> filters = OseeEventManager.getEventFiltersForBranch(otherBranchToken);
      listener.setEventFilters(filters);

      // Re-send dummy event
      processBranchEvent(sender, event);

      // Test that event did NOT come through
      Assert.assertNull(listener.getBranchEvent());
   }

   private static void processBranchEvent(Sender sender, BranchEvent branchEvent) throws OseeCoreException {
      OseeEventManager.internalTestProcessBranchEvent(sender, branchEvent);
   }

   private static final class BranchEventListener implements IBranchEventListener {

      private BranchEvent branchEvent;
      private Sender sender;
      private List<IEventFilter> eventFilters;

      @Override
      public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         this.branchEvent = branchEvent;
         this.sender = sender;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return eventFilters;
      }

      public BranchEvent getBranchEvent() {
         return branchEvent;
      }

      public Sender getSender() {
         return sender;
      }

      public void setEventFilters(List<IEventFilter> eventFilters) {
         this.eventFilters = eventFilters;
      }
   }

}