/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.xresults.IResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorTableTab extends IResultsEditorTab {

   public List<XViewerColumn> getTableColumns() throws OseeCoreException;

   public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException;

}
