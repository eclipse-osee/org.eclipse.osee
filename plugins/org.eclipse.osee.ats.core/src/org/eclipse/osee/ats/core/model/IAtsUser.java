/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsUser extends IAtsObject, Comparable<Object> {

   public String getUserId() throws OseeCoreException;

   public String getEmail() throws OseeCoreException;

   public boolean isActive() throws OseeCoreException;
}
