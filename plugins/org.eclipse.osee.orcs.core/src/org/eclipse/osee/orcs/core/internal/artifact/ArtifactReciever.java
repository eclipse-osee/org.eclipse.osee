/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public interface ArtifactReciever {

   void onArtifact(ReadableArtifact artifact, boolean isArtifactAlreadyLoaded) throws OseeCoreException;

}
