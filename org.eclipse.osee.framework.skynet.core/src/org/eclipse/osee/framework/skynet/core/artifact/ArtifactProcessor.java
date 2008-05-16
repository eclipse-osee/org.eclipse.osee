/*
 * Created on May 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.OseeCoreException;

/**
 * @author b1528444
 */
public interface ArtifactProcessor {
   void run(Artifact artifact) throws OseeCoreException;
}
