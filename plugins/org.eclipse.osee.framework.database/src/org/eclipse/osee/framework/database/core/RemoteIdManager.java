/*
 * Created on Aug 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.database.core;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface RemoteIdManager {

   Integer getLocalId(Long remoteId) throws OseeCoreException;

   Long getRemoteId(Integer localId) throws OseeCoreException;

   void store(Collection<Long> remoteIds) throws OseeCoreException;

   void clear();

}