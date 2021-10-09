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

package org.eclipse.osee.ats.ide.util.validate;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Test unit for {@link
 *
 * @author Donald G. Dunne
 */
public class AtsOperationalImpactValidator extends AtsXWidgetValidator {
   public static String WIDGET_NAME = "OperationalImpactXWidget";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Success;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         if (provider instanceof ArtifactValueProvider && ((ArtifactValueProvider) provider).getArtifact() instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) ((ArtifactValueProvider) provider).getArtifact();
            String impact = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, "No");
            if (impact.equals("Yes")) {
               String desc = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, "");
               if (!Strings.isValid(desc)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, "Must enter [%s]", widgetDef.getName());
               }
            }
         }
      }
      return result;
   }
}
