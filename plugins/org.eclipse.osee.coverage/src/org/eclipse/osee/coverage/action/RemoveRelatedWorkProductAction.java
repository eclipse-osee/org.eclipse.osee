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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class RemoveRelatedWorkProductAction extends Action {

   private final ISelectedArtifacts selectedArtifacts;
   private final CoverageEditor coverageEditor;

   public RemoveRelatedWorkProductAction(CoverageEditor coverageEditor, ISelectedArtifacts selectedArtifacts, IRefreshable refreshable) {
      super("Remove Related Work Product Action");
      this.coverageEditor = coverageEditor;
      this.selectedArtifacts = selectedArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DELETE);
   }

   @Override
   public void run() {
      if (selectedArtifacts.getSelectedArtifacts().isEmpty()) {
         AWorkbench.popup("Please select work product to remove");
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getText(),
         "Remove selected work product actions?")) {
         try {
            CoveragePackage coveragePackage = (CoveragePackage) coverageEditor.getCoveragePackageBase();
            OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, coverageEditor.getBranch());
            for (Artifact artifact : selectedArtifacts.getSelectedArtifacts()) {
               store.getArtifact(false).deleteRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact);
            }
            store.getArtifact(false).persist("Un-Relate Coverage work product Actions");

         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
