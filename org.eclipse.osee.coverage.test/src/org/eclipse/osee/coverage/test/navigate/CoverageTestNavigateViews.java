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
package org.eclipse.osee.coverage.test.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.navigate.ICoverageNavigateItem;
import org.eclipse.osee.coverage.test.import1.CoveragePackage1Import1;
import org.eclipse.osee.coverage.test.import1.CoveragePackage1Import1B;
import org.eclipse.osee.coverage.test.import2.CoveragePackage1Import2;
import org.eclipse.osee.coverage.test.import3.CoveragePackage1Import3;
import org.eclipse.osee.coverage.test.import4.CoveragePackage1Import4;
import org.eclipse.osee.coverage.test.package1.CoveragePackage1;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestNavigateViews implements ICoverageNavigateItem {

   public CoverageTestNavigateViews() {
      super();
   }

   public List<XNavigateItem> getNavigateItems() throws OseeCoreException {

      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (Activator.areOSEEServicesAvailable().isFalse()) {
         return items;
      }

      //      items.add(new CoverageImportTestNavigateItem(null, new CoverageImport1TestBlam()));
      //      items.add(new CoverageImportTestNavigateItem(null, new CoverageImport2TestBlam()));
      //      items.add(new CoverageImportTestNavigateItem(null, new CoverageImport3TestBlam()));
      items.add(new CoveragePackage1(null));
      items.add(new CoveragePackage1Import1(null));
      items.add(new CoveragePackage1Import1B(null));
      items.add(new CoveragePackage1Import2(null));
      items.add(new CoveragePackage1Import3(null));
      items.add(new CoveragePackage1Import4(null));

      return items;
   }

}
