/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorWorkProductTab;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class OpenMultipleWorkProductsAction extends Action {

   private KeyedImage image = null;
   private final CoverageEditor coverageEditor;
   private final CoverageEditorWorkProductTab coverageEditorWorkProductTab;

   public OpenMultipleWorkProductsAction(CoverageEditor coverageEditor, CoverageEditorWorkProductTab coverageEditorWorkProductTab) {
      super("Create Work Product Task");
      this.coverageEditor = coverageEditor;
      this.coverageEditorWorkProductTab = coverageEditorWorkProductTab;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      if (image == null) {
         IOseeCmService service = SkynetGuiPlugin.getInstance().getOseeCmService();
         if (service != null) {
            image = service.getOpenImage(OseeCmEditor.CmMultiPcrEditor);
         }
      }
      if (image == null) {
         return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
      }
      return ImageManager.getImageDescriptor(image);
   }

   @Override
   public void run() {
      if (coverageEditorWorkProductTab.getWorkProductArtifacts().isEmpty()) {
         AWorkbench.popup("No Work Products to open");
         return;
      }
      IOseeCmService service = SkynetGuiPlugin.getInstance().getOseeCmService();
      service.openArtifacts(coverageEditor.getTitle() + " - Work Products",
         coverageEditorWorkProductTab.getWorkProductArtifacts(), OseeCmEditor.CmMultiPcrEditor);
   }
}
