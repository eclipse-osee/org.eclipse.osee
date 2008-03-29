/*
 * Created on Mar 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

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

   public void initialize(IProgressMonitor monitor) throws Exception {
      reset();
      monitor.subTask(String.format("Loading Software Requirements from: [%s]", getBranch().getBranchShortestName()));

      directSwRequirements.addAll(artifactManager.getArtifactsFromSubtypeName("Software Requirement", getBranch()));
      populateTraceMap(monitor, directSwRequirements, directMap);
      monitor.worked(30);

      if (monitor.isCanceled() != true) {
         monitor.subTask(String.format("Load Indirect Software Requirements from: [%s]",
               getBranch().getBranchShortestName()));
         inDirectSwRequirements.addAll(artifactManager.getArtifactsFromSubtypeName("Indirect Software Requirement",
               getBranch()));
         populateTraceMap(monitor, inDirectSwRequirements, indirectMap);
         monitor.worked(7);

         if (monitor.isCanceled() != true) {
            allSwRequirements.addAll(directSwRequirements);
            allSwRequirements.addAll(inDirectSwRequirements);
            monitor.worked(1);
         }
      }
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

}
