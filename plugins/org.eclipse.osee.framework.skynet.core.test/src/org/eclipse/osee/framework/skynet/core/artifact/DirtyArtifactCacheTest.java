/*
 * Created on May 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import junit.framework.Assert;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * This test should be run as the last test of a suite to make sure that the ArtifactCache has no dirty artifacts.
 * 
 * @author Donald G. Dunne
 */
public class DirtyArtifactCacheTest {

   @org.junit.Test
   public void testArtifactCacheNotDirty() {
      Assert.assertTrue(String.format(
         "After all tests are run, there should be no dirty artifacts in Artifact Cache; Found [%s]",
         Artifacts.artNames(ArtifactCache.getDirtyArtifacts())), ArtifactCache.getDirtyArtifacts().isEmpty());
   }
}
