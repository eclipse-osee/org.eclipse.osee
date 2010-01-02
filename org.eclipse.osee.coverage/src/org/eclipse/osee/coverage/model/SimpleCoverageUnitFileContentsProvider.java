/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class SimpleCoverageUnitFileContentsProvider implements ICoverageUnitFileContentsProvider {

   Map<CoverageUnit, String> unitToContents = new HashMap<CoverageUnit, String>(1000);

   @Override
   public String getFileContents(CoverageUnit coverageUnit) {
      return unitToContents.get(coverageUnit);
   }

   @Override
   public void setFileContents(CoverageUnit coverageUnit, String fileContents) {
      unitToContents.put(coverageUnit, fileContents);
   }

}
