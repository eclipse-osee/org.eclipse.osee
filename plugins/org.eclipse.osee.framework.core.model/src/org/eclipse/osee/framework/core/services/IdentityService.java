/*
 * Created on Aug 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.services;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IdentityService {

   Integer getLocalId(Long universalId) throws OseeCoreException;

   Long getUniversalId(Integer localId) throws OseeCoreException;

   int getLocalId(Identity<Long> identity) throws OseeCoreException;

   void store(Collection<Long> universalIds) throws OseeCoreException;

   void clear();

}