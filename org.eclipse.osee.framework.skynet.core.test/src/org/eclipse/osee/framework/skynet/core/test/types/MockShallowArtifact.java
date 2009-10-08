package org.eclipse.osee.framework.skynet.core.test.types;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

// This class is used to avoid needing a full database to run this test.
// It purposely avoids using the getFullArtifact method from the base class
public final class MockShallowArtifact extends ShallowArtifact {

   private boolean wasGetFullArtifactCalled;

   public MockShallowArtifact(BranchCache cache, int artifactId) {
      super(cache, artifactId);
      clear();
   }

   public void clear() {
      wasGetFullArtifactCalled = false;
   }

   public boolean wasGetFullArtifactCalled() {
      return wasGetFullArtifactCalled;
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      wasGetFullArtifactCalled = true;
      return null;
   }

}