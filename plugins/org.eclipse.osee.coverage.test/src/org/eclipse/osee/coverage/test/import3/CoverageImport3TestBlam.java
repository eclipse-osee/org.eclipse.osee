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
package org.eclipse.osee.coverage.test.import3;

import java.util.Arrays;
import org.eclipse.osee.coverage.test.util.CoverageImportTestBlam;

/**
 * Imports new method initAdded to end of PowerUnit1
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport3TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 3";

   public CoverageImport3TestBlam() {
      super(NAME, Arrays.asList(
      //
            "import3/epu/PowerUnit1.java", "import1/epu/PowerUnit2.java", "import2/epu/PowerUnit3.java"
      //
      ));
   }

}