/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.demo;

/**
 * @author Roberto E. Escobar
 */
public enum CoverageExamples {
   COVERAGE_IMPORT_01,
   COVERAGE_IMPORT_02,
   COVERAGE_IMPORT_03,
   COVERAGE_IMPORT_04,
   COVERAGE_IMPORT_05,
   COVERAGE_IMPORT_06,
   COVERAGE_IMPORT_07,
   COVERAGE_IMPORT_08,
   COVERAGE_IMPORT_09,
   COVERAGE_IMPORT_10,
   COVERAGE_IMPORT_11,
   COVERAGE_IMPORT_11B,
   COVERAGE_IMPORT_12,
   COVERAGE_IMPORT_13;

   public String getExtensionId() {
      return String.format("%s.%s", getClass().getPackage().getName(), this.name());
   }

}
