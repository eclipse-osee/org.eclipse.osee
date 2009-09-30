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
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
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
      createOseeEnumTypeReport(tabs, cache.getEnumTypeCache().getAllDirty());
      createAttributeTypeReport(tabs, cache.getAttributeTypeCache().getAllDirty());
      createArtifactTypeReport(tabs, cache.getArtifactTypeCache());
      createRelationTypeReport(tabs, cache.getRelationTypeCache().getAllDirty());
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
      Collection<ArtifactType> types = cache.getAllDirty();
      ReportTab tab = new ReportTab("Artifact Types", tabs);
      addHeader(tab, types);
      for (ArtifactType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            boolean isDirty = type.isFieldDirty(fieldName);
            if (isDirty && ArtifactType.ARTIFACT_INHERITANCE_FIELD_KEY.equals(fieldName)) {
               data.add(cache.getArtifactSuperType(type).toString());
            } else if (isDirty && ArtifactType.ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY.equals(fieldName)) {
               data.add(cache.getLocalAttributeTypes(type).toString());
            } else {
               data.add(String.valueOf(isDirty));
            }
         }
         tab.addRow(data);
      }
      tab.endTable();
   }

   private void createAttributeTypeReport(List<IResultsEditorTab> tabs, Collection<AttributeType> types) throws OseeCoreException {
      ReportTab tab = new ReportTab("Attribute Types", tabs);
      addHeader(tab, types);
      for (AttributeType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            data.add(String.valueOf(type.isFieldDirty(fieldName)));
         }
         tab.addRow(data);
      }
      tab.endTable();
   }

   private void addHeader(ReportTab tab, Collection<?> types) {
      List<String> columns = new ArrayList<String>();
      columns.add("Name");
      columns.add("ModType");
      if (!types.isEmpty()) {
         AbstractOseeType type = (AbstractOseeType) types.iterator().next();
         columns.addAll(type.getFieldNames());
      }
      tab.addTableHeader(columns.toArray(new String[columns.size()]));
   }

   private void createRelationTypeReport(List<IResultsEditorTab> tabs, Collection<RelationType> types) throws OseeCoreException {
      ReportTab tab = new ReportTab("Relation Types", tabs);
      addHeader(tab, types);
      for (RelationType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            data.add(String.valueOf(type.isFieldDirty(fieldName)));
         }
         tab.addRow(data);
      }
      tab.endTable();
   }

   private void createOseeEnumTypeReport(List<IResultsEditorTab> tabs, Collection<OseeEnumType> types) throws OseeCoreException {
      ReportTab tab = new ReportTab("OseeEnum Types", tabs);
      addHeader(tab, types);
      String dirtyEntries;
      for (OseeEnumType type : types) {
         List<String> data = new ArrayList<String>();
         data.add(type.getName());
         data.add(type.getModificationType().getDisplayName());
         for (String fieldName : type.getFieldNames()) {
            boolean isDirty = type.isFieldDirty(fieldName);
            if (isDirty && OseeEnumType.OSEE_ENUM_TYPE_ENTRIES_FIELD.equals(fieldName)) {
               List<String> dirtyItems = new ArrayList<String>();
               for (OseeEnumEntry entry : type.values()) {
                  if (entry.isDirty()) {
                     dirtyItems.add(String.format("*{%s}", entry.toString()));
                  } else {
                     dirtyItems.add(entry.toString());
                  }
               }
               data.add(Collections.toString(dirtyItems, ","));
            } else {
               data.add(String.valueOf(isDirty));
            }
         }
         tab.addRow(data);
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

      public void addRow(Collection<String> data) {
         if (rows == null) {
            rows = new ArrayList<IResultsXViewerRow>();
         }
         rows.add(new ResultsXViewerRow(data.toArray(new String[data.size()])));
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
