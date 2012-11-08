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

package org.eclipse.osee.framework.skynet.core.test.integration;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.support.test.util.DemoArtifactTypes;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactTest {

   private SevereLoggingMonitor monitorLog;

   private Artifact artifactWithSpecialAttr;

   @Before
   public void setUp() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();

      artifactWithSpecialAttr =
         ArtifactTypeManager.addArtifact(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoSawBuilds.SAW_Bld_1);
   }

   @After
   public void tearDown() throws Exception {
      if (artifactWithSpecialAttr != null) {
         artifactWithSpecialAttr.deleteAndPersist();
      }
      TestUtil.severeLoggingEnd(monitorLog);
   }

   @Test
   public void attributeCopyAcrossRelatedBranches() throws Exception {
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");

      Artifact copiedArtifact = artifactWithSpecialAttr.duplicate(DemoSawBuilds.SAW_Bld_2);
      try {
         Assert.assertFalse(copiedArtifact.getAttributes(CoreAttributeTypes.Partition).isEmpty());
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
         Assert.assertTrue(copiedArtifact.getAttributes(CoreAttributeTypes.Partition).isEmpty());
      } finally {
         if (copiedArtifact != null) {
            copiedArtifact.deleteAndPersist();
         }
      }
   }

   @Test
   public void setSoleAttributeValueTest() throws Exception {
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
   }

}
