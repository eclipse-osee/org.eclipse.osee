/*
 * Created on Jun 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.LifecycleHandler;

public interface IAccessProvider extends LifecycleHandler {

   void computeAccess(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck, AccessData accessData) throws OseeCoreException;

}
