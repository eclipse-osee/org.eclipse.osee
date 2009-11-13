/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public abstract class CoverageStore implements ICoverageStore {

   protected final ICoverage coverage;

   public CoverageStore(ICoverage coverage) {
      this.coverage = coverage;
   }

}
