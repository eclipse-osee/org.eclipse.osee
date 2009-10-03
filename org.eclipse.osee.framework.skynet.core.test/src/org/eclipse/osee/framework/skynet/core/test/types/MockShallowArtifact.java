package org.eclipse.osee.framework.skynet.core.test.types;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.ShallowArtifact;

// This class is used to avoid needing a full database to run this test.
// It purposely avoids using the getFullArtifact method from the base class
public final class MockShallowArtifact extends ShallowArtifact {

   public MockShallowArtifact(BranchCache cache, int artifactId) {
      super(cache, artifactId);
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      Artifact associatedArtifact = null;
      //         if (getArtId() > 0) {
      //            associatedArtifact = ArtifactQuery.getArtifactFromId(getArtId(), getBranch());
      //         } else {
      //            associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
      //            artifactId = associatedArtifact.getArtId();
      //         }
      return associatedArtifact;
   }

}