/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class EditRationaleAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final IRefreshable refreshable;

   public EditRationaleAction(ISelectedCoverageEditorItem selectedCoverageEditorItem, IRefreshable refreshable, ISaveable saveable) {
      super("Edit Rationale");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.refreshable = refreshable;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() == 0) {
         AWorkbench.popup("Select Coverage Item");
         return;
      }
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      Set<String> rationale = new HashSet<String>();
      for (ICoverageEditorItem coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (coverageItem instanceof CoverageItem) {
            rationale.add(((CoverageItem) coverageItem).getCoverageRationale());
         }
      }
      EntryDialog ed = new EntryDialog("Coverage Rationale", "Enter Coverage Rationale");
      if (rationale.size() == 1 && Strings.isValid(rationale.iterator().next())) {
         ed.setEntry(rationale.iterator().next());
      }
      if (ed.open() == 0) {
         for (ICoverageEditorItem coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
            if (coverageItem instanceof CoverageItem) {
               ((CoverageItem) coverageItem).setCoverageRationale(ed.getEntry());
               refreshable.update(coverageItem);
            }
         }
      }
      saveable.save();
   }

}
