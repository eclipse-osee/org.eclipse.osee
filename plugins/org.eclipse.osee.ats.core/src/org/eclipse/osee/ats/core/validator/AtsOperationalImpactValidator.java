/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Test unit for {@link 
 * @author Donald G. Dunne
 */
public class AtsOperationalImpactValidator extends AtsXWidgetValidator {
   public static String WIDGET_NAME = "OperationalImpactXWidget";

   @Override
   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      WidgetResult result = WidgetResult.Valid;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         if (provider instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) provider;
            boolean checked = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, false);
            if (checked) {
               String desc = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, "");
               if (!Strings.isValid(desc)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef, "Must enter [%s]",
                     widgetDef.getName());
               }
            }
         }
      }
      return result;
   }
}
