/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage;

import org.eclipse.osee.coverage.model.CoverageImport;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageImporter {

   public CoverageImport run();
}
