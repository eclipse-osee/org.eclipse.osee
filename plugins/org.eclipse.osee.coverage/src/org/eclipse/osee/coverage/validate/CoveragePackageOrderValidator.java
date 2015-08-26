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
import org.eclipse.osee.framework.core.util.XResultData.Type;
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
      XResultData tempRd = new XResultData(false);
      for (CoverageUnit unit : coveragePackageBase.getCoverageUnits()) {
         validate(tempRd, unit, true);
      }
      if (tempRd.isErrors()) {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Package Order Validation: ") + AHTML.newline());
         rd.addRaw(tempRd.toString());
         rd.bumpCount(Type.Severe, tempRd.getNumErrors());
         rd.bumpCount(Type.Info, tempRd.getNumWarnings());
      } else {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Package Order Validation: ") + "Ok");
      }
   }

   public static XResultData validate(XResultData rd, CoverageUnit coverageUnit, boolean recurse) {

      // validate coverage units
      List<String> orderNums = new ArrayList<String>();
      for (CoverageUnit unit : coverageUnit.getCoverageUnits()) {
         if (Strings.isValid(unit.getOrderNumber())) {
            orderNums.add(unit.getOrderNumber());
         }
      }
      if (!orderNums.isEmpty()) {
         validateNumbers("child units", rd, orderNums, coverageUnit);
      }
      // validate coverage items
      orderNums.clear();
      for (CoverageItem item : coverageUnit.getCoverageItems()) {
         if (Strings.isValid(item.getOrderNumber())) {
            orderNums.add(item.getOrderNumber());
         }
      }
      if (!orderNums.isEmpty()) {
         validateNumbers("child items", rd, orderNums, coverageUnit);
      }

      if (recurse) {
         // process children coverage units
         for (CoverageUnit childUnit : coverageUnit.getCoverageUnits()) {
            validate(rd, childUnit, recurse);
         }
      }
      return rd;
   }

   private static void validateNumbers(String name, XResultData rd, List<String> orderNums, ICoverage coverage) {
      int maxNum = 0;
      Map<Integer, Boolean> maxNumToFound = new HashMap<Integer, Boolean>();
      for (String number : orderNums) {
         int orderNum = new Integer(number);
         if (orderNum > maxNum) {
            maxNum = orderNum;
         }
         if (maxNumToFound.containsKey(orderNum)) {
            rd.errorf("Found duplicate [%s] order num [%s] for %s", name, orderNum,
               coverage.toStringNoPackage());
         }
         maxNumToFound.put(orderNum, true);
      }
      for (int x = 1; x <= orderNums.size(); x++) {
         if (!maxNumToFound.containsKey(x)) {
            rd.errorf("[%s] order num [%s] not found for %s", name, x, coverage.toStringNoPackage());
         }
      }
   }

}
