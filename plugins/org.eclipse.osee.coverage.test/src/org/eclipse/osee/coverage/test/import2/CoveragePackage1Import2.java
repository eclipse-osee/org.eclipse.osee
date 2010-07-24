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
package org.eclipse.osee.coverage.test.import2;

import org.eclipse.osee.coverage.test.util.CpSelectAndImportItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import2 extends CpSelectAndImportItem {

   public CoveragePackage1Import2(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 2 - Add epu.PowerUnit3", CoverageImport2TestBlam.NAME);
   }

}
