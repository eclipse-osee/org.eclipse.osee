/*
 * Created on Aug 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCheck implements IArtifactCheck {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck#isDeleteable(java.util.Collection)
    */
   @Override
   public Result isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck#isModifiable(java.util.Collection, java.util.Collection)
    */
   @Override
   public Result isRenamable(Collection<Artifact> artifacts) throws OseeCoreException {
      return Result.TrueResult;
   }

}
