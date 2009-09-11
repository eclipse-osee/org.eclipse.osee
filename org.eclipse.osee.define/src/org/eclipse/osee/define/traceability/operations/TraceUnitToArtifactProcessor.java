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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.TraceabilityExtractor;
import org.eclipse.osee.define.traceability.data.CodeUnitData;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.define.traceability.data.TestUnitData;
import org.eclipse.osee.define.traceability.data.TraceMark;
import org.eclipse.osee.define.traceability.data.TraceUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnitToArtifactProcessor implements ITraceUnitProcessor {
   private RequirementData requirementData;

   private CodeUnitData codeUnitData;
   private TestUnitData testUnitData;

   private final Branch importIntoBranch;
   private SkynetTransaction transaction;

   private final HashCollection<TraceUnit, TraceMark> reportTraceNotFound;
   private final HashCollection<String, String> unknownRelationError;
   private final Set<String> unRelatedUnits;

   public TraceUnitToArtifactProcessor(Branch importIntoBranch) {
      this.importIntoBranch = importIntoBranch;
      this.reportTraceNotFound = new HashCollection<TraceUnit, TraceMark>(false, HashSet.class);
      this.unknownRelationError = new HashCollection<String, String>(false, HashSet.class);
      this.unRelatedUnits = new HashSet<String>();
   }

   @Override
   public void clear() {
      transaction = null;
      if (requirementData != null) {
         requirementData.reset();
         requirementData = null;
      }
      if (testUnitData != null) {
         testUnitData.reset();
         testUnitData = null;
      }
      if (codeUnitData != null) {
         codeUnitData.reset();
         codeUnitData = null;
      }
   }

   @Override
   public void initialize(IProgressMonitor monitor) {
      transaction = null;
      requirementData = new RequirementData(importIntoBranch);
      if (!monitor.isCanceled()) {
         requirementData.initialize(monitor);
      }
   }

   private Artifact getArtifactFromCache(IProgressMonitor monitor, String artifactType, String name) {
      if (Requirements.ALL_TEST_UNIT_TYPES.contains(artifactType)) {
         if (testUnitData == null) {
            testUnitData = new TestUnitData(importIntoBranch);
            if (!monitor.isCanceled()) {
               testUnitData.initialize(monitor);
            }
         }
         return testUnitData.getTestUnitByName(name);
      } else if (Requirements.CODE_UNIT.equals(artifactType)) {
         if (codeUnitData == null) {
            codeUnitData = new CodeUnitData(importIntoBranch);
            if (!monitor.isCanceled()) {
               codeUnitData.initialize(monitor);
            }
         }
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
      boolean wasRelated = false;
      String traceUnitType = traceUnit.getTraceUnitType();

      Artifact traceUnitArtifact = getArtifactFromCache(monitor, traceUnitType, traceUnit.getName());
      if (traceUnitArtifact == null) {
         traceUnitArtifact =
               ArtifactTypeManager.addArtifact(traceUnit.getTraceUnitType(), transaction.getBranch(),
                     traceUnit.getName());
         artifactWasCreated = true;
      }

      for (TraceMark traceMark : traceUnit.getTraceMarks()) {
         if (monitor.isCanceled()) {
            break;
         }

         Artifact requirementArtifact = getRequirementArtifact(traceMark.getRawTraceMark(), requirementData);
         if (requirementArtifact != null) {
            IRelationEnumeration relationType = getRelationFromTraceType(traceUnitArtifact, traceMark.getTraceType());
            if (relationType == null) {
               unknownRelationError.put(traceUnitArtifact.getArtifactTypeName(), traceMark.getTraceType());
            } else if (!requirementArtifact.isRelated(relationType, traceUnitArtifact)) {
               requirementArtifact.addRelation(relationType, traceUnitArtifact);
               hasChange = true;
               wasRelated = true;
            } else {
               wasRelated = true;
            }
         } else {
            reportTraceNotFound.put(traceUnit, traceMark);
         }
      }

      // TODO Report Items that were not used from the TEST UNIT DATA Structure
      // Not Part of this class though
      if (!wasRelated) {
         unRelatedUnits.add(traceUnitArtifact.getName());
      }

      if (hasChange || artifactWasCreated) {
         HierarchyHandler.addArtifact(transaction, traceUnitArtifact);
         if (traceUnitArtifact.isOfType(Requirements.ABSTRACT_TEST_UNIT)) {
            TestRunHandler.linkWithTestUnit(transaction, traceUnitArtifact);
         }
         traceUnitArtifact.persist(transaction);
      }
   }

   private boolean isUsesTraceType(String traceType) {
      return traceType.equalsIgnoreCase("USES");
   }

   private IRelationEnumeration getRelationFromTraceType(Artifact traceUnitArtifact, String traceType) throws OseeCoreException {
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
            toReturn = requirementData.getRequirementFromTraceMark(structuredRequirement.getFirst());
         }
      }
      return toReturn;
   }

   @Override
   public void onComplete(IProgressMonitor monitor) throws OseeCoreException {
      try {
         if (!monitor.isCanceled()) {
            if (transaction != null) {
               transaction.execute();
            }
         }
      } finally {
         openReport();
      }
   }

   private void openReport() {
      IExceptionableRunnable runnable = new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            ResultsEditor.open(new ResultEditorProvider());
            return Status.OK_STATUS;
         }
      };
      Jobs.runInJob("Trace Unit to Artifact Report", runnable, DefinePlugin.class, DefinePlugin.PLUGIN_ID);
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
                  ArtifactQuery.getArtifactListFromTypeAndName(Requirements.TEST_RUN, testCase.getName(),
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
            folder.persist(transaction);
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
         Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(transaction.getBranch());
         if (!root.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, codeUnitFolder)) {
            root.addChild(codeUnitFolder);
            root.persist(transaction);
         }
         return codeUnitFolder;
      }

      private static Artifact getOrCreateTestUnitSubFolder(SkynetTransaction transaction, String folderName) throws OseeCoreException {
         Artifact subFolder = getOrCreateFolder(transaction, folderName);
         Artifact testUnits = getOrCreateTestUnitsFolder(transaction);
         if (!testUnits.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, subFolder)) {
            testUnits.addChild(subFolder);
            testUnits.persist(transaction);
         }
         return subFolder;
      }

      private static Artifact getOrCreateTestUnitsFolder(SkynetTransaction transaction) throws OseeCoreException {
         Artifact testUnitFolder = getOrCreateFolder(transaction, "Test Units");
         Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(transaction.getBranch());
         if (!root.isRelated(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, testUnitFolder)) {
            root.addChild(testUnitFolder);
            root.persist(transaction);
         }
         return testUnitFolder;
      }

      private static Artifact getOrCreateFolder(SkynetTransaction transaction, String folderName) throws OseeCoreException {
         return OseeSystemArtifacts.getOrCreateArtifact("Folder", folderName, transaction.getBranch());
      }
   }

   private final class ResultEditorProvider implements IResultsEditorProvider {

      @Override
      public String getEditorName() throws OseeCoreException {
         return "Trace Units To Artifacts Report";
      }

      private List<XViewerColumn> createColumns(String... columnNames) {
         List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
         for (String name : columnNames) {
            columns.add(new XViewerColumn(name, name, 80, SWT.LEFT, true, SortDataType.String, false, ""));
         }
         return columns;
      }

      private void addUnRelatedTraceUnit(List<IResultsEditorTab> toReturn) {
         if (!unRelatedUnits.isEmpty()) {
            List<XViewerColumn> columns = createColumns("Trace Unit Name");
            List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
            for (String artifactName : unRelatedUnits) {
               rows.add(new ResultsXViewerRow(new String[] {artifactName}));
            }
            toReturn.add(new ResultsEditorTableTab("Trace Units Created But Had No Relations", columns, rows));
         }
      }

      private void addTraceNotFoundTab(List<IResultsEditorTab> toReturn) {
         if (!reportTraceNotFound.isEmpty()) {
            List<XViewerColumn> columns =
                  createColumns("Trace Unit Name", "Trace Unit Type", "Trace Mark Type", "Trace Mark");

            List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
            for (TraceUnit unit : reportTraceNotFound.keySet()) {
               Collection<TraceMark> traceMarks = reportTraceNotFound.getValues(unit);
               for (TraceMark traceMark : traceMarks) {
                  rows.add(new ResultsXViewerRow(new String[] {unit.getName(), unit.getTraceUnitType(),
                        traceMark.getTraceType(), traceMark.getRawTraceMark()}));
               }
            }
            toReturn.add(new ResultsEditorTableTab("Trace Marks Not Found", columns, rows));
         }
      }

      private void addRelationTypeNotFoundTab(List<IResultsEditorTab> toReturn) {
         if (!unknownRelationError.isEmpty()) {
            List<XViewerColumn> columns = createColumns("Artifact Type", "Trace Mark Type");
            List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
            for (String artifactType : unknownRelationError.keySet()) {
               Collection<String> traceTypes = unknownRelationError.getValues(artifactType);
               for (String traceType : traceTypes) {
                  rows.add(new ResultsXViewerRow(new String[] {artifactType, traceType}));
               }
            }
            toReturn.add(new ResultsEditorTableTab("Invalid Artifact Type to Trace Relation", columns, rows));
         }
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
         List<IResultsEditorTab> toReturn = new ArrayList<IResultsEditorTab>();
         addTraceNotFoundTab(toReturn);
         addUnRelatedTraceUnit(toReturn);
         addRelationTypeNotFoundTab(toReturn);
         if (toReturn.isEmpty()) {
            toReturn.add(new ResultsEditorHtmlTab("Trace Unit Import Status", "Import Status", "All Items Linked"));
         }
         return toReturn;
      }
   }
}
