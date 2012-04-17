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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Validate that all children CoverageUnits have different names
 *
 * @author Donald G. Dunne
 */
public class CoverageUnitChildNameValidator {

   private final CoveragePackageBase coveragePackageBase;
   private final XResultData rd;

   public CoverageUnitChildNameValidator(CoveragePackageBase coveragePackageBase, XResultData rd) {
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
         rd.log(AHTML.newline() + AHTML.bold("Coverage Unit Child Name Validation: ") + AHTML.newline());
         rd.addRaw(tempRd.toString());
         rd.bumpCount(XResultData.Type.Severe, tempRd.getNumErrors());
         rd.bumpCount(XResultData.Type.Info, tempRd.getNumWarnings());
      } else {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Unit Child Name Validation: ") + "Ok");
      }
   }

   public static XResultData validate(XResultData rd, CoverageUnit coverageUnit, boolean recurse) {
      return validate(new HashSet<String>(200), rd, coverageUnit, recurse);
   }

   public static XResultData validate(Set<String> uniqueResults, XResultData rd, CoverageUnit coverageUnit, boolean recurse) {

      Set<String> names = new HashSet<String>();
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         if (names.contains(childCoverageUnit.getName())) {
            String methodCount = CoverageUtil.getCoverageMethodCountStr("Method Count", coverageUnit);
            String errorStr =
               String.format("Coverage Unit children have same name [%s] for parent [%s]; [%s]",
                  childCoverageUnit.getName(), coverageUnit.toString(), methodCount);
            // Only handle error once
            if (!uniqueResults.contains(errorStr)) {
               rd.logError(errorStr);
               uniqueResults.add(errorStr);
            }
         }
         names.add(childCoverageUnit.getName());

         if (recurse) {
            // process children coverage units
            validate(rd, childCoverageUnit, recurse);
         }
      }
      return rd;
   }
}
