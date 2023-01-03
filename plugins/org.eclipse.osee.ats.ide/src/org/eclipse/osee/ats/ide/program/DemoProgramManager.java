/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.program;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramManager;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.validate.ArtifactValidationRule;
import org.eclipse.osee.ats.ide.util.validate.ValidationReportOperation;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class DemoProgramManager implements IAtsProgramManager {

   public static String NAME = "Demo";
   private static DemoProgramManager instance = new DemoProgramManager();

   public static DemoProgramManager instance() {
      return instance;
   }

   @Override
   public boolean isApplicable(IAtsTeamWorkflow teamWf) {
      return teamWf.getArtifactType().getName().startsWith("Demo ");
   }

   @Override
   public IOperation createValidateReqChangesOp(IAtsTeamWorkflow teamWf) {
      IOperation toReturn = org.eclipse.osee.framework.core.operation.Operations.getNoOpOperation();
      if (isApplicable(teamWf)) {
         IAtsProgram program = AtsApiService.get().getProgramService().getProgram(teamWf);
         final XResultData rd = new XResultData();
         if (program != null) {

            IOperation createReport = new AbstractOperation("Create Validate Rules Report", Activator.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  XResultDataUI.report(rd, "Demo Program Requirements Validation", Manipulations.CONVERT_NEWLINES,
                     Manipulations.ERROR_RED, Manipulations.ERROR_WARNING_FROM_SEARCH,
                     Manipulations.ERROR_WARNING_HEADER, Manipulations.WARNING_YELLOW);
               }
            };

            IOperation ops = createValidationOperation(teamWf, rd);
            toReturn = org.eclipse.osee.framework.core.operation.Operations.createBuilder("Validation Report", ops,
               createReport).build();
         }
      }
      return toReturn;
   }

   public final static IOperation createValidationOperation(IAtsTeamWorkflow teamWf, XResultData rd) {
      Set<AbstractValidationRule> rules = new HashSet<>();
      rules.add(new ArtifactValidationRule(AtsApiService.get()));

      IOperation operationCollectRules = createValidationRulesOperation((TeamWorkFlowArtifact) teamWf, rd, rules);

      IOperation validationOperation = new ValidationReportOperation(rd, (TeamWorkFlowArtifact) teamWf, rules);

      OperationBuilder builder = Operations.createBuilder("Requirement Change Validation Report - ",
         operationCollectRules, validationOperation);

      return builder.build();
   }

   private static IOperation createValidationRulesOperation(TeamWorkFlowArtifact teamArt, XResultData results, Set<AbstractValidationRule> rules) {
      return new DemoCreateValidationRulesOperation(results, teamArt, rules,
         AtsApiService.get().getProgramService().getWorkType(teamArt));
   }

   @Override
   public String getName() {
      return NAME;
   }

}
