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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TraceabilityExtractor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class RequirementData extends BaseTraceDataCache {
   private static final TraceabilityExtractor traceExtractor = TraceabilityExtractor.getInstance();

   private final List<Artifact> directRequirements = new ArrayList<>();
   private final HashMap<String, Artifact> allRequirementsMap = new HashMap<>();
   private final TraceabilityExtractor extractor = TraceabilityExtractor.getInstance();
   private final Collection<? extends IArtifactType> types;
   private final boolean withInheritance;

   public RequirementData(IOseeBranch branch, Collection<? extends IArtifactType> types, boolean withInheritance) {
      super("Software Requirements Data", branch);
      this.types = types;
      this.withInheritance = withInheritance;
   }

   public RequirementData(IOseeBranch branch) {
      super("Software Requirements Data", branch);
      types = Collections.singleton(CoreArtifactTypes.AbstractSoftwareRequirement);
      withInheritance = true;
   }

   @Override
   public void reset() {
      super.reset();
      this.directRequirements.clear();
      allRequirementsMap.clear();
   }

   @Override
   protected void doBulkLoad(IProgressMonitor monitor) throws Exception {
      List<Artifact> allSwRequirements = new ArrayList<>();
      for (IArtifactType type : types) {
         if (withInheritance) {
            allSwRequirements.addAll(
               ArtifactQuery.getArtifactListFromTypeWithInheritence(type, getBranch(), EXCLUDE_DELETED));
         } else {
            allSwRequirements.addAll(ArtifactQuery.getArtifactListFromType(type, getBranch(), EXCLUDE_DELETED));
         }
      }
      populateTraceMap(monitor, allSwRequirements, allRequirementsMap);

      for (Artifact requirement : getAllRequirements()) {
         if (!requirement.isOfType(CoreArtifactTypes.IndirectSoftwareRequirement)) {
            directRequirements.add(requirement);
         }
      }
      monitor.worked(38);
   }

   @Override
   protected String asTraceMapKey(Artifact artifact) {
      return traceExtractor.getCanonicalRequirementName(artifact.getName());
   }

   public Collection<Artifact> getDirectRequirements() {
      return directRequirements;
   }

   public Collection<Artifact> getAllRequirements() {
      return allRequirementsMap.values();
   }

   /**
    * Get Requirement Artifact based on traceMark mark
    * 
    * @return requirement artifact
    */
   public Artifact getRequirementFromTraceMark(String traceMark) {
      return allRequirementsMap.get(extractor.getCanonicalRequirementName(traceMark));
   }

   /**
    * Get Requirement Artifact based on traceMark mark if it fails, check if trace mark is a structured requirement and
    * try again
    * 
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