/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

public interface IAtsModelingService {

   WorkFlowDefinition getWorkFlowDefinition(String id);
}
