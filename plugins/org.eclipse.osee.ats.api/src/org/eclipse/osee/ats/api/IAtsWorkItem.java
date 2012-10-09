/*
 * Created on Feb 9, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api;

import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.HasStateProvider;
import org.eclipse.osee.ats.api.workflow.HasWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItem extends IAtsObject, HasWorkData, HasAssignees, HasStateProvider {

   IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException;

}
