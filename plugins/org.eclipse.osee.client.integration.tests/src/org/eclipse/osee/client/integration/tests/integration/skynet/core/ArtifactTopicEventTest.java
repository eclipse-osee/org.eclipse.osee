/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelationReorder1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistTopicEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTopicArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTopicAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTopicRelation1;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationReorderTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Torin Grenda, David Miller
 */
public class ArtifactTopicEventTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private final String[] ignoreLogs = isRemoteTest() ? new String[] {
      "OEM: ArtifactEvent Loopback enabled",
      "OEM: kickArtifactReloadEvent Loopback enabled",
      "OEM2: ArtifactEvent Loopback enabled",
      "OEM2: kickArtifactReloadEvent Loopback enabled"} : new String[] {"Duplicate relation objects"};

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule(ignoreLogs);

   @Rule
   public OseeHousekeepingRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private static GammaId incrementingGammaId = GammaId.valueOf(2231);
   private ArtifactTopicEventListener listener;
   private RemoteNetworkSender1 networkSender;
   private final TransactionId tx = TransactionId.valueOf(1);
   private final GammaId gammaId7 = GammaId.valueOf(7L);

   @Before
   public void setup() {
      Assume.assumeTrue(FrameworkEventUtil.USE_NEW_EVENTS);
      listener = new ArtifactTopicEventListener();

      networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(ArtifactTopicEventTest.class.getName());
      networkSender.setSessionId("N23422.32");
      networkSender.setMachineName("A2340422");
      networkSender.setUserId("b345344");
      networkSender.setMachineIp("123.421.56.342");
      networkSender.setPort(485);
      networkSender.setClientVersion("123.2");

      OseeEventManager.getPreferences().setPendRunning(true);
   }

   @After
   public void cleanup() {
      OseeEventManager.getPreferences().setPendRunning(false);
   }

   @Test
   public void testRegistration() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      OseeEventManager.removeListener(listener);
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());
   }

   /**
    * Test remote event system's injection of changes coming in by creating fake remote events and sending them through
    * remote event manager's onEvent then check the corresponding artifact/relation for changes. This tests that
    * incoming events correctly inject changes into artifact/relations.
    */
   @Test
   public void testArtifactRelationInjectionEvents() throws Exception {
      // Nothing to test in remote test
      if (isRemoteTest()) {
         return;
      }

      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Test attribute injection
      Artifact injectArt = remoteInjection_attributes_modifyName();
      remoteInjection_attributes_addNewAttribute(injectArt);
      remoteInjection_attributes_deleteAttribute(injectArt);

      // Test relation injection
      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);
      Assert.assertEquals(0, injectArt.internalGetRelations(rootArt).size());
      remoteInjection_relations_addNewRelation(rootArt, injectArt);
      remoteInjection_relations_deleteRelation(rootArt, injectArt);
      remoteInjection_relations_addNewRelationWithRationale(rootArt, injectArt);
      remoteInjection_relations_modifyRelationRationale(rootArt, injectArt);
      remoteInjection_relations_reorderRelation(rootArt, injectArt);
   }

   @Test
   public void testArtifactRelationEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      Artifact newArt = testArtifactRelationEvents__addArtifact();
      testArtifactRelationEvents__addRelation(newArt);
      testArtifactRelationEvents__modifyArtifact(newArt);
      testArtifactRelationEvents__modifyRelation(newArt);
      testArtifactRelationEvents__deleteArtifact(newArt);
   }

   @Test
   public void testArtifactRelationReorderEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Setup artifact and children to reorder
      SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, getClass().getSimpleName());
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON,
         getClass().getSimpleName() + " - testArtifactRelationReorderEvents");
      newArt.persist(transaction);
      for (int x = 1; x < 6; x++) {
         Artifact childArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "testRelationReorder - child " + x);
         newArt.addChild(childArt);
         newArt.persist(transaction);
      }
      transaction.execute();

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      List<Artifact> orderedChildren = newArt.getChildren();
      Assert.assertEquals(5, orderedChildren.size());
      Assert.assertTrue(orderedChildren.get(0).getName().endsWith(" 1"));
      Assert.assertTrue(orderedChildren.get(1).getName().endsWith(" 2"));
      Assert.assertTrue(orderedChildren.get(2).getName().endsWith(" 3"));
      Assert.assertTrue(orderedChildren.get(3).getName().endsWith(" 4"));
      Assert.assertTrue(orderedChildren.get(4).getName().endsWith(" 5"));

      List<Artifact> artifactsInNewOrder = new ArrayList<>();
      artifactsInNewOrder.add(orderedChildren.get(0));
      artifactsInNewOrder.add(orderedChildren.get(1));
      artifactsInNewOrder.add(orderedChildren.get(3));
      artifactsInNewOrder.add(orderedChildren.get(2));
      artifactsInNewOrder.add(orderedChildren.get(4));
      newArt.setRelationOrder(CoreRelationTypes.DefaultHierarchical_Child, artifactsInNewOrder);
      newArt.persist(getClass().getSimpleName());

      Assert.assertEquals("newArt will change cause attribute modified", 1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertEquals("1 reorder events should be sent", 1, listener.getReorders().size());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      EventTopicRelationReorderTransfer transferReorder = listener.getReorders().iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, transferReorder.getModType());
      Assert.assertEquals(newArt.getIdString(), transferReorder.getParentArt().getArtifactToken().getIdString());
      Assert.assertEquals(newArt.getArtifactType(), transferReorder.getParentArt().getArtifactTypeId());
      Assert.assertTrue(newArt.getBranch().equals((transferReorder.getBranch())));
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), transferReorder.getRelTypeUuid());

      List<Artifact> newOrderedChildren = newArt.getChildren();
      Assert.assertEquals(5, newOrderedChildren.size());
      Assert.assertTrue(newOrderedChildren.get(0).getName().endsWith(" 1"));
      Assert.assertTrue(newOrderedChildren.get(1).getName().endsWith(" 2"));
      Assert.assertTrue(newOrderedChildren.get(2).getName().endsWith(" 4"));
      Assert.assertTrue(newOrderedChildren.get(3).getName().endsWith(" 3"));
      Assert.assertTrue(newOrderedChildren.get(4).getName().endsWith(" 5"));
   }

   @Test
   public void testPurgeArtifactEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt.setName(getClass().getSimpleName() + " - testPurgeArtifactEvents");
      newArt.persist(getClass().getSimpleName());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // Purge Artifact
      newArt.purgeFromBranch();

      Assert.assertEquals(1, listener.getArtifacts().size());
      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Purged, topicArt.getEventModType());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      Assert.assertEquals(newArt.getIdString(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(newArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
      Assert.assertEquals(newArt.getBranch().getId(), topicArt.getBranch().getId());
   }

   @Test
   public void testReloadArtifactEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt.setName(getClass().getSimpleName() + " - testReloadArtifactEvents");
      newArt.persist(getClass().getSimpleName());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // reload Artifact
      newArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "this");
      Assert.assertTrue(newArt.isDirty());
      newArt.reloadAttributesAndRelations();
      Assert.assertFalse(newArt.isDirty());

      // Reload events are local only, confirm that nothing comes through remote
      if (isRemoteTest()) {
         Assert.assertEquals(0, listener.getArtifacts().size());
      } else {
         Assert.assertEquals(1, listener.getArtifacts().size());
         EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
         Assert.assertEquals(EventModType.Reloaded, topicArt.getEventModType());
         Assert.assertTrue(listener.getSender().isLocal());
         Assert.assertEquals(newArt.getIdString(), topicArt.getArtifactToken().getIdString());
         Assert.assertEquals(newArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
         Assert.assertEquals(newArt.getBranch().getId(), topicArt.getBranch().getId());
      }
   }

   @Test
   public void testChangeTypeArtifactEvents() throws Exception {
      OseeEventManager.removeAllListeners();
      Assert.assertEquals(0, OseeEventManager.getNumberOfListeners());

      // Add new Artifact for Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt.setName(getClass().getSimpleName() + " - testChangeTypeArtifactEvents");
      newArt.persist(getClass().getSimpleName());

      OseeEventManager.addListener(listener);
      Assert.assertEquals(1, OseeEventManager.getNumberOfListeners());

      // reload Artifact
      Assert.assertTrue(newArt.isOfType(CoreArtifactTypes.GeneralData));
      ChangeArtifactType.changeArtifactType(Arrays.asList(newArt), CoreArtifactTypes.HeadingMsWord, true);

      Assert.assertEquals(1, listener.getArtifacts().size());

      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventTopicTransferType.CHANGE, topicArt.getTransferType());
      Assert.assertEquals(EventModType.ChangeType, topicArt.getEventModType());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      Assert.assertEquals(newArt.getIdString(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(newArt.getBranch().getId(), topicArt.getBranch().getId());
      Assert.assertEquals(CoreArtifactTypes.HeadingMsWord.getIdString(), topicArt.getArtifactTypeId().getIdString());
      Assert.assertEquals(CoreArtifactTypes.GeneralData, topicArt.getFromArtTypeGuid());
      // Reload artifact; since artifact cache cleared, it should be loaded as new artifact type
      Artifact changedArt = ArtifactQuery.getArtifactFromToken(newArt);
      Assert.assertEquals(CoreArtifactTypes.HeadingMsWord, changedArt.getArtifactType());
   }

   @Test
   public void testDuplicateArtifact() throws Exception {
      //Add new Artifacts for Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt.setName(getClass().getSimpleName() + " - testDuplicateArtifact");
      newArt.persist(getClass().getSimpleName());
      Artifact newArt2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt2.setName(getClass().getSimpleName() + " - testDuplicateArtifact2");
      newArt2.persist(getClass().getSimpleName());

      //Create Relation for Test
      RelationManager.addRelation(CoreRelationTypes.SupportingInfo, newArt, newArt2, "Test Relation");
      newArt.persist(getClass().getSimpleName());
      List<RelationLink> relations =
         RelationManager.getRelations(newArt, CoreRelationTypes.SupportingInfo, RelationSide.SIDE_B);
      RelationLink newRelation = relations.get(0);
      EventTopicRelationTransfer relationTransfer = FrameworkEventUtil.relationTransferFactory(RelationEventType.Added,
         newRelation.getArtifactA(), newRelation.getArtifactB(), newRelation, newRelation.getRelationType().getId(),
         newRelation.getGammaId(), newRelation.getRationale());

      ArtifactTopicEvent artifactTopicEvent = new ArtifactTopicEvent(COMMON);
      artifactTopicEvent.addArtifact(newArt);
      artifactTopicEvent.addRelation(relationTransfer);
      artifactTopicEvent.setNetworkSender(FrameworkEventUtil.getNetworkSender(networkSender));

      ArtifactTopicEvent artifactTopicEvent2 =
         FrameworkEventUtil.getPersistTopicEvent(FrameworkEventUtil.getRemotePersistTopicEvent(artifactTopicEvent));

      Assert.assertEquals(artifactTopicEvent, artifactTopicEvent2);
   }

   @Test
   public void testSerializeArtifact() throws Exception {
      // Add new Artifact for Test
      ArtifactTypeId artType = ArtifactTypeId.valueOf(12L);
      String json = JsonUtil.toJson(artType);
      ArtifactTypeId transferred = JsonUtil.readValue(json, ArtifactTypeId.class);
      Assert.assertEquals(artType, transferred);

      EventTopicArtifactTransfer transferArt = new EventTopicArtifactTransfer();
      transferArt.setArtifactTypeId(artType);
      transferArt.setArtifactToken(ArtifactToken.valueOf(ArtifactId.valueOf("1234567890"), "Test Artifact Name"));
      transferArt.setBranch(COMMON);
      transferArt.setEventModType(EventModType.Added);

      String transferJson = JsonUtil.toJson(transferArt);
      EventTopicArtifactTransfer newTransferArt = JsonUtil.readValue(transferJson, EventTopicArtifactTransfer.class);
      Assert.assertEquals(transferArt, newTransferArt);
   }

   @Test
   public void testSerializeRelation() throws Exception {
      //Create New EventTopicRelationTransfer
      EventTopicRelationTransfer transferRel = new EventTopicRelationTransfer();
      transferRel.setRelationEventType(RelationEventType.Added);
      transferRel.setArtAId(ArtifactToken.valueOf(ArtifactId.valueOf("1234567890"), "Test Artifact Name A"));
      transferRel.setArtAIdType(ArtifactTypeId.valueOf(12L));
      transferRel.setArtBId(ArtifactToken.valueOf(ArtifactId.valueOf("1234567880"), "Test Artifact Name B"));
      transferRel.setArtBIdType(ArtifactTypeId.valueOf(12L));
      transferRel.setRationale("Test Rationale");
      transferRel.setRelTypeId(5L);
      transferRel.setGammaId(GammaId.valueOf(123L));
      transferRel.setRelationId(RelationId.valueOf(15L));
      transferRel.setBranch(COMMON);

      //Test that the EventTopicRelationTransfer can serialize correctly
      String transferJson = JsonUtil.toJson(transferRel);
      EventTopicRelationTransfer newTransferRel = JsonUtil.readValue(transferJson, EventTopicRelationTransfer.class);
      Assert.assertEquals(transferRel, newTransferRel);
   }

   @Test
   public void testSerializeRelationReorder() throws Exception {
      //Create new EventTopicRelationReorderTransfer
      EventTopicRelationReorderTransfer reorderTransfer = new EventTopicRelationReorderTransfer();
      EventTopicArtifactTransfer artifactTransfer = FrameworkEventUtil.artifactTransferFactory(COMMON,
         ArtifactToken.valueOf(ArtifactId.valueOf("1234567890"), "Test Artifact Name"), ArtifactTypeId.valueOf(12L),
         EventModType.Added, null, null, EventTopicTransferType.BASE);
      reorderTransfer.setParentArt(artifactTransfer);
      reorderTransfer.setBranch(COMMON);
      reorderTransfer.setModType(RelationOrderModType.Default);
      reorderTransfer.setRelTypeUuid(5L);

      //Test that the EventTopicRelationReorderTransfer can serialize correctly
      String transferJson = JsonUtil.toJson(reorderTransfer);
      EventTopicRelationReorderTransfer newReorderTransfer =
         JsonUtil.readValue(transferJson, EventTopicRelationReorderTransfer.class);
      Assert.assertEquals(reorderTransfer, newReorderTransfer);
   }

   protected boolean isRemoteTest() {
      return false;
   }

   private RemotePersistTopicEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactModified(Artifact modifiedArt) {
      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = new RemotePersistTopicEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransaction(tx);

      remoteEvent.setBranchGuid(COMMON);

      RemoteTopicArtifact1 remGuidArt = new RemoteTopicArtifact1();
      remGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      remGuidArt.setBranch(COMMON);
      remGuidArt.setArtifactType(CoreArtifactTypes.GeneralData);
      remGuidArt.setArtGuid(modifiedArt.getGuid());

      remoteEvent.getArtifacts().add(remGuidArt);
      return remoteEvent;
   }

   private RemotePersistTopicEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(RelationId relationId, RelationEventType relationEventType, RelationTypeToken relType, Artifact artA, Artifact artB) {
      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = new RemotePersistTopicEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransaction(tx);
      remoteEvent.setBranchGuid(COMMON);

      RemoteTopicRelation1 remGuidRel = new RemoteTopicRelation1();
      remGuidRel.setModTypeGuid(relationEventType.getGuid());

      remGuidRel.setBranchGuid(COMMON);
      remGuidRel.setGammaId(incrementingGammaId.increment(1));
      remGuidRel.setRelTypeGuid(relType.getId());
      remGuidRel.setRelationId(relationId);
      remGuidRel.setArtAId(artA.getToken());
      remGuidRel.setArtBId(artB.getToken());
      remGuidRel.setArtA(FrameworkEventUtil.getRemoteTopicArtifact(artA.getArtifactTransfer(EventModType.Modified)));
      remGuidRel.setArtB(FrameworkEventUtil.getRemoteTopicArtifact(artB.getArtifactTransfer(EventModType.Modified)));

      remoteEvent.getRelations().add(remGuidRel);
      return remoteEvent;
   }

   private Artifact testArtifactRelationEvents__addArtifact() throws Exception {
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      newArt.persist(getClass().getSimpleName());

      Assert.assertEquals(2, listener.getArtifacts().size());
      Assert.assertTrue("No relations events should be sent", listener.getRelations().isEmpty());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      boolean addedFound = false, modifiedFound = false;
      for (EventTopicArtifactTransfer topicArt : listener.getArtifacts()) {
         if (topicArt.getEventModType() == EventModType.Added) {
            addedFound = true;
         }
         if (topicArt.getEventModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getIdString(), topicArt.getArtifactToken().getIdString());
         Assert.assertEquals(newArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
         Assert.assertEquals(newArt.getBranch().getId(), topicArt.getBranch().getId());
      }
      Assert.assertTrue(addedFound);
      Assert.assertTrue(modifiedFound);
      Assert.assertFalse(newArt.isDirty());
      return newArt;
   }

   private void testArtifactRelationEvents__addRelation(Artifact newArt) throws Exception {
      listener.reset();

      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);
      rootArt.addChild(newArt);
      rootArt.persist(getClass().getSimpleName());

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Added, topicRel.getRelationEventType());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      RelationLink relLink = newArt.internalGetRelations(rootArt).iterator().next();
      Assert.assertEquals(relLink.getGammaId(), topicRel.getGammaId());
      Assert.assertEquals("", relLink.getRationale());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(newArt.getId(), topicRel.getArtBId().getId());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__modifyArtifact(Artifact newArt) throws Exception {
      listener.reset();

      newArt.setSingletonAttributeValue(CoreAttributeTypes.StaticId, "this");
      newArt.persist(getClass().getSimpleName());

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, topicArt.getEventModType());
      Assert.assertEquals(newArt.getGuid(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(newArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
      Assert.assertEquals(newArt.getBranch().getId(), topicArt.getBranch().getId());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__modifyRelation(Artifact newArt) throws Exception {
      listener.reset();

      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);
      String NEW_RATIONALE_STR = "This is the rationale";
      Assert.assertEquals(1, newArt.internalGetRelations(rootArt).size());
      RelationLink relLink = newArt.internalGetRelations(rootArt).iterator().next();
      relLink.setRationale(NEW_RATIONALE_STR);
      newArt.persist(getClass().getSimpleName());

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.ModifiedRationale, topicRel.getRelationEventType());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(newArt.internalGetRelations(rootArt).iterator().next().getGammaId(), topicRel.getGammaId());
      RelationLink modifiedRelLink = newArt.internalGetRelations(rootArt).iterator().next();
      Assert.assertEquals(NEW_RATIONALE_STR, modifiedRelLink.getRationale());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(newArt.getId(), topicRel.getArtBId().getId());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private void testArtifactRelationEvents__deleteArtifact(Artifact newArt) throws Exception {
      listener.reset();

      newArt.deleteAndPersist(getClass().getSimpleName());

      Assert.assertEquals(2, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      boolean deletedFound = false;
      boolean modifiedFound = false;
      for (EventTopicArtifactTransfer topicArt1 : listener.getArtifacts()) {
         if (isRemoteTest()) {
            Assert.assertTrue(listener.getSender().isRemote());
         } else {
            Assert.assertTrue(listener.getSender().isLocal());
         }
         if (topicArt1.getEventModType() == EventModType.Deleted) {
            deletedFound = true;
         }
         if (topicArt1.getEventModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getGuid(), topicArt1.getArtifactToken().getIdString());
         Assert.assertEquals(newArt.getArtifactType().getId(), topicArt1.getArtifactTypeId().getId());
         Assert.assertEquals(newArt.getBranch().getId(), topicArt1.getBranch().getId());
      }
      Assert.assertTrue(deletedFound);
      Assert.assertTrue(modifiedFound);

      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, topicRel.getRelationEventType());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(newArt.getId(), topicRel.getArtBId().getId());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private Artifact remoteInjection_relations_addNewRelation(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(
         RelationId.valueOf(getIncrementingRelationId()), RelationEventType.Added,
         CoreRelationTypes.DefaultHierarchical_Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Added, topicRel.getRelationEventType());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(injectArt.getId(), topicRel.getArtBId().getId());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicRel.getBranch().getId());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.DefaultHierarchical_Parent).iterator().next();
      Assert.assertEquals("", relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_deleteRelation(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.DefaultHierarchical_Parent).iterator().next();

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getRelationId(),
            RelationEventType.Deleted, CoreRelationTypes.DefaultHierarchical_Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, topicRel.getRelationEventType());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(injectArt.getId(), topicRel.getArtBId().getId());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicRel.getBranch().getId());

      Assert.assertEquals(0, injectArt.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Parent).size());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_reorderRelation(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransaction(tx);
      remoteEvent.setBranchGuid(COMMON);

      RemoteBasicGuidRelationReorder1 remoteReorder = new RemoteBasicGuidRelationReorder1();
      remoteReorder.setBranchGuid(COMMON);
      remoteReorder.setModTypeGuid(RelationOrderModType.Absolute.getGuid());
      remoteReorder.setRelTypeGuid(CoreRelationTypes.DefaultHierarchical_Child.getGuid());

      RemoteBasicGuidArtifact1 parentRemGuidArt = new RemoteBasicGuidArtifact1();
      parentRemGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      parentRemGuidArt.setBranch(COMMON);
      parentRemGuidArt.setArtifactType(CoreArtifactTypes.GeneralData);
      parentRemGuidArt.setArtGuid(GUID.create());

      remoteReorder.setParentArt(parentRemGuidArt);
      remoteEvent.getRelationReorders().add(remoteReorder);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertEquals("1 reorder events should be sent", 1, listener.getReorders().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicRelationReorderTransfer transferReorder = listener.getReorders().iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, transferReorder.getModType());
      Assert.assertEquals(parentRemGuidArt.getArtGuid(), transferReorder.getParentArt().getArtifactToken().getGuid());
      Assert.assertEquals(parentRemGuidArt.getArtifactType(), transferReorder.getParentArt().getArtifactTypeId());
      Assert.assertTrue(transferReorder.getParentArt().getBranch().equals(COMMON));
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), transferReorder.getRelTypeUuid());
      Assert.assertTrue(injectArt.getBranch().equals((transferReorder.getBranch())));

      return injectArt;
   }

   private Artifact remoteInjection_relations_addNewRelationWithRationale(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      String RATIONALE_STR = "This is the rationale";

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(
         RelationId.valueOf(getIncrementingRelationId()), RelationEventType.Added,
         CoreRelationTypes.DefaultHierarchical_Child, rootArt, injectArt);
      RemoteTopicRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(RATIONALE_STR);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Added, topicRel.getRelationEventType());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(injectArt.getId(), topicRel.getArtBId().getId());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicRel.getBranch().getId());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.DefaultHierarchical_Parent).iterator().next();
      Assert.assertEquals(RATIONALE_STR, relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_modifyRelationRationale(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      String NEW_RATIONALE_STR = "This is the NEW rationale";

      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.DefaultHierarchical_Parent).iterator().next();

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getRelationId(),
            RelationEventType.ModifiedRationale, CoreRelationTypes.DefaultHierarchical_Child, rootArt, injectArt);
      RemoteTopicRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(NEW_RATIONALE_STR);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicRelationTransfer topicRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.ModifiedRationale, topicRel.getRelationEventType());
      Assert.assertEquals(rootArt.getId(), topicRel.getArtAId().getId());
      Assert.assertEquals(injectArt.getId(), topicRel.getArtBId().getId());
      Assert.assertEquals(CoreRelationTypes.DefaultHierarchical_Child.getGuid(), topicRel.getRelTypeId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicRel.getBranch().getId());

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.DefaultHierarchical_Parent).size());
      relLink = injectArt.getRelations(CoreRelationTypes.DefaultHierarchical_Parent).iterator().next();
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
      Artifact injectArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      injectArt.setName(ORIG_NAME);
      injectArt.persist(getClass().getSimpleName());

      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteTopicArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      RemoteTopicAttributeChange1 remAttrChg = new RemoteTopicAttributeChange1();

      // Create modify attribute record
      Long nameAttrId = injectArt.getAttributes().iterator().next().getId();
      remAttrChg.setAttributeId(AttributeId.valueOf(nameAttrId));
      remAttrChg.setGammaId(gammaId7);
      remAttrChg.setAttributeType(CoreAttributeTypes.Name);
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Modified.getGuid());
      remAttrChg.getData().add(NEW_NAME);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, topicArt.getEventModType());
      Assert.assertEquals(injectArt.getIdString(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(injectArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicArt.getBranch().getId());
      Assert.assertEquals(1, topicArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = topicArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(nameAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Modified,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.Name, attrChg.getAttrTypeGuid());
      Assert.assertEquals(gammaId7, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(NEW_NAME, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(NEW_NAME, injectArt.getName());
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   private Artifact remoteInjection_attributes_addNewAttribute(Artifact injectArt) throws Exception {
      listener.reset();

      String GENERAL_DATA_STRING = "This is the string";

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteTopicArtifact1 remTopicArt = remoteEvent.getArtifacts().iterator().next();

      // Create add attribute record
      RemoteTopicAttributeChange1 remAttrChg = new RemoteTopicAttributeChange1();
      remAttrChg.setAttributeId(AttributeId.valueOf(2343L));
      remAttrChg.setGammaId(gammaId7);
      remAttrChg.setAttributeType(CoreAttributeTypes.GeneralStringData);
      remAttrChg.setModTypeGuid(AttributeEventModificationType.New.getGuid());
      remAttrChg.getData().add(GENERAL_DATA_STRING);
      remAttrChg.getData().add("");
      remTopicArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, topicArt.getEventModType());
      Assert.assertEquals(injectArt.getIdString(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(injectArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicArt.getBranch().getId());
      Assert.assertEquals(1, topicArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = topicArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(2343, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.New,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData, attrChg.getAttrTypeGuid());
      Assert.assertEquals(gammaId7, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(GENERAL_DATA_STRING, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(GENERAL_DATA_STRING,
         injectArt.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, ""));
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   private Artifact remoteInjection_attributes_deleteAttribute(Artifact injectArt) throws Exception {
      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistTopicEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteTopicArtifact1 remTopicArt = remoteEvent.getArtifacts().iterator().next();

      // Create delete attribute record
      RemoteTopicAttributeChange1 remAttrChg = new RemoteTopicAttributeChange1();
      Long genStrAttrId = injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).iterator().next().getId();
      remAttrChg.setAttributeId(AttributeId.valueOf(genStrAttrId));
      remAttrChg.setGammaId(gammaId7);
      remAttrChg.setAttributeType(CoreAttributeTypes.GeneralStringData);
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Deleted.getGuid());
      remTopicArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventTopicArtifactTransfer topicArt = listener.getArtifacts().iterator().next();
      // Artifact is modified, attribute id deleted
      Assert.assertEquals(EventModType.Modified, topicArt.getEventModType());
      Assert.assertEquals(injectArt.getIdString(), topicArt.getArtifactToken().getIdString());
      Assert.assertEquals(injectArt.getArtifactType().getId(), topicArt.getArtifactTypeId().getId());
      Assert.assertEquals(injectArt.getBranch().getId(), topicArt.getBranch().getId());
      Assert.assertEquals(1, topicArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = topicArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(genStrAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Deleted,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData, attrChg.getAttrTypeGuid());
      Assert.assertEquals(gammaId7, attrChg.getGammaId());

      // Validate that artifact was updated
      Assert.assertEquals(0, injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).size());
      return injectArt;
   }

   /**
    * Need to always get a new relationId that hasn't been used in this DB yet
    */
   private long getIncrementingRelationId() {
      return ConnectionHandler.getNextSequence(OseeData.REL_LINK_ID_SEQ, true);
   }

   private static final class ArtifactTopicEventListener implements IArtifactTopicEventListener {

      private final Set<EventTopicArtifactTransfer> resultEventArtifacts = new HashSet<>();
      private final Set<EventTopicRelationTransfer> resultEventRelations = new HashSet<>();
      private final Set<EventTopicRelationReorderTransfer> resultEventReorders = new HashSet<>();

      private Sender resultSender;

      @Override
      public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
         resultEventArtifacts.addAll(artifactTopicEvent.getArtifacts());
         resultEventRelations.addAll(artifactTopicEvent.getRelations());
         resultEventReorders.addAll(artifactTopicEvent.getRelationOrderRecords());
         resultSender = sender;
      }

      public void reset() {
         resultEventArtifacts.clear();
         resultEventRelations.clear();
         resultEventReorders.clear();
         resultSender = null;
      }

      public Set<EventTopicArtifactTransfer> getArtifacts() {
         return resultEventArtifacts;
      }

      public Set<EventTopicRelationTransfer> getRelations() {
         return resultEventRelations;
      }

      public Set<EventTopicRelationReorderTransfer> getReorders() {
         return resultEventReorders;
      }

      public Sender getSender() {
         return resultSender;
      }

      @Override
      public List<? extends ITopicEventFilter> getTopicEventFilters() {
         return null;
      }
   }
}