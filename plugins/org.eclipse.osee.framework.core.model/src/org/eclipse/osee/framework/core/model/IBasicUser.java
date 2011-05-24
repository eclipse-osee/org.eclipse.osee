/*
 * Created on May 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IBasicUser extends IArtifactToken {

   public String getUserId() throws OseeCoreException;

   public boolean isActive() throws OseeCoreException;

}
