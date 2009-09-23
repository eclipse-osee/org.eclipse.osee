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
package org.eclipse.osee.coverage.test.import1;

import java.net.URL;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.test.SampleJavaFileParser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestImporter1 extends XNavigateItemAction implements ICoverageImporter {

   public static String PATH = "../../../../../../../src/org/eclipse/osee/coverage/test/import1/";

   public CoverageTestImporter1() {
      super(null, "");
   }

   public CoverageTestImporter1(XNavigateItem parent) {
      super(parent, "Coverage Test Importer1");
   }

   @Override
   public CoverageImport run() {
      CoverageImport coverageImport = new CoverageImport();
      try {
         URL url = CoverageTestImporter1.class.getResource(PATH + "NavigationButton1.java");
         coverageImport.addCoverageUnit(SampleJavaFileParser.createCodeUnit(url));
      } catch (OseeCoreException ex) {
         OseeLog.log(CoveragePlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return coverageImport;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoverageManager.importCoverage(new CoverageTestImporter1());
   }

}
