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
package org.eclipse.osee.coverage.demo.examples.import12;

import java.util.Arrays;
import org.eclipse.osee.coverage.demo.examples.CoverageImportTestBlam;

/**
 * Import ase.Survive1 as prep for import13 case where non-unique method name <br>
 * <br>
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport12TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 12";

   public CoverageImport12TestBlam() {
      super(NAME, Arrays.asList(
      //
      "import12/ase/Survive1.java" //
      //
      ));
   }

}