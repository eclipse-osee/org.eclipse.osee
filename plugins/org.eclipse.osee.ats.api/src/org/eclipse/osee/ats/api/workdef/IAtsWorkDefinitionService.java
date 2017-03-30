/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionService {

   IAtsWorkDefinition getWorkDef(String id, XResultData resultData) throws Exception;

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData);

   boolean isStateWeightingEnabled(IAtsWorkDefinition workDef);

   Collection<String> getStateNames(IAtsWorkDefinition workDef);

   List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef);

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

   boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption rule);

   boolean isInState(IAtsWorkItem workItem, IAtsStateDefinition stateDef);

   Collection<IAtsWorkDefinition> getAllWorkDefinitions(XResultData resultData) throws OseeCoreException, Exception;

   Collection<String> getAllValidStateNames(XResultData resultData);

   List<IAtsRuleDefinition> getRuleDefinitions();

   void setWorkDefinitionStringProvider(IWorkDefinitionStringProvider workDefinitionStringProvider);

}
