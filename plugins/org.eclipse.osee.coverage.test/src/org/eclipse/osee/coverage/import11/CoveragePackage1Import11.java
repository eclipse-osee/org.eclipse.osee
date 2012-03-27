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

import org.eclipse.osee.coverage.util.CpSelectAndImportItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * Test that adding a top level coverage unit (not under a package) works; import12 will delete it. <br>
 * <br>
 * See comments in NavigationButton2.java file for details
 *
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import11 extends CpSelectAndImportItem {

   public CoveragePackage1Import11(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 11 - Add top level coverage unit (no package)", CoverageImport11TestBlam.NAME);
   }

}
