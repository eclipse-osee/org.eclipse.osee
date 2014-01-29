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
package org.eclipse.osee.coverage.demo.examples.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.demo.examples.CoveragePackage1;
import org.eclipse.osee.coverage.demo.examples.import01.CoveragePackage1Import1;
import org.eclipse.osee.coverage.demo.examples.import01.CoveragePackage1Import1B;
import org.eclipse.osee.coverage.demo.examples.import02.CoveragePackage1Import2;
import org.eclipse.osee.coverage.demo.examples.import03.CoveragePackage1Import3;
import org.eclipse.osee.coverage.demo.examples.import04.CoveragePackage1Import4;
import org.eclipse.osee.coverage.demo.examples.import05.CoveragePackage1Import5;
import org.eclipse.osee.coverage.demo.examples.import06.CoveragePackage1Import6;
import org.eclipse.osee.coverage.demo.examples.import07.CoveragePackage1Import7;
import org.eclipse.osee.coverage.demo.examples.import08.CoveragePackage1Import8;
import org.eclipse.osee.coverage.demo.examples.import09.CoveragePackage1Import9;
import org.eclipse.osee.coverage.demo.examples.import10.CoveragePackage1Import10;
import org.eclipse.osee.coverage.demo.examples.import10.CoveragePackage1Import10a;
import org.eclipse.osee.coverage.demo.examples.import11.CoveragePackage1Import11;
import org.eclipse.osee.coverage.demo.examples.import11.CoveragePackage1Import11b;
import org.eclipse.osee.coverage.demo.examples.import12.CoveragePackage1Import12;
import org.eclipse.osee.coverage.demo.examples.import13.CoveragePackage1Import13;
import org.eclipse.osee.coverage.navigate.ICoverageNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * @author Donald G. Dunne
 */
public class CoverageExampleNavigateItems implements ICoverageNavigateItem {

   public CoverageExampleNavigateItems() {
      super();
   }

   @Override
   public List<XNavigateItem> getNavigateItems() {

      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (DbConnectionUtility.areOSEEServicesAvailable().isFalse()) {
         return items;
      }
      items.add(new CoveragePackage1(null));
      items.add(new CoveragePackage1Import1(null));
      items.add(new CoveragePackage1Import1B(null));
      items.add(new CoveragePackage1Import2(null));
      items.add(new CoveragePackage1Import3(null));
      items.add(new CoveragePackage1Import4(null));
      items.add(new CoveragePackage1Import5(null));
      items.add(new CoveragePackage1Import6(null));
      items.add(new CoveragePackage1Import7(null));
      items.add(new CoveragePackage1Import8(null));
      items.add(new CoveragePackage1Import9(null));
      items.add(new CoveragePackage1Import10a(null));
      items.add(new CoveragePackage1Import10(null));
      items.add(new CoveragePackage1Import11(null));
      items.add(new CoveragePackage1Import11b(null));
      items.add(new CoveragePackage1Import12(null));
      items.add(new CoveragePackage1Import13(null));
      items.add(new CreateWorkProductNavigateItemAction(null));

      return items;
   }

}
