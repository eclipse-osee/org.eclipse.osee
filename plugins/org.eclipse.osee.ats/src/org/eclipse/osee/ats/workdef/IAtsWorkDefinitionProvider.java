/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public interface IAtsWorkDefinitionProvider {

   WorkDefinition getWorkFlowDefinition(String id);

   void convertAndOpenAtsDsl(WorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException;
}
