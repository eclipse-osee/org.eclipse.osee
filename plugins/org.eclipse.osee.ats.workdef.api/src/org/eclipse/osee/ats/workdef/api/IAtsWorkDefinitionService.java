/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.util.XResultData;

public interface IAtsWorkDefinitionService {

   IAtsWorkDefinition getWorkDef(String id, XResultData resultData) throws Exception;

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData, IAttributeResolver resolver, IUserResolver iUserResolver);

   boolean isStateWeightingEnabled(IAtsWorkDefinition workDef);

   Collection<String> getStateNames(IAtsWorkDefinition workDef);

   List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef);

   List<IAtsStateDefinition> getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef);

   void getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef, IAtsStateDefinition stateDefinition, List<IAtsStateDefinition> pages);

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef);

   boolean hasWidgetNamed(IAtsStateDefinition stateDef, String name);

   IAtsWorkDefinition getWorkDefinition(String workDefinitionDsl) throws Exception;

   String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception;

}
