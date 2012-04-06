/*
 * Created on Apr 5, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import org.eclipse.osee.framework.jdk.core.util.GUID;

public class CoverageUnitFactory {

   public static CoverageUnit createCoverageUnit(ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      return createCoverageUnit(GUID.create(), parent, name, location, coverageUnitFileContentsProvider);

   }

   public static CoverageUnit createCoverageUnit(String guid, ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      return createCoverageUnit(GUID.create(), parent, name, location, coverageUnitFileContentsProvider, true);

   }

   public static CoverageUnit createCoverageUnit(String guid, ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider, boolean addToParent) {
      CoverageUnit unit = new CoverageUnit(guid, parent, name, location, coverageUnitFileContentsProvider);
      if (addToParent) {
         if (parent != null && parent instanceof ICoverageUnitProvider) {
            ((ICoverageUnitProvider) parent).addCoverageUnit(unit);
         }
      }
      return unit;
   }

}
