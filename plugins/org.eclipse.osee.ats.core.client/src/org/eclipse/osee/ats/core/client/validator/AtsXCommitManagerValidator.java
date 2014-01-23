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
package org.eclipse.osee.ats.core.client.validator;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.core.validator.IValueProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsXCommitManagerValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) {
      WidgetResult result = WidgetResult.Valid;
      if ("XCommitManager".equals(widgetDef.getXWidgetName())) {
         try {
            if (provider instanceof ArtifactValueProvider && ((ArtifactValueProvider) provider).getArtifact() instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) ((ArtifactValueProvider) provider).getArtifact();
               if (AtsBranchManagerCore.isWorkingBranchInWork(teamArt) || AtsBranchManagerCore.isCommittedBranchExists(teamArt)) {
                  if (!AtsBranchManagerCore.isAllObjectsToCommitToConfigured(teamArt)) {
                     return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                        "All branches must be configured and committed.");
                  } else if (!transitionToWithWorkingBranchRuleExists(toStateDef) && !AtsBranchManagerCore.isBranchesAllCommitted(teamArt)) {
                     return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                        "All branches must be committed.");
                  }
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

   private boolean transitionToWithWorkingBranchRuleExists(IAtsStateDefinition toStateDef) {
      return toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name());
   }

}
