/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author David W. Miller
 */
public final class SafetyInformationAccumulator {
   private final ISheetWriter writer;
   private final SafetyReportGenerator safetyReport;
   private String functionalCategory;
   private List<ArtifactReadable> subsystemFunctions;
   private final HashMap<ArtifactReadable, List<ArtifactReadable>> subsystemRequirements = Maps.newHashMap();
   private final HashMap<ArtifactReadable, List<ArtifactReadable>> softwareRequirements = Maps.newHashMap();
   private static final Predicate<ArtifactReadable> notSoftwareRequirement = new Predicate<ArtifactReadable>() {
      @Override
      public boolean apply(ArtifactReadable input) {
         boolean toReturn = true;
         try {
            toReturn = !input.isOfType(CoreArtifactTypes.SoftwareRequirement);
         } catch (OseeCoreException ex) {
            // if there is an exception on the type, then we will treat it like it is not
            // an abstract software requirement (i.e. leave toReturn true and skip)
         }
         return toReturn;
      }
   };

   public SafetyInformationAccumulator(SafetyReportGenerator providedSafetyReport, ISheetWriter providedWriter) {
      safetyReport = providedSafetyReport;
      writer = providedWriter;
   }

   public String calculateBoeingEquivalentSWQualLevel(String softwareReqDAL, int partitionCount) {
      String toReturn = "";

      if (functionalCategory.equals("IFR/IMC")) {
         if (checkLevel(softwareReqDAL)) {
            if (partitionCount > 1) {
               toReturn = "C*";
            } else {
               toReturn = "C";
            }
         } else {
            toReturn = "BP";
         }
      } else if (functionalCategory.equals("Tactical")) {
         toReturn = "BP";
      }
      return toReturn;
   }

   public void reset(ArtifactReadable systemFunction) {
      functionalCategory = systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FunctionalCategory, "");
      subsystemRequirements.clear();
      softwareRequirements.clear();
   }

   public void buildSubsystemsRequirementsMap(ArtifactReadable systemFunction) {

      subsystemFunctions = Lists.newArrayList(systemFunction.getRelated(CoreRelationTypes.Dependency__Dependency));

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
         Lists.newArrayList(subsystemFunction.getRelated(CoreRelationTypes.Design__Requirement));

      Iterator<ArtifactReadable> ssrIter = localSubsystemRequirements.iterator();
      while (ssrIter.hasNext()) {
         ArtifactReadable subsystemRequirement = ssrIter.next();
         List<ArtifactReadable> localSoftwareRequirements =
            Lists.newArrayList(subsystemRequirement.getRelated(CoreRelationTypes.Requirement_Trace__Lower_Level));

         // test software requirements for suitability - is it a subclass of software requirement?
         Iterables.removeIf(localSoftwareRequirements, notSoftwareRequirement);

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

   public void output() throws IOException {
      for (ArtifactReadable subsystemFunction : subsystemFunctions) {
         processSubsystemFunction(subsystemFunction);
         writer.endRow();
      }
   }

   private boolean checkLevel(String input) {
      boolean toReturn = false;
      if (input.equals("A") || input.equals("B") || input.equals("C")) {
         toReturn = true;
      }
      return toReturn;
   }

   private String convertSafetyCriticalityToDAL(String inputSafetyCriticality) {
      if (inputSafetyCriticality.length() > 4) {
         return "Error";
      }
      return SafetyCriticalityLookup.getDALLevelFromSeverityCategory(inputSafetyCriticality);
   }

   private void processSubsystemFunction(ArtifactReadable subsystemFunction) throws IOException {
      writer.writeCell(subsystemFunction.getName(), SafetyReportGenerator.SUBSYSTEM_FUNCTION_INDEX);
      String sevCat =
         subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.SeverityCategory, "Error: not available");
      writer.writeCell(sevCat);
      writer.writeCell(subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.FunctionalDAL, ""));
      writer.writeCell(subsystemFunction.getSoleAttributeAsString(CoreAttributeTypes.FunctionalDALRationale, ""));

      for (ArtifactReadable subsystemRequirement : subsystemRequirements.get(subsystemFunction)) {
         processSubsystemRequirement(subsystemRequirement, convertSafetyCriticalityToDAL(sevCat));
      }
      writer.endRow();
   }

   private void processSubsystemRequirement(ArtifactReadable subsystemRequirement, String criticality) throws IOException {
      writer.writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.Subsystem, ""),
         SafetyReportGenerator.SUBSYSTEM_INDEX);
      writer.writeCell(subsystemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
      writer.writeCell(subsystemRequirement.getName());
      writer.writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.ItemDAL, ""));
      writer.writeCell(subsystemRequirement.getSoleAttributeAsString(CoreAttributeTypes.ItemDALRationale, ""));

      for (ArtifactReadable softwareRequirement : softwareRequirements.get(subsystemRequirement)) {
         processSoftwareRequirement(softwareRequirement);
      }
      writer.endRow();
   }

   private String writeCriticality(ArtifactReadable art, AttributeTypeId thisType) throws IOException {
      String current = art.getSoleAttributeAsString(thisType, "Error");

      if (AttributeId.UNSPECIFIED.equals(current)) {
         writer.writeCell(AttributeId.UNSPECIFIED);
      }
      writer.writeCell(current);
      return current;
   }

   private void processSoftwareRequirement(ArtifactReadable softwareRequirement) throws IOException {
      writer.writeCell(softwareRequirement.getName(), SafetyReportGenerator.SOFTWARE_REQUIREMENT_INDEX);
      String softwareRequirementDAL = writeCriticality(softwareRequirement, CoreAttributeTypes.ItemDAL);
      writer.writeCell(softwareRequirement.getSoleAttributeAsString(CoreAttributeTypes.ItemDALRationale, ""));

      writer.writeCell(calculateBoeingEquivalentSWQualLevel(softwareRequirementDAL,
         softwareRequirement.getAttributeCount(CoreAttributeTypes.Partition)));
      writer.writeCell(functionalCategory);

      writer.writeCell(
         Collections.toString(",", getAttributesToStringList(softwareRequirement, CoreAttributeTypes.Partition)));

      writer.writeCell(safetyReport.getComponentUtil().getQualifiedComponentNames(softwareRequirement));
      Collection<String> codeUnits = safetyReport.getRequirementToCodeUnitsValues(softwareRequirement);

      if (Conditions.hasValues(codeUnits)) {
         for (String codeUnit : codeUnits) {
            writer.writeCell(codeUnit, SafetyReportGenerator.CODE_UNIT_INDEX);
            writer.endRow();
         }
      } else {
         writer.endRow();
      }
   }

   public List<String> getAttributesToStringList(ArtifactReadable artifact, AttributeTypeId attributeType) {

      List<String> items = new ArrayList<>();
      for (AttributeReadable<?> attribute : artifact.getAttributes(attributeType)) {
         items.add(attribute.getDisplayableString());
      }
      return items;
   }

}
