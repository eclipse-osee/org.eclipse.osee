/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Ryan Schmitt
 */
public class SubsystemRequirementVerificationLevel extends AbstractBlam {

   @Override
   public Collection<String> getCategoriesStr() {
      return Arrays.asList("Define");
   }

   @Override
   public String getName() {
      return "Set Verification Level for Subsystem Requirements";
   }

   private BranchId branch;
   private Collection<Artifact> subsystemRequirements;
   private StringBuilder report;
   private SkynetTransaction transaction;
   private final String[] columnHeaders = {
      "Requirement",
      "Subsystem",
      CoreAttributeTypes.ParagraphNumber.getName(),
      "Current Verification Level",
      "Changed"};

   @SuppressWarnings("unused")
   private Collection<Artifact> bulkRequirements;

   private void loadFields(VariableMap variableMap) {
      branch = variableMap.getBranch("Branch");
      subsystemRequirements =
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMsWord, branch);
      bulkRequirements = RelationManager.getRelatedArtifacts(subsystemRequirements, 1,
         CoreRelationTypes.RequirementTrace_LowerLevelRequirement);
      report = new StringBuilder(AHTML.beginMultiColumnTable(100, 1));
      transaction = TransactionManager.createTransaction(branch, "Set Verification Level for Subsystem Requirements");
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      loadFields(variableMap);
      beginReport();

      for (Artifact req : subsystemRequirements) {
         processSubsystemRequirement(req);
      }

      report();
      transaction.execute();
   }

   private void report() {
      report.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(report.toString());
      XResultDataUI.report(rd, "Set Verification Level", Manipulations.RAW_HTML);
   }

   private void beginReport() {
      report.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
   }

   private void addReportRow(String... cells) {
      report.append(AHTML.addRowMultiColumnTable(cells));
   }

   private void processSubsystemRequirement(Artifact reqArt) {
      SubsystemRequirement req = new SubsystemRequirement(reqArt);
      req.process();
   }

   private class SubsystemRequirement {
      private final Artifact req;
      private int hardwareComponents;
      private int softwareRequirements;
      private String verificationLevel;
      private String paragraphNumber;
      private String subsystem;

      public SubsystemRequirement(Artifact req) {
         this.req = req;
      }

      public void process() {
         getData();
         if (meetsCriteria()) {
            if (isUnspecified()) {
               adjustVerificationLevel();
            }
            report();
         }
      }

      private void getData() {
         this.hardwareComponents = getHardwareComponentCount();
         this.softwareRequirements = getSoftwareRequirementCount();
         paragraphNumber = req.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "UNDEFINED");
         subsystem = req.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "UNDEFINED");
         verificationLevel = req.getSoleAttributeValue(CoreAttributeTypes.VerificationLevel, "UNDEFINED");
      }

      private int getHardwareComponentCount() {
         return RelationManager.getRelatedArtifactsCount(req, CoreRelationTypes.Allocation_Component);
      }

      private int getSoftwareRequirementCount() {
         Collection<Artifact> traceCollection =
            RelationManager.getRelatedArtifacts(req, CoreRelationTypes.RequirementTrace_LowerLevelRequirement);
         int ret = 0;
         for (Artifact trace : traceCollection) {
            if (trace.isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)) {
               ret++;
            }
         }
         return ret;
      }

      private boolean meetsCriteria() {
         return hardwareComponents == 1 && softwareRequirements == 0;
      }

      private void adjustVerificationLevel() {
         req.setSoleAttributeValue(CoreAttributeTypes.VerificationLevel, "Component");
         req.persist(SubsystemRequirementVerificationLevel.this.transaction);
      }

      public void report() {
         SubsystemRequirementVerificationLevel.this.addReportRow(req.getName(), subsystem, paragraphNumber,
            verificationLevel, String.valueOf(isUnspecified()));
      }

      private boolean isUnspecified() {
         return verificationLevel.equals(AttributeId.UNSPECIFIED);
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}
