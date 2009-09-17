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
package org.eclipse.osee.framework.types.bridge.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class ReportDirtyOseeTypesOperation extends AbstractOperation {
   private final OseeTypeCache cache;

   public ReportDirtyOseeTypesOperation(OseeTypeCache cache) {
      super("Report Osee Type Changes", Activator.PLUGIN_ID);
      this.cache = cache;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
      createOseeEnumTypeReport(tabs, cache.getEnumTypeCache().getDirtyTypes());
      createAttributeTypeReport(tabs, cache.getAttributeTypeCache().getDirtyTypes());
      createArtifactTypeReport(tabs, cache.getArtifactTypeCache());
      createRelationTypeReport(tabs, cache.getRelationTypeCache().getDirtyTypes());
      openReport(tabs);
   }

   private void openReport(final List<IResultsEditorTab> resultsTabs) {
      Job job = new UIJob("Un-Persistted Osee Types") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status;
            try {
               ResultsEditor.open(new OseeTypesReportProvider(getName(), resultsTabs));
               status = Status.OK_STATUS;
            } catch (Exception ex) {
               status =
                     new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error creating Un-Persistted Osee Types Report",
                           ex);
            }
            return status;
         }
      };
      Operations.scheduleJob(job, true, Job.SHORT, null);
   }

   private void createArtifactTypeReport(List<IResultsEditorTab> tabs, ArtifactTypeCache cache) throws OseeCoreException {
      Collection<ArtifactType> types = cache.getDirtyTypes();
      ReportTab tab = new ReportTab("Artifact Types", tabs);
      tab.addTableHeader("Name", "ModType", "Data Dirty", "IsAbstract", "Inherits", "Validity");
      String inheritance;
      String validity;
      for (ArtifactType type : types) {
         if (type.isAttributeTypeValidityDirty()) {
            validity = cache.getLocalAttributeTypes(type).toString();
         } else {
            validity = "Not Changed";
         }
         if (type.isInheritanceDirty()) {
            inheritance = cache.getArtifactSuperType(type).toString();
         } else {
            inheritance = "Not Changed";
         }
         tab.addRow(type.getName(), type.getModificationType().getDisplayName(), String.valueOf(type.isAbstract()),
               String.valueOf(type.isDataDirty()), inheritance, validity);
      }
      tab.endTable();
   }

   private void createAttributeTypeReport(List<IResultsEditorTab> tabs, Collection<AttributeType> types) {
      ReportTab tab = new ReportTab("Attribute Types", tabs);
      tab.addTableHeader("Name", "ModType");
      for (AttributeType type : types) {
         tab.addRow(type.getName(), type.getModificationType().getDisplayName());
      }
      tab.endTable();
   }

   private void createRelationTypeReport(List<IResultsEditorTab> tabs, Collection<RelationType> types) {
      ReportTab tab = new ReportTab("Relation Types", tabs);
      tab.addTableHeader("Name", "ModType");
      for (RelationType type : types) {
         tab.addRow(type.getName(), type.getModificationType().getDisplayName());
      }
      tab.endTable();
   }

   private void createOseeEnumTypeReport(List<IResultsEditorTab> tabs, Collection<OseeEnumType> types) throws OseeCoreException {
      ReportTab tab = new ReportTab("OseeEnum Types", tabs);
      tab.addTableHeader("Name", "ModType", "isDataDirty", "areEntriesDirty");
      String dirtyEntries;
      for (OseeEnumType type : types) {
         if (type.areEntriesDirty()) {
            List<String> data = new ArrayList<String>();
            for (OseeEnumEntry entry : type.values()) {
               if (entry.isDirty()) {
                  data.add(String.format("*{%s}", entry.toString()));
               } else {
                  data.add(entry.toString());
               }
            }
            dirtyEntries = Collections.toString(data, ",");
         } else {
            dirtyEntries = "Not Changed";
         }
         tab.addRow(type.getName(), type.getModificationType().getDisplayName(), String.valueOf(type.isDataDirty()),
               dirtyEntries);
      }
      tab.endTable();
   }
   private static final class OseeTypesReportProvider implements IResultsEditorProvider {
      private final List<IResultsEditorTab> resultsTabs;
      private final String editorName;

      public OseeTypesReportProvider(String editorName, List<IResultsEditorTab> resultsTabs) {
         this.resultsTabs = resultsTabs;
         this.editorName = editorName;
      }

      @Override
      public String getEditorName() throws OseeCoreException {
         return editorName;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
         return resultsTabs;
      }
   }

   private static final class ReportTab {
      private final String title;
      private List<XViewerColumn> columns;
      private List<IResultsXViewerRow> rows;
      private final List<IResultsEditorTab> resultsTabs;

      public ReportTab(String title, List<IResultsEditorTab> resultsTabs) {
         this.title = title;
         this.columns = null;
         this.rows = null;
         this.resultsTabs = resultsTabs;
      }

      public void addRow(String... data) {
         if (rows == null) {
            rows = new ArrayList<IResultsXViewerRow>();
         }
         rows.add(new ResultsXViewerRow(data));
      }

      public void addTableHeader(String... header) {
         this.columns = new ArrayList<XViewerColumn>();
         for (String name : header) {
            columns.add(new XViewerColumn(name, name, 80, SWT.LEFT, true, SortDataType.String, false, ""));
         }
      }

      public void endTable() {
         resultsTabs.add(new ResultsEditorTableTab(title, columns, rows));
      }
   }
}
