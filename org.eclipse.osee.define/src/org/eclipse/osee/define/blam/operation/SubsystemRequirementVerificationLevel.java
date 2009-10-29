/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.CoreArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

public class SubsystemRequirementVerificationLevel extends AbstractBlam {

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getName() {
      return "Set Verification Level for Subsystem Requirements";
   }

   private Branch branch;
   private Collection<Artifact> subsystemRequirements;
   private StringBuilder report;
   private SkynetTransaction transaction;
   private Collection<Artifact> bulkRequirements;

   private void loadFields(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      branch = variableMap.getBranch("Branch");
      subsystemRequirements = ArtifactQuery.getArtifactListFromType(CoreArtifacts.SubsystemRequirement, branch);
      bulkRequirements =
            RelationManager.getRelatedArtifacts(subsystemRequirements, 1,
                  CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL);
      report = new StringBuilder(bulkRequirements.size());
      transaction = new SkynetTransaction(branch, "Set Verification Level for Subsystem Requirements");
      monitor.beginTask("Set Subsystem Requirement Verification Level", subsystemRequirements.size());
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      loadFields(variableMap, monitor);
      beginReport();

      for (Artifact req : subsystemRequirements) {
         processSubsystemRequirement(req);
         incrementMonitor(monitor);
      }

      report();
      //      transaction.execute();
   }

   private void incrementMonitor(IProgressMonitor monitor) {
      monitor.worked(1);
   }

   private void report() {
      String finalReport = report.toString();
      System.out.println(finalReport);
   }

   private void beginReport() {
      report.append("\"Requirement\",\"Subsystem\",\"Imported Paragraph Number\",\"Current Verification Level\",\"Changed\"\n");
   }

   private void processSubsystemRequirement(Artifact reqArt) throws OseeCoreException {
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

      public SubsystemRequirement(Artifact req) throws OseeCoreException {
         this.req = req;
      }

      public void process() throws OseeCoreException {
         getData();
         if (meetsCriteria()) {
            if (isUnspecified()) {
               adjustVerificationLevel();
            }
            report();
         }
      }

      private void getData() throws OseeCoreException {
         this.hardwareComponents = getHardwareComponentCount();
         this.softwareRequirements = getSoftwareRequirementCount();
         paragraphNumber = req.getSoleAttributeValue(CoreAttributes.PARAGRAPH_NUMBER, "NONE");
         subsystem = req.getSoleAttributeValue(CoreAttributes.SUBSYSTEM, "NONE");
         verificationLevel = req.getSoleAttributeValue(CoreAttributes.VERIFICATION_LEVEL.getName(), "UNDEFINED");
      }

      private void adjustVerificationLevel() throws OseeCoreException {
         req.setSoleAttributeValue(CoreAttributes.VERIFICATION_LEVEL, "Component");
         req.persist(SubsystemRequirementVerificationLevel.this.transaction);
      }

      private boolean meetsCriteria() {
         return hardwareComponents == 1 && softwareRequirements == 0;
      }

      public void report() {
         SubsystemRequirementVerificationLevel.this.report.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
               req.getName(), subsystem, paragraphNumber, verificationLevel, isUnspecified()));
      }

      private int getHardwareComponentCount() throws OseeCoreException {
         return RelationManager.getRelatedArtifactsCount(req, CoreRelationEnumeration.ALLOCATION__COMPONENT);
      }

      private int getSoftwareRequirementCount() throws OseeCoreException {
         Collection<Artifact> traceCollection =
               RelationManager.getRelatedArtifacts(req, CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL);
         int ret = 0;
         for (Artifact trace : traceCollection) {
            if (trace.isOfType(CoreArtifacts.AbstractSoftwareRequirement)) {
               ret++;
            }
         }
         return ret;
      }

      private boolean isUnspecified() {
         return verificationLevel.equals("Unspecified");
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" defaultValue=\"NGFCZ - Update verification level for PIDS requirements traced to TPS/TIS - Update verification level for PIDS requ...\"/></xWidgets>";
   }
}
