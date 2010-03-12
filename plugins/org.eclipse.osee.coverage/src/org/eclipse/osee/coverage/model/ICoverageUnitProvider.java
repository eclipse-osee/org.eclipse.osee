/*
 * Created on Oct 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageUnitProvider {

   public List<CoverageUnit> getCoverageUnits();

   public void addCoverageUnit(CoverageUnit coverageUnit);

   public void removeCoverageUnit(CoverageUnit coverageUnit);

}
