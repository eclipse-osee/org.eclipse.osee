/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Simple provider that optimizes how test units are stored by sharing test unit names.
 * 
 * @author Donald G. Dunne
 */
public class SimpleTestUnitProvider implements ITestUnitProvider {

   // Since test units will cover many coverage items (sometimes thousands), it is more cost effective
   // to store single test script name shared by use of string.intern() rather than
   // create a new string for each coverage item.
   final HashCollection<CoverageItem, String> coverageItemToTestUnits = new HashCollection<CoverageItem, String>(1000);

   public SimpleTestUnitProvider() {
   }

   @Override
   public void addTestUnitName(CoverageItem coverageItem, String testUnitName) {
      if (!getTestUnits(coverageItem).contains(testUnitName)) {
         coverageItemToTestUnits.put(coverageItem, Strings.intern(testUnitName));
      }
   }

   @Override
   public Collection<String> getTestUnits(CoverageItem coverageItem) {
      if (coverageItemToTestUnits.containsKey(coverageItem)) {
         return coverageItemToTestUnits.getValues(coverageItem);
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public String toXml(CoverageItem coverageItem) {
      return Collections.toString(";", getTestUnits(coverageItem));
   }

   @Override
   public void fromXml(CoverageItem coverageItem, String testUnitNames) {
      if (Strings.isValid(testUnitNames)) {
         for (String testName : testUnitNames.split(";")) {
            addTestUnitName(coverageItem, testName);
         }
      }
   }

}
