/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.client.demo.DemoTypes;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String BREAKER_NAME = "ArtifactTest Breaker";
   private static Artifact artifactWithSpecialAttr;
   private static Artifact breakerArt;

   @BeforeClass
   public static void setUp() throws Exception {
      artifactWithSpecialAttr = ArtifactTypeManager.addArtifact(DemoTypes.DemoTestRequirement, SAW_Bld_1);
      breakerArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Breaker, COMMON);
      breakerArt.setName(BREAKER_NAME);
      breakerArt.persist("ArtifactTest");
   }

   @BeforeClass
   @AfterClass
   public static void cleanUp() throws Exception {
      if (breakerArt != null) {
         breakerArt.deleteAndPersist();
      }
      if (artifactWithSpecialAttr != null) {
         ArtifactCache.deCache(artifactWithSpecialAttr);
      }
   }

   @After
   public void tearDown() throws Exception {
      if (artifactWithSpecialAttr != null) {
         artifactWithSpecialAttr.deleteAndPersist();
      }
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeAndName(GeneralData, ArtifactTest.class.getSimpleName(),
         COMMON)) {
         art.deleteAndPersist();
      }
   }

   @Test
   public void attributeCopyAcrossRelatedBranches() throws Exception {
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");

      Artifact copiedArtifact = artifactWithSpecialAttr.duplicate(SAW_Bld_2);
      try {
         Assert.assertFalse(copiedArtifact.getAttributeCount(CoreAttributeTypes.Partition) == 0);
      } finally {
         if (copiedArtifact != null) {
            copiedArtifact.deleteAndPersist();
         }
      }
   }

   @Test
   public void attributeCopyAcrossUnrelatedBranches() throws Exception {
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");

      Artifact copiedArtifact = artifactWithSpecialAttr.duplicate(CoreBranches.COMMON);
      try {
         Assert.assertTrue(copiedArtifact.getAttributeCount(CoreAttributeTypes.Partition) == 0);
      } finally {
         if (copiedArtifact != null) {
            copiedArtifact.deleteAndPersist();
         }
      }
   }

   @Test
   public void testSetSoleAttributeValue() throws Exception {
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
   }

   /**
    * Setting same value should not change artifact
    */
   @Test
   public void testSetSoleAttributeValueSameValueBoolean() throws Exception {
      breakerArt.setSoleAttributeValue(CoreAttributeTypes.RequireConfirmation, true);
      breakerArt.persist("Set RequireConfirmation");
      Assert.assertEquals(true, breakerArt.getSoleAttributeValue(CoreAttributeTypes.RequireConfirmation, false));
      TransactionToken currentTrans = breakerArt.getTransaction();

      breakerArt.setSoleAttributeValue(CoreAttributeTypes.RequireConfirmation, true);
      breakerArt.persist("Re-set RequireConfirmation");
      Assert.assertEquals(true, breakerArt.getSoleAttributeValue(CoreAttributeTypes.RequireConfirmation, false));

      TransactionToken newTransaction = breakerArt.getTransaction();
      Assert.assertEquals(currentTrans, newTransaction);
   }

   @Test
   public void testSetSoleAttributeValueSameValueString() throws Exception {
      Assert.assertEquals(BREAKER_NAME, breakerArt.getName());
      TransactionToken currentTrans = breakerArt.getTransaction();

      breakerArt.setName(BREAKER_NAME);
      breakerArt.persist("Re-set Name");
      Assert.assertEquals(BREAKER_NAME, breakerArt.getName());

      TransactionToken newTransaction = breakerArt.getTransaction();
      Assert.assertEquals(currentTrans, newTransaction);
   }

   @Test
   public void testSetSoleAttributeValueSameValueInteger() throws Exception {
      breakerArt.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 34);
      breakerArt.persist("Set CircuitBreakerId");

      Assert.assertEquals(Integer.valueOf(34),
         breakerArt.getSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 0));
      TransactionToken currentTrans = breakerArt.getTransaction();

      breakerArt.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 34);
      breakerArt.persist("Re-Set CircuitBreakerId");
      Assert.assertEquals(Integer.valueOf(34),
         breakerArt.getSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 0));

      TransactionToken newTransaction = breakerArt.getTransaction();
      Assert.assertEquals(currentTrans, newTransaction);
   }

   @Test
   public void testEquals()  {
      Artifact art = ArtifactTypeManager.addArtifact(GeneralData, COMMON, ArtifactTest.class.getSimpleName());
      art.persist("test");

      DefaultBasicGuidArtifact equalGuid =
         new DefaultBasicGuidArtifact(SYSTEM_ROOT, CoreArtifactTypes.SoftwareDesign, art);
      Assert.assertNotSame(art, equalGuid);

      DefaultBasicGuidArtifact equalGuidArtType = new DefaultBasicGuidArtifact(SYSTEM_ROOT, GeneralData, art);
      Assert.assertNotSame(art, equalGuidArtType);

      DefaultBasicGuidArtifact equalGuidArtTypeBranchUuid = new DefaultBasicGuidArtifact(COMMON, GeneralData, art);
      Assert.assertEquals(art, equalGuidArtTypeBranchUuid);

      DefaultBasicGuidArtifact equalArtTypeBranchUuidNotGuid = new DefaultBasicGuidArtifact(COMMON, GeneralData);
      Assert.assertNotSame(art, equalArtTypeBranchUuidNotGuid);
   }

}
