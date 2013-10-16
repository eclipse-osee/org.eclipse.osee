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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventFiltersTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private final ArtifactEventListener artifactEventListener = new ArtifactEventListener();
   private ArtifactEvent resultArtifactEvent = null;
   private Sender resultSender = null;
   public static List<String> ignoreLogging = Arrays.asList("");
   private List<IEventFilter> eventFilters = null;

   @Before
   public void setup() {
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   @org.junit.Test
   public void testArtifactEventFilters() throws Exception {
      OseeEventManager.removeAllListeners();
      OseeEventManager.addListener(artifactEventListener);

      testArtifactEventFilters__artifactTypeInheritance();
      testArtifactEventFilters__branchFilter();
      testArtifactEventFilters__branchFilterArtifactType();
   }

   private void testArtifactEventFilters__artifactTypeInheritance() throws Exception {
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.Requirement);

      EventBasicGuidArtifact guidArt =
         new EventBasicGuidArtifact(EventModType.Added, GUID.create(), CoreArtifactTypes.Requirement.getGuid(),
            GUID.create());
      List<EventBasicGuidArtifact> guidArts = Arrays.asList(guidArt);

      Assert.assertTrue("Should match cause same artifact type", typeFilter.isMatchArtifacts(guidArts));

      guidArt.setArtTypeGuid(CoreArtifactTypes.SoftwareRequirement.getGuid());

      Assert.assertTrue("Should match cause SoftwareRequirement is subclass of Requirement",
         typeFilter.isMatchArtifacts(guidArts));

      typeFilter = new ArtifactTypeEventFilter(CoreArtifactTypes.SoftwareRequirement);
      guidArt.setArtTypeGuid(CoreArtifactTypes.Requirement.getGuid());

      Assert.assertFalse("Should NOT match cause Requirement is NOT subclass of Software Requirement",
         typeFilter.isMatchArtifacts(guidArts));

   }

   private void testArtifactEventFilters__branchFilter() throws Exception {
      // Create dummy artifact event
      String branchGuid = GUID.create();
      ArtifactEvent testArtifactEvent = new ArtifactEvent(TokenFactory.createBranch(branchGuid, "test branch"));
      testArtifactEvent.setNetworkSender(getDummyRemoteNetworkSender());

      // Register set filters to null to see if event comes through
      eventFilters = null;
      resultArtifactEvent = null;
      resultSender = null;

      // Send dummy event
      Sender sender =
         Sender.createSender(new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0"));
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());
      Assert.assertTrue(resultSender.isRemote());

      // Reset event filters only allow events from this branch
      IOseeBranch branchToken = TokenFactory.createBranch(resultArtifactEvent.getBranchGuid(), "Test Branch");
      eventFilters = OseeEventManager.getEventFiltersForBranch(branchToken);
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());

      // Reset event filters only filter out this branch
      String otherBranchGuid = GUID.create();
      IOseeBranch otherBranchToken = TokenFactory.createBranch(otherBranchGuid, "Other Test Branch");
      eventFilters = OseeEventManager.getEventFiltersForBranch(otherBranchToken);
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

   }

   private void testArtifactEventFilters__branchFilterArtifactType() throws Exception {
      // Create dummy artifact event
      String branchGuid = GUID.create();
      ArtifactEvent testArtifactEvent = new ArtifactEvent(TokenFactory.createBranch(branchGuid, "test branch 2"));
      testArtifactEvent.setNetworkSender(getDummyRemoteNetworkSender());
      testArtifactEvent.getArtifacts().add(
         new EventBasicGuidArtifact(EventModType.Added, branchGuid, CoreArtifactTypes.GeneralDocument.getGuid(),
            GUID.create()));
      testArtifactEvent.getArtifacts().add(
         new EventBasicGuidArtifact(EventModType.Added, branchGuid, CoreArtifactTypes.Component.getGuid(),
            GUID.create()));

      // Reset event filters only allow events from this branch
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(TokenFactory.createBranch(branchGuid, "Test Branch")));
      eventFilters.add(new ArtifactTypeEventFilter(CoreArtifactTypes.GeneralDocument));
      resultArtifactEvent = null;
      resultSender = null;

      // Send dummy event
      Sender sender =
         Sender.createSender(new NetworkSender(this, GUID.create(), "PC", "12345", "123.234.345.456", 34, "1.0.0"));
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event DID come through
      Assert.assertNotNull(resultArtifactEvent);
      Assert.assertEquals(branchGuid, resultArtifactEvent.getBranchGuid());

      // Reset event filters to only send other artifact type of this branch
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(TokenFactory.createBranch(branchGuid, "Test Branch")));
      eventFilters.add(new ArtifactTypeEventFilter(CoreArtifactTypes.Folder));
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

      // Reset event filters to only send OTHER branch events
      eventFilters = new ArrayList<IEventFilter>();
      eventFilters.add(new BranchGuidEventFilter(TokenFactory.createBranch(GUID.create(), "Other Test Branch")));
      eventFilters.add(new ArtifactTypeEventFilter(CoreArtifactTypes.GeneralDocument));
      resultArtifactEvent = null;
      resultSender = null;

      // Re-send dummy event
      processEventArtifactsAndRelations(sender, testArtifactEvent);

      // Test that event did NOT come through
      Assert.assertNull(resultArtifactEvent);

   }

   private static void processEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) throws OseeCoreException {
      OseeEventManager.internalTestProcessEventArtifactsAndRelations(sender, artifactEvent);
   }

   private static NetworkSender getDummyRemoteNetworkSender() {
      return new NetworkSender(ArtifactEventFiltersTest.class, GUID.create(), "PC", "12345", "123.234.345.456", 34,
         "1.0.0");
   }

   public void clearEventCollections() {
      resultArtifactEvent = null;
   }

   private final class ArtifactEventListener implements IArtifactEventListener {

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

}
