/*
 * Created on Apr 1, 2009
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public class TestUnitData {

   private final Branch testUnitBranch;
   private List<Artifact> testCases;
   private List<Artifact> testSupportItems;
   private Set<Artifact> allTestUnits;

   private final HashMap<String, Artifact> testCaseMap;
   private final HashMap<String, Artifact> testSupportMap;

   public TestUnitData(Branch testUnitBranch) {
      this.testUnitBranch = testUnitBranch;

      this.testCaseMap = new HashMap<String, Artifact>();
      this.testSupportMap = new HashMap<String, Artifact>();
      this.testCases = new ArrayList<Artifact>();
      this.testSupportItems = new ArrayList<Artifact>();
      this.allTestUnits = new TreeSet<Artifact>();
   }

   private void reset() {
      this.testCaseMap.clear();
      this.testSupportMap.clear();
      this.testCases.clear();
      this.testSupportItems.clear();
      this.allTestUnits.clear();
   }

   public Branch getBranch() {
      return testUnitBranch;
   }

   public IStatus initialize(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         reset();
         monitor.subTask(String.format("Loading Test Units from: [%s]", getBranch().getBranchShortName()));

         testCases.addAll(ArtifactQuery.getArtifactsFromType(Requirements.TEST_CASE, getBranch()));
         populateTraceMap(monitor, testCases, testCaseMap);
         monitor.worked(30);

         if (monitor.isCanceled() != true) {
            monitor.subTask(String.format("Load Test Support from: [%s]", getBranch().getBranchShortName()));

            testSupportItems.addAll(ArtifactQuery.getArtifactsFromType(Requirements.TEST_SUPPORT, getBranch()));
            populateTraceMap(monitor, testSupportItems, testSupportMap);
            monitor.worked(7);

            if (monitor.isCanceled() != true) {
               allTestUnits.addAll(testCases);
               allTestUnits.addAll(testSupportItems);
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
         toPopulate.put(artifact.getDescriptiveName(), artifact);
      }
   }

   /**
    * @return the test cases
    */
   public Collection<Artifact> getTestCases() {
      return testCases;
   }

   /**
    * @return the test support items
    */
   public Collection<Artifact> getTestSupportItems() {
      return testSupportItems;
   }

   /**
    * @return the allTestUnits
    */
   public Set<Artifact> getAllTestUnits() {
      return allTestUnits;
   }

   /**
    * @return the testUnitArtifact
    */
   public Artifact getTestUnitByName(String testUnitName) {
      Artifact testUnit = testCaseMap.get(testUnitName);
      if (testUnit == null) {
         testUnit = testSupportMap.get(testUnitName);
      }
      return testUnit;
   }
}
