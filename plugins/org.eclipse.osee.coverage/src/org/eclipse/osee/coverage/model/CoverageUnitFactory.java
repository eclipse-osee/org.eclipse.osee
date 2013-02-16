/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import org.eclipse.osee.framework.jdk.core.util.GUID;

public class CoverageUnitFactory {

   public static CoverageUnit createCoverageUnit(ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      return createCoverageUnit(GUID.create(), parent, name, location, coverageUnitFileContentsProvider);

   }

   public static CoverageUnit createCoverageUnit(String guid, ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider) {
      return createCoverageUnit(GUID.create(), parent, name, location, coverageUnitFileContentsProvider, true);

   }

   public static CoverageUnit createCoverageUnit(String guid, ICoverage parent, String name, String location, ICoverageUnitFileContentsProvider coverageUnitFileContentsProvider, boolean addToParent) {
      CoverageUnit unit = new CoverageUnit(guid, parent, name, location, coverageUnitFileContentsProvider);
      if (addToParent) {
         if (parent != null && parent instanceof ICoverageUnitProvider) {
            ((ICoverageUnitProvider) parent).addCoverageUnit(unit);
         }
      }
      return unit;
   }

}
