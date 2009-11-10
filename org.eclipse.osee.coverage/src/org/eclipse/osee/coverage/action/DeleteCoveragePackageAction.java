/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;

/**
 * @author Donald G. Dunne
 */
public class DeleteCoveragePackageAction extends Action {

   public static OseeImage OSEE_IMAGE = FrameworkImage.DELETE;

   public DeleteCoveragePackageAction() {
      super("Delete/Purge Coverage Package");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(OSEE_IMAGE);
   }

   @Override
   public void run() {
      try {
         if (!CoverageUtil.getBranchFromUser(false)) return;
         CoveragePackageArtifactListDialog dialog =
               new CoveragePackageArtifactListDialog("Delete Package", "Select Package");
         dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts());
         if (dialog.open() == 0) {
            Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
            CoveragePackage coveragePackage = OseeCoveragePackageStore.get(coveragePackageArtifact);
            CheckBoxDialog cDialog =
                  new CheckBoxDialog(
                        "Delete/Purge Package",
                        String.format(
                              "This will delete Coverage Package and all realted Coverage Units and Test Units.\n\nDelete/Purge Package [%s]?",
                              coveragePackage.getName()), "Purge");
            if (cDialog.open() == 0) {
               boolean purge = cDialog.isChecked();
               SkynetTransaction transaction = null;
               if (!purge) transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Delete Coverage Package");
               OseeCoverageStore.get(coveragePackage).delete(transaction, purge);
               if (!purge) transaction.execute();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
