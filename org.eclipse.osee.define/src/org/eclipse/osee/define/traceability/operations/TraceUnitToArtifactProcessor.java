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
package org.eclipse.osee.define.traceability.operations;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TraceabilityExtractor;
import org.eclipse.osee.define.traceability.data.CodeUnitData;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.define.traceability.data.TestUnitData;
import org.eclipse.osee.define.traceability.data.TraceMark;
import org.eclipse.osee.define.traceability.data.TraceUnit;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnitToArtifactProcessor implements ITraceUnitProcessor {
   private RequirementData requirementData;

   private CodeUnitData codeUnitData;
   private TestUnitData testUnitData;

   private final Branch importIntoBranch;
   private SkynetTransaction transaction;

   private final XResultData resultData;

   public TraceUnitToArtifactProcessor(XResultData resultData, Branch importIntoBranch) {
      this.resultData = resultData;
      this.importIntoBranch = importIntoBranch;
   }

   @Override
   public void clear() {
      transaction = null;
      requirementData.reset();
      requirementData = null;
      testUnitData.reset();
      testUnitData = null;
      codeUnitData.reset();
      codeUnitData = null;
   }

   @Override
   public void initialize(IProgressMonitor monitor) {
      transaction = null;
      resultData.addRaw(AHTML.beginMultiColumnTable(95, 1));
      resultData.addRaw(AHTML.bold("Unable to find requirements for test unit trace marks"));
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Test Unit Type", "Test Unit Name",
            "Trace Type", "Trace Mark"}));

      requirementData = new RequirementData(importIntoBranch);
      if (!monitor.isCanceled()) {
         requirementData.initialize(monitor);
         testUnitData = new TestUnitData(importIntoBranch);
         if (!monitor.isCanceled()) {
            testUnitData.initialize(monitor);
         }
         codeUnitData = new CodeUnitData(importIntoBranch);
         if (!monitor.isCanceled()) {
            codeUnitData.initialize(monitor);
         }
      }
   }

   private Artifact getArtifactFromCache(String artifactType, String name) {
      if (Requirements.ALL_TEST_UNIT_TYPES.contains(artifactType)) {
         return testUnitData.getTestUnitByName(name);
      } else if (Requirements.CODE_UNIT.equals(artifactType)) {
         return codeUnitData.getCodeUnitByName(name);
      }
      return null;
   }

   @Override
   public void process(IProgressMonitor monitor, TraceUnit traceUnit) throws OseeCoreException {
      if (transaction == null) {
         transaction = new SkynetTransaction(importIntoBranch);
      }
      boolean hasChange = false;
      boolean artifactWasCreated = false;

      String traceUnitType = traceUnit.getTraceUnitType();

      Artifact traceUnitArtifact = getArtifactFromCache(traceUnitType, traceUnit.getName());
      if (traceUnitArtifact == null) {
         traceUnitArtifact =
               ArtifactTypeManager.addArtifact(traceUnit.getTraceUnitType(), transaction.getBranch(),
                     traceUnit.getName());
         artifactWasCreated = true;
      }

      for (TraceMark traceMark : traceUnit.getTraceMarks()) {
         if (monitor.isCanceled()) break;

         Artifact requirementArtifact = getRequirementArtifact(traceMark.getRawTraceMark(), requirementData);
         if (requirementArtifact != null) {
            IRelationEnumeration relationType = getRelationFromTraceType(traceUnitArtifact, traceMark.getTraceType());
            if (relationType == null) {
               // TODO Report Unknown Relation for trace unit artifact
               //               resultData.addRaw(AHTML.addRowMultiColumnTable(traceUnit.getTraceUnitType(), traceUnit.getName(),
               //                     traceMark.getTraceType(), traceMark.getRawTraceMark()));
            } else if (!requirementArtifact.isRelated(relationType, traceUnitArtifact)) {
               requirementArtifact.addRelation(relationType, traceUnitArtifact);
               hasChange = true;
            }
         } else {
            // Report Trace not found
            resultData.addRaw(AHTML.addRowMultiColumnTable(traceUnit.getTraceUnitType(), traceUnit.getName(),
                  traceMark.getTraceType(), traceMark.getRawTraceMark()));
         }
      }

      // TODO Report Items that were not used from the TEST UNIT DATA Structure
      // Not Part of this class though

      if (traceUnitArtifact.isOfType(Requirements.TEST_CASE)) {
         // Create even if no Change;
         //      if (testUnitWasCreated && allTraceMarksNotFound) {
         //         //         resultData
         //         // Report have a new test unit but no trace ?
         //      }
      }

      if (hasChange) {
         HierarchyHandler.addArtifact(transaction, traceUnitArtifact);
         if (traceUnitArtifact.isOfType(Requirements.ABSTRACT_TEST_UNIT)) {
            TestRunHandler.linkWithTestUnit(transaction, traceUnitArtifact);
         }
         traceUnitArtifact.persistAttributesAndRelations(transaction);
      }
   }

   private boolean isUsesTraceType(String traceType) {
      return traceType.equalsIgnoreCase("USES");
   }

   private IRelationEnumeration getRelationFromTraceType(Artifact traceUnitArtifact, String traceType) {
      if (traceUnitArtifact.isOfType(Requirements.ABSTRACT_TEST_UNIT)) {
         if (isUsesTraceType(traceType)) {
            return CoreRelationEnumeration.Uses__TestUnit;
         } else {
            return CoreRelationEnumeration.Verification__Verifier;
         }
      } else if (traceUnitArtifact.isOfType(Requirements.CODE_UNIT)) {
         return CoreRelationEnumeration.CodeRequirement_CodeUnit;
      }
      return null;
   }

   private Artifact getRequirementArtifact(String traceMark, RequirementData requirementData) {
      Artifact toReturn = requirementData.getRequirementFromTraceMark(traceMark);
      if (toReturn == null) {
         Pair<String, String> structuredRequirement =
               TraceabilityExtractor.getInstance().getStructuredRequirement(traceMark);
         if (structuredRequirement != null) {
            toReturn = requirementData.getRequirementFromTraceMark(structuredRequirement.getKey());
         }
      }
      return toReturn;
   }

   @Override
   public void onComplete(IProgressMonitor monitor) throws OseeCoreException {
      resultData.addRaw(AHTML.endMultiColumnTable());
      if (!monitor.isCanceled()) {
         if (transaction != null) {
            transaction.execute();
         }
      }
   }

   private static final class TestRunHandler {

      private static void createGuidLink(Artifact testCase, Artifact testRun) throws OseeCoreException {
         if (testCase != null) {
            testRun.setSoleAttributeValue("Test Script GUID", testCase.getGuid());
         }
      }

      public static void linkWithTestUnit(SkynetTransaction transaction, Artifact testCase) throws OseeCoreException {
         if (testCase.isOfType(Requirements.TEST_CASE)) {
            List<Artifact> testRuns =
                  ArtifactQuery.getArtifactsFromTypeAndName(Requirements.TEST_RUN, testCase.getDescriptiveName(),
                        transaction.getBranch());

            for (Artifact testRun : testRuns) {
               createGuidLink(testCase, testRun);
            }
         }
      }
   }

   private static final class HierarchyHandler {

      public static void addArtifact(SkynetTransaction transaction, Artifact testUnit) throws OseeCoreException {
         Artifact folder = null;
         if (testUnit.isOfType(Requirements.TEST_CASE)) {
            folder = getOrCreateTestCaseFolder(transaction);
         } else if (testUnit.isOfType(Requirements.TEST_SUPPORT)) {
            folder = getOrCreateTestSupportFolder(transaction);
         } else if (testUnit.isOfType(Requirements.CODE_UNIT)) {
            folder = getOrCreateCodeUnitFolder(transaction);
         } else {
            folder = getOrCreateUnknownTestUnitFolder(transaction);
         }

         if (folder != null && !folder.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, testUnit)) {
            folder.addChild(testUnit);
            folder.persistAttributesAndRelations(transaction);
         }
      }

      private static Artifact getOrCreateUnknownTestUnitFolder(SkynetTransaction transaction) throws OseeCoreException {
         return getOrCreateTestUnitSubFolder(transaction, "Unknown Test Unit Type");
      }

      private static Artifact getOrCreateTestSupportFolder(SkynetTransaction transaction) throws OseeCoreException {
         return getOrCreateTestUnitSubFolder(transaction, Requirements.TEST_SUPPORT_UNITS);
      }

      private static Artifact getOrCreateTestCaseFolder(SkynetTransaction transaction) throws OseeCoreException {
         return getOrCreateTestUnitSubFolder(transaction, Requirements.TEST_CASES);
      }

      private static Artifact getOrCreateCodeUnitFolder(SkynetTransaction transaction) throws OseeCoreException {
         Artifact codeUnitFolder = getOrCreateFolder(transaction, "Code Units");
         Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(transaction.getBranch());
         if (!root.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, codeUnitFolder)) {
            root.addChild(codeUnitFolder);
            root.persistAttributesAndRelations(transaction);
         }
         return codeUnitFolder;
      }

      private static Artifact getOrCreateTestUnitSubFolder(SkynetTransaction transaction, String folderName) throws OseeCoreException {
         Artifact subFolder = getOrCreateFolder(transaction, folderName);
         Artifact testUnits = getOrCreateTestUnitsFolder(transaction);
         if (!testUnits.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, subFolder)) {
            testUnits.addChild(subFolder);
            testUnits.persistAttributesAndRelations(transaction);
         }
         return subFolder;
      }

      private static Artifact getOrCreateTestUnitsFolder(SkynetTransaction transaction) throws OseeCoreException {
         Artifact testUnitFolder = getOrCreateFolder(transaction, "Test Units");
         Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(transaction.getBranch());
         if (!root.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, testUnitFolder)) {
            root.addChild(testUnitFolder);
            root.persistAttributesAndRelations(transaction);
         }
         return testUnitFolder;
      }

      private static Artifact getOrCreateFolder(SkynetTransaction transaction, String folderName) throws OseeCoreException {
         Artifact folder = null;
         String key = "Folder:" + folderName;
         Branch branch = transaction.getBranch();
         try {
            folder = ArtifactCache.getByTextId(key, branch);
            if (folder == null) {
               folder = ArtifactQuery.getArtifactFromTypeAndName("Folder", folderName, branch);
               ArtifactCache.putByTextId(key, folder);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetActivator.class, Level.INFO, "Created " + folderName + " because was not found.");
            folder = ArtifactTypeManager.addArtifact("Folder", branch, folderName);
            folder.persistAttributes(transaction);
            ArtifactCache.putByTextId(key, folder);
         }
         return folder;
      }
   }
}
