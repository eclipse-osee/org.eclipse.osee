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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author John R. Misinco
 */
public class BranchTraceabilityOperation extends TraceabilityProviderOperation {

   private final IOseeBranch branch;
   private final RequirementData requirementData;
   private HashCollection<Artifact, String> requirementToTestUnitsMap;
   private Map<String, Artifact> testUnits;
   private final Collection<? extends IArtifactType> types;
   private final boolean withInheritance;

   private BranchTraceabilityOperation(RequirementData requirementData, Branch branch, Collection<? extends IArtifactType> types, boolean withInheritance) {
      super("Branch Traceability Provider", Activator.PLUGIN_ID);
      this.requirementData = requirementData;
      this.branch = branch;
      this.types = types;
      this.withInheritance = withInheritance;
   }

   public BranchTraceabilityOperation(Branch branch) {
      this(new RequirementData(branch), branch, Collections.singleton(CoreArtifactTypes.AbstractSoftwareRequirement),
         true);
   }

   public BranchTraceabilityOperation(Branch branch, Collection<? extends IArtifactType> types, boolean withInheritance) {
      this(new RequirementData(branch, types, withInheritance), branch, types, withInheritance);
   }

   @Override
   public RequirementData getRequirementData() {
      return requirementData;
   }

   @Override
   public HashCollection<Artifact, String> getRequirementToCodeUnitsMap() {
      return requirementToTestUnitsMap;
   }

   @Override
   public Set<String> getCodeUnits() {
      return testUnits.keySet();
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
      requirementData.initialize(monitor);
      requirementToTestUnitsMap = new HashCollection<Artifact, String>();

      List<Artifact> unitsOnBranch = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.TestCase, branch);
      testUnits = new HashMap<String, Artifact>();
      for (Artifact unit : unitsOnBranch) {
         testUnits.put(convertToJavaFileName(unit.getName()), unit);
      }

      List<Artifact> reqs = new ArrayList<Artifact>();
      for (IArtifactType type : types) {
         if (withInheritance) {
            reqs.addAll(ArtifactQuery.getArtifactListFromTypeWithInheritence(type, branch, DeletionFlag.EXCLUDE_DELETED));
         } else {
            reqs.addAll(ArtifactQuery.getArtifactListFromType(type, branch));
         }
      }

      for (Artifact req : reqs) {
         List<Artifact> verifiers = req.getRelatedArtifacts(CoreRelationTypes.Verification__Verifier);
         Collection<String> verifierNames = new HashSet<String>();
         String inspection = getInspectionQual(req);
         if (Strings.isValid(inspection)) {
            verifierNames.add(inspection);
         }

         for (Artifact verifier : verifiers) {
            verifierNames.add(convertToJavaFileName(verifier.getName()));
         }
         requirementToTestUnitsMap.put(req, verifierNames);
      }

   }

   @Override
   public Collection<Artifact> getTestUnitArtifacts(Artifact requirement) throws OseeCoreException {
      Collection<Artifact> toReturn = Collections.emptyList();
      if (!requirement.isHistorical()) {
         toReturn = requirement.getRelatedArtifacts(CoreRelationTypes.Verification__Verifier);
      }
      return toReturn;
   }

   @Override
   public Artifact getTestUnitByName(String name) {
      Artifact toReturn = testUnits.get(name);
      if (toReturn == null) {
         toReturn = testUnits.get(name + ".java");
      }
      return toReturn;
   }

}
