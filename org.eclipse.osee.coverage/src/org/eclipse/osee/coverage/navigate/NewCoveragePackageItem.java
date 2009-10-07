/*
 * Created on Oct 6, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.navigate;

import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class NewCoveragePackageItem extends XNavigateItem {

   public NewCoveragePackageItem(XNavigateItem parent) {
      super(parent, "Create New Coverage Package", CoverageImage.COVERAGE_PACKAGE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      EntryDialog dialog = new EntryDialog(getName(), "Enter Coverage Package Name");
      if (dialog.open() == 0) {
         CoveragePackage coveragePackage = new CoveragePackage(dialog.getEntry());
         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
         coveragePackage.save(transaction);
         transaction.execute();
         CoverageEditor.open(new CoverageEditorInput(coveragePackage));
      }
   }
}
