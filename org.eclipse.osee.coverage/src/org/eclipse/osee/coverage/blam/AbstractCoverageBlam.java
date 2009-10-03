/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.blam;

import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractCoverageBlam extends AbstractBlam {

   CoverageImport coverageImport;

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

   public void setCoverageImport(CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
   }

}
