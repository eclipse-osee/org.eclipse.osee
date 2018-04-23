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
package org.eclipse.osee.ats.util.validate;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsXCommitManagerValidator extends AtsXWidgetValidator {

   public static final String ALL_BRANCHES_MUST_BE_COMMITTED = "All branches must be committed.";
   protected static final String ALL_BRANCHES_MUST_BE_CONFIGURED_FOR_COMMIT =
      "All branches must be configured for commit.";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Valid;
      if ("XCommitManager".equals(widgetDef.getXWidgetName())) {
         try {
            IAtsBranchService branchService = atsServices.getBranchService();
            if (provider instanceof ArtifactValueProvider) {
               ArtifactValueProvider valueProvider = (ArtifactValueProvider) provider;
               if (valueProvider.getObject() instanceof IAtsTeamWorkflow) {
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) valueProvider.getObject();

                  boolean workingBranchInWork = branchService.isWorkingBranchInWork(teamWf);
                  boolean committedBranchExists = branchService.isCommittedBranchExists(teamWf);

                  boolean changesExistToCommit = workingBranchInWork || committedBranchExists;

                  if (changesExistToCommit) {
                     boolean allObjectsToCommitToConfigured = branchService.isAllObjectsToCommitToConfigured(teamWf);
                     if (!allObjectsToCommitToConfigured) {
                        return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                           ALL_BRANCHES_MUST_BE_CONFIGURED_FOR_COMMIT);
                     }
                     if (!transitionToWithWorkingBranchRuleExists(toStateDef) && !branchService.isBranchesAllCommitted(
                        teamWf)) {
                        return new WidgetResult(WidgetStatus.Invalid_Incompleted, widgetDef,
                           ALL_BRANCHES_MUST_BE_COMMITTED);
                     }
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

   protected boolean transitionToWithWorkingBranchRuleExists(IAtsStateDefinition toStateDef) {
      return toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name());
   }

}
