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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.TableWriterAdaptor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerContentProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;
import org.eclipse.osee.framework.ui.swt.ALayout;
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
   private List<XViewerColumn> columns;
   private Collection<IResultsXViewerRow> rows;
   private ResultsXViewer resultsXViewer;

   public ResultsEditorTableTab(String tabName) {
      this(tabName, null, null);
      this.columns = new ArrayList<XViewerColumn>();
      this.rows = new ArrayList<IResultsXViewerRow>();
   }

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows) {
      this.tabName = tabName;
      this.columns = columns;
      this.rows = rows;
   }

   public void addColumn(XViewerColumn xViewerColumn) {
      this.columns.add(xViewerColumn);
   }

   public void addRow(IResultsXViewerRow resultsXViewerRow) {
      this.rows.add(resultsXViewerRow);
   }

   @Override
   public List<XViewerColumn> getTableColumns() throws OseeCoreException {
      return columns;
   }

   @Override
   public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException {
      return rows;
   }

   @Override
   public String getTabName() {
      return tabName;
   }

   @Override
   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
      Composite comp = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(comp);
      addToolBarItems(toolBar);

      GridData gd = new GridData(GridData.FILL_BOTH);
      resultsXViewer = new ResultsXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, getTableColumns());
      resultsXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      resultsXViewer.setContentProvider(new ResultsXViewerContentProvider());
      resultsXViewer.setLabelProvider(new ResultsXViewerLabelProvider(resultsXViewer));
      resultsXViewer.setInput(getTableRows());
      resultsXViewer.getTree().setLayoutData(gd);
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
               (new HtmlExportTable(tabName, new XViewerTreeReport(resultsXViewer).getHtml(), true)).exportCsv();
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
               (new HtmlExportTable(tabName, new XViewerTreeReport(resultsXViewer).getHtml(), true)).exportTsv();
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getProgramImage("pdf"));
      item.setToolTipText("Export to PDF");
      item.addSelectionListener(new SelectionAdapter() {

         private List<String> getColumns() {
            List<String> cols = new ArrayList<String>();
            for (XViewerColumn col : columns) {
               cols.add(col.getName());
            }
            return cols;
         }

         private void writeRows(TableWriterAdaptor writerAdaptor) {
            List<String> rws = new ArrayList<String>();
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
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }
}
