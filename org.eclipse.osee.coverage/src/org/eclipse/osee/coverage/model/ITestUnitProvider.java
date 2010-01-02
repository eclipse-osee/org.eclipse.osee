/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;

/**
 * Allow external mechnism for test units to be provided to UI
 * 
 * @author Donald G. Dunne
 */
public interface ITestUnitProvider {

   public Collection<String> getTestUnits(CoverageItem coverageItem);

   public void addTestUnitName(CoverageItem coverageItem, String testUnitName);

   public String toXml(CoverageItem coverageItem);

   public void fromXml(CoverageItem coverageItem, String xml);
}
