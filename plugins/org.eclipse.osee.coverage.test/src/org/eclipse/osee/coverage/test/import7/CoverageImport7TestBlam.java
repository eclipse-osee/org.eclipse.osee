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
package org.eclipse.osee.coverage.test.import7;

import java.util.Arrays;
import org.eclipse.osee.coverage.test.util.CoverageImportTestBlam;

/**
 * Delete PowerUnit2.clear
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport7TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 7";

   public CoverageImport7TestBlam() {
      super(NAME, Arrays.asList(
      //
         "import7/apu/AuxPowerUnit1.java", "import1/apu/AuxPowerUnit2.java"
      //
      ));
   }

}