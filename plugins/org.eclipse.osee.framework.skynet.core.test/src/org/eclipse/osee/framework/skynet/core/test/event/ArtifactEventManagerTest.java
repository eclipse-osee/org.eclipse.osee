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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.FrameworkEventManager;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactListener;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
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

   public class ArtifactEventListener implements IArtifactListener {
      @Override
      public void handleArtifactModified(Collection<EventBasicGuidArtifact> eventArtifacts, Collection<EventBasicGuidRelation> eventRelations, Sender sender) {
         resultEventArtifacts.addAll(eventArtifacts);
         resultEventRelations.addAll(eventRelations);
         resultSender = sender;
      }
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

      Thread.sleep(3000);

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
      Assert.assertEquals(newArt.getRelations(rootArt).iterator().next().getGammaId(), guidArt.getGammaId());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
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
   }

   private void testArtifactRelationEvents__modifyRelation(Artifact newArt) throws Exception {
      clearEventCollections();
      Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());

      Assert.assertEquals(1, newArt.getRelations(rootArt).size());
      RelationLink relLink = newArt.getRelations(rootArt).iterator().next();
      relLink.setRationale("This is the rationale", true);
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
      Assert.assertEquals(RelationEventType.RationaleMod, guidArt.getModType());
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), guidArt.getRelTypeGuid());
      Assert.assertEquals(newArt.getRelations(rootArt).iterator().next().getGammaId(), guidArt.getGammaId());
      Assert.assertEquals(rootArt, guidArt.getArtA());
      Assert.assertEquals(newArt, guidArt.getArtB());
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
      Assert.assertEquals(CoreArtifactTypes.GeneralData.getGuid(), guidArt.getFromArtTypeGuid());
      // TODO Framework needs to reload artifact as new type; doesn't happen yet
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), newArt.getArtifactType().getGuid());
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), guidArt.getArtTypeGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }
}