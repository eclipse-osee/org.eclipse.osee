/*
 * Created on Oct 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.presenter;

import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public interface ArtifactProvider {

   ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException;

   ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException;

   List<ReadableArtifact> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException;
}
