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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.messaging.event.res.test.cases.RemoteNetworkSenderTest;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager2;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.msgs.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactListener;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.IncrementingNum;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventManagerTest {

   final Set<EventBasicGuidArtifact> resultEventArtifacts = new HashSet<EventBasicGuidArtifact>();
   final Set<EventBasicGuidRelation> resultEventRelations = new HashSet<EventBasicGuidRelation>();
   public static Sender resultSender = null;
   public static List<String> ignoreLogging =
         Arrays.asList("OEM: TransactionEvent Loopback enabled", "OEM: kickArtifactReloadEvent Loopback enabled",
               "OEM2: TransactionEvent Loopback enabled", "OEM2: kickArtifactReloadEvent Loopback enabled");
   public static int incrementingGammaId = 2231;

   public class ArtifactEventListener implements IArtifactListener {
      @Override
      public void handleArtifactModified(Collection<EventBasicGuidArtifact> eventArtifacts, Collection<EventBasicGuidRelation> eventRelations, Sender sender) {
         resultEventArtifacts.addAll(eventArtifacts);
         resultEventRelations.addAll(eventRelations);
         resultSender = sender;
      }
   }

   @org.junit.Before
   public void setUpTest() {
      OseeProperties.setNewEvents(true);
   }

   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private ArtifactEventListener artifactEventListener = new ArtifactEventListener();

   public void clearEventCollections() {
      resultEventArtifacts.clear();
      resultEventRelations.clear();
   }

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.removeListener(artifactEventListener);
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

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
      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

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

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   @org.junit.Test
   public void testArtifactRelationEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      Artifact newArt = testArtifactRelationEvents__addArtifact();
      testArtifactRelationEvents__addRelation(newArt);
      testArtifactRelationEvents__modifyArtifact(newArt);
      testArtifactRelationEvents__modifyRelation(newArt);
      testArtifactRelationEvents__deleteArtifact(newArt);

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   private Artifact testArtifactRelationEvents__addArtifact() throws Exception {
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.persist();

      Thread.sleep(4000);

      Assert.assertEquals(2, resultEventArtifacts.size());
      Assert.assertEquals("No relations events should be sent", 0, resultEventRelations.size());
      if (isRemoteTest()) {
         Assert.assertTrue(resultSender.isRemote());
      } else {
         Assert.assertTrue(resultSender.isLocal());
      }
      boolean addedFound = false, modifiedFound = false;
      for (EventBasicGuidArtifact guidArt : resultEventArtifacts) {
         if (guidArt.getModType() == EventModType.Added) addedFound = true;
         if (guidArt.getModType() == EventModType.Modified) modifiedFound = true;
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

      Thread.sleep(3000);

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

      Thread.sleep(3000);

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
      relLink.setRationale(NEW_RATIONALE_STR, true);
      newArt.persist();

      Thread.sleep(3000);

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

      Thread.sleep(3000);

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
         if (guidArt1.getModType() == EventModType.Deleted) deletedFound = true;
         if (guidArt1.getModType() == EventModType.Modified) modifiedFound = true;
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
      RemoteTransactionEvent1 remoteEvent =
            getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
                  RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = (EventBasicGuidRelation) resultEventRelations.iterator().next();
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
      RemoteTransactionEvent1 remoteEvent =
            getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getId(),
                  RelationEventType.Deleted, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = (EventBasicGuidRelation) resultEventRelations.iterator().next();
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

   private Artifact remoteInjection_relations_addNewRelationWithRationale(Artifact rootArt, Artifact injectArt) throws Exception {

      String RATIONALE_STR = "This is the rationale";
      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemoteTransactionEvent1 remoteEvent =
            getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(getIncrementingRelationId(),
                  RelationEventType.Added, CoreRelationTypes.Default_Hierarchical__Child, rootArt, injectArt);
      RemoteBasicGuidRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(RATIONALE_STR);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = (EventBasicGuidRelation) resultEventRelations.iterator().next();
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
      RemoteTransactionEvent1 remoteEvent =
            getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(relLink.getId(),
                  RelationEventType.ModifiedRationale, CoreRelationTypes.Default_Hierarchical__Child, rootArt,
                  injectArt);
      RemoteBasicGuidRelation1 relation = remoteEvent.getRelations().iterator().next();
      relation.setRationale(NEW_RATIONALE_STR);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

      Assert.assertEquals("No artifact events should be sent", 0, resultEventArtifacts.size());
      Assert.assertEquals(1, resultEventRelations.size());
      Assert.assertTrue(resultSender.isRemote());
      EventBasicGuidRelation guidRel = (EventBasicGuidRelation) resultEventRelations.iterator().next();
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

      Thread.sleep(2000);

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemoteTransactionEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();

      // Create modify attribute record
      int nameAttrId = injectArt.getAttributes().iterator().next().getId();
      remAttrChg.setAttributeId(nameAttrId);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.NAME.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Modified.getGuid());
      remAttrChg.getData().add(NEW_NAME);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

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
      Assert.assertEquals(CoreAttributeTypes.NAME.getGuid(), attrChg.getAttrTypeGuid());
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
      RemoteTransactionEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create add attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(2343);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.GENERAL_STRING_DATA.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.New.getGuid());
      remAttrChg.getData().add(GENERAL_DATA_STRING);
      remAttrChg.getData().add("");
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

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
      Assert.assertEquals(CoreAttributeTypes.GENERAL_STRING_DATA.getGuid(), attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      Assert.assertEquals(Arrays.asList(GENERAL_DATA_STRING, ""), remAttrChg.getData());

      // Validate that artifact was updated
      Assert.assertEquals(GENERAL_DATA_STRING, injectArt.getSoleAttributeValueAsString(
            CoreAttributeTypes.GENERAL_STRING_DATA, ""));
      Assert.assertFalse(injectArt.isDirty());
      return injectArt;
   }

   private Artifact remoteInjection_attributes_deleteAttribute(Artifact injectArt) throws Exception {

      clearEventCollections();

      // Create fake remote event that would come in from another client
      RemoteTransactionEvent1 remoteEvent = getFakeGeneralDataArtifactRemoteEventForArtifactModified(injectArt);
      RemoteBasicGuidArtifact1 remGuidArt = remoteEvent.getArtifacts().iterator().next();

      // Create delete attribute record
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      int genStrAttrId = injectArt.getAttributes(CoreAttributeTypes.GENERAL_STRING_DATA).iterator().next().getId();
      remAttrChg.setAttributeId(genStrAttrId);
      remAttrChg.setGammaId(1000);
      remAttrChg.setAttrTypeGuid(CoreAttributeTypes.GENERAL_STRING_DATA.getGuid());
      remAttrChg.setModTypeGuid(AttributeEventModificationType.Deleted.getGuid());
      remGuidArt.getAttributes().add(remAttrChg);

      // Send
      RemoteEventManager2.getInstance().onEvent(remoteEvent);

      // Wait for event to propagate
      Thread.sleep(4000);

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
      Assert.assertEquals(CoreAttributeTypes.GENERAL_STRING_DATA.getGuid(), attrChg.getAttrTypeGuid());
      Assert.assertEquals(1000, attrChg.getGammaId());

      // Validate that artifact was updated
      Assert.assertEquals(0, injectArt.getAttributes(CoreAttributeTypes.GENERAL_STRING_DATA).size());
      return injectArt;
   }

   private RemoteTransactionEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactModified(Artifact modifiedArt) throws OseeCoreException {
      // Create fake remote event that would come in from another client
      RemoteTransactionEvent1 remoteEvent = new RemoteTransactionEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(RemoteNetworkSenderTest.networkSender);
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

   private RemoteTransactionEvent1 getFakeGeneralDataArtifactRemoteEventForArtifactRelationModified(int relationId, RelationEventType relationEventType, IRelationType relType, Artifact artA, Artifact artB) throws OseeCoreException {
      // Create fake remote event that would come in from another client
      RemoteTransactionEvent1 remoteEvent = new RemoteTransactionEvent1();
      // Set sender to something other than this client so event system will think came from another client
      remoteEvent.setNetworkSender(RemoteNetworkSenderTest.networkSender);
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
      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      // Purge Artifact
      newArt.purgeFromBranch();

      Thread.sleep(3000);

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

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   protected boolean isRemoteTest() {
      return false;
   }

   @org.junit.Test
   public void testReloadArtifactEvents() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      // reload Artifact
      StaticIdManager.setSingletonAttributeValue(newArt, "this");
      Assert.assertTrue(newArt.isDirty());
      newArt.reloadAttributesAndRelations();

      Thread.sleep(3000);

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
      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   @org.junit.Test
   public void testChangeTypeArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FrameworkEventManager.removeAllListeners();
      Assert.assertEquals(0, FrameworkEventManager.getNumberOfListeners());

      // Add new Artifact for Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      FrameworkEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, FrameworkEventManager.getNumberOfListeners());

      // reload Artifact
      Assert.assertTrue(newArt.isOfType(CoreArtifactTypes.GeneralData));
      ChangeArtifactType.changeArtifactType(Arrays.asList(newArt),
            ArtifactTypeManager.getType(CoreArtifactTypes.Heading));

      Thread.sleep(5000);

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

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   /**
    * Need to always get a new relationId that hasn't been used in this DB yet
    */
   private int getIncrementingRelationId() {
      return 9999 + IncrementingNum.get();
   }
}