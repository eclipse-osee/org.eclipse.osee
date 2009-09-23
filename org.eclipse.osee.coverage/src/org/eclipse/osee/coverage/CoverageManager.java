/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.results.CoverageImportResultsEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class CoverageManager {

   public static void importCoverage(ICoverageImporter coverageImporter) throws OseeCoreException {
      CoverageImport coverageImport = coverageImporter.run();
      new CoverageImportResultsEditor(coverageImport).open();
   }
}
