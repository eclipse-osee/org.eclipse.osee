/*
 * Created on Jul 17, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

public interface IAtsWorkDefinitionProviderService {

   void addWorkDefinitionProvider(IAtsWorkDefinitionProvider workDefProvider);

   IAtsWorkDefinition getWorkDefinition(Long id);

   Collection<IAtsWorkDefinition> getAll();

   void addWorkDefinition(WorkDefinition workDef);

}