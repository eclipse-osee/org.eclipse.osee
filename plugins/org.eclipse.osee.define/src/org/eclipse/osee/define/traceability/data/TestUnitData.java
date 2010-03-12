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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public class TestUnitData extends BaseTraceDataCache {

   private final List<Artifact> testCases;
   private final List<Artifact> testProcedures;
   private final List<Artifact> testSupportItems;
   private final Set<Artifact> allTestUnits;

   private final HashMap<String, Artifact> testCaseMap;
   private final HashMap<String, Artifact> testProcedureMap;
   private final HashMap<String, Artifact> testSupportMap;

   public TestUnitData(Branch branch) {
      super("Test Unit Data", branch);
      this.testCaseMap = new HashMap<String, Artifact>();
      this.testProcedureMap = new HashMap<String, Artifact>();
      this.testSupportMap = new HashMap<String, Artifact>();

      this.testCases = new ArrayList<Artifact>();
      this.testProcedures = new ArrayList<Artifact>();
      this.testSupportItems = new ArrayList<Artifact>();

      this.allTestUnits = new TreeSet<Artifact>();
   }

   @Override
   public void reset() {
      super.reset();
      this.testCaseMap.clear();
      this.testProcedureMap.clear();
      this.testSupportMap.clear();

      this.testCases.clear();
      this.testProcedures.clear();
      this.testSupportItems.clear();

      this.allTestUnits.clear();
   }

   @Override
   protected void doBulkLoad(IProgressMonitor monitor) throws Exception {
      IProgressMonitor subMonitor = SubMonitor.convert(monitor);
      testCases.addAll(ArtifactQuery.getArtifactListFromType(Requirements.TEST_CASE, getBranch()));
      populateTraceMap(monitor, testCases, testCaseMap);
      subMonitor.worked(20);

      if (!monitor.isCanceled()) {
         monitor.subTask(String.format("Load Test Support from: [%s]", getBranch().getShortName()));

         testSupportItems.addAll(ArtifactQuery.getArtifactListFromType(Requirements.TEST_SUPPORT, getBranch()));
         populateTraceMap(monitor, testSupportItems, testSupportMap);
         subMonitor.worked(20);
      }

      if (!monitor.isCanceled()) {
         monitor.subTask(String.format("Load Test Procedures from: [%s]", getBranch().getShortName()));
         testProcedures.addAll(ArtifactQuery.getArtifactListFromType(Requirements.TEST_PROCEDURE, getBranch()));
         testProcedures.addAll(ArtifactQuery.getArtifactListFromType("Test Procedure WML", getBranch()));
         testProcedures.addAll(ArtifactQuery.getArtifactListFromType("Test Procedure XL", getBranch()));
         populateTraceMap(monitor, testProcedures, testProcedureMap);
         subMonitor.worked(20);
      }

      if (!monitor.isCanceled()) {
         allTestUnits.addAll(testCases);
         allTestUnits.addAll(testProcedures);
         allTestUnits.addAll(testSupportItems);
         subMonitor.worked(1);
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
    * @return the test procedures
    */
   public Collection<Artifact> getTestProcedures() {
      return testProcedures;
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
      if (testUnit == null) {
         testUnit = testProcedureMap.get(testUnitName);
      }
      return testUnit;
   }
}
