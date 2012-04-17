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
package org.eclipse.osee.coverage.import13;

import java.util.Arrays;
import org.eclipse.osee.coverage.util.CoverageImportTestBlam;

/**
 * Import coverage unit with invalid duplicate method names to test unmergable but overwrite case <br>
 * <br>
 *
 * @author Donald G. Dunne
 */
public class CoverageImport13TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 13";

   public CoverageImport13TestBlam() {
      super(NAME, Arrays.asList(
      //
      "import13/ase/Survive1.java" //
      //
      ));
   }

}