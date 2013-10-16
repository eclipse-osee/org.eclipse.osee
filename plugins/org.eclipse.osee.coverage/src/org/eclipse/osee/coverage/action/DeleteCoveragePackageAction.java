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
import org.eclipse.jface.window.Window;
import org.eclipse.osee.coverage.event.CoverageEventManager;
import org.eclipse.osee.coverage.event.CoverageEventType;
import org.eclipse.osee.coverage.event.CoveragePackageEvent;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class DeleteCoveragePackageAction extends Action {

   public static KeyedImage OSEE_IMAGE = FrameworkImage.DELETE;

   public DeleteCoveragePackageAction() {
      super("Delete Coverage Package");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(OSEE_IMAGE);
   }

   @Override
   public void run() {
      try {
         if (!CoverageUtil.getBranchFromUser(false)) {
            return;
         }
         IOseeBranch branch = CoverageUtil.getBranch();
         CoveragePackageArtifactListDialog dialog =
            new CoveragePackageArtifactListDialog("Delete Package", "Select Package");
         dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts(branch));
         if (dialog.open() == Window.OK) {
            if (dialog.getResult().length == 0) {
               AWorkbench.popup("Must select coverage package.");
               return;
            }
            Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
            CoveragePackage coveragePackage = OseeCoveragePackageStore.get(coveragePackageArtifact);
            MessageDialog cDialog =
               new MessageDialog(
                  Displays.getActiveShell(),
                  "Delete Package",
                  null,
                  String.format(
                     "This will delete Coverage Package and all related Coverage Units and Test Units.\n\nDelete Package [%s]?",
                     coveragePackage.getName()), MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
            if (cDialog.open() == Window.OK) {
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(branch, "Delete Coverage Package - " + coveragePackage.getName());
               CoveragePackageEvent coverageEvent =
                  new CoveragePackageEvent(coveragePackage, CoverageEventType.Deleted);
               OseeCoveragePackageStore.get(coveragePackage, branch).delete(transaction, coverageEvent, false);
               transaction.execute();
               CoverageEventManager.instance.sendRemoteEvent(coverageEvent);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
