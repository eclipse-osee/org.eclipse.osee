/*
 * Created on May 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test2.cases;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Donald G. Dunne
 */
public class Artifact_setAttributeValues extends TestCase {

   private static List<String> firstSet = Arrays.asList("First", "Second", "Third");
   private static List<String> addOneSet = Arrays.asList("First", "Second", "Third", "Fourth");
   private static List<String> addOneRemoveOneSet = Arrays.asList("Second", "Third", "Fourth", "Fifth");
   private static List<String> addDuplicates_set =
         Arrays.asList("Second", "Second", "Third", "Fourth", "Fifth", "Fourth");
   private static List<String> addDuplicates_result = Arrays.asList("Second", "Third", "Fifth", "Fourth");
   private static List<String> emptySet = Arrays.asList();

   public void testCleanupPre() throws Exception {
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.Artifact#setAttributeValues(java.lang.String, java.util.Collection)}
    * .
    */
   public void testSetAttributeValues() throws Exception {
      Artifact artifact =
            ArtifactTypeManager.addArtifact("General Document",
                  BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), getClass().getSimpleName());
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, firstSet);
      artifact.persistAttributes();

      assertTrue(Collections.isEqual(firstSet, artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   public void testSetAttributeValuesAddOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addOneSet);
      artifact.persistAttributes();

      assertTrue(Collections.isEqual(addOneSet, artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   public void testSetAttributeValuesAddOneRemoveOne() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addOneRemoveOneSet);
      artifact.persistAttributes();

      assertTrue(Collections.isEqual(addOneRemoveOneSet,
            artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   public void testSetAttributeValuesRemoveAll() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, emptySet);
      artifact.persistAttributes();

      assertTrue(artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).size() == 0);
   }

   public void testSetAttributeValuesWithDuplicates() throws Exception {
      Artifact artifact = getArtifact();
      artifact.setAttributeValues(StaticIdManager.STATIC_ID_ATTRIBUTE, addDuplicates_set);
      artifact.persistAttributes();

      assertTrue(Collections.isEqual(addDuplicates_result,
            artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)));
   }

   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private Artifact getArtifact() throws Exception {
      return ArtifactQuery.getArtifactsFromName(getClass().getSimpleName(),
            BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), false).iterator().next();
   }

   private void cleanup() throws Exception {
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromName(getClass().getSimpleName(),
                  BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), false);
      ArtifactPersistenceManager.purgeArtifacts(arts);
   }

}
