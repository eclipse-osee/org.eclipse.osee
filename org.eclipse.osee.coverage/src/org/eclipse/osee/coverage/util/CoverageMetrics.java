/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;

/**
 * @author Donald G. Dunne
 */
public class CoverageMetrics {

   public static int getPercentCoverage(Collection<CoverageUnit> coverageUnits) {
      int numItems = 0;
      int numCovered = 0;
      for (CoverageUnit coverageUnit : coverageUnits) {
         for (CoverageItem coverageItem : coverageUnit.getCoverageItems(true)) {
            numItems++;
            numCovered += coverageItem.getCoverageMethod() == CoverageMethodEnum.None ? 0 : 1;
         }
      }
      if (numItems == 0 || numCovered == 0) {
         return 0;
      }
      return new Integer(numItems / numCovered * 100).intValue();
   }
}
