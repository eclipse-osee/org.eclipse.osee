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
package org.eclipse.osee.coverage.demo.examples.import11;

import java.util.Arrays;
import org.eclipse.osee.coverage.demo.examples.CoverageImportTestBlam;

/**
 * Delete NavigationButton3 coverage unit <br>
 * <br>
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport11bTestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 11b";

   public CoverageImport11bTestBlam() {
      super(NAME, Arrays.asList(
      //
         "import11/cnd/DisplayButton1.java", "import11/cnd/DisplayButton3.java", //
         "import11/cnd/disp1/Page2.java"//
      //
      ));
   }
}