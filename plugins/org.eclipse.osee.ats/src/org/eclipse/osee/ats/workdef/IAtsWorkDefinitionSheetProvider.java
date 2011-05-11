/*
 * Created on Jan 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;

public interface IAtsWorkDefinitionSheetProvider {

   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets();
}
