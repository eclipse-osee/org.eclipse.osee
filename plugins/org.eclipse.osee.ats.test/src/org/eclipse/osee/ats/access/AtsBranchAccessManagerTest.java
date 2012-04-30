/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.access;

import java.util.Arrays;
import java.util.logging.Level;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.DemoActionableItems;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchAccessManagerTest {

   private static <T> T getService(Class<T> clazz) {
      Bundle bundle = FrameworkUtil.getBundle(AtsBranchAccessManager.class);
      Assert.assertNotNull(bundle);
      BundleContext context = bundle.getBundleContext();
      Assert.assertNotNull(context);
      ServiceReference<T> reference = context.getServiceReference(clazz);
      Assert.assertNotNull(reference);
      T service = context.getService(reference);
      Assert.assertNotNull(service);
      return service;
   }

   public static OseeEventService getEventService() {
      return getService(OseeEventService.class);
   }

   /**
    * Test method for {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#AtsBranchAccessManager()}.
    */
   @Test
   public void testAtsBranchAccessManager() {
      int numListeners = OseeEventManager.getNumberOfListeners();
      new AtsBranchAccessManager(getEventService());
      Assert.assertEquals(numListeners + 1, OseeEventManager.getNumberOfListeners());
   }

   /**
    * Test method for {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#dispose()}.
    */
   @Test
   public void testDispose() {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager(getEventService());
      int numListeners = OseeEventManager.getNumberOfListeners();
      mgr.dispose();
      Assert.assertEquals(numListeners - 1, OseeEventManager.getNumberOfListeners());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#isApplicable(org.eclipse.osee.framework.core.model.Branch)}
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testIsApplicable() throws OseeCoreException {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager(getEventService());
      Assert.assertFalse(mgr.isApplicable(AtsUtil.getAtsBranch()));
      Assert.assertFalse(mgr.isApplicable(DemoSawBuilds.SAW_Bld_1));

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertNotNull(teamArt);

      Branch branch = AtsBranchManagerCore.getWorkingBranch(teamArt);
      Assert.assertNotNull(branch);

      Assert.assertTrue(mgr.isApplicable(branch));
   }

   @Before
   @After
   public void cleanup() throws OseeCoreException {
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "testGetContextIdArtifact cleanup");
      teamArt.getTeamDefinition().deleteAttributes(CoreAttributeTypes.AccessContextId);
      teamArt.getTeamDefinition().persist(transaction);
      for (Artifact art : teamArt.getTeamDefinition().getRelatedArtifacts(
         AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         art.deleteAttributes(CoreAttributeTypes.AccessContextId);
         art.persist(transaction);
      }
      teamArt.deleteAttributes(CoreAttributeTypes.AccessContextId);
      teamArt.persist(transaction);
      transaction.execute();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#getContextId(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}
    * {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#getEventFilters()}.
    * {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#handleArtifactEvent(org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent, org.eclipse.osee.framework.skynet.core.event.model.Sender)}
    * 
    * @throws OseeCoreException
    */
   @Test
   public void testGetContextIdBranch() throws OseeCoreException {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager(getEventService());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);

      // confirm that no context id returned
      Assert.assertEquals(0, mgr.getContextId(teamArt.getWorkingBranch()).size());

      String teamDefContextId1 = "teamDef.context.1";
      String teamDefContextId2 = "teamDef.context.2";
      teamArt.getTeamDefinition().setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamDefContextId1, teamDefContextId2));
      teamArt.getTeamDefinition().persist(getClass().getSimpleName());

      try {
         TestUtil.sleep(1000);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Assert.assertEquals(2, mgr.getContextId(teamArt.getWorkingBranch()).size());

      String aiContextId = "ai.context.1";
      Artifact aiArt =
         ActionableItemManagerCore.getActionableItems(Arrays.asList(DemoActionableItems.SAW_Requirements.getName())).iterator().next();
      aiArt.setAttributeValues(CoreAttributeTypes.AccessContextId, Arrays.asList(aiContextId));
      aiArt.persist(getClass().getSimpleName());

      try {
         TestUtil.sleep(1000);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Assert.assertEquals(1, mgr.getContextId(teamArt.getWorkingBranch()).size());

      String teamContextId1 = "team.context.1";
      String teamContextId2 = "team.context.2";
      String teamContextId3 = "team.context.3";
      teamArt.setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamContextId1, teamContextId2, teamContextId3));
      teamArt.persist(getClass().getSimpleName());

      try {
         TestUtil.sleep(1000);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Assert.assertEquals(3, mgr.getContextId(teamArt.getWorkingBranch()).size());

   }

   /**
    * Test method for {@link org.eclipse.osee.ats.access.AtsBranchAccessManager#convertAccessAttributeToGuid
    * 
    */
   @Test
   public void testConvertAccessAttributeToGuid() throws OseeCoreException {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager(getEventService());
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);

      // confirm that no context id returned
      Assert.assertEquals(0, mgr.getContextId(teamArt.getWorkingBranch()).size());

      String teamDefContextId1 = "teamDef.context.1, this is the name";
      teamArt.getTeamDefinition().setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamDefContextId1));
      teamArt.getTeamDefinition().persist(getClass().getSimpleName());

      try {
         TestUtil.sleep(2000);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Assert.assertEquals(1, mgr.getContextId(teamArt.getWorkingBranch()).size());
      IAccessContextId contextId = mgr.getContextId(teamArt.getWorkingBranch()).iterator().next();
      Assert.assertEquals("teamDef.context.1", contextId.getGuid());
   }
}
