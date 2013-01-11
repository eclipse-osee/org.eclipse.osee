/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.demo.examples.import10;

import org.eclipse.osee.coverage.demo.CoverageBranches;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
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

      CoverageUtil.setNavigatorSelectedBranch(CoverageBranches.COVERAGE_TEST_BRANCH);
      Artifact coveragePackageArtifact = CoveragePackageTestUtil.getSelectedCoveragePackageFromDialog();
      if (coveragePackageArtifact == null) {
         return;
      }
      CoveragePackage coveragePackage = new OseeCoveragePackageStore(coveragePackageArtifact).getCoveragePackage();

      Result result = CoveragePackageTestUtil.setupCoveragePackageForImport10(coveragePackage, true);
      if (result.isFalse()) {
         AWorkbench.popup(result);
      }
      CoverageEditor.open(new CoverageEditorInput(coveragePackage.getName(), coveragePackageArtifact, coveragePackage,
         true));

   }

}
