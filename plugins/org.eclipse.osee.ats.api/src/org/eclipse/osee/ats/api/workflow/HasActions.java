/*
 * Created on Feb 9, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface HasActions {
   public Collection<IAtsWorkItem> getActions() throws OseeCoreException;

   public IAtsWorkItem getFirstAction() throws OseeCoreException;

}
