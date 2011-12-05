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

package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.DemoArtifactTypes;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactTest {

   private static SevereLoggingMonitor monitorLog;

   @BeforeClass
   public static void setUp() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
   }

   @Test
   public void attributeCopyAcrossRelatedBranches() throws Exception {
      Artifact artifactWithSpecialAttr =
         ArtifactTypeManager.addArtifact(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoSawBuilds.SAW_Bld_1);
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");

      Artifact copiedArtifact = artifactWithSpecialAttr.duplicate(DemoSawBuilds.SAW_Bld_2);

      Assert.assertFalse(copiedArtifact.getAttributes(CoreAttributeTypes.Partition).isEmpty());
   }

   @Test
   public void attributeCopyAcrossUnrelatedBranches() throws Exception {
      Artifact artifactWithSpecialAttr =
         ArtifactTypeManager.addArtifact(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoSawBuilds.SAW_Bld_1);
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
      artifactWithSpecialAttr.setName("ArtifactTest-artifactWithSpecialAttr");

      Artifact copiedArtifact = artifactWithSpecialAttr.duplicate(CoreBranches.COMMON);

      Assert.assertTrue(copiedArtifact.getAttributes(CoreAttributeTypes.Partition).isEmpty());
   }

   @Test(expected = OseeArgumentException.class)
   public void setSoleAttributeValueTest() throws Exception {
      Artifact artifactWithSpecialAttr =
         ArtifactTypeManager.addArtifact(DemoArtifactTypes.DemoCodeTeamWorkflow, CoreBranches.COMMON);
      artifactWithSpecialAttr.setSoleAttributeValue(CoreAttributeTypes.Partition, "Navigation");
   }

}
