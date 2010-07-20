/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Donald G. Dunne
 */
public class SimpleCoverageUnitFileContentsProvider implements ICoverageUnitFileContentsProvider {

   Map<CoverageUnit, String> unitToContents = new HashMap<CoverageUnit, String>(1000);

   @Override
   public String getFileContents(CoverageUnit coverageUnit) {
      return unitToContents.get(coverageUnit);
   }

   @Override
   public void setFileContents(CoverageUnit coverageUnit, String fileContents) {
      unitToContents.put(coverageUnit, fileContents);
   }

}
