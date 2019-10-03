/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author David W. Miller
 */
public class TraceInformationAccumulator {
   private final ISheetWriter traceWriter;
   private final TraceReportGenerator traceReport;

   private List<ArtifactReadable> softwareRequirements = null;
   private List<ArtifactReadable> components = null;
   private List<ArtifactReadable> tests = null;
   private List<ArtifactReadable> testPlans = null;

   public TraceInformationAccumulator(TraceReportGenerator TraceReportGenerator, ISheetWriter providedWriter) {
      traceReport = TraceReportGenerator;
      traceWriter = providedWriter;
   }

   public void outputSubsystemsRequirementsMap(ArtifactReadable systemReqt, ISheetWriter writer) throws IOException {
      softwareRequirements = systemReqt.getRelated(CoreRelationTypes.RequirementTrace_LowerLevelRequirement).getList();
      if (outputReqts() == 0) {
         writer.endRow();
      }
   }

   public void outputSubsystemsComponentsMap(ArtifactReadable systemReqt, ISheetWriter writer) throws IOException {
      components = systemReqt.getRelated(CoreRelationTypes.Allocation_Component).getList();
      if (outputItems(components) == 0) {
         writer.endRow();
      }
   }

   public void outputSubsystemsTestsMap(ArtifactReadable systemReqt, ISheetWriter writer) throws IOException {
      tests = systemReqt.getRelated(CoreRelationTypes.Verification_Verifier).getList();
      if (outputItems(tests) == 0) {
         writer.endRow();
      }
   }

   public void outputSubsystemsTestPlansMap(ArtifactReadable systemReqt, ISheetWriter writer) throws IOException {
      testPlans = systemReqt.getRelated(CoreRelationTypes.VerificationPlan_TestPlanElement).getList();
      if (outputItems(testPlans) == 0) {
         writer.endRow();
      }
   }

   private int outputReqts() throws IOException {
      int count = 0;
      for (ArtifactReadable ar : softwareRequirements) {
         processSoftwareRequirement(ar);
         traceWriter.endRow();
         count++;
      }
      return count;
   }

   private int outputItems(List<ArtifactReadable> items) throws IOException {
      int count = 0;
      for (ArtifactReadable ar : items) {
         traceWriter.writeCell(ar.getName(), TraceReportGenerator.SOFTWARE_SHEETREQ_INDEX);
         traceWriter.writeCell(ar.getId());
         traceWriter.endRow();
         count++;
      }
      return count;
   }

   private void processSoftwareRequirement(ArtifactReadable softwareRequirement) throws IOException {
      traceWriter.writeCell(handleEquivalentName(softwareRequirement), TraceReportGenerator.SOFTWARE_REQUIREMENT_INDEX);

      traceWriter.writeCell(softwareRequirement.getId());

      traceWriter.writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.SeverityCategory, "none"));

      int size = 0;

      size = processSoftwareReqtSubItem(softwareRequirement, CoreRelationTypes.Allocation_Component,
         TraceReportGenerator.SOFTWARE_COMPONENT_INDEX);
      size += processSoftwareReqtSubItem(softwareRequirement, CoreRelationTypes.Verification_Verifier,
         TraceReportGenerator.SOFTWARE_PROCEDURE_INDEX);// test procedure
      size += processSoftwareReqtSubItem(softwareRequirement, CoreRelationTypes.Validation_Validator,
         TraceReportGenerator.SOFTWARE_SCRIPT_INDEX); // test script???

      Collection<String> codeUnits = traceReport.getRequirementToCodeUnitsValues(softwareRequirement);

      if (Conditions.hasValues(codeUnits)) {
         for (String codeUnit : codeUnits) {
            traceWriter.writeCell(codeUnit, TraceReportGenerator.SOFTWARE_CODEUNIT_INDEX);
            traceWriter.endRow();
            size++;
         }
      }

      Collection<String> traceUnits = traceReport.getRequirementToTraceUnitsValues(softwareRequirement);

      if (Conditions.hasValues(traceUnits)) {
         for (String traceUnit : traceUnits) {
            traceWriter.writeCell(traceUnit, TraceReportGenerator.SOFTWARE_TRACEUNIT_INDEX);
            traceWriter.endRow();
            size++;
         }
      }
      if (size == 0) { // no end rows from sub elements
         traceWriter.endRow();
      }

   }

   public String handleEquivalentName(ArtifactReadable softwareRequirement) {
      String toReturn = softwareRequirement.getName();
      if (softwareRequirement.isOfType(traceReport.getAlternateArtifactType())) {
         AttributeTypeToken attrType = traceReport.getAlternateAttributeType();
         if (attrType != null) {
            toReturn = softwareRequirement.getSoleAttributeAsString(attrType, toReturn);
         }
      }
      return toReturn;
   }

   private int processSoftwareReqtSubItem(ArtifactReadable softwareReqt, RelationTypeSide type, int location) throws IOException {
      List<ArtifactReadable> items = softwareReqt.getRelated(type).getList();
      if (items.size() > 0) {
         for (ArtifactReadable ar : items) {
            traceWriter.writeCell(ar.getName(), location);
            traceWriter.writeCell(ar.getId());
            traceWriter.endRow();
         }
      }
      return items.size();
   }
}