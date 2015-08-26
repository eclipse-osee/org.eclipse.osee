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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Validate that all coverage items have a valid coverage method
 *
 * @author Donald G. Dunne
 */
public class CoverageMethodValidator {

   private final CoveragePackageBase coveragePackageBase;
   private final XResultData rd;

   public CoverageMethodValidator(CoveragePackageBase coveragePackageBase, XResultData rd) {
      super();
      this.coveragePackageBase = coveragePackageBase;
      this.rd = rd;
   }

   public void run() {
      List<String> orderErrors = new ArrayList<String>();
      validateCoverageMethods(orderErrors, coveragePackageBase.getCoverageUnits());
      if (orderErrors.isEmpty()) {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Method Validation: ") + "Ok");
      } else {
         rd.log(AHTML.newline() + AHTML.bold("Coverage Method Validation: ") + AHTML.newline());
         for (String str : orderErrors) {
            rd.error(str);
         }
      }
   }

   private void validateCoverageMethods(List<String> orderErrors, Collection<? extends ICoverage> children) {
      for (ICoverage child : children) {

         if (child instanceof CoverageItem) {
            CoverageItem items = (CoverageItem) child;
            if (items.getCoverageMethod() == null) {
               orderErrors.add(String.format("Coverage Items with null Coverage Method [%s]", items.toString()));
            }
         }

         // process children coverage units
         validateCoverageMethods(orderErrors, child.getChildren());
      }
   }

}
