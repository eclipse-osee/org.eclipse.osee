/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Donald G. Dunne
 */
public class ValidationReportOperation extends AbstractOperation {

   private final TeamWorkFlowArtifact teamArt;
   private final Set<AbstractValidationRule> rules;
   private final XResultData results;

   public ValidationReportOperation(XResultData results, TeamWorkFlowArtifact teamArt, Set<AbstractValidationRule> rules) {
      super("Validate Requirement Changes - " + teamArt.getName(), Activator.PLUGIN_ID, null);
      this.results = results;
      this.teamArt = teamArt;
      this.rules = rules;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      results.logf("<b>Validating Requirement Changes for %s</b>\n", teamArt.getName());

      List<AbstractValidationRule> rulesSorted = new ArrayList<>(rules);
      Collections.sort(rulesSorted, new ValidationRuleComparator());

      results.log(
         "<b>NOTE: </b>All errors are shown for artifact state on branch or at time of commit.  Select hyperlink to open most recent version of artifact.");

      try {
         ChangeData changeData = AtsBranchManager.getChangeDataFromEarliestTransactionId(teamArt);
         Collection<Artifact> changedArtifacts =
            changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.APPLICABILITY,
               ModificationType.INTRODUCED, ModificationType.MERGED, ModificationType.REPLACED_WITH_VERSION,
               ModificationType.UNDELETED, ModificationType.NEW, ModificationType.MODIFIED, ModificationType.DELETED);
         checkForCancelledStatus(monitor);

         double total = changedArtifacts.size() * rules.size();
         if (total > 0) {
            int workAmount = calculateWork(1 / total);

            String lastTitle = "";
            int ruleIndex = 1;
            for (AbstractValidationRule rule : rulesSorted) {
               checkForCancelledStatus(monitor);

               //check to see if we should print a header for sorted (and grouped) rule types
               String currentTitle = rule.getRuleTitle();
               if (!lastTitle.equals(currentTitle)) {
                  processNewTitle(rule, rulesSorted, results);
                  lastTitle = rule.getRuleTitle();
               }

               if (isSkipRelationCheck(rule.getRuleTitle())) {
                  results.logf("INFO: Relations Check skipped:  detected committed branches.");
               } else if (isSkipOrphanCheck(rule.getRuleTitle())) {
                  results.logf("INFO: Orphan Check skipped");
               } else {
                  int artIndex = 1;
                  for (Artifact art : changedArtifacts) {
                     try {
                        monitor.setTaskName(String.format("Validating: Rule[%s of %s] Artifact[%s of %s]", ruleIndex,
                           rules.size(), artIndex, changedArtifacts.size()));
                        checkForCancelledStatus(monitor);

                        rule.validate(art, results);
                     } catch (Exception ex) {
                        results.logf("Exception processing artifact %s Exception ex: %s\n", art.toStringWithId(),
                           ex.getMessage());
                     }
                     monitor.worked(workAmount);
                     artIndex++;
                  }
               }
               ruleIndex++;
            }
         }

         results.log("\n<b>Validation Complete</b>");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         results.error(Lib.exceptionToString(ex));
      }
   }

   private void processNewTitle(AbstractValidationRule newRule, List<AbstractValidationRule> rulesSorted, XResultData results) {
      results.logf("\n<b>%s</b>\n", newRule.getRuleTitle());
      for (AbstractValidationRule rule : rulesSorted) {
         if (rule.getRuleTitle().equals(newRule.getRuleTitle())) {
            results.logf("%s\n", rule.getRuleDescription());
         }
      }
      results.logf("========================================\n");
   }

   private boolean isSkipOrphanCheck(String ruleTitle) {
      return ruleTitle.equals("Orphan Validation Checks");
   }

   private boolean isSkipRelationCheck(String ruleTitle) {
      IAtsClient atsClient = AtsClientService.get();
      Conditions.checkNotNull(atsClient, "AtsClientService");
      IAtsBranchService branchService = atsClient.getBranchService();
      Conditions.checkNotNull(branchService, "AtsBranchService");
      return branchService.isBranchesAllCommitted(teamArt) && ruleTitle.equals("Relations Check:");
   }

   private final class ValidationRuleComparator implements Comparator<AbstractValidationRule> {

      @Override
      public int compare(AbstractValidationRule o1, AbstractValidationRule o2) {
         String title1 = o1.getRuleTitle();
         String title2 = o2.getRuleTitle();
         if (title1 == null) {
            title1 = "";
         }
         if (title2 == null) {
            title2 = "";
         }
         return title1.compareTo(title2);
      }
   }

}