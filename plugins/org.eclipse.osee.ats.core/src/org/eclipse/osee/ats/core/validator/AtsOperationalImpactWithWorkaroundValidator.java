/*
 * Created on Jun 13, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class AtsOperationalImpactWithWorkaroundValidator implements IAtsXWidgetValidator {

   public static String WIDGET_NAME = "OperationalImpactWithWorkaroundXWidget";

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
                     AtsAttributeTypes.OperationalImpactDescription.getName());
               }
            }
            boolean workaroundChecked =
               teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, false);
            if (workaroundChecked) {
               String desc =
                  teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, "");
               if (!Strings.isValid(desc)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef, "Must enter [%s]",
                     AtsAttributeTypes.OperationalImpactWorkaroundDescription.getName());
               }
            }

         }
      }
      return result;
   }

}
