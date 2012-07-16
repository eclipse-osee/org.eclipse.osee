/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.user;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUser extends IAtsObject, Comparable<Object> {

   @Override
   public String getName();

   public String getUserId() throws OseeCoreException;

   public String getEmail() throws OseeCoreException;

   public boolean isActive() throws OseeCoreException;
}
