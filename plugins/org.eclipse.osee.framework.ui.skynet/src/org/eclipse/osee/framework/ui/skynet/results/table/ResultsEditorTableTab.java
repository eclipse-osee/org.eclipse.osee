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
package org.eclipse.osee.framework.ui.skynet.results.table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorOutlineProvider;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.IResultsEditorTableListener;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerContentProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;
import org.eclipse.osee.framework.ui.skynet.util.TableWriterAdaptor;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorTableTab implements IResultsEditorTableTab {

   private final String tabName;
   private final List<XViewerColumn> columns;
   private final Collection<IResultsXViewerRow> rows;
   private ResultsXViewer resultsXViewer;
   private XViewerFactory xViewerFactory;
   private final ITreeContentProvider contentProvider;
   private final IResultsEditorLabelProvider labelProvider;
   private final List<IResultsEditorTableListener> listeners;
   private IResultsEditorOutlineProvider outlineProvider;
   private ResultsEditor editor;

   public void setEditor(ResultsEditor editor) {
      this.editor = editor;
   }

   public ResultsEditor getEditor() {
      return editor;
   }

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows, ITreeContentProvider contentProvider, IResultsEditorLabelProvider labelProvider) {
      this(tabName, columns, rows, contentProvider, labelProvider, null);
   }

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows, ITreeContentProvider contentProvider, IResultsEditorLabelProvider labelProvider, List<IResultsEditorTableListener> listeners) {
      this.tabName = tabName;
      this.columns = columns;
      this.rows = rows;
      this.xViewerFactory = new ResultsXViewerFactory(columns);
      this.contentProvider = contentProvider == null ? new ResultsXViewerContentProvider() : contentProvider;
      this.labelProvider = labelProvider;
      this.listeners = listeners == null ? new ArrayList<>() : listeners;
   }

   public ResultsEditorTableTab(String tabName) {
      this(tabName, null, null);
   }

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows) {
      this(tabName, columns, rows, new ResultsXViewerContentProvider(), null, null);
   }

   public void addColumn(XViewerColumn xViewerColumn) {
      this.columns.add(xViewerColumn);
   }

   @Override
   public void addListener(IResultsEditorTableListener listener) {
      listeners.add(listener);
   }

   public void addRow(IResultsXViewerRow resultsXViewerRow) {
      this.rows.add(resultsXViewerRow);
   }

   @Override
   public List<XViewerColumn> getTableColumns() {
      return columns;
   }

   @Override
   public Collection<IResultsXViewerRow> getTableRows() {
      return rows;
   }

   @Override
   public String getTabName() {
      return tabName;
   }

   @Override
   public Composite createTab(Composite parent, ResultsEditor resultsEditor) {
      Composite comp = ALayout.createCommonPageComposite(parent);
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));
      if (resultsEditor != null) {
         ToolBar toolBar = resultsEditor.createToolBar(comp);
         addToolBarItems(toolBar);
      }

      GridData gd = new GridData(GridData.FILL_BOTH);
      resultsXViewer =
         new ResultsXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, getTableColumns(), xViewerFactory);
      resultsXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      for (IResultsEditorTableListener listener : listeners) {
         resultsXViewer.addListener(listener);
      }

      resultsXViewer.setContentProvider(contentProvider);
      XViewerLabelProvider provider = null;
      if (labelProvider == null) {
         provider = new ResultsXViewerLabelProvider(resultsXViewer);
      } else {
         provider = labelProvider.getLabelProvider(resultsXViewer);
      }
      resultsXViewer.setLabelProvider(provider);
      resultsXViewer.setInput(getTableRows());
      resultsXViewer.getTree().setLayoutData(gd);
      resultsXViewer.updateStatusLabel();
      return comp;
   }

   private void addToolBarItems(ToolBar toolBar) {
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("html"));
      item.setToolTipText("Export as HTML");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            XViewerTreeReport report = resultsXViewer.getXViewerFactory().getXViewerTreeReport(resultsXViewer);
            if (report != null) {
               report.open();
            } else {
               new XViewerTreeReport(resultsXViewer).open(tabName + ".html");
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("csv"));
      item.setToolTipText("Export as CSV (comma seperated value)");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               new HtmlExportTable(tabName, new XViewerTreeReport(resultsXViewer).getHtml(), true).exportCsv();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("csv"));
      item.setToolTipText("Export as TSV (tab seperated value)");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               new HtmlExportTable(tabName, new XViewerTreeReport(resultsXViewer).getHtml(), true).exportTsv();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("pdf"));
      item.setToolTipText("Export to PDF");
      item.addSelectionListener(new SelectionAdapter() {

         private List<String> getColumns() {
            List<String> cols = new ArrayList<>();
            for (XViewerColumn col : columns) {
               cols.add(col.getName());
            }
            return cols;
         }

         private void writeRows(TableWriterAdaptor writerAdaptor) {
            List<String> rws = new ArrayList<>();
            for (IResultsXViewerRow row : rows) {
               for (int i = 0; i < columns.size(); i++) {
                  rws.add(row.getValue(i));
               }
               writerAdaptor.writeRow(rws.toArray(new String[rws.size()]));
               rws.clear();
            }
         }

         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               File TableResultsFile = OseeData.getFile(tabName + ".pdf");
               OutputStream outputStream = new FileOutputStream(TableResultsFile, true);
               TableWriterAdaptor writerAdaptor = new TableWriterAdaptor("pdf", outputStream);
               writerAdaptor.writeHeader(getColumns().toArray(new String[getColumns().size()]));
               writeRows(writerAdaptor);
               writerAdaptor.writeTitle(tabName);
               writerAdaptor.openDocument();
               writerAdaptor.writeDocument();
               writerAdaptor.close();
               Program.launch(TableResultsFile.getAbsolutePath());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }

   public ResultsXViewer getResultsXViewer() {
      return resultsXViewer;
   }

   public void setxViewerFactory(XViewerFactory xViewerFactory) {
      this.xViewerFactory = xViewerFactory;
   }

   public interface IResultsEditorLabelProvider {
      XViewerLabelProvider getLabelProvider(ResultsXViewer xViewer);
   }

   @Override
   public void addOutlineProvider(IResultsEditorOutlineProvider outlineProvider) {
      this.outlineProvider = outlineProvider;
   }

   @Override
   public IResultsEditorOutlineProvider getOutlineProvider() {
      return outlineProvider;
   }

}
