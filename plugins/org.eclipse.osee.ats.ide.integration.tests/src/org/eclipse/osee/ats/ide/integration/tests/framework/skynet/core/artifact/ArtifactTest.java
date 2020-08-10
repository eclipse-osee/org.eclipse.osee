/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.framework.skynet.core.artifact;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTest {

   public static final ArtifactToken TestSoftReq = ArtifactToken.valueOf(3842588L, ArtifactTest.class.getSimpleName(),
      DemoBranches.SAW_Bld_1, CoreArtifactTypes.SoftwareRequirementMsWord);

   @BeforeClass
   @AfterClass
   public static void setupCleanup() {
      Artifact robotReq = ArtifactQuery.getArtifactOrNull(TestSoftReq, DeletionFlag.EXCLUDE_DELETED);
      if (robotReq != null) {
         robotReq.purgeFromBranch();
      }
   }

   @Test
   public void testSetAttributeValues() {

      Artifact robotReq = ArtifactTypeManager.addArtifact(TestSoftReq);
      robotReq.persist(getClass().getSimpleName());

      Collection<String> parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(1, parts.size());
      Assert.assertEquals("Unspecified", parts.iterator().next());

      // After reload
      robotReq.reloadAttributesAndRelations();
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(1, parts.size());
      Assert.assertEquals("Unspecified", parts.iterator().next());

      // Test set
      robotReq.setAttributeValues(CoreAttributeTypes.Partition, Arrays.asList("ACS", "COMM"));
      robotReq.persist(getClass().getSimpleName());
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(2, parts.size());
      Assert.assertTrue(parts.contains("ACS"));
      Assert.assertTrue(parts.contains("COMM"));

      // After reload
      robotReq.reloadAttributesAndRelations();
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(2, parts.size());
      Assert.assertTrue(parts.contains("ACS"));
      Assert.assertTrue(parts.contains("COMM"));

      setupCleanup();

      // Before persist
      SkynetTransaction tx = TransactionManager.createTransaction(DemoBranches.SAW_Bld_1, getClass().getSimpleName());
      robotReq = ArtifactTypeManager.addArtifact(TestSoftReq);

      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(1, parts.size());
      Assert.assertEquals("Unspecified", parts.iterator().next());

      robotReq.setAttributeValues(CoreAttributeTypes.Partition, Arrays.asList("ACS", "COMM"));
      tx.addArtifact(robotReq);
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(2, parts.size());
      Assert.assertTrue(parts.contains("ACS"));
      Assert.assertTrue(parts.contains("COMM"));

      tx.execute();

      // After persist
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(2, parts.size());
      Assert.assertTrue(parts.contains("ACS"));
      Assert.assertTrue(parts.contains("COMM"));

      // After reload
      robotReq.reloadAttributesAndRelations();
      parts = robotReq.getAttributeValues(CoreAttributeTypes.Partition);
      Assert.assertEquals(2, parts.size());
      Assert.assertTrue(parts.contains("ACS"));
      Assert.assertTrue(parts.contains("COMM"));
   }
}
