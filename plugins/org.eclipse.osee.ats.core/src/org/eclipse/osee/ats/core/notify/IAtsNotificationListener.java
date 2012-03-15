/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.notify;

import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsNotificationListener {

   public void notifyAssigned(List<IAtsUser> notifyAssignees) throws OseeCoreException;
}
