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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
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

      Map<String, List<CoverageUnit>> mapNameToCvgUnits = new HashMap<String, List<CoverageUnit>>();
      populateMapNameToCvgUnits(mapNameToCvgUnits, coverageUnit);

      List<String> errorStrs = new ArrayList<String>();
      for (List<CoverageUnit> cvgUnits : mapNameToCvgUnits.values()) {
         if (cvgUnits.size() > 1) {

            for (CoverageUnit cvgUnit : cvgUnits) {
               ICoverage parent = cvgUnit.getParent();
               String errorStr =
                  String.format(
                     "Methods with same name - File:[%s] &nbsp;&nbsp;&nbsp;&nbsp; Method:[%s] &nbsp;&nbsp;&nbsp;&nbsp; MethodNumber:[%s]",
                     (parent != null) ? parent.getName() : "", cvgUnit.getName(), cvgUnit.getOrderNumber());
               errorStrs.add(errorStr);
            }
         }
      }
      Collections.sort(errorStrs);

      for (String errorStr : errorStrs) {
         uniqueResults.add(errorStr);
         rd.error(errorStr);
      }

      return rd;
   }

   public static void populateMapNameToCvgUnits(Map<String, List<CoverageUnit>> mapNameToCvgUnits, CoverageUnit coverageUnit) {
      String name = coverageUnit.getName();
      ICoverage parent = coverageUnit.getParent();
      String parentName = parent.getName();
      String key = parentName + ":" + name;
      List<CoverageUnit> cvgUnits = mapNameToCvgUnits.get(key);
      if (cvgUnits == null) {
         cvgUnits = new ArrayList<CoverageUnit>();
         mapNameToCvgUnits.put(key, cvgUnits);
      }
      cvgUnits.add(coverageUnit);

      // process children coverage units
      for (CoverageUnit childCoverageUnit : coverageUnit.getCoverageUnits()) {
         populateMapNameToCvgUnits(mapNameToCvgUnits, childCoverageUnit);
      }
   }
}
