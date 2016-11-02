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
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
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
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventTest {

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

   private static int incrementingGammaId = 2231;

   private ArtifactEventListener listener;
   private RemoteNetworkSender1 networkSender;
   private final TransactionId tx = TransactionId.valueOf(1000);

   @Before
   public void setup() {
      listener = new ArtifactEventListener();

      networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(ArtifactEventTest.class.getName());
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
      newArt.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, artifactsInNewOrder);
      newArt.persist(getClass().getSimpleName());

      Assert.assertEquals("newArt will change cause attribute modified", 1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertEquals("1 reorder events should be sent", 1, listener.getReorders().size());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      DefaultBasicUuidRelationReorder guidReorder = listener.getReorders().iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, guidReorder.getModType());
      Assert.assertEquals(newArt.getGuid(), guidReorder.getParentArt().getGuid());
      Assert.assertEquals(newArt.getArtifactTypeId(), guidReorder.getParentArt().getArtifactType());
      Assert.assertTrue(newArt.isOnSameBranch(guidReorder));
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidReorder.getRelTypeGuid());

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
      EventBasicGuidArtifact guidArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Purged, guidArt.getModType());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getArtifactTypeId(), guidArt.getArtifactType());
      Assert.assertTrue(newArt.isOnSameBranch(guidArt));
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
         EventBasicGuidArtifact guidArt = listener.getArtifacts().iterator().next();
         Assert.assertEquals(EventModType.Reloaded, guidArt.getModType());
         Assert.assertTrue(listener.getSender().isLocal());
         Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
         Assert.assertEquals(newArt.getArtifactTypeId(), guidArt.getArtifactType());
         Assert.assertTrue(newArt.isOnSameBranch(guidArt));
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
      ChangeArtifactType.changeArtifactType(Arrays.asList(newArt), CoreArtifactTypes.HeadingMSWord, true);

      Assert.assertEquals(1, listener.getArtifacts().size());
      EventChangeTypeBasicGuidArtifact guidArt =
         (EventChangeTypeBasicGuidArtifact) listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.ChangeType, guidArt.getModType());
      if (isRemoteTest()) {
         Assert.assertTrue(listener.getSender().isRemote());
      } else {
         Assert.assertTrue(listener.getSender().isLocal());
      }
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertTrue(newArt.isOnSameBranch(guidArt));
      Assert.assertEquals(CoreArtifactTypes.HeadingMSWord, guidArt.getArtifactType());
      Assert.assertEquals(CoreArtifactTypes.GeneralData, guidArt.getFromArtTypeGuid());
      // Reload artifact; since artifact cache cleared, it should be loaded as new artifact type
      Artifact changedArt = ArtifactQuery.getArtifactFromToken(newArt);
      Assert.assertEquals(CoreArtifactTypes.HeadingMSWord, changedArt.getArtifactType());
   }

   protected boolean isRemoteTest() {
      return false;
   }

   private RemotePersistEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactModified(Artifact modifiedArt) {
      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransaction(tx);

      remoteEvent.setBranchGuid(COMMON);

      RemoteBasicGuidArtifact1 remGuidArt = new RemoteBasicGuidArtifact1();
      remGuidArt.setModTypeGuid(EventModType.Modified.getGuid());
      remGuidArt.setBranch(COMMON);
      remGuidArt.setArtifactType(CoreArtifactTypes.GeneralData);
      remGuidArt.setArtGuid(modifiedArt.getGuid());

      remoteEvent.getArtifacts().add(remGuidArt);
      return remoteEvent;
   }

   private RemotePersistEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(int relationId, RelationEventType relationEventType, IRelationType relType, Artifact artA, Artifact artB) {
      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = new RemotePersistEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(networkSender);
      remoteEvent.setTransaction(tx);
      remoteEvent.setBranchGuid(COMMON);

      RemoteBasicGuidRelation1 remGuidRel = new RemoteBasicGuidRelation1();
      remGuidRel.setModTypeGuid(relationEventType.getGuid());

      remGuidRel.setBranchGuid(COMMON);
      remGuidRel.setGammaId(incrementingGammaId++);
      remGuidRel.setRelTypeGuid(relType.getId());
      remGuidRel.setRelationId(relationId);
      remGuidRel.setArtAId(artA.getArtId());
      remGuidRel.setArtBId(artB.getArtId());
      remGuidRel.setArtA(FrameworkEventUtil.getRemoteBasicGuidArtifact(artA.getBasicGuidArtifact()));
      remGuidRel.setArtB(FrameworkEventUtil.getRemoteBasicGuidArtifact(artB.getBasicGuidArtifact()));

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
      for (EventBasicGuidArtifact guidArt : listener.getArtifacts()) {
         if (guidArt.getModType() == EventModType.Added) {
            addedFound = true;
         }
         if (guidArt.getModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
         Assert.assertEquals(newArt.getArtifactTypeId(), guidArt.getArtifactType());
         Assert.assertTrue(newArt.isOnSameBranch(guidArt));
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
      EventBasicGuidRelation guidArt = listener.getRelations().iterator().next();
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
      EventBasicGuidArtifact guidArt = listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getArtifactTypeId(), guidArt.getArtifactType());
      Assert.assertTrue(newArt.isOnSameBranch(guidArt));
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
      EventBasicGuidRelation guidArt = listener.getRelations().iterator().next();
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
      listener.reset();

      newArt.deleteAndPersist();

      Assert.assertEquals(2, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      boolean deletedFound = false;
      boolean modifiedFound = false;
      for (EventBasicGuidArtifact guidArt1 : listener.getArtifacts()) {
         if (isRemoteTest()) {
            Assert.assertTrue(listener.getSender().isRemote());
         } else {
            Assert.assertTrue(listener.getSender().isLocal());
         }
         if (guidArt1.getModType() == EventModType.Deleted) {
            deletedFound = true;
         }
         if (guidArt1.getModType() == EventModType.Modified) {
            modifiedFound = true;
         }
         Assert.assertEquals(newArt.getGuid(), guidArt1.getGuid());
         Assert.assertEquals(newArt.getArtifactTypeId(), guidArt1.getArtifactType());
         Assert.assertTrue(newArt.isOnSameBranch(guidArt1));
      }
      Assert.assertTrue(deletedFound);
      Assert.assertTrue(modifiedFound);

      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);
      EventBasicGuidRelation guidArt = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, guidArt.getModType());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidArt.getRelTypeGuid());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
      Assert.assertFalse(rootArt.isDirty());
      Assert.assertFalse(newArt.isDirty());
   }

   private Artifact remoteInjection_relations_addNewRelation(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
            RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventBasicGuidRelation guidRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Added, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertTrue(injectArt.isOnSameBranch(guidRel));

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();
      Assert.assertEquals("", relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_deleteRelation(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(
         relLink.getId(), RelationEventType.Deleted, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventBasicGuidRelation guidRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Deleted, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertTrue(injectArt.isOnSameBranch(guidRel));

      Assert.assertEquals(0, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
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
      remoteReorder.setRelTypeGuid(CoreRelationTypes.Default_Hierarchical__Child.getGuid());

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
      DefaultBasicUuidRelationReorder guidReorder = listener.getReorders().iterator().next();
      Assert.assertEquals(RelationOrderModType.Absolute, guidReorder.getModType());
      Assert.assertEquals(parentRemGuidArt.getArtGuid(), guidReorder.getParentArt().getGuid());
      Assert.assertEquals(parentRemGuidArt.getArtifactType(), guidReorder.getParentArt().getArtifactType());
      Assert.assertTrue(guidReorder.getParentArt().isOnBranch(COMMON));
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidReorder.getRelTypeGuid());
      Assert.assertTrue(injectArt.isOnSameBranch(guidReorder));

      return injectArt;
   }

   private Artifact remoteInjection_relations_addNewRelationWithRationale(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      String RATIONALE_STR = "This is the rationale";

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent =
         getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
            RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);
      RemoteBasicGuidRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(RATIONALE_STR);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventBasicGuidRelation guidRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.Added, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertTrue(injectArt.isOnSameBranch(guidRel));

      Assert.assertEquals(1, injectArt.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent).size());
      RelationLink relLink = injectArt.getRelations(CoreRelationTypes.Default_Hierarchical__Parent).iterator().next();
      Assert.assertEquals(RATIONALE_STR, relLink.getRationale());
      Assert.assertFalse(injectArt.isDirty());
      Assert.assertFalse(rootArt.isDirty());

      return injectArt;
   }

   private Artifact remoteInjection_relations_modifyRelationRationale(Artifact rootArt, Artifact injectArt) throws Exception {
      listener.reset();

      String NEW_RATIONALE_STR = "This is the NEW rationale";

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

      Assert.assertEquals("No artifact events should be sent", 0, listener.getArtifacts().size());
      Assert.assertEquals(1, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventBasicGuidRelation guidRel = listener.getRelations().iterator().next();
      Assert.assertEquals(RelationEventType.ModifiedRationale, guidRel.getModType());
      Assert.assertEquals(rootArt, guidRel.getArtA());
      Assert.assertEquals(injectArt, guidRel.getArtB());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidRel.getRelTypeGuid());
      Assert.assertTrue(injectArt.isOnSameBranch(guidRel));

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
      Artifact injectArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, COMMON);
      injectArt.setName(ORIG_NAME);
      injectArt.persist(getClass().getSimpleName());

      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();

      // Create modify attribute record
      int nameAttrId = injectArt.getAttributes().iterator().next().getId().intValue();
      remAttrChg.setAttributeId(nameAttrId);
      remAttrChg.setGammaId(1000);
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
      EventModifiedBasicGuidArtifact guidArt =
         (EventModifiedBasicGuidArtifact) listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactTypeId(), guidArt.getArtifactType());
      Assert.assertTrue(injectArt.isOnSameBranch(guidArt));
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(nameAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Modified,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.Name, attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

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
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create add attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(2343);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttributeType(CoreAttributeTypes.GeneralStringData);
      remAttrChg.setModTypeGuid(AttributeEventModificationType.New.getGuid());
      remAttrChg.getData().add(GENERAL_DATA_STRING);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventModifiedBasicGuidArtifact guidArt =
         (EventModifiedBasicGuidArtifact) listener.getArtifacts().iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactTypeId(), guidArt.getArtifactType());
      Assert.assertTrue(injectArt.isOnSameBranch(guidArt));
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(2343, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.New,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData, attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(GENERAL_DATA_STRING, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(GENERAL_DATA_STRING,
         injectArt.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, ""));
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   @SuppressWarnings("deprecation")
   private Artifact remoteInjection_attributes_deleteAttribute(Artifact injectArt) throws Exception {
      listener.reset();

      // Create fake remote event that would come in from another client
      RemotePersistEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create delete attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      int genStrAttrId =
         injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).iterator().next().getId().intValue();
      remAttrChg.setAttributeId(genStrAttrId);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttributeType(CoreAttributeTypes.GeneralStringData);
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Deleted.getGuid());
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      OseeEventManager.internalTestSendRemoteEvent(remoteEvent);

      // Wait for event to propagate

      Assert.assertEquals(1, listener.getArtifacts().size());
      Assert.assertEquals("No relations events should be sent", 0, listener.getRelations().size());
      Assert.assertTrue(listener.getSender().isRemote());
      EventModifiedBasicGuidArtifact guidArt =
         (EventModifiedBasicGuidArtifact) listener.getArtifacts().iterator().next();
      // Artifact is modified, attribute id deleted
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(injectArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(injectArt.getArtifactTypeId(), guidArt.getArtifactType());
      Assert.assertTrue(injectArt.isOnSameBranch(guidArt));
      Assert.assertEquals(1, guidArt.getAttributeChanges().size());

      // Validate attribute change in event message
      AttributeChange attrChg = guidArt.getAttributeChanges().iterator().next();
      Assert.assertEquals(genStrAttrId, attrChg.getAttributeId());
      Assert.assertEquals(AttributeEventModificationType.Deleted,
         AttributeEventModificationType.getType(attrChg.getModTypeGuid()));
      Assert.assertEquals(CoreAttributeTypes.GeneralStringData, attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      // Validate that artifact was updated
      Assert.assertEquals(0, injectArt.getAttributes(CoreAttributeTypes.GeneralStringData).size());
      return injectArt;
   }

   /**
    * Need to always get a new relationId that hasn't been used in this DB yet
    *
    * @throws OseeDataStoreException
    */
   private int getIncrementingRelationId() {
      return (int) ConnectionHandler.getNextSequence(OseeData.REL_LINK_ID_SEQ, true);
   }

   private static final class ArtifactEventListener implements IArtifactEventListener {

      private final Set<EventBasicGuidArtifact> resultEventArtifacts = new HashSet<>();
      private final Set<EventBasicGuidRelation> resultEventRelations = new HashSet<>();
      private final Set<DefaultBasicUuidRelationReorder> resultEventReorders =
         new HashSet<DefaultBasicUuidRelationReorder>();

      private Sender resultSender;

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         resultEventArtifacts.addAll(artifactEvent.getArtifacts());
         resultEventRelations.addAll(artifactEvent.getRelations());
         resultEventReorders.addAll(artifactEvent.getRelationOrderRecords());
         resultSender = sender;
      }

      public void reset() {
         resultEventArtifacts.clear();
         resultEventRelations.clear();
         resultEventReorders.clear();
         resultSender = null;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }

      public Set<EventBasicGuidArtifact> getArtifacts() {
         return resultEventArtifacts;
      }

      public Set<EventBasicGuidRelation> getRelations() {
         return resultEventRelations;
      }

      public Set<DefaultBasicUuidRelationReorder> getReorders() {
         return resultEventReorders;
      }

      public Sender getSender() {
         return resultSender;
      }
   }
}