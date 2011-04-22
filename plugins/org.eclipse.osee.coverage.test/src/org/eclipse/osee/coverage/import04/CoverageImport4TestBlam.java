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
package org.eclipse.osee.coverage.import04;

import java.util.Arrays;
import org.eclipse.osee.coverage.util.CoverageImportTestBlam;

/**
 * Imports change to epu.PowerUnit1, adding a new method
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport4TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 4";

   public CoverageImport4TestBlam() {
      super(NAME, Arrays.asList(
      //
         "import04/epu/PowerUnit1.java", "import01/epu/PowerUnit2.java", "import02/epu/PowerUnit3.java"
      //
      ));
   }

}