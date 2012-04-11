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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
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
      List<String> orderErrors = new ArrayList<String>();
      for (CoverageUnit unit : coveragePackageBase.getCoverageUnits()) {
         validateCoverageMethods(orderErrors, unit);
      }
      if (orderErrors.isEmpty()) {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Unit Child Name Validation: ") + "Ok");
      } else {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Unit Child Name Validation: ") + AHTML.newline());
         for (String str : orderErrors) {
            rd.logError(str);
         }
      }
   }

   private void validateCoverageMethods(List<String> orderErrors, CoverageUnit coverageUnit) {
      Set<String> names = new HashSet<String>();
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         if (names.contains(childCoverageUnit.getName())) {
            orderErrors.add(String.format("Coverage Unit children with same name for parent [%s] children named [%s]",
               coverageUnit.toString(), childCoverageUnit.getName()));
         }
         names.add(childCoverageUnit.getName());

         // process children coverage units
         validateCoverageMethods(orderErrors, childCoverageUnit);
      }
   }
}
