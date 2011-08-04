/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Validate that all coverage objects valid order numbers. Not duplicates and no missing numbers. Add error to list that
 * will be showed on overview.
 * 
 * @author Donald G. Dunne
 */
public class CoveragePackageOrderValidator {

   private final CoveragePackageBase coveragePackageBase;
   private final XResultData rd;

   public CoveragePackageOrderValidator(CoveragePackageBase coveragePackageBase, XResultData rd) {
      super();
      this.coveragePackageBase = coveragePackageBase;
      this.rd = rd;
   }

   public void run() {
      List<String> orderErrors = new ArrayList<String>();
      validateCoverageOrderNums(orderErrors, coveragePackageBase.getCoverageUnits());
      if (orderErrors.isEmpty()) {
         rd.log(AHTML.newline() + AHTML.bold("Validation: ") + "Ok");
      } else {
         rd.log(AHTML.newline() + AHTML.bold("Validation: ") + AHTML.newline());
         for (String str : orderErrors) {
            rd.logError(str);
         }
      }
   }

   private void validateCoverageOrderNums(List<String> orderErrors, List<CoverageUnit> coverageUnits) {
      for (CoverageUnit coverageUnit : coverageUnits) {

         // validate coverage units
         List<String> orderNums = new ArrayList<String>();
         for (CoverageUnit unit : coverageUnit.getCoverageUnits()) {
            if (Strings.isValid(unit.getOrderNumber())) {
               orderNums.add(unit.getOrderNumber());
            }
         }
         if (!orderNums.isEmpty()) {
            validateNumbers("child units", orderErrors, orderNums, coverageUnit);
         }
         // validate coverage items
         orderNums.clear();
         for (CoverageItem item : coverageUnit.getCoverageItems()) {
            if (Strings.isValid(item.getOrderNumber())) {
               orderNums.add(item.getOrderNumber());
            }
         }
         if (!orderNums.isEmpty()) {
            validateNumbers("child items", orderErrors, orderNums, coverageUnit);
         }

         // process children coverage units
         for (CoverageUnit unit : coverageUnit.getCoverageUnits()) {
            validateCoverageOrderNums(orderErrors, unit.getCoverageUnits());
         }
      }
   }

   private void validateNumbers(String name, List<String> orderErrors, List<String> orderNums, ICoverage coverage) {
      int maxNum = 0;
      Map<Integer, Boolean> maxNumToFound = new HashMap<Integer, Boolean>();
      for (String number : orderNums) {
         int orderNum = new Integer(number);
         if (orderNum > maxNum) {
            maxNum = orderNum;
         }
         if (maxNumToFound.containsKey(orderNum)) {
            orderErrors.add(String.format("Found duplicate [%s] order num [%s] for %s", name, orderNum,
               coverage.toStringNoPackage()));
         }
         maxNumToFound.put(orderNum, true);
      }
      for (int x = 1; x <= orderNums.size(); x++) {
         if (!maxNumToFound.containsKey(x)) {
            orderErrors.add(String.format("[%s] order num [%s] not found for %s", name, x, coverage.toStringNoPackage()));
         }
      }
   }

}
