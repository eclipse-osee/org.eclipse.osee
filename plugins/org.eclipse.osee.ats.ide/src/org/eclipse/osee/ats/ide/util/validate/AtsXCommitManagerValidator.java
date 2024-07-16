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

import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsXCommitManagerValidator extends AtsXWidgetValidator {

   public static final String ALL_BRANCHES_MUST_BE_COMMITTED = "All branches must be configured for commit.";
   protected static final String ALL_BRANCHES_MUST_BE_CONFIGURED_FOR_COMMIT =
      "All branches must be configured for commit.";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Success;
      if ("XCommitManager".equals(widgetDef.getXWidgetName())) {
         try {
            IAtsBranchService branchService = atsServices.getBranchService();
            if (provider instanceof org.eclipse.osee.ats.core.util.ArtifactValueProvider) {
               org.eclipse.osee.ats.core.util.ArtifactValueProvider valueProvider =
                  (org.eclipse.osee.ats.core.util.ArtifactValueProvider) provider;
               if (valueProvider.getObject() instanceof IAtsTeamWorkflow) {
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) valueProvider.getObject();

                  boolean workingBranchInWork = branchService.isWorkingBranchInWork(teamWf);
                  boolean committedBranchExists = branchService.isCommittedBranchExists(teamWf);
                  boolean hasBranchesLeftToCommit = !branchService.getBranchesLeftToCommit(teamWf).isEmpty();

                  boolean changesExistToCommit =
                     (workingBranchInWork || committedBranchExists) && hasBranchesLeftToCommit;

                  if (changesExistToCommit) {
                     boolean allObjectsToCommitToConfigured = branchService.isAllObjectsToCommitToConfigured(teamWf);
                     if (!allObjectsToCommitToConfigured) {
                        return new WidgetResult(WidgetStatus.Invalid_Incompleted,
                           TransitionResult.NOT_ALL_BRANCHES_COMMITTED.toString());
                     }
                     if (!transitionToWithWorkingBranchRuleExists(toStateDef) && !branchService.isBranchesAllCommitted(
                        teamWf)) {
                        return new WidgetResult(WidgetStatus.Invalid_Incompleted,
                           TransitionResult.NOT_ALL_BRANCHES_COMMITTED.toString());
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return new WidgetResult(WidgetStatus.Exception, ex,
               "Exception validating Commits for transition validation [%s]; see error log", ex.getLocalizedMessage());
         }
      }
      return result;
   }

   protected boolean transitionToWithWorkingBranchRuleExists(StateDefinition toStateDef) {
      return toStateDef.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name());
   }

}
