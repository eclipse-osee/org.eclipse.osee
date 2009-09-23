/*
 * Created on Sep 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.import1;

import org.eclipse.osee.coverage.CoverageImport;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
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
         coverageImport.addCoverageUnit(SampleJavaFileParser.createCodeUnit("NavigationButton1.java"));
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
