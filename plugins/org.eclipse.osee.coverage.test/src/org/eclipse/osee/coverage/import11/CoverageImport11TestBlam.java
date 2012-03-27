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
package org.eclipse.osee.coverage.import11;

import java.util.Arrays;
import org.eclipse.osee.coverage.util.CoverageImportTestBlam;

/**
 * Test that adding a top level coverage unit (not under a package) works; import12 will delete it. <br>
 * <br>
 *
 * @author Donald G. Dunne
 */
public class CoverageImport11TestBlam extends CoverageImportTestBlam {

   public static String NAME = "Test Import 10";

   public CoverageImport11TestBlam() {
      super(NAME, Arrays.asList(
      //
      "import11/TopLevelButton1.java"
      //
      ));
   }

}