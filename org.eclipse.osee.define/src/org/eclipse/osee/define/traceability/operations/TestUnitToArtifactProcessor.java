/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.RequirementData;
import org.eclipse.osee.define.traceability.TestUnit;
import org.eclipse.osee.define.traceability.TestUnitData;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public class TestUnitToArtifactProcessor implements ITestUnitProcessor {
   private RequirementData requirementData;
   private TestUnitData testUnitData;

   private final Branch importIntoBranch;
   private final Branch requirementsBranch;

   public TestUnitToArtifactProcessor(Branch requirementsBranch, Branch importIntoBranch) {
      this.importIntoBranch = importIntoBranch;
      this.requirementsBranch = requirementsBranch;
   }

   @Override
   public void clear() {
      requirementData = null;
      testUnitData = null;
   }

   @Override
   public void initialize(IProgressMonitor monitor) {
      requirementData = new RequirementData(requirementsBranch);
      if (!monitor.isCanceled()) {
         requirementData.initialize(monitor);
         testUnitData = new TestUnitData(importIntoBranch);
         if (!monitor.isCanceled()) {
            testUnitData.initialize(monitor);
         }
      }
   }

   @Override
   public void process(IProgressMonitor monitor, TestUnit testUnit) {
      for (String traceTypes : testUnit.getTraceMarkTypes()) {
         for (String traceMark : testUnit.getTraceMarksByType(traceTypes)) {
            if (monitor.isCanceled()) break;
            // DO WORK HERE
            System.out.println(String.format("[%s] %s -- %s --> %s", testUnit.getTestUnitType(), testUnit.getName(),
                  traceTypes, traceMark));
         }
      }
   }

   @Override
   public void onComplete(IProgressMonitor monitor) {
      if (!monitor.isCanceled()) {
         //            SkynetTransaction transaction = new SkynetTransaction(importIntoBranch);
         //
         //            // Bulk Load all test script artifacts and create any missing ones
         //            final Map<String, Artifact> testScriptMap =
         //                  getAllTestScriptsAndCreateMissingOnes(monitor, branch, collector.getScriptNames());
         //
         //            if (monitor.isCanceled() != true) {
         //               // Create ScriptName to Requirement Map
         //               final HashCollection<String, Artifact> scriptNamesToRequirements =
         //                     convertToArtifactRequirement(monitor, requirementData, collector.getUnitToTraceMarks());
         //
         //               if (monitor.isCanceled() != true) {
         //                  // Persist and Relate all test scripts to requirements
         //                  int count = 1;
         //                  final int size = testScriptMap.keySet().size();
         //                  for (String key : testScriptMap.keySet()) {
         //                     monitor.subTask(String.format("Committing: [%s] [%s of %s]", key, count++, size));
         //                     Artifact testScript = testScriptMap.get(key);
         //                     Collection<Artifact> requirements = scriptNamesToRequirements.getValues(key);
         //                     if (requirements != null) {
         //                        for (Artifact requirement : requirements) {
         //                           testScript.addRelation(CoreRelationEnumeration.Verification__Requirement, requirement);
         //                        }
         //                     }
         //                     testScript.persistAttributesAndRelations(transaction);
         //                  }
         //                  transaction.execute();
         //               }
         //            }
      }
   }

   //   private final class TestUnitsToArtifacts {
   //      private Map<String, Artifact> getAllTestScriptsAndCreateMissingOnes(IProgressMonitor monitor, Branch branch, Set<String> scriptNames) throws OseeCoreException {
   //         monitor.subTask(String.format("Load and Create Test Sript Artifacts into branch [%s]", branch.getBranchName()));
   //         Map<String, Artifact> testScripts =
   //               TestRunOperator.getTestScriptFetcher().getAllArtifactsIndexedByName(branch);
   //
   //         for (String scriptName : scriptNames) {
   //            if (testScripts.containsKey(scriptName) != true) {
   //               Artifact artifact = TestRunOperator.getTestScriptFetcher().getNewArtifact(branch);
   //               artifact.setDescriptiveName(scriptName);
   //               testScripts.put(artifact.getDescriptiveName(), artifact);
   //            }
   //            if (monitor.isCanceled() == true) {
   //               break;
   //            }
   //         }
   //         monitor.worked(1);
   //         return testScripts;
   //      }
   //
   //      private HashCollection<String, Artifact> convertToArtifactRequirement(IProgressMonitor monitor, RequirementData requirementData, HashCollection<String, String> toConvert) {
   //         monitor.subTask("Mapping ScriptNames/Trace Marks to Requirements");
   //         HashCollection<String, Artifact> toReturn = new HashCollection<String, Artifact>();
   //         for (String unit : toConvert.keySet()) {
   //            Collection<String> traceMarks = toConvert.getValues(unit);
   //            for (String traceMark : traceMarks) {
   //               Artifact artifact = getRequirementArtifact(traceMark, requirementData);
   //               if (artifact != null) {
   //                  toReturn.put(unit, artifact);
   //               }
   //            }
   //            if (monitor.isCanceled() == true) {
   //               break;
   //            }
   //         }
   //         return toReturn;
   //      }
   //
   //      private Artifact getRequirementArtifact(String traceMark, RequirementData requirementData) {
   //         Artifact toReturn = requirementData.getRequirementFromTraceMark(traceMark);
   //         if (toReturn == null) {
   //            Pair<String, String> structuredRequirement = extractor.getStructuredRequirement(traceMark);
   //            if (structuredRequirement != null) {
   //               toReturn = requirementData.getRequirementFromTraceMark(structuredRequirement.getKey());
   //            }
   //         }
   //         return toReturn;
   //      }
   //   }
}
