/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Allow external mechnism for test units to be provided to UI
 * 
 * @author Donald G. Dunne
 */
public interface ITestUnitProvider {

   public Collection<String> getTestUnits(CoverageItem coverageItem) throws OseeCoreException;

   public void addTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException;

   public void setTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException;

   public String toXml(CoverageItem coverageItem);

   public void fromXml(CoverageItem coverageItem, String xml);
}
