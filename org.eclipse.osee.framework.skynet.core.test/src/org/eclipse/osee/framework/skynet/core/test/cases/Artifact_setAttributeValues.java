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

package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class Artifact_setAttributeValues {

   private static List<String> firstSet = Arrays.asList("First", "Second", "Third");
   private static List<String> addOneSet = Arrays.asList("First", "Second", "Third", "Fourth");
   private static List<String> addOneRemoveOneSet = Arrays.asList("Second", "Third", "Fourth", "Fifth");
   private static List<String> addDuplicates_set =
         Arrays.asList("Second", "Second", "Third", "Fourth", "Fifth", "Fourth");
   private static List<String> addDuplicates_result = Arrays.asList("Second", "Third", "Fifth", "Fourth");
   private static List<String> emptySet = Arrays.asList();

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      cleanup();
   }

   @org.junit.Test
   public void testSetAttributeValues() throws Exception {
      Branch branch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(branch);
      Artifact artifact = ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName());
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, firstSet);
      artifact.persist();

      assertTrue(Collections.isEqual(firstSet, artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   @org.junit.Test
   public void testSetAttributeValuesAddOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addOneSet);
      artifact.persist();

      assertTrue(Collections.isEqual(addOneSet, artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   @org.junit.Test
   public void testSetAttributeValuesAddOneRemoveOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addOneRemoveOneSet);
      artifact.persist();

      assertTrue(Collections.isEqual(addOneRemoveOneSet,
            artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   @org.junit.Test
   public void testSetAttributeValuesRemoveAll() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, emptySet);
      artifact.persist();

      assertTrue(artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).size() == 0);
   }

   @org.junit.Test
   public void testSetAttributeValuesWithDuplicates() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addDuplicates_set);
      artifact.persist();

      assertTrue(Collections.isEqual(addDuplicates_result,
            artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   private Artifact getArtifact() throws Exception {
      Branch branch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(branch);
      return ArtifactQuery.getArtifactListFromName(getClass().getSimpleName(), branch, false).iterator().next();
   }

   private static void cleanup() throws Exception {
      Branch branch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(branch);
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactListFromName(Artifact_setAttributeValues.class.getSimpleName(), branch, false);
      FrameworkTestUtil.purgeArtifacts(arts);
   }
}
