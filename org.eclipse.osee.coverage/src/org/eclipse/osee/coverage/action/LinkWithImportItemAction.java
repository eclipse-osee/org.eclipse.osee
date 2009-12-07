/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MatchItem;
import org.eclipse.osee.coverage.merge.MatchType;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
   private Collection<MatchType> matchTypes = Arrays.asList(MatchType.Match__Name_And_Order_Num);

   public LinkWithImportItemAction() {
      super("Link with Import Item", Action.AS_CHECK_BOX);
   }

   private void updateSelection() throws OseeStateException {
      if (isChecked() && ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().size() == 1) {
         ICoverage importCoverageEditorItem =
               ((ISelectedCoverageEditorItem) importXViewer.getXViewer()).getSelectedCoverageEditorItems().iterator().next();
         MatchItem matchItem = MergeManager.getPackageCoverageItem(coveragePackage, importCoverageEditorItem);
         if (matchItem != null && matchItem.getPackageItem() != null) {
            ((ISelectedCoverageEditorItem) packageXViewer.getXViewer()).setSelectedCoverageEditorItem(matchItem.getPackageItem());
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(CoverageImage.LINK);
   }

   @Override
   public void run() {
      try {
         updateSelection();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
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
            try {
               updateSelection();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }
}
