/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.define.rest.internal;

import org.eclipse.osee.define.rest.GenericReportBuilder;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author David W. Miller
 */
public class GenericReportCode {
   public void reportCode(GenericReportBuilder report) {
      report.level("System Functions",
         report.query().andIsOfType(new ArtifactTypeToken[] {CoreArtifactTypes.SystemFunctionMsWord})). //
         column("Artifact Id"). //
         column("System Function Name", CoreAttributeTypes.Name). //
         column(CoreAttributeTypes.FDAL). //
         column(CoreAttributeTypes.FdalRationale); //

      report.level("Subsystem Functions", report.query().follow(CoreRelationTypes.Dependency_Dependency)). //
         column("Artifact Id"). //
         column("Subsystem Function Name", CoreAttributeTypes.Name). //
         column(CoreAttributeTypes.FDAL). //
         column(CoreAttributeTypes.FdalRationale); //

      report.level("Subsystem Requirements", report.query().follow(CoreRelationTypes.Design_Requirement)). //
         column("Artifact Id"). //
         column("Subsystem Requirement Name", CoreAttributeTypes.Name). //
         column(CoreAttributeTypes.IDAL). //
         column(CoreAttributeTypes.IdalRationale); //

      report.level("Software Requirements",
         report.query().follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)). //
         column("Artifact Id"). //
         column("Software Requirement Name", CoreAttributeTypes.Name). //
         column(CoreAttributeTypes.IDAL). //
         column(CoreAttributeTypes.IdalRationale); //

   }

   public void traceCode(GenericReportBuilder report) {

      report.level("Subsystem Requirement", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //
         column("Artifact Id"). //
         column("Subsystem Function Name", CoreAttributeTypes.Name); //

      report.level("Related Code Unit", report.query().follow(CoreRelationTypes.CodeRequirement_CodeUnit)). //
         column("Artifact Id"). //
         column("Code Unit", CoreAttributeTypes.Name). //
         column("File System Path", CoreAttributeTypes.FileSystemPath); //

   }

   public void backTraceCode(GenericReportBuilder report) {

      report.level("Code Unit", report.query().andIsOfType(CoreArtifactTypes.CodeUnit)). //
         column("Artifact Id"). //
         column("Code Unit", CoreAttributeTypes.Name). //
         column("File System Path", CoreAttributeTypes.FileSystemPath); //

      report.level("Related Subsystem Requirement",
         report.query().follow(CoreRelationTypes.CodeRequirement_Requirement)). //
         column("Artifact Id"). //
         column("Subsystem Requirement Name", CoreAttributeTypes.Name); //

   }

   /*
    * level types - id with relation type collector
    */
   public void allocationTraceCode(GenericReportBuilder report) {

      report.level("Allocation Report", report.query().andId(ArtifactId.valueOf(579055L))). //
         column("Artifact Id"). //
         column("Allocation Parent Name", CoreAttributeTypes.Name); //
      report.level("System Requirements", report.query().follow(CoreRelationTypes.Allocation_Requirement)). //
         column("System Req Name", CoreAttributeTypes.Name); //
      report.level("Software Requirements",
         report.query().follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)). //
         column(CoreAttributeTypes.Name). //
         column(CoreAttributeTypes.IDAL); //

   }
}
