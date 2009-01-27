/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerContentProvider;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorTableTab implements IResultsEditorTableTab {

   private final String tabName;
   private final List<XViewerColumn> columns;
   private final Collection<IResultsXViewerRow> rows;
   private ResultsXViewer resultsXViewer;

   public ResultsEditorTableTab(String tabName, List<XViewerColumn> columns, Collection<IResultsXViewerRow> rows) {
      this.tabName = tabName;
      this.columns = columns;
      this.rows = rows;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTableTab#getTableColumns()
    */
   @Override
   public List<XViewerColumn> getTableColumns() throws OseeCoreException {
      return columns;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTableTab#getTableRows()
    */
   @Override
   public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException {
      return rows;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#createTab(org.eclipse.swt.widgets.Composite, org.eclipse.osee.framework.ui.skynet.results.ResultsEditor)
    */
   @Override
   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
      Composite comp = ALayout.createCommonPageComposite(parent);
      resultsEditor.createToolBar(comp);

      GridData gd = new GridData(GridData.FILL_BOTH);
      resultsXViewer = new ResultsXViewer(comp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, getTableColumns());
      resultsXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      resultsXViewer.setContentProvider(new ResultsXViewerContentProvider());
      resultsXViewer.setLabelProvider(new ResultsXViewerLabelProvider(resultsXViewer));
      resultsXViewer.setInput(getTableRows());
      resultsXViewer.getTree().setLayoutData(gd);

      return comp;
   }
}
