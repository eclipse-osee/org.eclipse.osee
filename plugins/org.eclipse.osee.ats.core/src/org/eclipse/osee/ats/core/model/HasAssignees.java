/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface HasAssignees {

   public List<IAtsUser> getAssignees() throws OseeCoreException;

   public List<IAtsUser> getImplementers() throws OseeCoreException;

}
