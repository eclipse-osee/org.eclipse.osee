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
package org.eclipse.osee.coverage.test.package1;

import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.test.import1.CoverageImportTest1Blam;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackage1Import2 extends XNavigateItemAction {

   public CoveragePackage1Import2() {
      super(null, "");
   }

   public CoveragePackage1Import2(XNavigateItem parent) {
      super(parent, "Open Coverage Package 1 - Import 2");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoveragePackageArtifactListDialog dialog =
            new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
      dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts());
      if (dialog.open() != 0) return;
      Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
      CoveragePackage coveragePackage = OseeCoveragePackageStore.get(coveragePackageArtifact);
      CoverageEditor.open(new CoverageEditorInput(coveragePackage));
      // Process Import 1
      CoverageEditor editor = null;
      for (CoverageEditor coverageEditor : CoverageEditor.getEditors()) {
         if (coverageEditor.getCoverageEditorInput().getCoveragePackageBase() instanceof CoveragePackage) {
            CoveragePackage editorPackage =
                  (CoveragePackage) coverageEditor.getCoverageEditorInput().getCoveragePackageBase();
            if (editorPackage.getGuid().equals(coveragePackage.getGuid())) {
               editor = coverageEditor;
            }
         }
      }
      if (editor == null) {
         AWorkbench.popup("Can't access opened Editor");
         return;
      }
      editor.simulateImport(CoverageImportTest1Blam.NAME);
   }
}
