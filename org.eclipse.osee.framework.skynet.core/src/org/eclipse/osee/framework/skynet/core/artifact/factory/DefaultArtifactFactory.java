/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * This artifact factory is used when no other ArtifactFactory has registered itself as being responsible. This is the
 * case when no specific ArtifactFactory has been defined such as when a new Artifact is created dynamically.
 * 
 * @author Donald G. Dunne
 */
public class DefaultArtifactFactory extends ArtifactFactory {

   public @Override
   Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      return new Artifact(this, guid, humandReadableId, branch, artifactType);
   }

}
