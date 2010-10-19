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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData.KindType;
import org.eclipse.osee.framework.ui.skynet.ArtifactValidationCheckOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ValidationReportOperation extends AbstractOperation {
   final Set<AttributeSetRule> attributeSetRules;
   final Set<RelationSetRule> relationSetRules;
   final TeamWorkFlowArtifact teamArt;
   final XResultData rd;

   public ValidationReportOperation(XResultData rd, TeamWorkFlowArtifact teamArt, Set<AttributeSetRule> attributeSetRules, Set<RelationSetRule> relationSetRules) {
      super("Validate Requirement Changes - " + teamArt.getName(), SkynetGuiPlugin.PLUGIN_ID);
      this.rd = rd;
      this.teamArt = teamArt;
      this.attributeSetRules = attributeSetRules;
      this.relationSetRules = relationSetRules;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      rd.log("<b>Validating Requirement Changes for " + teamArt.getName() + "</b>");
      for (AttributeSetRule attributeSetRule : attributeSetRules) {
         rd.log("<b>Attribute Check: </b>" + attributeSetRule.toString());
      }
      for (RelationSetRule relationSetRule : relationSetRules) {
         rd.log("<b>Relations Check: </b>" + relationSetRule.toString());
      }
      rd.log("<b>Artifact Validation Checks: </b> All Errors reported must be fixed.");
      rd.log("<br><br><b>NOTE: </b>All errors are shown for artifact state on branch or at time of commit.  Select hyperlink to open most recent version of artifact.");
      try {
         ChangeData changeData = teamArt.getBranchMgr().getChangeDataFromEarliestTransactionId();
         Collection<Artifact> changedArtifacts =
            changeData.getArtifacts(KindType.ArtifactOrRelation, ModificationType.NEW, ModificationType.MODIFIED);

         runOperations(changedArtifacts, rd);

         rd.log("Validation Complete");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         rd.logError(ex.getLocalizedMessage());
      }
      createReport();
   }

   private void createReport() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            String title = getName();
            if (title.length() > 60) {
               title = title.substring(0, 59) + "...";
            }
            rd.report(title, Manipulations.CONVERT_NEWLINES, Manipulations.ERROR_RED,
               Manipulations.ERROR_WARNING_HEADER, Manipulations.WARNING_YELLOW);
         }
      });
   }

   private void runOperations(Collection<Artifact> itemsToCheck, XResultData rd) {
      List<IOperation> operations = new ArrayList<IOperation>();
      operations.add(new AttributeRuleCheckOperation(itemsToCheck, rd, attributeSetRules));
      operations.add(new RelationRuleCheckOperation(itemsToCheck, rd, relationSetRules));
      operations.add(new ArtifactValidationCheckOperation(itemsToCheck, false));
      CompositeOperation operation = new CompositeOperation(getName(), AtsPlugin.PLUGIN_ID, operations);

      IStatus status = Operations.executeWork(operation);
      if (!status.isOK()) {
         if (status.isMultiStatus()) {
            for (IStatus child : status.getChildren()) {
               ValidateReqChangeReport.reportStatus(rd, child);
            }
         } else {
            ValidateReqChangeReport.reportStatus(rd, status);
         }
      }
   }
}