/*
 * Created on Oct 6, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.navigate;

import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.util.CoverageEditorItemListDialog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class DeleteCoveragePackageItem extends XNavigateItem {

   public DeleteCoveragePackageItem(XNavigateItem parent) {
      super(parent, "Delete/Purge Coverage Package", FrameworkImage.DELETE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoverageEditorItemListDialog dialog = new CoverageEditorItemListDialog("Delete Package", "Select Package");
      dialog.setInput(CoverageManager.getCoveragePackages());
      if (dialog.open() == 0) {
         CoveragePackage coveragePackage = (CoveragePackage) dialog.getResult()[0];
         CheckBoxDialog cDialog =
               new CheckBoxDialog(
                     "Delete/Purge Package",
                     String.format(
                           "This will delete Coverage Package and all realted Coverage Units and Test Units.\n\nDelete/Purge Package [%s]?",
                           coveragePackage.getName()), "Purge");
         if (cDialog.open() == 0) {
            boolean purge = cDialog.isChecked();
            SkynetTransaction transaction = null;
            if (!purge) transaction = new SkynetTransaction(BranchManager.getCommonBranch());
            coveragePackage.delete(transaction, purge);
            if (!purge) transaction.execute();
         }
      }
   }
}
