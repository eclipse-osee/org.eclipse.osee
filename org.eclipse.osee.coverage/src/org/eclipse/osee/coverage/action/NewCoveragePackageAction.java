/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class NewCoveragePackageAction extends Action {

   public static OseeImage OSEE_IMAGE = CoverageImage.COVERAGE_PACKAGE;

   public NewCoveragePackageAction() {
      super("Create New Coverage Package");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(OSEE_IMAGE);
   }

   @Override
   public void run() {
      try {
         if (!CoverageUtil.getBranchFromUser(false)) return;
         EntryDialog dialog = new EntryDialog(getText(), "Enter Coverage Package Name");
         if (dialog.open() == 0) {
            CoveragePackage coveragePackage =
                  new CoveragePackage(dialog.getEntry(), CoverageOptionManagerDefault.instance());
            SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Add Coverage Package");
            OseeCoveragePackageStore.get(coveragePackage).save(transaction);
            transaction.execute();
            CoverageEditor.open(new CoverageEditorInput(dialog.getEntry(),
                  OseeCoveragePackageStore.get(coveragePackage).getArtifact(false), coveragePackage, false));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
