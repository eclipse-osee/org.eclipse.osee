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
package org.eclipse.osee.coverage.demo.examples.import10;

import java.util.Arrays;
import org.eclipse.osee.coverage.demo.examples.CoverageImportTestBlam;

/**
 * Improvement to resolving coverage method differences<br>
 * - If coverage item CM is Test_Unit and import item is Not_Covered, overwrite with Not_Covered <br>
 * - If coverage item CM is a custom disposition and import is Not_Covered, do NOT overwrite coverage item <br>
 * - If coverage item CM is a custom disposition and import is Test_Unit, overwrite with Test_Unit and clear out
 * rationale <br>
 * <br>
 * See comments in NavigationButton2.java file for details
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport10TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 10";

   public CoverageImport10TestBlam() {
      super(NAME, Arrays.asList(
         //
         "import05/nav/NavigationButton1.java", "import10/nav/NavigationButton2.java",
         "import01/nav/NavigationButton3.java"
      //
      ));
   }

}