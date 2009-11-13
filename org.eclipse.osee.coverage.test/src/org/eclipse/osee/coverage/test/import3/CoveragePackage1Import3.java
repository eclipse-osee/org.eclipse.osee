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
package org.eclipse.osee.coverage.test.import3;

import org.eclipse.osee.coverage.test.util.CpSelectAndImportItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * Imports two completely new file CoverageUnits PowerUnit1 and ComScrnAButton1
 * 
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import3 extends CpSelectAndImportItem {

   public CoveragePackage1Import3(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 3 - Add PowerUnit1.initAdded to end", CoverageImport3TestBlam.NAME);
   }

}
