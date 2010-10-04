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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelationReorder1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.IncrementingNum;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventTest {

   private final Set<EventBasicGuidArtifact> resultEventArtifacts = new HashSet<EventBasicGuidArtifact>();
   private final Set<EventBasicGuidRelation> resultEventRelations = new HashSet<EventBasicGuidRelation>();
   private final Set<DefaultBasicGuidRelationReorder> resultEventReorders =
      new HashSet<DefaultBasicGuidRelationReorder>();
   private static Sender resultSender = null;
   private static List<String> ignoreLoggingRemote = Arrays.asList("OEM: ArtifactEvent Loopback enabled",
      "OEM: kickArtifactReloadEvent Loopback enabled", "OEM2: ArtifactEvent Loopback enabled",
      "OEM2: kickArtifactReloadEvent Loopback enabled");
   private static int incrementingGammaId = 2231;
   private static RemoteNetworkSender1 networkSender;

   private class ArtifactEventListener implements IArtifactEventListener {
      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         resultEventArtifacts.addAll(artifactEvent.getArtifacts());
         resultEventRelations.addAll(artifactEvent.getRelations());
         resultEventReorders.addAll(artifactEvent.getRelationOrderRecords());
         resultSender = sender;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }
   }

   @BeforeClass
   public static void setupStatic() {
      networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(ArtifactEventTest.class.getName());
      networkSender.setSessionId("N23422.32");
      networkSender.setMachineName("A2340422");
      networkSender.setUserId("b345344");
      networkSender.setMachineIp("123.421.56.342");
      networkSender.setPort(485);
      networkSender.setClientVersion("123.2");
   }

   @Before
   public void setup() {
      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private final ArtifactEventListener artifactEventListener = new ArtifactEventListener();

   public void clearEventCollections() {
      resultEventArtifacts.clear();
      resultEventRelations.clear();
   }

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(artifactEventListener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      TestUtil.severeLoggingEnd(monitorLog);
   }

   /**
    * Test remote event system's injection of changes coming in by creating fake remote events and sending them through
    * remote event manager's onEvent then check the corresponding artifact/relation for changes. This tests that
    * incoming events correctly inject changes into artifact/relations.
    */
   @org.junit.Test
   public void testArtifactRelationInjectionEvents() throws Exception {
      // Nothing to test in remote test
      if (isRemoteTest()) {
         return;
      }

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Test attribute injection
      Artifact injectArt = remoteInjection_attributes_modifyName();
      remoteInjection_attributes_addNewAttribute(injectArt);
      remoteInjection_attributes_deleteAttribute(injectArt);

      // Test relation injection
      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      Assert.assertEquals(0, injectArt.internalGetRelations(rootArt).size());
      remoteInjection_relations_addNewRelation(rootArt, injectArt);
      remoteInjection_relations_deleteRelation(rootArt, injectArt);
      remoteInjection_relations_addNewRelationWithRationale(rootArt, injectArt);
      remoteInjection_relations_modifyRelationRationale(rootArt, injectArt);
      remoteInjection_relations_reorderRelation(rootArt, injectArt);

      TestUtil.severeLoggingEnd(monitorLog,
         (isRemoteTest() ? ignoreLoggingRemote : Arrays.asList("Duplicate relation objects")));
   }

   @org.junit.Test
   public void testArtifactRelationEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      Artifact newArt = testArtifactRelationEvents__addArtifact();
      testArtifactRelationEvents__addRelation(newArt);
      testArtifactRelationEvents__modifyArtifact(newArt);
      testArtifactRelationEvents__modifyRelation(newArt);
      testArtifactRelationEvents__deleteArtifact(newArt);

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLoggingRemote : new ArrayList<String>()));
   }

   private Artifact testArtifactRelationEvents__addArtifact() throws Exception {
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.persist();

      Assert.assertEquals(2, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      boolean addedFound = false, modifiedFound = false;
      for (EventBasicGuidArtifact guidArt : resultEventArtifacts) {
         if (guidArt.getModType() == EventModType.Added) {
            addedFound = true;
         }
         if (guidArt.getModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
         Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
         Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      }
      Assert.assertTrue(addedFound);
      Assert.assertTrue(modifiedFound);
      Assert.assertFalse(newArt.isDirty());
      return newArt;
   }

   private void testArtifactRelationEvents__addRelation(Artifact newArt) throws Exception {
      clearEventCollections();
      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      rootArt.addChild(newArt);
      rootArt.persist();

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      EventBasicGuidRelation guidArt = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.Added, guidArt.getModType());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidArt.getRelTypeGuid());
      RelationLink relLink = newArt.internalGetRelations(rootArt).iterator().next();
      Assert.assertEquals(relLink.getGammaId(), guidArt.getGammaId());
      Assert.assertEquals("", relLink.getRationale());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__modifyArtifact(Artifact newArt) throws Exception {
      clearEventCollections();
      StaticIdManager.setSingletonAttributeValue(newArt, "this");
      newArt.persist();

      Assert.assertEquals(1, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      EventBasicGuidArtifact guidArt = resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__modifyRelation(Artifact newArt) throws Exception {
      clearEventCollections();
      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      String NEW_RATIONALE_STR = "This is the rationale";
      Assert.assertEquals(1, newArt.internalGetRelations(rootArt).size());
      RelationLink relLink = newArt.internalGetRelations(rootArt).iterator().next();
      relLink.setRationale(NEW_RATIONALE_STR);
      newArt.persist();

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      EventBasicGuidRelation guidArt = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.ModifiedRationale, guidArt.getModType());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidArt.getRelTypeGuid());
      Assert.assertEquals(newArt.internalGetRelations(rootArt).iterator().next().getGammaId(), guidArt.getGammaId());
      RelationLink modifiedRelLink = newArt.internalGetRelations(rootArt).iterator().next();
      Assert.assertEquals(NEW_RATIONALE_STR, modifiedRelLink.getRationale());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__deleteArtifact(Artifact newArt) throws Exception {
      clearEventCollections();
      newArt.deleteAndPersist();

      Assert.assertEquals(2, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      boolean deletedFound = false;
      boolean modifiedFound = false;
      for (EventBasicGuidArtifact guidArt1 : resultEventArtifacts) {
         if (isRemoteTest()) {
            Assert.assertTrue(resultSender.isRemote());
         } else {
            Assert.assertTrue(resultSender.isLocal());
         }
         if (guidArt1.getModType() == EventModType.Deleted) {
            deletedFound = true;
         }
         if (guidArt1.getModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getGuid(), guidArt1.getGuid());
         Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt1.getArtTypeGuid());
         Assert.assertEquals(newArt.getBranch().getGuid(), guidArt1.getBranchGuid());
      }
      Assert.assertTrue(deletedFound);
      Assert.assertTrue(modifiedFound);

      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());
      EventBasicGuidRelation guidArt = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, guidArt.getModType());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidArt.getRelTypeGuid());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private Artifact remoteInjection_relations_addNewRelation(Artifact rootArt, Artifact injectArt) throws Exception {

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
            RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.Added, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidRel.getBranchGuid());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();
      Assert.assertEquals("", relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_deleteRelation(Artifact rootArt, Artifact injectArt) throws Exception {

      clearEventCollections();
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getId(), RelationEventType.Deleted,
            CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidRel.getBranchGuid());

      Assert.assertEquals(0, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_reorderRelation(Artifact rootArt, Artifact injectArt) throws Exception {

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransactionId(1000);
      remoteEvent.setBranchGuid(BranchManager.getCommonBranch().getGuid());

      RemoteBasicGuidRelationReorder1 remoteReorder = new RemoteBasicGuidRelationReorder1();
      remoteReorder.setBranchGuid(BranchManager.getCommonBranch().getGuid());
      remoteReorder.setModTypeGuid(RelationOrderModType.Absolute.getGuid());
      remoteReorder.setRelTypeGuid(CoreRelationTypes.Default_Hierarchical__Child.getGuid());

      RemoteBasicGuidArtifact1 parentRemGuidArt = new RemoteBasicGuidArtifact1();
      parentRemGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      parentRemGuidArt.setBranchGuid(BranchManager.getCommonBranch().getGuid());
      parentRemGuidArt.setArtTypeGuid(CoreArtifactTypes.GeneralData.getGuid());
      parentRemGuidArt.setArtGuid(GUID.create());

      remoteReorder.setParentArt(parentRemGuidArt);
      remoteEvent.getRelationReorders().add(remoteReorder);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      Assert.assertEquals("1 reorder events should be sent", 1, resultEventReorders.size());
      Assert.assertTrue(resultSender.isRemote());
      DefaultBasicGuidRelationReorder guidReorder = resultEventReorders.iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, guidReorder.getModType());
      Assert.assertEquals(parentRemGuidArt.getArtGuid(), guidReorder.getParentArt().getGuid());
      Assert.assertEquals(parentRemGuidArt.getArtTypeGuid(), guidReorder.getParentArt().getArtTypeGuid());
      Assert.assertEquals(parentRemGuidArt.getBranchGuid(), guidReorder.getParentArt().getBranchGuid());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidReorder.getRelTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidReorder.getBranchGuid());

      return injectArt;
   }

   private Artifact remoteInjection_relations_addNewRelationWithRationale(Artifact rootArt, Artifact injectArt) throws Exception {

      String RATIONALE_STR = "This is the rationale";
      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
            RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);
      RemoteBasicGuidRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(RATIONALE_STR);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.Added, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidRel.getBranchGuid());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();
      Assert.assertEquals(RATIONALE_STR, relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_modifyRelationRationale(Artifact rootArt, Artifact injectArt) throws Exception {
      String NEW_RATIONALE_STR = "This is the NEW rationale";

      clearEventCollections();
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getId(),
            RelationEventType.ModifiedRationale, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);
      RemoteBasicGuidRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(NEW_RATIONALE_STR);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = resultEventRelations.iterator().next();
      Assert.assertEquals(RelationEventType.ModifiedRationale, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidRel.getBranchGuid());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();
      Assert.assertEquals(NEW_RATIONALE_STR, relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_attributes_modifyName() throws Exception {

      String ORIG_NAME = "Remote Injected Artifact";
      String NEW_NAME = "Remote Injected Artifact Chg";
      // Create artifact to test injection; this will also put it in cache which is necessary for it's update
      // Artifact must be stored static so it doesn't get garbage collected
      Artifact injectArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      injectArt.setName(ORIG_NAME);
      injectArt.persist();

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();

      // Create modify attribute record
      int nameAttrId = injectArt.getAttributes().iterator().next().getId();
      remAttrChg.setAttributeId(nameAttrId);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.Name.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Modified.getGuid());
      remAttrChg.getData().add(NEW_NAME);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventModifiedBasicGuidArtifact guidArt = (EventModifiedBasicGuidArtifact) resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(nameAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Modified,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(NEW_NAME, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(NEW_NAME, injectArt.getName());
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   private Artifact remoteInjection_attributes_addNewAttribute(Artifact injectArt) throws Exception {

      String GENERAL_DATA_STRING = "This is the string";
      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create add attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(2343);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.GeneralStringData.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.New.getGuid());
      remAttrChg.getData().add(GENERAL_DATA_STRING);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventModifiedBasicGuidArtifact guidArt = (EventModifiedBasicGuidArtifact) resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(2343, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.New,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData.getGuid(), attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(GENERAL_DATA_STRING, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(GENERAL_DATA_STRING,
         injectArt.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, ""));
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   private Artifact remoteInjection_attributes_deleteAttribute(Artifact injectArt) throws Exception {

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create delete attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      int genStrAttrId = injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).iterator().next().getId();
      remAttrChg.setAttributeId(genStrAttrId);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.GeneralStringData.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Deleted.getGuid());
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventModifiedBasicGuidArtifact guidArt = (EventModifiedBasicGuidArtifact) resultEventArtifacts.iterator().next();
      // Artifact is modified, attribute id deleted
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(injectArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(genStrAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Deleted,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData.getGuid(), attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      // Validate that artifact was updated
      Assert.assertEquals(0, injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).size());
      return injectArt;
   }

   @org.junit.Test
   public void testArtifactRelationReorderEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Setup artifact and children to reorder
      SkynetTransaction transaction =
         new SkynetTransaction(BranchManager.getCommonBranch(), getClass().getSimpleName());
      Artifact newArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            getClass().getSimpleName() + " - testArtifactRelationReorderEvents");
      newArt.persist(transaction);
      for (int x = 1; x < 6; x++) {
         Artifact childArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
               "testRelationReorder - child " + x);
         newArt.addChild(childArt);
         newArt.persist(transaction);
      }
      transaction.execute();

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      List<Artifact> orderedChildren = newArt.getChildren();
      Assert.assertEquals(5, orderedChildren.size());
      Assert.assertTrue(orderedChildren.get(0).getName().endsWith(" 1"));
      Assert.assertTrue(orderedChildren.get(1).getName().endsWith(" 2"));
      Assert.assertTrue(orderedChildren.get(2).getName().endsWith(" 3"));
      Assert.assertTrue(orderedChildren.get(3).getName().endsWith(" 4"));
      Assert.assertTrue(orderedChildren.get(4).getName().endsWith(" 5"));

      List<Artifact> artifactsInNewOrder = new ArrayList<Artifact>();
      artifactsInNewOrder.add(orderedChildren.get(0));
      artifactsInNewOrder.add(orderedChildren.get(1));
      artifactsInNewOrder.add(orderedChildren.get(3));
      artifactsInNewOrder.add(orderedChildren.get(2));
      artifactsInNewOrder.add(orderedChildren.get(4));
      newArt.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, artifactsInNewOrder);
      newArt.persist();

      Assert.assertEquals("newArt will change cause attribute modified", 1, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      Assert.assertEquals("1 reorder events should be sent", 1, resultEventReorders.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      DefaultBasicGuidRelationReorder guidReorder = resultEventReorders.iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, guidReorder.getModType());
      Assert.assertEquals(newArt.getGuid(), guidReorder.getParentArt().getGuid());
      Assert.assertEquals(newArt.getArtTypeGuid(), guidReorder.getParentArt().getArtTypeGuid());
      Assert.assertEquals(newArt.getBranchGuid(), guidReorder.getParentArt().getBranchGuid());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidReorder.getRelTypeGuid());

      List<Artifact> newOrderedChildren = newArt.getChildren();
      Assert.assertEquals(5, newOrderedChildren.size());
      Assert.assertTrue(newOrderedChildren.get(0).getName().endsWith(" 1"));
      Assert.assertTrue(newOrderedChildren.get(1).getName().endsWith(" 2"));
      Assert.assertTrue(newOrderedChildren.get(2).getName().endsWith(" 4"));
      Assert.assertTrue(newOrderedChildren.get(3).getName().endsWith(" 3"));
      Assert.assertTrue(newOrderedChildren.get(4).getName().endsWith(" 5"));

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLoggingRemote : new ArrayList<String>()));

   }

   private RemotePersistEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactModified(Artifact modifiedArt) throws OseeCoreException {
      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransactionId(1000);
      remoteEvent.setBranchGuid(BranchManager.getCommonBranch().getGuid());

      RemoteBasicGuidArtifact1 remGuidArt = new RemoteBasicGuidArtifact1();
      remGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      remGuidArt.setBranchGuid(BranchManager.getCommonBranch().getGuid());
      remGuidArt.setArtTypeGuid(CoreArtifactTypes.GeneralData.getGuid());
      remGuidArt.setArtGuid(modifiedArt.getGuid());

      remoteEvent.getArtifacts().add(remGuidArt);
      return remoteEvent;
   }

   private RemotePersistEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(int relationId, RelationEventType relationEventType, IRelationType relType, Artifact artA, Artifact artB) throws OseeCoreException {
      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransactionId(1000);
      remoteEvent.setBranchGuid(BranchManager.getCommonBranch().getGuid());

      RemoteBasicGuidRelation1 remGuidRel = new RemoteBasicGuidRelation1();
      remGuidRel.setModTypeGuid(relationEventType.getGuid());
      remGuidRel.setBranchGuid(BranchManager.getCommonBranch().getGuid());
      remGuidRel.setGammaId(incrementingGammaId++);
      remGuidRel.setRelTypeGuid(relType.getGuid());
      remGuidRel.setRelationId(relationId);
      remGuidRel.setArtAId(artA.getArtId());
      remGuidRel.setArtBId(artB.getArtId());
      remGuidRel.setArtA(FrameworkEventUtil.getRemoteBasicGuidArtifact(artA.getBasicGuidArtifact()));
      remGuidRel.setArtB(FrameworkEventUtil.getRemoteBasicGuidArtifact(artB.getBasicGuidArtifact()));

      remoteEvent.getRelations().add(remGuidRel);
      return remoteEvent;
   }

   @org.junit.Test
   public void testPurgeArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testPurgeArtifactEvents");
      newArt.persist();

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Purge Artifact
      newArt.purgeFromBranch();

      Assert.assertEquals(1, resultEventArtifacts.size());
      EventBasicGuidArtifact guidArt = resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Purged, guidArt.getModType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLoggingRemote : new ArrayList<String>()));
   }

   protected boolean isRemoteTest() {
      return false;
   }

   @org.junit.Test
   public void testReloadArtifactEvents() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testReloadArtifactEvents");
      newArt.persist();

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // reload Artifact
      StaticIdManager.setSingletonAttributeValue(newArt, "this");
      Assert.assertTrue(newArt.isDirty());
      newArt.reloadAttributesAndRelations();
      Assert.assertFalse(newArt.isDirty());

      // Reload events are local only, confirm that nothing comes through remote
      if (isRemoteTest()) {
         Assert.assertEquals(0, resultEventArtifacts.size());
      } else {
         Assert.assertEquals(1, resultEventArtifacts.size());
         EventBasicGuidArtifact guidArt = resultEventArtifacts.iterator().next();
         Assert.assertEquals(EventModType.Reloaded, guidArt.getModType());
         Assert.assertTrue(resultSender.isLocal());
         Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
         Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
         Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      }
      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLoggingRemote : new ArrayList<String>()));
   }

   @org.junit.Test
   public void testChangeTypeArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact for Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testChangeTypeArtifactEvents");
      newArt.persist();

      OseeEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // reload Artifact
      Assert.assertTrue(newArt.isOfType(CoreArtifactTypes.GeneralData));
      ChangeArtifactType.changeArtifactType(Arrays.asList(newArt), CoreArtifactTypes.Heading);

      Assert.assertEquals(1, resultEventArtifacts.size());
      EventChangeTypeBasicGuidArtifact guidArt =
         (EventChangeTypeBasicGuidArtifact) resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.ChangeType, guidArt.getModType());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(CoreArtifactTypes.GeneralData.getGuid(), guidArt.getFromArtTypeGuid());
      // Reload artifact; since artifact cache cleared, it should be loaded as new artifact type
      Artifact changedArt = ArtifactQuery.getArtifactFromId(newArt.getGuid(), newArt.getBranch());
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), changedArt.getArtifactType().getGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLoggingRemote : new ArrayList<String>()));
   }

   /**
    * Need to always get a new relationId that hasn't been used in this DB yet
    */
   private int getIncrementingRelationId() {
      return 9999 + IncrementingNum.get();
   }
}