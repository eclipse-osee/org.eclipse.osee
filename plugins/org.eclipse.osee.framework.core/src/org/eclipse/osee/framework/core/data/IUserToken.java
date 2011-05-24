/*
 * Created on May 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IUserToken extends IArtifactToken {

   public String getUserId() throws OseeCoreException;

   public boolean isActive() throws OseeCoreException;

   public boolean isAdmin() throws OseeCoreException;

   public String getEmail() throws OseeCoreException;

   public boolean isCreationRequired();

}
