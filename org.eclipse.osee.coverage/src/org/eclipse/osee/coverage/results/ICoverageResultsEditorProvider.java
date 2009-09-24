/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.results;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageResultsEditorProvider extends IResultsEditorProvider {

   public CoverageImport getCoverageImport();
}
