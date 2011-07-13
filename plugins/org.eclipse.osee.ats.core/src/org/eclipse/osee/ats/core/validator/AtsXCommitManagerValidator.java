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
            if (provider instanceof ArtifactValueProvider && ((ArtifactValueProvider) provider).getArtifact() instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) ((ArtifactValueProvider) provider).getArtifact();
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
