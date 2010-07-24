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
package org.eclipse.osee.coverage.test.import6;

import java.util.Arrays;
import org.eclipse.osee.coverage.test.util.CoverageImportTestBlam;

/**
 * Delete PowerUnit2.clear
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport6TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 6";

   public CoverageImport6TestBlam() {
      super(NAME, Arrays.asList(
      //
         "import4/epu/PowerUnit1.java", "import6/epu/PowerUnit2.java", "import2/epu/PowerUnit3.java"
      //
      ));
   }

}