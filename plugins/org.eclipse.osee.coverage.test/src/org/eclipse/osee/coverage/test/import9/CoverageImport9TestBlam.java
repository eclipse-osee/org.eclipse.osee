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
package org.eclipse.osee.coverage.test.import9;

import java.util.Arrays;
import org.eclipse.osee.coverage.test.util.CoverageImportTestBlam;

/**
 * Method update for NavigationButton2.getImage.line2 and line5
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport9TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 9";

   public CoverageImport9TestBlam() {
      super(NAME, Arrays.asList(
            //
            "import5/nav/NavigationButton1.java", "import9/nav/NavigationButton2.java",
            "import1/nav/NavigationButton3.java"
      //
      ));
   }

}