/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workflow.transition;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;

/**
 * Provider to validate XWidget/IAttributeWidget entry against workitem store model without use of XWidget UI
 * 
 * @author Donald G. Dunne
 */
public interface IAtsXWidgetValidator {

   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices);

}
