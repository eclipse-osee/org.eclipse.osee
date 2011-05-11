/*
 * Created on Apr 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.import10;

import org.eclipse.osee.coverage.CoveragePackageImportTest;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import10a extends XNavigateItem {

   public CoveragePackage1Import10a(XNavigateItem parent) {
      super(parent, "Open CP 1 - Import 10a - Setup coverage methods for Import 10", null);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      super.run(tableLoadOptions);

      CoverageUtil.setNavigatorSelectedBranch(CoverageTestUtil.getTestBranch());
      Artifact coveragePackageArtifact = CoverageTestUtil.getSelectedCoveragePackageFromDialog();
      if (coveragePackageArtifact == null) {
         return;
      }
      CoveragePackage coveragePackage = new OseeCoveragePackageStore(coveragePackageArtifact).getCoveragePackage();

      Result result = CoveragePackageImportTest.setupCoveragePackageForImport10(coveragePackage);
      if (result.isFalse()) {
         AWorkbench.popup(result);
      }

      CoverageEditor.open(new CoverageEditorInput(coveragePackage.getName(), coveragePackageArtifact, coveragePackage,
         true));

   }

}
