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
public interface ICoverageItemProvider {

   public List<CoverageItem> getCoverageItems();

   public void addCoverageItem(CoverageItem coverageItem);

   public void removeCoverageItem(CoverageItem coverageItem);

}
