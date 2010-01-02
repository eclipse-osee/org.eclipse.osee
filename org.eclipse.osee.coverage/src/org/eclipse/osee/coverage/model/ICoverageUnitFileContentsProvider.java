/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

/**
 * Allow external mecahnism for coverage unit file contents to be provided to UI. This provides for late loading of
 * large amounts of data that isn't needed often.
 * 
 * @author Donald G. Dunne
 */
public interface ICoverageUnitFileContentsProvider {

   public String getFileContents(CoverageUnit coverageUnit);

   public void setFileContents(CoverageUnit coverageUnit, String fileContents);

}
