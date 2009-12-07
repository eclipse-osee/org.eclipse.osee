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
package org.eclipse.osee.coverage.test.util;

import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class CpSelectAndImportItem extends XNavigateItemAction {

   private final String blamImportName;

   public CpSelectAndImportItem(XNavigateItem parent, String name, String blamImportName) {
      super(parent, name);
      this.blamImportName = blamImportName;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoverageUtil.getBranchFromUser(false);
      CoveragePackageArtifactListDialog dialog =
            new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
      dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts());
      if (dialog.open() != 0) return;
      Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
      CoveragePackage coveragePackage = OseeCoveragePackageStore.get(coveragePackageArtifact);
      CoverageEditor.open(new CoverageEditorInput(coveragePackage.getName(), coveragePackageArtifact, coveragePackage,
            true));
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
      editor.simulateImport(blamImportName);
   }
}
