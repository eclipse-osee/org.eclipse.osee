/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.define.ide.traceability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.data.RequirementData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ViewIdUtility;

/**
 * @author John R. Misinco
 */
public class BranchTraceabilityOperation extends TraceabilityProviderOperation {

   private final BranchId branch;
   private final RequirementData requirementData;
   private HashCollectionSet<Artifact, String> requirementToTestUnitsMap;
   private Map<String, Artifact> testUnits;
   private final Collection<? extends ArtifactTypeToken> types;
   private final boolean withInheritance;
   private final ArtifactId viewId;

   private BranchTraceabilityOperation(RequirementData requirementData, BranchId branch, Collection<? extends ArtifactTypeToken> types, boolean withInheritance, ArtifactId viewId) {
      super("Branch Traceability Provider", Activator.PLUGIN_ID);
      this.requirementData = requirementData;
      this.branch = branch;
      this.types = types;
      this.withInheritance = withInheritance;
      this.viewId = viewId;
   }

   public BranchTraceabilityOperation(BranchToken branch, ArtifactId viewId) {
      this(new RequirementData(branch, viewId), branch,
         Collections.singleton(CoreArtifactTypes.AbstractSoftwareRequirement), true, viewId);
   }

   public BranchTraceabilityOperation(BranchId branch, Collection<? extends ArtifactTypeToken> types, boolean withInheritance, ArtifactId viewId) {
      this(new RequirementData(branch, types, withInheritance, viewId), branch, types, withInheritance, viewId);
   }

   @Override
   public RequirementData getRequirementData() {
      return requirementData;
   }

   @Override
   public HashCollectionSet<Artifact, String> getRequirementToCodeUnitsMap() {
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
      requirementToTestUnitsMap = new HashCollectionSet<>(HashSet::new);

      Set<ArtifactId> excludedArtifactIdMap = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);
      List<Artifact> unitsOnBranch = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.TestCase, branch);
      if (unitsOnBranch != null) {
         ViewIdUtility.removeExcludedArtifacts(unitsOnBranch.iterator(), excludedArtifactIdMap);

         testUnits = new HashMap<>();
         for (Artifact unit : unitsOnBranch) {
            testUnits.put(convertToJavaFileName(unit.getName()), unit);
         }
      }

      List<Artifact> reqs = new ArrayList<>();
      for (ArtifactTypeToken type : types) {
         if (withInheritance) {
            reqs.addAll(
               ArtifactQuery.getArtifactListFromTypeWithInheritence(type, branch, DeletionFlag.EXCLUDE_DELETED));
         } else {
            reqs.addAll(ArtifactQuery.getArtifactListFromType(type, branch));
         }
      }
      ViewIdUtility.removeExcludedArtifacts(reqs.iterator(), excludedArtifactIdMap);

      for (Artifact req : reqs) {
         List<Artifact> verifiers = req.getRelatedArtifacts(CoreRelationTypes.Verification_Verifier);
         ViewIdUtility.removeExcludedArtifacts(verifiers.iterator(), excludedArtifactIdMap);
         Set<String> verifierNames = new HashSet<>();
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
   public Collection<Artifact> getTestUnitArtifacts(Artifact requirement) {
      Collection<Artifact> toReturn = Collections.emptyList();
      if (!requirement.isHistorical()) {
         toReturn = requirement.getRelatedArtifacts(CoreRelationTypes.Verification_Verifier);
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
