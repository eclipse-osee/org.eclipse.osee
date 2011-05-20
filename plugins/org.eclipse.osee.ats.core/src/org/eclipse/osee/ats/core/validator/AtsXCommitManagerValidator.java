/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsXCommitManagerValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) {
      WidgetResult result = WidgetResult.Valid;
      if ("XCommitManager".equals(widgetDef.getXWidgetName())) {
         try {
            if (provider instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) provider;
               if (!AtsBranchManagerCore.isAllObjectsToCommitToConfigured(teamArt)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                     "All branches must be configured and committed.");
               } else if (!AtsBranchManagerCore.isBranchesAllCommitted(teamArt)) {
                  return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                     "All branches must be committed.");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return new WidgetResult(WidgetStatus.Exception, widgetDef, ex,
               "Exception validating Commits for transition validation [%s]; see error log", ex.getLocalizedMessage());
         }
      }
      return result;
   }
}
