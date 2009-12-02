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
package org.eclipse.osee.coverage.test.import4;

import org.eclipse.osee.coverage.test.util.CpSelectAndImportItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * Imports deselectAdded to middle of epu.PowerUnit1
 * 
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import4 extends CpSelectAndImportItem {

   public CoveragePackage1Import4(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 4 - Add PowerUnit1.deselectAdded to middle", CoverageImport4TestBlam.NAME);
   }

}
