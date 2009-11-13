/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Donald G. Dunne
 */
public class LinkWithImportItemAction extends Action {
   private XCoverageMergeViewer packageXViewer;
   private XCoverageMergeViewer importXViewer;
   private CoveragePackage coveragePackage;

   public LinkWithImportItemAction() {
      super("Link with Import Item", Action.AS_CHECK_BOX);
   }

   private void updateSelection() {
      if (isChecked() && ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().size() == 1) {
         ICoverage importCoverageEditorItem =
               ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().iterator().next();
         ICoverage packageCoverageEditorItem =
               MergeManager.getPackageCoverageItem(coveragePackage, importCoverageEditorItem);
         if (packageCoverageEditorItem != null) {
            ((ISelectedCoverageEditorItem) packageXViewer.getXViewer()).setSelectedCoverageEditorItem(packageCoverageEditorItem);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(CoverageImage.LINK);
   }

   @Override
   public void run() {
      updateSelection();
   }

   public void setPackageXViewer(XCoverageMergeViewer packageXViewer, CoveragePackage coveragePackage) {
      this.packageXViewer = packageXViewer;
      this.coveragePackage = coveragePackage;
   }

   public XCoverageMergeViewer getImportXViewer() {
      return importXViewer;
   }

   public void setImportXViewer(XCoverageMergeViewer importXViewer) {
      this.importXViewer = importXViewer;
      this.importXViewer.getXViewer().getTree().addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            updateSelection();
         }
      });

   }
}
