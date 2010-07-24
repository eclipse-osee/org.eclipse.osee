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

package org.eclipse.osee.define.traceability.data;

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TraceabilityExtractor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class RequirementData extends BaseTraceDataCache {
   private static final TraceabilityExtractor traceExtractor = TraceabilityExtractor.getInstance();

   private final List<Artifact> directSwRequirements = new ArrayList<Artifact>();
   private final HashMap<String, Artifact> allSwRequirementsMap = new HashMap<String, Artifact>();
   private final TraceabilityExtractor extractor = TraceabilityExtractor.getInstance();

   public RequirementData(Branch branch) {
      super("Software Requirements Data", branch);
   }

   @Override
   public void reset() {
      super.reset();
      this.directSwRequirements.clear();
      allSwRequirementsMap.clear();
   }

   @Override
   protected void doBulkLoad(IProgressMonitor monitor) throws Exception {
      List<Artifact> allSwRequirements =
            ArtifactQuery.getArtifactListFromTypeWithInheritence(CoreArtifactTypes.AbstractSoftwareRequirement,
                  getBranch(), EXCLUDE_DELETED);
      populateTraceMap(monitor, allSwRequirements, allSwRequirementsMap);

      for (Artifact requirement : getAllSwRequirements()) {
         if (!requirement.isOfType(CoreArtifactTypes.IndirectSoftwareRequirement)) {
            directSwRequirements.add(requirement);
         }
      }
      monitor.worked(38);
   }

   @Override
   protected String asTraceMapKey(Artifact artifact) {
      return traceExtractor.getCanonicalRequirementName(artifact.getName());
   }

   public Collection<Artifact> getDirectSwRequirements() {
      return directSwRequirements;
   }

   public Collection<Artifact> getAllSwRequirements() {
      return allSwRequirementsMap.values();
   }

   /**
    * Get Requirement Artifact based on traceMark mark
    * 
    * @param traceMark
    * @return requirement artifact
    */
   public Artifact getRequirementFromTraceMark(String traceMark) {
      return allSwRequirementsMap.get(extractor.getCanonicalRequirementName(traceMark));
   }

   /**
    * Get Requirement Artifact based on traceMark mark if it fails, check if trace mark is a structured requirement and
    * try again
    * 
    * @param traceMark
    * @return requirement artifact
    */
   public Artifact getRequirementFromTraceMarkIncludeStructuredRequirements(String traceMark) {
      Artifact toReturn = getRequirementFromTraceMark(traceMark);
      if (toReturn == null) {
         Pair<String, String> structured = extractor.getStructuredRequirement(traceMark);
         if (structured != null) {
            toReturn = getRequirementFromTraceMark(structured.getFirst());
         }
      }
      return toReturn;
   }
}