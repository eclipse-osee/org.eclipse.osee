/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author John Misinco
 */
public class BranchTraceabilityOperation extends TraceabilityProviderOperation {

   private final IOseeBranch branch;
   private RequirementData requirementData;
   private HashCollection<Artifact, String> requirementToCodeUnitsMap;
   private HashSet<String> codeUnits;

   public BranchTraceabilityOperation(IOseeBranch branch) {
      super("Branch Traceability Provider", DefinePlugin.PLUGIN_ID);
      this.branch = branch;
   }

   @Override
   public RequirementData getRequirementData() {
      return requirementData;
   }

   @Override
   public HashCollection<Artifact, String> getRequirementToCodeUnitsMap() {
      return requirementToCodeUnitsMap;
   }

   @Override
   public HashSet<String> getCodeUnits() {
      return codeUnits;
   }

   private String convertToJavaFileName(String name) {
      int endOfPackageName = name.lastIndexOf(".");
      if (endOfPackageName != -1) {
         name = name.substring(endOfPackageName + 1) + ".java";
      }
      return name;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      requirementData = new RequirementData(branch);
      requirementData.initialize(monitor);

      requirementToCodeUnitsMap = new HashCollection<Artifact, String>();

      List<Artifact> softwareReqs =
         ArtifactQuery.getArtifactListFromTypeWithInheritence(CoreArtifactTypes.AbstractSoftwareRequirement, branch,
            DeletionFlag.EXCLUDE_DELETED);
      for (Artifact req : softwareReqs) {
         List<Artifact> verifiers = req.getRelatedArtifacts(CoreRelationTypes.Verification__Verifier);
         Collection<String> verifierNames = new HashSet<String>();
         for (Artifact verifier : verifiers) {
            verifierNames.add(convertToJavaFileName(verifier.getName()));
         }
         requirementToCodeUnitsMap.put(req, verifierNames);
      }

      codeUnits = new HashSet<String>();
      List<Artifact> unitsOnBranch = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.TestCase, branch);
      for (Artifact unit : unitsOnBranch) {
         codeUnits.add(convertToJavaFileName(unit.getName()));
      }
   }

}
