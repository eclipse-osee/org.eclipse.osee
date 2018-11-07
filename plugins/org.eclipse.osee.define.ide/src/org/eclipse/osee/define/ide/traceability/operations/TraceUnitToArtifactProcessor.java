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
package org.eclipse.osee.define.ide.traceability.operations;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.AbstractSourceTagger;
import org.eclipse.osee.define.ide.traceability.CodeUnitTagger;
import org.eclipse.osee.define.ide.traceability.HierarchyHandler;
import org.eclipse.osee.define.ide.traceability.TestUnitTagger;
import org.eclipse.osee.define.ide.traceability.TraceabilityExtractor;
import org.eclipse.osee.define.ide.traceability.data.CodeUnitData;
import org.eclipse.osee.define.ide.traceability.data.RequirementData;
import org.eclipse.osee.define.ide.traceability.data.TestUnitData;
import org.eclipse.osee.define.ide.traceability.data.TraceMark;
import org.eclipse.osee.define.ide.traceability.data.TraceUnit;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnitToArtifactProcessor implements ITraceUnitProcessor {
   private RequirementData requirementData;

   private CodeUnitData codeUnitData;
   private TestUnitData testUnitData;
   private final boolean addGuidToSourceFile;

   private final BranchId importIntoBranch;
   private SkynetTransaction transaction;
   private HierarchyHandler handler;

   private final HashCollectionSet<TraceUnit, TraceMark> reportTraceNotFound;
   private final HashCollectionSet<String, String> unknownRelationError;
   private final Set<String> unRelatedUnits;

   public TraceUnitToArtifactProcessor(BranchId importIntoBranch, boolean addGuidToSourceFile) {
      this.importIntoBranch = importIntoBranch;
      this.reportTraceNotFound = new HashCollectionSet<>(HashSet::new);
      this.unknownRelationError = new HashCollectionSet<>(HashSet::new);
      this.unRelatedUnits = new HashSet<>();
      this.addGuidToSourceFile = addGuidToSourceFile;
   }

   @Override
   public void clear() {
      transaction = null;
      handler = null;
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
      handler = null;
      requirementData = new RequirementData(importIntoBranch, ArtifactId.SENTINEL);
      if (!monitor.isCanceled()) {
         requirementData.initialize(monitor);
      }
   }

   private Artifact getArtifactFromCache(IProgressMonitor monitor, ArtifactType artifactType, String name) {

      if (artifactType.inheritsFrom(CoreArtifactTypes.TestUnit)) {
         if (testUnitData == null) {
            testUnitData = new TestUnitData(importIntoBranch);
            if (!monitor.isCanceled()) {
               testUnitData.initialize(monitor);
            }
         }
         return testUnitData.getTestUnitByName(name);
      } else if (artifactType.inheritsFrom(CoreArtifactTypes.CodeUnit)) {
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

   private AbstractSourceTagger getGuidUtility(IArtifactType artifactType) {
      if (artifactType.equals(CoreArtifactTypes.TestCase)) {
         return TestUnitTagger.getInstance();
      } else if (artifactType.equals(CoreArtifactTypes.CodeUnit)) {
         return CodeUnitTagger.getInstance();
      }
      return null;
   }

   @Override
   public void process(IProgressMonitor monitor, TraceUnit traceUnit) {
      if (transaction == null) {
         transaction = TransactionManager.createTransaction(importIntoBranch, "Importing Trace Unit(s)");
         handler = new HierarchyHandler(transaction);
      }
      boolean hasChange = false;
      boolean artifactWasCreated = false;
      boolean wasRelated = false;
      Artifact traceUnitArtifact = null;
      AbstractSourceTagger guidUtility = getGuidUtility(traceUnit.getTraceUnitType());
      String guid = null;

      if (guidUtility != null) {
         URI uriPath = traceUnit.getUriPath();
         if (uriPath != null) {
            try {
               guid = guidUtility.getSourceTag(uriPath);
            } catch (IOException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
         if (guid != null) {
            if (!GUID.isValid(guid) && addGuidToSourceFile) {
               try {
                  guidUtility.removeSourceTag(uriPath);
               } catch (IOException ex) {
                  OseeCoreException.wrapAndThrow(ex);
               }
            } else {
               traceUnitArtifact =
                  ArtifactQuery.checkArtifactFromId(guid, transaction.getBranch(), DeletionFlag.INCLUDE_DELETED);
               if (traceUnitArtifact != null && traceUnitArtifact.isDeleted()) {
                  traceUnitArtifact = null;
                  guid = null;
               }
            }
         }
      }
      if (traceUnitArtifact == null) {
         traceUnitArtifact = getArtifactFromCache(monitor, traceUnit.getTraceUnitType(), traceUnit.getName());
      }
      if (traceUnitArtifact == null) {
         traceUnitArtifact =
            ArtifactTypeManager.addArtifact(traceUnit.getTraceUnitType(), transaction.getBranch(), null, guid);
         traceUnitArtifact.setName(traceUnit.getName());
         artifactWasCreated = true;
      }

      if (guidUtility != null && !traceUnitArtifact.getGuid().equals(guid) && addGuidToSourceFile) {
         try {
            guidUtility.removeSourceTag(traceUnit.getUriPath());
            guidUtility.addSourceTag(traceUnit.getUriPath(), traceUnitArtifact.getGuid());
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);

         }
      }

      if (!traceUnitArtifact.getName().equals(traceUnit.getName())) {
         traceUnitArtifact.setName(traceUnit.getName());
         hasChange = true;
      }

      //          * populate traces in osee not in scripts
      //          * 1. add all relations for Uses and Verifies to collection
      //          * 2. remove from collection as tracemarks are processed
      //          * 3. all leftovers get un-related

      removeExistingTraceability(traceUnitArtifact);

      for (TraceMark traceMark : traceUnit.getTraceMarks()) {
         if (monitor.isCanceled()) {
            break;
         }

         Artifact requirementArtifact = getRequirementArtifact(traceMark.getRawTraceMark(), requirementData);

         if (requirementArtifact != null) {
            RelationTypeSide relationType = getRelationFromTraceType(traceUnitArtifact, traceMark.getTraceType());
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
         handler.addArtifact(traceUnitArtifact);
         traceUnitArtifact.persist(transaction);
      }
   }

   private boolean isUsesTraceType(String traceType) {
      return traceType.equalsIgnoreCase("USES");
   }

   private RelationTypeSide getRelationFromTraceType(Artifact traceUnitArtifact, String traceType) {
      if (traceUnitArtifact.isOfType(CoreArtifactTypes.TestUnit)) {
         if (isUsesTraceType(traceType)) {
            return CoreRelationTypes.Uses__TestUnit;
         } else {
            return CoreRelationTypes.Verification__Verifier;
         }
      } else if (traceUnitArtifact.isOfType(CoreArtifactTypes.CodeUnit)) {
         return CoreRelationTypes.CodeRequirement_CodeUnit;
      }
      return null;
   }

   private void removeExistingTraceability(Artifact traceUnitArtifact) {
      if (traceUnitArtifact.isOfType(CoreArtifactTypes.TestUnit)) {
         traceUnitArtifact.deleteRelations(CoreRelationTypes.Uses__Requirement);
         traceUnitArtifact.deleteRelations(CoreRelationTypes.Verification__Requirement);
      } else if (traceUnitArtifact.isOfType(CoreArtifactTypes.CodeUnit)) {
         traceUnitArtifact.deleteRelations(CoreRelationTypes.CodeRequirement_Requirement);
      }
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
   public void onComplete(IProgressMonitor monitor) {
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
      Jobs.runInJob("Trace Unit to Artifact Report", runnable, Activator.class, Activator.PLUGIN_ID);
   }

   private final class ResultEditorProvider implements IResultsEditorProvider {

      @Override
      public String getEditorName() {
         return "Trace Units To Artifacts Report";
      }

      private List<XViewerColumn> createColumns(String... columnNames) {
         List<XViewerColumn> columns = new ArrayList<>();
         for (String name : columnNames) {
            columns.add(new XViewerColumn(name, name, 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         }
         return columns;
      }

      private void addUnRelatedTraceUnit(List<IResultsEditorTab> toReturn) {
         if (!unRelatedUnits.isEmpty()) {
            List<XViewerColumn> columns = createColumns("Trace Unit Name");
            List<IResultsXViewerRow> rows = new ArrayList<>();
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

            List<IResultsXViewerRow> rows = new ArrayList<>();
            for (TraceUnit unit : reportTraceNotFound.keySet()) {
               Collection<TraceMark> traceMarks = reportTraceNotFound.getValues(unit);
               for (TraceMark traceMark : traceMarks) {
                  rows.add(new ResultsXViewerRow(new String[] {
                     unit.getName(),
                     unit.getTraceUnitType().getName(),
                     traceMark.getTraceType(),
                     traceMark.getRawTraceMark()}));
               }
            }
            toReturn.add(new ResultsEditorTableTab("Trace Marks Not Found", columns, rows));
         }
      }

      private void addRelationTypeNotFoundTab(List<IResultsEditorTab> toReturn) {
         if (!unknownRelationError.isEmpty()) {
            List<XViewerColumn> columns = createColumns("Artifact Type", "Trace Mark Type");
            List<IResultsXViewerRow> rows = new ArrayList<>();
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
      public List<IResultsEditorTab> getResultsEditorTabs() {
         List<IResultsEditorTab> toReturn = new ArrayList<>();
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
