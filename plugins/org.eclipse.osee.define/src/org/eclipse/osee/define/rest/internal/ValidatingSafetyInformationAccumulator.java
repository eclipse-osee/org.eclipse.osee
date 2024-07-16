/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public final class ValidatingSafetyInformationAccumulator {
   private final ISheetWriter writer;
   private final ValidatingSafetyReportGenerator safetyReport;
   private String functionalCategory;
   private List<ArtifactReadable> subsystemFunctions;
   private List<ArtifactId> allowedRequirements;
   private final HashMap<ArtifactReadable, List<ArtifactReadable>> subsystemRequirements = Maps.newHashMap();
   private final HashMap<ArtifactReadable, List<ArtifactReadable>> softwareRequirements = Maps.newHashMap();

   public ValidatingSafetyInformationAccumulator(ValidatingSafetyReportGenerator providedSafetyReport, ISheetWriter providedWriter) {
      safetyReport = providedSafetyReport;
      writer = providedWriter;
   }

   public void setupPartitions(QueryFactory qf, BranchId branch, ArtifactId view) {
      allowedRequirements =
         qf.fromBranch(branch, view).andIsOfType(CoreArtifactTypes.SoftwareRequirementMsWord).asArtifactIds();
   }

   public String calculateLevelForPartition(List<String> partitions) {
      return safetyReport.getPartitionLevel(partitions);
   }

   public void reset(ArtifactReadable systemFunction) {
      functionalCategory = systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FunctionalCategory, "");
      subsystemRequirements.clear();
      softwareRequirements.clear();
   }

   public void buildSubsystemsRequirementsMap(ArtifactReadable systemFunction) {

      subsystemFunctions = Lists.newArrayList(systemFunction.getRelated(CoreRelationTypes.Dependency_Dependency));

      Iterator<ArtifactReadable> sfIter = subsystemFunctions.iterator();
      while (sfIter.hasNext()) {
         ArtifactReadable subsystemFunction = sfIter.next();
         List<ArtifactReadable> localSubsystemRequirements = checkSubsystemRequirements(subsystemFunction);
         if (localSubsystemRequirements.isEmpty()) {
            // there aren't any related software requirements, so remove the subsystemFunction
            sfIter.remove();
         } else {
            subsystemRequirements.put(subsystemFunction, localSubsystemRequirements);
         }
      }
   }

   private List<ArtifactReadable> checkSubsystemRequirements(ArtifactReadable subsystemFunction) {

      // needs related artifacts
      List<ArtifactReadable> localSubsystemRequirements =
         Lists.newArrayList(subsystemFunction.getRelated(CoreRelationTypes.Design_Requirement));

      Iterator<ArtifactReadable> ssrIter = localSubsystemRequirements.iterator();
      while (ssrIter.hasNext()) {
         ArtifactReadable subsystemRequirement = ssrIter.next();

         List<ArtifactReadable> unfilteredSoftwareRequirements = Lists.newArrayList(
            subsystemRequirement.getRelated(CoreRelationTypes.RequirementTrace_LowerLevelRequirement));

         List<ArtifactReadable> localSoftwareRequirements = new ArrayList<>();
         for (ArtifactReadable art : unfilteredSoftwareRequirements) {
            if (allowedRequirements.contains(art)) {
               localSoftwareRequirements.add(art);
            }
         }

         if (localSoftwareRequirements.isEmpty()) {
            //remove the subsystemRequirement
            ssrIter.remove();
         } else {
            // save
            softwareRequirements.put(subsystemRequirement, localSoftwareRequirements);
         }
      }
      return localSubsystemRequirements;
   }

   public void output(String[] currentRowValues) throws IOException {
      for (ArtifactReadable subsystemFunction : subsystemFunctions) {
         processSubsystemFunction(subsystemFunction, currentRowValues);
      }
   }

   private String convertSafetyCriticalityToDAL(String inputSafetyCriticality) {
      if (inputSafetyCriticality.length() > 4) {
         return "Error";
      }
      return SafetyCriticalityLookup.getDALLevelFromSeverityCategory(inputSafetyCriticality);
   }

   private void processSubsystemFunction(ArtifactReadable subsystemFunction, String[] currentRowValues)
      throws IOException {
      writeCell(subsystemFunction.getName(), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_FUNCTION_INDEX);
      String sevCat =
         subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.SeverityCategory, "Error: not available");
      writeCell(sevCat, currentRowValues, ValidatingSafetyReportGenerator.SUBSYSTEM_FUNCTION_INDEX + 1);
      writeCell(subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.FDAL, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_FUNCTION_INDEX + 2);
      writeCell(subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.FdalRationale, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_FUNCTION_INDEX + 3);

      for (ArtifactReadable subsystemRequirement : subsystemRequirements.get(subsystemFunction)) {
         processSubsystemRequirement(subsystemRequirement, convertSafetyCriticalityToDAL(sevCat), currentRowValues);
      }
      if (safetyReport.saveToPreviousAndCheckIfDifferent(currentRowValues)) {
         writer.writeRow((Object[]) currentRowValues);
      }
   }

   private void writeCell(String value, String[] currentRow, int col) {
      currentRow[col] = value;
   }

   private void processSubsystemRequirement(ArtifactReadable subsystemRequirement, String criticality,
      String[] currentRowValues) throws IOException {
      writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.Subsystem, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX);
      writeCell(subsystemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX + 1);
      writeCell(subsystemRequirement.getName(), currentRowValues, ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX + 2);
      writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.IDAL, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX + 3);
      writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.IdalRationale, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX + 4);

      String currentCriticality = writeCriticalityWithDesignCheck(subsystemRequirement, criticality,
         CoreAttributeTypes.IDAL, CoreRelationTypes.Design_Design, CoreAttributeTypes.SeverityCategory,
         currentRowValues, ValidatingSafetyReportGenerator.SUBSYSTEM_INDEX + 5);
      for (ArtifactReadable softwareRequirement : softwareRequirements.get(subsystemRequirement)) {
         processSoftwareRequirement(softwareRequirement, currentCriticality, currentRowValues);
      }
   }

   private String writeCriticalityWithDesignCheck(ArtifactReadable art, String criticality, AttributeTypeToken thisType,
      RelationTypeSide relType, AttributeTypeToken otherType, String[] currentRowValues, int col) {
      String current = art.getSoleAttributeAsString(thisType, "Error");
      if ("Error".equals(criticality) || "Error".equals(current)) {
         writeCell("Error: invalid content", currentRowValues, col);
         return "Error";
      }

      if (AttributeId.UNSPECIFIED.equals(current)) {
         writeCell(AttributeId.UNSPECIFIED, currentRowValues, col);
         return AttributeId.UNSPECIFIED;
      }

      if (AttributeId.UNSPECIFIED.equals(criticality)) {
         criticality = "E";
      }

      /**
       * check to see if the safety criticality of the child at least equal to the parent
       */
      // error check removed at users request, print value instead
      writeCell(current, currentRowValues, col);
      //      int parentValue = SafetyCriticalityLookup.getDALLevel(criticality);
      //      int childValue = SafetyCriticalityLookup.getDALLevel(current);
      //
      //      if (parentValue < childValue) {
      //         writeCell(String.format("%s [Error:<%s]", current, criticality), currentRowValues, col);
      //      } else {
      //         checkBackTrace(art, childValue, thisType, relType, otherType, currentRowValues, col);
      //      }
      return current;
   }

   private void checkBackTrace(ArtifactReadable art, Integer current, AttributeTypeId thisType,
      RelationTypeSide relType, AttributeTypeToken otherType, String[] currentRowValues, int col) {
      /**
       * when the parent criticality is less critical than the child, we check to see if the child traces to any more
       * critical parent (thus justifying the criticality of the child) note: more critical = lower number
       */
      List<ArtifactReadable> tracedToRequirements = Lists.newArrayList(art.getRelated(relType));
      int maxCritVal = 4;
      int parentCritVal = 4;

      for (ArtifactReadable parent : tracedToRequirements) {
         if (otherType.equals(CoreAttributeTypes.SeverityCategory)) {
            String intermediate = parent.getSoleAttributeAsString(otherType, "NH");
            if (AttributeId.UNSPECIFIED.equals(intermediate)) {
               intermediate = "NH";
            }
            parentCritVal = SafetyCriticalityLookup.getSeverityLevel(intermediate);
         } else if (otherType.equals(CoreAttributeTypes.IDAL)) {
            String intermediate = parent.getSoleAttributeAsString(otherType, "E");
            if (AttributeId.UNSPECIFIED.equals(intermediate)) {
               intermediate = "E";
            }
            parentCritVal = SafetyCriticalityLookup.getDALLevel(intermediate);
         } else {
            throw new OseeArgumentException("Invalid attribute type: %s", otherType.toString());
         }

         maxCritVal = Integer.min(maxCritVal, parentCritVal);
      }
      if (current < maxCritVal) {
         writeCell(String.format("%s [Error:<%s]", SafetyCriticalityLookup.getDALLevelFromInt(current),
            SafetyCriticalityLookup.getDALLevelFromInt(maxCritVal)), currentRowValues, col);
      } else {
         writeCell(SafetyCriticalityLookup.getDALLevelFromInt(current), currentRowValues, col);
      }
   }

   private void processSoftwareRequirement(ArtifactReadable softwareRequirement, String sevCat,
      String[] currentRowValues) throws IOException {
      writeCell(softwareRequirement.getName(), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX);
      writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.IDAL, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 1);
      writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.IdalRationale, ""), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 2);
      writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.SoftwareControlCategory, ""),
         currentRowValues, ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 3);
      writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.SoftwareControlCategoryRationale, ""),
         currentRowValues, ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 4);
      List<String> partitions = softwareRequirement.getAttributeValues(CoreAttributeTypes.Partition);

      writeCell(calculateLevelForPartition(partitions), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 5);
      writeCell(functionalCategory, currentRowValues, ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 6);

      writeCell(softwareRequirement.getAttributeValuesAsString(CoreAttributeTypes.Partition), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 7);

      writeCell(safetyReport.getComponentUtil().getQualifiedComponentNames(softwareRequirement), currentRowValues,
         ValidatingSafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX + 8);
      Collection<String> codeUnits = safetyReport.getRequirementToCodeUnitsValues(softwareRequirement);

      if (Conditions.hasValues(codeUnits)) {
         for (String codeUnit : codeUnits) {
            writeCell(codeUnit, currentRowValues, ValidatingSafetyReportGenerator.CODE_UNIT_INDEX);
            if (safetyReport.saveToPreviousAndCheckIfDifferent(currentRowValues)) {
               writer.writeRow((Object[]) currentRowValues);
            }
         }
      } else {
         if (safetyReport.saveToPreviousAndCheckIfDifferent(currentRowValues)) {
            writer.writeRow((Object[]) currentRowValues);
         }
      }
   }
}