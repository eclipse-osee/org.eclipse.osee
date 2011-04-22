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
package org.eclipse.osee.coverage.import02;

import java.util.Arrays;
import org.eclipse.osee.coverage.util.CoverageImportTestBlam;

/**
 * @author Donald G. Dunne
 */
public class CoverageImport2TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 2";

   public CoverageImport2TestBlam() {
      super(NAME, Arrays.asList(
         //
         "import01/com/screenA/ComScrnAButton1.java",
         "import01/com/screenA/ComScrnAButton2.java",
         "import02/com/screenA/ComScrnAButton3.java",
         //
         "import01/com/screenB/ScreenBButton1.java", "import01/com/screenB/ScreenBButton2.java",
         "import01/com/screenB/ScreenBButton3.java",
         //
         "import01/epu/PowerUnit1.java", "import01/epu/PowerUnit2.java", "import02/epu/PowerUnit3.java",
         //
         "import01/apu/AuxPowerUnit1.java", "import01/apu/AuxPowerUnit2.java",
         //
         "import01/nav/NavigationButton1.java", "import01/nav/NavigationButton2.java",
         "import01/nav/NavigationButton3.java"
      //
      ));
   }

}