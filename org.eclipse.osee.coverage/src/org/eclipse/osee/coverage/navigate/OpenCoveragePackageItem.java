/*
 * Created on Oct 6, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.navigate;

import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.editor.ICoverageEditorProvider;
import org.eclipse.osee.coverage.util.CoverageEditorItemListDialog;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenCoveragePackageItem extends XNavigateItem {

   public OpenCoveragePackageItem(XNavigateItem parent) {
      super(parent, "Open Coverage Package", CoverageImage.COVERAGE_PACKAGE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoverageEditorItemListDialog dialog =
            new CoverageEditorItemListDialog("Open Coverage Package", "Select Coverage Package");
      dialog.setInput(CoverageManager.getCoveragePackages());
      if (dialog.open() == 0) {
         CoverageEditor.open(new CoverageEditorInput((ICoverageEditorProvider) dialog.getResult()[0]));
      }
   }

}
