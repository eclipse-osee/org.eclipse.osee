/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DeleteCoverUnitAction extends Action {
   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final IRefreshable refreshable;

   public DeleteCoverUnitAction(ISelectedCoverageEditorItem selectedCoverageEditorItem, IRefreshable refreshable, ISaveable saveable) {
      super("Delete Coverage Unit");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.refreshable = refreshable;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DELETE);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() == 0) {
         AWorkbench.popup("Select Coverage Unit");
         return;
      }
      for (ICoverageEditorItem item : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (!(item instanceof CoverageUnit)) {
            AWorkbench.popup("Can only delete Coverage Units");
            return;
         }
      }
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete Coverage Unit",
            "Delete Coverage Units")) {
         for (ICoverageEditorItem coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
            if (coverageItem.getParent() instanceof ICoverageUnitProvider) {
               ((ICoverageUnitProvider) coverageItem.getParent()).removeCoverageUnit((CoverageUnit) coverageItem);
               refreshable.remove(coverageItem);
            }
         }
      }
      saveable.save();
   }
}
