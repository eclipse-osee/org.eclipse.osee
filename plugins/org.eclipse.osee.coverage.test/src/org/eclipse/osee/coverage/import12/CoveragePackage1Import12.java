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
package org.eclipse.osee.coverage.import12;

import org.eclipse.osee.coverage.util.CpSelectAndImportItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * Import ase.Survive1 as prep for import13 case where non-unique method name <br>
 * <br>
 *
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import12 extends CpSelectAndImportItem {

   public CoveragePackage1Import12(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 12 - Create ase package", CoverageImport12TestBlam.NAME);
   }

}
