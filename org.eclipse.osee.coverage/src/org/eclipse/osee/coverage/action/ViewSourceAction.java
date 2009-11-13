/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ViewSourceAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;

   public ViewSourceAction(ISelectedCoverageEditorItem selectedCoverageEditorItem) {
      super("View Source");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() == 0) {
         AWorkbench.popup("Select Coverage Item");
         return;
      }
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() > 0) {
         ICoverage item = selectedCoverageEditorItem.getSelectedCoverageEditorItems().iterator().next();
         EntryDialog ed = new EntryDialog(item.getName(), "");
         ed.setFillVertically(true);
         String text = item.getFileContents();
         if (!Strings.isValid(text)) {
            text = item.getParent().getFileContents();
            if (!Strings.isValid(text)) {
               AWorkbench.popup("No Text Available");
               return;
            }
         }
         ed.setEntry(text);
         ed.open();
      }
   }

}
