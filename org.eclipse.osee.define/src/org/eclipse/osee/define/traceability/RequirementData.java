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

package org.eclipse.osee.define.traceability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public class RequirementData {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final TraceabilityExtractor traceExtractor = TraceabilityExtractor.getInstance();

   private Branch requirementsBranch;
   private List<Artifact> directSwRequirements;
   private List<Artifact> inDirectSwRequirements;
   private Set<Artifact> allSwRequirements;

   private final HashMap<String, Artifact> directMap;
   private final HashMap<String, Artifact> indirectMap;

   public RequirementData(Branch requirementsBranch) {
      this.requirementsBranch = requirementsBranch;

      this.directMap = new HashMap<String, Artifact>();
      this.indirectMap = new HashMap<String, Artifact>();
      this.directSwRequirements = new ArrayList<Artifact>();
      this.inDirectSwRequirements = new ArrayList<Artifact>();
      this.allSwRequirements = new TreeSet<Artifact>();
   }

   private void reset() {
      this.directMap.clear();
      this.indirectMap.clear();
      this.directSwRequirements.clear();
      this.inDirectSwRequirements.clear();
      this.allSwRequirements.clear();
   }

   public IStatus initialize(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         reset();
         monitor.subTask(String.format("Loading Software Requirements from: [%s]", getBranch().getBranchShortestName()));

         directSwRequirements.addAll(ArtifactQuery.getArtifactsFromType(Requirements.SOFTWARE_REQUIREMENT, getBranch()));
         populateTraceMap(monitor, directSwRequirements, directMap);
         monitor.worked(30);

         if (monitor.isCanceled() != true) {
            monitor.subTask(String.format("Load Indirect Software Requirements from: [%s]",
                  getBranch().getBranchShortestName()));
            inDirectSwRequirements.addAll(ArtifactQuery.getArtifactsFromType(
                  Requirements.INDIRECT_SOFTWARE_REQUIREMENT, getBranch()));
            populateTraceMap(monitor, inDirectSwRequirements, indirectMap);
            monitor.worked(7);

            if (monitor.isCanceled() != true) {
               allSwRequirements.addAll(directSwRequirements);
               allSwRequirements.addAll(inDirectSwRequirements);
               monitor.worked(1);
            }
         }
         if (monitor.isCanceled() != true) {
            toReturn = Status.OK_STATUS;
         }
      } catch (Exception ex) {
         toReturn = new Status(IStatus.ERROR, DefinePlugin.PLUGIN_ID, "Loading requirement data.", ex);
      }
      return toReturn;
   }

   private void populateTraceMap(IProgressMonitor monitor, List<Artifact> artList, HashMap<String, Artifact> toPopulate) {
      for (Artifact artifact : artList) {
         toPopulate.put(traceExtractor.getCanonicalRequirementName(artifact.getDescriptiveName()), artifact);
      }
   }

   /**
    * Get requirements source branch
    * 
    * @return source branch
    */
   public Branch getBranch() {
      return requirementsBranch;
   }

   /**
    * @return the directSwRequirements
    */
   public Collection<Artifact> getDirectSwRequirements() {
      return directSwRequirements;
   }

   /**
    * @return the inDirectSwRequirements
    */
   public Collection<Artifact> getInDirectSwRequirements() {
      return inDirectSwRequirements;
   }

   /**
    * @return the allSwRequirements
    */
   public Set<Artifact> getAllSwRequirements() {
      return allSwRequirements;
   }

   /**
    * Get Requirement Artifact based on traceMark mark
    * 
    * @param traceMark
    * @return requirement artifact
    */
   public Artifact getRequirementFromTraceMark(String traceMark) {
      String canonicalTraceMark = TraceabilityExtractor.getInstance().getCanonicalRequirementName(traceMark);
      Artifact reqArtifact = directMap.get(canonicalTraceMark);
      if (reqArtifact == null) {
         reqArtifact = indirectMap.get(canonicalTraceMark);
      }
      return reqArtifact;
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
         Pair<String, String> structured = TraceabilityExtractor.getInstance().getStructuredRequirement(traceMark);
         if (structured != null) {
            toReturn = getRequirementFromTraceMark(structured.getKey());
         }
      }
      return toReturn;
   }

}
