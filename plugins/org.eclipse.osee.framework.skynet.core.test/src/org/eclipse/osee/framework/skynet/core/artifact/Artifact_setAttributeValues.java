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

package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
   private static List<String> addDuplicates_set = Arrays.asList("Second", "Second", "Third", "Fourth", "Fifth",
      "Fourth");
   private static List<String> addDuplicates_result = Arrays.asList("Second", "Third", "Fifth", "Fourth");
   private static List<String> emptySet = Arrays.asList();

   @BeforeClass
   public static void testCleanupPre() throws Exception {
      cleanup();
   }

   @org.junit.Test
   public void testSetAttributeValues() throws Exception {
      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, DemoSawBuilds.SAW_Bld_1,
            getClass().getSimpleName());
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, firstSet);
      artifact.persist(getClass().getSimpleName());

      assertTrue(Collections.isEqual(firstSet, artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)));
   }

   @org.junit.Test
   public void testSetAttributeValuesAddOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, addOneSet);
      artifact.persist(getClass().getSimpleName());

      assertTrue(Collections.isEqual(addOneSet, artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)));
   }

   @org.junit.Test
   public void testSetAttributeValuesAddOneRemoveOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, addOneRemoveOneSet);
      artifact.persist(getClass().getSimpleName());

      assertTrue(Collections.isEqual(addOneRemoveOneSet,
         artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)));
   }

   @org.junit.Test
   public void testSetAttributeValuesRemoveAll() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, emptySet);
      artifact.persist(getClass().getSimpleName());

      assertTrue(artifact.getAttributesToStringList(CoreAttributeTypes.StaticId).isEmpty());
   }

   @org.junit.Test
   public void testSetAttributeValuesWithDuplicates() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, addDuplicates_set);
      artifact.persist(getClass().getSimpleName());

      assertTrue(Collections.isEqual(addDuplicates_result,
         artifact.getAttributesToStringList(CoreAttributeTypes.StaticId)));
   }

   @AfterClass
   public static void testCleanupPost() throws Exception {
      cleanup();
   }

   private Artifact getArtifact() throws Exception {
      return ArtifactQuery.getArtifactListFromName(getClass().getSimpleName(), DemoSawBuilds.SAW_Bld_1, EXCLUDE_DELETED).iterator().next();
   }

   private static void cleanup() throws Exception {
      Collection<Artifact> arts =
         ArtifactQuery.getArtifactListFromName(Artifact_setAttributeValues.class.getSimpleName(),
            DemoSawBuilds.SAW_Bld_1, EXCLUDE_DELETED);
      new PurgeArtifacts(arts).execute();
   }
}
