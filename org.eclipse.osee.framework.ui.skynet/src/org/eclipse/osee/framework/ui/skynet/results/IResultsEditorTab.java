/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorTab {

   public String getTabName();

   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException;
}
