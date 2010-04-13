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
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.ArtifactEventManager;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactListener;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEventManagerTest {

   final Set<EventBasicGuidArtifact> resultEventArtifacts = new HashSet<EventBasicGuidArtifact>();
   public static Sender resultSender = null;
   public static List<String> ignoreLogging =
         Arrays.asList("OEM: TransactionEvent Loopback enabled", "OEM: kickArtifactReloadEvent Loopback enabled",
               "OEM2: TransactionEvent Loopback enabled", "OEM2: kickArtifactReloadEvent Loopback enabled");

   public class ArtifactEventListener implements IArtifactListener {
      @Override
      public void handleArtifactModified(Collection<EventBasicGuidArtifact> eventArtifacts, Sender sender) {
         resultEventArtifacts.addAll(eventArtifacts);
         resultSender = sender;
      }
   }
   // artifact listener create for use by all tests to just capture result eventArtifacts for query
   private ArtifactEventListener artifactEventListener = new ArtifactEventListener();

   @org.junit.Test
   public void testRegistration() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      ArtifactEventManager.removeAllListeners();
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      ArtifactEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, ArtifactEventManager.getNumberOfListeners());

      ArtifactEventManager.removeListener(artifactEventListener);
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      TestUtil.severeLoggingEnd(monitorLog);
   }

   @org.junit.Test
   public void testAddModifyDeleteArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      ArtifactEventManager.removeAllListeners();
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      ArtifactEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, ArtifactEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.persist();

      Thread.sleep(3000);

      Assert.assertEquals(2, resultEventArtifacts.size());
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

      // Modify Artifact Test
      resultEventArtifacts.clear();
      StaticIdManager.setSingletonAttributeValue(newArt, "this");
      newArt.persist();

      Thread.sleep(3000);

      Assert.assertEquals(1, resultEventArtifacts.size());
      EventBasicGuidArtifact guidArt = resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Modified, guidArt.getModType());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
      Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());

      // Delete Artifact Test
      resultEventArtifacts.clear();
      newArt.deleteAndPersist();

      Thread.sleep(3000);

      Assert.assertEquals(2, resultEventArtifacts.size());
      boolean deletedFound = false;
      modifiedFound = false;
      for (EventBasicGuidArtifact guidArt1 : resultEventArtifacts) {
         if (guidArt1.getModType() == EventModType.Deleted) deletedFound = true;
         if (guidArt1.getModType() == EventModType.Modified) modifiedFound = true;
         Assert.assertEquals(newArt.getGuid(), guidArt1.getGuid());
         Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt1.getArtTypeGuid());
         Assert.assertEquals(newArt.getBranch().getGuid(), guidArt1.getBranchGuid());
      }
      Assert.assertTrue(deletedFound);
      Assert.assertTrue(modifiedFound);

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   @org.junit.Test
   public void testPurgeArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      ArtifactEventManager.removeAllListeners();
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      ArtifactEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, ArtifactEventManager.getNumberOfListeners());

      // Purge Artifact
      newArt.purgeFromBranch();

      Thread.sleep(3000);

      Assert.assertEquals(1, resultEventArtifacts.size());
      EventBasicGuidArtifact guidArt = resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.Purged, guidArt.getModType());
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
      ArtifactEventManager.removeAllListeners();
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      // Add new Artifact Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      ArtifactEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, ArtifactEventManager.getNumberOfListeners());

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
         Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
         Assert.assertEquals(newArt.getArtifactType().getGuid(), guidArt.getArtTypeGuid());
         Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      }
      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }

   @org.junit.Test
   public void testChangeTypeArtifactEvents() throws Exception {

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      ArtifactEventManager.removeAllListeners();
      Assert.assertEquals(0, ArtifactEventManager.getNumberOfListeners());

      // Add new Artifact for Test
      Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, BranchManager.getCommonBranch());
      newArt.setName(getClass().getSimpleName() + " - testEvents");
      newArt.persist();

      Thread.sleep(3000);

      ArtifactEventManager.addListener(artifactEventListener);
      Assert.assertEquals(1, ArtifactEventManager.getNumberOfListeners());

      // reload Artifact
      Assert.assertTrue(newArt.isOfType(CoreArtifactTypes.GeneralData));
      ChangeArtifactType.changeArtifactType(Arrays.asList(newArt),
            ArtifactTypeManager.getType(CoreArtifactTypes.Heading));

      Thread.sleep(5000);

      Assert.assertEquals(1, resultEventArtifacts.size());
      EventChangeTypeBasicGuidArtifact guidArt =
            (EventChangeTypeBasicGuidArtifact) resultEventArtifacts.iterator().next();
      Assert.assertEquals(EventModType.ChangeType, guidArt.getModType());
      Assert.assertEquals(newArt.getGuid(), guidArt.getGuid());
      Assert.assertEquals(newArt.getBranch().getGuid(), guidArt.getBranchGuid());
      Assert.assertEquals(CoreArtifactTypes.GeneralData.getGuid(), guidArt.getFromArtTypeGuid());
      // TODO Framework needs to reload artifact as new type; doesn't happen yet
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), newArt.getArtifactType().getGuid());
      Assert.assertEquals(CoreArtifactTypes.Heading.getGuid(), guidArt.getArtTypeGuid());

      TestUtil.severeLoggingEnd(monitorLog, (isRemoteTest() ? ignoreLogging : new ArrayList<String>()));
   }
}